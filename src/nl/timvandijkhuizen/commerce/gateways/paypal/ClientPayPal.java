package nl.timvandijkhuizen.commerce.gateways.paypal;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountBreakdown;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Capture;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Money;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnit;
import com.paypal.orders.PurchaseUnitRequest;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Transaction;
import nl.timvandijkhuizen.commerce.helpers.JsonHelper;
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
    public static final String PARTIAL_PATH = "/orders/partial";
    public static final String CONFIRMATION_PATH = "/orders/confirmation";

    private Gateway gateway;
    private PayPalEnvironment environment;
    private PayPalHttpClient client;
    private File template;
    public DecimalFormat amountFormat;

    public ClientPayPal(Gateway gateway, String clientId, String clientSecret, boolean testMode, File template) {
        this.gateway = gateway;
        
        // Create environment and client
        if (testMode) {
            environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        } else {
            environment = new PayPalEnvironment.Live(clientId, clientSecret);
        }

        client = new PayPalHttpClient(environment);

        // Set template
        this.template = template;
        
        // Create format
        DecimalFormatSymbols amountSymbols = new DecimalFormatSymbols();
            
        amountSymbols.setDecimalSeparator('.');
        amountFormat = new DecimalFormat("##0.##", amountSymbols);
    }

    @Override
    public String createPaymentUrl(nl.timvandijkhuizen.commerce.elements.Order order) throws Throwable {
        PurchaseUnitRequest unit = new PurchaseUnitRequest();
        
        // Get currency
        StoreCurrency currency = order.getCurrency();
        String currencyCode = currency.getCode().getCurrencyCode();
        
        // Get converted price
        float unpaid = order.getTotal() - order.getAmountPaid();
        float totalPrice = ShopHelper.convertPrice(unpaid, currency);
        String totalValue = amountFormat.format(totalPrice);

        // Add amount
        AmountWithBreakdown amount = new AmountWithBreakdown();

        amount.currencyCode(currencyCode);
        amount.value(totalValue);

        Money itemTotal = new Money()
            .currencyCode(currencyCode)
            .value(totalValue);

        amount.amountBreakdown(new AmountBreakdown().itemTotal(itemTotal));
        unit.amountWithBreakdown(amount);

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
            .map(link -> link.href())
            .findFirst().orElse(null);
    }

    @Override
    public FullHttpResponse handleWebRequest(nl.timvandijkhuizen.commerce.elements.Order order, FullHttpRequest request) throws Throwable {
        URL url = WebHelper.createWebUrl(request.uri());
        String path = url.getPath();

        if (path.equals(COMPLETE_PATH)) {
            return handleOrderComplete(order, url);
        } else if (path.equals(PARTIAL_PATH)) {
            return handleOrderPartial(order);
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
        if (order.isCompleted()) {
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

            // Get order information
            // ===========================

            PurchaseUnit unit = paypalOrder.purchaseUnits().get(0);
            StoreCurrency transactionCurrency = null;
            float transactionAmount = 0;
            
            for(Capture capture : unit.payments().captures()) {
                Money money = capture.amount();
                String currencyCode = money.currencyCode();
                StoreCurrency currency = ShopHelper.getCurrencyByCode(currencyCode);
                float amount = Float.valueOf(money.value());
                
                // Set transaction currency
                if(transactionCurrency == null) {
                    transactionCurrency = currency;
                }
                
                // Add raw amount or convert if its a different currency
                if(currency.equals(transactionCurrency)) {
                    transactionAmount += amount;
                } else {
                    transactionAmount += ShopHelper.convertPrice(amount, currency, transactionCurrency);
                }
                
            }
            
            // Create transaction
            int orderId = order.getId();
            String reference = paypalOrder.id();
            JsonObject meta = JsonHelper.toJsonObject(paypalOrder);
            long dateCreated = System.currentTimeMillis();
            
            Transaction transaction = new Transaction(orderId, gateway, transactionCurrency, reference, transactionAmount, meta, dateCreated);
            
            if (!orderService.completeOrder(order, transaction)) {
                return WebHelper.createRedirectRequest(PARTIAL_PATH + "?order=" + order.getUniqueId());
            }

            return WebHelper.createRedirectRequest(CONFIRMATION_PATH + "?order=" + order.getUniqueId());
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to capture payment", e);
            throw new ServerErrorHttpException("Failed to capture payment, an internal error occurred.");
        }
    }

    private FullHttpResponse handleOrderPartial(nl.timvandijkhuizen.commerce.elements.Order order) {
        WebService webService = Commerce.getInstance().getService("web");

        // Create map with variables
        Map<String, Object> variables = new HashMap<>();

        variables.put("order", order);

        // Render template
        String content = webService.renderTemplate("gateways/paypal/partial.html", variables);
        
        return WebHelper.createResponse(content);
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
