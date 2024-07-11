package org.example;
//import com.sun.glass.events.KeyEvent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;

public class App {
    public static void main(String[] args) throws InterruptedException {


        /** *************************
         * Initialize the context and Open Web page
         * **************************
         */
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", Collections.singletonMap("autofill.credit_card_enabled", false)); //To Block the save payment popup
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://demo.dev.tap.company/v2/sdk/card");
        WebDriverWait wait = new WebDriverWait(driver, 20);

        List<String> cardNumber = Arrays.asList("5123450000000008", "4508750015741019");
        List<String> expDates = Arrays.asList("01/39", "01/39");
        List<String> cvv = Arrays.asList("100", "100");
        int cardCount = 2;

        for (int i=0; i< cardCount; i++){
            /**
             * Set the currency and authentication scope
             */
            driver.navigate().refresh();
            WebElement currencyList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currency")));
            Select currencyListValue = new Select(currencyList);
            currencyListValue.selectByValue("BHD");

            WebElement authTokenList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("scope")));
            Select authTokenListValue = new Select(authTokenList);
            authTokenListValue.selectByValue("AuthenticatedToken");

            /**
             * Wait until the iframe is ready to enter the card details
             */
            Thread.sleep(10000);
            WebDriver tapCardIframe = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("tap-card-iframe")));

            /**
             * Enter the card details
             */

            WebElement cardNumberField = tapCardIframe.findElement(By.cssSelector("#root #card_input_mini"));
            cardNumberField.click();
            WebElement cardNumberField2 = tapCardIframe.findElement(By.cssSelector("#root #card_input"));
            cardNumberField2.sendKeys(cardNumber.get(i));

            WebElement dateInput = tapCardIframe.findElement(By.id("date_input"));
            dateInput.sendKeys(expDates.get(i));

            WebElement cvvInput = tapCardIframe.findElement(By.id("cvv_input"));
            cvvInput.sendKeys(cvv.get(i));

            Thread.sleep(2000);
            WebElement cardSaveSwitch = tapCardIframe.findElement(By.id("switch"));
            cardSaveSwitch.click();

            driver.switchTo().defaultContent();

            /**
             * Switched to default content and hit Create Token
             */

            WebElement createTokenButton = driver.findElement(By.xpath("//button[contains(text(), 'Create Token')]"));
            createTokenButton.click();

            /**
             * Wait of Iframes to be ready and switch to them to hit "Submit" button
             */

            Thread.sleep(20000);
            WebElement iframeElement4 = driver.findElement(By.id("tap-card-iframe"));
            driver.switchTo().frame(iframeElement4);

            WebElement iframeElement3 = driver.findElement(By.id("tap-card-iframe-authentication"));
            driver.switchTo().frame(iframeElement3);

            WebElement iframeElement2 = driver.findElement(By.id("challengeFrame"));
            driver.switchTo().frame(iframeElement2);

            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("acssubmit")));
            submitBtn.click();

            driver.switchTo().defaultContent();

            /**
             * Switched to default content and wait for Response to be available
             */

            Thread.sleep(10000);
            WebElement ResponseRadio = driver.findElement(By.id("Response ✅"));
            ResponseRadio.click();

            /**
             * Get the response and print the data on console
             */
            WebElement codeBlockContent = driver.findElement(By.xpath("//article[@data-testid='CodeBlock']"));
            // Get the text content of the element
            String contentText = codeBlockContent.getText();
            System.out.println("****************************************");
            System.out.println("Response for card : " + cardNumber.get(i));
            System.out.println("****************************************");
            System.out.println(contentText);
            driver.switchTo().defaultContent();

        }
    }
}
