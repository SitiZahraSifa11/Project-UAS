import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class FormTambahBuku extends JFrame implements ActionListener {
    JTextField tfIdBuku, tfJudul, tfPengarang, tfKategori, tfStok, tfHarga;
    JButton btnSimpan;

    public FormTambahBuku() {
        setTitle("Form Tambah Buku");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblIdBuku = new JLabel("ID Buku:");
        JLabel lblJudul = new JLabel("Judul:");
        JLabel lblPengarang = new JLabel("Pengarang:");
        JLabel lblKategori = new JLabel("ID Kategori:");
        JLabel lblStok = new JLabel("Stok:");
        JLabel lblHarga = new JLabel("Harga (Rp):");

        tfIdBuku = new JTextField(10);
        tfJudul = new JTextField(20);
        tfPengarang = new JTextField(20);
        tfKategori = new JTextField(5); 
        tfStok = new JTextField(5);
        tfHarga = new JTextField(10);

        btnSimpan = new JButton("Simpan");
        btnSimpan.addActionListener(this);

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(lblIdBuku); panel.add(tfIdBuku);
        panel.add(lblJudul); panel.add(tfJudul);
        panel.add(lblPengarang); panel.add(tfPengarang);
        panel.add(lblKategori); panel.add(tfKategori);
        panel.add(lblStok); panel.add(tfStok);
        panel.add(lblHarga); panel.add(tfHarga);
        panel.add(new JLabel("")); panel.add(btnSimpan);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSimpan) {
            try (Connection conn = DBConnection.getConnection()) {
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
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Stok, harga, dan ID kategori harus berupa angka.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan buku: " + ex.getMessage());
            }
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormTambahBuku::new);
    }
}
