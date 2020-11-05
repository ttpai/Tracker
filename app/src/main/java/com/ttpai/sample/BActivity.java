package com.ttpai.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ttpai.track.annotation.OnMethodCall;

public class BActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG="BActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @OnMethodCall
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BActivity.this, CActivity.class));
                test();
            }
        });

        findViewById(R.id.button2).setOnClickListener(this);
    }

    @OnMethodCall
    private void test(){

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button2:
                this.finish();
//                startActivity(new Intent(this, CActivity.class));
                /*android.support.v7.app.AlertDialog.Builder builder=new AlertDialog.Builder(this).setTitle("title")
                        .setMessage("I'm a Dialog")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG,"确定 onClick :"+dialog+" which:"+which);
                            }
                        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"取消 onClick :"+dialog+" which:"+which);
                    }
                });
                final AlertDialog dialog=builder.show();//.dismiss();
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @OnMethodCall
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.d(TAG,"onDismiss :"+dialog);
                    }
                });

                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                },2000);*/

               /*new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                   }
               },12,10,true).show();*/
              break;
            default:

              break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
