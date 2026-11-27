package view;

import controller.OficinaController;
import model.Modelo;
import model.Montadora;
import view.design.BuscaUtil;
import view.design.ScrollBar;
import view.paginas.V_PaginaInicial;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class V_VisualizarModelos extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mexa só aqui para alterar tudo de uma vez
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    // Vidro cinza claro estilo Windows 7 (Aero) — usado no cabeçalho da tabela
    private static final Color COR_AERO_TOPO_A  = Color.decode("#FBFBFC");
    private static final Color COR_AERO_TOPO_B  = Color.decode("#ECEEF1");
    private static final Color COR_AERO_BASE_A  = Color.decode("#DADDE2");
    private static final Color COR_AERO_BASE_B  = Color.decode("#EFF1F3");
    private static final Color COR_AERO_BORDA   = Color.decode("#B6BCC4");
    private static final Color COR_AERO_SEPARA  = Color.decode("#CCD1D8");
    private static final Color COR_AERO_TEXTO   = Color.decode("#3A4149");

    // Tabela de dados
    private static final Color COR_TABELA_FUNDO   = Color.WHITE;
    private static final Color COR_TABELA_GRADE   = Color.decode("#E3E9F0");
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");

    // Botão "Voltar" — tom secundário/cinza (preserva o tom original, só aplica o mesmo vidro)
    private static final Color COR_VOLTAR         = Color.decode("#6C757D");
    private static final Color COR_VOLTAR_CLARA   = Color.decode("#7C848C");
    private static final Color COR_VOLTAR_ESCURA  = Color.decode("#5A6169");

    // Botão "Buscar" — laranja do tema
    private static final Color COR_BUSCAR         = Color.decode("#FF9900");
    private static final Color COR_BUSCAR_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_BUSCAR_ESCURA  = Color.decode("#D98200");

    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_BOTAO = 13;
    private static final int LARGURA_BOTAO       = 130;
    private static final int ALTURA_BOTAO        = 40;
    private static final int ALTURA_CABECALHO    = 32;
    private static final int ALTURA_LINHA_TABELA = 28;

    private static final String TODOS_ANOS = "Todos os anos";

    // Ordem das colunas: Tipo, Montadora, Modelo, Ano, Cód. (o usuário pode reordenar arrastando)
    private static final int COL_TIPO      = 0;
    private static final int COL_MONTADORA = 1;
    private static final int COL_MODELO    = 2;
    private static final int COL_ANO       = 3;
    private static final int COL_ID        = 4;

    private final OficinaController controller;
    private DefaultTableModel mdl;

    private JTextField txtBusca;
    private JComboBox<String> cmbAno;
    private boolean atualizandoCombo = false;

    /** Todas as linhas carregadas (sem filtro), na ordem das colunas do modelo. */
    private final List<Object[]> todasLinhas = new ArrayList<>();

    public V_VisualizarModelos(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        construir();
    }

    private void construir() {
        JPanel card = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        card.setPreferredSize(new Dimension(760, 560));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Página Inicial > Estatísticas > Modelos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_TITULO);

        JButton btn_Novo = new JButton("+ Novo Modelo");
        btn_Novo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Novo.setForeground(Color.WHITE);
        btn_Novo.setBackground(Color.decode("#FF9900"));
        btn_Novo.setFocusPainted(false);
        btn_Novo.setBorderPainted(false);
        btn_Novo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Novo.setPreferredSize(new Dimension(150, 34));
        btn_Novo.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_CadastrarModelo(controller));
        });

        JPanel pnl_Titulo = new JPanel(new BorderLayout());
        pnl_Titulo.setOpaque(false);
        pnl_Titulo.add(titulo, BorderLayout.WEST);
        pnl_Titulo.add(btn_Novo, BorderLayout.EAST);

        // ---- barra de busca + filtro por ano ----
        JPanel pnl_Busca = construirBarraBusca();

        JPanel pnl_Cabecalho = new JPanel(new BorderLayout(0, 12));
        pnl_Cabecalho.setOpaque(false);
        pnl_Cabecalho.add(pnl_Titulo, BorderLayout.NORTH);
        pnl_Cabecalho.add(pnl_Busca, BorderLayout.CENTER);

        // ---- tabela ----
        String[] cols = {"Tipo", "Montadora", "Modelo", "Ano", "Cód."};
        mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(ALTURA_LINHA_TABELA);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setBackground(COR_TABELA_FUNDO);
        tabela.setForeground(COR_TEXTO_CAMPO);
        tabela.setOpaque(true);
        tabela.setShowGrid(true);
        tabela.setGridColor(COR_TABELA_GRADE);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(COR_TABELA_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO_CAMPO);
        tabela.setFillsViewportHeight(true);
        tabela.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());
        tabela.getColumnModel().getColumn(COL_ANO).setMaxWidth(70);
        tabela.getColumnModel().getColumn(COL_ID).setMaxWidth(60);

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setDefaultRenderer(new CabecalhoVidroClaro());
        cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, ALTURA_CABECALHO));
        cabecalho.setReorderingAllowed(true); // usuário pode arrastar as colunas para reordenar
        cabecalho.setOpaque(false);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(COR_TABELA_FUNDO);
        scroll.getViewport().setOpaque(true);
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        ScrollBar.aplicar(scroll);

        BotaoAcao btn_Voltar = new BotaoAcao("← Voltar", COR_VOLTAR, COR_VOLTAR_CLARA, COR_VOLTAR_ESCURA);
        btn_Voltar.setPreferredSize(new Dimension(LARGURA_BOTAO, ALTURA_BOTAO));
        btn_Voltar.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_PaginaInicial(controller));
        });

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        rodape.setOpaque(false);
        rodape.add(btn_Voltar);

        card.add(pnl_Cabecalho, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(card, gbc);

        carregar();
    }

    private JPanel construirBarraBusca() {
        txtBusca = new CampoArredondado();
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusca.setToolTipText("Busque por tipo, montadora, modelo ou código");
        txtBusca.setPreferredSize(new Dimension(0, 34));
        txtBusca.addActionListener(e -> aplicarFiltro()); // Enter também busca

        BotaoAcao btn_Buscar = new BotaoAcao("🔍 Buscar", COR_BUSCAR, COR_BUSCAR_CLARA, COR_BUSCAR_ESCURA);
        btn_Buscar.setPreferredSize(new Dimension(110, 34));
        btn_Buscar.addActionListener(e -> aplicarFiltro());

        JLabel lblAno = new JLabel("Ano:");
        lblAno.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAno.setForeground(COR_LABEL);

        cmbAno = new JComboBox<>();
        cmbAno.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbAno.setBackground(Color.WHITE);
        cmbAno.setUI(new ComboArredondadoUI());
        cmbAno.setOpaque(false);
        cmbAno.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 4));
        cmbAno.setPreferredSize(new Dimension(140, 34));
        cmbAno.addActionListener(e -> { if (!atualizandoCombo) aplicarFiltro(); });

        BotaoAcao btn_Limpar = new BotaoAcao("Limpar", COR_VOLTAR, COR_VOLTAR_CLARA, COR_VOLTAR_ESCURA);
        btn_Limpar.setPreferredSize(new Dimension(90, 34));
        btn_Limpar.addActionListener(e -> {
            txtBusca.setText("");
            atualizandoCombo = true;
            cmbAno.setSelectedIndex(0);
            atualizandoCombo = false;
            aplicarFiltro();
        });

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);
        direita.add(btn_Buscar);
        direita.add(lblAno);
        direita.add(cmbAno);
        direita.add(btn_Limpar);

        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setOpaque(false);
        barra.add(txtBusca, BorderLayout.CENTER);
        barra.add(direita, BorderLayout.EAST);
        return barra;
    }

    private void carregar() {
        todasLinhas.clear();
        TreeSet<Integer> anos = new TreeSet<>(java.util.Comparator.reverseOrder());

        for (Montadora mont : controller.montadorasComModelos()) {
            for (Modelo mod : mont.listarModelos()) {
                if (mod.getAno() > 0) anos.add(mod.getAno());
                todasLinhas.add(new Object[]{
                        mod.getTipo(),
                        mont.getNome(),
                        mod.getNome(),
                        mod.getAno() > 0 ? String.valueOf(mod.getAno()) : "—",
                        String.format("%04d", mod.getIdModelo())
                });
            }
        }

        // Popula o filtro de ano preservando a seleção atual (se ainda existir)
        Object selecionado = cmbAno.getSelectedItem();
        atualizandoCombo = true;
        cmbAno.removeAllItems();
        cmbAno.addItem(TODOS_ANOS);
        for (Integer a : anos) cmbAno.addItem(String.valueOf(a));
        if (selecionado != null) cmbAno.setSelectedItem(selecionado);
        if (cmbAno.getSelectedItem() == null) cmbAno.setSelectedIndex(0);
        atualizandoCombo = false;

        aplicarFiltro();
    }

    /** Aplica o termo digitado + o ano escolhido sobre as linhas carregadas. */
    private void aplicarFiltro() {
        String termo = txtBusca != null ? txtBusca.getText().trim() : "";
        String ano = cmbAno != null ? (String) cmbAno.getSelectedItem() : null;
        boolean filtraAno = ano != null && !TODOS_ANOS.equals(ano);

        mdl.setRowCount(0);
        for (Object[] linha : todasLinhas) {
            if (filtraAno && !ano.equals(linha[COL_ANO])) continue;
            if (!termo.isEmpty()) {
                boolean bate = false;
                for (int c : new int[]{COL_TIPO, COL_MONTADORA, COL_MODELO, COL_ID}) {
                    if (BuscaUtil.contem(String.valueOf(linha[c]), termo)) { bate = true; break; }
                }
                if (!bate) continue;
            }
            mdl.addRow(linha);
        }
    }

    // =========================================================================
    // INNER CLASSES — RENDERERS DA TABELA
    // =========================================================================

    /**
     * Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7):
     * gradiente claro na metade superior, tom mais escuro na metade inferior,
     * brilho de vidro no topo, separador entre colunas e linha de base.
     */
    private static class CabecalhoVidroClaro extends JLabel implements TableCellRenderer {

        CabecalhoVidroClaro() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(COR_AERO_TEXTO);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
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

    /** Células de dados sempre com fundo branco (e destaque no tom do tema quando selecionadas). */
    private static class CelulaBrancaRenderer extends DefaultTableCellRenderer {
        CelulaBrancaRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(isSelected ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
            setForeground(COR_TEXTO_CAMPO);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    // =========================================================================
    // INNER CLASSES — COMPONENTES EM VIDRO
    // =========================================================================

    /** Painel com fundo em gradiente suave, harmonizando o cartão com o restante das telas. */
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

    /** Campo de texto com fundo e borda arredondados; borda laranja quando focado. */
    private static class CampoArredondado extends JTextField {
        private static final int RAIO = 14;
        private boolean focado = false;

        CampoArredondado() {
            setOpaque(false);
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
            g2.setColor(focado ? Color.decode("#FF9900") : Color.decode("#CCCCCC"));
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

    /**
     * Botão de ação com a mesma linguagem visual dos campos em vidro das outras
     * telas: cantos arredondados, sombra suave, reflexo no topo e reação a
     * hover/clique. A cor é configurável (cinza para navegação, laranja para a busca).
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
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
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

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}