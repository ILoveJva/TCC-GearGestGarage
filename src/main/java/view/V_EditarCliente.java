package view;

import controller.OficinaController;
import model.Cliente;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class V_EditarCliente extends JPanel {

    private JPanel pnl_CardCentral;
    private JPanel pnl_Formulario;
    private JLabel lbl_TituloPaginacao;

    private JTextField txt_Nome, txt_CPF, txt_Celular, txt_Email;
    private JButton btn_Salvar;

    private final OficinaController controller;
    private final Cliente cliente;

    public V_EditarCliente(OficinaController controller, Cliente cliente) {
        this.controller = controller;
        this.cliente = cliente;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
        layoutComponents();
        aplicarFiltros();
        preencherCampos();
        vincularAcoes();
    }

    private void initComponents() {
        pnl_CardCentral = new JPanel(new BorderLayout(0, 20));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(520, 380));

        lbl_TituloPaginacao = new JLabel("Consultar Clientes > Editar Cliente");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(Color.decode("#4D4D4D"));

        pnl_Formulario = new JPanel(new GridLayout(4, 1, 0, 12));
        pnl_Formulario.setBackground(Color.WHITE);

        txt_Nome    = criarTextField();
        txt_CPF     = criarTextField();
        txt_Celular = criarTextField();
        txt_Email   = criarTextField();

        pnl_Formulario.add(criarContainerVertical(criarLabel("Nome completo *"), txt_Nome));
        pnl_Formulario.add(criarContainerVertical(criarLabel("CPF * (ex: 000.000.000-00)"), txt_CPF));
        pnl_Formulario.add(criarContainerVertical(criarLabel("Celular * (ex: (00) 00000-0000)"), txt_Celular));
        pnl_Formulario.add(criarContainerVertical(criarLabel("E-mail *"), txt_Email));

        btn_Salvar = new JButton("SALVAR ALTERAÇÕES");
        btn_Salvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Salvar.setForeground(Color.WHITE);
        btn_Salvar.setBackground(Color.decode("#FF9900"));
        btn_Salvar.setPreferredSize(new Dimension(220, 45));
        btn_Salvar.setFocusPainted(false);
        btn_Salvar.setBorderPainted(false);
        btn_Salvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Formulario, BorderLayout.CENTER);

        JPanel pnl_ContainerBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnl_ContainerBotao.setOpaque(false);
        pnl_ContainerBotao.add(btn_Salvar);
        pnl_CardCentral.add(pnl_ContainerBotao, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    private void preencherCampos() {
        txt_Nome.setText(cliente.getNome());
        txt_CPF.setText(cliente.getCpf());
        txt_Celular.setText(cliente.getTelefone());
        txt_Email.setText(cliente.getEmail());
    }

    private void aplicarFiltros() {
        ((AbstractDocument) txt_Nome.getDocument()).setDocumentFilter(new FiltroLetras());
        ((AbstractDocument) txt_CPF.getDocument()).setDocumentFilter(new FiltroCpf());
        ((AbstractDocument) txt_Celular.getDocument()).setDocumentFilter(new FiltroCelular());
    }

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

        if (!ok) {
            DialogoAlerta.aviso(this, "Corrija os campos destacados em vermelho:\n\n" + msg, "Dados Inválidos");
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
        for (JTextField f : new JTextField[]{ txt_Nome, txt_CPF, txt_Celular, txt_Email })
            limparErro(f);
    }

    private void vincularAcoes() {
        btn_Salvar.addActionListener(e -> {
            if (!validarFormulario()) return;

            String nome    = txt_Nome.getText().trim();
            String cpf     = txt_CPF.getText().trim();
            String celular = txt_Celular.getText().trim();
            String email   = txt_Email.getText().trim();

            try {
                controller.atualizarCliente(cliente.getIdUsuario(), nome, cpf, email, celular);
                DialogoAlerta.sucesso(this, "Cliente \"" + nome + "\" atualizado com sucesso!", "Sucesso");
                navegarPara(new V_VisualizarClientes(controller));
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao atualizar cliente: " + ex.getMessage(), "Erro no Sistema");
            }
        });
    }

    private void navegarPara(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
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
