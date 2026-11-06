package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Serviços (OS) dos veículos do cliente, alternando entre Em andamento e Finalizados. */
public class V_ClienteServicos extends JPanel {

    private final OficinaController controller;
    private final Map<Long, String> placas = new HashMap<>();
    private JPanel pnlLista;
    private boolean mostrandoFinalizados;
    private JButton btnAndamento, btnFinalizados;

    public V_ClienteServicos(OficinaController controller) {
        this(controller, false);
    }

    /** @param finalizados abre direto na aba "Finalizados" (atalho "Consultar Serviços Antigos"). */
    public V_ClienteServicos(OficinaController controller, boolean finalizados) {
        this.controller = controller;
        this.mostrandoFinalizados = finalizados;
        for (Veiculo v : controller.listarMeusVeiculos()) placas.put(v.getIdVeiculo(), v.getPlaca());

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel topo = new JPanel(new BorderLayout(0, 10));
        topo.setOpaque(false);

        JLabel titulo = new JLabel("Serviços");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topo.add(titulo, BorderLayout.NORTH);

        JPanel toggle = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toggle.setOpaque(false);
        btnAndamento = new JButton("Em andamento");
        btnFinalizados = new JButton("Finalizados");
        btnAndamento.addActionListener(e -> { mostrandoFinalizados = false; atualizarLista(); });
        btnFinalizados.addActionListener(e -> { mostrandoFinalizados = true; atualizarLista(); });
        toggle.add(btnAndamento);
        toggle.add(btnFinalizados);
        topo.add(toggle, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);

        pnlLista = new JPanel();
        pnlLista.setLayout(new BoxLayout(pnlLista, BoxLayout.Y_AXIS));
        pnlLista.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(pnlLista);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        atualizarLista();
    }

    private void atualizarLista() {
        btnAndamento.setFont(new Font("Segoe UI", mostrandoFinalizados ? Font.PLAIN : Font.BOLD, 13));
        btnFinalizados.setFont(new Font("Segoe UI", mostrandoFinalizados ? Font.BOLD : Font.PLAIN, 13));

        pnlLista.removeAll();
        List<ServicoResponseDTO> servicos = controller.listarMeusServicos().stream()
            .filter(s -> mostrandoFinalizados == "CONCLUIDA".equalsIgnoreCase(s.status()))
            .toList();

        if (servicos.isEmpty()) {
            JLabel vazio = new JLabel(mostrandoFinalizados
                ? "Nenhum serviço finalizado ainda." : "Nenhum serviço em andamento.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pnlLista.add(vazio);
        } else {
            for (ServicoResponseDTO s : servicos) {
                pnlLista.add(criarCartao(s));
                pnlLista.add(Box.createVerticalStrut(10));
            }
        }
        pnlLista.revalidate();
        pnlLista.repaint();
    }

    private JPanel criarCartao(ServicoResponseDTO s) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.decode("#F7F7F7"));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        String placa = placas.getOrDefault(s.idVeiculo(), "—");
        JLabel cabecalho = new JLabel("<html><b>" + s.titulo() + "</b> — " + placa
            + " &nbsp;&nbsp; Status: " + traduzirStatus(s.status()) + "</html>");
        cabecalho.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(cabecalho);

        if (s.itens() != null && !s.itens().isEmpty()) {
            card.add(Box.createVerticalStrut(6));
            for (var item : s.itens()) {
                String marca = etapaConcluida(item.status()) ? "✓" : "○";
                JLabel linha = new JLabel(marca + " " + item.descricao());
                linha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                linha.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(linha);
            }
        }

        if (mostrandoFinalizados) {
            card.add(Box.createVerticalStrut(8));
            JButton btnExportar = new JButton("Exportar relatório (.txt)");
            btnExportar.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnExportar.addActionListener(e -> exportar(s, placa));
            card.add(btnExportar);
        }

        return card;
    }

    private void exportar(ServicoResponseDTO s, String placa) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("servico_" + s.codigo() + ".txt"));
        int resultado = chooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter w = new PrintWriter(chooser.getSelectedFile(), "UTF-8")) {
            w.println("Relatório de Serviço");
            w.println("Código: " + s.codigo());
            w.println("Título: " + s.titulo());
            w.println("Veículo: " + placa);
            w.println("Data: " + s.dataServico());
            w.println("Status: " + traduzirStatus(s.status()));
            w.println();
            w.println("Checklist:");
            if (s.itens() != null)
                for (var item : s.itens())
                    w.println("  [" + (etapaConcluida(item.status()) ? "x" : " ") + "] " + item.descricao());
            DialogoAlerta.sucesso(this, "Relatório exportado com sucesso!", "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao exportar: " + ex.getMessage(), "Erro");
        }
    }

    /** Etapas são gravadas como CONCLUIDO pelo sistema, mas há dados antigos com CONCLUIDA. */
    private static boolean etapaConcluida(String status) {
        return "CONCLUIDO".equalsIgnoreCase(status) || "CONCLUIDA".equalsIgnoreCase(status);
    }

    private String traduzirStatus(String status) {
        if (status == null) return "—";
        return switch (status.toUpperCase()) {
            case "ABERTA" -> "Aberta";
            case "EM_ANDAMENTO" -> "Em andamento";
            case "CONCLUIDA" -> "Finalizado";
            case "CONGELADA" -> "Aguardando novo orçamento";
            default -> status;
        };
    }
}
