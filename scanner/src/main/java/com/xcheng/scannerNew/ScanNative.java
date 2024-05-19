package com.xcheng.scannerNew;

/**
 * This class define Scanner native interface, will description Scanner native interface
 */
public class ScanNative {
    static {
        System.loadLibrary("scanjni");
    }

    /**
     * Open Scanner device, call before scanning barcode/QRcode etc.
     *
     * @return (true,success; false, failed)
     */
    public static native boolean openDev();

    /**
     * Close Scanner device, call when scanning finished or User operation
     *
     * @return (true, success; false, failed)
     */
    public static native boolean closeDev();

    /**
     * call after openDev()
     *
     * @return the decode data in string
     */
    public static native String readData();

    /**
     * check the Symbology type enabled status
     *
     * @return true[enabled]/false[disabled]
     */
    public static native boolean checkType(int type);

    /**
     * enable the Symbology type
     *
     * @return true[enable success]/false[enable failed]
     */
    public static native boolean enableType(int type);

    /**
     * disable the Symbology type
     *
     * @return true[disable success]/false[disable failed]
     */
    public static native boolean disableType(int type);

    /**
     * get current scan timeout
     *
     * @return current timeout in string
     */
    public static native String getTimeout();

    /**
     * set scan timeout
     *
     * @return (true, set success; false, set failed)
     */
    public static native boolean setTimeout(int time);

    /**
     * send setting command
     *
     * @return (true, setting success; false, setting failed)
     */
    public static native boolean sendCommand(String command);

    /**
     * query data for All Symbologies setting
     *
     * @return (true, setting success; false, setting failed)
     */
    public static native String queryData(int command);

    public static native boolean profileScanType();
}
