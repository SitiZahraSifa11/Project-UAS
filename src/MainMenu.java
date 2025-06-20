import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JFrame implements ActionListener {
    JButton btnTambahBuku, btnDaftarBuku, btnDaftarPembeli, btnTransaksi, btnRiwayat, btnKeluar;

    public MainMenu() {
        setTitle("Aplikasi Toko Buku");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        btnTambahBuku = new JButton("Tambah Buku");
        btnDaftarBuku = new JButton("Daftar Buku");
        btnDaftarPembeli = new JButton("Data Pembeli");
        btnTransaksi = new JButton("Form Transaksi");
        btnRiwayat = new JButton("Riwayat Transaksi");
        btnKeluar = new JButton("Keluar");

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.add(btnTambahBuku);
        panel.add(btnDaftarBuku);
        panel.add(btnDaftarPembeli);
        panel.add(btnTransaksi);
        panel.add(btnRiwayat);
        panel.add(btnKeluar);

        add(panel);

        btnTambahBuku.addActionListener(this);
        btnDaftarBuku.addActionListener(this);
        btnDaftarPembeli.addActionListener(this);
        btnTransaksi.addActionListener(this);
        btnRiwayat.addActionListener(this);
        btnKeluar.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTambahBuku) {
            new FormTambahBuku();
        } else if (e.getSource() == btnDaftarBuku) {
            new DaftarBuku();
        } else if (e.getSource() == btnDaftarPembeli) {
            new DaftarPembeli();
        } else if (e.getSource() == btnTransaksi) {
            new FormTransaksi();
        } else if (e.getSource() == btnRiwayat) {
            new DaftarTransaksi();
        } else if (e.getSource() == btnKeluar) {
            int pilihan = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
            if (pilihan == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu());
    }
}
