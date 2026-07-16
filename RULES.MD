# 📜 OpenCode Rules: Web Jurnal Pribadi Git-Backed (ADHD-Optimized)

## 🎯 Target Proyek & Arsitektur
- Membangun aplikasi web blog jurnal pribadi halaman tunggal (SPA) yang sangat ringan untuk dideploy di Netlify.
- **Penyimpanan**: Git-backed CMS. Jurnal disimpan dalam folder `/journals/` berbentuk file `.md` (Markdown) yang memiliki YAML Frontmatter (id, title, date, tags).
- **Teknologi**: Vanilla HTML, CSS (Tailwind via CDN), dan Vanilla JavaScript saja. Jangan gunakan framework berat.
- **Custom CMS**: Halaman `tulis.html` rahasia untuk push file `.md` baru langsung ke repositori GitHub via GitHub API.

## 🧠 Penjaga Fokus ADHD (Sangat Penting)
- **Anti-Feature Creep**: Jika pengguna (saya) meminta fitur tambahan di luar MVP (seperti sistem login multi-user, gonta-ganti tema, atau kategori rumit), agent WAJIB mengingatkan pengguna untuk menyelesaikan fitur inti dulu sebelum melompat ke ide lain.
- **Desain Zero-Friksi**: Halaman input jurnal harus memiliki atribut `autofocus` agar pengguna bisa langsung mengetik tanpa klik apa pun saat web dibuka.
- **Otomatisasi Judul**: Jika input judul dikosongkan saat menulis jurnal, agent harus membuat sistem yang otomatis mengambil 5 kata pertama dari isi jurnal sebagai judulnya.

## 🤖 Optimasi OpenCode Go & Gaya Kode
- **Langsung ke Kode**: Tulis atau edit kode secara langsung tanpa penjelasan teori/konsep web development yang panjang lebar untuk menghemat token kuota.
- **Efisiensi Caching**: Jangan menulis ulang seluruh isi file jika hanya ingin mengubah satu fungsi kecil. Cukup berikan bagian kode yang berubah agar memanfaatkan Token Caching OpenCode Go.
- **Penanganan Eror Instan**: Setiap fungsi pemanggilan API (seperti push ke GitHub) wajib memiliki `try-catch` yang menampilkan pesan eror jelas di layar agar mudah di-debug melalui TUI terminal loop.
