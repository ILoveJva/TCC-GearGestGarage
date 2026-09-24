package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.estoque.CatalogoPecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;

public class V_EditarItemServico extends JPanel {

    private final OficinaController controller;
    private final CatalogoServicoEntity item;

    // Campos do formulário
    private JTextField txt_Nome;
    private JTextField txt_Valor;
    private JTextField txt_ValidadeKm;
    private JTextField txt_ValidadeMeses;
    private JRadioButton rdb_Padrao;
    private JRadioButton rdb_Revisao;
    private JPanel pnl_Validade;
    private JComboBox<String> cmb_Sistema;

    // Peças associadas
    private JComboBox<ItemPeca> cmb_NovaPeca;
    private JPanel pnl_ListaPecas;
    private JLabel lbl_StatusOrcamentos;

    // Links {idLink → idPeca} para remoção
    private final java.util.Map<Long, Long> linksAtuais = new java.util.LinkedHashMap<>();

    private static final String[] SISTEMAS = {
            "MOTOR", "TRANSMISSAO", "DIRECAO", "SUSPENSAO", "FREIOS",
            "ARREFECIMENTO", "ELETRICA", "ALIMENTACAO", "OUTROS"
    };
    private static final String[] SISTEMAS_LABEL = {
            "Motor", "Transmissão", "Direção", "Suspensão", "Freios",
            "Arrefecimento", "Elétrica", "Alimentação", "Outros"
    };

    public V_EditarItemServico(OficinaController controller, CatalogoServicoEntity item) {
        this.controller = controller;
        this.item = item;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        construirInterface();
        preencherFormulario();
        carregarPecasDisponiveis();
        carregarPecasAssociadas();
    }

    private void construirInterface() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(680, 720));

        JLabel titulo = new JLabel("Configurações > Itens de Serviço > Editar");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        // Campos
        txt_Nome  = criarTextField();
        txt_Valor = criarTextField();
        txt_Valor.setToolTipText("Ex: 120.00");
        ((AbstractDocument) txt_Valor.getDocument()).setDocumentFilter(new FiltroDecimal());

        txt_ValidadeKm    = criarTextField();
        txt_ValidadeMeses = criarTextField();
        ((AbstractDocument) txt_ValidadeKm.getDocument()).setDocumentFilter(new FiltroInteiro());
        ((AbstractDocument) txt_ValidadeMeses.getDocument()).setDocumentFilter(new FiltroInteiro());

        // Linha 1: Nome + Valor
        JPanel pnl_L1 = linha(60);
        JPanel pnl_ValW = bloco("Valor (R$) *", txt_Valor);
        pnl_ValW.setPreferredSize(new Dimension(130, 55));
        pnl_L1.add(bloco("Nome do Serviço *", txt_Nome), BorderLayout.CENTER);
        pnl_L1.add(pnl_ValW, BorderLayout.EAST);

        // Linha 2: Tipo Padrão / Revisão
        JPanel pnl_L2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        pnl_L2.setOpaque(false);
        pnl_L2.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_L2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        rdb_Padrao  = new JRadioButton("Padrão (com validade)");
        rdb_Revisao = new JRadioButton("Revisão (todos os carros)");
        rdb_Padrao.setOpaque(false); rdb_Revisao.setOpaque(false);
        rdb_Padrao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdb_Revisao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ButtonGroup grp = new ButtonGroup(); grp.add(rdb_Padrao); grp.add(rdb_Revisao);
        pnl_L2.add(criarLabel("Tipo *"));
        pnl_L2.add(Box.createHorizontalStrut(10));
        pnl_L2.add(rdb_Padrao);
        pnl_L2.add(Box.createHorizontalStrut(16));
        pnl_L2.add(rdb_Revisao);

        // Linha 3: Sistema do veículo
        JPanel pnl_L3 = linha(60);
        cmb_Sistema = new JComboBox<>(SISTEMAS_LABEL);
        cmb_Sistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnl_L3.add(bloco("Sistema do Veículo *", cmb_Sistema));

        // Linha 4: Validade
        pnl_Validade = linha(60);
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

        // Botões — glassmorphism: vidro translúcido, brilho no topo e
        // reação animada a hover/clique (BotaoGlass); "Voltar" como botão
        // fantasma que ganha um preenchimento de vidro suave só no hover
        BotaoGlass btn_Salvar = new BotaoGlass("SALVAR ALTERAÇÕES", Color.decode("#FF9900"));
        btn_Salvar.setPreferredSize(new Dimension(220, 42));
        btn_Salvar.addActionListener(e -> salvar());

        BotaoFantasma btn_Voltar = new BotaoFantasma("Voltar");
        btn_Voltar.addActionListener(e -> navegar(new V_CadastrarItemServico(controller)));

        JPanel pnl_Btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        pnl_Btn.setOpaque(false);
        pnl_Btn.add(btn_Voltar);
        pnl_Btn.add(btn_Salvar);

        card.add(titulo, BorderLayout.NORTH);
        card.add(new JScrollPane(form) {{
            setBorder(null); setOpaque(false); getViewport().setOpaque(false);
            ScrollBarPadrao.aplicar(this);
        }}, BorderLayout.CENTER);
        card.add(pnl_Btn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 40, 20, 40);
        add(card, gbc);
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 8));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel lbl = criarLabel("Peças Associadas");

        // Aviso de propagação
        lbl_StatusOrcamentos = new JLabel();
        lbl_StatusOrcamentos.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_StatusOrcamentos.setForeground(Color.decode("#888888"));

        JPanel pnl_Header = new JPanel(new BorderLayout());
        pnl_Header.setOpaque(false);
        pnl_Header.add(lbl, BorderLayout.WEST);
        pnl_Header.add(lbl_StatusOrcamentos, BorderLayout.EAST);

        cmb_NovaPeca = new JComboBox<>();
        cmb_NovaPeca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_NovaPeca.setBackground(Color.WHITE);

        BotaoGlass btn_Add = new BotaoGlass("+ Adicionar", Color.decode("#17A2B8"));
        btn_Add.setPreferredSize(new Dimension(140, 34));
        btn_Add.addActionListener(e -> adicionarPeca());

        JPanel pnl_Row = new JPanel(new BorderLayout(8, 0));
        pnl_Row.setOpaque(false);
        pnl_Row.add(cmb_NovaPeca, BorderLayout.CENTER);
        pnl_Row.add(btn_Add, BorderLayout.EAST);

        JLabel lbl_Hint = new JLabel("Ao adicionar, a peça é propagada automaticamente para os orçamentos que usam este serviço.");
        lbl_Hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_Hint.setForeground(Color.decode("#FF9900"));

        // Lista simples (sem JTable) das peças associadas — cada linha
        // tem um botão de remover em vidro embutido, sem depender de
        // seleção de linha de tabela
        pnl_ListaPecas = new JPanel();
        pnl_ListaPecas.setLayout(new BoxLayout(pnl_ListaPecas, BoxLayout.Y_AXIS));
        pnl_ListaPecas.setOpaque(false);

        JScrollPane scrollPecas = new JScrollPane(pnl_ListaPecas);
        scrollPecas.setPreferredSize(new Dimension(0, 110));
        scrollPecas.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        scrollPecas.setOpaque(false);
        scrollPecas.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scrollPecas);

        JPanel corpo = new JPanel(new BorderLayout(0, 6));
        corpo.setOpaque(false);
        corpo.add(pnl_Row, BorderLayout.NORTH);
        corpo.add(scrollPecas, BorderLayout.CENTER);

        sec.add(pnl_Header, BorderLayout.NORTH);
        sec.add(lbl_Hint, BorderLayout.CENTER);
        sec.add(corpo, BorderLayout.SOUTH);
        return sec;
    }

    private JPanel criarLinhaPeca(String nome, long idLink) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel lblNome = new JLabel(nome);
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNome.setForeground(Color.decode("#333333"));

        BotaoRemoverGlass btn_Rem = new BotaoRemoverGlass();
        btn_Rem.setToolTipText("Remover peça");
        btn_Rem.addActionListener(e -> removerPeca(idLink));

        row.add(lblNome, BorderLayout.CENTER);
        row.add(btn_Rem, BorderLayout.EAST);
        return row;
    }

    // =========================================================================

    private void preencherFormulario() {
        txt_Nome.setText(item.getNome());
        txt_Valor.setText(String.format("%.2f", item.getValor()));
        if ("REVISAO".equals(item.getTipo())) {
            rdb_Revisao.setSelected(true);
            pnl_Validade.setVisible(false);
        } else {
            rdb_Padrao.setSelected(true);
            pnl_Validade.setVisible(true);
        }
        if (item.getValidadeKm()    != null) txt_ValidadeKm.setText(String.valueOf(item.getValidadeKm()));
        if (item.getValidadeMeses() != null) txt_ValidadeMeses.setText(String.valueOf(item.getValidadeMeses()));

        String sistema = item.getSistema() != null ? item.getSistema() : "OUTROS";
        for (int i = 0; i < SISTEMAS.length; i++)
            if (SISTEMAS[i].equals(sistema)) { cmb_Sistema.setSelectedIndex(i); break; }
    }

    private void carregarPecasDisponiveis() {
        cmb_NovaPeca.removeAllItems();
        for (CatalogoPecaEntity p : controller.listarTodasPecas())
            cmb_NovaPeca.addItem(new ItemPeca(p));
    }

    private void carregarPecasAssociadas() {
        linksAtuais.clear();
        pnl_ListaPecas.removeAll();
        Map<Long, Long> links = controller.listarLinksPecasDoItemCatalogo(item.getIdCatalogoServico());
        linksAtuais.putAll(links);

        if (links.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma peça associada.");
            vazio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            vazio.setForeground(Color.decode("#999999"));
            vazio.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
            pnl_ListaPecas.add(vazio);
        } else {
            for (Map.Entry<Long, Long> entry : links.entrySet()) {
                long idLink = entry.getKey();
                CatalogoPecaEntity p = controller.listarTodasPecas().stream()
                        .filter(x -> x.getIdCatalogoPeca().equals(entry.getValue())).findFirst().orElse(null);
                String nome = p != null ? p.getNomePopular() : "(id " + entry.getValue() + ")";
                pnl_ListaPecas.add(criarLinhaPeca(nome, idLink));
            }
        }
        pnl_ListaPecas.revalidate();
        pnl_ListaPecas.repaint();
        atualizarStatusOrcamentos();
    }

    private void adicionarPeca() {
        ItemPeca sel = (ItemPeca) cmb_NovaPeca.getSelectedItem();
        if (sel == null || sel.peca == null) {
            DialogoAlerta.aviso(this, "Cadastre peças antes de associá-las.", "Sem peças"); return;
        }
        // evitar duplicata na UI
        if (linksAtuais.containsValue(sel.peca.getIdCatalogoPeca())) {
            DialogoAlerta.aviso(this, "Esta peça já está associada ao item.", "Duplicata"); return;
        }
        try {
            controller.adicionarPecaAItemCatalogo(item.getIdCatalogoServico(), sel.peca.getIdCatalogoPeca());
            carregarPecasAssociadas();
            int qtdOrc = controller.contarOrcamentosComItemCatalogo(item.getIdCatalogoServico());
            String msg = "Peça adicionada!" + (qtdOrc > 0
                    ? "\n" + qtdOrc + " orçamento(s) com este serviço foram atualizados automaticamente."
                    : "");
            DialogoAlerta.sucesso(this, msg, "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro: " + ex.getMessage(), "Erro");
        }
    }

    private void removerPeca(long idLink) {
        boolean confirmado = DialogoConfirmacao.confirmar(this,
                "Remover esta peça do item de serviço?\n(Orçamentos existentes não serão alterados.)",
                "Confirmar remoção");
        if (!confirmado) return;
        try {
            controller.removerPecaDeItemCatalogo(idLink);
            carregarPecasAssociadas();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro: " + ex.getMessage(), "Erro");
        }
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
            String km  = txt_ValidadeKm.getText().trim();
            String mes = txt_ValidadeMeses.getText().trim();
            if (!km.isEmpty())  try { validadeKm    = Integer.parseInt(km);  } catch (NumberFormatException ignored) {}
            if (!mes.isEmpty()) try { validadeMeses = Integer.parseInt(mes); } catch (NumberFormatException ignored) {}
        }

        try {
            controller.atualizarItemServico(item.getIdCatalogoServico(), nome, "", valor,
                    tipo, sistema, validadeKm, validadeMeses);
            DialogoAlerta.sucesso(this, "Item \"" + nome + "\" atualizado com sucesso!", "Sucesso");
            navegar(new V_CadastrarItemServico(controller));
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro: " + ex.getMessage(), "Erro");
        }
    }

    private void atualizarStatusOrcamentos() {
        int qtd = controller.contarOrcamentosComItemCatalogo(item.getIdCatalogoServico());
        if (qtd == 0)
            lbl_StatusOrcamentos.setText("Nenhum orçamento usa este serviço ainda.");
        else
            lbl_StatusOrcamentos.setText(qtd + " orçamento(s) usam este serviço — peças adicionadas serão propagadas.");
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // ========= helpers visuais =========

    private JPanel linha(int maxH) {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, maxH));
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

    private static Color clarear(Color c, float fator) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsb[2] = Math.min(1f, hsb[2] + fator);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    private static Color escurecer(Color c, float fator) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsb[2] = Math.max(0f, hsb[2] - fator);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    // ========= inner classes =========

    private static class ItemPeca {
        final CatalogoPecaEntity peca;
        ItemPeca(CatalogoPecaEntity p) { this.peca = p; }
        @Override public String toString() { return peca != null ? peca.getNomePopular() : ""; }
    }

    private static class FiltroDecimal extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9.,]", ""), a); }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9.,]", ""), a); }
    }

    private static class FiltroInteiro extends DocumentFilter {
        public void insertString(FilterBypass fb, int off, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.insertString(fb, off, t.replaceAll("[^0-9]", ""), a); }
        public void replace(FilterBypass fb, int off, int len, String t, AttributeSet a) throws BadLocationException {
            if (t != null) super.replace(fb, off, len, t.replaceAll("[^0-9]", ""), a); }
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

    /**
     * Botão de ação com glassmorphism: preenchimento translúcido na cor
     * base recebida, sombra suave, reflexo no topo e reação ANIMADA a
     * hover/clique (clareia/escurece + repaint). Usado tanto no botão
     * principal (laranja) quanto no secundário de "+ Adicionar" (azul).
     */
    private static class BotaoGlass extends JButton {
        private boolean sobreMouse = false;
        private boolean pressionado = false;
        private final Color corBase, corClara, corEscura;

        BotaoGlass(String texto, Color corBase) {
            super(texto);
            this.corBase   = corBase;
            this.corClara  = clarear(corBase, 0.16f);
            this.corEscura = escurecer(corBase, 0.14f);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
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
            int raio = 10;

            // Sombra fina e neutra
            g2.setColor(new Color(0, 0, 0, 32));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, raio, raio));

            Color preenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            // Preenchimento levemente translúcido, para manter a leitura de "vidro colorido"
            g2.setColor(new Color(preenchimento.getRed(), preenchimento.getGreen(), preenchimento.getBlue(), 225));
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, raio, raio));

            // Reflexo suave no topo — mesma linguagem visual dos campos em vidro
            Shape clipOriginal = g2.getClip();
            g2.clip(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, raio, raio));
            g2.setColor(new Color(255, 255, 255, 55));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.45), raio - 4, raio - 4));
            g2.setClip(clipOriginal);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão "fantasma" (texto simples, sem preenchimento em repouso) que
     * ganha um fundo de vidro fosco suave, animado por hover, reforçando a
     * hierarquia visual em relação ao botão de ação principal.
     */
    private static class BotaoFantasma extends JButton {
        private boolean sobreMouse = false;

        BotaoFantasma(String texto) {
            super(texto);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(Color.decode("#6C757D"));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { sobreMouse = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (sobreMouse) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, 10, 10);
                g2.setColor(new Color(255, 255, 255, 190));
                g2.fill(forma);
                g2.setColor(new Color(160, 175, 195, 120));
                g2.draw(forma);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Botão circular pequeno em vidro para remover uma peça da lista —
     * substitui a antiga tabela + "Remover peça selecionada": cada linha
     * carrega seu próprio botão de remoção, com destaque vermelho animado
     * no hover.
     */
    private static class BotaoRemoverGlass extends JButton {
        private boolean sobreMouse = false;
        private static final Color COR_ERRO = Color.decode("#DC3545");

        BotaoRemoverGlass() {
            setPreferredSize(new Dimension(26, 26));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { sobreMouse = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int d = Math.min(w, h) - 4;
            int x = (w - d) / 2;
            int y = (h - d) / 2;

            g2.setColor(sobreMouse ? new Color(220, 53, 69, 45) : new Color(160, 175, 195, 35));
            g2.fill(new java.awt.geom.Ellipse2D.Double(x, y, d, d));

            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(sobreMouse ? COR_ERRO : Color.decode("#9AA5B1"));
            double m = d * 0.28;
            g2.draw(new Line2D.Double(x + m, y + m, x + d - m, y + d - m));
            g2.draw(new Line2D.Double(x + d - m, y + m, x + m, y + d - m));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}