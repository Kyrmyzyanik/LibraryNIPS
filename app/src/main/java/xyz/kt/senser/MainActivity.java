package xyz.kt.senser;

/**
 * Senser Application
 *
 * Created by Björn Karge on 30/09/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.nips.requests.AsyncHttpClient;
import com.example.nips.requests.AsyncHttpResponseHandler;
import com.example.nips.requests.ResponseHandlerInterface;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private TextView speed;
    private Button startStop, good, bad, post;
    private EditText comment, user, passwd;
    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initializeViews();

        Intent intent = new Intent(this, SenserService.class);
        startService(intent);

        intent = new Intent();
        intent.setAction( "Senser.Credentials" );
        intent.putExtra( "user", user.getText().toString() );
        intent.putExtra( "passwd", passwd.getText().toString() );
        sendBroadcast( intent );

        Log.e(" --- "," ---");
        AsyncHttpClient n = new AsyncHttpClient();
        n.get(0, new AsyncHttpResponseHandler() {
            @Override
            public void sendStartMessage() {

            }

            @Override
            public void sendFinishMessage() {

            }

            @Override
            public void sendProgressMessage(long bytesWritten, long bytesTotal) {

            }

            @Override
            public void sendCancelMessage() {

            }

            @Override
            public boolean getUseSynchronousMode() {
                return false;
            }

            @Override
            public void setUseSynchronousMode(boolean useSynchronousMode) {

            }

            @Override
            public boolean getUsePoolThread() {
                return false;
            }

            @Override
            public void setUsePoolThread(boolean usePoolThread) {

            }

            @Override
            public void onPreProcessResponse(ResponseHandlerInterface instance) {

            }

            @Override
            public void onPostProcessResponse(ResponseHandlerInterface instance) {

            }

            @Override
            public Object getTag() {
                return null;
            }

            @Override
            public void setTag(Object TAG) {

            }

            @Override
            public void onSuccess(int statusCode, byte[] responseBody) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void initializeViews() {
        speed = (TextView) findViewById(R.id.speed);

        (user= (EditText) findViewById( R.id.edtUser )).setOnFocusChangeListener( new View.OnFocusChangeListener() {
            public void onFocusChange( View v, boolean hasFocus )
            {
                if (!hasFocus)
                {
                    Intent intent = new Intent();
                    intent.setAction( "Senser.Credentials" );
                    intent.putExtra( "user", user.getText().toString() );
                    intent.putExtra( "passwd", passwd.getText().toString() );
                    sendBroadcast( intent );
                }
            }
        } );

        (passwd= (EditText) findViewById( R.id.edtPasswd )).setOnFocusChangeListener( new View.OnFocusChangeListener() {
            public void onFocusChange( View v, boolean hasFocus )
            {
                if (!hasFocus)
                {
                    Intent intent = new Intent();
                    intent.setAction( "Senser.Credentials" );
                    intent.putExtra( "user", user.getText().toString() );
                    intent.putExtra( "passwd", passwd.getText().toString() );
                    sendBroadcast( intent );
                }
            }
        } );

        (startStop= (Button) findViewById( R.id.tglStartEnd )).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction( "Senser.Annotate" );
                if (startStop.getText().toString().contains( "START" ))
                {
                    intent.putExtra( "comment", "#START" );
                    startStop.setText( "TRIP STOPPED" );
                }
                else
                {
                    intent.putExtra( "comment", "#STOP" );
                    startStop.setText( "TRIP STARTED" );
                }


                sendBroadcast( intent );
            }
        });
        (good= (Button) findViewById( R.id.btnGood )).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction( "Senser.Annotate" );
                intent.putExtra( "comment", "#OK" );
                sendBroadcast( intent );
            }
        });


        (bad= (Button) findViewById( R.id.btnBad )).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction( "Senser.Annotate" );
                intent.putExtra( "comment", "#BAD" );
                sendBroadcast( intent );
            }
        });

        comment= (EditText) findViewById( R.id.comment );
        (post= (Button) findViewById( R.id.btnPost )).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction( "Senser.Annotate" );
                intent.putExtra( "comment", comment.getText().toString() );
                sendBroadcast( intent );
                comment.setText( "" );
            }
        });

    }

    @Override
    public void onResume()
    {
        super.onResume();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread( new Runnable() {
                    public void run() {
                        int kmh= (int)(LocationListener.lastSpeed * 3.6 + 0.5);
                        int mph= (int)(LocationListener.lastSpeed * 2.237 + 0.5);
                        speed.setText( kmh + "km/h  " + mph + "mph");
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onPause()
    {
        timer.cancel();
        super.onPause();
    }
}
