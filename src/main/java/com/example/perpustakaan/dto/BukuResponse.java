package com.example.perpustakaan.dto;

import com.example.perpustakaan.entity.Buku;

public record BukuResponse(String kodeBuku, String judul, String penulis, int stok, boolean tersedia, String statusKetersediaan) {
    public static BukuResponse dari(Buku b) {
        return new BukuResponse(b.getKodeBuku(), b.getJudul(), b.getPenulis(), b.getStok(), b.isTersedia(), b.getStatusKetersediaan());
    }
}
