package view;

import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * "Seus veículos" (protótipo do Figma): grade de cartões cinza com a foto em moldura
 * branca e o nome do carro. No desktop a grade tem 4 colunas e um último cartão
 * "Adicionar veículo". Clicar abre "Informações do Veículo".
 */
public class V_ClienteVeiculos extends JPanel {

    public V_ClienteVeiculos(OficinaController controller) {
        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);

        JPanel grade = new JPanel(new GridLayout(0, 4, 20, 20));
        grade.setOpaque(false);
        List<Veiculo> veiculos = controller.listarMeusVeiculos();
        for (Veiculo v : veiculos)
            grade.add(EstiloCliente.cartaoVeiculo(v,
                    () -> EstiloCliente.navegar(this, new V_ClienteDetalhesVeiculo(controller, v))));
        grade.add(EstiloCliente.tile("Adicionar veículo", "/assets/icons/vveiculos.png",
                EstiloCliente.iconeMais(14), () -> EstiloCliente.navegar(this, new V_ClienteCadastrarVeiculo(controller))));

        JPanel conteudo = new JPanel(new BorderLayout(0, 14));
        conteudo.setOpaque(false);
        if (veiculos.isEmpty()) {
            JLabel vazio = new JLabel("Você ainda não tem veículos cadastrados.", SwingConstants.CENTER);
            vazio.setFont(EstiloCliente.fonte(Font.ITALIC, 14));
            vazio.setForeground(EstiloCliente.TEXTO_SUAVE);
            conteudo.add(vazio, BorderLayout.NORTH);
        }
        conteudo.add(grade, BorderLayout.CENTER);

        add(EstiloCliente.pagina("Seus veículos",
                () -> EstiloCliente.navegar(this, new V_ClienteMenu(controller)), conteudo));
    }
}
