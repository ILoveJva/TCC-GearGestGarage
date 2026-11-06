package view;

import controller.OficinaController;
import model.Cliente;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

/**
 * Perfil do cliente (protótipo do Figma adaptado para desktop): à esquerda o avatar
 * circular, o nome com o lápis de edição e os tiles "Editar informações" / "Editar
 * Veículos"; à direita a grade "Seus veículos" com foto e nome de cada carro.
 */
public class V_ClientePerfil extends JPanel {

    private static final int TAM_AVATAR = 200;

    private final OficinaController controller;
    private JLabel lblAvatar;
    private JLabel lblNome;

    public V_ClientePerfil(OficinaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);

        JPanel colunas = new JPanel(new BorderLayout());
        colunas.setBackground(EstiloCliente.FUNDO);
        colunas.add(construirColunaPerfil(), BorderLayout.WEST);
        colunas.add(construirColunaVeiculos(), BorderLayout.CENTER);

        add(colunas, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------ coluna esquerda

    private JComponent construirColunaPerfil() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(EstiloCliente.FUNDO);
        col.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, EstiloCliente.LINHA),
                BorderFactory.createEmptyBorder(36, 36, 36, 36)));
        col.setPreferredSize(new Dimension(440, 10));

        lblAvatar = new JLabel();
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAvatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAvatar.setToolTipText("Clique para trocar a foto");
        lblAvatar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { trocarFoto(); }
        });
        atualizarAvatar();
        col.add(lblAvatar);

        JLabel trocar = new JLabel("Trocar foto");
        trocar.setFont(EstiloCliente.fonte(Font.PLAIN, 12));
        trocar.setForeground(EstiloCliente.TEXTO_SUAVE);
        trocar.setAlignmentX(Component.CENTER_ALIGNMENT);
        trocar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        trocar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { trocarFoto(); }
            @Override public void mouseEntered(MouseEvent e) { trocar.setForeground(EstiloCliente.ACENTO); }
            @Override public void mouseExited(MouseEvent e) { trocar.setForeground(EstiloCliente.TEXTO_SUAVE); }
        });
        col.add(Box.createVerticalStrut(6));
        col.add(trocar);
        col.add(Box.createVerticalStrut(18));

        // Nome grande com o lápis ao lado, como no protótipo.
        Cliente meu = controller.getMeuCliente();
        lblNome = new JLabel(meu != null ? meu.getNome() : "");
        lblNome.setFont(EstiloCliente.fonte(Font.PLAIN, 30));
        lblNome.setForeground(EstiloCliente.TEXTO);

        JLabel lapis = new JLabel(EstiloCliente.iconeLapis(22));
        lapis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lapis.setToolTipText("Editar informações");
        lapis.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { editarInformacoes(); }
        });

        JPanel linhaNome = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        linhaNome.setOpaque(false);
        linhaNome.add(lblNome);
        linhaNome.add(lapis);
        linhaNome.setAlignmentX(Component.CENTER_ALIGNMENT);
        linhaNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, linhaNome.getPreferredSize().height));
        col.add(linhaNome);

        if (meu != null && meu.getEmail() != null && !meu.getEmail().isBlank()) {
            JLabel email = new JLabel(meu.getEmail());
            email.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
            email.setForeground(EstiloCliente.TEXTO_SUAVE);
            email.setAlignmentX(Component.CENTER_ALIGNMENT);
            col.add(Box.createVerticalStrut(4));
            col.add(email);
        }
        col.add(Box.createVerticalStrut(26));

        JPanel tiles = new JPanel(new GridLayout(1, 2, 16, 0));
        tiles.setOpaque(false);
        tiles.add(EstiloCliente.tile("Editar informações", "/assets/icons/config.png",
                null, this::editarInformacoes));
        tiles.add(EstiloCliente.tile("Editar Veículos", "/assets/icons/vveiculos.png",
                EstiloCliente.iconeLapis(16), () -> EstiloCliente.navegar(this, new V_ClienteVeiculos(controller))));
        tiles.setAlignmentX(Component.CENTER_ALIGNMENT);
        tiles.setMaximumSize(new Dimension(360, 170));
        tiles.setPreferredSize(new Dimension(360, 170));
        col.add(tiles);
        col.add(Box.createVerticalGlue());
        return col;
    }

    // ------------------------------------------------------------------ coluna direita

    private JComponent construirColunaVeiculos() {
        JPanel col = new JPanel(new BorderLayout(0, 18));
        col.setBackground(EstiloCliente.FUNDO);
        col.setBorder(BorderFactory.createEmptyBorder(30, 32, 24, 32));

        col.add(EstiloCliente.tituloSecao("Seus veículos"), BorderLayout.NORTH);

        List<Veiculo> veiculos = controller.listarMeusVeiculos();
        JPanel grade = new JPanel(new GridLayout(0, 3, 18, 18));
        grade.setOpaque(false);
        if (veiculos.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum veículo cadastrado.", SwingConstants.CENTER);
            vazio.setFont(EstiloCliente.fonte(Font.ITALIC, 14));
            vazio.setForeground(EstiloCliente.TEXTO_SUAVE);
            col.add(vazio, BorderLayout.CENTER);
            return col;
        }
        for (Veiculo v : veiculos) grade.add(cartaoVeiculo(v));

        // NORTH impede que o GridLayout estique os cartões até o fim da tela.
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(grade, BorderLayout.NORTH);
        col.add(EstiloCliente.rolagem(topo), BorderLayout.CENTER);
        return col;
    }

    private JComponent cartaoVeiculo(Veiculo v) {
        EstiloCliente.PainelArredondado card = new EstiloCliente.PainelArredondado(
                new BorderLayout(0, 10), 12, EstiloCliente.TILE);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setToolTipText("Ver detalhes do veículo");

        // Área branca arredondada para a foto, como no protótipo.
        EstiloCliente.PainelArredondado moldura = new EstiloCliente.PainelArredondado(
                new BorderLayout(), 10, Color.WHITE);
        moldura.setPreferredSize(new Dimension(200, 130));
        JLabel foto = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = ImagemUtil.carregar(v.getFoto(), 190, 120);
        if (icon != null) {
            foto.setIcon(icon);
        } else {
            foto.setIcon(EstiloCliente.recurso("/assets/icons/vveiculos.png", 90, 90));
        }
        moldura.add(foto, BorderLayout.CENTER);

        JLabel nome = new JLabel("<html><div style='text-align:center'>" + nomeVeiculo(v) + "</div></html>",
                SwingConstants.CENTER);
        nome.setFont(EstiloCliente.fonte(Font.PLAIN, 14));
        nome.setForeground(EstiloCliente.TEXTO);

        card.add(moldura, BorderLayout.CENTER);
        card.add(nome, BorderLayout.SOUTH);

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                EstiloCliente.navegar(V_ClientePerfil.this, new V_ClienteDetalhesVeiculo(controller, v));
            }
            @Override public void mouseEntered(MouseEvent e) { card.setCor(EstiloCliente.TILE_HOVER); }
            @Override public void mouseExited(MouseEvent e) {
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), card);
                if (!card.contains(pt)) card.setCor(EstiloCliente.TILE);
            }
        };
        for (Component c : new Component[]{card, moldura, foto, nome}) c.addMouseListener(ma);
        return card;
    }

    private static String nomeVeiculo(Veiculo v) {
        StringBuilder sb = new StringBuilder();
        if (v.getModelo() != null) {
            if (v.getModelo().getMontadora() != null) sb.append(v.getModelo().getMontadora().getNome()).append(' ');
            sb.append(v.getModelo().getNome());
            if (v.getModelo().getAno() > 0) sb.append(' ').append(v.getModelo().getAno());
        }
        String linha1 = sb.toString().trim().replace("&", "&amp;").replace("<", "&lt;");
        String placa = v.getPlaca() == null ? "" : v.getPlaca();
        return (linha1.isEmpty() ? "" : linha1 + "<br>")
                + "<span style='color:#6B6B6B;font-size:11px'>" + placa + "</span>";
    }

    // ------------------------------------------------------------------ ações

    private void atualizarAvatar() {
        Cliente meu = controller.getMeuCliente();
        lblAvatar.setIcon(EstiloCliente.avatar(controller.getFotoPerfil(),
                meu != null ? meu.getNome() : "", TAM_AVATAR));
    }

    private void atualizarCabecalhoJanela() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_ClienteMain) ((V_ClienteMain) w).atualizarCabecalho();
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
            atualizarCabecalhoJanela();
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
            atualizarAvatar();
            atualizarCabecalhoJanela();
            DialogoAlerta.sucesso(this, "Informações atualizadas com sucesso!", "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao atualizar: " + ex.getMessage(), "Erro");
        }
    }
}
