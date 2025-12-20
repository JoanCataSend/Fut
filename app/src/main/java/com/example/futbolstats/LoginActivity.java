package com.example.futbolstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUser, inputPass;
    private Button btnLogin, btnPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔐 SI YA HAY SESIÓN → SALTAR LOGIN
        if (hasSession()) {
            openApp();
            return;
        }

        setContentView(R.layout.activity_login);

        inputUser = findViewById(R.id.inputUser);
        inputPass = findViewById(R.id.inputPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnPlayer = findViewById(R.id.btnPlayer);

        // LOGIN ADMIN
        btnLogin.setOnClickListener(v -> {
            String user = inputUser.getText().toString().trim();
            String pass = inputPass.getText().toString().trim();

            if (user.equals("admin") && pass.equals("123qweasd")) {
                saveRole(true);
                openApp();
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
        });

        // SOY JUGADOR
        btnPlayer.setOnClickListener(v -> {
            saveRole(false);
            openApp();
        });
    }

    // ================= SESIÓN =================

    private boolean hasSession() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        return prefs.contains("isAdmin");
    }

    private void saveRole(boolean isAdmin) {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit().putBoolean("isAdmin", isAdmin).apply();
    }

    private void openApp() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
