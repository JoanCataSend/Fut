package com.example.futbolstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.futbolstats.model.Player;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private ImageView img;
    private TextView name, txtPoints, txtGoals, txtAssists, txtMatches;
    private RadarChart radarChart;
    private Button btnEdit;

    private String playerId;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        playerId = getIntent().getStringExtra("id");
        if (playerId == null) {
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        img        = findViewById(R.id.imgPlayerDetail);
        name       = findViewById(R.id.txtPlayerName);
        txtPoints  = findViewById(R.id.txtPoints);
        txtGoals   = findViewById(R.id.txtGoals);
        txtAssists = findViewById(R.id.txtAssists);
        txtMatches = findViewById(R.id.txtMatches);
        radarChart = findViewById(R.id.radarChart);
        btnEdit    = findViewById(R.id.btnEdit);

        // 🔐 CONTROL DE ROL
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        boolean isAdmin = prefs.getBoolean("isAdmin", false);

        if (!isAdmin) {
            btnEdit.setVisibility(View.GONE);
        }

        // Listener UNA sola vez
        btnEdit.setOnClickListener(v -> {
            if (player == null) return;

            if ("goalkeeper".equals(player.position)) {
                Intent i = new Intent(this, EditGoalkeeperStatsActivity.class);
                i.putExtra("id", player.id);
                startActivity(i);
            } else {
                Intent i = new Intent(this, EditStatsActivity.class);
                i.putExtra("id", player.id);
                startActivity(i);
            }
        });

        loadPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlayer();
    }

    // ================= FIRESTORE =================

    private void loadPlayer() {
        db.collection("players")
                .document(playerId)
                .get()
                .addOnSuccessListener(this::bindPlayer);
    }

    private void bindPlayer(DocumentSnapshot d) {
        player = d.toObject(Player.class);
        if (player == null) return;

        player.id = d.getId();

        // 🔒 Valor por defecto
        if (player.position == null) {
            player.position = "field";
        }

        name.setText(player.name != null ? player.name : "");
        txtPoints.setText(String.valueOf(player.globalScore));
        txtGoals.setText(String.valueOf(player.goals));
        txtAssists.setText(String.valueOf(player.assists));
        txtMatches.setText(String.valueOf(player.matches));

        // Opcional: ocultar stats ofensivas a porteros
        if ("goalkeeper".equals(player.position)) {
            txtGoals.setVisibility(View.GONE);
            txtAssists.setVisibility(View.GONE);
        } else {
            txtGoals.setVisibility(View.VISIBLE);
            txtAssists.setVisibility(View.VISIBLE);
        }

        if (player.photoUrl != null && !player.photoUrl.isEmpty()) {
            Picasso.get().load(player.photoUrl).fit().centerCrop().into(img);
        } else {
            img.setImageResource(R.mipmap.ic_launcher);
        }

        setupRadar(player);
    }

    // ================= RADAR =================

    private void setupRadar(Player p) {
        if ("goalkeeper".equals(p.position)) {
            setupGoalkeeperRadar(p);
        } else {
            setupFieldRadar(p);
        }
    }

    private void setupFieldRadar(Player p) {
        if (p.physical == null || p.mental == null) return;

        ArrayList<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(p.physical.speed));
        entries.add(new RadarEntry(p.physical.strength));
        entries.add(new RadarEntry(p.physical.stamina));
        entries.add(new RadarEntry(p.mental.vision));
        entries.add(new RadarEntry(p.mental.creativity));
        entries.add(new RadarEntry(p.mental.leadership));

        List<String> labels = Arrays.asList(
                "Velocidad", "Fuerza", "Resistencia",
                "Visión", "Creatividad", "Liderazgo"
        );

        drawRadar(entries, labels);
    }

    private void setupGoalkeeperRadar(Player p) {
        if (p.goalkeeper == null) return;

        ArrayList<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(p.goalkeeper.reflexes));
        entries.add(new RadarEntry(p.goalkeeper.positioning));
        entries.add(new RadarEntry(p.goalkeeper.saves));
        entries.add(new RadarEntry(p.goalkeeper.aerial));
        entries.add(new RadarEntry(p.goalkeeper.distribution));
        entries.add(new RadarEntry(p.goalkeeper.oneOnOne));

        List<String> labels = Arrays.asList(
                "Reflejos", "Colocación", "Paradas",
                "Juego aéreo", "Distribución", "1 vs 1"
        );

        drawRadar(entries, labels);
    }

    private void drawRadar(List<RadarEntry> entries, List<String> labels) {
        RadarDataSet set = new RadarDataSet(entries, "");
        set.setColor(Color.WHITE);
        set.setFillColor(Color.parseColor("#FF3BAA"));
        set.setDrawFilled(true);
        set.setFillAlpha(150);
        set.setLineWidth(3f);
        set.setDrawValues(false);

        radarChart.setData(new RadarData(set));

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels.get(((int) value) % labels.size());
            }
        });

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setEnabled(false);

        radarChart.getLegend().setEnabled(false);
        radarChart.getDescription().setEnabled(false);
        radarChart.invalidate();
    }
}
