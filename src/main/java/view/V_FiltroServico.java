package view;

import javax.swing.*;
import java.awt.*;

/**
 * Classe responsável pela janela flutuante de filtros para a listagem de serviços.
 * Implementa os filtros de Etapa, Responsável, Tipo de Manutenção e Tipo de Serviço.
 */
public class V_FiltroServico extends JDialog {

    // =========================================================================
    // DECLARAÇÃO DOS COMPONENTES (Padrão: prefixo_NomeFuncional)
    // =========================================================================
    private JPanel pnl_Main;
    private JPanel pnl_Formulario;
    private JPanel pnl_RodapeBotoes;

    // Campo 1: Etapa
    private JLabel lbl_Etapa;
    private JComboBox<String> cbb_Etapa;

    // Campo 2: Responsável
    private JLabel lbl_Responsavel;
    private JComboBox<String> cbb_Responsavel;

    // Campo 3: Tipo de Manutenção
    private JLabel lbl_TipoManutencao;
    private JComboBox<String> cbb_TipoManutencao;

    // Campo 4: Tipo de Serviço
    private JLabel lbl_TipoServico;
    private JComboBox<String> cbb_TipoServico;

    // Botões de Ação do Rodapé
    private JButton btn_LimparFiltros;
    private JButton btn_AplicarFiltros;

    /**
     * Construtor para quando a janela mãe for um JFrame.
     */
    public V_FiltroServico(JFrame owner, boolean t) {
        super(owner, "Filtros Avançados", true); // true ativa o comportamento Modal
        configurarJanela();
    }

    /**
     * Construtor para quando a janela mãe for outro JDialog.
     */
    public V_FiltroServico(JDialog owner) {
        super(owner, "Filtros Avançados", true);
        configurarJanela();
    }

    public V_FiltroServico(JDialog parentWindow, boolean t) {
    }

    /**
     * Centraliza a rotina de inicialização e configurações de tamanho da janela.
     */
    private void configurarJanela() {
        setSize(400, 320);
        setResizable(false);
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();

        // Centraliza em relação à tela/janela mãe
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        // Painel Principal com Layout de Margens (BorderLayout)
        pnl_Main = new JPanel(new BorderLayout(10, 10));
        pnl_Main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnl_Main.setBackground(Color.WHITE);

        // Formulário configurado em Grid de duas colunas (Label e Componente)
        pnl_Formulario = new JPanel(new GridLayout(4, 2, 10, 15));
        pnl_Formulario.setOpaque(false);

        // Instanciação dos Componentes de Texto e Seleção
        lbl_Etapa = new JLabel("Etapa do Serviço:");
        lbl_Etapa.setFont(new Font("Arial", Font.BOLD, 13));
        cbb_Etapa = new JComboBox<>(new String[]{"Todas", "Revisão Inicial", "Troca de Peças", "Finalizado"});

        lbl_Responsavel = new JLabel("Mecânico Responsável:");
        lbl_Responsavel.setFont(new Font("Arial", Font.BOLD, 13));
        cbb_Responsavel = new JComboBox<>(new String[]{"Todos", "Carlos Eduardo", "Roberto Silva", "Alan Jones"});

        lbl_TipoManutencao = new JLabel("Tipo de Manutenção:");
        lbl_TipoManutencao.setFont(new Font("Arial", Font.BOLD, 13));
        cbb_TipoManutencao = new JComboBox<>(new String[]{"Todas", "Preventiva", "Corretiva", "Preditiva"});

        lbl_TipoServico = new JLabel("Categoria de Serviço:");
        lbl_TipoServico.setFont(new Font("Arial", Font.BOLD, 13));
        cbb_TipoServico = new JComboBox<>(new String[]{"Todas", "Mecânica Geral", "Elétrica", "Injeção Eletrônica", "Suspensão"});

        // Alinhamento do Rodapé de Ações
        pnl_RodapeBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnl_RodapeBotoes.setOpaque(false);

        btn_LimparFiltros = new JButton("Limpar");
        btn_LimparFiltros.setFont(new Font("Arial", Font.PLAIN, 13));
        btn_LimparFiltros.setBackground(Color.decode("#E0E0E0"));
        btn_LimparFiltros.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_LimparFiltros.addActionListener(e -> limparSelecoes());

        btn_AplicarFiltros = new JButton("Aplicar Filtros");
        btn_AplicarFiltros.setFont(new Font("Arial", Font.BOLD, 13));
        btn_AplicarFiltros.setForeground(Color.WHITE);
        btn_AplicarFiltros.setBackground(Color.decode("#1976D2")); // Azul Padrão do Sistema
        btn_AplicarFiltros.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_AplicarFiltros.addActionListener(e -> dispose()); // Fecha a janela ao aplicar
    }

    private void limparSelecoes() {
        cbb_Etapa.setSelectedIndex(0);
        cbb_Responsavel.setSelectedIndex(0);
        cbb_TipoManutencao.setSelectedIndex(0);
        cbb_TipoServico.setSelectedIndex(0);
    }

    private void layoutComponents() {
        // Adicionando os componentes de forma ordenada no Grid
        pnl_Formulario.add(lbl_Etapa);
        pnl_Formulario.add(cbb_Etapa);

        pnl_Formulario.add(lbl_Responsavel);
        pnl_Formulario.add(cbb_Responsavel);

        pnl_Formulario.add(lbl_TipoManutencao);
        pnl_Formulario.add(cbb_TipoManutencao);

        pnl_Formulario.add(lbl_TipoServico);
        pnl_Formulario.add(cbb_TipoServico);

        // Montagem do rodapé
        pnl_RodapeBotoes.add(btn_LimparFiltros);
        pnl_RodapeBotoes.add(btn_AplicarFiltros);

        pnl_Main.add(pnl_Formulario, BorderLayout.CENTER);
        pnl_Main.add(pnl_RodapeBotoes, BorderLayout.SOUTH);

        // Adiciona o container principal diretamente na raiz do JDialog
        add(pnl_Main, BorderLayout.CENTER);
    }


    // =========================================================================
    // GETTERS PARA COMPONENTES (Para uso dos Controllers de filtragem)
    // =========================================================================
    public JComboBox<String> getCbb_Etapa() { return cbb_Etapa; }
    public JComboBox<String> getCbb_Responsavel() { return cbb_Responsavel; }
    public JComboBox<String> getCbb_TipoManutencao() { return cbb_TipoManutencao; }
    public JComboBox<String> getCbb_TipoServico() { return cbb_TipoServico; }
    public JButton getBtn_LimparFiltros() { return btn_LimparFiltros; }
    public JButton getBtn_AplicarFiltros() { return btn_AplicarFiltros; }
}
