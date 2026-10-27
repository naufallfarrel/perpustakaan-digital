package com.example.perpustakaan.repository;

import com.example.perpustakaan.entity.Peminjaman;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PeminjamanRepository extends JpaRepository<Peminjaman, String> {
    long countByTanggalPinjam(LocalDate tanggal);
    List<Peminjaman> findByAnggotaNomorAnggotaOrderByTanggalPinjamDesc(String nomorAnggota);
}
