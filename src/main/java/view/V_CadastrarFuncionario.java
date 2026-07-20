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

public class V_CadastrarFuncionario extends JPanel {

    private JPanel pnl_CardCentral;
    private JLabel lbl_TituloPaginacao;
    private JPanel pnl_Formulario;

    private JLabel lbl_Nome, lbl_Cpf, lbl_Endereco, lbl_Email, lbl_Telefone, lbl_Cargo;
    private JTextField txt_Nome, txt_Cpf, txt_Endereco, txt_Email, txt_Telefone;
    private JComboBox<String> cbb_Cargo;
    private JButton btn_Cadastrar;

    private final OficinaController controller;

    public V_CadastrarFuncionario(OficinaController controller) {
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
        pnl_CardCentral.setPreferredSize(new Dimension(540, 520));

        lbl_TituloPaginacao = new JLabel("Configurações > Cadastrar Funcionário");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(Color.decode("#4D4D4D"));

        pnl_Formulario = new JPanel(new GridLayout(6, 1, 0, 12));
        pnl_Formulario.setBackground(Color.WHITE);

        lbl_Nome = criarLabel("Nome Completo *");
        txt_Nome = criarTextField();
        txt_Nome.setToolTipText("Ex: João Silva");

        lbl_Cpf = criarLabel("CPF *");
        txt_Cpf = criarTextField();
        txt_Cpf.setToolTipText("Somente números — 11 dígitos");

        lbl_Endereco = criarLabel("Endereço *");
        txt_Endereco = criarTextField();
        txt_Endereco.setToolTipText("Ex: Rua das Flores, 123 — São Paulo/SP");

        lbl_Email = criarLabel("E-mail *");
        txt_Email = criarTextField();
        txt_Email.setToolTipText("Ex: joao@email.com");

        lbl_Telefone = criarLabel("Telefone *");
        txt_Telefone = criarTextField();
        txt_Telefone.setToolTipText("Somente números — 10 ou 11 dígitos");

        lbl_Cargo = criarLabel("Cargo *");
        cbb_Cargo = new JComboBox<>(new String[]{
            "Mecânico", "Eletricista Automotivo", "Funileiro", "Pintor",
            "Auxiliar Mecânico", "Gerente", "Atendente", "Outro"
        });
        cbb_Cargo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbb_Cargo.setBackground(Color.WHITE);

        pnl_Formulario.add(criarContainerVertical(lbl_Nome, txt_Nome));
        pnl_Formulario.add(criarContainerVertical(lbl_Cpf, txt_Cpf));
        pnl_Formulario.add(criarContainerVertical(lbl_Endereco, txt_Endereco));
        pnl_Formulario.add(criarContainerVertical(lbl_Email, txt_Email));
        pnl_Formulario.add(criarContainerVertical(lbl_Telefone, txt_Telefone));
        pnl_Formulario.add(criarContainerVertical(lbl_Cargo, cbb_Cargo));

        btn_Cadastrar = new JButton("CADASTRAR FUNCIONÁRIO");
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cadastrar.setForeground(Color.WHITE);
        btn_Cadastrar.setBackground(Color.decode("#FF9900"));
        btn_Cadastrar.setPreferredSize(new Dimension(260, 45));
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
        ((AbstractDocument) txt_Cpf.getDocument()).setDocumentFilter(new FiltroDigitos(11));
        ((AbstractDocument) txt_Endereco.getDocument()).setDocumentFilter(new FiltroEndereco());
        ((AbstractDocument) txt_Telefone.getDocument()).setDocumentFilter(new FiltroDigitos(11));
    }

    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            limparErro(txt_Nome); limparErro(txt_Cpf);
            limparErro(txt_Endereco); limparErro(txt_Email); limparErro(txt_Telefone);

            StringBuilder erros = new StringBuilder();
            boolean ok = true;

            String nome = txt_Nome.getText().trim();
            if (nome.length() < 2) {
                marcarErro(txt_Nome);
                erros.append("• Nome deve ter pelo menos 2 caracteres.\n");
                ok = false;
            }

            String cpf = txt_Cpf.getText().trim();
            if (cpf.length() != 11) {
                marcarErro(txt_Cpf);
                erros.append("• CPF deve ter exatamente 11 dígitos.\n");
                ok = false;
            }

            String endereco = txt_Endereco.getText().trim();
            if (endereco.length() < 5) {
                marcarErro(txt_Endereco);
                erros.append("• Endereço deve ter pelo menos 5 caracteres.\n");
                ok = false;
            }

            String email = txt_Email.getText().trim();
            if (!email.contains("@") || !email.contains(".")) {
                marcarErro(txt_Email);
                erros.append("• Informe um e-mail válido.\n");
                ok = false;
            }

            String telefone = txt_Telefone.getText().trim();
            if (telefone.length() < 10) {
                marcarErro(txt_Telefone);
                erros.append("• Telefone deve ter 10 ou 11 dígitos.\n");
                ok = false;
            }

            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "Corrija os campos destacados em vermelho:\n\n" + erros,
                    "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cargo = cbb_Cargo.getSelectedItem().toString();
            try {
                controller.salvarFuncionario(nome, cpf, endereco, email, telefone, cargo);
                JOptionPane.showMessageDialog(this,
                    "Funcionário \"" + nome + "\" cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txt_Nome.setText(""); txt_Cpf.setText("");
                txt_Endereco.setText(""); txt_Email.setText("");
                txt_Telefone.setText(""); cbb_Cargo.setSelectedIndex(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar funcionário: " + ex.getMessage(),
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
            @Override public void focusGained(FocusEvent e) {
                limparErro(field); field.removeFocusListener(this);
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
    private JPanel criarContainerVertical(JLabel label, Component comp) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setOpaque(false);
        c.add(label, BorderLayout.NORTH);
        c.add(comp, BorderLayout.CENTER);
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
    private static class FiltroTexto extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, off, filtrar(text), attr);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, off, len, filtrar(text), attr);
        }
        private String filtrar(String t) { return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-.]", ""); }
    }

    private static class FiltroEndereco extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, off, filtrar(text), attr);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, off, len, filtrar(text), attr);
        }
        private String filtrar(String t) { return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-.,/°ªº]", ""); }
    }

    private static class FiltroDigitos extends DocumentFilter {
        private final int max;
        FiltroDigitos(int max) { this.max = max; }
        @Override public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() + novo.length() <= max) super.insertString(fb, off, novo, attr);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() - len + novo.length() <= max) super.replace(fb, off, len, novo, attr);
        }
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio; private final Color cor;
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
