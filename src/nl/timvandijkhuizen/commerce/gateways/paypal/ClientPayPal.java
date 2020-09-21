package nl.timvandijkhuizen.commerce.gateways.paypal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountBreakdown;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Item;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Money;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.base.PaymentResponse;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.OrderPayment;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;

public class ClientPayPal implements GatewayClient {

	private PayPalEnvironment environment;
	private PayPalHttpClient client;
	private ApplicationContext context;
	
	public ClientPayPal(String clientId, String clientSecret, boolean testMode) {
		YamlConfig pluginConfig = Commerce.getInstance().getConfig();
		ConfigOption<String> optionServerName = pluginConfig.getOption("general.serverName");
		
	    if(testMode) {
	    	environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
	    } else {
	    	environment = new PayPalEnvironment.Live(clientId, clientSecret);
	    }
		
	    // Create client
	    client = new PayPalHttpClient(environment);
	    
	    // Create context
		context = new ApplicationContext()
			.brandName(optionServerName.getValue(pluginConfig))
			.returnUrl(Commerce.createWebUrl("order/confirmation"))
			.cancelUrl(Commerce.createWebUrl("order/cancelled"))
			.landingPage("LOGIN");
	}
    
    @Override
    public String createPaymentUrl(nl.timvandijkhuizen.commerce.elements.Order order) throws Exception {
    	PurchaseUnitRequest unit = new PurchaseUnitRequest();
    	String currency = order.getCurrency().getCode();
    	String totalPrice = String.valueOf(order.getTotal());
    	
    	// Add amount
		AmountWithBreakdown amount = new AmountWithBreakdown();
		
		amount.currencyCode(currency);
		amount.value(totalPrice);
		
		Money itemTotal = new Money()
			.currencyCode(currency)
			.value(totalPrice);
		
		amount.amountBreakdown(new AmountBreakdown().itemTotal(itemTotal));
		unit.amountWithBreakdown(amount);
		
		// Add items
		List<Item> items = new ArrayList<>();
		
		for(LineItem lineItem : order.getLineItems()) {
			ProductSnapshot product = lineItem.getProduct();
			String price = String.valueOf(product.getPrice());
			Item item = new Item();
			
			item.name(product.getName());
			item.description(product.getDescription());
			item.unitAmount(new Money().currencyCode(currency).value(price));
			item.quantity(String.valueOf(lineItem.getQuantity()));
			
			items.add(item);
		}
		
		unit.items(items);
		
		// Create body
		OrderRequest requestBody = new OrderRequest();
		
		requestBody.checkoutPaymentIntent("CAPTURE");
		requestBody.applicationContext(context);
		requestBody.purchaseUnits(Arrays.asList(unit));
		
		// Create request
		OrdersCreateRequest request = new OrdersCreateRequest();

		request.requestBody(requestBody);
		
		// Handle response
		HttpResponse<Order> response = client.execute(request);
		
		// Get approval link from order
		Order paypalOrder = response.result();
		List<LinkDescription> links = paypalOrder.links();
		
		return links.stream()
			.filter(link -> link.rel().equals("approve"))
			.map(link -> link.href())
			.findFirst()
			.orElse(null);
    }

    @Override
    public OrderPayment processWebhook(PaymentResponse response) throws Exception {
        return null;
    }

}
