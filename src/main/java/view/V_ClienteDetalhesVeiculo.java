package view;

import br.com.oficina.veiculo.DetalhesVeiculoEntity;
import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

/**
 * "Informações do Veículo" (protótipo do Figma) em duas colunas: à esquerda a foto em
 * moldura arredondada (clique para trocar); à direita tipo, marca, modelo, placa/ano/cor
 * em "pílulas" cinza e a caixa "Informações técnicas" com o que a oficina cadastrou.
 */
public class V_ClienteDetalhesVeiculo extends JPanel {

    private static final int FOTO_L = 420, FOTO_A = 280;

    private final OficinaController controller;
    private final Veiculo veiculo;
    private JLabel lblFoto;

    public V_ClienteDetalhesVeiculo(OficinaController controller, Veiculo veiculo) {
        this.controller = controller;
        this.veiculo = veiculo;

        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);

        JPanel colunas = new JPanel(new GridBagLayout());
        colunas.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0; gc.anchor = GridBagConstraints.NORTH; gc.insets = new Insets(0, 0, 0, 36);
        colunas.add(construirFoto(), gc);
        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(0, 0, 0, 0);
        colunas.add(construirDados(), gc);

        add(EstiloCliente.pagina("Informações do Veículo",
                () -> EstiloCliente.navegar(this, new V_ClienteVeiculos(controller)), colunas));
    }

    // ------------------------------------------------------------------ foto

    private JComponent construirFoto() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        // Moldura branca com borda fina arredondada, como no protótipo.
        JPanel moldura = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D r = new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1.5, getHeight() - 1.5, 22, 22);
                g2.setColor(Color.WHITE);
                g2.fill(r);
                g2.setColor(EstiloCliente.LINHA);
                g2.draw(r);
                g2.dispose();
            }
        };
        moldura.setOpaque(false);
        moldura.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        Dimension tam = new Dimension(FOTO_L, FOTO_A);
        moldura.setPreferredSize(tam);
        moldura.setMaximumSize(tam);
        moldura.setCursor(new Cursor(Cursor.HAND_CURSOR));
        moldura.setToolTipText("Clique para trocar a foto");

        lblFoto = new JLabel("", SwingConstants.CENTER);
        atualizarFoto();
        moldura.add(lblFoto);
        MouseAdapter trocar = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { trocarFoto(); }
        };
        moldura.addMouseListener(trocar);
        lblFoto.addMouseListener(trocar);
        moldura.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(moldura);

        JLabel link = new JLabel("Trocar foto");
        link.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        link.setForeground(EstiloCliente.TEXTO_SUAVE);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setAlignmentX(Component.CENTER_ALIGNMENT);
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { trocarFoto(); }
            @Override public void mouseEntered(MouseEvent e) { link.setForeground(EstiloCliente.ACENTO); }
            @Override public void mouseExited(MouseEvent e) { link.setForeground(EstiloCliente.TEXTO_SUAVE); }
        });
        col.add(Box.createVerticalStrut(8));
        col.add(link);
        return col;
    }

    // ------------------------------------------------------------------ dados

    private JComponent construirDados() {
        String marca = veiculo.getModelo() != null && veiculo.getModelo().getMontadora() != null
            ? veiculo.getModelo().getMontadora().getNome() : null;
        String modelo = veiculo.getModelo() != null ? veiculo.getModelo().getNome() : null;
        String tipo = veiculo.getModelo() != null ? veiculo.getModelo().getTipo() : null;
        int ano = veiculo.getModelo() != null ? veiculo.getModelo().getAno() : 0;
        DetalhesVeiculoEntity d = controller.getDetalhesVeiculo(veiculo.getIdVeiculo());

        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        campo(col, "Tipo do Veículo:", EstiloCliente.pilula(tipo));
        campo(col, "Marca", EstiloCliente.pilula(marca));
        campo(col, "Modelo", EstiloCliente.pilula(modelo));

        JPanel linha = new JPanel(new GridLayout(2, 3, 18, 8));
        linha.setOpaque(false);
        linha.add(EstiloCliente.rotuloCampo("Placa"));
        linha.add(EstiloCliente.rotuloCampo("Ano"));
        linha.add(EstiloCliente.rotuloCampo("Cor"));
        linha.add(EstiloCliente.pilula(veiculo.getPlaca()));
        linha.add(EstiloCliente.pilula(ano > 0 ? String.valueOf(ano) : null));
        linha.add(EstiloCliente.pilula(d != null ? d.getCor() : null));
        linha.setAlignmentX(Component.CENTER_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
        col.add(linha);
        col.add(Box.createVerticalStrut(24));

        JLabel tecTitulo = new JLabel("Informações técnicas:", SwingConstants.CENTER);
        tecTitulo.setFont(EstiloCliente.fonte(Font.PLAIN, 15));
        tecTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(tecTitulo);
        col.add(Box.createVerticalStrut(8));

        StringBuilder html = new StringBuilder("<html><div style='text-align:center'>");
        if (d == null || todosVazios(d)) {
            html.append("<i>A oficina ainda não cadastrou as informações técnicas deste veículo.</i>");
        } else {
            linhaTec(html, "Motor", d.getMotor());
            linhaTec(html, "Câmbio", d.getCambio());
            linhaTec(html, "Direção", d.getDirecao());
            linhaTec(html, "Sistema de freios", d.getSistemaFreios());
            linhaTec(html, "Chassi (VIN)", d.getVin());
        }
        html.append("</div></html>");
        JLabel tec = new JLabel(html.toString(), SwingConstants.CENTER);
        tec.setFont(EstiloCliente.fonte(Font.PLAIN, 15));
        tec.setForeground(EstiloCliente.TEXTO);

        EstiloCliente.PainelArredondado caixa = new EstiloCliente.PainelArredondado(
                new BorderLayout(), 14, EstiloCliente.TILE);
        caixa.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        caixa.add(tec);
        caixa.setAlignmentX(Component.CENTER_ALIGNMENT);
        caixa.setMaximumSize(new Dimension(Integer.MAX_VALUE, caixa.getPreferredSize().height));
        col.add(caixa);
        return col;
    }

    private static boolean todosVazios(DetalhesVeiculoEntity d) {
        for (String s : new String[]{d.getMotor(), d.getCambio(), d.getDirecao(), d.getSistemaFreios(), d.getVin()})
            if (s != null && !s.isBlank()) return false;
        return true;
    }

    private static void linhaTec(StringBuilder html, String rotulo, String valor) {
        if (valor == null || valor.isBlank()) return;
        html.append(rotulo).append(": ").append(EstiloCliente.escapar(valor)).append("<br>");
    }

    private void campo(JPanel col, String rotulo, JComponent valor) {
        JLabel l = EstiloCliente.rotuloCampo(rotulo);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        valor.setAlignmentX(Component.CENTER_ALIGNMENT);
        valor.setMaximumSize(new Dimension(Integer.MAX_VALUE, valor.getPreferredSize().height));
        col.add(l);
        col.add(Box.createVerticalStrut(6));
        col.add(valor);
        col.add(Box.createVerticalStrut(14));
    }

    // ------------------------------------------------------------------ foto: ações

    private void atualizarFoto() {
        ImageIcon icon = ImagemUtil.carregar(veiculo.getFoto(), FOTO_L - 24, FOTO_A - 24);
        if (icon != null) {
            lblFoto.setIcon(icon);
            lblFoto.setText(null);
        } else {
            lblFoto.setIcon(EstiloCliente.recurso("/assets/icons/vveiculos.png", 150, 150));
            lblFoto.setText(null);
        }
    }

    private void trocarFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Imagens (jpg, png, gif, bmp)", "jpg", "jpeg", "png", "gif", "bmp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

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
}
