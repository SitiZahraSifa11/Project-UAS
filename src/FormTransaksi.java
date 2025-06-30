import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormTransaksi extends JFrame implements ActionListener {
    JTextField tfIdPembeli, tfNama, tfAlamat, tfIdBuku, tfJumlah;
    JButton btnSimpan;
    JTable table;
    DefaultTableModel tableModel;

    public FormTransaksi() {
        setTitle("Form Transaksi");
        setSize(600, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelInput.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblIdPembeli = new JLabel("ID Pembeli:");
        JLabel lblNama = new JLabel("Nama:");
        JLabel lblAlamat = new JLabel("Alamat:");
        JLabel lblIdBuku = new JLabel("ID Buku:");
        JLabel lblJumlah = new JLabel("Jumlah:");

        lblIdPembeli.setFont(fontLabel);
        lblNama.setFont(fontLabel);
        lblAlamat.setFont(fontLabel);
        lblIdBuku.setFont(fontLabel);
        lblJumlah.setFont(fontLabel);

        tfIdPembeli = new JTextField(20);
        tfNama = new JTextField(20);
        tfAlamat = new JTextField(20);
        tfIdBuku = new JTextField(20);
        tfJumlah = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; panelInput.add(lblIdPembeli, gbc);
        gbc.gridx = 1; panelInput.add(tfIdPembeli, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelInput.add(lblNama, gbc);
        gbc.gridx = 1; panelInput.add(tfNama, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelInput.add(lblAlamat, gbc);
        gbc.gridx = 1; panelInput.add(tfAlamat, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelInput.add(lblIdBuku, gbc);
        gbc.gridx = 1; panelInput.add(tfIdBuku, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panelInput.add(lblJumlah, gbc);
        gbc.gridx = 1; panelInput.add(tfJumlah, gbc);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBackground(new Color(0, 120, 215));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setPreferredSize(new Dimension(120, 35));
        btnSimpan.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelInput.add(btnSimpan, gbc);

        add(panelInput, BorderLayout.NORTH);

       
        String[] kolom = {"ID Pembeli", "Nama", "Alamat", "ID Buku", "Jumlah", "Subtotal"};
        tableModel = new DefaultTableModel(kolom, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Data Transaksi"));

        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSimpan) {
            try (Connection conn = DBConnection.getConnection()) {
                String idPembeli = tfIdPembeli.getText().trim();
                String nama = tfNama.getText().trim();
                String alamat = tfAlamat.getText().trim();
                String idBuku = tfIdBuku.getText().trim();
                int jumlah = Integer.parseInt(tfJumlah.getText().trim());

                if (idPembeli.isEmpty() || nama.isEmpty() || alamat.isEmpty() || idBuku.isEmpty() || jumlah <= 0) {
                    JOptionPane.showMessageDialog(this, "Semua data harus diisi dan jumlah > 0.");
                    return;
                }

                
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

                
                String sqlPembeli = "INSERT INTO pembeli (id_pembeli, nama, alamat) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE nama = ?, alamat = ?";
                PreparedStatement psPembeli = conn.prepareStatement(sqlPembeli);
                psPembeli.setString(1, idPembeli);
                psPembeli.setString(2, nama);
                psPembeli.setString(3, alamat);
                psPembeli.setString(4, nama);
                psPembeli.setString(5, alamat);
                psPembeli.executeUpdate();

                
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

                
                String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_buku, jumlah, harga_satuan, subtotal) " +
                        "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setInt(1, idTransaksi);
                psDetail.setString(2, idBuku);
                psDetail.setInt(3, jumlah);
                psDetail.setInt(4, hargaSatuan);
                psDetail.setInt(5, subtotal);
                psDetail.executeUpdate();

                
                String updateStok = "UPDATE buku SET stok = stok - ? WHERE id_buku = ?";
                PreparedStatement psStok = conn.prepareStatement(updateStok);
                psStok.setInt(1, jumlah);
                psStok.setString(2, idBuku);
                psStok.executeUpdate();

                
                String updateTotal = "UPDATE transaksi SET total_harga = (SELECT SUM(subtotal) FROM detail_transaksi WHERE id_transaksi = ?) WHERE id_transaksi = ?";
                PreparedStatement psTotal = conn.prepareStatement(updateTotal);
                psTotal.setInt(1, idTransaksi);
                psTotal.setInt(2, idTransaksi);
                psTotal.executeUpdate();

               
                tableModel.addRow(new Object[]{idPembeli, nama, alamat, idBuku, jumlah, subtotal});

                
                String cekStokBaru = "SELECT stok FROM buku WHERE id_buku = ?";
                PreparedStatement psCekStok = conn.prepareStatement(cekStokBaru);
                psCekStok.setString(1, idBuku);
                ResultSet rsStokBaru = psCekStok.executeQuery();
                if (rsStokBaru.next()) {
                    int stokBaru = rsStokBaru.getInt("stok");
                    JOptionPane.showMessageDialog(this, "Transaksi berhasil!\nSisa Stok: " + stokBaru + "\nTotal Bayar: Rp " + subtotal);
                }

               
                tfIdPembeli.setText("");
                tfNama.setText("");
                tfAlamat.setText("");
                tfIdBuku.setText("");
                tfJumlah.setText("");

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
