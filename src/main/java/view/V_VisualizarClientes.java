package view;

import controller.OficinaController;
import model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel de Conteúdo para a Listagem de Clientes.
 * Segue o padrão de design modular, utilizando o Pnl_SelecaoCliente no centro.
 */
public class V_VisualizarClientes extends JPanel {

    // ==========================================
    // DECLARAÇÃO DOS COMPONENTES (Padrão Prefixo)
    // ==========================================
    private JPanel pnl_CardCentral;
    private JLabel lbl_MapaNavegacao; // "Mapa" de onde o usuário se encontra

    // Painel Modular da Tabela de Clientes
    private Pnl_SelecaoCliente pnl_SelecaoCliente;

    // Referência do Controller
    private OficinaController controller;

    /**
     * Construtor recebe o Controller unificado do sistema.
     */
    public V_VisualizarClientes(OficinaController controller) {
        this.controller = controller;

        // Alinhamento centralizado idêntico ao painel de veículos e cadastros
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        initComponents();
        layoutComponents();
        carregarDadosClientes();
    }

    private void initComponents() {
        // Card Central de Contenção (Mesmas proporções das telas de cadastro e veículos)
        pnl_CardCentral = new JPanel(new BorderLayout(0, 15));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 560));

        // "Mapa" de Navegação Superior (Substituindo o antigo menu suspenso)
        lbl_MapaNavegacao = new JLabel("Página Inicial > Consultar Clientes");
        lbl_MapaNavegacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_MapaNavegacao.setForeground(Color.decode("#4D4D4D"));

        // Instanciação do Painel Modular da Tabela
        pnl_SelecaoCliente = new Pnl_SelecaoCliente();
    }

    private void layoutComponents() {
        // Adiciona o mapa de navegação no topo do card
        pnl_CardCentral.add(lbl_MapaNavegacao, BorderLayout.NORTH);

        // Injeta o painel modular da tabela ocupando o resto do espaço útil
        pnl_CardCentral.add(pnl_SelecaoCliente, BorderLayout.CENTER);

        // Injeta o card central usando o GridBagLayout para manter centralizado na tela
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    /**
     * Carrega a tabela varrendo os clientes reais obtidos do Controller.
     */
    private void carregarDadosClientes() {
        if (controller == null) return;

        ArrayList<Cliente> todosClientes = controller.listarClientes();
        System.out.println(todosClientes);
        // Repassa a lista de clientes para a regra de negócio interna do painel modular
        pnl_SelecaoCliente.atualizarTabela(todosClientes);
    }

    // Exposição da tabela e do painel para listeners ou manipulações externas
    public Pnl_SelecaoCliente getPnl_SelecaoCliente() {
        return pnl_SelecaoCliente;
    }

    public JTable getTbl_Clientes() {
        return pnl_SelecaoCliente.getTbl_Clientes();
    }
}
