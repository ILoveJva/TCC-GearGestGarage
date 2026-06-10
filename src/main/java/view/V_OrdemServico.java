package view;

import controller.OficinaController;
import model.OrdemDeServico;
import model.ItemServico;
import model.Peca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Painel de Conteúdo para Visualização Detalhada da Ordem de Serviço (OS).
 * Atualizado para exibir peças vinculadas exclusivamente na Etapa 2 e remover espaços sob a tabela.
 */
public class V_OrdemServico extends JPanel {

    private JPanel pnl_ConteudoPrincipal;
    private JLabel lbl_MapaNavegacao;
    private JButton btn_Voltar;

    private JLabel lbl_TituloServico;
    private JLabel lbl_StatusTexto;

    private JScrollPane scp_ScrollCorpo;
    private JPanel pnl_ContainerDivisoes;

    private OficinaController controller;
    private long idOS;

    public V_OrdemServico(OficinaController controller, long idOS) {
        this.controller = controller;
        this.idOS = idOS;

        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initComponents();
        layoutComponents();
        vincularEventosNavegacao();
        carregarDadosBaseadosNoItemServico();
    }

    private void initComponents() {
        lbl_MapaNavegacao = new JLabel("Página Inicial > Consultar Serviços > Detalhes do Serviço");
        lbl_MapaNavegacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_MapaNavegacao.setForeground(Color.decode("#4D4D4D"));

        btn_Voltar = new JButton("← Voltar para Lista");
        btn_Voltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Voltar.setForeground(Color.decode("#4D4D4D"));
        btn_Voltar.setContentAreaFilled(false);
        btn_Voltar.setBorderPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnl_ConteudoPrincipal = new JPanel();
        pnl_ConteudoPrincipal.setLayout(new BoxLayout(pnl_ConteudoPrincipal, BoxLayout.Y_AXIS));
        pnl_ConteudoPrincipal.setBackground(Color.WHITE);

        lbl_TituloServico = new JLabel("ORDEM DE SERVIÇO Nº " + String.format("%05d", idOS));
        lbl_TituloServico.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl_TituloServico.setForeground(Color.BLACK);

        lbl_StatusTexto = new JLabel("STATUS GERAL: CARREGANDO...");
        lbl_StatusTexto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl_StatusTexto.setForeground(Color.decode("#E67E22"));

        pnl_ContainerDivisoes = new JPanel();
        pnl_ContainerDivisoes.setLayout(new BoxLayout(pnl_ContainerDivisoes, BoxLayout.Y_AXIS));
        pnl_ContainerDivisoes.setBackground(Color.WHITE);

        scp_ScrollCorpo = new JScrollPane(pnl_ContainerDivisoes);
        scp_ScrollCorpo.setBorder(BorderFactory.createEmptyBorder());
        scp_ScrollCorpo.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void layoutComponents() {
        JPanel pnl_TopoNavegacao = new JPanel(new BorderLayout());
        pnl_TopoNavegacao.setBackground(Color.WHITE);
        pnl_TopoNavegacao.add(lbl_MapaNavegacao, BorderLayout.WEST);
        pnl_TopoNavegacao.add(btn_Voltar, BorderLayout.EAST);
        add(pnl_TopoNavegacao, BorderLayout.NORTH);

        pnl_ConteudoPrincipal.add(lbl_TituloServico);
        pnl_ConteudoPrincipal.add(Box.createVerticalStrut(4));
        pnl_ConteudoPrincipal.add(lbl_StatusTexto);
        pnl_ConteudoPrincipal.add(Box.createVerticalStrut(20));

        pnl_ConteudoPrincipal.add(scp_ScrollCorpo);
        add(pnl_ConteudoPrincipal, BorderLayout.CENTER);
    }

    private void carregarDadosBaseadosNoItemServico() {
        if (controller == null) return;

        OrdemDeServico os = controller.listarOS().get(1);
        if (os == null) return;

        if (os.getStatus() != null) {
            lbl_StatusTexto.setText("STATUS DA OS: " + os.getStatus().toString().toUpperCase());
        }

        String[] titulosEtapas = {
                "ETAPA 1: RECLAMAÇÃO DO CLIENTE",
                "ETAPA 2: DESCRIÇÃO DO PROBLEMA E PEÇAS",
                "ETAPA 3: RELATÓRIO DA TROCA",
                "ETAPA 4: REVISÃO GERAL"
        };

        for (int i = 0; i < 4; i++) {
            ItemServico item = null;
            if (os.getItensServico() != null && os.getItensServico().size() > i) {
                item = os.getItensServico().get(i);
            }

            JLabel lbl_EtapaTitulo = new JLabel(titulosEtapas[i]);
            lbl_EtapaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lbl_EtapaTitulo.setForeground(Color.decode("#2980B9"));
            pnl_ContainerDivisoes.add(lbl_EtapaTitulo);
            pnl_ContainerDivisoes.add(Box.createVerticalStrut(10));

            String descricao = "-";
            String statusItem = "-";
            String dataStr = "-";
            List<Peca> listaPecas = null;

            if (item != null) {
                if (item.getDescricao() != null && !item.getDescricao().trim().isEmpty()) {
                    descricao = item.getDescricao();
                }
                if (item.getStatus() != null) statusItem = item.getStatus().name();
                if (item.getDataRealizacao() != null) {
                    dataStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(item.getDataRealizacao());
                }
                listaPecas = item.getListaPecas();
            }

            pnl_ContainerDivisoes.add(criarCardDivisao("Informações do Item de Serviço", "Descrição Técnica", descricao));
            pnl_ContainerDivisoes.add(Box.createVerticalStrut(10));

            pnl_ContainerDivisoes.add(criarCardDivisao("Acompanhamento", "Dados Temporais", "Status Atual: " + statusItem + "\nData: " + dataStr));

            // Condicional modificada: Insere o painel de peças estritamente na ETAPA 2 (índice 1)
            if (i == 1) {
                pnl_ContainerDivisoes.add(Box.createVerticalStrut(10));
                pnl_ContainerDivisoes.add(criarPainelDePecas(listaPecas));
                pnl_ContainerDivisoes.add(Box.createVerticalStrut(15));
            } else {
                pnl_ContainerDivisoes.add(Box.createVerticalStrut(15));
            }

            if (i < 3) {
                JSeparator separator = new JSeparator();
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                separator.setForeground(Color.decode("#E0E0E0"));
                pnl_ContainerDivisoes.add(separator);
                pnl_ContainerDivisoes.add(Box.createVerticalStrut(20));
            }
        }
    }

    private JPanel criarPainelDePecas(List<Peca> pecas) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);

        String[] colunas = {"ID Peça", "Fabricante", "Descrição", "Valor Unit."};
        DefaultTableModel mdl_Pecas = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        if (pecas != null && !pecas.isEmpty()) {
            for (Peca p : pecas) {
                mdl_Pecas.addRow(new Object[]{p.getIdPeca(),p.getFabricantePeca().getNome() ,p.getNome(), String.format("R$ %.2f", p.getValor())});
            }
        } else {
            mdl_Pecas.addRow(new Object[]{"-", "-", "-", "-"});
        }

        JTable tbl_Pecas = new JTable(mdl_Pecas);
        tbl_Pecas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Pecas.setRowHeight(25);
        tbl_Pecas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scp = new JScrollPane(tbl_Pecas);

        // Ajuste dinâmico de tamanho para eliminar o espaço em branco abaixo da tabela
        int rowCount = mdl_Pecas.getRowCount();
        int calcHeight = (rowCount * 25) + 45;
        scp.setPreferredSize(new Dimension(600, Math.min(calcHeight, 130)));
        scp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")), "Peças Vinculadas"));

        pnl.add(scp, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel criarCardDivisao(String titulo, String subtitulo, String texto) {
        JPanel pnl_Card = new JPanel();
        pnl_Card.setLayout(new BoxLayout(pnl_Card, BoxLayout.Y_AXIS));
        pnl_Card.setBackground(Color.decode("#F9F9F9"));
        pnl_Card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E5E5E5"), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lbl_Titulo = new JLabel(titulo);
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.BLACK);
        pnl_Card.add(lbl_Titulo);
        pnl_Card.add(Box.createVerticalStrut(4));

        JLabel lbl_Subtitulo = new JLabel(subtitulo);
        lbl_Subtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl_Subtitulo.setForeground(Color.GRAY);
        pnl_Card.add(lbl_Subtitulo);
        pnl_Card.add(Box.createVerticalStrut(10));

        JTextArea txa_Texto = new JTextArea(texto);
        txa_Texto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txa_Texto.setForeground(Color.decode("#2C3E50"));
        txa_Texto.setLineWrap(true);
        txa_Texto.setWrapStyleWord(true);
        txa_Texto.setEditable(false);
        txa_Texto.setBackground(Color.decode("#F9F9F9"));

        pnl_Card.add(txa_Texto);
        return pnl_Card;
    }

    private void vincularEventosNavegacao() {
        btn_Voltar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof V_Main) {
                ((V_Main) window).atualizarConteudo(new V_VisualizarServicos(controller));
            }
        });
    }
}