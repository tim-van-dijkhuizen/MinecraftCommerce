package nl.timvandijkhuizen.commerce.gateways.paypal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.base.PaymentUrl;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;

public class ClientPayPal implements GatewayClient {

	public static final long URL_TTL = TimeUnit.HOURS.toMillis(2);
	
	private PayPalEnvironment environment;
	private PayPalHttpClient client;
	
	public ClientPayPal(String clientId, String clientSecret, boolean testMode) {		
	    if(testMode) {
	    	environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
	    } else {
	    	environment = new PayPalEnvironment.Live(clientId, clientSecret);
	    }
		
	    // Create client
	    client = new PayPalHttpClient(environment);
	}
    
    @Override
    public PaymentUrl createPaymentUrl(nl.timvandijkhuizen.commerce.elements.Order order) throws Exception {
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
		requestBody.purchaseUnits(Arrays.asList(unit));
		
		// Set application context
		YamlConfig pluginConfig = Commerce.getInstance().getConfig();
		ConfigOption<String> optionServerName = pluginConfig.getOption("general.serverName");
		URL returnUrl = WebHelper.createWebUrl("/order/confirmation?order=" + order.getUniqueId());
		
	    ApplicationContext context = new ApplicationContext()
			.brandName(optionServerName.getValue(pluginConfig))
			.returnUrl(returnUrl.toString())
			.landingPage("LOGIN");
	    
	    requestBody.applicationContext(context);
		
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
			.map(link -> new PaymentUrl(link.href(), System.currentTimeMillis() + URL_TTL))
			.findFirst()
			.orElse(null);
    }

    @Override
    public FullHttpResponse handleWebRequest(nl.timvandijkhuizen.commerce.elements.Order order, FullHttpRequest response) throws Exception {
        String content = "PayPal gateway response\n";
        
        content += "UUID: " + order.getUniqueId();
        content += "Player UUID: " + order.getPlayerUniqueId();
        content += "Player Name: " + order.getPlayerName();
        content += "Currency: " + order.getCurrency().getCode();
        content += "Total: " + order.getTotal();
        
        return WebHelper.createResponse(content);
    }

}
