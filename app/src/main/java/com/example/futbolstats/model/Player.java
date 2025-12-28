package com.example.futbolstats.model;

public class Player {

    // ======================
    // DATOS BÁSICOS
    // ======================
    public String id;
    public String name;
    public int number;
    public String photoUrl;

    // 👉 NUEVO: posición del jugador
    // "field" | "goalkeeper"
    public String position;

    // ======================
    // ESTADÍSTICAS GENERALES (NUEVO MODELO)
    // ======================
    public int goals;
    public int assists;
    public int matches;
    public int globalScore;

    // ======================
    // COMPATIBILIDAD FIRESTORE (MODELO ANTIGUO)
    // ======================
    // ⚠️ SOLO para evitar warnings si existen documentos antiguos
    public Stats stats;

    // ======================
    // ATRIBUTOS (JUGADORES DE CAMPO)
    // ======================
    public Physical physical;
    public Mental mental;

    // ======================
    // ATRIBUTOS (PORTEROS)  🔥 NUEVO
    // ======================
    public Goalkeeper goalkeeper;

    // 🔴 Constructor vacío OBLIGATORIO para Firestore
    public Player() {}

    // ======================
    // CLASES INTERNAS
    // ======================

    // ---- Stats antiguas (Firestore legacy) ----
    public static class Stats {
        public int goals;
        public int assists;
        public int matches;

        public Stats() {}
    }

    // ---- Atributos físicos (jugadores de campo) ----
    public static class Physical {
        public int speed;
        public int strength;
        public int stamina;

        public Physical() {}
    }

    // ---- Atributos mentales (jugadores de campo) ----
    public static class Mental {
        public int vision;
        public int creativity;
        public int leadership;

        public Mental() {}
    }

    // ---- Atributos de PORTERO ----
    public static class Goalkeeper {
        public int reflexes;      // Reflejos
        public int positioning;   // Colocación
        public int saves;         // Paradas
        public int aerial;        // Juego aéreo
        public int distribution;  // Saque / pase
        public int oneOnOne;      // Mano a mano

        public Goalkeeper() {}
    }
}
