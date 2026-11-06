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

    /** Navega dentro da janela do Portal do Cliente. */
    static void navegar(Component origem, JPanel pagina) {
        Window w = SwingUtilities.getWindowAncestor(origem);
        if (w instanceof V_ClienteMain) ((V_ClienteMain) w).atualizarConteudo(pagina);
    }
}
