
package view;

import controller.OficinaController;

import javax.swing.*;
import java.awt.*;

/**
 * Painel de Conteúdo da Tela de Pesquisa Geral.
 * Substitui dinamicamente o centro do sistema ao ser acionado.
 */
public class V_PesquisarGeral extends JPanel {

    // ==========================================
    // DECLARAÇÃO DOS COMPONENTES (Padrão Prefixo)
    // ==========================================
    private JPanel pnl_Conteudo;
    private JTextField txf_BarraPesquisa;
    private JButton btn_Buscar;
    private JLabel lbl_Instrucao;
    private JPanel pnl_ResultadosContainer;

    public V_PesquisarGeral(OficinaController controller) {
        // Define o layout principal para ocupar 100% da área útil ao lado do menu
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F5F5F5")); // Cinza claro padrão do sistema

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        pnl_Conteudo = new JPanel(new BorderLayout(20, 20));
        pnl_Conteudo.setBackground(Color.WHITE);
        pnl_Conteudo.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        lbl_Instrucao = new JLabel("Pesquise por nome do cliente, placa do veículo ou número da OS:");
        lbl_Instrucao.setFont(new Font("Arial", Font.BOLD, 14));
        lbl_Instrucao.setForeground(Color.DARK_GRAY);

        txf_BarraPesquisa = new JTextField();
        txf_BarraPesquisa.setFont(new Font("Arial", Font.PLAIN, 14));
        txf_BarraPesquisa.setPreferredSize(new Dimension(0, 40));

        btn_Buscar = new JButton("Buscar");
        btn_Buscar.setFont(new Font("Arial", Font.BOLD, 13));
        btn_Buscar.setPreferredSize(new Dimension(120, 40));
        btn_Buscar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Estruturação da barra de entrada superior
        JPanel pnl_BuscaBarra = new JPanel(new BorderLayout(15, 10));
        pnl_BuscaBarra.setOpaque(false);
        pnl_BuscaBarra.add(lbl_Instrucao, BorderLayout.NORTH);
        pnl_BuscaBarra.add(txf_BarraPesquisa, BorderLayout.CENTER);
        pnl_BuscaBarra.add(btn_Buscar, BorderLayout.EAST);

        pnl_ResultadosContainer = new JPanel();
        pnl_ResultadosContainer.setBackground(Color.WHITE);
        pnl_ResultadosContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Resultados encontrados",
                0, 0, new Font("Arial", Font.BOLD, 13), Color.GRAY));

        pnl_Conteudo.add(pnl_BuscaBarra, BorderLayout.NORTH);
        pnl_Conteudo.add(pnl_ResultadosContainer, BorderLayout.CENTER);

        // Dispara a busca
        btn_Buscar.addActionListener(e -> executarPesquisa(txf_BarraPesquisa.getText()));
    }

    private void layoutComponents() {
        // Cria uma margem externa para dar o efeito de "card flutuante" sobre o fundo cinza
        JPanel pnl_MargemExterna = new JPanel(new BorderLayout());
        pnl_MargemExterna.setOpaque(false);
        pnl_MargemExterna.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnl_MargemExterna.add(pnl_Conteudo, BorderLayout.CENTER);

        add(pnl_MargemExterna, BorderLayout.CENTER);
    }

    /**
     * Filtro e validação de buscas (Pronto para conectar ao seu banco/controller)
     */
    private void executarPesquisa(String termoBusca) {
        if (termoBusca.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um termo para pesquisar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // A sua lógica de pesquisa com o Banco de dados entrará aqui
    }
}