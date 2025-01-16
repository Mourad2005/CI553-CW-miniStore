package remote;

import catalogue.Product;
import dbAccess.StockRW;
import middle.StockException;

import javax.swing.*;
import java.rmi.RemoteException;

/**
 * Implements Read/Write access to the stock list,
 * the stock list is held in a relational database.
 * 
 * All transactions are done via StockRW to ensure
 * that a single connection to the database is used for all transactions.
 * 
 * @author Mike Smith University of Brighton
 * @version 2.0
 */
public class R_StockRW extends java.rmi.server.UnicastRemoteObject implements RemoteStockRW_I {
    private static final long serialVersionUID = 1;
    private StockRW aStockRW = null;

    /**
     * Constructor to initialize StockRW.
     * 
     * @param url URL of remote object (unused here)
     * @throws RemoteException  if RMI error occurs
     * @throws StockException   if stock database error occurs
     */
    public R_StockRW(String url) throws RemoteException, StockException {
        aStockRW = new StockRW();
    }

    /**
     * Returns true if the product exists.
     */
    public synchronized boolean exists(String pNum) throws StockException {
        return aStockRW.exists(pNum);
    }

    /**
     * Returns details about the product in the stock list.
     */
    public synchronized Product getDetails(String pNum) throws StockException {
        return aStockRW.getDetails(pNum);
    }

    /**
     * Returns an image of the product in the stock list.
     */
    public synchronized ImageIcon getImage(String pNum) throws StockException {
        return aStockRW.getImage(pNum);
    }

    /**
     * Buys stock and decrements the stock quantity.
     */
    public synchronized boolean buyStock(String pNum, int amount) throws StockException {
        return aStockRW.buyStock(pNum, amount);
    }

    /**
     * Adds (Restocks) stock to the product list.
     */
    public synchronized void addStock(String pNum, int amount) throws StockException {
        aStockRW.addStock(pNum, amount);
    }

    /**
     * Modifies stock details for a given product number.
     */
    public synchronized void modifyStock(Product product) throws StockException {
        aStockRW.modifyStock(product);
    }
}


