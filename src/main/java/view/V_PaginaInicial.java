package view;

import controller.OficinaController;
import javax.swing.*;
import java.awt.*;
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
    private JPanel pnl_BotoesAcao;
    private JPanel pnl_BotoesVisualizar;

    // Elementos do Cabeçalho
    private JLabel lbl_NomeGaragem;
    private JLabel lbl_CnpjGaragem;
    private JLabel lbl_Especialidade;
    private JButton btn_PesquisarHeader;

    // Botões de Ações Rápidas
    private JButton btn_AdicionarServico;
    private JButton btn_AdicionarCliente;
    private JButton btn_AdicionarVeiculo;

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

        // Botão de Busca do Header com a nova padronização de imagem (tamanho 20x20 para o topo)
        btn_PesquisarHeader = new JButton("");
        ImageIcon icoBusca = carregarERedimensionarIcone("/assets/icons/pesquisar.png", 50, 50);
        if (icoBusca != null) btn_PesquisarHeader.setIcon(icoBusca);
        btn_PesquisarHeader.setFocusPainted(false);
        btn_PesquisarHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_PesquisarHeader.setPreferredSize(new Dimension(80, 80));
        btn_PesquisarHeader.setBackground(Color.WHITE);

        JPanel pnl_ContainerBusca = new JPanel(new GridBagLayout());
        pnl_ContainerBusca.setOpaque(false);
        pnl_ContainerBusca.add(btn_PesquisarHeader);

        pnl_Header.add(pnl_TextoHeader, BorderLayout.CENTER);
        pnl_Header.add(pnl_ContainerBusca, BorderLayout.EAST);

        // Dimensões aumentadas para acomodar perfeitamente o layout vertical (Ícone em cima, texto em baixo)
        Dimension dimCardBtn = new Dimension(160, 130);

        // Tamanho padrão dos ícones internos dos botões conforme o protótipo
        int icoW = 80;
        int icoH = 80;

        // --------------------------------------------------------------------
        // 2. MÓDULO DE SEÇÃO: AÇÕES RÁPIDAS (Inserção / Os 3 botões)
        // --------------------------------------------------------------------
        pnl_BotoesAcao = new JPanel(new FlowLayout(FlowLayout.LEFT, 75, 45));
        pnl_BotoesAcao.setBackground(Color.WHITE);
        pnl_BotoesAcao.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Cadastros",
                0, 0, new Font("Arial", Font.BOLD, 14), Color.DARK_GRAY));

        btn_AdicionarServico = new JButton("Novo Serviço");
        configurarEstiloBotaoCard(btn_AdicionarServico, "/assets/icons/add_servico.png", icoW, icoH, dimCardBtn);

        btn_AdicionarCliente = new JButton("Novo Cliente");
        configurarEstiloBotaoCard(btn_AdicionarCliente, "/assets/icons/add_cliente.png", icoW, icoH, dimCardBtn);

        btn_AdicionarVeiculo = new JButton("Novo Veículo");
        configurarEstiloBotaoCard(btn_AdicionarVeiculo, "/assets/icons/add_veiculo.png", icoW, icoH, dimCardBtn);

        pnl_BotoesAcao.add(btn_AdicionarServico);
        pnl_BotoesAcao.add(btn_AdicionarCliente);
        pnl_BotoesAcao.add(btn_AdicionarVeiculo);

        // --------------------------------------------------------------------
        // 3. MÓDULO DE SEÇÃO: VISUALIZAR INFORMAÇÕES (Consultas / Os 4 botões)
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
        JPanel pnl_CorpoCards = new JPanel(new GridLayout(2, 1, 20, 20));
        pnl_CorpoCards.setOpaque(false);
        pnl_CorpoCards.add(pnl_BotoesAcao);
        pnl_CorpoCards.add(pnl_BotoesVisualizar);

        add(pnl_Header, BorderLayout.NORTH);
        add(pnl_CorpoCards, BorderLayout.CENTER);
    }

    /**
     * Vincula os cliques de navegação repassando o "controller" para as novas instâncias.
     */
    private void vincularAcoesBotoes() {
        // Botão de busca superior
        btn_PesquisarHeader.addActionListener(e -> navegarPara(new V_PesquisarGeral(this.controller)));

        // Fluxos de Inserção (Ações Rápidas)
        btn_AdicionarCliente.addActionListener(e -> navegarPara(new V_CadastrarCliente(this.controller)));
        btn_AdicionarVeiculo.addActionListener(e -> navegarPara(new V_CadastrarVeiculo(this.controller)));

        // Fluxos de Visualização / Consulta
        btn_VisClientes.addActionListener(e -> navegarPara(new V_VisualizarClientes(this.controller)));
        btn_VisVeiculos.addActionListener(e -> navegarPara(new V_VisualizarVeiculos(this.controller)));
        btn_VisServicos.addActionListener(e -> navegarPara(new V_VisualizarServicos(this.controller)));
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