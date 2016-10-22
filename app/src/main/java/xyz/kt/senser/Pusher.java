package xyz.kt.senser;

/**
 * Background Data
 * Created by bjoern on 30/09/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

class PusherTask extends AsyncTask<Pusher, Void, Void>
{
    private static final int MAX_AVG_DATA_RATE = 256; // Bytes/Second
    private static final int MAX_PEAK_DATA_RATE = 8192; // Bytes/Second

    private static Charset charset= Charset.forName("US-ASCII");

    private static String API_BASE_URL= "http://api.dasfuze.beta.kt.xyz/api";
    private static String LOGIN_URL= API_BASE_URL + "/login.php?U=%1$s&P=%2$s";
    private static String PUSH_URL= API_BASE_URL + "/sample.php?S=%1$s";

    private String login( String uid, String pwHash )
    {
        try
        {
            URL url = new URL( String.format( LOGIN_URL, uid, pwHash) );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (null == conn)
                return null;

            try
            {
                conn.setRequestMethod("GET");
                if (200 == conn.getResponseCode())
                    return StreamUtils.fetchAll(conn.getInputStream());

            } finally
            {
                conn.disconnect();
            }
        }
        catch(MalformedURLException e)
        {
            throw new RuntimeException( e );
        }
        catch (IOException e)
        {
            //swallow: we will sleep and retry later.
            Log.i( "PusherTask.login", e.getMessage() );
        }

        return null;
    }

    @Override
    protected Void doInBackground(Pusher... params)
    {
        Pusher pusher= params[0];
        StringBuilder dequeued= pusher.dequeued;
        Queue<String> chunks= pusher.chunks;
        AtomicInteger pendingBytes= pusher.pendingBytes;

        String taken; // the queue may also be poll()ed concurrently in enqueueChunk!
        while (!chunks.isEmpty() && dequeued.length() < 100000 && null != (taken= chunks.poll()))
            dequeued.append( taken );

        byte[] bytes= dequeued.toString().getBytes(charset);

        if (null == pusher.sid)
            pusher.sid = login( pusher.uid, pusher.pwHash );

        if (null != pusher.sid)
        {
            Log.i( "Pusher", pusher.sid );
            try
            {
                URL url = new URL( String.format( PUSH_URL, pusher.sid ) );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (null != conn)
                {
                    try
                    {
                        conn.setRequestMethod("POST");
                        conn.setFixedLengthStreamingMode( bytes.length );
                        conn.setDoOutput( true );
                        conn.setDoInput( true );

                        OutputStream os = new BufferedOutputStream( conn.getOutputStream() );
                        try
                        {
                            os.write( bytes );
                            os.flush();
                        }
                        catch (Exception e)
                        {
                            StreamUtils.closeQuietly( os );
                            throw e;
                        }
                        os.close();

                        if (200 == conn.getResponseCode())
                        {
                            StreamUtils.drain( conn.getInputStream() );
                            pendingBytes.addAndGet( -dequeued.length() );
                            dequeued.setLength( 0 );
                            pusher.currentTask = null;
                            return null;
                        }

                        if (401 == conn.getResponseCode())
                        {
                            pusher.sid = null;
                            pusher.currentTask = null;
                            return null;
                        }
                    } finally
                    {
                        conn.disconnect();
                    }
                }
            }
            catch(MalformedURLException e)
            {
                throw new RuntimeException(e);
            }
            catch (IOException e)
            {
                // swallow: we will sleep and retry later...
                Log.i( "PusherTask.do", e.getMessage() );
            }
        }

        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            // fine to eat this: we're about to exit anyway...
        }

        pusher.currentTask= null;
        return null;
    }

    @Override
    protected void onCancelled()
    {
        // nothing, yet.
    }

    @Override
    protected void onPostExecute( Void result )
    {
        // nothing, yet.
    }
}


class Pusher
{
    private static final int MAX_PENDING_DATA_VOLUME = 128 * 1024 * 1024;
    private static final int MAX_CHUNK_DATA_VOLUME = 64 * 1024;
    private static final int MAX_QUEUE_SIZE = 3 * 24 * 60 * 60; // 1 day * 3 sensors

    String sid, uid, pwHash;

    LinkedBlockingQueue<String> chunks= new LinkedBlockingQueue<>( MAX_QUEUE_SIZE );
    AtomicInteger pendingBytes= new AtomicInteger(0);

    StringBuilder dequeued = new StringBuilder();
    volatile AsyncTask<?,?,?> currentTask;

    void setCredentials( String uid, String pwHash )
    {
        this.uid= uid;
        this.pwHash= pwHash;
    }

    void enqueueChunk( String data )
    {
        if (MAX_CHUNK_DATA_VOLUME < data.length())
            return; // sorry, that can't be right

        String taken;
        if (!chunks.offer( data ))
        {
            // queue is full. forget old stuff first.

            // TODO: This is probably the place to offload stuff into the database.
            //       Mind that we need to include the DB data volume in pendingBytes
            // while (64 * 1024 * 1024 < data.length + pendingBytes.get())
            //     if (null != (taken= dbqueue.poll()) || null != (taken= chunks.poll()))
            //         pendingBytes.addAndGet( -taken.length() );
            //     else
            //     if (0 != pendingBytes.get())
            //         throw new Exception( "Successfully toppled checks and balances" )
            //     else
            //     if ()
            //         pendingBytes.addAndGet( -taken.length() );
            //
            //     pendingBytes.addAndGet(-dbqueue.take().length())
            // dbqueue.add(chunks.take())
            //

            if (null != (taken = chunks.poll()))
                pendingBytes.addAndGet( -taken.length() );
            // else: the queue suddenly is empty (unlikely but not impossible)

            // Since enqueueChunk is only called from the main thread,
            // the next chunks.offer should succeed.

            if (!chunks.offer( data ))
                return; // sorry, something is fishy
        }
        pendingBytes.addAndGet( data.length() );

        if (null == currentTask && 4000 < pendingBytes.get())
            currentTask = new PusherTask().execute( this );

    }
}
