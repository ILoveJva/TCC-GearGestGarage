package view;

import controller.OficinaController;
import model.Oficina;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;

/** Formulário de solicitação de revisão: veículo do cliente + oficina escolhida na busca. */
public class V_ClienteSolicitarRevisao extends JPanel {

    private final OficinaController controller;
    private Oficina oficina;

    private JComboBox<Veiculo> cbbVeiculo;
    private JLabel lblOficina;
    private JTextField txtMotivo;
    private JTextArea txtDescricao;
    private JCheckBox chkLevar;
    private JCheckBox chkBuscar;

    public V_ClienteSolicitarRevisao(OficinaController controller, Oficina oficina) {
        this.controller = controller;
        this.oficina = oficina;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.addActionListener(e -> navegar(new V_ClienteBuscarOficinas(this.controller)));
        add(btnVoltar, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 60, 0, 60));

        JLabel titulo = new JLabel("Solicitar Revisão");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        form.add(titulo);
        form.add(Box.createVerticalStrut(20));

        form.add(new JLabel("Veículo:"));
        cbbVeiculo = new JComboBox<>();
        cbbVeiculo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        for (Veiculo v : controller.listarMeusVeiculos()) cbbVeiculo.addItem(v);
        cbbVeiculo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Veiculo ve) {
                    String marca = ve.getModelo() != null && ve.getModelo().getMontadora() != null
                        ? ve.getModelo().getMontadora().getNome() : "";
                    String modelo = ve.getModelo() != null ? ve.getModelo().getNome() : "";
                    setText(marca + " " + modelo + " — " + ve.getPlaca());
                }
                return this;
            }
        });
        form.add(cbbVeiculo);
        form.add(Box.createVerticalStrut(16));

        form.add(new JLabel("Solicitando em:"));
        JPanel pnlOficina = new JPanel(new BorderLayout(10, 0));
        pnlOficina.setOpaque(false);
        pnlOficina.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        lblOficina = new JLabel();
        atualizarLabelOficina();
        JButton btnAlterar = new JButton("Alterar");
        btnAlterar.addActionListener(e -> navegar(new V_ClienteBuscarOficinas(this.controller)));
        pnlOficina.add(lblOficina, BorderLayout.CENTER);
        pnlOficina.add(btnAlterar, BorderLayout.EAST);
        form.add(pnlOficina);
        form.add(Box.createVerticalStrut(16));

        form.add(new JLabel("Motivo:"));
        txtMotivo = new JTextField();
        txtMotivo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(txtMotivo);
        form.add(Box.createVerticalStrut(12));

        form.add(new JLabel("Descrição:"));
        txtDescricao = new JTextArea(5, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        form.add(scrollDescricao);
        form.add(Box.createVerticalStrut(12));

        JPanel pnlOpcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnlOpcoes.setOpaque(false);
        chkLevar = new JCheckBox("Levar o veículo até a oficina");
        chkBuscar = new JCheckBox("Buscar o veículo na minha casa");
        pnlOpcoes.add(chkLevar);
        pnlOpcoes.add(chkBuscar);
        form.add(pnlOpcoes);
        form.add(Box.createVerticalStrut(20));

        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEnviar.addActionListener(e -> enviar());
        form.add(btnEnviar);

        add(new JScrollPane(form), BorderLayout.CENTER);
    }

    private void atualizarLabelOficina() {
        lblOficina.setText("<html><b>" + oficina.getNome() + "</b><br>" + oficina.getEndereco() + "</html>");
    }

    private void enviar() {
        Veiculo veiculo = (Veiculo) cbbVeiculo.getSelectedItem();
        String motivo = txtMotivo.getText().trim();

        if (veiculo == null) {
            DialogoAlerta.aviso(this, "Você precisa ter um veículo cadastrado para solicitar revisão.", "Dados inválidos");
            return;
        }
        if (motivo.isEmpty()) {
            DialogoAlerta.aviso(this, "Informe o motivo da solicitação.", "Dados inválidos");
            return;
        }

        try {
            controller.solicitarRevisao(veiculo.getIdVeiculo(), oficina.getIdOficina(), motivo,
                txtDescricao.getText().trim(), chkLevar.isSelected(), chkBuscar.isSelected());
            DialogoAlerta.sucesso(this, "Solicitação enviada com sucesso!", "Sucesso");
            navegar(new V_ClienteOrcamentos(controller));
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao enviar solicitação: " + ex.getMessage(), "Erro");
        }
    }

    private void navegar(JPanel pagina) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela instanceof V_ClienteMain) ((V_ClienteMain) janela).atualizarConteudo(pagina);
    }
}
