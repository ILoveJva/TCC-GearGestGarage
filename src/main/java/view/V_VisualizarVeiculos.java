package view;

import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * Painel de Conteudo para a Listagem de Veiculos.
 * Suporte a duplo clique para abrir os detalhes do veiculo.
 */
public class V_VisualizarVeiculos extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mesma linguagem visual de V_VisualizarServicos e
    // V_VisualizarMontadoras, com o efeito de vidro já reduzido (mais discreto,
    // não removido).
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    // Vidro cinza claro estilo Aero — usado no cabeçalho da tabela, com o brilho
    // já suavizado (menos opaco, faixa de reflexo mais fina que nas telas antigas)
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

    // Ação principal: "+ Cadastrar Veículo" (tom laranja original preservado)
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_BOTAO = 13;
    private static final int ALTURA_BOTAO        = 34;
    private static final int ALTURA_CABECALHO    = 26;
    private static final int ALTURA_LINHA_TABELA = 28;

    private JPanel pnl_CardCentral;
    private JLabel lbl_MapaNavegacao;
    private BotaoAcao btn_CadastrarVeiculo;
    private Consumer<Long> acaoAbrirDetalhes;

    private JTable tbl_Veiculos;
    private DefaultTableModel mdl_Veiculos;
    private JScrollPane scp_ScrollVeiculos;

    private OficinaController controller;

    public V_VisualizarVeiculos(OficinaController controller) {
        this.controller = controller;

        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);

        initComponents();
        layoutComponents();
        configurarEventosTabela();
        carregarDadosVeiculos();
    }

    private void initComponents() {
        pnl_CardCentral = new PainelGradiente(new BorderLayout(0, 15), COR_CARD_TOPO, COR_CARD_BASE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 560));
        pnl_CardCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lbl_MapaNavegacao = new JLabel("Página Inicial > Consultar Veículos");
        lbl_MapaNavegacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_MapaNavegacao.setForeground(COR_TITULO);

        String[] colunas = {"Cód.", "Veículo", "Tipo", "Placa"};
        mdl_Veiculos = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tbl_Veiculos = new JTable(mdl_Veiculos);
        tbl_Veiculos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Veiculos.setRowHeight(ALTURA_LINHA_TABELA);
        tbl_Veiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_Veiculos.setBackground(COR_TABELA_FUNDO);
        tbl_Veiculos.setForeground(COR_TEXTO_CAMPO);
        tbl_Veiculos.setOpaque(true);
        tbl_Veiculos.setShowGrid(true);
        tbl_Veiculos.setGridColor(COR_TABELA_GRADE);
        tbl_Veiculos.setIntercellSpacing(new Dimension(0, 0));
        tbl_Veiculos.setSelectionBackground(COR_TABELA_SELECAO);
        tbl_Veiculos.setSelectionForeground(COR_TEXTO_CAMPO);
        tbl_Veiculos.setFillsViewportHeight(true);
        tbl_Veiculos.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());
        tbl_Veiculos.getColumnModel().getColumn(0).setMaxWidth(80);

        JTableHeader cabecalho = tbl_Veiculos.getTableHeader();
        cabecalho.setDefaultRenderer(new CabecalhoVidroClaro());
        cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, ALTURA_CABECALHO));
        cabecalho.setReorderingAllowed(false);
        cabecalho.setOpaque(false);

        scp_ScrollVeiculos = new JScrollPane(tbl_Veiculos);
        scp_ScrollVeiculos.getViewport().setBackground(COR_TABELA_FUNDO);
        scp_ScrollVeiculos.getViewport().setOpaque(true);
        scp_ScrollVeiculos.setOpaque(false);
        scp_ScrollVeiculos.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        ScrollBarPadrao.aplicar(scp_ScrollVeiculos);

        ImageIcon icoVeiculo = carregarIcone("/assets/icons/add_veiculo.png", 18, 18);
        btn_CadastrarVeiculo = new BotaoAcao("+ Cadastrar Veículo", icoVeiculo, COR_ACAO, COR_ACAO_CLARA, COR_ACAO_ESCURA);
        btn_CadastrarVeiculo.setPreferredSize(new Dimension(190, ALTURA_BOTAO));
        btn_CadastrarVeiculo.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(V_VisualizarVeiculos.this);
            if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_CadastrarVeiculo(controller));
        });
    }

    private void layoutComponents() {
        JPanel pnl_HeaderCard = new JPanel(new BorderLayout());
        pnl_HeaderCard.setOpaque(false);
        pnl_HeaderCard.add(lbl_MapaNavegacao, BorderLayout.WEST);
        pnl_HeaderCard.add(btn_CadastrarVeiculo, BorderLayout.EAST);
        pnl_CardCentral.add(pnl_HeaderCard, BorderLayout.NORTH);
        pnl_CardCentral.add(scp_ScrollVeiculos, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    private void configurarEventosTabela() {
        tbl_Veiculos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tbl_Veiculos.getSelectedRow() != -1) {
                    int linha = tbl_Veiculos.getSelectedRow();
                    Object valorId = tbl_Veiculos.getValueAt(linha, 0);
                    if (valorId != null && acaoAbrirDetalhes != null) {
                        try {
                            long idVeiculo = Long.parseLong(valorId.toString());
                            acaoAbrirDetalhes.accept(idVeiculo);
                        } catch (NumberFormatException ex) {
                            System.err.println("Erro ao converter ID do veiculo: " + valorId);
                        }
                    }
                }
            }
        });
    }

    public void carregarDadosVeiculos() {
        mdl_Veiculos.setRowCount(0);
        if (controller == null) return;
        for (Veiculo veiculo : controller.listarVeiculos()) {
            String montadora = (veiculo.getModelo() != null && veiculo.getModelo().getMontadora() != null)
                    ? veiculo.getModelo().getMontadora().getNome() : "";
            String modelo = veiculo.getModelo() != null ? veiculo.getModelo().getNome() : "";
            mdl_Veiculos.addRow(new Object[]{
                    String.format("%04d", veiculo.getIdVeiculo()),
                    (montadora + " " + modelo).trim(),
                    veiculo.getTipo(),
                    veiculo.getPlaca()
            });
        }
    }

    public void setAcaoAbrirDetalhes(Consumer<Long> acaoAbrirDetalhes) {
        this.acaoAbrirDetalhes = acaoAbrirDetalhes;
    }

    public JTable getTbl_Veiculos() { return tbl_Veiculos; }

    private ImageIcon carregarIcone(String caminho, int w, int h) {
        java.net.URL url = getClass().getResource(caminho);
        if (url == null) {
            String s = caminho.startsWith("/") ? caminho.substring(1) : caminho;
            url = Thread.currentThread().getContextClassLoader().getResource(s);
        }
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    // =========================================================================
    // INNER CLASSES — RENDERERS DA TABELA
    // =========================================================================

    /**
     * Cabeçalho de coluna com vidro cinza claro no estilo Aero, igual às demais
     * telas de listagem — porém com a faixa de brilho mais fina e discreta
     * (efeito de vidro reduzido, não removido) e altura de célula mais enxuta.
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

    /**
     * Botão de ação com a mesma linguagem visual em vidro das outras telas:
     * cantos arredondados, sombra suave, reflexo no topo (já reduzido — mais
     * discreto que nas telas antigas) e reação a hover/clique. Aceita um ícone
     * opcional para preservar o carregamento de PNG já usado nesta tela.
     */
    private static class BotaoAcao extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Icon icone, Color corBase, Color corClara, Color corEscura) {
            super(texto, icone);
            this.corBase = corBase;
            this.corClara = corClara;
            this.corEscura = corEscura;
            setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_BOTAO));
            setForeground(Color.WHITE);
            setIconTextGap(8);
            setHorizontalTextPosition(SwingConstants.RIGHT);
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

        BotaoAcao(String texto, Color corBase, Color corClara, Color corEscura) {
            this(texto, null, corBase, corClara, corEscura);
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

            g2.setColor(new Color(255, 255, 255, 30));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}