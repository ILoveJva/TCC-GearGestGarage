package view;

import br.com.oficina.estoque.PecaEntity;

import javax.swing.*;
import java.awt.Component;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Helpers compartilhados pelos exportadores de documento (.rtf — abre nativamente
 * no Word/LibreOffice) usados nas telas de orçamento e de ordem de serviço.
 * Página A4 com margens ABNT (3 cm esquerda/topo, 2 cm direita/base), Times New
 * Roman 12 (1 cm ≈ 566,93 twips).
 */
final class RtfUtil {

    static final String PREAMBULO =
        "{\\rtf1\\ansi\\ansicpg1252\\deff0\\uc1{\\fonttbl{\\f0 Times New Roman;}}"
        + "{\\colortbl;\\red230\\green230\\blue230;}"
        + "\\paperw11906\\paperh16838\\margl1701\\margr1134\\margt1701\\margb1134\\f0\\fs24\n";
    static final String FECHAMENTO = "}";

    private RtfUtil() {}

    /** Abre "Salvar como" e grava o .rtf gerado por {@code gerador} no arquivo escolhido. */
    static void salvar(Component pai, String nomeSugerido, Supplier<String> gerador) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(nomeSugerido));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Documento RTF (Word)", "rtf"));

        int resultado = chooser.showSaveDialog(pai);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        File destino = chooser.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".rtf"))
            destino = new File(destino.getParentFile(), destino.getName() + ".rtf");

        try {
            String conteudo = gerador.get();
            try (PrintWriter w = new PrintWriter(destino, StandardCharsets.US_ASCII)) {
                w.print(conteudo);
            }
            DialogoAlerta.sucesso(pai, "Documento exportado com sucesso!", "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(pai, "Erro ao exportar documento: " + ex.getMessage(), "Erro");
        }
    }

    /** Borda simples fina nos quatro lados de uma célula. */
    private static final String BORDA_CELULA =
        "\\clbrdrt\\brdrs\\brdrw10\\clbrdrl\\brdrs\\brdrw10\\clbrdrb\\brdrs\\brdrw10\\clbrdrr\\brdrs\\brdrw10";

    /**
     * Escreve uma linha de tabela RTF; larguras em twips (somadas a partir da margem esquerda).
     * A linha de cabeçalho tem fundo cinza e é marcada para repetir a cada página (\\trhdr — o
     * Word respeita; o LibreOffice ignora ao importar RTF).
     */
    static void tabela(StringBuilder rtf, String[] celulas, int[] largurasTwips, boolean cabecalho) {
        rtf.append("\\trowd\\trgaph80\\trleft0");
        if (cabecalho) rtf.append("\\trhdr");
        int acumulado = 0;
        for (int largura : largurasTwips) {
            acumulado += largura;
            rtf.append(BORDA_CELULA);
            if (cabecalho) rtf.append("\\clcbpat1\\clshdng0");
            rtf.append("\\cellx").append(acumulado);
        }
        for (String texto : celulas) {
            rtf.append("\\pard\\intbl");
            if (cabecalho) rtf.append("\\b");
            rtf.append(" ").append(escapar(texto));
            if (cabecalho) rtf.append("\\b0");
            rtf.append("\\cell");
        }
        rtf.append("\\row\n");
    }

    /** Uma peça já agrupada (mesma peça+nome técnico+fabricante) com a quantidade necessária. */
    record PecaAgrupada(String nome, String nomeTecnico, String fabricante, int quantidade, double valorUnit) {}

    /** Agrupa a lista bruta de {@code listarPecasOrcamentoComValor} (uma linha por unidade) por peça. */
    static List<PecaAgrupada> agruparPecas(List<Object[]> pecasBrutas) {
        Map<String, PecaAgrupada> porChave = new LinkedHashMap<>();
        for (Object[] triple : pecasBrutas) {
            PecaEntity p = (PecaEntity) triple[0];
            double valor = (Double) triple[1];
            String nomeTecnico = triple.length > 2 ? (String) triple[2] : "";
            String fabricante = triple.length > 3 ? (String) triple[3] : "";
            String chave = p.getNomeExibicao() + "|" + nomeTecnico + "|" + fabricante;
            PecaAgrupada atual = porChave.get(chave);
            if (atual == null) {
                porChave.put(chave, new PecaAgrupada(p.getNomeExibicao(),
                    nomeTecnico.isBlank() ? "—" : nomeTecnico, fabricante.isBlank() ? "—" : fabricante, 1, valor));
            } else {
                porChave.put(chave, new PecaAgrupada(atual.nome(), atual.nomeTecnico(), atual.fabricante(),
                    atual.quantidade() + 1, atual.valorUnit()));
            }
        }
        return List.copyOf(porChave.values());
    }

    static String formatarValor(double valor) {
        return String.format("R$ %.2f", valor);
    }

    static String valorOuTraco(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    /** Escapa caracteres de controle do RTF e converte não-ASCII em \\uN (Unicode RTF). */
    static String escapar(String texto) {
        if (texto == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '\\' || c == '{' || c == '}') {
                sb.append('\\').append(c);
            } else if (c == '\n') {
                sb.append("\\par ");
            } else if (c > 127) {
                // \\uN do RTF é um inteiro de 16 bits COM sinal: acima de 32767 vira negativo.
                sb.append("\\u").append((int) (short) c).append("?");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
