package view;

import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Entrada (cadastro) de estoque: seleciona a peça, informa a quantidade e
 * registra a entrada — incrementando o estoque e o histórico de movimentações.
 */
public class V_EntradaEstoque extends JPanel {

    private final OficinaController controller;

    private JComboBox<ItemPeca> cmb_Peca;
    private JTextField txt_Quantidade;
    private JTextField txt_Observacao;
    private DefaultTableModel mdl_Estoque;
    private JTable tbl_Estoque;

    public V_EntradaEstoque(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        construirInterface();
        carregarPecas();
        carregarEstoque();
    }

    private void construirInterface() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(680, 560));

        JLabel lbl_Titulo = new JLabel("Estoque > Registrar Entrada");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        cmb_Peca = new JComboBox<>();
        cmb_Peca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmb_Peca.setBackground(Color.WHITE);

        txt_Quantidade = criarTextField();
        txt_Quantidade.setToolTipText("Ex: 10");
        ((AbstractDocument) txt_Quantidade.getDocument()).setDocumentFilter(new FiltroInteiro());

        txt_Observacao = criarTextField();
        txt_Observacao.setToolTipText("Ex: Compra no fornecedor X (opcional)");

        JPanel pnl_L1 = linha(60);
        pnl_L1.add(bloco("Peça *", cmb_Peca), BorderLayout.CENTER);
        JPanel pnl_QtdW = bloco("Quantidade *", txt_Quantidade);
        pnl_QtdW.setPreferredSize(new Dimension(140, 55));
        pnl_L1.add(pnl_QtdW, BorderLayout.EAST);

        JPanel pnl_L2 = linha(60);
        pnl_L2.add(bloco("Observação", txt_Observacao), BorderLayout.CENTER);

        form.add(pnl_L1);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L2);

        JButton btn_Registrar = new JButton("REGISTRAR ENTRADA");
        btn_Registrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Registrar.setForeground(Color.WHITE);
        btn_Registrar.setBackground(Color.decode("#28A745"));
        btn_Registrar.setPreferredSize(new Dimension(220, 42));
        btn_Registrar.setFocusPainted(false);
        btn_Registrar.setBorderPainted(false);
        btn_Registrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Registrar.addActionListener(e -> salvar());

        JButton btn_Voltar = new JButton("← Voltar");
        btn_Voltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Voltar.setForeground(Color.WHITE);
        btn_Voltar.setBackground(Color.decode("#6C757D"));
        btn_Voltar.setPreferredSize(new Dimension(110, 42));
        btn_Voltar.setFocusPainted(false);
        btn_Voltar.setBorderPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Voltar.addActionListener(e -> navegar(new V_Estoque(controller)));

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Voltar);
        pnl_Btn.add(btn_Registrar);

        JPanel pnl_Topo = new JPanel(new BorderLayout(0, 4));
        pnl_Topo.setOpaque(false);
        pnl_Topo.add(form, BorderLayout.CENTER);
        pnl_Topo.add(pnl_Btn, BorderLayout.SOUTH);

        // Tabela de estoque atual (referência rápida)
        String[] cols = {"Cód.", "Peça", "Sistema", "Qtd. atual"};
        mdl_Estoque = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Estoque = new JTable(mdl_Estoque);
        tbl_Estoque.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Estoque.setRowHeight(24);
        tbl_Estoque.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Estoque.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tbl_Estoque);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            "Estoque atual",
            0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.decode("#666666")));
        scroll.setPreferredSize(new Dimension(0, 220));
        ScrollBarPadrao.aplicar(scroll);

        card.add(lbl_Titulo, BorderLayout.NORTH);
        card.add(pnl_Topo, BorderLayout.CENTER);
        card.add(scroll, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 40, 20, 40);
        add(card, gbc);
    }

    private void salvar() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) {
            DialogoAlerta.aviso(this, "Selecione uma peça.", "Campo Inválido");
            return;
        }
        int qtd;
        try {
            qtd = Integer.parseInt(txt_Quantidade.getText().trim());
            if (qtd <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe uma quantidade válida maior que zero.", "Campo Inválido");
            return;
        }
        try {
            controller.registrarEntradaEstoque(sel.peca.getIdPeca(), qtd, txt_Observacao.getText().trim());
            DialogoAlerta.sucesso(this, "Entrada de " + qtd + " unidade(s) de \"" + sel.peca.getNomePopular()
                + "\" registrada com sucesso!", "Sucesso");
            txt_Quantidade.setText("");
            txt_Observacao.setText("");
            carregarPecas();
            carregarEstoque();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao registrar entrada: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void carregarPecas() {
        Object selecionado = cmb_Peca.getSelectedItem();
        cmb_Peca.removeAllItems();
        for (PecaEntity p : controller.listarEstoque()) cmb_Peca.addItem(new ItemPeca(p));
        if (selecionado instanceof ItemPeca ip && ip.peca != null) {
            for (int i = 0; i < cmb_Peca.getItemCount(); i++) {
                ItemPeca it = cmb_Peca.getItemAt(i);
                if (it.peca != null && it.peca.getIdPeca().equals(ip.peca.getIdPeca())) {
                    cmb_Peca.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void carregarEstoque() {
        mdl_Estoque.setRowCount(0);
        List<PecaEntity> pecas = controller.listarEstoque();
        for (PecaEntity p : pecas) {
            mdl_Estoque.addRow(new Object[]{
                String.format("%04d", p.getIdPeca()),
                p.getNomePopular(),
                p.getSistemaLabel(),
                p.getQuantidadeEstoque()
            });
        }
    }

    // ===== helpers =====
    private JPanel linha(int maxH) {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, maxH));
        return p;
    }

    private JPanel bloco(String rotulo, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(criarLabel(rotulo), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JLabel criarLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Color.decode("#333333"));
        return l;
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

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // ===== inner classes =====
    private static class ItemPeca {
        final PecaEntity peca;
        ItemPeca(PecaEntity p) { this.peca = p; }
        @Override public String toString() {
            if (peca == null) return "(sem peça)";
            return peca.getNomePopular() + "  (estoque: " + peca.getQuantidadeEstoque() + ")";
        }
    }

    private static class FiltroInteiro extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9]", ""), a);
        }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9]", ""), a);
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
            g2.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2.dispose();
        }
    }
}
