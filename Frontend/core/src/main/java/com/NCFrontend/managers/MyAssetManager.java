package com.NCFrontend.managers;

import com.badlogic.gdx.assets.AssetManager;

public class MyAssetManager {
    // 1. Instansiasi statis (Singleton)
    private static MyAssetManager instance;

    // 2. Objek AssetManager bawaan LibGDX
    public final AssetManager manager;

    // 3. Private constructor agar tidak bisa di-new dari luar class ini
    private MyAssetManager() {
        manager = new AssetManager();
    }

    // 4. Method global untuk mengambil instance (Pola Singleton)
    public static MyAssetManager getInstance() {
        if (instance == null) {
            instance = new MyAssetManager();
        }
        return instance;
    }

    // Method untuk membersihkan memori saat game ditutup
    public void dispose() {
        manager.dispose();
    }
}
