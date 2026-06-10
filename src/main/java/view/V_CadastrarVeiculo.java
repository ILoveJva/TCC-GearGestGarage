package view;

import controller.OficinaController;
import database.DB_Montadora;
import model.Cliente;
import model.Modelo;
import model.Montadora;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface Corrigida e Integrada para Cadastro de Veículo.
 * Utiliza ComboBox dinâmicos para Montadora e Modelo.
 */
public class V_CadastrarVeiculo extends JPanel {

    // =========================================================================
    // DECLARAÇÃO DOS COMPONENTES
    // =========================================================================
    private JPanel pnl_CardCentral;
    private JLabel lbl_TituloPagina;

    // Painel de Seleção do Cliente
    private Pnl_SelecaoCliente pnl_SelecaoCliente;

    // Painel do Formulário do Veículo
    private JPanel pnl_FormularioVeiculo;
    private JLabel lbl_Montadora, lbl_TipoVeiculo, lbl_Modelo, lbl_Ano, lbl_Placa, lbl_Vin;

    // Componentes atualizados para JComboBox
    private JComboBox<Montadora> cbb_Montadora;
    private JComboBox<Modelo> cbb_Modelo;
    private JComboBox<String> cbb_TipoVeiculo;

    private JTextField txt_Ano, txt_Placa, txt_Vin;

    // Rodapé
    private JButton btn_CadastrarVeiculo;

    // Referência do Controller do Sistema
    private OficinaController controller;

    /**
     * Construtor recebe a injeção do Controller unificado.
     */
    public V_CadastrarVeiculo(OficinaController controller) {
        this.controller = controller;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        initComponents();
        layoutComponents();

        carregarClientesTabela(); // Popula a tabela com os clientes cadastrados
        carregarMontadoras();     // Popula o ComboBox de Montadoras
        vincularAcoes();          // Ativa o ouvinte do botão cadastrar
    }

    private void initComponents() {
        pnl_CardCentral = new JPanel(new BorderLayout(0, 15));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 560));

        lbl_TituloPagina = new JLabel("Página Inicial > Cadastrar Veículo");
        lbl_TituloPagina.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPagina.setForeground(Color.decode("#4D4D4D"));

        pnl_SelecaoCliente = new Pnl_SelecaoCliente();
        pnl_SelecaoCliente.setPreferredSize(new Dimension(680, 180));

        pnl_FormularioVeiculo = new JPanel(new GridLayout(3, 1, 0, 10));
        pnl_FormularioVeiculo.setBackground(Color.WHITE);

        // --- Linha 1: Montadora / Tipo ---
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
                if (value instanceof Montadora) {
                    setText(((Montadora) value).getNome());
                }
                return this;
            }
        });
        JPanel pnl_CompMontadora = criarContainerVertical(lbl_Montadora, cbb_Montadora);

        lbl_TipoVeiculo = criarLabelCampo("Tipo *");
        String[] tipos = {"Carro", "Moto", "Caminhão", "Van"};
        cbb_TipoVeiculo = new JComboBox<>(tipos);
        cbb_TipoVeiculo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbb_TipoVeiculo.setBackground(Color.WHITE);
        cbb_TipoVeiculo.setPreferredSize(new Dimension(140, 36));
        JPanel pnl_CompTipo = criarContainerVertical(lbl_TipoVeiculo, cbb_TipoVeiculo);

        pnl_Linha1.add(pnl_CompMontadora, BorderLayout.CENTER);
        pnl_Linha1.add(pnl_CompTipo, BorderLayout.EAST);

        // --- Linha 2: Ano / Modelo ---
        JPanel pnl_Linha2 = new JPanel(new BorderLayout(15, 0));
        pnl_Linha2.setOpaque(false);

        lbl_Ano = criarLabelCampo("Ano *");
        txt_Ano = criarTextFieldInput();
        JPanel pnl_CompAno = criarContainerVertical(lbl_Ano, txt_Ano);
        pnl_CompAno.setPreferredSize(new Dimension(140, 55));

        lbl_Modelo = criarLabelCampo("Modelo *");
        cbb_Modelo = new JComboBox<>();
        cbb_Modelo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbb_Modelo.setBackground(Color.WHITE);
        cbb_Modelo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Modelo) {
                    setText(((Modelo) value).getNome());
                }
                return this;
            }
        });
        JPanel pnl_CompModelo = criarContainerVertical(lbl_Modelo, cbb_Modelo);

        pnl_Linha2.add(pnl_CompAno, BorderLayout.WEST);
        pnl_Linha2.add(pnl_CompModelo, BorderLayout.CENTER);

        // --- Linha 3: Placa / VIN (Chassi) ---
        JPanel pnl_Linha3 = new JPanel(new BorderLayout(15, 0));
        pnl_Linha3.setOpaque(false);

        lbl_Placa = criarLabelCampo("Placa *");
        txt_Placa = criarTextFieldInput();
        JPanel pnl_CompPlaca = criarContainerVertical(lbl_Placa, txt_Placa);
        pnl_CompPlaca.setPreferredSize(new Dimension(160, 55));

        lbl_Vin = criarLabelCampo("Nº do Chassi (VIN)");
        txt_Vin = criarTextFieldInput();
        JPanel pnl_CompVin = criarContainerVertical(lbl_Vin, txt_Vin);

        pnl_Linha3.add(pnl_CompPlaca, BorderLayout.WEST);
        pnl_Linha3.add(pnl_CompVin, BorderLayout.CENTER);

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

        JPanel pnl_CorpoCard = new JPanel(new BorderLayout(0, 15));
        pnl_CorpoCard.setOpaque(false);
        pnl_CorpoCard.add(pnl_SelecaoCliente, BorderLayout.NORTH);
        pnl_CorpoCard.add(pnl_FormularioVeiculo, BorderLayout.CENTER);
        pnl_CardCentral.add(pnl_CorpoCard, BorderLayout.CENTER);

        JPanel pnl_ContainerBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        pnl_ContainerBotao.setOpaque(false);
        pnl_ContainerBotao.add(btn_CadastrarVeiculo);
        pnl_CardCentral.add(pnl_ContainerBotao, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    /**
     * Busca a lista viva de clientes no controlador.
     */
    private void carregarClientesTabela() {
        if (controller != null) {
            ArrayList<Cliente> clientesDoSistema = controller.listarClientes();
            pnl_SelecaoCliente.atualizarTabela(clientesDoSistema);
        }
    }

    /**
     * Popula o ComboBox de montadoras e define o gatilho para os modelos.
     */
    private void carregarMontadoras() {
        List<Montadora> listaMontadoras = DB_Montadora.listarTodas();
        for (Montadora m : listaMontadoras) {
            cbb_Montadora.addItem(m);
        }

        // Listener para atualizar os modelos sempre que a montadora mudar
        cbb_Montadora.addActionListener(e -> atualizarModelos());

        // Chamada inicial para popular os modelos da primeira montadora selecionada
        atualizarModelos();
    }

    /**
     * Atualiza o ComboBox de Modelos baseado na Montadora selecionada.
     */
    private void atualizarModelos() {
        cbb_Modelo.removeAllItems();
        Montadora montadoraSelecionada = (Montadora) cbb_Montadora.getSelectedItem();

        if (montadoraSelecionada != null) {
            List<Modelo> modelos = montadoraSelecionada.listarModelos();
            if (modelos != null) {
                for (Modelo modelo : modelos) {
                    cbb_Modelo.addItem(modelo);
                }
            }
        }
    }

    private void vincularAcoes() {
        btn_CadastrarVeiculo.addActionListener(e -> {
            Montadora montadora = (Montadora) cbb_Montadora.getSelectedItem();
            Modelo modeloSelecionado = (Modelo) cbb_Modelo.getSelectedItem();
            String placa = txt_Placa.getText().trim();
            String vin = txt_Vin.getText().trim();
            String tipo = cbb_TipoVeiculo.getSelectedItem().toString();
            String anoTexto = txt_Ano.getText().trim();

            if (montadora == null || modeloSelecionado == null || placa.isEmpty() || anoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, preencha todos os campos obrigatórios (*) do veículo e verifique se há Modelos cadastrados.",
                        "Campos Vazios/Inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int linhaSelecionada = pnl_SelecaoCliente.getTbl_Clientes().getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this,
                        "Selecione o Cliente proprietário na tabela antes de salvar.",
                        "Proprietário Requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int ano = Integer.parseInt(anoTexto);

                if (controller != null) {
                    Cliente proprietario = controller.listarClientes().get(linhaSelecionada);
                    long idDono = proprietario.getIdUsuario();

                    // Agora passando o Objeto Modelo no lugar da String
                    controller.salvarVeiculo(modeloSelecionado, tipo, ano, placa);

                    JOptionPane.showMessageDialog(this,
                            "Veículo (Código Proprietário: " + String.format("%05d", idDono) + ") associado com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    limparCampos();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "O campo 'Ano' deve conter um valor numérico válido.",
                        "Formato Inválido", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void limparCampos() {
        if (cbb_Montadora.getItemCount() > 0) {
            cbb_Montadora.setSelectedIndex(0);
        }
        txt_Ano.setText("");
        txt_Placa.setText("");
        txt_Vin.setText("");
        cbb_TipoVeiculo.setSelectedIndex(0);
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

    private static class RoundedBorder implements javax.swing.border.Border {
        private int raio;
        private Color corBorda;

        RoundedBorder(int raio, Color corBorda) {
            this.raio = raio;
            this.corBorda = corBorda;
        }

        public Insets getBorderInsets(Component c) { return new Insets(this.raio/2, this.raio/2, this.raio/2, this.raio/2); }
        public boolean isBorderOpaque() { return false; }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(corBorda);
            g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, raio, raio));
            g2d.dispose();
        }
    }
}