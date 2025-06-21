import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DaftarPembeli extends JFrame {
    public DaftarPembeli() {
        setTitle("Data Pembeli");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        
        String[] kolom = {"ID Pembeli", "Nama", "Alamat"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);

        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM pembeli ORDER BY id_pembeli ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                    rs.getString("id_pembeli"),
                    rs.getString("nama"),
                    rs.getString("alamat")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data pembeli:\n" + e.getMessage());
        }

        
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DaftarPembeli::new);
    }
}
