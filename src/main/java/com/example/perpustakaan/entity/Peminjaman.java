package com.example.perpustakaan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "peminjaman")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Peminjaman {

    @Id
    @Column(name = "nomor_transaksi", length = 30)
    private String nomorTransaksi;

    @Column(name = "tanggal_pinjam", nullable = false)
    private LocalDate tanggalPinjam;

    @Column(name = "tanggal_kembali", nullable = false)
    private LocalDate tanggalKembali;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPeminjaman status;

    // Asosiasi: banyak peminjaman milik satu anggota
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nomor_anggota", nullable = false)
    private Anggota anggota;

    // Agregasi: peminjaman hanya MERUJUK buku; menghapus peminjaman tidak menghapus buku
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "kode_buku", nullable = false)
    private Buku buku;

    /** Domain method: tanggal kembali = tanggal pinjam + lama hari. */
    public void tetapkanTanggal(LocalDate pinjam, int lamaHari) {
        this.tanggalPinjam = pinjam;
        this.tanggalKembali = pinjam.plusDays(lamaHari);
    }
}
