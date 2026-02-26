# ArqoLogin 🛡️

Plugin otentikasi (Login/Register) Spigot/Paper yang ringan dan sangat stabil, dirancang dengan **Native Spigot API** untuk kompatibilitas versi Minecraft yang luas dan performa maksimal.

## ✨ Fitur Utama
- **Native Spigot Implementation:** Bebas dari ketergantungan PacketEvents/ProtocolLib yang sering pecah saat update Minecraft.
- **Java & Bedrock Support:** Otomatis mendeteksi pemain Bedrock (via Floodgate) dan menampilkan GUI visual (Cumulus Form).
- **Void Limbo World:** Memindahkan pemain yang belum login ke dunia hampa (Void) bernama `limb` untuk mencegah lag dan interaksi ilegal.
- **Keamanan Ekstra (Anti-Bruteforce):**
  - **IP Lockout:** Blokir IP selama 15 menit jika salah password 3 kali.
  - **IP Registration Limit:** Maksimal 3 akun per alamat IP.
  - **24h Session:** Auto-login jika IP dan UUID cocok dalam 24 jam terakhir.
  - **Password Strict:** Minimal 6 karakter dan proteksi terhadap password pasaran/mirip username.
- **Visual Timer:** BossBar merah dan ActionBar real-time (60 detik timeout).
- **Folia Compatible:** Menggunakan scheduler yang aman untuk server modern.

## 🛠️ Kompatibilitas
- **Server:** Spigot, Paper, Purpur, Pufferfish, Folia.
- **Versi:** 1.16.5, 1.17.x, 1.18.x, 1.19.x, 1.20.x, 1.21.x.
- **Java:** Minimal Java 17.
- **Dependencies:** [Floodgate 2.0](https://github.com/GeyserMC/Floodgate) (Wajib untuk Bedrock support).

## 📜 Perintah & Izin
- `/login <password>` - Masuk ke akun.
- `/register <password>` - Mendaftar akun baru.
- `/arqologin <subcommand>` - Administrasi (Permission: `arqologin.admin`)
  - `support` - Info bantuan.
  - `setspawn` - Set lokasi spawn di dunia Limbo.
  - `forcelogin <player>` - Paksa pemain masuk.
  - `dupeip <player/ip>` - Cek akun ganda di IP yang sama.
  - `unregister <player>` - Hapus password pemain.
  - `delete <player>` - Hapus total data pemain.

## 🏗️ Cara Build
Pastikan Anda memiliki Maven terinstal, lalu jalankan:
```bash
mvn clean package
```
File `.jar` akan tersedia di folder `target/`.

---
*Dibuat dengan ❤️ untuk stabilitas server Minecraft Anda.*
