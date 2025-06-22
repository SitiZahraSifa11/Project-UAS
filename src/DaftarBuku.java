import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DaftarBuku extends JFrame implements ActionListener {
    JTable table;
    DefaultTableModel model;
    JButton btnEdit, btnHapus, btnRefresh;

    public DaftarBuku() {
        setTitle("Daftar Buku");
        setSize(800, 400); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "Judul", "Pengarang", "Kategori", "Stok", "Harga (Rp)"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");

        JPanel panelBawah = new JPanel();
        panelBawah.add(btnEdit);
        panelBawah.add(btnHapus);
        panelBawah.add(btnRefresh);

        add(scrollPane, BorderLayout.CENTER);
        add(panelBawah, BorderLayout.SOUTH);

        btnEdit.addActionListener(this);
        btnHapus.addActionListener(this);
        btnRefresh.addActionListener(this);

        
        model.setRowCount(0); 
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT b.id_buku, b.judul, b.pengarang, k.nama_kategori, b.stok, b.harga " +
                         "FROM buku b LEFT JOIN kategori k ON b.id_kategori = k.id_kategori";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_buku"),
                    rs.getString("judul"),
                    rs.getString("pengarang"),
                    rs.getString("nama_kategori"), 
                    rs.getInt("stok"),
                    rs.getInt("harga")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data: " + e.getMessage());
        }

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        int baris = table.getSelectedRow();
        if (baris == -1) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu baris terlebih dahulu.");
            return;
        }

        String idBuku = model.getValueAt(baris, 0).toString();

        if (e.getSource() == btnHapus) {
            int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus buku " + idBuku + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (konfirmasi == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM buku WHERE id_buku = ?");
                    ps.setString(1, idBuku);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + ex.getMessage());
                }
            }

        } else if (e.getSource() == btnEdit) {
            String judulLama = model.getValueAt(baris, 1).toString();
            String pengarangLama = model.getValueAt(baris, 2).toString();
            int stokLama = Integer.parseInt(model.getValueAt(baris, 4).toString());
            int hargaLama = Integer.parseInt(model.getValueAt(baris, 5).toString());

            String judulBaru = JOptionPane.showInputDialog(this, "Judul:", judulLama);
            String pengarangBaru = JOptionPane.showInputDialog(this, "Pengarang:", pengarangLama);
            String stokBaruStr = JOptionPane.showInputDialog(this, "Stok:", stokLama);
            String hargaBaruStr = JOptionPane.showInputDialog(this, "Harga (Rp):", hargaLama);

            if (judulBaru == null || pengarangBaru == null || stokBaruStr == null || hargaBaruStr == null) return;

            try {
                int stokBaru = Integer.parseInt(stokBaruStr);
                int hargaBaru = Integer.parseInt(hargaBaruStr);

                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "UPDATE buku SET judul=?, pengarang=?, stok=?, harga=? WHERE id_buku=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, judulBaru);
                    ps.setString(2, pengarangBaru);
                    ps.setInt(3, stokBaru);
                    ps.setInt(4, hargaBaru);
                    ps.setString(5, idBuku);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Stok dan Harga harus berupa angka.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + ex.getMessage());
            }

        } else if (e.getSource() == btnRefresh) {
           
            model.setRowCount(0); 
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT b.id_buku, b.judul, b.pengarang, k.nama_kategori, b.stok, b.harga " +
                             "FROM buku b LEFT JOIN kategori k ON b.id_kategori = k.id_kategori";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("id_buku"),
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getString("nama_kategori"), 
                        rs.getInt("stok"),
                        rs.getInt("harga")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengambil data: " + ex.getMessage());
            }
        }
    }
}
