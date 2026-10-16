package hari7.penugasan9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Penguji Penugasan IX. Jalankan: klik kanan > Run File. Jangan diubah. */
public class UjiParalel {

    static int lulus = 0, total = 0;

    interface Uji {
        Object jalankan() throws Exception;
    }

    static void cek(String nama, Object harap, Uji uji) {
        total++;
        String hasil;
        try {
            hasil = String.valueOf(uji.jalankan());
        } catch (Throwable e) {
            hasil = e.getClass().getSimpleName() + (e.getMessage() == null ? "" : ": " + e.getMessage());
        }
        boolean ok = String.valueOf(harap).equals(hasil);
        if (ok) lulus++;
        String h = hasil.length() > 40 ? hasil.substring(0, 37) + "..." : hasil;
        System.out.printf("[%s] %-46s harap=%-12s hasil=%s%n", ok ? "LULUS" : "GAGAL", nama, harap, h);
    }

    /** Teks dengan kosakata sangat banyak (ribuan kata huruf acak), agar penyetoran ke map bersama lama dan sering bertabrakan. */
    static String teksRagam(int jumlahKata, long seed) {
        java.util.Random r = new java.util.Random(seed);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jumlahKata; i++) {
            int n = r.nextInt(20_000);
            StringBuilder kata = new StringBuilder();
            do {
                kata.append((char) ('a' + n % 26));
                n /= 26;
            } while (n > 0);
            sb.append(kata).append(' ');
        }
        return sb.toString();
    }

    static Map<String, Integer> jalankanSatuTugas(String teks, Map<String, Integer> bersama, String namaThread) throws InterruptedException {
        Thread t = new Thread(new TugasHitung(teks, bersama), namaThread);
        t.start();
        t.join();
        return bersama;
    }

    public static void main(String[] args) {
        List<String> empat = List.of("kopi dan teh", "Kopi, kopi, KOPI!", "teh manis", "hujan di pagi hari, kopi panas");
        Map<String, Integer> acuanEmpat = HitungParalel.hitungBerurutan(empat);
        List<String> besar = Korpus.buatBanyak(8, 60_000);
        Map<String, Integer> acuanBesar = HitungParalel.hitungBerurutan(besar);
        List<String> ragam = new ArrayList<>();
        for (int i = 0; i < 16; i++) ragam.add(teksRagam(30_000, i));
        Map<String, Integer> acuanRagam = HitungParalel.hitungBerurutan(ragam);

        // TODO 1
        cek("1. tugas menghitung satu teks", "{a=2, b=1}",
                () -> new java.util.TreeMap<>(jalankanSatuTugas("a b a", new HashMap<>(), "uji-1")));
        cek("2. run() dijalankan thread uji, bukan main", true, () -> {
            TugasHitung.CATATAN_THREAD.clear();
            jalankanSatuTugas("x", new HashMap<>(), "uji-2");
            return TugasHitung.CATATAN_THREAD.contains("uji-2");
        });
        cek("3. huruf besar/kecil dan tanda baca", 4,
                () -> jalankanSatuTugas("Kopi, kopi, KOPI! kopi.", new HashMap<>(), "uji-3").get("kopi"));

        // TODO 2
        cek("4. dua tugas, satu map bersama", 3, () -> {
            Map<String, Integer> m = new HashMap<>();
            jalankanSatuTugas("teh kopi", m, "uji-4a");
            jalankanSatuTugas("teh teh", m, "uji-4b");
            return m.get("teh");
        });
        cek("5. isi lama map ditambah, bukan ditimpa", 12, () -> {
            Map<String, Integer> m = new HashMap<>();
            m.put("buku", 10);
            return jalankanSatuTugas("buku buku", m, "uji-5").get("buku");
        });
        cek("6. 16 thread serentak ke map yang sama (5x)", true, () -> {
            for (int ulang = 0; ulang < 5; ulang++) {
                Map<String, Integer> m = new HashMap<>();
                List<Thread> ts = new ArrayList<>();
                for (String t : ragam) ts.add(new Thread(new TugasHitung(t, m)));
                for (Thread t : ts) t.start();
                for (Thread t : ts) t.join();
                if (!m.equals(acuanRagam)) return false;
            }
            return true;
        });

        // TODO 3 dan 4
        cek("7. daftar kosong -> map kosong", "{}", () -> HitungParalel.hitungDenganThread(List.of()));
        cek("8. satu teks", "{kopi=1, panas=1}", () -> new java.util.TreeMap<>(HitungParalel.hitungDenganThread(List.of("kopi panas"))));
        cek("9. empat teks sama dengan berurutan", true, () -> HitungParalel.hitungDenganThread(empat).equals(acuanEmpat));
        cek("10. memakai 4 thread baru (start, bukan run)", "4 tanpa main", () -> {
            TugasHitung.CATATAN_THREAD.clear();
            HitungParalel.hitungDenganThread(empat);
            int n = TugasHitung.CATATAN_THREAD.size();
            return n + (TugasHitung.CATATAN_THREAD.contains("main") ? " termasuk main" : " tanpa main");
        });
        cek("11. hasil lengkap saat dikembalikan (join)", acuanBesar.get("kopi"), () -> HitungParalel.hitungDenganThread(besar).get("kopi"));
        cek("12. 16 teks beragam 10x berturut-turut benar", true, () -> {
            for (int i = 0; i < 10; i++) {
                if (!HitungParalel.hitungDenganThread(ragam).equals(acuanRagam)) return false;
            }
            return true;
        });

        // TODO 5
        cek("13. kolam 2 thread, empat teks", true, () -> HitungParalel.hitungDenganKolam(empat, 2).equals(acuanEmpat));
        cek("14. kolam 1 thread", true, () -> HitungParalel.hitungDenganKolam(empat, 1).equals(acuanEmpat));
        cek("15. kolam 16 thread (lebih banyak dari teks)", true, () -> HitungParalel.hitungDenganKolam(empat, 16).equals(acuanEmpat));
        cek("16. kolam, daftar kosong", "{}", () -> HitungParalel.hitungDenganKolam(List.of(), 3));
        cek("17. jumlahThread 0 ditolak", "IllegalArgumentException: jumlahThread minimal 1",
                () -> HitungParalel.hitungDenganKolam(empat, 0));

        // gabungan
        cek("18. kolam, teks besar", true, () -> HitungParalel.hitungDenganKolam(besar, 4).equals(acuanBesar));
        cek("19. total kata teks besar (kolam)", 480000,
                () -> HitungParalel.hitungDenganKolam(besar, 4).values().stream().mapToInt(Integer::intValue).sum());
        cek("20. thread dan kolam sepakat", true, () -> {
            Map<String, Integer> t = HitungParalel.hitungDenganThread(besar);
            return !t.isEmpty() && t.equals(HitungParalel.hitungDenganKolam(besar, 3));
        });

        System.out.println("\nSKOR: " + lulus + " / " + total);
        if (lulus < total) {
            System.out.println("Masih ada yang GAGAL, periksa TODO terkait.");
        }
    }
}
