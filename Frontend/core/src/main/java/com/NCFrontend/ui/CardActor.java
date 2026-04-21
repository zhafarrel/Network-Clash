package com.NCFrontend.ui;

import com.NCFrontend.models.BaseCard;
import com.NCFrontend.models.ProgramData;
import com.NCFrontend.models.ScriptData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

    // VARIABEL BARU: Mengecek apakah kartu di tangan (buka) atau di deck (tutup)
    public boolean isFaceUp = true;

    public CardActor(BaseCard data, Texture illustrationTex) {
        this.data = data;

        // 1. Ukuran kartu proporsional
        this.setSize(210, 300);

        // 2. Setup ILUSTRASI (Lapisan Bawah)
        illustration = new Image(illustrationTex);
        illustration.setSize(getWidth(), getHeight());

        // Memberi filter gelap agar teks neon menonjol
        illustration.setColor(0.3f, 0.3f, 0.3f, 1f);
        this.addActor(illustration);

        // 3. Setup FRAME (Lapisan Atas)
        try {
            Texture frameTex = new Texture(Gdx.files.internal("images/card_frame.png"));
            frame = new Image(frameTex);
            frame.setSize(getWidth(), getHeight());
            this.addActor(frame);
        } catch (Exception e) {
            Gdx.app.log("CardActor", "Frame belum ada (card_frame.png tidak ditemukan).");
        }

        // 4. GENERATE CYBERPUNK FONT (.OTF)
        // Pastikan file .otf ada di assets/fonts/
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/cyber.otf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = 10; // Ukuran dasar
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.2f; // Outline hitam agar teks terbaca di gambar terang
        parameter.borderColor = Color.BLACK;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = new Color(0, 1, 1, 0.5f); // Bayangan biru neon transparan

        this.font = generator.generateFont(parameter);
        generator.dispose(); // Wajib di-dispose untuk cegah memory leak
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // --- EFEK KARTU MENGHADAP BELAKANG ---
        if (!isFaceUp) {
            illustration.setColor(0.1f, 0.1f, 0.1f, 1f); // Dibuat sangat gelap
            if (frame != null) frame.setColor(0.4f, 0.4f, 0.4f, 1f);
        } else {
            illustration.setColor(0.3f, 0.3f, 0.3f, 1f); // Warna normal
            if (frame != null) frame.setColor(1f, 1f, 1f, 1f);
        }

        // 1. Gambar background texture (otomatis di-scale oleh LibGDX)
        super.draw(batch, parentAlpha);

        // JIKA KARTU MENGHADAP BELAKANG (DI DECK), JANGAN GAMBAR TEKS/STAT!
        if (!isFaceUp) return;

        // --- 2. MAGIC TRICK: MATRIX TRANSFORM ---
        batch.flush(); // Wajib dipanggil sebelum mengubah matrix
        Matrix4 oldMatrix = batch.getTransformMatrix().cpy();

        Matrix4 newMatrix = new Matrix4(oldMatrix);
        // Sesuaikan kanvas dengan posisi, origin, dan scale kartu saat ini
        newMatrix.translate(getX() + getOriginX(), getY() + getOriginY(), 0);
        newMatrix.scale(getScaleX(), getScaleY(), 1.0f);
        newMatrix.rotate(0, 0, 1, getRotation());
        newMatrix.translate(-getOriginX(), -getOriginY(), 0);

        batch.setTransformMatrix(newMatrix);
        // ----------------------------------------

        // SEKARANG KITA GAMBAR TEKS SEOLAH-OLAH KARTU UKURAN 1.0 (NORMAL)
        float w = getWidth();
        float h = getHeight();
        float originalScaleX = font.getData().scaleX;
        float originalScaleY = font.getData().scaleY;

        // A. RAM COST (Kiri Atas)
        font.setColor(Color.CYAN);
        font.draw(batch, String.valueOf(data.cost), 18, h - 15);

        // B. NAMA KARTU (Tengah Atas)
        font.setColor(Color.WHITE);
        font.draw(batch, data.name.toUpperCase(), 0, h - 15, w, Align.center, false);

        // C. DESKRIPSI (Tengah)
        font.setColor(Color.WHITE); // FIX: Pastikan warna putih, bukan cyan!
        font.getData().setScale(1f); // Skala standar agar teks rapi

        // Ambil deskripsi secara aman dari BaseCard (atau cast jika perlu)
        String desc = data.description;

        if (desc != null && !desc.isEmpty()) {
            // Koordinat statis yang pasti rapi di ukuran normal
            font.draw(batch, desc, 25, 115, w - 50, Align.center, true);
        }

        // D. STATISTIK ATK & HP
        font.getData().setScale(1.1f);

        // FIX: Cek baik ProgramData maupun MalwareData
        int atk = -1, hp = -1;
        boolean hasStats = false;

        if (data instanceof ProgramData) {
            atk = ((ProgramData) data).atk;
            hp = ((ProgramData) data).hp;
            hasStats = true;
        } else if (data.getClass().getSimpleName().equals("MalwareData")) {
            // Menggunakan reflection/cast alternatif untuk menangkap class Malware-mu
            try {
                atk = (int) data.getClass().getField("atk").get(data);
                hp = (int) data.getClass().getField("hp").get(data);
                hasStats = true;
            } catch (Exception e) {}
        }

        // Gambar stat jika kartu tersebut punya ATK dan HP
        if (hasStats) {
            font.setColor(Color.ORANGE);
            font.draw(batch, String.valueOf(atk), 25, 35);

            font.setColor(Color.LIME);
            font.draw(batch, String.valueOf(hp), w - 55, 35);
        }

        // --- 3. KEMBALIKAN KE KONDISI SEMULA ---
        font.getData().setScale(originalScaleX, originalScaleY);
        batch.flush();
        batch.setTransformMatrix(oldMatrix); // Reset kanvas untuk aktor lain
    }
    public BaseCard getData() {
        return data;
    }
}
