package view;

import controller.OficinaController;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class V_CadastrarPeca extends JPanel {

    private JTextField txt_NomePopular, txt_VidaTempo, txt_VidaKm;
    private JComboBox<SistemaItem> cmb_Sistema;
    private JButton btn_Cadastrar;

    private final OficinaController controller;

    public V_CadastrarPeca(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
        vincularAcoes();
    }

    private void initComponents() {
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(480, 420));

        JLabel titulo = new JLabel("Configurações > Cadastrar Peça");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 12));
        form.setBackground(Color.WHITE);

        txt_NomePopular = criarTextField();
        txt_NomePopular.setToolTipText("Ex: Filtro de óleo, Pastilha de freio");

        txt_VidaTempo = criarTextField();
        txt_VidaTempo.setToolTipText("Ex: 12 meses, 2 anos");

        txt_VidaKm = criarTextField();
        txt_VidaKm.setToolTipText("Ex: 30000 km");

        ((AbstractDocument) txt_NomePopular.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_VidaTempo.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_VidaKm.getDocument()).setDocumentFilter(new FiltroTexto());

        cmb_Sistema = new JComboBox<>();
        cmb_Sistema.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmb_Sistema.setBackground(Color.WHITE);
        cmb_Sistema.addItem(new SistemaItem("MOTOR",        "Motor"));
        cmb_Sistema.addItem(new SistemaItem("TRANSMISSAO",  "Transmissão"));
        cmb_Sistema.addItem(new SistemaItem("DIRECAO",      "Direção"));
        cmb_Sistema.addItem(new SistemaItem("SUSPENSAO",    "Suspensão"));
        cmb_Sistema.addItem(new SistemaItem("FREIOS",       "Freios"));
        cmb_Sistema.addItem(new SistemaItem("ARREFECIMENTO","Arrefecimento"));
        cmb_Sistema.addItem(new SistemaItem("ELETRICA",     "Elétrica"));
        cmb_Sistema.addItem(new SistemaItem("ALIMENTACAO",  "Alimentação"));
        cmb_Sistema.addItem(new SistemaItem("OUTROS",       "Outros"));

        JLabel lbl_Info = new JLabel("Peças genéricas — o modelo/aplicação específica é descrito no serviço.");
        lbl_Info.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbl_Info.setForeground(Color.decode("#888888"));

        form.add(criarBloco("Nome da Peça *", txt_NomePopular));
        form.add(criarBloco("Sistema do Veículo *", cmb_Sistema));
        form.add(criarBloco("Vida Útil (tempo)", txt_VidaTempo));
        form.add(criarBloco("Vida Útil (km)", txt_VidaKm));

        btn_Cadastrar = new JButton("CADASTRAR PEÇA");
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cadastrar.setForeground(Color.WHITE);
        btn_Cadastrar.setBackground(Color.decode("#28A745"));
        btn_Cadastrar.setPreferredSize(new Dimension(220, 45));
        btn_Cadastrar.setFocusPainted(false);
        btn_Cadastrar.setBorderPainted(false);
        btn_Cadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel pnl_Sul = new JPanel(new BorderLayout(0, 8));
        pnl_Sul.setOpaque(false);
        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Cadastrar);
        pnl_Sul.add(lbl_Info, BorderLayout.CENTER);
        pnl_Sul.add(pnl_Btn, BorderLayout.SOUTH);

        card.add(titulo, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(pnl_Sul, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);
    }

    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            limparErro(txt_NomePopular);
            String nomePopular = txt_NomePopular.getText().trim();
            if (nomePopular.length() < 2) {
                marcarErro(txt_NomePopular);
                JOptionPane.showMessageDialog(this,
                    "Nome da peça deve ter pelo menos 2 caracteres.",
                    "Campo Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String vidaTempo = txt_VidaTempo.getText().trim();
            String vidaKm    = txt_VidaKm.getText().trim();
            SistemaItem sistema = (SistemaItem) cmb_Sistema.getSelectedItem();
            try {
                controller.salvarPeca(nomePopular,
                    vidaTempo.isEmpty() ? "Não informado" : vidaTempo,
                    vidaKm.isEmpty()    ? "Não informado" : vidaKm,
                    sistema != null ? sistema.codigo : "OUTROS");
                JOptionPane.showMessageDialog(this,
                    "Peça \"" + nomePopular + "\" cadastrada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txt_NomePopular.setText("");
                txt_VidaTempo.setText("");
                txt_VidaKm.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar peça: " + ex.getMessage(),
                    "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JPanel criarBloco(String rotulo, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(rotulo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Color.decode("#333333"));
        p.add(l, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private void marcarErro(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.RED),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { limparErro(f); f.removeFocusListener(this); }
        });
    }

    private void limparErro(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)));
    }

    private JTextField criarTextField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(100, 36));
        f.setBackground(Color.WHITE);
        f.setForeground(Color.BLACK);
        f.setCaretColor(Color.BLACK);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        return f;
    }

    private static class SistemaItem {
        final String codigo, label;
        SistemaItem(String c, String l) { this.codigo = c; this.label = l; }
        @Override public String toString() { return label; }
    }

    private static class FiltroTexto extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, off, filtrar(text), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, off, len, filtrar(text), attr);
        }
        private String filtrar(String t) {
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-./]", "");
        }
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio; private final Color cor;
        RoundedBorder(int raio, Color cor) { this.raio = raio; this.cor = cor; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(cor);
            g2d.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2d.dispose();
        }
    }
}
