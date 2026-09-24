package view;

import br.com.oficina.estoque.CatalogoPecaEntity;
import br.com.oficina.estoque.MovimentacaoEstoqueEntity;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Página do Estoque: mostra a quantidade atual de cada peça e o histórico de
 * movimentações (entradas manuais e saídas automáticas geradas pelas OS).
 *
 * Estilo de vidro (cards, cabeçalho Aero das tabelas e botões) unificado com o
 * resto do sistema: mesmas classes e mesma paleta usadas em V_VisualizarServicos
 * (PainelGradiente, BotaoAcao, CabecalhoVidroClaro, CelulaBrancaRenderer).
 */
public class V_Estoque extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mesmos valores usados nas demais telas (vidro Aero)
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#F5F5F5");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4D4D4D");
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

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

    // Botão "+ Comprar Peça" (ação positiva — verde)
    private static final Color COR_ENTRADA        = Color.decode("#28A745");
    private static final Color COR_ENTRADA_CLARA  = Color.decode("#3FC464");
    private static final Color COR_ENTRADA_ESCURA = Color.decode("#1E7E34");

    // Botão "Voltar" (neutro — cinza)
    private static final Color COR_VOLTAR         = Color.decode("#6C757D");
    private static final Color COR_VOLTAR_CLARA   = Color.decode("#868E96");
    private static final Color COR_VOLTAR_ESCURA  = Color.decode("#545B62");

    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_BOTAO = 12;
    private static final int ALTURA_BOTAO        = 34;
    private static final int ALTURA_CABECALHO    = 26;

    private final OficinaController controller;

    // Estoque atual: mestre-detalhe (catálogo ▸ peças reais). Estado de expansão por id_catalogo_peca.
    private final Set<Long> catalogosExpandidos = new HashSet<>();
    private final List<LinhaEstoque> linhasEstoque = new ArrayList<>();
    private DefaultTableModel mdlEstoque;
    private JTable tabelaEstoque;

    public V_Estoque(OficinaController controller) {
        this.controller = controller;
        setBackground(COR_FUNDO_PAGINA);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
    }

    private void construir() {
        // ---- Cabeçalho: título + botão de entrada ----
        JLabel titulo = new JLabel("Página Inicial > Estoque de Peças");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_TITULO);

        BotaoAcao btn_Entrada = new BotaoAcao("+ Comprar Peça", COR_ENTRADA, COR_ENTRADA_CLARA, COR_ENTRADA_ESCURA);
        btn_Entrada.setPreferredSize(new Dimension(180, ALTURA_BOTAO));
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

        BotaoAcao btn_Voltar = new BotaoAcao("← Voltar", COR_VOLTAR, COR_VOLTAR_CLARA, COR_VOLTAR_ESCURA);
        btn_Voltar.setPreferredSize(new Dimension(120, 38));
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

        if (controller.listarEstoque().isEmpty()) {
            corpo.add(rotuloVazio("Nenhuma peça cadastrada. Cadastre peças no catálogo para controlar o estoque."), BorderLayout.CENTER);
            return card;
        }

        String[] cols = {"", "Peça", "Sistema / Fabricante", "Qtd.", "Situação"};
        mdlEstoque = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        construirLinhasEstoque();

        tabelaEstoque = new JTable(mdlEstoque);
        estilizarTabela(tabelaEstoque);
        tabelaEstoque.setRowHeight(28);
        tabelaEstoque.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabelaEstoque.getColumnModel().getColumn(0).setMaxWidth(24);
        tabelaEstoque.getColumnModel().getColumn(3).setMaxWidth(70);

        tabelaEstoque.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, s, f, row, col);
                c.setBackground(s ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setForeground(COR_ACAO);
                c.setFont(new Font("Segoe UI", Font.BOLD, 11));
                c.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                return c;
            }
        });

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, s, f, row, col);
                c.setBackground(s ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
                c.setForeground(COR_TEXTO_CAMPO);
                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                c.setHorizontalAlignment(SwingConstants.CENTER);
                estilizarLinha(c, row);
                return c;
            }
        };
        tabelaEstoque.getColumnModel().getColumn(3).setCellRenderer(centro);

        // Colunas "Peça" e "Sistema/Fabricante" — linhas de peça real vêm indentadas/mais claras
        DefaultTableCellRenderer texto = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, s, f, row, col);
                c.setBackground(s ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
                c.setForeground(COR_TEXTO_CAMPO);
                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                estilizarLinha(c, row);
                return c;
            }
        };
        tabelaEstoque.getColumnModel().getColumn(1).setCellRenderer(texto);
        tabelaEstoque.getColumnModel().getColumn(2).setCellRenderer(texto);

        // Situação colorida — só preenchida nas linhas-catálogo
        tabelaEstoque.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, s, f, row, col);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                c.setOpaque(true);
                if (!s) {
                    switch (String.valueOf(v)) {
                        case "Sem estoque" -> { c.setForeground(Color.decode("#C0392B")); c.setBackground(Color.decode("#FDECEA")); }
                        case "Baixo"       -> { c.setForeground(Color.decode("#9A6700")); c.setBackground(Color.decode("#FEF6E4")); }
                        case "OK"          -> { c.setForeground(Color.decode("#1E8449")); c.setBackground(Color.decode("#E9F7EF")); }
                        default            -> { c.setForeground(COR_TEXTO_CAMPO); c.setBackground(COR_TABELA_FUNDO); }
                    }
                } else {
                    c.setBackground(COR_TABELA_SELECAO);
                }
                return c;
            }
        });

        tabelaEstoque.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tabelaEstoque.rowAtPoint(e.getPoint());
                if (row < 0 || row >= linhasEstoque.size()) return;
                LinhaEstoque linha = linhasEstoque.get(row);
                if (!linha.isCatalogo) return;
                if (catalogosExpandidos.contains(linha.id)) catalogosExpandidos.remove(linha.id);
                else catalogosExpandidos.add(linha.id);
                construirLinhasEstoque();
            }
        });

        JScrollPane sc = new JScrollPane(tabelaEstoque);
        sc.setPreferredSize(new Dimension(0, 220));
        sc.getViewport().setBackground(COR_TABELA_FUNDO);
        sc.getViewport().setOpaque(true);
        sc.setOpaque(false);
        sc.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        ScrollBarPadrao.aplicar(sc);
        corpo.add(sc, BorderLayout.CENTER);
        return card;
    }

    /** Reconstrói as linhas da tabela de estoque (catálogo + peças reais expandidas), preservando o estado de expansão. */
    private void construirLinhasEstoque() {
        mdlEstoque.setRowCount(0);
        linhasEstoque.clear();
        for (CatalogoPecaEntity cat : controller.listarEstoque()) {
            long idCat = cat.getIdCatalogoPeca();
            List<PecaEntity> reais = controller.listarPecasReaisDoCatalogo(idCat);
            int qtdTotal = 0;
            for (PecaEntity r : reais) qtdTotal += r.getQuantidadeEstoque();
            boolean expandido = catalogosExpandidos.contains(idCat);
            String seta = reais.isEmpty() ? "" : (expandido ? "v" : ">");

            mdlEstoque.addRow(new Object[]{ seta, cat.getNomePopular(), cat.getSistemaLabel(), qtdTotal, situacao(qtdTotal) });
            linhasEstoque.add(new LinhaEstoque(true, idCat));

            if (expandido) {
                for (PecaEntity r : reais) {
                    String fabricante = r.getFabricante() == null || r.getFabricante().isBlank() ? "—" : r.getFabricante();
                    mdlEstoque.addRow(new Object[]{ "", "     " + r.getNomeExibicao(), fabricante, r.getQuantidadeEstoque(), "" });
                    linhasEstoque.add(new LinhaEstoque(false, r.getIdPeca()));
                }
            }
        }
    }

    /** Linhas de peça real vêm em itálico/tom mais claro, reforçando a hierarquia catálogo → peça real. */
    private void estilizarLinha(JLabel c, int row) {
        boolean isCatalogo = row >= 0 && row < linhasEstoque.size() && linhasEstoque.get(row).isCatalogo;
        c.setFont(new Font("Segoe UI", isCatalogo ? Font.BOLD : Font.ITALIC, 12));
        if (!isCatalogo) c.setForeground(Color.decode("#6B7280"));
    }

    private String situacao(int qtd) {
        if (qtd <= 0) return "Sem estoque";
        if (qtd <= 2) return "Baixo";
        return "OK";
    }

    /** Uma linha da tabela de Estoque: catálogo (id_catalogo_peca) ou peça real (id_peca). */
    private record LinhaEstoque(boolean isCatalogo, long id) {}

    // =========================================================================
    // Movimentações
    // =========================================================================
    private JPanel criarCardMovimentacoes() {
        JPanel card = criarCard("Movimentações");
        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BorderLayout());

        Map<Long, String> nomePeca = new HashMap<>();
        for (CatalogoPecaEntity cat : controller.listarEstoque()) {
            for (PecaEntity real : controller.listarPecasReaisDoCatalogo(cat.getIdCatalogoPeca())) {
                String tecnico = real.getNomeTecnico();
                String nome = (tecnico != null && !tecnico.isBlank())
                        ? cat.getNomePopular() + " — " + tecnico : cat.getNomePopular();
                nomePeca.put(real.getIdPeca(), nome);
            }
        }

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
        estilizarTabela(tabela);
        tabela.setRowHeight(26);
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
                c.setOpaque(true);
                c.setBackground(COR_TABELA_FUNDO);
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
        sc.getViewport().setBackground(COR_TABELA_FUNDO);
        sc.getViewport().setOpaque(true);
        sc.setOpaque(false);
        sc.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        ScrollBarPadrao.aplicar(sc);
        corpo.add(sc, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // Helpers
    // =========================================================================
    private JPanel criarCard(String tituloTxt) {
        JPanel card = new PainelGradiente(new BorderLayout(0, 12), COR_CARD_TOPO, COR_CARD_BASE);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lbl = new JLabel(tituloTxt);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(COR_ACAO);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_CARD_BASE));
        lbl.setPreferredSize(new Dimension(0, 28));

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);

        card.add(lbl, BorderLayout.NORTH);
        card.add(corpo, BorderLayout.CENTER);
        return card;
    }

    /** Aplica o vidro Aero cinza no cabeçalho e fundo branco nas células, igual às demais telas. */
    private void estilizarTabela(JTable tabela) {
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setBackground(COR_TABELA_FUNDO);
        tabela.setForeground(COR_TEXTO_CAMPO);
        tabela.setOpaque(true);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(COR_TABELA_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO_CAMPO);
        tabela.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setDefaultRenderer(new CabecalhoVidroClaro());
        cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, ALTURA_CABECALHO));
        cabecalho.setReorderingAllowed(false);
        cabecalho.setOpaque(false);
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

    // =========================================================================
    // INNER CLASSES — mesmas classes de vidro usadas em V_VisualizarServicos
    // =========================================================================

    /** Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7). */
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

            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillRect(0, 0, w, Math.max(1, h / 6));

            g2.setColor(COR_AERO_SEPARA);
            g2.drawLine(w - 1, 3, w - 1, h - 4);

            g2.setColor(COR_AERO_BORDA);
            g2.drawLine(0, h - 1, w, h - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Células de dados sempre com fundo branco (e destaque no tom do tema quando selecionadas). */
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

    /** Painel com fundo em gradiente translúcido e cantos arredondados — o "card de vidro". */
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
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação com a mesma linguagem visual dos campos em vidro: cantos
     * arredondados, sombra suave, reflexo no topo e reação a hover/clique.
     * Cor configurável — mesma classe usada em todas as outras telas.
     */
    private static class BotaoAcao extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Color corBase, Color corClara, Color corEscura) {
            super(texto);
            this.corBase = corBase;
            this.corClara = corClara;
            this.corEscura = corEscura;
            setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_BOTAO));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
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

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 30));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}