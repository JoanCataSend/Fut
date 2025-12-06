package com.example.futbolstats;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.futbolstats.model.Player;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditStatsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String playerId;

    private int speed, strength, stamina, vision, creativity, leadership;

    private TextView txtSpeed, txtStrength, txtStamina, txtVision, txtCreativity, txtLeadership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stats);

        playerId = getIntent().getStringExtra("id");
        db = FirebaseFirestore.getInstance();

        txtSpeed = findViewById(R.id.txtSpeedValue);
        txtStrength = findViewById(R.id.txtStrengthValue);
        txtStamina = findViewById(R.id.txtStaminaValue);
        txtVision = findViewById(R.id.txtVisionValue);
        txtCreativity = findViewById(R.id.txtCreativityValue);
        txtLeadership = findViewById(R.id.txtLeadershipValue);

        Button btnSpeedMinus = findViewById(R.id.btnSpeedMinus);
        Button btnSpeedPlus = findViewById(R.id.btnSpeedPlus);
        Button btnStrengthMinus = findViewById(R.id.btnStrengthMinus);
        Button btnStrengthPlus = findViewById(R.id.btnStrengthPlus);
        Button btnStaminaMinus = findViewById(R.id.btnStaminaMinus);
        Button btnStaminaPlus = findViewById(R.id.btnStaminaPlus);
        Button btnVisionMinus = findViewById(R.id.btnVisionMinus);
        Button btnVisionPlus = findViewById(R.id.btnVisionPlus);
        Button btnCreativityMinus = findViewById(R.id.btnCreativityMinus);
        Button btnCreativityPlus = findViewById(R.id.btnCreativityPlus);
        Button btnLeadershipMinus = findViewById(R.id.btnLeadershipMinus);
        Button btnLeadershipPlus = findViewById(R.id.btnLeadershipPlus);
        Button btnSave = findViewById(R.id.btnSave);

        loadPlayer();

        btnSpeedPlus.setOnClickListener(v -> changeStat(1, "speed"));
        btnSpeedMinus.setOnClickListener(v -> changeStat(-1, "speed"));
        btnStrengthPlus.setOnClickListener(v -> changeStat(1, "strength"));
        btnStrengthMinus.setOnClickListener(v -> changeStat(-1, "strength"));
        btnStaminaPlus.setOnClickListener(v -> changeStat(1, "stamina"));
        btnStaminaMinus.setOnClickListener(v -> changeStat(-1, "stamina"));
        btnVisionPlus.setOnClickListener(v -> changeStat(1, "vision"));
        btnVisionMinus.setOnClickListener(v -> changeStat(-1, "vision"));
        btnCreativityPlus.setOnClickListener(v -> changeStat(1, "creativity"));
        btnCreativityMinus.setOnClickListener(v -> changeStat(-1, "creativity"));
        btnLeadershipPlus.setOnClickListener(v -> changeStat(1, "leadership"));
        btnLeadershipMinus.setOnClickListener(v -> changeStat(-1, "leadership"));

        btnSave.setOnClickListener(v -> saveStats());
    }

    private void changeStat(int delta, String stat) {
        switch (stat) {
            case "speed":
                speed = clamp(speed + delta);
                txtSpeed.setText(String.valueOf(speed));
                break;
            case "strength":
                strength = clamp(strength + delta);
                txtStrength.setText(String.valueOf(strength));
                break;
            case "stamina":
                stamina = clamp(stamina + delta);
                txtStamina.setText(String.valueOf(stamina));
                break;
            case "vision":
                vision = clamp(vision + delta);
                txtVision.setText(String.valueOf(vision));
                break;
            case "creativity":
                creativity = clamp(creativity + delta);
                txtCreativity.setText(String.valueOf(creativity));
                break;
            case "leadership":
                leadership = clamp(leadership + delta);
                txtLeadership.setText(String.valueOf(leadership));
                break;
        }
    }

    private int clamp(int value) {
        if (value < 0) return 0;
        if (value > 100) return 100;
        return value;
    }

    private void loadPlayer() {
        db.collection("players").document(playerId).get().addOnSuccessListener(d -> {
            Player p = d.toObject(Player.class);
            if (p == null || p.physical == null || p.mental == null) return;

            speed = p.physical.speed;
            strength = p.physical.strength;
            stamina = p.physical.stamina;
            vision = p.mental.vision;
            creativity = p.mental.creativity;
            leadership = p.mental.leadership;

            txtSpeed.setText(String.valueOf(speed));
            txtStrength.setText(String.valueOf(strength));
            txtStamina.setText(String.valueOf(stamina));
            txtVision.setText(String.valueOf(vision));
            txtCreativity.setText(String.valueOf(creativity));
            txtLeadership.setText(String.valueOf(leadership));
        });
    }

    private void saveStats() {
        int globalScore = (speed + strength + stamina + vision + creativity + leadership) / 6;

        Map<String, Object> updates = new HashMap<>();
        updates.put("physical.speed", speed);
        updates.put("physical.strength", strength);
        updates.put("physical.stamina", stamina);
        updates.put("mental.vision", vision);
        updates.put("mental.creativity", creativity);
        updates.put("mental.leadership", leadership);
        updates.put("globalScore", globalScore);

        db.collection("players").document(playerId)
                .update(updates)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Estadísticas guardadas", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }
}
