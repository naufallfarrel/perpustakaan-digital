package com.example.perpustakaan.dto;

import jakarta.validation.constraints.NotBlank;

public record PeminjamanRequest(
        @NotBlank(message = "Nomor anggota wajib diisi") String nomorAnggota,
        @NotBlank(message = "Kode buku wajib diisi") String kodeBuku) {
}
