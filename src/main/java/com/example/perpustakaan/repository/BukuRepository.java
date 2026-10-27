package com.example.perpustakaan.repository;

import com.example.perpustakaan.entity.Buku;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BukuRepository extends JpaRepository<Buku, String> {

    @Query("select b from Buku b where lower(b.judul) like lower(concat('%', :q, '%')) " +
           "or lower(b.penulis) like lower(concat('%', :q, '%'))")
    List<Buku> cari(@Param("q") String q);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Buku b where b.kodeBuku = :kode")
    Optional<Buku> findByIdForUpdate(@Param("kode") String kode);
}
