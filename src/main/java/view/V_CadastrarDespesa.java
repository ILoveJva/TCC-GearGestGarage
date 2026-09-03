package view;

import br.com.oficina.financeiro.DespesaEntity;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Página para registrar despesas (gastos) da oficina, no mesmo padrão visual
 * das telas de cadastro (V_CadastrarItemServico): cartão branco, formulário e
 * lista das despesas já registradas, com exclusão da linha selecionada.
 */
public class V_CadastrarDespesa extends JPanel {

    private final OficinaController controller;
    private final List<DespesaEntity> despesasCarregadas = new ArrayList<>();

    private JTextField txt_Descricao;
    private JTextField txt_Valor;
    private JTextField txt_Data;
    private JTextField txt_Observacao;
    private JComboBox<String> cmb_Categoria;
    private JComboBox<String> cmb_FormaPagamento;
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

    public V_CadastrarDespesa(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        construirInterface();
        carregarLista();
    }

    private void construirInterface() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(700, 660));

        JLabel lbl_Titulo = new JLabel("Configurações > Registrar Despesa");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));

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

        cmb_Categoria = new JComboBox<>(CAT_LABEL);
        cmb_Categoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Categoria.setBackground(Color.WHITE);

        cmb_FormaPagamento = new JComboBox<>(PAG_LABEL);
        cmb_FormaPagamento.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_FormaPagamento.setBackground(Color.WHITE);

        // Linha 1: Descrição + Valor
        JPanel pnl_L1 = linha(Integer.MAX_VALUE, 60);
        JPanel pnl_ValorW = bloco("Valor (R$) *", txt_Valor);
        pnl_ValorW.setPreferredSize(new Dimension(140, 55));
        pnl_L1.add(bloco("Descrição *", txt_Descricao), BorderLayout.CENTER);
        pnl_L1.add(pnl_ValorW, BorderLayout.EAST);

        // Linha 2: Categoria + Data
        JPanel pnl_L2 = linha(Integer.MAX_VALUE, 60);
        pnl_L2.setLayout(new GridLayout(1, 2, 16, 0));
        pnl_L2.add(bloco("Categoria *", cmb_Categoria));
        pnl_L2.add(bloco("Data *", txt_Data));

        // Linha 3: Forma de pagamento
        JPanel pnl_L3 = linha(Integer.MAX_VALUE, 60);
        pnl_L3.add(bloco("Forma de Pagamento *", cmb_FormaPagamento));

        // Linha 4: Observação
        JPanel pnl_L4 = linha(Integer.MAX_VALUE, 60);
        pnl_L4.add(bloco("Observação", txt_Observacao));

        form.add(pnl_L1);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L2);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L3);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L4);

        // ---- Botão ----
        JButton btn_Registrar = new JButton("REGISTRAR DESPESA");
        btn_Registrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Registrar.setForeground(Color.WHITE);
        btn_Registrar.setBackground(Color.decode("#FF9900"));
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
        btn_Voltar.addActionListener(e -> navegar(new V_Configuracoes(controller)));

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Voltar);
        pnl_Btn.add(btn_Registrar);

        JPanel pnl_Topo = new JPanel(new BorderLayout(0, 4));
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
        tbl_Despesas.setRowHeight(26);
        tbl_Despesas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_Despesas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Despesas.getTableHeader().setReorderingAllowed(false);
        tbl_Despesas.getColumnModel().getColumn(0).setMaxWidth(50);
        tbl_Despesas.getColumnModel().getColumn(1).setMaxWidth(100);
        tbl_Despesas.getColumnModel().getColumn(5).setMaxWidth(110);

        JScrollPane scroll = new JScrollPane(tbl_Despesas);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            "Despesas registradas",
            0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.decode("#666666")));
        scroll.setPreferredSize(new Dimension(0, 180));
        ScrollBarPadrao.aplicar(scroll);

        JButton btn_Excluir = new JButton("Excluir selecionada");
        btn_Excluir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_Excluir.setForeground(Color.decode("#DC3545"));
        btn_Excluir.setContentAreaFilled(false);
        btn_Excluir.setBorderPainted(false);
        btn_Excluir.setFocusPainted(false);
        btn_Excluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Excluir.addActionListener(e -> excluirSelecionada());

        lbl_Total = new JLabel("Total: R$ 0,00");
        lbl_Total.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl_Total.setForeground(Color.decode("#333333"));

        JPanel pnl_ListaRodape = new JPanel(new BorderLayout());
        pnl_ListaRodape.setOpaque(false);
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 40, 20, 40);
        add(card, gbc);
    }

    private void salvar() {
        String descricao = txt_Descricao.getText().trim();
        String valorTxt  = txt_Valor.getText().trim().replace(",", ".");
        String categoria = CAT_CODE[cmb_Categoria.getSelectedIndex()];
        String pagamento = PAG_CODE[cmb_FormaPagamento.getSelectedIndex()];
        String observ    = txt_Observacao.getText().trim();

        if (descricao.length() < 3) {
            DialogoAlerta.aviso(this, "A descrição deve ter pelo menos 3 caracteres.", "Campo Inválido");
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorTxt);
            if (valor <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe um valor válido maior que zero (ex: 250.00).", "Campo Inválido");
            return;
        }

        String dataIso = normalizarData(txt_Data.getText().trim());
        if (dataIso == null) {
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

    // ===== helpers =====

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
