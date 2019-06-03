package com.tutorial.tutorialopencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Detector extends AppCompatActivity {

    private static final String TAG = "Detector";

    ImageView imgView;
    Bitmap BM_img;
    public TextView colorText;

    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detector_layout);

        imgView = findViewById(R.id.imageViewDetector);
        BM_img = BitmapFactory.decodeResource(this.getResources(), R.drawable.hand);
        colorText = findViewById(R.id.colorText);

        Button processBtn = findViewById(R.id.process_btn);
        processBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processImage();
                findDominantColor(BM_img);
            }
        });
    }

    private void processImage() {

        //reading input image from internal storage.
        //Mat img = imread(Environment.getExternalStorageDirectory().getAbsolutePath() +"bedCare.png");
        //Mat oImg = detectColor(img);

        Mat conversion = new Mat();
        Utils.bitmapToMat(BM_img, conversion);
        Mat oImg = detectColor(conversion);

        // converting image from Mat to bitmap to display in ImageView:
        Bitmap bm = Bitmap.createBitmap(oImg.cols(), oImg.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(oImg, bm);

        //Setea el imageView
        imgView.setImageBitmap(bm);
    }

    Mat detectColor(Mat srcImg) {
        Mat blurImg = new Mat();
        Mat hsvImage = new Mat();
        Mat color_range = new Mat();

        //Bluring image to filter small noises.
        Imgproc.GaussianBlur(srcImg, blurImg, new Size(5, 5), 0);

        //Converting blured image from BGR to HSV
        Imgproc.cvtColor(blurImg, hsvImage, Imgproc.COLOR_RGB2HSV);

        //Filtering pixels based on given HSV color range
        Core.inRange(hsvImage, new Scalar(0, 150, 50), new Scalar(15, 255, 255), color_range);

        return color_range;
    }

    ////////////////////////////////////////////////////////////////////////
    ///////////////////////// COLOR FINDER /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public void findDominantColor(Bitmap bitmap) {
        new GetDominantColor().execute(bitmap);
    }

    private int getDominantColorFromBitmap(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        Map<Integer, PixelObject> pixelList = getMostDominantPixelList(pixels);
        return getDominantPixel(pixelList);
    }

    private Map<Integer, PixelObject> getMostDominantPixelList(int[] pixels) {
        Map<Integer, PixelObject> pixelList = new HashMap<>();

        for (int pixel : pixels) {
            if (pixelList.containsKey(pixel)) {
                pixelList.get(pixel).pixelCount++;
            } else {
                pixelList.put(pixel, new PixelObject(pixel, 1));
            }
        }
        return pixelList;
    }

    private int getDominantPixel(Map<Integer, PixelObject> pixelList) {
        int dominantColor = 0;
        int largestCount = 0;
        for (Map.Entry<Integer, PixelObject> entry : pixelList.entrySet()) {
            PixelObject pixelObject = entry.getValue();

            if (pixelObject.pixelCount > largestCount) {
                largestCount = pixelObject.pixelCount;
                dominantColor = pixelObject.pixel;
            }
        }

        return dominantColor;
    }

    private class GetDominantColor extends AsyncTask<Bitmap, Integer, Integer> {

        //public int dominantColor;

        @Override
        protected Integer doInBackground(Bitmap... params) {
            int dominantColor = getDominantColorFromBitmap(params[0]);
            return dominantColor;
        }

        @Override
        protected void onPostExecute(Integer dominantColor) {
            String hexColor = colorToHex(dominantColor);
            colorText.setText(hexColor);
        }

        private String colorToHex(int color) {
            return String.format("#%06X", (0xFFFFFF & color));
        }
    }
}