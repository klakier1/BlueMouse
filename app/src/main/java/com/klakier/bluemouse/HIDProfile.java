package com.klakier.bluemouse;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class HIDProfile {
    public static UUID HID_SERVICE = UUID.fromString("00001812-0000-1000-8000-00805f9b34fb");

    public static UUID PROTOCOL_MODE_CHARACTERISTIC = UUID.fromString("00002a4e-0000-1000-8000-00805f9b34fb");
    public static UUID REPORT_CHARACTERISTIC = UUID.fromString("00002a4d-0000-1000-8000-00805f9b34fb");
    public static UUID REPORT_MAP_CHARACTERISTIC = UUID.fromString("00002a4b-0000-1000-8000-00805f9b34fb");
    public static UUID BOOT_INPUT_REPORT_CHARACTERISTIC = UUID.fromString("00002a33-0000-1000-8000-00805f9b34fb");
    public static UUID HID_INFORMATION_CHARACTERISTIC = UUID.fromString("00002a4a-0000-1000-8000-00805f9b34fb");
    public static UUID HID_CONTROL_POINT_CHARACTERISTIC = UUID.fromString("00002a4c-0000-1000-8000-00805f9b34fb");

    public static UUID CLIENT_CHAR_CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static UUID REPORT_REFERENCE_DESCRIPTOR = UUID.fromString("00002908-0000-1000-8000-00805f9b34fb");

    public static byte[] HID_DESCRIPTOR = {
            0x05, 0x01,   /* Usage Page (Generic Desktop)*/
            0x09, 0x02,   /* Usage (Mouse) */
            (byte) 0xA1, 0x01,   /* Collection (Application) */
            0x09, 0x01,   /* Usage (Pointer) */
            (byte) 0xA1, 0x00,   /* Collection (Physical) */
            0x05, 0x09,   /* Usage Page (Buttons) */
            0x19, 0x01,   /* Usage Minimun (01) */
            0x29, 0x03,   /* Usage Maximum (03) */
            0x15, 0x00,   /* logical Minimun (0) */
            0x25, 0x01,   /* logical Maximum (1) */
            (byte) 0x95, 0x03,   /* Report Count (3) */
            0x75, 0x01,   /* Report Size (1) */
            (byte) 0x81, 0x02,   /* Input(Data, Variable, Absolute) 3 button bits */
            (byte) 0x95, 0x01,   /* Report count (1) */
            0x75, 0x05,   /* Report Size (5) */
            (byte) 0x81, 0x01,   /* Input (Constant), 5 bit padding */
            0x05, 0x01,   /* Usage Page (Generic Desktop) */
            0x09, 0x30,   /* Usage (X) */
            0x09, 0x31,   /* Usage (Y) */
            0x09, 0x38,   /* Usage (Z) */
            0x15, (byte) 0x81,   /* Logical Minimum (-127) */
            0x25, 0x7F,   /* Logical Maximum (127) */
            0x75, 0x08,   /* Report Size (8) */
            (byte) 0x95, 0x03,   /* Report Count (2) */
            (byte) 0x81, 0x06,   /* Input(Data, Variable, Relative), 2 position bytes (X & Y)*/
            (byte) 0xC0,         /* end collection */
            (byte) 0xC0          /* end collection */
    };
    public static byte[] HID_INFO = {0x01, 0x01, 0x00, 0x03};


    public static BluetoothGattService createHIDService() {

        BluetoothGattService hidService;

        BluetoothGattCharacteristic characteristicProtocolMode;
        BluetoothGattCharacteristic characteristicReport;
        BluetoothGattDescriptor descriptorReportRef;
        BluetoothGattCharacteristic characteristicReportMap;
        BluetoothGattCharacteristic characteristicBootInputRep;
        BluetoothGattCharacteristic characteristicHidInfo;
        BluetoothGattCharacteristic characteristicHidControl;

        hidService = new BluetoothGattService(HID_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        characteristicProtocolMode = new BluetoothGattCharacteristic(
                PROTOCOL_MODE_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                        | BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
                        | BluetoothGattCharacteristic.PERMISSION_WRITE);
        byte[] protocolMode = {0x01};
        characteristicProtocolMode.setValue(protocolMode);

        characteristicReport = new BluetoothGattCharacteristic(
                REPORT_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_WRITE
                        | BluetoothGattCharacteristic.PROPERTY_NOTIFY
                        | BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
                        | BluetoothGattCharacteristic.PERMISSION_WRITE);
        descriptorReportRef = new BluetoothGattDescriptor(
                REPORT_REFERENCE_DESCRIPTOR,
                0);
        byte[] raportRef = {0x00, 0x01};
        descriptorReportRef.setValue(raportRef);
        characteristicReport.addDescriptor(descriptorReportRef);

        characteristicReportMap = new BluetoothGattCharacteristic(
                REPORT_MAP_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        characteristicReportMap.setValue(HID_DESCRIPTOR);

        characteristicBootInputRep = new BluetoothGattCharacteristic(
                BOOT_INPUT_REPORT_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ
                        | BluetoothGattCharacteristic.PERMISSION_WRITE);

        characteristicHidInfo = new BluetoothGattCharacteristic(
                HID_INFORMATION_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        characteristicHidInfo.setValue(HID_INFO);

        characteristicHidControl = new BluetoothGattCharacteristic(
                HID_CONTROL_POINT_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);

        hidService.addCharacteristic(characteristicProtocolMode);
        hidService.addCharacteristic(characteristicReport);
        hidService.addCharacteristic(characteristicReportMap);
        hidService.addCharacteristic(characteristicBootInputRep);
        hidService.addCharacteristic(characteristicHidInfo);
        hidService.addCharacteristic(characteristicHidControl);

        return hidService;
    }
}
