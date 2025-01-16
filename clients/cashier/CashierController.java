package clients.cashier;

import debug.DEBUG;

/**
 * Controller for the Cashier client
 */
public class CashierController {
    private CashierModel model;      // The underlying model
    private CashierView view;        // The GUI view

    /**
     * Construct the controller for the cashier client using an existing model and view
     * 
     * @param model The existing model
     * @param view  The existing view
     */
    public CashierController(CashierModel model, CashierView view) {
        this.model = model;
        this.view = view;
        view.setController(this);         // Set controller in view
        model.addObserver(view);          // View observes model
        model.askForUpdate();             // Initialize view
    }

    /**
     * Check if a product is in stock
     * 
     * @param productNum The product number to check
     */
    public void doCheck(String productNum) {
        DEBUG.trace("CashierController: doCheck(%s)", productNum);
        model.doCheck(productNum);
    }

    /**
     * Buy the checked product with the specified quantity
     * 
     * @param quantity The quantity to purchase
     */
    public void doBuy(int quantity) {
        DEBUG.trace("CashierController: doBuy(%d)", quantity);
        model.doBuy(quantity);
    }

    /**
     * Complete the purchase and process the basket
     */
    public void doBought() {
        DEBUG.trace("CashierController: doBought()");
        model.doBought();
    }

    /**
     * Clear the basket
     */
    public void doClearBasket() {
        DEBUG.trace("CashierController: doClearBasket()");
        model.doClearBasket();
    }
}







