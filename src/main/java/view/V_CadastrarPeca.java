package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
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

public class V_CadastrarPeca extends JPanel {

    private JPanel pnl_CardCentral;
    private JLabel lbl_TituloPaginacao;
    private JPanel pnl_Formulario;

    private JLabel lbl_Nome, lbl_Sistema, lbl_VidaTempo, lbl_VidaKm, lbl_Servico;
    private GlassTextField txt_NomePopular, txt_VidaTempo, txt_VidaKm;
    private GlassComboBox<SistemaItem> cmb_Sistema;
    private GlassComboBox<ServicoItem> cmb_Servico;
    private BotaoAcao btn_Cadastrar;

    private List<CatalogoServicoEntity> catalogoServicos;

    // Paleta harmonizada com o efeito de vidro das caixas de texto
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

    // Ajustes rápidos de tipografia/tamanho — mexa só aqui para alterar tudo de uma vez
    private static final int RAIO_COMPONENTE     = 12;   // arredondamento compartilhado (campos, combos e botão)
    private static final int TAMANHO_FONTE_LABEL = 18;
    private static final int TAMANHO_FONTE_CAMPO = 14;
    private static final int LARGURA_CAMPO       = 140;
    private static final int ALTURA_CAMPO        = 34;
    private static final int TAMANHO_FONTE_BOTAO = 16;
    private static final int LARGURA_BOTAO       = 260;
    private static final int ALTURA_BOTAO        = 50;

    private final OficinaController controller;

    public V_CadastrarPeca(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        initComponents();
        layoutComponents();
        aplicarFiltros();
        vincularAcoes();
    }

    private void initComponents() {
        pnl_CardCentral = new PainelGradiente(new BorderLayout(0, 20), COR_CARD_TOPO, COR_CARD_BASE);
        pnl_CardCentral.setPreferredSize(new Dimension(500, 500));
        pnl_CardCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lbl_TituloPaginacao = new JLabel("Configurações > Cadastrar Peça");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(COR_TITULO);

        pnl_Formulario = new JPanel(new GridLayout(5, 1, 0, 12));
        pnl_Formulario.setOpaque(false);

        lbl_Nome = criarLabel("Nome da Peça *");
        txt_NomePopular = criarTextField();
        txt_NomePopular.setToolTipText("Ex: Filtro de óleo, Pastilha de freio");

        lbl_Sistema = criarLabel("Sistema do Veículo *");
        cmb_Sistema = new GlassComboBox<>();
        cmb_Sistema.setPreferredSize(new Dimension(LARGURA_CAMPO, ALTURA_CAMPO));
        cmb_Sistema.addItem(new SistemaItem("MOTOR",        "Motor"));
        cmb_Sistema.addItem(new SistemaItem("TRANSMISSAO",  "Transmissão"));
        cmb_Sistema.addItem(new SistemaItem("DIRECAO",      "Direção"));
        cmb_Sistema.addItem(new SistemaItem("SUSPENSAO",    "Suspensão"));
        cmb_Sistema.addItem(new SistemaItem("FREIOS",       "Freios"));
        cmb_Sistema.addItem(new SistemaItem("ARREFECIMENTO","Arrefecimento"));
        cmb_Sistema.addItem(new SistemaItem("ELETRICA",     "Elétrica"));
        cmb_Sistema.addItem(new SistemaItem("ALIMENTACAO",  "Alimentação"));
        cmb_Sistema.addItem(new SistemaItem("OUTROS",       "Outros"));

        lbl_VidaTempo = criarLabel("Vida Útil (tempo)");
        txt_VidaTempo = criarTextField();
        txt_VidaTempo.setToolTipText("Ex: 12 meses, 2 anos");

        lbl_VidaKm = criarLabel("Vida Útil (km)");
        txt_VidaKm = criarTextField();
        txt_VidaKm.setToolTipText("Ex: 30000 km");

        catalogoServicos = controller.listarCatalogoServicos();

        lbl_Servico = criarLabel("Vincular a um Serviço (opcional)");
        cmb_Servico = new GlassComboBox<>();
        cmb_Servico.setPreferredSize(new Dimension(LARGURA_CAMPO, ALTURA_CAMPO));
        atualizarServicosDoSistema();

        cmb_Sistema.addActionListener(e -> atualizarServicosDoSistema());

        pnl_Formulario.add(criarContainerVertical(lbl_Nome,     txt_NomePopular));
        pnl_Formulario.add(criarContainerVertical(lbl_Sistema,  cmb_Sistema));
        pnl_Formulario.add(criarContainerVertical(lbl_VidaTempo, txt_VidaTempo));
        pnl_Formulario.add(criarContainerVertical(lbl_VidaKm,    txt_VidaKm));
        pnl_Formulario.add(criarContainerVertical(lbl_Servico,   cmb_Servico));

        btn_Cadastrar = new BotaoAcao("CADASTRAR PEÇA");
        btn_Cadastrar.setPreferredSize(new Dimension(LARGURA_BOTAO, ALTURA_BOTAO));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Formulario, BorderLayout.CENTER);

        JPanel pnl_BtnContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnl_BtnContainer.setOpaque(false);
        pnl_BtnContainer.add(btn_Cadastrar);
        pnl_CardCentral.add(pnl_BtnContainer, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    /** Filtra o ComboBox de Serviços do catálogo pelo sistema selecionado. */
    private void atualizarServicosDoSistema() {
        SistemaItem sistema = (SistemaItem) cmb_Sistema.getSelectedItem();
        String codigo = sistema != null ? sistema.codigo : "OUTROS";
        cmb_Servico.removeAllItems();
        cmb_Servico.addItem(new ServicoItem(null, "— Nenhum —"));
        for (CatalogoServicoEntity servico : catalogoServicos)
            if (codigo.equals(servico.getSistema()))
                cmb_Servico.addItem(new ServicoItem(servico.getIdCatalogoServico(), servico.getNome()));
    }

    // =========================================================================
    // FILTROS DE ENTRADA (DocumentFilter)
    // =========================================================================
    private void aplicarFiltros() {
        ((AbstractDocument) txt_NomePopular.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_VidaTempo.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_VidaKm.getDocument()).setDocumentFilter(new FiltroTexto());
    }

    // =========================================================================
    // VALIDAÇÃO E TRATAMENTO DE ERROS
    // =========================================================================
    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            limparErro(txt_NomePopular);
            String nomePopular = txt_NomePopular.getText().trim();
            if (nomePopular.length() < 2) {
                marcarErro(txt_NomePopular);
                JOptionPane.showMessageDialog(this,
                    "Nome da peça deve ter pelo menos 2 caracteres.",
                    "Campo Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String vidaTempo = txt_VidaTempo.getText().trim();
            String vidaKm    = txt_VidaKm.getText().trim();
            SistemaItem sistema = (SistemaItem) cmb_Sistema.getSelectedItem();
            ServicoItem servico = (ServicoItem) cmb_Servico.getSelectedItem();
            try {
                controller.salvarPeca(nomePopular,
                    vidaTempo.isEmpty() ? "Não informado" : vidaTempo,
                    vidaKm.isEmpty()    ? "Não informado" : vidaKm,
                    sistema != null ? sistema.codigo : "OUTROS",
                    servico != null && servico.id != null ? servico.id : 0L);
                JOptionPane.showMessageDialog(this,
                    "Peça \"" + nomePopular + "\" cadastrada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txt_NomePopular.setText("");
                txt_VidaTempo.setText("");
                txt_VidaKm.setText("");
                atualizarServicosDoSistema();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar peça: " + ex.getMessage(),
                    "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
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

    // =========================================================================
    // UI HELPERS
    // =========================================================================
    private JPanel criarContainerVertical(JLabel label, Component campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(label, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL));
        l.setForeground(COR_LABEL);
        return l;
    }

    private GlassTextField criarTextField() {
        GlassTextField f = new GlassTextField();
        f.setPreferredSize(new Dimension(LARGURA_CAMPO, ALTURA_CAMPO));
        return f;
    }

    private static class SistemaItem {
        final String codigo, label;
        SistemaItem(String c, String l) { this.codigo = c; this.label = l; }
        @Override public String toString() { return label; }
    }

    private static class ServicoItem {
        final Long id; final String label;
        ServicoItem(Long id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    // =========================================================================
    // INNER CLASSES — DocumentFilter
    // =========================================================================

    /** Permite letras (incluindo acentuadas), números, espaços e hífens. */
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
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-./]", "");
        }
    }

    /**
     * Campo de texto com efeito de vidro translúcido (glassmorphism):
     * preenchimento em gradiente semi-transparente, reflexo sutil no topo,
     * sombra suave e borda que reage a foco/erro. Não altera nenhuma
     * regra de negócio — apenas a pintura do componente.
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

            // Sombra suave por baixo do vidro, para dar sensação de profundidade
            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            // Preenchimento translúcido (o "vidro" propriamente dito)
            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, erro ? 225 : 210),
                    0, h, new Color(255, 255, 255, erro ? 175 : 145)
            );
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            // Reflexo sutil na parte superior, reforçando a leitura de vidro
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

    /**
     * Painel com fundo em gradiente suave (branco levemente esfriado em direção
     * a um cinza-azulado), usado para harmonizar o cartão central com o
     * translucidez das caixas de texto em vidro.
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
     * Botão de ação com a mesma linguagem visual dos campos em vidro:
     * cantos arredondados (RAIO_COMPONENTE), sombra suave, reflexo no topo
     * e leve reação a hover/clique. A cor de ação (tema original) é mantida.
     */
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

            // Sombra suave, mesma linguagem visual usada nos campos em vidro
            g2.setColor(new Color(180, 100, 0, 60));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? COR_ACAO_ESCURA : (sobreMouse ? COR_ACAO_CLARA : COR_ACAO);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            // Reflexo sutil no topo, reforçando a mesma sensação de vidro dos campos
            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * JComboBox com o mesmo efeito de vidro dos campos de texto: fundo em
     * gradiente translúcido, reflexo no topo, sombra suave e borda que reage
     * ao foco. Usa uma UI customizada para que o Swing não pinte por cima
     * (fundo sólido / seta cinza padrão) do nosso desenho.
     */
    private static class GlassComboBox<T> extends JComboBox<T> {
        private boolean focado = false;

        GlassComboBox() { super(); estilizar(); }

        private void estilizar() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            // IMPORTANTE: não usar cor transparente aqui. O BasicComboPopup copia
            // este background para a lista suspensa, e um alpha 0 deixava o popup
            // inteiro transparente.
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
     * UI mínima para o GlassComboBox: evita que o Swing pinte um fundo sólido
     * por cima do gradiente translúcido, e troca a setinha padrão por um
     * triângulo vetorial discreto.
     */
    private static class GlassComboBoxUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Propositalmente vazio — o fundo já é pintado em GlassComboBox.paintComponent().
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
