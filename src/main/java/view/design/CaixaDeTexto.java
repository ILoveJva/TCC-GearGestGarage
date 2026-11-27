package view.design;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Componentes de formulário no padrão vidro, todos em uma classe:
 *
 *   new CaixaDeTexto.Campo(20)                              // campo de texto
 *   new CaixaDeTexto.Combo<>(new String[]{"A", "B"})        // lista suspensa
 *   new CaixaDeTexto.Selecao("Ativo", true)                 // checkbox
 *   new CaixaDeTexto.Cartao(new BorderLayout())             // painel arredondado
 */
public final class CaixaDeTexto {

    private CaixaDeTexto() {}

    private static final int RAIO = 12;
    private static final Color COR_TEXTO = Color.decode("#2B2E33");
    private static final Color COR_LABEL = Color.decode("#57626F");
    private static final Color COR_POPUP_SELECAO = Color.decode("#FFE4BF");
    private static final Color COR_POPUP_BORDA = Color.decode("#C3CDDA");

    // ---- pintura compartilhada por Campo e Combo ----
    private static void pintarFundo(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(70, 90, 110, 28));
        g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO, RAIO));
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 210), 0, h, new Color(255, 255, 255, 145)));
        g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO, RAIO));
        g2.setColor(new Color(255, 255, 255, 110));
        g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO - 5, RAIO - 5));
        g2.dispose();
    }

    private static void pintarBorda(Graphics g, int w, int h, boolean focado) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(focado ? 1.6f : 1f));
        g2.setColor(focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130));
        g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO, RAIO));
        g2.dispose();
    }

    private static void acompanharFoco(JComponent c, Runnable aoMudar) {
        c.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { aoMudar.run(); }
            @Override public void focusLost(FocusEvent e)   { aoMudar.run(); }
        });
    }

    // =====================================================================
    /** Campo de texto. */
    public static class Campo extends JTextField {
        public Campo() { this(0); }

        public Campo(int colunas) {
            super(colunas);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setForeground(COR_TEXTO);
            setCaretColor(COR_TEXTO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            acompanharFoco(this, this::repaint);
        }

        @Override protected void paintComponent(Graphics g) {
            pintarFundo(g, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override protected void paintBorder(Graphics g) {
            pintarBorda(g, getWidth(), getHeight(), hasFocus());
        }
    }

    // =====================================================================
    /** Lista suspensa. */
    public static class Combo<T> extends JComboBox<T> {
        public Combo() { super(); estilizar(); }
        public Combo(T[] itens) { super(itens); estilizar(); }

        private void estilizar() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setForeground(COR_TEXTO);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 4));
            setFocusable(true);
            setUI(new ComboUI());
            acompanharFoco(this, this::repaint);
        }

        @Override protected void paintComponent(Graphics g) {
            pintarFundo(g, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override protected void paintBorder(Graphics g) {
            pintarBorda(g, getWidth(), getHeight(), hasFocus());
        }
    }

    private static class ComboUI extends BasicComboBoxUI {
        @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {}

        @Override protected ComboPopup createPopup() { return new Popup(comboBox); }

        @Override protected JButton createArrowButton() {
            JButton seta = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    Path2D t = new Path2D.Double();
                    t.moveTo(cx - 4, cy - 2);
                    t.lineTo(cx + 4, cy - 2);
                    t.lineTo(cx, cy + 3);
                    t.closePath();
                    g2.setColor(COR_LABEL);
                    g2.fill(t);
                    g2.dispose();
                }
            };
            seta.setPreferredSize(new Dimension(20, 20));
            seta.setContentAreaFilled(false);
            seta.setBorderPainted(false);
            seta.setFocusPainted(false);
            seta.setOpaque(false);
            seta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return seta;
        }
    }

    private static class Popup extends BasicComboPopup {
        Popup(JComboBox<Object> combo) { super(combo); }

        @Override protected void configurePopup() {
            super.configurePopup();
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }

        @Override protected void configureList() {
            super.configureList();
            list.setOpaque(true);
            list.setBackground(Color.WHITE);
            list.setForeground(COR_TEXTO);
            list.setSelectionBackground(COR_POPUP_SELECAO);
            list.setSelectionForeground(COR_TEXTO);
            list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        @Override protected JScrollPane createScroller() {
            JScrollPane s = super.createScroller();
            s.setOpaque(false);
            s.getViewport().setOpaque(false);
            s.setBorder(BorderFactory.createEmptyBorder());
            ScrollBar.aplicar(s);
            return s;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO);
            g2.setColor(Color.WHITE);
            g2.fill(forma);
            g2.setColor(COR_POPUP_BORDA);
            g2.draw(forma);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =====================================================================
    /** Caixa de seleção (checkbox). */
    public static class Selecao extends JCheckBox {
        public Selecao(String texto) { this(texto, false); }

        public Selecao(String texto, boolean selecionado) {
            super(texto, selecionado);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(COR_LABEL);
            setOpaque(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    // =====================================================================
    /** Painel com fundo arredondado (raio 16, branco por padrão). */
    public static class Cartao extends JPanel {
        private final int raio;
        private final Color corFundo;

        public Cartao(LayoutManager layout) { this(layout, 16, Color.WHITE); }

        public Cartao(LayoutManager layout, int raio, Color corFundo) {
            super(layout);
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(corFundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}