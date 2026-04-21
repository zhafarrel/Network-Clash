package com.NetworkClash.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "master_cards")
public class CardEntity {

    @Id
    @Column(length = 50)
    private String id; // Contoh: "prog_01", "mal_01", "scpt_01"

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int cost; // Biaya RAM untuk memainkan kartu

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String type; // Nilai: "PROGRAM", "MALWARE", atau "SCRIPT"

    @Column(nullable = false)
    private String faction; // Nilai: "SYSADMIN" atau "OMEGA"

    // --- Atribut untuk Unit (PROGRAM & MALWARE) ---
    // Menggunakan Integer (Object) agar bisa bernilai NULL di database

    @Column(nullable = true)
    private Integer atk;

    @Column(nullable = true)
    private Integer hp;

    @Column(nullable = true)
    private String validLane; // Contoh: "LOCALHOST", "DMZ", "CLOUD", "DARK_NODE", "ANY_LANE"

    // --- Atribut untuk Spell (SCRIPT) ---

    @Column(nullable = true)
    private String effectType; // Contoh: "DIRECT_DAMAGE", "HEAL_HP", "BUFF_ATK"

    @Column(nullable = true)
    private Integer effectValue; // Besaran efek


    public CardEntity() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFaction() { return faction; }
    public void setFaction(String faction) { this.faction = faction; }

    public Integer getAtk() { return atk; }
    public void setAtk(Integer atk) { this.atk = atk; }

    public Integer getHp() { return hp; }
    public void setHp(Integer hp) { this.hp = hp; }

    public String getValidLane() { return validLane; }
    public void setValidLane(String validLane) { this.validLane = validLane; }

    public String getEffectType() { return effectType; }
    public void setEffectType(String effectType) { this.effectType = effectType; }

    public Integer getEffectValue() { return effectValue; }
    public void setEffectValue(Integer effectValue) { this.effectValue = effectValue; }
}