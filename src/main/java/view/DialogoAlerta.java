package view;

import javax.swing.*;
import java.awt.*;

/**
 * Alertas do sistema (sucesso, aviso, erro) com o mesmo padrão visual do
 * restante do sistema (fundo branco, fonte Segoe UI, botão de destaque
 * laranja #FF9900), usados no lugar do JOptionPane.showMessageDialog padrão
 * (cinza, com ícone nativo fora do padrão).
 */
public final class DialogoAlerta {

    private static final Color COR_DESTAQUE = Color.decode("#FF9900");
    private static final Color COR_TEXTO = Color.decode("#333333");
    private static final Font FONTE_MENSAGEM = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 13);

    private DialogoAlerta() {}

    /** Mensagem de sucesso (ex.: cadastro realizado, login efetuado). */
    public static void sucesso(Component pai, String mensagem, String titulo) {
        exibir(pai, mensagem, titulo);
    }

    /** Aviso (campo inválido, seleção obrigatória etc.). */
    public static void aviso(Component pai, String mensagem, String titulo) {
        exibir(pai, mensagem, titulo);
    }

    /** Erro (falha de sistema, senha incorreta etc.). */
    public static void erro(Component pai, String mensagem, String titulo) {
        exibir(pai, mensagem, titulo);
    }

    private static void exibir(Component pai, String mensagem, String titulo) {
        JButton btnOk = criarBotaoOk();
        Object[] opcoes = {btnOk};

        JOptionPane pane = new JOptionPane(mensagemLabel(mensagem), JOptionPane.PLAIN_MESSAGE,
            JOptionPane.DEFAULT_OPTION, null, opcoes, btnOk);
        PainelUtil.aplicarFundoBranco(pane);

        JDialog dialog = pane.createDialog(pai, titulo);
        btnOk.addActionListener(e -> pane.setValue(btnOk));
        dialog.setVisible(true);
        dialog.dispose();
    }

    private static JLabel mensagemLabel(String mensagem) {
        String html = mensagem.replace("\n", "<br>");
        JLabel lbl = new JLabel("<html><body style='width: 260px'>" + html + "</body></html>");
        lbl.setFont(FONTE_MENSAGEM);
        lbl.setForeground(COR_TEXTO);
        return lbl;
    }

    private static JButton criarBotaoOk() {
        JButton b = new JButton("OK");
        b.setFont(FONTE_BOTAO);
        b.setForeground(Color.WHITE);
        b.setBackground(COR_DESTAQUE);
        b.setPreferredSize(new Dimension(90, 34));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
