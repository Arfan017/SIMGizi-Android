package com.example.simgizi;

import static androidx.core.content.ContextCompat.startActivity;

import static com.example.simgizi.api.config.api_get_dashboard_stats;
import static com.example.simgizi.api.config.api_get_stok_harian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

public class DashboardActivity extends AppCompatActivity {

    private TextView textTotalDistribusi, textTerkonfirmasi, textBelumTerkonfirmasi;
    private TextView textSisaStok;
    private CardView cardDataDistribusi, cardRiwayatDistribusi, cardInputPorsi, cardInputDistribusi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bindViews();
        setupNavigation();
        fetchDashboardData();
        fetchSisaStok();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDashboardData();
        fetchSisaStok();
    }

    private void bindViews() {
        textTotalDistribusi = findViewById(R.id.textTotalDistribusi);
        textTerkonfirmasi = findViewById(R.id.textTerkonfirmasi);
        textBelumTerkonfirmasi = findViewById(R.id.textBelumTerkonfirmasi);
        textSisaStok = findViewById(R.id.textSisaStok);

        cardDataDistribusi = findViewById(R.id.cardDataDistribusi);
        cardRiwayatDistribusi = findViewById(R.id.cardRiwayatDistribusi);
        cardInputPorsi = findViewById(R.id.cardInputPorsi);
        cardInputDistribusi = findViewById(R.id.cardInputDistribusi);
    }

    private void fetchSisaStok() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api_get_stok_harian, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            textSisaStok.setText(String.valueOf(response.getInt("sisa_stok")));
                        } else {
                            textSisaStok.setText("0");
                        }
                    } catch (JSONException e) {
                        textSisaStok.setText("-");
                    }
                },
                error -> textSisaStok.setText("-")
        );
        queue.add(request);
    }

    private void fetchDashboardData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api_get_dashboard_stats, null, response -> {
            try {
                textTotalDistribusi.setText(String.valueOf(response.getInt("total_distribusi")));
                textTerkonfirmasi.setText(String.valueOf(response.getInt("terkonfirmasi")));
                textBelumTerkonfirmasi.setText(String.valueOf(response.getInt("belum_terkonfirmasi")));
            } catch (JSONException e) {
                Toast.makeText(this, "Gagal parsing data statistik", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Gagal mengambil data statistik", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void setupNavigation() {
        // Logika navigasi tetap sama, hanya variabelnya yang berubah
        cardDataDistribusi.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, DistribusiActivity.class)));

        cardInputDistribusi.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, InputActivity.class)));

        cardRiwayatDistribusi.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, RiwayatActivity.class))
        );

        cardInputPorsi.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, InputPorsiActivity.class)));
    }
}