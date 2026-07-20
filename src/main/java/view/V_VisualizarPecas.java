package view;

import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class V_VisualizarPecas extends JPanel {

    private final OficinaController controller;
    private DefaultTableModel mdl;
    private JTable tabela;
    private final List<PecaEntity> todasPecas = new ArrayList<>();
    private final List<PecaEntity> pecasFiltradas = new ArrayList<>();

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

    public V_VisualizarPecas(OficinaController controller) {
        this.controller = controller;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
    }

    private void construir() {
        // ---- Header ----
        JLabel titulo = new JLabel("Página Inicial > Peças Cadastradas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));

        JButton btn_Nova = new JButton("+ Nova Peça");
        btn_Nova.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Nova.setForeground(Color.WHITE);
        btn_Nova.setBackground(Color.decode("#28A745"));
        btn_Nova.setFocusPainted(false);
        btn_Nova.setBorderPainted(false);
        btn_Nova.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Nova.setPreferredSize(new Dimension(130, 34));
        btn_Nova.addActionListener(e -> navegar(new V_CadastrarPeca(controller)));

        JPanel pnl_TituloBtn = new JPanel(new BorderLayout());
        pnl_TituloBtn.setOpaque(false);
        pnl_TituloBtn.add(titulo, BorderLayout.WEST);
        pnl_TituloBtn.add(btn_Nova, BorderLayout.EAST);

        // ---- Barra de filtros ----
        txt_Busca = new JTextField();
        txt_Busca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Busca.setPreferredSize(new Dimension(220, 30));
        txt_Busca.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)));

        cmb_Sistema = new JComboBox<>(SISTEMAS_LABEL);
        cmb_Sistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Sistema.setPreferredSize(new Dimension(185, 30));

        JPanel pnl_Filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnl_Filtros.setOpaque(false);
        pnl_Filtros.add(new JLabel("Buscar:") {{ setFont(new Font("Segoe UI", Font.BOLD, 12)); }});
        pnl_Filtros.add(txt_Busca);
        pnl_Filtros.add(Box.createHorizontalStrut(4));
        pnl_Filtros.add(new JLabel("Sistema:") {{ setFont(new Font("Segoe UI", Font.BOLD, 12)); }});
        pnl_Filtros.add(cmb_Sistema);

        JPanel pnl_Header = new JPanel(new BorderLayout(0, 6));
        pnl_Header.setOpaque(false);
        pnl_Header.add(pnl_TituloBtn, BorderLayout.NORTH);
        pnl_Header.add(pnl_Filtros, BorderLayout.CENTER);

        // ---- Tabela ----
        String[] cols = {"Cód.", "Nome da Peça", "Sistema", "Vida Útil (Tempo)", "Vida Útil (KM)"};
        mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(28);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabela.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editarSelecionada();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));

        JLabel lnk_Editar = new JLabel("<html><u>✎ Editar selecionada</u></html>");
        lnk_Editar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lnk_Editar.setForeground(Color.decode("#0066CC"));
        lnk_Editar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lnk_Editar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { editarSelecionada(); }
        });

        JButton btn_Voltar = new JButton("← Voltar");
        btn_Voltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Voltar.setForeground(Color.WHITE);
        btn_Voltar.setBackground(Color.decode("#6C757D"));
        btn_Voltar.setFocusPainted(false);
        btn_Voltar.setBorderPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Voltar.setPreferredSize(new Dimension(120, 36));
        btn_Voltar.addActionListener(e -> navegar(new V_Configuracoes(controller)));

        JPanel rodape = new JPanel(new BorderLayout(8, 0));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        rodape.add(lnk_Editar, BorderLayout.WEST);
        rodape.add(btn_Voltar, BorderLayout.EAST);

        JPanel corpo = new JPanel(new BorderLayout(0, 0));
        corpo.setBackground(Color.WHITE);
        corpo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        corpo.add(scroll, BorderLayout.CENTER);
        corpo.add(rodape, BorderLayout.SOUTH);

        add(pnl_Header, BorderLayout.NORTH);
        add(corpo, BorderLayout.CENTER);

        // Listeners de filtro
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        };
        txt_Busca.getDocument().addDocumentListener(dl);
        cmb_Sistema.addActionListener(e -> filtrar());

        carregar();
    }

    private void carregar() {
        todasPecas.clear();
        todasPecas.addAll(controller.listarTodasPecas());
        filtrar();
    }

    private void filtrar() {
        String busca = txt_Busca.getText().trim().toLowerCase();
        String sistemaCode = SISTEMAS_CODE[cmb_Sistema.getSelectedIndex()];

        pecasFiltradas.clear();
        for (PecaEntity p : todasPecas) {
            if (!busca.isEmpty() && !p.getNomePopular().toLowerCase().contains(busca)) continue;
            if (sistemaCode != null && !sistemaCode.equals(p.getSistema())) continue;
            pecasFiltradas.add(p);
        }

        mdl.setRowCount(0);
        for (PecaEntity p : pecasFiltradas) {
            String tempo = "Não informado".equals(p.getVidaUtilTempo()) ? "—" : (p.getVidaUtilTempo() != null ? p.getVidaUtilTempo() : "—");
            String km    = "Não informado".equals(p.getVidaUtilKm())    ? "—" : (p.getVidaUtilKm()    != null ? p.getVidaUtilKm()    : "—");
            mdl.addRow(new Object[]{
                String.format("%04d", p.getIdPeca()),
                p.getNomePopular(),
                p.getSistemaLabel(),
                tempo,
                km
            });
        }
    }

    private void editarSelecionada() {
        int row = tabela.getSelectedRow();
        if (row >= 0 && row < pecasFiltradas.size()) {
            navegar(new V_EditarPeca(controller, pecasFiltradas.get(row)));
        } else {
            JOptionPane.showMessageDialog(this,
                "Selecione uma peça para editar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
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
            g2.draw(new RoundRectangle2D.Double(x, y, w - 1, h - 1, raio, raio));
            g2.dispose();
        }
    }
}
