package com.example.simgizi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextView textProfileNama, textProfileRole, textProfileUserId;
    private SessionManager sessionManager;
    private Button btnLogoutProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profil Pengguna");

        // Inisialisasi SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Bind Views
        textProfileNama = findViewById(R.id.textProfileNama);
        textProfileRole = findViewById(R.id.textProfileRole);
        textProfileUserId = findViewById(R.id.textProfileUserId);
        btnLogoutProfile = findViewById(R.id.btnLogoutProfile);

        // Ambil data pengguna dari session
        HashMap<String, String> user = sessionManager.getUserDetails();
        String nama = user.get(SessionManager.KEY_NAMA);
        String role = user.get(SessionManager.KEY_ROLE);
        String userId = user.get(SessionManager.KEY_USER_ID);

        // Tampilkan data ke TextView
        textProfileNama.setText(nama);
        textProfileRole.setText(role);
        textProfileUserId.setText("ID: " + userId);

        // --- TAMBAHKAN LISTENER UNTUK TOMBOL LOGOUT ---
        btnLogoutProfile.setOnClickListener(v -> {
            // Panggil method logout dari SessionManager
            sessionManager.logoutUser();
            // Tutup halaman profil setelah logout
            finish();
        });
    }
}