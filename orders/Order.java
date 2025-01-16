package orders;

import catalogue.Basket;
import debug.DEBUG;
import middle.OrderException;
import middle.OrderProcessing;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The order processing system, enhanced for better cohesion and reduced coupling.
 * Manages customer orders as they progress through different states:
 * - Waiting to be processed
 * - Currently being packed
 * - Waiting to be collected
 * 
 * @author  Mike Smith University of Brighton
 * @version 3.2 (Enhanced)
 */
public class Order implements OrderProcessing {
    
    private enum State { Waiting, BeingPacked, ToBeCollected }

    private static class Folder {
        private final Basket basket;
        private State state;

        public Folder(Basket basket) {
            this.basket = basket;
            this.state = State.Waiting;
        }

        public State getState() {
            return state;
        }

        public void setState(State newState) {
            this.state = newState;
        }

        public Basket getBasket() {
            return basket;
        }
    }

    private final List<Folder> folders = new ArrayList<>();
    private static int nextOrderNumber = 1;

    /**
     * Generates a unique order number.
     * 
     * @return A unique order number.
     * @throws OrderException if an error occurs during order number generation.
     */
    public synchronized int uniqueNumber() throws OrderException {
        return nextOrderNumber++;
    }

    /**
     * Adds a new order to the processing system.
     * 
     * @param order The new order to be processed.
     * @throws OrderException if an error occurs while adding the order.
     */
    public synchronized void newOrder(Basket order) throws OrderException {
        DEBUG.trace("New order added: #%d", order.getOrderNum());
        folders.add(new Folder(order));
    }

    /**
     * Retrieves an order that is waiting to be packed.
     * 
     * @return The order to pack or null if no orders are waiting.
     * @throws OrderException if an error occurs while retrieving the order.
     */
    public synchronized Basket getOrderToPack() throws OrderException {
        return folders.stream()
                      .filter(folder -> folder.getState() == State.Waiting)
                      .findFirst()
                      .map(folder -> {
                          folder.setState(State.BeingPacked);
                          return folder.getBasket();
                      })
                      .orElse(null);
    }

    /**
     * Marks an order as packed, transitioning it to the "ToBeCollected" state.
     * 
     * @param orderNum The order number to mark as packed.
     * @return True if the order was found and marked, false otherwise.
     * @throws OrderException if an error occurs while marking the order.
     */
    public synchronized boolean informOrderPacked(int orderNum) throws OrderException {
        return folders.stream()
                      .filter(folder -> folder.getBasket().getOrderNum() == orderNum &&
                                        folder.getState() == State.BeingPacked)
                      .findFirst()
                      .map(folder -> {
                          folder.setState(State.ToBeCollected);
                          return true;
                      })
                      .orElse(false);
    }

    /**
     * Marks an order as collected, removing it from the system.
     * 
     * @param orderNum The order number to mark as collected.
     * @return True if the order was found and removed, false otherwise.
     * @throws OrderException if an error occurs while marking the order.
     */
    public synchronized boolean informOrderCollected(int orderNum) throws OrderException {
        return folders.removeIf(folder -> folder.getBasket().getOrderNum() == orderNum &&
                                          folder.getState() == State.ToBeCollected);
    }

    /**
     * Retrieves the current state of all orders in the system.
     * 
     * @return A map containing lists of order numbers grouped by their states.
     * @throws OrderException if an error occurs while retrieving the order states.
     */
    public synchronized Map<String, List<Integer>> getOrderState() throws OrderException {
        return folders.stream()
                      .collect(Collectors.groupingBy(folder -> folder.getState().name(),
                                                     Collectors.mapping(folder -> folder.getBasket().getOrderNum(),
                                                                        Collectors.toList())));
    }
}

