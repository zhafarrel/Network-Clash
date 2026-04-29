package com.NCFrontend.logic;

import com.NCFrontend.models.*;
import com.NCFrontend.ui.CardActor;
import com.NCFrontend.logic.abilities.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.*;

import java.util.ArrayList; // JANGAN LUPA IMPORT INI

public class CardFactory {
    private static Gson gson = new Gson();

    public static CardActor createVisualCard(String cardJsonText) {
        JsonObject jsonObject = gson.fromJson(cardJsonText, JsonObject.class);
        String type = jsonObject.get("type").getAsString();

        BaseCard data;
        if (type.equalsIgnoreCase("SCRIPT")) {
            data = gson.fromJson(cardJsonText, ScriptData.class);
        } else {
            data = gson.fromJson(cardJsonText, ProgramData.class);
        }

        Texture texture;
        try {
            texture = new Texture(Gdx.files.internal("images/" + data.id + ".png"));
        } catch (Exception e) {
            Gdx.app.error("CardFactory", "Image not found for: " + data.id);
            texture = new Texture(Gdx.files.internal("libgdx.png"));
        }

        CardActor card = new CardActor(data, texture);

        // --- SOLUSI CRASH: Inisialisasi List jika masih null ---
        if (data.abilities == null) {
            data.abilities = new ArrayList<>();
        }
        // -------------------------------------------------------

        if (data.name != null) {
            String cardName = data.name.toUpperCase();

            if (cardName.contains("FIREWALL GOLEM")) {
                data.abilities.add(new VanguardAbility());
            }
            else if (cardName.contains("ROOTKIT WORM")) {
                data.abilities.add(new DeepInfectionAbility());
            }
            else if (cardName.contains("ANTI-VIRUS PALADIN")) {
                data.abilities.add(new SystemRestoreAbility());
            }
            else if (cardName.contains("TROJAN KNIGHT")) {
                data.abilities.add(new BackdoorBreachAbility());
            }
            else if (cardName.contains("DATA MINER")) {
                data.abilities.add(new OverclockAbility());
            }
            else if (cardName.contains("SPYWARE WASP")) {
                data.abilities.add(new InfiltrateAbility());
            }
            else if (cardName.contains("BOOT SECTOR VIRUS")) {
                data.abilities.add(new CorruptBootAbility());
            }
            else if (cardName.contains("DDOS SWARM")) {
                data.abilities.add(new FloodNetworkAbility());
            }
            else if (cardName.contains("C2 SERVER")) {
                data.abilities.add(new ExecuteOrderAbility());
            }
            else if (cardName.contains("PACKET SNIFFER HOUND")) {
                data.abilities.add(new DataInterceptAbility());
            }
            else if (cardName.contains("PRIVILEGE ESCALATOR")) {
                data.abilities.add(new SudoAccessAbility());
            }
            else if (cardName.contains("CROSS-SITE SCRIPTER")) {
                data.abilities.add(new SessionHijackAbility());
            }
            else if (cardName.contains("MUTEX LOCK")) {
                data.abilities.add(new ThreadSyncAbility());
            }
            else if (cardName.contains("LOAD BALANCER")) {
                data.abilities.add(new RerouteTrafficAbility());
            }
            else if (cardName.contains("SYSTEM PATCH")) {
                data.abilities.add(new SystemPatchAbility());
            }
            else if (cardName.contains("PING REQUEST")) {
                data.abilities.add(new PingRequestAbility());
            }
            else if (cardName.contains("CLEAR CACHE")) {
                data.abilities.add(new ClearCacheAbility());
            }
            else if (cardName.contains("ZERO-DAY EXPLOIT")) {
                data.abilities.add(new ZeroDayExploitAbility());
            }
            else if (cardName.contains("PHISHING CAMPAIGN")) {
                data.abilities.add(new PhishingCampaignAbility());
            }
            else if (cardName.contains("MAN-IN-THE-MIDDLE")) {
                data.abilities.add(new ManInTheMiddleAbility());
            }
            else if (cardName.contains("KERNEL GUARDIAN")) {
                data.abilities.add(new RingZeroPrivilegeAbility());
            }
            else if (cardName.contains("FORK BOMB")) {
                data.abilities.add(new ForkBombAbility());
            }
            else if (cardName.contains("DATA EXFILTRATOR")) {
                data.abilities.add(new DataExfiltratorAbility());
            }
            else if (cardName.contains("RANSOMWARE BEAST")) {
                data.abilities.add(new ThreadSyncAbility());
            }
            else if (cardName.contains("BACKUP SERVER")) {
                data.abilities.add(new BackupServerAbility());
            }
            else if (cardName.contains("INTERRUPT HANDLER")) {
                data.abilities.add(new PreemptionAbility());
            }
            else if (cardName.contains("HONEYPOT DECOY")) {
                data.abilities.add(new EntrapmentAbility());
            }
            else if (cardName.contains("PORT SCANNER")) {
                data.abilities.add(new VulnerabilityCheckAbility());
            }
            else if (cardName.contains("POLYMORPHIC VIRUS")) {
                data.abilities.add(new MutatingCodeAbility());
            }
            else if (cardName.contains("ADVANCED PERSISTENT THREAT") || cardName.contains("APT")) {
                data.abilities.add(new PersistenceAbility());
            }
            else if (cardName.contains("BOTNET NODE")) {
                data.abilities.add(new DistributedPowerAbility());
            }
            else if (cardName.contains("ZERO-DAY SCOUT")) {
                data.abilities.add(new ScanVulnerabilityAbility());
            }
            else if (cardName.contains("TRACEROUTE SENTINEL")) {
                data.abilities.add(new PingSweepAbility());
            }
            else if (cardName.contains("INTRUSION DETECTION SYSTEM") || cardName.contains("IDS")) {
                data.abilities.add(new SignatureDetectionAbility());
            }
            else if (cardName.contains("THREAT HUNTER")) {
                data.abilities.add(new ActiveDefenseAbility());
            }
            else if (cardName.contains("EMERGENCY PATCH UNIT")) {
                data.abilities.add(new RemoteHotfixAbility());
            }
            else if (cardName.contains("SYSTEM ADMIN AVATAR")) {
                data.abilities.add(new AdminCommandAbility());
            }
            else if (cardName.contains("SQLI PAYLOAD")) {
                data.abilities.add(new DataBreachAbility());
            }
            else if (cardName.contains("MOBILE PROXY")) {
                data.abilities.add(new RedirectionAbility());
            }
            else if (cardName.contains("REVERSE PROXY SHIELD")) {
                data.abilities.add(new TrafficFilterAbility());
            }
            else if (cardName.contains("DEEP PACKET INSPECTION")) {
                data.abilities.add(new DeepPacketInspectionAbility());
            }
            else if (cardName.contains("WEB APP FIREWALL")) {
                data.abilities.add(new HttpFilterAbility());
            }
            else if (cardName.contains("SANDBOX ENVIRONMENT")) {
                data.abilities.add(new IsolationAbility());
            }
            else if (cardName.contains("LOGIC BOMB")) {
                data.abilities.add(new CountdownAbility());
            }
            else if (cardName.contains("PING OF DEATH")) {
                data.abilities.add(new OversizedPacketAbility());
            }
        }

        return card;
    }
}
