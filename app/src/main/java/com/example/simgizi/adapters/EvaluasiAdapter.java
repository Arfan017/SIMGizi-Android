package com.example.simgizi.adapters;

import static com.example.simgizi.api.config.base_url_image;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Import ImageView jika belum ada
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simgizi.R;
import com.example.simgizi.models.Evaluasi;

import java.util.List;

// Import Glide jika ingin memuat ikon dinamis
// import com.bumptech.glide.Glide;

public class EvaluasiAdapter extends RecyclerView.Adapter<EvaluasiAdapter.ViewHolder> {
    private Context context;
    private List<Evaluasi> evaluasiList;

    public EvaluasiAdapter(Context context, List<Evaluasi> evaluasiList) {
        this.context = context;
        this.evaluasiList = evaluasiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_evaluasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Evaluasi evaluasi = evaluasiList.get(position);

        // --- Mengisi TextView Nilai ---
        holder.textEvalNamaSekolah.setText(evaluasi.getNama_sekolah());
        holder.textEvalTanggalDistribusi.setText(evaluasi.getTanggal()); // Mengisi nilai tanggal
        holder.textEvalCatatan.setText(evaluasi.getCatatan()); // Mengisi nilai catatan

        // --- Logika Status Distribusi (Angka ke Teks) ---
        String statusNumerik = evaluasi.getStatus_distribusi();
        String statusTeks = "-";
        int statusColor = Color.DKGRAY; // Warna default

        switch (statusNumerik) {
            case "1":
                statusTeks = "Baik";
                statusColor = ContextCompat.getColor(context, android.R.color.holo_green_dark); // Hijau
                break;
            case "2":
                statusTeks = "Kurang Baik";
                statusColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark); // Oranye
                break;
            case "3":
                statusTeks = "Tidak Baik";
                statusColor = ContextCompat.getColor(context, android.R.color.holo_red_dark); // Merah
                break;
            default:
                statusTeks = "N/A"; // Jika status tidak dikenali
                break;
        }
        holder.textEvalStatus.setText(statusTeks);
        holder.textEvalStatus.setTextColor(statusColor); // Ubah warna teksnya

        // --- Memuat Gambar menggunakan Glide ---
        String namaGambar = evaluasi.getGambar();
        if (namaGambar != null && !namaGambar.isEmpty() && !namaGambar.equalsIgnoreCase("null")) {
            String imageUrl = base_url_image + namaGambar;
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_input_porsi) // Gambar saat loading
                    .error(R.drawable.ic_riwayat)         // Gambar jika error
                    .into(holder.imageEvalIcon); // Target ImageView
        } else {
            // Jika tidak ada gambar, tampilkan ikon default
            holder.imageEvalIcon.setImageResource(R.drawable.ic_evaluasi); // Ikon evaluasi default
        }
    }

    @Override
    public int getItemCount() {
        return evaluasiList.size();
    }

    // ViewHolder tetap sama seperti sebelumnya
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageEvalIcon;
        TextView textEvalNamaSekolah, textEvalTanggalDistribusi, textEvalCatatan, textEvalStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageEvalIcon = itemView.findViewById(R.id.imageEvalIcon);
            textEvalNamaSekolah = itemView.findViewById(R.id.textEvalNamaSekolah);
            textEvalTanggalDistribusi = itemView.findViewById(R.id.textEvalTanggalDistribusi);
            textEvalCatatan = itemView.findViewById(R.id.textEvalCatatan);
            textEvalStatus = itemView.findViewById(R.id.textEvalStatus);
        }
    }
}