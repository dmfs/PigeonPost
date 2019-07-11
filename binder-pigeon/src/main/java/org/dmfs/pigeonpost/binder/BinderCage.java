package org.dmfs.pigeonpost.binder;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import org.dmfs.pigeonpost.Cage;
import org.dmfs.pigeonpost.Pigeon;

import androidx.annotation.NonNull;


/**
 * @author Marten Gajda
 */
public final class BinderCage<T extends Parcelable> implements Cage<T>
{
    private final IBinder mBinder;


    public BinderCage(IBinder binder)
    {
        mBinder = binder;
    }


    @NonNull
    @Override
    public Pigeon<T> pigeon(@NonNull final T payload)
    {
        return context ->
        {
            try
            {
                Bundle payloadBundle = new Bundle(1);
                payloadBundle.putParcelable("key", payload);
                PigeonBinder.Stub.asInterface(mBinder).send(payloadBundle);
            }
            catch (RemoteException e)
            {
                throw new RuntimeException("unable to send pigeon", e);
            }
        };
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeStrongBinder(mBinder);
    }


    public static Creator<BinderCage> CREATOR = new Creator<BinderCage>()
    {
        @Override
        public BinderCage createFromParcel(Parcel parcel)
        {
            return new BinderCage(parcel.readStrongBinder());
        }


        @Override
        public BinderCage[] newArray(int i)
        {
            return new BinderCage[i];
        }
    };
}
