package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import controller.OficinaController;
import model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Tela inicial do Portal do Cliente (protótipo mobile do Figma adaptado para desktop):
 * saudação personalizada, grade de atalhos em tiles cinza e a agenda do cliente
 * com as datas de cada processo (orçamentos, OS, etapas e revisões estimadas).
 */
public class V_ClienteMenu extends JPanel {

    private static final Locale PT_BR = new Locale("pt", "BR");

    private final OficinaController controller;

    public V_ClienteMenu(OficinaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);

        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBackground(EstiloCliente.FUNDO);
        corpo.setBorder(BorderFactory.createEmptyBorder(28, 36, 32, 36));

        JComponent saudacao = construirSaudacao();
        saudacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(saudacao);
        corpo.add(Box.createVerticalStrut(24));

        JComponent grade = construirGrade();
        grade.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(grade);
        corpo.add(Box.createVerticalStrut(28));

        JComponent agenda = construirAgenda();
        agenda.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(agenda);

        add(EstiloCliente.rolagem(corpo), BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------ saudação

    private JComponent construirSaudacao() {
        Cliente meu = controller.getMeuCliente();
        String nome = EstiloCliente.primeiroNome(meu != null ? meu.getNome() : "");

        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel data = new JLabel(capitalizar(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", PT_BR))));
        data.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        data.setForeground(EstiloCliente.TEXTO_SUAVE);

        JLabel ola = new JLabel(EstiloCliente.saudacaoDoHorario() + (nome.isEmpty() ? "!" : ", " + nome + "!"));
        ola.setFont(EstiloCliente.fonte(Font.BOLD, 30));
        ola.setForeground(EstiloCliente.TEXTO);

        JLabel resumo = new JLabel(resumoDoDia());
        resumo.setFont(EstiloCliente.fonte(Font.PLAIN, 15));
        resumo.setForeground(EstiloCliente.TEXTO_SUAVE);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        for (JLabel l : new JLabel[]{data, ola, resumo}) {
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            textos.add(l);
        }
        textos.add(Box.createVerticalStrut(0));

        // Barrinha laranja à esquerda: destaque discreto com a cor do sistema.
        JPanel faixa = new JPanel();
        faixa.setBackground(EstiloCliente.ACENTO);
        faixa.setPreferredSize(new Dimension(5, 10));

        JPanel linha = new JPanel(new BorderLayout(14, 0));
        linha.setOpaque(false);
        linha.add(faixa, BorderLayout.WEST);
        linha.add(textos, BorderLayout.CENTER);

        p.add(linha, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }

    /** Frase curta com o que está pendente para o cliente. */
    private String resumoDoDia() {
        long andamento = 0, aguardandoOrc = 0;
        for (ServicoResponseDTO s : controller.listarMeusServicos())
            if (!"CONCLUIDA".equalsIgnoreCase(s.status())) andamento++;
        for (model.Orcamento o : controller.listarMeusOrcamentos())
            if ("PENDENTE".equalsIgnoreCase(o.getStatus())) aguardandoOrc++;

        if (andamento == 0 && aguardandoOrc == 0)
            return "Tudo em dia com seus veículos. O que você precisa hoje?";
        StringBuilder sb = new StringBuilder("Você tem ");
        if (andamento > 0)
            sb.append(andamento).append(andamento == 1 ? " serviço em andamento" : " serviços em andamento");
        if (andamento > 0 && aguardandoOrc > 0) sb.append(" e ");
        if (aguardandoOrc > 0)
            sb.append(aguardandoOrc).append(aguardandoOrc == 1
                    ? " orçamento aguardando aprovação" : " orçamentos aguardando aprovação");
        return sb.append('.').toString();
    }

    // ------------------------------------------------------------------ tiles

    private JComponent construirGrade() {
        JPanel grade = new JPanel(new GridLayout(1, 6, 16, 16));
        grade.setOpaque(false);

        grade.add(EstiloCliente.tile("Adicionar novo Veículo", "/assets/icons/vveiculos.png",
                EstiloCliente.iconeMais(14), () -> navegar(new V_ClienteCadastrarVeiculo(controller))));
        grade.add(EstiloCliente.tile("Orçamentos", "/assets/icons/vorcamentos.png",
                null, () -> navegar(new V_ClienteOrcamentos(controller))));
        grade.add(EstiloCliente.tile("Consultar Veículos", "/assets/icons/vveiculos.png",
                EstiloCliente.iconeLupa(14), () -> navegar(new V_ClienteVeiculos(controller))));
        grade.add(EstiloCliente.tile("Serviços em Andamento", "/assets/icons/piston.png",
                null, () -> navegar(new V_ClienteServicos(controller, false))));
        grade.add(EstiloCliente.tile("Buscar oficinas", "/assets/icons/pesquisar.png",
                null, () -> navegar(new V_ClienteBuscarOficinas(controller))));
        grade.add(EstiloCliente.tile("Consultar Serviços Antigos", "/assets/icons/piston.png",
                null, () -> navegar(new V_ClienteServicos(controller, true))));

        grade.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        grade.setPreferredSize(new Dimension(1000, 160));
        return grade;
    }

    // ------------------------------------------------------------------ agenda

    private JComponent construirAgenda() {
        PainelCalendario calendario = PainelCalendario.paraCliente(controller);
        calendario.setPreferredSize(new Dimension(900, 520));

        JLabel titulo = new JLabel("Minha agenda");
        titulo.setFont(EstiloCliente.fonte(Font.BOLD, 20));
        titulo.setForeground(EstiloCliente.TEXTO);

        JLabel sub = new JLabel("Datas dos seus orçamentos, serviços, etapas concluídas e próximas revisões. "
                + "Clique em um dia ou evento para ver os detalhes.");
        sub.setFont(EstiloCliente.fonte(Font.PLAIN, 12));
        sub.setForeground(EstiloCliente.TEXTO_SUAVE);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(sub);

        JPanel cabecalho = new JPanel(new BorderLayout(12, 0));
        cabecalho.setOpaque(false);
        cabecalho.add(textos, BorderLayout.WEST);
        JPanel modos = new JPanel(new GridBagLayout());
        modos.setOpaque(false);
        modos.add(calendario.getPainelModos());
        cabecalho.add(modos, BorderLayout.EAST);

        EstiloCliente.PainelArredondado card = new EstiloCliente.PainelArredondado(
                new BorderLayout(0, 14), 12, Color.decode("#F4F4F4"));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));
        card.add(cabecalho, BorderLayout.NORTH);
        card.add(calendario, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 640));
        return card;
    }

    private void navegar(JPanel pagina) {
        EstiloCliente.navegar(this, pagina);
    }

    private static String capitalizar(String s) {
        return s == null || s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
