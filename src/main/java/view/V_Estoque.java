package view;

import br.com.oficina.estoque.CatalogoPecaEntity;
import br.com.oficina.estoque.MovimentacaoEstoqueEntity;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;
import view.design.ScrollBar;
import view.design.Tabela;
import view.paginas.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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
 * Página do Estoque refatorada utilizando a nova classe Tabela
 * separando os 3 tipos visuais nas respectivas seções.
 */
public class V_Estoque extends JPanel {

    private static final Color COR_FUNDO_PAGINA = Color.decode("#F5F5F5");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4D4D4D");
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_TABELA_FUNDO = Color.WHITE;
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");

    private static final Color COR_ENTRADA        = Color.decode("#28A745");
    private static final Color COR_ENTRADA_CLARA  = Color.decode("#3FC464");
    private static final Color COR_ENTRADA_ESCURA = Color.decode("#1E7E34");

    private static final Color COR_RELATORIO_CLARA  = Color.decode("#FFAD33");
    private static final Color COR_RELATORIO_ESCURA = Color.decode("#E68A00");

    private static final Color COR_MOV        = Color.decode("#2E86DE");
    private static final Color COR_MOV_CLARA  = Color.decode("#4A9AE8");
    private static final Color COR_MOV_ESCURA = Color.decode("#1F6FBF");

    private static final Color COR_VOLTAR         = Color.decode("#6C757D");
    private static final Color COR_VOLTAR_CLARA   = Color.decode("#868E96");
    private static final Color COR_VOLTAR_ESCURA  = Color.decode("#545B62");

    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_BOTAO = 12;
    private static final int ALTURA_BOTAO        = 34;

    private final OficinaController controller;

    private final Set<Long> catalogosExpandidos = new HashSet<>();
    private final List<LinhaEstoque> linhasEstoque = new ArrayList<>();
    private Tabela tabelaEstoque;

    public V_Estoque(OficinaController controller) {
        this.controller = controller;
        setBackground(COR_FUNDO_PAGINA);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
    }

    private void construir() {
        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);

        JLabel titulo = new JLabel("Página Inicial > Estoque de Peças");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(titulo);
        corpo.add(Box.createVerticalStrut(10));

        BotaoAcao btn_Entrada = new BotaoAcao("+ Comprar Peça", COR_ENTRADA, COR_ENTRADA_CLARA, COR_ENTRADA_ESCURA);
        btn_Entrada.setPreferredSize(new Dimension(180, ALTURA_BOTAO));
        btn_Entrada.addActionListener(e -> navegar(new V_EntradaEstoque(controller)));

        BotaoAcao btn_RelEstoque = new BotaoAcao("Relatório de Estoque", COR_ACAO, COR_RELATORIO_CLARA, COR_RELATORIO_ESCURA);
        btn_RelEstoque.setPreferredSize(new Dimension(180, ALTURA_BOTAO));
        btn_RelEstoque.addActionListener(e -> ExportadorEstoque.exportarEstoque(this, controller));

        BotaoAcao btn_RelMov = new BotaoAcao("Relatório de Movimentações", COR_MOV, COR_MOV_CLARA, COR_MOV_ESCURA);
        btn_RelMov.setPreferredSize(new Dimension(220, ALTURA_BOTAO));
        btn_RelMov.addActionListener(e -> ExportadorEstoque.exportarMovimentacoes(this, controller));

        JPanel pnl_BotoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnl_BotoesTopo.setOpaque(false);
        pnl_BotoesTopo.add(btn_RelEstoque);
        pnl_BotoesTopo.add(btn_RelMov);
        pnl_BotoesTopo.add(btn_Entrada);

        JPanel pnl_LinhaTopoBotoes = new JPanel(new BorderLayout());
        pnl_LinhaTopoBotoes.setOpaque(false);
        pnl_LinhaTopoBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_LinhaTopoBotoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_BOTAO));
        pnl_LinhaTopoBotoes.add(new JLabel(), BorderLayout.WEST);
        pnl_LinhaTopoBotoes.add(pnl_BotoesTopo, BorderLayout.EAST);

        corpo.add(pnl_LinhaTopoBotoes);
        corpo.add(Box.createVerticalStrut(16));

        // Adicionando os cards usando os 3 tipos de tabelas genéricas
        corpo.add(criarCardPecasParaComprar()); // TIPO 1: SIMPLES[cite: 4]
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(criarCardEstoqueAtual());     // TIPO 2: AGRUPADA[cite: 4]
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(criarCardMovimentacoes());    // TIPO 3: FINANCEIRA[cite: 4]
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
        ScrollBar.aplicar(scroll);

        removeAll();
        add(scroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // =========================================================================
    // TIPO 1: Tabela de Listagem Simples (Peças a Comprar)[cite: 4]
    // =========================================================================
    private JPanel criarCardPecasParaComprar() {
        JPanel card = criarCard("Peças a comprar (OS em aberto)");
        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BorderLayout(0, 8));

        BotaoAcao btn_RelPecas = new BotaoAcao("Relatório de Peças Necessárias", COR_ACAO, COR_RELATORIO_CLARA, COR_RELATORIO_ESCURA);
        btn_RelPecas.setPreferredSize(new Dimension(230, ALTURA_BOTAO));
        btn_RelPecas.addActionListener(e -> ExportadorEstoque.exportarPecasNecessarias(this, controller));
        JPanel pnl_BtnRelPecas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnl_BtnRelPecas.setOpaque(false);
        pnl_BtnRelPecas.add(btn_RelPecas);
        corpo.add(pnl_BtnRelPecas, BorderLayout.NORTH);

        List<OficinaController.PecaParaComprar> pendentes = controller.listarPecasParaComprar();
        if (pendentes.isEmpty()) {
            corpo.add(rotuloVazio("Nenhuma peça faltando. O estoque atende todas as OS em aberto."), BorderLayout.CENTER);
            return card;
        }

        String[] cols = {"Peça", "Sistema", "Veículo", "Qtd. a comprar"};
        Tabela tabelaPecas = new Tabela(cols, Tabela.TipoVisual.SIMPLES, 26);
        tabelaPecas.setMaxWidthColuna(3, 110);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        centro.setBackground(COR_TABELA_FUNDO);
        tabelaPecas.setRendererColuna(3, centro);

        for (OficinaController.PecaParaComprar p : pendentes) {
            tabelaPecas.adicionarLinha(new Object[]{p.peca(), p.sistema(), p.veiculo(), p.quantidade()});
        }

        corpo.add(tabelaPecas, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // TIPO 2: Tabela Agrupada / Expansível (Estoque Atual)[cite: 4]
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
        tabelaEstoque = new Tabela(cols, Tabela.TipoVisual.AGRUPADA, 28);

        tabelaEstoque.setMaxWidthColuna(0, 24);
        tabelaEstoque.setMaxWidthColuna(3, 70);

        tabelaEstoque.setRendererColuna(0, new DefaultTableCellRenderer() {
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

        tabelaEstoque.setRendererColuna(4, new DefaultTableCellRenderer() {
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

        construirLinhasEstoque();

        tabelaEstoque.aoClicar(e -> {
            int row = tabelaEstoque.tabela.rowAtPoint(e.getPoint());
            if (row < 0 || row >= linhasEstoque.size()) return;
            LinhaEstoque linha = linhasEstoque.get(row);
            if (!linha.isCatalogo) return;
            if (catalogosExpandidos.contains(linha.id)) catalogosExpandidos.remove(linha.id);
            else catalogosExpandidos.add(linha.id);
            construirLinhasEstoque();
        });

        corpo.add(tabelaEstoque, BorderLayout.CENTER);
        return card;
    }

    private void construirLinhasEstoque() {
        tabelaEstoque.limpar();
        linhasEstoque.clear();
        for (CatalogoPecaEntity cat : controller.listarEstoque()) {
            long idCat = cat.getIdCatalogoPeca();
            List<PecaEntity> reais = controller.listarPecasReaisDoCatalogo(idCat);
            int qtdTotal = 0;
            for (PecaEntity r : reais) qtdTotal += r.getQuantidadeEstoque();
            boolean expandido = catalogosExpandidos.contains(idCat);
            String seta = reais.isEmpty() ? "" : (expandido ? "v" : ">");

            tabelaEstoque.adicionarLinha(new Object[]{ seta, cat.getNomePopular(), cat.getSistemaLabel(), qtdTotal, situacao(qtdTotal) });
            linhasEstoque.add(new LinhaEstoque(true, idCat));

            if (expandido) {
                for (PecaEntity r : reais) {
                    String fabricante = r.getFabricante() == null || r.getFabricante().isBlank() ? "—" : r.getFabricante();
                    tabelaEstoque.adicionarLinha(new Object[]{ "", "     " + r.getNomeExibicao(), fabricante, r.getQuantidadeEstoque(), "" });
                    linhasEstoque.add(new LinhaEstoque(false, r.getIdPeca()));
                }
            }
        }
    }

    private String situacao(int qtd) {
        return ExportadorEstoque.situacao(qtd);
    }

    private record LinhaEstoque(boolean isCatalogo, long id) {}

    // =========================================================================
    // TIPO 3: Tabela com Células Personalizadas e Valores (Movimentações)[cite: 4]
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
        Tabela tabelaMov = new Tabela(cols, Tabela.TipoVisual.FINANCEIRA, 26);

        tabelaMov.setMaxWidthColuna(0, 90);
        tabelaMov.setMaxWidthColuna(2, 80);
        tabelaMov.setMaxWidthColuna(3, 60);
        tabelaMov.setMaxWidthColuna(4, 80);
        tabelaMov.setMaxWidthColuna(5, 90);

        tabelaMov.setRendererColuna(2, new DefaultTableCellRenderer() {
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
        tabelaMov.setRendererColuna(3, centro);

        List<MovimentacaoEstoqueEntity> movs = controller.listarMovimentacoesEstoque();
        for (MovimentacaoEstoqueEntity m : movs) {
            tabelaMov.adicionarLinha(new Object[]{
                    formatarData(m.getDataMovimentacao()),
                    nomePeca.getOrDefault(m.getIdPeca(), "Peça #" + m.getIdPeca()),
                    m.getTipoLabel(),
                    (m.isEntrada() ? "+" : "−") + m.getQuantidade(),
                    m.getOrigemLabel(),
                    m.getValorFormatado(),
                    m.getObservacao()
            });
        }

        if (movs.isEmpty()) {
            corpo.add(rotuloVazio("Nenhuma movimentação ainda. As entradas aparecem aqui e as saídas são geradas ao concluir OS com peças."), BorderLayout.CENTER);
            return card;
        }

        corpo.add(tabelaMov, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // Helpers Gerais
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