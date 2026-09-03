package view;

import br.com.oficina.estoque.MovimentacaoEstoqueEntity;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Página do Estoque: mostra a quantidade atual de cada peça e o histórico de
 * movimentações (entradas manuais e saídas automáticas geradas pelas OS).
 */
public class V_Estoque extends JPanel {

    private final OficinaController controller;

    public V_Estoque(OficinaController controller) {
        this.controller = controller;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
    }

    private void construir() {
        // ---- Cabeçalho: título + botão de entrada ----
        JLabel titulo = new JLabel("Página Inicial > Estoque de Peças");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));

        JButton btn_Entrada = new JButton("+ Comprar Peça");
        btn_Entrada.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Entrada.setForeground(Color.WHITE);
        btn_Entrada.setBackground(Color.decode("#28A745"));
        btn_Entrada.setFocusPainted(false);
        btn_Entrada.setBorderPainted(false);
        btn_Entrada.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Entrada.setPreferredSize(new Dimension(180, 34));
        btn_Entrada.addActionListener(e -> navegar(new V_EntradaEstoque(controller)));

        JPanel pnl_TituloBtn = new JPanel(new BorderLayout());
        pnl_TituloBtn.setOpaque(false);
        pnl_TituloBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        pnl_TituloBtn.add(titulo, BorderLayout.WEST);
        pnl_TituloBtn.add(btn_Entrada, BorderLayout.EAST);
        add(pnl_TituloBtn, BorderLayout.NORTH);

        // ---- Corpo: estoque atual + histórico ----
        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);

        corpo.add(criarCardEstoqueAtual());
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(criarCardMovimentacoes());
        corpo.add(Box.createVerticalStrut(16));

        JButton btn_Voltar = new JButton("← Voltar");
        btn_Voltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Voltar.setForeground(Color.WHITE);
        btn_Voltar.setBackground(Color.decode("#6C757D"));
        btn_Voltar.setPreferredSize(new Dimension(120, 38));
        btn_Voltar.setFocusPainted(false);
        btn_Voltar.setBorderPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Voltar.addActionListener(e -> navegar(new V_PaginaInicial(controller)));
        JPanel pnl_Voltar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_Voltar.setOpaque(false);
        pnl_Voltar.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Voltar.add(btn_Voltar);
        corpo.add(pnl_Voltar);

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scroll);
        add(scroll, BorderLayout.CENTER);
    }

    // =========================================================================
    // Estoque atual
    // =========================================================================
    private JPanel criarCardEstoqueAtual() {
        JPanel card = criarCard("Estoque atual");
        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BorderLayout());

        String[] cols = {"Cód.", "Peça", "Sistema", "Qtd.", "Situação"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<PecaEntity> pecas = controller.listarEstoque();
        for (PecaEntity p : pecas) {
            mdl.addRow(new Object[]{
                String.format("%04d", p.getIdPeca()),
                p.getNomePopular(),
                p.getSistemaLabel(),
                p.getQuantidadeEstoque(),
                situacao(p.getQuantidadeEstoque())
            });
        }

        JTable tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(28);
        tabela.setGridColor(Color.decode("#EEEEEE"));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getColumnModel().getColumn(3).setMaxWidth(70);
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(3).setCellRenderer(centro);

        // Situação colorida
        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, s, f, row, col);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                c.setOpaque(true);
                if (!s) {
                    switch (String.valueOf(v)) {
                        case "Sem estoque" -> { c.setForeground(Color.decode("#C0392B")); c.setBackground(Color.decode("#FDECEA")); }
                        case "Baixo"       -> { c.setForeground(Color.decode("#9A6700")); c.setBackground(Color.decode("#FEF6E4")); }
                        default            -> { c.setForeground(Color.decode("#1E8449")); c.setBackground(Color.decode("#E9F7EF")); }
                    }
                }
                return c;
            }
        });

        if (mdl.getRowCount() == 0) {
            corpo.add(rotuloVazio("Nenhuma peça cadastrada. Cadastre peças no catálogo para controlar o estoque."), BorderLayout.CENTER);
            return card;
        }

        JScrollPane sc = new JScrollPane(tabela);
        sc.setPreferredSize(new Dimension(0, 220));
        sc.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        ScrollBarPadrao.aplicar(sc);
        corpo.add(sc, BorderLayout.CENTER);
        return card;
    }

    private String situacao(int qtd) {
        if (qtd <= 0) return "Sem estoque";
        if (qtd <= 2) return "Baixo";
        return "OK";
    }

    // =========================================================================
    // Movimentações
    // =========================================================================
    private JPanel criarCardMovimentacoes() {
        JPanel card = criarCard("Movimentações");
        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BorderLayout());

        Map<Long, String> nomePeca = new HashMap<>();
        for (PecaEntity p : controller.listarEstoque()) nomePeca.put(p.getIdPeca(), p.getNomePopular());

        String[] cols = {"Data", "Peça", "Tipo", "Qtd.", "Origem", "Valor", "Observação"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<MovimentacaoEstoqueEntity> movs = controller.listarMovimentacoesEstoque();
        for (MovimentacaoEstoqueEntity m : movs) {
            mdl.addRow(new Object[]{
                formatarData(m.getDataMovimentacao()),
                nomePeca.getOrDefault(m.getIdPeca(), "Peça #" + m.getIdPeca()),
                m.getTipoLabel(),
                (m.isEntrada() ? "+" : "−") + m.getQuantidade(),
                m.getOrigemLabel(),
                m.getValorFormatado(),
                m.getObservacao()
            });
        }

        JTable tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(26);
        tabela.setGridColor(Color.decode("#EEEEEE"));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setMaxWidth(90);
        tabela.getColumnModel().getColumn(2).setMaxWidth(80);
        tabela.getColumnModel().getColumn(3).setMaxWidth(60);
        tabela.getColumnModel().getColumn(4).setMaxWidth(80);
        tabela.getColumnModel().getColumn(5).setMaxWidth(90);

        // Tipo colorido (Entrada verde / Saída vermelho)
        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, s, f, row, col);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (!s) c.setForeground("Entrada".equals(String.valueOf(v)) ? Color.decode("#1E8449") : Color.decode("#C0392B"));
                return c;
            }
        });
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(3).setCellRenderer(centro);

        if (mdl.getRowCount() == 0) {
            corpo.add(rotuloVazio("Nenhuma movimentação ainda. As entradas aparecem aqui e as saídas são geradas ao concluir OS com peças."), BorderLayout.CENTER);
            return card;
        }

        JScrollPane sc = new JScrollPane(tabela);
        sc.setPreferredSize(new Dimension(0, 220));
        sc.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        ScrollBarPadrao.aplicar(sc);
        corpo.add(sc, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // Helpers
    // =========================================================================
    private JPanel criarCard(String tituloTxt) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lbl = new JLabel(tituloTxt);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        lbl.setPreferredSize(new Dimension(0, 28));

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);

        card.add(lbl, BorderLayout.NORTH);
        card.add(corpo, BorderLayout.CENTER);
        return card;
    }

    private JComponent rotuloVazio(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        l.setForeground(Color.decode("#888888"));
        l.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
        return l;
    }

    private String formatarData(String iso) {
        if (iso == null || iso.isBlank()) return "—";
        try { return LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
        catch (Exception e) { return iso; }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }
}
