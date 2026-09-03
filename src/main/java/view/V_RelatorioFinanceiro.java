package view;

import br.com.oficina.financeiro.DespesaEntity;
import controller.OficinaController;
import controller.OficinaController.Ganho;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

/**
 * Relatório financeiro: total de ganhos (OS concluídas) x total de despesas,
 * o saldo entre eles, uma comparação visual e um detalhamento por mês.
 */
public class V_RelatorioFinanceiro extends JPanel {

    private static final Color COR_GANHO   = Color.decode("#27AE60");
    private static final Color COR_DESPESA = Color.decode("#E74C3C");
    private static final Color COR_SALDO_POS = Color.decode("#2E86DE");
    private static final Color COR_TEXTO   = Color.decode("#333333");
    private static final Color COR_SUAVE   = Color.decode("#888888");
    private static final Locale PT_BR = new Locale("pt", "BR");

    private final OficinaController controller;

    public V_RelatorioFinanceiro(OficinaController controller) {
        this.controller = controller;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
    }

    private void construir() {
        List<Ganho> ganhos = controller.listarGanhosConcluidos();
        List<DespesaEntity> despesas = controller.listarDespesas();

        double totalGanhos   = ganhos.stream().mapToDouble(Ganho::valor).sum();
        double totalDespesas = despesas.stream().mapToDouble(DespesaEntity::getValor).sum();
        double saldo = totalGanhos - totalDespesas;

        // ---- Cabeçalho ----
        JLabel titulo = new JLabel("Página Inicial > Configurações > Relatório Financeiro");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        add(titulo, BorderLayout.NORTH);

        // ---- Corpo rolável ----
        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);

        // Cards resumo
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        cards.add(cardValor("Ganhos (OS concluídas)", totalGanhos, COR_GANHO));
        cards.add(cardValor("Despesas", totalDespesas, COR_DESPESA));
        cards.add(cardValor(saldo >= 0 ? "Saldo (Lucro)" : "Saldo (Prejuízo)", saldo,
                saldo >= 0 ? COR_SALDO_POS : COR_DESPESA));
        corpo.add(cards);
        corpo.add(Box.createVerticalStrut(16));

        // Comparação visual
        JPanel cardComparacao = criarCard("Comparação");
        JPanel bodyComp = (JPanel) cardComparacao.getComponent(1);
        bodyComp.setLayout(new BorderLayout());
        bodyComp.add(new BarraComparativa(totalGanhos, totalDespesas), BorderLayout.CENTER);
        cardComparacao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        corpo.add(cardComparacao);
        corpo.add(Box.createVerticalStrut(16));

        // Detalhamento por mês
        JPanel cardMensal = criarCard("Detalhamento por mês");
        JPanel bodyMensal = (JPanel) cardMensal.getComponent(1);
        bodyMensal.setLayout(new BorderLayout());
        bodyMensal.add(criarTabelaMensal(ganhos, despesas), BorderLayout.CENTER);
        corpo.add(cardMensal);
        corpo.add(Box.createVerticalStrut(16));

        // Voltar
        JButton btn_Voltar = new JButton("← Voltar");
        btn_Voltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Voltar.setForeground(Color.WHITE);
        btn_Voltar.setBackground(Color.decode("#6C757D"));
        btn_Voltar.setPreferredSize(new Dimension(120, 38));
        btn_Voltar.setFocusPainted(false);
        btn_Voltar.setBorderPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Voltar.addActionListener(e -> navegar(new V_Configuracoes(controller)));
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
    // Detalhamento por mês
    // =========================================================================
    private JComponent criarTabelaMensal(List<Ganho> ganhos, List<DespesaEntity> despesas) {
        // Agrega por ano-mês (yyyy-MM)
        Map<String, double[]> porMes = new TreeMap<>(Comparator.reverseOrder()); // [0]=ganho, [1]=despesa
        for (Ganho g : ganhos) {
            String chave = chaveMes(g.data());
            if (chave == null) continue;
            porMes.computeIfAbsent(chave, k -> new double[2])[0] += g.valor();
        }
        for (DespesaEntity d : despesas) {
            String chave = chaveMes(d.getDataDespesa());
            if (chave == null) continue;
            porMes.computeIfAbsent(chave, k -> new double[2])[1] += d.getValor();
        }

        String[] cols = {"Mês", "Ganhos (R$)", "Despesas (R$)", "Saldo (R$)"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Map.Entry<String, double[]> e : porMes.entrySet()) {
            double ganho = e.getValue()[0];
            double desp  = e.getValue()[1];
            mdl.addRow(new Object[]{
                rotuloMes(e.getKey()),
                String.format("%.2f", ganho),
                String.format("%.2f", desp),
                String.format("%.2f", ganho - desp)
            });
        }

        JTable tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(28);
        tabela.setGridColor(Color.decode("#EEEEEE"));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setReorderingAllowed(false);

        // Alinha valores à direita e colore o saldo
        DefaultTableCellRenderer dir = new DefaultTableCellRenderer();
        dir.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int c = 1; c <= 2; c++) tabela.getColumnModel().getColumn(c).setCellRenderer(dir);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, s, f, row, col);
                c.setHorizontalAlignment(SwingConstants.RIGHT);
                double val = parseSeguro(String.valueOf(v));
                if (!s) c.setForeground(val < 0 ? COR_DESPESA : COR_GANHO);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                return c;
            }
        });

        if (mdl.getRowCount() == 0) {
            JLabel vazio = new JLabel("Ainda não há ganhos nem despesas registrados.");
            vazio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vazio.setForeground(COR_SUAVE);
            vazio.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
            return vazio;
        }

        JScrollPane sc = new JScrollPane(tabela);
        sc.setPreferredSize(new Dimension(0, 200));
        sc.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        ScrollBarPadrao.aplicar(sc);
        return sc;
    }

    private double parseSeguro(String s) {
        try { return Double.parseDouble(s.replace(",", ".")); } catch (Exception e) { return 0; }
    }

    private String chaveMes(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try { return YearMonth.from(LocalDate.parse(iso.trim())).toString(); } // yyyy-MM
        catch (Exception e) { return null; }
    }

    private String rotuloMes(String chave) {
        try {
            YearMonth ym = YearMonth.parse(chave);
            String mes = ym.getMonth().getDisplayName(TextStyle.FULL, PT_BR);
            return Character.toUpperCase(mes.charAt(0)) + mes.substring(1) + " " + ym.getYear();
        } catch (Exception e) {
            return chave;
        }
    }

    // =========================================================================
    // Componentes visuais
    // =========================================================================
    private JPanel cardValor(String rotulo, double valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel lbl = new JLabel(rotulo.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(COR_SUAVE);

        JLabel val = new JLabel(String.format("R$ %.2f", valor));
        val.setFont(new Font("Segoe UI", Font.BOLD, 24));
        val.setForeground(cor);

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    /** Card branco no padrão da tela: título laranja + corpo. corpo = getComponent(1). */
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

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    /** Duas barras horizontais proporcionais (ganhos x despesas) com rótulo de valor. */
    private static class BarraComparativa extends JPanel {
        private final double ganhos;
        private final double despesas;

        BarraComparativa(double ganhos, double despesas) {
            this.ganhos = ganhos;
            this.despesas = despesas;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 110));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int rotuloW = 90;
            int valorW = 130;
            int barX = rotuloW;
            int barMaxW = Math.max(10, w - rotuloW - valorW);
            double max = Math.max(Math.max(ganhos, despesas), 1);

            int alturaBarra = 26;
            int y1 = 18;
            int y2 = 62;

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));

            desenharBarra(g2, "Ganhos", ganhos, COR_GANHO, barX, y1, barMaxW, alturaBarra, max, rotuloW);
            desenharBarra(g2, "Despesas", despesas, COR_DESPESA, barX, y2, barMaxW, alturaBarra, max, rotuloW);

            g2.dispose();
        }

        private void desenharBarra(Graphics2D g2, String rotulo, double valor, Color cor,
                                   int barX, int y, int barMaxW, int altura, double max, int rotuloW) {
            g2.setColor(COR_TEXTO);
            g2.drawString(rotulo, 4, y + altura - 8);

            g2.setColor(Color.decode("#F0F0F0"));
            g2.fillRoundRect(barX, y, barMaxW, altura, 8, 8);

            int larg = (int) Math.round(barMaxW * (valor / max));
            g2.setColor(cor);
            g2.fillRoundRect(barX, y, Math.max(larg, valor > 0 ? 4 : 0), altura, 8, 8);

            g2.setColor(COR_TEXTO);
            g2.drawString(String.format("R$ %.2f", valor), barX + barMaxW + 10, y + altura - 8);
        }
    }
}
