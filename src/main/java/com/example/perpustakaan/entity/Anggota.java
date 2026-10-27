package com.example.perpustakaan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "anggota")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Anggota {

    @Id
    @Column(name = "nomor_anggota", length = 30)
    private String nomorAnggota;

    @Column(nullable = false, length = 100)
    private String nama;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_keanggotaan", nullable = false, length = 20)
    private StatusKeanggotaan statusKeanggotaan;

    @Column(name = "masa_berlaku", nullable = false)
    private LocalDate masaBerlaku;

    @Column(name = "jumlah_dipinjam", nullable = false)
    private int jumlahDipinjam;

    @Column(name = "batas_pinjam", nullable = false)
    private int batasPinjam;

    @OneToMany(mappedBy = "anggota", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Peminjaman> daftarPeminjaman = new ArrayList<>();

    // ===== Domain methods (Rich Domain Model) =====
    /** Aktif = status AKTIF dan masa berlaku belum lewat. */
    public boolean isAktif() {
        return statusKeanggotaan == StatusKeanggotaan.AKTIF && !masaBerlaku.isBefore(LocalDate.now());
    }

    public boolean masihPunyaKuota() {
        return jumlahDipinjam < batasPinjam;
    }

    public int getSisaKuota() {
        return batasPinjam - jumlahDipinjam;
    }

    public void tambahPinjaman() {
        if (!masihPunyaKuota()) {
            throw new IllegalStateException("Kuota pinjam sudah habis");
        }
        this.jumlahDipinjam++;
    }
}
