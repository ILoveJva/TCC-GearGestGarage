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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

public class V_CadastrarCliente extends JPanel {

    private JPanel pnl_CardCentral;
    private JPanel pnl_Formulario;
    private JLabel lbl_TituloPaginacao;

    private JLabel lbl_Nome, lbl_CPF, lbl_Celular, lbl_Email, lbl_CEP, lbl_Numero, lbl_Endereco, lbl_Complemento;
    private GlassTextField txt_Nome, txt_CPF, txt_Celular, txt_Email, txt_CEP, txt_Numero, txt_Endereco, txt_Complemento;
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
    private static final int TAMANHO_FONTE_LABEL = 18;
    private static final int TAMANHO_FONTE_CAMPO = 16;
    private static final int LARGURA_CAMPO       = 140;
    private static final int ALTURA_CAMPO        = 26;
    private static final int TAMANHO_FONTE_BOTAO = 22;
    private static final int LARGURA_BOTAO       = 345;
    private static final int ALTURA_BOTAO        = 66;
    private static final int TAMANHO_ICONE_BOTAO = 40;

    private OficinaController controller;

    public V_CadastrarCliente(OficinaController controller) {
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
        pnl_CardCentral.setPreferredSize(new Dimension(680, 520));
        pnl_CardCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lbl_TituloPaginacao = new JLabel("Página Inicial > Cadastrar Cliente");
        lbl_TituloPaginacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_TituloPaginacao.setForeground(COR_TITULO);

        pnl_Formulario = new JPanel(new GridLayout(4, 2, 25, 15));
        pnl_Formulario.setOpaque(false);

        lbl_Nome     = criarLabel("Nome completo *");
        txt_Nome     = criarTextField();

        lbl_CPF      = criarLabel("CPF * (ex: 000.000.000-00)");
        txt_CPF      = criarTextField();

        lbl_Celular  = criarLabel("Celular * (ex: (00) 00000-0000)");
        txt_Celular  = criarTextField();

        lbl_Email    = criarLabel("E-mail *");
        txt_Email    = criarTextField();

        lbl_CEP      = criarLabel("CEP (ex: 00000-000)");
        txt_CEP      = criarTextField();

        lbl_Numero   = criarLabel("Número");
        txt_Numero   = criarTextField();

        lbl_Endereco    = criarLabel("Endereço");
        txt_Endereco    = criarTextField();

        lbl_Complemento = criarLabel("Complemento");
        txt_Complemento = criarTextField();

        pnl_Formulario.add(criarContainerVertical(lbl_Nome,       txt_Nome));
        pnl_Formulario.add(criarContainerVertical(lbl_CPF,        txt_CPF));
        pnl_Formulario.add(criarContainerVertical(lbl_Celular,    txt_Celular));
        pnl_Formulario.add(criarContainerVertical(lbl_Email,      txt_Email));
        pnl_Formulario.add(criarContainerVertical(lbl_CEP,        txt_CEP));
        pnl_Formulario.add(criarContainerVertical(lbl_Numero,     txt_Numero));
        pnl_Formulario.add(criarContainerVertical(lbl_Endereco,   txt_Endereco));
        pnl_Formulario.add(criarContainerVertical(lbl_Complemento, txt_Complemento));

        btn_Cadastrar = new BotaoAcao("CADASTRAR CLIENTE", new IconeAdicionarUsuario(TAMANHO_ICONE_BOTAO, Color.WHITE));
        btn_Cadastrar.setPreferredSize(new Dimension(LARGURA_BOTAO, ALTURA_BOTAO));
    }

    private void layoutComponents() {
        pnl_CardCentral.add(lbl_TituloPaginacao, BorderLayout.NORTH);
        pnl_CardCentral.add(pnl_Formulario, BorderLayout.CENTER);

        JPanel pnl_ContainerBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        pnl_ContainerBotao.setOpaque(false);
        pnl_ContainerBotao.add(btn_Cadastrar);
        pnl_CardCentral.add(pnl_ContainerBotao, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    // =========================================================================
    // FILTROS DE ENTRADA (DocumentFilter)
    // =========================================================================
    private void aplicarFiltros() {
        ((AbstractDocument) txt_Nome.getDocument()).setDocumentFilter(new FiltroLetras());
        ((AbstractDocument) txt_CPF.getDocument()).setDocumentFilter(new FiltroCpf());
        ((AbstractDocument) txt_Celular.getDocument()).setDocumentFilter(new FiltroCelular());
        ((AbstractDocument) txt_CEP.getDocument()).setDocumentFilter(new FiltroCep());
        ((AbstractDocument) txt_Numero.getDocument()).setDocumentFilter(new FiltroDigitos(10));
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

        String cep = txt_CEP.getText().replaceAll("[^0-9]", "");
        if (!cep.isEmpty() && cep.length() != 8) {
            marcarErro(txt_CEP);
            msg.append("• CEP incompleto (informe os 8 dígitos).\n");
            ok = false;
        }

        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "Corrija os campos destacados em vermelho:\n\n" + msg,
                    "Dados Inválidos", JOptionPane.WARNING_MESSAGE);
        }
        return ok;
    }

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

    private void limparTodosErros() {
        for (GlassTextField f : new GlassTextField[]{ txt_Nome, txt_CPF, txt_Celular, txt_Email, txt_CEP, txt_Numero })
            limparErro(f);
    }

    private void vincularAcoes() {
        btn_Cadastrar.addActionListener(e -> {
            if (!validarFormulario()) return;

            String nome    = txt_Nome.getText().trim();
            String cpf     = txt_CPF.getText().trim();
            String celular = txt_Celular.getText().trim();
            String email   = txt_Email.getText().trim();

            if (controller != null) {
                try {
                    controller.salvarCliente(nome, cpf, celular, email);
                    JOptionPane.showMessageDialog(this,
                            "Cliente \"" + nome + "\" cadastrado com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCamposFormulario();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erro ao salvar cliente: " + ex.getMessage(),
                            "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro: O controlador do sistema não foi localizado.",
                        "Erro de Link de Dados", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void limparCamposFormulario() {
        txt_Nome.setText(""); txt_CPF.setText(""); txt_Celular.setText("");
        txt_Email.setText(""); txt_CEP.setText(""); txt_Numero.setText("");
        txt_Endereco.setText(""); txt_Complemento.setText("");
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

    private GlassTextField criarTextField() {
        GlassTextField f = new GlassTextField();
        f.setPreferredSize(new Dimension(LARGURA_CAMPO, ALTURA_CAMPO));
        return f;
    }

    // =========================================================================
    // INNER CLASSES — DocumentFilter
    // =========================================================================

    /** Permite apenas letras (incluindo acentuadas), espaços e hífens. */
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

    /** Auto-formata CPF como XXX.XXX.XXX-XX (aceita apenas dígitos). */
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

    /** Auto-formata celular como (XX) XXXXX-XXXX (aceita apenas dígitos). */
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

    /** Auto-formata CEP como XXXXX-XXX (aceita apenas dígitos). */
    private static class FiltroCep extends DocumentFilter {
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
            if (d.length() > 8) d = d.substring(0, 8);
            fb.replace(0, fb.getDocument().getLength(), formatar(d), attr);
        }
        private String formatar(String d) {
            int n = d.length();
            if (n <= 5) return d;
            return d.substring(0,5) + "-" + d.substring(5);
        }
    }

    /** Permite apenas dígitos numéricos com limite de comprimento. */
    private static class FiltroDigitos extends DocumentFilter {
        private final int max;
        FiltroDigitos(int max) { this.max = max; }
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            int espaço = max - fb.getDocument().getLength();
            if (espaço > 0) super.insertString(fb, off, novo.substring(0, Math.min(novo.length(), espaço)), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            int novoTamanho = fb.getDocument().getLength() - len + novo.length();
            if (novoTamanho <= max) super.replace(fb, off, len, novo, attr);
            else {
                int espaço = max - (fb.getDocument().getLength() - len);
                if (espaço > 0) super.replace(fb, off, len, novo.substring(0, espaço), attr);
            }
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
     * Ícone vetorial (pessoa + sinal de "+") desenhado diretamente com Java2D,
     * sem depender de arquivo externo — escala perfeitamente para qualquer
     * tamanho de fonte/botão.
     */
    private static class IconeAdicionarUsuario implements Icon {
        private final int tamanho;
        private final Color cor;

        IconeAdicionarUsuario(int tamanho, Color cor) {
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

            // Cabeça
            g2.draw(new Ellipse2D.Double(6.6, 0.9, 11.2, 11.2));

            // Corpo, aberto do lado direito (onde entra o "+")
            Path2D corpo = new Path2D.Double();
            corpo.moveTo(15.4, 12.3);
            corpo.curveTo(17.6, 13.0, 19.2, 13.9, 19.2, 13.9);
            corpo.moveTo(12.2, 12.1);
            corpo.curveTo(5.6, 12.3, 1.0, 17.6, 1.0, 21.6);
            corpo.curveTo(1.0, 22.6, 1.8, 23.2, 2.8, 23.2);
            corpo.lineTo(15.5, 23.2);
            g2.draw(corpo);

            // Sinal de "+"
            g2.draw(new Line2D.Double(19.6, 15.6, 19.6, 22.6));
            g2.draw(new Line2D.Double(16.1, 19.1, 23.1, 19.1));

            g2.dispose();
        }
    }
}