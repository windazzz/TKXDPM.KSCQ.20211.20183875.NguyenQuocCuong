package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import entity.order.Order;

class ValidateRushTest {

    private PlaceOrderController placeOrderController;
    
	@BeforeEach
	void setUp() throws Exception {
		placeOrderController = new PlaceOrderController();
	}
	
	@ParameterizedTest
	@CsvSource({
	            "Hà Nội, true, true",
	            "An Giang, true, false",
	            "Hà Nội, false, false",
	            "An Giang, false, false"
	    })
	@Test
	void testValidateRush(String province, boolean isRush, boolean expected) {
		HashMap<String, String> message =  new HashMap();
		
		message.put("province", province);
		
		Order order = new Order();
		order.setIsRush(isRush);
		order.setDeliveryInfo(message);
		
		boolean isValid = placeOrderController.validateRush(order);
		assertEquals(expected, isValid);
		
	}

}
