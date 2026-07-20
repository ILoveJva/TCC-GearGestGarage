package view;

import controller.OficinaController;
import model.Montadora;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class V_CadastrarModelo extends JPanel {

    private static final String[] TIPOS_VEICULO = {"Carro", "SUV", "Picape", "Moto", "Caminhão", "Van", "Misto"};

    private JPanel pnl_CardCentral;
    private JLabel lbl_TituloPaginacao;
    private JPanel pnl_Formulario;

    private JLabel lbl_Montadora, lbl_Nome, lbl_Tipo, lbl_Ano;
    private JComboBox<Montadora> cbb_Montadora;
    private JComboBox<String> cbb_Tipo;
    private JTextField txt_Nome, txt_Ano;
    private JButton btn_Cadastrar;

    private final OficinaController controller;

    public V_CadastrarModelo(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
        layoutComponents();
        carregarMontadoras();
        aplicarFiltros();
        vincularAcoes();
    }

    private void initComponents() {
        pnl_CardCentral = new JPanel(new BorderLayout(0, 20));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(500, 420));

        lbl_TituloPaginacao = new JLabel("Página Inicial > Cadastrar Modelo");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(Color.decode("#4D4D4D"));

        pnl_Formulario = new JPanel(new GridLayout(4, 1, 0, 15));
        pnl_Formulario.setBackground(Color.WHITE);

        // Montadora
        lbl_Montadora = criarLabel("Montadora *");
        cbb_Montadora = new JComboBox<>();
        cbb_Montadora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbb_Montadora.setBackground(Color.WHITE);
        cbb_Montadora.setPreferredSize(new Dimension(100, 36));
        cbb_Montadora.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof Montadora) setText(((Montadora) value).getNome());
                return this;
            }
        });

        // Nome
        lbl_Nome = criarLabel("Nome do Modelo *");
        txt_Nome = criarTextField();
        txt_Nome.setToolTipText("Ex: Corolla, Gol, Civic");

        // Tipo
        lbl_Tipo = criarLabel("Tipo *");
        cbb_Tipo = new JComboBox<>(TIPOS_VEICULO);
        cbb_Tipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbb_Tipo.setBackground(Color.WHITE);
        cbb_Tipo.setPreferredSize(new Dimension(100, 36));

        // Ano
        lbl_Ano = criarLabel("Ano * (ex: 2020)");
        txt_Ano = criarTextField();

        pnl_Formulario.add(criarContainerVertical(lbl_Montadora, cbb_Montadora));
        pnl_Formulario.add(criarContainerVertical(lbl_Nome, txt_Nome));
        pnl_Formulario.add(criarContainerVertical(lbl_Tipo, cbb_Tipo));
        pnl_Formulario.add(criarContainerVertical(lbl_Ano, txt_Ano));

        btn_Cadastrar = new JButton("CADASTRAR MODELO");
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

    private void carregarMontadoras() {
        if (controller == null) return;
        List<Montadora> lista = controller.montadorasComModelos();
        cbb_Montadora.removeAllItems();
        for (Montadora m : lista) cbb_Montadora.addItem(m);
    }

    private void aplicarFiltros() {
        ((AbstractDocument) txt_Nome.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_Ano.getDocument()).setDocumentFilter(new FiltroAno());
    }

    // =========================================================================
    // VALIDAÇÃO E TRATAMENTO DE ERROS
    // =========================================================================
    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            limparErro(txt_Nome);
            limparErro(txt_Ano);

            boolean ok = true;
            StringBuilder erros = new StringBuilder();

            Montadora montadora = (Montadora) cbb_Montadora.getSelectedItem();
            if (montadora == null) {
                erros.append("• Selecione uma Montadora.\n");
                ok = false;
            }

            String nome = txt_Nome.getText().trim();
            if (nome.length() < 2) {
                marcarErro(txt_Nome);
                erros.append("• Nome do modelo deve ter pelo menos 2 caracteres.\n");
                ok = false;
            }

            String tipo = cbb_Tipo.getSelectedItem() != null ? cbb_Tipo.getSelectedItem().toString() : "";
            if (tipo.isEmpty()) {
                erros.append("• Selecione o tipo do modelo.\n");
                ok = false;
            }

            int ano = 0;
            String anoTexto = txt_Ano.getText().trim();
            try {
                if (anoTexto.isEmpty()) throw new NumberFormatException();
                ano = Integer.parseInt(anoTexto);
                if (ano < 1886 || ano > 2100) {
                    marcarErro(txt_Ano);
                    erros.append("• Ano inválido. Informe entre 1886 e 2100.\n");
                    ok = false;
                }
            } catch (NumberFormatException ex) {
                marcarErro(txt_Ano);
                erros.append("• Informe um ano válido com 4 dígitos.\n");
                ok = false;
            }

            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "Corrija os campos destacados em vermelho:\n\n" + erros,
                    "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                controller.salvarModelo(nome, ano, tipo, montadora.getIdMontadora());
                JOptionPane.showMessageDialog(this,
                    "Modelo \"" + nome + "\" (" + tipo + ") cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txt_Nome.setText("");
                txt_Ano.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar modelo: " + ex,
                    "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void marcarErro(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.RED),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
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

    /** Permite letras (incluindo acentuadas), números, espaços e hífens. */
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
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-./]", "");
        }
    }

    /** Permite apenas dígitos, limite de 4 chars (ano). */
    private static class FiltroAno extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() + novo.length() <= 4)
                super.insertString(fb, off, novo, attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() - len + novo.length() <= 4)
                super.replace(fb, off, len, novo, attr);
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
