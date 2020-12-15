package nl.timvandijkhuizen.commerce;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.bukkit.Material;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.commands.CommandCommerce;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeOrderEffect;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypePort;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeStorageType;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeStoreCurrency;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeTerms;
import nl.timvandijkhuizen.commerce.effects.OrderEffectDefault;
import nl.timvandijkhuizen.commerce.services.CacheService;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.commerce.services.StorageService;
import nl.timvandijkhuizen.commerce.services.UserService;
import nl.timvandijkhuizen.commerce.services.WebService;
import nl.timvandijkhuizen.commerce.storagetypes.StorageMysql;
import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeFile;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeList;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.services.Service;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class Commerce extends PluginBase {

    public static final Material[] MATERIAL_ICONS = Stream.of(Material.values()).filter(icon -> icon.isItem() && icon != Material.AIR).toArray(Material[]::new);
    public static final StoreCurrency DEFAULT_CURRENCY = new StoreCurrency(Currency.getInstance("USD"), 1, "$###,###,##0.00", ',', '.');
    
    private static Commerce instance;
    private YamlConfig config;

    // Configuration options
    private ConfigOption<String> configServerName;
    private ConfigOption<Boolean> configDevMode;
    private ConfigOption<List<StoreCurrency>> configCurrencies;
    private ConfigOption<StoreCurrency> configBaseCurrency;
    private ConfigOption<SimpleDateFormat> configDateFormat;
    private ConfigOption<List<String>> configTerms;
    private ConfigOption<OrderEffect> configOrderEffect;
    private ConfigOption<String> configWebHost;
    private ConfigOption<Integer> configWebPort;
    private ConfigOption<File> configWebCertificate;
    private ConfigOption<File> configWebPrivateKey;
    private ConfigOption<File> configWebFavicon;
    private ConfigOption<StorageType> configStorageType;

    @Override
    public void init() throws Throwable {
        instance = this;

        // Setup configuration options
        config = new YamlConfig(this);

        // Create options
        ConfigTypeFile configTypeCert = new ConfigTypeFile(new Pattern[] { Pattern.compile("^.*\\.pem$") });
        ConfigTypeFile configTypeIco = new ConfigTypeFile(new Pattern[] { Pattern.compile("^.*\\.ico$") });

        configServerName = new ConfigOption<>("general.serverName", "Server Name", XMaterial.PAPER, ConfigTypes.STRING)
            .setRequired(true)
            .setDefaultValue("Minecraft Commerce");

        configDevMode = new ConfigOption<>("general.devMode", "Dev Mode", XMaterial.REDSTONE, ConfigTypes.BOOLEAN)
            .setRequired(true)
            .setDefaultValue(false);

        configCurrencies = new ConfigOption<>("general.currencies", "Currencies", XMaterial.SUNFLOWER, new ConfigTypeList<StoreCurrency>(StoreCurrency.class, "Currencies", XMaterial.SUNFLOWER))
            .setRequired(true)
            .setDefaultValue(Arrays.asList(DEFAULT_CURRENCY));

        configBaseCurrency = new ConfigOption<>("general.baseCurrency", "Base Currency", XMaterial.SUNFLOWER, new ConfigTypeStoreCurrency())
            .setRequired(true)
            .setDefaultValue(DEFAULT_CURRENCY);
        
        configDateFormat = new ConfigOption<>("general.dateFormat", "Date Format", XMaterial.CLOCK, ConfigTypes.DATE_FORMAT)
            .setRequired(true)
            .setDefaultValue(new SimpleDateFormat("yyyy-MM-dd HH:mm"));

        configTerms = new ConfigOption<>("general.termsAndConditions", "Terms & Conditions", XMaterial.WRITTEN_BOOK, new ConfigTypeTerms());
        
        configOrderEffect = new ConfigOption<>("general.completeEffect", "Order Complete Effect", XMaterial.FIREWORK_ROCKET, new ConfigTypeOrderEffect())
            .setRequired(true)
            .setDefaultValue(new OrderEffectDefault());

        configWebHost = new ConfigOption<>("webserver.host", "Webserver Host", XMaterial.COBWEB, ConfigTypes.STRING)
            .setRequired(true)
            .setDefaultValue(getServer().getIp());

        configWebPort = new ConfigOption<>("webserver.port", "Webserver Port", XMaterial.COBWEB, new ConfigTypePort())
            .setRequired(true)
            .setDefaultValue(8080)
            .setMeta(new DataArguments(true));

        configWebCertificate = new ConfigOption<>("webserver.certificate", "Webserver SSL Certificate", XMaterial.TRIPWIRE_HOOK, configTypeCert)
            .setMeta(new DataArguments(true));

        configWebPrivateKey = new ConfigOption<>("webserver.privateKey", "Webserver SSL Private Key", XMaterial.TRIPWIRE_HOOK, configTypeCert)
            .setMeta(new DataArguments(true));
        
        configWebFavicon = new ConfigOption<>("webserver.favicon", "Webserver Favicon", XMaterial.PAINTING, configTypeIco);
        
        configStorageType = new ConfigOption<>("storage.type", "Storage Type", XMaterial.BARREL, new ConfigTypeStorageType())
            .setRequired(true)
            .setDefaultValue(new StorageMysql())
            .setMeta(new DataArguments(true));

        // Add options
        config.addOption(configServerName);
        config.addOption(configDevMode);
        config.addOption(configCurrencies);
        config.addOption(configBaseCurrency);
        config.addOption(configDateFormat);
        config.addOption(configTerms);
        config.addOption(configOrderEffect);
        config.addOption(configWebHost);
        config.addOption(configWebPort);
        config.addOption(configWebCertificate);
        config.addOption(configWebPrivateKey);
        config.addOption(configWebFavicon);
        config.addOption(configStorageType);
    }

    @Override
    public void load() throws Throwable {
        config.setDefaultOptions();
        config.save();

        ConsoleHelper.setDevMode(configDevMode.getValue(config));
    }

    @Override
    public void ready() throws Throwable {
        Map<String, String> setupErrors = getServiceErrors();

        if (!setupErrors.isEmpty()) {
            ConsoleHelper.printError("========================================================");
            ConsoleHelper.printError("Please fix the setup errors below to use Commerce.");
            ConsoleHelper.printError("");
            ConsoleHelper.printError("Errors:");

            for (Entry<String, String> error : setupErrors.entrySet()) {
                ConsoleHelper.printError(UI.TAB + Icon.SQUARE + " " + error.getKey() + ": " + error.getValue());
            }

            ConsoleHelper.printError("");
            ConsoleHelper.printError("========================================================");
        }
    }

    @Override
    public Service[] registerServices() throws Throwable {
        CommandService commandService = new CommandService(this);

        // Register command
        commandService.register(new CommandCommerce());

        return new Service[] {
            new StorageService(),
            new CacheService(),
            new MenuService(),
            new CategoryService(),
            new ProductService(),
            new GatewayService(),
            new UserService(),
            new OrderService(),
            new FieldService(),
            commandService,
            new WebService()
        };
    }

    public static Commerce getInstance() {
        return instance;
    }

    public YamlConfig getConfig() {
        return config;
    }

    public StorageType getStorage() {
        StorageService service = getService("storage");
        
        if(service == null) {
            throw new RuntimeException("Storage service hasn't been initialized yet.");
        }
        
        return service.getStorage();
    }

}
