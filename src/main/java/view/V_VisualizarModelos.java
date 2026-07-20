package view;

import controller.OficinaController;
import model.Modelo;
import model.Montadora;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class V_VisualizarModelos extends JPanel {

    private final OficinaController controller;
    private DefaultTableModel mdl;

    public V_VisualizarModelos(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        construir();
    }

    private void construir() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(680, 520));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0"), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titulo = new JLabel("Página Inicial > Estatísticas > Modelos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));

        String[] cols = {"Cód.", "Modelo", "Tipo", "Montadora"};
        mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(28);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));

        JButton btn_Voltar = new JButton("← Voltar");
        btn_Voltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Voltar.setForeground(Color.WHITE);
        btn_Voltar.setBackground(Color.decode("#6C757D"));
        btn_Voltar.setFocusPainted(false);
        btn_Voltar.setBorderPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Voltar.setPreferredSize(new Dimension(120, 36));
        btn_Voltar.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_Configuracoes(controller));
        });

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        rodape.setOpaque(false);
        rodape.add(btn_Voltar);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(card, gbc);

        carregar();
    }

    private void carregar() {
        mdl.setRowCount(0);
        for (Montadora mont : controller.montadorasComModelos()) {
            for (Modelo mod : mont.listarModelos()) {
                mdl.addRow(new Object[]{
                    String.format("%04d", mod.getIdModelo()),
                    mod.getNome(),
                    mod.getTipo(),
                    mont.getNome()
                });
            }
        }
    }
}
