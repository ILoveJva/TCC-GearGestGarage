package view;

import controller.OficinaController;
import model.Orcamento;
import model.OrdemDeServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * Lista de orçamentos com aprovação/reprovação e geração de O.S.
 * a partir de um orçamento aprovado.
 */
public class V_AprovarOrcamento extends JPanel {

    private final OficinaController controller;
    private JTable tabela;
    private DefaultTableModel modelo;
    private List<Orcamento> orcamentos;

    public V_AprovarOrcamento(OficinaController controller) {
        this.controller = controller;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
        carregar();
    }

    private void construir() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Página Inicial > Orçamentos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));
        JButton btnNovo = new JButton("+ Novo Orçamento");
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNovo.setBackground(Color.decode("#FF9900"));
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorderPainted(false);
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ImageIcon icoOrc = carregarIcone("/assets/icons/vorcamentos.png", 20, 20);
        if (icoOrc != null) {
            btnNovo.setIcon(icoOrc);
            btnNovo.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        btnNovo.addActionListener(e -> navegar(new V_CadastrarOrcamento(controller)));
        topo.add(titulo, BorderLayout.WEST);
        topo.add(btnNovo, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        String[] cols = {"Cód.", "Cliente", "Veículo", "Peças", "Responsável", "Valor (R$)", "Reclamação", "Status"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(26);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Orcamento o = selecionado();
                    if (o != null) navegar(new V_VisualizarOrcamento(controller, o));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        add(scroll, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        acoes.setOpaque(false);
        JButton btnAprovar = criarBtn("Aprovar", "#28A745");
        JButton btnReprovar = criarBtn("Reprovar", "#DC3545");
        JButton btnGerar = criarBtn("Gerar Serviço", "#17A2B8");
        btnAprovar.addActionListener(e -> mudarStatus(true));
        btnReprovar.addActionListener(e -> mudarStatus(false));
        btnGerar.addActionListener(e -> gerarServico());
        acoes.add(btnAprovar);
        acoes.add(btnReprovar);
        acoes.add(btnGerar);
        add(acoes, BorderLayout.SOUTH);
    }

    private void carregar() {
        orcamentos = controller.listarOrcamentos();
        modelo.setRowCount(0);

        for (Orcamento o : orcamentos) {
            List<String> pecas = controller.listarPecasDoOrcamento(o.getIdOrcamento());
            modelo.addRow(new Object[]{
                o.getCodigo().isEmpty() ? String.format("%04d", o.getIdOrcamento()) : o.getCodigo(),
                controller.cliente_id(o.getIdCliente()).getNome(), o.getPlacaVeiculo(), String.join(", ", pecas), o.getResponsavel(),
                String.format("%.2f", o.getValor()), o.getReclamacao(), o.getStatus()
            });
        }
    }

    private Orcamento selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || orcamentos == null || linha >= orcamentos.size()) return null;
        return orcamentos.get(linha);
    }

    private void mudarStatus(boolean aprovar) {
        Orcamento o = selecionado();
        if (o == null) { aviso("Selecione um orçamento na tabela."); return; }
        if (!"PENDENTE".equalsIgnoreCase(o.getStatus())) {
            aviso("Este orçamento já foi " + o.getStatus().toLowerCase() + "."); return;
        }
        if (!confirmarComSenha(aprovar ? "aprovar este orçamento" : "reprovar este orçamento")) return;
        try {
            if (aprovar) controller.aprovarOrcamento(o.getIdOrcamento());
            else controller.reprovarOrcamento(o.getIdOrcamento());
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarServico() {
        Orcamento o = selecionado();
        if (o == null) { aviso("Selecione um orçamento na tabela."); return; }
        if (!"APROVADO".equalsIgnoreCase(o.getStatus())) {
            aviso("Só é possível gerar serviço de um orçamento APROVADO."); return;
        }

        JTextField txtTitulo = new JTextField();

        JComboBox<String> cmbTipo = new JComboBox<>();
        for (OrdemDeServico.TipoServicoOS t : OrdemDeServico.TipoServicoOS.values())
            cmbTipo.addItem(t.getLabel());

        JComboBox<String> cmbManutencao = new JComboBox<>();
        for (OrdemDeServico.TipoManutencao m : OrdemDeServico.TipoManutencao.values())
            cmbManutencao.addItem(m.getLabel());

        JTextField txtData = new JTextField(LocalDate.now().toString());
        JTextField txtKm = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Título do Serviço:"));   form.add(txtTitulo);
        form.add(new JLabel("Tipo de Serviço:"));      form.add(cmbTipo);
        form.add(new JLabel("Tipo de Manutenção:"));   form.add(cmbManutencao);
        form.add(new JLabel("Data (AAAA-MM-DD):"));    form.add(txtData);
        form.add(new JLabel("KM atual:"));             form.add(txtKm);

        int r = JOptionPane.showConfirmDialog(this, form,
            "Gerar Serviço do Orçamento " + String.format("%04d", o.getIdOrcamento()),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String titulo = txtTitulo.getText().trim();
        if (titulo.length() < 3) { aviso("Título deve ter pelo menos 3 caracteres."); return; }
        int km;
        try { km = Integer.parseInt(txtKm.getText().trim()); if (km < 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { aviso("Informe um KM válido."); return; }

        try {
            OrdemDeServico.TipoServicoOS tipoEnum =
                OrdemDeServico.TipoServicoOS.fromLabel((String) cmbTipo.getSelectedItem());
            OrdemDeServico.TipoManutencao manutEnum =
                OrdemDeServico.TipoManutencao.fromLabel((String) cmbManutencao.getSelectedItem());
            controller.abrirOSDeOrcamento(titulo, tipoEnum.name(), manutEnum.name(),
                txtData.getText().trim(), km, o.getIdVeiculo(), o.getIdOrcamento());
            JOptionPane.showMessageDialog(this,
                "Ordem de Serviço gerada a partir do orçamento!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar serviço: " + ex.getMessage(),
                "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
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

    private JButton criarBtn(String texto, String cor) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(Color.decode(cor));
        b.setPreferredSize(new Dimension(140, 38));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private ImageIcon carregarIcone(String caminho, int w, int h) {
        java.net.URL url = getClass().getResource(caminho);
        if (url == null) {
            String s = caminho.startsWith("/") ? caminho.substring(1) : caminho;
            url = Thread.currentThread().getContextClassLoader().getResource(s);
        }
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}
