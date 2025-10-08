package com.example.simgizi.models;

import java.io.Serializable;

public class Distribusi implements Serializable {
    private String id_distribusi;
    private String tanggal;
    private String waktu;
    private String jumlah;
    private String status_pengiriman;
    private String nama_petugas;
    private String sekolah_tujuan;
    private String lokasi_gps;
    private String foto;

    // Constructor
    public Distribusi(String id_distribusi, String tanggal, String waktu, String jumlah, String status_pengiriman, String nama_petugas, String sekolah_tujuan, String lokasi_gps, String foto) {
        this.id_distribusi = id_distribusi;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.jumlah = jumlah;
        this.status_pengiriman = status_pengiriman;
        this.nama_petugas = nama_petugas;
        this.sekolah_tujuan = sekolah_tujuan;
        this.lokasi_gps = lokasi_gps;
        this.foto = foto;
    }

    // Getter methods
    public String getLokasi_gps() {
        return lokasi_gps;
    }

    public String getId_distribusi() {
        return id_distribusi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getWaktu() {
        return tanggal;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getStatus_pengiriman() {
        return status_pengiriman;
    }

    public String getNama_petugas() {
        return nama_petugas;
    }

    public String getSekolah_tujuan() {
        return sekolah_tujuan;
    }

    public String getFoto() {
        return foto;
    }
}