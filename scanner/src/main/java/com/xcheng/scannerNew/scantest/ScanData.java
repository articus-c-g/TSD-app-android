package com.xcheng.scannerNew.scantest;

public class ScanData {

    private int id;
    private String symbology;
    private String barcode;

    public ScanData (int id, String symbology, String barcode) {
        super();
        this.id = id;
        this.symbology = symbology;
        this.barcode = barcode;
    }

    public ScanData (String symbology, String barcode) {
        super();
        this.id = -1;
        this.symbology = symbology;
        this.barcode = barcode;
    }

    public int getId() {
        return id;
    }

    public String getSymbology() {
        return symbology;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSymbology(String symbology) {
        this.symbology = symbology;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
