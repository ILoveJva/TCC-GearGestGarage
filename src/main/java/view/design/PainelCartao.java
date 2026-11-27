package view.design;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PainelCartao extends JPanel {
    private final int raio;
    private final Color corFundo;

    public PainelCartao(LayoutManager layout, int raio, Color corFundo) {
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