/*
 * Copyright 2016 dmfs GmbH
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dmfs.pigeonpost.localbroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.dmfs.pigeonpost.Cage;
import org.dmfs.pigeonpost.Dovecote;
import org.dmfs.pigeonpost.Pigeon;
import org.dmfs.pigeonpost.localbroadcast.tools.MainThreadExecutor;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


/**
 * A {@link Dovecote} that receives {@link Pigeon} with a {@link LocalBroadcastManager}.
 * <p>
 * {@link SerializableCage}s are good for communication within the same process. Note that sent {@link Pigeon}s are lost if there are no active {@link
 * SerializableDovecote}s available.
 *
 * @author Marten Gajda
 */
public final class SerializableDovecote<T extends Serializable> implements Dovecote<T>
{
    private final Context mContext;
    private final String mName;
    private final BroadcastReceiver mReceiver;


    /**
     * Creates a {@link Dovecote} that receives {@link Pigeon}s via a {@link LocalBroadcastManager}.
     *
     * @param context
     *         A {@link Context}.
     * @param name
     *         The name of this {@link Dovecote}. All {@link Dovecote}s with the same name will receive the same pigeons.
     * @param callback
     *         The {@link OnPigeonReturnCallback} called when a {@link Pigeon} arrived.
     */
    public SerializableDovecote(@NonNull Context context, @NonNull String name, @NonNull OnPigeonReturnCallback<T> callback)
    {
        mContext = context;
        mName = name;
        mReceiver = new DovecotReceiver<T>(callback);
        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, new IntentFilter(mName));
    }


    @NonNull
    @Override
    public Cage<T> cage()
    {
        return new SerializableCage<T>(new Intent(mName));
    }


    @Override
    public void dispose()
    {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
    }


    private static class DovecotReceiver<T extends Serializable> extends BroadcastReceiver
    {
        private final OnPigeonReturnCallback<T> mCallback;


        private DovecotReceiver(@NonNull OnPigeonReturnCallback<T> callback)
        {
            mCallback = callback;
        }


        @Override
        public void onReceive(Context context, final Intent intent)
        {
            MainThreadExecutor.INSTANCE.execute(new Runnable()
            {
                @SuppressWarnings("unchecked")
                @Override
                public void run()
                {
                    mCallback.onPigeonReturn((T) intent.getSerializableExtra("org.dmfs.pigeonpost.DATA"));
                }
            });
        }
    }
}
