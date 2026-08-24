package view;

import controller.OficinaController;
import model.Orcamento;
import model.OrdemDeServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class V_CadastrarServico extends JPanel {

    private JTable tbl_Orcamentos;
    private DefaultTableModel tbl_Model;
    private JTextField txt_Titulo, txt_Data, txt_Km;
    private JComboBox<String> cbb_Tipo;
    private JComboBox<String> cbb_Manutencao;
    private JButton btn_Criar, btn_Atualizar;

    private List<Orcamento> orcamentos = new ArrayList<>();
    private Orcamento selecionado;

    private final OficinaController controller;

    public V_CadastrarServico(OficinaController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        iniciarComponentes();
        carregarDados();
    }

    // =========================================================================
    // LAYOUT
    // =========================================================================

    private void iniciarComponentes() {
        add(construirNorte(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);
        add(construirSul(), BorderLayout.SOUTH);
    }

    private JPanel construirNorte() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel lbl = new JLabel("Serviços > Nova Ordem de Serviço");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.decode("#4D4D4D"));

        btn_Atualizar = new JButton("↺ Atualizar lista");
        btn_Atualizar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_Atualizar.setFocusPainted(false);
        btn_Atualizar.setBorderPainted(false);
        btn_Atualizar.setBackground(Color.decode("#F0F0F0"));
        btn_Atualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Atualizar.addActionListener(e -> carregarDados());

        pnl.add(lbl);
        pnl.add(Box.createHorizontalStrut(14));
        pnl.add(btn_Atualizar);
        return pnl;
    }

    private JPanel construirCentro() {
        String[] colunas = {"Código", "Responsável", "Veículo", "Cliente", "Valor (R$)", "Reclamação"};
        tbl_Model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Orcamentos = new JTable(tbl_Model);
        tbl_Orcamentos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Orcamentos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl_Orcamentos.setRowHeight(30);
        tbl_Orcamentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_Orcamentos.setGridColor(Color.decode("#EEEEEE"));
        tbl_Orcamentos.setShowVerticalLines(false);
        tbl_Orcamentos.setSelectionBackground(Color.decode("#FFF3E0"));
        tbl_Orcamentos.setSelectionForeground(Color.decode("#333333"));

        tbl_Orcamentos.getColumnModel().getColumn(0).setPreferredWidth(110);
        tbl_Orcamentos.getColumnModel().getColumn(1).setPreferredWidth(140);
        tbl_Orcamentos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbl_Orcamentos.getColumnModel().getColumn(3).setPreferredWidth(140);
        tbl_Orcamentos.getColumnModel().getColumn(4).setPreferredWidth(90);
        tbl_Orcamentos.getColumnModel().getColumn(5).setPreferredWidth(220);

        tbl_Orcamentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onSelecionarLinha();
        });

        JScrollPane scroll = new JScrollPane(tbl_Orcamentos);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.decode("#EEEEEE")));
        ScrollBarPadrao.aplicar(scroll);

        JLabel lbl_Instrucao = new JLabel("Selecione um orçamento APROVADO para criar a Ordem de Serviço:");
        lbl_Instrucao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl_Instrucao.setForeground(Color.decode("#666666"));
        lbl_Instrucao.setBorder(BorderFactory.createEmptyBorder(0, 30, 6, 0));

        JPanel pnl = new JPanel(new BorderLayout(0, 0));
        pnl.setBackground(Color.WHITE);
        pnl.add(lbl_Instrucao, BorderLayout.NORTH);
        pnl.add(scroll, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel construirSul() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 30, 24, 30));

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.decode("#DDDDDD"));

        // 5 colunas: Título | Tipo de Serviço | Manutenção | Data | KM
        JPanel pnl_Campos = new JPanel(new GridLayout(1, 5, 14, 0));
        pnl_Campos.setBackground(Color.WHITE);

        txt_Titulo = criarTextField();
        txt_Titulo.setEnabled(false);
        ((AbstractDocument) txt_Titulo.getDocument()).setDocumentFilter(new FiltroTexto());

        cbb_Tipo = new JComboBox<>();
        cbb_Tipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbb_Tipo.setBackground(Color.WHITE);
        cbb_Tipo.setEnabled(false);

        cbb_Manutencao = new JComboBox<>();
        cbb_Manutencao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbb_Manutencao.setBackground(Color.WHITE);
        cbb_Manutencao.setEnabled(false);

        txt_Data = criarTextField();
        txt_Data.setText(LocalDate.now().toString());
        txt_Data.setEnabled(false);

        txt_Km = criarTextField();
        txt_Km.setEnabled(false);
        ((AbstractDocument) txt_Km.getDocument()).setDocumentFilter(new FiltroDigitos(8));

        pnl_Campos.add(criarContainerVertical(criarLabel("Título da OS *"), txt_Titulo));
        pnl_Campos.add(criarContainerVertical(criarLabel("Tipo de Serviço *"), cbb_Tipo));
        pnl_Campos.add(criarContainerVertical(criarLabel("Manutenção *"), cbb_Manutencao));
        pnl_Campos.add(criarContainerVertical(criarLabel("Data *"), txt_Data));
        pnl_Campos.add(criarContainerVertical(criarLabel("KM Atual *"), txt_Km));

        btn_Criar = new JButton("ABRIR ORDEM DE SERVIÇO");
        btn_Criar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Criar.setForeground(Color.WHITE);
        btn_Criar.setBackground(Color.decode("#FF9900"));
        btn_Criar.setPreferredSize(new Dimension(290, 45));
        btn_Criar.setFocusPainted(false);
        btn_Criar.setBorderPainted(false);
        btn_Criar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Criar.setEnabled(false);
        btn_Criar.addActionListener(e -> criarOS());

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Criar);

        pnl.add(sep, BorderLayout.NORTH);
        pnl.add(pnl_Campos, BorderLayout.CENTER);
        pnl.add(pnl_Btn, BorderLayout.SOUTH);
        return pnl;
    }

    // =========================================================================
    // LÓGICA
    // =========================================================================

    private void carregarDados() {
        if (controller == null) return;

        cbb_Tipo.removeAllItems();
        for (OrdemDeServico.TipoServicoOS t : OrdemDeServico.TipoServicoOS.values())
            cbb_Tipo.addItem(t.getLabel());

        cbb_Manutencao.removeAllItems();
        for (OrdemDeServico.TipoManutencao m : OrdemDeServico.TipoManutencao.values())
            cbb_Manutencao.addItem(m.getLabel());

        tbl_Model.setRowCount(0);
        orcamentos = controller.listarOrcamentosAprovadosSemOS();
        for (Orcamento o : orcamentos) {
            tbl_Model.addRow(new Object[]{
                o.getCodigo().isEmpty() ? "#" + o.getIdOrcamento() : o.getCodigo(),
                o.getResponsavel(),
                o.getPlacaVeiculo(),
                o.getNomeCliente(),
                String.format("R$ %.2f", o.getValor()),
                o.getReclamacao()
            });
        }

        selecionado = null;
        setFormEnabled(false);
    }

    private void onSelecionarLinha() {
        int row = tbl_Orcamentos.getSelectedRow();
        if (row < 0 || row >= orcamentos.size()) {
            selecionado = null;
            setFormEnabled(false);
            return;
        }
        selecionado = orcamentos.get(row);
        txt_Titulo.setText(selecionado.getReclamacao());
        txt_Data.setText(LocalDate.now().toString());
        txt_Km.setText("");
        setFormEnabled(true);
    }

    private void setFormEnabled(boolean enabled) {
        txt_Titulo.setEnabled(enabled);
        cbb_Tipo.setEnabled(enabled);
        cbb_Manutencao.setEnabled(enabled);
        txt_Data.setEnabled(enabled);
        txt_Km.setEnabled(enabled);
        btn_Criar.setEnabled(enabled);
        if (!enabled) {
            txt_Titulo.setText("");
            txt_Km.setText("");
            txt_Data.setText(LocalDate.now().toString());
        }
    }

    private void criarOS() {
        if (selecionado == null) return;

        String titulo = txt_Titulo.getText().trim();
        String data   = txt_Data.getText().trim();
        String kmText = txt_Km.getText().trim();

        StringBuilder erros = new StringBuilder();
        if (titulo.length() < 3) erros.append("• Título deve ter pelo menos 3 caracteres.\n");
        if (data.isEmpty())       erros.append("• Informe a data do serviço.\n");
        int km = 0;
        try {
            if (kmText.isEmpty()) throw new NumberFormatException();
            km = Integer.parseInt(kmText);
            if (km < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            erros.append("• Informe o KM atual (número positivo).\n");
        }

        if (erros.length() > 0) {
            DialogoAlerta.aviso(this, "Corrija os campos:\n\n" + erros, "Dados Inválidos");
            return;
        }

        try {
            OrdemDeServico.TipoServicoOS tipoEnum =
                OrdemDeServico.TipoServicoOS.fromLabel(cbb_Tipo.getSelectedItem().toString());
            OrdemDeServico.TipoManutencao manutEnum =
                OrdemDeServico.TipoManutencao.fromLabel(cbb_Manutencao.getSelectedItem().toString());

            controller.abrirOSDeOrcamento(titulo, tipoEnum.name(), manutEnum.name(),
                data, km, selecionado.getIdVeiculo(), selecionado.getIdOrcamento());

            DialogoAlerta.sucesso(this, "Ordem de Serviço aberta com sucesso!\nOrçamento: " + selecionado.getCodigo(), "Sucesso");
            carregarDados();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao abrir OS: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    // =========================================================================
    // UI HELPERS
    // =========================================================================

    private JPanel criarContainerVertical(JLabel label, Component comp) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setOpaque(false);
        c.add(label, BorderLayout.NORTH);
        c.add(comp, BorderLayout.CENTER);
        return c;
    }

    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.decode("#333333"));
        return l;
    }

    private JTextField criarTextField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(100, 34));
        f.setBackground(Color.WHITE);
        f.setForeground(Color.BLACK);
        f.setCaretColor(Color.BLACK);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        return f;
    }

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

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
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-./,!?()+]", "");
        }
    }

    private static class FiltroDigitos extends DocumentFilter {
        private final int max;
        FiltroDigitos(int max) { this.max = max; }
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() + novo.length() <= max)
                super.insertString(fb, off, novo, attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() - len + novo.length() <= max)
                super.replace(fb, off, len, novo, attr);
        }
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio;
        private final Color cor;
        RoundedBorder(int raio, Color cor) { this.raio = raio; this.cor = cor; }
        public Insets getBorderInsets(Component c) { return new Insets(raio / 2, raio / 2, raio / 2, raio / 2); }
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
