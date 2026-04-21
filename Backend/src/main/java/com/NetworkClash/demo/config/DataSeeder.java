package com.NetworkClash.demo.config;

import com.NetworkClash.demo.entity.CardEntity;
import com.NetworkClash.demo.repository.CardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(CardRepository cardRepository) {
        return args -> {
            // Cek apakah tabel master_cards masih kosong (jumlah data = 0)
            if (cardRepository.count() == 0) {
                System.out.println("Database kosong. Menyuntikkan data kartu awal...");

                // Program (Sysadmin)
                CardEntity golem = new CardEntity();
                golem.setId("prog_01");
                golem.setName("Firewall Golem");
                golem.setCost(3);
                golem.setDescription("Memunculkan shield untuk lane kiri dan kanannya. ");
                golem.setType("PROGRAM");
                golem.setFaction("SYSADMIN");
                golem.setAtk(0);
                golem.setHp(8);
                golem.setValidLane("LOCALHOST");

                CardEntity paladin = new CardEntity();
                paladin.setId("prog_02");
                paladin.setName("Anti-Virus Paladin");
                paladin.setCost(3);
                paladin.setDescription("Memulihkan 1 HP untuk Program kawan di sebelahnya setiap akhir giliran. ");
                paladin.setType("PROGRAM");
                paladin.setFaction("SYSADMIN");
                paladin.setAtk(2);
                paladin.setHp(4);
                paladin.setValidLane("LOCALHOST");

                CardEntity guardian = new CardEntity();
                guardian.setId("prog_03");
                guardian.setName("Kernel Guardian");
                guardian.setCost(4);
                guardian.setDescription("Memulihkan 1 HP untuk Program kawan di sebelahnya setiap akhir giliran. ");
                guardian.setType("PROGRAM");
                guardian.setFaction("SYSADMIN");
                guardian.setAtk(3);
                guardian.setHp(5);
                guardian.setValidLane("LOCALHOST");

                CardEntity lock = new CardEntity();
                lock.setId("prog_04");
                lock.setName("Mutex Lock");
                lock.setCost(2);
                lock.setDescription("Miringkan kartu ini untuk mengunci 1 Program musuh di depannya; musuh tersebut tidak bisa menyerang pada giliran ini. ");
                lock.setType("PROGRAM");
                lock.setFaction("SYSADMIN");
                lock.setAtk(0);
                lock.setHp(6);
                lock.setValidLane("LOCALHOST");

                // Malware (O.M.E.G.A)
                CardEntity worm = new CardEntity();
                worm.setId("malw_01");
                worm.setName("Rootkit Worm");
                worm.setCost(2);
                worm.setDescription("Serangan mengabaikan efek perlindungan (Vanguard) musuh. ");
                worm.setType("MALWARE");
                worm.setFaction("OMEGA");
                worm.setAtk(2);
                worm.setHp(2);
                worm.setValidLane("LOCALHOST");

                CardEntity bootsector = new CardEntity();
                bootsector.setId("malw_02");
                bootsector.setName("Boot Sector Virus");
                bootsector.setCost(3);
                bootsector.setDescription("Memberikan 1 Damage langsung ke HP Sysadmin, mengabaikan Program di depannya. ");
                bootsector.setType("MALWARE");
                bootsector.setFaction("OMEGA");
                bootsector.setAtk(3);
                bootsector.setHp(3);
                bootsector.setValidLane("LOCALHOST");

                CardEntity bomb = new CardEntity();
                bomb.setId("malw_03");
                bomb.setName("Logic Bomb");
                bomb.setCost(2);
                bomb.setDescription("Di akhir giliran ke-2 setelah dimainkan, hancur dan beri 6 Damage besar ke depan.  ");
                bomb.setType("MALWARE");
                bomb.setFaction("OMEGA");
                bomb.setAtk(0);
                bomb.setHp(4);
                bomb.setValidLane("LOCALHOST");

                CardEntity escalator = new CardEntity();
                escalator.setId("malw_04");
                escalator.setName("Privilege Escalator");
                escalator.setCost(3);
                escalator.setDescription("Mendapat permanen +1 ATK setiap kali ada Program Sysadmin yang hancur di papan.  ");
                escalator.setType("MALWARE");
                escalator.setFaction("OMEGA");
                escalator.setAtk(2);
                escalator.setHp(3);
                escalator.setValidLane("LOCALHOST");



                // Script
                CardEntity ping = new CardEntity();
                ping.setId("scpt_01");
                ping.setName("Ping Request");
                ping.setCost(1);
                ping.setDescription("Berikan 2 Damage secara langsung kepada 1 Malware target di Lane mana saja. ");
                ping.setType("SCRIPT");
                ping.setFaction("SYSADMIN");
                // ATK dan HP dibiarkan null karena ini adalah Script
                ping.setEffectType("DIRECT_DAMAGE");

                CardEntity patch = new CardEntity();
                patch.setId("scpt_02");
                patch.setName("System Patch");
                patch.setCost(2);
                patch.setDescription("Pilih 1 Program kawan di Lane mana saja. Berikan +2 HP dan +1 ATK secara permanen.  ");
                patch.setType("SCRIPT");
                patch.setFaction("SYSADMIN");
                // ATK dan HP dibiarkan null karena ini adalah Script
                patch.setEffectType("DIRECT_DAMAGE");

                // Simpan ke PostgreSQL
                cardRepository.saveAll(List.of(golem, paladin, guardian, lock, bootsector, bomb, escalator, worm, ping, patch));

                System.out.println("Data kartu berhasil disuntikkan!");
            } else {
                System.out.println("Database sudah berisi data kartu. Melewati proses seeding.");
            }
        };
    }
}