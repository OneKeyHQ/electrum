package org.haobtc.onekey.passageway;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;

import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.Global;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HandleCommands {

    private static final String TAG = HandleCommands.class.getSimpleName();

    public static PyObject sBle, sCustomerUI, sNfc, sUsb, sBleHandler, sNfcHandler, sBleTransport,
            sNfcTransport, sUsbTransport, sProtocol;
    private static volatile PyObject sCommand;
    private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    private HandleCommands() {
    }

    static {
        sNfc = Global.py.getModule(PyConstant.TREZORLIB_TRANSPORT_NFC);
        sBle = Global.py.getModule(PyConstant.TREZORLIB_TRANSPORT_BLUETOOTH);
        sUsb = Global.py.getModule(PyConstant.TREZORLIB_TRANSPORT_ANDROID_USB);
        sProtocol = Global.py.getModule(PyConstant.TREZORLIB_TRANSPORT_PROTOCOL);
        sBleHandler = sBle.get(PyConstant.BLUETOOTH_HANDLER);
        sNfcHandler = sNfc.get(PyConstant.NFC_HANDLE);
        sUsbTransport = sUsb.get(PyConstant.ANDROID_USB_TRANSPORT);
        sNfcTransport = sNfc.get(PyConstant.NFC_TRANSPORT);
        sBleTransport = sBle.get(PyConstant.BLUETOOTH_TRANSPORT);
        sCustomerUI = Global.py.getModule(PyConstant.TREZORLIB_CUSTOMER_UI).get(PyConstant.CUSTOMER_UI);
        initCommand();
    }

    public interface CallBack<T> {
        void onResult(T result);
    }

    /**
     * init command
     */
    public static void initCommand() {
        if (Daemon.commands != null) {
            sCommand = Daemon.commands;
            return;
        }
        Global.guiConsole = Global.py.getModule(PyConstant.ELECTRUM_GUI_ANDROID_CONSOLE);
        try {
            sCommand = Global.guiConsole.callAttr(PyConstant.ANDROID_COMMANDS, new Kwarg(PyConstant.ANDROID_ID, "112233")
                    , new Kwarg(PyConstant.CALLBACK, Daemon.getInstance()));
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }


    /**
     * device init
     *
     * @param label
     * @param language
     * @param useSe
     * @param callBack
     */
    public static void init(String label, String language, String useSe, CallBack<String> callBack) {
        sExecutorService.execute(() -> {
            synchronized (HandleCommands.class) {
                try {
                    String result = sCommand.callAttr(CommandMethod.INIT,
                            MyApplication.getInstance().getDeviceWay(), label, language, useSe).toString();
                    callBack.onResult(result);
                } catch (Exception e) {
                    showException(e);
                }

            }
        });
    }

    /**
     * wipe device
     *
     * @param callBack
     */
    public static void wipeDevice(CallBack<String> callBack) {
        sExecutorService.execute(() -> {
            synchronized (HandleCommands.class) {
                try {
                    String result = sCommand.callAttr(CommandMethod.WIPE_DEVICE,
                            MyApplication.getInstance().getDeviceWay()).toString();
                    callBack.onResult(result);
                } catch (Exception e) {
                    showException(e);
                }

            }
        });
    }

    /**
     * show err by toast
     *
     * @param e
     */
    private static void showException(Exception e) {
        MyApplication.getInstance().toastErr(e);
    }


    /**
     * get feature
     *
     * @param callBack
     */
    public static void getFeature(CallBack<HardwareFeatures> callBack) {
        sExecutorService.execute(() -> {
            synchronized (HandleCommands.class) {
                try {
                    String result = sCommand.callAttr(CommandMethod.GET_FEATURE,
                            MyApplication.getInstance().getDeviceWay()).toString();
                    callBack.onResult(HardwareFeatures.objectFromData(result));
                } catch (Exception e) {
                    showException(e);
                }
            }
        });
    }


    /**
     * reset pin
     */
    public static void resetPin(CallBack callBack) {
        sExecutorService.execute(() -> {
            synchronized (HandleCommands.class) {
                try {
                    String ret = sCommand.callAttr(CommandMethod.RESET_PIN,
                            MyApplication.getInstance().getDeviceWay()).toString();
                    callBack.onResult(ret);
                } catch (Exception e) {
                    showException(e);
                }
            }
        });
    }


    /**
     * cancel ui by user
     */
    public static void cancelPinUi() {
        sExecutorService.execute(() -> {
            synchronized (HandleCommands.class) {
                try {
                    sCustomerUI.put(PyConstant.USER_CANCEL, 1);
                } catch (Exception e) {
                    showException(e);
                }
            }
        });
    }


    /**
     * set pin
     */
    public static void setPin(String pinCode) {
        sExecutorService.execute(() -> {
            synchronized (HandleCommands.class) {
                try {
                    sCustomerUI.put(PyConstant.PIN, pinCode);
                } catch (Exception e) {
                    showException(e);
                }
            }
        });
    }


    /**
     * import mnemonics
     */
    public static void importMnemonicsToDevice(String mnemonics, CallBack callBack) {
        sExecutorService.execute(() -> {
            synchronized (HandleCommands.class) {
                try {
                    String ret = sCommand.callAttr(CommandMethod.BIXIN_LOAD_DEVICE,
                            MyApplication.getInstance().getDeviceWay(), mnemonics).toString();
                    callBack.onResult(ret);
                } catch (Exception e) {
                    showException(e);
                }
            }
        });
    }

    /**
     * notify
     */
    public static void pyNotify() {
        sExecutorService.execute(() -> {
            synchronized (HandleCommands.class) {
                try {
                    sCommand.callAttr(CommandMethod.NOTIFY);
                } catch (Exception e) {
                    showException(e);
                }
            }
        });
    }


}
