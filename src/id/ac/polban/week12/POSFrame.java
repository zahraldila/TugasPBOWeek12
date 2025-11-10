package id.ac.polban.week12;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class POSFrame extends JFrame {
    private static final String LINE_SEPARATOR = "=============================================\n";


    // --- Komponen kiri (Produk) ---
    private JTable tblProduk;
    private JSpinner spQty;
    private JButton btnAddToCart;

    // --- Komponen kanan (Keranjang + Ringkasan + Struk) ---
    private JLabel lblTotal;
    private JLabel lblPoints;
    private JButton btnCheckout;
    private JButton btnCetak;
    private JTextArea txtStruk;

    private DefaultTableModel modelProduk;
    private DefaultTableModel modelCart;

    private final NumberFormat rupiah =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    public POSFrame() {
        super("POIN Off-Sales - Java Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        setJMenuBar(createMenuBar());
        setContentPane(buildContent());

        // Event handling
        btnAddToCart.addActionListener(e -> onAddToCart());
        btnCheckout.addActionListener(e -> onCheckout());
        btnCetak.addActionListener(e -> onCetak());
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.add(new JMenu("File"));
        bar.add(new JMenu("Help"));
        return bar;
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildProdukPanel(), buildRightPanel());
        split.setResizeWeight(0.4);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    // ===================== KIRI (Produk) =====================
    private JPanel buildProdukPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Produk"));

        modelProduk = new DefaultTableModel(new String[]{"ID","Nama Produk","Harga (Rp)"}, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
            @Override public Class<?> getColumnClass(int c){return c==2?Integer.class:String.class;}
        };
        // data contoh
        modelProduk.addRow(new Object[]{"P001","Air Mineral 600ml",3000});
        modelProduk.addRow(new Object[]{"P002","Kopi Sachet",5000});
        modelProduk.addRow(new Object[]{"P003","Roti isi",8000});
        modelProduk.addRow(new Object[]{"P004","Snack Keripik",6000});
        modelProduk.addRow(new Object[]{"P005","Minuman Botol",12000});

        tblProduk = new JTable(modelProduk);
        tblProduk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProduk.setFillsViewportHeight(true);
        panel.add(new JScrollPane(tblProduk), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(6, 6));
        bottom.add(new JLabel("Pilih produk dari tabel di kiri"), BorderLayout.NORTH);

        JPanel action = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        action.add(new JLabel("Qty:"));
        spQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        ((JSpinner.DefaultEditor) spQty.getEditor()).getTextField().setColumns(3);
        action.add(spQty);

        btnAddToCart = new JButton("Add to Cart");
        action.add(btnAddToCart);
        bottom.add(action, BorderLayout.SOUTH);

        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ===================== KANAN =====================
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(buildKeranjangPanel(), BorderLayout.CENTER);
        panel.add(buildStrukPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JComponent buildKeranjangPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(6, 6));
        wrapper.setBorder(BorderFactory.createTitledBorder("Keranjang"));

        modelCart = new DefaultTableModel(new String[]{"ID","Nama Produk","Qty","Harga","Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
            @Override public Class<?> getColumnClass(int c){
                return (c>=2)?Integer.class:String.class;
            }
        };
        JTable tblKeranjang = new JTable(modelCart);
        tblKeranjang.setFillsViewportHeight(true);
        wrapper.add(new JScrollPane(tblKeranjang), BorderLayout.CENTER);

        wrapper.add(buildSummaryPanel(), BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildSummaryPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        lblTotal = new JLabel("Total: " + rupiah.format(0));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        lblPoints = new JLabel("Points: 0");

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        line1.add(lblTotal);
        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        line2.add(lblPoints);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnCheckout = new JButton("Checkout");
        btnCetak = new JButton("Cetak");
        buttons.add(btnCheckout);
        buttons.add(btnCetak);

        p.add(line1);
        p.add(line2);
        p.add(buttons);
        return p;
    }

    private JPanel buildStrukPanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createTitledBorder("Struk:"));
        txtStruk = new JTextArea(6, 30);
        txtStruk.setEditable(false);
        // penting untuk print rapi:
        txtStruk.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtStruk.setLineWrap(false);      // jangan bungkus baris, supaya kolom sejajar
        txtStruk.setWrapStyleWord(false); // pastikan tidak memotong kata
        p.add(new JScrollPane(txtStruk), BorderLayout.CENTER);
        return p;
    }

    // ===================== EVENT LOGIC =====================
    private void onAddToCart() {
        int selected = tblProduk.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dulu produk di tabel kiri.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = String.valueOf(modelProduk.getValueAt(selected, 0));
        String name = String.valueOf(modelProduk.getValueAt(selected, 1));
        int price = ((Number) modelProduk.getValueAt(selected, 2)).intValue();
        int qty = (Integer) spQty.getValue();

        // jika produk sudah ada di keranjang -> gabung qty
        for (int i = 0; i < modelCart.getRowCount(); i++) {
            if (id.equals(modelCart.getValueAt(i, 0))) {
                int oldQty = ((Number) modelCart.getValueAt(i, 2)).intValue();
                int newQty = oldQty + qty;
                modelCart.setValueAt(newQty, i, 2);
                modelCart.setValueAt(price, i, 3);
                modelCart.setValueAt(price * newQty, i, 4);
                updateTotal();
                return;
            }
        }

        // belum ada tambah baris baru
        modelCart.addRow(new Object[]{id, name, qty, price, price * qty});
        updateTotal();
    }

    private void onCheckout() {
        int total = getTotal();
        int points = total / 1000; // 1 poin per Rp1.000
        lblPoints.setText("Points: " + points);
        JOptionPane.showMessageDialog(this,
                "Checkout berhasil.\nTotal: " + rupiah.format(total) + "\nPoin didapat: " + points,
                "Checkout", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onCetak() {
        if (modelCart.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // susun struk ke txtStruk
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEPARATOR);
        sb.append("               STRUK BELANJA                \n");
        sb.append(LINE_SEPARATOR);
        sb.append("Waktu : ")
          .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
          .append("\n\n");
        

        // header tabel (lebar kolom diatur agar sejajar)
        sb.append(String.format("%-6s %-20s %5s %12s %15s%n",
                "ID","Produk","Qty","Harga","Subtotal"));
        sb.append("-------------------------------------------------------------\n");

        // isi tabel
        for (int i = 0; i < modelCart.getRowCount(); i++) {
            String id = String.valueOf(modelCart.getValueAt(i, 0));
            String name = String.valueOf(modelCart.getValueAt(i, 1));
            int qty = ((Number) modelCart.getValueAt(i, 2)).intValue();
            int price = ((Number) modelCart.getValueAt(i, 3)).intValue();
            int sub = ((Number) modelCart.getValueAt(i, 4)).intValue();

            sb.append(String.format("%-6s %-20s %5d %12s %15s%n",
                    id, trimName(name, 20), qty,
                    rupiah.format(price), rupiah.format(sub)));
        }

        sb.append("-------------------------------------------------------------\n");
        int total = getTotal();
        int points = total / 1000; // 1 poin per Rp1.000
        sb.append(String.format("%-34s %15s%n", "TOTAL:", rupiah.format(total)));
        sb.append(String.format("POINTS DIDAPAT : %d (1 point per Rp 1000)%n", points));
        sb.append(LINE_SEPARATOR);
        sb.append("Terima kasih atas kunjungan Anda!\n");
        sb.append(LINE_SEPARATOR);

        txtStruk.setText(sb.toString());

        // ==== TAMPILKAN PRINT DIALOG & CETAK KE PDF ====
        try {
            boolean sukses = txtStruk.print(null, null, true, null, null, true);
            if (sukses) {
                // setelah tercetak, kosongkan keranjang & reset total/poin
                modelCart.setRowCount(0);
                updateTotal();
                lblPoints.setText("Points: 0");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mencetak: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getTotal() {
        int total = 0;
        for (int i = 0; i < modelCart.getRowCount(); i++) {
            total += ((Number) modelCart.getValueAt(i, 4)).intValue();
        }
        return total;
    }

    private void updateTotal() {
        lblTotal.setText("Total: " + rupiah.format(getTotal()));
    }

    private String trimName(String name, int max) {
        return (name.length() <= max) ? name : name.substring(0, max-1);
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ex) {
            ex.printStackTrace();
        }         
        SwingUtilities.invokeLater(() -> new POSFrame().setVisible(true));
    }
}
