package view;

import controller.OficinaController;
import model.Cliente;
import model.Veiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Página de perfil do cliente: dados cadastrais + veículos do cliente.
 * Acessada por duplo-clique na lista de clientes (V_VisualizarClientes).
 */
public class V_PerfilCliente extends JPanel {

    private final OficinaController controller;
    private final Cliente cliente;

    public V_PerfilCliente(OficinaController controller, Cliente cliente) {
        this.controller = controller;
        this.cliente = cliente;

        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        construirInterface();
    }

    private void construirInterface() {
        // ---- Topo: breadcrumb + voltar ----
        JPanel pnl_Topo = new JPanel(new BorderLayout());
        pnl_Topo.setOpaque(false);

        JLabel lbl_Titulo = new JLabel("Consultar Clientes > Perfil do Cliente");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));

        JButton btn_Voltar = new JButton("← Voltar à Lista");
        btn_Voltar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_Voltar.setFocusPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Voltar.addActionListener(e -> navegarPara(new V_VisualizarClientes(controller)));

        JButton btn_Editar = new JButton("Editar");
        btn_Editar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_Editar.setForeground(Color.WHITE);
        btn_Editar.setBackground(Color.decode("#6C757D"));
        btn_Editar.setFocusPainted(false);
        btn_Editar.setBorderPainted(false);
        btn_Editar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Editar.addActionListener(e -> {
            if (!confirmarComSenha("editar este cliente")) return;
            navegarPara(new V_EditarCliente(controller, cliente));
        });

        JButton btn_Excluir = new JButton("Excluir");
        btn_Excluir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_Excluir.setForeground(Color.WHITE);
        btn_Excluir.setBackground(Color.decode("#DC3545"));
        btn_Excluir.setFocusPainted(false);
        btn_Excluir.setBorderPainted(false);
        btn_Excluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Excluir.addActionListener(e -> excluirCliente());

        JPanel pnl_Botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnl_Botoes.setOpaque(false);
        pnl_Botoes.add(btn_Editar);
        pnl_Botoes.add(btn_Excluir);
        pnl_Botoes.add(btn_Voltar);

        pnl_Topo.add(lbl_Titulo, BorderLayout.WEST);
        pnl_Topo.add(pnl_Botoes, BorderLayout.EAST);
        add(pnl_Topo, BorderLayout.NORTH);

        // ---- Corpo ----
        JPanel pnl_Corpo = new JPanel();
        pnl_Corpo.setLayout(new BoxLayout(pnl_Corpo, BoxLayout.Y_AXIS));
        pnl_Corpo.setOpaque(false);

        pnl_Corpo.add(criarSecaoDados());
        pnl_Corpo.add(Box.createVerticalStrut(16));
        pnl_Corpo.add(criarSecaoVeiculos());

        JScrollPane scroll = new JScrollPane(pnl_Corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel criarSecaoDados() {
        JPanel card = criarCard("Dados do Cliente");

        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 12));
        grid.setOpaque(false);

        grid.add(criarCampoDado("Nome", cliente.getNome()));
        grid.add(criarCampoDado("CPF", cliente.getCpf()));
        grid.add(criarCampoDado("E-mail", cliente.getEmail()));
        grid.add(criarCampoDado("Telefone", cliente.getTelefone()));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.add(grid);
        return card;
    }

    private JPanel criarSecaoVeiculos() {
        JPanel card = criarCard("Veículos do Cliente");

        String[] colunas = {"Cód.", "Modelo", "Montadora", "Tipo", "Ano", "Placa"};
        DefaultTableModel mdl = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        if (cliente.getVeiculos() != null) {
            for (Veiculo v : cliente.getVeiculos()) {
                String modelo = v.getModelo() != null ? v.getModelo().getNome() : "—";
                String montadora = (v.getModelo() != null && v.getModelo().getMontadora() != null)
                    ? v.getModelo().getMontadora().getNome() : "—";
                String ano = v.getModelo() != null ? String.valueOf(v.getModelo().getAno()) : "—";
                mdl.addRow(new Object[]{
                    String.format("%04d", v.getIdVeiculo()), modelo, montadora,
                    v.getTipo(), ano, v.getPlaca()
                });
            }
        }

        JTable tabela = new JTable(mdl);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(26);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(0, 180));
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));

        JPanel corpo = (JPanel) card.getComponent(1);
        if (mdl.getRowCount() == 0) {
            JLabel vazio = new JLabel("Este cliente não possui veículos cadastrados.");
            vazio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vazio.setForeground(Color.decode("#999999"));
            corpo.add(vazio);
        } else {
            corpo.add(scroll);
        }
        return card;
    }

    private void navegarPara(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    private void excluirCliente() {
        int resp = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir o cliente \"" + cliente.getNome() + "\"?",
            "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp != JOptionPane.YES_OPTION) return;
        if (!confirmarComSenha("excluir este cliente")) return;
        try {
            controller.excluirCliente(cliente.getIdUsuario());
            JOptionPane.showMessageDialog(this,
                "Cliente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            navegarPara(new V_VisualizarClientes(controller));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao excluir cliente: " + ex.getMessage(), "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Exige a senha de acesso do usuário logado antes de prosseguir com uma ação sensível. */
    private boolean confirmarComSenha(String acao) {
        JPasswordField pwd = new JPasswordField();
        int r = JOptionPane.showConfirmDialog(this, pwd, "Confirme a senha para " + acao,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return false;
        String senha = new String(pwd.getPassword());
        if (senha.isEmpty() || !controller.confirmarSenha(senha)) {
            JOptionPane.showMessageDialog(this, "Senha incorreta.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // ===== Helpers visuais (mesmo estilo de V_Configuracoes) =====
    private JPanel criarCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        lbl.setPreferredSize(new Dimension(0, 30));

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        card.add(lbl, BorderLayout.NORTH);
        card.add(corpo, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCampoDado(String rotulo, String valor) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);

        JLabel lbl = new JLabel(rotulo.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(Color.decode("#999999"));

        JLabel val = new JLabel(valor != null && !valor.isBlank() ? valor : "—");
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(Color.decode("#333333"));

        p.add(lbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio;
        private final Color cor;
        RoundedBorder(int raio, Color cor) { this.raio = raio; this.cor = cor; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(cor);
            g2d.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2d.dispose();
        }
    }
}
