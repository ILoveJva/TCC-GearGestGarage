package view;

import controller.OficinaController;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class V_CadastrarMontadora extends JPanel {

    private JPanel pnl_CardCentral;
    private JLabel lbl_TituloPaginacao;
    private JPanel pnl_Formulario;

    private JLabel lbl_Nome, lbl_Pais;
    private JTextField txt_Nome, txt_Pais;
    private JButton btn_Cadastrar;

    private final OficinaController controller;

    public V_CadastrarMontadora(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
        layoutComponents();
        aplicarFiltros();
        vincularAcoes();
    }

    private void initComponents() {
        pnl_CardCentral = new JPanel(new BorderLayout(0, 20));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(500, 280));

        lbl_TituloPaginacao = new JLabel("Página Inicial > Cadastrar Montadora");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(Color.decode("#4D4D4D"));

        pnl_Formulario = new JPanel(new GridLayout(2, 1, 0, 15));
        pnl_Formulario.setBackground(Color.WHITE);

        lbl_Nome = criarLabel("Nome da Montadora *");
        txt_Nome = criarTextField();
        txt_Nome.setToolTipText("Ex: Toyota, Volkswagen, Ford");

        lbl_Pais = criarLabel("País de Origem");
        txt_Pais = criarTextField();
        txt_Pais.setToolTipText("Ex: Japão, Alemanha, Estados Unidos");

        pnl_Formulario.add(criarContainerVertical(lbl_Nome, txt_Nome));
        pnl_Formulario.add(criarContainerVertical(lbl_Pais, txt_Pais));

        btn_Cadastrar = new JButton("CADASTRAR MONTADORA");
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cadastrar.setForeground(Color.WHITE);
        btn_Cadastrar.setBackground(Color.decode("#FF9900"));
        btn_Cadastrar.setPreferredSize(new Dimension(240, 45));
        btn_Cadastrar.setFocusPainted(false);
        btn_Cadastrar.setBorderPainted(false);
        btn_Cadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Formulario, BorderLayout.CENTER);

        JPanel pnl_BtnContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnl_BtnContainer.setOpaque(false);
        pnl_BtnContainer.add(btn_Cadastrar);
        pnl_CardCentral.add(pnl_BtnContainer, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    private void aplicarFiltros() {
        ((AbstractDocument) txt_Nome.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_Pais.getDocument()).setDocumentFilter(new FiltroTexto());
    }

    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            limparErro(txt_Nome);

            String nome = txt_Nome.getText().trim();
            String pais = txt_Pais.getText().trim();

            if (nome.length() < 2) {
                marcarErro(txt_Nome);
                JOptionPane.showMessageDialog(this,
                    "O nome da montadora deve ter pelo menos 2 caracteres.",
                    "Campo Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                controller.salvarMontadora(nome, pais.isEmpty() ? "Não informado" : pais);
                JOptionPane.showMessageDialog(this,
                    "Montadora \"" + nome + "\" cadastrada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txt_Nome.setText("");
                txt_Pais.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar montadora: " + ex.getMessage(),
                    "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // =========================================================================
    // TRATAMENTO DE ERROS
    // =========================================================================
    private void marcarErro(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.RED),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                limparErro(field);
                field.removeFocusListener(this);
            }
        });
    }

    private void limparErro(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
    }

    // =========================================================================
    // UI HELPERS
    // =========================================================================
    private JPanel criarContainerVertical(JLabel label, JTextField field) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setOpaque(false);
        c.add(label, BorderLayout.NORTH);
        c.add(field, BorderLayout.CENTER);
        return c;
    }

    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Color.decode("#333333"));
        return l;
    }

    private JTextField criarTextField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(100, 36));
        f.setBackground(Color.WHITE);
        f.setForeground(Color.BLACK);
        f.setCaretColor(Color.BLACK);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        return f;
    }

    // =========================================================================
    // INNER CLASSES — DocumentFilter
    // =========================================================================

    /** Permite letras (incluindo acentuadas), espaços e hífens. */
    private static class FiltroTexto extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, off, filtrar(text), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, off, len, filtrar(text), attr);
        }
        private String filtrar(String t) {
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-.]", "");
        }
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio;
        private final Color cor;
        RoundedBorder(int raio, Color cor) { this.raio = raio; this.cor = cor; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(cor);
            g2d.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2d.dispose();
        }
    }
}
