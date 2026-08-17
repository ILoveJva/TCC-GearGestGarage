package view;

import br.com.oficina.usuario.FuncionarioEntity;
import controller.OficinaController;
import model.Cliente;
import model.Modelo;
import model.Montadora;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
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
    // PALETA E MEDIDAS — mexa só aqui para alterar tudo de uma vez
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#F5F7FA");
    private static final Color COR_CARD_TOPO    = Color.decode("#FFFFFF");
    private static final Color COR_CARD_BASE    = Color.decode("#EEF2F7");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    // Cores do popup da lista suspensa — precisa ser SÓLIDO (o vidro é só na caixa fechada)
    private static final Color COR_POPUP_FUNDO   = Color.decode("#FFFFFF");
    private static final Color COR_POPUP_SELECAO = Color.decode("#FFE4BF");
    private static final Color COR_POPUP_BORDA   = Color.decode("#C3CDDA");

    // Cor de ação (tema original preservado) e suas variações de hover/pressionado
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    // Vidro azulado estilo Windows 7 (Aero) — usado no cabeçalho da tabela
    private static final Color COR_AERO_TOPO_A   = Color.decode("#F2F9FE");
    private static final Color COR_AERO_TOPO_B   = Color.decode("#DCEEFB");
    private static final Color COR_AERO_BASE_A   = Color.decode("#C2E1F7");
    private static final Color COR_AERO_BASE_B   = Color.decode("#E2F1FC");
    private static final Color COR_AERO_BORDA    = Color.decode("#8FBFE0");
    private static final Color COR_AERO_SEPARA   = Color.decode("#B4D6EE");
    private static final Color COR_AERO_TEXTO    = Color.decode("#1F3A52");

    // Tabela de dados
    private static final Color COR_TABELA_FUNDO     = Color.WHITE;
    private static final Color COR_TABELA_GRADE     = Color.decode("#E3E9F0");
    private static final Color COR_TABELA_SELECAO   = Color.decode("#FFE4BF");

    private static final int RAIO_COMPONENTE     = 12;   // arredondamento compartilhado
    private static final int TAMANHO_FONTE_LABEL = 20;
    private static final int TAMANHO_FONTE_CAMPO = 12;
    private static final int ALTURA_CAMPO        = 34;
    private static final int TAMANHO_FONTE_BOTAO = 14;
    private static final int LARGURA_BOTAO       = 220;
    private static final int ALTURA_BOTAO        = 44;
    private static final int ALTURA_CABECALHO    = 32;
    private static final int ALTURA_LINHA_TABELA = 26;

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

    private GlassTextField txt_Ano, txt_Placa;

    private BotaoAcao btn_CadastrarVeiculo;

    private final OficinaController controller;

    public V_CadastrarVeiculo(OficinaController controller) {
        this.controller = controller;

        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);

        initComponents();
        layoutComponents();

        carregarClientesTabela();
        carregarMontadoras();
        aplicarFiltros();
        vincularAcoes();

        estilizarTabelaClientes();
    }

    private void initComponents() {
        pnl_CardCentral = new PainelGradiente(new BorderLayout(0, 15), COR_CARD_TOPO, COR_CARD_BASE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 520));
        pnl_CardCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lbl_TituloPagina = new JLabel("Página Inicial > Cadastrar Veículo");
        lbl_TituloPagina.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPagina.setForeground(COR_TITULO);

        pnl_SelecaoCliente = new Pnl_SelecaoCliente();
        pnl_SelecaoCliente.setPreferredSize(new Dimension(680, 160));

        rdb_ProprietarioCliente = new JRadioButton("Cliente", true);
        rdb_ProprietarioFuncionario = new JRadioButton("Funcionário");
        rdb_ProprietarioCliente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_ProprietarioFuncionario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_ProprietarioCliente.setForeground(COR_LABEL);
        rdb_ProprietarioFuncionario.setForeground(COR_LABEL);
        rdb_ProprietarioCliente.setOpaque(false);
        rdb_ProprietarioFuncionario.setOpaque(false);
        rdb_ProprietarioCliente.setFocusPainted(false);
        rdb_ProprietarioFuncionario.setFocusPainted(false);
        rdb_ProprietarioCliente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rdb_ProprietarioFuncionario.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ButtonGroup grpProprietario = new ButtonGroup();
        grpProprietario.add(rdb_ProprietarioCliente);
        grpProprietario.add(rdb_ProprietarioFuncionario);

        cmb_Funcionario = new GlassComboBox<>();
        cmb_Funcionario.setPreferredSize(new Dimension(300, ALTURA_CAMPO));
        cmb_Funcionario.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof FuncionarioEntity fe) setText(fe.getNome() + " — " + fe.getCargo());
                return this;
            }
        });

        JPanel pnl_FuncContainer = new JPanel(new BorderLayout());
        pnl_FuncContainer.setOpaque(false);
        JLabel lbl_FuncTitulo = criarLabelCampo("Selecione o Funcionário Proprietário:");
        pnl_FuncContainer.add(lbl_FuncTitulo, BorderLayout.NORTH);

        JPanel pnl_FuncWrapper = new JPanel(new BorderLayout());
        pnl_FuncWrapper.setOpaque(false);
        pnl_FuncWrapper.add(cmb_Funcionario, BorderLayout.NORTH);
        pnl_FuncContainer.add(pnl_FuncWrapper, BorderLayout.CENTER);
        pnl_FuncContainer.setPreferredSize(new Dimension(680, 160));

        pnl_ProprietarioSwitch = new JPanel(new CardLayout());
        pnl_ProprietarioSwitch.setOpaque(false);
        pnl_ProprietarioSwitch.add(pnl_SelecaoCliente, "CLI");
        pnl_ProprietarioSwitch.add(pnl_FuncContainer, "FUNC");

        pnl_FormularioVeiculo = new JPanel(new GridLayout(3, 1, 0, 10));
        pnl_FormularioVeiculo.setOpaque(false);

        // --- Linha 1: Montadora / Tipo (filtro) ---
        JPanel pnl_Linha1 = new JPanel(new BorderLayout(15, 0));
        pnl_Linha1.setOpaque(false);

        lbl_Montadora = criarLabelCampo("Montadora *");
        cbb_Montadora = new GlassComboBox<>();
        cbb_Montadora.setPreferredSize(new Dimension(200, ALTURA_CAMPO));
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
        cbb_TipoVeiculo = new GlassComboBox<>(TIPOS_VEICULO);
        cbb_TipoVeiculo.setPreferredSize(new Dimension(150, ALTURA_CAMPO));
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
        cbb_Modelo = new GlassComboBox<>();
        cbb_Modelo.setPreferredSize(new Dimension(200, ALTURA_CAMPO));
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
        btn_CadastrarVeiculo = new BotaoAcao("CADASTRAR VEÍCULO");
        btn_CadastrarVeiculo.setPreferredSize(new Dimension(LARGURA_BOTAO, ALTURA_BOTAO));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPagina, BorderLayout.NORTH);

        JPanel pnl_TipoOwner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
        pnl_TipoOwner.setOpaque(false);
        pnl_TipoOwner.add(criarLabelCampo("Proprietário:"));
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

    // =========================================================================
    // ESTILO DA TABELA DE CLIENTES
    // =========================================================================
    /**
     * Aplica o visual da tabela sem tocar no Pnl_SelecaoCliente:
     * cabeçalho em vidro azulado (estilo Aero do Windows 7) e células
     * de dados com fundo branco.
     */
    private void estilizarTabelaClientes() {
        JTable tabela = pnl_SelecaoCliente.getTbl_Clientes();
        if (tabela == null) return;

        // --- Células de dados: fundo branco ---
        tabela.setBackground(COR_TABELA_FUNDO);
        tabela.setForeground(COR_TEXTO_CAMPO);
        tabela.setOpaque(true);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(ALTURA_LINHA_TABELA);
        tabela.setShowGrid(true);
        tabela.setGridColor(COR_TABELA_GRADE);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(COR_TABELA_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO_CAMPO);
        tabela.setFillsViewportHeight(true);
        tabela.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());

        // --- Cabeçalho: vidro azulado estilo Windows 7 ---
        JTableHeader cabecalho = tabela.getTableHeader();
        if (cabecalho != null) {
            cabecalho.setDefaultRenderer(new CabecalhoVidroAzul());
            cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, ALTURA_CABECALHO));
            cabecalho.setReorderingAllowed(false);
            cabecalho.setOpaque(false);
        }

        // O viewport também precisa ficar branco para não vazar o fundo do card
        JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, tabela);
        if (scroll != null) {
            scroll.getViewport().setBackground(COR_TABELA_FUNDO);
            scroll.getViewport().setOpaque(true);
            scroll.setOpaque(false);
            scroll.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        }
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

    private void marcarErro(GlassTextField field) {
        field.setEstadoErro(true);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                limparErro(field);
                field.removeFocusListener(this);
            }
        });
    }

    private void limparErro(GlassTextField field) {
        field.setEstadoErro(false);
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
        label.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL));
        label.setForeground(COR_LABEL);
        return label;
    }

    private GlassTextField criarTextFieldInput() {
        GlassTextField field = new GlassTextField();
        field.setPreferredSize(new Dimension(150, ALTURA_CAMPO));
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

    // =========================================================================
    // INNER CLASSES — RENDERERS DA TABELA
    // =========================================================================

    /**
     * Cabeçalho de coluna com vidro azulado no estilo Aero (Windows 7):
     * gradiente claro na metade superior, gradiente azul mais saturado na
     * metade inferior, brilho de vidro no topo, separador entre colunas e
     * linha de base mais escura.
     */
    private static class CabecalhoVidroAzul extends JLabel implements TableCellRenderer {

        CabecalhoVidroAzul() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(COR_AERO_TEXTO);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int meio = h / 2;

            // Metade superior: vidro claro
            g2.setPaint(new GradientPaint(0, 0, COR_AERO_TOPO_A, 0, meio, COR_AERO_TOPO_B));
            g2.fillRect(0, 0, w, meio);

            // Metade inferior: azul mais saturado, subindo de volta ao claro
            g2.setPaint(new GradientPaint(0, meio, COR_AERO_BASE_A, 0, h, COR_AERO_BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            // Brilho de vidro no topo (o "gloss" característico do Aero)
            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillRect(0, 0, w, Math.max(1, h / 5));

            // Separador vertical entre colunas
            g2.setColor(COR_AERO_SEPARA);
            g2.drawLine(w - 1, 3, w - 1, h - 4);

            // Linha de base, delimitando o cabeçalho dos dados
            g2.setColor(COR_AERO_BORDA);
            g2.drawLine(0, h - 1, w, h - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Células de dados sempre com fundo branco (e destaque no tom do tema quando selecionadas). */
    private static class CelulaBrancaRenderer extends DefaultTableCellRenderer {
        CelulaBrancaRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(isSelected ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
            setForeground(COR_TEXTO_CAMPO);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    // =========================================================================
    // INNER CLASSES — COMPONENTES EM VIDRO
    // =========================================================================

    /**
     * Campo de texto com efeito de vidro translúcido (glassmorphism):
     * preenchimento em gradiente semi-transparente, reflexo sutil no topo,
     * sombra suave e borda que reage a foco/erro.
     */
    private static class GlassTextField extends JTextField {
        private boolean focado = false;
        private boolean erro = false;

        GlassTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        void setEstadoErro(boolean valor) {
            this.erro = valor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, erro ? 225 : 210),
                    0, h, new Color(255, 255, 255, erro ? 175 : 145)
            );
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda;
            float espessura;
            if (erro) {
                corBorda = new Color(214, 58, 68, 220);
                espessura = 1.6f;
            } else if (focado) {
                corBorda = new Color(255, 153, 0, 210);
                espessura = 1.6f;
            } else {
                corBorda = new Color(160, 175, 195, 130);
                espessura = 1f;
            }
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /** Painel com fundo em gradiente suave, harmonizando o cartão com os campos em vidro. */
    private static class PainelGradiente extends JPanel {
        private final Color corTopo;
        private final Color corBase;

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase) {
            super(layout);
            this.corTopo = corTopo;
            this.corBase = corBase;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, corTopo, 0, getHeight(), corBase);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botão de ação com a mesma linguagem visual dos campos em vidro. */
    private static class BotaoAcao extends JButton {
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto) {
            super(texto);
            setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_BOTAO));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)   { sobreMouse = false; repaint(); }
                @Override public void mousePressed(MouseEvent e)  { pressionado = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(180, 100, 0, 60));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? COR_ACAO_ESCURA : (sobreMouse ? COR_ACAO_CLARA : COR_ACAO);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** JComboBox com o mesmo efeito de vidro dos campos de texto. */
    private static class GlassComboBox<T> extends JComboBox<T> {
        private boolean focado = false;

        GlassComboBox() { super(); estilizar(); }
        GlassComboBox(T[] itens) { super(itens); estilizar(); }

        private void estilizar() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            // Não usar cor transparente: o BasicComboPopup copia este background
            // para a lista suspensa, e um alpha 0 deixaria o popup transparente.
            setBackground(COR_POPUP_FUNDO);
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 4));
            setFocusable(true);
            setUI(new GlassComboBoxUI());
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 210),
                    0, h, new Color(255, 255, 255, 145)
            );
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130);
            float espessura = focado ? 1.6f : 1f;
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /**
     * UI do GlassComboBox: impede que o Swing pinte fundo sólido (inclusive o
     * azul de seleção quando em foco) por cima do vidro, troca a seta padrão
     * por um triângulo vetorial e usa um popup arredondado.
     */
    private static class GlassComboBoxUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Vazio de propósito — o fundo já é pintado em GlassComboBox.paintComponent().
        }

        @Override
        @SuppressWarnings("unchecked")
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            ListCellRenderer renderer = comboBox.getRenderer();
            Component c = renderer.getListCellRendererComponent(
                    listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setFont(comboBox.getFont());
            c.setForeground(comboBox.isEnabled() ? COR_TEXTO_CAMPO : Color.GRAY);

            // O renderer é COMPARTILHADO com as linhas do popup: desliga o opaque
            // apenas durante esta pintura e restaura em seguida.
            boolean opacoOriginal = false;
            if (c instanceof JComponent) {
                opacoOriginal = ((JComponent) c).isOpaque();
                ((JComponent) c).setOpaque(false);
            }

            boolean shouldValidate = c instanceof JPanel;
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate);

            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(opacoOriginal);
            }
        }

        @Override
        protected ComboPopup createPopup() {
            return new GlassComboPopup(comboBox);
        }

        @Override
        protected JButton createArrowButton() {
            JButton seta = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    int cx = w / 2, cy = h / 2;
                    Path2D triangulo = new Path2D.Double();
                    triangulo.moveTo(cx - 4, cy - 2);
                    triangulo.lineTo(cx + 4, cy - 2);
                    triangulo.lineTo(cx, cy + 3);
                    triangulo.closePath();
                    g2.setColor(COR_LABEL);
                    g2.fill(triangulo);
                    g2.dispose();
                }
            };
            seta.setPreferredSize(new Dimension(22, 22));
            seta.setContentAreaFilled(false);
            seta.setBorderPainted(false);
            seta.setFocusPainted(false);
            seta.setOpaque(false);
            seta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return seta;
        }
    }

    /** Popup do combo com cantos arredondados e conteúdo sólido/legível. */
    private static class GlassComboPopup extends BasicComboPopup {

        GlassComboPopup(JComboBox<Object> combo) {
            super(combo);
        }

        @Override
        protected void configurePopup() {
            super.configurePopup();
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }

        @Override
        protected void configureList() {
            super.configureList();
            list.setOpaque(true);
            list.setBackground(COR_POPUP_FUNDO);
            list.setForeground(COR_TEXTO_CAMPO);
            list.setSelectionBackground(COR_POPUP_SELECAO);
            list.setSelectionForeground(COR_TEXTO_CAMPO);
            list.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
        }

        @Override
        protected JScrollPane createScroller() {
            JScrollPane scroller = super.createScroller();
            scroller.setOpaque(false);
            scroller.getViewport().setOpaque(false);
            scroller.setBorder(BorderFactory.createEmptyBorder());
            return scroller;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(COR_POPUP_FUNDO);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(COR_POPUP_BORDA);
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void show() {
            super.show();
            // Recorta a janela do popup no formato arredondado. Em plataformas sem
            // suporte a janelas com formato, segue normalmente com cantos retos.
            try {
                Window janela = SwingUtilities.getWindowAncestor(this);
                if (janela != null) {
                    janela.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RAIO_COMPONENTE, RAIO_COMPONENTE));
                }
            } catch (Exception | Error ignorado) {
                // Sem suporte a formato de janela nesta plataforma.
            }
        }
    }
}