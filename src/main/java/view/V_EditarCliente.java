package view;

import controller.OficinaController;
import model.Cliente;

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
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class V_EditarCliente extends JPanel {

    private JPanel pnl_CardCentral;
    private PainelQuadro pnl_Quadro;
    private JPanel pnl_Formulario;
    private JLabel lbl_TituloPaginacao;

    private CampoTexto txt_Nome, txt_CPF, txt_Celular, txt_Email;
    private BotaoAcao btn_Salvar;

    private final OficinaController controller;
    private final Cliente cliente;

    // Paleta harmonizada com o efeito de vidro das caixas de texto
    // (mesma linguagem visual usada em V_CadastrarCliente)
    private static final Color COR_FUNDO_PAGINA = Color.decode("#F5F7FA");
    private static final Color COR_CARD_TOPO    = Color.decode("#FFFFFF");
    private static final Color COR_CARD_BASE    = Color.decode("#EEF2F7");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    // Cor de ação (tema original preservado) e variações de hover/pressionado
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    // Campo de texto padrão (vidro translúcido, sem relevo/sombra 3D)
    private static final Color COR_BORDA_PADRAO  = Color.decode("#D7DEE7");
    private static final Color COR_ERRO          = Color.decode("#D63A44");

    // Ajustes rápidos de tipografia/tamanho
    private static final int RAIO_COMPONENTE     = 12;
    private static final int RAIO_CAMPO          = 8;
    private static final int RAIO_QUADRO         = 16;
    private static final int TAMANHO_FONTE_LABEL = 16;
    private static final int TAMANHO_FONTE_CAMPO = 15;
    private static final int ALTURA_CAMPO        = 23;
    private static final int TAMANHO_FONTE_BOTAO = 16;
    private static final int LARGURA_BOTAO       = 260;
    private static final int ALTURA_BOTAO        = 46;
    private static final int TAMANHO_ICONE_BOTAO = 20;

    public V_EditarCliente(OficinaController controller, Cliente cliente) {
        this.controller = controller;
        this.cliente = cliente;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        initComponents();
        layoutComponents();
        aplicarFiltros();
        preencherCampos();
        vincularAcoes();
    }

    private void initComponents() {
        pnl_CardCentral = new PainelGradiente(new BorderLayout(0, 20), COR_CARD_TOPO, COR_CARD_BASE);
        pnl_CardCentral.setPreferredSize(new Dimension(560, 440));
        pnl_CardCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lbl_TituloPaginacao = new JLabel("Consultar Clientes > Editar Cliente");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(COR_TITULO);

        // "Quadro" fosco por trás do formulário — dá volume/profundidade,
        // como se as informações estivessem emolduradas dentro do card
        pnl_Quadro = new PainelQuadro();
        pnl_Quadro.setLayout(new BorderLayout());
        pnl_Quadro.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        pnl_Formulario = new JPanel(new GridLayout(4, 1, 0, 14));
        pnl_Formulario.setOpaque(false);

        txt_Nome    = criarTextField();
        txt_CPF     = criarTextField();
        txt_Celular = criarTextField();
        txt_Email   = criarTextField();

        pnl_Formulario.add(criarContainerVertical(criarLabel("Nome completo *"), txt_Nome));
        pnl_Formulario.add(criarContainerVertical(criarLabel("CPF * (ex: 000.000.000-00)"), txt_CPF));
        pnl_Formulario.add(criarContainerVertical(criarLabel("Celular * (ex: (00) 00000-0000)"), txt_Celular));
        pnl_Formulario.add(criarContainerVertical(criarLabel("E-mail *"), txt_Email));

        pnl_Quadro.add(pnl_Formulario, BorderLayout.CENTER);

        btn_Salvar = new BotaoAcao("SALVAR ALTERAÇÕES", new IconeSalvar(TAMANHO_ICONE_BOTAO, Color.WHITE));
        btn_Salvar.setPreferredSize(new Dimension(LARGURA_BOTAO, ALTURA_BOTAO));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Quadro, BorderLayout.CENTER);

        JPanel pnl_ContainerBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnl_ContainerBotao.setOpaque(false);
        pnl_ContainerBotao.add(btn_Salvar);
        pnl_CardCentral.add(pnl_ContainerBotao, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    private void preencherCampos() {
        txt_Nome.setText(cliente.getNome());
        txt_CPF.setText(cliente.getCpf());
        txt_Celular.setText(cliente.getTelefone());
        txt_Email.setText(cliente.getEmail());
    }

    private void aplicarFiltros() {
        ((AbstractDocument) txt_Nome.getDocument()).setDocumentFilter(new FiltroLetras());
        ((AbstractDocument) txt_CPF.getDocument()).setDocumentFilter(new FiltroCpf());
        ((AbstractDocument) txt_Celular.getDocument()).setDocumentFilter(new FiltroCelular());
    }

    // =========================================================================
    // VALIDAÇÃO E TRATAMENTO DE ERROS
    // =========================================================================
    private boolean validarFormulario() {
        limparTodosErros();
        boolean ok = true;
        StringBuilder msg = new StringBuilder();

        String nome = txt_Nome.getText().trim();
        if (nome.length() < 3) {
            marcarErro(txt_Nome);
            msg.append("• Nome deve ter pelo menos 3 caracteres.\n");
            ok = false;
        }

        String cpf = txt_CPF.getText().replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            marcarErro(txt_CPF);
            msg.append("• CPF incompleto (informe os 11 dígitos).\n");
            ok = false;
        }

        String cel = txt_Celular.getText().replaceAll("[^0-9]", "");
        if (cel.length() < 10 || cel.length() > 11) {
            marcarErro(txt_Celular);
            msg.append("• Celular inválido (DDD + 8 ou 9 dígitos).\n");
            ok = false;
        }

        String email = txt_Email.getText().trim();
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[\\w.\\-]+$")) {
            marcarErro(txt_Email);
            msg.append("• E-mail em formato inválido.\n");
            ok = false;
        }

        if (!ok) {
            DialogoAlerta.aviso(this, "Corrija os campos destacados em vermelho:\n\n" + msg, "Dados Inválidos");
        }
        return ok;
    }

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

    private void limparTodosErros() {
        for (CampoTexto f : new CampoTexto[]{ txt_Nome, txt_CPF, txt_Celular, txt_Email })
            limparErro(f);
    }

    private void vincularAcoes() {
        btn_Salvar.addActionListener(e -> {
            if (!validarFormulario()) return;

            String nome    = txt_Nome.getText().trim();
            String cpf     = txt_CPF.getText().trim();
            String celular = txt_Celular.getText().trim();
            String email   = txt_Email.getText().trim();

            try {
                controller.atualizarCliente(cliente.getIdUsuario(), nome, cpf, email, celular);
                DialogoAlerta.sucesso(this, "Cliente \"" + nome + "\" atualizado com sucesso!", "Sucesso");
                navegarPara(new V_VisualizarClientes(controller));
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao atualizar cliente: " + ex.getMessage(), "Erro no Sistema");
            }
        });
    }

    private void navegarPara(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE ESTILIZAÇÃO
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

    private CampoTexto criarTextField() {
        CampoTexto f = new CampoTexto();
        f.setPreferredSize(new Dimension(100, ALTURA_CAMPO + 16));
        return f;
    }

    // =========================================================================
    // INNER CLASSES — DocumentFilter (regras de negócio inalteradas)
    // =========================================================================
    private static class FiltroLetras extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, filtrar(text), attr);
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, offset, length, filtrar(text), attr);
        }
        private String filtrar(String t) {
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ\\s'\\-]", "");
        }
    }

    private static class FiltroCpf extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + text + atual.substring(off), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + (text != null ? text : "") + atual.substring(off + len), attr);
        }
        @Override
        public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + atual.substring(off + len), null);
        }
        private void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
            String d = texto.replaceAll("[^0-9]", "");
            if (d.length() > 11) d = d.substring(0, 11);
            fb.replace(0, fb.getDocument().getLength(), formatar(d), attr);
        }
        private String formatar(String d) {
            int n = d.length();
            if (n <= 3) return d;
            if (n <= 6) return d.substring(0,3) + "." + d.substring(3);
            if (n <= 9) return d.substring(0,3) + "." + d.substring(3,6) + "." + d.substring(6);
            return d.substring(0,3) + "." + d.substring(3,6) + "." + d.substring(6,9) + "-" + d.substring(9);
        }
    }

    private static class FiltroCelular extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + text + atual.substring(off), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + (text != null ? text : "") + atual.substring(off + len), attr);
        }
        @Override
        public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + atual.substring(off + len), null);
        }
        private void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
            String d = texto.replaceAll("[^0-9]", "");
            if (d.length() > 11) d = d.substring(0, 11);
            fb.replace(0, fb.getDocument().getLength(), formatar(d), attr);
        }
        private String formatar(String d) {
            int n = d.length();
            if (n == 0) return "";
            if (n <= 2) return "(" + d;
            if (n <= 7) return "(" + d.substring(0,2) + ") " + d.substring(2);
            return "(" + d.substring(0,2) + ") " + d.substring(2,7) + "-" + d.substring(7, Math.min(n,11));
        }
    }

    // =========================================================================
    // INNER CLASSES — Glassmorphism / animações
    // (mesma linguagem visual de V_CadastrarCliente — não alterar lá)
    // =========================================================================

    /**
     * Campo de texto com efeito de glassmorphism: vidro fosco translúcido,
     * brilho difuso no topo (camadas translúcidas sobrepostas) e um
     * indicador de foco ANIMADO — linha de destaque que cresce suavemente a
     * partir do centro ao receber foco e recolhe ao perdê-lo. Sem sombra ou
     * relevo 3D. Apenas pintura do componente — nenhuma regra de negócio.
     */
    private static class CampoTexto extends JTextField {
        private boolean erro = false;
        private boolean focado = false;
        private float progressoFoco = 0f; // 0 = sem linha de destaque, 1 = linha completa
        private Timer timerFoco;

        CampoTexto() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

            // Anima a transição do indicador de foco em ~12 passos (~180ms)
            timerFoco = new Timer(15, e -> {
                float alvo = focado ? 1f : 0f;
                float passo = 0.16f;
                if (Math.abs(progressoFoco - alvo) <= passo) {
                    progressoFoco = alvo;
                    timerFoco.stop();
                } else {
                    progressoFoco += (alvo > progressoFoco) ? passo : -passo;
                }
                repaint();
            });

            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; timerFoco.start(); }
                @Override public void focusLost(FocusEvent e)   { focado = false; timerFoco.start(); }
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
            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_CAMPO, RAIO_CAMPO);

            // Preenchimento translúcido — o vidro propriamente dito
            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, erro ? 195 : 175),
                    0, h, new Color(255, 255, 255, erro ? 140 : 115)
            );
            g2.setPaint(vidro);
            g2.fill(forma);

            // Brilho difuso no topo — camadas com alfa decrescente,
            // simulando o desfoque de um vidro fosco
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
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Contorno base, fino e neutro (vermelho em erro)
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(erro ? COR_ERRO : COR_BORDA_PADRAO);
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 2, RAIO_CAMPO, RAIO_CAMPO));

            // Realce claro só na borda superior — luz "pegando" a borda do vidro
            if (!erro) {
                g2.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(255, 255, 255, 170));
                g2.draw(new Line2D.Double(RAIO_CAMPO * 0.7, 1.1, w - RAIO_CAMPO * 0.7, 1.1));
            }

            // Indicador de foco ANIMADO: linha de destaque crescendo do centro.
            // Em erro, fica sempre totalmente visível até o campo ser corrigido.
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
    }

    /**
     * Painel com fundo em gradiente suave (branco levemente esfriado em
     * direção a um cinza-azulado), usado no cartão externo.
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
     * "Quadro" fosco por trás das informações do formulário: um painel de
     * vidro leitoso, mais opaco que os campos, com uma sombra suave por
     * baixo e um leve realce no topo — como se o formulário estivesse
     * emoldurado dentro do card, dando volume/profundidade em vez de ficar
     * "flutuando" direto sobre o gradiente do card.
     */
    private static class PainelQuadro extends JPanel {
        PainelQuadro() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D forma = new RoundRectangle2D.Double(1, 1, w - 2, h - 3, RAIO_QUADRO, RAIO_QUADRO);

            // Sombra suave por baixo, dando a sensação de que o quadro está
            // levemente "elevado/recuado" em relação ao fundo do card
            g2.setColor(new Color(70, 90, 120, 28));
            g2.fill(new RoundRectangle2D.Double(1, 3, w - 2, h - 3, RAIO_QUADRO, RAIO_QUADRO));

            // Vidro fosco — mais opaco que os campos de texto, para servir
            // de "moldura" sólida por trás deles
            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 165),
                    0, h, new Color(255, 255, 255, 205)
            );
            g2.setPaint(vidro);
            g2.fill(forma);

            // Realce difuso no topo, mesma linguagem dos campos em vidro
            Shape clipOriginal = g2.getClip();
            g2.clip(forma);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fill(new RoundRectangle2D.Double(0, -h * 0.6, w, h * 0.9, RAIO_QUADRO, RAIO_QUADRO));
            g2.setClip(clipOriginal);

            // Contorno fino
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(160, 175, 195, 130));
            g2.draw(forma);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação com a mesma linguagem visual dos campos em vidro:
     * cantos arredondados, sombra suave, reflexo no topo e reação animada
     * a hover/clique (troca de cor + repaint). A cor de ação original é
     * mantida.
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

            // Sombra fina e neutra, mesma linguagem visual usada nos campos em vidro
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? COR_ACAO_ESCURA : (sobreMouse ? COR_ACAO_CLARA : COR_ACAO);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            // Reflexo suave no topo, reforçando a sensação de vidro dos campos
            g2.setColor(new Color(255, 255, 255, 25));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Ícone vetorial de "salvar" (disquete estilizado), desenhado com
     * Java2D — sem depender de arquivo externo, escala com o botão.
     */
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

            // Corpo do disquete
            g2.draw(new RoundRectangle2D.Double(2, 2, 20, 20, 3, 3));
            // Aba superior direita (dobra do disquete)
            g2.draw(new Line2D.Double(15, 2, 15, 8));
            g2.draw(new Line2D.Double(15, 8, 6, 8));
            g2.draw(new Line2D.Double(6, 8, 6, 2));
            // "Etiqueta" inferior
            g2.draw(new RoundRectangle2D.Double(6.5, 13.5, 11, 8, 2, 2));

            g2.dispose();
        }
    }
}