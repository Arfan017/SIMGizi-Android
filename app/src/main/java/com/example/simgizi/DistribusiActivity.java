package com.example.simgizi;

import static com.example.simgizi.api.config.api_get_distribusi;
import static com.example.simgizi.api.config.api_update_status_pengiriman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simgizi.adapters.DistribusiAdapter;
import com.example.simgizi.models.Distribusi;
import com.example.simgizi.services.TrackingService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistribusiActivity extends AppCompatActivity implements DistribusiAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private DistribusiAdapter adapter;
    private List<Distribusi> distribusiList;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String tempIdDistribusi;
    private String tempNewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribusi);
        setTitle("Data Distribusi");

        recyclerView = findViewById(R.id.recyclerViewDistribusi);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        distribusiList = new ArrayList<>();
        adapter = new DistribusiAdapter(this, distribusiList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchData();
    }

    @Override
    public void onStatusButtonClick(Distribusi item, String newStatus) {
        tempIdDistribusi = item.getId_distribusi();
        tempNewStatus = newStatus;

        checkLocationPermission();
    }

    @Override
    public void onItemViewClick(Distribusi item) {
        Intent intent = new Intent(DistribusiActivity.this, DetailActivity.class);

        intent.putExtra("EXTRA_DISTRIBUSI", item);

        startActivity(intent);
    }

    private void updateStatusDiServer(final String idDistribusi, final String newStatus, final String gps) {
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, api_update_status_pengiriman,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        Toast.makeText(DistribusiActivity.this, message, Toast.LENGTH_SHORT).show();

                        if (status.equals("success")) {
                            fetchData();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DistribusiActivity.this, "Error parsing response!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DistribusiActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_distribusi", idDistribusi);
                params.put("status", newStatus);
                params.put("gps", gps);
                return params;
            }
        };
        requestQueue.add(stringRequest);

        // --- TAMBAHKAN LOGIKA INI ---
        Intent serviceIntent = new Intent(this, TrackingService.class);
        if (newStatus.equals("1")) { // Jika statusnya menjadi "Dikirim"
            // Mulai service dan kirim ID distribusinya
            serviceIntent.putExtra("id_distribusi", idDistribusi);
            startService(serviceIntent);
            Toast.makeText(this, "Pelacakan dimulai...", Toast.LENGTH_SHORT).show();
        } else { // Jika statusnya menjadi "Selesai" (2) atau "Batal" (0)
            // Hentikan service
            stopService(serviceIntent);
            Toast.makeText(this, "Pelacakan dihentikan.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                api_get_distribusi,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        distribusiList.clear();
                        try {
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
                                distribusiList.add(distribusi);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DistribusiActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(DistribusiActivity.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    // 2. Method untuk memeriksa izin lokasi
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {

            getCurrentLocationAndSendUpdate();
        }
    }


    private void getCurrentLocationAndSendUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String gpsCoordinates = location.getLatitude() + "," + location.getLongitude();
                        Toast.makeText(this, "Lokasi didapatkan: " + gpsCoordinates, Toast.LENGTH_SHORT).show();

                        updateStatusDiServer(tempIdDistribusi, tempNewStatus, gpsCoordinates);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Gagal mendapatkan lokasi. Pastikan GPS Anda aktif.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error mendapatkan lokasi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}