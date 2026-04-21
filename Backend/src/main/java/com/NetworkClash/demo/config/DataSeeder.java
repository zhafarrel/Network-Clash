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
                golem.setDescription("Penjaga jaringan yang sangat tebal. Cocok ditempatkan di DMZ.");
                golem.setType("PROGRAM");
                golem.setFaction("SYSADMIN");
                golem.setAtk(0);
                golem.setHp(8);
                golem.setValidLane("DMZ");

                // Malware (O.M.E.G.A)
                CardEntity worm = new CardEntity();
                worm.setId("mal_01");
                worm.setName("Rootkit Worm");
                worm.setCost(2);
                worm.setDescription("Menyusup secara diam-diam untuk merusak sistem musuh.");
                worm.setType("MALWARE");
                worm.setFaction("OMEGA");
                worm.setAtk(2);
                worm.setHp(2);
                worm.setValidLane("ANY_LANE");

                // Script
                CardEntity ping = new CardEntity();
                ping.setId("script_01");
                ping.setName("Ping Request");
                ping.setCost(1);
                ping.setDescription("Mengirimkan paket data cepat untuk memberikan 2 Damage langsung.");
                ping.setType("SCRIPT");
                ping.setFaction("SYSADMIN");
                // ATK dan HP dibiarkan null karena ini adalah Script
                ping.setEffectType("DIRECT_DAMAGE");

                // Simpan ke PostgreSQL
                cardRepository.saveAll(List.of(golem, worm, ping));

                System.out.println("Data kartu berhasil disuntikkan!");
            } else {
                System.out.println("Database sudah berisi data kartu. Melewati proses seeding.");
            }
        };
    }
}