package middle;

import catalogue.Product;

/**
 * Interface for stock read/write operations
 */
public interface StockReadWriter extends StockReader {
    /**
     * Add stock for a product
     * 
     * @param productNum Product number
     * @param amount     Amount of stock to add
     * @throws StockException if there is an issue
     */
    void addStock(String productNum, int amount) throws StockException;

    /**
     * Buy stock for a product
     * 
     * @param productNum Product number
     * @param amount     Amount of stock to buy
     * @return true if successful, false otherwise
     * @throws StockException if there is an issue
     */
    boolean buyStock(String productNum, int amount) throws StockException;

    /**
     * Modify stock details
     * 
     * @param detail Product details to modify
     * @throws StockException if there is an issue
     */
    void modifyStock(Product detail) throws StockException;
}


