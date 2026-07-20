package view;

import br.com.oficina.usuario.FuncionarioEntity;
import controller.OficinaController;
import model.Cliente;
import model.Modelo;
import model.Montadora;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Cadastro de Veículo.
 * Tipo e Ano funcionam apenas como filtros para o ComboBox de Modelo.
 * O tipo do veículo é herdado do modelo selecionado e não é salvo separadamente.
 */
public class V_CadastrarVeiculo extends JPanel {

    private static final String[] TIPOS_VEICULO = {"Todos", "Carro", "SUV", "Picape", "Moto", "Caminhão", "Van", "Misto"};

    // =========================================================================
    // DECLARAÇÃO DOS COMPONENTES
    // =========================================================================
    private JPanel pnl_CardCentral;
    private JLabel lbl_TituloPagina;

    private Pnl_SelecaoCliente pnl_SelecaoCliente;

    // Toggle proprietário
    private JRadioButton rdb_ProprietarioCliente;
    private JRadioButton rdb_ProprietarioFuncionario;
    private JPanel pnl_ProprietarioSwitch;
    private JComboBox<FuncionarioEntity> cmb_Funcionario;

    private JPanel pnl_FormularioVeiculo;
    private JLabel lbl_Montadora, lbl_TipoVeiculo, lbl_Modelo, lbl_Ano, lbl_Placa;

    private JComboBox<Montadora> cbb_Montadora;
    private JComboBox<Modelo> cbb_Modelo;
    private JComboBox<String> cbb_TipoVeiculo;

    private JTextField txt_Ano, txt_Placa;

    private JButton btn_CadastrarVeiculo;

    private final OficinaController controller;

    public V_CadastrarVeiculo(OficinaController controller) {
        this.controller = controller;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        initComponents();
        layoutComponents();

        carregarClientesTabela();
        carregarMontadoras();
        aplicarFiltros();
        vincularAcoes();
    }

    private void initComponents() {
        pnl_CardCentral = new JPanel(new BorderLayout(0, 15));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 520));

        lbl_TituloPagina = new JLabel("Página Inicial > Cadastrar Veículo");
        lbl_TituloPagina.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPagina.setForeground(Color.decode("#4D4D4D"));

        pnl_SelecaoCliente = new Pnl_SelecaoCliente();
        pnl_SelecaoCliente.setPreferredSize(new Dimension(680, 160));

        rdb_ProprietarioCliente = new JRadioButton("Cliente", true);
        rdb_ProprietarioFuncionario = new JRadioButton("Funcionário");
        rdb_ProprietarioCliente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_ProprietarioFuncionario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_ProprietarioCliente.setOpaque(false);
        rdb_ProprietarioFuncionario.setOpaque(false);
        ButtonGroup grpProprietario = new ButtonGroup();
        grpProprietario.add(rdb_ProprietarioCliente);
        grpProprietario.add(rdb_ProprietarioFuncionario);

        cmb_Funcionario = new JComboBox<>();
        cmb_Funcionario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmb_Funcionario.setBackground(Color.WHITE);
        cmb_Funcionario.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof FuncionarioEntity fe) setText(fe.getNome() + " — " + fe.getCargo());
                return this;
            }
        });

        JPanel pnl_FuncContainer = new JPanel(new BorderLayout());
        pnl_FuncContainer.setOpaque(false);
        JLabel lbl_FuncTitulo = new JLabel("Selecione o Funcionário Proprietário:");
        lbl_FuncTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl_FuncTitulo.setForeground(Color.decode("#333333"));
        pnl_FuncContainer.add(lbl_FuncTitulo, BorderLayout.NORTH);
        pnl_FuncContainer.add(cmb_Funcionario, BorderLayout.CENTER);
        pnl_FuncContainer.setPreferredSize(new Dimension(680, 160));

        pnl_ProprietarioSwitch = new JPanel(new CardLayout());
        pnl_ProprietarioSwitch.setOpaque(false);
        pnl_ProprietarioSwitch.add(pnl_SelecaoCliente, "CLI");
        pnl_ProprietarioSwitch.add(pnl_FuncContainer, "FUNC");

        pnl_FormularioVeiculo = new JPanel(new GridLayout(3, 1, 0, 10));
        pnl_FormularioVeiculo.setBackground(Color.WHITE);

        // --- Linha 1: Montadora / Tipo (filtro) ---
        JPanel pnl_Linha1 = new JPanel(new BorderLayout(15, 0));
        pnl_Linha1.setOpaque(false);

        lbl_Montadora = criarLabelCampo("Montadora *");
        cbb_Montadora = new JComboBox<>();
        cbb_Montadora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbb_Montadora.setBackground(Color.WHITE);
        cbb_Montadora.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Montadora) setText(((Montadora) value).getNome());
                return this;
            }
        });
        JPanel pnl_CompMontadora = criarContainerVertical(lbl_Montadora, cbb_Montadora);

        lbl_TipoVeiculo = criarLabelCampo("Tipo (filtro)");
        cbb_TipoVeiculo = new JComboBox<>(TIPOS_VEICULO);
        cbb_TipoVeiculo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbb_TipoVeiculo.setBackground(Color.WHITE);
        cbb_TipoVeiculo.setPreferredSize(new Dimension(150, 36));
        cbb_TipoVeiculo.setToolTipText("Filtra os modelos disponíveis por tipo");
        JPanel pnl_CompTipo = criarContainerVertical(lbl_TipoVeiculo, cbb_TipoVeiculo);

        pnl_Linha1.add(pnl_CompMontadora, BorderLayout.CENTER);
        pnl_Linha1.add(pnl_CompTipo, BorderLayout.EAST);

        // --- Linha 2: Ano (filtro) / Modelo ---
        JPanel pnl_Linha2 = new JPanel(new BorderLayout(15, 0));
        pnl_Linha2.setOpaque(false);

        lbl_Ano = criarLabelCampo("Ano (filtro)");
        txt_Ano = criarTextFieldInput();
        txt_Ano.setToolTipText("Filtra os modelos disponíveis por ano");
        JPanel pnl_CompAno = criarContainerVertical(lbl_Ano, txt_Ano);
        pnl_CompAno.setPreferredSize(new Dimension(150, 55));

        lbl_Modelo = criarLabelCampo("Modelo *");
        cbb_Modelo = new JComboBox<>();
        cbb_Modelo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbb_Modelo.setBackground(Color.WHITE);
        cbb_Modelo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Modelo m) setText(m.getNome() + " (" + m.getAno() + ")");
                return this;
            }
        });
        JPanel pnl_CompModelo = criarContainerVertical(lbl_Modelo, cbb_Modelo);

        pnl_Linha2.add(pnl_CompAno, BorderLayout.WEST);
        pnl_Linha2.add(pnl_CompModelo, BorderLayout.CENTER);

        // --- Linha 3: Placa ---
        JPanel pnl_Linha3 = new JPanel(new BorderLayout(15, 0));
        pnl_Linha3.setOpaque(false);

        lbl_Placa = criarLabelCampo("Placa *");
        txt_Placa = criarTextFieldInput();
        JPanel pnl_CompPlaca = criarContainerVertical(lbl_Placa, txt_Placa);

        pnl_Linha3.add(pnl_CompPlaca, BorderLayout.CENTER);

        pnl_FormularioVeiculo.add(pnl_Linha1);
        pnl_FormularioVeiculo.add(pnl_Linha2);
        pnl_FormularioVeiculo.add(pnl_Linha3);

        // --- Botão de Envio ---
        btn_CadastrarVeiculo = new JButton("CADASTRAR VEÍCULO");
        btn_CadastrarVeiculo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_CadastrarVeiculo.setForeground(Color.WHITE);
        btn_CadastrarVeiculo.setBackground(Color.decode("#FF9900"));
        btn_CadastrarVeiculo.setPreferredSize(new Dimension(220, 45));
        btn_CadastrarVeiculo.setFocusPainted(false);
        btn_CadastrarVeiculo.setBorderPainted(false);
        btn_CadastrarVeiculo.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPagina, BorderLayout.NORTH);

        JPanel pnl_TipoOwner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
        pnl_TipoOwner.setOpaque(false);
        pnl_TipoOwner.add(new JLabel("Proprietário:") {{
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.decode("#333333"));
        }});
        pnl_TipoOwner.add(rdb_ProprietarioCliente);
        pnl_TipoOwner.add(rdb_ProprietarioFuncionario);

        rdb_ProprietarioCliente.addActionListener(e ->
            ((CardLayout) pnl_ProprietarioSwitch.getLayout()).show(pnl_ProprietarioSwitch, "CLI"));
        rdb_ProprietarioFuncionario.addActionListener(e ->
            ((CardLayout) pnl_ProprietarioSwitch.getLayout()).show(pnl_ProprietarioSwitch, "FUNC"));

        JPanel pnl_CorpoCard = new JPanel(new BorderLayout(0, 8));
        pnl_CorpoCard.setOpaque(false);

        JPanel pnl_ProprietarioArea = new JPanel(new BorderLayout(0, 4));
        pnl_ProprietarioArea.setOpaque(false);
        pnl_ProprietarioArea.add(pnl_TipoOwner, BorderLayout.NORTH);
        pnl_ProprietarioArea.add(pnl_ProprietarioSwitch, BorderLayout.CENTER);

        pnl_CorpoCard.add(pnl_ProprietarioArea, BorderLayout.NORTH);
        pnl_CorpoCard.add(pnl_FormularioVeiculo, BorderLayout.CENTER);
        pnl_CardCentral.add(pnl_CorpoCard, BorderLayout.CENTER);

        JPanel pnl_ContainerBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        pnl_ContainerBotao.setOpaque(false);
        pnl_ContainerBotao.add(btn_CadastrarVeiculo);
        pnl_CardCentral.add(pnl_ContainerBotao, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    private void carregarClientesTabela() {
        if (controller != null) {
            ArrayList<Cliente> clientes = controller.listarClientes();
            pnl_SelecaoCliente.atualizarTabela(clientes);
            cmb_Funcionario.removeAllItems();
            for (FuncionarioEntity f : controller.listarFuncionarios())
                cmb_Funcionario.addItem(f);
        }
    }

    /**
     * Popula o ComboBox de montadoras e registra os gatilhos de filtro.
     */
    private void carregarMontadoras() {
        List<Montadora> lista = controller.montadorasComModelos();
        for (Montadora m : lista) cbb_Montadora.addItem(m);

        cbb_Montadora.addActionListener(e -> atualizarModelos());
        cbb_TipoVeiculo.addActionListener(e -> atualizarModelos());

        atualizarModelos();
    }

    /**
     * Filtra o ComboBox de Modelos pela montadora selecionada, tipo e ano.
     * Tipo e ano são filtros de UI — não são salvos no veículo.
     */
    private void atualizarModelos() {
        cbb_Modelo.removeAllItems();
        Montadora montadora = (Montadora) cbb_Montadora.getSelectedItem();
        if (montadora == null) return;

        String tipoFiltro = (String) cbb_TipoVeiculo.getSelectedItem();
        boolean filtrarTipo = tipoFiltro != null && !tipoFiltro.equals("Todos");
        String anoTexto = txt_Ano.getText().trim();

        for (Modelo modelo : montadora.listarModelos()) {
            boolean tipoOk = !filtrarTipo || tipoFiltro.equalsIgnoreCase(modelo.getTipo());
            boolean anoOk = anoTexto.isEmpty() || String.valueOf(modelo.getAno()).startsWith(anoTexto);
            if (tipoOk && anoOk) cbb_Modelo.addItem(modelo);
        }
    }

    // =========================================================================
    // FILTROS E VALIDAÇÃO
    // =========================================================================
    private void aplicarFiltros() {
        ((AbstractDocument) txt_Placa.getDocument()).setDocumentFilter(new FiltroPlaca());
        ((AbstractDocument) txt_Ano.getDocument()).setDocumentFilter(new FiltroAno());
        txt_Ano.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarModelos(); }
            public void removeUpdate(DocumentEvent e) { atualizarModelos(); }
            public void changedUpdate(DocumentEvent e) {}
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

    private void vincularAcoes() {
        btn_CadastrarVeiculo.addActionListener(e -> {
            limparErro(txt_Placa);

            Montadora montadora = (Montadora) cbb_Montadora.getSelectedItem();
            Modelo modeloSelecionado = (Modelo) cbb_Modelo.getSelectedItem();
            String placa = txt_Placa.getText().trim();

            boolean ok = true;
            StringBuilder erros = new StringBuilder();

            if (montadora == null || modeloSelecionado == null) {
                erros.append("• Selecione uma Montadora e um Modelo.\n");
                ok = false;
            }

            if (placa.isEmpty() || placa.length() < 7) {
                marcarErro(txt_Placa);
                erros.append("• Placa inválida (mínimo 7 caracteres alfanuméricos).\n");
                ok = false;
            }

            // Validação de proprietário feita no bloco try abaixo

            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "Corrija os problemas abaixo:\n\n" + erros,
                    "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                long idDono;
                String nomeDono;

                if (rdb_ProprietarioCliente.isSelected()) {
                    JTable tbl = pnl_SelecaoCliente.getTbl_Clientes();
                    int linhaSelecionada = tbl.getSelectedRow();
                    if (linhaSelecionada == -1) {
                        JOptionPane.showMessageDialog(this,
                            "Corrija os problemas abaixo:\n\n• Selecione o cliente proprietário na tabela.\n",
                            "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    idDono = Long.parseLong(tbl.getValueAt(linhaSelecionada, 0).toString().trim());
                    nomeDono = tbl.getValueAt(linhaSelecionada, 1).toString();
                } else {
                    FuncionarioEntity func = (FuncionarioEntity) cmb_Funcionario.getSelectedItem();
                    if (func == null || func.getIdUsuario() == null) {
                        JOptionPane.showMessageDialog(this,
                            "Corrija os problemas abaixo:\n\n• Selecione o funcionário proprietário.\n",
                            "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    idDono = func.getIdUsuario();
                    nomeDono = func.getNome() + " (Funcionário)";
                }

                controller.salvarVeiculo(modeloSelecionado, placa, idDono);

                JOptionPane.showMessageDialog(this,
                    "Veículo cadastrado com sucesso para: " + nomeDono,
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                limparCampos();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar veículo: " + ex.getMessage(),
                    "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void limparCampos() {
        if (cbb_Montadora.getItemCount() > 0) cbb_Montadora.setSelectedIndex(0);
        cbb_TipoVeiculo.setSelectedIndex(0);
        txt_Ano.setText("");
        txt_Placa.setText("");
        pnl_SelecaoCliente.getTbl_Clientes().clearSelection();
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE ELEMENTOS VISUAIS
    // =========================================================================
    private JPanel criarContainerVertical(JLabel label, Component comp) {
        JPanel container = new JPanel(new BorderLayout(0, 4));
        container.setOpaque(false);
        container.add(label, BorderLayout.NORTH);
        container.add(comp, BorderLayout.CENTER);
        return container;
    }

    private JLabel criarLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(Color.decode("#333333"));
        return label;
    }

    private JTextField criarTextFieldInput() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        return field;
    }

    // =========================================================================
    // INNER CLASSES — DocumentFilter
    // =========================================================================

    /** Permite apenas alfanuméricos em maiúsculo, limite de 8 chars (placa). */
    private static class FiltroPlaca extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.toUpperCase().replaceAll("[^A-Z0-9]", "") : "";
            int espaço = 8 - fb.getDocument().getLength();
            if (espaço > 0) super.insertString(fb, off, novo.substring(0, Math.min(novo.length(), espaço)), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.toUpperCase().replaceAll("[^A-Z0-9]", "") : "";
            int novoTam = fb.getDocument().getLength() - len + novo.length();
            if (novoTam <= 8) super.replace(fb, off, len, novo, attr);
            else {
                int espaço = 8 - (fb.getDocument().getLength() - len);
                if (espaço > 0) super.replace(fb, off, len, novo.substring(0, espaço), attr);
            }
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
        private final Color corBorda;
        RoundedBorder(int raio, Color corBorda) { this.raio = raio; this.corBorda = corBorda; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(corBorda);
            g2d.draw(new RoundRectangle2D.Double(x, y, width-1, height-1, raio, raio));
            g2d.dispose();
        }
    }
}
