package hari7.penugasan9;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** SUDAH JADI. Membuat teks buatan yang panjang (selalu sama untuk seed yang sama), untuk menguji kecepatan. */
public class Korpus {

    private static final String[] KATA = {
        "kucing", "kopi", "hujan", "jalan", "rumah", "pagi", "malam", "teman", "buku", "laut",
        "gunung", "angin", "pasar", "sepeda", "kereta", "sawah", "bulan", "bintang", "lagu", "pintu",
        "jendela", "meja", "kursi", "sungai", "pohon", "daun", "bunga", "burung", "ikan", "nasi",
        "teh", "gula", "garam", "roti", "kota", "desa", "anak", "ibu", "ayah", "nenek",
        "dan", "di", "ke", "yang", "itu", "ini", "dengan", "dari", "untuk", "pada"
    };

    /** Satu teks berisi jumlahKata kata acak. Kata pendek (dan, di, ke, ...) sengaja lebih sering muncul. */
    public static String buat(int jumlahKata, long seed) {
        Random r = new Random(seed);
        StringBuilder sb = new StringBuilder(jumlahKata * 7);
        for (int i = 0; i < jumlahKata; i++) {
            int indeks = r.nextInt(3) == 0 ? 40 + r.nextInt(10) : r.nextInt(KATA.length);
            sb.append(KATA[indeks]).append(i % 12 == 11 ? ".\n" : " ");
        }
        return sb.toString();
    }

    /** Beberapa teks sekaligus, misalnya bab-bab sebuah novel. */
    public static List<String> buatBanyak(int jumlahTeks, int kataPerTeks) {
        List<String> hasil = new ArrayList<>();
        for (int i = 0; i < jumlahTeks; i++) {
            hasil.add(buat(kataPerTeks, 100 + i));
        }
        return hasil;
    }
}
