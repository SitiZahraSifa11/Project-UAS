import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;

public class FormTransaksi extends JFrame implements ActionListener {
    JTextField tfIdPembeli, tfNama, tfAlamat, tfIdBuku, tfJumlah;
    JButton btnSimpan;

    public FormTransaksi() {
        setTitle("Form Transaksi");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Komponen Form
        tfIdPembeli = new JTextField(10);
        tfNama = new JTextField(20);
        tfAlamat = new JTextField(30);
        tfIdBuku = new JTextField(10);
        tfJumlah = new JTextField(5);
        btnSimpan = new JButton("Simpan");
        btnSimpan.addActionListener(this);

        // Layout
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("ID Pembeli:")); panel.add(tfIdPembeli);
        panel.add(new JLabel("Nama:")); panel.add(tfNama);
        panel.add(new JLabel("Alamat:")); panel.add(tfAlamat);
        panel.add(new JLabel("ID Buku:")); panel.add(tfIdBuku);
        panel.add(new JLabel("Jumlah:")); panel.add(tfJumlah);
        panel.add(new JLabel("")); panel.add(btnSimpan);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSimpan) {
            try (Connection conn = DBConnection.getConnection()) {
                // Ambil input
                String idPembeli = tfIdPembeli.getText().trim();
                String nama = tfNama.getText().trim();
                String alamat = tfAlamat.getText().trim();
                String idBuku = tfIdBuku.getText().trim();
                int jumlah = Integer.parseInt(tfJumlah.getText().trim());

                if (idPembeli.isEmpty() || nama.isEmpty() || alamat.isEmpty() || idBuku.isEmpty() || jumlah <= 0) {
                    JOptionPane.showMessageDialog(this, "Semua data harus diisi dan jumlah > 0.");
                    return;
                }

                // Cek stok dan harga buku
                String cekBuku = "SELECT stok, harga FROM buku WHERE id_buku = ?";
                PreparedStatement psCek = conn.prepareStatement(cekBuku);
                psCek.setString(1, idBuku);
                ResultSet rs = psCek.executeQuery();

                int hargaSatuan = 0;
                if (rs.next()) {
                    int stok = rs.getInt("stok");
                    hargaSatuan = rs.getInt("harga");
                    if (stok < jumlah) {
                        JOptionPane.showMessageDialog(this, "Stok tidak mencukupi.");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Buku tidak ditemukan.");
                    return;
                }

                int subtotal = jumlah * hargaSatuan;

                // Simpan pembeli
                String sqlPembeli = "INSERT INTO pembeli (id_pembeli, nama, alamat) VALUES (?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE nama = ?, alamat = ?";
                PreparedStatement psPembeli = conn.prepareStatement(sqlPembeli);
                psPembeli.setString(1, idPembeli);
                psPembeli.setString(2, nama);
                psPembeli.setString(3, alamat);
                psPembeli.setString(4, nama);
                psPembeli.setString(5, alamat);
                psPembeli.executeUpdate();

                // Simpan transaksi
                String sqlTransaksi = "INSERT INTO transaksi (id_pembeli, tanggal) VALUES (?, ?)";
                PreparedStatement psTransaksi = conn.prepareStatement(sqlTransaksi, Statement.RETURN_GENERATED_KEYS);
                psTransaksi.setString(1, idPembeli);
                psTransaksi.setDate(2, Date.valueOf(LocalDate.now()));
                psTransaksi.executeUpdate();

                ResultSet rsTransaksi = psTransaksi.getGeneratedKeys();
                int idTransaksi = 0;
                if (rsTransaksi.next()) {
                    idTransaksi = rsTransaksi.getInt(1);
                }

                // Simpan detail transaksi
                String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_buku, jumlah, harga_satuan, subtotal) " +
                                   "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setInt(1, idTransaksi);
                psDetail.setString(2, idBuku);
                psDetail.setInt(3, jumlah);
                psDetail.setInt(4, hargaSatuan);
                psDetail.setInt(5, subtotal);
                psDetail.executeUpdate();

                // Update stok
                String updateStok = "UPDATE buku SET stok = stok - ? WHERE id_buku = ?";
                PreparedStatement psStok = conn.prepareStatement(updateStok);
                psStok.setInt(1, jumlah);
                psStok.setString(2, idBuku);
                psStok.executeUpdate();

                JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan.\nTotal Bayar: Rp " + subtotal);
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormTransaksi::new);
    }
}
