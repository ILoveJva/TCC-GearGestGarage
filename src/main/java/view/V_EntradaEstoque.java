package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
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
 * Compra de peças: seleciona a peça, a quantidade e o valor pago e escolhe o destino —
 * ou entra no estoque geral (uso futuro), ou é usada direto numa OS específica quando a
 * peça não está disponível em estoque (o custo vira parte do orçamento daquela OS).
 */
public class V_EntradaEstoque extends JPanel {

    private final OficinaController controller;

    private JComboBox<ItemPeca> cmb_Peca;
    private JTextField txt_Quantidade;
    private JTextField txt_Valor;
    private JRadioButton rad_Estoque;
    private JRadioButton rad_OSDireto;
    private JComboBox<ItemOS> cmb_OS;
    private JTextField txt_NomeTecnico;
    private JTextField txt_Fabricante;
    private JTextField txt_Observacao;
    private JPanel pnl_OS;
    private JPanel pnl_TecFab;
    private DefaultTableModel mdl_Estoque;
    private JTable tbl_Estoque;

    public V_EntradaEstoque(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        construirInterface();
        carregarPecas();
        carregarOS();
        carregarEstoque();
    }

    private void construirInterface() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(700, 680));

        JLabel lbl_Titulo = new JLabel("Estoque > Compra de Peças");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        cmb_Peca = new JComboBox<>();
        cmb_Peca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmb_Peca.setBackground(Color.WHITE);
        cmb_Peca.addActionListener(e -> sugerirDestinoPelaPeca());

        txt_Quantidade = criarTextField();
        txt_Quantidade.setToolTipText("Ex: 10");
        ((AbstractDocument) txt_Quantidade.getDocument()).setDocumentFilter(new FiltroInteiro());

        txt_Valor = criarTextField();
        txt_Valor.setToolTipText("Ex: 45.90 (valor unitário pago pela peça)");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_Observacao = criarTextField();
        txt_Observacao.setToolTipText("Ex: Compra no fornecedor X (opcional)");

        JPanel pnl_L1 = linha(60);
        pnl_L1.add(bloco("Peça *", cmb_Peca), BorderLayout.CENTER);
        JPanel pnl_QtdW = bloco("Quantidade *", txt_Quantidade);
        pnl_QtdW.setPreferredSize(new Dimension(140, 55));
        pnl_L1.add(pnl_QtdW, BorderLayout.EAST);

        JPanel pnl_L2 = linha(60);
        JPanel pnl_ValorW = bloco("Valor unitário pago (R$)", txt_Valor);
        pnl_ValorW.setPreferredSize(new Dimension(200, 55));
        pnl_L2.add(pnl_ValorW, BorderLayout.WEST);

        // ---- Destino da compra ----
        rad_Estoque = new JRadioButton("Adicionar ao estoque", true);
        rad_OSDireto = new JRadioButton("Usar direto numa OS (peça fora do estoque)");
        estilizarRadio(rad_Estoque);
        estilizarRadio(rad_OSDireto);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rad_Estoque);
        grupo.add(rad_OSDireto);
        rad_Estoque.addActionListener(e -> atualizarVisibilidadeDestino());
        rad_OSDireto.addActionListener(e -> atualizarVisibilidadeDestino());

        JPanel pnl_Destino = new JPanel();
        pnl_Destino.setLayout(new BoxLayout(pnl_Destino, BoxLayout.Y_AXIS));
        pnl_Destino.setOpaque(false);
        pnl_Destino.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Destino.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel lbl_Destino = criarLabel("Destino da compra *");
        pnl_Destino.add(lbl_Destino);
        pnl_Destino.add(rad_Estoque);
        pnl_Destino.add(rad_OSDireto);

        cmb_OS = new JComboBox<>();
        cmb_OS.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmb_OS.setBackground(Color.WHITE);

        pnl_OS = linha(60);
        pnl_OS.add(bloco("OS de destino *", cmb_OS), BorderLayout.CENTER);

        txt_NomeTecnico = criarTextField();
        txt_NomeTecnico.setToolTipText("Ex: Filtro Mann W811/80 (opcional)");
        txt_Fabricante = criarTextField();
        txt_Fabricante.setToolTipText("Ex: Mann, Bosch, NGK (opcional)");

        pnl_TecFab = linha(60);
        pnl_TecFab.add(bloco("Nome Técnico (opcional)", txt_NomeTecnico), BorderLayout.CENTER);
        JPanel pnl_FabW = bloco("Fabricante (opcional)", txt_Fabricante);
        pnl_FabW.setPreferredSize(new Dimension(200, 55));
        pnl_TecFab.add(pnl_FabW, BorderLayout.EAST);

        JPanel pnl_L5 = linha(60);
        pnl_L5.add(bloco("Observação", txt_Observacao), BorderLayout.CENTER);

        form.add(pnl_L1);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L2);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_Destino);
        form.add(Box.createVerticalStrut(4));
        form.add(pnl_OS);
        form.add(Box.createVerticalStrut(4));
        form.add(pnl_TecFab);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L5);

        JButton btn_Registrar = new JButton("REGISTRAR COMPRA");
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
        scroll.setPreferredSize(new Dimension(0, 200));
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

        atualizarVisibilidadeDestino();
    }

    private void atualizarVisibilidadeDestino() {
        boolean direto = rad_OSDireto.isSelected();
        pnl_OS.setVisible(direto);
        pnl_TecFab.setVisible(direto);
        revalidate();
        repaint();
    }

    /** Sugere o destino conforme o estoque atual da peça selecionada, sem travar a escolha do usuário. */
    private void sugerirDestinoPelaPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        if (sel.peca.getQuantidadeEstoque() <= 0 && cmb_OS.getItemCount() > 0) {
            rad_OSDireto.setSelected(true);
        } else {
            rad_Estoque.setSelected(true);
        }
        atualizarVisibilidadeDestino();
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

        Double valorUnitario = null;
        String valorTxt = txt_Valor.getText().trim().replace(",", ".");
        if (!valorTxt.isEmpty()) {
            try {
                valorUnitario = Double.parseDouble(valorTxt);
                if (valorUnitario < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                DialogoAlerta.aviso(this, "Informe um valor válido (ex: 45.90).", "Campo Inválido");
                return;
            }
        }

        String observacao = txt_Observacao.getText().trim();

        if (rad_OSDireto.isSelected()) {
            ItemOS osSel = (ItemOS) cmb_OS.getSelectedItem();
            if (osSel == null || osSel.dto == null) {
                DialogoAlerta.aviso(this, "Selecione a OS de destino.", "Campo Inválido");
                return;
            }
            if (valorUnitario == null || valorUnitario <= 0) {
                DialogoAlerta.aviso(this, "Informe o valor unitário cobrado pela peça nessa OS.", "Campo Inválido");
                return;
            }
            try {
                controller.adicionarPecaDiretoOS(osSel.dto.idOrcamento(), sel.peca.getIdPeca(), qtd,
                    txt_NomeTecnico.getText().trim(), txt_Fabricante.getText().trim(), valorUnitario);
                DialogoAlerta.sucesso(this, qtd + " unidade(s) de \"" + sel.peca.getNomePopular()
                    + "\" adicionada(s) direto à " + osSel + " (sem passar pelo estoque).", "Sucesso");
                limparCampos();
                carregarPecas();
                carregarOS();
                carregarEstoque();
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao registrar compra direta: " + ex.getMessage(), "Erro no Sistema");
            }
        } else {
            try {
                controller.registrarEntradaEstoque(sel.peca.getIdPeca(), qtd, valorUnitario, observacao);
                DialogoAlerta.sucesso(this, "Entrada de " + qtd + " unidade(s) de \"" + sel.peca.getNomePopular()
                    + "\" registrada no estoque com sucesso!", "Sucesso");
                limparCampos();
                carregarPecas();
                carregarOS();
                carregarEstoque();
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao registrar entrada: " + ex.getMessage(), "Erro no Sistema");
            }
        }
    }

    private void limparCampos() {
        txt_Quantidade.setText("");
        txt_Valor.setText("");
        txt_NomeTecnico.setText("");
        txt_Fabricante.setText("");
        txt_Observacao.setText("");
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

    private void carregarOS() {
        cmb_OS.removeAllItems();
        for (ServicoResponseDTO dto : controller.listarServicosEmAberto()) cmb_OS.addItem(new ItemOS(dto));
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

    private void estilizarRadio(JRadioButton r) {
        r.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        r.setForeground(Color.decode("#333333"));
        r.setOpaque(false);
        r.setCursor(new Cursor(Cursor.HAND_CURSOR));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    private static class ItemOS {
        final ServicoResponseDTO dto;
        ItemOS(ServicoResponseDTO dto) { this.dto = dto; }
        @Override public String toString() {
            if (dto == null) return "(sem OS)";
            String titulo = dto.titulo() != null && !dto.titulo().isBlank() ? dto.titulo() : "(sem título)";
            return "OS #" + dto.idServico() + " — " + titulo;
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

    private static class FiltroDecimal extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9.,]", ""), a);
        }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9.,]", ""), a);
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
