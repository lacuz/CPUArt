package com.orandnot.cpuart;

import android.app.Activity;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * 让任务管理器的刷新时间内一会儿忙，一会儿闲，然后调节忙和闲的比例。
 *让CPU在一段时间跑busy和idle两个循环，通过时间比例，调节CPU。
 *busy循环可以执行空循环实现，而idle可以通过sleep来实现。
 */
public class CpuTestMainActivity extends Activity {
    boolean flag_stop_line;
    boolean flag_stop_sin;
    boolean flag_finish_line = true;
    boolean flag_finish_sin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_stop_sin = true;
                if(flag_finish_line) {
                    flag_stop_line = false;
                    flag_finish_line = false;
                    new Thread(runnableLine).start();
                }else{
                    Toast.makeText(CpuTestMainActivity.this,"has a line thread exit...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_stop_line = true;
                if(flag_finish_sin) {
                    flag_stop_sin = false;
                    flag_finish_sin = false;
                    new Thread(runnableSin).start();
                }else{
                    Toast.makeText(CpuTestMainActivity.this,"has a sin thread exit...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//
//    Thread threadLine  = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            cpu_line();
//        }
//    });
//    Thread threadSin= new Thread(new Runnable() {
//        @Override
//        public void run() {
//            cpu_sin();
//        }
//    });

    Runnable runnableLine = new Runnable() {
        @Override
        public void run() {
            cpu_line();
        }
    };
    Runnable runnableSin = new Runnable() {
        @Override
        public void run() {
            cpu_sin();
        }
    };


    /**
     *按一定比例执行
     */
    private void cpu_line(){
        int busyTime = 10;
        int idleTime = busyTime;
        long startTime = 0;
        while(true)
        {
            if(flag_stop_line) break;
            startTime = SystemClock.uptimeMillis();
            while((SystemClock.uptimeMillis() - startTime) <= busyTime);
            sleep(idleTime);
        }
        flag_finish_line = true;
    }


    /**
     * 通过在一个周期2*PI中等分200份，
     * 将每一个间隔点的half + （sin( PI * radian) * half))的值存入busySpan[i]，将其补植存入idleSpan[i]。
     * half是整个值域INTERVAL的一半。这样可以近似趋近一条正弦曲线
     */
    private void cpu_sin(){
        double SPLIT = 0.01;
        int COUNT = 200;
        double PI = 3.14159265;
        int INTERVAL = 30;
        int[] busySpan = new int[COUNT]; //array of busy times
        int[] idleSpan = new int[COUNT]; //array of idle times

        int half = INTERVAL/2;
        double radian = 0.0;
        for(int i = 0; i < COUNT; ++i)
        {
            busySpan[i] = (int)(half + (Math.sin(PI * radian) * half));
            idleSpan[i] = INTERVAL - busySpan[i];
            radian += SPLIT;
        }
        long startTime = 0;
        int j = 0;
        while(true)
        {
            if(flag_stop_sin) break;
            j = j % COUNT;
            startTime = SystemClock.uptimeMillis();
            while((SystemClock.uptimeMillis() - startTime) <= busySpan[j]);
            sleep(idleSpan[j]);
            j++;
        }
        flag_finish_sin = false;
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
