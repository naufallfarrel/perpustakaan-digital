package com.example.perpustakaan.service;

import com.example.perpustakaan.dto.*;
import java.util.List;

public interface PeminjamanService {
    List<BukuResponse> cariBuku(String keyword);
    BukuResponse detailBuku(String kodeBuku);
    AnggotaResponse dataAnggota(String nomorAnggota);
    ValidasiResponse validasi(String nomorAnggota, String kodeBuku);
    PeminjamanResponse ajukanPeminjaman(PeminjamanRequest request);
    List<PeminjamanResponse> daftarPinjaman(String nomorAnggota);
}
