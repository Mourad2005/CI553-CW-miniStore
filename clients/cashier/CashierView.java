package clients.cashier;

import catalogue.Basket;
import middle.MiddleFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * View of the model
 */
public class CashierView implements Observer {
    private static final int H = 300;       // Height of window pixels
    private static final int W = 400;       // Width of window pixels

    private final JLabel pageTitle = new JLabel();
    private final JLabel theAction = new JLabel();
    private final JTextField theInput = new JTextField();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JTextArea theOutput = new JTextArea();
    private final JScrollPane theSP = new JScrollPane();
    private final JButton theBtCheck = new JButton("Check");
    private final JButton theBtBuy = new JButton("Buy");
    private final JButton theBtBought = new JButton("Bought/Pay");
    private final JButton theBtClear = new JButton("Clear");

    private CashierController cont = null;

    /**
     * Construct the view
     * 
     * @param rpc Window in which to construct
     * @param mf  Factory to deliver order and stock objects
     * @param x   x-coordinate of position of window on screen
     * @param y   y-coordinate of position of window on screen
     */
    public CashierView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        Container cp = rpc.getContentPane();
        cp.setLayout(null);
        Container rootWindow = (Container) rpc;
        rootWindow.setSize(W, H);
        rootWindow.setLocation(x, y);

        Font f = new Font("Monospaced", Font.PLAIN, 12);

        pageTitle.setBounds(110, 0, 270, 20);
        pageTitle.setText("Thank You for Shopping at MiniStore");
        cp.add(pageTitle);

        theBtCheck.setBounds(16, 25, 80, 40);
        theBtCheck.addActionListener(e -> cont.doCheck(theInput.getText()));
        cp.add(theBtCheck);

        theBtBuy.setBounds(16, 85, 80, 40);
        theBtBuy.addActionListener(e -> {
            int quantity = (int) quantitySpinner.getValue();
            cont.doBuy(quantity);
        });
        cp.add(theBtBuy);

        theBtBought.setBounds(16, 145, 80, 40);
        theBtBought.addActionListener(e -> cont.doBought());
        cp.add(theBtBought);

        theBtClear.setBounds(16, 205, 80, 40);
        theBtClear.addActionListener(e -> cont.doClearBasket());
        cp.add(theBtClear);

        theAction.setBounds(110, 25, 270, 20);
        cp.add(theAction);

        theInput.setBounds(110, 50, 160, 40);
        cp.add(theInput);

        quantitySpinner.setBounds(280, 50, 100, 40);
        cp.add(quantitySpinner);

        theSP.setBounds(110, 100, 270, 160);
        theOutput.setFont(f);
        cp.add(theSP);
        theSP.getViewport().add(theOutput);

        rootWindow.setVisible(true);
        theInput.requestFocus();
    }

    /**
     * The controller object, used so that an interaction can be passed to the controller
     * 
     * @param c The controller
     */
    public void setController(CashierController c) {
        cont = c;
    }

    /**
     * Update the view
     * 
     * @param modelC The observed model
     * @param arg    Specific args
     */
    @Override
    public void update(Observable modelC, Object arg) {
        CashierModel model = (CashierModel) modelC;
        String message = (String) arg;
        theAction.setText(message);
        Basket basket = model.getBasket();
        if (basket == null)
            theOutput.setText("Customer's order is empty.");
        else
            theOutput.setText(basket.getDetails());

        theInput.requestFocus();
    }
}






