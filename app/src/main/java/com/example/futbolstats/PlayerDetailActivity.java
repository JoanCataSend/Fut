package com.example.futbolstats;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.futbolstats.model.Player;
import com.github.mikephil.charting.formatter.ValueFormatter;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ImageView img;
    private TextView name, txtPoints;
    private RadarChart radarChart;
    private Button btnEdit;
    private String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        playerId = getIntent().getStringExtra("id");
        db = FirebaseFirestore.getInstance();

        img = findViewById(R.id.imgPlayerDetail);
        name = findViewById(R.id.txtPlayerName);
        txtPoints = findViewById(R.id.txtHeight); // el cuadro "PUNTOS"
        radarChart = findViewById(R.id.radarChart);
        btnEdit = findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(PlayerDetailActivity.this, EditStatsActivity.class);
            i.putExtra("id", playerId);
            startActivity(i);
        });

        loadPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlayer(); // refrescar al volver de editar
    }

    private void loadPlayer() {
        db.collection("players").document(playerId).get().addOnSuccessListener(this::bindPlayer);
    }

    private void bindPlayer(DocumentSnapshot d) {
        Player p = d.toObject(Player.class);
        if (p == null) return;

        // Nombre
        name.setText(p.name);

        // Foto
        if (p.photoUrl != null && !p.photoUrl.isEmpty())
            Picasso.get().load(p.photoUrl).into(img);

        // Puntos del panel negro
        txtPoints.setText(String.valueOf(p.globalScore));

        // Radar
        setupRadar(p);
    }

    private void setupRadar(Player p) {
        if (p.physical == null || p.mental == null) return;

        ArrayList<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(p.physical.speed));
        entries.add(new RadarEntry(p.physical.strength));
        entries.add(new RadarEntry(p.physical.stamina));
        entries.add(new RadarEntry(p.mental.vision));
        entries.add(new RadarEntry(p.mental.creativity));
        entries.add(new RadarEntry(p.mental.leadership));

        RadarDataSet set = new RadarDataSet(entries, "");
        set.setColor(Color.WHITE);
        set.setFillColor(Color.parseColor("#FF3BAA"));
        set.setDrawFilled(true);
        set.setFillAlpha(150);
        set.setLineWidth(3f);
        set.setDrawValues(false);

        RadarData data = new RadarData(set);
        radarChart.setData(data);

        // Etiquetas del radar
        List<String> labels = Arrays.asList(
                "Velocidad", "Fuerza", "Resistencia",
                "Visión", "Creatividad", "Liderazgo"
        );

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value % labels.size();
                return labels.get(index);
            }
        });

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setEnabled(false); // oculta números interiores

        // Estilo de la red
        radarChart.setBackgroundColor(Color.TRANSPARENT);
        radarChart.setWebColor(Color.WHITE);
        radarChart.setWebLineWidth(1.5f);
        radarChart.setWebColorInner(Color.WHITE);
        radarChart.setWebLineWidthInner(1.2f);

        radarChart.getDescription().setEnabled(false);
        radarChart.getLegend().setEnabled(false);

        radarChart.invalidate();
    }
}
