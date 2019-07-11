package org.dmfs.pigeonpost.binder;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;

import org.dmfs.pigeonpost.Cage;
import org.dmfs.pigeonpost.Dovecote;
import org.dmfs.pigeonpost.binder.tools.MainThreadExecutor;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;


/**
 * @author Marten Gajda
 */
public final class BinderDovecote<T extends Parcelable> implements Dovecote<T>
{

    private final OnPigeonReturnCallback<T> mCallBack;
    private final Executor mCallbackExecutor;

    private final IBinder mBinder = new PigeonBinder.Stub()
    {
        @Override
        public void send(final Bundle payload) throws RemoteException
        {
            mCallbackExecutor.execute(() -> mCallBack.onPigeonReturn(payload.getParcelable("key")));
        }
    };


    public BinderDovecote(OnPigeonReturnCallback<T> callBack)
    {
        this(callBack, MainThreadExecutor.INSTANCE);
    }


    public BinderDovecote(OnPigeonReturnCallback<T> callBack, Executor callbackExecutor)
    {
        mCallBack = callBack;
        mCallbackExecutor = callbackExecutor;
    }


    @NonNull
    @Override
    public Cage<T> cage()
    {
        return new BinderCage<>(mBinder);
    }


    @Override
    public void dispose()
    {
        // not supported
    }
}
