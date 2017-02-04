package com.qf.level.day41sensorkakaka;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView flowerImg,upImg,downImg;
    private SoundPool soundPool;//音乐池对象，专门处理短小提示音

    private SensorManager sensorManager;
    private Sensor accSensor;//加速度传感器
    private SensorEventListener accListener;//监听器
    private long lastTime=0;//记录上次摇一摇的时间
    private int rawId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flowerImg= (ImageView) findViewById(R.id.flowerId);
        upImg= (ImageView) findViewById(R.id.upimgId);
        downImg= (ImageView) findViewById(R.id.downimgId);

        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);//获取管理器
        initSensor();//初始化传感器
        initSoundpool();//初始化音乐资源
    }

    private void initSensor() {
        accSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//获取加速度传感器
        accListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                //值改变时调用
                long currentTimeMillis=System.currentTimeMillis();
                if ((currentTimeMillis-lastTime)<700){
                    return;
                }
                float[] values = sensorEvent.values;
                float valueX = values[0];
                float valueY = values[1];
                float valueZ = values[2];

                Log.d("ee", "x: "+valueX+", y: "+valueY+", z: "+valueZ);

                if (valueX>15 || valueY>15 ||valueZ>15){
                    //摇一摇成功
                    soundPool.play(rawId,2,2,1,0,1);//播放音乐
                    playAnim();//执行动画
                }
                lastTime=currentTimeMillis;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        //注册监听
        sensorManager.registerListener(accListener,accSensor,SensorManager.SENSOR_DELAY_GAME);

    }
    //处理动画
    private void playAnim() {
        //上面的向上 下面向下
        AnimatorSet animatorSet=new AnimatorSet();//动画引擎
        int measuredHeight = upImg.getMeasuredHeight();
        ObjectAnimator animator1=ObjectAnimator.ofFloat(upImg,
                "translationY",0,-measuredHeight).setDuration(300);
        ObjectAnimator animator2=ObjectAnimator.ofFloat(upImg,
                "translationY",-measuredHeight,0).setDuration(300);
        ObjectAnimator animator3=ObjectAnimator.ofFloat(downImg,
                "translationY",0,measuredHeight).setDuration(300);
        ObjectAnimator animator4=ObjectAnimator.ofFloat(downImg,
                "translationY",measuredHeight,0).setDuration(300);
        animatorSet.play(animator1).with(animator3);
        animatorSet.play(animator2).after(animator1).with(animator4);//调整动画播放顺序
        animatorSet.start();//启动动画

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(accListener,accSensor);//解除监听
    }

    private void initSoundpool() {

        soundPool=new SoundPool(10, AudioManager.STREAM_MUSIC,1);//创建一个音乐池来播放kakaka
        rawId = soundPool.load(this, R.raw.kakaka, 1);//将声音资源转换为对应的资源id
    }
}
