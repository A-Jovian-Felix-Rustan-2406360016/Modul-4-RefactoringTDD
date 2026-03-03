package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class EditProductFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    void createProductForEdit(ChromeDriver driver, String name) {
        driver.get(baseUrl + "/product/create");
        driver.findElement(By.id("nameInput")).sendKeys(name);
        driver.findElement(By.id("quantityInput")).sendKeys("10");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Test
    void editProduct_isSuccessful(ChromeDriver driver) {
        String originalName = "Tes Sebelum Edit";
        createProductForEdit(driver, originalName);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[contains(text(), '" + originalName + "')]/following-sibling::td/a[contains(@class, 'btn-warning')]")));editButton.click();
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameInput")));
        nameInput.clear();
        String newName = "Tes Sesudah Edit";
        nameInput.sendKeys(newName);
        WebElement quantityInput = driver.findElement(By.id("quantityInput"));
        quantityInput.clear();
        quantityInput.sendKeys("50");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/product/list"));
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(newName), "Nama produk baru tidak ditemukan!");
        assertFalse(pageSource.contains(originalName), "Nama produk lama masih ada!");
    }
}