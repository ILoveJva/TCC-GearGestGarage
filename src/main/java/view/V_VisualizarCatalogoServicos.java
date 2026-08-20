package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Path2D;
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

        // Barra de busca — mesmo estilo "vidro" (glassmorphism) das páginas de cadastro
        txt_Busca = new GlassTextField();
        txt_Busca.setPreferredSize(new Dimension(220, 34));
        txt_Busca.setToolTipText("Buscar serviço...");

        // Combo sistema
        cmb_Sistema = new GlassComboBox<>();
        cmb_Sistema.setPreferredSize(new Dimension(185, 34));
        for (String label : SISTEMAS_LABEL) cmb_Sistema.addItem(label);

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
        String[] cols = {"Serviço", "Valor Médio(R$)", "Validade KM", "Validade Meses"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (CatalogoServicoEntity i : itens) {
            String km  = i.getValidadeKm() != null ? i.getValidadeKm() + " km" : "—";
            String mes = i.getValidadeMeses() != null ? i.getValidadeMeses() + " meses" : "—";
            mdl.addRow(new Object[]{
                i.getNome(),
                String.format("R$ %.2f", i.getValor()), km, mes
            });
        }

        JTable tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(24);
        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(true);
        tabela.setShowVerticalLines(true);
        tabela.setGridColor(Color.decode("#E8E8E8"));
        tabela.setIntercellSpacing(new Dimension(0, 5));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionBackground(Color.decode("#FFE4BF"));
        tabela.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row >= 0 && row < itens.size()) abrirEditor(itens.get(row));
                }
            }
        });

        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 1; c < tabela.getColumnCount(); c++)
            tabela.getColumnModel().getColumn(c).setCellRenderer(centralizado);

        // Proporção de largura das colunas: Serviço 3x, demais 1x
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] proporcoes = {3, 1, 1, 1};
        for (int c = 0; c < tabela.getColumnCount(); c++)
            tabela.getColumnModel().getColumn(c).setPreferredWidth(proporcoes[c] * 100);

        DefaultTableCellRenderer cabecalhoCentralizado =
            (DefaultTableCellRenderer) tabela.getTableHeader().getDefaultRenderer();
        cabecalhoCentralizado.setHorizontalAlignment(SwingConstants.CENTER);

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

    // =========================================================================
    // CAMPOS "DE VIDRO" (glassmorphism) — mesmo padrão visual das páginas de
    // cadastro (ex.: V_CadastrarPeca), aplicado aqui aos filtros de busca/sistema.
    // =========================================================================
    private static final int RAIO_COMPONENTE_VIDRO = 12;

    /** Campo de texto com efeito de vidro translúcido, borda que reage a foco. */
    private static class GlassTextField extends JTextField {
        private boolean focado = false;

        GlassTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(Color.decode("#2B2E33"));
            setCaretColor(Color.decode("#2B2E33"));
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));

            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 210),
                    0, h, new Color(255, 255, 255, 145));
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));

            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE_VIDRO - 5, RAIO_COMPONENTE_VIDRO - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130);
            float espessura = focado ? 1.6f : 1f;
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));
            g2.dispose();
        }
    }

    /** JComboBox com o mesmo efeito de vidro do GlassTextField, sem fundo sólido padrão do Swing por cima. */
    private static class GlassComboBox<T> extends JComboBox<T> {
        private boolean focado = false;

        GlassComboBox() { super(); estilizar(); }

        private void estilizar() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(Color.decode("#2B2E33"));
            // O BasicComboPopup copia este background para a lista suspensa — não pode ser transparente.
            setBackground(Color.decode("#FFFFFF"));
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 4));
            setFocusable(true);
            setUI(new GlassComboBoxUI());
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));

            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 210),
                    0, h, new Color(255, 255, 255, 145));
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));

            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE_VIDRO - 5, RAIO_COMPONENTE_VIDRO - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130);
            float espessura = focado ? 1.6f : 1f;
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));
            g2.dispose();
        }
    }

    /** UI mínima para o GlassComboBox: evita fundo sólido do Swing e troca a seta por um triângulo vetorial. */
    private static class GlassComboBoxUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Propositalmente vazio — o fundo já é pintado em GlassComboBox.paintComponent().
        }

        @Override
        @SuppressWarnings("unchecked")
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            ListCellRenderer renderer = comboBox.getRenderer();
            Component c = renderer.getListCellRendererComponent(
                    listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setFont(comboBox.getFont());
            c.setForeground(comboBox.isEnabled() ? Color.decode("#2B2E33") : Color.GRAY);

            boolean opacoOriginal = false;
            if (c instanceof JComponent) {
                opacoOriginal = ((JComponent) c).isOpaque();
                ((JComponent) c).setOpaque(false);
            }

            boolean shouldValidate = c instanceof JPanel;
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate);

            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(opacoOriginal);
            }
        }

        @Override
        protected ComboPopup createPopup() {
            return new GlassComboPopup(comboBox);
        }

        @Override
        protected JButton createArrowButton() {
            JButton seta = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    int cx = w / 2, cy = h / 2;
                    Path2D triangulo = new Path2D.Double();
                    triangulo.moveTo(cx - 4, cy - 2);
                    triangulo.lineTo(cx + 4, cy - 2);
                    triangulo.lineTo(cx, cy + 3);
                    triangulo.closePath();
                    g2.setColor(Color.decode("#57626F"));
                    g2.fill(triangulo);
                    g2.dispose();
                }
            };
            seta.setPreferredSize(new Dimension(22, 22));
            seta.setContentAreaFilled(false);
            seta.setBorderPainted(false);
            seta.setFocusPainted(false);
            seta.setOpaque(false);
            seta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return seta;
        }
    }

    /** Popup do combo com cantos arredondados e conteúdo sólido/legível. */
    private static class GlassComboPopup extends BasicComboPopup {

        GlassComboPopup(JComboBox<Object> combo) {
            super(combo);
        }

        @Override
        protected void configurePopup() {
            super.configurePopup();
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }

        @Override
        protected void configureList() {
            super.configureList();
            list.setOpaque(true);
            list.setBackground(Color.decode("#FFFFFF"));
            list.setForeground(Color.decode("#2B2E33"));
            list.setSelectionBackground(Color.decode("#FFE4BF"));
            list.setSelectionForeground(Color.decode("#2B2E33"));
            list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        @Override
        protected JScrollPane createScroller() {
            JScrollPane scroller = super.createScroller();
            scroller.setOpaque(false);
            scroller.getViewport().setOpaque(false);
            scroller.setBorder(BorderFactory.createEmptyBorder());
            return scroller;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(Color.decode("#FFFFFF"));
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(Color.decode("#C3CDDA"));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void show() {
            super.show();
            try {
                Window janela = SwingUtilities.getWindowAncestor(this);
                if (janela != null) {
                    janela.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RAIO_COMPONENTE_VIDRO, RAIO_COMPONENTE_VIDRO));
                }
            } catch (Exception | Error ignorado) {
                // Sem suporte a formato de janela nesta plataforma.
            }
        }
    }
}
