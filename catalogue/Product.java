package catalogue;

public class Product {
    private int productNum;       // Product number
    private String description;   // Product description
    private double price;         // Product price
    private int quantity;         // Product quantity

    /**
     * Constructor for Product (int productNum)
     * @param productNum Product number
     * @param description Product description
     * @param price Product price
     * @param quantity Product quantity
     */
    public Product(int productNum, String description, double price, int quantity) {
        this.productNum = productNum;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Constructor for Product (String productNum)
     * @param productNum Product number (String)
     * @param description Product description
     * @param price Product price
     * @param quantity Product quantity
     */
    public Product(String productNum, String description, double price, int quantity) {
        this.productNum = Integer.parseInt(productNum); // Convert String to int
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductNum() {
        return productNum;
    }

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return this.productNum == product.productNum;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(productNum);
    }

    @Override
    public String toString() {
        return String.format("Product[Num=%d, Desc=%s, Price=%.2f, Qty=%d]",
                             productNum, description, price, quantity);
    }
}


