package view;

import controller.OficinaController;
import model.Cliente;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/** Perfil do cliente: avatar, dados pessoais e atalho para os veículos. */
public class V_ClientePerfil extends JPanel {

    private final OficinaController controller;
    private JLabel lblAvatar;
    private JLabel lblNome;

    public V_ClientePerfil(OficinaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBackground(Color.WHITE);

        lblAvatar = new JLabel();
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(120, 120));
        lblAvatar.setMaximumSize(new Dimension(120, 120));
        atualizarAvatar();
        corpo.add(lblAvatar);

        JButton btnTrocarFoto = new JButton("Trocar foto");
        btnTrocarFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTrocarFoto.addActionListener(e -> trocarFoto());
        corpo.add(Box.createVerticalStrut(8));
        corpo.add(btnTrocarFoto);
        corpo.add(Box.createVerticalStrut(16));

        Cliente meu = controller.getMeuCliente();
        lblNome = new JLabel(meu != null ? meu.getNome() : "");
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);
        corpo.add(lblNome);
        corpo.add(Box.createVerticalStrut(20));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botoes.setOpaque(false);

        JButton btnEditar = new JButton("Editar informações");
        btnEditar.addActionListener(e -> editarInformacoes());
        botoes.add(btnEditar);

        JButton btnVeiculos = new JButton("Editar veículos");
        btnVeiculos.addActionListener(e -> navegar(new V_ClienteVeiculos(this.controller)));
        botoes.add(btnVeiculos);

        botoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        corpo.add(botoes);
        corpo.add(Box.createVerticalStrut(24));

        JLabel tituloVeiculos = new JLabel("Seus veículos");
        tituloVeiculos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloVeiculos.setAlignmentX(Component.CENTER_ALIGNMENT);
        corpo.add(tituloVeiculos);
        corpo.add(Box.createVerticalStrut(10));

        List<Veiculo> veiculos = controller.listarMeusVeiculos();
        if (veiculos.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum veículo cadastrado.");
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            corpo.add(vazio);
        } else {
            for (Veiculo v : veiculos) {
                String marca = v.getModelo() != null && v.getModelo().getMontadora() != null
                    ? v.getModelo().getMontadora().getNome() : "";
                String modelo = v.getModelo() != null ? v.getModelo().getNome() : "";
                JLabel linha = new JLabel(marca + " " + modelo + " — " + v.getPlaca());
                linha.setAlignmentX(Component.CENTER_ALIGNMENT);
                corpo.add(linha);
            }
        }

        add(new JScrollPane(corpo), BorderLayout.CENTER);
    }

    private void atualizarAvatar() {
        ImageIcon icon = ImagemUtil.carregar(controller.getFotoPerfil(), 110, 110);
        if (icon != null) {
            lblAvatar.setIcon(icon);
            lblAvatar.setText(null);
        } else {
            lblAvatar.setIcon(null);
            lblAvatar.setText("(sem foto)");
        }
    }

    private void trocarFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Imagens (jpg, png, gif, bmp)", "jpg", "jpeg", "png", "gif", "bmp"));
        int resultado = chooser.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        File arquivo = chooser.getSelectedFile();
        try {
            controller.salvarFotoPerfil(arquivo);
            atualizarAvatar();
            DialogoAlerta.sucesso(this, "Foto atualizada com sucesso!", "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao salvar foto: " + ex.getMessage(), "Erro");
        }
    }

    private void editarInformacoes() {
        Cliente meu = controller.getMeuCliente();
        if (meu == null) return;

        JTextField txtNome = new JTextField(meu.getNome());
        JTextField txtTelefone = new JTextField(meu.getTelefone());
        JTextField txtEmail = new JTextField(meu.getEmail());

        JPanel painel = new JPanel(new GridLayout(0, 1, 4, 4));
        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);
        painel.add(new JLabel("Telefone:"));
        painel.add(txtTelefone);
        painel.add(new JLabel("E-mail:"));
        painel.add(txtEmail);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Editar informações",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado != JOptionPane.OK_OPTION) return;

        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String email = txtEmail.getText().trim();
        if (nome.isEmpty() || email.isEmpty()) {
            DialogoAlerta.aviso(this, "Nome e e-mail são obrigatórios.", "Dados inválidos");
            return;
        }

        try {
            controller.atualizarMeuPerfil(nome, telefone, email);
            lblNome.setText(nome);
            DialogoAlerta.sucesso(this, "Informações atualizadas com sucesso!", "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao atualizar: " + ex.getMessage(), "Erro");
        }
    }

    private void navegar(JPanel pagina) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela instanceof V_ClienteMain) ((V_ClienteMain) janela).atualizarConteudo(pagina);
    }
}
