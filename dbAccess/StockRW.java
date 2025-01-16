package dbAccess;

import catalogue.Product;
import debug.DEBUG;
import middle.StockException;
import middle.StockReadWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements Read/Write access to the stock list
 * The stock list is held in a relational database
 */
public class StockRW extends StockR implements StockReadWriter {

    /**
     * Connects to the database
     */
    public StockRW() throws StockException {
        super();  // Connection done in StockR's constructor
    }

    /**
     * Customer buys stock, quantity decreased if successful.
     * 
     * @param pNum   Product number
     * @param amount Amount of stock bought
     * @return true if successful, false otherwise
     */
    @Override
    public synchronized boolean buyStock(String pNum, int amount) throws StockException {
        DEBUG.trace("DB StockRW: buyStock(%s,%d)", pNum, amount);

        int updates;
        try {
            // Fetch the current stock level
            PreparedStatement checkStmt = getConnectionObject().prepareStatement(
                "SELECT stockLevel FROM StockTable WHERE productNo = ?"
            );
            checkStmt.setString(1, pNum);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int stockLevel = rs.getInt("stockLevel");
                DEBUG.trace("Current stock level for %s: %d", pNum, stockLevel);

                if (stockLevel < amount) {
                    DEBUG.trace("Insufficient stock: requested=%d, available=%d", amount, stockLevel);
                    return false; // Not enough stock
                }
            } else {
                DEBUG.trace("Product %s does not exist", pNum);
                return false; // Product not found
            }

            // Execute stock reduction
            PreparedStatement updateStmt = getConnectionObject().prepareStatement(
                "UPDATE StockTable SET stockLevel = stockLevel - ? WHERE productNo = ? AND stockLevel >= ?"
            );
            updateStmt.setInt(1, amount);
            updateStmt.setString(2, pNum);
            updateStmt.setInt(3, amount);

            updates = updateStmt.executeUpdate();
            updateStmt.close();

        } catch (SQLException e) {
            throw new StockException("SQL buyStock: " + e.getMessage());
        }

        DEBUG.trace("Stock update result: %d rows affected", updates);
        return updates > 0; // Success if at least one row updated
    }


    /**
     * Adds stock (Re-stocks) to the store.
     * Assumed to exist in database.
     * 
     * @param pNum   Product number
     * @param amount Amount of stock to add
     */
    @Override
    public synchronized void addStock(String pNum, int amount) throws StockException {
        DEBUG.trace("DB StockRW: addStock(%s,%d)", pNum, amount);
        try {
            PreparedStatement stmt = getConnectionObject().prepareStatement(
                    "UPDATE StockTable SET stockLevel = stockLevel + ? WHERE productNo = ?"
            );
            stmt.setInt(1, amount);
            stmt.setString(2, pNum);
            stmt.executeUpdate();
            stmt.close();

            DEBUG.trace("DB StockRW: addStock(%s,%d) successful", pNum, amount);
        } catch (SQLException e) {
            throw new StockException("SQL addStock: " + e.getMessage());
        }
    }

    /**
     * Modifies stock details for a given product number.
     * Assumed to exist in database.
     * Information modified: Description, Price
     * 
     * @param detail Product details to change stock list to
     */
    @Override
    public synchronized void modifyStock(Product detail) throws StockException {
        DEBUG.trace("DB StockRW: modifyStock(%s)", detail.getProductNum());
        try {
            getConnectionObject().setAutoCommit(false);  // Start transaction

            if (!exists(String.valueOf(detail.getProductNum()))) {
                PreparedStatement insertProduct = getConnectionObject().prepareStatement(
                        "INSERT INTO ProductTable (productNo, description, picture, price) " +
                        "VALUES (?, ?, ?, ?)"
                );
                insertProduct.setInt(1, detail.getProductNum());
                insertProduct.setString(2, detail.getDescription());
                insertProduct.setString(3, "images/Pic" + detail.getProductNum() + ".jpg");
                insertProduct.setDouble(4, detail.getPrice());
                insertProduct.executeUpdate();
                insertProduct.close();

                PreparedStatement insertStock = getConnectionObject().prepareStatement(
                        "INSERT INTO StockTable (productNo, stockLevel) VALUES (?, ?)"
                );
                insertStock.setInt(1, detail.getProductNum());
                insertStock.setInt(2, detail.getQuantity());
                insertStock.executeUpdate();
                insertStock.close();
            } else {
                PreparedStatement updateProduct = getConnectionObject().prepareStatement(
                        "UPDATE ProductTable SET description = ?, price = ? WHERE productNo = ?"
                );
                updateProduct.setString(1, detail.getDescription());
                updateProduct.setDouble(2, detail.getPrice());
                updateProduct.setInt(3, detail.getProductNum());
                updateProduct.executeUpdate();
                updateProduct.close();

                PreparedStatement updateStock = getConnectionObject().prepareStatement(
                        "UPDATE StockTable SET stockLevel = ? WHERE productNo = ?"
                );
                updateStock.setInt(1, detail.getQuantity());
                updateStock.setInt(2, detail.getProductNum());
                updateStock.executeUpdate();
                updateStock.close();
            }

            getConnectionObject().commit();  // Commit transaction
            DEBUG.trace("DB StockRW: modifyStock(%s) successful", detail.getProductNum());

        } catch (SQLException e) {
            try {
                getConnectionObject().rollback();  // Rollback on error
            } catch (SQLException rollbackEx) {
                DEBUG.error("Rollback failed: %s", rollbackEx.getMessage());
            }
            throw new StockException("SQL modifyStock: " + e.getMessage());
        } finally {
            try {
                getConnectionObject().setAutoCommit(true);  // Restore auto-commit
            } catch (SQLException e) {
                DEBUG.error("Failed to restore auto-commit: %s", e.getMessage());
            }
        }
    }

    /**
     * Searches for products by name.
     * 
     * @param name The name or part of the product name to search
     * @return A list of matching products
     * @throws StockException if there is an issue
     */
    @Override
    public synchronized List<Product> searchByName(String name) throws StockException {
        List<Product> matchingProducts = new ArrayList<>();
        try {
            PreparedStatement stmt = getConnectionObject().prepareStatement(
                    "SELECT ProductTable.productNo, description, price, stockLevel " +
                    "FROM ProductTable INNER JOIN StockTable " +
                    "ON ProductTable.productNo = StockTable.productNo " +
                    "WHERE description LIKE ?"
            );
            stmt.setString(1, "%" + name + "%");

            DEBUG.trace("Executing search query with parameter: name=%s", name);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("productNo"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stockLevel")
                );
                matchingProducts.add(product);
            }
            rs.close();
            stmt.close();

            DEBUG.trace("DB StockRW: searchByName(%s) found %d products", name, matchingProducts.size());

        } catch (SQLException e) {
            throw new StockException("SQL searchByName: " + e.getMessage());
        }
        return matchingProducts;
    }
}

