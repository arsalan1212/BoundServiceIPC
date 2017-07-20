package com.example.arsalankhan.boundserviceipc;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MyService extends Service {

    public static final int SERVER_REQUEST_CODE=11;
    /**
     * MyService run on a separate process, by setting android:process=":process_name"
     * this make android component run on a separate process, by default all android
     * components run in the same default process that android create at the time of launching app
      */

    //---------------***********************************--------------------------------------
    /**
     * As the message request come from different process(i.e from process in which mainActivity is running)
     * so we need to handle the message for that we create handler in order to handle the message
     */

    private class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what){

                case MainActivity.CLIENT_REQUEST_CODE:
                    Bundle bundle=msg.getData();
                    int num1=bundle.getInt("num1");
                    int num2=bundle.getInt("num2");

                    int result= add(num1,num2);

                    Message messageToActivity=Message.obtain(null,SERVER_REQUEST_CODE);
                    messageToActivity.arg1=result;

                    Messenger incommingMessanger=msg.replyTo;

                    try {
                        incommingMessanger.send(messageToActivity);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    Messenger messenger=new Messenger(new IncomingHandler());
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    //adding two number and sending back to caller activity
    public int add(int a,int b){
        return a+b;
    }
}
