package com.example.futbolstats.model;

public class Player {

    public String id;
    public String name;
    public int number;
    public String photoUrl;
    public int globalScore;
    public Stats stats;
    public Physical physical;
    public Mental mental;

    // Constructor vacío requerido por Firebase
    public Player() {}

    // --- FORCE FIX: convierte strings o números sin crash ---
    public void setNumber(Object value) {
        this.number = parseInt(value);
    }

    public void setGlobalScore(Object value) {
        this.globalScore = parseInt(value);
    }

    // Método seguro para convertir cualquier cosa a int
    private int parseInt(Object v) {
        try {
            if (v instanceof Long) return ((Long) v).intValue();
            if (v instanceof Integer) return (int) v;
            if (v instanceof String) return Integer.parseInt((String) v);
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }
}
