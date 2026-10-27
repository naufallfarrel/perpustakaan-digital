package com.example.perpustakaan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buku")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Buku {

    @Id
    @Column(name = "kode_buku", length = 20)
    private String kodeBuku;

    @Column(nullable = false, length = 200)
    private String judul;

    @Column(nullable = false, length = 100)
    private String penulis;

    /** Jumlah eksemplar yang masih tersedia. */
    @Column(nullable = false)
    private int stok;

    /** Optimistic locking: pengaman tambahan bila dua request menyentuh buku yang sama. */
    @Version
    private Long version;

    // ===== Domain methods =====
    public boolean isTersedia() {
        return stok > 0;
    }

    public boolean kurangStok() {
        if (!isTersedia()) return false;
        this.stok--;
        return true;
    }

    public void tambahStok() {
        this.stok++;
    }

    public String getStatusKetersediaan() {
        return isTersedia() ? "Tersedia (" + stok + ")" : "Dipinjam semua";
    }
}
