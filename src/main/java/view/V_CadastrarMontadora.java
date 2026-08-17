package view;

import controller.OficinaController;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Path2D;
import java.awt.geom.Line2D;

public class V_CadastrarMontadora extends JPanel {

    private JPanel pnl_CardCentral;
    private JLabel lbl_TituloPaginacao;
    private JPanel pnl_Formulario;

    private JLabel lbl_Nome, lbl_Pais;
    private GlassTextField txt_Nome, txt_Pais;
    private BotaoAcao btn_Cadastrar;

    // Paleta harmonizada com o efeito de vidro das caixas de texto
    private static final Color COR_FUNDO_PAGINA = Color.decode("#F5F7FA");
    private static final Color COR_CARD_TOPO    = Color.decode("#FFFFFF");
    private static final Color COR_CARD_BASE    = Color.decode("#EEF2F7");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    // Cor de ação (tema original preservado) e suas variações de hover/pressionado
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    // Ajustes rápidos de tipografia/tamanho — mexa só aqui para alterar tudo de uma vez
    private static final int RAIO_COMPONENTE     = 12;   // arredondamento compartilhado (campos + botão)
    private static final int TAMANHO_FONTE_LABEL = 20;
    private static final int TAMANHO_FONTE_CAMPO = 12;
    private static final int LARGURA_CAMPO       = 140;
    private static final int ALTURA_CAMPO        = 34;
    private static final int TAMANHO_FONTE_BOTAO = 14;
    private static final int LARGURA_BOTAO       = 250;
    private static final int ALTURA_BOTAO        = 44;
    private static final int TAMANHO_ICONE_BOTAO = 18;

    private final OficinaController controller;

    public V_CadastrarMontadora(OficinaController controller) {
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
        pnl_CardCentral.setPreferredSize(new Dimension(500, 280));
        pnl_CardCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lbl_TituloPaginacao = new JLabel("Página Inicial > Cadastrar Montadora");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(COR_TITULO);

        pnl_Formulario = new JPanel(new GridLayout(2, 1, 0, 15));
        pnl_Formulario.setOpaque(false);

        lbl_Nome = criarLabel("Nome da Montadora *");
        txt_Nome = criarTextField();
        txt_Nome.setToolTipText("Ex: Toyota, Volkswagen, Ford");

        lbl_Pais = criarLabel("País de Origem");
        txt_Pais = criarTextField();
        txt_Pais.setToolTipText("Ex: Japão, Alemanha, Estados Unidos");

        pnl_Formulario.add(criarContainerVertical(lbl_Nome, txt_Nome));
        pnl_Formulario.add(criarContainerVertical(lbl_Pais, txt_Pais));

        btn_Cadastrar = new BotaoAcao("CADASTRAR MONTADORA", new IconeAdicionarMontadora(TAMANHO_ICONE_BOTAO, Color.WHITE));
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

    private void aplicarFiltros() {
        ((AbstractDocument) txt_Nome.getDocument()).setDocumentFilter(new FiltroTexto());
        ((AbstractDocument) txt_Pais.getDocument()).setDocumentFilter(new FiltroTexto());
    }

    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            limparErro(txt_Nome);

            String nome = txt_Nome.getText().trim();
            String pais = txt_Pais.getText().trim();

            if (nome.length() < 2) {
                marcarErro(txt_Nome);
                JOptionPane.showMessageDialog(this,
                        "O nome da montadora deve ter pelo menos 2 caracteres.",
                        "Campo Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                controller.salvarMontadora(nome, pais.isEmpty() ? "Não informado" : pais);
                JOptionPane.showMessageDialog(this,
                        "Montadora \"" + nome + "\" cadastrada com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                txt_Nome.setText("");
                txt_Pais.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao cadastrar montadora: " + ex.getMessage(),
                        "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // =========================================================================
    // TRATAMENTO DE ERROS
    // =========================================================================
    private void marcarErro(GlassTextField field) {
        field.setEstadoErro(true);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
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
    private JPanel criarContainerVertical(JLabel label, JTextField field) {
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

    private GlassTextField criarTextField() {
        GlassTextField f = new GlassTextField();
        f.setPreferredSize(new Dimension(LARGURA_CAMPO, ALTURA_CAMPO));
        return f;
    }

    // =========================================================================
    // INNER CLASSES — DocumentFilter
    // =========================================================================

    /** Permite letras (incluindo acentuadas), espaços e hífens. */
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
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-.]", "");
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
     * Ícone vetorial (fábrica/montadora + sinal de "+") desenhado diretamente
     * com Java2D, sem depender de arquivo externo — escala perfeitamente para
     * qualquer tamanho de fonte/botão. Segue a mesma linguagem visual (traço
     * arredondado) do ícone usado em Cadastrar Cliente.
     */
    private static class IconeAdicionarMontadora implements Icon {
        private final int tamanho;
        private final Color cor;

        IconeAdicionarMontadora(int tamanho, Color cor) {
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

            // Telhado (linha aberta, no mesmo espírito do corpo aberto do ícone de pessoa)
            Path2D telhado = new Path2D.Double();
            telhado.moveTo(1.0, 9.0);
            telhado.lineTo(7.5, 2.2);
            telhado.lineTo(14.0, 9.0);
            g2.draw(telhado);

            // Corpo do prédio/fábrica
            g2.draw(new RoundRectangle2D.Double(1.0, 9.0, 13.0, 13.2, 2.0, 2.0));

            // Porta de entrada
            g2.draw(new RoundRectangle2D.Double(6.2, 16.0, 2.6, 6.2, 1.0, 1.0));

            // Sinal de "+"
            g2.draw(new Line2D.Double(19.6, 15.6, 19.6, 22.6));
            g2.draw(new Line2D.Double(16.1, 19.1, 23.1, 19.1));

            g2.dispose();
        }
    }
}