package com.example.simgizi.models;


public class BahanMakanan {
    private String id;
    private String nama;

    public BahanMakanan(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    // PENTING: Agar ArrayAdapter menampilkan nama di Spinner
    @Override
    public String toString() {
        return nama;
    }
}
