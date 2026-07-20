package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class V_EditarItemServico extends JPanel {

    private final OficinaController controller;
    private final CatalogoServicoEntity item;

    // Campos do formulário
    private JTextField txt_Nome;
    private JTextField txt_Valor;
    private JTextField txt_ValidadeKm;
    private JTextField txt_ValidadeMeses;
    private JRadioButton rdb_Padrao;
    private JRadioButton rdb_Revisao;
    private JPanel pnl_Validade;
    private JComboBox<String> cmb_Sistema;

    // Peças associadas
    private JComboBox<ItemPeca> cmb_NovaPeca;
    private DefaultTableModel mdl_Pecas;
    private JTable tbl_Pecas;
    private JLabel lbl_StatusOrcamentos;

    // Links {idLink → idPeca} para remoção
    private final java.util.Map<Long, Long> linksAtuais = new java.util.LinkedHashMap<>();

    private static final String[] SISTEMAS = {
        "MOTOR", "TRANSMISSAO", "DIRECAO", "SUSPENSAO", "FREIOS",
        "ARREFECIMENTO", "ELETRICA", "ALIMENTACAO", "OUTROS"
    };
    private static final String[] SISTEMAS_LABEL = {
        "Motor", "Transmissão", "Direção", "Suspensão", "Freios",
        "Arrefecimento", "Elétrica", "Alimentação", "Outros"
    };

    public V_EditarItemServico(OficinaController controller, CatalogoServicoEntity item) {
        this.controller = controller;
        this.item = item;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        construirInterface();
        preencherFormulario();
        carregarPecasDisponiveis();
        carregarPecasAssociadas();
    }

    private void construirInterface() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(680, 720));

        JLabel titulo = new JLabel("Configurações > Itens de Serviço > Editar");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        // Campos
        txt_Nome  = criarTextField();
        txt_Valor = criarTextField();
        txt_Valor.setToolTipText("Ex: 120.00");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_ValidadeKm    = criarTextField();
        txt_ValidadeMeses = criarTextField();
        ((AbstractDocument) txt_ValidadeKm.getDocument()).setDocumentFilter(new FiltroInteiro());
        ((AbstractDocument) txt_ValidadeMeses.getDocument()).setDocumentFilter(new FiltroInteiro());

        // Linha 1: Nome + Valor
        JPanel pnl_L1 = linha(60);
        JPanel pnl_ValW = bloco("Valor (R$) *", txt_Valor);
        pnl_ValW.setPreferredSize(new Dimension(130, 55));
        pnl_L1.add(bloco("Nome do Serviço *", txt_Nome), BorderLayout.CENTER);
        pnl_L1.add(pnl_ValW, BorderLayout.EAST);

        // Linha 2: Tipo Padrão / Revisão
        JPanel pnl_L2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        pnl_L2.setOpaque(false);
        pnl_L2.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_L2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        rdb_Padrao  = new JRadioButton("Padrão (com validade)");
        rdb_Revisao = new JRadioButton("Revisão (todos os carros)");
        rdb_Padrao.setOpaque(false); rdb_Revisao.setOpaque(false);
        rdb_Padrao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_Revisao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ButtonGroup grp = new ButtonGroup(); grp.add(rdb_Padrao); grp.add(rdb_Revisao);
        pnl_L2.add(criarLabel("Tipo *"));
        pnl_L2.add(Box.createHorizontalStrut(10));
        pnl_L2.add(rdb_Padrao);
        pnl_L2.add(Box.createHorizontalStrut(16));
        pnl_L2.add(rdb_Revisao);

        // Linha 3: Sistema do veículo
        JPanel pnl_L3 = linha(60);
        cmb_Sistema = new JComboBox<>(SISTEMAS_LABEL);
        cmb_Sistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnl_L3.add(bloco("Sistema do Veículo *", cmb_Sistema));

        // Linha 4: Validade
        pnl_Validade = linha(60);
        pnl_Validade.setLayout(new GridLayout(1, 2, 16, 0));
        pnl_Validade.add(bloco("Validade (KM)", txt_ValidadeKm));
        pnl_Validade.add(bloco("Validade (meses)", txt_ValidadeMeses));
        rdb_Padrao.addActionListener(e  -> pnl_Validade.setVisible(true));
        rdb_Revisao.addActionListener(e -> pnl_Validade.setVisible(false));

        form.add(pnl_L1);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L2);
        form.add(Box.createVerticalStrut(8));
        form.add(pnl_L3);
        form.add(Box.createVerticalStrut(8));
        form.add(pnl_Validade);
        form.add(Box.createVerticalStrut(14));
        form.add(criarSecaoPecas());

        // Botões
        JButton btn_Salvar = new JButton("SALVAR ALTERAÇÕES");
        btn_Salvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Salvar.setForeground(Color.WHITE);
        btn_Salvar.setBackground(Color.decode("#FF9900"));
        btn_Salvar.setPreferredSize(new Dimension(220, 42));
        btn_Salvar.setFocusPainted(false);
        btn_Salvar.setBorderPainted(false);
        btn_Salvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Salvar.addActionListener(e -> salvar());

        JButton btn_Voltar = new JButton("Voltar");
        btn_Voltar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn_Voltar.setForeground(Color.decode("#6C757D"));
        btn_Voltar.setContentAreaFilled(false);
        btn_Voltar.setBorderPainted(false);
        btn_Voltar.setFocusPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Voltar.addActionListener(e -> navegar(new V_CadastrarItemServico(controller)));

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Voltar);
        pnl_Btn.add(btn_Salvar);

        card.add(titulo, BorderLayout.NORTH);
        card.add(new JScrollPane(form) {{
            setBorder(null); setOpaque(false); getViewport().setOpaque(false);
            getVerticalScrollBar().setUnitIncrement(12);
        }}, BorderLayout.CENTER);
        card.add(pnl_Btn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 40, 20, 40);
        add(card, gbc);
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 8));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel lbl = criarLabel("Peças Associadas");

        // Aviso de propagação
        lbl_StatusOrcamentos = new JLabel();
        lbl_StatusOrcamentos.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_StatusOrcamentos.setForeground(Color.decode("#888888"));

        JPanel pnl_Header = new JPanel(new BorderLayout());
        pnl_Header.setOpaque(false);
        pnl_Header.add(lbl, BorderLayout.WEST);
        pnl_Header.add(lbl_StatusOrcamentos, BorderLayout.EAST);

        cmb_NovaPeca = new JComboBox<>();
        cmb_NovaPeca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_NovaPeca.setBackground(Color.WHITE);

        JButton btn_Add = botaoAcao("+ Adicionar", "#17A2B8");
        btn_Add.addActionListener(e -> adicionarPeca());

        JPanel pnl_Row = new JPanel(new BorderLayout(8, 0));
        pnl_Row.setOpaque(false);
        pnl_Row.add(cmb_NovaPeca, BorderLayout.CENTER);
        pnl_Row.add(btn_Add, BorderLayout.EAST);

        JLabel lbl_Hint = new JLabel("Ao adicionar, a peça é propagada automaticamente para os orçamentos que usam este serviço.");
        lbl_Hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_Hint.setForeground(Color.decode("#FF9900"));

        String[] cols = {"Peça", "Ação"};
        mdl_Pecas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public int getColumnCount() { return 1; } // só mostra "Peça"
        };
        tbl_Pecas = new JTable(mdl_Pecas);
        tbl_Pecas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Pecas.setRowHeight(26);
        tbl_Pecas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Pecas.getTableHeader().setReorderingAllowed(false);
        tbl_Pecas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btn_Rem = new JButton("Remover peça selecionada");
        btn_Rem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_Rem.setForeground(Color.decode("#DC3545"));
        btn_Rem.setContentAreaFilled(false);
        btn_Rem.setBorderPainted(false);
        btn_Rem.setFocusPainted(false);
        btn_Rem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Rem.addActionListener(e -> removerPecaSelecionada());

        tbl_Pecas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) removerPecaSelecionada();
            }
        });

        JScrollPane scrollPecas = new JScrollPane(tbl_Pecas);
        scrollPecas.setPreferredSize(new Dimension(0, 100));
        scrollPecas.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));

        JPanel corpo = new JPanel(new BorderLayout(0, 6));
        corpo.setOpaque(false);
        corpo.add(pnl_Row, BorderLayout.NORTH);
        corpo.add(scrollPecas, BorderLayout.CENTER);
        corpo.add(btn_Rem, BorderLayout.SOUTH);

        sec.add(pnl_Header, BorderLayout.NORTH);
        sec.add(lbl_Hint, BorderLayout.CENTER);
        sec.add(corpo, BorderLayout.SOUTH);
        return sec;
    }

    // =========================================================================

    private void preencherFormulario() {
        txt_Nome.setText(item.getNome());
        txt_Valor.setText(String.format("%.2f", item.getValor()));
        if ("REVISAO".equals(item.getTipo())) {
            rdb_Revisao.setSelected(true);
            pnl_Validade.setVisible(false);
        } else {
            rdb_Padrao.setSelected(true);
            pnl_Validade.setVisible(true);
        }
        if (item.getValidadeKm()    != null) txt_ValidadeKm.setText(String.valueOf(item.getValidadeKm()));
        if (item.getValidadeMeses() != null) txt_ValidadeMeses.setText(String.valueOf(item.getValidadeMeses()));

        String sistema = item.getSistema() != null ? item.getSistema() : "OUTROS";
        for (int i = 0; i < SISTEMAS.length; i++)
            if (SISTEMAS[i].equals(sistema)) { cmb_Sistema.setSelectedIndex(i); break; }
    }

    private void carregarPecasDisponiveis() {
        cmb_NovaPeca.removeAllItems();
        for (PecaEntity p : controller.listarTodasPecas())
            cmb_NovaPeca.addItem(new ItemPeca(p));
    }

    private void carregarPecasAssociadas() {
        linksAtuais.clear();
        mdl_Pecas.setRowCount(0);
        Map<Long, Long> links = controller.listarLinksPecasDoItemCatalogo(item.getIdCatalogoServico());
        linksAtuais.putAll(links);
        for (Map.Entry<Long, Long> entry : links.entrySet()) {
            PecaEntity p = controller.listarTodasPecas().stream()
                .filter(x -> x.getIdPeca().equals(entry.getValue())).findFirst().orElse(null);
            mdl_Pecas.addRow(new Object[]{p != null ? p.getNomePopular() : "(id " + entry.getValue() + ")"});
        }
        atualizarStatusOrcamentos();
    }

    private void adicionarPeca() {
        ItemPeca sel = (ItemPeca) cmb_NovaPeca.getSelectedItem();
        if (sel == null || sel.peca == null) {
            JOptionPane.showMessageDialog(this, "Cadastre peças antes de associá-las.",
                "Sem peças", JOptionPane.WARNING_MESSAGE); return;
        }
        // evitar duplicata na UI
        if (linksAtuais.containsValue(sel.peca.getIdPeca())) {
            JOptionPane.showMessageDialog(this, "Esta peça já está associada ao item.",
                "Duplicata", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            controller.adicionarPecaAItemCatalogo(item.getIdCatalogoServico(), sel.peca.getIdPeca());
            carregarPecasAssociadas();
            int qtdOrc = controller.contarOrcamentosComItemCatalogo(item.getIdCatalogoServico());
            String msg = "Peça adicionada!" + (qtdOrc > 0
                ? "\n" + qtdOrc + " orçamento(s) com este serviço foram atualizados automaticamente."
                : "");
            JOptionPane.showMessageDialog(this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerPecaSelecionada() {
        int row = tbl_Pecas.getSelectedRow();
        if (row < 0) return;
        List<Long> linkIds = new ArrayList<>(linksAtuais.keySet());
        if (row >= linkIds.size()) return;
        long idLink = linkIds.get(row);
        int conf = JOptionPane.showConfirmDialog(this,
            "Remover esta peça do item de serviço?\n(Orçamentos existentes não serão alterados.)",
            "Confirmar remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            controller.removerPecaDeItemCatalogo(idLink);
            carregarPecasAssociadas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvar() {
        String nome     = txt_Nome.getText().trim();
        String valorTxt = txt_Valor.getText().trim().replace(",", ".");
        String tipo     = rdb_Revisao.isSelected() ? "REVISAO" : "PADRAO";
        String sistema  = SISTEMAS[cmb_Sistema.getSelectedIndex()];

        if (nome.length() < 3) {
            JOptionPane.showMessageDialog(this, "O nome deve ter pelo menos 3 caracteres.",
                "Campo Inválido", JOptionPane.WARNING_MESSAGE); return;
        }
        double valor;
        try {
            valor = Double.parseDouble(valorTxt);
            if (valor < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um valor válido (ex: 120.00).",
                "Campo Inválido", JOptionPane.WARNING_MESSAGE); return;
        }

        Integer validadeKm = null, validadeMeses = null;
        if ("PADRAO".equals(tipo)) {
            String km  = txt_ValidadeKm.getText().trim();
            String mes = txt_ValidadeMeses.getText().trim();
            if (!km.isEmpty())  try { validadeKm    = Integer.parseInt(km);  } catch (NumberFormatException ignored) {}
            if (!mes.isEmpty()) try { validadeMeses = Integer.parseInt(mes); } catch (NumberFormatException ignored) {}
        }

        try {
            controller.atualizarItemServico(item.getIdCatalogoServico(), nome, "", valor,
                tipo, sistema, validadeKm, validadeMeses);
            JOptionPane.showMessageDialog(this,
                "Item \"" + nome + "\" atualizado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            navegar(new V_CadastrarItemServico(controller));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarStatusOrcamentos() {
        int qtd = controller.contarOrcamentosComItemCatalogo(item.getIdCatalogoServico());
        if (qtd == 0)
            lbl_StatusOrcamentos.setText("Nenhum orçamento usa este serviço ainda.");
        else
            lbl_StatusOrcamentos.setText(qtd + " orçamento(s) usam este serviço — peças adicionadas serão propagadas.");
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // ========= helpers visuais =========

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

    private JButton botaoAcao(String texto, String corHex) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode(corHex));
        btn.setPreferredSize(new Dimension(150, 34));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ========= inner classes =========

    private static class ItemPeca {
        final PecaEntity peca;
        ItemPeca(PecaEntity p) { this.peca = p; }
        @Override public String toString() { return peca != null ? peca.getNomePopular() : ""; }
    }

    private static class FiltroDecimal extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9.,]", ""), a); }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9.,]", ""), a); }
    }

    private static class FiltroInteiro extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9]", ""), a); }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9]", ""), a); }
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
