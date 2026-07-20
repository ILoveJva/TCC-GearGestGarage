package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import controller.OficinaController;
import br.com.oficina.veiculo.DetalhesVeiculoEntity;
import model.Cliente;
import model.Veiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Interface de Visualização de Veículos refatorada.
 * Padrão atualizado para visualização unificada em tela única,
 * com expansão da tabela de componentes via JDialog.
 */
public class V_DetalhesVeiculo extends JPanel {

    private OficinaController controller;
    private Veiculo veiculoAtual;
    private Cliente proprietarioAtual;
    private String nomeProprietario;
    private Runnable acaoVoltarParaLista;

    // Tabela de serviços realizados
    private JTable tbl_Servicos;
    private DefaultTableModel mdl_Servicos;

    // Formulário de detalhes técnicos (editável)
    private JTextField txt_Motor, txt_Cambio, txt_Direcao, txt_Freios, txt_Cor, txt_Vin;

    public V_DetalhesVeiculo(OficinaController controller, long idVeiculo, Runnable acaoVoltarParaLista) {
        this.controller = controller;
        this.acaoVoltarParaLista = acaoVoltarParaLista;

        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        buscarDadosVeiculo(idVeiculo);

        inicializarUI();
        popularTabelaServicos();
    }

    private void buscarDadosVeiculo(long idVeiculo) {
        if (controller == null) return;
        // Busca o veículo em todos os clientes
        for (Cliente c : controller.listarClientes()) {
            if (c.getVeiculos() != null) {
                for (Veiculo v : c.getVeiculos()) {
                    if (v.getIdVeiculo() == idVeiculo) {
                        this.veiculoAtual = v;
                        this.proprietarioAtual = c;
                        this.nomeProprietario = c.getNome();
                        return;
                    }
                }
            }
        }
        // Se não encontrou entre clientes, busca entre funcionários
        for (br.com.oficina.usuario.FuncionarioEntity f : controller.listarFuncionarios()) {
            if (f.getIdUsuario() == null) continue;
            for (Veiculo v : controller.listarVeiculosPorProprietario(f.getIdUsuario())) {
                if (v.getIdVeiculo() == idVeiculo) {
                    this.veiculoAtual = v;
                    this.nomeProprietario = f.getNome() + " (Funcionário)";
                    return;
                }
            }
        }
    }

    private void inicializarUI() {
        // --- 1. CABEÇALHO ---
        JPanel pnlTopo = new JPanel(new BorderLayout());
        pnlTopo.setBackground(Color.WHITE);

        String nomeModelo = (veiculoAtual != null && veiculoAtual.getModelo() != null) ? veiculoAtual.getModelo().getNome() : "Volkswagen Gol";
        JLabel lblTitulo = new JLabel("<html><font color='#808080'>Veículos Cadastrados ></font> <b>" + nomeModelo + "</b></html>");
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        pnlTopo.add(lblTitulo, BorderLayout.WEST);

        JButton btnVoltarGeral = new JButton("← Voltar à Lista");
        btnVoltarGeral.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVoltarGeral.setFocusPainted(false);
        btnVoltarGeral.addActionListener(e -> { if(acaoVoltarParaLista != null) acaoVoltarParaLista.run(); });
        pnlTopo.add(btnVoltarGeral, BorderLayout.EAST);

        pnlTopo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        add(pnlTopo, BorderLayout.NORTH);

        // --- 2. CORPO: INFORMAÇÕES DO VEÍCULO ---
        JPanel pnlInfo = new JPanel(new GridLayout(1, 2, 40, 0));
        pnlInfo.setBackground(Color.WHITE);

        boolean temVeiculo = veiculoAtual != null;
        Veiculo v = veiculoAtual;
        model.Modelo modelo = temVeiculo ? v.getModelo() : null;
        model.Montadora montadora = modelo != null ? modelo.getMontadora() : null;

        // Coluna Esquerda — dados identificadores (somente leitura)
        JPanel pnlEsquerda = new JPanel();
        pnlEsquerda.setLayout(new BoxLayout(pnlEsquerda, BoxLayout.Y_AXIS));
        pnlEsquerda.setBackground(Color.WHITE);

        pnlEsquerda.add(criarBlocoInfo("Id:", temVeiculo ? String.format("%04d", v.getIdVeiculo()) : "—"));
        pnlEsquerda.add(criarBlocoInfo("Tipo de Veículo:", temVeiculo ? v.getTipo() : "—"));
        pnlEsquerda.add(criarBlocoInfo("Montadora:", montadora != null ? montadora.getNome() : "—"));
        pnlEsquerda.add(criarBlocoInfo("Modelo:", modelo != null ? modelo.getNome() : "—"));

        // Ano e Placa lado a lado
        JPanel pnlAnoPlaca = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlAnoPlaca.setBackground(Color.WHITE);
        pnlAnoPlaca.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlAnoPlaca.add(criarBlocoInfo("Ano:", modelo != null ? String.valueOf(modelo.getAno()) : "—"));
        pnlAnoPlaca.add(criarBlocoInfo("Placa:", temVeiculo ? v.getPlaca() : "—"));
        pnlEsquerda.add(pnlAnoPlaca);

        pnlEsquerda.add(criarBlocoInfo("Dono:", nomeProprietario != null ? nomeProprietario : "—"));

        // Coluna Direita — formulário de detalhes técnicos (editável)
        txt_Motor = criarCampoEditavel();
        txt_Cambio = criarCampoEditavel();
        txt_Direcao = criarCampoEditavel();
        txt_Freios = criarCampoEditavel();
        txt_Cor = criarCampoEditavel();
        txt_Vin = criarCampoEditavel();

        // Carrega valores já persistidos, se houver
        if (temVeiculo && controller != null) {
            DetalhesVeiculoEntity d = controller.getDetalhesVeiculo(v.getIdVeiculo());
            if (d != null) {
                txt_Motor.setText(nvl(d.getMotor()));
                txt_Cambio.setText(nvl(d.getCambio()));
                txt_Direcao.setText(nvl(d.getDirecao()));
                txt_Freios.setText(nvl(d.getSistemaFreios()));
                txt_Cor.setText(nvl(d.getCor()));
                txt_Vin.setText(nvl(d.getVin()));
            }
        }

        JPanel pnlDireita = new JPanel();
        pnlDireita.setLayout(new BoxLayout(pnlDireita, BoxLayout.Y_AXIS));
        pnlDireita.setBackground(Color.WHITE);

        JLabel lblFormTitulo = new JLabel("Detalhes Técnicos");
        lblFormTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFormTitulo.setForeground(Color.decode("#FF9900"));
        lblFormTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDireita.add(lblFormTitulo);
        pnlDireita.add(Box.createVerticalStrut(8));

        pnlDireita.add(criarBlocoCampo("Motor:", txt_Motor));
        pnlDireita.add(criarBlocoCampo("Câmbio:", txt_Cambio));
        pnlDireita.add(criarBlocoCampo("Direção:", txt_Direcao));
        pnlDireita.add(criarBlocoCampo("Sistema de Freios:", txt_Freios));
        pnlDireita.add(criarBlocoCampo("Cor:", txt_Cor));
        pnlDireita.add(criarBlocoCampo("VIN:", txt_Vin));

        JButton btnSalvarDetalhes = new JButton("Salvar Detalhes");
        btnSalvarDetalhes.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvarDetalhes.setForeground(Color.WHITE);
        btnSalvarDetalhes.setBackground(Color.decode("#FF9900"));
        btnSalvarDetalhes.setFocusPainted(false);
        btnSalvarDetalhes.setBorderPainted(false);
        btnSalvarDetalhes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvarDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalvarDetalhes.setEnabled(temVeiculo);
        btnSalvarDetalhes.addActionListener(e -> salvarDetalhes());
        pnlDireita.add(Box.createVerticalStrut(6));
        pnlDireita.add(btnSalvarDetalhes);

        pnlInfo.add(pnlEsquerda);
        pnlInfo.add(pnlDireita);

        // --- 3. SERVIÇOS REALIZADOS ---
        JPanel pnlCentro = new JPanel(new BorderLayout(0, 20));
        pnlCentro.setBackground(Color.WHITE);
        pnlCentro.add(pnlInfo, BorderLayout.NORTH);

        JPanel pnlAreaServicos = new JPanel(new BorderLayout());
        pnlAreaServicos.setBackground(Color.decode("#E0E0E0"));
        pnlAreaServicos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlHeaderServicos = new JPanel(new BorderLayout());
        pnlHeaderServicos.setOpaque(false);

        JLabel lblTituloServicos = new JLabel("Serviços Realizados neste Veículo");
        lblTituloServicos.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblDica = new JLabel("Duplo-clique para abrir a O.S.");
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(Color.decode("#777777"));

        pnlHeaderServicos.add(lblTituloServicos, BorderLayout.WEST);
        pnlHeaderServicos.add(lblDica, BorderLayout.EAST);
        pnlAreaServicos.add(pnlHeaderServicos, BorderLayout.NORTH);

        String[] colServicos = {"ID", "Oficina", "Data", "Status"};
        mdl_Servicos = new DefaultTableModel(colServicos, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Long.class : String.class;
            }
        };

        tbl_Servicos = new JTable(mdl_Servicos);
        tbl_Servicos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Servicos.setRowHeight(25);
        tbl_Servicos.getTableHeader().setReorderingAllowed(false);
        tbl_Servicos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Servicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_Servicos.getColumnModel().getColumn(0).setMaxWidth(60);
        tbl_Servicos.getColumnModel().getColumn(0).setMinWidth(50);

        tbl_Servicos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tbl_Servicos.getSelectedRow();
                    if (row >= 0) {
                        Long idServico = (Long) mdl_Servicos.getValueAt(row, 0);
                        navegar(new V_OrdemServico(controller, idServico));
                    }
                }
            }
        });

        JScrollPane scrollServicos = new JScrollPane(tbl_Servicos);
        scrollServicos.setPreferredSize(new Dimension(0, 180));
        pnlAreaServicos.add(scrollServicos, BorderLayout.CENTER);

        pnlCentro.add(pnlAreaServicos, BorderLayout.CENTER);
        add(pnlCentro, BorderLayout.CENTER);
    }

    /**
     * Cria blocos padronizados contendo um título e o valor abaixo para as colunas.
     */
    private JPanel criarBlocoInfo(String titulo, String valor) {
        JPanel pnl = new JPanel(new GridLayout(2, 1));
        pnl.setBackground(Color.WHITE);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pnl.add(lblTitulo);
        pnl.add(lblValor);
        pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return pnl;
    }

    /** Bloco com rótulo em cima e campo de texto editável embaixo. */
    private JPanel criarBlocoCampo(String titulo, JTextField campo) {
        JPanel pnl = new JPanel(new BorderLayout(0, 2));
        pnl.setBackground(Color.WHITE);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pnl.add(lblTitulo, BorderLayout.NORTH);
        pnl.add(campo, BorderLayout.CENTER);
        pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return pnl;
    }

    private JTextField criarCampoEditavel() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBackground(Color.WHITE);
        f.setForeground(Color.BLACK);
        f.setCaretColor(Color.BLACK);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return f;
    }

    /** Persiste os detalhes técnicos do veículo atual. */
    private void salvarDetalhes() {
        if (veiculoAtual == null || controller == null) return;
        try {
            controller.salvarDetalhesVeiculo(
                veiculoAtual.getIdVeiculo(),
                txt_Motor.getText().trim(),
                txt_Cambio.getText().trim(),
                txt_Direcao.getText().trim(),
                txt_Freios.getText().trim(),
                txt_Cor.getText().trim(),
                txt_Vin.getText().trim());
            JOptionPane.showMessageDialog(this,
                "Detalhes do veículo salvos com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar detalhes: " + ex.getMessage(),
                "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String nvl(String s) { return s == null ? "" : s; }

    private void popularTabelaServicos() {
        mdl_Servicos.setRowCount(0);
        if (veiculoAtual == null || controller == null) return;
        String nomeOficina = controller.getOficina() != null ? controller.getOficina().getNome() : "—";
        List<ServicoResponseDTO> servicos = controller.listarServicosPorVeiculo(veiculoAtual.getIdVeiculo());
        for (ServicoResponseDTO s : servicos) {
            mdl_Servicos.addRow(new Object[]{
                s.idServico(),
                nomeOficina,
                s.dataServico() != null ? s.dataServico() : "—",
                s.status() != null ? s.status() : "—"
            });
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }
}