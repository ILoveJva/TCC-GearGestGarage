package view;

import controller.OficinaController;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal do Portal do Cliente — equivalente a {@link V_Main}, mas com
 * uma barra superior fixa (em vez da sidebar do lado da oficina) e navegação
 * centrada na tela inicial de tiles ({@link V_ClienteMenu}).
 */
public class V_ClienteMain extends JFrame {

    private final OficinaController controller;
    private JPanel pnl_ConteudoDinamico;
    private JLabel lbl_OlaNome;

    public V_ClienteMain(OficinaController controller) {
        this.controller = controller;

        setTitle("Gear Gest Garage - Portal do Cliente");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel pnl_Main = new JPanel(new BorderLayout());
        pnl_Main.add(construirBarraSuperior(), BorderLayout.NORTH);

        pnl_ConteudoDinamico = new JPanel(new BorderLayout());
        pnl_ConteudoDinamico.setBackground(Color.WHITE);
        pnl_Main.add(pnl_ConteudoDinamico, BorderLayout.CENTER);

        add(pnl_Main);

        atualizarConteudo(new V_ClienteMenu(this.controller));
    }

    private JPanel construirBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.decode("#6E6968"));
        barra.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel lblLogo = new JLabel("GEAR GEST GARAGE");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                atualizarConteudo(new V_ClienteMenu(V_ClienteMain.this.controller));
            }
        });

        model.Cliente meu = this.controller.getMeuCliente();
        lbl_OlaNome = new JLabel("Olá, " + (meu != null ? meu.getNome() : ""));
        lbl_OlaNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl_OlaNome.setForeground(Color.WHITE);

        JButton btnPerfil = new JButton("Meu Perfil");
        btnPerfil.addActionListener(e -> atualizarConteudo(new V_ClientePerfil(this.controller)));

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new V_Login(this.controller).setVisible(true));
        });

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setOpaque(false);
        direita.add(lbl_OlaNome);
        direita.add(btnPerfil);
        direita.add(btnSair);

        barra.add(lblLogo, BorderLayout.WEST);
        barra.add(direita, BorderLayout.EAST);
        return barra;
    }

    public void atualizarConteudo(JPanel pnl_NovaPagina) {
        pnl_ConteudoDinamico.removeAll();
        pnl_ConteudoDinamico.add(pnl_NovaPagina, BorderLayout.CENTER);
        pnl_ConteudoDinamico.revalidate();
        pnl_ConteudoDinamico.repaint();
    }
}
