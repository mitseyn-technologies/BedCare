package com.tutorial.tutorialopencv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ImageView img_1, img_2;
    private Bitmap BM_img, BM_img_1;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Comentario Git

        if(OpenCVLoader.initDebug()) Toast.makeText(getApplicationContext(),"OpenCV Working",Toast.LENGTH_LONG).show();

        else Toast.makeText(getApplicationContext(),"OpenCV Not Working",Toast.LENGTH_LONG).show();

        BM_img = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.hand);

        BM_img_1 = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.hand_two);



        if (BM_img_1 == null){

            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
        }

        Bitmap result = combineImages(BM_img,BM_img_1);

        ImageView image = findViewById(R.id.imgView);

        image.setImageBitmap(result);

        saveToInternalStorage(result);


    }


    //Metodo para Concatenar imagenes
    public Bitmap combineImages(Bitmap firstImage, Bitmap secondImage){

        Bitmap cs = null;//nuevo BitMap

        int width, height = 0;//nuebo ancho y largo

        if(firstImage.getWidth() > secondImage.getWidth()) {
            width = firstImage.getWidth() + secondImage.getWidth();
            height = firstImage.getHeight();
        } else {
            width = secondImage.getWidth() + secondImage.getWidth();
            height = firstImage.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(firstImage, 0f, 0f, null);
        comboImage.drawBitmap(secondImage, firstImage.getWidth(), 0f, null);

        return cs;
    }

    //Metodo para almacenar en memoria
    private void saveToInternalStorage(Bitmap bitmap){
        String dir = Environment.getExternalStorageDirectory()+ File.separator+"DCIM/bedCare";

        //createfolder
        File folder = new File(dir);
        if(!folder.exists()){
            folder.mkdirs();
            Toast.makeText(getApplicationContext(),"I am in if",
                    Toast.LENGTH_LONG).show();
        }
        //creatname file
        String simpleDate=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nameImage="STE_"+simpleDate;

        //create file
        OutputStream outputStream;
        File file = new File(dir,nameImage+".png");

        try {
            outputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
