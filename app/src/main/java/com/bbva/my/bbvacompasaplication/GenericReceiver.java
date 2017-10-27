package com.bbva.my.bbvacompasaplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by chers026 on 10/26/17.
 */

public class GenericReceiver extends ResultReceiver {

    private Receiver receiver;

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public GenericReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
