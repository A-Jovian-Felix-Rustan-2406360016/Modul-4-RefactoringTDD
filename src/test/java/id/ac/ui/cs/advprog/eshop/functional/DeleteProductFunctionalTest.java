package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class DeleteProductFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    void createProductForDelete(ChromeDriver driver, String name) {
        driver.get(baseUrl + "/product/create");
        driver.findElement(By.id("nameInput")).sendKeys(name);
        driver.findElement(By.id("quantityInput")).sendKeys("10");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Test
    void deleteProduct_isSuccessful(ChromeDriver driver) {
        String productToDelete = "Produk Dihapus";
        createProductForDelete(driver, productToDelete);
        driver.findElement(By.xpath("//td[contains(text(), '" + productToDelete + "')]/following-sibling::td/a[contains(@class, 'btn-danger')]")).click();
        String pageSource = driver.getPageSource();
        assertFalse(pageSource.contains(productToDelete));
    }
}