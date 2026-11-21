package view;

import controller.OficinaController;
import model.Orcamento;
import model.OrdemDeServico;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abertura de uma Ordem de Serviço a partir de um orçamento APROVADO que ainda não tem OS.
 *
 * Visual alinhado ao padrão em vidro (glassmorphism) usado em V_EditarOrcamento /
 * V_CadastrarOrcamento / V_VisualizarOrcamento — mesma paleta, mesmo cabeçalho Aero,
 * campos arredondados e botões de vidro. A antiga tabela de orçamentos foi substituída
 * por uma caixa de seleção + um resumo somente-leitura do orçamento escolhido.
 */
public class V_CadastrarServico extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mesma linguagem visual das telas de orçamento
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_BORDA_CARD   = Color.decode("#DDE3EA");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_LABEL_SUTIL  = Color.decode("#8A94A3");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_AERO_TEXTO   = Color.decode("#3A4149");
    private static final Color COR_LARANJA      = Color.decode("#FF9900");

    // Lista suspensa das caixas de seleção — precisa ser SÓLIDA (o arredondado é só no contorno)
    private static final Color COR_POPUP_FUNDO   = Color.decode("#FFFFFF");
    private static final Color COR_POPUP_SELECAO = Color.decode("#FFE4BF");
    private static final Color COR_POPUP_BORDA   = Color.decode("#C3CDDA");
    private static final int   RAIO_COMBO        = 14;

    // =========================================================================
    // COMPONENTES
    // =========================================================================
    private JComboBox<ItemOrcamento> cbb_Orcamento;
    private JTextField txt_Titulo, txt_Data, txt_Km;
    private JComboBox<String> cbb_Tipo;
    private JComboBox<String> cbb_Manutencao;
    private JButton btn_Criar;

    // Resumo somente-leitura do orçamento selecionado
    private JLabel val_Cliente, val_Veiculo, val_Responsavel, val_Valor, val_Reclamacao;

    private List<Orcamento> orcamentos = new ArrayList<>();
    private Orcamento selecionado;
    private boolean carregando = false;

    private final OficinaController controller;

    public V_CadastrarServico(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        iniciarComponentes();
        carregarDados();
    }

    // =========================================================================
    // LAYOUT
    // =========================================================================

    private void iniciarComponentes() {
        JPanel card = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        card.setPreferredSize(new Dimension(680, 640));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // ---- cabeçalho em vidro Aero ----
        JPanel headerGlass = new CabecalhoVidro();
        headerGlass.setLayout(new BorderLayout());
        headerGlass.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        headerGlass.setPreferredSize(new Dimension(10, 42));
        headerGlass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel titulo = new JLabel("Serviços > Nova Ordem de Serviço");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_AERO_TEXTO);
        headerGlass.add(titulo, BorderLayout.WEST);

        // ---- formulário ----
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        form.add(construirSecaoOrcamento());
        form.add(Box.createVerticalStrut(12));
        form.add(construirResumo());
        form.add(Box.createVerticalStrut(16));
        form.add(construirSecaoDados());

        // ---- rodapé: Voltar + Abrir OS ----
        BotaoVidro btn_Voltar = botaoAcao("← Voltar", "#8A94A3", 130, 40);
        btn_Voltar.addActionListener(e -> navegar(new V_VisualizarServicos(controller)));

        btn_Criar = new BotaoVidro("ABRIR ORDEM DE SERVIÇO", COR_LARANJA);
        btn_Criar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Criar.setPreferredSize(new Dimension(290, 45));
        btn_Criar.setEnabled(false);
        btn_Criar.addActionListener(e -> criarOS());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btn_Voltar);
        pnlBtn.add(btn_Criar);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scrollForm);

        card.add(headerGlass, BorderLayout.NORTH);
        card.add(scrollForm, BorderLayout.CENTER);
        card.add(pnlBtn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(card, gbc);
    }

    /** Caixa de seleção do orçamento aprovado (substitui a antiga tabela) + botão de atualizar a lista. */
    private JPanel construirSecaoOrcamento() {
        JPanel sec = new JPanel(new BorderLayout(0, 4));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel lbl = rotulo("Orçamento aprovado *");

        cbb_Orcamento = new JComboBox<>();
        cbb_Orcamento.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        estilizarCombo(cbb_Orcamento);
        cbb_Orcamento.setPreferredSize(new Dimension(0, 34));
        cbb_Orcamento.addActionListener(e -> onSelecionarOrcamento());

        BotaoVidro btn_Atualizar = botaoAcao("↺ Atualizar", "#17A2B8", 120, 34);
        btn_Atualizar.setToolTipText("Recarregar a lista de orçamentos aprovados sem OS");
        btn_Atualizar.addActionListener(e -> carregarDados());

        JPanel linha = new JPanel(new BorderLayout(8, 0));
        linha.setOpaque(false);
        linha.add(cbb_Orcamento, BorderLayout.CENTER);
        linha.add(btn_Atualizar, BorderLayout.EAST);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(linha, BorderLayout.CENTER);
        return sec;
    }

    /** Resumo somente-leitura do orçamento escolhido (cliente, veículo, responsável, valor, reclamação). */
    private JPanel construirResumo() {
        JPanel pnl = new PainelGradiente(new GridLayout(0, 2, 8, 4), COR_CARD_TOPO, COR_CARD_BASE, COR_BORDA_CARD);
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        val_Cliente     = valorInfo();
        val_Veiculo     = valorInfo();
        val_Responsavel = valorInfo();
        val_Valor       = valorInfo();
        val_Reclamacao  = valorInfo();

        pnl.add(rotuloInfo("Cliente:"));      pnl.add(val_Cliente);
        pnl.add(rotuloInfo("Veículo:"));      pnl.add(val_Veiculo);
        pnl.add(rotuloInfo("Responsável:"));  pnl.add(val_Responsavel);
        pnl.add(rotuloInfo("Valor:"));        pnl.add(val_Valor);
        pnl.add(rotuloInfo("Reclamação:"));   pnl.add(val_Reclamacao);
        return pnl;
    }

    /** Campos de dados da OS: título, tipo, manutenção, data e KM. */
    private JPanel construirSecaoDados() {
        JPanel sec = new JPanel();
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

        txt_Titulo = new CampoArredondado();
        txt_Titulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Titulo.setPreferredSize(new Dimension(0, 34));
        txt_Titulo.setEnabled(false);
        ((AbstractDocument) txt_Titulo.getDocument()).setDocumentFilter(new FiltroTexto());

        cbb_Tipo = new JComboBox<>();
        cbb_Tipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        estilizarCombo(cbb_Tipo);
        cbb_Tipo.setPreferredSize(new Dimension(0, 34));
        cbb_Tipo.setEnabled(false);

        cbb_Manutencao = new JComboBox<>();
        cbb_Manutencao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        estilizarCombo(cbb_Manutencao);
        cbb_Manutencao.setPreferredSize(new Dimension(0, 34));
        cbb_Manutencao.setEnabled(false);

        txt_Data = new CampoArredondado();
        txt_Data.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Data.setPreferredSize(new Dimension(0, 34));
        txt_Data.setText(LocalDate.now().toString());
        txt_Data.setEnabled(false);

        txt_Km = new CampoArredondado();
        txt_Km.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Km.setPreferredSize(new Dimension(0, 34));
        txt_Km.setEnabled(false);
        ((AbstractDocument) txt_Km.getDocument()).setDocumentFilter(new FiltroDigitos(8));

        // Linha 1: Título (largura total)
        JPanel linhaTitulo = campoVertical("Título da OS *", txt_Titulo);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        // Linha 2: Tipo de Serviço | Manutenção
        JPanel linhaTipos = new JPanel(new GridLayout(1, 2, 14, 0));
        linhaTipos.setOpaque(false);
        linhaTipos.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaTipos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        linhaTipos.add(campoVertical("Tipo de Serviço *", cbb_Tipo));
        linhaTipos.add(campoVertical("Manutenção *", cbb_Manutencao));

        // Linha 3: Data | KM Atual
        JPanel linhaDataKm = new JPanel(new GridLayout(1, 2, 14, 0));
        linhaDataKm.setOpaque(false);
        linhaDataKm.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaDataKm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        linhaDataKm.add(campoVertical("Data *", txt_Data));
        linhaDataKm.add(campoVertical("KM Atual *", txt_Km));

        sec.add(linhaTitulo);
        sec.add(Box.createVerticalStrut(12));
        sec.add(linhaTipos);
        sec.add(Box.createVerticalStrut(12));
        sec.add(linhaDataKm);
        return sec;
    }

    // =========================================================================
    // LÓGICA
    // =========================================================================

    private void carregarDados() {
        if (controller == null) return;

        carregando = true;

        cbb_Tipo.removeAllItems();
        for (OrdemDeServico.TipoServicoOS t : OrdemDeServico.TipoServicoOS.values())
            cbb_Tipo.addItem(t.getLabel());

        cbb_Manutencao.removeAllItems();
        for (OrdemDeServico.TipoManutencao m : OrdemDeServico.TipoManutencao.values())
            cbb_Manutencao.addItem(m.getLabel());

        orcamentos = controller.listarOrcamentosAprovadosSemOS();

        cbb_Orcamento.removeAllItems();
        cbb_Orcamento.addItem(new ItemOrcamento(null, orcamentos.isEmpty()
                ? "(nenhum orçamento aprovado sem OS)"
                : "Selecione um orçamento aprovado..."));
        for (Orcamento o : orcamentos) cbb_Orcamento.addItem(new ItemOrcamento(o, null));
        cbb_Orcamento.setSelectedIndex(0);

        carregando = false;

        selecionado = null;
        atualizarResumo();
        setFormEnabled(false);
    }

    private void onSelecionarOrcamento() {
        if (carregando) return;

        ItemOrcamento item = (ItemOrcamento) cbb_Orcamento.getSelectedItem();
        if (item == null || item.orcamento == null) {
            selecionado = null;
            atualizarResumo();
            setFormEnabled(false);
            return;
        }
        selecionado = item.orcamento;
        atualizarResumo();
        txt_Titulo.setText(selecionado.getReclamacao());
        txt_Data.setText(LocalDate.now().toString());
        txt_Km.setText("");
        setFormEnabled(true);
    }

    private void atualizarResumo() {
        if (selecionado == null) {
            val_Cliente.setText("—");
            val_Veiculo.setText("—");
            val_Responsavel.setText("—");
            val_Valor.setText("—");
            val_Reclamacao.setText("—");
            val_Reclamacao.setToolTipText(null);
            return;
        }
        val_Cliente.setText(valorOuTraco(selecionado.getNomeCliente()));
        val_Veiculo.setText(valorOuTraco(selecionado.getPlacaVeiculo()));
        val_Responsavel.setText(valorOuTraco(selecionado.getResponsavel()));
        val_Valor.setText(String.format("R$ %.2f", selecionado.getValor()));
        val_Reclamacao.setText(valorOuTraco(selecionado.getReclamacao()));
        val_Reclamacao.setToolTipText(selecionado.getReclamacao());
    }

    private String valorOuTraco(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    private void setFormEnabled(boolean enabled) {
        txt_Titulo.setEnabled(enabled);
        cbb_Tipo.setEnabled(enabled);
        cbb_Manutencao.setEnabled(enabled);
        txt_Data.setEnabled(enabled);
        txt_Km.setEnabled(enabled);
        btn_Criar.setEnabled(enabled);
        if (!enabled) {
            txt_Titulo.setText("");
            txt_Km.setText("");
            txt_Data.setText(LocalDate.now().toString());
        }
    }

    private void criarOS() {
        if (selecionado == null) return;

        String titulo = txt_Titulo.getText().trim();
        String data   = txt_Data.getText().trim();
        String kmText = txt_Km.getText().trim();

        StringBuilder erros = new StringBuilder();
        if (titulo.length() < 3) erros.append("• Título deve ter pelo menos 3 caracteres.\n");
        if (data.isEmpty())       erros.append("• Informe a data do serviço.\n");
        int km = 0;
        try {
            if (kmText.isEmpty()) throw new NumberFormatException();
            km = Integer.parseInt(kmText);
            if (km < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            erros.append("• Informe o KM atual (número positivo).\n");
        }

        if (erros.length() > 0) {
            DialogoAlerta.aviso(this, "Corrija os campos:\n\n" + erros, "Dados Inválidos");
            return;
        }

        try {
            OrdemDeServico.TipoServicoOS tipoEnum =
                    OrdemDeServico.TipoServicoOS.fromLabel(cbb_Tipo.getSelectedItem().toString());
            OrdemDeServico.TipoManutencao manutEnum =
                    OrdemDeServico.TipoManutencao.fromLabel(cbb_Manutencao.getSelectedItem().toString());

            controller.abrirOSDeOrcamento(titulo, tipoEnum.name(), manutEnum.name(),
                    data, km, selecionado.getIdVeiculo(), selecionado.getIdOrcamento());

            DialogoAlerta.sucesso(this, "Ordem de Serviço aberta com sucesso!\nOrçamento: " + selecionado.getCodigo(), "Sucesso");
            carregarDados();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao abrir OS: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // =========================================================================
    // HELPERS VISUAIS
    // =========================================================================

    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COR_LABEL);
        return l;
    }

    private JLabel rotuloInfo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COR_LABEL);
        return l;
    }

    private JLabel valorInfo() {
        JLabel l = new JLabel("—");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(COR_TEXTO_CAMPO);
        return l;
    }

    private JPanel campoVertical(String rotuloTexto, Component comp) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setOpaque(false);
        c.add(rotulo(rotuloTexto), BorderLayout.NORTH);
        c.add(comp, BorderLayout.CENTER);
        return c;
    }

    private BotaoVidro botaoAcao(String texto, String corHex, int w, int h) {
        BotaoVidro btn = new BotaoVidro(texto, Color.decode(corHex));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setPreferredSize(new Dimension(w, h));
        return btn;
    }

    /**
     * Caixa de seleção no mesmo padrão das demais telas: campo arredondado branco com borda
     * laranja no foco/aberta, lista suspensa sólida com cantos arredondados, destaque âmbar
     * no item sob o cursor e texto desabilitado acinzentado.
     */
    private static void estilizarCombo(JComboBox<?> cmb) {
        cmb.setUI(new ComboArredondadoUI());
        cmb.setOpaque(false);
        // O BasicComboPopup copia este background para a lista: precisa ser sólido.
        cmb.setBackground(COR_POPUP_FUNDO);
        cmb.setForeground(COR_TEXTO_CAMPO);
        cmb.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 4));
        cmb.setFocusable(true);
        cmb.setRenderer(new RendererCombo());
        cmb.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { cmb.repaint(); }
            @Override public void focusLost(FocusEvent e)   { cmb.repaint(); }
        });
    }

    // =========================================================================
    // CLASSES INTERNAS
    // =========================================================================

    /** Item da caixa de orçamentos; {@code orcamento == null} representa o texto-guia (placeholder). */
    private static class ItemOrcamento {
        final Orcamento orcamento;
        private final String textoGuia;

        ItemOrcamento(Orcamento o, String textoGuia) {
            this.orcamento = o;
            this.textoGuia = textoGuia;
        }

        @Override
        public String toString() {
            if (orcamento == null) return textoGuia != null ? textoGuia : "";
            String cod = orcamento.getCodigo() == null || orcamento.getCodigo().isEmpty()
                    ? "#" + orcamento.getIdOrcamento() : orcamento.getCodigo();
            return cod + "  —  " + orcamento.getNomeCliente()
                    + "  —  " + orcamento.getPlacaVeiculo()
                    + "  —  " + String.format("R$ %.2f", orcamento.getValor());
        }
    }

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
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-./,!?()+]", "");
        }
    }

    private static class FiltroDigitos extends DocumentFilter {
        private final int max;
        FiltroDigitos(int max) { this.max = max; }
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() + novo.length() <= max)
                super.insertString(fb, off, novo, attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() - len + novo.length() <= max)
                super.replace(fb, off, len, novo, attr);
        }
    }

    /** Painel com fundo em gradiente suave, opcionalmente com uma borda arredondada colorida. */
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

            g2.setPaint(new GradientPaint(0, 0, corTopo, 0, h, corBase));
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

    /** Campo de texto com fundo e borda arredondados; borda laranja quando focado; acinzentado quando desabilitado. */
    private static class CampoArredondado extends JTextField {
        private static final int RAIO = 14;
        private boolean focado = false;

        CampoArredondado() {
            setOpaque(false);
            setForeground(COR_TEXTO_CAMPO);
            setDisabledTextColor(COR_LABEL_SUTIL);
            setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e)   { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? Color.WHITE : Color.decode("#F2F4F7"));
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!isEnabled()) g2.setColor(Color.decode("#DDE3EA"));
            else g2.setColor(focado ? COR_LARANJA : Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO));
            g2.dispose();
        }
    }

    /**
     * Renderer das caixas de seleção: o valor exibido no campo fecha sem fundo próprio
     * (o branco arredondado é pintado pela UI) e os itens da lista ganham respiro lateral
     * e destaque âmbar ao passar o cursor.
     */
    private static class RendererCombo extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, false);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            if (index < 0) {
                // valor exibido no campo fechado
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                boolean ativo = list == null || list.isEnabled();
                setForeground(ativo ? COR_TEXTO_CAMPO : COR_LABEL_SUTIL);
            } else {
                setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                setBackground(isSelected ? COR_POPUP_SELECAO : COR_POPUP_FUNDO);
                setForeground(COR_TEXTO_CAMPO);
            }
            return this;
        }
    }

    /** UI de combo com cantos arredondados, seta simples, borda laranja no foco e popup arredondado. */
    private static class ComboArredondadoUI extends BasicComboBoxUI {

        @Override
        protected ComboPopup createPopup() {
            return new ComboPopupArredondado(comboBox);
        }

        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(isEnabled() ? Color.decode("#666666") : Color.decode("#B6BCC4"));
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                    g2.dispose();
                }
            };
            b.setPreferredSize(new Dimension(22, 20));
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean ativo = c.isEnabled();
            boolean destaque = ativo && (comboBox.hasFocus() || comboBox.isPopupVisible());

            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, c.getWidth() - 1, c.getHeight() - 1, RAIO_COMBO, RAIO_COMBO);
            g2.setColor(ativo ? Color.WHITE : Color.decode("#F2F4F7"));
            g2.fill(forma);

            if (!ativo)        g2.setColor(Color.decode("#DDE3EA"));
            else if (destaque) g2.setColor(COR_LARANJA);
            else               g2.setColor(Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(destaque ? 1.4f : 1f));
            g2.draw(forma);
            g2.dispose();
            super.paint(g, c);
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // fundo já pintado em paint(), com cantos arredondados
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false); // evita o retângulo azul de seleção
        }
    }

    /** Lista suspensa sólida com cantos arredondados, borda suave e barra de rolagem padrão do sistema. */
    private static class ComboPopupArredondado extends BasicComboPopup {

        @SuppressWarnings("unchecked")
        ComboPopupArredondado(JComboBox<?> combo) {
            super((JComboBox<Object>) combo);
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
            list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMBO, RAIO_COMBO));
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(COR_POPUP_BORDA);
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMBO, RAIO_COMBO));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void show() {
            super.show();
            try {
                Window janela = SwingUtilities.getWindowAncestor(this);
                if (janela != null) {
                    janela.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RAIO_COMBO, RAIO_COMBO));
                }
            } catch (Exception | Error ignorado) {
                // Sem suporte a formato de janela nesta plataforma.
            }
        }
    }

    /** Cabeçalho fino em vidro Aero, usado na faixa de título no topo do card. */
    private static class CabecalhoVidro extends JPanel {
        private static final Color TOPO_A = Color.decode("#FBFBFC");
        private static final Color TOPO_B = Color.decode("#ECEEF1");
        private static final Color BASE_A = Color.decode("#DADDE2");
        private static final Color BASE_B = Color.decode("#EFF1F3");
        private static final Color BORDA  = Color.decode("#B6BCC4");

        CabecalhoVidro() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int meio = h / 2;

            RoundRectangle2D corpo = new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14);
            Shape clipAnterior = g2.getClip();
            g2.clip(corpo);

            g2.setPaint(new GradientPaint(0, 0, TOPO_A, 0, meio, TOPO_B));
            g2.fillRect(0, 0, w, meio);
            g2.setPaint(new GradientPaint(0, meio, BASE_A, 0, h, BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 120));
            g2.fillRect(0, 0, w, Math.max(1, h / 4));

            g2.setClip(clipAnterior);

            g2.setColor(BORDA);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(corpo);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botão de ação em vidro: gradiente translúcido, brilho frosted no topo e reação a hover/clique. */
    private static class BotaoVidro extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoVidro(String texto, Color corBase) {
            super(texto);
            this.corBase = corBase;
            this.corClara = clarear(corBase);
            this.corEscura = escurecer(corBase);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
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
            int raio = 12;

            // Desabilitado: o mesmo vidro, dessaturado e mais transparente
            boolean ativo = isEnabled();
            Color base = ativo ? corBase : Color.decode("#B6BCC4");
            Color clara = ativo ? corClara : clarear(base);
            Color escura = ativo ? corEscura : escurecer(base);

            g2.setColor(new Color(0, 0, 0, ativo ? 35 : 15));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, raio, raio));

            Color corPreenchimento = pressionado && ativo ? escura : (sobreMouse && ativo ? clara : base);
            RoundRectangle2D corpo = new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, raio, raio);

            Shape clipAnterior = g2.getClip();
            g2.clip(corpo);

            GradientPaint gp = new GradientPaint(0, 0, comAlpha(clarear(corPreenchimento), 220),
                    0, h, comAlpha(corPreenchimento, 190));
            g2.setPaint(gp);
            g2.fill(corpo);

            g2.setColor(new Color(255, 255, 255, 60));
            g2.fill(new Ellipse2D.Double(-w * 0.1, -h * 0.7, w * 1.2, h * 1.4));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), raio - 5, raio - 5));

            g2.setClip(clipAnterior);

            g2.setColor(comAlpha(escurecer(corPreenchimento), 160));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(corpo);

            // filete interno claro: reforça o efeito de vidro
            g2.setColor(new Color(255, 255, 255, 110));
            g2.draw(new RoundRectangle2D.Double(1.5, 1.5, w - 4, h - 6, raio - 2, raio - 2));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== utilitários de cor =====
    private static Color clarear(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1] * 0.75f, Math.min(1f, hsb[2] * 1.18f));
    }

    private static Color escurecer(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.82f);
    }

    private static Color comAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}