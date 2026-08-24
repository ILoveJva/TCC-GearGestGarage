package view;

import controller.OficinaController;
import model.OrdemDeServico;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * Painel de Conteúdo da Página Inicial.
 * Dados do cabeçalho carregados dinamicamente via Controller e Model Oficina.
 *
 * Camada visual reorganizada com base em princípios de Gestalt:
 * - Proximidade: itens relacionados (ex.: CNPJ + Especialidade, grupos de estatísticas) foram
 *   aproximados entre si e afastados de itens não relacionados.
 * - Similaridade / Unidade Comum: "Consultas" e "Estatísticas do Sistema" passaram a usar o
 *   mesmo padrão de cartão (mesmo cabeçalho, mesma borda), reforçando que são seções do mesmo nível.
 * - Figura-Fundo: feedback de hover foi padronizado em todos os elementos clicáveis.
 * - Fechamento: bordas arredondadas e divisores sutis continuam delimitando cada agrupamento.
 * Nesta rodada: os fundos brancos lisos viraram cards com opacidade (deixam a cor de fundo da
 * tela "respirar" nos cantos), a scrollbar ganhou estilo e animação próprios, e cada seção
 * passou a ter um ícone garantido — se o PNG em /assets/icons/ ainda não existir, um ícone
 * vetorial equivalente é desenhado na hora (sem depender de nenhum arquivo externo).
 * Nenhuma cor de destaque do tema, dado, chamada de Controller ou fluxo de navegação foi alterado.
 */
public class V_PaginaInicial extends JPanel {

    // ==========================================
    // PALETA DO "FUNDO BRANCO" DOS CARDS
    // Antes era Color.WHITE liso. Agora é um branco levemente amarelado e com opacidade,
    // pintado por dentro (PainelCartao) para permitir cantos realmente arredondados.
    // ==========================================
    private static final Color COR_CARTAO              = new Color(255, 253, 248, 235);
    private static final Color COR_CARTAO_HOVER         = new Color(255, 248, 235, 245);
    private static final Color COR_CARTAO_OPACA          = new Color(255, 253, 248); // versão 100% opaca p/ JButton
    private static final Color COR_CARTAO_OPACA_HOVER    = new Color(255, 248, 235);

    // ==========================================
    // PADRÃO ÚNICO DE "BOTÃO-CARD" (Similaridade)
    // Usado tanto pelos botões de "Consultas" quanto pelos cards de "Estatísticas",
    // garantindo que os dois grupos tenham exatamente o mesmo tamanho e ícone.
    // ==========================================
    private static final Dimension DIM_BOTAO_CARD    = new Dimension(140, 110);
    private static final int       TAM_ICONE_CARD    = 65;
    private static final int       TAM_ICONE_ESTAT   = 36; // menor que o de Consultas: sobra espaço p/ número + descrição

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
        pnl_Header = new PainelCartao(new BorderLayout(20, 10), 14, COR_CARTAO);
        pnl_Header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Princípio da Proximidade: o Nome fica com maior respiro acima, enquanto CNPJ e
        // Especialidade (informações secundárias, do mesmo "tipo") ficam coladas entre si,
        // sinalizando visualmente que pertencem ao mesmo grupo de metadados.
        JPanel pnl_TextoHeader = new JPanel();
        pnl_TextoHeader.setLayout(new BoxLayout(pnl_TextoHeader, BoxLayout.Y_AXIS));
        pnl_TextoHeader.setOpaque(false);

        // Inicializados vazios; serão preenchidos pelo método preencherDadosDinamicos()
        lbl_NomeGaragem = new JLabel();
        lbl_NomeGaragem.setFont(new Font("Arial", Font.BOLD, 20));
        lbl_NomeGaragem.setAlignmentX(Component.LEFT_ALIGNMENT);

        lbl_CnpjGaragem = new JLabel();
        lbl_CnpjGaragem.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl_CnpjGaragem.setForeground(Color.GRAY);
        lbl_CnpjGaragem.setAlignmentX(Component.LEFT_ALIGNMENT);

        lbl_Especialidade = new JLabel();
        lbl_Especialidade.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl_Especialidade.setForeground(Color.GRAY);
        lbl_Especialidade.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnl_TextoHeader.add(lbl_NomeGaragem);
        pnl_TextoHeader.add(Box.createVerticalStrut(6));   // separa o "grupo principal" do "grupo secundário"
        pnl_TextoHeader.add(lbl_CnpjGaragem);
        pnl_TextoHeader.add(Box.createVerticalStrut(2));   // aproxima CNPJ e Especialidade (mesmo grupo)
        pnl_TextoHeader.add(lbl_Especialidade);

        // Ícone de identidade da garagem: dá um ponto focal (imagem) ao cabeçalho.
        // Tenta o PNG do projeto; se ainda não existir, desenha um ícone vetorial equivalente
        // na hora — a imagem SEMPRE aparece, não depende de nenhum arquivo externo.
        JLabel lbl_AvatarGaragem = new JLabel(obterIcone("/assets/icons/oficina.png", 52, IconeVetorial.Tipo.GARAGEM, Color.decode("#FF9900")));
        lbl_AvatarGaragem.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        JPanel pnl_Identidade = new JPanel(new BorderLayout(14, 0));
        pnl_Identidade.setOpaque(false);
        pnl_Identidade.add(lbl_AvatarGaragem, BorderLayout.WEST);
        pnl_Identidade.add(pnl_TextoHeader, BorderLayout.CENTER);

        btn_PesquisarHeader = new JButton("");
        ImageIcon icoBusca = carregarERedimensionarIcone("/assets/icons/pesquisar.png", 50, 50);
        if (icoBusca != null) btn_PesquisarHeader.setIcon(icoBusca);
        btn_PesquisarHeader.setFocusPainted(false);
        btn_PesquisarHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_PesquisarHeader.setPreferredSize(new Dimension(80, 80));
        btn_PesquisarHeader.setBackground(COR_CARTAO_OPACA);
        btn_PesquisarHeader.setToolTipText("Pesquisar");

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
        btn_Configuracoes.setBackground(COR_CARTAO_OPACA);
        btn_Configuracoes.setToolTipText("Configurações da Oficina");

        JPanel pnl_ContainerBusca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnl_ContainerBusca.setOpaque(false);
        pnl_ContainerBusca.add(btn_Configuracoes);
        pnl_ContainerBusca.add(btn_PesquisarHeader);

        pnl_Header.add(pnl_Identidade, BorderLayout.CENTER);
        pnl_Header.add(pnl_ContainerBusca, BorderLayout.EAST);

        // --------------------------------------------------------------------
        // 2. MÓDULO DE SEÇÃO: VISUALIZAR INFORMAÇÕES (Consultas / Os 4 botões)
        // Princípio da Similaridade / Unidade Comum: usa o MESMO padrão de cartão
        // (título com ícone + borda arredondada) já usado na seção de Estatísticas,
        // deixando claro que as duas são seções de mesmo nível hierárquico.
        // --------------------------------------------------------------------
        pnl_BotoesVisualizar = criarCard("Consultas / Cadastros", obterIcone("/assets/icons/consultas.png", 18, IconeVetorial.Tipo.LUPA, Color.decode("#FF9900")));
        JPanel corpoConsultas = (JPanel) pnl_BotoesVisualizar.getComponent(1);
        corpoConsultas.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 18));

        btn_VisServicos = new JButton("Serviços");
        configurarEstiloBotaoCard(btn_VisServicos, obterIcone("/assets/icons/vservicos.png", TAM_ICONE_CARD, IconeVetorial.Tipo.SERVICO, Color.DARK_GRAY), DIM_BOTAO_CARD);
        btn_VisServicos.setToolTipText("Ver serviços cadastrados");

        btn_VisOrcamentos = new JButton("Orçamentos");
        configurarEstiloBotaoCard(btn_VisOrcamentos, obterIcone("/assets/icons/vorcamentos.png", TAM_ICONE_CARD, IconeVetorial.Tipo.DOCUMENTO, Color.DARK_GRAY), DIM_BOTAO_CARD);
        btn_VisOrcamentos.setToolTipText("Aprovar orçamentos");

        btn_VisClientes = new JButton("Clientes");
        configurarEstiloBotaoCard(btn_VisClientes, obterIcone("/assets/icons/vclientes.png", TAM_ICONE_CARD, IconeVetorial.Tipo.PESSOA, Color.DARK_GRAY), DIM_BOTAO_CARD);
        btn_VisClientes.setToolTipText("Ver clientes cadastrados");

        btn_VisVeiculos = new JButton("Veículos");
        configurarEstiloBotaoCard(btn_VisVeiculos, obterIcone("/assets/icons/vveiculos.png", TAM_ICONE_CARD, IconeVetorial.Tipo.MODELO, Color.DARK_GRAY), DIM_BOTAO_CARD);
        btn_VisVeiculos.setToolTipText("Ver veículos cadastrados");

        corpoConsultas.add(btn_VisServicos);
        corpoConsultas.add(btn_VisOrcamentos);
        corpoConsultas.add(btn_VisClientes);
        corpoConsultas.add(btn_VisVeiculos);
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
        ScrollBarPadrao.aplicar(scroll);

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
        JPanel card = criarCard("Estatísticas da Oficina", obterIcone("/assets/icons/estatisticas.png", 18, IconeVetorial.Tipo.GRAFICO, Color.decode("#FF9900")));

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
        linha1.add(criarCardEstat("Montadoras",           totalMontadoras, "#9B59B6", "/assets/icons/montadoras.png", IconeVetorial.Tipo.MONTADORA, () -> navegarPara(new V_VisualizarMontadoras(controller))));
        linha1.add(criarCardEstat("Modelos",              totalModelos,    "#E67E22", "/assets/icons/modelos.png",    IconeVetorial.Tipo.MODELO,    () -> navegarPara(new V_VisualizarModelos(controller))));
        linha1.add(criarCardEstat("Peças Cadastradas",    totalPecas,      "#16A085", "/assets/icons/pecas.png",      IconeVetorial.Tipo.PECA,      () -> navegarPara(new V_VisualizarPecas(controller))));
        linha1.add(criarCardEstat("Serviços Cadastrados", totalCatalogo,   "#8E44AD", "/assets/icons/vservicos.png",  IconeVetorial.Tipo.SERVICO,   () -> navegarPara(new V_VisualizarCatalogoServicos(controller))));

        JPanel linha2 = new JPanel(new GridLayout(1, 4, 12, 0));
        linha2.setOpaque(false);
        linha2.add(criarCardEstat("OS Total",     totalOS,      "#34495E", "/assets/icons/os_total.png",     IconeVetorial.Tipo.DOCUMENTO,  () -> navegarPara(new V_VisualizarOSFiltrado(controller, null))));
        linha2.add(criarCardEstat("OS Abertas",   osAbertas,    "#E74C3C", "/assets/icons/os_aberta.png",    IconeVetorial.Tipo.ABERTA,     () -> navegarPara(new V_VisualizarOSFiltrado(controller, OrdemDeServico.Status.ABERTA))));
        linha2.add(criarCardEstat("Em Andamento", osAndamento,  "#F39C12", "/assets/icons/os_andamento.png", IconeVetorial.Tipo.ANDAMENTO,  () -> navegarPara(new V_VisualizarOSFiltrado(controller, OrdemDeServico.Status.EM_ANDAMENTO))));
        linha2.add(criarCardEstat("Concluídas",   osConcluidas, "#27AE60", "/assets/icons/os_concluida.png", IconeVetorial.Tipo.CONCLUIDA,  () -> navegarPara(new V_VisualizarOSFiltrado(controller, OrdemDeServico.Status.CONCLUIDA))));

        // Princípio da Proximidade + Unidade Comum: os 4 primeiros cards (dados de catálogo)
        // e os 4 últimos (dados de ordens de serviço) são naturezas diferentes de informação.
        // Um rótulo discreto (agora com ícone) + espaçamento maior entre os grupos deixa essa
        // separação óbvia ao olho, sem precisar de nenhuma cor nova ou caixa extra.
        JLabel lbl_GrupoCatalogo = new JLabel("Catálogo", obterIcone(null, 14, IconeVetorial.Tipo.CATALOGO, Color.decode("#999999")), SwingConstants.LEFT);
        lbl_GrupoCatalogo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl_GrupoCatalogo.setForeground(Color.decode("#999999"));
        lbl_GrupoCatalogo.setIconTextGap(6);
        lbl_GrupoCatalogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl_GrupoCatalogo.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));

        JLabel lbl_GrupoOS = new JLabel("Ordens de Serviço", obterIcone(null, 14, IconeVetorial.Tipo.DOCUMENTO, Color.decode("#999999")), SwingConstants.LEFT);
        lbl_GrupoOS.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl_GrupoOS.setForeground(Color.decode("#999999"));
        lbl_GrupoOS.setIconTextGap(6);
        lbl_GrupoOS.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl_GrupoOS.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));

        linha1.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.add(lbl_GrupoCatalogo);
        corpo.add(linha1);
        corpo.add(Box.createVerticalStrut(20)); // respiro maior = separa os dois grupos (proximidade)
        corpo.add(lbl_GrupoOS);
        corpo.add(linha2);
        return card;
    }

    private JPanel criarCard(String titulo) {
        return criarCard(titulo, null);
    }

    /**
     * Mesmo card padrão, agora com fundo em PainelCartao (cor com opacidade, cantos de verdade)
     * e um pequeno ícone opcional antes do título — a imagem é resolvida por obterIcone(),
     * então SEMPRE aparece algo (PNG do projeto ou vetor de fallback).
     */
    private JPanel criarCard(String titulo, Icon icone) {
        int raioCard = 14;
        PainelCartao card = new PainelCartao(new BorderLayout(0, 12), raioCard, COR_CARTAO);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(raioCard, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl_TituloCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_TituloCard.setOpaque(false);
        pnl_TituloCard.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        pnl_TituloCard.setPreferredSize(new Dimension(0, 30));

        if (icone != null) {
            pnl_TituloCard.add(new JLabel(icone));
        }

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

    private JButton criarCardEstat(String rotulo, int valor, String corHex, Runnable aoClicar) {
        return criarCardEstat(rotulo, valor, corHex, null, null, aoClicar);
    }

    /**
     * Card de estatística no MESMO padrão visual dos botões de "Consultas"
     * (ícone em cima, mesma dimensão DIM_BOTAO_CARD, mesmo hover) — Princípio da Similaridade.
     * Mostra o número (destaque, na cor já usada por essa categoria) E a descrição da categoria
     * logo abaixo — o ícone foi reduzido (TAM_ICONE_ESTAT) para sobrar espaço para as duas linhas.
     */
    private JButton criarCardEstat(String rotulo, int valor, String corHex, String caminhoIconePng, IconeVetorial.Tipo tipoIconeFallback, Runnable aoClicar) {
        Icon icone = (tipoIconeFallback != null)
                ? obterIcone(caminhoIconePng, TAM_ICONE_ESTAT, tipoIconeFallback, Color.decode(corHex))
                : (caminhoIconePng != null ? carregarERedimensionarIcone(caminhoIconePng, TAM_ICONE_ESTAT, TAM_ICONE_ESTAT) : null);

        // HTML dentro do próprio JButton: mantém "ícone em cima + conteúdo embaixo" (mesmo padrão),
        // só que agora o "conteúdo embaixo" tem duas linhas — número (cor da categoria) e descrição.
        String textoHtml = "<html><body style='text-align:center; margin:0; padding:0;'>"
                + "<div style='font-size:20px; font-weight:bold; color:" + corHex + ";'>" + valor + "</div>"
                + "<div style='font-size:10px; color:#666666; margin-top:1px;'>" + rotulo + "</div>"
                + "</body></html>";

        JButton btn = new JButton(textoHtml);
        configurarEstiloBotaoCard(btn, icone, DIM_BOTAO_CARD);
        btn.setIconTextGap(6);
        btn.addActionListener(e -> aoClicar.run());
        return btn;
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
     * Configura o botão no formato de Card (Ícone acima do texto, fundo clean na nova paleta)
     */
    private void configurarEstiloBotaoCard(JButton btn, Icon icone, Dimension dimensao) {
        if (icone != null) {
            btn.setIcon(icone);
        }

        // Força o texto para baixo e centraliza o ícone
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);

        // Estilização visual limpa (Clean flat design)
        btn.setPreferredSize(dimensao);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBackground(COR_CARTAO_OPACA); // Fundo do Card (nova paleta, era branco puro)
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Adiciona um pequeno espaçamento entre o ícone e o texto
        btn.setIconTextGap(10);

        // Princípio da Figura-Fundo: mesmo tom já usado nos demais hovers da tela,
        // dando feedback consistente ao passar o mouse.
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(COR_CARTAO_OPACA_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(COR_CARTAO_OPACA); }
        });
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

    /**
     * Resolve um ícone para qualquer seção da tela: tenta o PNG do projeto primeiro;
     * se não existir (ou o caminho for null), desenha um ícone vetorial equivalente na hora.
     * Garante que TODA seção tenha uma imagem, mesmo antes de os assets finais existirem.
     */
    private Icon obterIcone(String caminhoPng, int tamanho, IconeVetorial.Tipo tipoFallback, Color corFallback) {
        if (caminhoPng != null) {
            ImageIcon png = carregarERedimensionarIcone(caminhoPng, tamanho, tamanho);
            if (png != null) return png;
        }
        return new IconeVetorial(tipoFallback, corFallback, tamanho);
    }

    // ==========================================
    // FUNDO DOS CARDS (COR + OPACIDADE, CANTOS REAIS)
    // ==========================================

    /**
     * JPanel que pinta seu próprio fundo como um retângulo arredondado com opacidade,
     * substituindo o antigo Color.WHITE liso e opaco. Como só a área arredondada é
     * pintada, os "cantos quadrados" que apareciam atrás da borda arredondada somem —
     * o que sobra nos cantos é a cor real de fundo da tela (#F5F5F5), visível por trás.
     */
    private static class PainelCartao extends JPanel {
        private final int raio;
        private Color corFundo;

        PainelCartao(LayoutManager layout, int raio, Color corFundoInicial) {
            super(layout);
            this.raio = raio;
            this.corFundo = corFundoInicial;
            setOpaque(false);
        }

        void definirCorFundo(Color nova) {
            this.corFundo = nova;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(corFundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==========================================
    // ÍCONES VETORIAIS (FALLBACK SEM ARQUIVOS EXTERNOS)
    // ==========================================

    /**
     * Ícone desenhado por código (Graphics2D), sem depender de nenhum arquivo PNG.
     * Usado como fallback em obterIcone() para garantir que toda seção tenha uma imagem
     * própria mesmo antes de a pasta /assets/icons ter os arquivos finais.
     */
    private static class IconeVetorial implements Icon {
        enum Tipo { GARAGEM, LUPA, GRAFICO, MONTADORA, MODELO, PECA, SERVICO, CATALOGO, DOCUMENTO, PESSOA, ABERTA, ANDAMENTO, CONCLUIDA }

        private final Tipo tipo;
        private final Color cor;
        private final int tam;

        IconeVetorial(Tipo tipo, Color cor, int tam) {
            this.tipo = tipo;
            this.cor = cor;
            this.tam = tam;
        }

        @Override public int getIconWidth()  { return tam; }
        @Override public int getIconHeight() { return tam; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(Math.max(1.4f, tam / 11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int s = tam;
            int pad = Math.max(2, s / 7);
            int area = s - pad * 2;

            switch (tipo) {
                case GARAGEM:
                    g2.drawPolyline(new int[]{pad, s / 2, s - pad}, new int[]{pad + area / 3, pad, pad + area / 3}, 3);
                    g2.drawRect(pad, pad + area / 3, area, area - area / 3);
                    break;
                case LUPA: {
                    int d = (int) (area * 0.68);
                    g2.drawOval(pad, pad, d, d);
                    g2.drawLine(pad + d - 2, pad + d - 2, s - pad, s - pad);
                    break;
                }
                case GRAFICO: {
                    int larguraBarra = Math.max(2, area / 4);
                    g2.fillRoundRect(pad, s - pad - area / 3, larguraBarra, area / 3, 2, 2);
                    g2.fillRoundRect(pad + larguraBarra + 3, s - pad - (int) (area * 0.62), larguraBarra, (int) (area * 0.62), 2, 2);
                    g2.fillRoundRect(pad + (larguraBarra + 3) * 2, s - pad - area, larguraBarra, area, 2, 2);
                    break;
                }
                case MONTADORA:
                    g2.drawRect(pad, pad + area / 4, area, area - area / 4);
                    g2.drawRect(pad + area / 6, pad, area / 4, area / 4);
                    g2.fillRect(pad + area / 4, pad + area / 2, Math.max(2, area / 8), Math.max(2, area / 8));
                    g2.fillRect(pad + area * 5 / 8, pad + area / 2, Math.max(2, area / 8), Math.max(2, area / 8));
                    break;
                case MODELO:
                    g2.drawRoundRect(pad, pad + area / 3, area, area / 3, area / 6, area / 6);
                    g2.fillOval(pad + area / 8, pad + area * 2 / 3, Math.max(3, area / 5), Math.max(3, area / 5));
                    g2.fillOval(pad + area * 5 / 8, pad + area * 2 / 3, Math.max(3, area / 5), Math.max(3, area / 5));
                    break;
                case PECA: {
                    int[] xs = new int[6];
                    int[] ys = new int[6];
                    double raioHex = area / 2.0;
                    double cx = s / 2.0, cy = s / 2.0;
                    for (int i = 0; i < 6; i++) {
                        double ang = Math.toRadians(60 * i - 90);
                        xs[i] = (int) (cx + raioHex * Math.cos(ang));
                        ys[i] = (int) (cy + raioHex * Math.sin(ang));
                    }
                    g2.drawPolygon(xs, ys, 6);
                    g2.drawOval(s / 2 - area / 6, s / 2 - area / 6, Math.max(3, area / 3), Math.max(3, area / 3));
                    break;
                }
                case SERVICO:
                    g2.drawLine(pad + 2, s - pad - 2, s - pad - 2, pad + 2);
                    g2.drawOval(pad, s - pad - area / 4, Math.max(3, area / 4), Math.max(3, area / 4));
                    g2.drawOval(s - pad - area / 4, pad, Math.max(3, area / 4), Math.max(3, area / 4));
                    break;
                case CATALOGO: {
                    int q = Math.max(3, area / 2 - 2);
                    g2.fillRoundRect(pad, pad, q, q, 3, 3);
                    g2.fillRoundRect(pad + q + 4, pad, q, q, 3, 3);
                    g2.fillRoundRect(pad, pad + q + 4, q, q, 3, 3);
                    g2.fillRoundRect(pad + q + 4, pad + q + 4, q, q, 3, 3);
                    break;
                }
                case DOCUMENTO:
                    g2.drawRoundRect(pad, pad, area, area, 4, 4);
                    for (int i = 1; i <= 3; i++) {
                        int ly = pad + (area * i) / 4;
                        g2.drawLine(pad + area / 5, ly, s - pad - area / 5, ly);
                    }
                    break;
                case PESSOA:
                    g2.drawOval(s / 2 - area / 5, pad, area * 2 / 5, area * 2 / 5);
                    g2.drawArc(pad, pad + area / 2, area, area / 2, 0, 180);
                    break;
                case ABERTA: {
                    int[] xs = { pad, pad + area / 4, s - pad, s - pad - area / 5 };
                    int[] ys = { s - pad, pad + area / 3, pad + area / 3, s - pad };
                    g2.drawPolygon(xs, ys, 4);
                    g2.drawLine(pad, pad + area / 3, pad + area / 3, pad);
                    g2.drawLine(pad + area / 3, pad, pad + area / 2, pad + area / 3);
                    break;
                }
                case ANDAMENTO:
                    g2.drawOval(pad, pad, area, area);
                    g2.drawLine(s / 2, s / 2, s / 2, pad + area / 4);
                    g2.drawLine(s / 2, s / 2, s / 2 + area / 4, s / 2);
                    break;
                case CONCLUIDA:
                    g2.drawOval(pad, pad, area, area);
                    g2.drawPolyline(
                        new int[]{pad + area / 4, s / 2 - 1, s - pad - area / 6},
                        new int[]{s / 2, s - pad - area / 3, pad + area / 3},
                        3
                    );
                    break;
            }
            g2.dispose();
        }
    }
}