package com.example.simgizi.adapters;

import static com.example.simgizi.api.config.base_url_image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide
import com.example.simgizi.R;
import com.example.simgizi.models.Distribusi;

import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private Context context;
    private List<Distribusi> riwayatList;
    // Ganti dengan base URL folder uploads Anda

    public RiwayatAdapter(Context context, List<Distribusi> riwayatList) {
        this.context = context;
        this.riwayatList = riwayatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_riwayat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Distribusi riwayat = riwayatList.get(position);

        holder.textSekolahRiwayat.setText(riwayat.getSekolah_tujuan());
        holder.textTanggalRiwayat.setText(riwayat.getTanggal());
        holder.textJumlahRiwayat.setText(riwayat.getJumlah() + " Porsi");

        // Memuat gambar menggunakan Glide
        String imageUrl = base_url_image + riwayat.getFoto();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background) // Gambar sementara saat loading
                .error(R.drawable.ic_input_distribusi) // Gambar jika gagal load
                .into(holder.imageFotoRiwayat);
    }

    @Override
    public int getItemCount() {
        return riwayatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFotoRiwayat;
        TextView textSekolahRiwayat, textTanggalRiwayat, textJumlahRiwayat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageFotoRiwayat = itemView.findViewById(R.id.imageFotoRiwayat);
            textSekolahRiwayat = itemView.findViewById(R.id.textSekolahRiwayat);
            textTanggalRiwayat = itemView.findViewById(R.id.textTanggalRiwayat);
            textJumlahRiwayat = itemView.findViewById(R.id.textJumlahRiwayat);
        }
    }
}