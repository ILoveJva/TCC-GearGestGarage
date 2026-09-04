package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;

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
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Compra de peças: seleciona a peça, a quantidade e o valor pago e escolhe o destino —
 * ou entra no estoque geral (uso futuro), ou é usada direto numa OS específica quando a
 * peça não está disponível em estoque (o custo vira parte do orçamento daquela OS).
 *
 * Visual alinhado ao mesmo acabamento em vidro (glassmorphism) usado em V_CadastrarOrcamento —
 * a lógica de negócio permanece exatamente a mesma.
 */
public class V_EntradaEstoque extends JPanel {

    private final OficinaController controller;

    private GlassComboBox<ItemPeca> cmb_Peca;
    private GlassTextField txt_Quantidade;
    private GlassTextField txt_Valor;
    private JRadioButton rad_Estoque;
    private JRadioButton rad_OSDireto;
    private GlassComboBox<ItemOS> cmb_OS;
    private GlassTextField txt_NomeTecnico;
    private GlassTextField txt_Fabricante;
    private GlassTextField txt_Observacao;
    private JPanel pnl_OS;
    private JPanel pnl_TecFab;

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

        txt_Quantidade = criarTextField();
        txt_Quantidade.setToolTipText("Ex: 10");
        ((AbstractDocument) txt_Quantidade.getDocument()).setDocumentFilter(new FiltroInteiro());

        txt_Valor = criarTextField();
        txt_Valor.setToolTipText("Ex: 45.90 (valor unitário pago pela peça)");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_Observacao = criarTextField();
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

        JPanel pnl_L5 = linha(200);
        pnl_L5.add(bloco("Observação", txt_Observacao), BorderLayout.CENTER);

        form.add(pnl_L1);
        form.add(Box.createVerticalStrut(12));
        form.add(pnl_L2);
        form.add(Box.createVerticalStrut(12));
        form.add(pnl_Destino);
        form.add(Box.createVerticalStrut(6));
        form.add(pnl_OS);
        form.add(Box.createVerticalStrut(6));
        form.add(pnl_TecFab);
        form.add(Box.createVerticalStrut(12));
        form.add(pnl_L5);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scrollForm);

        BotaoAcao btn_Registrar = new BotaoAcao("REGISTRAR COMPRA", COR_SUCESSO, COR_SUCESSO_CLARA, COR_SUCESSO_ESCURA);
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
        pnl_TecFab.setVisible(direto);
        revalidate();
        repaint();
    }

    /** Sugere o destino conforme o estoque atual da peça selecionada, sem travar a escolha do usuário. */
    private void sugerirDestinoPelaPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        if (sel.peca.getQuantidadeEstoque() <= 0 && cmb_OS.getItemCount() > 0) {
            rad_OSDireto.setSelected(true);
        } else {
            rad_Estoque.setSelected(true);
        }
        atualizarVisibilidadeDestino();
    }

    private void salvar() {
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

        Double valorUnitario = null;
        String valorTxt = txt_Valor.getText().trim().replace(",", ".");
        if (!valorTxt.isEmpty()) {
            try {
                valorUnitario = Double.parseDouble(valorTxt);
                if (valorUnitario < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                DialogoAlerta.aviso(this, "Informe um valor válido (ex: 45.90).", "Campo Inválido");
                return;
            }
        }

        String observacao = txt_Observacao.getText().trim();

        if (rad_OSDireto.isSelected()) {
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
                controller.adicionarPecaDiretoOS(osSel.dto.idOrcamento(), sel.peca.getIdPeca(), qtd,
                        txt_NomeTecnico.getText().trim(), txt_Fabricante.getText().trim(), valorUnitario);
                DialogoAlerta.sucesso(this, qtd + " unidade(s) de \"" + sel.peca.getNomePopular()
                        + "\" adicionada(s) direto à " + osSel + " (sem passar pelo estoque).", "Sucesso");
                limparCampos();
                carregarPecas();
                carregarOS();
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao registrar compra direta: " + ex.getMessage(), "Erro no Sistema");
            }
        } else {
            try {
                controller.registrarEntradaEstoque(sel.peca.getIdPeca(), qtd, valorUnitario, observacao);
                DialogoAlerta.sucesso(this, "Entrada de " + qtd + " unidade(s) de \"" + sel.peca.getNomePopular()
                        + "\" registrada no estoque com sucesso!", "Sucesso");
                limparCampos();
                carregarPecas();
                carregarOS();
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao registrar entrada: " + ex.getMessage(), "Erro no Sistema");
            }
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
        Object selecionado = cmb_Peca.getSelectedItem();
        cmb_Peca.removeAllItems();
        for (PecaEntity p : controller.listarEstoque()) cmb_Peca.addItem(new ItemPeca(p));
        if (selecionado instanceof ItemPeca ip && ip.peca != null) {
            for (int i = 0; i < cmb_Peca.getItemCount(); i++) {
                ItemPeca it = cmb_Peca.getItemAt(i);
                if (it.peca != null && it.peca.getIdPeca().equals(ip.peca.getIdPeca())) {
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
        final PecaEntity peca;
        ItemPeca(PecaEntity p) { this.peca = p; }
        @Override public String toString() {
            if (peca == null) return "(sem peça)";
            return peca.getNomePopular() + "  (estoque: " + peca.getQuantidadeEstoque() + ")";
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