package view;

import controller.OficinaController;
import javax.swing.*;
import java.awt.*;

/**
 * Tela Principal do Sistema (Template Estrutural - JFrame).
 * Controla e faz a ponte de navegação dinâmica entre as telas do sistema.
 */
public class V_Main extends JFrame {

    private JPanel pnl_Main;
    private V_MenuLateral pnl_MenuLateral;
    private JPanel pnl_ConteudoDinamico;

    // Referência do Controller que gerencia as regras e o Model
    private OficinaController controller;

    /**
     * Construtor modificado para receber o Controller do sistema.
     */
    public V_Main(OficinaController controller) {
        this.controller = controller;

        setTitle("Sistema de Gerenciamento - Gear Gest Garage");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();

        // Inicia o painel de conteúdo com a Página Inicial padrão
        atualizarConteudo(new V_PaginaInicial(this.controller));
    }

    private void initComponents() {
        pnl_Main = new JPanel(new BorderLayout());

        // Passa o controller adiante para que o menu possa injetá-lo nas próximas telas
        pnl_MenuLateral = new V_MenuLateral(this.controller);

        pnl_ConteudoDinamico = new JPanel(new BorderLayout());
        pnl_ConteudoDinamico.setBackground(Color.WHITE);
    }

    private void layoutComponents() {
        pnl_Main.add(pnl_MenuLateral, BorderLayout.WEST);
        pnl_Main.add(pnl_ConteudoDinamico, BorderLayout.CENTER);
        add(pnl_Main);
    }

    /**
     * Injeta dinamicamente os JPanels no espaço central da tela.
     * Intercepta se a página que está a entrar é a listagem de veículos para injetar o link de detalhes.
     */
    public void atualizarConteudo(JPanel pnl_NovaPagina) {
        // CORREÇÃO CRÍTICA: Se a tela que está a ser aberta for a listagem de veículos,
        // configuramos o evento de "link" para a tela de detalhes avançados antes de exibi-la.
        if (pnl_NovaPagina instanceof V_VisualizarVeiculos) {
            V_VisualizarVeiculos telaLista = (V_VisualizarVeiculos) pnl_NovaPagina;

            telaLista.setAcaoAbrirDetalhes((Long idVeiculo) -> {
                // Quando disparar o duplo clique na tabela, criamos a tela de detalhes
                // Passamos o controller, o ID selecionado e a ação de "Voltar"
                V_DetalhesVeiculo telaDetalhes = new V_DetalhesVeiculo(this.controller, idVeiculo, () -> {
                    // Ação do botão voltar: reabre a listagem de veículos recarregando os dados atualizados
                    V_VisualizarVeiculos novaLista = new V_VisualizarVeiculos(this.controller);
                    atualizarConteudo(novaLista);
                });

                // Transiciona fisicamente o painel central para a tela de detalhes
                atualizarConteudo(telaDetalhes);
            });
        }

        // Fluxo padrão de substituição de componentes na interface Swing
        pnl_ConteudoDinamico.removeAll();
        pnl_ConteudoDinamico.add(pnl_NovaPagina, BorderLayout.CENTER);
        pnl_ConteudoDinamico.revalidate();
        pnl_ConteudoDinamico.repaint();
    }
}