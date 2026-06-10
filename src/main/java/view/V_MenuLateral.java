package view;

import controller.OficinaController;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Barra lateral de navegação (JPanel).
 * Atualizada para disparar eventos usando o gerenciador centralizado (Controller).
 */
public class V_MenuLateral extends JPanel {

    // ==========================================
    // DECLARAÇÃO DOS COMPONENTES (Padrão Prefixo)
    // ==========================================
    private JLabel lbl_LogoOficina;
    private JButton btn_PaginaInicial;
    private JButton btn_Pesquisar;
    private JButton btn_Servicos;
    private JButton btn_Estoque;

    private OficinaController controller;

    /**
     * Construtor recebe o controller vindo da janela principal.
     */
    public V_MenuLateral(OficinaController controller) {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.decode("#6E6968")); // Cor de fundo padrão
        setPreferredSize(new Dimension(190, 768));

        initComponents();
        vincularAcoesNavegacao(); // ATIVADO: Vincula as ações de clique nos botões do menu
    }

    private void initComponents() {
        Dimension dimBtn = new Dimension(170, 90);

        // 1. Configuração do Logotipo
        ImageIcon icoLogo = carregarERedimensionarIcone("/assets/images/logo.png", 180, 135);
        lbl_LogoOficina = new JLabel();
        if (icoLogo != null) {
            lbl_LogoOficina.setIcon(icoLogo);
        } else {
            lbl_LogoOficina.setText("GEAR GEST GARAGE");
            lbl_LogoOficina.setForeground(Color.WHITE);
            lbl_LogoOficina.setFont(new Font("Arial", Font.BOLD, 14));
        }
        lbl_LogoOficina.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Inicialização dos Botões de Navegação com seus respectivos ícones
        btn_PaginaInicial = new JButton("Página Inicial");
        estilizarBotaoMenu(btn_PaginaInicial, dimBtn, "/assets/icons/home.png", 50, 50);

        btn_Pesquisar = new JButton("Pesquisar");
        estilizarBotaoMenu(btn_Pesquisar, dimBtn, "/assets/icons/pesquisar.png", 50, 50);

        btn_Servicos = new JButton("Serviços");
        estilizarBotaoMenu(btn_Servicos, dimBtn, "/assets/icons/servicos.png", 50, 50);

        btn_Estoque = new JButton("Estoque");
        estilizarBotaoMenu(btn_Estoque, dimBtn, "/assets/icons/estoque.png", 50, 50);

        // 3. Organização estrutural com espaçadores rígidos (Box)
        add(Box.createVerticalStrut(30));
        add(lbl_LogoOficina);
        add(Box.createVerticalStrut(30));
        add(btn_PaginaInicial);
        add(Box.createVerticalStrut(30));
        add(btn_Pesquisar);
        add(Box.createVerticalStrut(30));
        add(btn_Servicos);
        add(Box.createVerticalStrut(30));
        add(btn_Estoque);
    }

    /**
     * Vincula as ações de clique dos botões para alternar as telas no V_Main.
     */
    private void vincularAcoesNavegacao() {
        // Retorna para a home do sistema passando o controlador para não perder os dados salvos
        btn_PaginaInicial.addActionListener(e -> navegarPara(new V_PaginaInicial(this.controller)));

        // Links para as demais telas (descomente conforme criar os painéis correspondentes)
        btn_Pesquisar.addActionListener(e -> {
            navegarPara(new V_PesquisarGeral(this.controller));
        });

        btn_Servicos.addActionListener(e -> {
             navegarPara(new V_VisualizarServicos(this.controller));
        });

        btn_Estoque.addActionListener(e -> {
            // navegarPara(new V_GerenciarEstoque(this.controller));
        });
    }

    /**
     * Recupera o JFrame ancestral (V_Main) e injeta o novo painel no centro do sistema.
     */
    private void navegarPara(JPanel novoPainelCentral) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof V_Main) {
            V_Main mainFrame = (V_Main) window;
            mainFrame.atualizarConteudo(novoPainelCentral);
        } else {
            System.err.println("Erro: V_Main não pôde ser localizado a partir do Menu Lateral.");
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE ESTILIZAÇÃO E MÍDIA
    // ==========================================
    private void estilizarBotaoMenu(JButton btn, Dimension dimensaoBtn, String caminhoIcone, int larguraIco, int alturaIco) {
        btn.setMaximumSize(dimensaoBtn);
        btn.setPreferredSize(dimensaoBtn);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));

        ImageIcon icoBotao = carregarERedimensionarIcone(caminhoIcone, larguraIco, alturaIco);
        if (icoBotao != null) {
            btn.setIcon(icoBotao);
        }

        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(8);

        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(5, 5, 5, 5));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

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