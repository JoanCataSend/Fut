package com.example.futbolstats.model;

public class Player {

    // ======================
    // DATOS BÁSICOS
    // ======================
    public String id;
    public String name;
    public int number;
    public String photoUrl;

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
    // ATRIBUTOS
    // ======================
    public Physical physical;
    public Mental mental;

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

    // ---- Atributos físicos ----
    public static class Physical {
        public int speed;
        public int strength;
        public int stamina;

        public Physical() {}
    }

    // ---- Atributos mentales ----
    public static class Mental {
        public int vision;
        public int creativity;
        public int leadership;

        public Mental() {}
    }
}
