package view;

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
    private static final Color COR_PRAZO     = Color.decode("#8E44AD"); // reservado p/ prazos de orçamento

    private static final Locale PT_BR = new Locale("pt", "BR");

    /** Modos de visualização e o "passo" de navegação de cada um. */
    private enum Modo {
        DIA("Dia"), SEMANA("Semana"), MES("Mês"), TRIMESTRE("3 Meses"), ANO("Ano");
        final String rotulo;
        Modo(String rotulo) { this.rotulo = rotulo; }
    }

    /** Um item na agenda. {@code idOS <= 0} quando não há OS navegável (ex.: futuro prazo de orçamento). */
    private static final class EventoAgenda {
        enum Tipo { SERVICO, PRAZO_ORCAMENTO }
        final LocalDate data;
        final String titulo;
        final String detalhe;
        final String statusLabel;
        final Color cor;
        final long idOS;
        final Tipo tipo;
        EventoAgenda(LocalDate data, String titulo, String detalhe, String statusLabel,
                     Color cor, long idOS, Tipo tipo) {
            this.data = data; this.titulo = titulo; this.detalhe = detalhe;
            this.statusLabel = statusLabel; this.cor = cor; this.idOS = idOS; this.tipo = tipo;
        }
    }

    private final OficinaController controller;

    private final List<EventoAgenda> eventos = new ArrayList<>();
    private final Map<LocalDate, List<EventoAgenda>> indice = new HashMap<>();

    private Modo modoAtual = Modo.MES;
    private LocalDate dataReferencia = LocalDate.now();

    private final Map<Modo, JButton> botoesModo = new EnumMap<>(Modo.class);
    private JLabel lbl_Periodo;
    private JPanel pnl_View;

    public PainelCalendario(OficinaController controller) {
        this.controller = controller;
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

        JPanel modos = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        modos.setOpaque(false);
        for (Modo m : Modo.values()) {
            JButton b = botaoBarra(m.rotulo);
            b.addActionListener(e -> { modoAtual = m; renderizar(); });
            botoesModo.put(m, b);
            modos.add(b);
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

        barra.add(modos, BorderLayout.WEST);
        barra.add(lbl_Periodo, BorderLayout.CENTER);
        barra.add(nav, BorderLayout.EAST);
        return barra;
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

        indice.clear();
        for (EventoAgenda e : eventos)
            indice.computeIfAbsent(e.data, k -> new ArrayList<>()).add(e);
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
            lista.add(rotuloVazio("Nenhum serviço agendado para este dia."));
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
        if (tem) c.setToolTipText(evs.size() + " serviço(s) neste dia");
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

    /** Torna o componente clicável: abre a OS quando houver uma associada. */
    private void adicionarClique(JComponent comp, EventoAgenda ev) {
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
