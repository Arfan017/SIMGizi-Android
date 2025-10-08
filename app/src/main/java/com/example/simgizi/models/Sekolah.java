package com.example.simgizi.models;

public class Sekolah {
    private String id_sekolah;
    private String nama_sekolah;
    private String lokasi_gps;

    public Sekolah(String id_sekolah, String nama_sekolah, String lokasi_gps) {
        this.id_sekolah = id_sekolah;
        this.nama_sekolah = nama_sekolah;
        this.lokasi_gps = lokasi_gps;
    }

    // Getter methods
    public String getId_sekolah() {
        return id_sekolah;
    }

    public String getNama_sekolah() {
        return nama_sekolah;
    }

    public String getLokasi_gps() {
        return lokasi_gps;
    }

    // PENTING: Method ini akan dipanggil oleh ArrayAdapter untuk menampilkan nama di Spinner
    @Override
    public String toString() {
        return nama_sekolah;
    }
}