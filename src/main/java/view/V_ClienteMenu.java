package view;

import controller.OficinaController;

import javax.swing.*;
import java.awt.*;

/** Tela inicial do Portal do Cliente: grade de atalhos para as demais telas. */
public class V_ClienteMenu extends JPanel {

    private final OficinaController controller;

    public V_ClienteMenu(OficinaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("O que você precisa?");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        JPanel grade = new JPanel(new GridLayout(2, 3, 16, 16));
        grade.setBackground(Color.WHITE);
        grade.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        grade.add(criarTile("Adicionar novo Veículo", () -> navegar(new V_ClienteCadastrarVeiculo(this.controller))));
        grade.add(criarTile("Consultar Veículos", () -> navegar(new V_ClienteVeiculos(this.controller))));
        grade.add(criarTile("Orçamentos", () -> navegar(new V_ClienteOrcamentos(this.controller))));
        grade.add(criarTile("Serviços", () -> navegar(new V_ClienteServicos(this.controller))));
        grade.add(criarTile("Buscar oficinas", () -> navegar(new V_ClienteBuscarOficinas(this.controller))));
        grade.add(criarTile("Meu Perfil", () -> navegar(new V_ClientePerfil(this.controller))));

        add(grade, BorderLayout.CENTER);
    }

    private JButton criarTile(String texto, Runnable acao) {
        JButton btn = new JButton("<html><div style='text-align:center;'>" + texto + "</div></html>");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBackground(Color.decode("#F2F2F2"));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> acao.run());
        return btn;
    }

    private void navegar(JPanel pagina) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela instanceof V_ClienteMain) ((V_ClienteMain) janela).atualizarConteudo(pagina);
    }
}
