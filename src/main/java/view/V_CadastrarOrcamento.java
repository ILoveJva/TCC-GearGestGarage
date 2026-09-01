package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.estoque.PecaEntity;
import br.com.oficina.usuario.FuncionarioEntity;
import controller.OficinaController;
import model.Cliente;
import model.Veiculo;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class V_CadastrarOrcamento extends JPanel {

    private final OficinaController controller;

    private GlassComboBox<ItemCliente> cmb_Cliente;
    private GlassComboBox<ItemFuncionario> cmb_FuncionarioProprietario;
    private GlassComboBox<ItemVeiculo> cmb_Veiculo;
    private GlassComboBox<ItemFuncionario> cmb_Responsavel;
    private GlassTextArea txt_Reclamacao;

    // Toggle proprietário
    private JRadioButton rdb_PropCliente;
    private JRadioButton rdb_PropFuncionario;
    private JPanel pnl_ProprietarioSwitch;

    // -- Itens de serviço --
    private GlassComboBox<CatalogoServicoEntity> cmb_ItemCatalogo;
    private GlassComboBox<SistemaItem> cmb_FiltroSistema;
    private final List<CatalogoServicoEntity> todosItens = new ArrayList<>();
    private DefaultTableModel mdl_ItensSelecionados;
    private JTable tbl_ItensSelecionados;
    private JLabel lbl_Total;
    private final List<CatalogoServicoEntity> itensSelecionados = new ArrayList<>();
    private final List<Double> valoresItensSelecionados = new ArrayList<>();

    // -- Peças --
    private GlassComboBox<ItemPeca> cmb_Peca;
    private GlassTextField txt_NomeTecnicoPeca;
    private GlassTextField txt_FabricantePeca;
    private DefaultTableModel mdl_PecasSelecionadas;
    private JTable tbl_PecasSelecionadas;
    private final List<Long> pecasSelecionadas = new ArrayList<>();
    private final List<Double> valoresPecasSelecionadas = new ArrayList<>();
    private final List<String> nomesTecnicosPecas = new ArrayList<>();
    private final List<String> fabricantesPecas = new ArrayList<>();

    private BotaoAcao btn_Cadastrar;

    // Paleta harmonizada com o mesmo efeito de vidro usado nas demais telas
    // (V_CadastrarCliente, V_CadastrarVeiculo, V_CadastrarMontadora, V_CadastrarModelo),
    // com o tom acinzentado/corporativo trazido de V_VisualizarServicos para um
    // acabamento mais sério e profissional.
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_LABEL_SUTIL  = Color.decode("#8A94A3");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_BORDA_SUAVE  = new Color(160, 175, 195, 130);

    // Cores do popup da lista suspensa — precisa ser SÓLIDO (o vidro é só na caixa fechada)
    private static final Color COR_POPUP_FUNDO   = Color.decode("#FFFFFF");
    private static final Color COR_POPUP_SELECAO = Color.decode("#FFE4BF");
    private static final Color COR_POPUP_BORDA   = Color.decode("#C3CDDA");

    // Vidro cinza claro estilo Windows 7 (Aero) — cabeçalho das tabelas, igual à V_VisualizarServicos
    private static final Color COR_AERO_TOPO_A  = Color.decode("#FBFBFC");
    private static final Color COR_AERO_TOPO_B  = Color.decode("#ECEEF1");
    private static final Color COR_AERO_BASE_A  = Color.decode("#DADDE2");
    private static final Color COR_AERO_BASE_B  = Color.decode("#EFF1F3");
    private static final Color COR_AERO_BORDA   = Color.decode("#B6BCC4");
    private static final Color COR_AERO_SEPARA  = Color.decode("#CCD1D8");
    private static final Color COR_AERO_TEXTO   = Color.decode("#3A4149");

    // Tabela de dados
    private static final Color COR_TABELA_FUNDO   = Color.WHITE;
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");

    // Cor de ação primária (tema original preservado)
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    // Cores de apoio para as ações secundárias (adicionar item / adicionar peça / remover)
    private static final Color COR_SUCESSO = Color.decode("#28A745");
    private static final Color COR_INFO    = Color.decode("#17A2B8");
    private static final Color COR_PERIGO  = Color.decode("#D63A44");

    // Ajustes rápidos de tipografia/tamanho — mexa só aqui para alterar tudo de uma vez
    private static final int RAIO_COMPONENTE      = 12;
    private static final int TAMANHO_FONTE_TITULO = 14;
    private static final int TAMANHO_FONTE_SECAO  = 15;
    private static final int TAMANHO_FONTE_LABEL  = 13;
    private static final int TAMANHO_FONTE_CAMPO  = 14;
    private static final int ALTURA_CAMPO         = 34;
    private static final int TAMANHO_FONTE_BOTAO  = 16;
    private static final int LARGURA_BOTAO        = 260;
    private static final int ALTURA_BOTAO         = 52;
    private static final int TAMANHO_ICONE_BOTAO  = 24;
    private static final int ALTURA_CABECALHO     = 26;

    public V_CadastrarOrcamento(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        initComponents();
        carregarClientes();
        carregarCatalogo();
        carregarFuncionarios();
        carregarPecas();
        vincularAcoes();
    }

    private void initComponents() {
        JPanel card = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        card.setPreferredSize(new Dimension(680, 720));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel titulo = new JLabel("Página Inicial > Novo Orçamento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_TITULO));
        titulo.setForeground(COR_TITULO);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        cmb_Cliente = criarCombo();
        cmb_Cliente.setRenderer(criarRendererPadrao());

        cmb_FuncionarioProprietario = criarCombo();
        cmb_FuncionarioProprietario.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof ItemFuncionario fi) lbl.setText(fi.funcionario.getNome() + " — " + fi.funcionario.getCargo());
                estilizarCelula(lbl, i, s);
                return lbl;
            }
        });

        cmb_Veiculo = criarCombo();
        cmb_Veiculo.setRenderer(criarRendererPadrao());

        cmb_Responsavel = criarCombo();
        cmb_Responsavel.setRenderer(criarRendererPadrao());

        // Toggle proprietário
        rdb_PropCliente = criarRadio("Cliente", true);
        rdb_PropFuncionario = criarRadio("Funcionário", false);
        ButtonGroup grpProp = new ButtonGroup();
        grpProp.add(rdb_PropCliente);
        grpProp.add(rdb_PropFuncionario);

        pnl_ProprietarioSwitch = new JPanel(new CardLayout());
        pnl_ProprietarioSwitch.setOpaque(false);
        pnl_ProprietarioSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_ProprietarioSwitch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        pnl_ProprietarioSwitch.add(bloco("Cliente *", cmb_Cliente), "CLI");
        pnl_ProprietarioSwitch.add(bloco("Funcionário *", cmb_FuncionarioProprietario), "FUNC");

        rdb_PropCliente.addActionListener(e -> {
            ((CardLayout) pnl_ProprietarioSwitch.getLayout()).show(pnl_ProprietarioSwitch, "CLI");
            atualizarVeiculos();
        });
        rdb_PropFuncionario.addActionListener(e -> {
            ((CardLayout) pnl_ProprietarioSwitch.getLayout()).show(pnl_ProprietarioSwitch, "FUNC");
            atualizarVeiculos();
        });

        txt_Reclamacao = new GlassTextArea(3, 20);

        JPanel pnl_TipoOwner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
        pnl_TipoOwner.setOpaque(false);
        pnl_TipoOwner.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_TipoOwner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lbl_Proprietario = new JLabel("Proprietário:");
        lbl_Proprietario.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL));
        lbl_Proprietario.setForeground(COR_LABEL);
        pnl_TipoOwner.add(lbl_Proprietario);
        pnl_TipoOwner.add(rdb_PropCliente);
        pnl_TipoOwner.add(rdb_PropFuncionario);

        form.add(pnl_TipoOwner);
        form.add(Box.createVerticalStrut(6));
        form.add(pnl_ProprietarioSwitch);
        form.add(Box.createVerticalStrut(12));
        form.add(bloco("Veículo *", cmb_Veiculo));
        form.add(Box.createVerticalStrut(12));
        form.add(bloco("Responsável *", cmb_Responsavel));
        form.add(Box.createVerticalStrut(12));
        form.add(bloco("Reclamação / Relato", envolverEmScroll(txt_Reclamacao), 100));
        form.add(Box.createVerticalStrut(16));
        form.add(criarSecaoItens());
        form.add(Box.createVerticalStrut(16));
        form.add(criarSecaoPecas());

        btn_Cadastrar = new BotaoAcao("CRIAR ORÇAMENTO", new IconeOrcamento(TAMANHO_ICONE_BOTAO, Color.WHITE),
                COR_ACAO, COR_ACAO_CLARA, COR_ACAO_ESCURA);
        btn_Cadastrar.setPreferredSize(new Dimension(LARGURA_BOTAO, ALTURA_BOTAO));

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btn_Cadastrar);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scrollForm);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scrollForm, BorderLayout.CENTER);
        card.add(pnlBtn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(14, 20, 14, 20);
        add(card, gbc);
    }

    // ---- Seção: Itens de Serviço (catálogo) ----
    private JPanel criarSecaoItens() {
        JPanel sec = new PainelSecao(new BorderLayout(0, 8));
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        sec.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel lbl = criarLabelSecao("Itens de Serviço");

        cmb_ItemCatalogo = criarCombo();
        cmb_ItemCatalogo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean focus) {
                JLabel lblItem = (JLabel) super.getListCellRendererComponent(l, v, i, sel, focus);
                if (v instanceof CatalogoServicoEntity c) {
                    String tipo = "REVISAO".equals(c.getTipo()) ? "Revisão" : "Padrão";
                    lblItem.setText(c.getNome() + "  [" + tipo + " – R$ " + String.format("%.2f", c.getValor()) + "]");
                }
                estilizarCelula(lblItem, i, sel);
                return lblItem;
            }
        });

        cmb_FiltroSistema = criarCombo();
        cmb_FiltroSistema.setRenderer(criarRendererPadrao());
        cmb_FiltroSistema.addItem(new SistemaItem("",             "Todos os Sistemas"));
        cmb_FiltroSistema.addItem(new SistemaItem("MOTOR",        "Motor"));
        cmb_FiltroSistema.addItem(new SistemaItem("TRANSMISSAO",  "Transmissão"));
        cmb_FiltroSistema.addItem(new SistemaItem("DIRECAO",      "Direção"));
        cmb_FiltroSistema.addItem(new SistemaItem("SUSPENSAO",    "Suspensão"));
        cmb_FiltroSistema.addItem(new SistemaItem("FREIOS",       "Freios"));
        cmb_FiltroSistema.addItem(new SistemaItem("ARREFECIMENTO","Arrefecimento"));
        cmb_FiltroSistema.addItem(new SistemaItem("ELETRICA",     "Elétrica"));
        cmb_FiltroSistema.addItem(new SistemaItem("ALIMENTACAO",  "Alimentação"));
        cmb_FiltroSistema.addItem(new SistemaItem("OUTROS",       "Outros"));
        cmb_FiltroSistema.addActionListener(e -> filtrarCatalogo());

        JLabel lbl_LocalFiltro = criarLabelMini("Local:");
        lbl_LocalFiltro.setPreferredSize(new Dimension(46, ALTURA_CAMPO));

        JPanel pnl_FiltroRow = new JPanel(new BorderLayout(8, 0));
        pnl_FiltroRow.setOpaque(false);
        pnl_FiltroRow.add(lbl_LocalFiltro, BorderLayout.WEST);
        pnl_FiltroRow.add(cmb_FiltroSistema, BorderLayout.CENTER);

        JButton btn_Add = botaoAcao("+ Adicionar", COR_SUCESSO);
        btn_Add.addActionListener(e -> adicionarItem());

        JPanel pnl_AddRow = new JPanel(new BorderLayout(8, 0));
        pnl_AddRow.setOpaque(false);
        pnl_AddRow.add(cmb_ItemCatalogo, BorderLayout.CENTER);
        pnl_AddRow.add(btn_Add, BorderLayout.EAST);

        JPanel pnl_NorteRows = new JPanel(new GridLayout(2, 1, 0, 6));
        pnl_NorteRows.setOpaque(false);
        pnl_NorteRows.add(pnl_FiltroRow);
        pnl_NorteRows.add(pnl_AddRow);

        String[] cols = {"Serviço", "Tipo", "Valor (R$)"};
        mdl_ItensSelecionados = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 2; }
        };
        mdl_ItensSelecionados.addTableModelListener(ev -> {
            if (ev.getType() != TableModelEvent.UPDATE || ev.getColumn() != 2) return;
            int row = ev.getFirstRow();
            if (row < 0 || row >= valoresItensSelecionados.size()) return;
            try {
                String txt = String.valueOf(mdl_ItensSelecionados.getValueAt(row, 2)).replace(",", ".");
                double novoValor = Double.parseDouble(txt);
                valoresItensSelecionados.set(row, novoValor);
            } catch (NumberFormatException ex) {
                valoresItensSelecionados.set(row, itensSelecionados.get(row).getValor());
                mdl_ItensSelecionados.setValueAt(
                        String.format("%.2f", itensSelecionados.get(row).getValor()), row, 2);
            }
            atualizarTotal();
        });
        tbl_ItensSelecionados = criarTabela(mdl_ItensSelecionados);
        JScrollPane scroll = scrollTabela(tbl_ItensSelecionados, 100);

        JButton btn_Rem = botaoLink("Remover selecionado", COR_PERIGO);
        btn_Rem.addActionListener(e -> {
            int row = tbl_ItensSelecionados.getSelectedRow();
            if (row >= 0) {
                itensSelecionados.remove(row);
                valoresItensSelecionados.remove(row);
                mdl_ItensSelecionados.removeRow(row);
                atualizarTotal();
            }
        });

        lbl_Total = new JLabel("Total: R$ 0,00");
        lbl_Total.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl_Total.setForeground(COR_ACAO_ESCURA);
        lbl_Total.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel pnl_Rodape = new JPanel(new BorderLayout());
        pnl_Rodape.setOpaque(false);
        pnl_Rodape.add(btn_Rem, BorderLayout.WEST);
        pnl_Rodape.add(lbl_Total, BorderLayout.EAST);

        JPanel corpo = new JPanel(new BorderLayout(0, 8));
        corpo.setOpaque(false);
        corpo.add(pnl_NorteRows, BorderLayout.NORTH);
        corpo.add(scroll, BorderLayout.CENTER);
        corpo.add(pnl_Rodape, BorderLayout.SOUTH);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(corpo, BorderLayout.CENTER);
        return sec;
    }

    // ---- Seção: Peças a substituir ----
    private JPanel criarSecaoPecas() {
        JPanel sec = new PainelSecao(new BorderLayout(0, 8));
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));
        sec.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel lbl = criarLabelSecao("Peças a Substituir");

        JLabel lnk_CadPeca = new JLabel("<html><u>+ Cadastrar Peça</u></html>");
        lnk_CadPeca.setForeground(COR_INFO);
        lnk_CadPeca.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lnk_CadPeca.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lnk_CadPeca.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                V_Main main = (V_Main) SwingUtilities.getWindowAncestor(V_CadastrarOrcamento.this);
                if (main != null) main.atualizarConteudo(new V_CadastrarPeca(controller));
            }
        });

        JPanel pnl_Header = new JPanel(new BorderLayout());
        pnl_Header.setOpaque(false);
        pnl_Header.add(lbl, BorderLayout.WEST);
        pnl_Header.add(lnk_CadPeca, BorderLayout.EAST);

        cmb_Peca = criarCombo();
        cmb_Peca.setRenderer(criarRendererPadrao());

        txt_NomeTecnicoPeca = criarTextField();
        txt_NomeTecnicoPeca.setToolTipText("Ex: Filtro Mann W811/80 (nome técnico específico desta peça)");

        txt_FabricantePeca = criarTextField();
        txt_FabricantePeca.setToolTipText("Ex: Mann, Bosch, NGK");

        JButton btn_AddPeca = botaoAcao("+ Adicionar", COR_INFO);
        btn_AddPeca.addActionListener(e -> adicionarPeca());

        JPanel pnl_Row = new JPanel(new BorderLayout(8, 0));
        pnl_Row.setOpaque(false);
        pnl_Row.add(cmb_Peca, BorderLayout.CENTER);
        pnl_Row.add(btn_AddPeca, BorderLayout.EAST);

        JPanel pnl_NomeTec = new JPanel(new BorderLayout(0, 3));
        pnl_NomeTec.setOpaque(false);
        JLabel lbl_NomeTec = criarLabelMini("Nome Técnico da Peça (opcional)");
        pnl_NomeTec.add(lbl_NomeTec, BorderLayout.NORTH);
        pnl_NomeTec.add(txt_NomeTecnicoPeca, BorderLayout.CENTER);

        JPanel pnl_Fabricante = new JPanel(new BorderLayout(0, 3));
        pnl_Fabricante.setOpaque(false);
        JLabel lbl_Fabricante = criarLabelMini("Fabricante da Peça (opcional)");
        pnl_Fabricante.add(lbl_Fabricante, BorderLayout.NORTH);
        pnl_Fabricante.add(txt_FabricantePeca, BorderLayout.CENTER);

        String[] cols = {"Peça (Popular)", "Nome Técnico", "Fabricante", "Valor (R$)"};
        mdl_PecasSelecionadas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c >= 1; }
        };
        mdl_PecasSelecionadas.addTableModelListener(ev -> {
            if (ev.getType() != TableModelEvent.UPDATE) return;
            int row = ev.getFirstRow();
            int col = ev.getColumn();
            if (row < 0) return;
            if (col == 3 && row < valoresPecasSelecionadas.size()) {
                try {
                    String txt = String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 3)).replace(",", ".");
                    valoresPecasSelecionadas.set(row, Double.parseDouble(txt));
                } catch (NumberFormatException ex) {
                    valoresPecasSelecionadas.set(row, 0.0);
                    mdl_PecasSelecionadas.setValueAt("0.00", row, 3);
                }
                atualizarTotal();
            } else if (col == 1 && row < nomesTecnicosPecas.size()) {
                nomesTecnicosPecas.set(row, String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 1)));
            } else if (col == 2 && row < fabricantesPecas.size()) {
                fabricantesPecas.set(row, String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 2)));
            }
        });
        tbl_PecasSelecionadas = criarTabela(mdl_PecasSelecionadas);
        JScrollPane scroll = scrollTabela(tbl_PecasSelecionadas, 160);

        JButton btn_RemPeca = botaoLink("Remover selecionada", COR_PERIGO);
        btn_RemPeca.addActionListener(e -> {
            int row = tbl_PecasSelecionadas.getSelectedRow();
            if (row >= 0) {
                pecasSelecionadas.remove(row);
                valoresPecasSelecionadas.remove(row);
                nomesTecnicosPecas.remove(row);
                fabricantesPecas.remove(row);
                mdl_PecasSelecionadas.removeRow(row);
                atualizarTotal();
            }
        });

        JPanel pnl_Rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_Rodape.setOpaque(false);
        pnl_Rodape.add(btn_RemPeca);

        JPanel pnl_Norte = new JPanel();
        pnl_Norte.setLayout(new BoxLayout(pnl_Norte, BoxLayout.Y_AXIS));
        pnl_Norte.setOpaque(false);
        pnl_Norte.add(pnl_Row);
        pnl_Norte.add(Box.createVerticalStrut(8));
        pnl_Norte.add(pnl_NomeTec);
        pnl_Norte.add(Box.createVerticalStrut(8));
        pnl_Norte.add(pnl_Fabricante);

        JPanel corpo = new JPanel(new BorderLayout(0, 8));
        corpo.setOpaque(false);
        corpo.add(pnl_Norte, BorderLayout.NORTH);
        corpo.add(scroll, BorderLayout.CENTER);
        corpo.add(pnl_Rodape, BorderLayout.SOUTH);

        sec.add(pnl_Header, BorderLayout.NORTH);
        sec.add(corpo, BorderLayout.CENTER);
        return sec;
    }

    // ---- Carregamento de dados (lógica original — não alterada) ----
    private void carregarClientes() {
        cmb_Cliente.removeAllItems();
        for (Cliente c : controller.listarClientes()) {
            if (c.getCpf() != null && !c.getCpf().isBlank())
                cmb_Cliente.addItem(new ItemCliente(c));
        }
        atualizarVeiculos();
        cmb_Cliente.addActionListener(e -> atualizarVeiculos());
        cmb_FuncionarioProprietario.addActionListener(e -> atualizarVeiculos());
    }

    private void carregarCatalogo() {
        todosItens.clear();
        todosItens.addAll(controller.listarCatalogoServicos());
        filtrarCatalogo();
    }

    private void filtrarCatalogo() {
        cmb_ItemCatalogo.removeAllItems();
        SistemaItem sel = (SistemaItem) cmb_FiltroSistema.getSelectedItem();
        String cod = (sel != null && !sel.codigo.isEmpty()) ? sel.codigo : "";
        for (CatalogoServicoEntity item : todosItens) {
            if (cod.isEmpty() || cod.equals(item.getSistema()))
                cmb_ItemCatalogo.addItem(item);
        }
    }

    private void carregarFuncionarios() {
        cmb_Responsavel.removeAllItems();
        cmb_FuncionarioProprietario.removeAllItems();
        for (FuncionarioEntity f : controller.listarFuncionarios()) {
            cmb_Responsavel.addItem(new ItemFuncionario(f));
            cmb_FuncionarioProprietario.addItem(new ItemFuncionario(f));
        }
    }

    private void atualizarVeiculos() {
        cmb_Veiculo.removeAllItems();
        if (rdb_PropFuncionario != null && rdb_PropFuncionario.isSelected()) {
            ItemFuncionario sel = (ItemFuncionario) cmb_FuncionarioProprietario.getSelectedItem();
            if (sel == null || sel.funcionario.getIdUsuario() == null) return;
            for (Veiculo v : controller.listarVeiculosPorProprietario(sel.funcionario.getIdUsuario()))
                cmb_Veiculo.addItem(new ItemVeiculo(v));
        } else {
            ItemCliente sel = (ItemCliente) cmb_Cliente.getSelectedItem();
            if (sel == null) return;
            if (sel.cliente.getVeiculos() != null)
                for (Veiculo v : sel.cliente.getVeiculos())
                    cmb_Veiculo.addItem(new ItemVeiculo(v));
        }
    }

    /** Peças são genéricas (não dependem de veículo/modelo) — o valor é definido no próprio orçamento. */
    private void carregarPecas() {
        cmb_Peca.removeAllItems();
        List<PecaEntity> pecas = controller.listarTodasPecas();
        if (pecas.isEmpty()) {
            cmb_Peca.addItem(new ItemPeca(null));
        } else {
            for (PecaEntity p : pecas) cmb_Peca.addItem(new ItemPeca(p));
        }
    }

    private void adicionarItem() {
        CatalogoServicoEntity item = (CatalogoServicoEntity) cmb_ItemCatalogo.getSelectedItem();
        if (item == null) return;
        itensSelecionados.add(item);
        valoresItensSelecionados.add(item.getValor());
        String tipo = "REVISAO".equals(item.getTipo()) ? "Revisão" : "Padrão";
        mdl_ItensSelecionados.addRow(new Object[]{item.getNome(), tipo, String.format("%.2f", item.getValor())});

        // Auto-adicionar peças associadas a este item do catálogo
        List<PecaEntity> pecasDoItem = controller.listarPecasDoCatalogoItem(item.getIdCatalogoServico());
        for (PecaEntity p : pecasDoItem) {
            pecasSelecionadas.add(p.getIdPeca());
            valoresPecasSelecionadas.add(0.0);
            nomesTecnicosPecas.add("");
            fabricantesPecas.add("");
            mdl_PecasSelecionadas.addRow(new Object[]{
                    p.getNomeExibicao() + " (automático)", "", "", "0.00"
            });
        }

        atualizarTotal();
    }

    private void adicionarPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        String nomeTecnico = txt_NomeTecnicoPeca.getText().trim();
        String fabricante = txt_FabricantePeca.getText().trim();
        pecasSelecionadas.add(sel.peca.getIdPeca());
        valoresPecasSelecionadas.add(0.0);
        nomesTecnicosPecas.add(nomeTecnico);
        fabricantesPecas.add(fabricante);
        mdl_PecasSelecionadas.addRow(new Object[]{sel.peca.getNomeExibicao(), nomeTecnico, fabricante, "0.00"});
        txt_NomeTecnicoPeca.setText("");
        txt_FabricantePeca.setText("");
        atualizarTotal();
    }

    private void atualizarTotal() {
        double total = valoresItensSelecionados.stream().mapToDouble(Double::doubleValue).sum()
                + valoresPecasSelecionadas.stream().mapToDouble(Double::doubleValue).sum();
        lbl_Total.setText(String.format("Total: R$ %.2f", total));
    }

    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            ItemVeiculo veic = (ItemVeiculo) cmb_Veiculo.getSelectedItem();
            ItemFuncionario resp = (ItemFuncionario) cmb_Responsavel.getSelectedItem();
            String reclamacao  = txt_Reclamacao.getText().trim();

            // Resolve proprietário
            long idCliente;
            if (rdb_PropFuncionario != null && rdb_PropFuncionario.isSelected()) {
                ItemFuncionario funcProp = (ItemFuncionario) cmb_FuncionarioProprietario.getSelectedItem();
                if (funcProp == null || funcProp.funcionario.getIdUsuario() == null) {
                    aviso("Selecione um funcionário proprietário."); return;
                }
                idCliente = funcProp.funcionario.getIdUsuario();
            } else {
                ItemCliente cli = (ItemCliente) cmb_Cliente.getSelectedItem();
                if (cli == null) { aviso("Selecione um cliente."); return; }
                idCliente = cli.cliente.getIdUsuario();
            }

            if (veic == null) { aviso("Selecione um veículo do proprietário."); return; }
            if (resp == null) { aviso("Selecione o responsável."); return; }
            if (itensSelecionados.isEmpty()) { aviso("Adicione pelo menos um item de serviço."); return; }

            double total = valoresItensSelecionados.stream().mapToDouble(Double::doubleValue).sum()
                    + valoresPecasSelecionadas.stream().mapToDouble(Double::doubleValue).sum();

            try {
                controller.criarOrcamento(total, resp.funcionario.getNome(), reclamacao,
                        veic.veiculo.getIdVeiculo(), idCliente,
                        resp.funcionario.getIdFuncionario(),
                        new ArrayList<>(itensSelecionados), new ArrayList<>(valoresItensSelecionados),
                        new ArrayList<>(pecasSelecionadas), new ArrayList<>(valoresPecasSelecionadas),
                        new ArrayList<>(nomesTecnicosPecas), new ArrayList<>(fabricantesPecas));

                DialogoAlerta.sucesso(this, String.format("Orçamento criado! Total: R$ %.2f  |  Status: PENDENTE.", total), "Sucesso");

                txt_Reclamacao.setText("");
                itensSelecionados.clear();
                valoresItensSelecionados.clear();
                mdl_ItensSelecionados.setRowCount(0);
                pecasSelecionadas.clear();
                valoresPecasSelecionadas.clear();
                nomesTecnicosPecas.clear();
                mdl_PecasSelecionadas.setRowCount(0);
                lbl_Total.setText("Total: R$ 0,00");

            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao criar orçamento: " + ex.getMessage(), "Erro no Sistema");
            }
        });
    }

    private void aviso(String msg) {
        DialogoAlerta.aviso(this, msg, "Campo Inválido");
    }

    // ===== inner classes (modelo de dados — não alteradas) =====
    private static class ItemCliente {
        final Cliente cliente;
        ItemCliente(Cliente c) { this.cliente = c; }
        @Override public String toString() { return cliente.getNome() + " (" + cliente.getCpf() + ")"; }
    }
    private static class ItemVeiculo {
        final Veiculo veiculo;
        ItemVeiculo(Veiculo v) { this.veiculo = v; }
        @Override public String toString() {
            String m = veiculo.getModelo() != null ? veiculo.getModelo().getNome() : "Veículo";
            return m + " — " + veiculo.getPlaca();
        }
    }
    private static class ItemPeca {
        final PecaEntity peca;
        ItemPeca(PecaEntity p) { this.peca = p; }
        @Override public String toString() {
            return peca != null ? peca.getNomeExibicao() : "(nenhuma peça cadastrada)";
        }
    }
    private static class ItemFuncionario {
        final FuncionarioEntity funcionario;
        ItemFuncionario(FuncionarioEntity f) { this.funcionario = f; }
        @Override public String toString() { return funcionario.getNome() + " — " + funcionario.getCargo(); }
    }
    private static class SistemaItem {
        final String codigo, label;
        SistemaItem(String c, String l) { this.codigo = c; this.label = l; }
        @Override public String toString() { return label; }
    }

    // ===== helpers visuais =====
    private JPanel bloco(String rotulo, JComponent comp) {
        return bloco(rotulo, comp, 62);
    }

    private JPanel bloco(String rotulo, JComponent comp, int alturaMax) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, alturaMax));
        JLabel l = new JLabel(rotulo);
        l.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL));
        l.setForeground(COR_LABEL);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JLabel criarLabelSecao(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_SECAO));
        l.setForeground(COR_TITULO);
        return l;
    }

    private JLabel criarLabelMini(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(COR_LABEL_SUTIL);
        return l;
    }

    private JRadioButton criarRadio(String texto, boolean selecionado) {
        JRadioButton r = new JRadioButton(texto, selecionado);
        r.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_LABEL));
        r.setForeground(COR_LABEL);
        r.setOpaque(false);
        r.setFocusPainted(false);
        r.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return r;
    }

    private <T> GlassComboBox<T> criarCombo() {
        GlassComboBox<T> cmb = new GlassComboBox<>();
        cmb.setPreferredSize(new Dimension(100, ALTURA_CAMPO));
        return cmb;
    }

    private GlassTextField criarTextField() {
        GlassTextField f = new GlassTextField();
        f.setPreferredSize(new Dimension(0, ALTURA_CAMPO));
        return f;
    }

    private JScrollPane envolverEmScroll(JTextArea area) {
        JScrollPane scp = new JScrollPane(area);
        scp.setOpaque(false);
        scp.getViewport().setOpaque(false);
        scp.setBorder(BorderFactory.createEmptyBorder());
        ScrollBarPadrao.aplicar(scp);
        return scp;
    }

    private DefaultListCellRenderer criarRendererPadrao() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, i, sel, foc);
                estilizarCelula(lbl, i, sel);
                return lbl;
            }
        };
    }

    private void estilizarCelula(JLabel lbl, int indice, boolean selecionado) {
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        if (indice == -1) {
            // valor exibido na própria caixa: mantém transparente para revelar o vidro de fundo
            lbl.setOpaque(false);
            lbl.setForeground(COR_TEXTO_CAMPO);
        } else if (selecionado) {
            lbl.setOpaque(true);
            lbl.setBackground(COR_POPUP_SELECAO);
            lbl.setForeground(COR_TEXTO_CAMPO);
        } else {
            lbl.setOpaque(true);
            lbl.setBackground(COR_POPUP_FUNDO);
            lbl.setForeground(COR_TEXTO_CAMPO);
        }
    }

    private JButton botaoAcao(String texto, Color cor) {
        return new BotaoAcaoPequeno(texto, cor);
    }

    private JButton botaoLink(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(cor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable criarTabela(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setForeground(COR_TEXTO_CAMPO);
        tbl.setBackground(COR_TABELA_FUNDO);
        tbl.setOpaque(true);
        tbl.setRowHeight(26);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setSelectionBackground(COR_TABELA_SELECAO);
        tbl.setSelectionForeground(COR_TEXTO_CAMPO);
        tbl.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());

        JTableHeader header = tbl.getTableHeader();
        header.setDefaultRenderer(new CabecalhoVidroClaro());
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, ALTURA_CABECALHO));
        header.setReorderingAllowed(false);
        header.setOpaque(false);
        return tbl;
    }

    private JScrollPane scrollTabela(JTable tbl, int altura) {
        JScrollPane scp = new JScrollPane(tbl);
        scp.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        scp.setOpaque(false);
        scp.getViewport().setBackground(COR_TABELA_FUNDO);
        scp.getViewport().setOpaque(true);
        scp.setPreferredSize(new Dimension(0, altura));
        ScrollBarPadrao.aplicar(scp);
        return scp;
    }

    /** Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7), igual à V_VisualizarServicos. */
    private static class CabecalhoVidroClaro extends JLabel implements TableCellRenderer {

        CabecalhoVidroClaro() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(COR_AERO_TEXTO);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
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

            g2.setPaint(new GradientPaint(0, 0, COR_AERO_TOPO_A, 0, meio, COR_AERO_TOPO_B));
            g2.fillRect(0, 0, w, meio);

            g2.setPaint(new GradientPaint(0, meio, COR_AERO_BASE_A, 0, h, COR_AERO_BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillRect(0, 0, w, Math.max(1, h / 5));

            g2.setColor(COR_AERO_SEPARA);
            g2.drawLine(w - 1, 3, w - 1, h - 4);

            g2.setColor(COR_AERO_BORDA);
            g2.drawLine(0, h - 1, w, h - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Células de dados sempre com fundo branco (e destaque âmbar quando selecionadas), igual à V_VisualizarServicos. */
    private static class CelulaBrancaRenderer extends DefaultTableCellRenderer {
        CelulaBrancaRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(isSelected ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
            setForeground(COR_TEXTO_CAMPO);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return this;
        }
    }

    /**
     * Campo de texto com efeito de vidro translúcido (glassmorphism), no mesmo
     * padrão visual usado em V_CadastrarCliente / V_CadastrarVeiculo. Não altera
     * nenhuma regra de negócio — apenas a pintura do componente.
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

        void setEstadoErro(boolean valor) { this.erro = valor; repaint(); }

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

            Color corBorda; float espessura;
            if (erro) { corBorda = new Color(214, 58, 68, 220); espessura = 1.6f; }
            else if (focado) { corBorda = new Color(255, 153, 0, 210); espessura = 1.6f; }
            else { corBorda = COR_BORDA_SUAVE; espessura = 1f; }

            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /**
     * Variante em "vidro" do JTextArea, usada no campo de Reclamação/Relato,
     * seguindo exatamente a mesma linguagem visual do GlassTextField.
     */
    private static class GlassTextArea extends JTextArea {
        private boolean focado = false;

        GlassTextArea(int linhas, int colunas) {
            super(linhas, colunas);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setLineWrap(true);
            setWrapStyleWord(true);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
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
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.3), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : COR_BORDA_SUAVE;
            float espessura = focado ? 1.6f : 1f;

            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /**
     * ComboBox com o mesmo acabamento em vidro dos demais campos: pintura própria
     * do fundo/gradiente/borda, com o botão de seta redesenhado via GlassComboBoxUI.
     * A lista de itens/renderer de cada combo continua controlada externamente,
     * preservando toda a lógica de carregamento e seleção original.
     */
    private static class GlassComboBox<T> extends JComboBox<T> {
        private boolean focado = false;

        GlassComboBox() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            // Não usar cor transparente: o BasicComboPopup copia este background
            // para a lista suspensa, e um alpha 0 deixaria o popup transparente.
            setBackground(COR_POPUP_FUNDO);
            setFocusable(true);
            setUI(new GlassComboBoxUI());
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 28));
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

            Color corBorda = focado ? new Color(255, 153, 0, 210) : COR_BORDA_SUAVE;
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
     * por um triângulo vetorial e usa um popup arredondado — igual à
     * V_VisualizarServicos, corrigindo o bug de pintura em estados de foco.
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
            seta.setPreferredSize(new Dimension(20, 20));
            seta.setContentAreaFilled(false);
            seta.setBorderPainted(false);
            seta.setFocusPainted(false);
            seta.setOpaque(false);
            seta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return seta;
        }
    }

    /** Popup do combo com cantos arredondados e conteúdo sólido/legível, igual à V_VisualizarServicos. */
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
            ScrollBarPadrao.aplicar(scroller);
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

    /**
     * Painel com fundo em gradiente suave, usado para harmonizar o cartão
     * central com a translucidez dos campos em vidro.
     */
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

    /**
     * Painel "cartão" leve e semitransparente, usado para agrupar visualmente
     * cada subseção do formulário (Itens de Serviço / Peças a Substituir),
     * reforçando a hierarquia dentro do cartão principal.
     */
    private static class PainelSecao extends JPanel {
        PainelSecao(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(255, 255, 255, 150));
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE + 4, RAIO_COMPONENTE + 4));

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(COR_BORDA_SUAVE);
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE + 4, RAIO_COMPONENTE + 4));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação principal com a mesma linguagem visual dos campos em vidro:
     * cantos arredondados, sombra suave, reflexo no topo e reação a hover/clique.
     * As cores são parametrizadas para permitir reaproveitar a classe em outras
     * ações (aqui, apenas o botão "CRIAR ORÇAMENTO" usa este estilo grande).
     */
    private static class BotaoAcao extends JButton {
        private final Color corBase, corClara, corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Icon icone, Color corBase, Color corClara, Color corEscura) {
            super(texto, icone);
            this.corBase = corBase;
            this.corClara = corClara;
            this.corEscura = corEscura;
            setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_BOTAO));
            setForeground(Color.WHITE);
            setIconTextGap(10);
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

            g2.setColor(new Color(0, 0, 0, 45));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Versão compacta do BotaoAcao, usada nos botões "+ Adicionar" das seções de
     * Itens de Serviço e Peças. A cor de hover/clique é derivada automaticamente
     * da cor base (brighter()/darker()), então basta uma única cor por chamada.
     */
    private static class BotaoAcaoPequeno extends JButton {
        private final Color corBase;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcaoPequeno(String texto, Color corBase) {
            super(texto);
            this.corBase = corBase;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            setPreferredSize(new Dimension(120, 34));
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
            int raio = RAIO_COMPONENTE - 2;

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1, 2, w - 2, h - 2, raio, raio));

            Color corPreenchimento = pressionado ? corBase.darker() : (sobreMouse ? corBase.brighter() : corBase);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, raio, raio));

            g2.setColor(new Color(255, 255, 255, 40));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), raio - 3, raio - 3));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Ícone vetorial (prancheta com check) desenhado via Java2D, seguindo a
     * mesma técnica do IconeAdicionarUsuario usado em V_CadastrarCliente —
     * escala perfeitamente para qualquer tamanho de botão, sem arquivo externo.
     */
    private static class IconeOrcamento implements Icon {
        private final int tamanho;
        private final Color cor;

        IconeOrcamento(int tamanho, Color cor) {
            this.tamanho = tamanho;
            this.cor = cor;
        }

        @Override public int getIconWidth()  { return tamanho; }
        @Override public int getIconHeight() { return tamanho; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            double escala = tamanho / 24.0;
            g2.scale(escala, escala);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Prancheta / documento
            g2.draw(new RoundRectangle2D.Double(3.5, 1.5, 15, 20, 3, 3));

            // Clipe no topo
            g2.draw(new RoundRectangle2D.Double(8.5, 0.2, 5, 3, 1.5, 1.5));

            // Linhas de texto do orçamento
            g2.draw(new Line2D.Double(7, 8, 15, 8));
            g2.draw(new Line2D.Double(7, 12, 13, 12));

            // Check de aprovação
            Path2D check = new Path2D.Double();
            check.moveTo(7.5, 16.3);
            check.lineTo(10, 18.6);
            check.lineTo(15.5, 13.2);
            g2.draw(check);

            g2.dispose();
        }
    }
}