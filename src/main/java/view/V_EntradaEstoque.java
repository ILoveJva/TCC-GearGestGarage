package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import br.com.oficina.estoque.CatalogoPecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
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
 * Compra de peças: ao escolher "Adicionar ao estoque", cada peça (com nome técnico,
 * fabricante, quantidade e valor) é adicionada a uma lista — várias peças diferentes
 * entram numa única compra, cada uma gerando sua própria entrada no estoque ao finalizar.
 * Ao escolher "Usar direto numa OS" (peça fora do estoque), o fluxo continua sendo de
 * uma peça por vez, já que o custo é somado diretamente ao orçamento daquela OS específica.
 *
 * Visual alinhado ao mesmo acabamento em vidro (glassmorphism) usado em V_CadastrarOrcamento.
 */
public class V_EntradaEstoque extends JPanel {

    private final OficinaController controller;

    private GlassComboBox<ItemPeca> cmb_Peca;
    private GlassTextField txt_BuscaPeca;
    private final List<ItemPeca> todosItensPeca = new ArrayList<>();
    private GlassTextField txt_Quantidade;
    private GlassTextField txt_Valor;
    private JRadioButton rad_Estoque;
    private JRadioButton rad_OSDireto;
    private GlassComboBox<ItemOS> cmb_OS;
    private GlassTextField txt_NomeTecnico;
    private GlassTextField txt_Fabricante;
    private GlassTextArea txt_Observacao;
    private JPanel pnl_OS;
    private JPanel pnl_TecFab;
    private BotaoAcao btn_Registrar;

    // Lista de peças da compra atual (destino "Adicionar ao estoque").
    private final List<ItemCompra> itensCompra = new ArrayList<>();
    private DefaultTableModel mdl_Itens;
    private JTable tbl_Itens;
    private JLabel lbl_TotalItens;
    private JPanel pnl_AdicionarLista;
    private JPanel pnl_TabelaItens;

    // Paleta harmonizada com o mesmo efeito de vidro usado nas demais telas
    // (V_CadastrarOrcamento, V_CadastrarCliente, V_CadastrarVeiculo, ...).
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_BORDA_SUAVE  = new Color(160, 175, 195, 130);

    // Cores do popup da lista suspensa — precisa ser SÓLIDO (o vidro é só na caixa fechada)
    private static final Color COR_POPUP_FUNDO   = Color.decode("#FFFFFF");
    private static final Color COR_POPUP_SELECAO = Color.decode("#FFE4BF");
    private static final Color COR_POPUP_BORDA   = Color.decode("#C3CDDA");

    // Cor de ação primária (tema original preservado)
    private static final Color COR_SUCESSO       = Color.decode("#28A745");
    private static final Color COR_SUCESSO_CLARA = COR_SUCESSO.brighter();
    private static final Color COR_SUCESSO_ESCURA = COR_SUCESSO.darker();
    private static final Color COR_SECUNDARIA    = Color.decode("#6C757D");

    // "+ Adicionar à lista" (azul, para diferenciar do verde de "Finalizar compra")
    private static final Color COR_ADICIONAR        = Color.decode("#2E86DE");
    private static final Color COR_ADICIONAR_CLARA  = COR_ADICIONAR.brighter();
    private static final Color COR_ADICIONAR_ESCURA = COR_ADICIONAR.darker();
    // "Remover" item da lista (vermelho)
    private static final Color COR_REMOVER        = Color.decode("#DC3545");
    private static final Color COR_REMOVER_CLARA  = COR_REMOVER.brighter();
    private static final Color COR_REMOVER_ESCURA = COR_REMOVER.darker();

    // Ajustes rápidos de tipografia/tamanho — mexa só aqui para alterar tudo de uma vez
    private static final int RAIO_COMPONENTE      = 12;
    private static final int TAMANHO_FONTE_TITULO = 14;
    private static final int TAMANHO_FONTE_LABEL  = 13;
    private static final int TAMANHO_FONTE_CAMPO  = 14;
    private static final int ALTURA_CAMPO         = 34;
    private static final int TAMANHO_FONTE_BOTAO  = 14;

    public V_EntradaEstoque(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        construirInterface();
        carregarPecas();
        carregarOS();
    }

    private void construirInterface() {
        JPanel card = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        card.setPreferredSize(new Dimension(640, 560));
        card.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lbl_Titulo = new JLabel("Estoque > Compra de Peças");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_TITULO));
        lbl_Titulo.setForeground(COR_TITULO);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        cmb_Peca = criarCombo();
        cmb_Peca.setRenderer(criarRendererPadrao());
        cmb_Peca.addActionListener(e -> sugerirDestinoPelaPeca());

        txt_BuscaPeca = criarTextField();
        txt_BuscaPeca.setToolTipText("Digite para buscar uma peça pelo nome popular");
        BuscaUtil.aoDigitar(txt_BuscaPeca, this::filtrarPecas);

        JPanel pnl_L0 = linha(58);
        pnl_L0.add(bloco("Buscar peça", txt_BuscaPeca), BorderLayout.CENTER);

        txt_Quantidade = criarTextField();
        txt_Quantidade.setToolTipText("Ex: 10");
        ((AbstractDocument) txt_Quantidade.getDocument()).setDocumentFilter(new FiltroInteiro());

        txt_Valor = criarTextField();
        txt_Valor.setToolTipText("Ex: 45.90 (valor unitário pago pela peça)");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_Observacao = new GlassTextArea();
        txt_Observacao.setToolTipText("Ex: Compra no fornecedor X (opcional)");

        JPanel pnl_L1 = linha(62);
        pnl_L1.add(bloco("Peça *", cmb_Peca), BorderLayout.CENTER);
        JPanel pnl_QtdW = bloco("Quantidade *", txt_Quantidade);
        pnl_QtdW.setPreferredSize(new Dimension(140, 58));
        pnl_L1.add(pnl_QtdW, BorderLayout.EAST);

        JPanel pnl_L2 = linha(62);
        JPanel pnl_ValorW = bloco("Valor unitário pago (R$)", txt_Valor);
        pnl_ValorW.setPreferredSize(new Dimension(220, 58));
        pnl_L2.add(pnl_ValorW, BorderLayout.WEST);

        // ---- Destino da compra ----
        rad_Estoque = criarRadio("Adicionar ao estoque", true);
        rad_OSDireto = criarRadio("Usar direto numa OS (peça fora do estoque)", false);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rad_Estoque);
        grupo.add(rad_OSDireto);
        rad_Estoque.addActionListener(e -> atualizarVisibilidadeDestino());
        rad_OSDireto.addActionListener(e -> atualizarVisibilidadeDestino());

        JPanel pnl_Destino = new JPanel();
        pnl_Destino.setLayout(new BoxLayout(pnl_Destino, BoxLayout.Y_AXIS));
        pnl_Destino.setOpaque(false);
        pnl_Destino.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Destino.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel lbl_Destino = criarLabel("Destino da compra *");
        pnl_Destino.add(lbl_Destino);
        pnl_Destino.add(Box.createVerticalStrut(4));
        pnl_Destino.add(rad_Estoque);
        pnl_Destino.add(rad_OSDireto);

        cmb_OS = criarCombo();
        cmb_OS.setRenderer(criarRendererPadrao());

        pnl_OS = linha(62);
        pnl_OS.add(bloco("OS de destino *", cmb_OS), BorderLayout.CENTER);

        txt_NomeTecnico = criarTextField();
        txt_NomeTecnico.setToolTipText("Ex: Filtro Mann W811/80 (opcional)");
        txt_Fabricante = criarTextField();
        txt_Fabricante.setToolTipText("Ex: Mann, Bosch, NGK (opcional)");

        pnl_TecFab = linha(62);
        pnl_TecFab.add(bloco("Nome Técnico (opcional)", txt_NomeTecnico), BorderLayout.CENTER);
        JPanel pnl_FabW = bloco("Fabricante (opcional)", txt_Fabricante);
        pnl_FabW.setPreferredSize(new Dimension(200, 58));
        pnl_TecFab.add(pnl_FabW, BorderLayout.EAST);

        // ---- "Adicionar ao estoque": lista de peças da compra atual ----
        BotaoAcao btn_Adicionar = new BotaoAcao("+ Adicionar à lista", COR_ADICIONAR, COR_ADICIONAR_CLARA, COR_ADICIONAR_ESCURA);
        btn_Adicionar.setPreferredSize(new Dimension(180, 36));
        btn_Adicionar.addActionListener(e -> adicionarItemLista());
        pnl_AdicionarLista = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_AdicionarLista.setOpaque(false);
        pnl_AdicionarLista.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_AdicionarLista.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        pnl_AdicionarLista.add(btn_Adicionar);

        String[] colsItens = {"Peça", "Nome Técnico", "Fabricante", "Qtd.", "Valor Unit.", "Subtotal"};
        mdl_Itens = new DefaultTableModel(colsItens, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Itens = new JTable(mdl_Itens);
        tbl_Itens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Itens.setRowHeight(24);
        tbl_Itens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_Itens.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Itens.getTableHeader().setReorderingAllowed(false);
        tbl_Itens.getColumnModel().getColumn(3).setMaxWidth(50);
        tbl_Itens.getColumnModel().getColumn(4).setMaxWidth(90);
        tbl_Itens.getColumnModel().getColumn(5).setMaxWidth(90);

        JScrollPane scrollItens = new JScrollPane(tbl_Itens);
        scrollItens.setPreferredSize(new Dimension(0, 140));
        ScrollBarPadrao.aplicar(scrollItens);

        JButton btn_Remover = new JButton("Remover selecionado");
        btn_Remover.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_Remover.setForeground(COR_REMOVER);
        btn_Remover.setContentAreaFilled(false);
        btn_Remover.setBorderPainted(false);
        btn_Remover.setFocusPainted(false);
        btn_Remover.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Remover.addActionListener(e -> removerItemSelecionado());

        lbl_TotalItens = new JLabel("Total: R$ 0,00 (0 item(ns))");
        lbl_TotalItens.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl_TotalItens.setForeground(COR_TITULO);

        JPanel pnl_RodapeItens = new JPanel(new BorderLayout());
        pnl_RodapeItens.setOpaque(false);
        pnl_RodapeItens.add(btn_Remover, BorderLayout.WEST);
        pnl_RodapeItens.add(lbl_TotalItens, BorderLayout.EAST);

        pnl_TabelaItens = new JPanel(new BorderLayout(0, 4));
        pnl_TabelaItens.setOpaque(false);
        pnl_TabelaItens.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_TabelaItens.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        pnl_TabelaItens.add(scrollItens, BorderLayout.CENTER);
        pnl_TabelaItens.add(pnl_RodapeItens, BorderLayout.SOUTH);

        JPanel pnl_L5 = linha(200);
        pnl_L5.add(bloco("Observação", txt_Observacao), BorderLayout.CENTER);

        form.add(pnl_L0);
        form.add(Box.createVerticalStrut(12));
        form.add(pnl_L1);
        form.add(Box.createVerticalStrut(12));
        form.add(pnl_L2);
        form.add(Box.createVerticalStrut(12));
        form.add(pnl_Destino);
        form.add(Box.createVerticalStrut(6));
        form.add(pnl_OS);
        form.add(Box.createVerticalStrut(6));
        form.add(pnl_TecFab);
        form.add(Box.createVerticalStrut(6));
        form.add(pnl_AdicionarLista);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_TabelaItens);
        form.add(Box.createVerticalStrut(12));
        form.add(pnl_L5);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scrollForm);

        btn_Registrar = new BotaoAcao("REGISTRAR COMPRA", COR_SUCESSO, COR_SUCESSO_CLARA, COR_SUCESSO_ESCURA);
        btn_Registrar.setPreferredSize(new Dimension(220, 44));
        btn_Registrar.addActionListener(e -> salvar());

        BotaoAcao btn_Voltar = new BotaoAcao("← Voltar", COR_SECUNDARIA, COR_SECUNDARIA.brighter(), COR_SECUNDARIA.darker());
        btn_Voltar.setPreferredSize(new Dimension(120, 44));
        btn_Voltar.addActionListener(e -> navegar(new V_Estoque(controller)));

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Voltar);
        pnl_Btn.add(btn_Registrar);

        card.add(lbl_Titulo, BorderLayout.NORTH);
        card.add(scrollForm, BorderLayout.CENTER);
        card.add(pnl_Btn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 40, 20, 40);
        add(card, gbc);

        atualizarVisibilidadeDestino();
    }

    private void atualizarVisibilidadeDestino() {
        boolean direto = rad_OSDireto.isSelected();
        pnl_OS.setVisible(direto);
        // Nome Técnico e Fabricante ficam visíveis nos dois destinos.
        // A lista de peças (e o botão para adicionar a ela) só existe para "Adicionar ao estoque";
        // "Usar direto numa OS" continua sendo uma peça por vez.
        pnl_AdicionarLista.setVisible(!direto);
        pnl_TabelaItens.setVisible(!direto);
        if (btn_Registrar != null)
            btn_Registrar.setText(direto ? "REGISTRAR COMPRA" : "FINALIZAR COMPRA (" + itensCompra.size() + ")");
        revalidate();
        repaint();
    }

    /** Sugere o destino conforme o estoque atual (agregado) da peça selecionada, sem travar a escolha do usuário. */
    private void sugerirDestinoPelaPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        if (sel.quantidadeEstoque <= 0 && cmb_OS.getItemCount() > 0) {
            rad_OSDireto.setSelected(true);
        } else {
            rad_Estoque.setSelected(true);
        }
        atualizarVisibilidadeDestino();
    }

    private void salvar() {
        if (rad_OSDireto.isSelected()) salvarDiretoOS();
        else salvarListaEstoque();
    }

    /** "Usar direto numa OS": continua sendo uma peça por vez (o custo entra direto no orçamento daquela OS). */
    private void salvarDiretoOS() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) {
            DialogoAlerta.aviso(this, "Selecione uma peça.", "Campo Inválido");
            return;
        }
        int qtd;
        try {
            qtd = Integer.parseInt(txt_Quantidade.getText().trim());
            if (qtd <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe uma quantidade válida maior que zero.", "Campo Inválido");
            return;
        }

        Double valorUnitario = lerValorOpcional();
        if (valorUnitario == null && !txt_Valor.getText().trim().isEmpty()) {
            DialogoAlerta.aviso(this, "Informe um valor válido (ex: 45.90).", "Campo Inválido");
            return;
        }

        ItemOS osSel = (ItemOS) cmb_OS.getSelectedItem();
        if (osSel == null || osSel.dto == null) {
            DialogoAlerta.aviso(this, "Selecione a OS de destino.", "Campo Inválido");
            return;
        }
        if (valorUnitario == null || valorUnitario <= 0) {
            DialogoAlerta.aviso(this, "Informe o valor unitário cobrado pela peça nessa OS.", "Campo Inválido");
            return;
        }
        try {
            controller.adicionarPecaDiretoOS(osSel.dto.idOrcamento(), sel.peca.getIdCatalogoPeca(), qtd,
                    txt_NomeTecnico.getText().trim(), txt_Fabricante.getText().trim(), valorUnitario);
            DialogoAlerta.sucesso(this, qtd + " unidade(s) de \"" + sel.peca.getNomePopular()
                    + "\" adicionada(s) direto à " + osSel + " (sem passar pelo estoque).", "Sucesso");
            limparCampos();
            carregarPecas();
            carregarOS();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao registrar compra direta: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    /** "Adicionar ao estoque": finaliza a compra inteira, registrando uma entrada por peça da lista. */
    private void salvarListaEstoque() {
        if (itensCompra.isEmpty()) {
            DialogoAlerta.aviso(this, "Adicione ao menos uma peça à lista antes de registrar a compra.", "Lista vazia");
            return;
        }
        String observacao = txt_Observacao.getText().trim();
        try {
            int totalUnidades = 0;
            double totalValor = 0;
            for (ItemCompra it : itensCompra) {
                controller.registrarEntradaEstoque(it.peca().getIdCatalogoPeca(), it.quantidade(),
                        it.valorUnitario(), observacao, it.nomeTecnico(), it.fabricante());
                totalUnidades += it.quantidade();
                totalValor += it.subtotal();
            }
            DialogoAlerta.sucesso(this, itensCompra.size() + " peça(s) (" + totalUnidades + " unidade(s), "
                    + String.format("R$ %.2f", totalValor) + ") registrada(s) no estoque com sucesso!", "Sucesso");
            itensCompra.clear();
            atualizarTabelaItens();
            limparCampos();
            carregarPecas();
            carregarOS();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao registrar compra: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    /** Adiciona a peça atualmente preenchida no formulário à lista da compra em andamento. */
    private void adicionarItemLista() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) {
            DialogoAlerta.aviso(this, "Selecione uma peça.", "Campo Inválido");
            return;
        }
        int qtd;
        try {
            qtd = Integer.parseInt(txt_Quantidade.getText().trim());
            if (qtd <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe uma quantidade válida maior que zero.", "Campo Inválido");
            return;
        }

        Double valorUnitario = lerValorOpcional();
        if (valorUnitario == null && !txt_Valor.getText().trim().isEmpty()) {
            DialogoAlerta.aviso(this, "Informe um valor válido (ex: 45.90).", "Campo Inválido");
            return;
        }

        itensCompra.add(new ItemCompra(sel.peca, txt_NomeTecnico.getText().trim(),
                txt_Fabricante.getText().trim(), qtd, valorUnitario));
        atualizarTabelaItens();

        txt_Quantidade.setText("");
        txt_Valor.setText("");
        txt_NomeTecnico.setText("");
        txt_Fabricante.setText("");
    }

    private void removerItemSelecionado() {
        int row = tbl_Itens.getSelectedRow();
        if (row < 0 || row >= itensCompra.size()) {
            DialogoAlerta.aviso(this, "Selecione um item da lista para remover.", "Nenhum selecionado");
            return;
        }
        itensCompra.remove(row);
        atualizarTabelaItens();
    }

    private void atualizarTabelaItens() {
        mdl_Itens.setRowCount(0);
        double total = 0;
        for (ItemCompra it : itensCompra) {
            double subtotal = it.subtotal();
            total += subtotal;
            mdl_Itens.addRow(new Object[]{
                it.peca().getNomePopular(),
                it.nomeTecnico().isBlank() ? "—" : it.nomeTecnico(),
                it.fabricante().isBlank() ? "—" : it.fabricante(),
                it.quantidade(),
                it.valorUnitario() != null ? String.format("R$ %.2f", it.valorUnitario()) : "—",
                String.format("R$ %.2f", subtotal)
            });
        }
        lbl_TotalItens.setText("Total: " + String.format("R$ %.2f", total) + "  (" + itensCompra.size() + " item(ns))");
        if (btn_Registrar != null && rad_Estoque.isSelected())
            btn_Registrar.setText("FINALIZAR COMPRA (" + itensCompra.size() + ")");
    }

    /** Lê o campo de valor (opcional): null se vazio, ou null se inválido (chamador decide o aviso). */
    private Double lerValorOpcional() {
        String valorTxt = txt_Valor.getText().trim().replace(",", ".");
        if (valorTxt.isEmpty()) return null;
        try {
            double v = Double.parseDouble(valorTxt);
            return v >= 0 ? v : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void limparCampos() {
        txt_Quantidade.setText("");
        txt_Valor.setText("");
        txt_NomeTecnico.setText("");
        txt_Fabricante.setText("");
        txt_Observacao.setText("");
    }

    private void carregarPecas() {
        todosItensPeca.clear();
        for (CatalogoPecaEntity p : controller.listarEstoque())
            todosItensPeca.add(new ItemPeca(p, controller.quantidadeEstoqueCatalogo(p.getIdCatalogoPeca())));
        filtrarPecas();
    }

    private void filtrarPecas() {
        Object selecionado = cmb_Peca.getSelectedItem();
        cmb_Peca.removeAllItems();
        String termo = txt_BuscaPeca != null ? txt_BuscaPeca.getText() : "";
        for (ItemPeca it : todosItensPeca)
            if (it.peca == null || BuscaUtil.contem(it.peca.getNomeExibicao(), termo))
                cmb_Peca.addItem(it);
        if (selecionado instanceof ItemPeca ip && ip.peca != null) {
            for (int i = 0; i < cmb_Peca.getItemCount(); i++) {
                ItemPeca it = cmb_Peca.getItemAt(i);
                if (it.peca != null && it.peca.getIdCatalogoPeca().equals(ip.peca.getIdCatalogoPeca())) {
                    cmb_Peca.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void carregarOS() {
        cmb_OS.removeAllItems();
        for (ServicoResponseDTO dto : controller.listarServicosEmAberto()) cmb_OS.addItem(new ItemOS(dto));
    }

    // ===== helpers visuais =====
    private JPanel linha(int maxH) {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, maxH));
        return p;
    }

    private JPanel bloco(String rotulo, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.add(criarLabel(rotulo), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JLabel criarLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL));
        l.setForeground(COR_LABEL);
        return l;
    }

    private JRadioButton criarRadio(String texto, boolean selecionado) {
        JRadioButton r = new JRadioButton(texto, selecionado);
        r.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_LABEL));
        r.setForeground(COR_LABEL);
        r.setOpaque(false);
        r.setFocusPainted(false);
        r.setCursor(new Cursor(Cursor.HAND_CURSOR));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // ===== inner classes (modelo de dados — lógica não alterada) =====
    private static class ItemPeca {
        final CatalogoPecaEntity peca;
        final int quantidadeEstoque;
        ItemPeca(CatalogoPecaEntity p, int quantidadeEstoque) { this.peca = p; this.quantidadeEstoque = quantidadeEstoque; }
        @Override public String toString() {
            if (peca == null) return "(sem peça)";
            return peca.getNomePopular() + "  (estoque: " + quantidadeEstoque + ")";
        }
    }

    private static class ItemOS {
        final ServicoResponseDTO dto;
        ItemOS(ServicoResponseDTO dto) { this.dto = dto; }
        @Override public String toString() {
            if (dto == null) return "(sem OS)";
            String titulo = dto.titulo() != null && !dto.titulo().isBlank() ? dto.titulo() : "(sem título)";
            return "OS #" + dto.idServico() + " — " + titulo;
        }
    }

    /** Uma peça da lista da compra atual (destino "Adicionar ao estoque"), com todas as informações necessárias. */
    private record ItemCompra(CatalogoPecaEntity peca, String nomeTecnico, String fabricante,
                              int quantidade, Double valorUnitario) {
        double subtotal() { return valorUnitario != null ? valorUnitario * quantidade : 0.0; }
    }

    private static class FiltroInteiro extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9]", ""), a);
        }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9]", ""), a);
        }
    }

    private static class FiltroDecimal extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9.,]", ""), a);
        }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9.,]", ""), a);
        }
    }

    /** Campo de texto com efeito de vidro translúcido (glassmorphism), igual à V_CadastrarOrcamento. */
    private static class GlassTextField extends JTextField {
        private boolean focado = false;

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
     * Área de texto multilinha com o mesmo vidro do GlassTextField, usada em
     * "Observação": ao contrário do campo de uma linha, o texto começa no topo
     * e quebra linha sozinho, preenchendo a caixa inteira corretamente.
     */
    private static class GlassTextArea extends JTextArea {
        private boolean focado = false;

        GlassTextArea() {
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

            // Reflexo mais fino que o do campo de uma linha — numa caixa alta,
            // uma faixa de brilho ocupando 40% da altura ficaria pesada demais.
            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.15), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

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

    /** ComboBox com o mesmo acabamento em vidro, igual à V_CadastrarOrcamento. */
    private static class GlassComboBox<T> extends JComboBox<T> {
        private boolean focado = false;

        GlassComboBox() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
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

    /** UI do GlassComboBox: impede pintura sólida por cima do vidro, seta vetorial e popup arredondado. */
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

    /** Painel com fundo em gradiente suave, usado para o cartão central. */
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
     * Botão de ação com a mesma linguagem visual dos campos em vidro: cantos
     * arredondados, sombra suave, reflexo no topo e reação a hover/clique.
     */
    private static class BotaoAcao extends JButton {
        private final Color corBase, corClara, corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Color corBase, Color corClara, Color corEscura) {
            super(texto);
            this.corBase = corBase;
            this.corClara = corClara;
            this.corEscura = corEscura;
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
}