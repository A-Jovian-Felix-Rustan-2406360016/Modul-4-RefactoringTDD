package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {
    private CarRepositoryImpl carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepositoryImpl();
    }

    @Test
    void testCreateAndFindAll() {
        Car car = new Car();
        car.setCarId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        car.setCarName("Toyota Supra");
        car.setCarColor("Red");
        car.setCarQuantity(10);
        carRepository.create(car);
        Iterator<Car> carIterator = carRepository.findAll();
        assertTrue(carIterator.hasNext());
        Car savedCar = carIterator.next();
        assertEquals(car.getCarId(), savedCar.getCarId());
        assertEquals(car.getCarName(), savedCar.getCarName());
        assertEquals(car.getCarColor(), savedCar.getCarColor());
        assertEquals(car.getCarQuantity(), savedCar.getCarQuantity());
    }

    @Test
    void testCreateWithNullId() {
        Car car = new Car();
        car.setCarName("Honda Civic");
        car.setCarColor("Black");
        car.setCarQuantity(5);
        Car savedCar = carRepository.create(car);
        assertNotNull(savedCar.getCarId());
        assertFalse(savedCar.getCarId().isEmpty());
    }

    @Test
    void testFindByIdSuccess() {
        Car car = new Car();
        car.setCarId("123");
        carRepository.create(car);
        Car foundCar = carRepository.findById("123");
        assertNotNull(foundCar);
        assertEquals("123", foundCar.getCarId());
    }

    @Test
    void testFindByIdNotFound() {
        Car car = new Car();
        car.setCarId("123");
        carRepository.create(car);
        Car foundCar = carRepository.findById("456");
        assertNull(foundCar);
    }

    @Test
    void testUpdateSuccess() {
        Car car = new Car();
        car.setCarId("123");
        car.setCarName("Old Name");
        car.setCarColor("Old Color");
        car.setCarQuantity(1);
        carRepository.create(car);
        Car updatedCarData = new Car();
        updatedCarData.setCarName("New Name");
        updatedCarData.setCarColor("New Color");
        updatedCarData.setCarQuantity(2);
        Car result = carRepository.update("123", updatedCarData);
        assertNotNull(result);
        assertEquals("New Name", result.getCarName());
        assertEquals("New Color", result.getCarColor());
        assertEquals(2, result.getCarQuantity());
    }

    @Test
    void testUpdateNotFound() {
        Car car = new Car();
        car.setCarId("123");
        carRepository.create(car);
        Car updatedCarData = new Car();
        updatedCarData.setCarName("New Name");
        Car result = carRepository.update("456", updatedCarData);
        assertNull(result);
    }

    @Test
    void testDelete() {
        Car car = new Car();
        car.setCarId("123");
        carRepository.create(car);
        carRepository.delete("123");
        Car foundCar = carRepository.findById("123");
        assertNull(foundCar);
    }
}