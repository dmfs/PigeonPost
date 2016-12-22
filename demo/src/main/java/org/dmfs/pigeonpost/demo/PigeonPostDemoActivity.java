package org.dmfs.pigeonpost.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.dmfs.pigeonpost.Dovecote;
import org.dmfs.pigeonpost.localbroadcast.SerializableDovecote;

import java.io.Serializable;


public class PigeonPostDemoActivity extends AppCompatActivity implements View.OnClickListener, Dovecote.OnPigeonReturnCallback<Serializable>
{

    private Dovecote<Serializable> mDovecote;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDovecote = new SerializableDovecote<>(this, "main", this);
        setContentView(R.layout.activity_pigeon_post_demo);
        findViewById(R.id.main_thread_button).setOnClickListener(this);
        findViewById(R.id.background_thread_button).setOnClickListener(this);
    }


    @Override
    protected void onDestroy()
    {
        mDovecote.dispose();
        super.onDestroy();
    }


    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.main_thread_button)
        {
            mDovecote.cage().pigeon("Send from UI thread").send(this);
        }
        if (v.getId() == R.id.background_thread_button)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    mDovecote.cage().pigeon("Send from background thread").send(PigeonPostDemoActivity.this);
                }
            }).start();
        }
    }


    @Override
    public void onPigeonReturn(@NonNull Serializable payload)
    {
        ((TextView) findViewById(R.id.text)).setText(
                String.format("Received Pigeon \"%s\" on Thread \"%s\"", payload.toString(), Thread.currentThread().getName()));
    }
}
