package playwrightLLM;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class BookstoreLLMTest {

    @Test
    public void testDePaulBookstoreFlow() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
            );

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("https://depaul.bncollege.com/");
            page.waitForLoadState();
            page.waitForTimeout(4000);

            try {
                page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Accept")).click();
            } catch (Exception ignored) {
            }

            page.waitForTimeout(2000);

            try {
                page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Search")).click();
            } catch (Exception ignored) {
            }

            page.waitForTimeout(2000);

            page.locator("input:visible").first().fill("earbuds");
            page.locator("input:visible").first().press("Enter");

            page.waitForLoadState();
            page.waitForTimeout(5000);

            assertTrue(
                page.content().toLowerCase().contains("earbuds")
                    || page.url().toLowerCase().contains("earbuds")
                    || page.url().toLowerCase().contains("search")
            );

            context.close();
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }
}