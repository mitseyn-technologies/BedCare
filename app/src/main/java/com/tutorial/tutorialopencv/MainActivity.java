package com.tutorial.tutorialopencv;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.Core.normalize;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_64FC1;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.COLORMAP_JET;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.applyColorMap;
import static org.opencv.imgproc.Imgproc.pointPolygonTest;
import static org.opencv.imgproc.Imgproc.threshold;

public class MainActivity extends AppCompatActivity {

    private Bitmap BM_img, BM_img_1;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug()) Toast.makeText(getApplicationContext(),"OpenCV Working",Toast.LENGTH_LONG).show();

        else Toast.makeText(getApplicationContext(),"OpenCV Not Working",Toast.LENGTH_LONG).show();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

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

        Button paint_button = findViewById(R.id.paint_btn);
        paint_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Llama funcion para superponer imagenes
                Bitmap result = colorPaint();
                //Muestra y guarda la imagen
                showImage(result);
            }
        });

        Button detection_button = findViewById(R.id.detection_btn);
        detection_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Cambia al siguiente activity
                //startActivity(new Intent(MainActivity.this, BlobDetectorActivity.class));
                startActivity(new Intent(MainActivity.this, Detector.class));
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
            //Toast.makeText(getApplicationContext(),"I am in if",
                   //Toast.LENGTH_LONG).show();
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

    //Método de pintado de arreglos
    private Bitmap colorPaint()
    {
        /*
        * 1.- Llega el numero flotante de la mlx90640, que indica temperatura.
        * 2.- Dicho numero entra en un arreglo.
        * 3.- Una vez que el arreglo llega a 768 elementos, se procede a trabajarlo.
        * 4.- Se recorrerá el arreglo, verificando las temperaturas.
        * 5.- Si la temperatura es de X°, se elige un color determinado.
        * 6.- Dicho color debe ser introducido como pixel a un Mat.
        * 7.- Una vez se hayan introducido 768 pixeles, se trabaja la imagen.
        * */

        //Primero, declarar el arreglo
        float[] datos_MLX = new float[576];
        Mat modifiedMat;

        /* 2) Llenamos el arreglo - Método 1 (para pruebas) */
        Random r = new Random();
        float min = 28.0f; //Valor mínimo
        float max = 42.0f; //Valor máximo

        //for(int y = 0; y < 24; y++){
        //for(int x = 0; x < 32; x++){
        //        float random = min + r.nextFloat() * (max - min); //Generamos un valor nuevo
        //        datos_MLX[32 * (23-y) + x] = random; //Introducimos el valor
        //    }
        //}

        /* 2) Llenamos el arreglo - Método 2 */
        for(int n = 0; n < 576; n++){
            float random = min + r.nextFloat() * (max - min); //Generamos un valor nuevo
            datos_MLX[n] = random; //Introducimos el valor al arreglo
        }

        //4) Se verifican las temperaturas
        modifiedMat = CrearMat(datos_MLX);
        //modifiedMat.convertTo(modifiedMat, CV_8UC1);


        //Crea la imagen que será devuelta para mostrar
        Bitmap coloredImage = Bitmap.createBitmap(modifiedMat.rows(), modifiedMat.cols(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(modifiedMat, coloredImage);

        imwrite("ibc.jpg", modifiedMat);

        //Devuelve la imagen modificada
        return coloredImage;
    }

    private Mat CrearMat(float[] tempArray)
    {
        Mat paintMat = new Mat(24, 24, CV_64FC1);

        for(int n = 0; n < 576; n++)
        {
            for(int y = 0; y < 24; y++)
            {
                for(int x = 0; x < 24; x++)
                {
                    if(tempArray[n] <= 28.5)
                    {
                        paintMat.put(y,x, new double[]{255, 255, 0});
                    }
                    else if (tempArray[n] <= 29 && tempArray[n] > 28.5)
                    {
                        paintMat.put(y,x, new double[]{255, 245, 0});
                    }
                    else if (tempArray[n] <= 29.5 && tempArray[n] > 29)
                    {
                        paintMat.put(y,x, new double[]{255, 235, 0});
                    }
                    else if (tempArray[n] <= 30 && tempArray[n] > 29.5)
                    {
                        paintMat.put(y,x, new double[]{255, 225, 0});
                    }
                    else if (tempArray[n] <= 30.5 && tempArray[n] > 30)
                    {
                        paintMat.put(y,x, new double[]{255, 215, 0});
                    }
                    else if (tempArray[n] <= 31 && tempArray[n] > 30.5)
                    {
                        paintMat.put(y,x, new double[]{255, 205, 0});
                    }
                    else if (tempArray[n] <= 31.5 && tempArray[n] > 31)
                    {
                        paintMat.put(y,x, new double[]{255, 195, 0});
                    }
                    else if (tempArray[n] <= 32 && tempArray[n] > 31.5)
                    {
                        paintMat.put(y,x, new double[]{255, 185, 0});
                    }
                    else if (tempArray[n] <= 32.5 && tempArray[n] > 32)
                    {
                        paintMat.put(y,x, new double[]{255, 175, 0});
                    }
                    else if (tempArray[n] <= 33 && tempArray[n] > 32.5)
                    {
                        paintMat.put(y,x, new double[]{255, 165, 0});
                    }
                    else if (tempArray[n] <= 33.5 && tempArray[n] > 33)
                    {
                        paintMat.put(y,x, new double[]{255, 155, 0});
                    }
                    else if (tempArray[n] <= 34 && tempArray[n] > 33.5)
                    {
                        paintMat.put(y,x, new double[]{255, 145, 0});
                    }
                    else if (tempArray[n] <= 34.5 && tempArray[n] > 34)
                    {
                        paintMat.put(y,x, new double[]{255, 135, 0});
                    }
                    else if (tempArray[n] <= 35 && tempArray[n] > 34.5)
                    {
                        paintMat.put(y,x, new double[]{255, 125, 0});
                    }
                    else if (tempArray[n] <= 35.5 && tempArray[n] > 35)
                    {
                        paintMat.put(y,x, new double[]{255, 115, 0});
                    }
                    else if (tempArray[n] <= 36 && tempArray[n] > 35.5)
                    {
                        paintMat.put(y,x, new double[]{255, 105, 0});
                    }
                    else if (tempArray[n] <= 36.5 && tempArray[n] > 36)
                    {
                        paintMat.put(y,x, new double[]{255, 95, 0});
                    }
                    else if (tempArray[n] <= 37 && tempArray[n] > 36.5)
                    {
                        paintMat.put(y,x, new double[]{255, 85, 0});
                    }
                    else if (tempArray[n] <= 37.5 && tempArray[n] > 37)
                    {
                        paintMat.put(y,x, new double[]{255, 75, 0});
                    }
                    else if (tempArray[n] <= 38 && tempArray[n] > 37.5)
                    {
                        paintMat.put(y,x, new double[]{255, 65, 0});
                    }
                    else if (tempArray[n] <= 38.5 && tempArray[n] > 38)
                    {
                        paintMat.put(y,x, new double[]{255, 55, 0});
                    }
                    else if (tempArray[n] <= 39 && tempArray[n] > 38.5)
                    {
                        paintMat.put(y,x, new double[]{255, 45, 0});
                    }
                    else if (tempArray[n] <= 39.5 && tempArray[n] > 39)
                    {
                        paintMat.put(y,x, new double[]{255, 35, 0});
                    }
                    else if (tempArray[n] <= 40 && tempArray[n] > 39.5)
                    {
                        paintMat.put(y,x, new double[]{255, 25, 0});
                    }
                    else if (tempArray[n] <= 40.5 && tempArray[n] > 40)
                    {
                        paintMat.put(y,x, new double[]{255, 15, 0});
                    }
                    else if (tempArray[n] <= 41 && tempArray[n] > 40.5)
                    {
                        paintMat.put(y,x, new double[]{255, 5, 0});
                    }
                    else if (tempArray[n] <= 41.5 && tempArray[n] > 41)
                    {
                        paintMat.put(y,x, new double[]{255, 0, 15});
                    }
                    else if (tempArray[n] <= 42 && tempArray[n] > 41.5)
                    {
                        paintMat.put(y,x, new double[]{255, 0, 30});
                    }
                }
            }
        }



        return paintMat;
    }

}
