package com.example.simgizi.models;

public class Evaluasi {
    private String nama_sekolah;
    private String tanggal;
    private String status_distribusi; // Ini masih berupa angka (String "1", "2", atau "3")
    private String catatan;
    private String gambar; // Field baru untuk nama file gambar

    // Update Constructor
    public Evaluasi(String nama_sekolah, String tanggal, String status_distribusi, String catatan, String gambar) {
        this.nama_sekolah = nama_sekolah;
        this.tanggal = tanggal;
        this.status_distribusi = status_distribusi;
        this.catatan = catatan;
        this.gambar = gambar;
    }

    // Getter methods
    public String getNama_sekolah() {
        return nama_sekolah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getStatus_distribusi() {
        return status_distribusi;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getGambar() {
        return gambar;
    } // Getter baru
}