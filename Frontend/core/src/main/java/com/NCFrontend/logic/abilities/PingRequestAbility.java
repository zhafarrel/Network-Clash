package com.NCFrontend.logic.abilities;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.logic.CombatResolver;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class PingRequestAbility implements CardAbility {
    @Override
    public void onPlayScript(CardActor owner, String targetLane, GameplayScreen screen) {
        // Karena ini ofensif, targetnya adalah papan musuh
        if (screen.enemyActiveCards.containsKey(targetLane)) {
            CardActor target = screen.enemyActiveCards.get(targetLane);

            // Kurangi HP musuh sebanyak 2
            int currentHp = CombatResolver.getHp(target.getData());
            CombatResolver.setHp(target.getData(), currentHp - 2);

            Gdx.app.log("Script", "PING REQUEST! Menembak " + target.getData().name + " sebesar 2 Damage.");

            // Efek bergetar karena terkena peluru Script
            target.addAction(Actions.sequence(
                Actions.color(Color.RED, 0.1f),
                Actions.moveBy(10, 0, 0.05f), Actions.moveBy(-20, 0, 0.05f), Actions.moveBy(10, 0, 0.05f),
                Actions.color(Color.WHITE, 0.1f)
            ));

            // Jika mati karena Script, musuh akan dihapus (kita panggil onDeath-nya juga)
            if (CombatResolver.getHp(target.getData()) <= 0) {
                if (target.getData().abilities != null) {
                    for (CardAbility ability : target.getData().abilities) {
                        ability.onDeath(target, screen);
                    }
                }
                target.addAction(Actions.sequence(
                    Actions.parallel(Actions.scaleTo(0, 0, 0.3f), Actions.fadeOut(0.3f)),
                    Actions.removeActor()
                ));
                screen.enemyActiveCards.remove(targetLane);
            }

        } else {
            Gdx.app.log("Script", "PING REQUEST Gagal: Tidak ada target musuh di " + targetLane);
        }
    }
}
