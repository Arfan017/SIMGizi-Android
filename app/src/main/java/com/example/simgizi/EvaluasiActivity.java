package com.example.simgizi;

import static com.example.simgizi.api.config.api_get_evaluasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.simgizi.adapters.EvaluasiAdapter;
import com.example.simgizi.models.Evaluasi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EvaluasiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EvaluasiAdapter adapter;
    private List<Evaluasi> evaluasiList;
    private ProgressBar progressBar;
    private TextView textDataKosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluasi);
        setTitle("Data Evaluasi");

        bindViews();
        setupRecyclerView();
        fetchEvaluasiData();
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerViewEvaluasi);
        progressBar = findViewById(R.id.progressBarEvaluasi);
        textDataKosong = findViewById(R.id.textDataEvaluasiKosong);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        evaluasiList = new ArrayList<>();
        adapter = new EvaluasiAdapter(this, evaluasiList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchEvaluasiData() {
        progressBar.setVisibility(View.VISIBLE);
        textDataKosong.setVisibility(View.GONE);
        evaluasiList.clear();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api_get_evaluasi, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            if (dataArray.length() == 0) {
                                textDataKosong.setVisibility(View.VISIBLE);
                            } else {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject evalJson = dataArray.getJSONObject(i);
                                    evaluasiList.add(new Evaluasi(
                                            evalJson.getString("nama_sekolah"),
                                            evalJson.getString("tanggal"),
                                            evalJson.getString("status_distribusi"),
                                            evalJson.getString("catatan"),
                                            evalJson.getString("gambar")
                                    ));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(this, "Gagal memuat data: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            textDataKosong.setText("Gagal memuat data");
                            textDataKosong.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                        textDataKosong.setText("Error parsing data");
                        textDataKosong.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal menghubungi server: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    textDataKosong.setText("Gagal menghubungi server");
                    textDataKosong.setVisibility(View.VISIBLE);
                }
        );
        queue.add(request);
    }
}