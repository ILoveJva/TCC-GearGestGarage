package view;

import controller.OficinaController;
import model.OrdemDeServico;
import model.Veiculo;
import model.Funcionario;
import model.ItemServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Painel de Conteúdo para a Listagem Geral de Serviços.
 * Atualizado para exibir as 4 etapas obrigatoriamente com lógica de responsáveis individuais.
 */
public class V_VisualizarServicos extends JPanel {

    private final OficinaController controller;

    private JPanel pnl_CardCentral;
    private JLabel lbl_MapaNavegacao;
    private JButton btn_Filtros;

    private JPanel pnl_ContainerCartoes;
    private JScrollPane scp_ScrollServicos;

    public V_VisualizarServicos(OficinaController controller) {
        this.controller = controller;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        initComponents();
        layoutComponents();
        vincularEventos();
        carregarDadosServicos();
    }

    private void initComponents() {
        pnl_CardCentral = new JPanel(new BorderLayout(0, 15));
        pnl_CardCentral.setBackground(Color.WHITE);
        pnl_CardCentral.setPreferredSize(new Dimension(800, 550));
        pnl_CardCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E0E0E0"), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel pnl_HeaderCard = new JPanel(new BorderLayout());
        pnl_HeaderCard.setOpaque(false);

        lbl_MapaNavegacao = new JLabel("Inicio > Consultar Serviços");
        lbl_MapaNavegacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_MapaNavegacao.setForeground(Color.decode("#4D4D4D"));
        pnl_HeaderCard.add(lbl_MapaNavegacao, BorderLayout.WEST);

        btn_Filtros = new JButton("Filtrar 🔍");
        btn_Filtros.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_Filtros.setForeground(Color.decode("#4D4D4D"));
        btn_Filtros.setBackground(Color.WHITE);
        btn_Filtros.setFocusPainted(false);
        btn_Filtros.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        pnl_HeaderCard.add(btn_Filtros, BorderLayout.EAST);

        pnl_CardCentral.add(pnl_HeaderCard, BorderLayout.NORTH);

        pnl_ContainerCartoes = new JPanel();
        pnl_ContainerCartoes.setLayout(new BoxLayout(pnl_ContainerCartoes, BoxLayout.Y_AXIS));
        pnl_ContainerCartoes.setBackground(Color.WHITE);

        scp_ScrollServicos = new JScrollPane(pnl_ContainerCartoes);
        scp_ScrollServicos.setBorder(BorderFactory.createEmptyBorder());
        scp_ScrollServicos.getVerticalScrollBar().setUnitIncrement(16);
        pnl_CardCentral.add(scp_ScrollServicos, BorderLayout.CENTER);
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(pnl_CardCentral, gbc);
    }

    private void carregarDadosServicos() {
        pnl_ContainerCartoes.removeAll();
        if (controller == null) return;

        List<OrdemDeServico> listaOS = controller.listarOS();
        if (listaOS == null || listaOS.isEmpty()) {
            JLabel lblVazio = new JLabel("Nenhuma Ordem de Serviço encontrada.");
            lblVazio.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            pnl_ContainerCartoes.add(lblVazio);
            return;
        }

        for (OrdemDeServico os : listaOS) {
            pnl_ContainerCartoes.add(criarCartaoExpansivel(os));
            pnl_ContainerCartoes.add(Box.createVerticalStrut(15));
        }

        // Adiciona um espaçador vertical colado no fundo para empurrar os cartões para cima
        pnl_ContainerCartoes.add(Box.createVerticalGlue());

        pnl_ContainerCartoes.revalidate();
        pnl_ContainerCartoes.repaint();
    }

    private JPanel criarCartaoExpansivel(OrdemDeServico os) {
        JPanel pnl_Cartao = new JPanel(new BorderLayout());
        pnl_Cartao.setBackground(Color.decode("#9A9A9A"));
        pnl_Cartao.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Define apenas uma altura padrão inicial flexível para o BoxLayout
        pnl_Cartao.setPreferredSize(new Dimension(760, 75));
        pnl_Cartao.setMinimumSize(new Dimension(500, 75));
        pnl_Cartao.setMaximumSize(new Dimension(800, 75));

        String tituloDescricao = "Serviço sem descrição";
        if (os != null && os.getItensServico() != null && !os.getItensServico().isEmpty() && os.getItensServico().get(0) != null) {
            String desc = os.getTitulo();
            if (desc != null && !desc.isEmpty()) tituloDescricao = desc;
        }

        Veiculo v = os.getVeiculo();
        String subtituloVeiculo = v != null ? v.getModelo().getMontadora().getNome() + " " + v.getModelo().getNome() + " " + v.getPlaca() : "Veículo não especificado";

        JPanel pnl_Header = new JPanel(new BorderLayout());
        pnl_Header.setOpaque(false);

        JPanel pnl_Titulo = new JPanel();
        pnl_Titulo.setLayout(new BoxLayout(pnl_Titulo, BoxLayout.Y_AXIS));
        pnl_Titulo.setOpaque(false);

        JLabel lbl_Titulo = new JLabel(tituloDescricao);
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl_Titulo.setForeground(Color.BLACK);

        JLabel lbl_Subtitulo = new JLabel("   " + subtituloVeiculo);
        lbl_Subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl_Subtitulo.setForeground(Color.DARK_GRAY);

        pnl_Titulo.add(lbl_Titulo);
        pnl_Titulo.add(lbl_Subtitulo);
        pnl_Header.add(pnl_Titulo, BorderLayout.CENTER);

        JButton btn_Expandir = new JButton("▼");
        btn_Expandir.setOpaque(false);
        btn_Expandir.setContentAreaFilled(false);
        btn_Expandir.setBorderPainted(false);
        btn_Expandir.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn_Expandir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnl_Header.add(btn_Expandir, BorderLayout.EAST);

        JPanel pnl_Detalhes = new JPanel(new BorderLayout());
        pnl_Detalhes.setOpaque(false);
        pnl_Detalhes.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        pnl_Detalhes.setVisible(false);

        String[] colunas = {"Etapa", "Responsável", "Status", "Data", "Ação"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        String[] nomesEtapas = {
                "1. Reclame do Cliente",
                "2. Problema / Peças",
                "3. Relatório de Troca",
                "4. Revisão Geral"
        };

        for (int i = 0; i < 4; i++) {
            ItemServico item = null;
            if (os.getItensServico() != null && os.getItensServico().size() > i) {
                item = os.getItensServico().get(i);
            }

            String responsavelEtapa = "-";
            String status = "-";
            String dataStr = "-";

            if (item != null) {
                if (item.getResponsavel() != null) {
                    responsavelEtapa = item.getResponsavel().getNome();
                } else if (item.getResponsavel() != null) {
                    responsavelEtapa = os.getResponsavel().getNome();
                }

                if (item.getStatus() != null) status = item.getStatus().name();
                if (item.getDataRealizacao() != null) {
                    dataStr = new SimpleDateFormat("dd/MM/yyyy").format(item.getDataRealizacao());
                }
            }
            model.addRow(new Object[]{nomesEtapas[i], responsavelEtapa, status, dataStr, "ACESSAR ↗"});
        }

        JTable tbl_Mini = new JTable(model);
        tbl_Mini.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Mini.setRowHeight(30);
        tbl_Mini.setBackground(Color.decode("#C0C0C0"));
        tbl_Mini.getTableHeader().setBackground(Color.decode("#9A9A9A"));
        tbl_Mini.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // CORREÇÃO: Força o view container a se ajustar exatamente ao tamanho das linhas, tirando o fundo vazio do scroll da tabela
        tbl_Mini.setPreferredScrollableViewportSize(tbl_Mini.getPreferredSize());
        tbl_Mini.setFillsViewportHeight(true);

        tbl_Mini.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tbl_Mini.columnAtPoint(e.getPoint());
                if (col == 4) {
                    navegarPara(new V_OrdemServico(controller, os.getIdOrdemDeServico()));
                }
            }
        });

        JScrollPane scp_Tabela = new JScrollPane(tbl_Mini);
        scp_Tabela.setBorder(BorderFactory.createEmptyBorder());
        pnl_Detalhes.add(scp_Tabela, BorderLayout.CENTER);

        btn_Expandir.addActionListener(e -> {
            boolean vaiExpandir = !pnl_Detalhes.isVisible();
            pnl_Detalhes.setVisible(vaiExpandir);
            btn_Expandir.setText(vaiExpandir ? "▲" : "▼");

            if (vaiExpandir) {
                // CORREÇÃO: Ajusta dinamicamente a altura máxima com base no tamanho real da tabela (linhas + cabeçalho) + cabeçalho do cartão
                int alturaTabela = tbl_Mini.getPreferredSize().height + tbl_Mini.getTableHeader().getPreferredSize().height;
                int novaAltura = 75 + alturaTabela + 10; // 75 base + tabela + margens do painel

                pnl_Cartao.setPreferredSize(new Dimension(760, novaAltura));
                pnl_Cartao.setMaximumSize(new Dimension(800, novaAltura));
            } else {
                pnl_Cartao.setPreferredSize(new Dimension(760, 75));
                pnl_Cartao.setMaximumSize(new Dimension(800, 75));
            }

            pnl_ContainerCartoes.revalidate();
            pnl_ContainerCartoes.repaint();
        });

        pnl_Cartao.add(pnl_Header, BorderLayout.NORTH);
        pnl_Cartao.add(pnl_Detalhes, BorderLayout.CENTER);

        return pnl_Cartao;
    }

    private void vincularEventos() {
        btn_Filtros.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            V_FiltroServico filtroDialog;
            if (parentWindow instanceof JFrame) {
                filtroDialog = new V_FiltroServico((JFrame) parentWindow, true);
            } else {
                filtroDialog = new V_FiltroServico((JDialog) parentWindow, true);
            }
            filtroDialog.setLocationRelativeTo(parentWindow);
            filtroDialog.setVisible(true);
        });
    }

    private void navegarPara(JPanel novoPainelCentral) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof V_Main) {
            ((V_Main) window).atualizarConteudo(novoPainelCentral);
        }
    }
}