package com.example.simgizi;

import static com.example.simgizi.api.config.api_crud_distribusi;
import static com.example.simgizi.api.config.api_get_sekolah;
import static com.example.simgizi.api.config.api_get_stok_harian;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.simgizi.models.Sekolah;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InputActivity extends AppCompatActivity {

    // UI Components
    private Spinner spinnerSekolah;
    private EditText editTextJumlah, editTextLokasi;
    private Button btnPilihTanggal, btnPilihJam, btnPilihFoto, btnAmbilLokasi, btnSimpan;
    private TextView textTanggal, textJam, textAlamatFoto, textInfoStok;
    ;
    private ImageView imagePreview;

    private List<Sekolah> dataSekolahList = new ArrayList<>();
    private String selectedDate = "", selectedTime = "", imagePath = "";

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        setTitle("Input Distribusi");

        bindViews();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        loadSekolahList();
        setupImagePicker();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSisaStok();
    }

    private void bindViews() {
        spinnerSekolah = findViewById(R.id.spinnerSekolah);
        editTextJumlah = findViewById(R.id.editTextJumlah);
        editTextLokasi = findViewById(R.id.editTextLokasi);
        btnPilihTanggal = findViewById(R.id.btnPilihTanggal);
        btnPilihJam = findViewById(R.id.btnPilihJam);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnAmbilLokasi = findViewById(R.id.btnAmbilLokasi);
        btnSimpan = findViewById(R.id.btnSimpan);
        textTanggal = findViewById(R.id.textTanggal);
        textJam = findViewById(R.id.textJam);
        textAlamatFoto = findViewById(R.id.textAlamatFoto);
        imagePreview = findViewById(R.id.imagePreview);
        textInfoStok = findViewById(R.id.textInfoStok);
    }

    private void fetchSisaStok() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api_get_stok_harian, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            String sisaStok = String.valueOf(response.getInt("sisa_stok"));
                            textInfoStok.setText(sisaStok);
                            // Set batas maksimal untuk EditText jumlah
                            editTextJumlah.setHint("Maks: " + sisaStok);
                        } else {
                            textInfoStok.setText("0 (Belum diinput)");
                        }
                    } catch (JSONException e) {
                        textInfoStok.setText("Error");
                    }
                },
                error -> textInfoStok.setText("Error")
        );
        queue.add(request);
    }

    private void loadSekolahList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, api_get_sekolah, null,
                response -> {
                    try {
                        // Kosongkan list sebelum diisi data baru
                        dataSekolahList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject sekolahJson = response.getJSONObject(i);

                            // Buat objek Sekolah dari JSON
                            Sekolah sekolah = new Sekolah(
                                    sekolahJson.getString("id_sekolah"),
                                    sekolahJson.getString("nama_sekolah"),
                                    sekolahJson.getString("lokasi_gps")
                            );
                            dataSekolahList.add(sekolah); // Tambahkan objek ke list
                        }

                        // Gunakan List<Sekolah> langsung ke Adapter
                        ArrayAdapter<Sekolah> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataSekolahList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSekolah.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(this, "Gagal memuat daftar sekolah", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        imagePreview.setImageURI(selectedImageUri);
                        imagePath = getPathFromUri(selectedImageUri);
                        textAlamatFoto.setText(new File(imagePath).getName());
                    }
                }
        );
    }

    private void setupClickListeners() {
        btnPilihTanggal.setOnClickListener(v -> showDatePicker());
        btnPilihJam.setOnClickListener(v -> showTimePicker());
        btnPilihFoto.setOnClickListener(v -> checkStoragePermissionAndPickImage());
        btnAmbilLokasi.setOnClickListener(v -> checkLocationPermissionAndGetLocation());
        btnSimpan.setOnClickListener(v -> simpanData());

        // --- TAMBAHKAN LISTENER UNTUK SPINNER DI SINI ---
        spinnerSekolah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Ambil objek Sekolah yang dipilih berdasarkan posisinya
                Sekolah sekolahTerpilih = dataSekolahList.get(position);

                // Ambil data GPS dari objek tersebut
                String lokasiGps = sekolahTerpilih.getLokasi_gps();

                // Set teks di EditText lokasi
                if (lokasiGps != null && !lokasiGps.isEmpty() && !lokasiGps.equalsIgnoreCase("null")) {
                    editTextLokasi.setText(lokasiGps);
                } else {
                    // Kosongkan jika tidak ada data GPS
                    editTextLokasi.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Biarkan kosong
            }
        });

    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            textTanggal.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = hourOfDay + ":" + minute;
            textJam.setText(selectedTime);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void checkStoragePermissionAndPickImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void checkLocationPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                String gps = location.getLatitude() + "," + location.getLongitude();
                editTextLokasi.setText(gps);
            } else {
                Toast.makeText(this, "Gagal mendapat lokasi, pastikan GPS aktif", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            Toast.makeText(this, "Izin ditolak!", Toast.LENGTH_SHORT).show();
        }
    }

    private void simpanData() {
        if (spinnerSekolah.getSelectedItemPosition() < 0 || editTextJumlah.getText().toString().isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty() || imagePath.isEmpty() || editTextLokasi.getText().toString().isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Mengupload data...", Toast.LENGTH_SHORT).show();

        Sekolah sekolahTerpilih = (Sekolah) spinnerSekolah.getSelectedItem();
        String idSekolah = sekolahTerpilih.getId_sekolah();
        File fotoFile = new File(imagePath);
        String idPetugas = "1";

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("foto", fotoFile.getName(),
                        RequestBody.create(fotoFile, MediaType.parse("image/jpeg")))
                .addFormDataPart("id_petugas_distribusi", idPetugas)
                .addFormDataPart("nama_barang", "Makanan")
                .addFormDataPart("sekolah", idSekolah)
                .addFormDataPart("jumlah", editTextJumlah.getText().toString())
                .addFormDataPart("tanggal", selectedDate)
                .addFormDataPart("jam", selectedTime)
                .addFormDataPart("lokasi", editTextLokasi.getText().toString())
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(api_crud_distribusi)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Jika Gagal (masalah jaringan, dll)
                runOnUiThread(() -> {
                    Toast.makeText(InputActivity.this, "Upload Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("UploadError", "onFailure: ", e);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Jika Berhasil mendapat respons dari server
                final String responseBody = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(InputActivity.this, "Data berhasil disimpan!", Toast.LENGTH_LONG).show();
                            finish(); // Kembali ke halaman utama
                        } else {
                            Toast.makeText(InputActivity.this, "Gagal: " + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(InputActivity.this, "Error parsing response!", Toast.LENGTH_SHORT).show();
                        Log.e("UploadError", "onResponse JSONException: " + responseBody);
                    }
                });
            }
        });
        // --- AKHIR LOGIKA UPLOAD DENGAN OKHTTP ---
    }

    private String getPathFromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }
}