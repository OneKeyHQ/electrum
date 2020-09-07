package org.haobtc.keymanager.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.MyApplication;
import org.haobtc.keymanager.event.HandlerEvent;

import java.util.Map;
import java.util.Optional;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.bleTransport;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.nfcTransport;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.usb;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.usbTransport;

public class CustomerUsbManager {

    private UsbManager usbManager;
    private static CustomerUsbManager customerUsbManager;
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private IntentFilter permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
    private IntentFilter observerFilter;
    private PendingIntent permissionIntent;
    public static final String TAG = CustomerUsbManager.class.getSimpleName();

    public static CustomerUsbManager getInstance(Context context) {
        if (customerUsbManager == null) {
            synchronized (CustomerUsbManager.class) {
                if (customerUsbManager == null) {
                   customerUsbManager = new CustomerUsbManager(context);
                }
            }
        }
        return customerUsbManager;
    }

    private CustomerUsbManager(Context context) {
        this.usbManager = (UsbManager) context.getApplicationContext().getSystemService(Context.USB_SERVICE);
        observerFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        observerFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
    }

    private final BroadcastReceiver connectionStateChangeReceiver =  new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
           if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
               UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
               if (device != null) {
                   // call your method that cleans up and closes communication with the device
//                   UsbInterface intf = device.getInterface(0);
//                   UsbDeviceConnection connection = usbManager.openDevice(device);
//                   connection.releaseInterface(intf);
//                   connection.close();
                   Toast.makeText(MyApplication.getInstance(), context.getString(R.string.usb_break_link), Toast.LENGTH_SHORT).show();
               }
           } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
               assert usbManager != null;
               UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
               Log.d(TAG, "find usb device ===" +  device);
               UsbDevice bixinKEY = findBixinKEYDevice();
               if (bixinKEY != null) {
                  doBusiness(device);
               }
           }

       }
   };

    public UsbDevice findBixinKEYDevice() {
        Optional<Map.Entry<String, UsbDevice>> first = usbManager.getDeviceList().entrySet().stream().filter(stringUsbDeviceEntry -> {
            UsbDevice usbDevice = stringUsbDeviceEntry.getValue();
            return usbDevice.getVendorId() == 4617 && (usbDevice.getProductId() == 21441 || usbDevice.getProductId() == 21440);
        }).findFirst();

        return first.map(Map.Entry::getValue).orElse(null);
    }

    public void doBusiness(UsbDevice device) {
        synchronized (CustomerUsbManager.class) {
            if (!usbManager.hasPermission(device)) {
                usbManager.requestPermission(device, permissionIntent);
            } else {
                usb.put("USB_Manager", usbManager);
                usb.put("USB_DEVICE", device);
                usbTransport.put("ENABLED", true);
                bleTransport.put("ENABLED", false);
                nfcTransport.put("ENABLED", false);
                EventBus.getDefault().postSticky(new HandlerEvent());
            }
        }
    }

    private final BroadcastReceiver permissionGrantedStateChangeReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
               String action = intent.getAction();
               if (ACTION_USB_PERMISSION.equals(action)) {
                   synchronized (this) {
                       UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                       if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                           if(device != null) {
                               doBusiness(device);
                           }
                       }
                       else {
                           Log.d(TAG, "permission denied for device " + device);
                       }
                   }
               }
           }
   };

    public void register(Context context) {
       context.registerReceiver(connectionStateChangeReceiver, observerFilter);
       context.registerReceiver(permissionGrantedStateChangeReceiver, permissionFilter);
   }

   public void unRegister(Context context) {
        context.unregisterReceiver(connectionStateChangeReceiver);
        context.unregisterReceiver(permissionGrantedStateChangeReceiver);
   }

}
