package com.example.futbolstats;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
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

    private EditText txtSpeed, txtStrength, txtStamina;
    private EditText txtVision, txtCreativity, txtLeadership;

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

        // ===== STATS (EDITABLES) =====
        txtSpeed = findViewById(R.id.txtSpeedValue);
        txtStrength = findViewById(R.id.txtStrengthValue);
        txtStamina = findViewById(R.id.txtStaminaValue);
        txtVision = findViewById(R.id.txtVisionValue);
        txtCreativity = findViewById(R.id.txtCreativityValue);
        txtLeadership = findViewById(R.id.txtLeadershipValue);

        // ===== BOTONES + / - =====
        findViewById(R.id.btnSpeedPlus).setOnClickListener(v -> updateStat(txtSpeed, +1));
        findViewById(R.id.btnSpeedMinus).setOnClickListener(v -> updateStat(txtSpeed, -1));

        findViewById(R.id.btnStrengthPlus).setOnClickListener(v -> updateStat(txtStrength, +1));
        findViewById(R.id.btnStrengthMinus).setOnClickListener(v -> updateStat(txtStrength, -1));

        findViewById(R.id.btnStaminaPlus).setOnClickListener(v -> updateStat(txtStamina, +1));
        findViewById(R.id.btnStaminaMinus).setOnClickListener(v -> updateStat(txtStamina, -1));

        findViewById(R.id.btnVisionPlus).setOnClickListener(v -> updateStat(txtVision, +1));
        findViewById(R.id.btnVisionMinus).setOnClickListener(v -> updateStat(txtVision, -1));

        findViewById(R.id.btnCreativityPlus).setOnClickListener(v -> updateStat(txtCreativity, +1));
        findViewById(R.id.btnCreativityMinus).setOnClickListener(v -> updateStat(txtCreativity, -1));

        findViewById(R.id.btnLeadershipPlus).setOnClickListener(v -> updateStat(txtLeadership, +1));
        findViewById(R.id.btnLeadershipMinus).setOnClickListener(v -> updateStat(txtLeadership, -1));

        // ===== LISTENERS DE ESCRITURA =====
        addWatcher(txtSpeed, v -> speed = v);
        addWatcher(txtStrength, v -> strength = v);
        addWatcher(txtStamina, v -> stamina = v);
        addWatcher(txtVision, v -> vision = v);
        addWatcher(txtCreativity, v -> creativity = v);
        addWatcher(txtLeadership, v -> leadership = v);

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

                    txtSpeed.setText(String.valueOf(speed));
                    txtStrength.setText(String.valueOf(strength));
                    txtStamina.setText(String.valueOf(stamina));
                    txtVision.setText(String.valueOf(vision));
                    txtCreativity.setText(String.valueOf(creativity));
                    txtLeadership.setText(String.valueOf(leadership));
                });
    }

    // ================== HELPERS ==================

    private void updateStat(EditText et, int delta) {
        int v = parse(et.getText().toString());
        v = clamp(v + delta);
        et.setText(String.valueOf(v));
    }

    private void addWatcher(EditText et, StatCallback cb) {
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                int v = clamp(parse(s.toString()));
                cb.onChange(v);
            }
        });
    }

    private int parse(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return 0; }
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }

    // ================== SAVE ==================

    private void saveStats() {

        String name = inputName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        int goals = parse(inputGoals.getText().toString());
        int assists = parse(inputAssists.getText().toString());
        int matches = parse(inputMatches.getText().toString());

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

    // ================== CALLBACK ==================
    private interface StatCallback {
        void onChange(int value);
    }
}
