package clients;

import clients.backDoor.BackDoorController;
import clients.backDoor.BackDoorModel;
import clients.backDoor.BackDoorView;
import clients.cashier.CashierController;
import clients.cashier.CashierModel;
import clients.cashier.CashierView;
import clients.customer.CustomerController;
import clients.customer.CustomerModel;
import clients.customer.CustomerView;
import clients.packing.PackingController;
import clients.packing.PackingModel;
import clients.packing.PackingView;
import middle.LocalMiddleFactory;
import middle.MiddleFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Starts all the clients (user interface) as a single application.
 * Good for testing the system using a single application.
 * 
 * @author Mike Smith
 * @version 2.0
 * @author Shine
 * @version year-2024
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Apply Nimbus Look and Feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            // Customize UI styles
            UIManager.put("Button.background", new Color(70, 130, 180));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));
            UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 16));
            UIManager.put("Label.foreground", Color.DARK_GRAY);
            UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            UIManager.put("Panel.background", new Color(240, 248, 255));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the application
        SwingUtilities.invokeLater(() -> {
            System.out.println("Launching application...");
            new Main().begin();
        });
    }

    /**
     * Starts the system (Non-distributed)
     */
    public void begin() {
        System.out.println("Starting all clients...");
        MiddleFactory mlf = new LocalMiddleFactory();  // Direct access
        startCustomerGUI_MVC(mlf);
        startCashierGUI_MVC(mlf);  // First Cashier
        startCashierGUI_MVC(mlf);  // Second Cashier
        startPackingGUI_MVC(mlf);
        startBackDoorGUI_MVC(mlf);
    }

    /**
     * Start the Customer client - search product.
     * @param mlf A factory to create objects to access the stock list.
     */
    public void startCustomerGUI_MVC(MiddleFactory mlf) {
        JFrame window = new JFrame();
        window.setTitle("Customer Client MVC");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension pos = PosOnScrn.getPos();

        CustomerModel model = new CustomerModel(mlf);
        CustomerView view = new CustomerView(window, mlf, pos.width, pos.height);
        CustomerController cont = new CustomerController(model, view);
        view.setController(cont);

        model.addObserver(view); // Add observer to the model
        window.setVisible(true); // Start Screen
    }

    /**
     * Start the Cashier client - customer check stock, buy product.
     * @param mlf A factory to create objects to access the stock list.
     */
    public void startCashierGUI_MVC(MiddleFactory mlf) {
        JFrame window = new JFrame();
        window.setTitle("Cashier Client MVC");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension pos = PosOnScrn.getPos();

        CashierModel model = new CashierModel(mlf);
        CashierView view = new CashierView(window, mlf, pos.width, pos.height);
        CashierController cont = new CashierController(model, view);
        view.setController(cont);

        model.addObserver(view); // Add observer to the model
        window.setVisible(true); // Make window visible
        model.askForUpdate();    // Initial display
    }

    /**
     * Start the Packing client - for warehouse staff to pack the bought order for customers, one order at a time.
     * @param mlf A factory to create objects to access the stock list.
     */
    public void startPackingGUI_MVC(MiddleFactory mlf) {
        JFrame window = new JFrame();
        window.setTitle("Packing Client MVC");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension pos = PosOnScrn.getPos();

        PackingModel model = new PackingModel(mlf);
        PackingView view = new PackingView(window, mlf, pos.width, pos.height);
        PackingController cont = new PackingController(model, view);
        view.setController(cont);

        model.addObserver(view); // Add observer to the model
        window.setVisible(true); // Make window visible
    }

    /**
     * Start the BackDoor client - store staff to check and update stock.
     * @param mlf A factory to create objects to access the stock list.
     */
    public void startBackDoorGUI_MVC(MiddleFactory mlf) {
        JFrame window = new JFrame();
        window.setTitle("BackDoor Client MVC");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension pos = PosOnScrn.getPos();

        BackDoorModel model = new BackDoorModel(mlf);
        BackDoorView view = new BackDoorView(window, mlf, pos.width, pos.height);
        BackDoorController cont = new BackDoorController(model, view);
        view.setController(cont);

        model.addObserver(view); // Add observer to the model
        window.setVisible(true); // Make window visible
    }
    
}

