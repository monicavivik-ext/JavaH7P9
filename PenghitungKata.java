package hari7.penugasan9;

import java.util.HashMap;
import java.util.Map;

/** SUDAH JADI. Menghitung kemunculan setiap kata dalam satu teks (pola FrekuensiKata Hari 6). */
public class PenghitungKata {

    public static Map<String, Integer> hitung(String teks) {
        Map<String, Integer> hasil = new HashMap<>();
        for (String kata : teks.toLowerCase().split("[^\\p{L}]+")) {
            if (!kata.isEmpty()) {
                hasil.merge(kata, 1, Integer::sum);
            }
        }
        return hasil;
    }
}
