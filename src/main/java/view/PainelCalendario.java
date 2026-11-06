package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

/**
 * Agenda / calendário simples da oficina, embutido na Página Inicial.
 *
 * Mostra as Ordens de Serviço já cadastradas (pela data do serviço) e foi
 * preparado para, no futuro, exibir também os prazos de orçamentos — basta
 * implementar {@link #carregarPrazosOrcamento()} (hoje retorna lista vazia).
 *
 * Cinco modos de visualização: Dia, Semana, Mês, 3 Meses e Ano.
 * A semana começa no domingo (padrão dos calendários brasileiros).
 *
 * <p><b>Modo cliente</b> ({@link #paraCliente(OficinaController)}): mesma interface,
 * mas mostra só os processos do cliente logado — solicitação de orçamento, início
 * de cada OS, etapas concluídas e a data estimada da próxima revisão (validade em
 * meses dos serviços do catálogo). Clicar num dia abre o modo "Dia" e clicar num
 * evento expande seus detalhes em vez de abrir a OS (tela exclusiva da oficina).
 */
public class PainelCalendario extends JPanel {

    // ---- Paleta ----
    private static final Color COR_ACENTO   = Color.decode("#FF9900");
    private static final Color COR_TEXTO    = Color.decode("#333333");
    private static final Color COR_SUAVE    = Color.decode("#888888");
    private static final Color COR_BORDA    = Color.decode("#EEEEEE");
    private static final Color COR_ABERTA    = Color.decode("#E74C3C");
    private static final Color COR_ANDAMENTO = Color.decode("#F39C12");
    private static final Color COR_CONCLUIDA = Color.decode("#27AE60");
    private static final Color COR_PRAZO     = Color.decode("#8E44AD"); // prazos/solicitações de orçamento
    private static final Color COR_ETAPA     = Color.decode("#16A085"); // etapa da OS concluída (modo cliente)
    private static final Color COR_REVISAO   = Color.decode("#2980B9"); // próxima revisão estimada (modo cliente)
    private static final Color COR_CONGELADA = Color.decode("#7F8C8D"); // OS aguardando novo orçamento

    private static final Locale PT_BR = new Locale("pt", "BR");

    /** Modos de visualização e o "passo" de navegação de cada um. */
    private enum Modo {
        DIA("Dia"), SEMANA("Semana"), MES("Mês"), TRIMESTRE("3 Meses"), ANO("Ano");
        final String rotulo;
        Modo(String rotulo) { this.rotulo = rotulo; }
    }

    /** Um item na agenda. {@code idOS <= 0} quando não há OS navegável (ex.: futuro prazo de orçamento). */
    private static final class EventoAgenda {
        enum Tipo { SERVICO, PRAZO_ORCAMENTO, ORCAMENTO, ETAPA, PREVISAO_REVISAO }
        final LocalDate data;
        final String titulo;
        final String detalhe;
        final String statusLabel;
        final Color cor;
        final long idOS;
        final Tipo tipo;
        /** Linhas exibidas quando o evento é expandido (modo cliente). */
        final List<String> linhasExpandidas;
        EventoAgenda(LocalDate data, String titulo, String detalhe, String statusLabel,
                     Color cor, long idOS, Tipo tipo) {
            this(data, titulo, detalhe, statusLabel, cor, idOS, tipo, Collections.emptyList());
        }
        EventoAgenda(LocalDate data, String titulo, String detalhe, String statusLabel,
                     Color cor, long idOS, Tipo tipo, List<String> linhasExpandidas) {
            this.data = data; this.titulo = titulo; this.detalhe = detalhe;
            this.statusLabel = statusLabel; this.cor = cor; this.idOS = idOS; this.tipo = tipo;
            this.linhasExpandidas = linhasExpandidas;
        }
    }

    private final OficinaController controller;
    private final boolean modoCliente;
    /** Evento com detalhes abertos no modo "Dia" (modo cliente). */
    private EventoAgenda eventoExpandido;
    /** Status de cada OS do cliente (id → status), para escolher a aba certa em "Ver meus serviços". */
    private final Map<Long, String> statusOS = new HashMap<>();

    private final List<EventoAgenda> eventos = new ArrayList<>();
    private final Map<LocalDate, List<EventoAgenda>> indice = new HashMap<>();

    private Modo modoAtual = Modo.MES;
    private LocalDate dataReferencia = LocalDate.now();

    private final Map<Modo, JButton> botoesModo = new EnumMap<>(Modo.class);
    private JLabel lbl_Periodo;
    private JPanel pnl_View;
    private JPanel pnl_Modos;

    public PainelCalendario(OficinaController controller) {
        this(controller, false);
    }

    /** Calendário do Portal do Cliente: só os processos do cliente logado. */
    public static PainelCalendario paraCliente(OficinaController controller) {
        return new PainelCalendario(controller, true);
    }

    private PainelCalendario(OficinaController controller, boolean modoCliente) {
        this.controller = controller;
        this.modoCliente = modoCliente;
        setLayout(new BorderLayout(0, 8));
        setOpaque(false);
        setPreferredSize(new Dimension(880, 480));

        add(construirBarra(), BorderLayout.NORTH);

        pnl_View = new JPanel(new BorderLayout());
        pnl_View.setOpaque(false);
        add(pnl_View, BorderLayout.CENTER);

        add(construirLegenda(), BorderLayout.SOUTH);

        carregarEventos();
        renderizar();
    }

    // =========================================================================
    // BARRA SUPERIOR (modos + navegação + período)
    // =========================================================================
    private JComponent construirBarra() {
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setOpaque(false);

        // Os botões de modo (Dia/Semana/Mês/3 Meses/Ano) NÃO ficam mais aqui: agora
        // vivem na mesma linha do título do card "Agenda de Serviços e Prazos"
        // (ver getPainelModos(), usado por V_PaginaInicial.criarSecaoCalendario()).
        pnl_Modos = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnl_Modos.setOpaque(false);
        for (Modo m : Modo.values()) {
            JButton b = botaoBarra(m.rotulo);
            b.addActionListener(e -> { modoAtual = m; renderizar(); });
            botoesModo.put(m, b);
            pnl_Modos.add(b);
        }

        lbl_Periodo = new JLabel("", SwingConstants.CENTER);
        lbl_Periodo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl_Periodo.setForeground(COR_TEXTO);

        JButton btnAnt = botaoBarra("◀");
        JButton btnHoje = botaoBarra("Hoje");
        JButton btnProx = botaoBarra("▶");
        btnAnt.addActionListener(e -> navegar(-1));
        btnProx.addActionListener(e -> navegar(1));
        btnHoje.addActionListener(e -> { dataReferencia = LocalDate.now(); renderizar(); });

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        nav.setOpaque(false);
        nav.add(btnAnt);
        nav.add(btnHoje);
        nav.add(btnProx);

        // Espaçador invisível do mesmo tamanho de "nav": sem os botões de modo aqui
        // para equilibrar o lado esquerdo, o rótulo do período ficaria puxado para a
        // esquerda. Este espelho garante que "lbl_Periodo" fique realmente centralizado.
        JPanel espacadorEsquerda = new JPanel();
        espacadorEsquerda.setOpaque(false);
        espacadorEsquerda.setPreferredSize(nav.getPreferredSize());

        barra.add(espacadorEsquerda, BorderLayout.WEST);
        barra.add(lbl_Periodo, BorderLayout.CENTER);
        barra.add(nav, BorderLayout.EAST);
        return barra;
    }

    /**
     * Painel com os botões de modo (Dia/Semana/Mês/3 Meses/Ano), para ser embutido
     * na mesma linha do título do card pelo chamador (V_PaginaInicial).
     */
    JComponent getPainelModos() {
        return pnl_Modos;
    }

    private JButton botaoBarra(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#DDDDDD")),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        b.setBackground(Color.WHITE);
        b.setForeground(Color.decode("#555555"));
        return b;
    }

    private void atualizarBotoesModo() {
        for (Map.Entry<Modo, JButton> e : botoesModo.entrySet()) {
            boolean ativo = e.getKey() == modoAtual;
            JButton b = e.getValue();
            b.setBackground(ativo ? COR_ACENTO : Color.WHITE);
            b.setForeground(ativo ? Color.WHITE : Color.decode("#555555"));
        }
    }

    private JComponent construirLegenda() {
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        legenda.setOpaque(false);
        if (modoCliente) {
            legenda.add(itemLegenda("Orçamento", COR_PRAZO));
            legenda.add(itemLegenda("Serviço aberto", COR_ABERTA));
            legenda.add(itemLegenda("Em andamento", COR_ANDAMENTO));
            legenda.add(itemLegenda("Concluído", COR_CONCLUIDA));
            legenda.add(itemLegenda("Etapa concluída", COR_ETAPA));
            legenda.add(itemLegenda("Revisão estimada", COR_REVISAO));
            return legenda;
        }
        legenda.add(itemLegenda("Aberta", COR_ABERTA));
        legenda.add(itemLegenda("Em Andamento", COR_ANDAMENTO));
        legenda.add(itemLegenda("Concluída", COR_CONCLUIDA));
        legenda.add(itemLegenda("Prazo de Orçamento", COR_PRAZO));
        return legenda;
    }

    private JComponent itemLegenda(String texto, Color cor) {
        JLabel dot = new JLabel("●");
        dot.setForeground(cor);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel txt = new JLabel(texto);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txt.setForeground(COR_SUAVE);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        p.add(dot);
        p.add(txt);
        return p;
    }

    // =========================================================================
    // CARGA DE DADOS
    // =========================================================================
    private void carregarEventos() {
        eventos.clear();
        if (modoCliente) carregarEventosCliente();
        else carregarEventosOficina();

        indice.clear();
        for (EventoAgenda e : eventos)
            indice.computeIfAbsent(e.data, k -> new ArrayList<>()).add(e);
    }

    private void carregarEventosOficina() {
        Map<Long, Veiculo> veic = new HashMap<>();
        for (Veiculo v : controller.listarVeiculos()) veic.put(v.getIdVeiculo(), v);

        for (ServicoResponseDTO dto : controller.listarTodosServicos()) {
            LocalDate d = parseData(dto.dataServico());
            if (d == null) continue;
            Veiculo v = dto.idVeiculo() != null ? veic.get(dto.idVeiculo()) : null;
            String detalhe = v == null ? "" :
                    (v.getModelo() != null ? v.getModelo().getNome() + " · " + v.getPlaca() : v.getPlaca());
            String titulo = (dto.titulo() != null && !dto.titulo().isBlank())
                    ? dto.titulo() : ("OS #" + dto.idServico());
            long id = dto.idServico() != null ? dto.idServico() : -1;
            eventos.add(new EventoAgenda(d, titulo, detalhe, statusLabel(dto.status()),
                    corStatus(dto.status()), id, EventoAgenda.Tipo.SERVICO));
        }

        // Prazos de orçamento — recurso ainda não implementado (ver método abaixo).
        eventos.addAll(carregarPrazosOrcamento());
    }

    // ---------------------------------------------------------------- modo cliente

    /**
     * Todas as datas conhecidas de cada processo do cliente logado:
     * <ul>
     *   <li>solicitação do orçamento ({@code orcamento.data_criacao});</li>
     *   <li>início da OS ({@code servico.data_servico});</li>
     *   <li>cada etapa concluída ({@code item_servico.data_realizacao});</li>
     *   <li>próxima revisão <i>estimada</i>: data da OS (ou da última etapa, se já
     *       concluída) + {@code validade_meses} de cada serviço do catálogo do orçamento.</li>
     * </ul>
     */
    private void carregarEventosCliente() {
        statusOS.clear();
        Map<Long, Veiculo> veic = new HashMap<>();
        for (Veiculo v : controller.listarMeusVeiculos()) veic.put(v.getIdVeiculo(), v);
        Map<Long, String> oficinas = new HashMap<>();
        for (model.Oficina o : controller.listarTodas()) oficinas.put(o.getIdOficina(), o.getNome());
        DateTimeFormatter br = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 1. Orçamentos
        for (model.Orcamento o : controller.listarMeusOrcamentos()) {
            LocalDate d = parseData(o.getDataCriacao());
            if (d == null) continue;
            String veiculo = descreverVeiculo(veic.get(o.getIdVeiculo()), o.getPlacaVeiculo());
            String situacao = statusOrcamento(o.getStatus());
            List<String> det = new ArrayList<>();
            det.add("<b>Solicitado em:</b> " + d.format(br));
            det.add("<b>Oficina:</b> " + esc(valor(o.getNomeOficina())));
            det.add("<b>Veículo:</b> " + esc(veiculo));
            det.add("<b>Situação:</b> " + esc(situacao));
            if (o.getValor() > 0)
                det.add("<b>Valor:</b> " + String.format(PT_BR, "R$ %,.2f", o.getValor()));
            String motivo = o.getReclamacao() == null ? "" : o.getReclamacao().split("\\R", 2)[0].trim();
            if (!motivo.isEmpty()) det.add("<b>Motivo:</b> " + esc(motivo));
            if (o.isLevar()) det.add("Você levará o veículo até a oficina.");
            if (o.isBuscar()) det.add("A oficina buscará o veículo.");
            String titulo = "Orçamento" + (o.getCodigo().isBlank() ? " #" + o.getIdOrcamento() : " " + o.getCodigo());
            eventos.add(new EventoAgenda(d, titulo, veiculo, situacao, COR_PRAZO, -1,
                    EventoAgenda.Tipo.ORCAMENTO, det));
        }

        // 2. Serviços (OS), suas etapas e a revisão estimada
        for (ServicoResponseDTO s : controller.listarMeusServicos()) {
            LocalDate inicio = parseData(s.dataServico());
            String nomeOS = (s.titulo() != null && !s.titulo().isBlank()) ? s.titulo() : "OS #" + s.idServico();
            String veiculo = descreverVeiculo(veic.get(s.idVeiculo()), null);
            String oficina = s.idOficina() != null ? oficinas.getOrDefault(s.idOficina(), "—") : "—";
            String situacao = statusServicoCliente(s.status());
            long idOS = s.idServico() != null ? s.idServico() : -1;
            statusOS.put(idOS, s.status());

            List<ServicoResponseDTO.ItemView> etapas = s.itens() == null ? List.of() : s.itens();
            int concluidas = 0;
            LocalDate ultimaEtapa = null;
            List<String> checklist = new ArrayList<>();
            for (ServicoResponseDTO.ItemView it : etapas) {
                boolean ok = etapaConcluida(it.status());
                LocalDate dr = parseData(it.dataRealizacao());
                if (ok) concluidas++;
                if (ok && dr != null && (ultimaEtapa == null || dr.isAfter(ultimaEtapa))) ultimaEtapa = dr;
                checklist.add((ok ? "<span style='color:#27AE60'>✓</span> " : "○ ") + esc(it.descricao())
                        + (ok && dr != null ? " <span style='color:#888888'>— " + dr.format(br) + "</span>" : ""));
            }

            List<String> det = new ArrayList<>();
            if (s.codigo() != null && !s.codigo().isBlank()) det.add("<b>Código:</b> " + esc(s.codigo()));
            if (inicio != null) det.add("<b>Início:</b> " + inicio.format(br));
            det.add("<b>Oficina:</b> " + esc(oficina));
            det.add("<b>Veículo:</b> " + esc(veiculo));
            det.add("<b>Situação:</b> " + esc(situacao));
            if (!etapas.isEmpty()) {
                det.add("<b>Progresso:</b> " + concluidas + " de " + etapas.size() + " etapa(s) concluída(s)");
                det.addAll(checklist);
            }

            if (inicio != null)
                eventos.add(new EventoAgenda(inicio, "Início: " + nomeOS, veiculo, situacao,
                        corServicoCliente(s.status()), idOS, EventoAgenda.Tipo.SERVICO, det));

            for (ServicoResponseDTO.ItemView it : etapas) {
                LocalDate dr = parseData(it.dataRealizacao());
                if (!etapaConcluida(it.status()) || dr == null) continue;
                List<String> detEtapa = new ArrayList<>();
                detEtapa.add("<b>Etapa:</b> " + esc(it.descricao()));
                detEtapa.add("<b>Concluída em:</b> " + dr.format(br));
                if (it.tempoGasto() != null && !it.tempoGasto().isBlank())
                    detEtapa.add("<b>Tempo gasto:</b> " + esc(it.tempoGasto()));
                detEtapa.add("<b>Serviço:</b> " + esc(nomeOS) + " (" + esc(situacao) + ")");
                detEtapa.add("<b>Veículo:</b> " + esc(veiculo));
                detEtapa.add("<b>Oficina:</b> " + esc(oficina));
                eventos.add(new EventoAgenda(dr, "Etapa: " + valor(it.descricao()), nomeOS + " · " + veiculo,
                        "Concluída", COR_ETAPA, idOS, EventoAgenda.Tipo.ETAPA, detEtapa));
            }

            // Revisão estimada: só faz sentido para OS que não estão congeladas nem canceladas.
            if (s.idOrcamento() == null || "CONGELADA".equalsIgnoreCase(s.status())
                    || "CANCELADA".equalsIgnoreCase(s.status())) continue;
            boolean concluida = "CONCLUIDA".equalsIgnoreCase(s.status());
            LocalDate base = concluida && ultimaEtapa != null ? ultimaEtapa : inicio;
            if (base == null) continue;
            for (CatalogoServicoEntity c : controller.listarItensDoOrcamento(s.idOrcamento())) {
                Integer meses = c.getValidadeMeses();
                if (meses == null || meses <= 0) continue;
                LocalDate prevista = base.plusMonths(meses);
                List<String> detRev = new ArrayList<>();
                detRev.add("<b>Data estimada:</b> " + prevista.format(br));
                detRev.add("<b>Serviço de referência:</b> " + esc(c.getNome()));
                detRev.add("<b>Validade:</b> " + meses + " mes(es)"
                        + (c.getValidadeKm() != null && c.getValidadeKm() > 0
                           ? String.format(PT_BR, " ou %,d km (o que vier primeiro)", c.getValidadeKm()) : ""));
                detRev.add((concluida ? "<b>Realizado em:</b> " : "<b>Calculado a partir do início da OS:</b> ")
                        + base.format(br));
                detRev.add("<b>Veículo:</b> " + esc(veiculo));
                detRev.add("<b>Oficina:</b> " + esc(oficina));
                eventos.add(new EventoAgenda(prevista, "Revisão: " + valor(c.getNome()), veiculo,
                        "Estimada", COR_REVISAO, idOS, EventoAgenda.Tipo.PREVISAO_REVISAO, detRev));
            }
        }
    }

    private static String descreverVeiculo(Veiculo v, String placaFallback) {
        if (v == null) return placaFallback != null && !placaFallback.isBlank() ? placaFallback : "—";
        StringBuilder sb = new StringBuilder();
        if (v.getModelo() != null) {
            if (v.getModelo().getMontadora() != null) sb.append(v.getModelo().getMontadora().getNome()).append(' ');
            sb.append(v.getModelo().getNome()).append(" · ");
        }
        sb.append(v.getPlaca());
        return sb.toString();
    }

    /** Etapas são gravadas como CONCLUIDO pelo sistema, mas há dados antigos com CONCLUIDA. */
    private static boolean etapaConcluida(String status) {
        return "CONCLUIDO".equalsIgnoreCase(status) || "CONCLUIDA".equalsIgnoreCase(status);
    }

    private static String valor(String s) { return s == null || s.isBlank() ? "—" : s; }

    private static String statusOrcamento(String s) {
        if (s == null) return "Aguardando";
        return switch (s.toUpperCase()) {
            case "PENDENTE" -> "Aguardando aprovação";
            case "APROVADO" -> "Aprovado";
            case "REPROVADO", "RECUSADO" -> "Reprovado";
            default -> s;
        };
    }

    private static String statusServicoCliente(String s) {
        if (s == null) return "Aberto";
        return switch (s.toUpperCase()) {
            case "EM_ANDAMENTO" -> "Em andamento";
            case "CONCLUIDA" -> "Concluído";
            case "CONGELADA" -> "Aguardando novo orçamento";
            case "CANCELADA" -> "Cancelado";
            default -> "Aberto";
        };
    }

    private static Color corServicoCliente(String s) {
        if (s == null) return COR_ABERTA;
        return switch (s.toUpperCase()) {
            case "EM_ANDAMENTO" -> COR_ANDAMENTO;
            case "CONCLUIDA" -> COR_CONCLUIDA;
            case "CONGELADA", "CANCELADA" -> COR_CONGELADA;
            default -> COR_ABERTA;
        };
    }

    /**
     * Ponto de extensão para o futuro recurso de "prazos de orçamentos".
     * Quando o orçamento passar a ter uma data-limite, monte aqui os
     * {@link EventoAgenda} do tipo {@code PRAZO_ORCAMENTO} (cor {@link #COR_PRAZO})
     * e eles aparecerão automaticamente em todos os modos do calendário.
     */
    private List<EventoAgenda> carregarPrazosOrcamento() {
        return new ArrayList<>();
    }

    private LocalDate parseData(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        try { return LocalDate.parse(t); } catch (Exception ignored) { }
        try { return LocalDate.parse(t, DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (Exception ignored) { }
        return null;
    }

    private String statusLabel(String s) {
        if (s == null) return "Aberta";
        return switch (s) {
            case "CONCLUIDA"    -> "Concluída";
            case "EM_ANDAMENTO" -> "Em Andamento";
            default             -> "Aberta";
        };
    }

    private Color corStatus(String s) {
        if (s == null) return COR_ABERTA;
        return switch (s) {
            case "CONCLUIDA"    -> COR_CONCLUIDA;
            case "EM_ANDAMENTO" -> COR_ANDAMENTO;
            default             -> COR_ABERTA;
        };
    }

    // =========================================================================
    // NAVEGAÇÃO / RENDERIZAÇÃO
    // =========================================================================
    private void navegar(int dir) {
        switch (modoAtual) {
            case DIA       -> dataReferencia = dataReferencia.plusDays(dir);
            case SEMANA    -> dataReferencia = dataReferencia.plusWeeks(dir);
            case MES       -> dataReferencia = dataReferencia.plusMonths(dir);
            case TRIMESTRE -> dataReferencia = dataReferencia.plusMonths(3L * dir);
            case ANO       -> dataReferencia = dataReferencia.plusYears(dir);
        }
        renderizar();
    }

    private void renderizar() {
        atualizarBotoesModo();
        lbl_Periodo.setText(rotuloPeriodo());
        pnl_View.removeAll();
        JComponent view = switch (modoAtual) {
            case DIA       -> viewDia();
            case SEMANA    -> viewSemana();
            case MES       -> viewMes();
            case TRIMESTRE -> viewTrimestre();
            case ANO       -> viewAno();
        };
        pnl_View.add(view, BorderLayout.CENTER);
        pnl_View.revalidate();
        pnl_View.repaint();
    }

    private String rotuloPeriodo() {
        switch (modoAtual) {
            case DIA: {
                DateTimeFormatter f = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", PT_BR);
                return capitalizar(dataReferencia.format(f));
            }
            case SEMANA: {
                LocalDate ini = inicioSemana(dataReferencia);
                LocalDate fim = ini.plusDays(6);
                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM", PT_BR);
                String ano = ini.getYear() == fim.getYear()
                        ? String.valueOf(fim.getYear())
                        : ini.getYear() + "/" + fim.getYear();
                return ini.format(f) + " – " + fim.format(f) + "   " + ano;
            }
            case MES: {
                YearMonth ym = YearMonth.from(dataReferencia);
                return capitalizar(ym.getMonth().getDisplayName(TextStyle.FULL, PT_BR)) + " " + ym.getYear();
            }
            case TRIMESTRE: {
                YearMonth base = YearMonth.from(dataReferencia);
                YearMonth fim = base.plusMonths(2);
                String a = capitalizar(base.getMonth().getDisplayName(TextStyle.SHORT, PT_BR));
                String b = capitalizar(fim.getMonth().getDisplayName(TextStyle.SHORT, PT_BR));
                String ano = base.getYear() == fim.getYear()
                        ? String.valueOf(base.getYear())
                        : base.getYear() + "/" + fim.getYear();
                return a + " – " + b + " " + ano;
            }
            case ANO:
                return String.valueOf(dataReferencia.getYear());
        }
        return "";
    }

    private LocalDate inicioSemana(LocalDate d) {
        return d.minusDays(d.getDayOfWeek().getValue() % 7); // domingo
    }

    // =========================================================================
    // VIEW: DIA
    // =========================================================================
    private JComponent viewDia() {
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        List<EventoAgenda> evs = eventosOrdenados(dataReferencia);
        if (evs.isEmpty()) {
            lista.add(rotuloVazio(modoCliente ? "Nenhum evento neste dia."
                                              : "Nenhum serviço agendado para este dia."));
        } else {
            for (EventoAgenda ev : evs) {
                lista.add(linhaEventoDia(ev));
                lista.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane sc = new JScrollPane(lista);
        sc.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        sc.getViewport().setBackground(Color.WHITE);
        ScrollBarPadrao.aplicar(sc);
        return sc;
    }

    private JComponent linhaEventoDia(EventoAgenda ev) {
        if (modoCliente) return linhaEventoExpansivel(ev);
        JPanel linha = new JPanel(new BorderLayout(10, 0));
        linha.setBackground(Color.WHITE);
        linha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, ev.cor),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        JLabel t = new JLabel(ev.titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setForeground(COR_TEXTO);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(t);
        if (!ev.detalhe.isEmpty()) {
            JLabel d = new JLabel(ev.detalhe);
            d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            d.setForeground(COR_SUAVE);
            d.setAlignmentX(Component.LEFT_ALIGNMENT);
            textos.add(d);
        }

        JLabel status = new JLabel(ev.statusLabel);
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setForeground(ev.cor);

        linha.add(textos, BorderLayout.CENTER);
        linha.add(status, BorderLayout.EAST);

        adicionarClique(linha, ev);
        return linha;
    }

    /**
     * Linha do modo "Dia" no calendário do cliente: clicar alterna entre recolhido
     * (título + status) e expandido (todas as datas e informações do processo).
     */
    private JComponent linhaEventoExpansivel(EventoAgenda ev) {
        boolean aberto = ev == eventoExpandido;

        JPanel linha = new JPanel(new BorderLayout(0, 6));
        linha.setBackground(aberto ? Color.decode("#FAFAFA") : Color.WHITE);
        linha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, ev.cor),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel cab = new JPanel(new BorderLayout(10, 0));
        cab.setOpaque(false);
        cab.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel seta = new JLabel(aberto ? "▾" : "▸");
        seta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        seta.setForeground(COR_SUAVE);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        JLabel t = new JLabel(ev.titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setForeground(COR_TEXTO);
        textos.add(t);
        if (!ev.detalhe.isEmpty()) {
            JLabel d = new JLabel(ev.detalhe);
            d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            d.setForeground(COR_SUAVE);
            textos.add(d);
        }

        JLabel status = new JLabel(ev.statusLabel);
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setForeground(ev.cor);

        cab.add(seta, BorderLayout.WEST);
        cab.add(textos, BorderLayout.CENTER);
        cab.add(status, BorderLayout.EAST);
        linha.add(cab, BorderLayout.NORTH);

        MouseAdapter alternar = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                eventoExpandido = aberto ? null : ev;
                renderizar();
            }
        };
        for (Component c : new Component[]{cab, seta, textos, status, t}) c.addMouseListener(alternar);

        if (aberto) {
            StringBuilder html = new StringBuilder("<html>");
            for (String l : ev.linhasExpandidas) html.append(l).append("<br>");
            html.append("</html>");
            JLabel corpo = new JLabel(html.toString());
            corpo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            corpo.setForeground(COR_TEXTO);
            corpo.setBorder(BorderFactory.createEmptyBorder(2, 22, 0, 0));
            linha.add(corpo, BorderLayout.CENTER);

            JComponent acao = acaoDoEvento(ev);
            if (acao != null) {
                JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 0));
                rodape.setOpaque(false);
                rodape.add(acao);
                linha.add(rodape, BorderLayout.SOUTH);
            }
        }

        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
        return linha;
    }

    /** Atalho para a tela do portal relacionada ao evento expandido. */
    private JComponent acaoDoEvento(EventoAgenda ev) {
        String texto;
        Runnable acao;
        switch (ev.tipo) {
            case ORCAMENTO -> { texto = "Ver meus orçamentos"; acao = () -> EstiloCliente.navegar(this, new V_ClienteOrcamentos(controller)); }
            case SERVICO, ETAPA -> {
                boolean concluido = "CONCLUIDA".equalsIgnoreCase(statusOS.get(ev.idOS));
                texto = "Ver meus serviços";
                acao = () -> EstiloCliente.navegar(this, new V_ClienteServicos(controller, concluido));
            }
            case PREVISAO_REVISAO -> { texto = "Buscar oficina para a revisão"; acao = () -> EstiloCliente.navegar(this, new V_ClienteBuscarOficinas(controller)); }
            default -> { return null; }
        }
        JButton b = botaoBarra(texto);
        b.addActionListener(e -> acao.run());
        return b;
    }

    // =========================================================================
    // VIEW: SEMANA
    // =========================================================================
    private JComponent viewSemana() {
        LocalDate ini = inicioSemana(dataReferencia);
        JPanel grid = new JPanel(new GridLayout(1, 7, 6, 0));
        grid.setOpaque(false);
        for (int i = 0; i < 7; i++) grid.add(colunaSemana(ini.plusDays(i)));

        JScrollPane sc = new JScrollPane(grid,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sc.setBorder(null);
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(sc);
        return sc;
    }

    private JComponent colunaSemana(LocalDate dia) {
        boolean hoje = dia.equals(LocalDate.now());
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(Color.WHITE);
        col.setBorder(BorderFactory.createLineBorder(COR_BORDA));

        String nomeDia = capitalizar(dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, PT_BR));
        JLabel head = new JLabel(nomeDia + " " + dia.getDayOfMonth(), SwingConstants.CENTER);
        head.setOpaque(true);
        head.setBackground(hoje ? COR_ACENTO : Color.decode("#F7F7F7"));
        head.setForeground(hoje ? Color.WHITE : COR_TEXTO);
        head.setFont(new Font("Segoe UI", Font.BOLD, 12));
        head.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        col.add(head);
        col.add(Box.createVerticalStrut(4));

        List<EventoAgenda> evs = eventosOrdenados(dia);
        for (EventoAgenda ev : evs) {
            col.add(chip(ev, true));
            col.add(Box.createVerticalStrut(3));
        }
        col.add(Box.createVerticalGlue());
        return col;
    }

    // =========================================================================
    // VIEW: MÊS
    // =========================================================================
    private JComponent viewMes() {
        JPanel cont = new JPanel(new BorderLayout());
        cont.setOpaque(false);

        JPanel head = new JPanel(new GridLayout(1, 7));
        head.setOpaque(false);
        for (String d : new String[]{"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"}) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            l.setForeground(COR_SUAVE);
            l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            head.add(l);
        }
        cont.add(head, BorderLayout.NORTH);

        YearMonth ym = YearMonth.from(dataReferencia);
        LocalDate primeiro = ym.atDay(1);
        LocalDate inicio = primeiro.minusDays(primeiro.getDayOfWeek().getValue() % 7);

        JPanel grid = new JPanel(new GridLayout(6, 7));
        grid.setOpaque(false);
        for (int i = 0; i < 42; i++) {
            LocalDate d = inicio.plusDays(i);
            grid.add(celulaMes(d, YearMonth.from(d).equals(ym)));
        }
        cont.add(grid, BorderLayout.CENTER);
        return cont;
    }

    private JComponent celulaMes(LocalDate dia, boolean doMes) {
        boolean hoje = dia.equals(LocalDate.now());
        JPanel cel = new JPanel();
        cel.setLayout(new BoxLayout(cel, BoxLayout.Y_AXIS));
        cel.setBackground(doMes ? Color.WHITE : Color.decode("#FAFAFA"));
        cel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));

        JLabel num = new JLabel(String.valueOf(dia.getDayOfMonth()));
        num.setFont(new Font("Segoe UI", hoje ? Font.BOLD : Font.PLAIN, 12));
        num.setForeground(!doMes ? Color.decode("#BBBBBB")
                : (hoje ? COR_ACENTO : COR_TEXTO));
        num.setAlignmentX(Component.LEFT_ALIGNMENT);
        cel.add(num);

        List<EventoAgenda> evs = eventosOrdenados(dia);
        int max = 3;
        for (int i = 0; i < Math.min(max, evs.size()); i++) {
            cel.add(Box.createVerticalStrut(2));
            cel.add(chip(evs.get(i), true));
        }
        if (evs.size() > max) {
            JLabel mais = new JLabel("+" + (evs.size() - max) + " mais");
            mais.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            mais.setForeground(COR_SUAVE);
            mais.setAlignmentX(Component.LEFT_ALIGNMENT);
            cel.add(mais);
        }

        // Clique numa área livre do dia abre o modo "Dia" naquela data.
        MouseAdapter irDia = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dataReferencia = dia; modoAtual = Modo.DIA; renderizar();
            }
        };
        cel.addMouseListener(irDia);
        num.addMouseListener(irDia);
        cel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return cel;
    }

    // =========================================================================
    // VIEW: 3 MESES e ANO (mini-meses)
    // =========================================================================
    private JComponent viewTrimestre() {
        YearMonth base = YearMonth.from(dataReferencia);
        JPanel p = new JPanel(new GridLayout(1, 3, 16, 0));
        p.setOpaque(false);
        for (int i = 0; i < 3; i++) p.add(molduraBranca(miniMes(base.plusMonths(i))));
        return p;
    }

    private JComponent viewAno() {
        int ano = dataReferencia.getYear();
        JPanel p = new JPanel(new GridLayout(3, 4, 12, 12));
        p.setOpaque(false);
        for (int m = 1; m <= 12; m++) p.add(molduraBranca(miniMes(YearMonth.of(ano, m))));

        JScrollPane sc = new JScrollPane(p,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sc.setBorder(null);
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(sc);
        return sc;
    }

    private JComponent molduraBranca(JComponent conteudo) {
        JPanel moldura = new JPanel(new BorderLayout());
        moldura.setBackground(Color.WHITE);
        moldura.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        moldura.add(conteudo, BorderLayout.CENTER);
        return moldura;
    }

    private JComponent miniMes(YearMonth ym) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel titulo = new JLabel(
                capitalizar(ym.getMonth().getDisplayName(TextStyle.FULL, PT_BR)) + " " + ym.getYear(),
                SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titulo.setForeground(Color.decode("#4D4D4D"));
        p.add(titulo, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 7, 1, 1));
        grid.setOpaque(false);
        for (String d : new String[]{"D", "S", "T", "Q", "Q", "S", "S"}) {
            JLabel h = new JLabel(d, SwingConstants.CENTER);
            h.setFont(new Font("Segoe UI", Font.BOLD, 10));
            h.setForeground(COR_SUAVE);
            grid.add(h);
        }
        LocalDate primeiro = ym.atDay(1);
        int leading = primeiro.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < leading; i++) grid.add(new JLabel(""));
        for (int dia = 1; dia <= ym.lengthOfMonth(); dia++) grid.add(celulaMini(ym.atDay(dia)));

        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JComponent celulaMini(LocalDate d) {
        List<EventoAgenda> evs = indice.getOrDefault(d, Collections.emptyList());
        boolean tem = !evs.isEmpty();
        boolean hoje = d.equals(LocalDate.now());

        JLabel c = new JLabel(String.valueOf(d.getDayOfMonth()), SwingConstants.CENTER);
        c.setFont(new Font("Segoe UI", tem ? Font.BOLD : Font.PLAIN, 11));
        c.setOpaque(true);
        c.setBackground(hoje ? Color.decode("#FFE0B2") : (tem ? Color.decode("#FFF3E0") : Color.WHITE));
        c.setForeground(tem ? Color.decode("#E67E22") : Color.decode("#555555"));
        c.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        c.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (tem) c.setToolTipText(evs.size() + (modoCliente ? " evento(s)" : " serviço(s)") + " neste dia");
        c.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dataReferencia = d; modoAtual = Modo.DIA; renderizar();
            }
        });
        return c;
    }

    // =========================================================================
    // AUXILIARES
    // =========================================================================
    private List<EventoAgenda> eventosOrdenados(LocalDate dia) {
        List<EventoAgenda> evs = new ArrayList<>(indice.getOrDefault(dia, Collections.emptyList()));
        evs.sort(Comparator.comparingInt(this::ordemStatus));
        return evs;
    }

    private int ordemStatus(EventoAgenda ev) {
        if (modoCliente) return ev.tipo.ordinal(); // serviço, orçamento, etapa, revisão
        if (ev.cor == COR_ABERTA)    return 0;
        if (ev.cor == COR_ANDAMENTO) return 1;
        if (ev.cor == COR_CONCLUIDA) return 2;
        return 3;
    }

    /** Etiqueta colorida clicável usada nas células de semana e mês. */
    private JComponent chip(EventoAgenda ev, boolean compacto) {
        JLabel c = new JLabel(limitar(ev.titulo, compacto ? 16 : 30));
        c.setOpaque(true);
        c.setBackground(ev.cor);
        c.setForeground(Color.WHITE);
        c.setFont(new Font("Segoe UI", Font.PLAIN, compacto ? 10 : 12));
        c.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, compacto ? 16 : 20));
        c.setToolTipText("<html><b>" + esc(ev.titulo) + "</b><br>" + esc(ev.statusLabel)
                + (ev.detalhe.isEmpty() ? "" : "<br>" + esc(ev.detalhe)) + "</html>");
        adicionarClique(c, ev);
        return c;
    }

    /**
     * Torna o componente clicável. Na oficina abre a OS associada; no portal do
     * cliente abre o dia do evento no modo "Dia" já com os detalhes expandidos.
     */
    private void adicionarClique(JComponent comp, EventoAgenda ev) {
        if (modoCliente) {
            comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
            comp.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    eventoExpandido = ev;
                    dataReferencia = ev.data;
                    modoAtual = Modo.DIA;
                    renderizar();
                    e.consume();
                }
            });
            return;
        }
        if (ev.idOS <= 0) return;
        comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comp.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { abrirOS(ev.idOS); }
        });
    }

    private void abrirOS(long idOS) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_OrdemServico(controller, idOS));
    }

    private JComponent rotuloVazio(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        l.setForeground(COR_SUAVE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 0));
        return l;
    }

    private static String capitalizar(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String limitar(String s, int n) {
        if (s == null) return "";
        return s.length() <= n ? s : s.substring(0, n - 1) + "…";
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}