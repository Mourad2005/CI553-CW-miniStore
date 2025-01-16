package clients.cashier;

import catalogue.BetterBasket;
import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;
import middle.StockException;
import middle.StockReadWriter;
import java.io.IOException;
import java.util.Observable;

/**
 * Implements the Model of the cashier client
 */
public class CashierModel extends Observable {
    private enum State { process, checked }

    private State theState = State.process;
    private Product theProduct = null;
    private BetterBasket theBasket = null;

    private StockReadWriter theStock = null;
    private OrderProcessing theOrder = null;

    public CashierModel(MiddleFactory mf) {
        try {
            theStock = mf.makeStockReadWriter();
            theOrder = mf.makeOrderProcessing();
        } catch (Exception e) {
            DEBUG.error("CashierModel.constructor\n%s", e.getMessage());
        }
        theState = State.process;
    }

    public BetterBasket getBasket() {
        return theBasket;
    }

    public void doCheck(String productNum) {
        String theAction = "";
        theState = State.process;
        try {
            if (theStock.exists(productNum)) {
                Product pr = theStock.getDetails(productNum);
                if (pr.getQuantity() > 0) {
                    theProduct = pr;
                    theAction = String.format("%s : %7.2f (%2d)", pr.getDescription(), pr.getPrice(), pr.getQuantity());
                    theState = State.checked;
                } else {
                    theAction = pr.getDescription() + " is out of stock.";
                }
            } else {
                theAction = "Unknown product number: " + productNum;
            }
        } catch (StockException e) {
            DEBUG.error("CashierModel.doCheck\n%s", e.getMessage());
            theAction = e.getMessage();
        }
        setChanged();
        notifyObservers(theAction);
    }

    public void doBuy(int quantity) {
        String theAction = "";
        try {
            if (theState != State.checked) {  // Not checked with customer
                theAction = "Please check its availability";
            } else {
                theProduct.setQuantity(quantity); // Set the specified quantity
                boolean stockBought = theStock.buyStock(
                    String.format("%04d", theProduct.getProductNum()), // Format as 4-digit number
                    quantity
                );

                if (stockBought) {  // Stock bought
                    makeBasketIfReq();              // new Basket?
                    theBasket.add(theProduct);      // Add to bought
                    theBasket.mergeDuplicates();    // Merge duplicates in basket
                    theBasket.sortByProductNumber();// Sort products by product number
                    theAction = "Purchased: " + theProduct.getDescription();
                } else {
                    theAction = "!!! Not in stock";  // Now no stock
                }
            }
        } catch (StockException e) {
            DEBUG.error("%s\n%s", "CashierModel.doBuy", e.getMessage());
            theAction = e.getMessage();
        }
        theState = State.process;  // All Done
        setChanged();
        notifyObservers(theAction);
    }


    private void generateReceipt() {
        if (theBasket == null || theBasket.size() == 0) {
            DEBUG.error("Cannot generate receipt: Basket is empty.");
            return;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("MiniStore Receipt\n");
        receipt.append("===========================\n");
        receipt.append("Date: ").append(java.time.LocalDateTime.now()).append("\n\n");

        receipt.append("Products:\n");
        for (Product product : theBasket.getProducts()) {
            receipt.append(String.format("  %s (x%d) - $%.2f\n",
                    product.getDescription(),
                    product.getQuantity(),
                    product.getPrice() * product.getQuantity()));
        }

        double totalCost = theBasket.getTotalCost();
        receipt.append("\nTotal Cost: $").append(String.format("%.2f", totalCost)).append("\n");
        receipt.append("===========================\n");

        saveReceiptToFile(receipt.toString());
    }

    private void saveReceiptToFile(String receiptContent) {
        try {
            String fileName = "Receipt_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".txt";
            java.nio.file.Files.write(
                java.nio.file.Paths.get(fileName),
                receiptContent.getBytes()
            );
            System.out.println("Saving receipt to: " + java.nio.file.Paths.get(fileName).toAbsolutePath());
            DEBUG.trace("Receipt saved as: %s", fileName);
        } catch (IOException e) {
            DEBUG.error("Failed to save receipt: %s", e.getMessage());
        }
    }

    
    
    public void doBought() {
        String theAction = "";
        try {
            if (theBasket != null && theBasket.size() >= 1) { // items > 1
                theOrder.newOrder(theBasket);       // Process order
                generateReceipt();                  // Generate receipt
                theBasket = null;                   // reset
                theAction = "Start New Order";      // New order
            } else {
                theAction = "No items in the basket to process.";
            }
        } catch (OrderException e) {
            DEBUG.error("%s\n%s", "CashierModel.doBought", e.getMessage());
            theAction = "Failed to process order: " + e.getMessage();
        }
        setChanged();
        notifyObservers(theAction);                 // Notify
    }


    /**
     * Clear the basket and return the stock of items to the database.
     */
    public void doClearBasket() {
        String theAction = "";
        if (theBasket != null) {
            try {
                for (Product product : theBasket.getProducts()) {
                    // Return the stock of each product to the database
                    theStock.addStock(
                        String.format("%04d", product.getProductNum()), // Format product number as 4 digits
                        product.getQuantity()
                    );
                }
                theBasket.clear();  // Clear the basket contents
                theAction = "Basket cleared and stock returned.";
            } catch (StockException e) {
                DEBUG.error("%s\n%s", "CashierModel.doClearBasket", e.getMessage());
                theAction = "Failed to clear basket: " + e.getMessage();
            }
        } else {
            theAction = "Basket is already empty.";
        }

        setChanged();
        notifyObservers(theAction); // Notify observers to update the view
    }


    public void askForUpdate() {
        setChanged();
        notifyObservers("Welcome to MiniStore.");
    }

    private void makeBasketIfReq() {
        if (theBasket == null) {
            try {
                int uon = theOrder.uniqueNumber(); // Get unique order number
                theBasket = makeBasket();
                theBasket.setOrderNum(uon);       // Set order number in basket
            } catch (OrderException e) {
                DEBUG.error("Comms failure\nCashierModel.makeBasket()\n%s", e.getMessage());
            }
        }
    }
    /**
     * Create a new instance of BetterBasket.
     * @return a new BetterBasket instance
     */
    protected BetterBasket makeBasket() {
        return new BetterBasket();
    }

}






