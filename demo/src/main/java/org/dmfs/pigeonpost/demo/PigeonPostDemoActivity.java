package org.dmfs.pigeonpost.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.dmfs.pigeonpost.Dovecote;
import org.dmfs.pigeonpost.binder.BinderDovecote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class PigeonPostDemoActivity extends AppCompatActivity implements View.OnClickListener, Dovecote.OnPigeonReturnCallback<Bundle>
{

    private Dovecote<Bundle> mDovecote;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDovecote = new BinderDovecote(this);
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
            Bundle b = new Bundle();
            b.putString("a", "Ui thread");
            mDovecote.cage().pigeon(b).send(this);
        }
        if (v.getId() == R.id.background_thread_button)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Bundle b = new Bundle();
                    b.putString("a", "background thread");
                    mDovecote.cage().pigeon(b).send(PigeonPostDemoActivity.this);
                }
            }).start();
        }
    }


    @Override
    public void onPigeonReturn(@NonNull Bundle payload)
    {
        ((TextView) findViewById(R.id.text)).setText(
                String.format("Received Pigeon \"%s\" on Thread \"%s\"", payload.toString(), Thread.currentThread().getName()));
    }
}
