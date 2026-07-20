package view;

import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Painel de Conteudo para a Listagem de Veiculos.
 * Suporte a duplo clique para abrir os detalhes do veiculo.
 */
public class V_VisualizarVeiculos extends JPanel {

    private JPanel pnl_CardCentral;
    private JLabel lbl_MapaNavegacao;
    private JButton btn_CadastrarVeiculo;
    private Consumer<Long> acaoAbrirDetalhes;

    private JTable tbl_Veiculos;
    private DefaultTableModel mdl_Veiculos;
    private JScrollPane scp_ScrollVeiculos;

    private OficinaController controller;

    public V_VisualizarVeiculos(OficinaController controller) {
        this.controller = controller;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        initComponents();
        layoutComponents();
        configurarEventosTabela();
        carregarDadosVeiculos();
    }

    private void initComponents() {
        pnl_CardCentral = new JPanel(new BorderLayout(0, 15));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 560));

        lbl_MapaNavegacao = new JLabel("Pagina Inicial > Consultar Veiculos");
        lbl_MapaNavegacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_MapaNavegacao.setForeground(Color.decode("#4D4D4D"));

        String[] colunas = {"Cod. Veiculo", "Veiculo", "Tipo", "Placa"};
        mdl_Veiculos = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tbl_Veiculos = new JTable(mdl_Veiculos);
        tbl_Veiculos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Veiculos.setRowHeight(30);
        tbl_Veiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_Veiculos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl_Veiculos.getTableHeader().setReorderingAllowed(false);
        tbl_Veiculos.getTableHeader().setPreferredSize(new Dimension(0, 35));

        scp_ScrollVeiculos = new JScrollPane(tbl_Veiculos);
        scp_ScrollVeiculos.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC")));

        btn_CadastrarVeiculo = new JButton("+ Cadastrar Veículo");
        btn_CadastrarVeiculo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_CadastrarVeiculo.setForeground(Color.WHITE);
        btn_CadastrarVeiculo.setBackground(Color.decode("#FF9900"));
        btn_CadastrarVeiculo.setFocusPainted(false);
        btn_CadastrarVeiculo.setBorderPainted(false);
        btn_CadastrarVeiculo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ImageIcon icoVeiculo = carregarIcone("/assets/icons/add_veiculo.png", 20, 20);
        if (icoVeiculo != null) {
            btn_CadastrarVeiculo.setIcon(icoVeiculo);
            btn_CadastrarVeiculo.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
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
}
