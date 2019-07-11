// PigeonBinder.aidl
package org.dmfs.pigeonpost.binder;


interface PigeonBinder {
    oneway void send(in Bundle payload);
}
