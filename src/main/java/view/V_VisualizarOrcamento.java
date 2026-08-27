package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.estoque.PecaEntity;
import br.com.oficina.usuario.FuncionarioEntity;
import controller.OficinaController;
import model.Cliente;
import model.Orcamento;
import model.OrdemDeServico;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class V_VisualizarOrcamento extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mexa só aqui para alterar tudo de uma vez
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_BORDA_CARD   = Color.decode("#DDE3EA");

    // Rodapé de totais — vidro âmbar/laranja (tema original preservado)
    private static final Color COR_TOTAL_TOPO   = Color.decode("#FFF8F0");
    private static final Color COR_TOTAL_BASE   = Color.decode("#FFE9CC");
    private static final Color COR_TOTAL_BORDA  = Color.decode("#FF9900");

    // Vidro cinza claro estilo Windows 7 (Aero) — cabeçalho das tabelas
    private static final Color COR_AERO_TOPO_A  = Color.decode("#FBFBFC");
    private static final Color COR_AERO_TOPO_B  = Color.decode("#ECEEF1");
    private static final Color COR_AERO_BASE_A  = Color.decode("#DADDE2");
    private static final Color COR_AERO_BASE_B  = Color.decode("#EFF1F3");
    private static final Color COR_AERO_BORDA   = Color.decode("#B6BCC4");
    private static final Color COR_AERO_SEPARA  = Color.decode("#CCD1D8");
    private static final Color COR_AERO_TEXTO   = Color.decode("#3A4149");

    // Tabela de dados
    private static final Color COR_TABELA_FUNDO   = Color.WHITE;
    private static final Color COR_TABELA_GRADE   = Color.decode("#E3E9F0");
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");

    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_BOTAO = 13;
    private static final int LARGURA_BOTAO       = 130;
    private static final int ALTURA_BOTAO        = 34;
    private static final int ALTURA_CABECALHO    = 28;
    private static final int ALTURA_LINHA_TABELA = 26;

    private final OficinaController controller;
    private final Orcamento orcamento;

    private LinkedHashMap<CatalogoServicoEntity, Double> dadosItens = new LinkedHashMap<>();
    private List<Object[]> dadosPecas = new ArrayList<>();
    private double subtotalServicos = 0;
    private double subtotalPecas = 0;

    public V_VisualizarOrcamento(OficinaController controller, Orcamento orcamento) {
        this.controller = controller;
        this.orcamento = orcamento;
        carregarDados();
        setBackground(COR_FUNDO_PAGINA);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
    }

    private void carregarDados() {
        try {
            dadosItens = controller.listarItensOrcamentoComValor(orcamento.getIdOrcamento());
            subtotalServicos = dadosItens.values().stream().mapToDouble(Double::doubleValue).sum();
        } catch (Exception ignored) {}
        try {
            dadosPecas = controller.listarPecasOrcamentoComValor(orcamento.getIdOrcamento());
            subtotalPecas = dadosPecas.stream().mapToDouble(t -> (Double) t[1]).sum();
        } catch (Exception ignored) {}
    }

    private String resolverNomeProprietario() {
        long id = orcamento.getIdCliente();
        Cliente c = controller.cliente_id(id);
        if (c != null) return c.getNome();
        for (FuncionarioEntity f : controller.listarFuncionarios())
            if (f.getIdUsuario() != null && f.getIdUsuario() == id)
                return f.getNome() + " (Func.)";
        return "—";
    }

    private void construir() {
        add(criarTopo(), BorderLayout.NORTH);

        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);
        corpo.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        corpo.add(criarBadgeStatus());
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(criarCardInfo());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarCardReclamacao());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarSecaoItens());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarSecaoPecas());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarRodapeTotal());
        corpo.add(Box.createVerticalStrut(8));

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scroll);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel criarTopo() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);

        JLabel lbl = new JLabel("Orçamentos > Detalhes do Orçamento");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(COR_TITULO);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);

        String status = orcamento.getStatus();
        if ("PENDENTE".equalsIgnoreCase(status)) {
            BotaoAcao btnAprovar = criarBotao("Aprovar", "#28A745");
            BotaoAcao btnReprovar = criarBotao("Reprovar", "#DC3545");
            btnAprovar.addActionListener(e -> mudarStatus(true));
            btnReprovar.addActionListener(e -> mudarStatus(false));
            botoes.add(btnReprovar);
            botoes.add(btnAprovar);
        } else if ("APROVADO".equalsIgnoreCase(status)) {
            BotaoAcao btnGerar = criarBotao("Gerar Serviço", "#17A2B8");
            btnGerar.addActionListener(e -> gerarServico());
            botoes.add(btnGerar);
        }

        if (!"REPROVADO".equalsIgnoreCase(status)) {
            BotaoAcao btnEditar = criarBotao("✎ Editar", "#2980B9");
            btnEditar.addActionListener(e -> navegar(new V_EditarOrcamento(controller, orcamento.getIdOrcamento())));
            botoes.add(btnEditar);
        }

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVoltar.setForeground(COR_LABEL);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnVoltar.setForeground(COR_TITULO); }
            @Override public void mouseExited(MouseEvent e)  { btnVoltar.setForeground(COR_LABEL); }
        });
        btnVoltar.addActionListener(e -> navegar(new V_AprovarOrcamento(controller)));
        botoes.add(btnVoltar);

        pnl.add(lbl, BorderLayout.WEST);
        pnl.add(botoes, BorderLayout.EAST);
        return pnl;
    }

    private JPanel criarBadgeStatus() {
        String status = orcamento.getStatus() != null ? orcamento.getStatus().toUpperCase() : "—";
        Color cor = switch (status) {
            case "APROVADO"  -> Color.decode("#28A745");
            case "REPROVADO" -> Color.decode("#DC3545");
            default          -> Color.decode("#FF9900");
        };

        String codigo = orcamento.getCodigo().isEmpty()
                ? String.format("#%04d", orcamento.getIdOrcamento())
                : orcamento.getCodigo();

        JPanel pnl = new PainelStatusVidro(new BorderLayout(12, 0), cor);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lblCodigo = new JLabel("Orçamento " + codigo);
        lblCodigo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCodigo.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel(status);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(Color.WHITE);

        pnl.add(lblCodigo, BorderLayout.WEST);
        pnl.add(lblStatus, BorderLayout.EAST);
        return pnl;
    }

    private JPanel criarCardInfo() {
        JPanel card = new PainelGradiente(new GridLayout(0, 4, 16, 10), COR_CARD_TOPO, COR_CARD_BASE, COR_BORDA_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        addCampo(card, "Cliente", resolverNomeProprietario());
        addCampo(card, "Veículo (Placa)", valorOuTracinho(orcamento.getPlacaVeiculo()));
        addCampo(card, "Responsável", valorOuTracinho(orcamento.getResponsavel()));
        addCampo(card, "Data de Criação", valorOuTracinho(orcamento.getDataCriacao()));

        return card;
    }

    private JPanel criarCardReclamacao() {
        JPanel card = new PainelGradiente(new BorderLayout(0, 6), COR_CARD_TOPO, COR_CARD_BASE, COR_BORDA_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel lbl = new JLabel("Reclamação / Descrição do Problema");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COR_LABEL);

        JLabel val = new JLabel("<html>" + valorOuTracinho(orcamento.getReclamacao()).replace("\n", "<br>") + "</html>");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(COR_TEXTO_CAMPO);

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarSecaoItens() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel lbl = new JLabel("Itens de Serviço");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COR_TITULO);

        String[] cols = {"Serviço", "Tipo", "Valor (R$)"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (var entry : dadosItens.entrySet()) {
            CatalogoServicoEntity item = entry.getKey();
            String tipo = "REVISAO".equals(item.getTipo()) ? "Revisão" : "Padrão";
            mdl.addRow(new Object[]{item.getNome(), tipo, String.format("R$ %.2f", entry.getValue())});
        }

        JTable tbl = criarTabela(mdl);
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.getViewport().setBackground(COR_TABELA_FUNDO);
        scroll.getViewport().setOpaque(true);
        scroll.setOpaque(false);
        ScrollBarPadrao.aplicar(scroll);
        scroll.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(scroll, BorderLayout.CENTER);
        return sec;
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel lbl = new JLabel("Peças a Substituir");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COR_TITULO);

        sec.add(lbl, BorderLayout.NORTH);

        if (dadosPecas.isEmpty()) {
            JLabel lblVazio = new JLabel("Nenhuma peça vinculada a este orçamento.");
            lblVazio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblVazio.setForeground(COR_LABEL);
            sec.add(lblVazio, BorderLayout.CENTER);
        } else {
            String[] cols = {"Peça (Popular)", "Nome Técnico", "Fabricante", "Valor (R$)"};
            DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Object[] triple : dadosPecas) {
                PecaEntity peca = (PecaEntity) triple[0];
                double valor = (Double) triple[1];
                String nomeTecnico = triple.length > 2 ? (String) triple[2] : "";
                String fabricante = triple.length > 3 ? (String) triple[3] : "";
                mdl.addRow(new Object[]{peca.getNomeExibicao(), nomeTecnico,
                        fabricante.isBlank() ? "—" : fabricante, String.format("R$ %.2f", valor)});
            }
            JTable tbl = criarTabela(mdl);
            JScrollPane scroll = new JScrollPane(tbl);
            scroll.getViewport().setBackground(COR_TABELA_FUNDO);
            scroll.getViewport().setOpaque(true);
            scroll.setOpaque(false);
            scroll.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
            ScrollBarPadrao.aplicar(scroll);
            sec.add(scroll, BorderLayout.CENTER);
        }
        return sec;
    }

    private JPanel criarRodapeTotal() {
        JPanel pnl = new PainelGradiente(new GridLayout(3, 2, 0, 4), COR_TOTAL_TOPO, COR_TOTAL_BASE, COR_TOTAL_BORDA);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        addLinhaTotais(pnl, "Serviços:", String.format("R$ %.2f", subtotalServicos), "#57626F", 12, Font.PLAIN);
        addLinhaTotais(pnl, "Peças:", String.format("R$ %.2f", subtotalPecas), "#57626F", 12, Font.PLAIN);
        addLinhaTotais(pnl, "Total:", String.format("R$ %.2f", subtotalServicos + subtotalPecas), "#FF9900", 15, Font.BOLD);

        return pnl;
    }

    private void addLinhaTotais(JPanel pnl, String rotulo, String valor, String corHex, int tamanho, int estilo) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("Segoe UI", estilo, tamanho));
        lbl.setForeground(COR_LABEL);

        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", estilo, tamanho));
        val.setForeground(Color.decode(corHex));
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        pnl.add(lbl);
        pnl.add(val);
    }

    private void mudarStatus(boolean aprovar) {
        if (!confirmarComSenha(aprovar ? "aprovar este orçamento" : "reprovar este orçamento")) return;
        try {
            if (aprovar) controller.aprovarOrcamento(orcamento.getIdOrcamento());
            else controller.reprovarOrcamento(orcamento.getIdOrcamento());
            DialogoAlerta.sucesso(this, "Orçamento " + (aprovar ? "aprovado" : "reprovado") + " com sucesso!", "Sucesso");
            navegar(new V_AprovarOrcamento(controller));
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void gerarServico() {
        JTextField txtTitulo = new JTextField();
        JComboBox<String> cmbTipo = new JComboBox<>();
        for (OrdemDeServico.TipoServicoOS t : OrdemDeServico.TipoServicoOS.values())
            cmbTipo.addItem(t.getLabel());
        JComboBox<String> cmbManutencao = new JComboBox<>();
        for (OrdemDeServico.TipoManutencao m : OrdemDeServico.TipoManutencao.values())
            cmbManutencao.addItem(m.getLabel());
        JTextField txtData = new JTextField(LocalDate.now().toString());
        JTextField txtKm = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Título do Serviço:")); form.add(txtTitulo);
        form.add(new JLabel("Tipo de Serviço:"));   form.add(cmbTipo);
        form.add(new JLabel("Tipo de Manutenção:")); form.add(cmbManutencao);
        form.add(new JLabel("Data (AAAA-MM-DD):")); form.add(txtData);
        form.add(new JLabel("KM atual:"));           form.add(txtKm);

        int r = JOptionPane.showConfirmDialog(this, form,
                "Gerar Serviço do Orçamento " + orcamento.getCodigo(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String titulo = txtTitulo.getText().trim();
        if (titulo.length() < 3) { aviso("Título deve ter pelo menos 3 caracteres."); return; }
        int km;
        try { km = Integer.parseInt(txtKm.getText().trim()); if (km < 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { aviso("Informe um KM válido."); return; }

        try {
            OrdemDeServico.TipoServicoOS tipoEnum =
                    OrdemDeServico.TipoServicoOS.fromLabel((String) cmbTipo.getSelectedItem());
            OrdemDeServico.TipoManutencao manutEnum =
                    OrdemDeServico.TipoManutencao.fromLabel((String) cmbManutencao.getSelectedItem());
            controller.abrirOSDeOrcamento(titulo, tipoEnum.name(), manutEnum.name(),
                    txtData.getText().trim(), km, orcamento.getIdVeiculo(), orcamento.getIdOrcamento());
            DialogoAlerta.sucesso(this, "Ordem de Serviço gerada com sucesso!", "Sucesso");
            navegar(new V_AprovarOrcamento(controller));
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao gerar serviço: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    private void aviso(String msg) {
        DialogoAlerta.aviso(this, msg, "Atenção");
    }

    private boolean confirmarComSenha(String acao) {
        String senha = DialogoConfirmacao.pedirSenha(this, "Confirme a senha para " + acao);
        if (senha == null) return false;
        if (senha.isEmpty() || !controller.confirmarSenha(senha)) {
            DialogoAlerta.erro(this, "Senha incorreta.", "Acesso Negado");
            return false;
        }
        return true;
    }

    private void addCampo(JPanel pnl, String rotulo, String valor) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COR_LABEL);
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(COR_TEXTO_CAMPO);
        pnl.add(lbl);
        pnl.add(val);
    }

    /** Monta a tabela com o vidro Aero cinza no cabeçalho e fundo branco nas células. */
    private JTable criarTabela(DefaultTableModel mdl) {
        JTable tbl = new JTable(mdl);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setRowHeight(ALTURA_LINHA_TABELA);
        tbl.setBackground(COR_TABELA_FUNDO);
        tbl.setForeground(COR_TEXTO_CAMPO);
        tbl.setOpaque(true);
        tbl.setShowGrid(true);
        tbl.setGridColor(COR_TABELA_GRADE);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setSelectionBackground(COR_TABELA_SELECAO);
        tbl.setSelectionForeground(COR_TEXTO_CAMPO);
        tbl.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());

        JTableHeader cabecalho = tbl.getTableHeader();
        cabecalho.setDefaultRenderer(new CabecalhoVidroClaro());
        cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, ALTURA_CABECALHO));
        cabecalho.setReorderingAllowed(false);
        cabecalho.setOpaque(false);
        return tbl;
    }

    /** Cria um BotaoAcao com as sombras/hover derivadas automaticamente da cor base informada. */
    private BotaoAcao criarBotao(String texto, String corHex) {
        Color base = Color.decode(corHex);
        BotaoAcao btn = new BotaoAcao(texto, base, clarear(base), escurecer(base));
        btn.setPreferredSize(new Dimension(LARGURA_BOTAO, ALTURA_BOTAO));
        return btn;
    }

    private String valorOuTracinho(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    // =========================================================================
    // UTILITÁRIOS DE COR
    // =========================================================================
    private static Color clarear(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1] * 0.75f, Math.min(1f, hsb[2] * 1.18f));
    }

    private static Color escurecer(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.82f);
    }

    // =========================================================================
    // INNER CLASSES — RENDERERS DA TABELA
    // =========================================================================

    /** Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7). */
    private static class CabecalhoVidroClaro extends JLabel implements TableCellRenderer {

        CabecalhoVidroClaro() {
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
     * Painel com fundo em gradiente suave, opcionalmente com uma borda
     * arredondada colorida — usado nos cartões de informação e no rodapé
     * de totais, harmonizando com o restante das telas.
     */
    private static class PainelGradiente extends JPanel {
        private final Color corTopo;
        private final Color corBase;
        private final Color corBorda;

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase) {
            this(layout, corTopo, corBase, null);
        }

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase, Color corBorda) {
            super(layout);
            this.corTopo = corTopo;
            this.corBase = corBase;
            this.corBorda = corBorda;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, corTopo, 0, h, corBase);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14));

            if (corBorda != null) {
                g2.setStroke(new BasicStroke(1.3f));
                g2.setColor(corBorda);
                g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.5, h - 1.5, 14, 14));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Banner de status em vidro: gradiente da cor do status (verde/vermelho/
     * laranja) com um leve brilho no topo, mesma linguagem visual dos botões.
     */
    private static class PainelStatusVidro extends JPanel {
        private final Color base;

        PainelStatusVidro(LayoutManager layout, Color base) {
            super(layout);
            this.base = base;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, clarear(base), 0, h, base);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14));

            g2.setColor(new Color(255, 255, 255, 55));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.35), 10, 10));

            g2.setColor(escurecer(base));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação com a mesma linguagem visual usada em todo o sistema:
     * cantos arredondados, sombra suave, reflexo no topo e reação a
     * hover/clique. A cor é configurável.
     */
    private static class BotaoAcao extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
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
            setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
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

            g2.setColor(new Color(0, 0, 0, 35));
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