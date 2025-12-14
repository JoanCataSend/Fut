package com.example.futbolstats;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
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

    // ===== DATOS GENERALES =====
    private EditText inputName, inputGoals, inputAssists, inputMatches;

    // ===== STATS =====
    private int speed, strength, stamina, vision, creativity, leadership;

    private TextView txtSpeed, txtStrength, txtStamina;
    private TextView txtVision, txtCreativity, txtLeadership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stats);

        playerId = getIntent().getStringExtra("id");
        db = FirebaseFirestore.getInstance();

        // ===== INPUTS GENERALES =====
        inputName = findViewById(R.id.inputName);
        inputGoals = findViewById(R.id.inputGoals);
        inputAssists = findViewById(R.id.inputAssists);
        inputMatches = findViewById(R.id.inputMatches);

        // ===== TEXTOS DE STATS =====
        txtSpeed = findViewById(R.id.txtSpeedValue);
        txtStrength = findViewById(R.id.txtStrengthValue);
        txtStamina = findViewById(R.id.txtStaminaValue);
        txtVision = findViewById(R.id.txtVisionValue);
        txtCreativity = findViewById(R.id.txtCreativityValue);
        txtLeadership = findViewById(R.id.txtLeadershipValue);

        // ===== BOTONES =====
        findViewById(R.id.btnSpeedPlus).setOnClickListener(v -> changeStat(1, "speed"));
        findViewById(R.id.btnSpeedMinus).setOnClickListener(v -> changeStat(-1, "speed"));

        findViewById(R.id.btnStrengthPlus).setOnClickListener(v -> changeStat(1, "strength"));
        findViewById(R.id.btnStrengthMinus).setOnClickListener(v -> changeStat(-1, "strength"));

        findViewById(R.id.btnStaminaPlus).setOnClickListener(v -> changeStat(1, "stamina"));
        findViewById(R.id.btnStaminaMinus).setOnClickListener(v -> changeStat(-1, "stamina"));

        findViewById(R.id.btnVisionPlus).setOnClickListener(v -> changeStat(1, "vision"));
        findViewById(R.id.btnVisionMinus).setOnClickListener(v -> changeStat(-1, "vision"));

        findViewById(R.id.btnCreativityPlus).setOnClickListener(v -> changeStat(1, "creativity"));
        findViewById(R.id.btnCreativityMinus).setOnClickListener(v -> changeStat(-1, "creativity"));

        findViewById(R.id.btnLeadershipPlus).setOnClickListener(v -> changeStat(1, "leadership"));
        findViewById(R.id.btnLeadershipMinus).setOnClickListener(v -> changeStat(-1, "leadership"));

        findViewById(R.id.btnSave).setOnClickListener(v -> saveStats());

        loadPlayer();
    }

    // ================== LOAD ==================

    private void loadPlayer() {
        db.collection("players").document(playerId)
                .get()
                .addOnSuccessListener(d -> {
                    Player p = d.toObject(Player.class);
                    if (p == null) return;

                    // Generales
                    inputName.setText(p.name);
                    inputGoals.setText(String.valueOf(p.goals));
                    inputAssists.setText(String.valueOf(p.assists));
                    inputMatches.setText(String.valueOf(p.matches));

                    if (p.physical != null) {
                        speed = p.physical.speed;
                        strength = p.physical.strength;
                        stamina = p.physical.stamina;
                    }

                    if (p.mental != null) {
                        vision = p.mental.vision;
                        creativity = p.mental.creativity;
                        leadership = p.mental.leadership;
                    }

                    updateUI();
                });
    }

    private void updateUI() {
        txtSpeed.setText(String.valueOf(speed));
        txtStrength.setText(String.valueOf(strength));
        txtStamina.setText(String.valueOf(stamina));
        txtVision.setText(String.valueOf(vision));
        txtCreativity.setText(String.valueOf(creativity));
        txtLeadership.setText(String.valueOf(leadership));
    }

    // ================== LOGIC ==================

    private void changeStat(int delta, String stat) {
        switch (stat) {
            case "speed": speed = clamp(speed + delta); break;
            case "strength": strength = clamp(strength + delta); break;
            case "stamina": stamina = clamp(stamina + delta); break;
            case "vision": vision = clamp(vision + delta); break;
            case "creativity": creativity = clamp(creativity + delta); break;
            case "leadership": leadership = clamp(leadership + delta); break;
        }
        updateUI();
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }

    // ================== SAVE ==================

    private void saveStats() {

        String name = inputName.getText().toString().trim();
        int goals = parseInt(inputGoals);
        int assists = parseInt(inputAssists);
        int matches = parseInt(inputMatches);

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        int globalScore =
                (speed + strength + stamina + vision + creativity + leadership) / 6;

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("goals", goals);
        updates.put("assists", assists);
        updates.put("matches", matches);

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
                    Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }

    private int parseInt(EditText et) {
        try {
            return Integer.parseInt(et.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }
}
