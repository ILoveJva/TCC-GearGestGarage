package view.paginas;

import controller.OficinaController;
import view.design.ScrollBar;
import view.*;
import view.design.Botao;
import view.design.GerenciadorDeIcones;
import view.design.PainelCartao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Painel de Conteúdo da Página Inicial (Limpo, Otimizado e com Calendário/Botões Corrigidos).
 * Organizado com separação clara entre inicialização e configuração de componentes Swing,
 * além de comentários detalhados em todo o código.
 */
public class V_PaginaInicial extends JPanel {

    // Constantes visuais e de layout
    private static final Color COR_CARTAO = new Color(255, 253, 248, 235);
    private static final int TAM_ICONE_CARD = 65;
    private static final int TAM_ICONE_ESTAT = 36;
    private static final int GAP_CONSULTAS = 16;

    // Declaração dos componentes de interface (Painéis, Rótulos, Botões)
    private JPanel pnl_Header;
    private JPanel pnl_BotoesVisualizar;
    private JLabel lbl_NomeGaragem;
    private JLabel lbl_CnpjGaragem;
    private JLabel lbl_Especialidade;
    private JButton btn_Configuracoes;

    private Botao.CardNavegacao btn_VisServicos;
    private Botao.CardNavegacao btn_VisOrcamentos;
    private Botao.CardNavegacao btn_VisClientes;
    private Botao.CardNavegacao btn_VisVeiculos;
    private Botao.CardNavegacao btn_Estoque;

    // Controlador da oficina
    private OficinaController controller;

    /**
     * Construtor principal da Página Inicial.
     * @param controller Controlador contendo as regras de negócio e dados da oficina.
     */
    public V_PaginaInicial(OficinaController controller) {
        this.controller = controller;

        // Configuração inicial do painel principal
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Chamada dos métodos de construção, dados, layout e ações
        initComponents();
        preencherDadosDinamicos();
        layoutComponents();
        vincularAcoesBotoes();
    }

    /**
     * Inicializa e configura os componentes da interface gráfica.
     * Separado em duas etapas lógicas:
     * 1. INICIALIZAÇÃO: Criação/instanciação de todos os objetos e componentes.
     * 2. CONFIGURAÇÃO: Definição de propriedades, layouts, fontes, cores e tamanhos.
     */
    private void initComponents() {

        // =====================================================================
        // ETAPA 1: TODAS AS INICIALIZAÇÕES (Instanciação dos Objetos) JUNTAS
        // =====================================================================
        pnl_Header = new PainelCartao(new BorderLayout(20, 10), 14, COR_CARTAO);

        JPanel pnl_TextoHeader = new JPanel();
        lbl_NomeGaragem = new JLabel();
        lbl_CnpjGaragem = new JLabel();
        lbl_Especialidade = new JLabel();

        JLabel lbl_AvatarGaragem = new JLabel(GerenciadorDeIcones.obterIcone("/assets/icons/oficina.png", 52, 52));
        JPanel pnl_Identidade = new JPanel(new BorderLayout(14, 0));

        btn_Configuracoes = new BotaoVidro3D();
        JPanel pnl_ContainerBusca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));

        // Seção de Consultas / Cadastros
        pnl_BotoesVisualizar = criarCard("Consultas / Cadastros", GerenciadorDeIcones.obterIcone("/assets/icons/consultas.png", 18, 18));
        JPanel corpoConsultas = (JPanel) pnl_BotoesVisualizar.getComponent(1);

        btn_VisServicos = new Botao.CardNavegacao("Serviços", GerenciadorDeIcones.obterIcone("/assets/icons/vservicos.png", TAM_ICONE_CARD, TAM_ICONE_CARD));
        btn_VisOrcamentos = new Botao.CardNavegacao("Orçamentos", GerenciadorDeIcones.obterIcone("/assets/icons/vorcamentos.png", TAM_ICONE_CARD, TAM_ICONE_CARD));
        btn_VisClientes = new Botao.CardNavegacao("Clientes", GerenciadorDeIcones.obterIcone("/assets/icons/vclientes.png", TAM_ICONE_CARD, TAM_ICONE_CARD));
        btn_VisVeiculos = new Botao.CardNavegacao("Veículos", GerenciadorDeIcones.obterIcone("/assets/icons/vveiculos.png", TAM_ICONE_CARD, TAM_ICONE_CARD));
        btn_Estoque = new Botao.CardNavegacao("Estoque", GerenciadorDeIcones.obterIcone("/assets/icons/estoque.png", TAM_ICONE_CARD, TAM_ICONE_CARD));


        // =====================================================================
        // ETAPA 2: CONFIGURAÇÕES DOS OBJETOS SWING
        // =====================================================================

        // Configuração do Cabeçalho
        pnl_Header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Configuração do painel de texto do cabeçalho
        pnl_TextoHeader.setLayout(new BoxLayout(pnl_TextoHeader, BoxLayout.Y_AXIS));
        pnl_TextoHeader.setOpaque(false);

        // Configuração das labels informativas
        lbl_NomeGaragem.setFont(new Font("Arial", Font.BOLD, 20));
        lbl_NomeGaragem.setAlignmentX(Component.LEFT_ALIGNMENT);

        lbl_CnpjGaragem.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl_CnpjGaragem.setForeground(Color.GRAY);
        lbl_CnpjGaragem.setAlignmentX(Component.LEFT_ALIGNMENT);

        lbl_Especialidade.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl_Especialidade.setForeground(Color.GRAY);
        lbl_Especialidade.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adicionando os textos ao painel correspondente
        pnl_TextoHeader.add(lbl_NomeGaragem);
        pnl_TextoHeader.add(Box.createVerticalStrut(6));
        pnl_TextoHeader.add(lbl_CnpjGaragem);
        pnl_TextoHeader.add(Box.createVerticalStrut(2));
        pnl_TextoHeader.add(lbl_Especialidade);

        // Configuração do avatar da oficina
        lbl_AvatarGaragem.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        // Configuração do painel de identidade visual
        pnl_Identidade.setOpaque(false);
        pnl_Identidade.add(lbl_AvatarGaragem, BorderLayout.WEST);
        pnl_Identidade.add(pnl_TextoHeader, BorderLayout.CENTER);

        // Configuração do botão de configurações
        ImageIcon icoConfig = GerenciadorDeIcones.obterIcone("/assets/icons/config.png", 50, 50);
        if (icoConfig != null) {
            btn_Configuracoes.setIcon(icoConfig);
        } else {
            btn_Configuracoes.setText("Configurações");
            btn_Configuracoes.setFont(new Font("Segoe UI", Font.PLAIN, 26));
            btn_Configuracoes.setForeground(Color.decode("#555555"));
        }
        btn_Configuracoes.setPreferredSize(new Dimension(80, 80));
        btn_Configuracoes.setToolTipText("Configurações da Oficina");

        // Configuração do container de busca/configurações
        pnl_ContainerBusca.setOpaque(false);
        pnl_ContainerBusca.add(btn_Configuracoes);

        // Montagem final do cabeçalho
        pnl_Header.add(pnl_Identidade, BorderLayout.CENTER);
        pnl_Header.add(pnl_ContainerBusca, BorderLayout.EAST);

        // Configuração da seção de consultas e cadastros
        corpoConsultas.setLayout(new GradeResponsiva(Botao.DIM_CARD.width, Botao.DIM_CARD.height, GAP_CONSULTAS, GAP_CONSULTAS));
        corpoConsultas.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

        // Adicionando os cards de navegação ao corpo de consultas
        corpoConsultas.add(btn_VisServicos);
        corpoConsultas.add(btn_VisOrcamentos);
        corpoConsultas.add(btn_VisClientes);
        corpoConsultas.add(btn_VisVeiculos);
        corpoConsultas.add(btn_Estoque);

        // Assegura tamanho adequado para renderizar a grade de botões sem cortes
        pnl_BotoesVisualizar.setPreferredSize(new Dimension(0, Botao.DIM_CARD.height + 95));
        pnl_BotoesVisualizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, Botao.DIM_CARD.height + 180));
        corpoConsultas.revalidate();
    }

    /**
     * Preenche os dados dinâmicos da oficina obtidos através do controller.
     */
    private void preencherDadosDinamicos() {
        if (controller != null && controller.getOficina() != null) {
            lbl_NomeGaragem.setText(controller.getOficina().getNome());
            lbl_CnpjGaragem.setText("CNPJ: " + controller.getOficina().getCnpj());
            lbl_Especialidade.setText("Especialidade: " + controller.getOficina().getDescricao());
        } else {
            lbl_NomeGaragem.setText("Oficina Desconectada");
        }
    }

    /**
     * Realiza o layout e a organização de todos os componentes centrais na tela com barra de rolagem.
     */
    private void layoutComponents() {
        JPanel pnl_Centro = new PainelCentroRolavel();
        pnl_Centro.setLayout(new BoxLayout(pnl_Centro, BoxLayout.Y_AXIS));
        pnl_Centro.setOpaque(false);
        pnl_BotoesVisualizar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adiciona os blocos principais ao painel central rolável
        pnl_Centro.add(pnl_BotoesVisualizar);
        pnl_Centro.add(Box.createVerticalStrut(16));
        pnl_Centro.add(criarSecaoCalendario());
        pnl_Centro.add(Box.createVerticalStrut(16));
        pnl_Centro.add(criarSecaoEstatisticas());

        JScrollPane scroll = new JScrollPane(pnl_Centro);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        ScrollBar.aplicar(scroll);

        // Adiciona os painéis principais ao painel da página inicial
        add(pnl_Header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Vincula os eventos de clique (ActionListeners) aos botões de navegação.
     */
    private void vincularAcoesBotoes() {
        btn_Configuracoes.addActionListener(e -> navegarPara(new V_Configuracoes(this.controller)));
        btn_VisServicos.addActionListener(e -> navegarPara(new V_VisualizarServicos(this.controller)));
        btn_VisOrcamentos.addActionListener(e -> navegarPara(new V_AprovarOrcamento(this.controller)));
        btn_VisClientes.addActionListener(e -> navegarPara(new V_VisualizarClientes(this.controller)));
        btn_VisVeiculos.addActionListener(e -> navegarPara(new V_VisualizarVeiculos(this.controller)));
        btn_Estoque.addActionListener(e -> navegarPara(new V_Estoque(this.controller)));
    }

    /**
     * Realiza a navegação para um novo painel central dentro da janela principal.
     * @param novoPainelCentral Painel de destino.
     */
    private void navegarPara(JPanel novoPainelCentral) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof V_Main) {
            ((V_Main) window).atualizarConteudo(novoPainelCentral);
        } else {
            System.err.println("Erro: V_Main não encontrado como ancestral.");
        }
    }

    /**
     * Cria e retorna a seção do calendário de serviços e prazos.
     * @return Painel contendo o calendário configurado.
     */
    private JPanel criarSecaoCalendario() {
        JPanel card = criarCard("Agenda de Serviços e Prazos",
                GerenciadorDeIcones.obterIcone("/assets/icons/calendario.png", 30, 30));

        PainelCalendario painelCalendario = new PainelCalendario(controller);
        painelCalendario.setPreferredSize(new Dimension(0, 420));

        JPanel pnl_TituloCard = (JPanel) card.getComponent(0);
        JPanel pnl_TituloTexto = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_TituloTexto.setOpaque(false);
        while (pnl_TituloCard.getComponentCount() > 0) {
            pnl_TituloTexto.add(pnl_TituloCard.getComponent(0));
        }
        pnl_TituloCard.setLayout(new BorderLayout());
        pnl_TituloCard.add(pnl_TituloTexto, BorderLayout.WEST);
        pnl_TituloCard.add(painelCalendario.getPainelModos(), BorderLayout.EAST);
        pnl_TituloCard.setPreferredSize(new Dimension(0, 38));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BorderLayout());
        corpo.add(painelCalendario, BorderLayout.CENTER);

        card.setPreferredSize(new Dimension(0, 500));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));
        return card;
    }

    /**
     * Cria e retorna a seção de estatísticas gerais da oficina.
     * @return Painel contendo os cards estatísticos.
     */
    private JPanel criarSecaoEstatisticas() {
        JPanel card = criarCard("Estatísticas da Oficina", GerenciadorDeIcones.obterIcone("/assets/icons/estatisticas.png", 18, 18));

        JPanel linha = new JPanel(new GridLayout(1, 5, 12, 0));
        linha.setOpaque(false);
        linha.add(criarCardEstat("Montadoras", controller.contarMontadoras(), "#9B59B6", "/assets/icons/montadoras.png", () -> navegarPara(new V_VisualizarMontadoras(controller))));
        linha.add(criarCardEstat("Modelos", controller.contarModelos(), "#E67E22", "/assets/icons/modelos.png", () -> navegarPara(new V_VisualizarModelos(controller))));
        linha.add(criarCardEstat("Peças Cadastradas", controller.listarTodasPecas().size(), "#16A085", "/assets/icons/pecas.png", () -> navegarPara(new V_VisualizarPecas(controller))));
        linha.add(criarCardEstat("Serviços Cadastrados", controller.contarCatalogoServicos(), "#8E44AD", "/assets/icons/vservicos.png", () -> navegarPara(new V_VisualizarCatalogoServicos(controller))));
        linha.add(criarCardEstat("OS Total", controller.contarOS(), "#34495E", "/assets/icons/os_total.png", () -> navegarPara(new V_VisualizarOSFiltrado(controller, null))));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BorderLayout());
        corpo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        corpo.add(linha, BorderLayout.CENTER);
        return card;
    }

    /**
     * Método auxiliar para criar um card padrão com título e ícone.
     * @param titulo Título do card.
     * @param icone Ícone exibido ao lado do título.
     * @return Painel customizado de card.
     */
    private JPanel criarCard(String titulo, Icon icone) {
        int raioCard = 14;
        PainelCartao card = new PainelCartao(new BorderLayout(0, 12), raioCard, COR_CARTAO);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(raioCard, Color.decode("#E0E0E0")),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl_TituloCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_TituloCard.setOpaque(false);
        pnl_TituloCard.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        pnl_TituloCard.setPreferredSize(new Dimension(0, 30));

        if (icone != null) pnl_TituloCard.add(new JLabel(icone));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        pnl_TituloCard.add(lbl);

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BorderLayout());

        card.add(pnl_TituloCard, BorderLayout.NORTH);
        card.add(corpo, BorderLayout.CENTER);
        return card;
    }

    /**
     * Cria um card estatístico individual com base nos parâmetros fornecidos.
     */
    private Botao.CardEstatistica criarCardEstat(String rotulo, int valor, String corHex, String caminhoIconePng, Runnable aoClicar) {
        Icon icone = caminhoIconePng != null ? GerenciadorDeIcones.obterIcone(caminhoIconePng, TAM_ICONE_ESTAT, TAM_ICONE_ESTAT) : null;
        Botao.CardEstatistica btn = new Botao.CardEstatistica(rotulo, valor, corHex, icone);
        btn.addActionListener(e -> aoClicar.run());
        return btn;
    }

    /**
     * Classe interna customizada para criar botões com efeito visual de vidro 3D.
     */
    private static class BotaoVidro3D extends JButton {
        private boolean hover = false, pressionado = false;

        BotaoVidro3D() {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)   { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e)  { pressionado = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int raio = 20, elevacao = pressionado ? 1 : (hover ? 3 : 2), topo = pressionado ? 2 : 0;
            g2.setColor(new Color(0, 0, 0, hover ? 45 : 28));
            g2.fillRoundRect(2, 2 + elevacao, w - 4, h - 4, raio, raio);

            g2.setPaint(new GradientPaint(0, topo, hover ? new Color(255, 255, 255, 235) : new Color(255, 255, 255, 215), 0, h - 4, hover ? new Color(206, 221, 236, 215) : new Color(198, 212, 228, 195)));
            g2.fillRoundRect(1, topo, w - 4, h - 4 - topo, raio, raio);

            g2.setColor(new Color(255, 255, 255, hover ? 165 : 130));
            g2.fillRoundRect(4, topo + 2, w - 10, Math.max(4, (h - 4) / 3), raio - 6, raio - 6);

            g2.setColor(new Color(255, 255, 255, 170));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(1, topo, w - 5, h - 5 - topo, raio, raio);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Classe interna para desenhar bordas arredondadas personalizadas.
     */
    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio;
        private final Color cor;
        RoundedBorder(int raio, Color cor) { this.raio = raio; this.cor = cor; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(cor);
            g2d.draw(new RoundRectangle2D.Double(x, y, w - 1, h - 1, raio, raio));
            g2d.dispose();
        }
    }

    /**
     * Classe interna para suporte a painel central rolável otimizado.
     */
    private static class PainelCentroRolavel extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 24; }
        @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return Math.max(24, visibleRect.height - 24); }
        @Override public boolean getScrollableTracksViewportWidth()  { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    /**
     * Gerenciador de layout customizado para organizar elementos em grade responsiva.
     */
    private static class GradeResponsiva implements LayoutManager {
        private final int larguraMin, altura, gapH, gapV;
        private int ultimasLinhas = -1;

        GradeResponsiva(int larguraMin, int altura, int gapH, int gapV) {
            this.larguraMin = larguraMin; this.altura = altura; this.gapH = gapH; this.gapV = gapV;
        }

        private int colunas(int disp, int n) { return Math.max(1, Math.min(Math.max(1, n), (disp + gapH) / (larguraMin + gapH))); }
        private int linhas(int n, int cols) { return n == 0 ? 0 : (n + cols - 1) / cols; }

        @Override public void addLayoutComponent(String name, Component comp) {}
        @Override public void removeLayoutComponent(Component comp) {}
        @Override public Dimension preferredLayoutSize(Container parent) {
            int n = parent.getComponentCount(), disp = parent.getWidth();
            if (disp <= 0) disp = n * larguraMin;
            int cols = colunas(disp, n), rows = linhas(n, cols);
            return new Dimension(parent.getWidth(), rows == 0 ? 0 : rows * altura + (rows - 1) * gapV);
        }
        @Override public Dimension minimumLayoutSize(Container parent) { return preferredLayoutSize(parent); }
        @Override public void layoutContainer(Container parent) {
            int n = parent.getComponentCount();
            if (n == 0) return;
            int disp = parent.getWidth(), cols = colunas(disp, n), rows = linhas(n, cols);
            int larguraCelula = Math.max(larguraMin, (disp - (cols - 1) * gapH) / cols);

            for (int i = 0; i < n; i++) {
                parent.getComponent(i).setBounds((i % cols) * (larguraCelula + gapH), (i / cols) * (altura + gapV), larguraCelula, altura);
            }
            if (rows != ultimasLinhas) {
                ultimasLinhas = rows;
                SwingUtilities.invokeLater(parent::revalidate);
            }
        }
    }
}