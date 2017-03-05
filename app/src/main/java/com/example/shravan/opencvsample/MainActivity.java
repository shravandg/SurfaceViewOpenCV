package com.example.shravan.opencvsample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    TouchImageView myTVF;
    ImageView iv;
    double Threshold, myThreshold;
    String fileName;
    public static final String TAG="Arunachala";
    static{
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "Open CV not loaded");
        }
        else
        {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    Camera camera;
    SurfaceView camView;
    SurfaceHolder surfaceHolder;
    boolean camCondition= false;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        camView = (SurfaceView)findViewById(R.id.mySV);
        surfaceHolder = camView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

        b= (Button)findViewById(R.id.myB);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, null, mPictureCallback);
            }
        });
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            FileOutputStream outStream = null;
            File outFile = null;
            try{
                File MyDir = Environment.getExternalStorageDirectory();
                File dir = new File(MyDir.getAbsolutePath() + "/SurfaceView");
                dir.mkdirs();

                 fileName = "Image_" + System.currentTimeMillis() + ".jpg";
                outFile = new File(dir, fileName);
                outStream = new FileOutputStream(outFile);

 //               outStream = new FileOutputStream("/SurfaceView/Sample_"+System.currentTimeMillis()+".jpg");
                outStream.write(bytes);
                outStream.close();
                print("file saved at "+outFile.getAbsolutePath());
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }finally{
            }
            myAlert();
            refreshCamera();
        }
    };

    public void refreshCamera(){
        if(surfaceHolder.getSurface() == null){
            return;
        }
        try{
            camera.stopPreview();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
        }
        catch(RuntimeException e){
            e.printStackTrace();
        }
        Camera.Parameters param;
        param = camera.getParameters();
        param.setPreviewSize(352,288);
        camera.setParameters(param);
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        camCondition = false;
    }

    public void myAlert(){

        print("my alert called");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SurfaceView", fileName);
        print("got file and file path is: "+file.getAbsolutePath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        print("decoded bitmap");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View myView;
        myView = inflater.inflate(R.layout.postimage, (ViewGroup)findViewById(R.id.alertlayout));

        print("builder inflated and set to the view");
        builder.setTitle("Image analysis");
        builder.setView(myView);
        print("View set");
        //myTVF.setImageBitmap(null);
        //iv= (ImageView)myView.findViewById(R.id.imageView);
        myTVF = (TouchImageView)myView.findViewById(R.id.img);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rbmImg= Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        myTVF.setImageBitmap(rbmImg);

       // myTVF.setRotation(myTVF.getRotation()+90);
        print("bitmap set");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                seekbarAlert();
            }
        }, 2000);
       // seekbarAlert();
        builder.setPositiveButton("Process", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BitmapDrawable bd = (BitmapDrawable) myTVF.getDrawable();
                Bitmap bmp = bd.getBitmap();
                Save save = new Save();
                File file = null;
                file = save.saveImage(bmp);
                refreshGallery(file);
                print("I am here");
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public void seekbarAlert(){
        final AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        final LayoutInflater inflater = LayoutInflater.from(getApplicationContext() );
        final View myV = inflater.inflate(R.layout.seekbarlayout, null);
        builder.setView(myV);
        final TextView tv = (TextView)myV.findViewById(R.id.myTV);
        //tv.setTypeface(null, Typeface.BOLD);
        builder.setIcon(android.R.drawable.sym_def_app_icon);
        builder.setTitle("Select the Threshold!");
        SeekBar seek = (SeekBar)myV.findViewById(R.id.mySB);
        seek.setMax(20);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = ((float)i / 10.0f);
                tv.setText(" Threshold is: "+value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Threshold = (double) Math.round(myThreshold * 10) / 10;
                tv.setText("Threshold value is: "+ Threshold);
                toast("Selected threshold is: "+Threshold);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Threshold = 0.5;
                toast("Default threshold is: "+Threshold);
                tv.setText("Threshold value is: "+ Threshold);
                dialogInterface.dismiss();
            }
        });

        builder.create();
        builder.show();
    }

    public void print(String s){
        Log.d(TAG,s);
    }

    public void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

public void toast(){
    Toast.makeText(this," Processing! ",Toast.LENGTH_LONG);
}

    public void toast(String s){
        Toast.makeText(this, s,Toast.LENGTH_LONG);
    }
}
