package com.example.futbolstats;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.futbolstats.model.Player;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditGoalkeeperStatsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String playerId;

    private Player player;

    // ===== GENERALES =====
    private EditText inputName, inputGoals, inputAssists, inputMatches;

    // ===== PORTERO (REUTILIZA IDS DE CAMPO) =====
    private EditText txtSpeed, txtStrength, txtStamina;
    private EditText txtVision, txtCreativity, txtLeadership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stats_goalkeeper);

        playerId = getIntent().getStringExtra("id");
        if (playerId == null) {
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        // ===== GENERALES =====
        inputName = findViewById(R.id.inputName);
        inputGoals = findViewById(R.id.inputGoals);
        inputAssists = findViewById(R.id.inputAssists);
        inputMatches = findViewById(R.id.inputMatches);

        // ===== EDITTEXTS (IDS REALES DEL XML) =====
        txtSpeed = findViewById(R.id.txtSpeedValue);           // Reflejos
        txtStrength = findViewById(R.id.txtStrengthValue);     // Colocación
        txtStamina = findViewById(R.id.txtStaminaValue);       // Paradas
        txtVision = findViewById(R.id.txtVisionValue);         // Juego aéreo
        txtCreativity = findViewById(R.id.txtCreativityValue); // 1 vs 1
        txtLeadership = findViewById(R.id.txtLeadershipValue); // Juego con pies

        // ===== BOTONES + / - (IDS REALES) =====
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

        findViewById(R.id.btnSave).setOnClickListener(v -> save());

        loadPlayer();
    }

    // ================= LOAD =================

    private void loadPlayer() {
        db.collection("players")
                .document(playerId)
                .get()
                .addOnSuccessListener(d -> {

                    player = d.toObject(Player.class);
                    if (player == null) return;

                    player.id = d.getId();

                    inputName.setText(player.name);
                    inputGoals.setText(String.valueOf(player.goals));
                    inputAssists.setText(String.valueOf(player.assists));
                    inputMatches.setText(String.valueOf(player.matches));

                    if (player.goalkeeper == null) {
                        player.goalkeeper = new Player.Goalkeeper();
                    }

                    txtSpeed.setText(String.valueOf(player.goalkeeper.reflexes));
                    txtStrength.setText(String.valueOf(player.goalkeeper.positioning));
                    txtStamina.setText(String.valueOf(player.goalkeeper.saves));
                    txtVision.setText(String.valueOf(player.goalkeeper.aerial));
                    txtCreativity.setText(String.valueOf(player.goalkeeper.oneOnOne));
                    txtLeadership.setText(String.valueOf(player.goalkeeper.distribution));
                });
    }

    // ================= HELPERS =================

    private void updateStat(EditText et, int delta) {
        int v = parse(et);
        v = clamp(v + delta);
        et.setText(String.valueOf(v));
    }

    // ================= SAVE =================

    private void save() {

        if (player == null) return;

        String name = inputName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nombre obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("name", name);
        update.put("goals", parse(inputGoals));
        update.put("assists", parse(inputAssists));
        update.put("matches", parse(inputMatches));
        update.put("position", "goalkeeper");

        update.put("goalkeeper.reflexes", parse(txtSpeed));
        update.put("goalkeeper.positioning", parse(txtStrength));
        update.put("goalkeeper.saves", parse(txtStamina));
        update.put("goalkeeper.aerial", parse(txtVision));
        update.put("goalkeeper.oneOnOne", parse(txtCreativity));
        update.put("goalkeeper.distribution", parse(txtLeadership));

        db.collection("players")
                .document(playerId)
                .update(update)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Portero actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    // ================= UTILS =================

    private int parse(EditText e) {
        try {
            return Integer.parseInt(e.getText().toString().trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(100, v));
    }
}
