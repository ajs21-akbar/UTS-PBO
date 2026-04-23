// ============================================
// ITK-Ride - Sistem Pemesanan Ojek Kampus
// UTS Pemrograman Berorientasi Objek 2025/2026
// ============================================

// ─── CLASS PENUMPANG ───────────────────────
class Penumpang(private val nama: String, saldoAwal: Double) {

    // Data sensitif: saldo PRIVATE, tidak bisa diubah langsung dari luar
    private var saldo: Double = saldoAwal

    fun getNama(): String = nama
    fun getSaldo(): Double = saldo

    // Jalur resmi top up saldo
    fun topUpSaldo(jumlah: Double) {
        if (jumlah <= 0) {
            println("❌ GAGAL: Jumlah top up harus lebih dari 0.")
        } else {
            saldo += jumlah
            println("✅ Top up berhasil. Saldo ${nama} sekarang: Rp${saldo}")
        }
    }

    // Dipanggil oleh Perjalanan saat transaksi berhasil
    fun kurangiSaldo(jumlah: Double): Boolean {
        return if (saldo < jumlah) {
            println("❌ GAGAL: Saldo ${nama} tidak cukup. Saldo: Rp${saldo}, Dibutuhkan: Rp${jumlah}")
            false
        } else {
            saldo -= jumlah
            println("✅ Saldo ${nama} berhasil dikurangi Rp${jumlah}. Sisa saldo: Rp${saldo}")
            true
        }
    }
}

// ─── CLASS DRIVER ──────────────────────────
class Driver(private val nama: String, statusAwal: String = "Tersedia") {

    // Status driver PRIVATE — tidak bisa diubah sembarangan
    private var statusDriver: String = statusAwal

    fun getNama(): String = nama
    fun getStatus(): String = statusDriver

    // Jalur resmi ubah status — hanya menerima nilai yang valid
    fun ubahStatus(statusBaru: String) {
        val statusValid = listOf("Tersedia", "Sibuk", "Offline")
        if (statusBaru !in statusValid) {
            println("❌ GAGAL: Status '$statusBaru' tidak valid. Pilihan: $statusValid")
        } else {
            statusDriver = statusBaru
            println("🔄 Status driver ${nama} berubah menjadi: $statusDriver")
        }
    }
}

// ─── CLASS PERJALANAN ──────────────────────
class Perjalanan(
    private val idPerjalanan: String,
    private val estimasiHarga: Double
) {
    // Status perjalanan PRIVATE
    private var status: String = "Menunggu"

    fun getStatus(): String = status
    fun getEstimasiHarga(): Double = estimasiHarga

    // INTI BISNIS: proses pemesanan ojek dengan validasi lengkap
    fun prosesPerjalanan(penumpang: Penumpang, driver: Driver) {
        println("\n📋 Memproses Perjalanan ID: $idPerjalanan")
        println("   Penumpang : ${penumpang.getNama()} | Saldo: Rp${penumpang.getSaldo()}")
        println("   Driver    : ${driver.getNama()} | Status: ${driver.getStatus()}")
        println("   Estimasi  : Rp${estimasiHarga}")
        println("   ─────────────────────────────────────")

        // Validasi 1: Cek status driver
        if (driver.getStatus() == "Sibuk" || driver.getStatus() == "Offline") {
            status = "Ditolak"
            println("❌ GAGAL: Driver ${driver.getNama()} sedang ${driver.getStatus()}. Pesanan ditolak.")
            return
        }

        // Validasi 2: Cek saldo penumpang
        val berhasil = penumpang.kurangiSaldo(estimasiHarga)
        if (!berhasil) {
            status = "Ditolak"
            println("❌ Perjalanan $idPerjalanan DITOLAK karena saldo tidak cukup.")
            return
        }

        // Semua validasi lolos → proses berhasil
        driver.ubahStatus("Sibuk")
        status = "Berlangsung"
        println("🚗 Perjalanan $idPerjalanan BERHASIL dipesan!")
        println("   Status perjalanan: $status")
    }

    fun selesaiPerjalanan(driver: Driver) {
        if (status == "Berlangsung") {
            status = "Selesai"
            driver.ubahStatus("Tersedia")
            println("🏁 Perjalanan $idPerjalanan telah SELESAI. Driver kembali Tersedia.")
        }
    }
}

// ─── MAIN FUNCTION — SIMULASI ──────────────
fun main() {
    println("========================================")
    println("   SIMULASI SISTEM ITK-RIDE")
    println("========================================")

    // Inisialisasi objek
    val penumpang1 = Penumpang("Akbar", 15000.0)
    val penumpang2 = Penumpang("Budi", 3000.0)
    val driver1 = Driver("Pak Joko", "Tersedia")
    val driver2 = Driver("Pak Budi", "Sibuk")

    // ══════════════════════════════════════
    // SIMULASI GAGAL 1: Driver sedang Sibuk
    // ══════════════════════════════════════
    println("\n[SKENARIO 1] Pesan ojek ke driver yang SIBUK")
    val perjalanan1 = Perjalanan("TRX-001", 10000.0)
    perjalanan1.prosesPerjalanan(penumpang1, driver2)

    // ══════════════════════════════════════
    // SIMULASI GAGAL 2: Saldo tidak cukup
    // ══════════════════════════════════════
    println("\n[SKENARIO 2] Pesan ojek dengan SALDO KURANG")
    val perjalanan2 = Perjalanan("TRX-002", 10000.0)
    perjalanan2.prosesPerjalanan(penumpang2, driver1) // Budi hanya punya Rp3000

    // ══════════════════════════════════════
    // SIMULASI SUKSES: Semua kondisi valid
    // ══════════════════════════════════════
    println("\n[SKENARIO 3] Pesan ojek — KONDISI VALID")
    val perjalanan3 = Perjalanan("TRX-003", 12000.0)
    perjalanan3.prosesPerjalanan(penumpang1, driver1) // Akbar punya Rp15000

    // Selesaikan perjalanan
    println()
    perjalanan3.selesaiPerjalanan(driver1)

    // ══════════════════════════════════════
    // SIMULASI GAGAL 3: Ubah status tidak valid
    // ══════════════════════════════════════
    println("\n[SKENARIO 4] Ubah status driver dengan nilai TIDAK VALID")
    driver1.ubahStatus("Liburan")

    println("\n========================================")
    println("   SIMULASI SELESAI")
    println("========================================")
}