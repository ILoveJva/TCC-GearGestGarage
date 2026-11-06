package view;

import br.com.oficina.estoque.CatalogoPecaEntity;
import br.com.oficina.estoque.MovimentacaoEstoqueEntity;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;
import model.Oficina;

import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static view.RtfUtil.*;

/**
 * Relatórios do estoque em .rtf (abre no Word, margens/fonte ABNT), no mesmo padrão
 * de {@link ExportadorOrcamento} e {@link ExportadorFichaTarefas}:
 * <ul>
 *   <li>{@link #exportarEstoque} — posição atual: data/hora de emissão e cada peça
 *       com quantidade, valor e situação (sem as movimentações);</li>
 *   <li>{@link #exportarMovimentacoes} — histórico de entradas e saídas.</li>
 * </ul>
 */
public final class ExportadorEstoque {

    private static final DateTimeFormatter EMISSAO = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    private static final DateTimeFormatter ARQUIVO = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    private ExportadorEstoque() {}

    /** Mesma regra da coluna "Situação" da tela de Estoque. */
    static String situacao(int qtd) {
        if (qtd <= 0) return "Sem estoque";
        if (qtd <= 2) return "Baixo";
        return "OK";
    }

    // =========================================================================
    // RELATÓRIO DE ESTOQUE (posição atual)
    // =========================================================================

    public static void exportarEstoque(Component pai, OficinaController controller) {
        LocalDateTime agora = LocalDateTime.now();
        RtfUtil.salvar(pai, "relatorio_estoque_" + agora.format(ARQUIVO) + ".rtf",
            () -> gerarEstoque(controller, agora));
    }

    private static String gerarEstoque(OficinaController controller, LocalDateTime emissao) {
        StringBuilder rtf = new StringBuilder(PREAMBULO);
        cabecalho(rtf, controller, "RELATÓRIO DE ESTOQUE", emissao);

        List<CatalogoPecaEntity> catalogo = controller.listarEstoque();
        if (catalogo.isEmpty()) {
            rtf.append(escapar("Nenhuma peça cadastrada no estoque.")).append("\\par\n");
            rtf.append(FECHAMENTO);
            return rtf.toString();
        }

        // Largura útil da página A4 com margens ABNT: 9071 twips (16 cm).
        int[] larguras = {2268, 1984, 1474, 737, 1191, 1417};
        tabela(rtf, new String[]{"Peça", "Nome técnico", "Fabricante", "Qtd.", "Valor unit.", "Valor em estoque"},
            larguras, true);

        int itens = 0, unidades = 0, semEstoque = 0, baixo = 0;
        double valorTotal = 0;
        for (CatalogoPecaEntity cat : catalogo) {
            List<PecaEntity> reais = controller.listarPecasReaisDoCatalogo(cat.getIdCatalogoPeca());
            int qtdCatalogo = 0;
            for (PecaEntity r : reais) qtdCatalogo += r.getQuantidadeEstoque();
            String sit = situacao(qtdCatalogo);
            if (qtdCatalogo <= 0) semEstoque++; else if (qtdCatalogo <= 2) baixo++;

            String nomeCat = cat.getNomePopular() + " (" + cat.getSistemaLabel() + ") — " + sit;
            if (reais.isEmpty()) {
                tabela(rtf, new String[]{nomeCat, "—", "—", "0", "—", "—"}, larguras, false);
                itens++;
                continue;
            }
            boolean primeira = true;
            for (PecaEntity r : reais) {
                int qtd = r.getQuantidadeEstoque();
                double subtotal = qtd * r.getValor();
                tabela(rtf, new String[]{
                    primeira ? nomeCat : "",
                    valorOuTraco(r.getNomeTecnico()),
                    valorOuTraco(r.getFabricante()),
                    String.valueOf(qtd),
                    formatarValor(r.getValor()),
                    formatarValor(subtotal)
                }, larguras, false);
                primeira = false;
                itens++;
                unidades += Math.max(qtd, 0);
                valorTotal += Math.max(subtotal, 0);
            }
        }
        rtf.append("\\pard\\par\n");

        rtf.append("\\b\\fs26 ").append(escapar("Resumo")).append("\\b0\\fs24\\par\n");
        linha(rtf, "Tipos de peça (catálogo): ", String.valueOf(catalogo.size()));
        linha(rtf, "Itens (peças por fabricante): ", String.valueOf(itens));
        linha(rtf, "Unidades em estoque: ", String.valueOf(unidades));
        linha(rtf, "Peças sem estoque: ", String.valueOf(semEstoque));
        linha(rtf, "Peças com estoque baixo (até 2 un.): ", String.valueOf(baixo));
        rtf.append("\\par\\qr\\fs28\\b ").append(escapar("VALOR TOTAL EM ESTOQUE: " + formatarValor(valorTotal)))
           .append("\\b0\\fs24\\par\\pard\n");

        assinatura(rtf);
        rtf.append(FECHAMENTO);
        return rtf.toString();
    }

    // =========================================================================
    // RELATÓRIO DE MOVIMENTAÇÕES (entradas e saídas)
    // =========================================================================

    public static void exportarMovimentacoes(Component pai, OficinaController controller) {
        LocalDateTime agora = LocalDateTime.now();
        RtfUtil.salvar(pai, "relatorio_movimentacoes_" + agora.format(ARQUIVO) + ".rtf",
            () -> gerarMovimentacoes(controller, agora));
    }

    private static String gerarMovimentacoes(OficinaController controller, LocalDateTime emissao) {
        StringBuilder rtf = new StringBuilder(PREAMBULO);
        cabecalho(rtf, controller, "RELATÓRIO DE MOVIMENTAÇÕES DE ESTOQUE", emissao);

        List<MovimentacaoEstoqueEntity> movs = controller.listarMovimentacoesEstoque();
        if (movs.isEmpty()) {
            rtf.append(escapar("Nenhuma movimentação registrada.")).append("\\par\n");
            rtf.append(FECHAMENTO);
            return rtf.toString();
        }

        Map<Long, String> nomePeca = nomesDasPecas(controller);

        int[] larguras = {1400, 2228, 964, 624, 964, 1134, 1757};
        tabela(rtf, new String[]{"Data", "Peça", "Tipo", "Qtd.", "Origem", "Valor", "Observação"}, larguras, true);

        int entradas = 0, saidas = 0, unEntrada = 0, unSaida = 0;
        double totalCompras = 0;
        for (MovimentacaoEstoqueEntity m : movs) {
            tabela(rtf, new String[]{
                formatarData(m.getDataMovimentacao()),
                nomePeca.getOrDefault(m.getIdPeca(), "Peça #" + m.getIdPeca()),
                m.getTipoLabel(),
                (m.isEntrada() ? "+" : "-") + m.getQuantidade(),
                m.getOrigemLabel(),
                m.getValorFormatado(),
                valorOuTraco(m.getObservacao())
            }, larguras, false);
            if (m.isEntrada()) {
                entradas++;
                unEntrada += m.getQuantidade();
                if (m.getValor() != null) totalCompras += m.getValor();
            } else {
                saidas++;
                unSaida += m.getQuantidade();
            }
        }
        rtf.append("\\pard\\par\n");

        rtf.append("\\b\\fs26 ").append(escapar("Resumo")).append("\\b0\\fs24\\par\n");
        linha(rtf, "Movimentações: ", String.valueOf(movs.size()));
        linha(rtf, "Entradas: ", entradas + " (" + unEntrada + " unidade(s))");
        linha(rtf, "Saídas: ", saidas + " (" + unSaida + " unidade(s))");
        rtf.append("\\par\\qr\\fs28\\b ").append(escapar("TOTAL EM COMPRAS: " + formatarValor(totalCompras)))
           .append("\\b0\\fs24\\par\\pard\n");

        assinatura(rtf);
        rtf.append(FECHAMENTO);
        return rtf.toString();
    }

    // =========================================================================
    // AUXILIARES
    // =========================================================================

    /** "Pastilha de freio — Cobreq N-123", igual ao nome exibido na tela de Estoque. */
    static Map<Long, String> nomesDasPecas(OficinaController controller) {
        Map<Long, String> nomes = new HashMap<>();
        for (CatalogoPecaEntity cat : controller.listarEstoque()) {
            for (PecaEntity real : controller.listarPecasReaisDoCatalogo(cat.getIdCatalogoPeca())) {
                String tecnico = real.getNomeTecnico();
                nomes.put(real.getIdPeca(), (tecnico != null && !tecnico.isBlank())
                    ? cat.getNomePopular() + " — " + tecnico : cat.getNomePopular());
            }
        }
        return nomes;
    }

    private static void cabecalho(StringBuilder rtf, OficinaController controller, String titulo,
                                  LocalDateTime emissao) {
        Oficina oficina = controller.getOficina();
        rtf.append("\\qc\\b\\fs28 ").append(escapar(titulo)).append("\\b0\\fs24\\par\n");
        if (oficina != null) {
            rtf.append("\\qc ").append(escapar(oficina.getNome())).append("\\par\n");
            if (oficina.getEndereco() != null && !oficina.getEndereco().isBlank())
                rtf.append("\\qc ").append(escapar(oficina.getEndereco())).append("\\par\n");
            rtf.append("\\qc ").append(escapar("Telefone: " + valorOuTraco(oficina.getTelefone())
                + "   CNPJ: " + valorOuTraco(oficina.getCnpj()))).append("\\par\n");
        }
        rtf.append("\\par\n\\pard\\ql\n");
        linha(rtf, "Emitido em: ", emissao.format(EMISSAO));
        rtf.append("\\par\n");
    }

    private static void linha(StringBuilder rtf, String rotulo, String valor) {
        rtf.append("\\b ").append(escapar(rotulo)).append("\\b0 ").append(escapar(valor)).append("\\par\n");
    }

    private static void assinatura(StringBuilder rtf) {
        rtf.append("\\par\\par\\par\n");
        rtf.append("\\qc ").append(escapar("_________________________________________")).append("\\par\n");
        rtf.append("\\qc ").append(escapar("Responsável pelo Estoque")).append("\\par\\pard\n");
    }

    private static String formatarData(String iso) {
        if (iso == null || iso.isBlank()) return "—";
        try {
            return LocalDate.parse(iso.trim()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return iso;
        }
    }
}
