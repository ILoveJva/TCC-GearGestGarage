package view;

import br.com.oficina.estoque.CatalogoPecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Cadastro de item do catálogo de serviços (nome, valor, tipo, sistema, validade e peças associadas).
 *
 * Visual alinhado ao padrão em vidro (glassmorphism) usado em V_EditarOrcamento /
 * V_CadastrarOrcamento / V_VisualizarOrcamento — mesma paleta, mesmo cabeçalho Aero,
 * campos arredondados, tabela com cabeçalho em vidro e botões de vidro.
 * A listagem dos itens já cadastrados foi removida desta tela (fica na tela de consulta).
 */
public class V_CadastrarItemServico extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mesma linguagem visual das telas de orçamento
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_LABEL_SUTIL  = Color.decode("#8A94A3");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_LARANJA      = Color.decode("#FF9900");

    // Vidro cinza claro estilo Windows 7 (Aero) — cabeçalho das tabelas
    private static final Color COR_AERO_TOPO_A  = Color.decode("#FBFBFC");
    private static final Color COR_AERO_TOPO_B  = Color.decode("#ECEEF1");
    private static final Color COR_AERO_BASE_A  = Color.decode("#DADDE2");
    private static final Color COR_AERO_BASE_B  = Color.decode("#EFF1F3");
    private static final Color COR_AERO_BORDA   = Color.decode("#B6BCC4");
    private static final Color COR_AERO_SEPARA  = Color.decode("#CCD1D8");
    private static final Color COR_AERO_TEXTO   = Color.decode("#3A4149");

    // Tabela de dados
    private static final Color COR_TABELA_FUNDO   = Color.WHITE;
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");

    private static final int ALTURA_CABECALHO = 26;
    private static final int ALTURA_LINHA     = 26;

    private final OficinaController controller;

    private JTextField txt_Nome;
    private JTextField txt_Valor;
    private JTextField txt_ValidadeKm;
    private JTextField txt_ValidadeMeses;
    private JRadioButton rdb_Padrao;
    private JRadioButton rdb_Revisao;
    private JPanel pnl_Validade;
    private JComboBox<String> cmb_Sistema;
    private JButton btn_Cadastrar;

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
        setBackground(COR_FUNDO_PAGINA);
        construirInterface();
        carregarPecas();
    }

    // =========================================================================
    // LAYOUT
    // =========================================================================

    private void construirInterface() {
        JPanel card = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        card.setPreferredSize(new Dimension(680, 680));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // ---- cabeçalho em vidro Aero ----
        JPanel headerGlass = new CabecalhoVidro();
        headerGlass.setLayout(new BorderLayout());
        headerGlass.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        headerGlass.setPreferredSize(new Dimension(10, 42));
        headerGlass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel lbl_Titulo = new JLabel("Página Inicial > Serviços Cadastrados > Itens de Serviço");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(COR_AERO_TEXTO);
        headerGlass.add(lbl_Titulo, BorderLayout.WEST);

        // ---- formulário ----
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        txt_Nome = campoTexto();

        txt_Valor = campoTexto();
        txt_Valor.setToolTipText("Ex: 120.00");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_ValidadeKm = campoTexto();
        txt_ValidadeKm.setToolTipText("Ex: 5000");
        ((AbstractDocument) txt_ValidadeKm.getDocument()).setDocumentFilter(new FiltroInteiro());

        txt_ValidadeMeses = campoTexto();
        txt_ValidadeMeses.setToolTipText("Ex: 6");
        ((AbstractDocument) txt_ValidadeMeses.getDocument()).setDocumentFilter(new FiltroInteiro());

        // Linha 1: Nome + Valor
        JPanel pnl_L1 = linha(Integer.MAX_VALUE, 62);
        JPanel pnl_ValorW = bloco("Valor (R$) *", txt_Valor);
        pnl_ValorW.setPreferredSize(new Dimension(130, 58));
        pnl_L1.add(bloco("Nome do Serviço *", txt_Nome), BorderLayout.CENTER);
        pnl_L1.add(pnl_ValorW, BorderLayout.EAST);

        // Linha 2: Tipo (Padrão / Revisão)
        JPanel pnl_L2 = linha(Integer.MAX_VALUE, 40);
        pnl_L2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 4));
        JLabel lbl_Manut = rotulo("Tipo *");
        rdb_Padrao  = radio("Padrão (com validade)");
        rdb_Revisao = radio("Revisão (todos os carros)");
        rdb_Padrao.setSelected(true);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rdb_Padrao); grupo.add(rdb_Revisao);
        pnl_L2.add(lbl_Manut);
        pnl_L2.add(Box.createHorizontalStrut(10));
        pnl_L2.add(rdb_Padrao);
        pnl_L2.add(Box.createHorizontalStrut(16));
        pnl_L2.add(rdb_Revisao);

        // Linha 3: Sistema do veículo
        JPanel pnl_L3 = linha(Integer.MAX_VALUE, 62);
        cmb_Sistema = new JComboBox<>(SISTEMAS_LABEL);
        cmb_Sistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Sistema.setBackground(Color.WHITE);
        estilizarCombo(cmb_Sistema);
        cmb_Sistema.setPreferredSize(new Dimension(0, 34));
        cmb_Sistema.setSelectedIndex(SISTEMAS_LABEL.length - 1); // Outros padrão
        pnl_L3.add(bloco("Sistema do Veículo *", cmb_Sistema));

        // Linha 4: Validade (visível apenas para Padrão)
        pnl_Validade = linha(Integer.MAX_VALUE, 62);
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

        // ---- rodapé: Voltar + Cadastrar ----
        BotaoVidro btn_Voltar = botaoAcao("← Voltar", "#8A94A3", 130, 40);
        btn_Voltar.addActionListener(e -> navegar(new V_VisualizarCatalogoServicos(controller)));

        btn_Cadastrar = new BotaoVidro("+ CADASTRAR ITEM", COR_LARANJA);
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cadastrar.setPreferredSize(new Dimension(250, 45));
        btn_Cadastrar.addActionListener(e -> salvar());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btn_Voltar);
        pnlBtn.add(btn_Cadastrar);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scrollForm);

        card.add(headerGlass, BorderLayout.NORTH);
        card.add(scrollForm, BorderLayout.CENTER);
        card.add(pnlBtn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(card, gbc);
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel lbl = new JLabel("Peças Associadas (auto-atribuídas ao Orçamento)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COR_LABEL);

        cmb_Peca = new JComboBox<>();
        cmb_Peca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Peca.setBackground(Color.WHITE);
        estilizarCombo(cmb_Peca);
        cmb_Peca.setPreferredSize(new Dimension(0, 34));

        BotaoVidro btn_AddPeca = botaoAcao("+ Adicionar Peça", "#17A2B8", 160, 34);
        btn_AddPeca.addActionListener(e -> adicionarPeca());

        JPanel pnl_Row = new JPanel(new BorderLayout(8, 0));
        pnl_Row.setOpaque(false);
        pnl_Row.add(cmb_Peca, BorderLayout.CENTER);
        pnl_Row.add(btn_AddPeca, BorderLayout.EAST);

        String[] cols = {"Peça"};
        mdl_Pecas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Pecas = criarTabela(mdl_Pecas);

        JScrollPane scrollPecas = scrollTabela(tbl_Pecas, 100);

        BotaoVidro btn_RemPeca = botaoAcao("Remover selecionada", "#DC3545", 170, 30);
        btn_RemPeca.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_RemPeca.addActionListener(e -> {
            int row = tbl_Pecas.getSelectedRow();
            if (row >= 0) {
                idPecasSelecionadas.remove(row);
                mdl_Pecas.removeRow(row);
            }
        });

        JLabel lbl_Hint = new JLabel("Ao adicionar este serviço a um orçamento, as peças aqui listadas serão incluídas automaticamente.");
        lbl_Hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_Hint.setForeground(COR_LABEL_SUTIL);

        JPanel pnl_Rem = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_Rem.setOpaque(false);
        pnl_Rem.add(btn_RemPeca);

        JPanel corpo = new JPanel(new BorderLayout(0, 6));
        corpo.setOpaque(false);
        corpo.add(pnl_Row, BorderLayout.NORTH);
        corpo.add(scrollPecas, BorderLayout.CENTER);
        corpo.add(pnl_Rem, BorderLayout.SOUTH);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(corpo, BorderLayout.CENTER);
        sec.add(lbl_Hint, BorderLayout.SOUTH);
        return sec;
    }

    // =========================================================================
    // LÓGICA
    // =========================================================================

    private void adicionarPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        idPecasSelecionadas.add(sel.peca.getIdCatalogoPeca());
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
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao cadastrar item: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void carregarPecas() {
        cmb_Peca.removeAllItems();
        for (CatalogoPecaEntity p : controller.listarTodasPecas())
            cmb_Peca.addItem(new ItemPeca(p));
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // =========================================================================
    // HELPERS VISUAIS
    // =========================================================================

    private JPanel linha(int maxW, int maxH) {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(maxW, maxH));
        return p;
    }

    private JPanel bloco(String rotuloTexto, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(rotulo(rotuloTexto), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JLabel rotulo(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COR_LABEL);
        return l;
    }

    private JTextField campoTexto() {
        JTextField f = new CampoArredondado();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(100, 34));
        return f;
    }

    private JRadioButton radio(String texto) {
        JRadioButton r = new JRadioButton(texto);
        r.setOpaque(false);
        r.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        r.setForeground(COR_LABEL);
        r.setFocusPainted(false);
        r.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return r;
    }

    private BotaoVidro botaoAcao(String texto, String corHex, int w, int h) {
        BotaoVidro btn = new BotaoVidro(texto, Color.decode(corHex));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setPreferredSize(new Dimension(w, h));
        return btn;
    }

    /** Combo com fundo e borda arredondados (mesmo raio dos campos de texto). */
    private static void estilizarCombo(JComboBox<?> cmb) {
        cmb.setUI(new ComboArredondadoUI());
        cmb.setOpaque(false);
        cmb.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 4));
    }

    /** Monta a tabela com o mesmo vidro Aero cinza no cabeçalho e fundo branco nas células das demais telas. */
    private JTable criarTabela(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setForeground(COR_TEXTO_CAMPO);
        tbl.setBackground(COR_TABELA_FUNDO);
        tbl.setOpaque(true);
        tbl.setRowHeight(ALTURA_LINHA);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setSelectionBackground(COR_TABELA_SELECAO);
        tbl.setSelectionForeground(COR_TEXTO_CAMPO);
        tbl.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());

        JTableHeader header = tbl.getTableHeader();
        header.setDefaultRenderer(new CabecalhoVidroClaro());
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, ALTURA_CABECALHO));
        header.setReorderingAllowed(false);
        header.setOpaque(false);
        return tbl;
    }

    private JScrollPane scrollTabela(JTable tbl, int altura) {
        JScrollPane scp = new JScrollPane(tbl);
        scp.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        scp.setOpaque(false);
        scp.getViewport().setBackground(COR_TABELA_FUNDO);
        scp.getViewport().setOpaque(true);
        scp.setPreferredSize(new Dimension(0, altura));
        ScrollBarPadrao.aplicar(scp);
        return scp;
    }

    // =========================================================================
    // CLASSES INTERNAS
    // =========================================================================

    private static class ItemPeca {
        final CatalogoPecaEntity peca;
        ItemPeca(CatalogoPecaEntity p) { this.peca = p; }
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

    /** Painel com fundo em gradiente suave e cantos arredondados. */
    private static class PainelGradiente extends JPanel {
        private final Color corTopo;
        private final Color corBase;

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase) {
            super(layout);
            this.corTopo = corTopo;
            this.corBase = corBase;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, corTopo, 0, getHeight(), corBase));
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 14, 14));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Campo de texto com fundo e borda arredondados; borda laranja quando focado. */
    private static class CampoArredondado extends JTextField {
        private static final int RAIO = 14;
        private boolean focado = false;

        CampoArredondado() {
            setOpaque(false);
            setForeground(COR_TEXTO_CAMPO);
            setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e)   { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(focado ? COR_LARANJA : Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO));
            g2.dispose();
        }
    }

    /** UI de combo com cantos arredondados e seta simples. */
    private static class ComboArredondadoUI extends BasicComboBoxUI {
        private static final int RAIO = 14;

        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.decode("#666666"));
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                    g2.dispose();
                }
            };
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, c.getWidth() - 1, c.getHeight() - 1, RAIO, RAIO);
            g2.setColor(Color.WHITE);
            g2.fill(forma);
            g2.setColor(Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(forma);
            g2.dispose();
            super.paint(g, c);
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // fundo já pintado em paint(), com cantos arredondados
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false); // evita o retângulo azul de seleção
        }
    }

    /** Cabeçalho fino em vidro Aero, usado na faixa de título no topo do card. */
    private static class CabecalhoVidro extends JPanel {
        private static final Color TOPO_A = Color.decode("#FBFBFC");
        private static final Color TOPO_B = Color.decode("#ECEEF1");
        private static final Color BASE_A = Color.decode("#DADDE2");
        private static final Color BASE_B = Color.decode("#EFF1F3");
        private static final Color BORDA  = Color.decode("#B6BCC4");

        CabecalhoVidro() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int meio = h / 2;

            RoundRectangle2D corpo = new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14);
            Shape clipAnterior = g2.getClip();
            g2.clip(corpo);

            g2.setPaint(new GradientPaint(0, 0, TOPO_A, 0, meio, TOPO_B));
            g2.fillRect(0, 0, w, meio);
            g2.setPaint(new GradientPaint(0, meio, BASE_A, 0, h, BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 120));
            g2.fillRect(0, 0, w, Math.max(1, h / 4));

            g2.setClip(clipAnterior);

            g2.setColor(BORDA);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(corpo);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7), igual às demais telas. */
    private static class CabecalhoVidroClaro extends JLabel implements TableCellRenderer {

        CabecalhoVidroClaro() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(COR_AERO_TEXTO);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int meio = h / 2;

            g2.setPaint(new GradientPaint(0, 0, COR_AERO_TOPO_A, 0, meio, COR_AERO_TOPO_B));
            g2.fillRect(0, 0, w, meio);

            g2.setPaint(new GradientPaint(0, meio, COR_AERO_BASE_A, 0, h, COR_AERO_BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillRect(0, 0, w, Math.max(1, h / 5));

            g2.setColor(COR_AERO_SEPARA);
            g2.drawLine(w - 1, 3, w - 1, h - 4);

            g2.setColor(COR_AERO_BORDA);
            g2.drawLine(0, h - 1, w, h - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Células de dados sempre com fundo branco (e destaque âmbar quando selecionadas), igual às demais telas. */
    private static class CelulaBrancaRenderer extends DefaultTableCellRenderer {
        CelulaBrancaRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(isSelected ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
            setForeground(COR_TEXTO_CAMPO);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return this;
        }
    }

    /** Botão de ação em vidro: gradiente translúcido, brilho frosted no topo e reação a hover/clique. */
    private static class BotaoVidro extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoVidro(String texto, Color corBase) {
            super(texto);
            this.corBase = corBase;
            this.corClara = clarear(corBase);
            this.corEscura = escurecer(corBase);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)   { sobreMouse = false; repaint(); }
                @Override public void mousePressed(MouseEvent e)  { pressionado = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int raio = 12;

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, raio, raio));

            Color corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            RoundRectangle2D corpo = new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, raio, raio);

            Shape clipAnterior = g2.getClip();
            g2.clip(corpo);

            GradientPaint gp = new GradientPaint(0, 0, comAlpha(clarear(corPreenchimento), 220),
                    0, h, comAlpha(corPreenchimento, 190));
            g2.setPaint(gp);
            g2.fill(corpo);

            g2.setColor(new Color(255, 255, 255, 60));
            g2.fill(new Ellipse2D.Double(-w * 0.1, -h * 0.7, w * 1.2, h * 1.4));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), raio - 5, raio - 5));

            g2.setClip(clipAnterior);

            g2.setColor(comAlpha(escurecer(corPreenchimento), 160));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(corpo);

            // filete interno claro: reforça o efeito de vidro
            g2.setColor(new Color(255, 255, 255, 110));
            g2.draw(new RoundRectangle2D.Double(1.5, 1.5, w - 4, h - 6, raio - 2, raio - 2));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== utilitários de cor =====
    private static Color clarear(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1] * 0.75f, Math.min(1f, hsb[2] * 1.18f));
    }

    private static Color escurecer(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.82f);
    }

    private static Color comAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}