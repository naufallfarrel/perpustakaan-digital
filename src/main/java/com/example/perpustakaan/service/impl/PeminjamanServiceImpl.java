package com.example.perpustakaan.service.impl;

import com.example.perpustakaan.dto.*;
import com.example.perpustakaan.entity.*;
import com.example.perpustakaan.exception.BusinessValidationException;
import com.example.perpustakaan.exception.ResourceNotFoundException;
import com.example.perpustakaan.repository.*;
import com.example.perpustakaan.service.PeminjamanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Orchestrator: mengoordinasikan Anggota, Buku, dan Peminjaman.
 * Tidak menyimpan state di memori (stateless); semua state ada di entity/database.
 */
@Service
@RequiredArgsConstructor
public class PeminjamanServiceImpl implements PeminjamanService {

    private static final DateTimeFormatter TGL = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

    private final AnggotaRepository anggotaRepository;
    private final BukuRepository bukuRepository;
    private final PeminjamanRepository peminjamanRepository;

    @Value("${perpustakaan.lama-pinjam-hari:14}")
    private int lamaPinjamHari = 14;

    @Override
    @Transactional(readOnly = true)
    public List<BukuResponse> cariBuku(String keyword) {
        List<Buku> hasil = (keyword == null || keyword.isBlank()) ? bukuRepository.findAll() : bukuRepository.cari(keyword.trim());
        return hasil.stream().map(BukuResponse::dari).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BukuResponse detailBuku(String kodeBuku) {
        return BukuResponse.dari(ambilBuku(kodeBuku));
    }

    @Override
    @Transactional(readOnly = true)
    public AnggotaResponse dataAnggota(String nomorAnggota) {
        return AnggotaResponse.dari(ambilAnggota(nomorAnggota));
    }

    /** Validasi tahap 1 (mencerminkan keputusan "Klien" pada Alur_Validasi): tidak mengubah data. */
    @Override
    @Transactional(readOnly = true)
    public ValidasiResponse validasi(String nomorAnggota, String kodeBuku) {
        Anggota anggota = ambilAnggota(nomorAnggota);
        Buku buku = ambilBuku(kodeBuku);
        List<String> alasan = periksa(anggota, buku);
        LocalDate hariIni = LocalDate.now();
        return new ValidasiResponse(alasan.isEmpty(), alasan, BukuResponse.dari(buku), AnggotaResponse.dari(anggota),
                hariIni, hariIni.plusDays(lamaPinjamHari));
    }

    /** Validasi tahap 2 (server periksa ulang) + simpan transaksi + kurangi stok + hitung tanggal kembali. */
    @Override
    @Transactional
    public PeminjamanResponse ajukanPeminjaman(PeminjamanRequest request) {
        // Kunci baris (urutan tetap: anggota lalu buku) agar dua request bersamaan tidak lolos bersama
        Anggota anggota = anggotaRepository.findByIdForUpdate(request.nomorAnggota())
                .orElseThrow(() -> new ResourceNotFoundException("Anggota " + request.nomorAnggota() + " tidak ditemukan"));
        Buku buku = bukuRepository.findByIdForUpdate(request.kodeBuku())
                .orElseThrow(() -> new ResourceNotFoundException("Buku " + request.kodeBuku() + " tidak ditemukan"));

        List<String> alasan = periksa(anggota, buku);
        if (!alasan.isEmpty()) {
            throw new BusinessValidationException("Peminjaman tidak dapat diajukan", alasan);
        }

        buku.kurangStok();
        anggota.tambahPinjaman();

        LocalDate hariIni = LocalDate.now();
        Peminjaman peminjaman = Peminjaman.builder()
                .nomorTransaksi(buatNomorTransaksi(hariIni))
                .status(StatusPeminjaman.DIPINJAM)
                .anggota(anggota)
                .buku(buku)
                .build();
        peminjaman.tetapkanTanggal(hariIni, lamaPinjamHari);

        return PeminjamanResponse.dari(peminjamanRepository.save(peminjaman));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeminjamanResponse> daftarPinjaman(String nomorAnggota) {
        ambilAnggota(nomorAnggota);
        return peminjamanRepository.findByAnggotaNomorAnggotaOrderByTanggalPinjamDesc(nomorAnggota)
                .stream().map(PeminjamanResponse::dari).toList();
    }

    // ================= helper =================
    /** Aturan bisnis tunggal yang dipakai validasi() dan ajukanPeminjaman(). */
    List<String> periksa(Anggota anggota, Buku buku) {
        List<String> alasan = new ArrayList<>();
        if (!anggota.isAktif()) {
            alasan.add("Keanggotaan Anda tidak aktif (berakhir " + anggota.getMasaBerlaku().format(TGL)
                    + "). Hubungi petugas perpustakaan untuk perpanjangan.");
        }
        if (!anggota.masihPunyaKuota()) {
            alasan.add("Anda telah mencapai batas maksimal " + anggota.getBatasPinjam() + " buku.");
        }
        if (!buku.isTersedia()) {
            alasan.add("Seluruh eksemplar " + buku.getJudul() + " sedang dipinjam.");
        }
        return alasan;
    }

    private String buatNomorTransaksi(LocalDate tanggal) {
        long urut = peminjamanRepository.countByTanggalPinjam(tanggal) + 1;
        return String.format("PJM-%s-%04d", tanggal.format(DateTimeFormatter.BASIC_ISO_DATE), urut);
    }

    private Anggota ambilAnggota(String nomor) {
        return anggotaRepository.findById(nomor)
                .orElseThrow(() -> new ResourceNotFoundException("Anggota " + nomor + " tidak ditemukan"));
    }

    private Buku ambilBuku(String kode) {
        return bukuRepository.findById(kode)
                .orElseThrow(() -> new ResourceNotFoundException("Buku " + kode + " tidak ditemukan"));
    }
}
