package com.ttpai.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ttpai.track.annotation.OnMethodCall;

public class BActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "BActivity";
    private ViewGroup root;

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
        findViewById(R.id.button3).setOnClickListener(this);
        root = findViewById(R.id.ll_root);
    }

    @OnMethodCall
    private void test() {

    }

    AlertDialog dialog;
    PopupWindow pop;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
//                startActivity(new Intent(this, CActivity.class));
                if (dialog != null) {
                    dialog.show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("title")
                        .setMessage("I'm a Dialog")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "确定 onClick :" + dialog + " which:" + which);
                            }
                        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "取消 onClick :" + dialog + " which:" + which);
                            }
                        });
                dialog = builder.create();//.dismiss();

                Button button2=new Button(this);
                button2.setText("test");
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "AlertDialog.onClick :" + button2);
                    }
                });
                dialog.setView(button2);

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.d(TAG, "222  AlertDialog.onDismiss :" + dialog);

                    }
                });
                dialog.show();
                View view2=dialog.getWindow().getDecorView();
                Log.d(TAG,"view2="+view2+" id="+view2.getId());
//                window.setOnDismissListener();
                /*dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.d(TAG, "AlertDialog.onDismiss :" + dialog);
                    }
                });*/
                /*v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 2000);*/

               /*new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                   }
               },12,10,true).show();*/
                break;
            case R.id.button3:

                if(pop!=null){
                    pop.showAtLocation(root, Gravity.CENTER, 0, 0);//, Gravity.CENTER
                    return;
                }
                View contentView = getLayoutInflater().inflate(R.layout.content_view_popup, root, false);
                EditText editText = contentView.findViewById(R.id.edit_query);
                Button button = contentView.findViewById(R.id.bt_query);
                pop = new PopupWindow(contentView, getDisplayWidth(this),-1);
                pop.setContentView(contentView);
                pop.setOutsideTouchable(true);
                pop.setFocusable(true);
                pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Log.d(TAG, "PopupWindow onDismiss :" + pop);
                    }
                });
//                pop.setOutsideTouchable(true);
//                pop.showAsDropDown(root,0,0, Gravity.CENTER);//, Gravity.CENTER
                pop.showAtLocation(root, Gravity.CENTER, 0, 0);//, Gravity.CENTER
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(BActivity.this, "click", Toast.LENGTH_SHORT).show();
                    }
                });

                /*editText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pop.dismiss();
                    }
                }, 2000);*/

                break;
            default:

                break;
        }
    }

    public static int getDisplayWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
