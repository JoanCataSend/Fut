package com.example.futbolstats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.futbolstats.model.Player;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    public interface OnPlayerClick {
        void onClick(Player player);
    }

    private List<Player> list;
    private OnPlayerClick listener;

    public PlayerAdapter(List<Player> list, OnPlayerClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player p = list.get(position);

        holder.txtName.setText(p.name);
        holder.txtNumber.setText("#" + p.number);

        // ⭐ ASIGNAR LOS PUNTOS DEL JUGADOR
        holder.txtPoints.setText(String.valueOf(p.globalScore));

        // FOTO
        if (p.photoUrl != null && !p.photoUrl.isEmpty()) {
            Picasso.get().load(p.photoUrl).into(holder.imgPlayer);
        }

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPlayer;
        TextView txtName, txtNumber, txtPoints;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPlayer = itemView.findViewById(R.id.imgPlayer);
            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);

            // ⭐ AÑADIMOS EL CAMPO DE PUNTOS
            txtPoints = itemView.findViewById(R.id.txtPoints);
        }
    }
}
