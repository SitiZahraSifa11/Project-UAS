import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormTambahBuku extends JFrame implements ActionListener {
    JTextField tfIdBuku, tfJudul, tfPengarang, tfKategori, tfStok, tfHarga;
    JButton btnSimpan;
    JTable table;
    DefaultTableModel tableModel;

    public FormTambahBuku() {
        setTitle("Form Tambah Buku");
        setSize(600, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Input Buku
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelInput.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblIdBuku = new JLabel("ID Buku:");
        JLabel lblJudul = new JLabel("Judul:");
        JLabel lblPengarang = new JLabel("Pengarang:");
        JLabel lblKategori = new JLabel("ID Kategori:");
        JLabel lblStok = new JLabel("Stok:");
        JLabel lblHarga = new JLabel("Harga (Rp):");

        lblIdBuku.setFont(fontLabel);
        lblJudul.setFont(fontLabel);
        lblPengarang.setFont(fontLabel);
        lblKategori.setFont(fontLabel);
        lblStok.setFont(fontLabel);
        lblHarga.setFont(fontLabel);

        tfIdBuku = new JTextField(20);
        tfJudul = new JTextField(20);
        tfPengarang = new JTextField(20);
        tfKategori = new JTextField(20);
        tfStok = new JTextField(20);
        tfHarga = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; panelInput.add(lblIdBuku, gbc);
        gbc.gridx = 1; panelInput.add(tfIdBuku, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelInput.add(lblJudul, gbc);
        gbc.gridx = 1; panelInput.add(tfJudul, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelInput.add(lblPengarang, gbc);
        gbc.gridx = 1; panelInput.add(tfPengarang, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelInput.add(lblKategori, gbc);
        gbc.gridx = 1; panelInput.add(tfKategori, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panelInput.add(lblStok, gbc);
        gbc.gridx = 1; panelInput.add(tfStok, gbc);
        gbc.gridx = 0; gbc.gridy = 5; panelInput.add(lblHarga, gbc);
        gbc.gridx = 1; panelInput.add(tfHarga, gbc);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBackground(new Color(0, 120, 215));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setPreferredSize(new Dimension(120, 35));
        btnSimpan.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelInput.add(btnSimpan, gbc);

        // Tabel Buku
        String[] kolom = {"ID Buku", "Judul", "Pengarang", "Stok", "Harga", "ID Kategori"};
        tableModel = new DefaultTableModel(kolom, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(panelInput, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data saat pertama kali
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM buku");
            while (rs.next()) {
                Object[] data = {
                    rs.getString("id_buku"),
                    rs.getString("judul"),
                    rs.getString("pengarang"),
                    rs.getInt("stok"),
                    rs.getInt("harga"),
                    rs.getInt("id_kategori")
                };
                tableModel.addRow(data);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSimpan) {
            try {
                String idBuku = tfIdBuku.getText().trim();
                String judul = tfJudul.getText().trim();
                String pengarang = tfPengarang.getText().trim();
                String kategoriStr = tfKategori.getText().trim();
                String stokStr = tfStok.getText().trim();
                String hargaStr = tfHarga.getText().trim();

                if (idBuku.isEmpty() || judul.isEmpty() || pengarang.isEmpty() ||
                    kategoriStr.isEmpty() || stokStr.isEmpty() || hargaStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
                    return;
                }

                int idKategori = Integer.parseInt(kategoriStr);
                int stok = Integer.parseInt(stokStr);
                int harga = Integer.parseInt(hargaStr);

                if (stok < 0 || harga < 0) {
                    JOptionPane.showMessageDialog(this, "Stok dan harga harus positif.");
                    return;
                }

                Connection conn = DBConnection.getConnection();
                String sql = "INSERT INTO buku (id_buku, judul, pengarang, stok, harga, id_kategori) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, idBuku);
                ps.setString(2, judul);
                ps.setString(3, pengarang);
                ps.setInt(4, stok);
                ps.setInt(5, harga);
                ps.setInt(6, idKategori);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan.");

                // Kosongkan form
                tfIdBuku.setText("");
                tfJudul.setText("");
                tfPengarang.setText("");
                tfKategori.setText("");
                tfStok.setText("");
                tfHarga.setText("");

                // Tambahkan ke tabel langsung
                Object[] barisBaru = {idBuku, judul, pengarang, stok, harga, idKategori};
                tableModel.addRow(barisBaru);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Stok, harga, dan ID kategori harus angka.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormTambahBuku::new);
    }
}
