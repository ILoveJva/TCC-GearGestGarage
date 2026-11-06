package view;

import controller.OficinaController;
import model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Janela principal do Portal do Cliente — equivalente a {@link V_Main}, mas com
 * uma barra superior fixa (em vez da sidebar do lado da oficina) e navegação
 * centrada na tela inicial de tiles ({@link V_ClienteMenu}).
 *
 * Cabeçalho segue o protótipo do Figma: logo "Gear Gest GARAGE" à esquerda,
 * saudação + avatar circular à direita e uma linha fina separando o conteúdo.
 */
public class V_ClienteMain extends JFrame {

    private static final int ALTURA_LOGO = 46;
    private static final int TAM_AVATAR = 48;

    private final OficinaController controller;
    private JPanel pnl_ConteudoDinamico;
    private JLabel lbl_OlaNome;
    private JLabel lbl_Avatar;

    public V_ClienteMain(OficinaController controller) {
        this.controller = controller;

        setTitle("Gear Gest Garage - Portal do Cliente");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel pnl_Main = new JPanel(new BorderLayout());
        pnl_Main.setBackground(EstiloCliente.FUNDO);
        pnl_Main.add(construirBarraSuperior(), BorderLayout.NORTH);

        pnl_ConteudoDinamico = new JPanel(new BorderLayout());
        pnl_ConteudoDinamico.setBackground(EstiloCliente.FUNDO);
        pnl_Main.add(pnl_ConteudoDinamico, BorderLayout.CENTER);

        add(pnl_Main);

        atualizarConteudo(new V_ClienteMenu(this.controller));
    }

    private JPanel construirBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(EstiloCliente.FUNDO);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, EstiloCliente.LINHA),
                BorderFactory.createEmptyBorder(10, 28, 10, 28)));

        JLabel lblLogo = EstiloCliente.logo(ALTURA_LOGO);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.setToolTipText("Página inicial");
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                atualizarConteudo(new V_ClienteMenu(V_ClienteMain.this.controller));
            }
        });

        // Saudação: "Olá," em peso normal + nome em negrito, como no protótipo.
        lbl_OlaNome = new JLabel();
        lbl_OlaNome.setFont(EstiloCliente.fonte(Font.PLAIN, 15));
        lbl_OlaNome.setForeground(EstiloCliente.TEXTO);

        lbl_Avatar = new JLabel();
        lbl_Avatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl_Avatar.setToolTipText("Meu perfil");

        JPopupMenu menu = construirMenuUsuario();
        MouseAdapter abrirMenu = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                menu.show(lbl_Avatar, lbl_Avatar.getWidth() - menu.getPreferredSize().width,
                        lbl_Avatar.getHeight() + 4);
            }
        };
        lbl_Avatar.addMouseListener(abrirMenu);
        lbl_OlaNome.addMouseListener(abrirMenu);
        lbl_OlaNome.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        direita.setOpaque(false);
        direita.add(lbl_OlaNome);
        direita.add(lbl_Avatar);

        JPanel centroVert = new JPanel(new GridBagLayout());
        centroVert.setOpaque(false);
        centroVert.add(direita);

        barra.add(lblLogo, BorderLayout.WEST);
        barra.add(centroVert, BorderLayout.EAST);

        atualizarCabecalho();
        return barra;
    }

    private JPopupMenu construirMenuUsuario() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC")));
        menu.setBackground(Color.WHITE);

        JMenuItem itemPerfil = itemMenu("Meu perfil");
        itemPerfil.addActionListener(e -> atualizarConteudo(new V_ClientePerfil(controller)));
        JMenuItem itemInicio = itemMenu("Página inicial");
        itemInicio.addActionListener(e -> atualizarConteudo(new V_ClienteMenu(controller)));
        JMenuItem itemSair = itemMenu("Sair");
        itemSair.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new V_Login(this.controller).setVisible(true));
        });

        menu.add(itemInicio);
        menu.add(itemPerfil);
        menu.addSeparator();
        menu.add(itemSair);
        return menu;
    }

    private JMenuItem itemMenu(String texto) {
        JMenuItem it = new JMenuItem(texto);
        it.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        it.setBackground(Color.WHITE);
        it.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 24));
        return it;
    }

    /** Recarrega nome e foto do cabeçalho (chamado após editar o perfil ou trocar a foto). */
    public void atualizarCabecalho() {
        Cliente meu = controller.getMeuCliente();
        String nome = meu != null ? meu.getNome() : "";
        lbl_OlaNome.setText("<html>Olá, <b>" + escapar(nome) + "</b></html>");
        lbl_Avatar.setIcon(EstiloCliente.avatar(controller.getFotoPerfil(), nome, TAM_AVATAR));
    }

    public void atualizarConteudo(JPanel pnl_NovaPagina) {
        pnl_ConteudoDinamico.removeAll();
        pnl_ConteudoDinamico.add(pnl_NovaPagina, BorderLayout.CENTER);
        pnl_ConteudoDinamico.revalidate();
        pnl_ConteudoDinamico.repaint();
    }

    private static String escapar(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
