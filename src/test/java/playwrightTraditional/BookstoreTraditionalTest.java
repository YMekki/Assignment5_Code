package playwrightTraditional;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class BookstoreTraditionalTest {

    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    @AfterAll
    static void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    void bookstoreTraditionalFlow() {
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setRecordVideoDir(Paths.get("videos/"))
                        .setRecordVideoSize(1280, 720)
        );

        Page page = context.newPage();

        page.navigate("https://depaul.bncollege.com/");
        page.getByPlaceholder("Enter your search details (").click();
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("#facet-brand")
                .getByRole(AriaRole.LIST)
                .locator("label")
                .filter(new Locator.FilterOptions().setHasText("brand JBL (10)"))
                .getByRole(AriaRole.IMG)
                .click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.locator("label")
                .filter(new Locator.FilterOptions().setHasText("Color Black (5)"))
                .getByRole(AriaRole.IMG)
                .click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.locator("#facet-price").getByRole(AriaRole.IMG).click();

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
        page.getByLabel("Add to cart").click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();

        page.getByLabel("Enter Promo Code").click();
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();

        page.getByLabel("Proceed To Checkout").click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();

        page.getByPlaceholder("Please enter your first name").click();
        page.getByPlaceholder("Please enter your first name").fill("Yasin");
        page.getByPlaceholder("Please enter your first name").press("Tab");
        page.getByPlaceholder("Please enter your last name").fill("Mekki");
        page.getByPlaceholder("Please enter your last name").press("Tab");
        page.getByPlaceholder("Please enter a valid email").fill("Yasin@example.com");
        page.getByPlaceholder("Please enter a valid email").press("Tab");
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("United States: +")).press("Tab");
        page.getByPlaceholder("Please enter a valid phone").fill("55555555555");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
        page.getByLabel("Remove product JBL Quantum").click();

        context.close();
    }
}