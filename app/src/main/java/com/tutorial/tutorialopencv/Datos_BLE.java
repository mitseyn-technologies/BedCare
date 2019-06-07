package com.tutorial.tutorialopencv;

public class Datos_BLE {
    private String Name_Ble;
    private String Adress_Ble;
    private int Mag_Rssi;

    public Datos_BLE(String Name_Ble, String Adress_Ble){
        this.Name_Ble=Name_Ble;
        this.Adress_Ble=Adress_Ble;
        this.Mag_Rssi=Mag_Rssi;
    }
    public String getName_BLE (){
        return Name_Ble;
    }
    public String getAdress_BLE(){
        return Adress_Ble;
    }

}
