package view;

import controller.OficinaController;
import model.OrdemDeServico;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * Painel de Conteúdo da Página Inicial.
 * Dados do cabeçalho carregados dinamicamente via Controller e Model Oficina.
 */
public class V_PaginaInicial extends JPanel {

    // ==========================================
    // DECLARAÇÃO DOS COMPONENTES (Padrão Prefixo)
    // ==========================================
    private JPanel pnl_Header;
    private JPanel pnl_BotoesVisualizar;

    // Elementos do Cabeçalho
    private JLabel lbl_NomeGaragem;
    private JLabel lbl_CnpjGaragem;
    private JLabel lbl_Especialidade;
    private JButton btn_PesquisarHeader;
    private JButton btn_Configuracoes;

    // Botões de Visualização
    private JButton btn_VisServicos;
    private JButton btn_VisOrcamentos;
    private JButton btn_VisClientes;
    private JButton btn_VisVeiculos;

    // Referência do Controller do Sistema
    private OficinaController controller;

    /**
     * Construtor Corrigido: Recebe o Controller e remove o Hardcode.
     */
    public V_PaginaInicial(OficinaController controller) {
        this.controller = controller;

        // Configura o fundo cinza claro idêntico ao protótipo para destacar os cards brancos
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        preencherDadosDinamicos(); // Alimenta o cabeçalho com os dados do Model
        layoutComponents();
        vincularAcoesBotoes();
    }

    private void initComponents() {
        // --------------------------------------------------------------------
        // 1. MÓDULO DO CABEÇALHO (Informações do Topo)
        // --------------------------------------------------------------------
        pnl_Header = new JPanel(new BorderLayout(20, 10));
        pnl_Header.setBackground(Color.WHITE);
        pnl_Header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel pnl_TextoHeader = new JPanel(new GridLayout(3, 1, 5, 5));
        pnl_TextoHeader.setOpaque(false);

        // Inicializados vazios; serão preenchidos pelo método preencherDadosDinamicos()
        lbl_NomeGaragem = new JLabel();
        lbl_NomeGaragem.setFont(new Font("Arial", Font.BOLD, 20));

        lbl_CnpjGaragem = new JLabel();
        lbl_CnpjGaragem.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl_CnpjGaragem.setForeground(Color.GRAY);

        lbl_Especialidade = new JLabel();
        lbl_Especialidade.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl_Especialidade.setForeground(Color.GRAY);

        pnl_TextoHeader.add(lbl_NomeGaragem);
        pnl_TextoHeader.add(lbl_CnpjGaragem);
        pnl_TextoHeader.add(lbl_Especialidade);

        btn_PesquisarHeader = new JButton("");
        ImageIcon icoBusca = carregarERedimensionarIcone("/assets/icons/pesquisar.png", 50, 50);
        if (icoBusca != null) btn_PesquisarHeader.setIcon(icoBusca);
        btn_PesquisarHeader.setFocusPainted(false);
        btn_PesquisarHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_PesquisarHeader.setPreferredSize(new Dimension(80, 80));
        btn_PesquisarHeader.setBackground(Color.WHITE);

        btn_Configuracoes = new JButton("");
        ImageIcon icoConfig = carregarERedimensionarIcone("/assets/icons/config.png", 50, 50);
        if (icoConfig != null) {
            btn_Configuracoes.setIcon(icoConfig);
        } else {
            btn_Configuracoes.setText("⚙");
            btn_Configuracoes.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        }
        btn_Configuracoes.setFocusPainted(false);
        btn_Configuracoes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Configuracoes.setPreferredSize(new Dimension(80, 80));
        btn_Configuracoes.setBackground(Color.WHITE);
        btn_Configuracoes.setToolTipText("Configurações da Oficina");

        JPanel pnl_ContainerBusca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnl_ContainerBusca.setOpaque(false);
        pnl_ContainerBusca.add(btn_Configuracoes);
        pnl_ContainerBusca.add(btn_PesquisarHeader);

        pnl_Header.add(pnl_TextoHeader, BorderLayout.CENTER);
        pnl_Header.add(pnl_ContainerBusca, BorderLayout.EAST);

        Dimension dimCardBtn = new Dimension(140, 110);

        int icoW = 65;
        int icoH = 65;

        // --------------------------------------------------------------------
        // 2. MÓDULO DE SEÇÃO: VISUALIZAR INFORMAÇÕES (Consultas / Os 4 botões)
        // --------------------------------------------------------------------
        pnl_BotoesVisualizar = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 50));
        pnl_BotoesVisualizar.setBackground(Color.WHITE);
        pnl_BotoesVisualizar.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Consultas",
                0, 0, new Font("Arial", Font.BOLD, 14), Color.DARK_GRAY));

        btn_VisServicos = new JButton("Serviços");
        configurarEstiloBotaoCard(btn_VisServicos, "/assets/icons/vservicos.png", icoW, icoH, dimCardBtn);

        btn_VisOrcamentos = new JButton("Orçamentos");
        configurarEstiloBotaoCard(btn_VisOrcamentos, "/assets/icons/vorcamentos.png", icoW, icoH, dimCardBtn);

        btn_VisClientes = new JButton("Clientes");
        configurarEstiloBotaoCard(btn_VisClientes, "/assets/icons/vclientes.png", icoW, icoH, dimCardBtn);

        btn_VisVeiculos = new JButton("Veículos");
        configurarEstiloBotaoCard(btn_VisVeiculos, "/assets/icons/vveiculos.png", icoW, icoH, dimCardBtn);

        pnl_BotoesVisualizar.add(btn_VisServicos);
        pnl_BotoesVisualizar.add(btn_VisOrcamentos);
        pnl_BotoesVisualizar.add(btn_VisClientes);
        pnl_BotoesVisualizar.add(btn_VisVeiculos);
    }

    /**
     * Remove o hardcode injetando as variáveis vindas da classe Oficina (Model).
     */
    private void preencherDadosDinamicos() {
        if (controller != null) {
            lbl_NomeGaragem.setText(controller.getOficina().getNome());
            lbl_CnpjGaragem.setText("CNPJ: " + controller.getOficina().getCnpj());
            lbl_Especialidade.setText("Especialidade: " + controller.getOficina().getDescricao());
        } else {
            lbl_NomeGaragem.setText("Oficina Desconectada");
        }
    }

    private void layoutComponents() {
        JPanel pnl_Centro = new JPanel();
        pnl_Centro.setLayout(new BoxLayout(pnl_Centro, BoxLayout.Y_AXIS));
        pnl_Centro.setOpaque(false);
        pnl_BotoesVisualizar.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Centro.add(pnl_BotoesVisualizar);
        pnl_Centro.add(Box.createVerticalStrut(16));
        pnl_Centro.add(criarSecaoEstatisticas());

        JScrollPane scroll = new JScrollPane(pnl_Centro);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        add(pnl_Header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Vincula os cliques de navegação repassando o "controller" para as novas instâncias.
     */
    private void vincularAcoesBotoes() {
        btn_PesquisarHeader.addActionListener(e -> navegarPara(new V_PesquisarGeral(this.controller)));
        btn_Configuracoes.addActionListener(e -> navegarPara(new V_Configuracoes(this.controller)));

        // Fluxos de Visualização / Consulta
        btn_VisServicos.addActionListener(e -> navegarPara(new V_VisualizarServicos(this.controller)));
        btn_VisOrcamentos.addActionListener(e -> navegarPara(new V_AprovarOrcamento(this.controller)));
        btn_VisClientes.addActionListener(e -> navegarPara(new V_VisualizarClientes(this.controller)));
        btn_VisVeiculos.addActionListener(e -> navegarPara(new V_VisualizarVeiculos(this.controller)));
    }

    /**
     * Captura o Frame Principal (V_Main) e atualiza o painel central dinamicamente.
     */
    private void navegarPara(JPanel novoPainelCentral) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof V_Main) {
            V_Main mainFrame = (V_Main) window;
            mainFrame.atualizarConteudo(novoPainelCentral);
        } else {
            System.err.println("Erro: V_Main não encontrado como ancestral.");
        }
    }

    // ==========================================
    // SEÇÃO DE ESTATÍSTICAS
    // ==========================================

    private JPanel criarSecaoEstatisticas() {
        JPanel card = criarCard("Estatísticas do Sistema");

        int totalMontadoras = controller.contarMontadoras();
        int totalModelos    = controller.contarModelos();
        int totalPecas      = controller.listarTodasPecas().size();
        int totalCatalogo   = controller.contarCatalogoServicos();
        int totalOS         = controller.contarOS();
        int osAbertas       = controller.contarOSPorStatus(OrdemDeServico.Status.ABERTA);
        int osAndamento     = controller.contarOSPorStatus(OrdemDeServico.Status.EM_ANDAMENTO);
        int osConcluidas    = controller.contarOSPorStatus(OrdemDeServico.Status.CONCLUIDA);

        JPanel linha1 = new JPanel(new GridLayout(1, 4, 12, 0));
        linha1.setOpaque(false);
        linha1.add(criarCardEstat("Montadoras",           totalMontadoras, "#9B59B6", () -> navegarPara(new V_VisualizarMontadoras(controller))));
        linha1.add(criarCardEstat("Modelos",              totalModelos,    "#E67E22", () -> navegarPara(new V_VisualizarModelos(controller))));
        linha1.add(criarCardEstat("Peças Cadastradas",    totalPecas,      "#16A085", () -> navegarPara(new V_VisualizarPecas(controller))));
        linha1.add(criarCardEstat("Serviços Cadastrados", totalCatalogo,   "#8E44AD", () -> navegarPara(new V_VisualizarCatalogoServicos(controller))));

        JPanel linha2 = new JPanel(new GridLayout(1, 4, 12, 0));
        linha2.setOpaque(false);
        linha2.add(criarCardEstat("OS Total",     totalOS,      "#34495E", () -> navegarPara(new V_VisualizarOSFiltrado(controller, null))));
        linha2.add(criarCardEstat("OS Abertas",   osAbertas,    "#E74C3C", () -> navegarPara(new V_VisualizarOSFiltrado(controller, OrdemDeServico.Status.ABERTA))));
        linha2.add(criarCardEstat("Em Andamento", osAndamento,  "#F39C12", () -> navegarPara(new V_VisualizarOSFiltrado(controller, OrdemDeServico.Status.EM_ANDAMENTO))));
        linha2.add(criarCardEstat("Concluídas",   osConcluidas, "#27AE60", () -> navegarPara(new V_VisualizarOSFiltrado(controller, OrdemDeServico.Status.CONCLUIDA))));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.add(linha1);
        corpo.add(Box.createVerticalStrut(10));
        corpo.add(linha2);
        return card;
    }

    private JPanel criarCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        lbl.setPreferredSize(new Dimension(0, 30));

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BorderLayout());

        card.add(lbl, BorderLayout.NORTH);
        card.add(corpo, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardEstat(String rotulo, int valor, String corHex, Runnable aoClicar) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel barra = new JPanel();
        barra.setBackground(Color.decode(corHex));
        barra.setPreferredSize(new Dimension(0, 4));

        JLabel num = new JLabel(String.valueOf(valor), SwingConstants.CENTER);
        num.setFont(new Font("Segoe UI", Font.BOLD, 28));
        num.setForeground(Color.decode(corHex));

        JLabel lbl = new JLabel(rotulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Color.decode("#666666"));

        JLabel lbl_Dica = new JLabel("Ver detalhes →", SwingConstants.CENTER);
        lbl_Dica.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl_Dica.setForeground(Color.decode(corHex));

        JPanel sul = new JPanel(new BorderLayout(0, 2));
        sul.setOpaque(false);
        sul.add(lbl, BorderLayout.NORTH);
        sul.add(lbl_Dica, BorderLayout.SOUTH);

        MouseAdapter clique = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { aoClicar.run(); }
            @Override public void mouseEntered(MouseEvent e) { p.setBackground(Color.decode("#F9F9F9")); }
            @Override public void mouseExited(MouseEvent e)  { p.setBackground(Color.WHITE); }
        };
        p.addMouseListener(clique);
        num.addMouseListener(clique);
        lbl.addMouseListener(clique);
        lbl_Dica.addMouseListener(clique);

        p.add(barra, BorderLayout.NORTH);
        p.add(num,   BorderLayout.CENTER);
        p.add(sul,   BorderLayout.SOUTH);
        return p;
    }

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
            g2d.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2d.dispose();
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE ESTILIZAÇÃO E MÍDIA
    // ==========================================

    /**
     * Configura o botão no formato de Card (Ícone acima do texto, fundo branco/cinza claro clean)
     */
    private void configurarEstiloBotaoCard(JButton btn, String caminhoIcone, int larguraIco, int alturaIco, Dimension dimensao) {
        // Carrega e adiciona o ícone
        ImageIcon icoBotao = carregarERedimensionarIcone(caminhoIcone, larguraIco, alturaIco);
        if (icoBotao != null) {
            btn.setIcon(icoBotao);
        }

        // Força o texto para baixo e centraliza o ícone
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);

        // Estilização visual limpa (Clean flat design)
        btn.setPreferredSize(dimensao);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBackground(Color.WHITE); // Fundo do Card branco
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Adiciona um pequeno espaçamento entre o ícone e o texto
        btn.setIconTextGap(10);
    }

    /**
     * Método idêntico ao do Menu Lateral para buscar os recursos de imagem de forma segura.
     */
    private ImageIcon carregarERedimensionarIcone(String caminho, int larguraAlvo, int alturaAlvo) {
        URL url = getClass().getResource(caminho);
        if (url == null) {
            String caminhoLimpo = caminho.startsWith("/") ? caminho.substring(1) : caminho;
            url = Thread.currentThread().getContextClassLoader().getResource(caminhoLimpo);
        }

        if (url != null) {
            ImageIcon imagemOriginal = new ImageIcon(url);
            Image imgEscalada = imagemOriginal.getImage().getScaledInstance(larguraAlvo, alturaAlvo, Image.SCALE_SMOOTH);
            return new ImageIcon(imgEscalada);
        }
        return null;
    }
}