package com.ttpai.sample;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.ttpai.track.annotation.OnMethodCall;

/**
 * FileName: SecondOnClick
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-28
 * Description:
 */
public class SecondOnClick implements View.OnClickListener {
    private String TAG = "SecondOnClick";

    @Override
    public void onClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                .setTitle("title")
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
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @OnMethodCall
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d(TAG, "onDismiss :" + dialog + " :" + this);
            }
        });
        builder.show();//.dismiss();

    }
}
