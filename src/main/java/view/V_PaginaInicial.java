package view;

import controller.OficinaController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * Painel de Conteúdo da Página Inicial (Limpo e Otimizado).
 */
public class V_PaginaInicial extends JPanel {

    private static final Color COR_CARTAO = new Color(255, 253, 248, 235);
    private static final int TAM_ICONE_CARD = 65;
    private static final int TAM_ICONE_ESTAT = 36;
    private static final int GAP_CONSULTAS = 16;

    private JPanel pnl_Header;
    private JPanel pnl_BotoesVisualizar;
    private JLabel lbl_NomeGaragem;
    private JLabel lbl_CnpjGaragem;
    private JLabel lbl_Especialidade;
    private JButton btn_Configuracoes;

    private Botoes.CardNavegacao btn_VisServicos;
    private Botoes.CardNavegacao btn_VisOrcamentos;
    private Botoes.CardNavegacao btn_VisClientes;
    private Botoes.CardNavegacao btn_VisVeiculos;
    private Botoes.CardNavegacao btn_Estoque;

    private OficinaController controller;

    public V_PaginaInicial(OficinaController controller) {
        this.controller = controller;

        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        preencherDadosDinamicos();
        layoutComponents();
        vincularAcoesBotoes();
    }

    private void initComponents() {
        pnl_Header = new PainelCartao(new BorderLayout(20, 10), 14, COR_CARTAO);
        pnl_Header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel pnl_TextoHeader = new JPanel();
        pnl_TextoHeader.setLayout(new BoxLayout(pnl_TextoHeader, BoxLayout.Y_AXIS));
        pnl_TextoHeader.setOpaque(false);

        lbl_NomeGaragem = new JLabel();
        lbl_NomeGaragem.setFont(new Font("Arial", Font.BOLD, 20));
        lbl_NomeGaragem.setAlignmentX(Component.LEFT_ALIGNMENT);

        lbl_CnpjGaragem = new JLabel();
        lbl_CnpjGaragem.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl_CnpjGaragem.setForeground(Color.GRAY);
        lbl_CnpjGaragem.setAlignmentX(Component.LEFT_ALIGNMENT);

        lbl_Especialidade = new JLabel();
        lbl_Especialidade.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl_Especialidade.setForeground(Color.GRAY);
        lbl_Especialidade.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnl_TextoHeader.add(lbl_NomeGaragem);
        pnl_TextoHeader.add(Box.createVerticalStrut(6));
        pnl_TextoHeader.add(lbl_CnpjGaragem);
        pnl_TextoHeader.add(Box.createVerticalStrut(2));
        pnl_TextoHeader.add(lbl_Especialidade);

        JLabel lbl_AvatarGaragem = new JLabel(obterIcone("/assets/icons/oficina.png", 52, IconeVetorial.Tipo.GARAGEM, Color.decode("#FF9900")));
        lbl_AvatarGaragem.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        JPanel pnl_Identidade = new JPanel(new BorderLayout(14, 0));
        pnl_Identidade.setOpaque(false);
        pnl_Identidade.add(lbl_AvatarGaragem, BorderLayout.WEST);
        pnl_Identidade.add(pnl_TextoHeader, BorderLayout.CENTER);

        btn_Configuracoes = new BotaoVidro3D();
        ImageIcon icoConfig = carregarERedimensionarIcone("/assets/icons/config.png", 50, 50);
        if (icoConfig != null) {
            btn_Configuracoes.setIcon(icoConfig);
        } else {
            btn_Configuracoes.setText("⚙");
            btn_Configuracoes.setFont(new Font("Segoe UI", Font.PLAIN, 26));
            btn_Configuracoes.setForeground(Color.decode("#555555"));
        }
        btn_Configuracoes.setPreferredSize(new Dimension(80, 80));
        btn_Configuracoes.setToolTipText("Configurações da Oficina");

        JPanel pnl_ContainerBusca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnl_ContainerBusca.setOpaque(false);
        pnl_ContainerBusca.add(btn_Configuracoes);

        pnl_Header.add(pnl_Identidade, BorderLayout.CENTER);
        pnl_Header.add(pnl_ContainerBusca, BorderLayout.EAST);

        // Seção de Consultas / Cadastros
        pnl_BotoesVisualizar = criarCard("Consultas / Cadastros", obterIcone("/assets/icons/consultas.png", 18, IconeVetorial.Tipo.LUPA, Color.decode("#FF9900")));
        JPanel corpoConsultas = (JPanel) pnl_BotoesVisualizar.getComponent(1);
        corpoConsultas.setLayout(new GradeResponsiva(Botoes.DIM_CARD.width, Botoes.DIM_CARD.height, GAP_CONSULTAS, GAP_CONSULTAS));
        corpoConsultas.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

        btn_VisServicos = new Botoes.CardNavegacao("Serviços", obterIcone("/assets/icons/vservicos.png", TAM_ICONE_CARD, IconeVetorial.Tipo.SERVICO, Color.DARK_GRAY));
        btn_VisOrcamentos = new Botoes.CardNavegacao("Orçamentos", obterIcone("/assets/icons/vorcamentos.png", TAM_ICONE_CARD, IconeVetorial.Tipo.DOCUMENTO, Color.DARK_GRAY));
        btn_VisClientes = new Botoes.CardNavegacao("Clientes", obterIcone("/assets/icons/vclientes.png", TAM_ICONE_CARD, IconeVetorial.Tipo.PESSOA, Color.DARK_GRAY));
        btn_VisVeiculos = new Botoes.CardNavegacao("Veículos", obterIcone("/assets/icons/vveiculos.png", TAM_ICONE_CARD, IconeVetorial.Tipo.MODELO, Color.DARK_GRAY));
        btn_Estoque = new Botoes.CardNavegacao("Estoque", obterIcone("/assets/icons/estoque.png", TAM_ICONE_CARD, IconeVetorial.Tipo.PECA, Color.DARK_GRAY));

        corpoConsultas.add(btn_VisServicos);
        corpoConsultas.add(btn_VisOrcamentos);
        corpoConsultas.add(btn_VisClientes);
        corpoConsultas.add(btn_VisVeiculos);
        corpoConsultas.add(btn_Estoque);
    }

    private void preencherDadosDinamicos() {
        if (controller != null) {
            lbl_NomeGaragem.setText(controller.getOficina().getNome());
            lbl_CnpjGaragem.setText("CNPJ: " + controller.getOficina().getCnpj());
            lbl_Especialidade.setText("Especialidade: " + controller.getOficina().getDescricao());
        } else {
            lbl_NomeGaragem.setText("Oficina Desconectada");
        }
    }

    private void layoutComponents() {
        JPanel pnl_Centro = new PainelCentroRolavel();
        pnl_Centro.setLayout(new BoxLayout(pnl_Centro, BoxLayout.Y_AXIS));
        pnl_Centro.setOpaque(false);
        pnl_BotoesVisualizar.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Centro.add(pnl_BotoesVisualizar);
        pnl_Centro.add(Box.createVerticalStrut(16));
        pnl_Centro.add(criarSecaoCalendario());
        pnl_Centro.add(Box.createVerticalStrut(16));
        pnl_Centro.add(criarSecaoEstatisticas());

        JScrollPane scroll = new JScrollPane(pnl_Centro);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scroll);

        add(pnl_Header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void vincularAcoesBotoes() {
        btn_Configuracoes.addActionListener(e -> navegarPara(new V_Configuracoes(this.controller)));
        btn_VisServicos.addActionListener(e -> navegarPara(new V_VisualizarServicos(this.controller)));
        btn_VisOrcamentos.addActionListener(e -> navegarPara(new V_AprovarOrcamento(this.controller)));
        btn_VisClientes.addActionListener(e -> navegarPara(new V_VisualizarClientes(this.controller)));
        btn_VisVeiculos.addActionListener(e -> navegarPara(new V_VisualizarVeiculos(this.controller)));
        btn_Estoque.addActionListener(e -> navegarPara(new V_Estoque(this.controller)));
    }

    private void navegarPara(JPanel novoPainelCentral) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof V_Main) {
            ((V_Main) window).atualizarConteudo(novoPainelCentral);
        } else {
            System.err.println("Erro: V_Main não encontrado como ancestral.");
        }
    }

    private JPanel criarSecaoCalendario() {
        JPanel card = criarCard("Agenda de Serviços e Prazos",
                obterIcone("/assets/icons/calendario.png", 18, IconeVetorial.Tipo.CALENDARIO, Color.decode("#FF9900")));

        PainelCalendario painelCalendario = new PainelCalendario(controller);

        JPanel pnl_TituloCard = (JPanel) card.getComponent(0);
        JPanel pnl_TituloTexto = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_TituloTexto.setOpaque(false);
        while (pnl_TituloCard.getComponentCount() > 0) {
            pnl_TituloTexto.add(pnl_TituloCard.getComponent(0));
        }
        pnl_TituloCard.setLayout(new BorderLayout());
        pnl_TituloCard.add(pnl_TituloTexto, BorderLayout.WEST);
        pnl_TituloCard.add(painelCalendario.getPainelModos(), BorderLayout.EAST);
        pnl_TituloCard.setPreferredSize(new Dimension(0, 38));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BorderLayout());
        corpo.add(painelCalendario, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarSecaoEstatisticas() {
        JPanel card = criarCard("Estatísticas da Oficina", obterIcone("/assets/icons/estatisticas.png", 18, IconeVetorial.Tipo.GRAFICO, Color.decode("#FF9900")));

        JPanel linha = new JPanel(new GridLayout(1, 5, 12, 0));
        linha.setOpaque(false);
        linha.add(criarCardEstat("Montadoras", controller.contarMontadoras(), "#9B59B6", "/assets/icons/montadoras.png", IconeVetorial.Tipo.MONTADORA, () -> navegarPara(new V_VisualizarMontadoras(controller))));
        linha.add(criarCardEstat("Modelos", controller.contarModelos(), "#E67E22", "/assets/icons/modelos.png", IconeVetorial.Tipo.MODELO, () -> navegarPara(new V_VisualizarModelos(controller))));
        linha.add(criarCardEstat("Peças Cadastradas", controller.listarTodasPecas().size(), "#16A085", "/assets/icons/pecas.png", IconeVetorial.Tipo.PECA, () -> navegarPara(new V_VisualizarPecas(controller))));
        linha.add(criarCardEstat("Serviços Cadastrados", controller.contarCatalogoServicos(), "#8E44AD", "/assets/icons/vservicos.png", IconeVetorial.Tipo.SERVICO, () -> navegarPara(new V_VisualizarCatalogoServicos(controller))));
        linha.add(criarCardEstat("OS Total", controller.contarOS(), "#34495E", "/assets/icons/os_total.png", IconeVetorial.Tipo.DOCUMENTO, () -> navegarPara(new V_VisualizarOSFiltrado(controller, null))));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BorderLayout());
        corpo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        corpo.add(linha, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCard(String titulo, Icon icone) {
        int raioCard = 14;
        PainelCartao card = new PainelCartao(new BorderLayout(0, 12), raioCard, COR_CARTAO);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(raioCard, Color.decode("#E0E0E0")),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl_TituloCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_TituloCard.setOpaque(false);
        pnl_TituloCard.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        pnl_TituloCard.setPreferredSize(new Dimension(0, 30));

        if (icone != null) pnl_TituloCard.add(new JLabel(icone));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        pnl_TituloCard.add(lbl);

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BorderLayout());

        card.add(pnl_TituloCard, BorderLayout.NORTH);
        card.add(corpo, BorderLayout.CENTER);
        return card;
    }

    private Botoes.CardEstatistica criarCardEstat(String rotulo, int valor, String corHex, String caminhoIconePng, IconeVetorial.Tipo tipoIconeFallback, Runnable aoClicar) {
        Icon icone = (tipoIconeFallback != null)
                ? obterIcone(caminhoIconePng, TAM_ICONE_ESTAT, tipoIconeFallback, Color.decode(corHex))
                : (caminhoIconePng != null ? carregarERedimensionarIcone(caminhoIconePng, TAM_ICONE_ESTAT, TAM_ICONE_ESTAT) : null);

        Botoes.CardEstatistica btn = new Botoes.CardEstatistica(rotulo, valor, corHex, icone);
        btn.addActionListener(e -> aoClicar.run());
        return btn;
    }

    private ImageIcon carregarERedimensionarIcone(String caminho, int larguraAlvo, int alturaAlvo) {
        URL url = getClass().getResource(caminho);
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(caminho.startsWith("/") ? caminho.substring(1) : caminho);
        }
        if (url != null) {
            return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(larguraAlvo, alturaAlvo, Image.SCALE_SMOOTH));
        }
        return null;
    }

    private Icon obterIcone(String caminhoPng, int tamanho, IconeVetorial.Tipo tipoFallback, Color corFallback) {
        if (caminhoPng != null) {
            ImageIcon png = carregarERedimensionarIcone(caminhoPng, tamanho, tamanho);
            if (png != null) return png;
        }
        return new IconeVetorial(tipoFallback, corFallback, tamanho);
    }

    private static class BotaoVidro3D extends JButton {
        private boolean hover = false, pressionado = false;

        BotaoVidro3D() {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)   { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e)  { pressionado = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int raio = 20, elevacao = pressionado ? 1 : (hover ? 3 : 2), topo = pressionado ? 2 : 0;
            g2.setColor(new Color(0, 0, 0, hover ? 45 : 28));
            g2.fillRoundRect(2, 2 + elevacao, w - 4, h - 4, raio, raio);

            g2.setPaint(new GradientPaint(0, topo, hover ? new Color(255, 255, 255, 235) : new Color(255, 255, 255, 215), 0, h - 4, hover ? new Color(206, 221, 236, 215) : new Color(198, 212, 228, 195)));
            g2.fillRoundRect(1, topo, w - 4, h - 4 - topo, raio, raio);

            g2.setColor(new Color(255, 255, 255, hover ? 165 : 130));
            g2.fillRoundRect(4, topo + 2, w - 10, Math.max(4, (h - 4) / 3), raio - 6, raio - 6);

            g2.setColor(new Color(255, 255, 255, 170));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(1, topo, w - 5, h - 5 - topo, raio, raio);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio;
        private final Color cor;
        RoundedBorder(int raio, Color cor) { this.raio = raio; this.cor = cor; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(cor);
            g2d.draw(new RoundRectangle2D.Double(x, y, w - 1, h - 1, raio, raio));
            g2d.dispose();
        }
    }

    private static class PainelCentroRolavel extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 24; }
        @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return Math.max(24, visibleRect.height - 24); }
        @Override public boolean getScrollableTracksViewportWidth()  { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private static class GradeResponsiva implements LayoutManager {
        private final int larguraMin, altura, gapH, gapV;
        private int ultimasLinhas = -1;

        GradeResponsiva(int larguraMin, int altura, int gapH, int gapV) {
            this.larguraMin = larguraMin; this.altura = altura; this.gapH = gapH; this.gapV = gapV;
        }

        private int colunas(int disp, int n) { return Math.max(1, Math.min(Math.max(1, n), (disp + gapH) / (larguraMin + gapH))); }
        private int linhas(int n, int cols) { return n == 0 ? 0 : (n + cols - 1) / cols; }

        @Override public void addLayoutComponent(String name, Component comp) {}
        @Override public void removeLayoutComponent(Component comp) {}
        @Override public Dimension preferredLayoutSize(Container parent) {
            int n = parent.getComponentCount(), disp = parent.getWidth();
            if (disp <= 0) disp = n * larguraMin;
            int cols = colunas(disp, n), rows = linhas(n, cols);
            return new Dimension(parent.getWidth(), rows == 0 ? 0 : rows * altura + (rows - 1) * gapV);
        }
        @Override public Dimension minimumLayoutSize(Container parent) { return preferredLayoutSize(parent); }
        @Override public void layoutContainer(Container parent) {
            int n = parent.getComponentCount();
            if (n == 0) return;
            int disp = parent.getWidth(), cols = colunas(disp, n), rows = linhas(n, cols);
            int larguraCelula = Math.max(larguraMin, (disp - (cols - 1) * gapH) / cols);

            for (int i = 0; i < n; i++) {
                parent.getComponent(i).setBounds((i % cols) * (larguraCelula + gapH), (i / cols) * (altura + gapV), larguraCelula, altura);
            }
            if (rows != ultimasLinhas) {
                ultimasLinhas = rows;
                SwingUtilities.invokeLater(parent::revalidate);
            }
        }
    }

    private static class PainelCartao extends JPanel {
        private final int raio;
        private final Color corFundo;

        PainelCartao(LayoutManager layout, int raio, Color corFundo) {
            super(layout);
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(corFundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class IconeVetorial implements Icon {
        enum Tipo { GARAGEM, LUPA, GRAFICO, MONTADORA, MODELO, PECA, SERVICO, CATALOGO, DOCUMENTO, PESSOA, CALENDARIO }
        private final Tipo tipo;
        private final Color cor;
        private final int tam;

        IconeVetorial(Tipo tipo, Color cor, int tam) { this.tipo = tipo; this.cor = cor; this.tam = tam; }
        @Override public int getIconWidth()  { return tam; }
        @Override public int getIconHeight() { return tam; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(Math.max(1.4f, tam / 11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int s = tam, pad = Math.max(2, s / 7), area = s - pad * 2;
            switch (tipo) {
                case GARAGEM:
                    g2.drawPolyline(new int[]{pad, s / 2, s - pad}, new int[]{pad + area / 3, pad, pad + area / 3}, 3);
                    g2.drawRect(pad, pad + area / 3, area, area - area / 3);
                    break;
                case LUPA:
                    int d = (int) (area * 0.68);
                    g2.drawOval(pad, pad, d, d);
                    g2.drawLine(pad + d - 2, pad + d - 2, s - pad, s - pad);
                    break;
                case GRAFICO:
                    int bWidth = Math.max(2, area / 4);
                    g2.fillRoundRect(pad, s - pad - area / 3, bWidth, area / 3, 2, 2);
                    g2.fillRoundRect(pad + bWidth + 3, s - pad - (int) (area * 0.62), bWidth, (int) (area * 0.62), 2, 2);
                    g2.fillRoundRect(pad + (bWidth + 3) * 2, s - pad - area, bWidth, area, 2, 2);
                    break;
                case MONTADORA:
                    g2.drawRect(pad, pad + area / 4, area, area - area / 4);
                    g2.drawRect(pad + area / 6, pad, area / 4, area / 4);
                    g2.fillRect(pad + area / 4, pad + area / 2, Math.max(2, area / 8), Math.max(2, area / 8));
                    g2.fillRect(pad + area * 5 / 8, pad + area / 2, Math.max(2, area / 8), Math.max(2, area / 8));
                    break;
                case MODELO:
                    g2.drawRoundRect(pad, pad + area / 3, area, area / 3, area / 6, area / 6);
                    g2.fillOval(pad + area / 8, pad + area * 2 / 3, Math.max(3, area / 5), Math.max(3, area / 5));
                    g2.fillOval(pad + area * 5 / 8, pad + area * 2 / 3, Math.max(3, area / 5), Math.max(3, area / 5));
                    break;
                case PECA:
                    int[] xs = new int[6], ys = new int[6];
                    double rHex = area / 2.0, cx = s / 2.0, cy = s / 2.0;
                    for (int i = 0; i < 6; i++) {
                        xs[i] = (int) (cx + rHex * Math.cos(Math.toRadians(60 * i - 90)));
                        ys[i] = (int) (cy + rHex * Math.sin(Math.toRadians(60 * i - 90)));
                    }
                    g2.drawPolygon(xs, ys, 6);
                    g2.drawOval(s / 2 - area / 6, s / 2 - area / 6, Math.max(3, area / 3), Math.max(3, area / 3));
                    break;
                case SERVICO:
                    g2.drawLine(pad + 2, s - pad - 2, s - pad - 2, pad + 2);
                    g2.drawOval(pad, s - pad - area / 4, Math.max(3, area / 4), Math.max(3, area / 4));
                    g2.drawOval(s - pad - area / 4, pad, Math.max(3, area / 4), Math.max(3, area / 4));
                    break;
                case DOCUMENTO:
                    g2.drawRoundRect(pad, pad, area, area, 4, 4);
                    for (int i = 1; i <= 3; i++) g2.drawLine(pad + area / 5, pad + (area * i) / 4, s - pad - area / 5, pad + (area * i) / 4);
                    break;
                case PESSOA:
                    g2.drawOval(s / 2 - area / 5, pad, area * 2 / 5, area * 2 / 5);
                    g2.drawArc(pad, pad + area / 2, area, area / 2, 0, 180);
                    break;
                case CALENDARIO:
                    g2.drawRoundRect(pad, pad + area / 6, area, area - area / 6, 4, 4);
                    g2.drawLine(pad, pad + area / 6 + area / 4, s - pad, pad + area / 6 + area / 4);
                    g2.drawLine(pad + area / 4, pad, pad + area / 4, pad + area / 3);
                    g2.drawLine(pad + area * 3 / 4, pad, pad + area * 3 / 4, pad + area / 3);
                    break;
                default:
                    break;
            }
            g2.dispose();
        }
    }
}