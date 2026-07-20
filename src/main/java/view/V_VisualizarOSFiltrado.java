package view;

import controller.OficinaController;
import model.OrdemDeServico;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class V_VisualizarOSFiltrado extends JPanel {

    private final OficinaController controller;
    private final OrdemDeServico.Status filtroInicial;

    private DefaultTableModel mdl;
    private JTable tabela;
    private final List<OrdemDeServico> todas = new ArrayList<>();

    private JTextField txt_Busca;
    private JComboBox<String> cmb_Status;

    private static final String[] STATUS_LABELS  = {"Todos os status", "Aberta", "Em Andamento", "Concluída"};
    private static final OrdemDeServico.Status[] STATUS_VALUES =
        {null, OrdemDeServico.Status.ABERTA, OrdemDeServico.Status.EM_ANDAMENTO, OrdemDeServico.Status.CONCLUIDA};

    /** @param filtroInicial null = todas; pré-seleciona o combo ao abrir via card de estatística */
    public V_VisualizarOSFiltrado(OficinaController controller, OrdemDeServico.Status filtroInicial) {
        this.controller    = controller;
        this.filtroInicial = filtroInicial;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
        carregar();
    }

    private void construir() {
        // ---- Breadcrumb + contagem ----
        JLabel lbl_Titulo = new JLabel("Página Inicial > Estatísticas > Ordens de Serviço");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));

        // ---- Barra de filtros ----
        txt_Busca = new JTextField();
        txt_Busca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Busca.setPreferredSize(new Dimension(280, 34));
        txt_Busca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JLabel ph = new JLabel("  Buscar por título ou veículo...");
        ph.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ph.setForeground(Color.LIGHT_GRAY);

        // placeholder manual
        txt_Busca.setLayout(new BorderLayout());
        txt_Busca.add(ph, BorderLayout.CENTER);
        txt_Busca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { ph.setVisible(false); filtrar(); }
            public void removeUpdate(DocumentEvent e)  {
                if (txt_Busca.getText().isEmpty()) ph.setVisible(true);
                filtrar();
            }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        cmb_Status = new JComboBox<>(STATUS_LABELS);
        cmb_Status.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Status.setPreferredSize(new Dimension(190, 34));
        cmb_Status.setBackground(Color.WHITE);
        for (int i = 0; i < STATUS_VALUES.length; i++) {
            if (STATUS_VALUES[i] == filtroInicial) { cmb_Status.setSelectedIndex(i); break; }
        }
        cmb_Status.addActionListener(e -> filtrar());

        JLabel lbl_Busca   = new JLabel("Buscar:");
        JLabel lbl_Status  = new JLabel("Etapa:");
        lbl_Busca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl_Status.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel pnl_Filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_Filtros.setOpaque(false);
        pnl_Filtros.add(lbl_Busca);
        pnl_Filtros.add(txt_Busca);
        pnl_Filtros.add(Box.createHorizontalStrut(16));
        pnl_Filtros.add(lbl_Status);
        pnl_Filtros.add(cmb_Status);

        JPanel pnl_Topo = new JPanel();
        pnl_Topo.setLayout(new BoxLayout(pnl_Topo, BoxLayout.Y_AXIS));
        pnl_Topo.setOpaque(false);
        pnl_Topo.add(lbl_Titulo);
        pnl_Topo.add(Box.createVerticalStrut(10));
        pnl_Topo.add(pnl_Filtros);
        pnl_Topo.add(Box.createVerticalStrut(10));

        // ---- Tabela ----
        String[] cols = {"Cód.", "Título", "Tipo de Serviço", "Status", "Veículo"};
        mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setGridColor(Color.decode("#F0F0F0"));
        tabela.setShowHorizontalLines(true);
        tabela.setShowVerticalLines(false);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(Color.decode("#F8F8F8"));
        tabela.getTableHeader().setForeground(Color.decode("#555555"));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // Larguras das colunas
        tabela.getColumnModel().getColumn(0).setMinWidth(50);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(3).setMinWidth(120);
        tabela.getColumnModel().getColumn(3).setMaxWidth(150);

        // Renderer colorido para a coluna Status (col 3)
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                c.setOpaque(true);
                if (!sel) {
                    String s = String.valueOf(val);
                    switch (s) {
                        case "Aberta"       -> { c.setForeground(Color.decode("#C0392B")); c.setBackground(Color.decode("#FDECEA")); }
                        case "Em Andamento" -> { c.setForeground(Color.decode("#9A6700")); c.setBackground(Color.decode("#FEF6E4")); }
                        case "Concluída"    -> { c.setForeground(Color.decode("#1E8449")); c.setBackground(Color.decode("#E9F7EF")); }
                        default             -> { c.setForeground(Color.decode("#333333")); c.setBackground(Color.WHITE); }
                    }
                } else {
                    c.setForeground(Color.WHITE);
                    c.setBackground(tabela.getSelectionBackground());
                }
                return c;
            }
        });

        // Duplo clique abre a OS
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() != 2) return;
                int row = tabela.getSelectedRow();
                if (row < 0) return;
                try {
                    long id = Long.parseLong(String.valueOf(tabela.getValueAt(row, 0)).trim());
                    navegar(new V_OrdemServico(controller, id));
                } catch (NumberFormatException ignored) {}
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        scroll.getViewport().setBackground(Color.WHITE);

        JLabel dica = new JLabel("Duplo clique em uma linha para abrir a O.S. completa.");
        dica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dica.setForeground(Color.decode("#AAAAAA"));
        dica.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));

        add(pnl_Topo,  BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(dica,      BorderLayout.SOUTH);
    }

    private void carregar() {
        todas.clear();
        todas.addAll(controller.listarOS());
        filtrar();
    }

    private void filtrar() {
        String busca = txt_Busca.getText().trim().toLowerCase();
        int idx = cmb_Status.getSelectedIndex();
        OrdemDeServico.Status statusFiltro = (idx >= 0 && idx < STATUS_VALUES.length) ? STATUS_VALUES[idx] : null;

        mdl.setRowCount(0);
        for (OrdemDeServico os : todas) {
            if (statusFiltro != null && os.getStatus() != statusFiltro) continue;

            if (!busca.isEmpty()) {
                String titulo  = os.getTitulo() != null ? os.getTitulo().toLowerCase() : "";
                String placa   = os.getVeiculo() != null ? os.getVeiculo().getPlaca().toLowerCase() : "";
                String modelo  = os.getVeiculo() != null && os.getVeiculo().getModelo() != null
                    ? os.getVeiculo().getModelo().getNome().toLowerCase() : "";
                if (!titulo.contains(busca) && !placa.contains(busca) && !modelo.contains(busca)) continue;
            }

            String tipo       = os.getTipoServico() != null ? os.getTipoServico().getLabel() : "—";
            String statusLabel = os.getStatus() != null ? formatarStatus(os.getStatus()) : "—";
            String veiculo    = os.getVeiculo() != null
                ? (os.getVeiculo().getModelo() != null
                    ? os.getVeiculo().getModelo().getNome() + "  ·  " + os.getVeiculo().getPlaca()
                    : os.getVeiculo().getPlaca())
                : "—";

            mdl.addRow(new Object[]{os.getIdOrdemDeServico(), os.getTitulo(), tipo, statusLabel, veiculo});
        }
    }

    private String formatarStatus(OrdemDeServico.Status s) {
        return switch (s) {
            case ABERTA        -> "Aberta";
            case EM_ANDAMENTO  -> "Em Andamento";
            case CONCLUIDA     -> "Concluída";
        };
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }
}
