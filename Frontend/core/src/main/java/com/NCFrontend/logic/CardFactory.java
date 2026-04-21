package com.NCFrontend.logic;

import com.NCFrontend.models.*;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.*;

public class CardFactory {
    private static Gson gson = new Gson();

    public static CardActor createVisualCard(String cardJsonText) {
        // Parsing ke JsonObject untuk intip field "type"
        JsonObject jsonObject = gson.fromJson(cardJsonText, JsonObject.class);
        String type = jsonObject.get("type").getAsString();

        BaseCard data;
        if (type.equalsIgnoreCase("SCRIPT")) {
            data = gson.fromJson(cardJsonText, ScriptData.class);
        } else {
            // Berlaku untuk PROGRAM dan MALWARE
            data = gson.fromJson(cardJsonText, ProgramData.class);
        }

        // Ambil tekstur (Pastikan field 'id' di BaseCard adalah public atau punya getter)
        Texture texture;
        try {
            // Jika 'id' merah, ganti jadi data.getId()
            texture = new Texture(Gdx.files.internal("images/" + data.id + ".png"));
        } catch (Exception e) {
            Gdx.app.error("CardFactory", "Image not found for: " + data.id);
            texture = new Texture(Gdx.files.internal("libgdx.png"));
        }

        return new CardActor(data, texture);
    }
}
