
package hari7.penugasan9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** PENUGASAN IX - TODO 3, 4, 5. Tiga cara menghitung kata dari banyak teks. */
public class HitungParalel {

    /** SUDAH JADI. Cara biasa, satu thread. Dipakai sebagai pembanding. */
    public static Map<String, Integer> hitungBerurutan(
            List<String> daftarTeks) {

        Map<String, Integer> hasil = new HashMap<>();

        for (String teks : daftarTeks) {
            PenghitungKata.hitung(teks)
                    .forEach((k, v) -> hasil.merge(k, v, Integer::sum));
        }

        return hasil;
    }

    /**
     * TODO 3 dan 4. Satu thread untuk setiap teks.
     */
    public static Map<String, Integer> hitungDenganThread(
            List<String> daftarTeks) throws InterruptedException {

        Map<String, Integer> hasil = new HashMap<>();
        List<Thread> daftarThread = new ArrayList<>();

        // TODO 3: Buat dan jalankan semua thread terlebih dahulu.
        for (String teks : daftarTeks) {
            Thread t = new Thread(new TugasHitung(teks, hasil));
            daftarThread.add(t);
            t.start();
        }

        // TODO 4: Tunggu semua thread selesai.
        for (Thread t : daftarThread) {
            t.join();
        }

        return hasil;
    }

    /**
     * TODO 5. Menggunakan ExecutorService dan Callable.
     */
    public static Map<String, Integer> hitungDenganKolam(
            List<String> daftarTeks, int jumlahThread) throws Exception {

        // Jumlah thread harus minimal 1.
        if (jumlahThread < 1) {
            throw new IllegalArgumentException("jumlahThread minimal 1");
        }

        ExecutorService kolam = Executors.newFixedThreadPool(jumlahThread);

        try {
            List<Future<Map<String, Integer>>> daftarFuture =
                    new ArrayList<>();

            // Submit satu tugas untuk setiap teks.
            for (String teks : daftarTeks) {
                Callable<Map<String, Integer>> tugas = () -> PenghitungKata.hitung(teks);

                Future<Map<String, Integer>> future = kolam.submit(tugas);

                daftarFuture.add(future);
            }

            // Gabungkan hasil setiap tugas di thread pemanggil.
            Map<String, Integer> hasil = new HashMap<>();

            for (Future<Map<String, Integer>> future : daftarFuture) {
                Map<String, Integer> lokal = future.get();

                for (Map.Entry<String, Integer> e : lokal.entrySet()) {
                    hasil.merge(
                            e.getKey(),
                            e.getValue(),
                            Integer::sum
                    );
                }
            }

            return hasil;

        } finally {
            kolam.shutdown();
        }
    }
}
