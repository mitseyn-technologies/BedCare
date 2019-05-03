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
import android.view.View;
import android.widget.Button;
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

    private Bitmap BM_img, BM_img_1;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug()) Toast.makeText(getApplicationContext(),"OpenCV Working",Toast.LENGTH_LONG).show();

        else Toast.makeText(getApplicationContext(),"OpenCV Not Working",Toast.LENGTH_LONG).show();

        //Declaración de imagenes
        BM_img = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.hand);

        BM_img_1 = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.hand_two);

        //Comprueba que las imagenes estén OK
        imageCheck(BM_img, BM_img_1);

        //Botones para activar los métodos
        Button concat_button = findViewById(R.id.cctn_btn);
        concat_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Llama funcion para combinar imagenes
                Bitmap result = combineImages(BM_img, BM_img_1);
                //Muestra y guarda la imagen
                showImage(result);
            }
        });

        Button overlap_button = findViewById(R.id.ovlp_btn);
        overlap_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Llama funcion para superponer imagenes
                Bitmap result = overlapImages(BM_img, BM_img_1);
                //Muestra y guarda la imagen
                showImage(result);
            }
        });
    }

    public void imageCheck(Bitmap img1, Bitmap img2){
        //Comprobación de las imagenes
        if (img1 == null){
            Toast.makeText(getApplicationContext(),"Error, no se pudo leer la imagen: " + BM_img,Toast.LENGTH_LONG).show();
        }
        else if (img2 == null){
            Toast.makeText(getApplicationContext(),"Error, no se pudo leer la imagen: " + BM_img_1,Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Imagenes OK",Toast.LENGTH_SHORT).show();
        }
    }

    public void showImage(Bitmap result){
        //Obtiene el imageView
        ImageView image = findViewById(R.id.imgView);
        //Muestra la imagen en el imageView
        image.setImageBitmap(result);
        //Guarda la imagen en el celular
        saveToInternalStorage(result);
    }

    //Método para superponer imagenes
    public Bitmap overlapImages(Bitmap firstImage, Bitmap secondImage){

        Bitmap oi;

        oi = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(oi);
        //Primera imagen. No modificar valores.
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        /*Segunda imagen:
            -El primer valor numérico indica que tan a la izquierda será desplazada la imagen.
            -El segundo valor numérico indica que tan hacia abajo será desplazada la imagen.
            -Los valores no indican pixeles (aún no estoy seguro que indican).
        */
        canvas.drawBitmap(secondImage, 0f, 40f, null);
        return oi;
    }


    //Método para concatenar imagenes
    public Bitmap combineImages(Bitmap firstImage, Bitmap secondImage){

        //Nuevo Bitmap
        Bitmap cs;

        //Nuevo ancho y largo
        int width, height;

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

        //create folder
        File folder = new File(dir);
        if(!folder.exists()){
            folder.mkdirs();
            Toast.makeText(getApplicationContext(),"I am in if",
                    Toast.LENGTH_LONG).show();
        }
        //create name file
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
