package view;

import br.com.oficina.veiculo.DetalhesVeiculoEntity;
import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/** Informações do veículo (especificações reais do cadastro) + foto. */
public class V_ClienteDetalhesVeiculo extends JPanel {

    private final OficinaController controller;
    private final Veiculo veiculo;
    private JLabel lblFoto;

    public V_ClienteDetalhesVeiculo(OficinaController controller, Veiculo veiculo) {
        this.controller = controller;
        this.veiculo = veiculo;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.addActionListener(e -> navegar(new V_ClienteVeiculos(this.controller)));
        add(btnVoltar, BorderLayout.NORTH);

        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBackground(Color.WHITE);
        corpo.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JLabel titulo = new JLabel("Informações do Veículo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        corpo.add(titulo);
        corpo.add(Box.createVerticalStrut(16));

        lblFoto = new JLabel();
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblFoto.setPreferredSize(new Dimension(260, 160));
        lblFoto.setMaximumSize(new Dimension(260, 160));
        atualizarFoto();
        corpo.add(lblFoto);

        JButton btnTrocarFoto = new JButton("Trocar foto");
        btnTrocarFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTrocarFoto.addActionListener(e -> trocarFoto());
        corpo.add(Box.createVerticalStrut(8));
        corpo.add(btnTrocarFoto);
        corpo.add(Box.createVerticalStrut(20));

        String marca = veiculo.getModelo() != null && veiculo.getModelo().getMontadora() != null
            ? veiculo.getModelo().getMontadora().getNome() : "—";
        String modelo = veiculo.getModelo() != null ? veiculo.getModelo().getNome() : "—";
        String tipo = veiculo.getModelo() != null ? veiculo.getModelo().getTipo() : "—";
        int ano = veiculo.getModelo() != null ? veiculo.getModelo().getAno() : 0;

        corpo.add(linhaInfo("Placa", veiculo.getPlaca()));
        corpo.add(linhaInfo("Tipo", tipo));
        corpo.add(linhaInfo("Marca", marca));
        corpo.add(linhaInfo("Modelo", modelo));
        corpo.add(linhaInfo("Ano", ano > 0 ? String.valueOf(ano) : "—"));

        DetalhesVeiculoEntity d = this.controller.getDetalhesVeiculo(veiculo.getIdVeiculo());
        corpo.add(Box.createVerticalStrut(12));
        JLabel tecTitulo = new JLabel("Informações técnicas");
        tecTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tecTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        corpo.add(tecTitulo);
        corpo.add(Box.createVerticalStrut(8));
        if (d != null) {
            corpo.add(linhaInfo("Motor", d.getMotor()));
            corpo.add(linhaInfo("Câmbio", d.getCambio()));
            corpo.add(linhaInfo("Direção", d.getDirecao()));
            corpo.add(linhaInfo("Freios", d.getSistemaFreios()));
            corpo.add(linhaInfo("Cor", d.getCor()));
            corpo.add(linhaInfo("VIN", d.getVin()));
        } else {
            JLabel semDados = new JLabel("Sem informações técnicas cadastradas.");
            semDados.setAlignmentX(Component.CENTER_ALIGNMENT);
            corpo.add(semDados);
        }

        add(new JScrollPane(corpo), BorderLayout.CENTER);
    }

    private JPanel linhaInfo(String rotulo, String valor) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        linha.setBackground(Color.WHITE);
        JLabel lblRotulo = new JLabel(rotulo + ":");
        lblRotulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblValor = new JLabel(valor == null || valor.isBlank() ? "—" : valor);
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        linha.add(lblRotulo);
        linha.add(lblValor);
        linha.setAlignmentX(Component.CENTER_ALIGNMENT);
        return linha;
    }

    private void atualizarFoto() {
        ImageIcon icon = ImagemUtil.carregar(veiculo.getFoto(), 240, 150);
        if (icon != null) {
            lblFoto.setIcon(icon);
            lblFoto.setText(null);
        } else {
            lblFoto.setIcon(null);
            lblFoto.setText("(sem foto)");
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
            controller.salvarFotoVeiculo(veiculo.getIdVeiculo(), arquivo);
            veiculo.setFoto(arquivo.getAbsolutePath());
            atualizarFoto();
            DialogoAlerta.sucesso(this, "Foto atualizada com sucesso!", "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao salvar foto: " + ex.getMessage(), "Erro");
        }
    }

    private void navegar(JPanel pagina) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela instanceof V_ClienteMain) ((V_ClienteMain) janela).atualizarConteudo(pagina);
    }
}
