# Perpustakaan Digital - Ajukan Peminjaman Buku (Spring Boot 3)

## Menjalankan
Prasyarat: JDK 17/21 dan Maven (atau import ke IntelliJ/VS Code/Eclipse).

    mvn spring-boot:run

- UI uji   : http://localhost:8080/
- H2 console: http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:perpus_db, user: sa, password kosong)
- Unit test: mvn test

Database default H2 (in-memory, data seed otomatis dari data.sql). Untuk PostgreSQL, lihat komentar di application.yml.

## Endpoint
| Method | URL | Fungsi |
|---|---|---|
| GET | /api/v1/buku?q= | Cari buku |
| GET | /api/v1/buku/{kode} | Detail buku |
| GET | /api/v1/anggota/{nomor} | Data peminjam |
| GET | /api/v1/peminjaman/validasi?nomorAnggota=&kodeBuku= | Validasi awal (tombol aktif/nonaktif) |
| POST | /api/v1/peminjaman | Ajukan peminjaman (server cek ulang) |
| GET | /api/v1/peminjaman/anggota/{nomor} | Pinjaman saya |
