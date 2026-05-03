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
        createDropZone("Slot 0", 300, 280, 175, 230);
        createDropZone("Slot 1", 700, 280, 175, 230);
        createDropZone("Slot 2", 1100, 280, 175, 230);
        createDropZone("Slot 3", 1500, 280, 175, 230);
    }

    public void registerCard(CardActor visualCard) {
        setupHoverEffect(visualCard);

        dragAndDrop.addSource(new DragAndDrop.Source(visualCard) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                // 1. CEK FASE YANG DIIZINKAN (Bisa Main atau Setup)
                boolean isPlayerMain = screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.PLAYER_MAIN;
                boolean isLaneSetup = screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.LANE_SETUP;

                // Jika bukan kedua fase itu, atau kartu tidak ada di tangan, batalkan drag!
                if (!(isPlayerMain || isLaneSetup) || !screen.hand.contains(visualCard, true)) return null;

                // 2. CEK RAM (Hanya Berlaku saat PLAYER_MAIN)
                if (isPlayerMain && screen.playerProfile.currentRam < visualCard.getData().ramCost) {
                    visualCard.addAction(Actions.sequence(
                        Actions.moveBy(15, 0, 0.05f),
                        Actions.moveBy(-30, 0, 0.05f),
                        Actions.moveBy(15, 0, 0.05f)
                    ));
                    return null;
                }

                // 3. MULAI DRAG
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

    private int getSlotIndexFromName(String zoneName) {
        if (zoneName.equals("Slot 0")) return 0;
        if (zoneName.equals("Slot 1")) return 1;
        if (zoneName.equals("Slot 2")) return 2;
        if (zoneName.equals("Slot 3")) return 3;
        return -1;
    }

    private void setupHoverEffect(CardActor card) {
        card.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // CEK FASE YANG DIIZINKAN UNTUK HOVER
                boolean validPhase = screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.PLAYER_MAIN ||
                    screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.LANE_SETUP;

                if (!screen.uiManager.isDialogOpen && pointer == -1 && screen.hand.contains(card, true) && validPhase) {
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
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap((int) width, (int) height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 1, 0, 0.3f)); pixmap.fill();
        Image dropZone = new Image(new Texture(pixmap)); pixmap.dispose();
        dropZone.setPosition(x, y);
        screen.stage.addActor(dropZone);

        dragAndDrop.addTarget(new DragAndDrop.Target(dropZone) {

            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                CardActor card = (CardActor) payload.getObject();
                com.NCFrontend.models.BaseCard cardData = card.getData();

                // Izinkan drag hijau semua saat Setup
                if (screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.LANE_SETUP) {
                    getActor().setColor(new Color(0, 1, 0, 0.6f));
                    return true;
                }

                if (cardData instanceof com.NCFrontend.models.ScriptData) {
                    getActor().setColor(new Color(0, 1, 1, 0.6f));
                    return true;
                }

                // Terjemahkan nama Slot menjadi nama Jalur Asli
                int slotIdx = getSlotIndexFromName(zoneName);
                String actualLaneName = (slotIdx != -1 && screen.boardLanes[slotIdx] != null) ? screen.boardLanes[slotIdx] : zoneName;

                String cardLane = null;
                try {
                    cardLane = (String) cardData.getClass().getField("validLane").get(cardData);
                } catch (Exception e) {}

                if (cardLane == null || cardLane.equalsIgnoreCase("ANY_LANE") || actualLaneName.equalsIgnoreCase(cardLane)) {
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
                com.NCFrontend.models.BaseCard cardData = newCard.getData();

                // ==========================================
                // FASE 1: PENYUSUNAN JALUR
                // ==========================================
                if (screen.phaseManager.currentPhase == GamePhaseManager.GamePhase.LANE_SETUP) {
                    if (cardData instanceof com.NCFrontend.models.ProgramData) {
                        com.NCFrontend.models.ProgramData pData = (com.NCFrontend.models.ProgramData) cardData;

                        if (pData.faction != null && pData.faction.equals("LANE")) {
                            int slotIndex = getSlotIndexFromName(zoneName);

                            if (slotIndex != -1 && screen.boardLanes[slotIndex] == null) {
                                // 1. Kunci posisi jalur di array global
                                screen.boardLanes[slotIndex] = cardData.name;
                                screen.placedLanesCount++;

                                // 2. Buang dari tangan agar posisi kartu lain merapat
                                screen.hand.removeValue(newCard, true);
                                screen.updateHandPositions();
                                dragAndDrop.removeSource(source); // Matikan drag kartu ini

                                // 3. EFEK KARTU MENGHILANG (Seperti Script)
                                newCard.clearActions();
                                newCard.addAction(Actions.sequence(
                                    Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)),
                                    Actions.removeActor()
                                ));

                                // 4. MUNCULKAN TEKS NAMA JALUR DI DROPZONE
                                com.badlogic.gdx.graphics.g2d.BitmapFont font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
                                font.getData().setScale(1.5f); // Perbesar font sedikit
                                com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle style = new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(font, Color.WHITE);
                                com.badlogic.gdx.scenes.scene2d.ui.Label laneLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label(cardData.name.toUpperCase(), style);

                                // Kalkulasi posisi agar teks berada tepat di tengah kotak DropZone
                                laneLabel.setPosition(
                                    getActor().getX() + (getActor().getWidth() - laneLabel.getWidth()) / 2f,
                                    getActor().getY() + (getActor().getHeight() - laneLabel.getHeight()) / 2f
                                );

                                // Efek teks muncul perlahan (Fade-in)
                                laneLabel.getColor().a = 0f;
                                laneLabel.addAction(Actions.fadeIn(0.5f));
                                screen.stage.addActor(laneLabel);

                                // 5. Jika selesai 4 jalur, MULAI GAME dengan sedikit JEDA ANIMASI
                                if (screen.placedLanesCount == 4) {
                                    com.badlogic.gdx.Gdx.app.log("Game", "Semua jalur telah disetup! Memulai permainan...");

                                    screen.stage.addAction(Actions.sequence(
                                        Actions.delay(0.6f), // Tunggu animasi selesai
                                        Actions.run(new Runnable() {
                                            @Override
                                            public void run() {
                                                screen.phaseManager.startPlayerTurn();
                                            }
                                        })
                                    ));
                                }
                            }
                        }
                    }
                    return;
                }
                // ==========================================

                // ==========================================
                // FASE 2: PERMAINAN ASLI (PROGRAM/SCRIPT)
                // ==========================================

                // Terjemahkan nama Slot menjadi nama Jalur Asli!
                int slotIdx = getSlotIndexFromName(zoneName);
                String actualLaneName = (slotIdx != -1 && screen.boardLanes[slotIdx] != null) ? screen.boardLanes[slotIdx] : zoneName;

                if (cardData instanceof com.NCFrontend.models.ScriptData) {
                    screen.phaseManager.useRam(cardData.ramCost);
                    screen.hand.removeValue(newCard, true);
                    screen.updateHandPositions();

                    if (cardData.abilities != null) {
                        for (com.NCFrontend.logic.CardAbility ability : cardData.abilities) ability.onPlayScript(newCard, actualLaneName, screen);
                    }

                    newCard.addAction(Actions.sequence(
                        Actions.parallel(Actions.scaleTo(0.1f, 0.1f, 0.3f), Actions.fadeOut(0.3f)),
                        Actions.removeActor()
                    ));
                    return;
                }

                CardActor existingCard = screen.activeCards.get(actualLaneName);

                if (existingCard != null) {
                    screen.uiManager.showReplaceDialog(existingCard, newCard, actualLaneName, getActor());
                } else {
                    screen.phaseManager.useRam(cardData.ramCost);
                    screen.hand.removeValue(newCard, true);
                    screen.updateHandPositions();

                    newCard.isOnBoard = true;
                    // Simpan di koordinat DropZone, TAPI rekam dengan nama jalur asli
                    screen.placeCardInSlot(newCard, actualLaneName, getActor());

                    for (CardActor ally : screen.activeCards.values()) {
                        if (ally.getData().abilities != null) {
                            for (com.NCFrontend.logic.CardAbility ability : ally.getData().abilities) ability.onCardDeployed(ally, newCard, actualLaneName, screen);
                        }
                    }
                }
            }
        });
    }
}
