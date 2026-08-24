package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import controller.OficinaController;
import br.com.oficina.veiculo.DetalhesVeiculoEntity;
import model.Cliente;
import model.Veiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Interface de Visualização de Veículos refatorada.
 * Padrão atualizado para visualização unificada em tela única,
 * com expansão da tabela de componentes via JDialog.
 */
public class V_DetalhesVeiculo extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mexa só aqui para alterar tudo de uma vez
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#2B2E33");
    private static final Color COR_LABEL        = Color.decode("#8A93A0");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_LINHA_TOPO   = Color.decode("#DDE3EA");

    // Cor de ação (tema original preservado) e suas variações de hover/pressionado
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    // Botão secundário (Voltar) — tom cinza
    private static final Color COR_VOLTAR         = Color.decode("#6C757D");
    private static final Color COR_VOLTAR_CLARA   = Color.decode("#7C848C");
    private static final Color COR_VOLTAR_ESCURA  = Color.decode("#5A6169");

    // Vidro cinza claro estilo Windows 7 (Aero) — cabeçalho da tabela de serviços
    private static final Color COR_AERO_TOPO_A  = Color.decode("#FBFBFC");
    private static final Color COR_AERO_TOPO_B  = Color.decode("#ECEEF1");
    private static final Color COR_AERO_BASE_A  = Color.decode("#DADDE2");
    private static final Color COR_AERO_BASE_B  = Color.decode("#EFF1F3");
    private static final Color COR_AERO_BORDA   = Color.decode("#B6BCC4");
    private static final Color COR_AERO_SEPARA  = Color.decode("#CCD1D8");
    private static final Color COR_AERO_TEXTO   = Color.decode("#3A4149");

    // Tabela de dados
    private static final Color COR_TABELA_FUNDO   = Color.WHITE;
    private static final Color COR_TABELA_GRADE   = Color.decode("#E3E9F0");
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");

    private static final int RAIO_COMPONENTE         = 12;
    private static final int TAMANHO_FONTE_LABEL_INFO = 11;   // rótulo pequeno (ex: "MONTADORA")
    private static final int TAMANHO_FONTE_VALOR_INFO = 15;   // valor grande e em destaque
    private static final int TAMANHO_FONTE_LABEL_CAMPO = 11;  // rótulo dos campos editáveis
    private static final int TAMANHO_FONTE_CAMPO      = 13;   // texto digitado nos campos
    private static final int ALTURA_CAMPO             = 28;
    private static final int TAMANHO_FONTE_BOTAO      = 12;
    private static final int ALTURA_BOTAO             = 32;
    private static final int LARGURA_BOTAO_VOLTAR     = 140;
    private static final int LARGURA_BOTAO_SALVAR     = 170;
    private static final int ALTURA_CABECALHO         = 26;
    private static final int ALTURA_LINHA_TABELA       = 22;

    private OficinaController controller;
    private Veiculo veiculoAtual;
    private Cliente proprietarioAtual;
    private String nomeProprietario;
    private Runnable acaoVoltarParaLista;

    // Tabela de serviços realizados
    private JTable tbl_Servicos;
    private DefaultTableModel mdl_Servicos;

    // Formulário de detalhes técnicos (editável)
    private GlassTextField txt_Motor, txt_Cambio, txt_Direcao, txt_Freios, txt_Cor, txt_Vin;

    public V_DetalhesVeiculo(OficinaController controller, long idVeiculo, Runnable acaoVoltarParaLista) {
        this.controller = controller;
        this.acaoVoltarParaLista = acaoVoltarParaLista;

        setBackground(COR_FUNDO_PAGINA);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

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
        pnlTopo.setOpaque(false);

        String nomeModelo = (veiculoAtual != null && veiculoAtual.getModelo() != null) ? veiculoAtual.getModelo().getNome() : "Volkswagen Gol";
        JLabel lblTitulo = new JLabel("<html><font color='#8A93A0'>Veículos Cadastrados &gt;</font> <b><font color='#2B2E33'>" + nomeModelo + "</font></b></html>");
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        pnlTopo.add(lblTitulo, BorderLayout.WEST);

        BotaoAcao btnVoltarGeral = new BotaoAcao("← Voltar à Lista", COR_VOLTAR, COR_VOLTAR_CLARA, COR_VOLTAR_ESCURA);
        btnVoltarGeral.setPreferredSize(new Dimension(LARGURA_BOTAO_VOLTAR, ALTURA_BOTAO));
        btnVoltarGeral.addActionListener(e -> { if (acaoVoltarParaLista != null) acaoVoltarParaLista.run(); });
        pnlTopo.add(btnVoltarGeral, BorderLayout.EAST);

        pnlTopo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_LINHA_TOPO),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)));
        add(pnlTopo, BorderLayout.NORTH);

        // --- 2. CORPO: INFORMAÇÕES DO VEÍCULO ---
        JPanel pnlInfo = new JPanel(new GridLayout(1, 2, 30, 0));
        pnlInfo.setOpaque(false);

        boolean temVeiculo = veiculoAtual != null;
        Veiculo v = veiculoAtual;
        model.Modelo modelo = temVeiculo ? v.getModelo() : null;
        model.Montadora montadora = modelo != null ? modelo.getMontadora() : null;

        // Coluna Esquerda — dados identificadores (somente leitura)
        JPanel pnlEsquerda = new JPanel();
        pnlEsquerda.setLayout(new BoxLayout(pnlEsquerda, BoxLayout.Y_AXIS));
        pnlEsquerda.setOpaque(false);

        pnlEsquerda.add(criarBlocoInfo("Id", temVeiculo ? String.format("%04d", v.getIdVeiculo()) : "—"));
        pnlEsquerda.add(criarBlocoInfo("Tipo de Veículo", temVeiculo ? v.getTipo() : "—"));
        pnlEsquerda.add(criarBlocoInfo("Montadora", montadora != null ? montadora.getNome() : "—"));
        pnlEsquerda.add(criarBlocoInfo("Modelo", modelo != null ? modelo.getNome() : "—"));

        // Ano e Placa lado a lado
        JPanel pnlAnoPlaca = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlAnoPlaca.setOpaque(false);
        pnlAnoPlaca.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlAnoPlaca.add(criarBlocoInfo("Ano", modelo != null ? String.valueOf(modelo.getAno()) : "—"));
        pnlAnoPlaca.add(criarBlocoInfo("Placa", temVeiculo ? v.getPlaca() : "—"));
        pnlEsquerda.add(pnlAnoPlaca);

        pnlEsquerda.add(criarBlocoInfo("Dono", nomeProprietario != null ? nomeProprietario : "—"));

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
        pnlDireita.setOpaque(false);

        JLabel lblFormTitulo = new JLabel("Detalhes Técnicos");
        lblFormTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFormTitulo.setForeground(COR_ACAO);
        lblFormTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDireita.add(lblFormTitulo);
        pnlDireita.add(Box.createVerticalStrut(6));

        pnlDireita.add(criarBlocoCampo("Motor", txt_Motor));
        pnlDireita.add(criarBlocoCampo("Câmbio", txt_Cambio));
        pnlDireita.add(criarBlocoCampo("Direção", txt_Direcao));
        pnlDireita.add(criarBlocoCampo("Sistema de Freios", txt_Freios));
        pnlDireita.add(criarBlocoCampo("Cor", txt_Cor));
        pnlDireita.add(criarBlocoCampo("VIN", txt_Vin));

        BotaoAcao btnSalvarDetalhes = new BotaoAcao("Salvar Detalhes", COR_ACAO, COR_ACAO_CLARA, COR_ACAO_ESCURA);
        btnSalvarDetalhes.setPreferredSize(new Dimension(LARGURA_BOTAO_SALVAR, ALTURA_BOTAO));
        btnSalvarDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalvarDetalhes.setEnabled(temVeiculo);
        btnSalvarDetalhes.addActionListener(e -> salvarDetalhes());
        pnlDireita.add(Box.createVerticalStrut(4));

        JPanel pnlBotaoWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlBotaoWrapper.setOpaque(false);
        pnlBotaoWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBotaoWrapper.add(btnSalvarDetalhes);
        pnlDireita.add(pnlBotaoWrapper);

        pnlInfo.add(pnlEsquerda);
        pnlInfo.add(pnlDireita);

        // --- 3. SERVIÇOS REALIZADOS ---
        JPanel pnlCentro = new JPanel(new BorderLayout(0, 12));
        pnlCentro.setOpaque(false);
        pnlCentro.add(pnlInfo, BorderLayout.NORTH);

        JPanel pnlAreaServicos = new PainelGradiente(new BorderLayout(), COR_CARD_TOPO, COR_CARD_BASE);
        pnlAreaServicos.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel pnlHeaderServicos = new JPanel(new BorderLayout());
        pnlHeaderServicos.setOpaque(false);

        JLabel lblTituloServicos = new JLabel("Serviços Realizados neste Veículo");
        lblTituloServicos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloServicos.setForeground(COR_TITULO);

        JLabel lblDica = new JLabel("Duplo-clique para abrir a O.S.");
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(COR_LABEL);

        pnlHeaderServicos.add(lblTituloServicos, BorderLayout.WEST);
        pnlHeaderServicos.add(lblDica, BorderLayout.EAST);
        pnlHeaderServicos.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 2));
        pnlAreaServicos.add(pnlHeaderServicos, BorderLayout.NORTH);

        String[] colServicos = {"ID", "Oficina", "Data", "Status"};
        mdl_Servicos = new DefaultTableModel(colServicos, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Long.class : String.class;
            }
        };

        tbl_Servicos = new JTable(mdl_Servicos);
        tbl_Servicos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Servicos.setRowHeight(ALTURA_LINHA_TABELA);
        tbl_Servicos.setBackground(COR_TABELA_FUNDO);
        tbl_Servicos.setForeground(COR_TEXTO_CAMPO);
        tbl_Servicos.setOpaque(true);
        tbl_Servicos.setShowGrid(true);
        tbl_Servicos.setGridColor(COR_TABELA_GRADE);
        tbl_Servicos.setIntercellSpacing(new Dimension(0, 0));
        tbl_Servicos.setSelectionBackground(COR_TABELA_SELECAO);
        tbl_Servicos.setSelectionForeground(COR_TEXTO_CAMPO);
        tbl_Servicos.setFillsViewportHeight(true);
        tbl_Servicos.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());
        tbl_Servicos.getTableHeader().setReorderingAllowed(false);
        tbl_Servicos.getColumnModel().getColumn(0).setMaxWidth(60);
        tbl_Servicos.getColumnModel().getColumn(0).setMinWidth(50);

        JTableHeader cabecalho = tbl_Servicos.getTableHeader();
        cabecalho.setDefaultRenderer(new CabecalhoVidroClaro());
        cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, ALTURA_CABECALHO));
        cabecalho.setOpaque(false);

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
        scrollServicos.setPreferredSize(new Dimension(0, 140));
        scrollServicos.getViewport().setBackground(COR_TABELA_FUNDO);
        scrollServicos.getViewport().setOpaque(true);
        ScrollBarPadrao.aplicar(scrollServicos);
        scrollServicos.setOpaque(false);
        scrollServicos.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        pnlAreaServicos.add(scrollServicos, BorderLayout.CENTER);

        pnlCentro.add(pnlAreaServicos, BorderLayout.CENTER);
        add(pnlCentro, BorderLayout.CENTER);
    }

    /**
     * Cria blocos padronizados contendo um título e o valor abaixo para as colunas.
     * Rótulo pequeno em maiúsculas + valor grande e em destaque.
     */
    private JPanel criarBlocoInfo(String titulo, String valor) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL_INFO));
        lblTitulo.setForeground(COR_LABEL);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_VALOR_INFO));
        lblValor.setForeground(COR_TITULO);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnl.add(lblTitulo);
        pnl.add(Box.createVerticalStrut(2));
        pnl.add(lblValor);
        pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 9, 0));
        return pnl;
    }

    /** Bloco com rótulo em cima e campo de texto editável (vidro) embaixo. */
    private JPanel criarBlocoCampo(String titulo, JTextField campo) {
        JPanel pnl = new JPanel(new BorderLayout(0, 3));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_CAMPO + 18));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL_CAMPO));
        lblTitulo.setForeground(COR_LABEL);

        pnl.add(lblTitulo, BorderLayout.NORTH);
        pnl.add(campo, BorderLayout.CENTER);
        pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return pnl;
    }

    private GlassTextField criarCampoEditavel() {
        GlassTextField f = new GlassTextField();
        f.setPreferredSize(new Dimension(220, ALTURA_CAMPO));
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
            DialogoAlerta.sucesso(this, "Detalhes do veículo salvos com sucesso!", "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao salvar detalhes: " + ex.getMessage(), "Erro no Sistema");
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

    // =========================================================================
    // INNER CLASSES — RENDERERS DA TABELA
    // =========================================================================

    /** Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7). */
    private static class CabecalhoVidroClaro extends JLabel implements TableCellRenderer {

        CabecalhoVidroClaro() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(COR_AERO_TEXTO);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int meio = h / 2;

            g2.setPaint(new GradientPaint(0, 0, COR_AERO_TOPO_A, 0, meio, COR_AERO_TOPO_B));
            g2.fillRect(0, 0, w, meio);

            g2.setPaint(new GradientPaint(0, meio, COR_AERO_BASE_A, 0, h, COR_AERO_BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillRect(0, 0, w, Math.max(1, h / 5));

            g2.setColor(COR_AERO_SEPARA);
            g2.drawLine(w - 1, 3, w - 1, h - 4);

            g2.setColor(COR_AERO_BORDA);
            g2.drawLine(0, h - 1, w, h - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Células de dados sempre com fundo branco (e destaque no tom do tema quando selecionadas). */
    private static class CelulaBrancaRenderer extends DefaultTableCellRenderer {
        CelulaBrancaRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(isSelected ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
            setForeground(COR_TEXTO_CAMPO);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    // =========================================================================
    // INNER CLASSES — COMPONENTES EM VIDRO
    // =========================================================================

    /**
     * Campo de texto com efeito de vidro translúcido: cantos arredondados,
     * sombra leve por baixo, reflexo sutil no topo e borda que reage a foco.
     */
    private static class GlassTextField extends JTextField {
        private boolean focado = false;

        GlassTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Sombra leve por baixo do vidro
            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            // Preenchimento translúcido
            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 210),
                    0, h, new Color(255, 255, 255, 145)
            );
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            // Reflexo sutil no topo
            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130);
            float espessura = focado ? 1.6f : 1f;
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /** Painel com fundo em gradiente suave, harmonizando o cartão de serviços com o restante das telas. */
    private static class PainelGradiente extends JPanel {
        private final Color corTopo;
        private final Color corBase;

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase) {
            super(layout);
            this.corTopo = corTopo;
            this.corBase = corBase;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, corTopo, 0, getHeight(), corBase);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação com a mesma linguagem visual dos campos em vidro: cantos
     * arredondados, sombra suave, reflexo no topo e reação a hover/clique.
     * A cor é configurável (laranja para a ação principal, cinza para "Voltar").
     */
    private static class BotaoAcao extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Color corBase, Color corClara, Color corEscura) {
            super(texto);
            this.corBase = corBase;
            this.corClara = corClara;
            this.corEscura = corEscura;
            setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_BOTAO));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)   { sobreMouse = false; repaint(); }
                @Override public void mousePressed(MouseEvent e)  { pressionado = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corSombra = isEnabled() ? new Color(0, 0, 0, 40) : new Color(0, 0, 0, 15);
            g2.setColor(corSombra);
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento;
            if (!isEnabled()) {
                corPreenchimento = new Color(180, 185, 192);
            } else {
                corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            }
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}