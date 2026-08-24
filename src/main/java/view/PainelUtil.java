package view;

import javax.swing.*;
import java.awt.*;

/** Utilitário compartilhado pelos diálogos padronizados (DialogoConfirmacao, DialogoAlerta). */
final class PainelUtil {

    private PainelUtil() {}

    /** Força fundo branco em toda a árvore de um diálogo, mesmo em painéis internos do Swing. */
    static void aplicarFundoBranco(Component c) {
        if (c instanceof JPanel || c instanceof JOptionPane) {
            c.setBackground(Color.WHITE);
        }
        if (c instanceof Container) {
            for (Component filho : ((Container) c).getComponents()) {
                aplicarFundoBranco(filho);
            }
        }
    }
}
