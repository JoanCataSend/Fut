package com.example.futbolstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.futbolstats.model.Player;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private PlayerAdapter adapter;
    private final List<Player> players = new ArrayList<>();

    private FirebaseFirestore db;

    private Button btnAdd;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔐 Si no hay sesión, volvemos al login
        if (!hasSession()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // RecyclerView
        recycler = findViewById(R.id.recyclerPlayers);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Botones
        btnAdd = findViewById(R.id.btnAdd);
        btnLogout = findViewById(R.id.btnLogout);

        boolean isAdmin = isAdmin();

        // ================= PERMISOS =================

        if (!isAdmin) {
            // 👤 MODO JUGADOR
            btnAdd.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        } else {
            // 👑 MODO ADMIN
            btnAdd.setOnClickListener(v ->
                    startActivity(new Intent(this, AddPlayerActivity.class))
            );

            btnLogout.setOnClickListener(v -> logout());
        }

        // Firestore
        db = FirebaseFirestore.getInstance();
        loadPlayers();
    }

    // ================= SESIÓN =================

    private boolean hasSession() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        return prefs.contains("isAdmin");
    }

    private boolean isAdmin() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        return prefs.getBoolean("isAdmin", false);
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    // ================= FIRESTORE =================

    private void loadPlayers() {
        db.collection("players")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(
                            @Nullable QuerySnapshot snapshot,
                            @Nullable FirebaseFirestoreException error) {

                        if (error != null || snapshot == null) return;

                        players.clear();

                        for (DocumentSnapshot d : snapshot.getDocuments()) {
                            Player p = d.toObject(Player.class);
                            if (p != null) {
                                p.id = d.getId();
                                players.add(p);
                            }
                        }

                        if (adapter == null) {
                            adapter = new PlayerAdapter(players, player -> {
                                Intent i = new Intent(
                                        MainActivity.this,
                                        PlayerDetailActivity.class
                                );
                                i.putExtra("id", player.id);
                                startActivity(i);
                            });
                            recycler.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
