package com.NCFrontend.models;

public class PlayerData {
    public String playerName;
    public String faction; // "Sysadmin" atau "O.M.E.G.A"

    public int hp;         // System Integrity saat ini
    public int maxHp;      // Batas maksimal System Integrity

    public int currentRam; // RAM yang bisa dipakai giliran ini
    public int maxRam;     // Kapasitas slot RAM (Biasanya bertambah 1 setiap giliran)

    public PlayerData(String playerName, String faction, int startingHp) {
        this.playerName = playerName;
        this.faction = faction;
        this.hp = startingHp;
        this.maxHp = startingHp;
        this.currentRam = 1; // Mulai dengan 1 RAM di giliran pertama
        this.maxRam = 1;
    }

    // Fungsi pembantu jika butuh nge-heal
    public void heal(int amount) {
        this.hp += amount;
        if (this.hp > maxHp) this.hp = maxHp;
    }

    // Fungsi pembantu saat menerima damage langsung
    public void takeDamage(int amount) {
        this.hp -= amount;
        if (this.hp < 0) this.hp = 0;
    }
}
