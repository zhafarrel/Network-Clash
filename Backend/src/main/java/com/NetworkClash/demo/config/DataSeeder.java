package com.NetworkClash.demo.config;

import com.NetworkClash.demo.entity.CardEntity;
import com.NetworkClash.demo.repository.CardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(CardRepository cardRepository) {
        return args -> {
            if (cardRepository.count() == 0) {
                System.out.println("Database kosong. Menyuntikkan data kartu awal...");

// ==========================================
// LANE: LOCALHOST
// ==========================================

// --- SYSADMIN (PROGRAMS) ---
                CardEntity golem = new CardEntity();
                golem.setId("prog_01");
                golem.setName("Firewall Golem");
                golem.setCost(3);
                golem.setDescription("Vanguard (PASSIVE): Musuh di lane kosong sebelahnya dipaksa menyerang kartu ini secara menyamping.");
                golem.setType("PROGRAM");
                golem.setFaction("SYSADMIN");
                golem.setAtk(0);
                golem.setHp(8);
                golem.setValidLane("Localhost");

                CardEntity paladin = new CardEntity();
                paladin.setId("prog_02");
                paladin.setName("Anti-Virus Paladin");
                paladin.setCost(3);
                paladin.setDescription("System Restore (PASSIVE): Pulihkan 1 HP Program kawan di sebelahnya tiap akhir giliran.");
                paladin.setType("PROGRAM");
                paladin.setFaction("SYSADMIN");
                paladin.setAtk(2);
                paladin.setHp(4);
                paladin.setValidLane("Localhost");

                CardEntity guardian = new CardEntity();
                guardian.setId("prog_03");
                guardian.setName("Kernel Guardian");
                guardian.setCost(4);
                guardian.setDescription("Ring 0 Privilege (PASSIVE): Kebal dari efek hancur instan kartu Script musuh.");
                guardian.setType("PROGRAM");
                guardian.setFaction("SYSADMIN");
                guardian.setAtk(3);
                guardian.setHp(5);
                guardian.setValidLane("Localhost");

                CardEntity mutex = new CardEntity();
                mutex.setId("prog_04");
                mutex.setName("Mutex Lock");
                mutex.setCost(2);
                mutex.setDescription("Thread Sync (EXECUTE): Kunci 1 Program musuh di depan agar tak bisa serang giliran ini.");
                mutex.setType("PROGRAM");
                mutex.setFaction("SYSADMIN");
                mutex.setAtk(0);
                mutex.setHp(6);
                mutex.setValidLane("Localhost");

// --- OMEGA (MALWARE) ---
                CardEntity worm = new CardEntity();
                worm.setId("malw_01");
                worm.setName("Rootkit Worm");
                worm.setCost(2);
                worm.setDescription("Deep Infection (PASSIVE): Serangan mengabaikan efek Vanguard musuh.");
                worm.setType("MALWARE");
                worm.setFaction("OMEGA");
                worm.setAtk(2);
                worm.setHp(2);
                worm.setValidLane("Localhost");

                CardEntity bootVirus = new CardEntity();
                bootVirus.setId("malw_02");
                bootVirus.setName("Boot Sector Virus");
                bootVirus.setCost(3);
                bootVirus.setDescription("Corrupt Boot (EXECUTE): Beri 1 Damage langsung ke HP Sysadmin (Bypass).");
                bootVirus.setType("MALWARE");
                bootVirus.setFaction("OMEGA");
                bootVirus.setAtk(3);
                bootVirus.setHp(3);
                bootVirus.setValidLane("Localhost");

                CardEntity logicBomb = new CardEntity();
                logicBomb.setId("malw_03");
                logicBomb.setName("Logic Bomb");
                logicBomb.setCost(2);
                logicBomb.setDescription("Countdown (PASSIVE): Hancur di akhir giliran ke-2 & beri 6 Damage ke depan.");
                logicBomb.setType("MALWARE");
                logicBomb.setFaction("OMEGA");
                logicBomb.setAtk(0);
                logicBomb.setHp(4);
                logicBomb.setValidLane("Localhost");

                CardEntity escalator = new CardEntity();
                escalator.setId("malw_04");
                escalator.setName("Privilege Escalator");
                escalator.setCost(3);
                escalator.setDescription("Sudo Access (PASSIVE): +1 ATK permanen tiap ada Program Sysadmin yang hancur.");
                escalator.setType("MALWARE");
                escalator.setFaction("OMEGA");
                escalator.setAtk(2);
                escalator.setHp(3);
                escalator.setValidLane("Localhost");

// ==========================================
// LANE: CLOUD STORAGE
// ==========================================

// --- SYSADMIN (PROGRAMS) ---
                CardEntity dataMiner = new CardEntity();
                dataMiner.setId("prog_05");
                dataMiner.setName("Data Miner");
                dataMiner.setCost(2);
                dataMiner.setDescription("Overclock (EXECUTE): Dapatkan +1 RAM ekstra giliran ini.");
                dataMiner.setType("PROGRAM");
                dataMiner.setFaction("SYSADMIN");
                dataMiner.setAtk(1);
                dataMiner.setHp(3);
                dataMiner.setValidLane("Cloud Storage");

                CardEntity loadBalancer = new CardEntity();
                loadBalancer.setId("prog_06");
                loadBalancer.setName("Load Balancer");
                loadBalancer.setCost(4);
                loadBalancer.setDescription("Reroute Traffic (EXECUTE): Tukar posisi dengan Program kawan di lane sebelah.");
                loadBalancer.setType("PROGRAM");
                loadBalancer.setFaction("SYSADMIN");
                loadBalancer.setAtk(2);
                loadBalancer.setHp(5);
                loadBalancer.setValidLane("Cloud Storage");

                CardEntity backupServer = new CardEntity();
                backupServer.setId("prog_07");
                backupServer.setName("Backup Server");
                backupServer.setCost(3);
                backupServer.setDescription("Redundancy (PASSIVE): Pulihkan 2 HP Sysadmin jika Program kawan di sebelahnya hancur.");
                backupServer.setType("PROGRAM");
                backupServer.setFaction("SYSADMIN");
                backupServer.setAtk(1);
                backupServer.setHp(4);
                backupServer.setValidLane("Cloud Storage");

                CardEntity interruptHandler = new CardEntity();
                interruptHandler.setId("prog_08");
                interruptHandler.setName("Interrupt Handler");
                interruptHandler.setCost(1);
                interruptHandler.setDescription("Preemption (EXECUTE): Hilangkan kemampuan EXECUTE 1 musuh untuk giliran depan.");
                interruptHandler.setType("PROGRAM");
                interruptHandler.setFaction("SYSADMIN");
                interruptHandler.setAtk(1);
                interruptHandler.setHp(2);
                interruptHandler.setValidLane("Cloud Storage");

// --- OMEGA (MALWARE) ---
                CardEntity wasp = new CardEntity();
                wasp.setId("malw_05");
                wasp.setName("Spyware Wasp");
                wasp.setCost(2);
                wasp.setDescription("Infiltrate (PASSIVE): Serangan Bypass (langsung ke HP Sysadmin).");
                wasp.setType("MALWARE");
                wasp.setFaction("OMEGA");
                wasp.setAtk(2);
                wasp.setHp(1);
                wasp.setValidLane("Cloud Storage");

                CardEntity cryptoJacker = new CardEntity();
                cryptoJacker.setId("malw_06");
                cryptoJacker.setName("Crypto-Jacker");
                cryptoJacker.setCost(3);
                cryptoJacker.setDescription("Resource Theft (PASSIVE): Awal giliran, curi 1 max RAM Sysadmin.");
                cryptoJacker.setType("MALWARE");
                cryptoJacker.setFaction("OMEGA");
                cryptoJacker.setAtk(1);
                cryptoJacker.setHp(4);
                cryptoJacker.setValidLane("Cloud Storage");

                CardEntity exfiltrator = new CardEntity();
                exfiltrator.setId("malw_07");
                exfiltrator.setName("Data Exfiltrator");
                exfiltrator.setCost(2);
                exfiltrator.setDescription("Bandwidth Theft (EXECUTE): Paksa musuh buang (discard) 1 kartu Script secara acak.");
                exfiltrator.setType("MALWARE");
                exfiltrator.setFaction("OMEGA");
                exfiltrator.setAtk(1);
                exfiltrator.setHp(3);
                exfiltrator.setValidLane("Cloud Storage");

                CardEntity shadowInstance = new CardEntity();
                shadowInstance.setId("malw_08");
                shadowInstance.setName("Shadow Instance");
                shadowInstance.setCost(3);
                shadowInstance.setDescription("Ghost Process (PASSIVE): Malware kawan di lane sebelah kebal target Script musuh.");
                shadowInstance.setType("MALWARE");
                shadowInstance.setFaction("OMEGA");
                shadowInstance.setAtk(2);
                shadowInstance.setHp(4);
                shadowInstance.setValidLane("Cloud Storage");

// ==========================================
// LANE: DMZ
// ==========================================

// --- SYSADMIN (PROGRAMS) ---
                CardEntity ids = new CardEntity();
                ids.setId("prog_09");
                ids.setName("Intrusion Detection System");
                ids.setCost(2);
                ids.setDescription("Signature Detection (PASSIVE): Beri 1 Damage tiap ada Malware baru ditaruh di lane ini.");
                ids.setType("PROGRAM");
                ids.setFaction("SYSADMIN");
                ids.setAtk(1);
                ids.setHp(4);
                ids.setValidLane("DMZ");

                CardEntity proxyShield = new CardEntity();
                proxyShield.setId("prog_10");
                proxyShield.setName("Reverse Proxy Shield");
                proxyShield.setCost(3);
                proxyShield.setDescription("Traffic Filter (PASSIVE): Kurangi damage yang diterima sebesar 1 poin.");
                proxyShield.setType("PROGRAM");
                proxyShield.setFaction("SYSADMIN");
                proxyShield.setAtk(1);
                proxyShield.setHp(5);
                proxyShield.setValidLane("DMZ");

                CardEntity waf = new CardEntity();
                waf.setId("prog_11");
                waf.setName("Web App Firewall");
                waf.setCost(3);
                waf.setDescription("HTTP Filter (PASSIVE): Malware musuh di lane ini biayanya +1 RAM.");
                waf.setType("PROGRAM");
                waf.setFaction("SYSADMIN");
                waf.setAtk(2);
                waf.setHp(4);
                waf.setValidLane("DMZ");

                CardEntity portScanner = new CardEntity();
                portScanner.setId("prog_12");
                portScanner.setName("Port Scanner");
                portScanner.setCost(3);
                portScanner.setDescription("Vulnerability Check (EXECUTE): Beri 1 Damage. Jika musuh mati, draw 1 kartu.");
                portScanner.setType("PROGRAM");
                portScanner.setFaction("SYSADMIN");
                portScanner.setAtk(3);
                portScanner.setHp(3);
                portScanner.setValidLane("DMZ");

// --- OMEGA (MALWARE) ---
                CardEntity ddos = new CardEntity();
                ddos.setId("malw_09");
                ddos.setName("DDoS Swarm");
                ddos.setCost(3);
                ddos.setDescription("Flood Network (EXECUTE): Beri 1 Damage ke SEMUA Program Sysadmin.");
                ddos.setType("MALWARE");
                ddos.setFaction("OMEGA");
                ddos.setAtk(3);
                ddos.setHp(2);
                ddos.setValidLane("DMZ");

                CardEntity sqli = new CardEntity();
                sqli.setId("malw_10");
                sqli.setName("SQLi Payload");
                sqli.setCost(3);
                sqli.setDescription("Data Breach (PASSIVE): +1 RAM giliran depan tiap kali menghancurkan musuh.");
                sqli.setType("MALWARE");
                sqli.setFaction("OMEGA");
                sqli.setAtk(2);
                sqli.setHp(3);
                sqli.setValidLane("DMZ");

                CardEntity xss = new CardEntity();
                xss.setId("malw_11");
                xss.setName("Cross-Site Scripter");
                xss.setCost(2);
                xss.setDescription("Session Hijack (EXECUTE): Paksa Program musuh di depan serang HP Sysadmin 1 Damage.");
                xss.setType("MALWARE");
                xss.setFaction("OMEGA");
                xss.setAtk(2);
                xss.setHp(2);
                xss.setValidLane("DMZ");

                CardEntity pingOfDeath = new CardEntity();
                pingOfDeath.setId("malw_12");
                pingOfDeath.setName("Ping of Death");
                pingOfDeath.setCost(4);
                pingOfDeath.setDescription("Oversized Packet (PASSIVE): Sisa damage setelah musuh mati tembus ke HP Sysadmin.");
                pingOfDeath.setType("MALWARE");
                pingOfDeath.setFaction("OMEGA");
                pingOfDeath.setAtk(4);
                pingOfDeath.setHp(2);
                pingOfDeath.setValidLane("DMZ");

// ==========================================
// LANE: DARK NODE
// ==========================================

// --- SYSADMIN (PROGRAMS) ---
                CardEntity honeypot = new CardEntity();
                honeypot.setId("prog_13");
                honeypot.setName("Honeypot Decoy");
                honeypot.setCost(2);
                honeypot.setDescription("Entrapment (PASSIVE): Stun musuh 2 giliran jika kartu ini dihancurkan.");
                honeypot.setType("PROGRAM");
                honeypot.setFaction("SYSADMIN");
                honeypot.setAtk(0);
                honeypot.setHp(4);
                honeypot.setValidLane("Dark Node");

                CardEntity traceroute = new CardEntity();
                traceroute.setId("prog_14");
                traceroute.setName("Traceroute Sentinel");
                traceroute.setCost(3);
                traceroute.setDescription("Ping Sweep (EXECUTE): Beri 2 Damage ke target Malware di lane mana saja.");
                traceroute.setType("PROGRAM");
                traceroute.setFaction("SYSADMIN");
                traceroute.setAtk(2);
                traceroute.setHp(3);
                traceroute.setValidLane("Dark Node");

                CardEntity threatHunter = new CardEntity();
                threatHunter.setId("prog_15");
                threatHunter.setName("Threat Hunter");
                threatHunter.setCost(4);
                threatHunter.setDescription("Active Defense (PASSIVE): Beri 1 Damage ke Malware baru di lane ini.");
                threatHunter.setType("PROGRAM");
                threatHunter.setFaction("SYSADMIN");
                threatHunter.setAtk(3);
                threatHunter.setHp(4);
                threatHunter.setValidLane("Dark Node");

                CardEntity sandbox = new CardEntity();
                sandbox.setId("prog_16");
                sandbox.setName("Sandbox Environment");
                sandbox.setCost(2);
                sandbox.setDescription("Isolation (PASSIVE): Malware yang menyerang kartu ini kehilangan efek Pasif.");
                sandbox.setType("PROGRAM");
                sandbox.setFaction("SYSADMIN");
                sandbox.setAtk(0);
                sandbox.setHp(5);
                sandbox.setValidLane("Dark Node");

// --- OMEGA (MALWARE) ---
                CardEntity ransomware = new CardEntity();
                ransomware.setId("malw_13");
                ransomware.setName("Ransomware Beast");
                ransomware.setCost(4);
                ransomware.setDescription("Encrypt (EXECUTE): Stun Program musuh di depan selama 1 giliran.");
                ransomware.setType("MALWARE");
                ransomware.setFaction("OMEGA");
                ransomware.setAtk(3);
                ransomware.setHp(5);
                ransomware.setValidLane("Dark Node");

                CardEntity trojan = new CardEntity();
                trojan.setId("malw_14");
                trojan.setName("Trojan Knight");
                trojan.setCost(3);
                trojan.setDescription("Backdoor Breach (PASSIVE): Saat hancur, munculkan \"Worm Token\" (1 ATK/1 HP) di sini.");
                trojan.setType("MALWARE");
                trojan.setFaction("OMEGA");
                trojan.setAtk(2);
                trojan.setHp(3);
                trojan.setValidLane("Dark Node");

                CardEntity c2server = new CardEntity();
                c2server.setId("malw_15");
                c2server.setName("C2 Server");
                c2server.setCost(4);
                c2server.setDescription("Execute Order (EXECUTE): +1 ATK untuk semua Malware kawan giliran ini.");
                c2server.setType("MALWARE");
                c2server.setFaction("OMEGA");
                c2server.setAtk(1);
                c2server.setHp(6);
                c2server.setValidLane("Dark Node");

                CardEntity polyVirus = new CardEntity();
                polyVirus.setId("malw_16");
                polyVirus.setName("Polymorphic Virus");
                polyVirus.setCost(3);
                polyVirus.setDescription("Mutating Code (PASSIVE): Pulihkan 1 HP di awal giliran (jika HP tidak penuh).");
                polyVirus.setType("MALWARE");
                polyVirus.setFaction("OMEGA");
                polyVirus.setAtk(2);
                polyVirus.setHp(3);
                polyVirus.setValidLane("Dark Node");

// ==========================================
// WILDCARD CARDS (ANY LANE)
// ==========================================

// --- SYSADMIN (PROGRAMS) ---
                CardEntity sniffer = new CardEntity();
                sniffer.setId("prog_17");
                sniffer.setName("Packet Sniffer Hound");
                sniffer.setCost(1);
                sniffer.setDescription("Data Intercept (EXECUTE): Intip kartu teratas deck musuh. Jika Script, +1 ATK permanen.");
                sniffer.setType("PROGRAM");
                sniffer.setFaction("SYSADMIN");
                sniffer.setAtk(1);
                sniffer.setHp(2);
                sniffer.setValidLane("ANY_LANE");

                CardEntity emergencyPatch = new CardEntity();
                emergencyPatch.setId("prog_18");
                emergencyPatch.setName("Emergency Patch Unit");
                emergencyPatch.setCost(2);
                emergencyPatch.setDescription("Remote Hotfix (PASSIVE): Saat ditaruh, pulihkan 2 HP 1 Program kawan (bebas lane).");
                emergencyPatch.setType("PROGRAM");
                emergencyPatch.setFaction("SYSADMIN");
                emergencyPatch.setAtk(1);
                emergencyPatch.setHp(3);
                emergencyPatch.setValidLane("ANY_LANE");

                CardEntity mobileProxy = new CardEntity();
                mobileProxy.setId("prog_19");
                mobileProxy.setName("Mobile Proxy");
                mobileProxy.setCost(2);
                mobileProxy.setDescription("Redirection (PASSIVE): Penyerang kartu ini kena debuff -1 ATK giliran depan.");
                mobileProxy.setType("PROGRAM");
                mobileProxy.setFaction("SYSADMIN");
                mobileProxy.setAtk(0);
                mobileProxy.setHp(5);
                mobileProxy.setValidLane("ANY_LANE");

                CardEntity sysadminAvatar = new CardEntity();
                sysadminAvatar.setId("prog_20");
                sysadminAvatar.setName("System Admin Avatar");
                sysadminAvatar.setCost(4);
                sysadminAvatar.setDescription("Admin Command (EXECUTE): Pilih 1 Program kawan: dapat +2 ATK & +1 HP giliran ini.");
                sysadminAvatar.setType("PROGRAM");
                sysadminAvatar.setFaction("SYSADMIN");
                sysadminAvatar.setAtk(3);
                sysadminAvatar.setHp(4);
                sysadminAvatar.setValidLane("ANY_LANE");

// --- OMEGA (MALWARE) ---
                CardEntity botnet = new CardEntity();
                botnet.setId("malw_17");
                botnet.setName("Botnet Node");
                botnet.setCost(1);
                botnet.setDescription("Distributed Power (PASSIVE): +1 ATK per Botnet Node kawan di papan.");
                botnet.setType("MALWARE");
                botnet.setFaction("OMEGA");
                botnet.setAtk(1);
                botnet.setHp(2);
                botnet.setValidLane("ANY_LANE");

                CardEntity adware = new CardEntity();
                adware.setId("malw_18");
                adware.setName("Adware Drone");
                adware.setCost(1);
                adware.setDescription("Pop-up (PASSIVE): Draw 1 kartu tambahan tiap kali serang HP Sysadmin.");
                adware.setType("MALWARE");
                adware.setFaction("OMEGA");
                adware.setAtk(1);
                adware.setHp(1);
                adware.setValidLane("ANY_LANE");

                CardEntity zeroDay = new CardEntity();
                zeroDay.setId("malw_19");
                zeroDay.setName("Zero-Day Scout");
                zeroDay.setCost(2);
                zeroDay.setDescription("Scan Vulnerability (EXECUTE): Intip 2 kartu atas deck musuh, buang 1 ke discard.");
                zeroDay.setType("MALWARE");
                zeroDay.setFaction("OMEGA");
                zeroDay.setAtk(2);
                zeroDay.setHp(2);
                zeroDay.setValidLane("ANY_LANE");

                CardEntity apt = new CardEntity();
                apt.setId("malw_20");
                apt.setName("Advanced Persistent Threat");
                apt.setCost(4);
                apt.setDescription("Persistence (PASSIVE): Saat hancur, kembali ke tangan tapi RAM Cost naik +1.");
                apt.setType("MALWARE");
                apt.setFaction("OMEGA");
                apt.setAtk(3);
                apt.setHp(3);
                apt.setValidLane("ANY_LANE");

// ==========================================
// SCRIPTS / SPELLS (ANY LANE, 0 ATK/HP)
// ==========================================

// --- SYSADMIN (SCRIPTS) ---
                CardEntity sysPatch = new CardEntity();
                sysPatch.setId("scpt_01");
                sysPatch.setName("System Patch");
                sysPatch.setCost(2);
                sysPatch.setDescription("Pilih 1 Program kawan di lane mana saja: Dapat +2 HP & +1 ATK permanen.");
                sysPatch.setType("SCRIPT");
                sysPatch.setFaction("SYSADMIN");
                sysPatch.setAtk(0);
                sysPatch.setHp(0);
                sysPatch.setValidLane("ANY_LANE");

                CardEntity pingReq = new CardEntity();
                pingReq.setId("scpt_02");
                pingReq.setName("Ping Request");
                pingReq.setCost(1);
                pingReq.setDescription("Beri 2 Damage langsung ke 1 Malware target.");
                pingReq.setType("SCRIPT");
                pingReq.setFaction("SYSADMIN");
                pingReq.setAtk(0);
                pingReq.setHp(0);
                pingReq.setValidLane("ANY_LANE");

                CardEntity clearCache = new CardEntity();
                clearCache.setId("scpt_03");
                clearCache.setName("Clear Cache");
                clearCache.setCost(2);
                clearCache.setDescription("Hapus semua efek negatif atau Stun dari semua Program kawan.");
                clearCache.setType("SCRIPT");
                clearCache.setFaction("SYSADMIN");
                clearCache.setAtk(0);
                clearCache.setHp(0);
                clearCache.setValidLane("ANY_LANE");

                CardEntity dpi = new CardEntity();
                dpi.setId("scpt_04");
                dpi.setName("Deep Packet Inspection");
                dpi.setCost(3);
                dpi.setDescription("Tukar tempo giliran ini untuk mengisi resource: Draw 2 kartu ekstra.");
                dpi.setType("SCRIPT");
                dpi.setFaction("SYSADMIN");
                dpi.setAtk(0);
                dpi.setHp(0);
                dpi.setValidLane("ANY_LANE");

// --- OMEGA (SCRIPTS) ---
                CardEntity zde = new CardEntity();
                zde.setId("scpt_05");
                zde.setName("Zero-Day Exploit");
                zde.setCost(3);
                zde.setDescription("Hancurkan instan 1 Program musuh yang bersisa HP 3 atau kurang.");
                zde.setType("SCRIPT");
                zde.setFaction("OMEGA");
                zde.setAtk(0);
                zde.setHp(0);
                zde.setValidLane("ANY_LANE");

                CardEntity phishing = new CardEntity();
                phishing.setId("scpt_06");
                phishing.setName("Phishing Campaign");
                phishing.setCost(2);
                phishing.setDescription("Paksa Sysadmin membuang (discard) 1 kartu secara acak dari tangannya.");
                phishing.setType("SCRIPT");
                phishing.setFaction("OMEGA");
                phishing.setAtk(0);
                phishing.setHp(0);
                phishing.setValidLane("ANY_LANE");

                CardEntity forkBomb = new CardEntity();
                forkBomb.setId("scpt_07");
                forkBomb.setName("Fork Bomb");
                forkBomb.setCost(4);
                forkBomb.setDescription("Beri 1 Damage ke SEMUA musuh. Dapatkan +1 RAM per musuh yang mati.");
                forkBomb.setType("SCRIPT");
                forkBomb.setFaction("OMEGA");
                forkBomb.setAtk(0);
                forkBomb.setHp(0);
                forkBomb.setValidLane("ANY_LANE");

                CardEntity mitm = new CardEntity();
                mitm.setId("scpt_08");
                mitm.setName("Man-in-the-Middle");
                mitm.setCost(2);
                mitm.setDescription("Stun 1 Program musuh (Tidak bisa Attack/EXECUTE giliran depan).");
                mitm.setType("SCRIPT");
                mitm.setFaction("OMEGA");
                mitm.setAtk(0);
                mitm.setHp(0);
                mitm.setValidLane("ANY_LANE");

                cardRepository.saveAll(Arrays.asList(
                        golem, paladin, guardian, mutex, worm, bootVirus, logicBomb, escalator,
                        dataMiner, loadBalancer, backupServer, interruptHandler, wasp, cryptoJacker, exfiltrator, shadowInstance,
                        ids, proxyShield, waf, portScanner, ddos, sqli, xss, pingOfDeath,
                        honeypot, traceroute, threatHunter, sandbox, ransomware, trojan, c2server, polyVirus,
                        sniffer, emergencyPatch, mobileProxy, sysadminAvatar, botnet, adware, zeroDay, apt,
                        sysPatch, pingReq, clearCache, dpi, zde, phishing, forkBomb, mitm
                ));

                System.out.println("Data kartu berhasil disuntikkan!");
            } else {
                System.out.println("Database sudah berisi data kartu. Melewati proses seeding.");
            }
        };
    }
}