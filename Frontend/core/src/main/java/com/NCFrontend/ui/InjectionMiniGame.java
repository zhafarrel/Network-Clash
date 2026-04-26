package com.NCFrontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InjectionMiniGame extends Group {
    private Image bgBar, hitZone, critZone, cursorImage, cursorLine;
    private Group cursorGroup, trackGroup;
    private boolean isStopped = false;
    private Runnable onComplete;
    private int finalDamageMultiplier = 1;

    // TAMBAHAN PARAMETER: isPlayerAttacking
    public InjectionMiniGame(int atk, boolean hasEnemy, boolean isPlayerAttacking, Runnable onComplete) {
        this.onComplete = onComplete;

        this.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        trackGroup = new Group();
        float barWidth = 600f;
        float barHeight = 80f;
        trackGroup.setSize(barWidth, barHeight);
        trackGroup.setPosition((getWidth() - barWidth) / 2f, (getHeight() - barHeight) / 2f);
        addActor(trackGroup);

        Texture darkGrey = createColorTexture(Color.DARK_GRAY);
        Texture green = createColorTexture(Color.valueOf("90EE90"));
        Texture blue = createColorTexture(Color.valueOf("4169E1"));
        Texture white = createColorTexture(Color.WHITE);
        Texture black = createColorTexture(Color.BLACK);

        bgBar = new Image(darkGrey);
        bgBar.setSize(barWidth, barHeight);
        trackGroup.addActor(bgBar);

        // --- LOGIKA UKURAN ZONA DINAMIS ---
        // Jika Player yang menyerang: Zona Hijau LEBIH BESAR
        // Jika Musuh yang menyerang: Zona Hijau LEBIH KECIL (Lebih banyak Miss/Abu-abu)
        float hitWidth = isPlayerAttacking ? 220f : 120f;
        float critWidth = isPlayerAttacking ? 60f : 40f;

        hitZone = new Image(green);
        hitZone.setSize(hitWidth, barHeight);

        critZone = new Image(blue);
        critZone.setSize(critWidth, barHeight);

        if (hasEnemy) {
            float randomX = MathUtils.random(50f, barWidth - hitWidth - 50f);
            hitZone.setPosition(randomX, 0);
            critZone.setPosition(randomX + (hitWidth / 2f) - (critWidth / 2f), 0);
        } else {
            hitZone.setSize(barWidth, barHeight);
            hitZone.setPosition(0, 0);
            float randomX = MathUtils.random(50f, barWidth - critWidth - 50f);
            critZone.setPosition(randomX, 0);
        }

        trackGroup.addActor(hitZone);
        trackGroup.addActor(critZone);

        cursorGroup = new Group();

        cursorImage = new Image(black);
        cursorImage.setSize(40, barHeight + 20);
        cursorImage.setColor(1, 1, 1, 0.6f);

        cursorLine = new Image(white);
        cursorLine.setSize(4, barHeight + 40);
        cursorLine.setPosition(cursorImage.getWidth() / 2f - 2, -10);

        cursorGroup.addActor(cursorImage);
        cursorGroup.addActor(cursorLine);
        cursorGroup.setPosition(0, -10);

        trackGroup.addActor(cursorGroup);

        float duration = Math.max(0.2f, 1.2f - (atk * 0.1f));
        cursorGroup.addAction(Actions.forever(Actions.sequence(
            Actions.moveTo(barWidth - cursorImage.getWidth(), -10, duration, Interpolation.sine),
            Actions.moveTo(0, -10, duration, Interpolation.sine)
        )));

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isStopped) inject();
            }
        });
    }

    private void inject() {
        isStopped = true;
        cursorGroup.clearActions();

        float cursorCenter = cursorGroup.getX() + (cursorImage.getWidth() / 2f);

        if (cursorCenter >= critZone.getX() && cursorCenter <= critZone.getX() + critZone.getWidth()) {
            finalDamageMultiplier = 2; // Crit / Counter
        } else if (cursorCenter >= hitZone.getX() && cursorCenter <= hitZone.getX() + hitZone.getWidth()) {
            finalDamageMultiplier = 1; // Hit / Block
        } else {
            finalDamageMultiplier = 0; // Miss / Failed Block
        }

        this.addAction(Actions.sequence(
            Actions.delay(0.8f),
            Actions.fadeOut(0.2f),
            Actions.run(() -> {
                if (onComplete != null) onComplete.run();
                this.remove();
            })
        ));
    }

    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    public int getFinalDamageMultiplier() {
        return finalDamageMultiplier;
    }
}
