package com.ttpai.sample;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ttpai.sample.databinding.ActivityMainBinding;
import com.ttpai.sample.fragment.TabFragmentActivity;
import com.ttpai.track.annotation.OnMethodCall;

public class AActivity extends AppCompatActivity {

    private static final String TAG = "AActivity";
    private PopupWindow pop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setData(new Person(10, "请打开Pointer.java 查看log"));

        final TextView view = findViewById(R.id.tv_text);

        /*view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        view.setOnClickListener(v-> {
                setVisibili(view);
        });
        int i=0;

        View.OnClickListener click=new View.OnClickListener() {
            @OnMethodCall
            @Override
            public void onClick(View v) {
                Log.d(TAG," i="+i);
                setVisibili(view);
                Intent intent=new Intent(getApplicationContext(), CActivity.class);
                startActivity(intent);
                if(pop!=null)
                    pop.dismiss();
            }
        };
        Log.d(TAG,"click :"+click.getClass()+" interface:"+click.getClass().getInterfaces());
        binding.setClick(click);
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), CActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Application app=getApplication();
                Context ctx=getApplication().getApplicationContext();
                Log.d(TAG,"app:"+app+" :"+ctx);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    app.startActivity(intent);
                }
            }
        });
/*
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, SecondActivity.class));

                ViewGroup group = findViewById(R.id.ll_root);
                View view = new View(MainActivity.this);
                view.setBackgroundColor(Color.BLACK);
                group.addView(view, 200, 200);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "aaaaa");
                    }
                });
            }
        });*/
        getStringAbc("aa", 1);
        Log.d(TAG, "aaaaa");

        getHeight();

        final ViewGroup root=findViewById(R.id.ll_root);
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View contentView=getLayoutInflater().inflate(R.layout.content_view_popup,root,false);
                EditText editText=contentView.findViewById(R.id.edit_query);
                Button button=contentView.findViewById(R.id.bt_query);
                pop = new PopupWindow(contentView,getDisplayWidth(AActivity.this),getDisplayHeight(AActivity.this));
                pop.setContentView(contentView);
                pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//                pop.showAsDropDown(root,0,0, Gravity.CENTER);//, Gravity.CENTER
                pop.showAtLocation(root,Gravity.CENTER,0,0);//, Gravity.CENTER
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AActivity.this,"click",Toast.LENGTH_SHORT).show();
                    }
                });

                editText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pop.dismiss();
                    }
                },5000);
            }
        });
//        finish();
//        divideZero();

        Button button=findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AActivity.this, TabFragmentActivity.class));
            }
        });

        findViewById(R.id.button5).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(AActivity.this,"long click",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AActivity.this, DActivity.class));
            }
        });
    }

    @OnMethodCall
    private View setVisibili(TextView view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    @OnMethodCall
    private int getHeight() {
        return 1280;
    }

    public void divideZero() {
        int i = 2 / 0;
    }

    private String getStringAbc(String a, int b) {
        return a + b;
    }

    /**
     * 获得屏幕高
     *
     * @param context
     * @return
     */
    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }
    /**
     * 获得屏幕高
     *
     * @param context
     * @return
     */
    public static int getDisplayWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }


}
