package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Identidade visual do Portal do Cliente (protótipo mobile do Figma adaptado para desktop):
 * fundo branco, tiles cinza-claro arredondados com ícone preto, logo em texto no topo
 * e avatar circular do usuário.
 */
final class EstiloCliente {

    private EstiloCliente() {}

    static final Color FUNDO        = Color.WHITE;
    static final Color TILE         = Color.decode("#DCDCDC");
    static final Color TILE_HOVER   = Color.decode("#CFCFCF");
    static final Color TEXTO        = Color.decode("#1A1A1A");
    static final Color TEXTO_SUAVE  = Color.decode("#6B6B6B");
    static final Color LINHA        = Color.decode("#1A1A1A");
    static final Color ACENTO       = Color.decode("#FF9900");

    static final String FONTE = "Segoe UI";

    static Font fonte(int estilo, int tamanho) { return new Font(FONTE, estilo, tamanho); }

    // ------------------------------------------------------------------ imagens

    /** Carrega um recurso do classpath redimensionado mantendo a proporção (cabe em larg x alt). */
    static ImageIcon recurso(String caminho, int larg, int alt) {
        URL url = EstiloCliente.class.getResource(caminho);
        if (url == null) return null;
        ImageIcon orig = new ImageIcon(url);
        int w = orig.getIconWidth(), h = orig.getIconHeight();
        if (w <= 0 || h <= 0) return null;
        double esc = Math.min(larg / (double) w, alt / (double) h);
        return new ImageIcon(orig.getImage().getScaledInstance(
                (int) Math.round(w * esc), (int) Math.round(h * esc), Image.SCALE_SMOOTH));
    }

    /** Logo "Gear Gest GARAGE" (só o texto, como no cabeçalho do protótipo). */
    static JLabel logo(int altura) {
        JLabel lbl = new JLabel();
        ImageIcon ic = recurso("/assets/images/logo_texto.png", altura * 3, altura);
        if (ic != null) {
            lbl.setIcon(ic);
        } else {
            lbl.setText("<html><span style='font-size:11px'>Gear Gest</span><br><b style='font-size:18px'>GARAGE</b></html>");
            lbl.setForeground(TEXTO);
        }
        return lbl;
    }

    /**
     * Imagem quadrada recortada em círculo. Se não houver foto, desenha as iniciais do nome
     * sobre um círculo laranja (cor de destaque do sistema).
     */
    static ImageIcon avatar(String caminhoFoto, String nome, int diametro) {
        BufferedImage out = new BufferedImage(diametro, diametro, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        float traco = Math.max(1.5f, diametro / 60f);
        double m = traco / 2 + 0.5; // margem para a borda não ser cortada
        Ellipse2D circulo = new Ellipse2D.Double(m, m, diametro - 2 * m, diametro - 2 * m);

        BufferedImage foto = lerImagem(caminhoFoto);
        if (foto != null) {
            // recorte central quadrado ("object-fit: cover")
            int lado = Math.min(foto.getWidth(), foto.getHeight());
            int x = (foto.getWidth() - lado) / 2, y = (foto.getHeight() - lado) / 2;
            g.setClip(circulo);
            g.drawImage(foto, 0, 0, diametro, diametro, x, y, x + lado, y + lado, null);
            g.setClip(null);
        } else {
            g.setColor(ACENTO);
            g.fill(circulo);
            g.setColor(Color.WHITE);
            g.setFont(fonte(Font.BOLD, Math.max(10, diametro * 2 / 5)));
            String ini = iniciais(nome);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(ini, (diametro - fm.stringWidth(ini)) / 2,
                    (diametro - fm.getHeight()) / 2 + fm.getAscent());
        }
        g.setColor(LINHA);
        g.setStroke(new BasicStroke(traco));
        g.draw(circulo);
        g.dispose();
        return new ImageIcon(out);
    }

    private static BufferedImage lerImagem(String caminho) {
        if (caminho == null || caminho.isBlank()) return null;
        try {
            java.io.File f = new java.io.File(caminho);
            return f.isFile() ? javax.imageio.ImageIO.read(f) : null;
        } catch (Exception e) {
            return null;
        }
    }

    static String iniciais(String nome) {
        if (nome == null || nome.isBlank()) return "?";
        String[] p = nome.trim().split("\\s+");
        String s = p[0].substring(0, 1);
        if (p.length > 1) s += p[p.length - 1].substring(0, 1);
        return s.toUpperCase();
    }

    static String primeiroNome(String nome) {
        if (nome == null || nome.isBlank()) return "";
        return nome.trim().split("\\s+")[0];
    }

    /** "Bom dia" / "Boa tarde" / "Boa noite" conforme o horário local. */
    static String saudacaoDoHorario() {
        int h = java.time.LocalTime.now().getHour();
        if (h >= 5 && h < 12) return "Bom dia";
        if (h >= 12 && h < 18) return "Boa tarde";
        return "Boa noite";
    }

    // ------------------------------------------------------------------ componentes

    /** Painel com fundo arredondado pintado manualmente (o resto fica transparente). */
    static class PainelArredondado extends JPanel {
        private final int raio;
        private Color cor;

        PainelArredondado(LayoutManager layout, int raio, Color cor) {
            super(layout);
            this.raio = raio;
            this.cor = cor;
            setOpaque(false);
        }

        void setCor(Color cor) { this.cor = cor; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), raio * 2, raio * 2));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Tile do protótipo: quadrado cinza arredondado, ícone grande ao centro, rótulo abaixo
     * e um ícone pequeno opcional no canto superior direito (ex.: "+" ou lupa).
     */
    static JComponent tile(String rotulo, String icone, Icon iconeCanto, Runnable acao) {
        PainelArredondado p = new PainelArredondado(new BorderLayout(), 12, TILE);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 14, 12));
        p.setPreferredSize(new Dimension(170, 150));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topo.setOpaque(false);
        JLabel canto = new JLabel();
        if (iconeCanto != null) canto.setIcon(iconeCanto);
        canto.setPreferredSize(new Dimension(16, 16));
        topo.add(canto);

        JLabel ic = new JLabel(recurso(icone, 56, 56), SwingConstants.CENTER);

        JLabel txt = new JLabel("<html><div style='text-align:center'>" + rotulo + "</div></html>",
                SwingConstants.CENTER);
        txt.setFont(fonte(Font.PLAIN, 14));
        txt.setForeground(TEXTO);

        p.add(topo, BorderLayout.NORTH);
        p.add(ic, BorderLayout.CENTER);
        p.add(txt, BorderLayout.SOUTH);

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { p.setCor(TILE_HOVER); }
            @Override public void mouseExited(MouseEvent e) {
                // ignora a saída para um componente filho
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), p);
                if (!p.contains(pt)) p.setCor(TILE);
            }
            @Override public void mouseClicked(MouseEvent e) { acao.run(); }
        };
        p.addMouseListener(ma);
        for (Component c : new Component[]{topo, canto, ic, txt}) c.addMouseListener(ma);
        return p;
    }

    /** Título de seção centralizado, como "Seus veículos" no protótipo. */
    static JLabel tituloSecao(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(fonte(Font.PLAIN, 22));
        l.setForeground(TEXTO);
        return l;
    }

    /** Linha divisória fina preta usada entre as seções do protótipo. */
    static JComponent divisor() {
        JSeparator s = new JSeparator();
        s.setForeground(LINHA);
        s.setBackground(LINHA);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    /** "+" fino do canto do tile "Adicionar novo Veículo". */
    static Icon iconeMais(int tam) {
        return new Icon() {
            @Override public int getIconWidth() { return tam; }
            @Override public int getIconHeight() { return tam; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXTO);
                g2.setStroke(new BasicStroke(1.6f));
                int m = tam / 2;
                g2.drawLine(x + 1, y + m, x + tam - 1, y + m);
                g2.drawLine(x + m, y + 1, x + m, y + tam - 1);
                g2.dispose();
            }
        };
    }

    /** Lupa pequena do canto do tile "Consultar Veículos". */
    static Icon iconeLupa(int tam) {
        ImageIcon ic = recurso("/assets/icons/pesquisar.png", tam, tam);
        return ic != null ? ic : iconeMais(tam);
    }

    /** Ícone de lápis desenhado em vetor (o protótipo usa um lápis preto ao lado do nome). */
    static Icon iconeLapis(int tam) {
        return new Icon() {
            @Override public int getIconWidth() { return tam; }
            @Override public int getIconHeight() { return tam; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(TEXTO);
                double s = tam / 24.0;
                g2.scale(s, s);
                // corpo do lápis inclinado a 45°
                Polygon corpo = new Polygon(new int[]{3, 15, 19, 7}, new int[]{17, 5, 9, 21}, 4);
                g2.fill(corpo);
                g2.fill(new Polygon(new int[]{3, 7, 2}, new int[]{17, 21, 22}, 3)); // ponta
                g2.fill(new Polygon(new int[]{16, 18, 22, 20}, new int[]{4, 2, 6, 8}, 4)); // borracha
                g2.dispose();
            }
        };
    }

    /** Scroll vertical sem borda, fundo branco e rolagem suave. */
    static JScrollPane rolagem(JComponent conteudo) {
        JScrollPane sc = new JScrollPane(conteudo,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sc.setBorder(null);
        sc.getViewport().setBackground(FUNDO);
        sc.getVerticalScrollBar().setUnitIncrement(18);
        ScrollBarPadrao.aplicar(sc);
        return sc;
    }

    // ------------------------------------------------------------------ telas internas (Figma)

    static final Color CORPO_CARTAO = Color.decode("#BDBDBD"); // área expandida dos cartões
    static final Color CAIXA_CLARA  = Color.decode("#DCDCDC"); // caixas dentro da área expandida
    static final Color VERDE        = Color.decode("#22C55E");
    static final Color AMARELO      = Color.decode("#F2E600");
    static final Color VERMELHO     = Color.decode("#E11D1D");

    /** Largura máxima do conteúdo: em telas largas o miolo fica centralizado, como numa página web. */
    static final int LARGURA_CONTEUDO = 1040;

    /**
     * Cabeçalho das telas internas: título centralizado (como no protótipo) e, à esquerda,
     * o link "← Voltar" — no desktop não há o botão "voltar" do celular.
     */
    static JComponent cabecalhoPagina(String titulo, Runnable voltar) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel t = new JLabel(titulo, SwingConstants.CENTER);
        t.setFont(fonte(Font.PLAIN, 28));
        t.setForeground(TEXTO);
        p.add(t, BorderLayout.CENTER);

        JLabel v = new JLabel(voltar != null ? "← Voltar" : "");
        v.setFont(fonte(Font.PLAIN, 14));
        v.setForeground(TEXTO_SUAVE);
        v.setPreferredSize(new Dimension(90, 20));
        if (voltar != null) {
            v.setCursor(new Cursor(Cursor.HAND_CURSOR));
            v.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { voltar.run(); }
                @Override public void mouseEntered(MouseEvent e) { v.setForeground(ACENTO); }
                @Override public void mouseExited(MouseEvent e) { v.setForeground(TEXTO_SUAVE); }
            });
        }
        JLabel espelho = new JLabel();
        espelho.setPreferredSize(new Dimension(90, 20));
        p.add(v, BorderLayout.WEST);
        p.add(espelho, BorderLayout.EAST);
        return p;
    }

    /**
     * Página padrão das telas internas: cabeçalho + conteúdo centralizado com largura máxima,
     * tudo dentro de uma rolagem vertical.
     */
    static JPanel pagina(String titulo, Runnable voltar, JComponent conteudo) {
        JPanel coluna = new JPanel(new BorderLayout(0, 22));
        coluna.setOpaque(false);
        coluna.add(cabecalhoPagina(titulo, voltar), BorderLayout.NORTH);
        coluna.add(conteudo, BorderLayout.CENTER);

        JPanel centro = new JPanel(new CentroComLarguraMaxima(LARGURA_CONTEUDO));
        centro.setBackground(FUNDO);
        centro.setBorder(BorderFactory.createEmptyBorder(24, 32, 32, 32));
        centro.add(coluna);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FUNDO);
        raiz.add(rolagem(new LarguraDaViewport(centro)), BorderLayout.CENTER);
        return raiz;
    }

    /** Layout de um filho só: ocupa a largura disponível até {@code max} e fica centralizado. */
    private static final class CentroComLarguraMaxima implements LayoutManager {
        private final int max;
        CentroComLarguraMaxima(int max) { this.max = max; }
        @Override public void addLayoutComponent(String n, Component c) {}
        @Override public void removeLayoutComponent(Component c) {}
        @Override public Dimension minimumLayoutSize(Container p) { return preferredLayoutSize(p); }
        @Override public Dimension preferredLayoutSize(Container p) {
            Insets in = p.getInsets();
            if (p.getComponentCount() == 0) return new Dimension(in.left + in.right, in.top + in.bottom);
            Dimension d = p.getComponent(0).getPreferredSize();
            return new Dimension(Math.min(d.width, max) + in.left + in.right, d.height + in.top + in.bottom);
        }
        @Override public void layoutContainer(Container p) {
            if (p.getComponentCount() == 0) return;
            Insets in = p.getInsets();
            int disp = p.getWidth() - in.left - in.right;
            int w = Math.min(disp, max);
            Component c = p.getComponent(0);
            // Altura preferida (não estica até o fim da tela): o conteúdo fica no topo.
            c.setBounds(in.left + (disp - w) / 2, in.top, w, c.getPreferredSize().height);
        }
    }

    /** Painel que acompanha a largura da viewport (só rola na vertical). */
    private static final class LarguraDaViewport extends JPanel implements Scrollable {
        LarguraDaViewport(JComponent c) {
            super(new BorderLayout());
            setBackground(FUNDO);
            add(c);
        }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return r.height - 40; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() {
            return getParent() instanceof JViewport vp && vp.getHeight() > getPreferredSize().height;
        }
    }

    /** Rótulo centralizado acima de um campo ("Tipo do veículo", "Marca"...). */
    static JLabel rotuloCampo(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(fonte(Font.PLAIN, 19));
        l.setForeground(TEXTO);
        return l;
    }

    /** Borda arredondada "invisível": só dá cantos ao fundo pintado pelo componente. */
    static final class BordaPilula extends javax.swing.border.AbstractBorder {
        private final int raio;
        BordaPilula(int raio) { this.raio = raio; }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4, 12, 4, 12); }
        @Override public Insets getBorderInsets(Component c, Insets i) {
            i.set(4, 12, 4, 12); return i;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            // Cobre os cantos com a cor do pai, simulando arredondamento num componente opaco.
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fundoPai = c.getParent() != null ? c.getParent().getBackground() : FUNDO;
            java.awt.geom.Area cantos = new java.awt.geom.Area(new Rectangle(x, y, w, h));
            cantos.subtract(new java.awt.geom.Area(new RoundRectangle2D.Double(x, y, w - 1, h - 1, raio * 2, raio * 2)));
            g2.setColor(fundoPai);
            g2.fill(cantos);
            g2.dispose();
        }
    }

    /** Campo de texto cinza arredondado do protótipo. */
    static JTextField campoTexto() {
        JTextField t = new JTextField();
        t.setFont(fonte(Font.PLAIN, 15));
        t.setBackground(TILE);
        t.setForeground(TEXTO);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        t.setBorder(new BordaPilula(10));
        t.setPreferredSize(new Dimension(200, 36));
        return t;
    }

    /** JComboBox cinza arredondado, texto centralizado e seta "⌄" à direita, como no protótipo. */
    static <T> JComboBox<T> combo(java.util.function.Function<T, String> texto) {
        JComboBox<T> c = new JComboBox<>();
        c.setFont(fonte(Font.PLAIN, 15));
        c.setBackground(TILE);
        c.setForeground(TEXTO);
        c.setFocusable(false);
        c.setPreferredSize(new Dimension(200, 36));
        c.setBorder(new BordaPilula(10));
        c.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton b = new JButton("⌄");
                b.setFont(fonte(Font.BOLD, 16));
                b.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 4));
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setForeground(TEXTO);
                return b;
            }
            @Override public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean f) {
                g.setColor(TILE);
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        });
        c.setRenderer(new DefaultListCellRenderer() {
            @SuppressWarnings("unchecked")
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                setText(v == null ? "" : texto.apply((T) v));
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(fonte(Font.PLAIN, 15));
                if (i < 0) { setOpaque(false); setBackground(TILE); }
                else if (!s) setBackground(Color.WHITE);
                return this;
            }
        });
        return c;
    }

    /** Valor somente leitura em "pílula" cinza (tela Informações do Veículo). */
    static JComponent pilula(String valor) {
        PainelArredondado p = new PainelArredondado(new BorderLayout(), 10, TILE);
        p.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        JLabel l = new JLabel(valor == null || valor.isBlank() ? "—" : valor, SwingConstants.CENTER);
        l.setFont(fonte(Font.PLAIN, 16));
        l.setForeground(TEXTO);
        p.add(l);
        return p;
    }

    /** Botão arredondado de cor sólida (Aprovar/Reprovar, Solicitar Revisão, Exportar...). */
    static JButton botao(String texto, Color fundo, Color textoCor, int tamFonte) {
        JButton b = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? fundo.darker()
                        : getModel().isRollover() ? misturar(fundo, Color.WHITE, 0.15) : fundo;
                g2.setColor(c);
                int raio = Math.min(getHeight(), 24);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), raio, raio));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(fonte(Font.PLAIN, tamFonte));
        b.setForeground(textoCor);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        return b;
    }

    static Color misturar(Color a, Color b, double t) {
        return new Color((int) (a.getRed() * (1 - t) + b.getRed() * t),
                (int) (a.getGreen() * (1 - t) + b.getGreen() * t),
                (int) (a.getBlue() * (1 - t) + b.getBlue() * t));
    }

    /** Bolinha de status dos cartões (cheia, ou só contorno para "finalizado"). */
    static Icon bolinha(Color cor, boolean vazada) {
        return new Icon() {
            @Override public int getIconWidth() { return 14; }
            @Override public int getIconHeight() { return 14; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (vazada) {
                    g2.setColor(TEXTO);
                    g2.setStroke(new BasicStroke(1.3f));
                    g2.draw(new Ellipse2D.Double(x + 1, y + 1, 11.5, 11.5));
                } else {
                    g2.setColor(cor);
                    g2.fill(new Ellipse2D.Double(x + 1, y + 1, 12, 12));
                }
                g2.dispose();
            }
        };
    }

    /** Chevron (⌄ / ⌃) desenhado em vetor. */
    static Icon chevron(boolean paraCima) {
        return new Icon() {
            @Override public int getIconWidth() { return 18; }
            @Override public int getIconHeight() { return 18; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXTO);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (paraCima) g2.drawPolyline(new int[]{x + 3, x + 9, x + 15}, new int[]{y + 12, y + 6, y + 12}, 3);
                else g2.drawPolyline(new int[]{x + 3, x + 9, x + 15}, new int[]{y + 6, y + 12, y + 6}, 3);
                g2.dispose();
            }
        };
    }

    /**
     * Cartão sanfona das telas Orçamentos e Serviços: cabeçalho cinza (bolinha de status,
     * título, veículo, "Por: oficina" e chevron) e, ao clicar, a área expandida mais escura.
     */
    static class CartaoExpansivel extends JPanel {
        private final JComponent corpo;
        private final JLabel seta;
        private boolean aberto;

        CartaoExpansivel(Icon status, String titulo, String subtitulo, String oficina,
                         JComponent corpo, boolean abertoInicial) {
            super(new BorderLayout());
            this.corpo = corpo;
            setOpaque(false);

            JPanel cab = new JPanel(new BorderLayout(14, 0));
            cab.setOpaque(false);
            cab.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
            cab.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel dot = new JLabel(status);
            JPanel textos = new JPanel();
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            textos.setOpaque(false);
            JLabel t = new JLabel(titulo);
            t.setFont(fonte(Font.PLAIN, 17));
            t.setForeground(TEXTO);
            JLabel st = new JLabel(subtitulo);
            st.setFont(fonte(Font.PLAIN, 13));
            st.setForeground(TEXTO);
            textos.add(t);
            textos.add(st);

            JPanel esquerda = new JPanel(new BorderLayout(12, 0));
            esquerda.setOpaque(false);
            esquerda.add(dot, BorderLayout.WEST);
            esquerda.add(textos, BorderLayout.CENTER);

            JLabel por = new JLabel("<html><div style='text-align:center'><span style='font-size:13px'>Por:</span><br>"
                    + escapar(oficina) + "</div></html>", SwingConstants.CENTER);
            por.setFont(fonte(Font.PLAIN, 12));
            por.setForeground(TEXTO);
            seta = new JLabel();

            JPanel direita = new JPanel(new BorderLayout(18, 0));
            direita.setOpaque(false);
            direita.add(por, BorderLayout.CENTER);
            direita.add(seta, BorderLayout.EAST);

            cab.add(esquerda, BorderLayout.CENTER);
            cab.add(direita, BorderLayout.EAST);

            MouseAdapter alternar = new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { setAberto(!aberto); }
            };
            for (Component c : new Component[]{cab, dot, textos, t, st, por, seta, esquerda, direita})
                c.addMouseListener(alternar);

            corpo.setOpaque(false);
            add(cab, BorderLayout.NORTH);
            add(corpo, BorderLayout.CENTER);
            setAberto(abertoInicial);
        }

        void setAberto(boolean a) {
            aberto = a;
            corpo.setVisible(a);
            seta.setIcon(chevron(a));
            revalidate();
            repaint();
        }

        @Override public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D forma = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(TILE);
            g2.fill(forma);
            if (aberto) {
                int y = getComponent(0).getHeight();
                g2.clip(forma);
                g2.setColor(CORPO_CARTAO);
                g2.fillRect(0, y, getWidth(), getHeight() - y);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** "Ford Fiesta 2004" — marca, modelo e ano do veículo. */
    static String nomeVeiculo(model.Veiculo v) {
        if (v == null) return "—";
        StringBuilder sb = new StringBuilder();
        if (v.getModelo() != null) {
            if (v.getModelo().getMontadora() != null) sb.append(v.getModelo().getMontadora().getNome()).append(' ');
            sb.append(v.getModelo().getNome());
            if (v.getModelo().getAno() > 0) sb.append(' ').append(v.getModelo().getAno());
        }
        String s = sb.toString().trim();
        return s.isEmpty() ? valorOuPlaca(v) : s;
    }

    private static String valorOuPlaca(model.Veiculo v) { return v.getPlaca() == null ? "—" : v.getPlaca(); }

    /** Cartão de veículo do protótipo ("Seus veículos"): moldura branca com a foto e o nome abaixo. */
    static JComponent cartaoVeiculo(model.Veiculo v, Runnable aoClicar) {
        PainelArredondado card = new PainelArredondado(new BorderLayout(0, 10), 12, TILE);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setToolTipText("Ver informações do veículo");

        PainelArredondado moldura = new PainelArredondado(new BorderLayout(), 10, Color.WHITE);
        moldura.setPreferredSize(new Dimension(190, 130));
        JLabel foto = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = ImagemUtil.carregar(v.getFoto(), 180, 120);
        foto.setIcon(icon != null ? icon : recurso("/assets/icons/vveiculos.png", 90, 90));
        moldura.add(foto, BorderLayout.CENTER);

        JLabel nome = new JLabel("<html><div style='text-align:center'>" + escapar(nomeVeiculo(v))
                + "<br><span style='color:#6B6B6B;font-size:11px'>" + escapar(valorOuPlaca(v))
                + "</span></div></html>", SwingConstants.CENTER);
        nome.setFont(fonte(Font.PLAIN, 14));
        nome.setForeground(TEXTO);

        card.add(moldura, BorderLayout.CENTER);
        card.add(nome, BorderLayout.SOUTH);
        hoverClique(card, aoClicar, moldura, foto, nome);
        return card;
    }

    /** Hover (escurece o cinza) + clique num PainelArredondado e em seus filhos. */
    static void hoverClique(PainelArredondado card, Runnable aoClicar, Component... filhos) {
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { aoClicar.run(); }
            @Override public void mouseEntered(MouseEvent e) { card.setCor(TILE_HOVER); }
            @Override public void mouseExited(MouseEvent e) {
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), card);
                if (!card.contains(pt)) card.setCor(TILE);
            }
        };
        card.addMouseListener(ma);
        for (Component c : filhos) c.addMouseListener(ma);
    }

    static String escapar(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static String dataBR(String iso) {
        if (iso == null || iso.isBlank()) return "—";
        try {
            return java.time.LocalDate.parse(iso.trim()).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return iso;
        }
    }

    static String reais(double v) {
        return String.format(new java.util.Locale("pt", "BR"), "R$ %,.2f", v);
    }

    /** Navega dentro da janela do Portal do Cliente. */
    static void navegar(Component origem, JPanel pagina) {
        Window w = SwingUtilities.getWindowAncestor(origem);
        if (w instanceof V_ClienteMain) ((V_ClienteMain) w).atualizarConteudo(pagina);
    }
}
