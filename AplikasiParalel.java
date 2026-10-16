package hari7.penugasan9;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Membandingkan tiga cara pada 8 teks x 400.000 kata, lalu mencetak lima kata terbanyak. */
public class AplikasiParalel {

    interface Cara {
        Map<String, Integer> jalankan(List<String> teks) throws Exception;
    }

    static Map<String, Integer> ukur(String nama, Cara cara, List<String> teks) throws Exception {
        long mulai = System.nanoTime();
        Map<String, Integer> hasil = cara.jalankan(teks);
        System.out.printf("%-26s %5d ms  (%d kata berbeda)%n", nama, (System.nanoTime() - mulai) / 1_000_000, hasil.size());
        return hasil;
    }

    public static void main(String[] args) throws Exception {
        int inti = Runtime.getRuntime().availableProcessors();
        System.out.println("Prosesor tersedia: " + inti + " inti");
        List<String> bab = Korpus.buatBanyak(8, 400_000);
        HitungParalel.hitungBerurutan(bab);                         // pemanasan JVM, tidak diukur

        Map<String, Integer> a = ukur("Berurutan (1 thread)", HitungParalel::hitungBerurutan, bab);
        Map<String, Integer> b = ukur("Thread per teks (8)", HitungParalel::hitungDenganThread, bab);
        Map<String, Integer> c = ukur("Kolam " + inti + " thread", t -> HitungParalel.hitungDenganKolam(t, inti), bab);
        System.out.println("Hasil ketiganya sama: " + (a.equals(b) && a.equals(c)));

        List<Map.Entry<String, Integer>> urut = new ArrayList<>(a.entrySet());
        urut.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        System.out.println("Lima kata terbanyak:");
        for (int i = 0; i < 5 && i < urut.size(); i++) {
            System.out.printf("  %-8s %,d%n", urut.get(i).getKey(), urut.get(i).getValue());
        }
    }
}
