package android.cst.hqu.edu.cn.chapter6_6;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private EditText etUrl;
    private EditText etLocation;
    private ProgressBar pbShowProgress;
    private Button btStart;
    private int mDownStatus;
    private DownLoadUtil mDownLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUrl=findViewById(R.id.idEtTargetURL);
        etLocation=findViewById(R.id.idEtTargetLocation);
        pbShowProgress=findViewById(R.id.idProgressBar);
        pbShowProgress.setMax(100);
        btStart=findViewById(R.id.idBtnDown);
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0x123){
                    pbShowProgress.setProgress(mDownStatus);
                }
            }
        };
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownLoad=new DownLoadUtil(etUrl.getText().toString(),etLocation.getText().toString(),6);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            mDownLoad.start();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        final Timer timer=new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                double CompleteRate=mDownLoad.getCompleteRate();
                                mDownStatus=(int)(CompleteRate*100);

                                handler.sendEmptyMessage(0x123);
                                if(mDownStatus>=100){
                                    timer.cancel();
                                }
                            }
                        },100,1000);
                    }
                }.start();

            }
        });

    }
}
