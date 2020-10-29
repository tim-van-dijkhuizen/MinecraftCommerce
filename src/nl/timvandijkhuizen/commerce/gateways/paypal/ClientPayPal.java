package nl.timvandijkhuizen.commerce.gateways.paypal;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.PaymentUrl;
import nl.timvandijkhuizen.commerce.elements.Transaction;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.commerce.services.WebService;
import nl.timvandijkhuizen.commerce.webserver.QueryParameters;
import nl.timvandijkhuizen.commerce.webserver.errors.BadRequestHttpException;
import nl.timvandijkhuizen.commerce.webserver.errors.NotFoundHttpException;
import nl.timvandijkhuizen.commerce.webserver.errors.ServerErrorHttpException;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class ClientPayPal implements GatewayClient {

    public static final String COMPLETE_PATH = "/orders/complete";
    public static final String CONFIRMATION_PATH = "/orders/confirmation";
    public static final long URL_TTL = TimeUnit.HOURS.toMillis(2);
    public static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("##0.##");

    private PayPalEnvironment environment;
    private PayPalHttpClient client;
    private File template;

    public ClientPayPal(String clientId, String clientSecret, boolean testMode, File template) {
        if (testMode) {
            environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        } else {
            environment = new PayPalEnvironment.Live(clientId, clientSecret);
        }

        // Create client
        client = new PayPalHttpClient(environment);

        // Set template
        this.template = template;
    }

    @Override
    public PaymentUrl createPaymentUrl(nl.timvandijkhuizen.commerce.elements.Order order) throws Throwable {
        PurchaseUnitRequest unit = new PurchaseUnitRequest();
        
        // Get currency
        StoreCurrency currency = order.getCurrency();
        String currencyCode = currency.getCode().getCurrencyCode();
        
        // Get converted price
        float totalPrice = ShopHelper.convertPrice(order.getTotal(), currency);
        String totalValue = AMOUNT_FORMAT.format(totalPrice);

        // Add amount
        AmountWithBreakdown amount = new AmountWithBreakdown();

        amount.currencyCode(currencyCode);
        amount.value(totalValue);

        Money itemTotal = new Money()
            .currencyCode(currencyCode)
            .value(totalValue);

        amount.amountBreakdown(new AmountBreakdown().itemTotal(itemTotal));
        unit.amountWithBreakdown(amount);

        // Add items
        List<Item> items = new ArrayList<>();

        for (LineItem lineItem : order.getLineItems()) {
            ProductSnapshot product = lineItem.getProduct();
            Item item = new Item();

            // Get converted price
            float price = ShopHelper.convertPrice(product.getPrice(), currency);
            String priceValue = AMOUNT_FORMAT.format(price);
            
            item.name(product.getName());
            item.description(product.getDescription());
            item.unitAmount(new Money().currencyCode(currencyCode).value(priceValue));
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
        URL returnUrl = WebHelper.createWebUrl(COMPLETE_PATH + "?order=" + order.getUniqueId());

        ApplicationContext context = new ApplicationContext()
            .brandName(optionServerName.getValue(pluginConfig))
            .returnUrl(returnUrl.toString()).landingPage("LOGIN");

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
            .map(link -> new PaymentUrl(order.getId(), link.href(), System.currentTimeMillis() + URL_TTL))
            .findFirst().orElse(null);
    }

    @Override
    public FullHttpResponse handleWebRequest(nl.timvandijkhuizen.commerce.elements.Order order, FullHttpRequest request) throws Throwable {
        URL url = WebHelper.createWebUrl(request.uri());
        String path = url.getPath();

        if (path.equals(COMPLETE_PATH)) {
            return handleOrderComplete(order, url);
        } else if (path.equals(CONFIRMATION_PATH)) {
            return handleOrderConfirmation(order);
        } else {
            throw new NotFoundHttpException("Page not found");
        }
    }

    private FullHttpResponse handleOrderComplete(nl.timvandijkhuizen.commerce.elements.Order order, URL url) throws Throwable {
        OrderService orderService = Commerce.getInstance().getService("orders");
        QueryParameters queryParams = WebHelper.parseQuery(url);
        String paypalOrderId = queryParams.getString("token");

        // Return success if the order has already been completed
        if (order.getTransaction() != null) {
            return WebHelper.createRedirectRequest(CONFIRMATION_PATH + "?order=" + order.getUniqueId());
        }

        // Check if token parameter exists
        if (paypalOrderId == null) {
            throw new BadRequestHttpException("Missing required token parameter.");
        }

        // Try to capture payment
        OrdersCaptureRequest catureRequest = new OrdersCaptureRequest(paypalOrderId);

        try {
            HttpResponse<Order> captureResponse = client.execute(catureRequest);
            Order paypalOrder = captureResponse.result();

            // Check if the order was completed
            if (!paypalOrder.status().equals("COMPLETED")) {
                ConsoleHelper.printError(paypalOrder.toString());
                throw new ServerErrorHttpException("Failed to capture payment, please try again.");
            }

            // Complete order
            Transaction transaction = new Transaction(order.getId(), paypalOrder.id(), System.currentTimeMillis());
            
            if (!orderService.completeOrder(order, transaction)) {
                throw new ServerErrorHttpException("An error occurred while completing your order, please contact an administrator.");
            }

            return WebHelper.createRedirectRequest(CONFIRMATION_PATH + "?order=" + order.getUniqueId());
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to capture payment", e);
            throw new ServerErrorHttpException("Failed to capture payment, an internal error occurred.");
        }
    }

    private FullHttpResponse handleOrderConfirmation(nl.timvandijkhuizen.commerce.elements.Order order) {
        WebService webService = Commerce.getInstance().getService("web");

        // Create map with variables
        Map<String, Object> variables = new HashMap<>();

        variables.put("order", order);

        // Render template
        String content = null;

        if (template != null) {
            content = webService.renderTemplate(template, variables);
        } else {
            content = webService.renderTemplate("gateways/paypal/confirmation.html", variables);
        }

        // Return response
        return WebHelper.createResponse(content);
    }

}
