package com.example.simgizi.adapters;

import static com.example.simgizi.api.config.base_url_image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.simgizi.R;
import com.example.simgizi.models.Distribusi;

import java.util.List;

public class DistribusiAdapter extends RecyclerView.Adapter<DistribusiAdapter.ViewHolder> {

    private Context context;
    private List<Distribusi> distribusiList;
    private OnItemClickListener listener;

    // PENTING: Ganti URL ini dengan alamat IP dan folder proyek Anda di server!
    private final String UPLOADS_BASE_URL = "http://192.168.1.10/nama_folder_projek/uploads/";

    /**
     * Interface untuk menangani klik pada item di RecyclerView.
     */
    public interface OnItemClickListener {
        void onStatusButtonClick(Distribusi item, String newStatus);
        void onItemViewClick(Distribusi item);
    }

    /**
     * Constructor untuk adapter.
     * @param context Konteks dari Activity yang memanggil.
     * @param distribusiList Daftar data yang akan ditampilkan.
     */
    public DistribusiAdapter(Context context, List<Distribusi> distribusiList) {
        this.context = context;
        this.distribusiList = distribusiList;
    }

    /**
     * Method untuk mendaftarkan listener dari Activity.
     * @param listener Activity yang mengimplementasikan OnItemClickListener.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Membuat view baru dari layout list_item_distribusi.xml
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_distribusi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Mengambil data pada posisi tertentu
        Distribusi distribusi = distribusiList.get(position);

        // Mengisi data ke dalam komponen UI di ViewHolder
        holder.textSekolah.setText(distribusi.getSekolah_tujuan());
        holder.textTanggal.setText("Tanggal: " + distribusi.getTanggal());
        holder.textJumlah.setText("Jumlah: " + distribusi.getJumlah() + " Porsi");

        // --- Logika untuk Status dan Tombol ---
        String statusPengiriman = distribusi.getStatus_pengiriman();
        String statusText;
        int statusColor;

        switch (statusPengiriman) {
            case "0": // Belum Dikirim
                statusText = "Belum Dikirim";
                statusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                holder.btnUbahStatus.setVisibility(View.VISIBLE);
                holder.btnUbahStatus.setText("Kirim");
                holder.btnUbahStatus.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusButtonClick(distribusi, "1");
                });
                break;
            case "1": // Dalam Perjalanan
                statusText = "Dalam Perjalanan";
                statusColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                holder.btnUbahStatus.setVisibility(View.VISIBLE);
                holder.btnUbahStatus.setText("Selesaikan");
                holder.btnUbahStatus.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusButtonClick(distribusi, "2");
                });
                break;
            case "2": // Diterima
                statusText = "Diterima";
                statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                holder.btnUbahStatus.setVisibility(View.GONE); // Sembunyikan tombol jika sudah selesai
                break;
            default:
                statusText = "Tidak Diketahui";
                statusColor = context.getResources().getColor(android.R.color.darker_gray);
                holder.btnUbahStatus.setVisibility(View.GONE);
                break;
        }
        holder.textStatus.setText(statusText);
        holder.textStatus.setBackgroundColor(statusColor);

        // --- Memuat Gambar menggunakan Glide ---
        String foto = distribusi.getFoto();
        if (foto != null && !foto.isEmpty() && !foto.equalsIgnoreCase("null")) {
            String imageUrl = base_url_image + foto;
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_input_porsi) // Gambar default saat loading
                    .error(R.drawable.ic_riwayat)         // Gambar default jika terjadi error
                    .into(holder.imageDistribusi);
        } else {
            // Jika tidak ada URL foto, tampilkan gambar default
            holder.imageDistribusi.setImageResource(R.drawable.ic_input_distribusi);
        }

        // Menangani klik pada seluruh area kartu untuk membuka detail
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemViewClick(distribusi);
            }
        });
    }

    @Override
    public int getItemCount() {
        return distribusiList.size();
    }

    /**
     * ViewHolder untuk menyimpan referensi ke komponen UI di setiap item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textSekolah, textTanggal, textJumlah, textStatus;
        Button btnUbahStatus;
        ImageView imageDistribusi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSekolah = itemView.findViewById(R.id.textSekolah);
            textTanggal = itemView.findViewById(R.id.textTanggal);
            textJumlah = itemView.findViewById(R.id.textJumlah);
            textStatus = itemView.findViewById(R.id.textStatus);
            btnUbahStatus = itemView.findViewById(R.id.btnUbahStatus);
            imageDistribusi = itemView.findViewById(R.id.imageDistribusi);
        }
    }
}