package com.example.shravan.opencvsample;

/**
 * Created by Shravan on 3/5/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Save extends Activity {
    String path;
    public static String MyDate;
    private final String TAG = "Arunachala";

    public void saveImage1(Bitmap ImageToSave) {
        String myDate = getCurrentDateAndTime();
        FileOutputStream outStream = null;
        try {
            File MyDir = Environment.getExternalStorageDirectory();
            File dir = new File(MyDir.getAbsolutePath() + "/SurfaceView");
            print(dir.getName());
            dir.mkdirs();

            String fileName = "Image_" /*+ myDate*/ + ".jpg";
            File outFile = new File(dir, fileName);

            outStream = new FileOutputStream(outFile);
            ImageToSave.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            path = outFile.getAbsolutePath();
            print("File saved at " + path);
            //refreshGallery(outFile);

        } catch (FileNotFoundException e) {
            print("FileNotFound exception");
            e.printStackTrace();
        } catch (IOException e) {
            print("IOException");
            e.printStackTrace();
        } finally {
        }
    }


    public File saveImage(Bitmap ImageToSave) {
        MyDate = getCurrentDateAndTime();
        FileOutputStream outStream = null;
        File outFile = null;
        try {
            File MyDir = Environment.getExternalStorageDirectory();
            File dir = new File(MyDir.getAbsolutePath() + "/SurfaceView");
            dir.mkdirs();

            String fileName = "Image_" + MyDate + ".jpg";
            outFile = new File(dir, fileName);

            outStream = new FileOutputStream(outFile);
            ImageToSave.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            path = outFile.getAbsolutePath();
            print("File saved at " + path);
            //refreshGallery(outFile);
            //return outFile;

        } catch (FileNotFoundException e) {
            print("FileNotFound exception");
            e.printStackTrace();
        } catch (IOException e) {
            print("IOException");
            e.printStackTrace();
        } finally {

        }
        return outFile;
    }

    public void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private void print(String str){
        Log.d(TAG, str);
    }

}
