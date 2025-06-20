import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class DaftarTransaksi extends JFrame {
    JTextArea textArea;

    public DaftarTransaksi() {
        setTitle("Daftar Transaksi");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // untuk sejajar
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        tampilkanTransaksi();
        setVisible(true);
    }

    void tampilkanTransaksi() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql =
                "SELECT t.id_transaksi, t.tanggal, b.judul, d.jumlah, d.harga_satuan, " +
                "(d.jumlah * d.harga_satuan) AS total_harga " +
                "FROM transaksi t " +
                "JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi " +
                "JOIN buku b ON d.id_buku = b.id_buku " +
                "ORDER BY t.id_transaksi";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-5s %-12s %-25s %-8s %-12s %-12s\n", "ID", "Tanggal", "Judul Buku", "Jumlah", "Harga", "Total"));
            sb.append("==========================================================================\n");

            while (rs.next()) {
                sb.append(String.format("%-5d %-12s %-25s %-8d Rp %-10d Rp %-10d\n",
                    rs.getInt("id_transaksi"),
                    rs.getDate("tanggal").toString(),
                    rs.getString("judul"),
                    rs.getInt("jumlah"),
                    rs.getInt("harga_satuan"),
                    rs.getInt("total_harga")
                ));
            }

            textArea.setText(sb.toString());

        } catch (Exception e) {
            textArea.setText("Gagal tampilkan transaksi: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DaftarTransaksi::new);
    }
}
