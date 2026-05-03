package com.NCFrontend.models;

import com.NCFrontend.logic.CardAbility;
import com.google.gson.annotations.SerializedName; // 1. TAMBAHKAN IMPORT INI
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCard {
    public String id;
    public String name;
    public String type;

    @SerializedName("cost") // 2. TAMBAHKAN ANOTASI INI DI SINI
    public int ramCost;

    public String description;
    public String faction;
    public String imagePath;

    public String validLane = "ANY_LANE";

    public List<CardAbility> abilities = new ArrayList<>();
}
