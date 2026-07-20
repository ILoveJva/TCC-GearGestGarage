package view;

import controller.OficinaController;
import br.com.oficina.atendimento.TipoServicoEntity;

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

public class V_CadastrarTipoServico extends JPanel {

    private JPanel pnl_CardCentral;
    private JLabel lbl_TituloPaginacao;
    private JTextField txt_Nome;
    private JButton btn_Cadastrar;
    private DefaultListModel<String> listModel;
    private JList<String> lst_Tipos;

    private final OficinaController controller;

    public V_CadastrarTipoServico(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
        layoutComponents();
        aplicarFiltros();
        vincularAcoes();
        carregarLista();
    }

    private void initComponents() {
        pnl_CardCentral = new JPanel(new BorderLayout(0, 20));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(500, 440));

        lbl_TituloPaginacao = new JLabel("Configurações > Cadastrar Tipo de Serviço");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(Color.decode("#4D4D4D"));

        JLabel lbl_Nome = criarLabel("Nome do Tipo de Serviço *");
        txt_Nome = criarTextField();
        txt_Nome.setToolTipText("Ex: Troca de Correia Dentada, Injeção Eletrônica, Suspensão...");

        btn_Cadastrar = new JButton("ADICIONAR TIPO");
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cadastrar.setForeground(Color.WHITE);
        btn_Cadastrar.setBackground(Color.decode("#FF9900"));
        btn_Cadastrar.setPreferredSize(new Dimension(200, 42));
        btn_Cadastrar.setFocusPainted(false);
        btn_Cadastrar.setBorderPainted(false);
        btn_Cadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel pnl_Campo = new JPanel(new BorderLayout(0, 6));
        pnl_Campo.setOpaque(false);
        pnl_Campo.add(lbl_Nome, BorderLayout.NORTH);
        pnl_Campo.add(txt_Nome, BorderLayout.CENTER);

        JPanel pnl_Topo = new JPanel(new BorderLayout(0, 12));
        pnl_Topo.setOpaque(false);
        pnl_Topo.add(pnl_Campo, BorderLayout.CENTER);
        JPanel pnl_BtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnl_BtnRow.setOpaque(false);
        pnl_BtnRow.add(btn_Cadastrar);
        pnl_Topo.add(pnl_BtnRow, BorderLayout.SOUTH);

        listModel = new DefaultListModel<>();
        lst_Tipos = new JList<>(listModel);
        lst_Tipos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lst_Tipos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(lst_Tipos);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            "Tipos de Serviço cadastrados",
            0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.decode("#666666")));

        JPanel pnl_Corpo = new JPanel(new BorderLayout(0, 14));
        pnl_Corpo.setOpaque(false);
        pnl_Corpo.add(pnl_Topo, BorderLayout.NORTH);
        pnl_Corpo.add(scroll, BorderLayout.CENTER);

        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Corpo, BorderLayout.CENTER);
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 40, 20, 40);
        add(pnl_CardCentral, gbc);
    }

    private void aplicarFiltros() {
        ((AbstractDocument) txt_Nome.getDocument()).setDocumentFilter(new FiltroTexto());
    }

    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            String nome = txt_Nome.getText().trim();
            if (nome.length() < 3) {
                marcarErro(txt_Nome);
                JOptionPane.showMessageDialog(this,
                    "O nome deve ter pelo menos 3 caracteres.",
                    "Campo Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            limparErro(txt_Nome);
            try {
                controller.salvarTipoServico(nome);
                JOptionPane.showMessageDialog(this,
                    "Tipo de serviço \"" + nome + "\" cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txt_Nome.setText("");
                carregarLista();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar: " + ex.getMessage(),
                    "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void carregarLista() {
        listModel.clear();
        List<TipoServicoEntity> tipos = controller.listarTiposServico();
        for (TipoServicoEntity t : tipos) listModel.addElement(t.getNome());
        if (listModel.isEmpty()) listModel.addElement("Nenhum tipo cadastrado ainda.");
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
    // INNER CLASSES
    // =========================================================================
    private static class FiltroTexto extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, off, filtrar(text), attr);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, off, len, filtrar(text), attr);
        }
        private String filtrar(String t) { return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-./,]", ""); }
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
