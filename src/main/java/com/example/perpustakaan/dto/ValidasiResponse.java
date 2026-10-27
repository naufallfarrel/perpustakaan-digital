package com.example.perpustakaan.dto;

import java.time.LocalDate;
import java.util.List;

/** Hasil pemeriksaan sisi-klien (Layar A/B/C): menentukan tombol Ajukan aktif atau tidak. */
public record ValidasiResponse(boolean dapatDiajukan, List<String> alasan, BukuResponse buku, AnggotaResponse anggota,
                               LocalDate tanggalPinjam, LocalDate estimasiTanggalKembali) {
}
