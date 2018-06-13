package com.wen.asyl.videoplayerdemo;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView mVideoView;
    private LinearLayout mControllerLayout;
    private ImageView mPauseImg,mScreenImg,mVolumeImg;
    private TextView mCurrentTextTv,mTotalTimeTv;
    private SeekBar mPlaySeek,mVolumeSeek;
    private AudioManager mAudioManager;
    private  int screen_width,screen_height;
    private RelativeLayout mVideoLayout;
    private  boolean isFullScreen=false;
    private boolean isAdjust=false;
    private  int threshold=54;
    private  float mBrightness;
    private ImageView mOperationBg,mOperationPercent;
    private FrameLayout mFrameLayout;
    private float lastX;
    private float lastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAudioManager= (AudioManager) getSystemService(AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        initView();

        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/ceshi.mp4";
        //本地视频播放
        mVideoView.setVideoPath(path);
        mVideoView.start();
        UIHandler.sendEmptyMessage(1);

//        String url="http://192.168.1.92:8080/jiangwenju/video.mp4";
//         // 网络播放
//       mVideoView.setVideoURI(Uri.parse(url));
//         //使用MediaController控制视频播放
//        MediaController controller=new MediaController(this);
//        //设置videoView与MediaController建立关联
//        mVideoView.setMediaController(controller);
//        //设置MediaController与VideoView建立关键
//        controller.setMediaPlayer(mVideoView);
    }

    private void initView() {
        PixelUtil.initContext(this);
        mVideoView= (VideoView) findViewById(R.id.videoview);
        mControllerLayout= (LinearLayout) findViewById(R.id.controllerbar_layout);
        mPlaySeek= (SeekBar) findViewById(R.id.play_seek);
        mPauseImg= (ImageView) findViewById(R.id.pause_img);
        mCurrentTextTv= (TextView) findViewById(R.id.time_current_tv);
        mTotalTimeTv= (TextView) findViewById(R.id.time_total_tv);
        mVolumeImg= (ImageView) findViewById(R.id.volume_img);
        mVolumeSeek= (SeekBar) findViewById(R.id.volume_seekbar);
        mScreenImg= (ImageView) findViewById(R.id.screen_img);
        mVideoLayout= (RelativeLayout) findViewById(R.id.videoLayout);
        mOperationBg= (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent= (ImageView) findViewById(R.id.operation_percent);
        mFrameLayout= (FrameLayout) findViewById(R.id.frameLayout);

       screen_width= getResources().getDisplayMetrics().widthPixels;
        screen_height= getResources().getDisplayMetrics().heightPixels;

        mPauseImg.setOnClickListener(this);
        mScreenImg.setOnClickListener(this);
        mPlaySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTextViewWithTimeFormat(mCurrentTextTv,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UIHandler.removeMessages(1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress=seekBar.getProgress();
                //令视频播放进度遵循seekbar停止拖动的这一刻的进度
                mVideoView.seekTo(progress);
                UIHandler.sendEmptyMessage(1);
            }
        });
        //获取当前设备最大音量
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //获取当前设备的音量
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumeSeek.setMax(streamMaxVolume);
        mVolumeSeek.setProgress(streamVolume);
        mVolumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置当前设备的音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    //手指落下屏幕的那一刻(只会调用一次)
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        lastY = event.getY();
                        break;
                    //手指在屏幕上移动(调用多次)
                    case MotionEvent.ACTION_MOVE:
                        float endY = event.getY();
                        float endX = event.getX();
                        float detlaY = lastY - endY;
                        float detlaX = lastX - endX;
                        float absdetlaX=Math.abs(detlaX);
                        float absdetlaY=Math.abs(detlaY);
                        if (absdetlaX>threshold&&absdetlaY>threshold){
                            if (absdetlaX<absdetlaY){
                                isAdjust=true;
                            }else{
                                isAdjust=false;
                            }
                        }else if (absdetlaX<threshold&&absdetlaY>threshold){
                            isAdjust=true;
                        }else if (absdetlaX>threshold&&absdetlaY<threshold){
                            isAdjust=false;
                        }
                        if (isAdjust){
                            //在判断好当前手势事件已经合法的前提下，去区分此时手势应该调节亮度还是音量
                            if (endX<screen_width/2){
                                //调节亮度
                                if (detlaY>0){
                                    //降低亮度
                                }else{
                                    //升高亮度
                                }
                                changBrightness(-detlaY);
                            }else{
                                //调节声音
                                if (detlaY>0){
                                    //减少声音
                                }else{
                                    //增大声音
                                }
                               changVolume(-detlaY);
                            }
                        }

                        break;
                    //手指离开屏幕的那一刻(调用一次)
                    case MotionEvent.ACTION_UP:
                        mFrameLayout.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });

    }

    private  void changVolume(float detlaY){
        int max=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int index=(int)(detlaY/screen_width*max*3);
        int volume=Math.max(current+index,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
        if (mFrameLayout.getVisibility()==View.GONE){
            mFrameLayout.setVisibility(View.VISIBLE);
        }
        mOperationBg.setImageResource(R.mipmap.volume_tj);
        ViewGroup.LayoutParams layoutParams = mOperationPercent.getLayoutParams();
        layoutParams.width=(int)(PixelUtil.dp2dx(94)*(float)volume/max);
        mOperationPercent.setLayoutParams(layoutParams);
        mVolumeSeek.setProgress(volume);

    }
    private  void changBrightness(float detlaY){
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        mBrightness= attributes.screenBrightness;
       float index= detlaY/screen_height/3;
        mBrightness+=index;
        if (mBrightness>1.0f){
            mBrightness=1.0f;
        }
        if (mBrightness<0.01f){
            mBrightness=0.01f;
        }
        attributes.screenBrightness=mBrightness;
        if (mFrameLayout.getVisibility()==View.GONE){
            mFrameLayout.setVisibility(View.VISIBLE);
        }
        mOperationBg.setImageResource(R.mipmap.volume_tj);
        ViewGroup.LayoutParams layoutParams = mOperationPercent.getLayoutParams();
        layoutParams.width=(int)(PixelUtil.dp2dx(94)*mBrightness);
        mOperationPercent.setLayoutParams(layoutParams);
        getWindow().setAttributes(attributes);
    }
    private  void setVideoViewScale(int width,int height){
        ViewGroup.LayoutParams layoutParams=mVideoView.getLayoutParams();
        layoutParams.width=width;
        layoutParams.height=height;
        mVideoView.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams params = mVideoLayout.getLayoutParams();
        params.width=width;
        params.height=height;
        mVideoLayout.setLayoutParams(params);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 4:
                if(grantResults.length>0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "已打开读取内存权限！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "请打开读取内存权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    private void updateTextViewWithTimeFormat(TextView textView ,int millsecond){
        int second=millsecond/1000;
        int hh=second/3600;
        int mm=second%3600/60;
        int ss=second%60;
        String  str=null;
        if (hh!=0){
            str=String.format("%02d:%02d:%02d",hh,mm,ss);
        }else{
            str=String.format("%02d:%02d",mm,ss);
        }
        textView.setText(str);
    }

    private Handler UIHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                //获取视频当前播放时间
                int currentPosition=mVideoView.getCurrentPosition();
                //获取视频播放总时间
                int totalduration=mVideoView.getDuration();
                updateTextViewWithTimeFormat(mCurrentTextTv,currentPosition);
                updateTextViewWithTimeFormat(mTotalTimeTv,totalduration);

                mPlaySeek.setMax(totalduration);
                mPlaySeek.setProgress(currentPosition);
                UIHandler.sendEmptyMessageDelayed(1,500);
            }

        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pause_img:
                if (mVideoView.isPlaying()){
                    mPauseImg.setImageResource(R.drawable.play_btn_style);
                    //暂停播放
                    mVideoView.pause();
                    UIHandler.removeMessages(1);
                }else{
                     mPauseImg.setImageResource(R.drawable.pause_btn_style);
                     //继续播放
                     mVideoView.start();
                    UIHandler.sendEmptyMessage(1);
                }
                break;
            case R.id.screen_img:
                if (isFullScreen){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
        }
    }

    /**
     * 监听到屏幕方向的改变
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //当屏幕方向为横屏的时候
        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
              setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
               mVolumeSeek.setVisibility(View.VISIBLE);
               mVolumeImg.setVisibility(View.VISIBLE);
               isFullScreen=true;
               getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            //当屏幕方向为竖屏的时候
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT,PixelUtil.dp2dx(240));
            mVolumeSeek.setVisibility(View.GONE);
            mVolumeImg.setVisibility(View.GONE);
            isFullScreen=false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UIHandler.removeMessages(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIHandler.removeMessages(1);
    }

}
