package view;

import br.com.oficina.estoque.PecaEntity;
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

public class V_EditarPeca extends JPanel {

    private final OficinaController controller;
    private final PecaEntity peca;

    private JTextField txt_NomePopular, txt_VidaTempo, txt_VidaKm;
    private JComboBox<SistemaItem> cmb_Sistema;
    private JButton btn_Salvar;

    public V_EditarPeca(OficinaController controller, PecaEntity peca) {
        this.controller = controller;
        this.peca = peca;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
        preencherDados();
        vincularAcoes();
    }

    private void initComponents() {
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(480, 440));

        JLabel titulo = new JLabel("Peças > Editar Peça");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 12));
        form.setBackground(Color.WHITE);

        txt_NomePopular = criarTextField();
        txt_VidaTempo   = criarTextField();
        txt_VidaTempo.setToolTipText("Ex: 12 meses, 2 anos");
        txt_VidaKm      = criarTextField();
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

        form.add(criarBloco("Nome da Peça *", txt_NomePopular));
        form.add(criarBloco("Sistema do Veículo *", cmb_Sistema));
        form.add(criarBloco("Vida Útil (Tempo)", txt_VidaTempo));
        form.add(criarBloco("Vida Útil (KM)", txt_VidaKm));

        btn_Salvar = new JButton("SALVAR ALTERAÇÕES");
        btn_Salvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Salvar.setForeground(Color.WHITE);
        btn_Salvar.setBackground(Color.decode("#FF9900"));
        btn_Salvar.setPreferredSize(new Dimension(220, 45));
        btn_Salvar.setFocusPainted(false);
        btn_Salvar.setBorderPainted(false);
        btn_Salvar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btn_Cancelar = new JButton("← Cancelar");
        btn_Cancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Cancelar.setForeground(Color.decode("#4D4D4D"));
        btn_Cancelar.setContentAreaFilled(false);
        btn_Cancelar.setBorderPainted(false);
        btn_Cancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Cancelar.addActionListener(e -> navegar(new V_VisualizarPecas(controller)));

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Cancelar);
        pnl_Btn.add(btn_Salvar);

        card.add(titulo, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(pnl_Btn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);
    }

    private void preencherDados() {
        txt_NomePopular.setText(peca.getNomePopular() != null ? peca.getNomePopular() : "");
        String tempo = peca.getVidaUtilTempo();
        txt_VidaTempo.setText("Não informado".equals(tempo) || tempo == null ? "" : tempo);
        String km = peca.getVidaUtilKm();
        txt_VidaKm.setText("Não informado".equals(km) || km == null ? "" : km);
        String sistemaAtual = peca.getSistema();
        for (int i = 0; i < cmb_Sistema.getItemCount(); i++) {
            if (cmb_Sistema.getItemAt(i).codigo.equals(sistemaAtual)) {
                cmb_Sistema.setSelectedIndex(i);
                break;
            }
        }
    }

    private void vincularAcoes() {
        btn_Salvar.addActionListener(e -> {
            limparErro(txt_NomePopular);
            String nome = txt_NomePopular.getText().trim();
            if (nome.length() < 2) {
                marcarErro(txt_NomePopular);
                DialogoAlerta.aviso(this, "Nome da peça deve ter pelo menos 2 caracteres.", "Campo Inválido");
                return;
            }
            String tempo = txt_VidaTempo.getText().trim();
            String km    = txt_VidaKm.getText().trim();
            SistemaItem sistema = (SistemaItem) cmb_Sistema.getSelectedItem();
            try {
                controller.atualizarPeca(peca.getIdPeca(),
                    nome,
                    tempo.isEmpty() ? "Não informado" : tempo,
                    km.isEmpty()    ? "Não informado" : km,
                    sistema != null ? sistema.codigo : "OUTROS");
                DialogoAlerta.sucesso(this, "Peça \"" + nome + "\" atualizada com sucesso!", "Sucesso");
                navegar(new V_VisualizarPecas(controller));
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao salvar: " + ex.getMessage(), "Erro no Sistema");
            }
        });
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
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
        RoundedBorder(int r, Color c) { this.raio = r; this.cor = c; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.draw(new RoundRectangle2D.Double(x, y, w - 1, h - 1, raio, raio));
            g2.dispose();
        }
    }
}
