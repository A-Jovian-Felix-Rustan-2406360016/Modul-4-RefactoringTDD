package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class PaymentFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    String createOrderAndGoToPayment(ChromeDriver driver) {
        driver.get(baseUrl + "/order/create");
        driver.findElement(By.name("author")).sendKeys("Jovian Felix");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        return driver.getCurrentUrl();
    }

    @Test
    void testVoucherPaymentSuccess(ChromeDriver driver) {
        createOrderAndGoToPayment(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement methodSelect = driver.findElement(By.id("method"));
        new Select(methodSelect).selectByValue("VOUCHER");

        driver.findElement(By.name("voucherCode")).sendKeys("ESHOP12345678ABCD");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/pay"));
        assertTrue(driver.getPageSource().contains("SUCCESS") || driver.getPageSource().contains("Successful"));
    }

    @Test
    void testBankTransferFieldsToggle(ChromeDriver driver) {
        createOrderAndGoToPayment(driver);

        WebElement methodSelect = driver.findElement(By.id("method"));
        WebElement voucherField = driver.findElement(By.id("voucherField"));
        WebElement bankField = driver.findElement(By.id("bankField"));

        assertTrue(voucherField.isDisplayed());
        assertFalse(bankField.isDisplayed());

        new Select(methodSelect).selectByValue("BANK_TRANSFER");

        assertTrue(bankField.isDisplayed());
        assertFalse(voucherField.isDisplayed());
    }

    @Test
    void testBankTransferPaymentSuccess(ChromeDriver driver) {
        createOrderAndGoToPayment(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        new Select(driver.findElement(By.id("method"))).selectByValue("BANK_TRANSFER");

        driver.findElement(By.name("bankName")).sendKeys("BCA");
        driver.findElement(By.name("referenceCode")).sendKeys("REF-12345");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/pay"));
        assertTrue(driver.getPageSource().contains("SUCCESS") || driver.getPageSource().contains("Successful"));
    }
}