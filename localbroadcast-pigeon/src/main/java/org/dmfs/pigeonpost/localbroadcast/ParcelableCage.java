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

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import org.dmfs.pigeonpost.Cage;
import org.dmfs.pigeonpost.Pigeon;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


/**
 * A {@link Cage} to send {@link Parcelable} objects via a {@link LocalBroadcastManager}.
 *
 * @author Marten Gajda
 */
public final class ParcelableCage<T extends Parcelable> implements Cage<T>
{
    private final Intent mIntent;


    /**
     * Creates a {@link ParcelableCage} that sends pigeons to the given {@link Intent}.
     *
     * @param intent
     *         The intent to send the broadcast to.
     */
    public ParcelableCage(@NonNull Intent intent)
    {
        mIntent = intent;
    }


    @NonNull
    @Override
    public Pigeon<T> pigeon(@NonNull final T payload)
    {
        final Intent originalIntent = mIntent;
        return new Pigeon<T>()
        {
            @Override
            public void send(@NonNull Context context)
            {
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                Intent intent = new Intent(originalIntent);
                intent.putExtra("org.dmfs.pigeonpost.DATA", payload);
                localBroadcastManager.sendBroadcastSync(intent);
            }
        };
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mIntent, flags);
    }


    public final static Parcelable.Creator<ParcelableCage> CREATOR = new Parcelable.Creator<ParcelableCage>()
    {
        @Override
        public ParcelableCage createFromParcel(Parcel source)
        {
            return new ParcelableCage((Intent) source.readParcelable(getClass().getClassLoader()));
        }


        @Override
        public ParcelableCage[] newArray(int size)
        {
            return new ParcelableCage[size];
        }
    };
}
