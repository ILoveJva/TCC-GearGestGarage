package view;

import controller.OficinaController;
import model.Cliente;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel Modular de Listagem de Clientes.
 * Exibe apenas a tabela simplificada ocupando o espaço útil de forma compacta.
 */
public class Pnl_SelecaoCliente extends JPanel {

    private JTextField txt_PesquisaCliente;
    private JButton btn_FiltrosCliente;
    private JTable tbl_Clientes;
    private DefaultTableModel mdl_Clientes;
    private JScrollPane scp_ScrollClientes;
    private OficinaController controller; // Referência para o controlador

    // Novo construtor: Recebe o controller e carrega os dados automaticamente
    public Pnl_SelecaoCliente(OficinaController controller) {
        this.controller = controller;
        setOpaque(false);
        setLayout(new BorderLayout(0, 5));
        initComponents();
        carregarTodosClientes(); // Carrega os dados assim que o painel é criado
    }

    // Mantido o construtor padrão sem parâmetros para evitar quebras em outras telas ou editores visuais
    public Pnl_SelecaoCliente() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 5));
        initComponents();
    }

    private void initComponents() {
        // Estrutura de colunas simples e estritas de acordo com a regra de negócio
        String[] colunas = {"Cód.", "Nome", "CPF", "Telefone"};
        mdl_Clientes = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tbl_Clientes = new JTable(mdl_Clientes);
        tbl_Clientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Clientes.setRowHeight(26); // Altura compacta ideal de acordo com o wireframe
        tbl_Clientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajuste proporcional do Cabeçalho
        tbl_Clientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl_Clientes.getTableHeader().setReorderingAllowed(false);
        tbl_Clientes.getTableHeader().setPreferredSize(new Dimension(0, 30));

        scp_ScrollClientes = new JScrollPane(tbl_Clientes);
        scp_ScrollClientes.setBorder(BorderFactory.createLineBorder(Color.decode("#4D4D4D")));

        add(scp_ScrollClientes, BorderLayout.CENTER);
    }

    /**
     * Busca a lista completa de clientes do controlador e popula a tabela.
     */
    public void carregarTodosClientes() {
        if (controller != null) {
            // ATENÇÃO: Verifique se o método no seu OficinaController chama-se getClientes() ou getListaClientes()
            ArrayList<Cliente> lista = controller.listarClientes();
            atualizarTabela(lista);
        }
    }

    /**
     * Atualiza os dados da tabela dinamicamente com clientes válidos.
     * Corrigido o tratamento do getIdUsuario() que é do tipo primitivo long.
     */
    public void atualizarTabela(ArrayList<Cliente> clientes) {
        mdl_Clientes.setRowCount(0);
        if (clientes == null) return;

        clientes.stream()
                .filter(c -> c.getCpf() != null && !c.getCpf().trim().isEmpty())
                .forEach(c -> {
                    mdl_Clientes.addRow(new Object[]{
                            String.format("%05d", c.getIdUsuario()), // Tratado diretamente como long primitivo
                            c.getNome(),
                            c.getCpf(),
                            c.getTelefone()
                    });
                });
    }

    public JTable getTbl_Clientes() { return tbl_Clientes; }
    public JTextField getTxt_PesquisaCliente() { return txt_PesquisaCliente; }

    // Getter e Setter para o controlador, permitindo injeção tardia se necessário
    public OficinaController getController() { return controller; }
    public void setController(OficinaController controller) {
        this.controller = controller;
        carregarTodosClientes();
    }
}