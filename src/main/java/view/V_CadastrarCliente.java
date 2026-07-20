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

public class V_CadastrarCliente extends JPanel {

    private JPanel pnl_CardCentral;
    private JPanel pnl_Formulario;
    private JLabel lbl_TituloPaginacao;

    private JLabel lbl_Nome, lbl_CPF, lbl_Celular, lbl_Email, lbl_CEP, lbl_Numero, lbl_Endereco, lbl_Complemento;
    private JTextField txt_Nome, txt_CPF, txt_Celular, txt_Email, txt_CEP, txt_Numero, txt_Endereco, txt_Complemento;
    private JButton btn_Cadastrar;

    private OficinaController controller;

    public V_CadastrarCliente(OficinaController controller) {
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
        pnl_CardCentral.setPreferredSize(new Dimension(680, 520));

        lbl_TituloPaginacao = new JLabel("Página Inicial > Cadastrar Cliente");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(Color.decode("#4D4D4D"));

        pnl_Formulario = new JPanel(new GridLayout(4, 2, 25, 15));
        pnl_Formulario.setBackground(Color.WHITE);

        lbl_Nome     = criarLabel("Nome completo *");
        txt_Nome     = criarTextField();

        lbl_CPF      = criarLabel("CPF * (ex: 000.000.000-00)");
        txt_CPF      = criarTextField();

        lbl_Celular  = criarLabel("Celular * (ex: (00) 00000-0000)");
        txt_Celular  = criarTextField();

        lbl_Email    = criarLabel("E-mail *");
        txt_Email    = criarTextField();

        lbl_CEP      = criarLabel("CEP (ex: 00000-000)");
        txt_CEP      = criarTextField();

        lbl_Numero   = criarLabel("Número");
        txt_Numero   = criarTextField();

        lbl_Endereco    = criarLabel("Endereço");
        txt_Endereco    = criarTextField();

        lbl_Complemento = criarLabel("Complemento");
        txt_Complemento = criarTextField();

        pnl_Formulario.add(criarContainerVertical(lbl_Nome,       txt_Nome));
        pnl_Formulario.add(criarContainerVertical(lbl_CPF,        txt_CPF));
        pnl_Formulario.add(criarContainerVertical(lbl_Celular,    txt_Celular));
        pnl_Formulario.add(criarContainerVertical(lbl_Email,      txt_Email));
        pnl_Formulario.add(criarContainerVertical(lbl_CEP,        txt_CEP));
        pnl_Formulario.add(criarContainerVertical(lbl_Numero,     txt_Numero));
        pnl_Formulario.add(criarContainerVertical(lbl_Endereco,   txt_Endereco));
        pnl_Formulario.add(criarContainerVertical(lbl_Complemento, txt_Complemento));

        btn_Cadastrar = new JButton("CADASTRAR CLIENTE");
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cadastrar.setForeground(Color.WHITE);
        btn_Cadastrar.setBackground(Color.decode("#FF9900"));
        btn_Cadastrar.setPreferredSize(new Dimension(220, 45));
        btn_Cadastrar.setFocusPainted(false);
        btn_Cadastrar.setBorderPainted(false);
        btn_Cadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Formulario, BorderLayout.CENTER);

        JPanel pnl_ContainerBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnl_ContainerBotao.setOpaque(false);
        pnl_ContainerBotao.add(btn_Cadastrar);
        pnl_CardCentral.add(pnl_ContainerBotao, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    // =========================================================================
    // FILTROS DE ENTRADA (DocumentFilter)
    // =========================================================================
    private void aplicarFiltros() {
        ((AbstractDocument) txt_Nome.getDocument()).setDocumentFilter(new FiltroLetras());
        ((AbstractDocument) txt_CPF.getDocument()).setDocumentFilter(new FiltroCpf());
        ((AbstractDocument) txt_Celular.getDocument()).setDocumentFilter(new FiltroCelular());
        ((AbstractDocument) txt_CEP.getDocument()).setDocumentFilter(new FiltroCep());
        ((AbstractDocument) txt_Numero.getDocument()).setDocumentFilter(new FiltroDigitos(10));
    }

    // =========================================================================
    // VALIDAÇÃO E TRATAMENTO DE ERROS
    // =========================================================================
    private boolean validarFormulario() {
        limparTodosErros();
        boolean ok = true;
        StringBuilder msg = new StringBuilder();

        String nome = txt_Nome.getText().trim();
        if (nome.length() < 3) {
            marcarErro(txt_Nome);
            msg.append("• Nome deve ter pelo menos 3 caracteres.\n");
            ok = false;
        }

        String cpf = txt_CPF.getText().replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            marcarErro(txt_CPF);
            msg.append("• CPF incompleto (informe os 11 dígitos).\n");
            ok = false;
        }

        String cel = txt_Celular.getText().replaceAll("[^0-9]", "");
        if (cel.length() < 10 || cel.length() > 11) {
            marcarErro(txt_Celular);
            msg.append("• Celular inválido (DDD + 8 ou 9 dígitos).\n");
            ok = false;
        }

        String email = txt_Email.getText().trim();
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[\\w.\\-]+$")) {
            marcarErro(txt_Email);
            msg.append("• E-mail em formato inválido.\n");
            ok = false;
        }

        String cep = txt_CEP.getText().replaceAll("[^0-9]", "");
        if (!cep.isEmpty() && cep.length() != 8) {
            marcarErro(txt_CEP);
            msg.append("• CEP incompleto (informe os 8 dígitos).\n");
            ok = false;
        }

        if (!ok) {
            JOptionPane.showMessageDialog(this,
                "Corrija os campos destacados em vermelho:\n\n" + msg,
                "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
        }
        return ok;
    }

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

    private void limparTodosErros() {
        for (JTextField f : new JTextField[]{ txt_Nome, txt_CPF, txt_Celular, txt_Email, txt_CEP, txt_Numero })
            limparErro(f);
    }

    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            if (!validarFormulario()) return;

            String nome    = txt_Nome.getText().trim();
            String cpf     = txt_CPF.getText().trim();
            String celular = txt_Celular.getText().trim();
            String email   = txt_Email.getText().trim();

            if (controller != null) {
                try {
                    controller.salvarCliente(nome, cpf, celular, email);
                    JOptionPane.showMessageDialog(this,
                        "Cliente \"" + nome + "\" cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCamposFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao salvar cliente: " + ex.getMessage(),
                        "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro: O controlador do sistema não foi localizado.",
                    "Erro de Link de Dados", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void limparCamposFormulario() {
        txt_Nome.setText(""); txt_CPF.setText(""); txt_Celular.setText("");
        txt_Email.setText(""); txt_CEP.setText(""); txt_Numero.setText("");
        txt_Endereco.setText(""); txt_Complemento.setText("");
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE ESTILIZAÇÃO
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

    /** Permite apenas letras (incluindo acentuadas), espaços e hífens. */
    private static class FiltroLetras extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, filtrar(text), attr);
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, offset, length, filtrar(text), attr);
        }
        private String filtrar(String t) {
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ\\s'\\-]", "");
        }
    }

    /** Auto-formata CPF como XXX.XXX.XXX-XX (aceita apenas dígitos). */
    private static class FiltroCpf extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + text + atual.substring(off), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + (text != null ? text : "") + atual.substring(off + len), attr);
        }
        @Override
        public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + atual.substring(off + len), null);
        }
        private void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
            String d = texto.replaceAll("[^0-9]", "");
            if (d.length() > 11) d = d.substring(0, 11);
            fb.replace(0, fb.getDocument().getLength(), formatar(d), attr);
        }
        private String formatar(String d) {
            int n = d.length();
            if (n <= 3) return d;
            if (n <= 6) return d.substring(0,3) + "." + d.substring(3);
            if (n <= 9) return d.substring(0,3) + "." + d.substring(3,6) + "." + d.substring(6);
            return d.substring(0,3) + "." + d.substring(3,6) + "." + d.substring(6,9) + "-" + d.substring(9);
        }
    }

    /** Auto-formata celular como (XX) XXXXX-XXXX (aceita apenas dígitos). */
    private static class FiltroCelular extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + text + atual.substring(off), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + (text != null ? text : "") + atual.substring(off + len), attr);
        }
        @Override
        public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + atual.substring(off + len), null);
        }
        private void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
            String d = texto.replaceAll("[^0-9]", "");
            if (d.length() > 11) d = d.substring(0, 11);
            fb.replace(0, fb.getDocument().getLength(), formatar(d), attr);
        }
        private String formatar(String d) {
            int n = d.length();
            if (n == 0) return "";
            if (n <= 2) return "(" + d;
            if (n <= 7) return "(" + d.substring(0,2) + ") " + d.substring(2);
            return "(" + d.substring(0,2) + ") " + d.substring(2,7) + "-" + d.substring(7, Math.min(n,11));
        }
    }

    /** Auto-formata CEP como XXXXX-XXX (aceita apenas dígitos). */
    private static class FiltroCep extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + text + atual.substring(off), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + (text != null ? text : "") + atual.substring(off + len), attr);
        }
        @Override
        public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + atual.substring(off + len), null);
        }
        private void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
            String d = texto.replaceAll("[^0-9]", "");
            if (d.length() > 8) d = d.substring(0, 8);
            fb.replace(0, fb.getDocument().getLength(), formatar(d), attr);
        }
        private String formatar(String d) {
            int n = d.length();
            if (n <= 5) return d;
            return d.substring(0,5) + "-" + d.substring(5);
        }
    }

    /** Permite apenas dígitos numéricos com limite de comprimento. */
    private static class FiltroDigitos extends DocumentFilter {
        private final int max;
        FiltroDigitos(int max) { this.max = max; }
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            int espaço = max - fb.getDocument().getLength();
            if (espaço > 0) super.insertString(fb, off, novo.substring(0, Math.min(novo.length(), espaço)), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            int novoTamanho = fb.getDocument().getLength() - len + novo.length();
            if (novoTamanho <= max) super.replace(fb, off, len, novo, attr);
            else {
                int espaço = max - (fb.getDocument().getLength() - len);
                if (espaço > 0) super.replace(fb, off, len, novo.substring(0, espaço), attr);
            }
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
