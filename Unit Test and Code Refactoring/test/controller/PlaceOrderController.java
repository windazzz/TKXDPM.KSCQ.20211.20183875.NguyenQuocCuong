package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.cart.Cart;
import entity.cart.CartMedia;
import common.exception.InvalidDeliveryInfoException;
import entity.invoice.Invoice;
import entity.order.Order;
import entity.order.OrderMedia;
import views.screen.popup.PopupScreen;

/**
 * This class controls the flow of place order usecase in our AIMS project
 * @author nguyenlm
 */
public class PlaceOrderController extends BaseController{

    /**
     * Just for logging purpose
     */
    private static Logger LOGGER = utils.Utils.getLogger(PlaceOrderController.class.getName());

    /**
     * This method checks the avalibility of product when user click PlaceOrder button
     * @throws SQLException
     */
    public void placeOrder() throws SQLException{
        Cart.getCart().checkAvailabilityOfProduct();
    }

    /**
     * This method creates the new Order based on the Cart
     * @return Order
     * @throws SQLException
     */
    public Order createOrder() throws SQLException{
        Order order = new Order();
        for (Object object : Cart.getCart().getListMedia()) {
            CartMedia cartMedia = (CartMedia) object;
            OrderMedia orderMedia = new OrderMedia(cartMedia.getMedia(), 
                                                   cartMedia.getQuantity(), 
                                                   cartMedia.getPrice());    
            order.getlstOrderMedia().add(orderMedia);
        }
        return order;
    }

    /**
     * This method creates the new Invoice based on order
     * @param order
     * @return Invoice
     */
    public Invoice createInvoice(Order order) {
        return new Invoice(order);
    }

    /**
     * This method takes responsibility for processing the shipping info from user
     * @param info
     * @throws InterruptedException
     * @throws IOException
     */
    public void processDeliveryInfo(HashMap info) throws InterruptedException, IOException{
        LOGGER.info("Process Delivery Info");
        LOGGER.info(info.toString());
        validateDeliveryInfo(info);
    }
    
    /**
   * The method validates the info
   * @param info
   * @throws InterruptedException
   * @throws IOException
   */
    public void validateDeliveryInfo(HashMap<String, String> info) throws InterruptedException, IOException{
    	
    }
    
    /**
     * Validate phone number
     * @param phoneNumber: user's phone number
     * @return phone number is valid or not
     */
    public boolean validatePhoneNumber(String phoneNumber) {
    	//check length
    	if (phoneNumber == null ||phoneNumber.length() != 10) return false;
    	
    	if (phoneNumber.isEmpty()) return false;
    	
    	//check prefix
        if (!phoneNumber.startsWith("0")) return false;
        
        try {
        	Integer.parseInt(phoneNumber);
        } catch (Exception e) {
			
        	return false;
		}
        return true;
    }
    /**
     * Validate receiver's name
     * @param name
     * @return name is valided or not
     */
    public boolean validateName(String name) {
    	//check null or empty
    	if (name == null || name.isEmpty()) return false;
    	
    	// name RegEx for common name
        String regex = "^[\\p{L} .'-]+$";
        return name.matches(regex);
    }
    /**
     * Validate address
     * @param address
     * @return address is valid or not
     */
    public boolean validateAddress(String address) {
    	//check null or empty
    	if (address == null || address.isEmpty()) return false;
    	
        String regex = "[A-Za-z0-9\\s,/\\.-]+";

        return address.matches(regex);
    }
    
    /**
     * validate rush order
     * @param order:
     * @return order is rush or not
     */
    public boolean validateRush(Order order) {
    	
    	// check if user choose place rush order and province = Ha Noi
    	if ((order.getIsRush()) && (order.getDeliveryInfo().get("province").equals("Hà Nội"))) 
    		return true;
    	return false;
    }
    
    /**
     * This method calculates the shipping fees of order, place rush order if province is supported
     * @param order
     * @return shippingFee
     */
    public int calculateShippingFee(Order order){
    	// calculate rush order fees if place rush order
    	int rushFees = 0;
    	if (validateRush(order)) 
    		rushFees = 10000 * order.getQuantity();
    	
    	// check if amount > 100000 VND
    	if (order.getAmount() >= 110000)
    		return 0;
    	
        Random rand = new Random();
        int fees = (int)( ( (rand.nextFloat()*10)/100 ) * order.getAmount() );
        LOGGER.info("Order Amount: " + order.getAmount() + " -- Shipping Fees: " + (fees + rushFees));
        
        return fees + rushFees;
    }
}
