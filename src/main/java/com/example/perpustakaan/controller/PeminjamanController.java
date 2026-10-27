package com.example.perpustakaan.controller;

import com.example.perpustakaan.dto.*;
import com.example.perpustakaan.service.PeminjamanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PeminjamanController {

    private final PeminjamanService service;

    /** Langkah 1: Cari buku (judul/penulis). */
    @GetMapping("/buku")
    public ApiResponse<List<BukuResponse>> cariBuku(@RequestParam(required = false) String q) {
        return ApiResponse.success("Pencarian buku berhasil", service.cariBuku(q));
    }

    /** Langkah 2: Detail buku terpilih (hanya-baca). */
    @GetMapping("/buku/{kodeBuku}")
    public ApiResponse<BukuResponse> detailBuku(@PathVariable String kodeBuku) {
        return ApiResponse.success("Detail buku", service.detailBuku(kodeBuku));
    }

    /** Langkah 3: Data peminjam otomatis dari akun (hanya-baca). */
    @GetMapping("/anggota/{nomorAnggota}")
    public ApiResponse<AnggotaResponse> dataAnggota(@PathVariable String nomorAnggota) {
        return ApiResponse.success("Data anggota", service.dataAnggota(nomorAnggota));
    }

    /** Pemeriksaan awal: menentukan tombol "Ajukan Peminjaman" aktif/nonaktif + alasan. */
    @GetMapping("/peminjaman/validasi")
    public ApiResponse<ValidasiResponse> validasi(@RequestParam String nomorAnggota, @RequestParam String kodeBuku) {
        return ApiResponse.success("Hasil validasi", service.validasi(nomorAnggota, kodeBuku));
    }

    /** Tombol "Ajukan Peminjaman": server memeriksa ulang, lalu menyimpan transaksi. */
    @PostMapping("/peminjaman")
    public ResponseEntity<ApiResponse<PeminjamanResponse>> ajukan(@Valid @RequestBody PeminjamanRequest request) {
        return new ResponseEntity<>(ApiResponse.success("Peminjaman berhasil dicatat", service.ajukanPeminjaman(request)),
                HttpStatus.CREATED);
    }

    /** Tombol "Lihat Pinjaman Saya". */
    @GetMapping("/peminjaman/anggota/{nomorAnggota}")
    public ApiResponse<List<PeminjamanResponse>> pinjamanSaya(@PathVariable String nomorAnggota) {
        return ApiResponse.success("Daftar pinjaman", service.daftarPinjaman(nomorAnggota));
    }
}
