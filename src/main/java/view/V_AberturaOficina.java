package view;

import br.com.oficina.shared.viacep.ViaCepClient;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

/**
 * Tela de "abertura" de uma nova oficina: cadastra a oficina e o usuário
 * administrador que fará login nela. Acessível a partir do link "Não tem uma
 * conta?" no login. Ao concluir, entra automaticamente na oficina recém-criada.
 */
public class V_AberturaOficina extends JFrame {

    private final OficinaController controller;

    private CampoTexto txt_NomeOficina, txt_Cnpj, txt_TelefoneOficina;
    private CampoTexto txt_Cep, txt_Numero, txt_Endereco, txt_Complemento;
    private CampoTexto txt_NomeAdmin, txt_CpfAdmin, txt_EmailAdmin, txt_TelefoneAdmin;
    private CampoSenha txt_Senha, txt_ConfirmarSenha;
    private BotaoAcao btn_Abrir;

    private static final Color COR_FUNDO_PAGINA = Color.decode("#F5F7FA");
    private static final Color COR_CARD_TOPO    = Color.decode("#FFFFFF");
    private static final Color COR_CARD_BASE    = Color.decode("#EEF2F7");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_SECAO        = Color.decode("#2980B9");

    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    private static final Color COR_BORDA_PADRAO = Color.decode("#D7DEE7");
    private static final Color COR_ERRO         = Color.decode("#D63A44");

    private static final int RAIO_COMPONENTE     = 12;
    private static final int RAIO_CAMPO          = 8;
    private static final int TAMANHO_FONTE_LABEL = 13;
    private static final int TAMANHO_FONTE_CAMPO = 14;
    private static final int ALTURA_CAMPO        = 32;

    public V_AberturaOficina(OficinaController controller) {
        this.controller = controller;

        setTitle("Gear Gest Garage - Abertura de Oficina");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 780);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(COR_FUNDO_PAGINA);
        raiz.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        setContentPane(raiz);

        JLabel lblTitulo = new JLabel("Abertura de Oficina");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COR_TITULO);

        JLabel lblSub = new JLabel("Cadastre sua oficina e crie o acesso do administrador.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COR_LABEL);
        lblSub.setBorder(BorderFactory.createEmptyBorder(4, 0, 16, 0));

        JPanel pnlTopo = new JPanel();
        pnlTopo.setLayout(new BoxLayout(pnlTopo, BoxLayout.Y_AXIS));
        pnlTopo.setOpaque(false);
        pnlTopo.add(lblTitulo);
        pnlTopo.add(lblSub);

        JPanel pnlFormulario = construirFormulario();

        JScrollPane scroll = new JScrollPane(pnlFormulario);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ScrollBarPadrao.aplicar(scroll);

        JButton btnVoltar = botaoLink("← Já tenho uma conta / Voltar para o login");
        btnVoltar.addActionListener(e -> voltarParaLogin());

        JPanel pnlRodape = new JPanel(new BorderLayout());
        pnlRodape.setOpaque(false);
        pnlRodape.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        pnlRodape.add(btnVoltar, BorderLayout.WEST);
        pnlRodape.add(btn_Abrir, BorderLayout.EAST);

        raiz.add(pnlTopo, BorderLayout.NORTH);
        raiz.add(scroll, BorderLayout.CENTER);
        raiz.add(pnlRodape, BorderLayout.SOUTH);

        aplicarFiltros();
        vincularAcoes();
    }

    private JPanel construirFormulario() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        // ---- Seção: Dados da Oficina ----
        JPanel cardOficina = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        cardOficina.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardOficina.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblSecaoOficina = criarLabelSecao("DADOS DA OFICINA");

        JPanel gridOficina1 = new JPanel(new GridLayout(1, 2, 20, 12));
        gridOficina1.setOpaque(false);
        txt_NomeOficina = criarCampo();
        txt_Cnpj = criarCampo();
        gridOficina1.add(criarContainerVertical(criarLabel("Nome da oficina *"), txt_NomeOficina));
        gridOficina1.add(criarContainerVertical(criarLabel("CNPJ * (00.000.000/0000-00)"), txt_Cnpj));

        JPanel gridOficina2 = new JPanel(new GridLayout(1, 2, 20, 12));
        gridOficina2.setOpaque(false);
        txt_TelefoneOficina = criarCampo();
        txt_Cep = criarCampo();
        gridOficina2.add(criarContainerVertical(criarLabel("Telefone * (ex: (00) 00000-0000)"), txt_TelefoneOficina));
        gridOficina2.add(criarContainerVertical(criarLabel("CEP (ex: 00000-000)"), txt_Cep));

        JPanel gridOficina3 = new JPanel(new GridLayout(1, 3, 20, 12));
        gridOficina3.setOpaque(false);
        txt_Numero = criarCampo();
        txt_Endereco = criarCampo();
        txt_Complemento = criarCampo();
        gridOficina3.add(criarContainerVertical(criarLabel("Número"), txt_Numero));
        gridOficina3.add(criarContainerVertical(criarLabel("Endereço"), txt_Endereco));
        gridOficina3.add(criarContainerVertical(criarLabel("Complemento"), txt_Complemento));

        JPanel corpoOficina = new JPanel();
        corpoOficina.setLayout(new BoxLayout(corpoOficina, BoxLayout.Y_AXIS));
        corpoOficina.setOpaque(false);
        corpoOficina.add(gridOficina1);
        corpoOficina.add(Box.createVerticalStrut(12));
        corpoOficina.add(gridOficina2);
        corpoOficina.add(Box.createVerticalStrut(12));
        corpoOficina.add(gridOficina3);

        cardOficina.add(lblSecaoOficina, BorderLayout.NORTH);
        cardOficina.add(corpoOficina, BorderLayout.CENTER);

        // ---- Seção: Dados do Administrador ----
        JPanel cardAdmin = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        cardAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardAdmin.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblSecaoAdmin = criarLabelSecao("DADOS DO ADMINISTRADOR (LOGIN)");

        JPanel gridAdmin1 = new JPanel(new GridLayout(1, 2, 20, 12));
        gridAdmin1.setOpaque(false);
        txt_NomeAdmin = criarCampo();
        txt_CpfAdmin = criarCampo();
        gridAdmin1.add(criarContainerVertical(criarLabel("Nome completo *"), txt_NomeAdmin));
        gridAdmin1.add(criarContainerVertical(criarLabel("CPF * (000.000.000-00)"), txt_CpfAdmin));

        JPanel gridAdmin2 = new JPanel(new GridLayout(1, 2, 20, 12));
        gridAdmin2.setOpaque(false);
        txt_EmailAdmin = criarCampo();
        txt_TelefoneAdmin = criarCampo();
        gridAdmin2.add(criarContainerVertical(criarLabel("E-mail * (será seu login)"), txt_EmailAdmin));
        gridAdmin2.add(criarContainerVertical(criarLabel("Telefone * (ex: (00) 00000-0000)"), txt_TelefoneAdmin));

        JPanel gridAdmin3 = new JPanel(new GridLayout(1, 2, 20, 12));
        gridAdmin3.setOpaque(false);
        txt_Senha = criarCampoSenha();
        txt_ConfirmarSenha = criarCampoSenha();
        gridAdmin3.add(criarContainerVertical(criarLabel("Senha * (mín. 6 caracteres)"), txt_Senha));
        gridAdmin3.add(criarContainerVertical(criarLabel("Confirmar senha *"), txt_ConfirmarSenha));

        JPanel corpoAdmin = new JPanel();
        corpoAdmin.setLayout(new BoxLayout(corpoAdmin, BoxLayout.Y_AXIS));
        corpoAdmin.setOpaque(false);
        corpoAdmin.add(gridAdmin1);
        corpoAdmin.add(Box.createVerticalStrut(12));
        corpoAdmin.add(gridAdmin2);
        corpoAdmin.add(Box.createVerticalStrut(12));
        corpoAdmin.add(gridAdmin3);

        cardAdmin.add(lblSecaoAdmin, BorderLayout.NORTH);
        cardAdmin.add(corpoAdmin, BorderLayout.CENTER);

        btn_Abrir = new BotaoAcao("ABRIR OFICINA");
        btn_Abrir.setPreferredSize(new Dimension(220, 44));

        container.add(cardOficina);
        container.add(Box.createVerticalStrut(16));
        container.add(cardAdmin);
        return container;
    }

    // =========================================================================
    // FILTROS DE ENTRADA
    // =========================================================================
    private void aplicarFiltros() {
        ((AbstractDocument) txt_NomeOficina.getDocument()).setDocumentFilter(new FiltroLetras());
        ((AbstractDocument) txt_Cnpj.getDocument()).setDocumentFilter(new FiltroCnpj());
        ((AbstractDocument) txt_TelefoneOficina.getDocument()).setDocumentFilter(new FiltroTelefone());
        ((AbstractDocument) txt_Cep.getDocument()).setDocumentFilter(new FiltroCep());
        ((AbstractDocument) txt_Numero.getDocument()).setDocumentFilter(new FiltroDigitos(10));
        ((AbstractDocument) txt_NomeAdmin.getDocument()).setDocumentFilter(new FiltroLetras());
        ((AbstractDocument) txt_CpfAdmin.getDocument()).setDocumentFilter(new FiltroCpf());
        ((AbstractDocument) txt_TelefoneAdmin.getDocument()).setDocumentFilter(new FiltroTelefone());

        txt_Cep.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { verificarCep(); }
            @Override public void removeUpdate(DocumentEvent e) { }
            @Override public void changedUpdate(DocumentEvent e) { }
        });
    }

    private void verificarCep() {
        String cep = txt_Cep.getText().replaceAll("[^0-9]", "");
        if (cep.length() != 8) return;
        new SwingWorker<ViaCepClient.Endereco, Void>() {
            @Override protected ViaCepClient.Endereco doInBackground() throws Exception {
                return ViaCepClient.buscar(cep);
            }
            @Override protected void done() {
                try {
                    ViaCepClient.Endereco end = get();
                    if (end == null) { marcarErro(txt_Cep); return; }
                    limparErro(txt_Cep);
                    txt_Endereco.setText(end.formatado());
                    if (!end.complemento().isBlank()) txt_Complemento.setText(end.complemento());
                } catch (Exception ignored) {
                    // Falha de rede/serviço indisponível: usuário preenche o endereço manualmente.
                }
            }
        }.execute();
    }

    // =========================================================================
    // VALIDAÇÃO
    // =========================================================================
    private boolean validarFormulario() {
        limparTodosErros();
        boolean ok = true;
        StringBuilder msg = new StringBuilder();

        if (txt_NomeOficina.getText().trim().length() < 3) {
            marcarErro(txt_NomeOficina);
            msg.append("• Nome da oficina deve ter pelo menos 3 caracteres.\n");
            ok = false;
        }

        String cnpj = txt_Cnpj.getText().replaceAll("[^0-9]", "");
        if (cnpj.length() != 14) {
            marcarErro(txt_Cnpj);
            msg.append("• CNPJ incompleto (informe os 14 dígitos).\n");
            ok = false;
        }

        String telOficina = txt_TelefoneOficina.getText().replaceAll("[^0-9]", "");
        if (telOficina.length() < 10 || telOficina.length() > 11) {
            marcarErro(txt_TelefoneOficina);
            msg.append("• Telefone da oficina inválido (DDD + 8 ou 9 dígitos).\n");
            ok = false;
        }

        String cep = txt_Cep.getText().replaceAll("[^0-9]", "");
        if (!cep.isEmpty() && cep.length() != 8) {
            marcarErro(txt_Cep);
            msg.append("• CEP incompleto (informe os 8 dígitos).\n");
            ok = false;
        }

        if (txt_NomeAdmin.getText().trim().length() < 3) {
            marcarErro(txt_NomeAdmin);
            msg.append("• Nome do administrador deve ter pelo menos 3 caracteres.\n");
            ok = false;
        }

        String cpfAdmin = txt_CpfAdmin.getText().replaceAll("[^0-9]", "");
        if (cpfAdmin.length() != 11) {
            marcarErro(txt_CpfAdmin);
            msg.append("• CPF incompleto (informe os 11 dígitos).\n");
            ok = false;
        }

        String email = txt_EmailAdmin.getText().trim();
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[\\w.\\-]+$")) {
            marcarErro(txt_EmailAdmin);
            msg.append("• E-mail em formato inválido.\n");
            ok = false;
        }

        String telAdmin = txt_TelefoneAdmin.getText().replaceAll("[^0-9]", "");
        if (telAdmin.length() < 10 || telAdmin.length() > 11) {
            marcarErro(txt_TelefoneAdmin);
            msg.append("• Telefone do administrador inválido (DDD + 8 ou 9 dígitos).\n");
            ok = false;
        }

        String senha = new String(txt_Senha.getPassword());
        if (senha.length() < 6) {
            marcarErro(txt_Senha);
            msg.append("• Senha deve ter pelo menos 6 caracteres.\n");
            ok = false;
        }

        String confirmar = new String(txt_ConfirmarSenha.getPassword());
        if (!senha.equals(confirmar)) {
            marcarErro(txt_ConfirmarSenha);
            msg.append("• As senhas não conferem.\n");
            ok = false;
        }

        if (!ok) {
            DialogoAlerta.aviso(this, "Corrija os campos destacados em vermelho:\n\n" + msg, "Dados Inválidos");
        }
        return ok;
    }

    private void marcarErro(JComponent field) {
        setEstadoErro(field, true);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                limparErro(field);
                field.removeFocusListener(this);
            }
        });
    }

    private void limparErro(JComponent field) { setEstadoErro(field, false); }

    private void setEstadoErro(JComponent field, boolean erro) {
        if (field instanceof CampoTexto ct) ct.setEstadoErro(erro);
        else if (field instanceof CampoSenha cs) cs.setEstadoErro(erro);
    }

    private void limparTodosErros() {
        for (JComponent f : new JComponent[]{
                txt_NomeOficina, txt_Cnpj, txt_TelefoneOficina, txt_Cep,
                txt_NomeAdmin, txt_CpfAdmin, txt_EmailAdmin, txt_TelefoneAdmin,
                txt_Senha, txt_ConfirmarSenha})
            limparErro(f);
    }

    private void vincularAcoes() {
        btn_Abrir.addActionListener(e -> {
            if (!validarFormulario()) return;

            String nomeOficina = txt_NomeOficina.getText().trim();
            String cnpj = txt_Cnpj.getText().trim();
            String telOficina = txt_TelefoneOficina.getText().trim();
            String endereco = montarEndereco();
            String nomeAdmin = txt_NomeAdmin.getText().trim();
            String cpfAdmin = txt_CpfAdmin.getText().trim();
            String emailAdmin = txt_EmailAdmin.getText().trim();
            String telAdmin = txt_TelefoneAdmin.getText().trim();
            String senha = new String(txt_Senha.getPassword());

            try {
                controller.abrirOficina(nomeOficina, endereco, telOficina, cnpj,
                    nomeAdmin, cpfAdmin, emailAdmin, senha, telAdmin);
                DialogoAlerta.sucesso(this, "Oficina \"" + nomeOficina + "\" aberta com sucesso!\nVocê já está logado.", "Sucesso");
                dispose();
                SwingUtilities.invokeLater(() -> new V_Main(controller).setVisible(true));
            } catch (Exception ex) {
                DialogoAlerta.erro(this, ex.getMessage(), "Não foi possível abrir a oficina");
            }
        });
    }

    private String montarEndereco() {
        String endereco = txt_Endereco.getText().trim();
        String numero = txt_Numero.getText().trim();
        String complemento = txt_Complemento.getText().trim();
        StringBuilder sb = new StringBuilder(endereco);
        if (!numero.isEmpty()) sb.append(", ").append(numero);
        if (!complemento.isEmpty()) sb.append(" - ").append(complemento);
        return sb.toString();
    }

    private void voltarParaLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> new V_Login(controller).setVisible(true));
    }

    // =========================================================================
    // AUXILIARES VISUAIS
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

    private JLabel criarLabelSecao(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(COR_SECAO);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return l;
    }

    private CampoTexto criarCampo() {
        CampoTexto f = new CampoTexto();
        f.setPreferredSize(new Dimension(0, ALTURA_CAMPO));
        return f;
    }

    private CampoSenha criarCampoSenha() {
        CampoSenha f = new CampoSenha();
        f.setPreferredSize(new Dimension(0, ALTURA_CAMPO));
        return f;
    }

    private JButton botaoLink(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(COR_LABEL);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // INNER CLASSES — DocumentFilter
    // =========================================================================
    private static class FiltroLetras extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, filtrar(text), attr);
        }
        @Override public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, offset, length, filtrar(text), attr);
        }
        private String filtrar(String t) { return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ\\s'\\-]", ""); }
    }

    private static abstract class FiltroMascara extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + text + atual.substring(off), attr);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + (text != null ? text : "") + atual.substring(off + len), attr);
        }
        @Override public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            aplicar(fb, atual.substring(0, off) + atual.substring(off + len), null);
        }
        abstract void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException;
    }

    /** Auto-formata CPF como XXX.XXX.XXX-XX. */
    private static class FiltroCpf extends FiltroMascara {
        void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
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

    /** Auto-formata CNPJ como XX.XXX.XXX/XXXX-XX. */
    private static class FiltroCnpj extends FiltroMascara {
        void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
            String d = texto.replaceAll("[^0-9]", "");
            if (d.length() > 14) d = d.substring(0, 14);
            fb.replace(0, fb.getDocument().getLength(), formatar(d), attr);
        }
        private String formatar(String d) {
            int n = d.length();
            if (n <= 2) return d;
            if (n <= 5) return d.substring(0,2) + "." + d.substring(2);
            if (n <= 8) return d.substring(0,2) + "." + d.substring(2,5) + "." + d.substring(5);
            if (n <= 12) return d.substring(0,2) + "." + d.substring(2,5) + "." + d.substring(5,8) + "/" + d.substring(8);
            return d.substring(0,2) + "." + d.substring(2,5) + "." + d.substring(5,8) + "/" + d.substring(8,12) + "-" + d.substring(12);
        }
    }

    /** Auto-formata telefone/celular como (XX) XXXXX-XXXX. */
    private static class FiltroTelefone extends FiltroMascara {
        void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
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

    /** Auto-formata CEP como XXXXX-XXX. */
    private static class FiltroCep extends FiltroMascara {
        void aplicar(FilterBypass fb, String texto, AttributeSet attr) throws BadLocationException {
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

    private static class FiltroDigitos extends DocumentFilter {
        private final int max;
        FiltroDigitos(int max) { this.max = max; }
        @Override public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            int espaco = max - fb.getDocument().getLength();
            if (espaco > 0) super.insertString(fb, off, novo.substring(0, Math.min(novo.length(), espaco)), attr);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            int novoTamanho = fb.getDocument().getLength() - len + novo.length();
            if (novoTamanho <= max) super.replace(fb, off, len, novo, attr);
            else {
                int espaco = max - (fb.getDocument().getLength() - len);
                if (espaco > 0) super.replace(fb, off, len, novo.substring(0, espaco), attr);
            }
        }
    }

    // =========================================================================
    // CAMPOS EM "VIDRO" (glassmorphism) — mesma linguagem visual do restante do app
    // =========================================================================
    private static class CampoTexto extends JTextField {
        private boolean erro = false;
        private boolean focado = false;

        CampoTexto() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        void setEstadoErro(boolean valor) { this.erro = valor; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            pintarFundoVidro(g, getWidth(), getHeight(), erro);
            super.paintComponent(g);
        }

        @Override protected void paintBorder(Graphics g) {
            pintarBordaVidro(g, getWidth(), getHeight(), erro, focado);
        }
    }

    private static class CampoSenha extends JPasswordField {
        private boolean erro = false;
        private boolean focado = false;

        CampoSenha() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        void setEstadoErro(boolean valor) { this.erro = valor; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            pintarFundoVidro(g, getWidth(), getHeight(), erro);
            super.paintComponent(g);
        }

        @Override protected void paintBorder(Graphics g) {
            pintarBordaVidro(g, getWidth(), getHeight(), erro, focado);
        }
    }

    private static void pintarFundoVidro(Graphics g, int w, int h, boolean erro) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_CAMPO, RAIO_CAMPO);
        GradientPaint vidro = new GradientPaint(
                0, 0, new Color(255, 255, 255, erro ? 195 : 175),
                0, h, new Color(255, 255, 255, erro ? 140 : 115));
        g2.setPaint(vidro);
        g2.fill(forma);
        g2.dispose();
    }

    private static void pintarBordaVidro(Graphics g, int w, int h, boolean erro, boolean focado) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color corBorda = erro ? COR_ERRO : (focado ? COR_ACAO : COR_BORDA_PADRAO);
        float espessura = (focado || erro) ? 1.6f : 1f;
        g2.setStroke(new BasicStroke(espessura));
        g2.setColor(corBorda);
        g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 2, RAIO_CAMPO, RAIO_CAMPO));
        g2.dispose();
    }

    /** Painel com fundo em gradiente suave, mesmo estilo de card usado nas outras telas de cadastro. */
    private static class PainelGradiente extends JPanel {
        private final Color corTopo;
        private final Color corBase;

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase) {
            super(layout);
            this.corTopo = corTopo;
            this.corBase = corBase;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, corTopo, 0, getHeight(), corBase);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botão de ação com a mesma linguagem visual (laranja) usada nas demais telas. */
    private static class BotaoAcao extends JButton {
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto) {
            super(texto);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { sobreMouse = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressionado = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
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
}
