
package hari7.penugasan9;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PENUGASAN IX - TODO 1 dan 2.
 */
public class TugasHitung implements Runnable {

    /** SUDAH JADI. Jangan dihapus. */
    public static final Set<String> CATATAN_THREAD = ConcurrentHashMap.newKeySet();

    private final String teks;
    private final Map<String, Integer> hasilBersama;

    public TugasHitung(String teks, Map<String, Integer> hasilBersama) {
        this.teks = teks;
        this.hasilBersama = hasilBersama;
    }

    @Override
    public void run() {
        CATATAN_THREAD.add(Thread.currentThread().getName());

        // TODO 1: Hitung kata ke dalam map lokal.
        Map<String, Integer> lokal = PenghitungKata.hitung(teks);

        // TODO 2: Gabungkan hasil ke map bersama secara aman.
        synchronized (hasilBersama) {
            for (Map.Entry<String, Integer> e : lokal.entrySet()) {
                hasilBersama.merge(
                        e.getKey(),
                        e.getValue(),
                        Integer::sum
                );
            }
        }
    }
}
