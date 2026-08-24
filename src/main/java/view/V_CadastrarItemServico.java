package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class V_CadastrarItemServico extends JPanel {

    private final OficinaController controller;
    private final List<CatalogoServicoEntity> itensCadastrados = new ArrayList<>();

    private JTextField txt_Nome;
    private JTextField txt_Valor;
    private JTextField txt_ValidadeKm;
    private JTextField txt_ValidadeMeses;
    private JRadioButton rdb_Padrao;
    private JRadioButton rdb_Revisao;
    private JPanel pnl_Validade;
    private JComboBox<String> cmb_Sistema;
    private JButton btn_Cadastrar;
    private DefaultTableModel mdl_Itens;
    private JTable tbl_Itens;

    // Peças associadas ao item
    private JComboBox<ItemPeca> cmb_Peca;
    private DefaultTableModel mdl_Pecas;
    private JTable tbl_Pecas;
    private final List<Long> idPecasSelecionadas = new ArrayList<>();

    private static final String[] SISTEMAS = {
        "MOTOR", "TRANSMISSAO", "DIRECAO", "SUSPENSAO", "FREIOS",
        "ARREFECIMENTO", "ELETRICA", "ALIMENTACAO", "OUTROS"
    };
    private static final String[] SISTEMAS_LABEL = {
        "Motor", "Transmissão", "Direção", "Suspensão", "Freios",
        "Arrefecimento", "Elétrica", "Alimentação", "Outros"
    };

    public V_CadastrarItemServico(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        construirInterface();
        carregarPecas();
        carregarLista();
    }

    private void construirInterface() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(680, 680));

        JLabel lbl_Titulo = new JLabel("Configurações > Itens de Serviço");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        txt_Nome  = criarTextField();
        txt_Valor = criarTextField();
        txt_Valor.setToolTipText("Ex: 120.00");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_ValidadeKm     = criarTextField();
        txt_ValidadeKm.setToolTipText("Ex: 5000");
        ((AbstractDocument) txt_ValidadeKm.getDocument()).setDocumentFilter(new FiltroInteiro());

        txt_ValidadeMeses  = criarTextField();
        txt_ValidadeMeses.setToolTipText("Ex: 6");
        ((AbstractDocument) txt_ValidadeMeses.getDocument()).setDocumentFilter(new FiltroInteiro());

        // Linha 1: Nome + Valor
        JPanel pnl_L1 = linha(Integer.MAX_VALUE, 60);
        JPanel pnl_ValorW = bloco("Valor (R$) *", txt_Valor);
        pnl_ValorW.setPreferredSize(new Dimension(130, 55));
        pnl_L1.add(bloco("Nome do Serviço *", txt_Nome), BorderLayout.CENTER);
        pnl_L1.add(pnl_ValorW, BorderLayout.EAST);

        // Linha 2: Tipo (Padrão / Revisão)
        JPanel pnl_L2 = linha(Integer.MAX_VALUE, 50);
        pnl_L2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 4));
        JLabel lbl_Manut = criarLabel("Tipo *");
        rdb_Padrao  = new JRadioButton("Padrão (com validade)");
        rdb_Revisao = new JRadioButton("Revisão (todos os carros)");
        rdb_Padrao.setOpaque(false); rdb_Revisao.setOpaque(false);
        rdb_Padrao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_Revisao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_Padrao.setSelected(true);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rdb_Padrao); grupo.add(rdb_Revisao);
        pnl_L2.add(lbl_Manut);
        pnl_L2.add(Box.createHorizontalStrut(10));
        pnl_L2.add(rdb_Padrao);
        pnl_L2.add(Box.createHorizontalStrut(16));
        pnl_L2.add(rdb_Revisao);

        // Linha 3: Sistema do veículo
        JPanel pnl_L3 = linha(Integer.MAX_VALUE, 60);

        cmb_Sistema = new JComboBox<>(SISTEMAS_LABEL);
        cmb_Sistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Sistema.setSelectedIndex(SISTEMAS_LABEL.length - 1); // Outros padrão

        pnl_L3.add(bloco("Sistema do Veículo *", cmb_Sistema));

        // Linha 4: Validade (visível apenas para Padrão)
        pnl_Validade = linha(Integer.MAX_VALUE, 60);
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

        // Botão
        btn_Cadastrar = new JButton("ADICIONAR ITEM");
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cadastrar.setForeground(Color.WHITE);
        btn_Cadastrar.setBackground(Color.decode("#FF9900"));
        btn_Cadastrar.setPreferredSize(new Dimension(200, 42));
        btn_Cadastrar.setFocusPainted(false);
        btn_Cadastrar.setBorderPainted(false);
        btn_Cadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Cadastrar.addActionListener(e -> salvar());

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Cadastrar);

        JPanel pnl_Topo = new JPanel(new BorderLayout(0, 4));
        pnl_Topo.setOpaque(false);
        pnl_Topo.add(form, BorderLayout.CENTER);
        pnl_Topo.add(pnl_Btn, BorderLayout.SOUTH);

        // Tabela de itens cadastrados
        String[] colunas = {"Nome", "Sistema", "Tipo", "Valor (R$)", "Val. KM", "Val. Meses"};
        mdl_Itens = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Itens = new JTable(mdl_Itens);
        tbl_Itens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Itens.setRowHeight(26);
        tbl_Itens.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Itens.getTableHeader().setReorderingAllowed(false);

        tbl_Itens.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editarSelecionado();
            }
        });

        JButton btn_Editar = new JButton("Editar selecionado");
        btn_Editar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_Editar.setForeground(Color.decode("#FF9900"));
        btn_Editar.setContentAreaFilled(false);
        btn_Editar.setBorderPainted(false);
        btn_Editar.setFocusPainted(false);
        btn_Editar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Editar.addActionListener(e -> editarSelecionado());

        JPanel pnl_ListaRodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        pnl_ListaRodape.setOpaque(false);
        pnl_ListaRodape.add(new JLabel("Duplo-clique ou: ") {{
            setFont(new Font("Segoe UI", Font.ITALIC, 11));
            setForeground(Color.decode("#999999"));
        }});
        pnl_ListaRodape.add(btn_Editar);

        JPanel pnl_Lista = new JPanel(new BorderLayout(0, 4));
        pnl_Lista.setOpaque(false);

        JScrollPane scroll = new JScrollPane(tbl_Itens);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            "Itens de Serviço cadastrados",
            0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.decode("#666666")));
        scroll.setPreferredSize(new Dimension(0, 160));
        ScrollBarPadrao.aplicar(scroll);
        pnl_Lista.add(scroll, BorderLayout.CENTER);
        pnl_Lista.add(pnl_ListaRodape, BorderLayout.SOUTH);

        card.add(lbl_Titulo, BorderLayout.NORTH);
        card.add(pnl_Topo, BorderLayout.CENTER);
        card.add(pnl_Lista, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 40, 20, 40);
        add(card, gbc);
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel lbl = criarLabel("Peças Associadas (auto-atribuídas ao Orçamento)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cmb_Peca = new JComboBox<>();
        cmb_Peca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Peca.setBackground(Color.WHITE);

        JButton btn_AddPeca = new JButton("+ Adicionar Peça");
        btn_AddPeca.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_AddPeca.setForeground(Color.WHITE);
        btn_AddPeca.setBackground(Color.decode("#17A2B8"));
        btn_AddPeca.setPreferredSize(new Dimension(160, 32));
        btn_AddPeca.setFocusPainted(false);
        btn_AddPeca.setBorderPainted(false);
        btn_AddPeca.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_AddPeca.addActionListener(e -> adicionarPeca());

        JPanel pnl_Row = new JPanel(new BorderLayout(8, 0));
        pnl_Row.setOpaque(false);
        pnl_Row.add(cmb_Peca, BorderLayout.CENTER);
        pnl_Row.add(btn_AddPeca, BorderLayout.EAST);

        String[] cols = {"Peça"};
        mdl_Pecas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Pecas = new JTable(mdl_Pecas);
        tbl_Pecas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Pecas.setRowHeight(24);

        JButton btn_RemPeca = new JButton("Remover selecionada");
        btn_RemPeca.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_RemPeca.setForeground(Color.decode("#DC3545"));
        btn_RemPeca.setContentAreaFilled(false);
        btn_RemPeca.setBorderPainted(false);
        btn_RemPeca.setFocusPainted(false);
        btn_RemPeca.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_RemPeca.addActionListener(e -> {
            int row = tbl_Pecas.getSelectedRow();
            if (row >= 0) {
                idPecasSelecionadas.remove(row);
                mdl_Pecas.removeRow(row);
            }
        });

        JScrollPane scrollPecas = new JScrollPane(tbl_Pecas);
        scrollPecas.setPreferredSize(new Dimension(0, 70));
        scrollPecas.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        ScrollBarPadrao.aplicar(scrollPecas);

        JLabel lbl_Hint = new JLabel("Ao adicionar este serviço a um orçamento, as peças aqui listadas serão incluídas automaticamente.");
        lbl_Hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_Hint.setForeground(Color.decode("#888888"));

        JPanel corpo = new JPanel(new BorderLayout(0, 4));
        corpo.setOpaque(false);
        corpo.add(pnl_Row, BorderLayout.NORTH);
        corpo.add(scrollPecas, BorderLayout.CENTER);
        corpo.add(btn_RemPeca, BorderLayout.SOUTH);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(corpo, BorderLayout.CENTER);
        sec.add(lbl_Hint, BorderLayout.SOUTH);
        return sec;
    }

    private void adicionarPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        idPecasSelecionadas.add(sel.peca.getIdPeca());
        mdl_Pecas.addRow(new Object[]{sel.peca.getNomePopular()});
    }

    private void salvar() {
        String nome     = txt_Nome.getText().trim();
        String valorTxt = txt_Valor.getText().trim().replace(",", ".");
        String tipo     = rdb_Revisao.isSelected() ? "REVISAO" : "PADRAO";
        String sistema  = SISTEMAS[cmb_Sistema.getSelectedIndex()];

        if (nome.length() < 3) {
            DialogoAlerta.aviso(this, "O nome deve ter pelo menos 3 caracteres.", "Campo Inválido"); return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorTxt);
            if (valor < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe um valor válido (ex: 120.00).", "Campo Inválido"); return;
        }

        Integer validadeKm = null, validadeMeses = null;
        if ("PADRAO".equals(tipo)) {
            String kmTxt  = txt_ValidadeKm.getText().trim();
            String mesTxt = txt_ValidadeMeses.getText().trim();
            if (!kmTxt.isEmpty())  { try { validadeKm    = Integer.parseInt(kmTxt);  } catch (NumberFormatException ignored) {} }
            if (!mesTxt.isEmpty()) { try { validadeMeses = Integer.parseInt(mesTxt); } catch (NumberFormatException ignored) {} }
        }

        try {
            controller.salvarItemServico(nome, "", valor, tipo, sistema,
                validadeKm, validadeMeses, new ArrayList<>(idPecasSelecionadas));
            DialogoAlerta.sucesso(this, "Item \"" + nome + "\" cadastrado com sucesso!", "Sucesso");
            txt_Nome.setText(""); txt_Valor.setText("");
            txt_ValidadeKm.setText(""); txt_ValidadeMeses.setText("");
            idPecasSelecionadas.clear();
            mdl_Pecas.setRowCount(0);
            rdb_Padrao.setSelected(true);
            pnl_Validade.setVisible(true);
            carregarLista();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao cadastrar item: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void carregarPecas() {
        cmb_Peca.removeAllItems();
        for (PecaEntity p : controller.listarTodasPecas())
            cmb_Peca.addItem(new ItemPeca(p));
    }

    private void editarSelecionado() {
        int row = tbl_Itens.getSelectedRow();
        if (row < 0 || row >= itensCadastrados.size()) {
            DialogoAlerta.aviso(this, "Selecione um item na lista para editar.", "Nenhum selecionado");
            return;
        }
        navegar(new V_EditarItemServico(controller, itensCadastrados.get(row)));
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    private void carregarLista() {
        mdl_Itens.setRowCount(0);
        itensCadastrados.clear();
        List<CatalogoServicoEntity> todos = controller.listarCatalogoServicos();
        itensCadastrados.addAll(todos);
        for (CatalogoServicoEntity i : todos) {
            String tipoLabel   = "REVISAO".equals(i.getTipo()) ? "Revisão" : "Padrão";
            String km   = i.getValidadeKm() != null ? i.getValidadeKm() + " km" : "—";
            String mes  = i.getValidadeMeses() != null ? i.getValidadeMeses() + " meses" : "—";
            mdl_Itens.addRow(new Object[]{
                i.getNome(), i.getSistemaLabel(), tipoLabel,
                String.format("R$ %.2f", i.getValor()), km, mes
            });
        }
    }

    // ===== helpers visuais =====
    private JPanel linha(int maxW, int maxH) {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(maxW, maxH));
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

    // ===== inner classes =====
    private static class ItemPeca {
        final PecaEntity peca;
        ItemPeca(PecaEntity p) { this.peca = p; }
        @Override public String toString() { return peca != null ? peca.getNomePopular() : "(sem peça)"; }
    }

    private static class FiltroDecimal extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9.,]", ""), a);
        }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9.,]", ""), a);
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
