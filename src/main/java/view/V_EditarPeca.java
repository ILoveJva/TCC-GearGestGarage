package view;

import br.com.oficina.estoque.CatalogoPecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class V_EditarPeca extends JPanel {

    private final OficinaController controller;
    private final CatalogoPecaEntity peca;

    private JPanel pnl_CardCentral;
    private JPanel pnl_Formulario;
    private JLabel lbl_TituloPaginacao;

    private JLabel lbl_NomePopular, lbl_Sistema, lbl_VidaTempo, lbl_VidaKm;
    private CampoTexto txt_NomePopular, txt_VidaTempo, txt_VidaKm;
    private CampoCombo<SistemaItem> cmb_Sistema;
    private BotaoAcao btn_Salvar;
    private JButton btn_Cancelar;

    // Paleta harmonizada com o efeito de vidro (mesma de V_CadastrarCliente)
    private static final Color COR_FUNDO_PAGINA = Color.decode("#F5F7FA");
    private static final Color COR_CARD_TOPO    = Color.decode("#FFFFFF");
    private static final Color COR_CARD_BASE    = Color.decode("#EEF2F7");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    private static final Color COR_BORDA_PADRAO = Color.decode("#D7DEE7");
    private static final Color COR_ERRO         = Color.decode("#D63A44");

    // Ajustes rápidos de tipografia/tamanho
    private static final int RAIO_COMPONENTE     = 12;
    private static final int RAIO_CAMPO          = 8;
    private static final int TAMANHO_FONTE_LABEL = 16;
    private static final int TAMANHO_FONTE_CAMPO = 15;
    private static final int LARGURA_CAMPO       = 130;
    private static final int ALTURA_CAMPO        = 23;
    private static final int TAMANHO_FONTE_BOTAO = 16;
    private static final int LARGURA_BOTAO       = 250;
    private static final int ALTURA_BOTAO        = 46;
    private static final int TAMANHO_ICONE_BOTAO = 22;

    public V_EditarPeca(OficinaController controller, CatalogoPecaEntity peca) {
        this.controller = controller;
        this.peca = peca;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        initComponents();
        layoutComponents();
        aplicarFiltros();
        preencherDados();
        vincularAcoes();
    }

    private void initComponents() {
        pnl_CardCentral = new PainelGradiente(new BorderLayout(0, 20), COR_CARD_TOPO, COR_CARD_BASE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 330));
        pnl_CardCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lbl_TituloPaginacao = new JLabel("Peças > Editar Peça");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(COR_TITULO);

        pnl_Formulario = new JPanel(new GridLayout(2, 2, 25, 15));
        pnl_Formulario.setOpaque(false);

        lbl_NomePopular = criarLabel("Nome da peça *");
        txt_NomePopular = criarTextField();

        lbl_Sistema = criarLabel("Sistema do veículo *");
        cmb_Sistema = new CampoCombo<>();
        cmb_Sistema.setPreferredSize(new Dimension(LARGURA_CAMPO, ALTURA_CAMPO));
        cmb_Sistema.addItem(new SistemaItem("MOTOR",         "Motor"));
        cmb_Sistema.addItem(new SistemaItem("TRANSMISSAO",   "Transmissão"));
        cmb_Sistema.addItem(new SistemaItem("DIRECAO",       "Direção"));
        cmb_Sistema.addItem(new SistemaItem("SUSPENSAO",     "Suspensão"));
        cmb_Sistema.addItem(new SistemaItem("FREIOS",        "Freios"));
        cmb_Sistema.addItem(new SistemaItem("ARREFECIMENTO", "Arrefecimento"));
        cmb_Sistema.addItem(new SistemaItem("ELETRICA",      "Elétrica"));
        cmb_Sistema.addItem(new SistemaItem("ALIMENTACAO",   "Alimentação"));
        cmb_Sistema.addItem(new SistemaItem("OUTROS",        "Outros"));

        lbl_VidaTempo = criarLabel("Vida útil (tempo) (ex: 12 meses)");
        txt_VidaTempo = criarTextField();
        txt_VidaTempo.setToolTipText("Ex: 12 meses, 2 anos");

        lbl_VidaKm = criarLabel("Vida útil (km) (ex: 30000 km)");
        txt_VidaKm = criarTextField();
        txt_VidaKm.setToolTipText("Ex: 30000 km");

        pnl_Formulario.add(criarContainerVertical(lbl_NomePopular, txt_NomePopular));
        pnl_Formulario.add(criarContainerVertical(lbl_Sistema,     cmb_Sistema));
        pnl_Formulario.add(criarContainerVertical(lbl_VidaTempo,   txt_VidaTempo));
        pnl_Formulario.add(criarContainerVertical(lbl_VidaKm,      txt_VidaKm));

        btn_Salvar = new BotaoAcao("SALVAR ALTERAÇÕES", new IconeSalvar(TAMANHO_ICONE_BOTAO, Color.WHITE));
        btn_Salvar.setPreferredSize(new Dimension(LARGURA_BOTAO, ALTURA_BOTAO));

        btn_Cancelar = new JButton("← Cancelar");
        btn_Cancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Cancelar.setForeground(COR_LABEL);
        btn_Cancelar.setContentAreaFilled(false);
        btn_Cancelar.setBorderPainted(false);
        btn_Cancelar.setFocusPainted(false);
        btn_Cancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Cancelar.addActionListener(e -> navegar(new V_VisualizarPecas(controller)));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Formulario, BorderLayout.CENTER);

        JPanel pnl_ContainerBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        pnl_ContainerBotao.setOpaque(false);
        pnl_ContainerBotao.add(btn_Cancelar);
        pnl_ContainerBotao.add(btn_Salvar);
        pnl_CardCentral.add(pnl_ContainerBotao, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    private void aplicarFiltros() {
        ((AbstractDocument) txt_NomePopular.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_VidaTempo.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_VidaKm.getDocument()).setDocumentFilter(new FiltroTexto());
    }

    private void preencherDados() {
        txt_NomePopular.setText(peca.getNomePopular() != null ? peca.getNomePopular() : "");
        String tempo = peca.getVidaUtilTempo();
        txt_VidaTempo.setText("Não informado".equals(tempo) || tempo == null ? "" : tempo);
        String km = peca.getVidaUtilKm();
        txt_VidaKm.setText("Não informado".equals(km) || km == null ? "" : km);
        String sistemaAtual = peca.getSistema();
        for (int i = 0; i < cmb_Sistema.getItemCount(); i++) {
            if (cmb_Sistema.getItemAt(i).codigo.equals(sistemaAtual)) {
                cmb_Sistema.setSelectedIndex(i);
                break;
            }
        }
    }

    private void vincularAcoes() {
        btn_Salvar.addActionListener(e -> {
            limparErro(txt_NomePopular);
            String nome = txt_NomePopular.getText().trim();
            if (nome.length() < 2) {
                marcarErro(txt_NomePopular);
                DialogoAlerta.aviso(this, "Nome da peça deve ter pelo menos 2 caracteres.", "Campo Inválido");
                return;
            }
            String tempo = txt_VidaTempo.getText().trim();
            String km    = txt_VidaKm.getText().trim();
            SistemaItem sistema = (SistemaItem) cmb_Sistema.getSelectedItem();
            try {
                controller.atualizarPeca(peca.getIdCatalogoPeca(),
                        nome,
                        tempo.isEmpty() ? "Não informado" : tempo,
                        km.isEmpty()    ? "Não informado" : km,
                        sistema != null ? sistema.codigo : "OUTROS");
                DialogoAlerta.sucesso(this, "Peça \"" + nome + "\" atualizada com sucesso!", "Sucesso");
                navegar(new V_VisualizarPecas(controller));
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao salvar: " + ex.getMessage(), "Erro no Sistema");
            }
        });
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // =========================================================================
    // ERROS
    // =========================================================================
    private void marcarErro(CampoTexto field) {
        field.setEstadoErro(true);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                limparErro(field);
                field.removeFocusListener(this);
            }
        });
    }

    private void limparErro(CampoTexto field) {
        field.setEstadoErro(false);
    }

    // =========================================================================
    // AUXILIARES DE ESTILIZAÇÃO
    // =========================================================================
    private JPanel criarContainerVertical(JLabel label, JComponent field) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setOpaque(false);
        c.add(label, BorderLayout.NORTH);
        c.add(field, BorderLayout.CENTER);
        return c;
    }

    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL));
        l.setForeground(COR_LABEL);
        return l;
    }

    private CampoTexto criarTextField() {
        CampoTexto f = new CampoTexto();
        f.setPreferredSize(new Dimension(LARGURA_CAMPO, ALTURA_CAMPO));
        return f;
    }

    private static class SistemaItem {
        final String codigo, label;
        SistemaItem(String c, String l) { this.codigo = c; this.label = l; }
        @Override public String toString() { return label; }
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
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-./]", "");
        }
    }

    // =========================================================================
    // PINTURA COMPARTILHADA (vidro + indicador de foco animado)
    // =========================================================================

    /** Vidro fosco translúcido com brilho difuso no topo. */
    private static void pintarVidro(Graphics g, JComponent c, boolean erro) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = c.getWidth();
        int h = c.getHeight();
        RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_CAMPO, RAIO_CAMPO);

        GradientPaint vidro = new GradientPaint(
                0, 0, new Color(255, 255, 255, erro ? 195 : 175),
                0, h, new Color(255, 255, 255, erro ? 140 : 115)
        );
        g2.setPaint(vidro);
        g2.fill(forma);

        Shape clipOriginal = g2.getClip();
        g2.clip(forma);
        for (int i = 0; i < 4; i++) {
            int alpha = 22 - i * 5;
            if (alpha <= 0) break;
            double raio = h * (1.1 - i * 0.18);
            g2.setColor(new Color(255, 255, 255, alpha));
            g2.fill(new Ellipse2D.Double(-raio * 0.25, -raio * 0.85, w + raio * 0.5, raio * 1.3));
        }
        g2.setClip(clipOriginal);
        g2.dispose();
    }

    /** Contorno fino, realce superior e linha de foco que cresce a partir do centro. */
    private static void pintarContorno(Graphics g, JComponent c, boolean erro, float progressoFoco) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = c.getWidth();
        int h = c.getHeight();

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(erro ? COR_ERRO : COR_BORDA_PADRAO);
        g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 2, RAIO_CAMPO, RAIO_CAMPO));

        if (!erro) {
            g2.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255, 255, 255, 170));
            g2.draw(new Line2D.Double(RAIO_CAMPO * 0.7, 1.1, w - RAIO_CAMPO * 0.7, 1.1));
        }

        float progresso = erro ? 1f : progressoFoco;
        float larguraMax = Math.max(0, w - 16);
        float largura = larguraMax * progresso;
        if (largura > 0.5f) {
            float x = (w - largura) / 2f;
            float y = h - 2f;
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(erro ? COR_ERRO : COR_ACAO);
            g2.draw(new Line2D.Double(x, y, x + largura, y));
        }
        g2.dispose();
    }

    /** Anima o progresso do indicador de foco em ~12 passos (~180ms). */
    private static class AnimacaoFoco {
        float progresso = 0f;
        private boolean focado = false;
        private final Timer timer;

        AnimacaoFoco(JComponent componente) {
            timer = new Timer(15, e -> {
                float alvo = focado ? 1f : 0f;
                float passo = 0.16f;
                if (Math.abs(progresso - alvo) <= passo) {
                    progresso = alvo;
                    ((Timer) e.getSource()).stop();
                } else {
                    progresso += (alvo > progresso) ? passo : -passo;
                }
                componente.repaint();
            });
            componente.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; timer.start(); }
                @Override public void focusLost(FocusEvent e)   { focado = false; timer.start(); }
            });
        }
    }

    // =========================================================================
    // COMPONENTES
    // =========================================================================

    /** Campo de texto em vidro com indicador de foco animado. */
    private static class CampoTexto extends JTextField {
        private boolean erro = false;
        private final AnimacaoFoco animacao;

        CampoTexto() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            animacao = new AnimacaoFoco(this);
        }

        void setEstadoErro(boolean valor) {
            this.erro = valor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            pintarVidro(g, this, erro);
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            pintarContorno(g, this, erro, animacao.progresso);
        }
    }

    /** ComboBox com o mesmo acabamento em vidro e a mesma animação de foco. */
    private static class CampoCombo<T> extends JComboBox<T> {
        private final AnimacaoFoco animacao;

        CampoCombo() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setFocusable(true);
            setUI(new ComboVidroUI());
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 28));
            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean focus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, i, sel, focus);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
                    lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                    if (i == -1) {
                        lbl.setOpaque(false);
                        lbl.setForeground(COR_TEXTO_CAMPO);
                    } else if (sel) {
                        lbl.setOpaque(true);
                        lbl.setBackground(new Color(255, 173, 51, 60));
                        lbl.setForeground(COR_TEXTO_CAMPO);
                    } else {
                        lbl.setOpaque(true);
                        lbl.setBackground(Color.WHITE);
                        lbl.setForeground(COR_TEXTO_CAMPO);
                    }
                    return lbl;
                }
            });
            animacao = new AnimacaoFoco(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            pintarVidro(g, this, false);
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            pintarContorno(g, this, false, animacao.progresso);
        }
    }

    /** Remove a pintura padrão do Swing e estiliza apenas o botão de seta. */
    private static class ComboVidroUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // vazio de propósito: o próprio componente já pinta o fundo em vidro
        }

        @Override
        protected JButton createArrowButton() {
            JButton seta = new JButton("\u25BE");
            seta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            seta.setForeground(COR_LABEL);
            seta.setContentAreaFilled(false);
            seta.setBorderPainted(false);
            seta.setFocusPainted(false);
            seta.setOpaque(false);
            seta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return seta;
        }
    }

    /** Painel com fundo em gradiente suave, usado como cartão central. */
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

    /** Botão de ação com sombra suave, reflexo no topo e reação a hover/clique. */
    private static class BotaoAcao extends JButton {
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Icon icone) {
            super(texto, icone);
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

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? COR_ACAO_ESCURA : (sobreMouse ? COR_ACAO_CLARA : COR_ACAO);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 25));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Ícone vetorial de "salvar" (check dentro de um círculo), desenhado com Java2D. */
    private static class IconeSalvar implements Icon {
        private final int tamanho;
        private final Color cor;

        IconeSalvar(int tamanho, Color cor) {
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

            g2.draw(new Ellipse2D.Double(1.5, 1.5, 21, 21));

            Path2D check = new Path2D.Double();
            check.moveTo(7.0, 12.5);
            check.lineTo(10.6, 16.2);
            check.lineTo(17.2, 8.2);
            g2.draw(check);

            g2.dispose();
        }
    }
}