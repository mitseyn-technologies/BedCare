package com.tutorial.tutorialopencv;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.os.Handler;

public class BLE_Service extends Service {

    private static final String TAG = BLE_Service.class.getSimpleName();
    private BluetoothManager mylocalBluetoothManager;
    private BluetoothAdapter mylocalBluetoothAdapter;
    private BluetoothGatt myBluetoothGatt;
    private int myLocalRssi = 0;
    private Timer LoopRescan = new Timer();

    private List<BluetoothGattCharacteristic> Characteristic_List_BedCare = new ArrayList<>();

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public static final String ACTION_GATT_CONNECTED = " \"com.example.bluetooth.le.ACTION_GATT_CONNECTED\"";
    public static final String ACTION_GATT_DISCONNECTED = " \"com.example.bluetooth.le.ACTION_GATT_DISCONNECTED\"";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = " \"com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED\"";
    public static final String ACTION_DATA_AVAILABLE = " \"com.example.bluetooth.le.ACTION_GATT_DATA_AVAILABLE\"";
    public static final String EXTRA_DATA = " \"com.example.bluetooth.le.EXTRA_DATA\"";

    private static final UUID UUID_BEDCARE_SERVICE = UUID.fromString("19b10000-e8f2-537e-4f6c-d104768a1214");
    private static final UUID UUID_BEDCARE_CHARACTERISTIC = UUID.fromString("19b10001-e8f2-537e-4f6c-d104768a1214");

    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_ATRIBUTE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_SERVICE_CHANGED = UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb");


    String intentAction;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public int myStatusConnection;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newBleState) {

            switch (newBleState) {
                case BluetoothProfile.STATE_CONNECTED:
                    intentAction = ACTION_GATT_CONNECTED;
                    myStatusConnection = STATE_CONNECTED;
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        LoopRescan = new Timer();
                        TimerTask RescanConnect = new TimerTask() {
                            @Override
                            public void run() {
                                myBluetoothGatt.readRemoteRssi();
                            }
                        };

                        LoopRescan.schedule(RescanConnect, 0, 1000);
                    }



                    broadcastUpdate(intentAction);


                    myBluetoothGatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    intentAction = ACTION_GATT_DISCONNECTED;
                    myStatusConnection = STATE_DISCONNECTED;
                    StopLoop();
                    broadcastUpdate(intentAction);
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.i(TAG, "Servicios decubiertos, received: " + status);
            } else {
                Log.i(TAG, "no se descubrieron servicios, received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            byte[] value=characteristic.getValue();
            String v = new String(value);
            Log.i("onCharacteristicRead", "Value: " + v);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            broadcastUpdate(characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, int status) {
            Log.i(TAG, String.format("BluetoothGatt bef ReadRssi[%d]", rssi));
            myLocalRssi = rssi;
            Log.i(TAG, String.format("BluetoothGatt ReadRssi[%d]", myLocalRssi));

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, int status) {
            Log.i(TAG, "descriptor leido");
           // gatt.setCharacteristicNotification()
          //  setCharacteristicNotification(descriptor, true);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(BLE_Service.ACTION_DATA_AVAILABLE);
        if (UUID_BEDCARE_CHARACTERISTIC.equals(characteristic.getUuid())) {
            Log.i(TAG, "carac uuid");
            final byte[] TravelWord = characteristic.getValue();
            if (TravelWord != null && TravelWord.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(TravelWord.length);

                for (byte byteChar : TravelWord)
                    stringBuilder.append(String.format("%02X ", byteChar));
                Log.i(TAG, "Broadcast: " + new String(TravelWord) + "\n" + stringBuilder.toString());
                intent.putExtra(EXTRA_DATA,  stringBuilder.toString());
            }
            sendBroadcast(intent);
        }
    }

    public class myLocalBinder extends Binder {
        BLE_Service getService() {
            return BLE_Service.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder myBinder = new myLocalBinder();

    public boolean ini_invert() {

        if (mylocalBluetoothManager == null) {
            mylocalBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mylocalBluetoothManager == null) {
                return true;
            }
        }

        mylocalBluetoothAdapter = mylocalBluetoothManager.getAdapter();
        if (mylocalBluetoothAdapter == null) {
            return true;
        }
        return false;
    }

    public String Conectar(String DeviceAddress, String DeviceName) {
        Log.i(TAG, "conectando BT");
        String selectDeviceName;
        if (mylocalBluetoothAdapter == null || DeviceAddress == null) {
            Log.i(TAG, "BT not initialized or unspecified address." + DeviceAddress);
            return null;
        }
        final BluetoothDevice device = mylocalBluetoothAdapter.getRemoteDevice(DeviceAddress);
        if (device == null) {
            Log.i(TAG, "BT no reconocido.");
            return null;
        } else {
            myBluetoothGatt = device.connectGatt(this, false, mGattCallback);
            selectDeviceName = DeviceName;
            Log.i(TAG, "BT conectado");
        }
        return selectDeviceName;
    }

    public void Desconectar() {
        if (mylocalBluetoothAdapter == null || myBluetoothGatt == null) {
            return;
        }
        Log.i(TAG, "BT desconectado");
        myBluetoothGatt.disconnect();
    }

    public void close() {
        if (myBluetoothGatt == null) {
            return;
        }
        myBluetoothGatt.close();
        myBluetoothGatt = null;
    }

    public void StopLoop() {
        LoopRescan.cancel();
        LoopRescan.purge();
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (mylocalBluetoothAdapter == null || myBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        } else {

        }
        Log.i(TAG, "Tesc :" + characteristic);
        myBluetoothGatt.readCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        Log.i(TAG, "carac SETEAR");
        if (mylocalBluetoothAdapter == null || myBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.i (TAG,"Notification set for " + characteristic.getUuid().toString() + " : " + String.valueOf(enabled));

        boolean success = myBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if(success) {
            Log.i(TAG, "Setting proper notification status for characteristic success!");
        }

          if (characteristic.getUuid().equals(UUID_BEDCARE_CHARACTERISTIC)) {
            Log.i(TAG, "carac SETEANDO");
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
            if (descriptor != null) {
                byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                descriptor.setValue(val);
                myBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    public int getLocalRssi() {
        return myLocalRssi;
    }

    private List<BluetoothGattService> getSupportedGattServices() {
        if (myBluetoothGatt == null) {
            return null;
        }
        return myBluetoothGatt.getServices();
    }


    public void getCharacteristicDescriptor(BluetoothGattDescriptor descriptor) {
        if (mylocalBluetoothAdapter == null || myBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        myBluetoothGatt.readDescriptor(descriptor);
    }

    public void BuscarBedCareService() {
        Log.i(TAG, "carac leida");
        for (BluetoothGattService service : myBluetoothGatt.getServices()) {
            Log.d(TAG, "Found Service " + service.getUuid().toString());
            for (BluetoothGattCharacteristic mcharacteristic : service.getCharacteristics()) {
                Log.d(TAG, "Found Characteristic " + mcharacteristic.getUuid().toString());
            }


        }

        BluetoothGattService BedCare_S = myBluetoothGatt.getService(UUID_BEDCARE_SERVICE);
        List<BluetoothGattService> services_BedCareList = getSupportedGattServices();
        boolean Found_Service_BedCare = false;
        UUID current_uuid;
        UUID current_uuid_des;
        String otheruuid;
        List<String> noservices = new ArrayList<>();
        int BuscarBedCareService = Objects.requireNonNull(services_BedCareList).size();

        for (BluetoothGattService CurrentService : services_BedCareList) {
            current_uuid = CurrentService.getUuid();


            if (current_uuid.equals(UUID_BEDCARE_SERVICE)) {
                Found_Service_BedCare = true;
                Log.i(TAG, "carac BedCare");
            } else {
                Found_Service_BedCare = false;
                otheruuid = current_uuid.toString();
                noservices.add(otheruuid);
            }
        }

        if (Found_Service_BedCare) {
            BluetoothGattService GenericInfoService_BedCare = myBluetoothGatt.getService(UUID_ATRIBUTE);
            Characteristic_List_BedCare = GenericInfoService_BedCare.getCharacteristics();
            int searchAsJ43Characteristics = Characteristic_List_BedCare.size();
            Log.i(TAG, "Test" + searchAsJ43Characteristics);
            BluetoothGattCharacteristic Message_GenericInfoService_BedCare = GenericInfoService_BedCare.getCharacteristic(UUID_SERVICE_CHANGED);
            List<BluetoothGattDescriptor> secondMessage_BedCare = Message_GenericInfoService_BedCare.getDescriptors();
            BluetoothGattCharacteristic Message_Variant_BedCare = BedCare_S.getCharacteristic(UUID_BEDCARE_CHARACTERISTIC);
            List<BluetoothGattDescriptor> firstMessage_BedCare = Message_Variant_BedCare.getDescriptors();
            Log.i(TAG, "Test: " + Message_Variant_BedCare.getUuid().toString());

            for (BluetoothGattDescriptor localdescriptor : firstMessage_BedCare) {
                current_uuid_des = localdescriptor.getUuid();
                if (current_uuid_des.equals(CLIENT_CHARACTERISTIC_CONFIG)){
                    Log.i(TAG, "descriptor descubierto");
                  //  myBluetoothGatt.readDescriptor(Message_GenericInfoService_BedCare.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG));
                   // getCharacteristicDescriptor(Message_GenericInfoService_BedCare.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG));
                   setCharacteristicNotification(Message_Variant_BedCare, true);
                   readCharacteristic(Message_Variant_BedCare);
                }

            }
            // Log.i(TAG, "Test: " + firstMessage_BedCare.toString());
            //getCharacteristicDescriptor();

        }
    }

}


