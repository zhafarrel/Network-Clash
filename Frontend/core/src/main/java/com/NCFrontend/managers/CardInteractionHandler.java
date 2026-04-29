package com.NCFrontend.managers;

import com.NCFrontend.logic.CardAbility;
import com.NCFrontend.screens.GameplayScreen;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.models.BaseCard;
import com.NCFrontend.models.ScriptData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class CardInteractionHandler {
    private GameplayScreen screen;
    private DragAndDrop dragAndDrop;

    public CardInteractionHandler(GameplayScreen screen) {
        this.screen = screen;
        this.dragAndDrop = new DragAndDrop();
        this.dragAndDrop.setTapSquareSize(10);
    }

    public void setupZonesAndButtons() {
        createDropZone("Localhost", 300, 280, 175, 230);
        createDropZone("Cloud Storage", 700, 280, 175, 230);
        createDropZone("DMZ", 1100, 280, 175, 230);
        createDropZone("Dark Node", 1500, 280, 175, 230);
    }

    public void registerCard(CardActor visualCard) {
        setupHoverEffect(visualCard);

        dragAndDrop.addSource(new DragAndDrop.Source(visualCard) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (screen.phaseManager.currentPhase != GamePhaseManager.GamePhase.PLAYER_MAIN || !screen.hand.contains(visualCard, true)) return null;

                if (screen.playerProfile.currentRam < visualCard.getData().ramCost) {
                    visualCard.addAction(Actions.sequence(
                        Actions.moveBy(15, 0, 0.05f),
                        Actions.moveBy(-30, 0, 0.05f),
                        Actions.moveBy(15, 0, 0.05f)
                    ));
                    return null;
                }

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(visualCard);
                payload.setDragActor(visualCard);
                visualCard.setScale(1.0f);
                visualCard.toFront();
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target == null) {
                    Vector2 origin = (Vector2) visualCard.getUserObject();
                    visualCard.clearActions();
                    visualCard.addAction(Actions.parallel(
                        Actions.scaleTo(1.0f, 1.0f, 0.2f),
                        Actions.moveTo(origin.x, origin.y, 0.2f, Interpolation.pow3Out),
                        Actions.rotateTo(0, 0.2f)
                    ));
                    screen.refreshHandZIndex();
                }
            }
        });
    }

    public void setupEnemyInspect(CardActor card) {
        card.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (card.isFaceUp) {
                    screen.uiManager.showCardDetail(card.getData());
                    return true;
                }
                return false;
            }
        });
    }

    private void setupHoverEffect(CardActor card) {
        card.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!screen.uiManager.isDialogOpen && pointer == -1 && screen.hand.contains(card, true) && screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.PLAYER_MAIN) {
                    card.toFront();
                    card.clearActions();
                    Vector2 origin = (Vector2) card.getUserObject();
                    card.addAction(Actions.parallel(
                        Actions.scaleTo(1.25f, 1.25f, 0.15f, Interpolation.smooth),
                        Actions.moveTo(origin.x, origin.y + 40, 0.15f, Interpolation.smooth)
                    ));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!screen.uiManager.isDialogOpen && pointer == -1 && screen.hand.contains(card, true)) {
                    card.clearActions();
                    Vector2 origin = (Vector2) card.getUserObject();
                    card.addAction(Actions.parallel(
                        Actions.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.smooth),
                        Actions.moveTo(origin.x, origin.y, 0.15f, Interpolation.smooth)
                    ));
                    screen.refreshHandZIndex();
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == com.badlogic.gdx.Input.Buttons.RIGHT) {
                    screen.uiManager.showCardDetail(card.getData());
                    return true;
                }

                if (button == com.badlogic.gdx.Input.Buttons.LEFT) {
                    if (screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.PLAYER_MAIN
                        && !screen.hand.contains(card, true)
                        && !card.isFlooped) {

                        boolean hasExecute = card.getData().description != null && card.getData().description.contains("(EXECUTE)");

                        if (hasExecute && y < 150) {
                            // --- MEMICU SKILL EXECUTE (FLOOP) ---
                            card.isFlooped = true;
                            card.addAction(Actions.rotateTo(-90, 0.4f, Interpolation.smooth));

                            // Eksekusi semua skill tipe Floop di kartu ini
                            if (card.getData().abilities != null) {
                                for (com.NCFrontend.logic.CardAbility ability : card.getData().abilities) {
                                    ability.onFloop(card, screen);
                                }
                            }
                            return true;
                        }
                        else if (y >= 150) {
                            screen.uiManager.showCardDetail(card.getData());
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void createDropZone(String zoneName, float x, float y, float width, float height) {
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 1, 0, 0.3f)); pixmap.fill();
        Image dropZone = new Image(new Texture(pixmap)); pixmap.dispose();
        dropZone.setPosition(x, y);
        screen.stage.addActor(dropZone);

        dragAndDrop.addTarget(new DragAndDrop.Target(dropZone) {

            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                CardActor card = (CardActor) payload.getObject();
                BaseCard cardData = card.getData();

                if (cardData instanceof ScriptData) {
                    getActor().setColor(new Color(0, 1, 1, 0.6f));
                    return true;
                }

                String cardLane = cardData.validLane;
                if (cardLane == null || cardLane.equalsIgnoreCase("ANY_LANE") || zoneName.equalsIgnoreCase(cardLane)) {
                    getActor().setColor(new Color(0, 1, 0, 0.6f));
                    return true;
                }

                getActor().setColor(new Color(1, 0, 0, 0.6f));
                return false;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(new Color(0, 1, 0, 0.3f));
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                CardActor newCard = (CardActor) payload.getObject();
                BaseCard cardData = newCard.getData();

                // ==========================================
                // JIKA KARTU YANG DI-DROP ADALAH SCRIPT
                // ==========================================
                if (cardData instanceof ScriptData) {
                    screen.phaseManager.useRam(cardData.ramCost);
                    screen.hand.removeValue(newCard, true);
                    screen.updateHandPositions();

                    // 1. Eksekusi semua kemampuan (spell) di dalam kartu ini!
                    if (cardData.abilities != null) {
                        for (com.NCFrontend.logic.CardAbility ability : cardData.abilities) {
                            // zoneName adalah lane tempat pemain melepaskan (drop) kartunya
                            ability.onPlayScript(newCard, zoneName, screen);
                        }
                    }

                    // 2. Hancurkan kartu secara visual karena sudah selesai dipakai
                    newCard.addAction(Actions.sequence(
                        Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)),
                        Actions.removeActor()
                    ));
                    return;
                }
                // ==========================================

                CardActor existingCard = screen.activeCards.get(zoneName);

                if (existingCard != null) {
                    screen.uiManager.showReplaceDialog(existingCard, newCard, zoneName, getActor());
                } else {
                    screen.phaseManager.useRam(cardData.ramCost);
                    screen.hand.removeValue(newCard, true);
                    screen.updateHandPositions();

                    newCard.isOnBoard = true;
                    screen.placeCardInSlot(newCard, zoneName, getActor());

                    for (CardActor ally : screen.activeCards.values()) {
                        if (ally.getData().abilities != null) {
                            for (CardAbility ability : ally.getData().abilities) ability.onCardDeployed(ally, newCard, zoneName, screen);
                        }
                    }
                    for (CardActor enemy : screen.enemyActiveCards.values()) {
                        if (enemy.getData().abilities != null) {
                            for (CardAbility ability : enemy.getData().abilities) ability.onCardDeployed(enemy, newCard, zoneName, screen);
                        }
                    }
                }
            }
        });
    }
}
