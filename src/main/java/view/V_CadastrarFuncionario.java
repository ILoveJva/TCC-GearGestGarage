package view;

import br.com.oficina.shared.viacep.ViaCepClient;
import controller.OficinaController;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

    private JLabel lbl_Nome, lbl_Cpf, lbl_Cep, lbl_Endereco, lbl_Email, lbl_Telefone, lbl_Cargo;
    private JTextField txt_Nome, txt_Cpf, txt_Cep, txt_Endereco, txt_Email, txt_Telefone;
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

        pnl_Formulario = new JPanel(new GridLayout(7, 1, 0, 12));
        pnl_Formulario.setBackground(Color.WHITE);

        lbl_Nome = criarLabel("Nome Completo *");
        txt_Nome = criarTextField();
        txt_Nome.setToolTipText("Ex: João Silva");

        lbl_Cpf = criarLabel("CPF *");
        txt_Cpf = criarTextField();
        txt_Cpf.setToolTipText("Somente números — 11 dígitos");

        lbl_Cep = criarLabel("CEP");
        txt_Cep = criarTextField();
        txt_Cep.setToolTipText("Ex: 00000-000 — preenche o endereço automaticamente");

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
        pnl_Formulario.add(criarContainerVertical(lbl_Cep, txt_Cep));
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
        ((AbstractDocument) txt_Cep.getDocument()).setDocumentFilter(new FiltroCep());
        ((AbstractDocument) txt_Endereco.getDocument()).setDocumentFilter(new FiltroEndereco());
        ((AbstractDocument) txt_Telefone.getDocument()).setDocumentFilter(new FiltroDigitos(11));

        txt_Cep.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { verificarCep(); }
            @Override public void removeUpdate(DocumentEvent e) { }
            @Override public void changedUpdate(DocumentEvent e) { }
        });
    }

    /** Dispara a busca automática na ViaCEP assim que o CEP tiver os 8 dígitos. */
    private void verificarCep() {
        String cep = txt_Cep.getText().replaceAll("[^0-9]", "");
        if (cep.length() != 8) return;
        new SwingWorker<ViaCepClient.Endereco, Void>() {
            @Override protected ViaCepClient.Endereco doInBackground() throws Exception {
                return ViaCepClient.buscar(cep);
            }
            @Override protected void done() {
                try {
                    ViaCepClient.Endereco end = get();
                    if (end == null) { marcarErro(txt_Cep); return; }
                    limparErro(txt_Cep);
                    txt_Endereco.setText(end.formatado());
                } catch (Exception ignored) {
                    // Falha de rede/serviço indisponível: usuário preenche o endereço manualmente.
                }
            }
        }.execute();
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
                DialogoAlerta.aviso(this, "Corrija os campos destacados em vermelho:\n\n" + erros, "Dados Inválidos");
                return;
            }

            String cargo = cbb_Cargo.getSelectedItem().toString();
            try {
                controller.salvarFuncionario(nome, cpf, endereco, email, telefone, cargo);
                DialogoAlerta.sucesso(this, "Funcionário \"" + nome + "\" cadastrado com sucesso!", "Sucesso");
                txt_Nome.setText(""); txt_Cpf.setText(""); txt_Cep.setText("");
                txt_Endereco.setText(""); txt_Email.setText("");
                txt_Telefone.setText(""); cbb_Cargo.setSelectedIndex(0);
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao cadastrar funcionário: " + ex.getMessage(), "Erro no Sistema");
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

    /** Auto-formata CEP como XXXXX-XXX (aceita apenas dígitos). */
    private static class FiltroCep extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + text + atual.substring(off), attr);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + (text != null ? text : "") + atual.substring(off + len), attr);
        }
        @Override public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + atual.substring(off + len), null);
        }
        private void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
            String d = texto.replaceAll("[^0-9]", "");
            if (d.length() > 8) d = d.substring(0, 8);
            fb.replace(0, fb.getDocument().getLength(), formatar(d), attr);
        }
        private String formatar(String d) {
            return d.length() <= 5 ? d : d.substring(0, 5) + "-" + d.substring(5);
        }
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
