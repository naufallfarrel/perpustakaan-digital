package com.example.perpustakaan.dto;

import com.example.perpustakaan.entity.Anggota;
import java.time.LocalDate;

public record AnggotaResponse(String nomorAnggota, String nama, String statusKeanggotaan, LocalDate masaBerlaku,
                              int jumlahDipinjam, int batasPinjam, int sisaKuota, String keteranganPinjaman) {
    public static AnggotaResponse dari(Anggota a) {
        return new AnggotaResponse(a.getNomorAnggota(), a.getNama(), a.isAktif() ? "Aktif" : "Nonaktif", a.getMasaBerlaku(),
                a.getJumlahDipinjam(), a.getBatasPinjam(), a.getSisaKuota(),
                a.getJumlahDipinjam() + " dari " + a.getBatasPinjam());
    }
}
