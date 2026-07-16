---
name: Jurnal Pribadi
description: Blog jurnal pribadi minimalis — katarsis tulisan sehari-hari
colors:
  surface-bg: "#f9fafb"
  surface-card: "#ffffff"
  ink-body: "#111827"
  ink-muted: "#6b7280"
  ink-content: "#374151"
  ink-quiet: "#9ca3af"
  accent-primary: "#2563eb"
  accent-link: "#3b82f6"
  tag-bg: "#e5e7eb"
  tag-ink: "#4b5563"
  border-card: "#e5e7eb"
  border-light: "#d1d5db"
  dark-bg: "#030712"
  dark-card: "#1f2937"
  dark-ink-body: "#f3f4f6"
  dark-ink-muted: "#9ca3af"
  dark-ink-content: "#d1d5db"
  dark-ink-tag: "#d1d5db"
  dark-border-card: "#374151"
  dark-accent-link: "#60a5fa"
typography:
  title:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, sans-serif"
    fontSize: "clamp(1.875rem, 4vw, 2.25rem)"
    fontWeight: 700
    lineHeight: 1.2
  card-heading:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, sans-serif"
    fontSize: "1.125rem"
    fontWeight: 600
    lineHeight: 1.3
  body:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, sans-serif"
    fontSize: "0.875rem"
    fontWeight: 400
    lineHeight: 1.7
  label:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, sans-serif"
    fontSize: "0.75rem"
    fontWeight: 500
    lineHeight: 1.4
rounded:
  card: "8px"
  button: "8px"
  input: "8px"
  pill: "9999px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "12px"
  lg: "16px"
  xl: "24px"
  2xl: "48px"
components:
  card:
    backgroundColor: "{colors.surface-card}"
    textColor: "{colors.ink-content}"
    rounded: "{rounded.card}"
    padding: "20px"
  card-dark:
    backgroundColor: "{colors.dark-card}"
    borderColor: "{colors.dark-border-card}"
    rounded: "{rounded.card}"
    padding: "20px"
  button-primary:
    backgroundColor: "{colors.accent-primary}"
    textColor: "#ffffff"
    rounded: "{rounded.button}"
    padding: "12px 16px"
  input:
    backgroundColor: "{colors.surface-card}"
    borderColor: "{colors.border-light}"
    rounded: "{rounded.input}"
    padding: "10px 16px"
  tag:
    backgroundColor: "{colors.tag-bg}"
    textColor: "{colors.tag-ink}"
    rounded: "{rounded.pill}"
    padding: "2px 8px"
  tag-dark:
    backgroundColor: "{colors.dark-card}"
    textColor: "{colors.dark-ink-tag}"
    rounded: "{rounded.pill}"
    padding: "2px 8px"
---

# Design System: Jurnal Pribadi

## 1. Overview

**Creative North Star: "The Open Notebook"**

Halaman ini terasa seperti buku catatan seseorang yang sedang dipinjamkan — personal tanpa berusaha, rapi tanpa kaku. Setiap elemen ada karena tulisan butuh ruang, bukan karena tren desain.

Tidak ada yang mencolok. Warna terang netral di siang hari, bergeser ke gelap di malam hari (toggle di pojok). Card putih dengan bayangan ringan, tipografi sistem yang familiar, aksen biru yang hanya muncul di tombol dan link — selebihnya konten yang bicara.

Sistem ini menolak estetika "AI-made": steril, seragam, terlalu sempurna. Sebaliknya, ia menerima ketidaksempurnaan, variasi panjang tulisan, dan mood yang berganti-ganti (tercermin di emoji mood dan cover album vinyl).

**Key Characteristics:**
- Minimalis yang memberi napas pada konten
- Dua mode (terang/gelap) untuk dua suasana
- Elemen personal: mood emoji, vinyl cover berputar, gambar terpasang
- Zero-friction untuk penulis (autofocus, auto-title, form langsung ngetik)

## 2. Colors

Palet ini diambil dari utilitas Tailwind default — tidak ada warna kustom. Aksen biru (#2563eb) sengaja tidak dominan; ia sinyal interaksi, bukan identitas merek.

### Light Mode

- **Surface Background** (#f9fafb, gray-50): Halaman latar.
- **Card Surface** (#ffffff, white): Card konten. Putih murni dengan shadow ringan.
- **Body Ink** (#111827, gray-900): Teks utama.
- **Content Ink** (#374151, gray-700): Teks excerpt jurnal.
- **Muted Ink** (#6b7280, gray-500): Metadata, waktu, tag.
- **Quiet Ink** (#9ca3af, gray-400): Footer.
- **Accent Blue** (#2563eb, blue-600): Tombol utama dan link.
- **Tag Background** (#e5e7eb, gray-200): Chip tag.
- **Card Border** (#e5e7eb, gray-200): Garis tipis card.

### Dark Mode

- **Surface Background** (#030712, gray-950): Latar gelap, bukan hitam pekat.
- **Card Surface** (#1f2937, gray-800): Card di mode gelap.
- **Body Ink** (#f3f4f6, gray-100): Teks utama.
- **Content Ink** (#d1d5db, gray-300): Teks excerpt.
- **Muted Ink** (#9ca3af, gray-400): Metadata dan tag.
- **Card Border** (#374151, gray-700): Border card gelap.
- **Accent Link** (#60a5fa, blue-400): Link dan tombol di mode gelap.

### Named Rules

**The One Accent Rule.** Biru (#2563eb) hanya dipakai di tombol utama dan link. Tidak di border, tidak di dekorasi. Kelangkaannya yang membuatnya berarti.

**The Gray Shift Rule.** Abu-abu di mode gelap jangan turun ke suhu kebiruan atau keunguan. Tetaplah di gray-700/800/950 murni.

## 3. Typography

**Font Stack:** ui-sans-serif, system-ui, -apple-system, sans-serif

Satu keluarga untuk semua peran. Tidak ada font kustom — kecepatan dan keakraban lebih penting. Hierarki dibangun dari weight dan ukuran.

### Hierarchy

- **Title** (sans-serif, 700, clamp(1.875rem, 4vw, 2.25rem)): H1 utama.
- **Card Heading** (sans-serif, 600, 1.125rem): Judul jurnal di card.
- **Body** (sans-serif, 400, 0.875rem, line-height 1.7): Konten jurnal.
- **Label** (sans-serif, 500, 0.75rem): Label form, grouping header.
- **Metadata** (sans-serif, 400, 0.75rem): Waktu, tag, footer.

### Named Rules

**The System Stack Rule.** Tidak ada font kustom. Tidak ada @font-face, tidak ada FOIT, tidak ada layout shift dari font swap.

## 4. Elevation

Sistem ini datar (flat). Kedalaman dibangun dari layering (card putih di atas background abu-abu) dan satu level shadow: shadow-sm (0 1px 2px rgba(0,0,0,0.05)). Di mode gelap shadow tidak muncul — kontras value antara card gray-800 dan background gray-950 sudah cukup untuk pemisahan.

### Named Rules

**The Flat Rule.** Tidak ada card terangkat lebih dari satu level. Tidak ada card di dalam card. Satu layer shadow.

## 5. Components

### Buttons

- **Primary (Simpan Jurnal, + Tulis Baru):** Rounded 8px, fill #2563eb (blue-600), white text (14px/500). Hover: #1d4ed8 (blue-700). Padding: 12px 16px.
- **Toggle (Dark Mode 🌙/☀️):** Square 36x36px, rounded 8px, border gray-300, text gray-500. Hover: bg-gray-100. Icon adalah text glyph.
- **Ghost (Ganti konfigurasi, hapus ×):** Underline only, text gray-400, hover gray-600. 12px.

### Cards

- **Corner:** Rounded 8px
- **Background:** #ffffff (light) / #1f2937 (dark)
- **Border:** #e5e7eb (light) / #374151 (dark)
- **Shadow:** 0 1px 2px rgba(0,0,0,0.05) (light only)
- **Internal Padding:** 20px
- **Internal Gap:** 24px (space-y-6)

### Inputs

- **Style:** Stroke border (#d1d5db), white fill (#ffffff), rounded 8px
- **Focus:** Ring 2px #60a5fa (blue-400), border transparent
- **Input padding:** 10px 16px (text), 12px 16px (textarea)
- **Textarea rows:** 16, resize vertical
- **Autofocus:** Wajib — halaman tulis langsung siap ngetik.

### Chips / Tags

- **Background:** #e5e7eb (gray-200), text gray-600
- **Rounded:** full (pill shape)
- **Font:** 12px
- **Padding:** 2px 8px

### Music Search (Deezer)

- **Input:** sama dengan input biasa
- **Dropdown:** absolute, bg-white, border, shadow-lg, max-h-60 overflow-y-auto
- **Item:** flex row, 40px thumbnail + truncated title + artist name
- **Selected:** bg-gray-100 row, 40px circular cover, nama lagu, × hapus

### Image Upload

- **Drop zone:** border-2 dashed #d1d5db, rounded 8px, hover border blue-400
- **Preview:** max-h-48, rounded 8px, × delete overlay (absolute, top-right)
- **Upload flow:** image → commit ke journals/images/ → raw URL → frontmatter

### Vinyl Record Component

- **Size:** 48x48px, circular
- **Fill:** #1a1a2e (dark navy)
- **Animation:** spin 4s linear infinite
- **Center label:** cover album (CSS background-image), inset 12px, circular
- **Center hole:** 6px white dot
- **Groove:** 2px dashed white ring at 4px inset
- **Performance:** content-visibility: auto pada parent article — animasi hanya jalan saat card terlihat.

## 6. Do's and Don'ts

### Do:

- **Do** biarkan konten jadi fokus utama. Warna, spacing, dan tipografi hanyalah pendukung.
- **Do** gunakan mode gelap sebagai fitur aksesibilitas malam hari — toggle harus mudah dijangkau.
- **Do** pertahankan jarak baris longgar (1.7) untuk body text — jurnal dibaca di layar, bukan cetak.
- **Do** tampilkan mood emoji dan cover album — ini yang membuat jurnal terasa personal, bukan buatan AI.

### Don't:

- **Don't** tambahkan elemen dekoratif yang tidak punya fungsi — tidak ada gradient text, glassmorphism, side-stripe borders.
- **Don't** gunakan lebih dari satu aksen warna. Biru accent adalah satu-satunya warna jenuh.
- **Don't** tampilkan hero-metric, numbered sections, atau eyebrow headers (01 / ABOUT / dll.). Jurnal bukan landing page.
- **Don't** bikin card yang terlihat "AI-generated" — terlalu rapi, terlalu simetris, tanpa kepribadian.
- **Don't** letakkan border-left atau border-right sebagai accent dekoratif.
- **Don't** optimize untuk engagement metrics. Jurnal ini untuk katarsis dan koneksi, bukan untuk click rate.
- **Don't** shadow di mode gelap — tonal layering saja.
