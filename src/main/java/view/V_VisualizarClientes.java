package view;

import controller.OficinaController;
import model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
    private JButton btn_CadastrarCliente;
    private JButton btn_EditarCliente;
    private JButton btn_ExcluirCliente;

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
        vincularAcoes();
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

        btn_CadastrarCliente = new JButton("+ Cadastrar Cliente");
        btn_CadastrarCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_CadastrarCliente.setForeground(Color.WHITE);
        btn_CadastrarCliente.setBackground(Color.decode("#FF9900"));
        btn_CadastrarCliente.setFocusPainted(false);
        btn_CadastrarCliente.setBorderPainted(false);
        btn_CadastrarCliente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ImageIcon icoCliente = carregarIcone("/assets/icons/add_cliente.png", 20, 20);
        if (icoCliente != null) {
            btn_CadastrarCliente.setIcon(icoCliente);
            btn_CadastrarCliente.setHorizontalTextPosition(SwingConstants.RIGHT);
        }

        btn_EditarCliente = new JButton("Editar");
        btn_EditarCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_EditarCliente.setForeground(Color.WHITE);
        btn_EditarCliente.setBackground(Color.decode("#6C757D"));
        btn_EditarCliente.setFocusPainted(false);
        btn_EditarCliente.setBorderPainted(false);
        btn_EditarCliente.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn_ExcluirCliente = new JButton("Excluir");
        btn_ExcluirCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_ExcluirCliente.setForeground(Color.WHITE);
        btn_ExcluirCliente.setBackground(Color.decode("#DC3545"));
        btn_ExcluirCliente.setFocusPainted(false);
        btn_ExcluirCliente.setBorderPainted(false);
        btn_ExcluirCliente.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void layoutComponents() {
        // Header com mapa de navegação e botão de cadastro
        JPanel pnl_HeaderCard = new JPanel(new BorderLayout());
        pnl_HeaderCard.setOpaque(false);
        pnl_HeaderCard.add(lbl_MapaNavegacao, BorderLayout.WEST);

        JPanel pnl_BotoesHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnl_BotoesHeader.setOpaque(false);
        pnl_BotoesHeader.add(btn_EditarCliente);
        pnl_BotoesHeader.add(btn_ExcluirCliente);
        pnl_BotoesHeader.add(btn_CadastrarCliente);
        pnl_HeaderCard.add(pnl_BotoesHeader, BorderLayout.EAST);

        pnl_CardCentral.add(pnl_HeaderCard, BorderLayout.NORTH);

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
        // Repassa a lista de clientes para a regra de negócio interna do painel modular
        pnl_SelecaoCliente.atualizarTabela(todosClientes);
    }

    /**
     * Duplo-clique em um cliente abre a página de perfil correspondente.
     */
    private void vincularAcoes() {
        btn_CadastrarCliente.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(V_VisualizarClientes.this);
            if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_CadastrarCliente(controller));
        });

        btn_EditarCliente.addActionListener(e -> {
            Cliente alvo = obterClienteSelecionado();
            if (alvo == null) {
                JOptionPane.showMessageDialog(this,
                    "Selecione um cliente na lista para editar.",
                    "Nenhum cliente selecionado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Window w = SwingUtilities.getWindowAncestor(V_VisualizarClientes.this);
            if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_EditarCliente(controller, alvo));
        });

        btn_ExcluirCliente.addActionListener(e -> {
            Cliente alvo = obterClienteSelecionado();
            if (alvo == null) {
                JOptionPane.showMessageDialog(this,
                    "Selecione um cliente na lista para excluir.",
                    "Nenhum cliente selecionado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int resp = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o cliente \"" + alvo.getNome() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (resp != JOptionPane.YES_OPTION) return;
            try {
                controller.excluirCliente(alvo.getIdUsuario());
                JOptionPane.showMessageDialog(this,
                    "Cliente excluído com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarDadosClientes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir cliente: " + ex.getMessage(),
                    "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });

        JTable tabela = pnl_SelecaoCliente.getTbl_Clientes();
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) return;
                Cliente alvo = obterClienteSelecionado();
                if (alvo == null) return;

                Window w = SwingUtilities.getWindowAncestor(V_VisualizarClientes.this);
                if (w instanceof V_Main) {
                    ((V_Main) w).atualizarConteudo(new V_PerfilCliente(controller, alvo));
                }
            }
        });
    }

    /** Resolve o Cliente correspondente à linha atualmente selecionada na tabela. */
    private Cliente obterClienteSelecionado() {
        JTable tabela = pnl_SelecaoCliente.getTbl_Clientes();
        int linha = tabela.getSelectedRow();
        if (linha < 0) return null;

        Object codCel = tabela.getValueAt(linha, 0); // "00005"
        long idCliente;
        try {
            idCliente = Long.parseLong(String.valueOf(codCel).trim());
        } catch (NumberFormatException ex) {
            return null;
        }

        for (Cliente c : controller.listarClientes()) {
            if (c.getIdUsuario() == idCliente) return c;
        }
        return null;
    }

    // Exposição da tabela e do painel para listeners ou manipulações externas
    public Pnl_SelecaoCliente getPnl_SelecaoCliente() {
        return pnl_SelecaoCliente;
    }

    public JTable getTbl_Clientes() {
        return pnl_SelecaoCliente.getTbl_Clientes();
    }

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
}
