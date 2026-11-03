package view;

import controller.OficinaController;
import model.Oficina;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Busca oficinas (qualquer oficina do sistema) por nome/endereço; cada resultado pode solicitar revisão. */
public class V_ClienteBuscarOficinas extends JPanel {

    private final OficinaController controller;
    private JTextField txtBusca;
    private JPanel pnlResultados;

    public V_ClienteBuscarOficinas(OficinaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel topo = new JPanel(new BorderLayout(0, 10));
        topo.setOpaque(false);

        JLabel titulo = new JLabel("Buscar oficinas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topo.add(titulo, BorderLayout.NORTH);

        JPanel pnlBusca = new JPanel(new BorderLayout(8, 0));
        pnlBusca.setOpaque(false);
        txtBusca = new JTextField();
        txtBusca.addActionListener(e -> atualizarResultados());
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> atualizarResultados());
        pnlBusca.add(txtBusca, BorderLayout.CENTER);
        pnlBusca.add(btnBuscar, BorderLayout.EAST);
        topo.add(pnlBusca, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);

        pnlResultados = new JPanel();
        pnlResultados.setLayout(new BoxLayout(pnlResultados, BoxLayout.Y_AXIS));
        pnlResultados.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(pnlResultados);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        atualizarResultados();
    }

    private void atualizarResultados() {
        pnlResultados.removeAll();
        List<Oficina> oficinas = controller.buscarOficinas(txtBusca.getText());

        if (oficinas.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma oficina encontrada.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pnlResultados.add(vazio);
        } else {
            for (Oficina o : oficinas) {
                pnlResultados.add(criarCartao(o));
                pnlResultados.add(Box.createVerticalStrut(10));
            }
        }
        pnlResultados.revalidate();
        pnlResultados.repaint();
    }

    private JPanel criarCartao(Oficina o) {
        JPanel card = new JPanel(new BorderLayout(10, 4));
        card.setBackground(Color.decode("#F7F7F7"));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        String endereco = o.getEndereco() == null || o.getEndereco().isBlank() ? "—" : o.getEndereco();
        String telefone = o.getTelefone() == null || o.getTelefone().isBlank() ? "—" : o.getTelefone();
        JLabel info = new JLabel("<html><b>" + o.getNome() + "</b><br>" + endereco + "<br>" + telefone + "</html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(info, BorderLayout.CENTER);

        JButton btnSolicitar = new JButton("Solicitar Revisão");
        btnSolicitar.addActionListener(e -> navegar(new V_ClienteSolicitarRevisao(controller, o)));
        card.add(btnSolicitar, BorderLayout.EAST);

        return card;
    }

    private void navegar(JPanel pagina) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela instanceof V_ClienteMain) ((V_ClienteMain) janela).atualizarConteudo(pagina);
    }
}
