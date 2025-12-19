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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView
        recycler = findViewById(R.id.recyclerPlayers);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Botón añadir jugador
        btnAdd = findViewById(R.id.btnAdd);

        // 🔐 CONTROL DE ROL
        if (!isAdmin()) {
            btnAdd.setVisibility(View.GONE); // jugador NO lo ve
        } else {
            btnAdd.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, AddPlayerActivity.class);
                startActivity(i);
            });
        }

        // Firestore
        db = FirebaseFirestore.getInstance();

        // Cargar jugadores
        loadPlayers();
    }

    // ================== SESIÓN ==================

    private boolean isAdmin() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        return prefs.getBoolean("isAdmin", false);
    }

    // ================== FIRESTORE ==================

    private void loadPlayers() {
        db.collection("players").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
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
                        Intent i = new Intent(MainActivity.this, PlayerDetailActivity.class);
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
