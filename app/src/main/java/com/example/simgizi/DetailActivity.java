package com.example.simgizi;

import static com.example.simgizi.api.config.base_url_image;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simgizi.models.Distribusi;

public class DetailActivity extends AppCompatActivity {

    private TextView textDetailSekolah, textDetailPetugas, textDetailWaktu, textDetailJumlah, textDetailStatus;
    private Button btnLihatLokasi;
    private ImageView imageDetailFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        bindViews();

        Intent intent = getIntent();
        Distribusi distribusi = (Distribusi) intent.getSerializableExtra("EXTRA_DISTRIBUSI");

        if (distribusi != null) {
            populateData(distribusi);
        } else {
            Toast.makeText(this, "Gagal memuat data detail.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void bindViews() {
        textDetailSekolah = findViewById(R.id.textDetailSekolah);
        textDetailPetugas = findViewById(R.id.textDetailPetugas);
        textDetailWaktu = findViewById(R.id.textDetailWaktu);
        textDetailJumlah = findViewById(R.id.textDetailJumlah);
        textDetailStatus = findViewById(R.id.textDetailStatus);
        btnLihatLokasi = findViewById(R.id.btnLihatLokasi);
        imageDetailFoto = findViewById(R.id.imageDetailFoto);
    }

    private void populateData(Distribusi distribusi) {
        // Set judul halaman
        setTitle("Detail Distribusi");

        // --- Memuat gambar menggunakan Glide ---
        if (distribusi.getFoto() != null && !distribusi.getFoto().isEmpty() && !distribusi.getFoto().equalsIgnoreCase("null")) {
            String imageUrl = base_url_image + distribusi.getFoto();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_input_distribusi) // Gambar default saat loading/jika foto tidak ada
                    .error(R.drawable.ic_input_distribusi) // Gambar default jika terjadi error
                    .into(imageDetailFoto);
        } else {
            // Jika tidak ada foto, tampilkan placeholder default
            imageDetailFoto.setImageResource(R.drawable.ic_input_distribusi);
        }

        // Isi semua TextView dengan data dari objek
        textDetailSekolah.setText(distribusi.getSekolah_tujuan());
        textDetailPetugas.setText(distribusi.getNama_petugas());
        textDetailWaktu.setText(distribusi.getTanggal() + " " + distribusi.getWaktu()); // Gabungkan tanggal dan jam
        textDetailJumlah.setText(distribusi.getJumlah() + " Porsi");

        // Logika untuk menampilkan status dan warna
        String statusText;
        int statusColor;
        switch (distribusi.getStatus_pengiriman()) {
            case "0":
                statusText = "Belum Dikirim";
                statusColor = getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "1":
                statusText = "Dalam Perjalanan";
                statusColor = getResources().getColor(android.R.color.holo_blue_dark);
                break;
            case "2":
                statusText = "Diterima";
                statusColor = getResources().getColor(android.R.color.holo_green_dark);
                break;
            default:
                statusText = "Status Tidak Diketahui";
                statusColor = getResources().getColor(android.R.color.darker_gray);
                break;
        }
        textDetailStatus.setText(statusText);
        textDetailStatus.setBackgroundColor(statusColor);

        // Listener untuk tombol lihat lokasi
        btnLihatLokasi.setOnClickListener(v -> {
            String gps = distribusi.getLokasi_gps();
            if (gps != null && !gps.isEmpty() && !gps.equals("0,0")) {
                // Buat Intent untuk membuka Google Maps
                Uri gmmIntentUri = Uri.parse("geo:" + gps + "?q=" + gps + "(" + Uri.encode(distribusi.getSekolah_tujuan()) + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // Cek apakah Google Maps terinstall
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(this, "Google Maps tidak terinstall.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Data lokasi tidak tersedia.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
