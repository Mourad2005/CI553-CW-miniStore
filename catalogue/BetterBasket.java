package catalogue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BetterBasket extends the functionality of Basket by adding
 * methods for merging duplicate products and sorting by product number.
 */
public class BetterBasket extends Basket {

    /**
     * Merge duplicate products in the basket.
     * Products with the same product number are combined into a single entry with updated quantity.
     */
    public void mergeDuplicates() {
        System.out.println("Before merging: " + this);

        List<Product> mergedProducts = new ArrayList<>();

        this.stream()
            .collect(Collectors.groupingBy(Product::getProductNum))
            .forEach((productNum, productList) -> {
                int totalQuantity = productList.stream()
                                               .mapToInt(Product::getQuantity)
                                               .sum();
                Product representativeProduct = productList.get(0);
                representativeProduct.setQuantity(totalQuantity);
                mergedProducts.add(representativeProduct);
            });

        this.clear();
        this.addAll(mergedProducts);

        System.out.println("After merging: " + this);
    }

    /**
     * Sort the products in the basket by product number in ascending order.
     */
    public void sortByProductNumber() {
        Collections.sort(this, Comparator.comparing(Product::getProductNum));
    }

    /**
     * Retrieve the list of products in the basket.
     * 
     * @return A list of products.
     */
    public List<Product> getProducts() {
        return new ArrayList<>(this);
    }

    @Override
    public String toString() {
        return this.stream()
                   .map(Product::toString)
                   .collect(Collectors.joining(", "));
    }
    
    public double getTotalCost() {
        double total = 0.0;
        for (Product product : getProducts()) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

}


