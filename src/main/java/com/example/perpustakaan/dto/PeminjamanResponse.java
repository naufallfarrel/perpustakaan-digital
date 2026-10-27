package com.example.perpustakaan.dto;

import com.example.perpustakaan.entity.Peminjaman;
import java.time.LocalDate;

/** Data untuk Layar D (Peminjaman Berhasil). */
public record PeminjamanResponse(String nomorTransaksi, String status, String judulBuku, String kodeBuku, String peminjam,
                                 int sisaKuota, LocalDate tanggalPinjam, LocalDate tanggalKembali) {
    public static PeminjamanResponse dari(Peminjaman p) {
        return new PeminjamanResponse(p.getNomorTransaksi(), "Dipinjam", p.getBuku().getJudul(), p.getBuku().getKodeBuku(),
                p.getAnggota().getNama() + " (" + p.getAnggota().getNomorAnggota() + ")",
                p.getAnggota().getSisaKuota(), p.getTanggalPinjam(), p.getTanggalKembali());
    }
}
