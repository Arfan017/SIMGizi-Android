package com.example.simgizi;

import static com.example.simgizi.api.config.api_crud_stok;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InputPorsiActivity extends AppCompatActivity {

    private TextView textTanggalPorsi;
    private EditText editTextJumlahPorsi;
    private Button btnPilihTanggalPorsi, btnSimpanPorsi;

    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_porsi);

        setTitle("Input Porsi Harian");

        bindViews();
        setupListeners();
    }

    private void bindViews() {
        textTanggalPorsi = findViewById(R.id.textTanggalPorsi);
        editTextJumlahPorsi = findViewById(R.id.editTextJumlahPorsi);
        btnPilihTanggalPorsi = findViewById(R.id.btnPilihTanggalPorsi);
        btnSimpanPorsi = findViewById(R.id.btnSimpanPorsi);
    }

    private void setupListeners() {
        btnPilihTanggalPorsi.setOnClickListener(v -> showDatePicker());
        btnSimpanPorsi.setOnClickListener(v -> simpanDataPorsi());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Format tanggal agar sesuai dengan YYYY-MM-DD
                    selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    textTanggalPorsi.setText(selectedDate);
                    textTanggalPorsi.setTextColor(getResources().getColor(android.R.color.black));
                }, year, month, day);
        datePickerDialog.show();
    }

    private void simpanDataPorsi() {
        String jumlah = editTextJumlahPorsi.getText().toString().trim();

        // Validasi sederhana
        if (selectedDate.isEmpty() || jumlah.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Menyimpan data...", Toast.LENGTH_SHORT).show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api_crud_stok,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        Toast.makeText(InputPorsiActivity.this, message, Toast.LENGTH_LONG).show();

                        if (status.equals("success")) {
                        }
                    } catch (JSONException e) {
                        Toast.makeText(InputPorsiActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(InputPorsiActivity.this, "Gagal menghubungi server: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Nama key harus sama dengan nama 'name' di form HTML atau yang dibaca oleh $_POST di PHP
                params.put("tanggal_stok", selectedDate);
                params.put("jumlah_total", jumlah);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}