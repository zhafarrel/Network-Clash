package com.NCFrontend.models;

public abstract class BaseCard {
    public String id;
    public String name;
    public int cost;
    public String description;
    public String type; // PROGRAM, MALWARE, SCRIPT
    public String faction;
    public String validLane;
}
