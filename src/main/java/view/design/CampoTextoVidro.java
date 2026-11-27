package view.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class CampoTextoVidro extends JTextField {
    private boolean focado = false;
    private static final int RAIO_COMPONENTE = 12;
    private static final int TAMANHO_FONTE_CAMPO = 12;
    private static final Color COR_TEXTO_CAMPO = Color.decode("#2B2E33");

    public CampoTextoVidro() {
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
        setForeground(COR_TEXTO_CAMPO);
        setCaretColor(COR_TEXTO_CAMPO);
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
        int w = getWidth(), h = getHeight();

        g2.setColor(new Color(70, 90, 110, 28));
        g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 210), 0, h, new Color(255, 255, 255, 145)));
        g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

        g2.setColor(new Color(255, 255, 255, 110));
        g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setStroke(new BasicStroke(focado ? 1.6f : 1f));
        g2.setColor(focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130));
        g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
        g2.dispose();
    }
}
