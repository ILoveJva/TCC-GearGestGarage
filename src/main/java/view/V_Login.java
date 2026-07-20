package view;

import controller.OficinaController;
import model.Cliente;
import model.Oficina;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class V_Login extends JFrame {

    private OficinaController controller;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnEntrar;

    public V_Login(OficinaController controller) {
        this.controller = controller;

        setTitle("Gear Gest Garage - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        // Painel Principal com fundo cinza escuro/médio (#7D7876)
        JPanel pnlPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.decode("#7D7876"));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Desenho das engrenagens decorativas
                g2d.setColor(Color.decode("#66615F"));
                g2d.fillOval(getWidth() - 80, -80, 160, 160);
                g2d.fillOval(-60, getHeight() - 100, 160, 160);

                g2d.dispose();
            }
        };
        pnlPrincipal.setLayout(null);
        setContentPane(pnlPrincipal);

        // --- Imagem da Logo ---
        JLabel lblLogoImagem = new JLabel();
        lblLogoImagem.setBounds(225, 20, 225, 225);
        lblLogoImagem.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon iconeOriginal = new ImageIcon(getClass().getResource("/assets/images/logo.png"));
            Image imagemRedimensionada = iconeOriginal.getImage().getScaledInstance(225, 225, Image.SCALE_SMOOTH);
            lblLogoImagem.setIcon(new ImageIcon(imagemRedimensionada));
        } catch (Exception e) {
            lblLogoImagem.setText("GEAR GEST");
            lblLogoImagem.setFont(new Font("Segoe UI", Font.BOLD, 36));
            lblLogoImagem.setForeground(Color.BLACK);
        }
        pnlPrincipal.add(lblLogoImagem);

        // --- Formulário de Login ---
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogin.setForeground(Color.WHITE);
        lblLogin.setBounds(200, 240, 300, 25);
        pnlPrincipal.add(lblLogin);

        // Campo de Email (Correção no Paint)
        txtEmail = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.decode("#D2D0CF"));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2d.dispose();
                super.paintComponent(g); // Chamado DEPOIS do fundo customizado
            }
        };
        txtEmail.setOpaque(false);
        txtEmail.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setForeground(Color.DARK_GRAY);
        txtEmail.setCaretColor(Color.DARK_GRAY); // Garante visibilidade do cursor
        txtEmail.setBounds(200, 275, 300, 35);
        txtEmail.setText("oficina@geargest.com");
        pnlPrincipal.add(txtEmail);

        // Campo de Senha (Correção no Paint)
        txtSenha = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.decode("#D2D0CF"));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2d.dispose();
                super.paintComponent(g); // Chamado DEPOIS do fundo customizado
            }
        };
        txtSenha.setOpaque(false);
        txtSenha.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSenha.setForeground(Color.DARK_GRAY);
        txtSenha.setCaretColor(Color.DARK_GRAY);
        txtSenha.setBounds(200, 320, 300, 35);
        txtSenha.setText("123456");
        pnlPrincipal.add(txtSenha);

        // Link de Cadastro (Adicionado Evento de Clique)
        JLabel lblCadastro = new JLabel("<html>Não tem uma conta? <font color='#3B82F6'><u>Clique aqui</u></font></html>");
        lblCadastro.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCadastro.setForeground(Color.WHITE);
        lblCadastro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblCadastro.setBounds(200, 362, 200, 18);
        lblCadastro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                irParaCadastro();
            }
        });
        pnlPrincipal.add(lblCadastro);

        // Botão Entrar (Correção no Paint)
        btnEntrar = new JButton("Entrar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2d.dispose();
                super.paintComponent(g); // Chamado DEPOIS do fundo customizado
            }
        };
        btnEntrar.setOpaque(false);
        btnEntrar.setContentAreaFilled(false);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEntrar.setBounds(200, 395, 300, 42);

        // Ação de Autenticação
        btnEntrar.addActionListener(e -> efetuarLogin());
        pnlPrincipal.add(btnEntrar);
    }

    private void efetuarLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha e-mail e senha.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean autenticado = controller.autenticar(email, senha);

        if (autenticado) {
            JOptionPane.showMessageDialog(this, "Login efetuado com sucesso!", "Bem-vindo", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            SwingUtilities.invokeLater(() -> new V_Main(controller).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this, "E-mail ou senha incorretos.", "Erro de Autenticacao", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void irParaCadastro() {
        // Método para abrir a tela de cadastro
        this.dispose();
        // Descomente e altere para o nome da sua View de Cadastro:
        // SwingUtilities.invokeLater(() -> new V_Cadastro(controller).setVisible(true));
    }
}