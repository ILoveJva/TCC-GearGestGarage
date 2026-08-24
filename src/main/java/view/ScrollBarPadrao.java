package view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Barra de rolagem fina e animada, no padrão visual do sistema: trilho
 * praticamente invisível e "thumb" arredondado na cor de destaque laranja
 * (#FF9900), que cresce e ganha opacidade ao passar o mouse.
 */
public class ScrollBarPadrao extends BasicScrollBarUI {

    private static final Color COR_THUMB = Color.decode("#FF9900");
    private static final int LARGURA_MIN = 5;
    private static final int LARGURA_MAX = 9;

    private float larguraAtual = LARGURA_MIN;
    private float alphaAtual = 100f;
    private boolean crescendo = false;
    private Timer timerAnimacao;

    /** Aplica o estilo padrão às barras de rolagem (vertical e horizontal) de um JScrollPane. */
    public static void aplicar(JScrollPane scroll) {
        aplicar(scroll.getVerticalScrollBar());
        aplicar(scroll.getHorizontalScrollBar());
    }

    /** Aplica o estilo padrão a uma barra de rolagem individual. */
    public static void aplicar(JScrollBar barra) {
        if (barra == null) return;
        barra.setUI(new ScrollBarPadrao());
        barra.setUnitIncrement(14);
        barra.setOpaque(false);
        barra.setPreferredSize(new Dimension(10, 10));
    }

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = COR_THUMB;
        this.trackColor = new Color(0, 0, 0, 0);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) { return botaoInvisivel(); }

    @Override
    protected JButton createIncreaseButton(int orientation) { return botaoInvisivel(); }

    private JButton botaoInvisivel() {
        JButton botao = new JButton();
        botao.setPreferredSize(new Dimension(0, 0));
        botao.setMinimumSize(new Dimension(0, 0));
        botao.setMaximumSize(new Dimension(0, 0));
        return botao;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        scrollbar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { animarPara(true); }
            @Override public void mouseExited(MouseEvent e)  { animarPara(false); }
        });
    }

    private void animarPara(boolean crescer) {
        this.crescendo = crescer;
        if (timerAnimacao != null && timerAnimacao.isRunning()) {
            timerAnimacao.stop();
        }
        timerAnimacao = new Timer(12, e -> {
            float alvoLargura = crescendo ? LARGURA_MAX : LARGURA_MIN;
            float alvoAlpha   = crescendo ? 230f : 100f;
            larguraAtual += (alvoLargura - larguraAtual) * 0.28f;
            alphaAtual    += (alvoAlpha - alphaAtual) * 0.28f;
            if (scrollbar != null) scrollbar.repaint();
            if (Math.abs(larguraAtual - alvoLargura) < 0.3f && Math.abs(alphaAtual - alvoAlpha) < 1f) {
                larguraAtual = alvoLargura;
                alphaAtual = alvoAlpha;
                ((Timer) e.getSource()).stop();
            }
        });
        timerAnimacao.start();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Trilho invisível: só o "polegar" aparece, estilo flutuante e discreto.
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean horizontal = scrollbar.getOrientation() == JScrollBar.HORIZONTAL;
        int espessura = Math.round(larguraAtual);
        Color cor = new Color(COR_THUMB.getRed(), COR_THUMB.getGreen(), COR_THUMB.getBlue(), Math.round(alphaAtual));
        g2.setColor(cor);
        if (horizontal) {
            int y = thumbBounds.y + (thumbBounds.height - espessura) / 2;
            g2.fillRoundRect(thumbBounds.x + 2, y, thumbBounds.width - 4, espessura, espessura, espessura);
        } else {
            int x = thumbBounds.x + (thumbBounds.width - espessura) / 2;
            g2.fillRoundRect(x, thumbBounds.y + 2, espessura, thumbBounds.height - 4, espessura, espessura);
        }
        g2.dispose();
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(LARGURA_MAX, 30);
    }
}
