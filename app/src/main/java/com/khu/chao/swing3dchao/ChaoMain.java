//automatically 10 seconds recording finished version
//replay finished version
package com.khu.chao.swing3dchao;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//import android.os.Handler;
//import android.widget.TextView;

public class ChaoMain extends Activity implements OnClickListener {
    Button record, play_video;
    // video file
    File viodFile;
    MediaRecorder mRecorder;
    // show the SurfaceView of the video
    SurfaceView sView;

    boolean isRecording = false;
    Camera camera;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide title bar  Chapter 2.24
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.chao_main);
        record = (Button) findViewById(R.id.record);
        play_video = (Button) findViewById(R.id.play_video);
        sView = (SurfaceView) findViewById(R.id.dView);
        // set Surface do not need its own buffer zone
        sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // resolution
        sView.getHolder().setFixedSize(720, 560);
        // keep the screen
        sView.getHolder().setKeepScreenOn(true);
        record.setOnClickListener(this);
        play_video.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record:
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(this, "No SD card！", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {

                    int cameraType = 1; // front
                    camera = Camera.open(cameraType);
                    Camera.Size size = getBestPreviewSize(720,560, camera.getParameters());
                    //List sizes = camera.getParameters().getSupportedVideoSizes();
                    //System.out.println(sizes);
                    camera.unlock();

                    // create MediaPlayer
                    mRecorder = new MediaRecorder();
                    mRecorder.reset();
                    mRecorder.setCamera(camera);

   /* camera = Camera.open();
    camera.unlock();
    camera.setDisplayOrientation(0);
    mRecorder.setCamera(camera);*/
                    // save the video file
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String currentDateandTime = sdf.format(new Date());
                    viodFile = new File(Environment.getExternalStorageDirectory().getCanonicalFile() + "/myvideo"+currentDateandTime+".mp4");
                    if (!viodFile.exists())
                        viodFile.createNewFile();
                    //android.hardware.Camera.open(0);

                    // 设置从麦克风采集声音
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    // 设置从摄像头采集图像
                    mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    // 设置视频、音频的输出格式
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    // 设置音频的编码格式、
                    //CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                    //mRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
                    mRecorder.setVideoEncodingBitRate(3000000);
                    mRecorder.setVideoSize(size.width, size.height);
                    //CamcorderProfile cpHigh = CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_FRONT,CamcorderProfile.QUALITY_HIGH);
                    //mRecorder.setProfile(cpHigh);
                    //mRecorder.setVideoFrameRate(15);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    // 设置图像编码格式
                    mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                    //mRecorder.setMaxDuration(10000);
                    //视频旋转90度
                    //mRecorder.setOrientationHint(90);
                    //mRecorder.setVideoFrameRate(15);
                    //mRecorder.setVideoSize(320, 280);
                    // 指定SurfaceView来预览视频
                    mRecorder.setPreviewDisplay(sView.getHolder().getSurface());
                    mRecorder.setOutputFile(viodFile.getAbsolutePath());

                    mRecorder.prepare();
                    // start
                    mRecorder.start();

                    // record button not available
                    record.setEnabled(false);
                    // play button not available
                    play_video.setEnabled(false);
                    isRecording = true;

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        //int i = 10;
                        @Override
                        public void run(){
                           //stop
                            mRecorder.stop();
                           //release resource
                            mRecorder.release();
                            mRecorder = null;
                            isRecording = false;
                            camera.lock();
                            camera.release();
                            camera=null;
                          //define a message to send
                           //Message msg = new Message();
                            //msg.what = i--;
                            //handler.sendMessage(msg);
                        }
                    }, 10000);
                   //record button available
                    record.setEnabled(true);
                    //  button available
                    play_video.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.play_video:
            // if it is not recording now
            if (!isRecording) {
                try {
                    Intent intent = new Intent(ChaoMain.this, ChaoSecond.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
              break;
            default:
                break;
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                } else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }
        return(result);
    }
}


