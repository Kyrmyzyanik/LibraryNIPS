package xyz.kt.senser;

/**
 * Stream Utilities
 *
 * Created by bjoern on 04/10/2016.
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

final class StreamUtils
{
    /**
     *
     * @param c
     * @throws IOException
     */
    static void closeQuietly( Closeable c )
    {
        try
        {
            c.close();
        }
        catch (Exception e)
        {
            // swallow.
        }
    }

    /**
     *
     * @param is
     * @throws IOException
     */
    static String fetchAll( InputStream is ) throws IOException
    {
        String result;
        boolean isNeedsClose= true; // proper English: object with name `is` needs close()
        try
        {
            BufferedInputStream bis = new BufferedInputStream(is);
            try
            {
                isNeedsClose= false;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try
                {
                    int c;
                    while ((c= bis.read()) != -1)
                        baos.write((byte) c);
                    result= baos.toString();
                }
                catch (Exception e)
                {
                    closeQuietly( baos );
                    throw e;
                }
                baos.close();

            }
            catch (Exception e)
            {
                closeQuietly( bis );
                throw e;
            }
            bis.close();
        }
        catch (Exception e)
        {
            if (isNeedsClose)
                closeQuietly( is );
            throw e;
        }
        return result;
    }

    /**
     *
     * @param is
     * @throws IOException
     */
    static void drain( InputStream is ) throws IOException
    {
        boolean isNeedsClose= true; // proper English: object with name `is` needs close()
        try
        {
            BufferedInputStream bis = new BufferedInputStream(is);
            try
            {
                isNeedsClose= false;
                while (bis.read() != -1)
                {
                    /*loop*/
                }
            }
            catch (Exception e)
            {
                closeQuietly( bis );
                throw e;
            }
            bis.close();
        }
        catch (Exception e)
        {
            if (isNeedsClose)
                closeQuietly( is );
            throw e;
        }
    }
}
