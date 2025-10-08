package com.example.simgizi;

import static com.example.simgizi.api.config.api_get_riwayat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.simgizi.adapters.RiwayatAdapter;
import com.example.simgizi.models.Distribusi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RiwayatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RiwayatAdapter adapter;
    private List<Distribusi> riwayatList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);
        setTitle("Riwayat Distribusi");

        recyclerView = findViewById(R.id.recyclerViewRiwayat);
        progressBar = findViewById(R.id.progressBarRiwayat);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        riwayatList = new ArrayList<>();
        adapter = new RiwayatAdapter(this, riwayatList);
        recyclerView.setAdapter(adapter);

        fetchRiwayatData();
    }

    private void fetchRiwayatData() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, api_get_riwayat, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        riwayatList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Distribusi distribusi = new Distribusi(
                                    jsonObject.getString("id_distribusi"),
                                    jsonObject.getString("tanggal"),
                                    jsonObject.getString("jam"),
                                    jsonObject.getString("jumlah"),
                                    jsonObject.getString("status_pengiriman"),
                                    jsonObject.getString("nama_petugas"),
                                    jsonObject.getString("sekolah_tujuan"),
                                    jsonObject.getString("lokasi_gps"),
                                    jsonObject.getString("foto")
                            );
                            riwayatList.add(distribusi);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RiwayatActivity.this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RiwayatActivity.this, "Gagal mengambil data riwayat", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }
}