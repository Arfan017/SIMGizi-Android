package com.example.simgizi;

import static com.example.simgizi.api.config.api_get_bahan_makanan;
import static com.example.simgizi.api.config.api_save_menu_stok;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simgizi.models.BahanMakanan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputPorsiActivity extends AppCompatActivity {

    // UI Elements
    private TextView textTanggalPorsi;
    private Button btnPilihTanggalPorsi, btnSimpanPorsi;
    private Spinner spinnerKh, spinnerProtein1, spinnerProtein2, spinnerSayur, spinnerBuah;
    private EditText editTextTambahan, editTextJumlahPorsi;

    // Data Holders for Spinners
    private List<BahanMakanan> listKh = new ArrayList<>();
    private List<BahanMakanan> listProtein = new ArrayList<>();
    private List<BahanMakanan> listSayur = new ArrayList<>();
    private List<BahanMakanan> listBuah = new ArrayList<>();

    // Adapters for Spinners
    private ArrayAdapter<BahanMakanan> adapterKh, adapterProtein1, adapterProtein2, adapterSayur, adapterBuah;

    private String selectedDate = ""; // Menyimpan tanggal terpilih
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_porsi);
        setTitle("Input Menu & Porsi Harian");

        requestQueue = Volley.newRequestQueue(this);

        bindViews();
        setupAdapters(); // Siapkan adapter sebelum fetch data
        setupListeners();
        fetchBahanMakananOptions(); // Ambil data untuk dropdown
    }

    private void bindViews() {
        textTanggalPorsi = findViewById(R.id.textTanggalPorsi);
        btnPilihTanggalPorsi = findViewById(R.id.btnPilihTanggalPorsi);
        spinnerKh = findViewById(R.id.spinnerKh);
        spinnerProtein1 = findViewById(R.id.spinnerProtein1);
        spinnerProtein2 = findViewById(R.id.spinnerProtein2);
        spinnerSayur = findViewById(R.id.spinnerSayur);
        spinnerBuah = findViewById(R.id.spinnerBuah);
        editTextTambahan = findViewById(R.id.editTextTambahan);
        editTextJumlahPorsi = findViewById(R.id.editTextJumlahPorsi);
        btnSimpanPorsi = findViewById(R.id.btnSimpanPorsi);
    }

    private void setupAdapters() {
        // Buat adapter dengan layout dropdown standar
        adapterKh = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listKh);
        adapterKh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKh.setAdapter(adapterKh);

        adapterProtein1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listProtein);
        adapterProtein1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProtein1.setAdapter(adapterProtein1);

        adapterProtein2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listProtein); // Protein 2 pakai list yg sama
        adapterProtein2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProtein2.setAdapter(adapterProtein2);

        adapterSayur = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listSayur);
        adapterSayur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSayur.setAdapter(adapterSayur);

        adapterBuah = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listBuah);
        adapterBuah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBuah.setAdapter(adapterBuah);
    }


    private void setupListeners() {
        btnPilihTanggalPorsi.setOnClickListener(v -> showDatePicker());
        btnSimpanPorsi.setOnClickListener(v -> simpanMenuDanStok());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    textTanggalPorsi.setText(selectedDate);
                    textTanggalPorsi.setTextColor(getResources().getColor(android.R.color.black));
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void fetchBahanMakananOptions() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api_get_bahan_makanan, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONObject options = response.getJSONObject("options");
                            // Panggil fungsi populate untuk setiap kategori
                            populateSpinnerList(options.getJSONArray("KH"), listKh, adapterKh, "- Pilih KH -");
                            populateSpinnerList(options.getJSONArray("Protein"), listProtein, adapterProtein1, "- Pilih Protein 1 -");
                            populateSpinnerList(options.getJSONArray("Protein"), listProtein, adapterProtein2, "- Pilih Protein 2 (Opsional) -"); // Panggil lagi untuk Protein 2
                            populateSpinnerList(options.getJSONArray("Sayur"), listSayur, adapterSayur, "- Pilih Sayur -");
                            populateSpinnerList(options.getJSONArray("Buah"), listBuah, adapterBuah, "- Pilih Buah (Opsional) -");
                        } else {
                            Toast.makeText(this, "Gagal memuat opsi: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing data opsi!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Gagal menghubungi server opsi!", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    // Helper untuk mengisi list dan update adapter spinner
    private void populateSpinnerList(JSONArray itemsJson, List<BahanMakanan> itemList, ArrayAdapter<BahanMakanan> adapter, String placeholder) {
        itemList.clear();
        itemList.add(new BahanMakanan("", placeholder)); // Tambahkan placeholder di awal
        try {
            for (int i = 0; i < itemsJson.length(); i++) {
                JSONObject item = itemsJson.getJSONObject(i);
                itemList.add(new BahanMakanan(item.getString("id"), item.getString("nama")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged(); // Beritahu adapter bahwa data berubah
    }


    private void simpanMenuDanStok() {
        String jumlah = editTextJumlahPorsi.getText().toString().trim();
        String tambahan = editTextTambahan.getText().toString().trim();

        // Ambil item terpilih dari spinner
        BahanMakanan selectedKh = (BahanMakanan) spinnerKh.getSelectedItem();
        BahanMakanan selectedP1 = (BahanMakanan) spinnerProtein1.getSelectedItem();
        BahanMakanan selectedP2 = (BahanMakanan) spinnerProtein2.getSelectedItem();
        BahanMakanan selectedSayur = (BahanMakanan) spinnerSayur.getSelectedItem();
        BahanMakanan selectedBuah = (BahanMakanan) spinnerBuah.getSelectedItem();

        // Validasi Wajib
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Tanggal wajib dipilih!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedKh == null || selectedKh.getId().isEmpty()) {
            Toast.makeText(this, "Karbohidrat wajib dipilih!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedP1 == null || selectedP1.getId().isEmpty()) {
            Toast.makeText(this, "Protein 1 wajib dipilih!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedSayur == null || selectedSayur.getId().isEmpty()) {
            Toast.makeText(this, "Sayur wajib dipilih!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (jumlah.isEmpty()) {
            Toast.makeText(this, "Jumlah Porsi wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }
        try { // Cek apakah jumlah valid
            if (Integer.parseInt(jumlah) <= 0) {
                Toast.makeText(this, "Jumlah Porsi harus lebih dari 0!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Jumlah Porsi tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil ID (bisa kosong jika placeholder dipilih untuk opsional)
        String idKh = selectedKh.getId();
        String idP1 = selectedP1.getId();
        String idP2 = (selectedP2 != null && !selectedP2.getId().isEmpty()) ? selectedP2.getId() : ""; // Kirim string kosong jika opsional tidak dipilih
        String idSayur = selectedSayur.getId();
        String idBuah = (selectedBuah != null && !selectedBuah.getId().isEmpty()) ? selectedBuah.getId() : ""; // Kirim string kosong jika opsional tidak dipilih

        Toast.makeText(this, "Menyimpan data...", Toast.LENGTH_SHORT).show();
        btnSimpanPorsi.setEnabled(false); // Nonaktifkan tombol saat proses

        StringRequest stringRequest = new StringRequest(Request.Method.POST, api_save_menu_stok,
                response -> {
                    btnSimpanPorsi.setEnabled(true); // Aktifkan lagi tombol
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        Toast.makeText(InputPorsiActivity.this, message, Toast.LENGTH_LONG).show();

                        if (status.equals("success")) {
                            // Reset form jika berhasil
                            textTanggalPorsi.setText("Pilih tanggal...");
                            textTanggalPorsi.setTextColor(getResources().getColor(android.R.color.darker_gray));
                            selectedDate = "";
                            spinnerKh.setSelection(0);
                            spinnerProtein1.setSelection(0);
                            spinnerProtein2.setSelection(0);
                            spinnerSayur.setSelection(0);
                            spinnerBuah.setSelection(0);
                            editTextTambahan.setText("");
                            editTextJumlahPorsi.setText("");
                        }
                    } catch (JSONException e) {
                        Toast.makeText(InputPorsiActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    btnSimpanPorsi.setEnabled(true); // Aktifkan lagi tombol
                    Toast.makeText(InputPorsiActivity.this, "Gagal menghubungi server: " + error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Nama key harus sama persis dengan yang dibaca $_POST di API PHP
                params.put("tanggal_stok", selectedDate);
                params.put("menu_kh", idKh);
                params.put("menu_protein1", idP1);
                params.put("menu_protein2", idP2);
                params.put("menu_sayur", idSayur);
                params.put("menu_buah", idBuah);
                params.put("menu_tambahan", tambahan);
                params.put("jumlah_total", jumlah);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}