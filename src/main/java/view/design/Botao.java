package view.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Biblioteca única de botões do sistema. Existem apenas 5 tipos:
 *
 *  FAMÍLIA CARD (ícone em cima, texto embaixo, fundo chapado + hover, borda padrão do L&F)
 *    T1  Botoes.CardNavegacao   -> "Serviços", "Orçamentos"... (Página Inicial)
 *    T2  Botoes.CardEstatistica -> "Montadoras 12", "Modelos 40"... (Página Inicial)
 *
 *  FAMÍLIA VIDRO (cantos arredondados, sombra, reflexo no topo, cor da Paleta)
 *    T3  Botoes.LinkIcone       -> "+ Cadastrar Cliente" (ícone + texto, compacto, abre página)
 *    T4  Botoes.LinkTexto       -> "+ Criar OS", "Editar", "Excluir" (só texto, compacto)
 *    T5  Botoes.AcaoPrincipal   -> "CADASTRAR CLIENTE" (ícone + texto, grande, ação do formulário)
 *
 * Uso:
 *   JButton b = new Botoes.CardNavegacao("Serviços", icone);
 *   JButton c = new Botoes.CardEstatistica("Montadoras", 12, "#9B59B6", icone);
 *   JButton d = new Botoes.LinkIcone("+ Cadastrar Cliente", icone);
 *   JButton e = new Botoes.LinkTexto("+ Criar OS");
 *   JButton f = new Botoes.LinkTexto("Excluir", Botoes.Paleta.VERMELHO);
 *   JButton g = new Botoes.AcaoPrincipal("CADASTRAR CLIENTE", icone);
 *
 * TAMANHO: cada tipo tem um tamanho padrão, mas todos aceitam largura e altura no construtor
 * (ou setTamanho(largura, altura) depois de criado):
 *   new Botoes.CardNavegacao("Serviços", icone, 160, 120);
 *   new Botoes.CardEstatistica("Modelos", 40, "#E67E22", icone, 150, 110);
 *   new Botoes.LinkIcone("+ Cadastrar Cliente", icone, 210, 38);
 *   new Botoes.LinkTexto("Editar", Botoes.Paleta.CINZA, 90, 34);
 *   new Botoes.AcaoPrincipal("CADASTRAR CLIENTE", icone, 280, 50);
 *   botao.setTamanho(200, 40);
 */
public final class Botao {

    private Botao() {}

    // =========================================================================
    // MEDIDAS E CORES COMPARTILHADAS — mexa só aqui para alterar tudo de uma vez
    // =========================================================================
    public static final Dimension DIM_CARD = new Dimension(140, 110);
    public static final int RAIO_VIDRO = 12;

    private static final Color CARD_FUNDO       = new Color(255, 253, 248);
    private static final Color CARD_FUNDO_HOVER = new Color(255, 248, 235);

    /** Cores da família Vidro: base, hover (clara) e pressionado (escura). */
    public enum Paleta {
        LARANJA("#FF9900", "#FFAD33", "#E68A00"),   // ação principal / navegação
        CINZA("#6C757D", "#7C848C", "#5A6169"),     // ação secundária (Editar)
        VERMELHO("#DC3545", "#E4606D", "#BD2130");  // ação destrutiva (Excluir)

        private final Color base, clara, escura;

        Paleta(String base, String clara, String escura) {
            this.base = Color.decode(base);
            this.clara = Color.decode(clara);
            this.escura = Color.decode(escura);
        }
    }

    // =========================================================================
    // BASE COMUM: cursor, foco e rastreio de hover/clique
    // =========================================================================
    public abstract static class Base extends JButton {
        protected boolean sobreMouse = false;
        protected boolean pressionado = false;

        protected Base(String texto, Icon icone) {
            super(texto, icone);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { sobreMouse = true;  estadoMudou(); }
                @Override public void mouseExited(MouseEvent e)   { sobreMouse = false; estadoMudou(); }
                @Override public void mousePressed(MouseEvent e)  { pressionado = true;  estadoMudou(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; estadoMudou(); }
            });
        }

        /** Chamado quando hover/clique mudam. Cada família decide como reagir. */
        protected void estadoMudou() { repaint(); }

        /** Troca o tamanho do botão depois de criado (largura x altura, em pixels). */
        public void setTamanho(int largura, int altura) {
            setPreferredSize(new Dimension(largura, altura));
            revalidate();
        }
    }

    // =========================================================================
    // FAMÍLIA CARD — ícone acima do texto, fundo chapado
    // =========================================================================
    public abstract static class Card extends Base {
        protected Card(String texto, Icon icone, int espacoIconeTexto, int largura, int altura) {
            super(texto, icone);
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setPreferredSize(new Dimension(largura, altura));
            setFont(new Font("Arial", Font.BOLD, 14));
            setForeground(Color.DARK_GRAY);
            setBackground(CARD_FUNDO);
            setIconTextGap(espacoIconeTexto);
        }

        @Override
        protected void estadoMudou() {
            setBackground(sobreMouse ? CARD_FUNDO_HOVER : CARD_FUNDO);
        }
    }

    /** T1 — Link de navegação: ícone grande + rótulo (ex.: "Serviços"). */
    public static class CardNavegacao extends Card {
        public CardNavegacao(String texto, Icon icone) {
            this(texto, icone, DIM_CARD.width, DIM_CARD.height);
        }

        public CardNavegacao(String texto, Icon icone, int largura, int altura) {
            super(texto, icone, 10, largura, altura);
        }
    }

    /** T2 — Card de estatística: ícone menor + número em destaque + descrição (ex.: "Montadoras"). */
    public static class CardEstatistica extends Card {
        private final String rotulo;
        private final String corHex;

        public CardEstatistica(String rotulo, int valor, String corHex, Icon icone) {
            this(rotulo, valor, corHex, icone, DIM_CARD.width, DIM_CARD.height);
        }

        public CardEstatistica(String rotulo, int valor, String corHex, Icon icone, int largura, int altura) {
            super(montarHtml(rotulo, valor, corHex), icone, 6, largura, altura);
            this.rotulo = rotulo;
            this.corHex = corHex;
        }

        /** Atualiza o número sem recriar o botão. */
        public void setValor(int valor) {
            setText(montarHtml(rotulo, valor, corHex));
        }

        private static String montarHtml(String rotulo, int valor, String corHex) {
            return "<html><body style='text-align:center; margin:0; padding:0;'>"
                    + "<div style='font-size:20px; font-weight:bold; color:" + corHex + ";'>" + valor + "</div>"
                    + "<div style='font-size:10px; color:#666666; margin-top:1px;'>" + rotulo + "</div>"
                    + "</body></html>";
        }
    }

    // =========================================================================
    // FAMÍLIA VIDRO — cantos arredondados, sombra, reflexo e cor da Paleta
    // =========================================================================
    public abstract static class Vidro extends Base {
        private final Paleta paleta;
        private final int alfaReflexo;

        protected Vidro(String texto, Icon icone, Paleta paleta,
                        Dimension tamanho, int tamanhoFonte, int espacoIconeTexto,
                        Insets margem, int alfaReflexo) {
            super(texto, icone);
            this.paleta = paleta;
            this.alfaReflexo = alfaReflexo;
            setFont(new Font("Segoe UI", Font.BOLD, tamanhoFonte));
            setForeground(Color.WHITE);
            setIconTextGap(espacoIconeTexto);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(margem.top, margem.left, margem.bottom, margem.right));
            setPreferredSize(tamanho);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Sombra suave
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_VIDRO, RAIO_VIDRO));

            // Corpo (muda com hover / clique)
            Color cor = pressionado ? paleta.escura : (sobreMouse ? paleta.clara : paleta.base);
            g2.setColor(cor);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_VIDRO, RAIO_VIDRO));

            // Reflexo no topo
            g2.setColor(new Color(255, 255, 255, alfaReflexo));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4),
                    RAIO_VIDRO - 5, RAIO_VIDRO - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** T3 — Link compacto com ícone + texto que abre outra página (ex.: "+ Cadastrar Cliente"). */
    public static class LinkIcone extends Vidro {
        public static final Dimension TAMANHO_PADRAO = new Dimension(190, 34);

        public LinkIcone(String texto, Icon icone) {
            this(texto, icone, Paleta.LARANJA, TAMANHO_PADRAO.width, TAMANHO_PADRAO.height);
        }

        public LinkIcone(String texto, Icon icone, Paleta paleta) {
            this(texto, icone, paleta, TAMANHO_PADRAO.width, TAMANHO_PADRAO.height);
        }

        public LinkIcone(String texto, Icon icone, int largura, int altura) {
            this(texto, icone, Paleta.LARANJA, largura, altura);
        }

        public LinkIcone(String texto, Icon icone, Paleta paleta, int largura, int altura) {
            super(texto, icone, paleta, new Dimension(largura, altura), 12, 8, new Insets(6, 16, 6, 16), 45);
        }
    }

    /** T4 — Botão compacto só com texto (ex.: "+ Criar OS", "Editar", "Excluir"). */
    public static class LinkTexto extends Vidro {
        public static final Dimension TAMANHO_PADRAO = new Dimension(140, 34);

        public LinkTexto(String texto) {
            this(texto, Paleta.LARANJA, TAMANHO_PADRAO.width, TAMANHO_PADRAO.height);
        }

        public LinkTexto(String texto, Paleta paleta) {
            this(texto, paleta, TAMANHO_PADRAO.width, TAMANHO_PADRAO.height);
        }

        public LinkTexto(String texto, int largura, int altura) {
            this(texto, Paleta.LARANJA, largura, altura);
        }

        public LinkTexto(String texto, Paleta paleta, int largura, int altura) {
            super(texto, null, paleta, new Dimension(largura, altura), 12, 0, new Insets(6, 16, 6, 16), 30);
        }
    }

    /** T5 — Botão grande de ação do formulário, ícone + texto (ex.: "CADASTRAR CLIENTE"). */
    public static class AcaoPrincipal extends Vidro {
        public static final Dimension TAMANHO_PADRAO = new Dimension(250, 46);

        public AcaoPrincipal(String texto, Icon icone) {
            this(texto, icone, TAMANHO_PADRAO.width, TAMANHO_PADRAO.height);
        }

        public AcaoPrincipal(String texto, Icon icone, int largura, int altura) {
            super(texto, icone, Paleta.LARANJA, new Dimension(largura, altura), 16, 10, new Insets(8, 22, 8, 22), 25);
        }
    }
}