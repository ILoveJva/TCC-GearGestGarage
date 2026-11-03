package view;

import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Grade com os veículos do cliente logado; clique em um cartão abre o detalhe. */
public class V_ClienteVeiculos extends JPanel {

    private final OficinaController controller;

    public V_ClienteVeiculos(OficinaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Seus veículos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topo.add(titulo, BorderLayout.WEST);

        JButton btnNovo = new JButton("+ Adicionar veículo");
        btnNovo.addActionListener(e -> navegar(new V_ClienteCadastrarVeiculo(this.controller)));
        topo.add(btnNovo, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);

        List<Veiculo> veiculos = controller.listarMeusVeiculos();

        if (veiculos.isEmpty()) {
            JLabel vazio = new JLabel("Você ainda não tem veículos cadastrados.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vazio.setHorizontalAlignment(SwingConstants.CENTER);
            add(vazio, BorderLayout.CENTER);
            return;
        }

        JPanel grade = new JPanel(new GridLayout(0, 3, 16, 16));
        grade.setBackground(Color.WHITE);
        for (Veiculo v : veiculos) grade.add(criarCartao(v));

        JScrollPane scroll = new JScrollPane(grade);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel criarCartao(Veiculo v) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.decode("#F7F7F7"));
        card.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC")));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(220, 180));

        JLabel foto = new JLabel();
        foto.setHorizontalAlignment(SwingConstants.CENTER);
        foto.setPreferredSize(new Dimension(220, 110));
        ImageIcon icon = ImagemUtil.carregar(v.getFoto(), 200, 100);
        if (icon != null) foto.setIcon(icon);
        else foto.setText("(sem foto)");

        String marca = v.getModelo() != null && v.getModelo().getMontadora() != null
            ? v.getModelo().getMontadora().getNome() : "";
        String modelo = v.getModelo() != null ? v.getModelo().getNome() : "";
        int ano = v.getModelo() != null ? v.getModelo().getAno() : 0;

        JLabel nome = new JLabel("<html><div style='text-align:center;'>" + marca + " " + modelo
            + " " + (ano > 0 ? ano : "") + "<br>" + v.getPlaca() + "</div></html>");
        nome.setHorizontalAlignment(SwingConstants.CENTER);
        nome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nome.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        card.add(foto, BorderLayout.CENTER);
        card.add(nome, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                navegar(new V_ClienteDetalhesVeiculo(controller, v));
            }
        });
        return card;
    }

    private void navegar(JPanel pagina) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela instanceof V_ClienteMain) ((V_ClienteMain) janela).atualizarConteudo(pagina);
    }
}
