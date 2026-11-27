package view.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Componente genérico para Caixas de Seleção (JCheckBox) padronizado
 * com a identidade visual do sistema (Segoe UI, cores consistentes, ícone customizado e destaque laranja).
 */
public class SelectBox extends JCheckBox {

    private static final Color COR_LABEL = Color.decode("#57626F");
    private static final Color COR_LARANJA = Color.decode("#FF9900");
    private static final Color COR_HOVER = Color.decode("#2B2E33");
    private static final Color COR_BORDA = Color.decode("#C3CDDA");
    private static final int TAMANHO_FONTE = 13;

    private boolean sobreMouse = false;

    public SelectBox(String texto) {
        super(texto);
        estilizar();
    }

    public SelectBox(String texto, boolean selecionado) {
        super(texto, selecionado);
        estilizar();
    }

    private void estilizar() {
        setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE));
        setForeground(COR_LABEL);
        setOpaque(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Aplica ícones customizados alinhados com o padrão visual do sistema
        setIcon(new CustomCheckBoxIcon(false));
        setSelectedIcon(new CustomCheckBoxIcon(true));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sobreMouse = true;
                setForeground(COR_HOVER);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sobreMouse = false;
                setForeground(COR_LABEL);
                repaint();
            }
        });
    }

    /**
     * Ícone customizado para renderizar a caixinha de seleção com cantos arredondados
     * e o tom laranja (#FF9900) quando marcada.
     */
    private static class CustomCheckBoxIcon implements Icon {
        private final boolean selecionado;

        CustomCheckBoxIcon(boolean selecionado) {
            this.selecionado = selecionado;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int tamanho = 16;
            RoundRectangle2D rect = new RoundRectangle2D.Double(x, y + (getIconHeight() - tamanho) / 2.0, tamanho, tamanho, 4, 4);

            if (selecionado) {
                g2.setColor(COR_LARANJA);
                g2.fill(rect);

                // Desenha o marcador de seleção (check) em branco
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 4, y + 8, x + 7, y + 11);
                g2.drawLine(x + 7, y + 11, x + 12, y + 5);
            } else {
                g2.setColor(Color.WHITE);
                g2.fill(rect);
                g2.setColor(COR_BORDA);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}