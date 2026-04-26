package com.NCFrontend.models;

public class ProgramData extends BaseCard {
    public int hp;
    public int maxHp; // Berguna jika nanti ada efek heal (System Patch)
    public int atk;

    public ProgramData(String name, int ramCost, int hp, int atk, String faction, String description) {
        this.name = name;
        this.ramCost = ramCost;
        this.hp = hp;
        this.maxHp = hp; // Awalnya max HP sama dengan HP awal
        this.atk = atk;
        this.faction = faction;
        this.description = description;
    }
}
