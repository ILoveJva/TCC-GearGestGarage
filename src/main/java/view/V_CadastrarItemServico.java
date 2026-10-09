package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.estoque.CatalogoPecaEntity;
import controller.OficinaController;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class V_CadastrarItemServico extends JPanel {

    private final OficinaController controller;
    private final List<CatalogoServicoEntity> itensCadastrados = new ArrayList<>();

    private JTextField txt_Nome;
    private JTextField txt_Valor;
    private JTextField txt_ValidadeKm;
    private JTextField txt_ValidadeMeses;
    private JRadioButton rdb_Padrao;
    private JRadioButton rdb_Revisao;
    private JPanel pnl_Validade;
    private JComboBox<String> cmb_Sistema;
    private JButton btn_Cadastrar;
    private DefaultTableModel mdl_Itens;
    private JTable tbl_Itens;

    // Pesquisa da tabela de itens cadastrados
    private TableRowSorter<DefaultTableModel> sorter_Itens;
    private CampoBusca txt_Busca;
    private JLabel lbl_Contagem;
    private JPopupMenu pop_Sugestoes;
    private DefaultListModel<String> mdl_Sugestoes;
    private JList<String> lst_Sugestoes;
    private boolean atualizandoBusca = false;

    private static final String[] SUGESTOES_PADRAO = {
            "Revisão", "Padrão", "Motor", "Transmissão", "Freios", "Suspensão", "Elétrica"
    };
    private static final int ALTURA_SUGESTAO = 28;
    private static final int MAX_SUGESTOES = 6;

    // Peças associadas ao item
    private JComboBox<ItemPeca> cmb_Peca;
    private DefaultTableModel mdl_Pecas;
    private JTable tbl_Pecas;
    private final List<Long> idPecasSelecionadas = new ArrayList<>();

    private static final String[] SISTEMAS = {
            "MOTOR", "TRANSMISSAO", "DIRECAO", "SUSPENSAO", "FREIOS",
            "ARREFECIMENTO", "ELETRICA", "ALIMENTACAO", "OUTROS"
    };
    private static final String[] SISTEMAS_LABEL = {
            "Motor", "Transmissão", "Direção", "Suspensão", "Freios",
            "Arrefecimento", "Elétrica", "Alimentação", "Outros"
    };

    public V_CadastrarItemServico(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        construirInterface();
        carregarPecas();
        carregarLista();
    }

    private void construirInterface() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(680, 680));

        JLabel lbl_Titulo = new JLabel("Configurações > Itens de Serviço");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        txt_Nome  = criarTextField();
        txt_Valor = criarTextField();
        txt_Valor.setToolTipText("Ex: 120.00");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_ValidadeKm     = criarTextField();
        txt_ValidadeKm.setToolTipText("Ex: 5000");
        ((AbstractDocument) txt_ValidadeKm.getDocument()).setDocumentFilter(new FiltroInteiro());

        txt_ValidadeMeses  = criarTextField();
        txt_ValidadeMeses.setToolTipText("Ex: 6");
        ((AbstractDocument) txt_ValidadeMeses.getDocument()).setDocumentFilter(new FiltroInteiro());

        // Linha 1: Nome + Valor
        JPanel pnl_L1 = linha(Integer.MAX_VALUE, 60);
        JPanel pnl_ValorW = bloco("Valor (R$) *", txt_Valor);
        pnl_ValorW.setPreferredSize(new Dimension(130, 55));
        pnl_L1.add(bloco("Nome do Serviço *", txt_Nome), BorderLayout.CENTER);
        pnl_L1.add(pnl_ValorW, BorderLayout.EAST);

        // Linha 2: Tipo (Padrão / Revisão)
        JPanel pnl_L2 = linha(Integer.MAX_VALUE, 50);
        pnl_L2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 4));
        JLabel lbl_Manut = criarLabel("Tipo *");
        rdb_Padrao  = new JRadioButton("Padrão (com validade)");
        rdb_Revisao = new JRadioButton("Revisão (todos os carros)");
        rdb_Padrao.setOpaque(false); rdb_Revisao.setOpaque(false);
        rdb_Padrao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_Revisao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_Padrao.setSelected(true);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rdb_Padrao); grupo.add(rdb_Revisao);
        pnl_L2.add(lbl_Manut);
        pnl_L2.add(Box.createHorizontalStrut(10));
        pnl_L2.add(rdb_Padrao);
        pnl_L2.add(Box.createHorizontalStrut(16));
        pnl_L2.add(rdb_Revisao);

        // Linha 3: Sistema do veículo
        JPanel pnl_L3 = linha(Integer.MAX_VALUE, 60);

        cmb_Sistema = new JComboBox<>(SISTEMAS_LABEL);
        cmb_Sistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Sistema.setSelectedIndex(SISTEMAS_LABEL.length - 1); // Outros padrão

        pnl_L3.add(bloco("Sistema do Veículo *", cmb_Sistema));

        // Linha 4: Validade (visível apenas para Padrão)
        pnl_Validade = linha(Integer.MAX_VALUE, 60);
        pnl_Validade.setLayout(new GridLayout(1, 2, 16, 0));
        pnl_Validade.add(bloco("Validade (KM)", txt_ValidadeKm));
        pnl_Validade.add(bloco("Validade (meses)", txt_ValidadeMeses));

        rdb_Padrao.addActionListener(e  -> pnl_Validade.setVisible(true));
        rdb_Revisao.addActionListener(e -> pnl_Validade.setVisible(false));

        form.add(pnl_L1);
        form.add(Box.createVerticalStrut(10));
        form.add(pnl_L2);
        form.add(Box.createVerticalStrut(8));
        form.add(pnl_L3);
        form.add(Box.createVerticalStrut(8));
        form.add(pnl_Validade);
        form.add(Box.createVerticalStrut(14));
        form.add(criarSecaoPecas());

        // Botão compacto — fica na mesma linha da barra de pesquisa (ver criarBarraBusca)
        btn_Cadastrar = new BotaoVidro("+ ADICIONAR ITEM", Color.decode("#FF9900"), true);
        btn_Cadastrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_Cadastrar.setPreferredSize(new Dimension(150, 32));
        btn_Cadastrar.addActionListener(e -> salvar());

        JPanel pnl_Topo = new JPanel(new BorderLayout(0, 4));
        pnl_Topo.setOpaque(false);
        pnl_Topo.add(form, BorderLayout.CENTER);

        // Tabela de itens cadastrados
        String[] colunas = {"Nome", "Sistema", "Tipo", "Valor (R$)", "Val. KM", "Val. Meses"};
        mdl_Itens = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Itens = new JTable(mdl_Itens);
        tbl_Itens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Itens.setRowHeight(26);
        tbl_Itens.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Itens.getTableHeader().setReorderingAllowed(false);

        // Sorter usado apenas para filtrar pela barra de pesquisa (sem ordenar ao clicar no cabeçalho)
        sorter_Itens = new TableRowSorter<>(mdl_Itens);
        for (int c = 0; c < mdl_Itens.getColumnCount(); c++) sorter_Itens.setSortable(c, false);
        tbl_Itens.setRowSorter(sorter_Itens);

        tbl_Itens.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editarSelecionado();
            }
        });

        JButton btn_Editar = new BotaoVidro("Editar selecionado", Color.decode("#FF9900"), false);
        btn_Editar.addActionListener(e -> editarSelecionado());

        JPanel pnl_ListaRodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        pnl_ListaRodape.setOpaque(false);
        pnl_ListaRodape.add(new JLabel("Duplo-clique ou: ") {{
            setFont(new Font("Segoe UI", Font.ITALIC, 11));
            setForeground(Color.decode("#999999"));
        }});
        pnl_ListaRodape.add(btn_Editar);

        JPanel pnl_Lista = new JPanel(new BorderLayout(0, 4));
        pnl_Lista.setOpaque(false);

        JScrollPane scroll = new JScrollPane(tbl_Itens);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
                "Itens de Serviço cadastrados",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.decode("#666666")));
        scroll.setPreferredSize(new Dimension(0, 160));
        ScrollBarPadrao.aplicar(scroll);
        pnl_Lista.add(criarBarraBusca(), BorderLayout.NORTH);
        pnl_Lista.add(scroll, BorderLayout.CENTER);
        pnl_Lista.add(pnl_ListaRodape, BorderLayout.SOUTH);

        card.add(lbl_Titulo, BorderLayout.NORTH);
        card.add(pnl_Topo, BorderLayout.CENTER);
        card.add(pnl_Lista, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 40, 20, 40);
        add(card, gbc);
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        JLabel lbl = criarLabel("Peças Associadas (auto-atribuídas ao Orçamento)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cmb_Peca = new JComboBox<>();
        cmb_Peca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Peca.setBackground(Color.WHITE);

        JButton btn_AddPeca = new BotaoVidro("+ Adicionar Peça", Color.decode("#17A2B8"), true);
        btn_AddPeca.setPreferredSize(new Dimension(160, 32));
        btn_AddPeca.addActionListener(e -> adicionarPeca());

        JPanel pnl_Row = new JPanel(new BorderLayout(8, 0));
        pnl_Row.setOpaque(false);
        pnl_Row.add(cmb_Peca, BorderLayout.CENTER);
        pnl_Row.add(btn_AddPeca, BorderLayout.EAST);

        String[] cols = {"Peça"};
        mdl_Pecas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Pecas = new JTable(mdl_Pecas);
        tbl_Pecas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Pecas.setRowHeight(24);

        JButton btn_RemPeca = new BotaoVidro("Remover selecionada", Color.decode("#DC3545"), false);
        btn_RemPeca.addActionListener(e -> {
            int row = tbl_Pecas.getSelectedRow();
            if (row >= 0) {
                idPecasSelecionadas.remove(row);
                mdl_Pecas.removeRow(row);
            }
        });

        JScrollPane scrollPecas = new JScrollPane(tbl_Pecas);
        scrollPecas.setPreferredSize(new Dimension(0, 70));
        scrollPecas.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        ScrollBarPadrao.aplicar(scrollPecas);

        JLabel lbl_Hint = new JLabel("Ao adicionar este serviço a um orçamento, as peças aqui listadas serão incluídas automaticamente.");
        lbl_Hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_Hint.setForeground(Color.decode("#888888"));

        JPanel corpo = new JPanel(new BorderLayout(0, 4));
        corpo.setOpaque(false);
        corpo.add(pnl_Row, BorderLayout.NORTH);
        corpo.add(scrollPecas, BorderLayout.CENTER);
        JPanel pnl_Rem = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_Rem.setOpaque(false);
        pnl_Rem.add(btn_RemPeca);
        corpo.add(pnl_Rem, BorderLayout.SOUTH);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(corpo, BorderLayout.CENTER);
        sec.add(lbl_Hint, BorderLayout.SOUTH);
        return sec;
    }

    private void adicionarPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        idPecasSelecionadas.add(sel.peca.getIdCatalogoPeca());
        mdl_Pecas.addRow(new Object[]{sel.peca.getNomePopular()});
    }

    private void salvar() {
        String nome     = txt_Nome.getText().trim();
        String valorTxt = txt_Valor.getText().trim().replace(",", ".");
        String tipo     = rdb_Revisao.isSelected() ? "REVISAO" : "PADRAO";
        String sistema  = SISTEMAS[cmb_Sistema.getSelectedIndex()];

        if (nome.length() < 3) {
            DialogoAlerta.aviso(this, "O nome deve ter pelo menos 3 caracteres.", "Campo Inválido"); return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorTxt);
            if (valor < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe um valor válido (ex: 120.00).", "Campo Inválido"); return;
        }

        Integer validadeKm = null, validadeMeses = null;
        if ("PADRAO".equals(tipo)) {
            String kmTxt  = txt_ValidadeKm.getText().trim();
            String mesTxt = txt_ValidadeMeses.getText().trim();
            if (!kmTxt.isEmpty())  { try { validadeKm    = Integer.parseInt(kmTxt);  } catch (NumberFormatException ignored) {} }
            if (!mesTxt.isEmpty()) { try { validadeMeses = Integer.parseInt(mesTxt); } catch (NumberFormatException ignored) {} }
        }

        try {
            controller.salvarItemServico(nome, "", valor, tipo, sistema,
                    validadeKm, validadeMeses, new ArrayList<>(idPecasSelecionadas));
            DialogoAlerta.sucesso(this, "Item \"" + nome + "\" cadastrado com sucesso!", "Sucesso");
            txt_Nome.setText(""); txt_Valor.setText("");
            txt_ValidadeKm.setText(""); txt_ValidadeMeses.setText("");
            idPecasSelecionadas.clear();
            mdl_Pecas.setRowCount(0);
            rdb_Padrao.setSelected(true);
            pnl_Validade.setVisible(true);
            definirTextoBusca("");
            aplicarFiltro("");
            carregarLista();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao cadastrar item: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void carregarPecas() {
        cmb_Peca.removeAllItems();
        for (CatalogoPecaEntity p : controller.listarTodasPecas())
            cmb_Peca.addItem(new ItemPeca(p));
    }

    private void editarSelecionado() {
        int viewRow = tbl_Itens.getSelectedRow();
        int row = viewRow < 0 ? -1 : tbl_Itens.convertRowIndexToModel(viewRow); // considera o filtro da pesquisa
        if (row < 0 || row >= itensCadastrados.size()) {
            DialogoAlerta.aviso(this, "Selecione um item na lista para editar.", "Nenhum selecionado");
            return;
        }
        navegar(new V_EditarItemServico(controller, itensCadastrados.get(row)));
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    private void carregarLista() {
        mdl_Itens.setRowCount(0);
        itensCadastrados.clear();
        List<CatalogoServicoEntity> todos = controller.listarCatalogoServicos();
        itensCadastrados.addAll(todos);
        for (CatalogoServicoEntity i : todos) {
            String tipoLabel   = "REVISAO".equals(i.getTipo()) ? "Revisão" : "Padrão";
            String km   = i.getValidadeKm() != null ? i.getValidadeKm() + " km" : "—";
            String mes  = i.getValidadeMeses() != null ? i.getValidadeMeses() + " meses" : "—";
            mdl_Itens.addRow(new Object[]{
                    i.getNome(), i.getSistemaLabel(), tipoLabel,
                    String.format("R$ %.2f", i.getValor()), km, mes
            });
        }
    }

    // ===== helpers visuais =====
    private JPanel linha(int maxW, int maxH) {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(maxW, maxH));
        return p;
    }

    private JPanel bloco(String rotulo, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(criarLabel(rotulo), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JLabel criarLabel(String txt) {
        JLabel l = new JLabel(txt);
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
                BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        return f;
    }

    // ===== pesquisa na tabela de itens =====
    private JPanel criarBarraBusca() {
        lbl_Contagem = new JLabel(" ");
        lbl_Contagem.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_Contagem.setForeground(Color.decode("#888888"));

        txt_Busca = new CampoBusca();
        txt_Busca.setPreferredSize(new Dimension(280, 32));
        txt_Busca.setToolTipText("Pesquise por nome, sistema, tipo ou valor");

        // Lista de sugestões (aparece abaixo do campo, como na barra de um navegador)
        mdl_Sugestoes = new DefaultListModel<>();
        lst_Sugestoes = new JList<>(mdl_Sugestoes);
        lst_Sugestoes.setFocusable(false);
        lst_Sugestoes.setFixedCellHeight(ALTURA_SUGESTAO);
        lst_Sugestoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lst_Sugestoes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lst_Sugestoes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foco) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, i, sel, false);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                lbl.setOpaque(true);
                lbl.setForeground(Color.decode("#2B2E33"));
                lbl.setBackground(sel ? new Color(255, 173, 51, 70) : Color.WHITE);
                return lbl;
            }
        });
        lst_Sugestoes.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int idx = indiceSob(e.getPoint());
                if (idx >= 0) lst_Sugestoes.setSelectedIndex(idx);
            }
        });
        lst_Sugestoes.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int idx = indiceSob(e.getPoint());
                if (idx >= 0) selecionarSugestao(idx);
            }
        });

        pop_Sugestoes = new JPopupMenu();
        pop_Sugestoes.setFocusable(false);
        pop_Sugestoes.setBorder(BorderFactory.createLineBorder(Color.decode("#D7DEE7")));
        pop_Sugestoes.add(lst_Sugestoes);

        // Digitar atualiza as sugestões; campo vazio restaura a tabela inteira
        txt_Busca.getDocument().addDocumentListener(new DocumentListener() {
            private void mudou() {
                if (atualizandoBusca) return;
                if (txt_Busca.getText().trim().isEmpty()) aplicarFiltro("");
                atualizarSugestoes();
            }
            @Override public void insertUpdate(DocumentEvent e)  { mudou(); }
            @Override public void removeUpdate(DocumentEvent e)  { mudou(); }
            @Override public void changedUpdate(DocumentEvent e) { mudou(); }
        });
        txt_Busca.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { atualizarSugestoes(); }
            @Override public void focusLost(FocusEvent e)   { pop_Sugestoes.setVisible(false); }
        });
        txt_Busca.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!pop_Sugestoes.isVisible()) atualizarSugestoes();
            }
        });

        // Setas, Enter e Esc
        InputMap im = txt_Busca.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = txt_Busca.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "sugBaixo");
        am.put("sugBaixo", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { moverSugestao(1); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "sugCima");
        am.put("sugCima", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { moverSugestao(-1); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "sugFechar");
        am.put("sugFechar", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { pop_Sugestoes.setVisible(false); }
        });
        txt_Busca.addActionListener(e -> {
            int idx = lst_Sugestoes.getSelectedIndex();
            if (pop_Sugestoes.isVisible() && idx >= 0) definirTextoBusca(mdl_Sugestoes.get(idx));
            pop_Sugestoes.setVisible(false);
            aplicarFiltro(txt_Busca.getText());
        });

        sorter_Itens.addRowSorterListener(e -> atualizarContagem());
        atualizarContagem();

        JPanel pnl = new JPanel(new BorderLayout(10, 0));
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        JPanel pnl_Direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnl_Direita.setOpaque(false);
        pnl_Direita.add(txt_Busca);
        pnl_Direita.add(btn_Cadastrar);

        pnl.add(lbl_Contagem, BorderLayout.WEST);
        pnl.add(pnl_Direita, BorderLayout.EAST);
        return pnl;
    }

    private int indiceSob(Point p) {
        int idx = lst_Sugestoes.locationToIndex(p);
        if (idx < 0) return -1;
        Rectangle r = lst_Sugestoes.getCellBounds(idx, idx);
        return (r != null && r.contains(p)) ? idx : -1;
    }

    private void moverSugestao(int delta) {
        if (!pop_Sugestoes.isVisible()) atualizarSugestoes();
        int n = mdl_Sugestoes.getSize();
        if (n == 0) return;
        int idx = lst_Sugestoes.getSelectedIndex() + delta;
        if (idx < 0) idx = n - 1;
        if (idx >= n) idx = 0;
        lst_Sugestoes.setSelectedIndex(idx);
    }

    /** Sem texto: sugestões pré-estabelecidas. Com texto: nomes, sistemas e tipos que combinam. */
    private void atualizarSugestoes() {
        if (txt_Busca == null || !txt_Busca.isShowing()) return;
        String q = normalizar(txt_Busca.getText().trim());
        List<String> sugestoes = new ArrayList<>();

        if (q.isEmpty()) {
            sugestoes.addAll(Arrays.asList(SUGESTOES_PADRAO));
        } else {
            LinkedHashSet<String> base = new LinkedHashSet<>();
            for (CatalogoServicoEntity i : itensCadastrados)
                if (i.getNome() != null) base.add(i.getNome());
            base.addAll(Arrays.asList(SISTEMAS_LABEL));
            base.add("Revisão");
            base.add("Padrão");

            List<String> comecam = new ArrayList<>(), contem = new ArrayList<>();
            for (String c : base) {
                String n = normalizar(c);
                if (n.startsWith(q)) comecam.add(c);
                else if (n.contains(q)) contem.add(c);
            }
            sugestoes.addAll(comecam);
            sugestoes.addAll(contem);
            if (sugestoes.size() > MAX_SUGESTOES) sugestoes = sugestoes.subList(0, MAX_SUGESTOES);
        }

        mdl_Sugestoes.clear();
        for (String s : sugestoes) mdl_Sugestoes.addElement(s);
        lst_Sugestoes.clearSelection();

        if (sugestoes.isEmpty()) {
            pop_Sugestoes.setVisible(false);
            return;
        }
        lst_Sugestoes.setFixedCellWidth(Math.max(100, txt_Busca.getWidth() - 2));
        if (pop_Sugestoes.isVisible()) pop_Sugestoes.pack();
        else pop_Sugestoes.show(txt_Busca, 0, txt_Busca.getHeight() + 2);
    }

    private void selecionarSugestao(int idx) {
        definirTextoBusca(mdl_Sugestoes.get(idx));
        pop_Sugestoes.setVisible(false);
        aplicarFiltro(txt_Busca.getText());
    }

    private void definirTextoBusca(String texto) {
        if (txt_Busca == null) return;
        atualizandoBusca = true;
        txt_Busca.setText(texto);
        atualizandoBusca = false;
    }

    /** Filtra a tabela: todas as palavras digitadas precisam aparecer em alguma coluna da linha. */
    private void aplicarFiltro(String texto) {
        String q = normalizar(texto.trim());
        if (q.isEmpty()) {
            sorter_Itens.setRowFilter(null);
            return;
        }
        final String[] termos = q.split("\\s+");
        sorter_Itens.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < entry.getValueCount(); c++)
                    sb.append(normalizar(String.valueOf(entry.getValue(c)))).append(' ');
                String linha = sb.toString();
                for (String t : termos) if (!linha.contains(t)) return false;
                return true;
            }
        });
    }

    private void atualizarContagem() {
        if (lbl_Contagem == null) return;
        int total = mdl_Itens.getRowCount();
        int visiveis = tbl_Itens.getRowCount();
        if (total > 0 && visiveis == 0) lbl_Contagem.setText("Nenhum item encontrado");
        else if (visiveis == total) lbl_Contagem.setText(total + (total == 1 ? " item" : " itens"));
        else lbl_Contagem.setText(visiveis + " de " + total + " itens");
    }

    private static String normalizar(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }

    // ===== inner classes =====

    /**
     * Botão em glassmorphism: vidro translúcido tingido pela cor do botão, brilho
     * difuso no topo, contorno fino com realce claro e reação a hover/clique.
     * "preenchido" = vidro com cor forte (ação principal); senão, vidro suave
     * com texto colorido (ação secundária).
     */
    private static class BotaoVidro extends JButton {
        private static final int RAIO = 12;
        private final Color cor;
        private final boolean preenchido;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoVidro(String texto, Color cor, boolean preenchido) {
            super(texto);
            this.cor = cor;
            this.preenchido = preenchido;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(preenchido ? Color.WHITE : cor.darker());
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)   { sobreMouse = false; pressionado = false; repaint(); }
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
            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 4, RAIO, RAIO);

            // Sombra suave, tingida com a cor do botão
            g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), preenchido ? 70 : 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 4, RAIO, RAIO));

            // Corpo de vidro translúcido
            int alfaTopo = preenchido ? 205 : 42;
            int alfaBase = preenchido ? 160 : 20;
            if (sobreMouse)  { alfaTopo += preenchido ? 25 : 28; alfaBase += preenchido ? 25 : 18; }
            if (pressionado) { alfaTopo += preenchido ? 25 : 28; alfaBase += preenchido ? 40 : 30; }
            alfaTopo = Math.min(alfaTopo, 255);
            alfaBase = Math.min(alfaBase, 255);
            g2.setPaint(new GradientPaint(
                    0, 0, new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), alfaTopo),
                    0, h, new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), alfaBase)));
            g2.fill(forma);

            // Brilho difuso na metade de cima
            Shape clipOriginal = g2.getClip();
            g2.clip(forma);
            g2.setPaint(new GradientPaint(
                    0, 0, new Color(255, 255, 255, preenchido ? 105 : 150),
                    0, h * 0.6f, new Color(255, 255, 255, 0)));
            g2.fillRect(0, 0, w, h);
            g2.setClip(clipOriginal);

            // Contorno fino + realce claro na borda superior
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), preenchido ? 190 : 130));
            g2.draw(forma);
            g2.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255, 255, 255, 190));
            g2.draw(new Line2D.Double(RAIO * 0.7, 1.4, w - RAIO * 0.7 - 1, 1.4));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Campo de pesquisa em vidro, com lupa, texto de dica e botão "×" para limpar.
     */
    private static class CampoBusca extends JTextField {
        private static final int RAIO = 10;
        private boolean focado = false;

        CampoBusca() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(Color.decode("#2B2E33"));
            setCaretColor(Color.decode("#2B2E33"));
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(5, 34, 5, 28));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e)   { focado = false; repaint(); }
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (!getText().isEmpty() && e.getX() > getWidth() - 28) setText("");
                }
            });
            addMouseMotionListener(new MouseAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    boolean sobreX = !getText().isEmpty() && e.getX() > getWidth() - 28;
                    setCursor(Cursor.getPredefinedCursor(sobreX ? Cursor.HAND_CURSOR : Cursor.TEXT_CURSOR));
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO, RAIO));
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 215), 0, h, new Color(255, 255, 255, 150)));
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO, RAIO));
            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO - 4, RAIO - 4));
            g2.dispose();

            super.paintComponent(g);

            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double cy = (h - 2) / 2.0;
            Color corIcone = focado ? new Color(255, 153, 0) : Color.decode("#8A94A0");

            // Lupa
            g3.setColor(corIcone);
            g3.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g3.draw(new Ellipse2D.Double(11, cy - 6.5, 9, 9));
            g3.draw(new Line2D.Double(19, cy + 2, 22.5, cy + 5.5));

            // Texto de dica
            if (getText().isEmpty()) {
                g3.setFont(getFont().deriveFont(Font.ITALIC));
                g3.setColor(Color.decode("#9AA3AE"));
                FontMetrics fm = g3.getFontMetrics();
                int y = (h - fm.getHeight()) / 2 + fm.getAscent() - 1;
                g3.drawString("Pesquisar itens...", getInsets().left, y);
            } else {
                // Botão de limpar (×)
                double cx = w - 15;
                g3.setColor(Color.decode("#8A94A0"));
                g3.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g3.draw(new Line2D.Double(cx - 3.5, cy - 3.5, cx + 3.5, cy + 3.5));
                g3.draw(new Line2D.Double(cx - 3.5, cy + 3.5, cx + 3.5, cy - 3.5));
            }
            g3.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setStroke(new BasicStroke(focado ? 1.6f : 1f));
            g2.setColor(focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130));
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO, RAIO));
            g2.dispose();
        }
    }

    private static class ItemPeca {
        final CatalogoPecaEntity peca;
        ItemPeca(CatalogoPecaEntity p) { this.peca = p; }
        @Override public String toString() { return peca != null ? peca.getNomePopular() : "(sem peça)"; }
    }

    private static class FiltroDecimal extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9.,]", ""), a);
        }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9.,]", ""), a);
        }
    }

    private static class FiltroInteiro extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9]", ""), a);
        }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9]", ""), a);
        }
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio; private final Color cor;
        RoundedBorder(int r, Color c) { this.raio = r; this.cor = c; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2.dispose();
        }
    }
}