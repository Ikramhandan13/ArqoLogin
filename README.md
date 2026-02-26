# ArqoLogin Plugin

Plugin Spigot/Paper untuk otentikasi pemain (login/register) dengan
support Java dan Bedrock (via Floodgate). Dirancang modular dengan
PacketEvents v2, Floodgate API, dan kompatibel dengan Folia.

## Fitur Utama

- Multi-database (SQLite/MySQL) dengan hashing BCrypt
- GUI hybrid: Sign virtual untuk Java, Cumulus custom form untuk Bedrock
- "Close to chat" behavior
- Limbo world ringan (void world) untuk pemain belum auth
- Proteksi jaringan: batalkan paket pergerakan, interaksi, chat false
- BossBar & ActionBar timeout 60 detik
- Username/password validasi ketat, brute-force/IP lockout
- Premium auto-login & sesi IP
- Admin commands

## Struktur Proyek

```
src/main/java/com/arqologin/
  - ArqoLoginPlugin.java (main)
  - auth/ (AuthManager, AuthConfig, LimboSession, TimerManager)
  - config/ (ConfigHandler)
  - database/ (DatabaseManager, UserRecord, FileDatabase, DatabaseType)
  - gui/ (GUIManager, SignUtil)
  - limbo/ (LimboManager)
  - listeners/ (PacketEventListener, PlayerJoinListener)
  - commands/ (AdminCommandExecutor)
  - utils/ (SchedulerUtil, MessageUtil)
resources/
  - plugin.yml
  - config.yml
```

Semua operasi database berjalan asynchronous, scheduler Folia-aware
menggunakan `SchedulerUtil`.

## Build

Gunakan Maven (`pom.xml`) atau Gradle (`build.gradle`) untuk mengompilasi.
Depends: PacketEvents, Floodgate-api, Cumulus, PaperLib, jBCrypt.

```bash
mvn clean package
# atau
gradle build
```

## Penggunaan

1. Salin `ArqoLogin.jar` ke folder `plugins` server Paper/Bukkit.
2. Jalankan server, edit `config.yml` sesuai kebutuhan (database, pesan).
3. Restart, plugin akan membuat world `limb` dan tabel database.

> Perhatikan: world `limb` dihasilkan dengan generator void sederhana.

## Catatan Kode

Semua kelas dilengkapi komentar berbahasa Indonesia.
Fitur tambahan seperti IP lockout dan session tracking perlu diimplementasi
lengkap pada `AuthManager` dan `DatabaseManager`.

---

dokumentasi lebih lanjut tersedia di sumber kode.
