import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

@Override
public void draw(Batch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);

    font.draw(batch, data.getName(), getX() + 10, getY() + 180);
    font.draw(batch, "RAM: " + data.getCost(), getX() + 10, getY() + 150);

    // Sekarang logika pengecekannya mencakup PROGRAM dan MALWARE
    if (data.getType().equals("PROGRAM") || data.getType().equals("MALWARE")) {
        // Tampilkan HP dan ATK
        font.draw(batch, "ATK: " + data.getAtk(), getX() + 10, getY() + 40);
        font.draw(batch, "HP: " + data.getHp(), getX() + 90, getY() + 40);

        // Opsional: Beri tanda jika itu Malware
        if(data.getType().equals("MALWARE")) {
            font.setColor(Color.RED); // Malware warnanya merah biar serem
            font.draw(batch, "MALWARE", getX() + 10, getY() + 130);
            font.setColor(Color.WHITE); // Balikkan warna ke putih
        }
    } else {
        font.draw(batch, "EFEK: " + data.getEffectType(), getX() + 10, getY() + 40);
    }
}
