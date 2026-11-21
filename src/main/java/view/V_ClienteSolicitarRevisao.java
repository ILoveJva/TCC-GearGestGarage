package view;

import controller.OficinaController;
import model.Oficina;
import model.Veiculo;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Formulário de solicitação de revisão: veículo do cliente + oficina escolhida na busca.
 *
 * Visual alinhado ao padrão em vidro (glassmorphism) usado em
 * V_EditarOrcamento / V_CadastrarOrcamento / V_VisualizarOrcamento —
 * mesma paleta, mesmo cabeçalho Aero, campos arredondados e botões de vidro.
 */
public class V_ClienteSolicitarRevisao extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mesma linguagem visual das telas de orçamento
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_BORDA_CARD   = Color.decode("#DDE3EA");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_LABEL_SUTIL  = Color.decode("#8A94A3");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_AERO_TEXTO   = Color.decode("#3A4149");
    private static final Color COR_LARANJA      = Color.decode("#FF9900");

    private final OficinaController controller;
    private final Oficina oficina;

    private JComboBox<Veiculo> cbbVeiculo;
    private JLabel lblOficina;
    private JTextField txtMotivo;
    private JTextArea txtDescricao;
    private JCheckBox chkLevar;
    private JCheckBox chkBuscar;

    public V_ClienteSolicitarRevisao(OficinaController controller, Oficina oficina) {
        this.controller = controller;
        this.oficina = oficina;

        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        initComponents();
    }

    private void initComponents() {
        JPanel card = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        card.setPreferredSize(new Dimension(620, 660));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // ---- cabeçalho em vidro Aero ----
        JPanel headerGlass = new CabecalhoVidro();
        headerGlass.setLayout(new BorderLayout());
        headerGlass.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        headerGlass.setPreferredSize(new Dimension(10, 42));
        headerGlass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel titulo = new JLabel("Oficinas > Solicitar Revisão");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_AERO_TEXTO);
        headerGlass.add(titulo, BorderLayout.WEST);

        // ---- formulário ----
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        // Veículo
        form.add(rotulo("Veículo"));
        form.add(Box.createVerticalStrut(4));
        cbbVeiculo = new JComboBox<>();
        cbbVeiculo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbbVeiculo.setBackground(Color.WHITE);
        estilizarCombo(cbbVeiculo);
        for (Veiculo v : controller.listarMeusVeiculos()) cbbVeiculo.addItem(v);
        cbbVeiculo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Veiculo ve) {
                    String marca = ve.getModelo() != null && ve.getModelo().getMontadora() != null
                            ? ve.getModelo().getMontadora().getNome() : "";
                    String modelo = ve.getModelo() != null ? ve.getModelo().getNome() : "";
                    setText(marca + " " + modelo + " — " + ve.getPlaca());
                }
                return this;
            }
        });
        cbbVeiculo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbbVeiculo.setPreferredSize(new Dimension(0, 34));
        cbbVeiculo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        form.add(cbbVeiculo);
        form.add(Box.createVerticalStrut(14));

        // Oficina escolhida
        form.add(rotulo("Solicitando em"));
        form.add(Box.createVerticalStrut(4));

        PainelGradiente pnlOficina = new PainelGradiente(new BorderLayout(10, 0),
                Color.WHITE, COR_CARD_TOPO, COR_BORDA_CARD);
        pnlOficina.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 10));
        pnlOficina.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlOficina.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        pnlOficina.setPreferredSize(new Dimension(0, 64));

        lblOficina = new JLabel();
        lblOficina.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblOficina.setForeground(COR_TEXTO_CAMPO);
        atualizarLabelOficina();

        BotaoVidro btnAlterar = botaoAcao("Alterar", "#8A94A3", 100, 32);
        btnAlterar.addActionListener(e -> navegar(new V_ClienteBuscarOficinas(this.controller)));

        pnlOficina.add(lblOficina, BorderLayout.CENTER);
        pnlOficina.add(btnAlterar, BorderLayout.EAST);
        form.add(pnlOficina);
        form.add(Box.createVerticalStrut(14));

        // Motivo
        form.add(rotulo("Motivo"));
        form.add(Box.createVerticalStrut(4));
        txtMotivo = new CampoArredondado();
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMotivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtMotivo.setPreferredSize(new Dimension(0, 34));
        txtMotivo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        form.add(txtMotivo);
        form.add(Box.createVerticalStrut(14));

        // Descrição
        form.add(rotulo("Descrição"));
        form.add(Box.createVerticalStrut(4));
        txtDescricao = new JTextArea(5, 20);
        txtDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescricao.setForeground(COR_TEXTO_CAMPO);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setOpaque(false);
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.setBorder(null);
        scrollDescricao.setOpaque(false);
        scrollDescricao.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scrollDescricao);

        PainelArredondado pnlDescricao = new PainelArredondado(new BorderLayout(), txtDescricao);
        pnlDescricao.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        pnlDescricao.add(scrollDescricao, BorderLayout.CENTER);
        pnlDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDescricao.setPreferredSize(new Dimension(0, 130));
        pnlDescricao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        form.add(pnlDescricao);
        form.add(Box.createVerticalStrut(12));

        // Opções de logística
        JPanel pnlOpcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnlOpcoes.setOpaque(false);
        pnlOpcoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlOpcoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        chkLevar = checkbox("Levar o veículo até a oficina");
        chkBuscar = checkbox("Buscar o veículo na minha casa");
        pnlOpcoes.add(chkLevar);
        pnlOpcoes.add(chkBuscar);
        form.add(pnlOpcoes);

        // ---- rodapé: Voltar + Enviar ----
        BotaoVidro btnVoltar = botaoAcao("← Voltar", "#8A94A3", 130, 40);
        btnVoltar.addActionListener(e -> navegar(new V_ClienteBuscarOficinas(this.controller)));

        BotaoVidro btnEnviar = new BotaoVidro("ENVIAR", COR_LARANJA);
        btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEnviar.setPreferredSize(new Dimension(220, 45));
        btnEnviar.addActionListener(e -> enviar());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btnVoltar);
        pnlBtn.add(btnEnviar);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scrollForm);

        card.add(headerGlass, BorderLayout.NORTH);
        card.add(scrollForm, BorderLayout.CENTER);
        card.add(pnlBtn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(card, gbc);
    }

    // =========================================================================
    // LÓGICA (inalterada)
    // =========================================================================
    private void atualizarLabelOficina() {
        lblOficina.setText("<html><b>" + oficina.getNome() + "</b><br>" + oficina.getEndereco() + "</html>");
    }

    private void enviar() {
        Veiculo veiculo = (Veiculo) cbbVeiculo.getSelectedItem();
        String motivo = txtMotivo.getText().trim();

        if (veiculo == null) {
            DialogoAlerta.aviso(this, "Você precisa ter um veículo cadastrado para solicitar revisão.", "Dados inválidos");
            return;
        }
        if (motivo.isEmpty()) {
            DialogoAlerta.aviso(this, "Informe o motivo da solicitação.", "Dados inválidos");
            return;
        }

        try {
            controller.solicitarRevisao(veiculo.getIdVeiculo(), oficina.getIdOficina(), motivo,
                    txtDescricao.getText().trim(), chkLevar.isSelected(), chkBuscar.isSelected());
            DialogoAlerta.sucesso(this, "Solicitação enviada com sucesso!", "Sucesso");
            navegar(new V_ClienteOrcamentos(controller));
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao enviar solicitação: " + ex.getMessage(), "Erro");
        }
    }

    private void navegar(JPanel pagina) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela instanceof V_ClienteMain) ((V_ClienteMain) janela).atualizarConteudo(pagina);
    }

    // =========================================================================
    // HELPERS VISUAIS
    // =========================================================================
    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COR_LABEL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JCheckBox checkbox(String texto) {
        JCheckBox c = new JCheckBox(texto);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        c.setForeground(COR_LABEL);
        c.setOpaque(false);
        c.setFocusPainted(false);
        c.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return c;
    }

    private BotaoVidro botaoAcao(String texto, String corHex, int w, int h) {
        BotaoVidro btn = new BotaoVidro(texto, Color.decode(corHex));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setPreferredSize(new Dimension(w, h));
        return btn;
    }

    /** Combo com fundo e borda arredondados (mesmo raio dos campos de texto). */
    private static void estilizarCombo(JComboBox<?> cmb) {
        cmb.setUI(new ComboArredondadoUI());
        cmb.setOpaque(false);
        cmb.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 4));
    }

    // =========================================================================
    // CLASSES VISUAIS (mesmas das demais telas em vidro)
    // =========================================================================

    /** Painel com fundo em gradiente suave, opcionalmente com uma borda arredondada colorida. */
    private static class PainelGradiente extends JPanel {
        private final Color corTopo;
        private final Color corBase;
        private final Color corBorda;

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase) {
            this(layout, corTopo, corBase, null);
        }

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase, Color corBorda) {
            super(layout);
            this.corTopo = corTopo;
            this.corBase = corBase;
            this.corBorda = corBorda;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setPaint(new GradientPaint(0, 0, corTopo, 0, h, corBase));
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14));

            if (corBorda != null) {
                g2.setStroke(new BasicStroke(1.3f));
                g2.setColor(corBorda);
                g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.5, h - 1.5, 14, 14));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Painel branco arredondado (para a área de descrição); borda laranja quando o campo interno tem foco. */
    private static class PainelArredondado extends JPanel {
        private static final int RAIO = 14;
        private boolean focado = false;

        PainelArredondado(LayoutManager layout, JComponent campo) {
            super(layout);
            setOpaque(false);
            campo.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e)   { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO);
            g2.setColor(Color.WHITE);
            g2.fill(forma);
            g2.setColor(focado ? Color.decode("#FF9900") : Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(forma);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Campo de texto com fundo e borda arredondados; borda laranja quando focado. */
    private static class CampoArredondado extends JTextField {
        private static final int RAIO = 14;
        private boolean focado = false;

        CampoArredondado() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e)   { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(focado ? Color.decode("#FF9900") : Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO));
            g2.dispose();
        }
    }

    /** UI de combo com cantos arredondados e seta simples. */
    private static class ComboArredondadoUI extends BasicComboBoxUI {
        private static final int RAIO = 14;

        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.decode("#666666"));
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                    g2.dispose();
                }
            };
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, c.getWidth() - 1, c.getHeight() - 1, RAIO, RAIO);
            g2.setColor(Color.WHITE);
            g2.fill(forma);
            g2.setColor(Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(forma);
            g2.dispose();
            super.paint(g, c);
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // fundo já pintado em paint(), com cantos arredondados
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false); // evita o retângulo azul de seleção
        }
    }

    /** Cabeçalho fino em vidro Aero, usado na faixa de título no topo do card. */
    private static class CabecalhoVidro extends JPanel {
        private static final Color TOPO_A = Color.decode("#FBFBFC");
        private static final Color TOPO_B = Color.decode("#ECEEF1");
        private static final Color BASE_A = Color.decode("#DADDE2");
        private static final Color BASE_B = Color.decode("#EFF1F3");
        private static final Color BORDA  = Color.decode("#B6BCC4");

        CabecalhoVidro() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int meio = h / 2;

            RoundRectangle2D corpo = new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14);
            Shape clipAnterior = g2.getClip();
            g2.clip(corpo);

            g2.setPaint(new GradientPaint(0, 0, TOPO_A, 0, meio, TOPO_B));
            g2.fillRect(0, 0, w, meio);
            g2.setPaint(new GradientPaint(0, meio, BASE_A, 0, h, BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 120));
            g2.fillRect(0, 0, w, Math.max(1, h / 4));

            g2.setClip(clipAnterior);

            g2.setColor(BORDA);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(corpo);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botão de ação em vidro: gradiente translúcido, brilho frosted no topo e reação a hover/clique. */
    private static class BotaoVidro extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoVidro(String texto, Color corBase) {
            super(texto);
            this.corBase = corBase;
            this.corClara = clarear(corBase);
            this.corEscura = escurecer(corBase);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)   { sobreMouse = false; repaint(); }
                @Override public void mousePressed(MouseEvent e)  { pressionado = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int raio = 12;

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, raio, raio));

            Color corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            RoundRectangle2D corpo = new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, raio, raio);

            Shape clipAnterior = g2.getClip();
            g2.clip(corpo);

            g2.setPaint(new GradientPaint(0, 0, comAlpha(clarear(corPreenchimento), 220),
                    0, h, comAlpha(corPreenchimento, 190)));
            g2.fill(corpo);

            g2.setColor(new Color(255, 255, 255, 60));
            g2.fill(new Ellipse2D.Double(-w * 0.1, -h * 0.7, w * 1.2, h * 1.4));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), raio - 5, raio - 5));

            g2.setClip(clipAnterior);

            g2.setColor(comAlpha(escurecer(corPreenchimento), 160));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(corpo);

            g2.setColor(new Color(255, 255, 255, 110));
            g2.draw(new RoundRectangle2D.Double(1.5, 1.5, w - 4, h - 6, raio - 2, raio - 2));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== utilitários de cor =====
    private static Color clarear(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1] * 0.75f, Math.min(1f, hsb[2] * 1.18f));
    }

    private static Color escurecer(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.82f);
    }

    private static Color comAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}