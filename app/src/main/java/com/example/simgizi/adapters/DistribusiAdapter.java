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

    public interface OnItemClickListener {
        void onStatusButtonClick(Distribusi item, String newStatus);
        void onItemViewClick(Distribusi item);
    }


    public DistribusiAdapter(Context context, List<Distribusi> distribusiList) {
        this.context = context;
        this.distribusiList = distribusiList;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_distribusi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Distribusi distribusi = distribusiList.get(position);

        holder.textSekolah.setText(distribusi.getSekolah_tujuan());
        holder.textTanggal.setText("Tanggal: " + distribusi.getTanggal());
        holder.textJumlah.setText("Jumlah: " + distribusi.getJumlah() + " Porsi");

        String statusPengiriman = distribusi.getStatus_pengiriman();
        String statusText;
        int statusColor;

        switch (statusPengiriman) {
            case "0":
                statusText = "Belum Dikirim";
                statusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                holder.btnUbahStatus.setVisibility(View.VISIBLE);
                holder.btnUbahStatus.setText("Kirim");
                holder.btnUbahStatus.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusButtonClick(distribusi, "1");
                });
                break;
            case "1":
                statusText = "Dalam Perjalanan";
                statusColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                holder.btnUbahStatus.setVisibility(View.VISIBLE);
                holder.btnUbahStatus.setText("Selesaikan");
                holder.btnUbahStatus.setOnClickListener(v -> {
                    if (listener != null) listener.onStatusButtonClick(distribusi, "2");
                });
                break;
            case "2":
                statusText = "Diterima";
                statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                holder.btnUbahStatus.setVisibility(View.GONE);
                break;
            default:
                statusText = "Tidak Diketahui";
                statusColor = context.getResources().getColor(android.R.color.darker_gray);
                holder.btnUbahStatus.setVisibility(View.GONE);
                break;
        }
        holder.textStatus.setText(statusText);
        holder.textStatus.setBackgroundColor(statusColor);

        String foto = distribusi.getFoto();
        if (foto != null && !foto.isEmpty() && !foto.equalsIgnoreCase("null")) {
            String imageUrl = base_url_image + foto;
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_input_porsi)
                    .error(R.drawable.ic_riwayat)
                    .into(holder.imageDistribusi);
        } else {
            holder.imageDistribusi.setImageResource(R.drawable.ic_input_distribusi);
        }

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