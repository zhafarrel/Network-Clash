package com.NCFrontend.ui;

import com.NCFrontend.models.BaseCard;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.models.ScriptData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Matrix4;

public class CardActor extends Group {
    private BaseCard data;
    private Image illustration;
    private Image frame;
    private BitmapFont font;

    private static Texture executeBtnTex;
    private static Texture swordIcon;
    private static Texture heartIcon;

    public boolean isFaceUp = true;
    public boolean isFlooped = false;
    public boolean isOnBoard = false;
    public boolean isStunned = false;
    public boolean isSilenced = false;

    public CardActor(BaseCard data, Texture illustrationTex) {
        this.data = data;

        this.setSize(210, 300);
        this.setOrigin(getWidth() / 2f, getHeight() / 2f);

        // --- INISIALISASI TEKSTUR & IKON ---
        if (executeBtnTex == null) {
            Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pix.setColor(Color.ORANGE); pix.fill();
            executeBtnTex = new Texture(pix); pix.dispose();
        }

        if (swordIcon == null) {
            try {
                swordIcon = new Texture(Gdx.files.internal("images/sword.png"));
            } catch (Exception e) {
                Pixmap pix = new Pixmap(40, 40, Pixmap.Format.RGBA8888);
                pix.setColor(Color.LIGHT_GRAY); pix.fill();
                swordIcon = new Texture(pix); pix.dispose();
            }
        }

        if (heartIcon == null) {
            try {
                heartIcon = new Texture(Gdx.files.internal("images/heart.png"));
            } catch (Exception e) {
                Pixmap pix = new Pixmap(40, 40, Pixmap.Format.RGBA8888);
                pix.setColor(Color.FIREBRICK); pix.fill();
                heartIcon = new Texture(pix); pix.dispose();
            }
        }
        // ------------------------------------

        illustration = new Image(illustrationTex);
        illustration.setSize(getWidth(), getHeight());
        illustration.setColor(0.3f, 0.3f, 0.3f, 1f);
        this.addActor(illustration);

        try {
            Texture frameTex = new Texture(Gdx.files.internal("images/card_frame.png"));
            frame = new Image(frameTex);
            frame.setSize(getWidth(), getHeight());
            this.addActor(frame);
        } catch (Exception e) {
            Gdx.app.log("CardActor", "Frame belum ada.");
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/cyber.otf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = 10;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.2f;
        parameter.borderColor = Color.BLACK;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = new Color(0, 1, 1, 0.5f);

        this.font = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isFaceUp) {
            illustration.setColor(0.1f, 0.1f, 0.1f, 1f);
            if (frame != null) frame.setColor(0.4f, 0.4f, 0.4f, 1f);
        } else {
            illustration.setColor(0.3f, 0.3f, 0.3f, 1f);
            if (frame != null) frame.setColor(1f, 1f, 1f, 1f);
        }

        super.draw(batch, parentAlpha);
        if (!isFaceUp) return;

        batch.flush();
        Matrix4 oldMatrix = batch.getTransformMatrix().cpy();

        Matrix4 newMatrix = new Matrix4(oldMatrix);
        newMatrix.translate(getX() + getOriginX(), getY() + getOriginY(), 0);
        newMatrix.scale(getScaleX(), getScaleY(), 1.0f);
        newMatrix.rotate(0, 0, 1, getRotation());
        newMatrix.translate(-getOriginX(), -getOriginY(), 0);
        batch.setTransformMatrix(newMatrix);

        float w = getWidth();
        float h = getHeight();

        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;

        try {
            // A. RAM COST
            font.getData().setScale(1.0f);
            font.setColor(Color.CYAN);
            font.draw(batch, String.valueOf(data.ramCost), 18, h - 15);

            // B. TIPE KARTU
            String cardType = "UNKNOWN";
            if (data instanceof ScriptData) cardType = "SCRIPT";
            else if (data instanceof ProgramData) cardType = "PROGRAM";
            else cardType = "MALWARE";

            font.setColor(Color.LIGHT_GRAY);
            font.getData().setScale(0.85f);
            font.draw(batch, cardType, 0, h - 15, w, Align.center, false);

            // C. NAMA KARTU
            font.setColor(Color.YELLOW);
            font.getData().setScale(1.0f);
            font.draw(batch, data.name.toUpperCase(), 0, 155, w, Align.center, false);

            // D. DESKRIPSI
            font.setColor(Color.WHITE);
            font.getData().setScale(0.8f);
            String desc = data.description;
            if (desc != null && !desc.isEmpty()) {
                font.draw(batch, desc, 25, 125, w - 50, Align.center, true);
            }

            // E. VISUAL TOMBOL EXECUTE
            boolean hasExecute = desc != null && desc.contains("(EXECUTE)");

            if (isOnBoard && hasExecute && !isFlooped) {
                float btnW = 100; float btnH = 26;
                float btnX = (w - btnW) / 2f; float btnY = 65;

                batch.setColor(Color.WHITE);
                batch.draw(executeBtnTex, btnX, btnY, btnW, btnH);

                font.getData().setScale(0.8f);
                font.setColor(Color.BLACK);
                font.draw(batch, "EXECUTE", 0, btnY + 20, w, Align.center, false);
            }

            // F. STATISTIK ATK & HP (SAAT KARTU DI TANGAN)
            int atk = -1, hp = -1;
            boolean hasStats = false;

            if (data instanceof ProgramData) {
                atk = ((ProgramData) data).atk;
                hp = ((ProgramData) data).hp;
                hasStats = true;
            } else if (data.getClass().getSimpleName().equals("MalwareData")) {
                try {
                    atk = (int) data.getClass().getField("atk").get(data);
                    hp = (int) data.getClass().getField("hp").get(data);
                    hasStats = true;
                } catch (Exception e) {}
            }

            if (hasStats) {
                // Di tangan (Statistik Kecil di bawah ilustrasi)
                font.getData().setScale(1.1f);
                font.setColor(Color.ORANGE);
                font.draw(batch, String.valueOf(atk), 25, 35);
                font.setColor(Color.LIME);
                font.draw(batch, String.valueOf(hp), w - 55, 35);

                // G. STATISTIK FLOATING DI LUAR KARTU (Saat di Arena)
                if (isOnBoard) {
                    font.getData().setScale(4.0f);

                    // -- BAGIAN KIRI: [Ikon Pedang] [Angka ATK] --
                    batch.setColor(Color.WHITE);
                    // Ikon diperbesar jadi 60x60, posisi Y sejajar dengan teks
                    batch.draw(swordIcon, -95, h - 60, 60, 60);

                    font.setColor(Color.WHITE);
                    font.draw(batch, String.valueOf(atk), -30, h - 15);

                    // -- BAGIAN KANAN: [Angka HP] [Ikon Hati] --
                    font.setColor(Color.RED);
                    font.draw(batch, String.valueOf(hp), w + 15, h - 15);

                    batch.setColor(Color.WHITE);
                    // Cek jika HP puluhan, geser ikon hati agak ke kanan agar tidak tabrakan dengan angka
                    float heartOffsetX = (hp > 9) ? w + 85 : w + 60;
                    batch.draw(heartIcon, heartOffsetX, h - 60, 60, 60);
                }
            }

        } finally {
            font.getData().setScale(oldScaleX, oldScaleY);
        }

        batch.flush();
        batch.setTransformMatrix(oldMatrix);
    }

    public BaseCard getData() {
        return data;
    }
}
