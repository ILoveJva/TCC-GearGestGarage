package view;

import br.com.oficina.shared.config.DatabaseConfig;
import controller.OficinaController;
import javax.swing.SwingUtilities;

/** Ponto de entrada da aplicacao (camada View / Swing). */
public class Main {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConfig::fechar));
        SwingUtilities.invokeLater(() -> {
            OficinaController controller = new OficinaController();
            new V_Login(controller).setVisible(true);
        });
        System.out.println("toma no cu");
    }
}
