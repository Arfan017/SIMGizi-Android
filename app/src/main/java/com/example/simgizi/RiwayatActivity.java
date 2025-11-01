package com.example.simgizi;

import static com.example.simgizi.api.config.api_filter;
import static com.example.simgizi.api.config.api_get_riwayat;
import static com.example.simgizi.api.config.api_get_sekolah;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simgizi.adapters.RiwayatAdapter;
import com.example.simgizi.models.Distribusi;
import com.example.simgizi.models.Sekolah;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiwayatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RiwayatAdapter adapter;
    private List<Distribusi> riwayatList;
    private ProgressBar progressBar;
    private TextView textDataKosong;
    private CardView cardFilter;
    private FloatingActionButton fabFilter;

    private Button btnTanggalMulai, btnTanggalAkhir, btnTerapkanFilter, btnClearFilter;
    private Spinner spinnerFilterSekolah;
    private List<Sekolah> dataSekolahList = new ArrayList<>();

    private String filterTanggalMulai = "";
    private String filterTanggalAkhir = "";
    private String filterIdSekolah = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);
        setTitle("Riwayat Distribusi");

        bindViews();
        setupRecyclerView();

        loadSekolahList();
        setupFilterListeners();
        fetchRiwayatData(filterTanggalMulai, filterTanggalAkhir, filterIdSekolah);
        setupFabListener();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        riwayatList = new ArrayList<>();
        adapter = new RiwayatAdapter(this, riwayatList);
        recyclerView.setAdapter(adapter);
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerViewRiwayat);
        progressBar = findViewById(R.id.progressBarRiwayat);
        textDataKosong = findViewById(R.id.textDataKosong);

        btnTanggalMulai = findViewById(R.id.btnTanggalMulai);
        btnTanggalAkhir = findViewById(R.id.btnTanggalAkhir);
        btnTerapkanFilter = findViewById(R.id.btnTerapkanFilter);
        btnClearFilter = findViewById(R.id.btnClearFilter);
        spinnerFilterSekolah = findViewById(R.id.spinnerFilterSekolah);

        cardFilter = findViewById(R.id.cardFilter);
        fabFilter = findViewById(R.id.fabFilter);
    }

    private void loadSekolahList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, api_get_sekolah, null,
                response -> {
                    try {
                        dataSekolahList.clear();
                        dataSekolahList.add(new Sekolah("", "Semua Sekolah", ""));

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject sekolahJson = response.getJSONObject(i);
                            dataSekolahList.add(new Sekolah(
                                    sekolahJson.getString("id_sekolah"),
                                    sekolahJson.getString("nama_sekolah"),
                                    sekolahJson.getString("lokasi_gps")
                            ));
                        }
                        ArrayAdapter<Sekolah> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataSekolahList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerFilterSekolah.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(this, "Gagal memuat daftar sekolah", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void setupFabListener() {
        fabFilter.setOnClickListener(v -> {
            if (cardFilter.getVisibility() == View.GONE) {
                Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
                cardFilter.setVisibility(View.VISIBLE);
                cardFilter.startAnimation(slideDown);
            } else {
                Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                cardFilter.startAnimation(slideUp);

                slideUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        cardFilter.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });
    }

    private void setupFilterListeners() {
        btnTanggalMulai.setOnClickListener(v -> showDatePicker(true));

        btnTanggalAkhir.setOnClickListener(v -> showDatePicker(false));

        btnTerapkanFilter.setOnClickListener(v -> {
            Sekolah sekolahTerpilih = (Sekolah) spinnerFilterSekolah.getSelectedItem();
            if (sekolahTerpilih != null) {
                filterIdSekolah = sekolahTerpilih.getId_sekolah();
            }
            fetchRiwayatData(filterTanggalMulai, filterTanggalAkhir, filterIdSekolah);
        });

        btnClearFilter.setOnClickListener(v -> {
            filterTanggalMulai = "";
            filterTanggalAkhir = "";
            filterIdSekolah = "";
            btnTanggalMulai.setText("Dari Tanggal");
            btnTanggalAkhir.setText("Sampai Tanggal");
            spinnerFilterSekolah.setSelection(0);
            fetchRiwayatData(filterTanggalMulai, filterTanggalAkhir, filterIdSekolah);
        });
    }

    private void showDatePicker(boolean isTanggalMulai) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    if (isTanggalMulai) {
                        filterTanggalMulai = selectedDate;
                        btnTanggalMulai.setText(selectedDate);
                    } else {
                        filterTanggalAkhir = selectedDate;
                        btnTanggalAkhir.setText(selectedDate);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void fetchRiwayatData(String tglMulai, String tglAkhir, String idSekolah) {
        progressBar.setVisibility(View.VISIBLE);
        textDataKosong.setVisibility(View.GONE);
        riwayatList.clear();
        adapter.notifyDataSetChanged();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api_filter,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() == 0) {
                            textDataKosong.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            riwayatList.add(new Distribusi(
                                    jsonObject.getString("id_distribusi"),
                                    jsonObject.getString("tanggal"),
                                    jsonObject.getString("jam"),
                                    jsonObject.getString("jumlah"),
                                    jsonObject.getString("status_pengiriman"),
                                    "",
                                    jsonObject.getString("sekolah_tujuan"),
                                    jsonObject.getString("lokasi_gps"),
                                    jsonObject.getString("foto")
                            ));
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RiwayatActivity.this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RiwayatActivity.this, "Gagal mengambil data riwayat: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tanggal_mulai", tglMulai);
                params.put("tanggal_akhir", tglAkhir);
                params.put("sekolah", idSekolah);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}