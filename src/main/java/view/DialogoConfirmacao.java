package view;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo de confirmação com o mesmo padrão visual do restante do sistema
 * (fundo branco, fonte Segoe UI, botão de destaque laranja #FF9900), usado
 * no lugar do JOptionPane.showConfirmDialog padrão (cinza, fora do padrão).
 */
public final class DialogoConfirmacao {

    private static final Color COR_DESTAQUE = Color.decode("#FF9900");
    private static final Color COR_BORDA = Color.decode("#CCCCCC");
    private static final Color COR_TEXTO = Color.decode("#333333");
    private static final Font FONTE_MENSAGEM = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 13);

    private DialogoConfirmacao() {}

    /** Confirmação simples Sim/Não (ex.: exclusão). Retorna true se o usuário confirmar. */
    public static boolean confirmar(Component pai, String mensagem, String titulo) {
        JButton btnCancelar = criarBotao("Cancelar", false);
        JButton btnConfirmar = criarBotao("Confirmar", true);
        return exibir(pai, mensagemLabel(mensagem), titulo, btnCancelar, btnConfirmar) == btnConfirmar;
    }

    /**
     * Exige a senha do usuário logado antes de uma ação sensível.
     * Retorna a senha digitada, ou null se o usuário cancelou.
     */
    public static String pedirSenha(Component pai, String titulo) {
        JPasswordField campo = new JPasswordField(16);
        campo.setFont(FONTE_MENSAGEM);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel rotulo = new JLabel("Digite sua senha para continuar:");
        rotulo.setFont(FONTE_MENSAGEM);
        rotulo.setForeground(COR_TEXTO);
        rotulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel corpo = new JPanel(new BorderLayout());
        corpo.setOpaque(false);
        corpo.add(rotulo, BorderLayout.NORTH);
        corpo.add(campo, BorderLayout.CENTER);

        JButton btnCancelar = criarBotao("Cancelar", false);
        JButton btnConfirmar = criarBotao("Confirmar", true);
        campo.addActionListener(e -> btnConfirmar.doClick());

        Object valor = exibir(pai, corpo, titulo, btnCancelar, btnConfirmar, campo);
        return valor == btnConfirmar ? new String(campo.getPassword()) : null;
    }

    private static Object exibir(Component pai, JComponent conteudo, String titulo,
                                  JButton btnCancelar, JButton btnConfirmar) {
        return exibir(pai, conteudo, titulo, btnCancelar, btnConfirmar, null);
    }

    private static Object exibir(Component pai, JComponent conteudo, String titulo,
                                  JButton btnCancelar, JButton btnConfirmar, JComponent foco) {
        Object[] opcoes = {btnCancelar, btnConfirmar};
        JOptionPane pane = new JOptionPane(conteudo, JOptionPane.PLAIN_MESSAGE,
            JOptionPane.YES_NO_OPTION, null, opcoes, btnConfirmar);
        PainelUtil.aplicarFundoBranco(pane);

        JDialog dialog = pane.createDialog(pai, titulo);
        btnCancelar.addActionListener(e -> pane.setValue(btnCancelar));
        btnConfirmar.addActionListener(e -> pane.setValue(btnConfirmar));
        if (foco != null) SwingUtilities.invokeLater(foco::requestFocusInWindow);
        dialog.setVisible(true);
        dialog.dispose();
        return pane.getValue();
    }

    private static JLabel mensagemLabel(String mensagem) {
        String html = mensagem.replace("\n", "<br>");
        JLabel lbl = new JLabel("<html><body style='width: 260px'>" + html + "</body></html>");
        lbl.setFont(FONTE_MENSAGEM);
        lbl.setForeground(COR_TEXTO);
        return lbl;
    }

    private static JButton criarBotao(String texto, boolean destaque) {
        JButton b = new JButton(texto);
        b.setFont(FONTE_BOTAO);
        b.setPreferredSize(new Dimension(110, 34));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (destaque) {
            b.setForeground(Color.WHITE);
            b.setBackground(COR_DESTAQUE);
            b.setBorderPainted(false);
            b.setOpaque(true);
        } else {
            b.setForeground(COR_TEXTO);
            b.setBackground(Color.WHITE);
            b.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        }
        return b;
    }
}
