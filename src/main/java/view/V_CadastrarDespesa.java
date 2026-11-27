package view;

import br.com.oficina.financeiro.DespesaEntity;
import controller.OficinaController;
import view.design.ScrollBar;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Página para registrar despesas (gastos) da oficina, seguindo o padrão visual
 * de glassmorphism de V_CadastrarPeca: cartão com gradiente suave, campos e
 * combos translúcidos, botões estilizados e lista das despesas registradas.
 */
public class V_CadastrarDespesa extends JPanel {

    private final OficinaController controller;
    private final List<DespesaEntity> despesasCarregadas = new ArrayList<>();

    private GlassTextField txt_Descricao;
    private GlassTextField txt_Valor;
    private GlassTextField txt_Data;
    private GlassTextField txt_Observacao;
    private GlassComboBox<String> cmb_Categoria;
    private GlassComboBox<String> cmb_FormaPagamento;
    private DefaultTableModel mdl_Despesas;
    private JTable tbl_Despesas;
    private JLabel lbl_Total;

    private static final String[] CAT_LABEL = {
            "Peças", "Salário", "Aluguel", "Ferramentas", "Impostos", "Contas", "Outros"
    };
    private static final String[] CAT_CODE = {
            "PECAS", "SALARIO", "ALUGUEL", "FERRAMENTAS", "IMPOSTOS", "CONTAS", "OUTROS"
    };
    private static final String[] PAG_LABEL = {
            "Dinheiro", "Pix", "Cartão de Crédito", "Cartão de Débito", "Boleto", "Transferência", "Outros"
    };
    private static final String[] PAG_CODE = {
            "DINHEIRO", "PIX", "CARTAO_CREDITO", "CARTAO_DEBITO", "BOLETO", "TRANSFERENCIA", "OUTROS"
    };

    // Paleta harmonizada com o efeito de vidro (mesmo padrão de V_CadastrarPeca)
    private static final Color COR_FUNDO_PAGINA   = Color.decode("#F5F7FA");
    private static final Color COR_CARD_TOPO      = Color.decode("#FFFFFF");
    private static final Color COR_CARD_BASE      = Color.decode("#EEF2F7");
    private static final Color COR_TITULO         = Color.decode("#4A5568");
    private static final Color COR_LABEL          = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO    = Color.decode("#2B2E33");

    // Cores do popup da lista suspensa e da tabela
    private static final Color COR_POPUP_FUNDO    = Color.decode("#FFFFFF");
    private static final Color COR_POPUP_SELECAO  = Color.decode("#FFE4BF");
    private static final Color COR_POPUP_BORDA    = Color.decode("#C3CDDA");

    // Cor de ação primária e variações
    private static final Color COR_ACAO           = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA     = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA    = Color.decode("#E68A00");

    // Cor de ação secundária (botão Voltar) e variações
    private static final Color COR_SECUNDARIA        = Color.decode("#6C757D");
    private static final Color COR_SECUNDARIA_CLARA  = Color.decode("#7D868E");
    private static final Color COR_SECUNDARIA_ESCURA = Color.decode("#5A6268");

    // Ajustes rápidos de tipografia/tamanho
    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_LABEL = 14;
    private static final int TAMANHO_FONTE_CAMPO = 14;
    private static final int ALTURA_CAMPO        = 36;
    private static final int TAMANHO_FONTE_BOTAO = 14;

    public V_CadastrarDespesa(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        construirInterface();
        carregarLista();
    }

    private void construirInterface() {
        PainelGradiente card = new PainelGradiente(new BorderLayout(0, 16), COR_CARD_TOPO, COR_CARD_BASE);
        card.setPreferredSize(new Dimension(720, 680));
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lbl_Titulo = new JLabel("Configurações > Registrar Despesa");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(COR_TITULO);

        // ---- Formulário ----
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        txt_Descricao = criarTextField();
        txt_Descricao.setToolTipText("Ex: Compra de óleo, conta de energia, salário do mecânico...");

        txt_Valor = criarTextField();
        txt_Valor.setToolTipText("Ex: 250.00");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_Data = criarTextField();
        txt_Data.setText(LocalDate.now().toString());
        txt_Data.setToolTipText("Formato: AAAA-MM-DD (ex: " + LocalDate.now() + ")");

        txt_Observacao = criarTextField();
        txt_Observacao.setToolTipText("Detalhes adicionais (opcional)");

        cmb_Categoria = new GlassComboBox<>(CAT_LABEL);
        cmb_Categoria.setPreferredSize(new Dimension(140, ALTURA_CAMPO));

        cmb_FormaPagamento = new GlassComboBox<>(PAG_LABEL);
        cmb_FormaPagamento.setPreferredSize(new Dimension(140, ALTURA_CAMPO));

        // Linha 1: Descrição + Valor
        JPanel pnl_L1 = linha(Integer.MAX_VALUE, 64);
        JPanel pnl_ValorW = bloco("Valor (R$) *", txt_Valor);
        pnl_ValorW.setPreferredSize(new Dimension(150, 60));
        pnl_L1.add(bloco("Descrição *", txt_Descricao), BorderLayout.CENTER);
        pnl_L1.add(pnl_ValorW, BorderLayout.EAST);

        // Linha 2: Categoria + Data
        JPanel pnl_L2 = linha(Integer.MAX_VALUE, 64);
        pnl_L2.setLayout(new GridLayout(1, 2, 16, 0));
        pnl_L2.add(bloco("Categoria *", cmb_Categoria));
        pnl_L2.add(bloco("Data *", txt_Data));

        // Linha 3: Forma de pagamento
        JPanel pnl_L3 = linha(Integer.MAX_VALUE, 64);
        pnl_L3.add(bloco("Forma de Pagamento *", cmb_FormaPagamento));

        // Linha 4: Observação
        JPanel pnl_L4 = linha(Integer.MAX_VALUE, 64);
        pnl_L4.add(bloco("Observação", txt_Observacao));

        form.add(pnl_L1);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L2);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L3);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L4);

        // ---- Botões ----
        BotaoAcao btn_Registrar = new BotaoAcao(
                "REGISTRAR DESPESA", COR_ACAO, COR_ACAO_CLARA, COR_ACAO_ESCURA, new Color(180, 100, 0, 60));
        btn_Registrar.setPreferredSize(new Dimension(220, 44));
        btn_Registrar.addActionListener(e -> salvar());

        BotaoAcao btn_Voltar = new BotaoAcao(
                "← Voltar", COR_SECUNDARIA, COR_SECUNDARIA_CLARA, COR_SECUNDARIA_ESCURA, new Color(70, 80, 90, 45));
        btn_Voltar.setPreferredSize(new Dimension(120, 44));
        btn_Voltar.addActionListener(e -> navegar(new V_Configuracoes(controller)));

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Voltar);
        pnl_Btn.add(btn_Registrar);

        JPanel pnl_Topo = new JPanel(new BorderLayout(0, 6));
        pnl_Topo.setOpaque(false);
        pnl_Topo.add(form, BorderLayout.CENTER);
        pnl_Topo.add(pnl_Btn, BorderLayout.SOUTH);

        // ---- Tabela de despesas ----
        String[] colunas = {"Cód.", "Data", "Descrição", "Categoria", "Pagamento", "Valor (R$)"};
        mdl_Despesas = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Despesas = new JTable(mdl_Despesas);
        tbl_Despesas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Despesas.setForeground(COR_TEXTO_CAMPO);
        tbl_Despesas.setBackground(COR_POPUP_FUNDO);
        tbl_Despesas.setSelectionBackground(COR_POPUP_SELECAO);
        tbl_Despesas.setSelectionForeground(COR_TEXTO_CAMPO);
        tbl_Despesas.setGridColor(Color.decode("#E6ECF2"));
        tbl_Despesas.setRowHeight(28);
        tbl_Despesas.setShowVerticalLines(false);
        tbl_Despesas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = tbl_Despesas.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(COR_LABEL);
        header.setBackground(Color.decode("#F1F5F9"));
        header.setReorderingAllowed(false);

        tbl_Despesas.getColumnModel().getColumn(0).setMaxWidth(55);
        tbl_Despesas.getColumnModel().getColumn(1).setMaxWidth(100);
        tbl_Despesas.getColumnModel().getColumn(5).setMaxWidth(110);

        DefaultTableCellRenderer alinhadoDireita = new DefaultTableCellRenderer();
        alinhadoDireita.setHorizontalAlignment(SwingConstants.RIGHT);
        tbl_Despesas.getColumnModel().getColumn(5).setCellRenderer(alinhadoDireita);

        JScrollPane scroll = new JScrollPane(tbl_Despesas);
        scroll.getViewport().setBackground(COR_POPUP_FUNDO);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_POPUP_BORDA),
                "Despesas registradas",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), COR_LABEL));
        scroll.setPreferredSize(new Dimension(0, 180));
        ScrollBar.aplicar(scroll);

        JButton btn_Excluir = new JButton("Excluir selecionada");
        btn_Excluir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_Excluir.setForeground(Color.decode("#DC3545"));
        btn_Excluir.setContentAreaFilled(false);
        btn_Excluir.setBorderPainted(false);
        btn_Excluir.setFocusPainted(false);
        btn_Excluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Excluir.addActionListener(e -> excluirSelecionada());

        lbl_Total = new JLabel("Total: R$ 0,00");
        lbl_Total.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Total.setForeground(COR_TEXTO_CAMPO);

        JPanel pnl_ListaRodape = new JPanel(new BorderLayout());
        pnl_ListaRodape.setOpaque(false);
        pnl_ListaRodape.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 2));
        pnl_ListaRodape.add(btn_Excluir, BorderLayout.WEST);
        pnl_ListaRodape.add(lbl_Total, BorderLayout.EAST);

        JPanel pnl_Lista = new JPanel(new BorderLayout(0, 4));
        pnl_Lista.setOpaque(false);
        pnl_Lista.add(scroll, BorderLayout.CENTER);
        pnl_Lista.add(pnl_ListaRodape, BorderLayout.SOUTH);

        card.add(lbl_Titulo, BorderLayout.NORTH);
        card.add(pnl_Topo, BorderLayout.CENTER);
        card.add(pnl_Lista, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(16, 24, 16, 24);
        add(card, gbc);
    }

    // =========================================================================
    // VALIDAÇÃO E AÇÕES
    // =========================================================================
    private void salvar() {
        limparErro(txt_Descricao);
        limparErro(txt_Valor);
        limparErro(txt_Data);

        String descricao = txt_Descricao.getText().trim();
        String valorTxt  = txt_Valor.getText().trim().replace(",", ".");
        String categoria = CAT_CODE[cmb_Categoria.getSelectedIndex()];
        String pagamento = PAG_CODE[cmb_FormaPagamento.getSelectedIndex()];
        String observ    = txt_Observacao.getText().trim();

        if (descricao.length() < 3) {
            marcarErro(txt_Descricao);
            DialogoAlerta.aviso(this, "A descrição deve ter pelo menos 3 caracteres.", "Campo Inválido");
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorTxt);
            if (valor <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            marcarErro(txt_Valor);
            DialogoAlerta.aviso(this, "Informe um valor válido maior que zero (ex: 250.00).", "Campo Inválido");
            return;
        }

        String dataIso = normalizarData(txt_Data.getText().trim());
        if (dataIso == null) {
            marcarErro(txt_Data);
            DialogoAlerta.aviso(this, "Informe uma data válida no formato AAAA-MM-DD.", "Campo Inválido");
            return;
        }

        try {
            controller.salvarDespesa(descricao, categoria, valor, dataIso, pagamento, observ);
            DialogoAlerta.sucesso(this, "Despesa registrada com sucesso!", "Sucesso");
            txt_Descricao.setText("");
            txt_Valor.setText("");
            txt_Observacao.setText("");
            txt_Data.setText(LocalDate.now().toString());
            cmb_Categoria.setSelectedIndex(0);
            cmb_FormaPagamento.setSelectedIndex(0);
            carregarLista();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao registrar despesa: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void excluirSelecionada() {
        int row = tbl_Despesas.getSelectedRow();
        if (row < 0 || row >= despesasCarregadas.size()) {
            DialogoAlerta.aviso(this, "Selecione uma despesa na lista para excluir.", "Nenhuma selecionada");
            return;
        }
        DespesaEntity alvo = despesasCarregadas.get(row);
        boolean ok = DialogoConfirmacao.confirmar(this,
                "Tem certeza que deseja excluir a despesa \"" + alvo.getDescricao() + "\"?",
                "Confirmar exclusão");
        if (!ok) return;
        try {
            controller.excluirDespesa(alvo.getIdDespesa());
            DialogoAlerta.sucesso(this, "Despesa excluída com sucesso!", "Sucesso");
            carregarLista();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao excluir despesa: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void carregarLista() {
        mdl_Despesas.setRowCount(0);
        despesasCarregadas.clear();
        despesasCarregadas.addAll(controller.listarDespesas());
        double total = 0;
        for (DespesaEntity d : despesasCarregadas) {
            total += d.getValor();
            mdl_Despesas.addRow(new Object[]{
                    d.getIdDespesa() != null ? String.format("%04d", d.getIdDespesa()) : "—",
                    formatarData(d.getDataDespesa()),
                    d.getDescricao(),
                    d.getCategoriaLabel(),
                    d.getFormaPagamentoLabel(),
                    String.format("%.2f", d.getValor())
            });
        }
        lbl_Total.setText(String.format("Total: R$ %.2f", total));
    }

    private void marcarErro(GlassTextField field) {
        field.setEstadoErro(true);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                limparErro(field);
                field.removeFocusListener(this);
            }
        });
    }

    private void limparErro(GlassTextField field) {
        field.setEstadoErro(false);
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    /** Aceita AAAA-MM-DD ou DD/MM/AAAA e retorna sempre no formato ISO (AAAA-MM-DD). Null se inválida. */
    private String normalizarData(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        try { return LocalDate.parse(t).toString(); } catch (Exception ignored) { }
        try { return LocalDate.parse(t, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString(); } catch (Exception ignored) { }
        return null;
    }

    private String formatarData(String iso) {
        if (iso == null || iso.isBlank()) return "—";
        try {
            return LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return iso;
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

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
        l.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL));
        l.setForeground(COR_LABEL);
        return l;
    }

    private GlassTextField criarTextField() {
        GlassTextField f = new GlassTextField();
        f.setPreferredSize(new Dimension(100, ALTURA_CAMPO));
        return f;
    }

    private static class FiltroDecimal extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9.,]", ""), a);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9.,]", ""), a);
        }
    }

    // =========================================================================
    // COMPONENTES VISUAIS CUSTOMIZADOS (Glassmorphism — padrão V_CadastrarPeca)
    // =========================================================================

    private static class GlassTextField extends JTextField {
        private boolean focado = false;
        private boolean erro = false;

        GlassTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        void setEstadoErro(boolean valor) {
            this.erro = valor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Sombra suave por baixo do vidro
            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            // Preenchimento translúcido
            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, erro ? 225 : 210),
                    0, h, new Color(255, 255, 255, erro ? 175 : 145)
            );
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            // Reflexo sutil na parte superior
            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda;
            float espessura;
            if (erro) {
                corBorda = new Color(214, 58, 68, 220);
                espessura = 1.6f;
            } else if (focado) {
                corBorda = new Color(255, 153, 0, 210);
                espessura = 1.6f;
            } else {
                corBorda = new Color(160, 175, 195, 130);
                espessura = 1f;
            }
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

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
            GradientPaint gp = new GradientPaint(0, 0, corTopo, 0, getHeight(), corBase);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotaoAcao extends JButton {
        private final Color corNormal;
        private final Color corClara;
        private final Color corEscura;
        private final Color corSombra;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Color corNormal, Color corClara, Color corEscura, Color corSombra) {
            super(texto);
            this.corNormal = corNormal;
            this.corClara = corClara;
            this.corEscura = corEscura;
            this.corSombra = corSombra;

            setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_BOTAO));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
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

            g2.setColor(corSombra);
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corNormal);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GlassComboBox<T> extends JComboBox<T> {
        private boolean focado = false;

        GlassComboBox(T[] items) {
            super(items);
            estilizar();
        }

        private void estilizar() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setBackground(COR_POPUP_FUNDO);
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 4));
            setFocusable(true);
            setUI(new GlassComboBoxUI());
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 210),
                    0, h, new Color(255, 255, 255, 145)
            );
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130);
            float espessura = focado ? 1.6f : 1f;
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    private static class GlassComboBoxUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Propositalmente vazio — o fundo já é pintado em GlassComboBox.paintComponent().
        }

        @Override
        @SuppressWarnings("unchecked")
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            ListCellRenderer renderer = comboBox.getRenderer();
            Component c = renderer.getListCellRendererComponent(
                    listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setFont(comboBox.getFont());
            c.setForeground(comboBox.isEnabled() ? COR_TEXTO_CAMPO : Color.GRAY);

            boolean opacoOriginal = false;
            if (c instanceof JComponent) {
                opacoOriginal = ((JComponent) c).isOpaque();
                ((JComponent) c).setOpaque(false);
            }

            boolean shouldValidate = c instanceof JPanel;
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate);

            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(opacoOriginal);
            }
        }

        @Override
        protected ComboPopup createPopup() {
            return new GlassComboPopup(comboBox);
        }

        @Override
        protected JButton createArrowButton() {
            JButton seta = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    int cx = w / 2, cy = h / 2;
                    Path2D triangulo = new Path2D.Double();
                    triangulo.moveTo(cx - 4, cy - 2);
                    triangulo.lineTo(cx + 4, cy - 2);
                    triangulo.lineTo(cx, cy + 3);
                    triangulo.closePath();
                    g2.setColor(COR_LABEL);
                    g2.fill(triangulo);
                    g2.dispose();
                }
            };
            seta.setPreferredSize(new Dimension(22, 22));
            seta.setContentAreaFilled(false);
            seta.setBorderPainted(false);
            seta.setFocusPainted(false);
            seta.setOpaque(false);
            seta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return seta;
        }
    }

    private static class GlassComboPopup extends BasicComboPopup {

        GlassComboPopup(JComboBox<Object> combo) {
            super(combo);
        }

        @Override
        protected void configurePopup() {
            super.configurePopup();
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }

        @Override
        protected void configureList() {
            super.configureList();
            list.setOpaque(true);
            list.setBackground(COR_POPUP_FUNDO);
            list.setForeground(COR_TEXTO_CAMPO);
            list.setSelectionBackground(COR_POPUP_SELECAO);
            list.setSelectionForeground(COR_TEXTO_CAMPO);
            list.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
        }

        @Override
        protected JScrollPane createScroller() {
            JScrollPane scroller = super.createScroller();
            scroller.setOpaque(false);
            scroller.getViewport().setOpaque(false);
            scroller.setBorder(BorderFactory.createEmptyBorder());
            ScrollBar.aplicar(scroller);
            return scroller;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(COR_POPUP_FUNDO);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(COR_POPUP_BORDA);
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void show() {
            super.show();
            try {
                Window janela = SwingUtilities.getWindowAncestor(this);
                if (janela != null) {
                    janela.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RAIO_COMPONENTE, RAIO_COMPONENTE));
                }
            } catch (Exception | Error ignorado) {
                // Sem suporte a formato de janela nesta plataforma.
            }
        }
    }
}