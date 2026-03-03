package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    private ProductRepositoryImpl productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepositoryImpl();
    }

    @Test
    void testCreateAndFind() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        productRepository.create(product);
        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product.getProductId(), savedProduct.getProductId());
        assertEquals(product.getProductName(), savedProduct.getProductName());
        assertEquals(product.getProductQuantity(), savedProduct.getProductQuantity());
    }

    @Test
    void testCreateWithNullId() {
        Product product = new Product();
        product.setProductName("Sampo Cap Bango");
        product.setProductQuantity(50);
        Product savedProduct = productRepository.create(product);
        assertNotNull(savedProduct.getProductId());
        assertFalse(savedProduct.getProductId().isEmpty());
    }

    @Test
    void testFindById() {
        Product product = new Product();
        product.setProductId("123");
        productRepository.create(product);
        Product foundProduct = productRepository.findById("123");
        assertNotNull(foundProduct);
        assertEquals("123", foundProduct.getProductId());
    }

    @Test
    void testFindByIdNotFound() {
        Product foundProduct = productRepository.findById("non-existent-id");
        assertNull(foundProduct);
    }

    @Test
    void testEditSuccess() {
        Product product = new Product();
        product.setProductId("123");
        product.setProductName("Original");
        productRepository.create(product);
        Product updatedProduct = new Product();
        updatedProduct.setProductId("123");
        updatedProduct.setProductName("Updated");
        Product result = productRepository.edit(updatedProduct);
        assertNotNull(result);
        assertEquals("Updated", result.getProductName());
        assertEquals("Updated", productRepository.findById("123").getProductName());
    }

    @Test
    void testEditNotFound() {
        Product product = new Product();
        product.setProductId("123");
        productRepository.create(product);
        Product updatedProduct = new Product();
        updatedProduct.setProductId("456");
        updatedProduct.setProductName("Updated");
        Product result = productRepository.edit(updatedProduct);
        assertNull(result);
    }

    @Test
    void testDelete() {
        Product product = new Product();
        product.setProductId("123");
        productRepository.create(product);
        productRepository.delete("123");
        assertNull(productRepository.findById("123"));
    }

    @Test
    void testCreateWithExistingId() {
        Product product = new Product();
        product.setProductId("existing-id-123");
        product.setProductName("Sampo Original");
        product.setProductQuantity(10);
        Product savedProduct = productRepository.create(product);
        assertEquals("existing-id-123", savedProduct.getProductId());
    }

    @Test
    void testFindByIdButIdDoesNotMatch() {
        Product product = new Product();
        product.setProductId("123");
        product.setProductName("Sampo Cap Bango");
        productRepository.create(product);
        Product foundProduct = productRepository.findById("456");
        assertNull(foundProduct);
    }
}