package com.example.perpustakaan.service;

import com.example.perpustakaan.dto.PeminjamanRequest;
import com.example.perpustakaan.dto.PeminjamanResponse;
import com.example.perpustakaan.entity.*;
import com.example.perpustakaan.exception.BusinessValidationException;
import com.example.perpustakaan.repository.*;
import com.example.perpustakaan.service.impl.PeminjamanServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeminjamanServiceTest {

    @Mock AnggotaRepository anggotaRepository;
    @Mock BukuRepository bukuRepository;
    @Mock PeminjamanRepository peminjamanRepository;
    @InjectMocks PeminjamanServiceImpl service;

    private Anggota anggota(StatusKeanggotaan s, LocalDate berlaku, int dipinjam) {
        return Anggota.builder().nomorAnggota("AGT-1").nama("Uji").statusKeanggotaan(s)
                .masaBerlaku(berlaku).jumlahDipinjam(dipinjam).batasPinjam(5).build();
    }

    private Buku buku(int stok) {
        return Buku.builder().kodeBuku("BK-1").judul("Basis Data").penulis("X").stok(stok).build();
    }

    private void stub(Anggota a, Buku b) {
        when(anggotaRepository.findByIdForUpdate("AGT-1")).thenReturn(Optional.of(a));
        when(bukuRepository.findByIdForUpdate("BK-1")).thenReturn(Optional.of(b));
    }

    @Test
    void sukses_stokBerkurang_kuotaBertambah_tanggalPlus14() {
        Anggota a = anggota(StatusKeanggotaan.AKTIF, LocalDate.now().plusYears(1), 3);
        Buku b = buku(2);
        stub(a, b);
        when(peminjamanRepository.countByTanggalPinjam(any())).thenReturn(127L);
        when(peminjamanRepository.save(any(Peminjaman.class))).thenAnswer(i -> i.getArgument(0));

        PeminjamanResponse r = service.ajukanPeminjaman(new PeminjamanRequest("AGT-1", "BK-1"));

        assertEquals(1, b.getStok());
        assertEquals(4, a.getJumlahDipinjam());
        assertEquals(1, r.sisaKuota());
        assertTrue(r.nomorTransaksi().endsWith("-0128"));
        assertEquals(r.tanggalPinjam().plusDays(14), r.tanggalKembali());
    }

    @Test
    void ditolak_stokHabis_dan_batasTercapai_duaAlasan() {
        Anggota a = anggota(StatusKeanggotaan.AKTIF, LocalDate.now().plusYears(1), 5);
        Buku b = buku(0);
        stub(a, b);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> service.ajukanPeminjaman(new PeminjamanRequest("AGT-1", "BK-1")));

        assertEquals(2, ex.getAlasan().size());
        assertEquals(0, b.getStok());
        assertEquals(5, a.getJumlahDipinjam());
        verify(peminjamanRepository, never()).save(any());
    }

    @Test
    void ditolak_keanggotaanNonaktif() {
        Anggota a = anggota(StatusKeanggotaan.NONAKTIF, LocalDate.of(2026, 9, 30), 3);
        Buku b = buku(2);
        stub(a, b);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> service.ajukanPeminjaman(new PeminjamanRequest("AGT-1", "BK-1")));

        assertEquals(1, ex.getAlasan().size());
        assertTrue(ex.getAlasan().get(0).contains("tidak aktif"));
        assertEquals(2, b.getStok());
    }
}
