package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class V_VisualizarCatalogoServicos extends JPanel {

    private final OficinaController controller;
    private List<CatalogoServicoEntity> todos = new ArrayList<>();

    private JPanel corpo;
    private JScrollPane scroll;
    private JTextField txt_Busca;
    private JComboBox<String> cmb_Sistema;

    private static final String[] SISTEMAS_LABEL = {
        "Todos os sistemas", "Motor", "Transmissão", "Direção",
        "Suspensão", "Freios", "Arrefecimento", "Elétrica", "Alimentação", "Outros"
    };
    private static final String[] SISTEMAS_CODE = {
        null, "MOTOR", "TRANSMISSAO", "DIRECAO",
        "SUSPENSAO", "FREIOS", "ARREFECIMENTO", "ELETRICA", "ALIMENTACAO", "OUTROS"
    };

    public V_VisualizarCatalogoServicos(OficinaController controller) {
        this.controller = controller;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construirInterface();
        carregar();
    }

    private void construirInterface() {
        // Header: título + filtros
        JLabel titulo = new JLabel("Página Inicial > Serviços Cadastrados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Barra de busca
        txt_Busca = new JTextField();
        txt_Busca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Busca.setPreferredSize(new Dimension(220, 32));
        txt_Busca.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        txt_Busca.putClientProperty("JTextField.placeholderText", "Buscar serviço...");

        // Combo sistema
        cmb_Sistema = new JComboBox<>(SISTEMAS_LABEL);
        cmb_Sistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Sistema.setPreferredSize(new Dimension(185, 32));

        JPanel pnl_Filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_Filtros.setOpaque(false);
        pnl_Filtros.add(new JLabel("Buscar:") {{ setFont(new Font("Segoe UI", Font.BOLD, 12)); }});
        pnl_Filtros.add(txt_Busca);
        pnl_Filtros.add(Box.createHorizontalStrut(4));
        pnl_Filtros.add(new JLabel("Sistema:") {{ setFont(new Font("Segoe UI", Font.BOLD, 12)); }});
        pnl_Filtros.add(cmb_Sistema);

        JPanel pnl_Norte = new JPanel(new BorderLayout(0, 6));
        pnl_Norte.setOpaque(false);
        pnl_Norte.add(titulo, BorderLayout.NORTH);
        pnl_Norte.add(pnl_Filtros, BorderLayout.CENTER);
        add(pnl_Norte, BorderLayout.NORTH);

        // Corpo rolável
        corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);

        scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);

        // Listeners
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarCorpo(); }
            public void removeUpdate(DocumentEvent e) { atualizarCorpo(); }
            public void changedUpdate(DocumentEvent e) { atualizarCorpo(); }
        };
        txt_Busca.getDocument().addDocumentListener(dl);
        cmb_Sistema.addActionListener(e -> atualizarCorpo());
    }

    private void carregar() {
        todos = controller.listarCatalogoServicos();
        atualizarCorpo();
    }

    private void atualizarCorpo() {
        String busca = txt_Busca.getText().trim().toLowerCase();
        String sistemaCode = SISTEMAS_CODE[cmb_Sistema.getSelectedIndex()];

        List<CatalogoServicoEntity> filtrados = todos.stream()
            .filter(c -> busca.isEmpty() || c.getNome().toLowerCase().contains(busca))
            .filter(c -> sistemaCode == null || sistemaCode.equals(c.getSistema()))
            .collect(Collectors.toList());

        corpo.removeAll();

        if (filtrados.isEmpty()) {
            JLabel vazio = new JLabel(todos.isEmpty()
                ? "Nenhum serviço cadastrado. Acesse 'Itens de Serviço' para adicionar."
                : "Nenhum serviço corresponde ao filtro.");
            vazio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vazio.setForeground(Color.decode("#888888"));
            vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
            vazio.setBorder(BorderFactory.createEmptyBorder(20, 4, 0, 0));
            corpo.add(vazio);
        } else {
            corpo.add(criarSecaoServicos(filtrados));
        }

        corpo.revalidate();
        corpo.repaint();
    }

    private JPanel criarSecaoServicos(List<CatalogoServicoEntity> itens) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("Catálogo de Serviços  (" + itens.size() + " serviços)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        lbl.setPreferredSize(new Dimension(0, 32));
        card.add(lbl, BorderLayout.NORTH);

        JPanel gruposPanel = new JPanel();
        gruposPanel.setLayout(new BoxLayout(gruposPanel, BoxLayout.Y_AXIS));
        gruposPanel.setOpaque(false);

        // Agrupa por sistema preservando a ordem
        Map<String, List<CatalogoServicoEntity>> porSistema = new LinkedHashMap<>();
        for (String s : new String[]{"MOTOR","TRANSMISSAO","DIRECAO","SUSPENSAO","FREIOS",
                "ARREFECIMENTO","ELETRICA","ALIMENTACAO","OUTROS"})
            porSistema.put(s, new ArrayList<>());
        for (CatalogoServicoEntity c : itens)
            porSistema.getOrDefault(c.getSistema(), porSistema.get("OUTROS")).add(c);

        for (Map.Entry<String, List<CatalogoServicoEntity>> entry : porSistema.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            String sistemaLabel = new CatalogoServicoEntity(null, "", "", 0,
                "PADRAO", entry.getKey(), null, null).getSistemaLabel();

            JLabel lblSist = new JLabel("  ▸  " + sistemaLabel);
            lblSist.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblSist.setForeground(Color.decode("#555555"));
            lblSist.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
            lblSist.setAlignmentX(Component.LEFT_ALIGNMENT);
            gruposPanel.add(lblSist);

            gruposPanel.add(criarTabelaServicos(entry.getValue(), "#FF9900"));
            gruposPanel.add(Box.createVerticalStrut(4));
        }

        card.add(gruposPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarTabelaServicos(List<CatalogoServicoEntity> itens, String corHex) {
        String[] cols = {"Serviço", "Tipo", "Valor (R$)", "Validade KM", "Validade Meses"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (CatalogoServicoEntity i : itens) {
            String tipoLabel = "REVISAO".equals(i.getTipo()) ? "Revisão" : "Padrão";
            String km  = i.getValidadeKm() != null ? i.getValidadeKm() + " km" : "—";
            String mes = i.getValidadeMeses() != null ? i.getValidadeMeses() + " meses" : "—";
            mdl.addRow(new Object[]{
                i.getNome(), tipoLabel,
                String.format("R$ %.2f", i.getValor()), km, mes
            });
        }

        JTable tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(24);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionBackground(Color.decode(corHex).brighter());
        tabela.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row >= 0 && row < itens.size()) abrirEditor(itens.get(row));
                }
            }
        });

        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        tabela.getColumnModel().getColumn(2).setCellRenderer(direita);

        JScrollPane sp = new JScrollPane(tabela);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        int altTabela = tabela.getRowHeight() * itens.size() + 28;
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, altTabela));
        sp.setPreferredSize(new Dimension(0, altTabela));
        sp.setBorder(BorderFactory.createLineBorder(Color.decode("#E8E8E8")));

        JLabel lnk_Editar = new JLabel("<html><u>✎ Editar selecionado</u></html>");
        lnk_Editar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lnk_Editar.setForeground(Color.decode("#0066CC"));
        lnk_Editar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lnk_Editar.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        lnk_Editar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabela.getSelectedRow();
                if (row >= 0 && row < itens.size()) abrirEditor(itens.get(row));
                else JOptionPane.showMessageDialog(V_VisualizarCatalogoServicos.this,
                    "Selecione um serviço na tabela para editar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout(0, 2));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, altTabela + 24));
        wrapper.setPreferredSize(new Dimension(0, altTabela + 24));
        wrapper.add(sp, BorderLayout.CENTER);
        wrapper.add(lnk_Editar, BorderLayout.SOUTH);
        return wrapper;
    }

    private void abrirEditor(CatalogoServicoEntity item) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_EditarItemServico(controller, item));
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio; private final Color cor;
        RoundedBorder(int r, Color c) { this.raio = r; this.cor = c; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2.dispose();
        }
    }
}
