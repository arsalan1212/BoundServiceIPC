package com.example.arsalankhan.boundserviceipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int CLIENT_REQUEST_CODE =123;
    private boolean isBound=false;
    private Messenger serviceMessanger=null;
    private TextView textView;

    /**
     * As the response come back from Myservice which is in separat process so we need to handle
     * that respone, so for that we override the handleMessage method
     */
    private class IncommingResponseHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MyService.SERVER_REQUEST_CODE:
                    int result=msg.arg1;
                    textView.setText("Result: "+result);
                    break;
            }
        }
    }
    Messenger incommingMessanger=new Messenger(new IncommingResponseHandler());
    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isBound=true;
            serviceMessanger=new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound=false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView= (TextView) findViewById(R.id.tv_result);
    }

    public void bindService(View view){
        Intent intent=new Intent(this,MyService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        //bindService will call Myservice onBind method where we return iBinder object and that
        //object we receive onServiceConnected as Ibinder
    }

    public void UnbindService(View view){
        if(isBound){
            Intent intent=new Intent(this,MyService.class);
            unbindService(serviceConnection);
            isBound=false;
        }
    }

    public void AddOperation(View view){

        if(isBound){
            EditText et_num1= (EditText) findViewById(R.id.et_num1);
            EditText et_num2= (EditText) findViewById(R.id.et_num2);

            int num1=Integer.valueOf(et_num1.getText().toString());
            int num2=Integer.valueOf(et_num2.getText().toString());

            //putting the data in bundle
            Bundle bundle=new Bundle();
            bundle.putInt("num1",num1);
            bundle.putInt("num2",num2);

            //creating the message object
            Message message=Message.obtain(null, CLIENT_REQUEST_CODE);
            message.setData(bundle);  //putting the bundle into message object and now send through
                                      //serviceMessanger to Myservice where they receive in handleMessage

            message.replyTo=incommingMessanger; //reply to incomming response
            try {
                serviceMessanger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "Sevice is unbind", Toast.LENGTH_SHORT).show();
        }



    }
}
