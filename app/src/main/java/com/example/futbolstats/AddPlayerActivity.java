package com.example.futbolstats;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPlayerActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText inputName, inputNumber, inputPhoto;
    EditText inputGoals, inputAssists, inputMatches;
    EditText inputSpeed, inputStrength, inputStamina;
    EditText inputVision, inputCreativity, inputLeadership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        inputName = findViewById(R.id.inputName);
        inputNumber = findViewById(R.id.inputNumber);
        inputPhoto = findViewById(R.id.inputPhoto);

        inputGoals = findViewById(R.id.inputGoals);
        inputAssists = findViewById(R.id.inputAssists);
        inputMatches = findViewById(R.id.inputMatches);

        inputSpeed = findViewById(R.id.inputSpeed);
        inputStrength = findViewById(R.id.inputStrength);
        inputStamina = findViewById(R.id.inputStamina);

        inputVision = findViewById(R.id.inputVision);
        inputCreativity = findViewById(R.id.inputCreativity);
        inputLeadership = findViewById(R.id.inputLeadership);

        Button btnSave = findViewById(R.id.btnSavePlayer);
        btnSave.setOnClickListener(v -> savePlayer());
    }

    private int getInt(EditText e) {
        return Integer.parseInt(e.getText().toString().trim());
    }

    private void savePlayer() {
        String name = inputName.getText().toString().trim();
        String photo = inputPhoto.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Pon un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> player = new HashMap<>();
        player.put("name", name);
        player.put("number", getInt(inputNumber));
        player.put("photoUrl", photo);

        // GENERAL STATS
        Map<String, Object> stats = new HashMap<>();
        stats.put("goals", getInt(inputGoals));
        stats.put("assists", getInt(inputAssists));
        stats.put("matches", getInt(inputMatches));

        // PHYSICAL
        Map<String, Object> physical = new HashMap<>();
        physical.put("speed", getInt(inputSpeed));
        physical.put("strength", getInt(inputStrength));
        physical.put("stamina", getInt(inputStamina));

        // MENTAL
        Map<String, Object> mental = new HashMap<>();
        mental.put("vision", getInt(inputVision));
        mental.put("creativity", getInt(inputCreativity));
        mental.put("leadership", getInt(inputLeadership));

        player.put("stats", stats);
        player.put("physical", physical);
        player.put("mental", mental);

        // Global Score
        int globalScore =
                (getInt(inputSpeed) +
                        getInt(inputStrength) +
                        getInt(inputStamina) +
                        getInt(inputVision) +
                        getInt(inputCreativity) +
                        getInt(inputLeadership)) / 6;

        player.put("globalScore", globalScore);

        db.collection("players")
                .add(player)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Jugador añadido", Toast.LENGTH_SHORT).show();
                    finish(); // volver al listado
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }
}
