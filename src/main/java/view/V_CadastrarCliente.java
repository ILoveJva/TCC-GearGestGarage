package view;

import controller.OficinaController;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Interface Corrigida para o Cadastro de Clientes (JPanel).
 * Interage diretamente com o OficinaController para persistência em memória.
 */
public class V_CadastrarCliente extends JPanel {

    // =========================================================================
    // DECLARAÇÃO DOS COMPONENTES
    // =========================================================================
    private JPanel pnl_CardCentral;
    private JPanel pnl_Formulario;
    private JLabel lbl_TituloPaginacao;

    private JLabel lbl_Nome, lbl_CPF, lbl_Celular, lbl_Email, lbl_CEP, lbl_Numero, lbl_Endereco, lbl_Complemento;
    private JTextField txt_Nome, txt_CPF, txt_Celular, txt_Email, txt_CEP, txt_Numero, txt_Endereco, txt_Complemento;
    private JButton btn_Cadastrar;

    // Link com o Controller do Sistema
    private OficinaController controller;

    /**
     * Construtor modificado para receber o Controller.
     */
    public V_CadastrarCliente(OficinaController controller) {
        this.controller = controller;

        // Centraliza o cartão no meio da área útil do Frame Principal
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        initComponents();
        layoutComponents();
        vincularAcoes(); // Ativa o escutador do botão Salvar
    }

    private void initComponents() {
        // Container do Cartão Central (Trava o tamanho máximo do formulário)
        pnl_CardCentral = new JPanel(new BorderLayout(0, 20));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 520));

        // Título Superior
        lbl_TituloPaginacao = new JLabel("Página Inicial > Cadastrar Cliente");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(Color.decode("#4D4D4D"));

        // Painel do Formulário Interno (Campos organizados em Grid)
        pnl_Formulario = new JPanel(new GridLayout(4, 2, 25, 15));
        pnl_Formulario.setBackground(Color.WHITE);

        // Inicialização dos Campos e Labels
        lbl_Nome = criarLabelCampo("Nome completo *");
        txt_Nome = criarTextFieldInput();

        lbl_CPF = criarLabelCampo("CPF *");
        txt_CPF = criarTextFieldInput();

        lbl_Celular = criarLabelCampo("Celular / Telefone *");
        txt_Celular = criarTextFieldInput();

        lbl_Email = criarLabelCampo("E-mail *");
        txt_Email = criarTextFieldInput();

        lbl_CEP = criarLabelCampo("CEP");
        txt_CEP = criarTextFieldInput();

        lbl_Numero = criarLabelCampo("Número");
        txt_Numero = criarTextFieldInput();

        lbl_Endereco = criarLabelCampo("Endereço");
        txt_Endereco = criarTextFieldInput();

        lbl_Complemento = criarLabelCampo("Complemento");
        txt_Complemento = criarTextFieldInput();

        // Adiciona os componentes em pares (Label em cima, Input embaixo no fluxo lógico do Grid)
        pnl_Formulario.add(criarContainerVerticalCampo(lbl_Nome, txt_Nome));
        pnl_Formulario.add(criarContainerVerticalCampo(lbl_CPF, txt_CPF));
        pnl_Formulario.add(criarContainerVerticalCampo(lbl_Celular, txt_Celular));
        pnl_Formulario.add(criarContainerVerticalCampo(lbl_Email, txt_Email));
        pnl_Formulario.add(criarContainerVerticalCampo(lbl_CEP, txt_CEP));
        pnl_Formulario.add(criarContainerVerticalCampo(lbl_Numero, txt_Numero));
        pnl_Formulario.add(criarContainerVerticalCampo(lbl_Endereco, txt_Endereco));
        pnl_Formulario.add(criarContainerVerticalCampo(lbl_Complemento, txt_Complemento));

        // Botão de Confirmação Final
        btn_Cadastrar = new JButton("CADASTRAR CLIENTE");
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cadastrar.setForeground(Color.WHITE);
        btn_Cadastrar.setBackground(Color.decode("#FF9900")); // Laranja de Destaque
        btn_Cadastrar.setPreferredSize(new Dimension(220, 45));
        btn_Cadastrar.setFocusPainted(false);
        btn_Cadastrar.setBorderPainted(false);
        btn_Cadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Formulario, BorderLayout.CENTER);

        // Alinha o botão à direita usando um painel auxiliar de fluxo
        JPanel pnl_ContainerBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnl_ContainerBotao.setOpaque(false);
        pnl_ContainerBotao.add(btn_Cadastrar);
        pnl_CardCentral.add(pnl_ContainerBotao, BorderLayout.SOUTH);

        // Restrição do GridBagLayout para manter o painel centralizado e sem distorções na View
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        add(pnl_CardCentral, gbc);
    }

    /**
     * Vincula a ação de clique enviando os dados capturados para o Controller,
     * respeitando os atributos herdados da classe Usuario.
     */
    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            String nome = txt_Nome.getText().trim();
            String cpf = txt_CPF.getText().trim();
            String celular = txt_Celular.getText().trim();
            String email = txt_Email.getText().trim();

            // Validação estrita de campos obrigatórios
            if (nome.isEmpty() || cpf.isEmpty() || celular.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, preencha todos os campos obrigatórios (*).",
                        "Campos Pendentes",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Envia os dados para o Controller instanciar e guardar o model.Cliente
            if (controller != null) {
                controller.salvarCliente(nome, cpf, celular, email);

                JOptionPane.showMessageDialog(this,
                        "Cliente \"" + nome + "\" cadastrado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);

                limparCamposFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro: O controlador do sistema não foi localizado.",
                        "Erro de Link de Dados",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void limparCamposFormulario() {
        txt_Nome.setText("");
        txt_CPF.setText("");
        txt_Celular.setText("");
        txt_Email.setText("");
        txt_CEP.setText("");
        txt_Numero.setText("");
        txt_Endereco.setText("");
        txt_Complemento.setText("");
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE ESTILIZAÇÃO (UI FACTORY)
    // =========================================================================
    private JPanel criarContainerVerticalCampo(JLabel label, JTextField field) {
        JPanel container = new JPanel(new BorderLayout(0, 4));
        container.setOpaque(false);
        container.add(label, BorderLayout.NORTH);
        container.add(field, BorderLayout.CENTER);
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
        field.setPreferredSize(new Dimension(100, 36));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(6, Color.decode("#CCCCCC")),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        return field;
    }

    /**
     * Classe Interna para renderização de bordas arredondadas nos campos.
     */
    private static class RoundedBorder implements javax.swing.border.Border {
        private int raio;
        private Color corBorda;

        RoundedBorder(int raio, Color corBorda) {
            this.raio = raio;
            this.corBorda = corBorda;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.raio / 2, this.raio / 2, this.raio / 2, this.raio / 2);
        }

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