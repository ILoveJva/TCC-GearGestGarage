package view;

import controller.OficinaController;
import model.Cliente;
import model.Veiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Interface de Visualização de Veículos refatorada.
 * Padrão atualizado para visualização unificada em tela única,
 * com expansão da tabela de componentes via JDialog.
 */
public class V_DetalhesVeiculo extends JPanel {

    private OficinaController controller;
    private Veiculo veiculoAtual;
    private Cliente proprietarioAtual;
    private Runnable acaoVoltarParaLista;

    // Componentes da Tabela
    private JTable tbl_Componentes;
    private DefaultTableModel mdl_Componentes;

    public V_DetalhesVeiculo(OficinaController controller, long idVeiculo, Runnable acaoVoltarParaLista) {
        this.controller = controller;
        this.acaoVoltarParaLista = acaoVoltarParaLista;

        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        buscarDadosVeiculo(idVeiculo);

        inicializarUI();
        popularTabelaComponentes();
    }

    private void buscarDadosVeiculo(long idVeiculo) {
        if (controller == null || controller.listarClientes() == null) return;
        for (Cliente c : controller.listarClientes()) {
            if (c.getVeiculos() != null) {
                for (Veiculo v : c.getVeiculos()) {
                    if (v.getIdVeiculo() == idVeiculo) {
                        this.veiculoAtual = v;
                        this.proprietarioAtual = c;
                        return;
                    }
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

        // Coluna Esquerda
        JPanel pnlEsquerda = new JPanel();
        pnlEsquerda.setLayout(new BoxLayout(pnlEsquerda, BoxLayout.Y_AXIS));
        pnlEsquerda.setBackground(Color.WHITE);

        pnlEsquerda.add(criarBlocoInfo("Id:", veiculoAtual != null ? String.format("%04d", veiculoAtual.getIdVeiculo()) : "XXXXXXXXXXXXXXXXX"));
        pnlEsquerda.add(criarBlocoInfo("Tipo de Veiculo:", veiculoAtual != null ? veiculoAtual.getTipo() : "XXXXXXXXXXXXXXXXX"));
        pnlEsquerda.add(criarBlocoInfo("Montadora:", veiculoAtual.getModelo().getMontadora().getNome()));
        pnlEsquerda.add(criarBlocoInfo("Modelo:", veiculoAtual.getModelo().getMontadora().getNome()));

        // Ano e Placa lado a lado
        JPanel pnlAnoPlaca = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlAnoPlaca.setBackground(Color.WHITE);
        pnlAnoPlaca.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlAnoPlaca.add(criarBlocoInfo("Ano:", veiculoAtual != null ? String.valueOf(veiculoAtual.getModelo().getAno()) : "XXXX"));
        pnlAnoPlaca.add(criarBlocoInfo("Placa:", veiculoAtual != null ? veiculoAtual.getPlaca() : "XXX0X00"));
        pnlEsquerda.add(pnlAnoPlaca);

        pnlEsquerda.add(criarBlocoInfo("Versão:", "XXXXXXXXXXXXXXXXX"));
        pnlEsquerda.add(criarBlocoInfo("VIN:", "XXXXXXXXXXXXXXXXX"));

        // Coluna Direita
        JPanel pnlDireita = new JPanel();
        pnlDireita.setLayout(new BoxLayout(pnlDireita, BoxLayout.Y_AXIS));
        pnlDireita.setBackground(Color.WHITE);

        pnlDireita.add(criarBlocoInfo("Dono:", proprietarioAtual != null ? proprietarioAtual.getNome() : "XXXXXXXXXXXXXXXXX"));
        pnlDireita.add(criarBlocoInfo("Versão:", "XXXXXXXXXXXXXXXXX"));
        pnlDireita.add(criarBlocoInfo("Motor:", "XXXXXXXXXXXXXXXXX"));
        pnlDireita.add(criarBlocoInfo("Cambio:", "XXXXXXXXXXXXXXXXX"));
        pnlDireita.add(criarBlocoInfo("Direção:", "XXXXXXXXXXXXXXXXX"));
        pnlDireita.add(criarBlocoInfo("Alimentação:", "XXXXXXXXXXXXXXXXX"));
        pnlDireita.add(criarBlocoInfo("Configuração:", "XXXXXXXXXXXXXXXXX"));

        pnlInfo.add(pnlEsquerda);
        pnlInfo.add(pnlDireita);

        // --- 3. TABELA DE PEÇAS NO QUADRO INFERIOR ---
        JPanel pnlCentro = new JPanel(new BorderLayout(0, 20));
        pnlCentro.setBackground(Color.WHITE);
        pnlCentro.add(pnlInfo, BorderLayout.NORTH);

        JPanel pnlAreaTabela = new JPanel(new BorderLayout());
        pnlAreaTabela.setBackground(Color.decode("#E0E0E0"));
        pnlAreaTabela.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlHeaderTabela = new JPanel(new BorderLayout());
        pnlHeaderTabela.setOpaque(false);

        JLabel lblTituloTabela = new JLabel("Tabela de Peças");
        lblTituloTabela.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloTabela.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblAmpliar = new JLabel("Ver mais");
        lblAmpliar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAmpliar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAmpliar.setToolTipText("Clique para visualizar em tela cheia e buscar");
        lblAmpliar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirJanelaAmpliada();
            }
        });

        pnlHeaderTabela.add(lblTituloTabela, BorderLayout.CENTER);
        pnlHeaderTabela.add(lblAmpliar, BorderLayout.EAST);
        pnlAreaTabela.add(pnlHeaderTabela, BorderLayout.NORTH);

        // Configuração do Scroll e JTable
        String[] colunas = {"ID", "Sistema", "Nome", "Última Troca", "Durabilidade KM", "Durabilidade Tempo"};
        mdl_Componentes = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tbl_Componentes = new JTable(mdl_Componentes);
        tbl_Componentes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Componentes.setRowHeight(25);
        tbl_Componentes.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollTabela = new JScrollPane(tbl_Componentes);
        scrollTabela.setPreferredSize(new Dimension(0, 180)); // Define uma altura base para mostrar scroll na tela
        pnlAreaTabela.add(scrollTabela, BorderLayout.CENTER);

        pnlCentro.add(pnlAreaTabela, BorderLayout.CENTER);
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

    private void popularTabelaComponentes() {
        mdl_Componentes.setRowCount(0);
        if (veiculoAtual == null || veiculoAtual.getListaPecas() == null) return;

        Object[][] componentesMock = veiculoAtual.getListaPecas().stream()
                .map(peca -> new Object[]{
                        peca.getIdPeca(),
                        peca.getSistema(),
                        peca.getNome(),
                        "10/06/2025",
                        peca.getVidaUtilKm() + " KM",
                        peca.getVidaUtilTempo() + " meses"
                })
                .toArray(Object[][]::new);

        for (Object[] linha : componentesMock) {
            mdl_Componentes.addRow(linha);
        }
    }

    /**
     * Abre a Janela (JDialog) menor quando clicado no botão de Expandir/Ampliar.
     */
    private void abrirJanelaAmpliada() {
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) ancestor, "Tabela de Peças - Pesquisa Avançada", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Barra Superior do Dialog (Pesquisa e Filtros)
        JPanel pnlBarraDialog = new JPanel(new BorderLayout());
        pnlBarraDialog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlPesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlPesquisa.add(new JLabel("Pesquisar:"));
        JTextField txtPesquisa = new JTextField(25);
        pnlPesquisa.add(txtPesquisa);


        pnlBarraDialog.add(pnlPesquisa, BorderLayout.WEST);
        dialog.add(pnlBarraDialog, BorderLayout.NORTH);

        // Tabela Sincronizada com o Dialog
        JTable tblDialog = new JTable(mdl_Componentes);
        tblDialog.setRowHeight(25);
        TableRowSorter<DefaultTableModel> sorterDialog = new TableRowSorter<>(mdl_Componentes);
        tblDialog.setRowSorter(sorterDialog);

        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String termo = txtPesquisa.getText().trim();
                if (termo.isEmpty()) {
                    sorterDialog.setRowFilter(null);
                } else {
                    sorterDialog.setRowFilter(RowFilter.regexFilter("(?i)" + termo));
                }
            }
        });

        dialog.add(new JScrollPane(tblDialog), BorderLayout.CENTER);

        // Base
        JPanel pnlFechamento = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dialog.dispose());
        pnlFechamento.add(btnFechar);
        pnlFechamento.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        dialog.add(pnlFechamento, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}