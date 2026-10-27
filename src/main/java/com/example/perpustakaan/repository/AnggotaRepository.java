package com.example.perpustakaan.repository;

import com.example.perpustakaan.entity.Anggota;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface AnggotaRepository extends JpaRepository<Anggota, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Anggota a where a.nomorAnggota = :id")
    Optional<Anggota> findByIdForUpdate(@Param("id") String id);
}
