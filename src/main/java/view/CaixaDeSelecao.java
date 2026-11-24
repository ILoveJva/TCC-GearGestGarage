package view;

import javax.swing.*;
import java.awt.*;

/**
 * Componente genérico para Caixas de Seleção (JCheckBox) padronizado
 * com a identidade visual do sistema (Segoe UI, cores consistentes e cursor interativo).
 */
public class CaixaDeSelecao extends JCheckBox {

    private static final Color COR_LABEL = Color.decode("#57626F");
    private static final int TAMANHO_FONTE = 13;

    public CaixaDeSelecao(String texto) {
        super(texto);
        estilizar();
    }

    public CaixaDeSelecao(String texto, boolean selecionado) {
        super(texto, selecionado);
        estilizar();
    }

    private void estilizar() {
        setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE));
        setForeground(COR_LABEL);
        setOpaque(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}