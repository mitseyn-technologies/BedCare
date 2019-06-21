package com.tutorial.tutorialopencv;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.Core.minMaxLoc;
import static org.opencv.core.Core.normalize;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_64FC1;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
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

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FILE_NAME = "bedcare_data.txt";
    private Bitmap BM_img, BM_img_1, imagenMLX;
    private double[] datos_MLX = new double[768];

    private BluetoothAdapter BTAdapter;
    private BluetoothLeScanner BLE_detector;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ScanSettings Configuración_Escaneo;
    private List<ScanFilter> Dispositivos_BT;
    private boolean ScaneoViejo = true;

    private boolean BTConnectado = false;
    private int Tiempo_Espera = 1000;
    private int Periodo_Escaneo = 14000;
    private Timer Rescanear = new Timer();
    private final Handler Auxiliar_Escaneo = new Handler();
    private String NombreAhora;
    private String DirecciónAhora;
    private int RSSI;
    private String FiltrarBT = null;
    private String NombreAntes = "BedCare";
    private String DireccionAntes = "FA:03:AE:59:51:AD";
    private final Handler Handler_Ble = new Handler();
    private Datos_BLE DatosBLE;
    private ListView BLE_List;
    private int NumeroBT = 0;
    private boolean ArrayEmpty;
    private String direccion_BtSelect;
    private String nombre_BtSelect;
    private BLE_Service myBleService = new BLE_Service();
    private boolean BTConnected_Bef = false;
    private String ValorRecibido = null;
    //private int arrayCounter = 0;
    private int contador_pixel = 0;
    private int contador_datos = 0;
    private double[] vector_pixel = new double[768];
    private TextView conexion_txt;
    private double[] arregloDobles = new double[768];
    private double pixelFlotante;
    private boolean arrayIsDone = false;
    private boolean isNewArrayReady = true;


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

        if (Build.VERSION.SDK_INT < 21) {
            ScaneoViejo = true;
        } else {
            ScaneoViejo = false;
        }

        Ini_Detect_BLE();
        PermisoUbicación();

        IntentFilter BT_estado = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(BLE_broadcast, BT_estado);
        Intent gattServiceIntent = new Intent(this, BLE_Service.class);
        startService(gattServiceIntent);
        bindService(gattServiceIntent, my_Service_Basic, BIND_AUTO_CREATE);

        datos_MLX = new double[768];

        //Declaración de imagenes
        BM_img = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.hand);

        BM_img_1 = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.hand_two);

        //Comprueba que las imagenes estén OK
        //imageCheck(BM_img, BM_img_1);

        TextView conexion_txt = findViewById(R.id.conexion_txt);

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

        Button conection_button = findViewById(R.id.conection_btn);
        conection_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //myBleService.Conectar(DireccionAntes, NombreAntes);
                NuevaBusqueda_BLE(true);
            }
        });

        Button paint_button = findViewById(R.id.paint_btn);
        paint_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Llama funcion para superponer imagenes
                //if(imagenMLX != null)
                //{
                    //Muestra y guarda la imagen
                    //showImage(imagenMLX);
                    showImage(colorPaint(arregloPruebas()));
                    //Maybe fill the rest of the image with zeros?
                /*}
                else
                {
                    Toast.makeText(getApplicationContext(),"No hay datos para pintar",Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!BTConnectado) {
            //Scan_loop(true);
        } else {
            Scan_loop(false);
        }
        registerReceiver(myGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        StopTimers();
        if (BTConnectado == false) {
            //Scan_loop(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StopTimers();
        unregisterReceiver(myGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(BLE_broadcast);
        myBleService.StopLoop();
        myBleService.Desconectar();
        stopService(new Intent(this, BLE_Service.class));
        unbindService(my_Service_Basic);
        myBleService = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CloseActivity();
    }

    private void CloseActivity() {

        if (BTConnectado) {
            myBleService.StopLoop();
            myBleService.Desconectar();
            stopService(new Intent(this, BLE_Service.class));
        }
        StopTimers();
        this.finish();
    }

    private void Scan_loop(boolean Rescan) {

        long tiempo_Recall = Tiempo_Espera + Periodo_Escaneo;
        if (Rescan) {
            Rescanear = new Timer();
            Rescanear.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Auxiliar_Escaneo.post(new Runnable() {
                        @Override
                        public void run() {
                            if (BTAdapter.isEnabled()) {
                                if (ScaneoViejo)
                                    ViejaBusquedaBLE(true);
                                else
                                    NuevaBusqueda_BLE(true);
                            }
                        }
                    });
                }
            }, 0, tiempo_Recall);

        }
        if (!Rescan) {
            if (ScaneoViejo) {
                ViejaBusquedaBLE(false);
            } else {
                NuevaBusqueda_BLE(false);
            }
            Rescanear.cancel();
            Rescanear.purge();
        }
    }

    private void Ini_Detect_BLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            this.finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BTAdapter = Objects.requireNonNull(bluetoothManager).getAdapter();
        if (BTAdapter == null || !BTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {

            if (!ScaneoViejo) {
                BLE_detector = BTAdapter.getBluetoothLeScanner();
                Configuración_Escaneo = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_OPPORTUNISTIC).build();
                Dispositivos_BT = new ArrayList<>();
                // DispBLE_Scan.add(new ScanFilter.Builder().setDeviceName(Search_DeviceName2).build());
            }

        }
    }

    private final BroadcastReceiver BLE_broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String Act_BLE = intent.getAction();
            int BT_Cambio = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(Act_BLE)) {
                switch (BT_Cambio) {
                    case BluetoothAdapter.STATE_OFF: {
                        BTAdapter.enable();
                        // CloseActivity();
                        break;
                    }
                }
            }
        }
    };

    private void PermisoUbicación() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @RequiresApi(21)
    private void NuevaBusqueda_BLE(final boolean NewEnable) {

        final ScanCallback ReScanBLE_NewApi = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                //Toast.makeText(MainActivity.this, "escaneando", Toast.LENGTH_SHORT).show();
                NombreAhora = result.getDevice().getName();
                DirecciónAhora = result.getDevice().getAddress();
                RSSI = result.getRssi();
                    if (((/*!Objects.equals(NombreAhora, NombreAntes)) && (!Objects.equals(DirecciónAhora, DireccionAntes))) &&*/ (Objects.equals(NombreAhora, "BedCare"))))) {
                        Toast.makeText(MainActivity.this, NombreAhora, Toast.LENGTH_SHORT).show();
                        DatosBLE = new Datos_BLE(NombreAhora, DirecciónAhora);
                        NombreAntes = NombreAhora;
                        DireccionAntes = DirecciónAhora;

                        Toast.makeText(MainActivity.this, " Nombre seleccion: " + direccion_BtSelect, Toast.LENGTH_SHORT).show();
                        myBleService.Conectar(DireccionAntes, NombreAntes);
                        //conexion_txt.setText("Estado: Conectado");
                    }
                }
        };

        if (NewEnable) {
            Handler_Ble.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BLE_detector.stopScan(ReScanBLE_NewApi);
                }
            }, Periodo_Escaneo);

            BLE_detector.startScan(ReScanBLE_NewApi);
        } else {
            BLE_detector.stopScan(ReScanBLE_NewApi);
        }
    }

    private void ViejaBusquedaBLE(final boolean OldEnable) {
        final BluetoothAdapter.LeScanCallback ReScanBLE_OldApi = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NombreAhora = device.getName();
                        DirecciónAhora = device.getAddress();
                        RSSI = rssi;

                        if (FiltrarBT != null) {
                            if (((!Objects.equals(NombreAhora, NombreAntes)) && (!Objects.equals(DirecciónAhora, DireccionAntes))) && (Objects.equals(NombreAhora, FiltrarBT))) {
                                DatosBLE = new Datos_BLE(NombreAhora, DirecciónAhora);
                                NombreAntes = NombreAhora;
                                DireccionAntes = DirecciónAhora;
                            }
                        }
                    }
                });
            }
        };

        if (OldEnable) {
            Handler_Ble.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BTAdapter.stopLeScan(ReScanBLE_OldApi);
                }
            }, Periodo_Escaneo);
            BTAdapter.startLeScan(ReScanBLE_OldApi);
        } else {
            BTAdapter.stopLeScan(ReScanBLE_OldApi);
        }
    }

    private void StopTimers() {
        if (!BTConnectado) {
            Rescanear.cancel();
            Rescanear.purge();
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_Service.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLE_Service.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLE_Service.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLE_Service.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final ServiceConnection my_Service_Basic = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            myBleService = ((BLE_Service.myLocalBinder) service).getService();
            if (myBleService.ini_invert()) {
                Log.i(TAG, "BT no inicializado");
                finish();
            } else {
                Log.i(TAG, "BT inicializado");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myBleService = null;
        }
    };

    private final BroadcastReceiver myGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (Objects.requireNonNull(action)) {
                case BLE_Service.ACTION_GATT_CONNECTED:
                    BTConnectado = true;
                    if (BTConnected_Bef != BTConnectado) {
                        Scan_loop(false);
                        BTConnected_Bef = BTConnectado;
                    }
                    break;
                case BLE_Service.ACTION_GATT_DISCONNECTED:
                    BTConnectado = false;
                    if (BTConnected_Bef != BTConnectado) {

                        BTConnected_Bef = BTConnectado;
                    }
                    break;
                case BLE_Service.ACTION_GATT_SERVICES_DISCOVERED:
                    //if (FiltrarBT.equals("BedCare")) {
                    Log.i(TAG, "Services discovered");
                    myBleService.BuscarBedCareService();
                    //}
                    break;

                //case BLE_Service.ACTION_DATA_AVAILABLE:
                    //ValorRecibido = intent.getStringExtra(BLE_Service.EXTRA_DATA);
                    //Log.i(TAG, "hay datos");
                    //if (ValorRecibido != null) {
                        //byte[] ValorEnBytes = ValorRecibido.getBytes();
                        //Log.i(TAG, "valor en string:" + ValorRecibido);
                        //Log.i(TAG, "valor en bytes puros: " + ValorEnBytes);
                        //Log.i(TAG, "valor en bytes string: " + ValorEnBytes.toString());
                        //Log.i(TAG, "valor en parse bytes: " + Byte.parseByte(ValorRecibido));
                        //double miDoble = ByteBuffer.wrap(ValorRecibido).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                        //datos_MLX[arrayCounter] = miDoble;
                        //arrayCounter++;
                        //if(arrayCounter < 768)
                        //{
                        //   imagenMLX = colorPaint(datos_MLX);
                        //   arrayCounter = 0;
                        //   Toast.makeText(MainActivity.this, "Se ha recibido un arreglo completo", Toast.LENGTH_SHORT).show();
                        //}
                    //}
                    //break;

                case BLE_Service.ACTION_DATA_AVAILABLE:
                    ValorRecibido = intent.getStringExtra(BLE_Service.EXTRA_DATA);

                    if (ValorRecibido != null) {
                        //Si pasó un frame, empieza a contar de nuevo.
                        if(contador_datos >= 767) {
                            Log.i(TAG, "Nuevo frame iniciado");
                            contador_datos = 0;
                        }

                        //Declara que el nuevo arreglo está listo sólo si el arreglo anterior ya pasó y está llegando un frame nuevo.
                        if(arrayIsDone == false && contador_datos == 0)
                            isNewArrayReady = true;

                        //Cuenta todos los datos que han llegado, ignorados o no.
                        contador_datos++;
                    }

                    //Solo puede entrar si está recibiendo un frame totalmente nuevo y el frame anterior ya está listo
                    if (ValorRecibido != null  && isNewArrayReady == true) {
                        if (contador_pixel <= 767) {
                            pixelFlotante = todosflotan(ValorRecibido);
                            vector_pixel[contador_pixel] = pixelFlotante;
                            //datos_MLX = vector_pixel;
                            /*if(contador_pixel == 200) {
                                writeToFile(datos_MLX);
                            }*/
                            contador_pixel++;
                        }
                        else
                        {
                            datos_MLX = vector_pixel;
                            arrayIsDone = true;
                            isNewArrayReady = false;
                            //writeToFile(datos_MLX);
                            showImage(colorPaint(datos_MLX));
                            //showImage(colorPaint(arregloPruebas()));
                            contador_pixel = 0;
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    datos_MLX = null;
                                    arrayIsDone = false;
                                    Log.i(TAG, "Listo para recibir otro arreglo");
                                }
                            }, 7000);
                        }
                    }
                    break;
            }
        }
    };

    private double todosflotan(String Input) {
        String Input_prime = Input;
        Log.i(TAG, "Input: " + Input_prime);
        String auxVar = Input_prime.replaceAll("\\s+", "");
        String[] auxArray = {"0", "0", "0", "0", "0", "0", "0", "0"};
        String[] newAuxArray = {"0", "0", "0", "0", "0", "0", "0", "0"};
        String[] basuraArray = auxVar.split("");
        for (int i = 0; i < auxArray.length - 1; i++) {
            auxArray[(auxArray.length - 1) - i] = basuraArray[(basuraArray.length - 1) - i];
        }


        //Los datos llegan codificados en formato Big Endian desde la cámara. Para leerlos correctamente, hay que traspasarlos a Little Endian,
        //que es un formato de codificación similar. La única diferencia entre ambos, es que uno es la versión invertida del otro. Por tanto,
        //para obtener el número correcto, hay que invertir el arreglo antes de transformarlo.
        //
        //EJ:
        //String[] auxArray = {"4","1","B","C","D","1","E","9"}; Datos de la cámara (Big Endian)
        //String[] newAuxArray = {"E","9","D","1","B","C","4","1"}; Datos invertidos (Little Endian)


        //Arreglo hardcodeado porque se me hizo menos complejo y más corto que hacer el for.
        //Se puede cambiar por un for más adelante.

        newAuxArray[0] = auxArray[6];
        newAuxArray[1] = auxArray[7];
        newAuxArray[2] = auxArray[4];
        newAuxArray[3] = auxArray[5];
        newAuxArray[4] = auxArray[2];
        newAuxArray[5] = auxArray[3];
        newAuxArray[6] = auxArray[0]; //<-- Aqui hay un problema. Lo que llegue es reemplazado por un 0 el 100% de las veces.
        newAuxArray[7] = auxArray[1]; //    No parece alterar el numero final más de 0.001 unidades, pero es importante saberlo.

        StringBuilder builder2 = new StringBuilder();
        for (String s : newAuxArray) {
            builder2.append(s);
        }

        String DatosInvertidos = builder2.toString();

        Long floatInvertido = Long.parseLong(DatosInvertidos, 16);
        Log.i(TAG, "Input invertida: " + DatosInvertidos);

        //float enFlotante = Float.intBitsToFloat(paraFlotar.intValue());
        //Log.i(TAG, "FLOTANTE: " + enFlotante);

        float enFlotanteInvertido = Float.intBitsToFloat(floatInvertido.intValue());
        Log.i(TAG, "FLOTANTE INVERTIDO: " + enFlotanteInvertido);

        //double d = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN ).getDouble();
        //Log.i(TAG, "DOUBLE: " + d);

        double enDouble = enFlotanteInvertido;
        return enDouble;
    }

    public void showImage(Bitmap result)
    {
        //Obtiene el imageView
        ImageView image = findViewById(R.id.imgView);
        //Muestra la imagen en el imageView
        image.setImageBitmap(result);
        //Guarda la imagen en el celular
        //saveToInternalStorage(result);
    }

    //Método para superponer imagenes
    public Bitmap overlapImages(Bitmap firstImage, Bitmap secondImage)
    {

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
    private void saveToInternalStorage(Bitmap bitmap)
    {
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

    public void writeToFile(double[] array)
    {
        String[] numbers = new String[768];

        for (int i = 0; i < array.length; i++) {
            numbers[i] = "("+ i +") " + (String.valueOf(array[i])) + "\n";
        }

        StringBuilder builder = new StringBuilder();
        for(String s : numbers) {
            builder.append(s);
        }
        String str = builder.toString();

        String path = Environment.getExternalStorageDirectory() + File.separator  + "/DCIM/bedCare";
        // Create the folder.
        File folder = new File(path);
        folder.mkdirs();

        // Create the file.
        File file = new File(folder, "bedcareData.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(str);
            myOutWriter.append(" - Largo del array recibido: " + array.length);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    //Método de pintado de arreglos
    private Bitmap colorPaint(double[] array)
    {
        //Test
        //Random r = new Random();
        //float min = 28.0f; //Valor mínimo
        //float max = 42.0f; //Valor máximo

        //Test2
        //for(int n = 0; n < 768; n++){
        //    float random = min + r.nextFloat() * (max - min); //Generamos un valor nuevo
        //    datos_MLX[n] = random; //Introducimos el valor al arreglo
        //}

        //Arreglo de pruebas
        //double[] testArray = arregloPruebas();

        //4) Se verifican las temperaturas

        //Crea la imagen que será devuelta para mostrar
        Bitmap coloredImage = Bitmap.createBitmap(24, 32, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(CrearMat2(array), coloredImage);

        //Devuelve la imagen modificada
        return coloredImage;
    }

    private Mat CrearMat2(double[] tempArray)
    {
        Mat paintMat = new Mat(32,24,CV_8UC3);
        double buff[]= new double[tempArray.length * 3];
        int p = 0;

        for(int n = 0; n < tempArray.length; n ++)

            if (tempArray[n] <=0.5&& tempArray[n] > 0.0){
                buff[p]= 0 ;buff[p+1]=  0 ;buff[p+2]=  0  ;
                p = p+3;}
            else if (tempArray[n] <=1.0&& tempArray[n] > 0.5){
                buff[p]= 0 ;buff[p+1]=  0 ;buff[p+2]=  36 ;
                p = p+3;}
            else if (tempArray[n] <=1.5&& tempArray[n] > 1.0){
                buff[p]= 0 ;buff[p+1]=  0 ;buff[p+2]=  51  ;
                p = p+3;}
            else if (tempArray[n] <=2.0&& tempArray[n] > 1.5){
                buff[p]= 0 ;buff[p+1]=  0 ;buff[p+2]=  66  ;

                p = p+3;}
            else if (tempArray[n] <=2.5&& tempArray[n] > 2.0){
                buff[p]= 0 ;buff[p+1]=  0 ;buff[p+2]=  81  ;

                p = p+3;}
            else if (tempArray[n] <=3.0&& tempArray[n] > 2.5){
                buff[p]= 2 ;buff[p+1]=  0 ;buff[p+2]=  90  ;

                p = p+3;}
            else if (tempArray[n] <=3.5&& tempArray[n] > 3.0){
                buff[p]= 4 ;buff[p+1]=  0 ;buff[p+2]=  99  ;

                p = p+3;}
            else if (tempArray[n] <=4.0&& tempArray[n] > 3.5){
                buff[p]= 7 ;buff[p+1]=  0 ;buff[p+2]=  106  ;

                p = p+3;}
            else if (tempArray[n] <=4.5&& tempArray[n] > 4.0){
                buff[p]= 11 ;buff[p+1]=  0 ;buff[p+2]=  115  ;

                p = p+3;}
            else if (tempArray[n] <=5.0&& tempArray[n] > 4.5){
                buff[p]= 14 ;buff[p+1]=  0 ;buff[p+2]=  119  ;

                p = p+3;}
            else if (tempArray[n] <=5.5&& tempArray[n] > 5.0){
                buff[p]= 20 ;buff[p+1]=  0 ;buff[p+2]=  123  ;

                p = p+3;}
            else if (tempArray[n] <=6.0&& tempArray[n] > 5.5){
                buff[p]= 27 ;buff[p+1]=  0 ;buff[p+2]=  128  ;

                p = p+3;}
            else if (tempArray[n] <=6.5&& tempArray[n] > 6.0){
                buff[p]= 33 ;buff[p+1]=  0 ;buff[p+2]=  133  ;

                p = p+3;}
            else if (tempArray[n] <=7.0&& tempArray[n] > 6.5){
                buff[p]= 41 ;buff[p+1]=  0 ;buff[p+2]=  137  ;

                p = p+3;}
            else if (tempArray[n] <=7.5&& tempArray[n] > 7.0){
                buff[p]= 48 ;buff[p+1]=  0 ;buff[p+2]=  140  ;

                p = p+3;}
            else if (tempArray[n] <=8.0&& tempArray[n] > 7.5){
                buff[p]= 55 ;buff[p+1]=  0 ;buff[p+2]=  143  ;

                p = p+3;}
            else if (tempArray[n] <=8.5&& tempArray[n] > 8.0){
                buff[p]= 61 ;buff[p+1]=  0 ;buff[p+2]=  146  ;

                p = p+3;}
            else if (tempArray[n] <=9.0&& tempArray[n] > 8.5){
                buff[p]= 66 ;buff[p+1]=  0 ;buff[p+2]=  149  ;

                p = p+3;}
            else if (tempArray[n] <=9.5&& tempArray[n] > 9.0){
                buff[p]= 72 ;buff[p+1]=  0 ;buff[p+2]=  150  ;

                p = p+3;}
            else if (tempArray[n] <=10.0&& tempArray[n] > 9.5){
                buff[p]= 78 ;buff[p+1]=  0 ;buff[p+2]=  151  ;

                p = p+3;}
            else if (tempArray[n] <=10.5&& tempArray[n] > 10.0){
                buff[p]= 84 ;buff[p+1]=  0 ;buff[p+2]=  152  ;

                p = p+3;}
            else if (tempArray[n] <=11.0&& tempArray[n] > 10.5){
                buff[p]= 91 ;buff[p+1]=  0 ;buff[p+2]=  153  ;

                p = p+3;}
            else if (tempArray[n] <=11.5&& tempArray[n] > 11.0){
                buff[p]= 97 ;buff[p+1]=  0 ;buff[p+2]=  155  ;

                p = p+3;}
            else if (tempArray[n] <=12.0&& tempArray[n] > 11.5){
                buff[p]= 104 ;buff[p+1]=  0 ;buff[p+2]=  155  ;

                p = p+3;}
            else if (tempArray[n] <=12.5&& tempArray[n] > 12.0){
                buff[p]= 110 ;buff[p+1]=  0 ;buff[p+2]=  156  ;

                p = p+3;}
            else if (tempArray[n] <=13.0&& tempArray[n] > 12.5){
                buff[p]= 115 ;buff[p+1]=  0 ;buff[p+2]=  157  ;

                p = p+3;}
            else if (tempArray[n] <=13.5&& tempArray[n] > 13.0){
                buff[p]= 122 ;buff[p+1]=  0 ;buff[p+2]=  157  ;

                p = p+3;}
            else if (tempArray[n] <=14.0&& tempArray[n] > 13.5){
                buff[p]= 128 ;buff[p+1]=  0 ;buff[p+2]=  157  ;

                p = p+3;}
            else if (tempArray[n] <=14.5&& tempArray[n] > 14.0){
                buff[p]= 134 ;buff[p+1]=  0 ;buff[p+2]=  157  ;

                p = p+3;}
            else if (tempArray[n] <=15.0&& tempArray[n] > 14.5){
                buff[p]= 139 ;buff[p+1]=  0 ;buff[p+2]=  157  ;

                p = p+3;}
            else if (tempArray[n] <=15.5&& tempArray[n] > 15.0){
                buff[p]= 146 ;buff[p+1]=  0 ;buff[p+2]=  156  ;

                p = p+3;}
            else if (tempArray[n] <=16.0&& tempArray[n] > 15.5){
                buff[p]= 152 ;buff[p+1]=  0 ;buff[p+2]=  155  ;

                p = p+3;}
            else if (tempArray[n] <=16.5&& tempArray[n] > 16.0){
                buff[p]= 157 ;buff[p+1]=  0 ;buff[p+2]=  155  ;

                p = p+3;}
            else if (tempArray[n] <=17.0&& tempArray[n] > 16.5){
                buff[p]= 162 ;buff[p+1]=  0 ;buff[p+2]=  155  ;

                p = p+3;}
            else if (tempArray[n] <=17.5&& tempArray[n] > 17.0){
                buff[p]= 167 ;buff[p+1]=  0 ;buff[p+2]=  154  ;

                p = p+3;}
            else if (tempArray[n] <=18.0&& tempArray[n] > 17.5){
                buff[p]= 171 ;buff[p+1]=  0 ;buff[p+2]=  153  ;

                p = p+3;}
            else if (tempArray[n] <=18.5&& tempArray[n] > 18.0){
                buff[p]= 175 ;buff[p+1]=  1 ;buff[p+2]=  152  ;

                p = p+3;}
            else if (tempArray[n] <=19.0&& tempArray[n] > 18.5){
                buff[p]= 178 ;buff[p+1]=  1 ;buff[p+2]=  151  ;

                p = p+3;}
            else if (tempArray[n] <=19.5&& tempArray[n] > 19.0){
                buff[p]= 182 ;buff[p+1]=  2 ;buff[p+2]=  149  ;

                p = p+3;}
            else if (tempArray[n] <=20.0&& tempArray[n] > 19.5){
                buff[p]= 185 ;buff[p+1]=  4 ;buff[p+2]=  149  ;

                p = p+3;}
            else if (tempArray[n] <=20.5&& tempArray[n] > 20.0){
                buff[p]= 188 ;buff[p+1]=  5 ;buff[p+2]=  147  ;

                p = p+3;}
            else if (tempArray[n] <=21.0&& tempArray[n] > 20.5){
                buff[p]= 191 ;buff[p+1]=  6 ;buff[p+2]=  146  ;

                p = p+3;}
            else if (tempArray[n] <=21.5&& tempArray[n] > 21.0){
                buff[p]= 193 ;buff[p+1]=  8 ;buff[p+2]=  144  ;

                p = p+3;}
            else if (tempArray[n] <=22.0&& tempArray[n] > 21.5){
                buff[p]= 195 ;buff[p+1]=  11 ;buff[p+2]=  142  ;

                p = p+3;}
            else if (tempArray[n] <=22.5&& tempArray[n] > 22.0){
                buff[p]= 198 ;buff[p+1]=  13 ;buff[p+2]=  139  ;

                p = p+3;}
            else if (tempArray[n] <=23.0&& tempArray[n] > 22.5){
                buff[p]= 201 ;buff[p+1]=  17 ;buff[p+2]=  135  ;

                p = p+3;}
            else if (tempArray[n] <=23.5&& tempArray[n] > 23.0){
                buff[p]= 203 ;buff[p+1]=  20 ;buff[p+2]=  132  ;

                p = p+3;}
            else if (tempArray[n] <=24.0&& tempArray[n] > 23.5){
                buff[p]= 206 ;buff[p+1]=  23 ;buff[p+2]=  127  ;

                p = p+3;}
            else if (tempArray[n] <=24.5&& tempArray[n] > 24.0){
                buff[p]= 208 ;buff[p+1]=  26 ;buff[p+2]=  121  ;

                p = p+3;}
            else if (tempArray[n] <=25.0&& tempArray[n] > 24.5){
                buff[p]= 210 ;buff[p+1]=  29 ;buff[p+2]=  116  ;

                p = p+3;}
            else if (tempArray[n] <=25.5&& tempArray[n] > 25.0){
                buff[p]= 212 ;buff[p+1]=  33 ;buff[p+2]=  111  ;

                p = p+3;}
            else if (tempArray[n] <=26.0&& tempArray[n] > 25.5){
                buff[p]= 214 ;buff[p+1]=  37 ;buff[p+2]=  103  ;

                p = p+3;}
            else if (tempArray[n] <=26.5&& tempArray[n] > 26.0){
                buff[p]= 217 ;buff[p+1]=  41 ;buff[p+2]=  97  ;

                p = p+3;}
            else if (tempArray[n] <=27.0&& tempArray[n] > 26.5){
                buff[p]= 219 ;buff[p+1]=  46 ;buff[p+2]=  89  ;

                p = p+3;}
            else if (tempArray[n] <=27.5&& tempArray[n] > 27.0){
                buff[p]= 221 ;buff[p+1]=  49 ;buff[p+2]=  78  ;

                p = p+3;}
            else if (tempArray[n] <=28.0&& tempArray[n] > 27.5){
                buff[p]= 223 ;buff[p+1]=  53 ;buff[p+2]=  66  ;

                p = p+3;}
            else if (tempArray[n] <=28.5&& tempArray[n] > 28.0){
                buff[p]= 224 ;buff[p+1]=  56 ;buff[p+2]=  54  ;

                p = p+3;}
            else if (tempArray[n] <=29.0&& tempArray[n] > 28.5){
                buff[p]= 226 ;buff[p+1]=  60 ;buff[p+2]=  42  ;

                p = p+3;}
            else if (tempArray[n] <=29.5&& tempArray[n] > 29.0){
                buff[p]= 228 ;buff[p+1]=  64 ;buff[p+2]=  30  ;

                p = p+3;}
            else if (tempArray[n] <=30.0&& tempArray[n] > 29.5){
                buff[p]= 229 ;buff[p+1]=  68 ;buff[p+2]=  25  ;

                p = p+3;}
            else if (tempArray[n] <=30.5&& tempArray[n] > 30.0){
                buff[p]= 231 ;buff[p+1]=  72 ;buff[p+2]=  20  ;

                p = p+3;}
            else if (tempArray[n] <=31.0&& tempArray[n] > 30.5){
                buff[p]= 232 ;buff[p+1]=  76 ;buff[p+2]=  16  ;

                p = p+3;}
            else if (tempArray[n] <=31.5&& tempArray[n] > 31.0){
                buff[p]= 234 ;buff[p+1]=  78 ;buff[p+2]=  12  ;

                p = p+3;}
            else if (tempArray[n] <=32.0&& tempArray[n] > 31.5){
                buff[p]= 235 ;buff[p+1]=  82 ;buff[p+2]=  10  ;

                p = p+3;}
            else if (tempArray[n] <=32.5&& tempArray[n] > 32.0){
                buff[p]= 236 ;buff[p+1]=  86 ;buff[p+2]=  8  ;

                p = p+3;}
            else if (tempArray[n] <=33.0&& tempArray[n] > 32.5){
                buff[p]= 237 ;buff[p+1]=  90 ;buff[p+2]=  7  ;

                p = p+3;}
            else if (tempArray[n] <=33.5&& tempArray[n] > 33.0){
                buff[p]= 238 ;buff[p+1]=  93 ;buff[p+2]=  5  ;

                p = p+3;}
            else if (tempArray[n] <=34.0&& tempArray[n] > 33.5){
                buff[p]= 239 ;buff[p+1]=  96 ;buff[p+2]=  4  ;

                p = p+3;}
            else if (tempArray[n] <=34.5&& tempArray[n] > 34.0){
                buff[p]= 240 ;buff[p+1]=  100 ;buff[p+2]=  3  ;

                p = p+3;}
            else if (tempArray[n] <=35.0&& tempArray[n] > 34.5){
                buff[p]= 241 ;buff[p+1]=  103 ;buff[p+2]=  3  ;

                p = p+3;}
            else if (tempArray[n] <=35.5&& tempArray[n] > 35.0){
                buff[p]= 241 ;buff[p+1]=  106 ;buff[p+2]=  2  ;

                p = p+3;}
            else if (tempArray[n] <=36.0&& tempArray[n] > 35.5){
                buff[p]= 242 ;buff[p+1]=  109 ;buff[p+2]=  1  ;

                p = p+3;}
            else if (tempArray[n] <=36.5&& tempArray[n] > 36.0){
                buff[p]= 243 ;buff[p+1]=  113 ;buff[p+2]=  1  ;

                p = p+3;}
            else if (tempArray[n] <=37.0&& tempArray[n] > 36.5){
                buff[p]= 244 ;buff[p+1]=  116 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=37.5&& tempArray[n] > 37.0){
                buff[p]= 244 ;buff[p+1]=  120 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=38.0&& tempArray[n] > 37.5){
                buff[p]= 245 ;buff[p+1]=  125 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=38.5&& tempArray[n] > 38.0){
                buff[p]= 246 ;buff[p+1]=  129 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=39.0&& tempArray[n] > 38.5){
                buff[p]= 247 ;buff[p+1]=  133 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=39.5&& tempArray[n] > 39.0){
                buff[p]= 248 ;buff[p+1]=  136 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=40.0&& tempArray[n] > 39.5){
                buff[p]= 248 ;buff[p+1]=  139 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=40.5&& tempArray[n] > 40.0){
                buff[p]= 249 ;buff[p+1]=  142 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=41.0&& tempArray[n] > 40.5){
                buff[p]= 249 ;buff[p+1]=  145 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=41.5&& tempArray[n] > 41.0){
                buff[p]= 250 ;buff[p+1]=  149 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=42.0&& tempArray[n] > 41.5){
                buff[p]= 251 ;buff[p+1]=  154 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=42.5&& tempArray[n] > 42.0){
                buff[p]= 252 ;buff[p+1]=  159 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=43.0&& tempArray[n] > 42.5){
                buff[p]= 253 ;buff[p+1]=  163 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=43.5&& tempArray[n] > 43.0){
                buff[p]= 253 ;buff[p+1]=  168 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=44.0&& tempArray[n] > 43.5){
                buff[p]= 253 ;buff[p+1]=  172 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=44.5&& tempArray[n] > 44.0){
                buff[p]= 254 ;buff[p+1]=  176 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=45.0&& tempArray[n] > 44.5){
                buff[p]= 254 ;buff[p+1]=  179 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=45.5&& tempArray[n] > 45.0){
                buff[p]= 254 ;buff[p+1]=  184 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=46.0&& tempArray[n] > 45.5){
                buff[p]= 254 ;buff[p+1]=  187 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=46.5&& tempArray[n] > 46.0){
                buff[p]= 254 ;buff[p+1]=  191 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=47.0&& tempArray[n] > 46.5){
                buff[p]= 254 ;buff[p+1]=  195 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=47.5&& tempArray[n] > 47.0){
                buff[p]= 254 ;buff[p+1]=  199 ;buff[p+2]=  0  ;

                p = p+3;}
            else if (tempArray[n] <=48.0&& tempArray[n] > 47.5){
                buff[p]= 254 ;buff[p+1]=  202 ;buff[p+2]=  1  ;

                p = p+3;}
            else if (tempArray[n] <=48.5&& tempArray[n] > 48.0){
                buff[p]= 254 ;buff[p+1]=  205 ;buff[p+2]=  2  ;

                p = p+3;}
            else if (tempArray[n] <=49.0&& tempArray[n] > 48.5){
                buff[p]= 254 ;buff[p+1]=  208 ;buff[p+2]=  5  ;

                p = p+3;}
            else if (tempArray[n] <=49.5&& tempArray[n] > 49.0){
                buff[p]= 254 ;buff[p+1]=  212 ;buff[p+2]=  9  ;

                p = p+3;}
            else if (tempArray[n] <=50.0&& tempArray[n] > 49.5){
                buff[p]= 254 ;buff[p+1]=  216 ;buff[p+2]=  12  ;

                p = p+3;}


        paintMat.put(0,0, buff);
        return paintMat;
    }

    private double[] arregloPruebas()
    {
        double[] testArray = {27.7338, 29.5807, 28.7423, 29.7828, 24.8727, 24.2204, 22.9945, 22.9719, 22.9645, 22.9435, 22.1984, 23.0156, 21.7423, 22.5661, 22.4075, 22.3112, 22.1346, 22.6645, 21.5899, 22.7662, 21.6041, 21.943, 21.8226, 22.731,
                28.6073, 28.2903, 29.3967, 29.0581, 25.974, 24.1191, 23.1006, 22.6296, 23.1425, 22.9814, 22.716, 21.9725, 22.5889, 22.1089, 22.3865, 22.3011, 22.7648, 21.9272, 22.2396, 22.1547, 22.6457, 21.8276, 21.971, 22.1773,
                28.9579, 29.4773, 28.8617, 29.4667, 27.7077, 24.4979, 23.1996, 22.7911, 23.0617, 23.3056, 22.8482, 22.9373, 22.8796, 22.705, 22.9848, 23.0445, 23.1786, 23.3576, 22.7963, 22.6613, 22.2887, 22.7754, 21.8657, 22.0261,
                29.1141, 28.6123, 29.0444, 29.0599, 28.1681, 24.2864, 23.4575, 23.3342, 23.3345, 23.075, 23.3105, 23.1762, 23.0707, 22.7823, 23.4348, 23.1257, 23.3755, 23.0546, 22.4398, 22.4767, 22.3984, 22.2597, 22.5511, 22.5695,
                27.9424, 28.519, 28.2808, 28.5843, 27.7761, 26.0702, 23.5344, 24.2213, 24.2451, 24.3346, 22.8769, 23.4172, 24.1697, 27.3626, 27.7287, 26.6231, 23.2225, 23.4979, 23.0072, 23.2985, 22.0944, 23.0438, 22.2808, 22.287,
                28.6072, 27.9108, 28.1029, 27.7689, 27.7406, 25.0127, 24.4245, 24.6296, 27.5158, 26.4131, 24.6769, 22.9765, 24.5377, 28.1754, 29.4784, 27.5797, 24.5431, 23.2592, 23.3527, 22.4287, 22.7461, 22.1213, 22.9997, 22.1112,
                27.9835, 28.0069, 26.9696, 27.2764, 24.5044, 24.1698, 24.0517, 26.035, 29.9092, 30.3517, 28.6461, 27.1128, 24.2872, 26.3388, 30.3376, 31.1226, 27.8686, 26.1057, 23.6605, 23.576, 22.7991, 23.4462, 22.4522, 23.1227,
                28.068, 27.958, 27.2849, 26.6618, 24.0302, 23.4888, 23.7569, 25.0042, 29.5617, 30.357, 30.8621, 28.7864, 25.2929, 25.657, 29.3534, 30.8068, 30.303, 27.3334, 24.8034, 23.2809, 23.3177, 23.3456, 22.6547, 23.0892,
                27.5862, 27.7301, 26.366, 25.1298, 23.1061, 23.4309, 23.4232, 23.7103, 25.3753, 27.9636, 30.4717, 30.9616, 29.3111, 28.0113, 26.5449, 28.652, 30.3834, 30.9869, 26.7215, 25.9989, 23.9318, 23.8381, 24.5681, 25.3714,
                28.5092, 27.1235, 26.1514, 24.2147, 23.4717, 23.4869, 24.4957, 23.5452, 24.053, 25.8314, 29.8428, 30.7956, 30.9108, 29.2339, 27.1556, 27.9361, 30.8446, 31.0674, 29.1168, 26.3189, 24.6283, 24.1384, 25.6985, 27.1007,
                28.0067, 28.593, 25.6567, 24.1001, 24.2652, 25.895, 28.7001, 29.2652, 26.1367, 24.7335, 25.8034, 28.5331, 30.9558, 31.3617, 30.5706, 29.2112, 29.7401, 31.1345, 31.02, 30.5596, 25.8241, 25.5768, 28.5184, 30.0217,
                26.6188, 27.8889, 25.332, 24.1871, 25.063, 25.9882, 29.9147, 30.4436, 29.469, 26.8749, 24.6151, 27.1179, 30.8406, 31.1677, 32.228, 30.2221, 28.5222, 30.6724, 31.5504, 30.7431, 27.5414, 25.4767, 28.8716, 30.3893,
                23.7389, 24.0776, 23.585, 24.3132, 24.2664, 25.5495, 28.6728, 29.5278, 30.2416, 30.988, 29.534, 27.8029, 27.723, 30.2015, 31.2664, 31.2289, 29.8865, 29.6824, 31.428, 32.0465, 29.7297, 28.3631, 28.5062, 30.6676,
                23.1245, 23.2496, 23.7104, 24.0302, 24.6599, 24.298, 26.8125, 28.589, 30.9108, 31.0103, 31.2179, 30.1919, 28.2774, 28.1093, 31.2324, 31.2367, 31.3975, 30.9651, 31.7667, 31.681, 31.1394, 29.0992, 28.0034, 30.0595,
                22.8432, 23.3131, 23.3266, 24.0472, 24.2358, 23.8831, 22.6031, 22.9639, 26.645, 28.9141, 30.9476, 31.0101, 31.7299, 31.3635, 28.7606, 30.876, 32.2167, 32.2155, 32.3036, 32.0743, 31.8898, 32.0329, 30.4734, 31.3241,
                23.3077, 23.2308, 23.3672, 23.7015, 24.6559, 23.8348, 22.2857, 22.0769, 23.3028, 26.1468, 30.2156, 30.9446, 31.9114, 31.5292, 30.1055, 30.6735, 32.1709, 32.0659, 32.1712, 32.2597, 32.3931, 32.4344, 32.4261, 32.2889,
                23.2201, 23.0214, 22.7208, 23.4618, 23.5794, 24.2234, 22.8272, 23.775, 24.3117, 23.9872, 23.9541, 26.1429, 30.4181, 31.0127, 31.548, 32.4248, 31.5897, 31.7348, 32.1766, 32.4861, 32.3885, 32.9089, 31.6236, 32.0784,
                23.1815, 22.6702, 23.6937, 22.728, 23.3062, 23.3414, 24.1604, 26.4562, 27.8267, 27.1703, 25.9194, 25.1094, 27.9542, 30.2559, 32.2318, 31.7133, 31.8731, 31.7685, 32.297, 32.1332, 32.9023, 31.938, 31.962, 31.886,
                22.8923, 23.7198, 23.5456, 23.5769, 23.2247, 23.7088, 25.261, 27.9115, 29.799, 30.3857, 30.5544, 31.2288, 31.3146, 30.9358, 31.8526, 31.8031, 31.907, 32.4736, 32.7153, 32.9849, 33.2703, 32.8554, 32.7476, 32.0933,
                23.3033, 23.4549, 23.8945, 23.4115, 23.6376, 23.667, 24.9729, 26.2032, 28.5471, 29.5528, 30.5243, 30.8036, 31.5648, 31.572, 32.1728, 32.0724, 31.5888, 31.9899, 32.5933, 33.2226, 33.1832, 32.8604, 32.5373, 32.3808,
                23.4324, 23.7985, 23.2987, 23.7528, 23.1138, 23.9011, 23.9596, 24.3158, 24.5548, 25.2409, 27.4462, 28.3202, 29.3076, 30.5092, 31.2958, 31.5836, 31.1979, 31.8434, 32.3861, 33.0276, 32.6376, 32.5877, 31.9292, 32.3924,
                23.5352, 22.963, 23.1927, 23.2479, 23.8564, 23.3959, 24.1106, 24.0255, 24.6247, 24.5355, 26.3006, 26.5352, 27.5421, 28.0226, 30.6791, 30.4506, 31.4613, 31.6223, 31.6863, 32.2289, 32.7021, 32.7208, 32.4891, 31.9574,
                23.3579, 24.0015, 23.87, 24.1239, 23.7503, 24.0827, 24.5261, 24.8169, 24.7002, 25.2504, 24.5546, 25.0846, 24.8729, 25.3025, 26.3593, 27.4557, 30.078, 30.5805, 31.5838, 31.869, 32.1645, 32.6244, 31.8058, 31.6039,
                23.9234, 23.6859, 24.153, 24.0486, 24.4691, 24.1749, 24.4755, 24.6734, 25.1277, 25.3133, 25.0024, 24.9874, 24.9978, 24.8929, 25.4347, 25.9759, 28.5452, 29.7832, 31.2714, 31.3627, 31.9183, 31.9556, 32.383, 30.5982,
                23.1477, 23.9479, 23.5472, 24.311, 24.131, 24.0091, 24.0021, 24.2148, 24.2414, 24.933, 24.7906, 25.8462, 25.956, 26.3218, 25.7327, 26.2095, 26.2163, 27.2288, 27.9957, 29.2942, 29.829, 30.8129, 29.5519, 29.1059,
                24.1746, 23.631, 24.1131, 23.7707, 24.4972, 23.8174, 24.3168, 24.0112, 24.8344, 24.3131, 25.1632, 25.1574, 26.0544, 25.4358, 26.3461, 25.925, 26.1223, 26.4221, 27.3058, 27.3137, 28.7709, 28.8149, 28.3463, 27.9969,
                23.2855, 23.7559, 23.8797, 24.0193, 24.2204, 24.464, 23.9858, 24.6842, 24.5604, 24.9901, 24.7952, 25.3313, 25.4708, 25.7783, 25.9886, 25.9696, 25.9539, 26.2819, 26.8884, 26.8856, 27.5572, 27.7039, 27.2375, 27.2567,
                24.4752, 23.995, 24.1956, 24.0868, 24.66, 24.6536, 24.6612, 24.3046, 24.7044, 24.3469, 25.5401, 25.4147, 25.543, 25.7482, 26.2056, 25.2693, 25.843, 25.697, 26.2944, 27.0369, 26.9406, 27.2129, 27.1327, 27.1413,
                23.9696, 23.9823, 24.1517, 24.9076, 24.0259, 24.7265, 23.8002, 24.5968, 24.182, 25.0435, 24.6094, 24.9426, 24.955, 25.6571, 24.9155, 26.0131, 24.9882, 25.4719, 25.8474, 25.8148, 25.4893, 26.1956, 26.1838, 25.9879,
                25.0733, 23.5059, 24.5329, 23.5944, 24.8632, 24.0083, 24.7048, 24.4272, 24.6249, 24.8784, 25.545, 25.1497, 25.0581, 24.7804, 25.4124, 24.8788, 25.6109, 25.4566, 25.8515, 25.257, 26.5295, 25.9031, 25.697, 25.4898,
                24.8246, 26.5563, 25.02, 24.9707, 24.7414, 24.9192, 24.5848, 24.7225, 24.9354, 25.506, 25.414, 26.3793, 25.8302, 25.6665, 26.3764, 26.6515, 26.2251, 26.2028, 25.7226, 26.0077, 25.9912, 26.1441, 26.0869, 26.9519,
                27.1802, 26.5706, 25.7697, 24.8803, 25.5232, 24.5276, 24.8723, 24.7011, 25.2309, 25.3623, 25.8385, 25.3815, 26.1351, 25.9693, 26.1252, 26.1105, 26.7165, 26.3875, 26.2347, 25.8826, 26.5567, 25.9715, 27.0518, 28.0354
        };

        return testArray;
    }
}
