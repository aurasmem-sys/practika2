package com.mysuperproject.atelier;

import com.mysuperproject.atelier.entity.*;
import com.mysuperproject.atelier.pool.ConnectionPool;
import com.mysuperproject.atelier.repository.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InfrastructureIntegrationTest {

    private static final String TEST_DB_FILE = "test_atelier.db";

    @BeforeAll
    public static void setupDatabase() throws Exception {
        // Remove old test db if exists
        File dbFile = new File(TEST_DB_FILE);
        if (dbFile.exists()) {
            dbFile.delete();
        }

        // Initialize test DB
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            String ddl = new String(Files.readAllBytes(Paths.get("db/DDL.sql")));
            for (String sql : ddl.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }

            String dml = new String(Files.readAllBytes(Paths.get("db/DML.sql")));
            for (String sql : dml.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }

    @AfterAll
    public static void tearDownDatabase() {
        ConnectionPool.getInstance().closePool();
    }

    @Test
    @Order(1)
    public void testClientRepository() {
        ClientRepository repo = new ClientRepository();
        
        // Test findAll
        List<Client> clients = repo.findAll();
        assertFalse(clients.isEmpty(), "Clients should not be empty");

        // Test save
        Client newClient = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+380123456789")
                .email("john.doe@example.com")
                .build();
        Client saved = repo.save(newClient);
        assertNotNull(saved.getId(), "Saved client should have an ID");

        // Test findById
        Optional<Client> found = repo.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());

        // Test update
        saved.setFirstName("Johnny");
        repo.update(saved);
        Optional<Client> updated = repo.findById(saved.getId());
        assertEquals("Johnny", updated.get().getFirstName());

        // Test delete
        assertTrue(repo.delete(saved.getId()));
        assertFalse(repo.findById(saved.getId()).isPresent());
    }

    @Test
    @Order(2)
    public void testEmployeeRepository() {
        EmployeeRepository repo = new EmployeeRepository();
        
        List<Employee> list = repo.findAll();
        assertFalse(list.isEmpty());

        Employee e = Employee.builder()
                .firstName("Test")
                .lastName("Worker")
                .position("Assistant")
                .phoneNumber("+380000000000")
                .build();
        Employee saved = repo.save(e);
        assertNotNull(saved.getId());

        saved.setPosition("Manager");
        repo.update(saved);
        assertEquals("Manager", repo.findById(saved.getId()).get().getPosition());

        assertTrue(repo.delete(saved.getId()));
    }

    @Test
    @Order(3)
    public void testMaterialRepository() {
        MaterialRepository repo = new MaterialRepository();
        
        List<Material> list = repo.findAll();
        assertFalse(list.isEmpty());

        Material m = Material.builder()
                .materialName("Silk")
                .unit("m")
                .pricePerUnit(new BigDecimal("1500.00"))
                .build();
        Material saved = repo.save(m);
        assertNotNull(saved.getId());

        saved.setPricePerUnit(new BigDecimal("1600.00"));
        repo.update(saved);
        assertEquals(new BigDecimal("1600.00"), repo.findById(saved.getId()).get().getPricePerUnit());

        assertTrue(repo.delete(saved.getId()));
    }

    @Test
    @Order(4)
    public void testServiceRepository() {
        ServiceRepository repo = new ServiceRepository();
        
        List<Service> list = repo.findAll();
        assertFalse(list.isEmpty());

        Service s = Service.builder()
                .serviceName("Custom Tailoring")
                .description("Bespoke suit")
                .basePrice(new BigDecimal("5000.00"))
                .build();
        Service saved = repo.save(s);
        assertNotNull(saved.getId());

        saved.setBasePrice(new BigDecimal("5500.00"));
        repo.update(saved);
        assertEquals(new BigDecimal("5500.00"), repo.findById(saved.getId()).get().getBasePrice());

        assertTrue(repo.delete(saved.getId()));
    }

    @Test
    @Order(5)
    public void testOrderRepository() {
        OrderRepository repo = new OrderRepository();
        
        List<Order> list = repo.findAll();
        assertFalse(list.isEmpty());

        Order o = Order.builder()
                .clientId(1)
                .employeeId(1)
                .orderDate(LocalDate.now())
                .status("Нове")
                .totalPrice(new BigDecimal("100.00"))
                .build();
        Order saved = repo.save(o);
        assertNotNull(saved.getId());

        saved.setStatus("В роботі");
        repo.update(saved);
        assertEquals("В роботі", repo.findById(saved.getId()).get().getStatus());

        assertTrue(repo.delete(saved.getId()));
    }

    @Test
    @Order(6)
    public void testOrderMaterialRepository() {
        OrderMaterialRepository repo = new OrderMaterialRepository();
        
        List<OrderMaterial> list = repo.findAll();
        assertFalse(list.isEmpty());

        OrderMaterial om = OrderMaterial.builder()
                .orderId(1)
                .materialId(2)
                .quantity(new BigDecimal("5.0"))
                .build();
        OrderMaterial saved = repo.save(om);
        assertEquals(1, saved.getOrderId());

        saved.setQuantity(new BigDecimal("10.0"));
        repo.update(saved);
        
        String idStr = saved.getOrderId() + "_" + saved.getMaterialId();
        assertEquals(new BigDecimal("10.0"), repo.findById(idStr).get().getQuantity());

        assertTrue(repo.delete(idStr));
    }

    @Test
    @Order(7)
    public void testOrderServiceRepository() {
        OrderServiceRepository repo = new OrderServiceRepository();
        
        List<OrderService> list = repo.findAll();
        assertFalse(list.isEmpty());

        OrderService os = OrderService.builder()
                .orderId(1)
                .serviceId(3)
                .quantity(2)
                .actualPrice(new BigDecimal("300.00"))
                .build();
        OrderService saved = repo.save(os);
        assertEquals(1, saved.getOrderId());

        saved.setActualPrice(new BigDecimal("350.00"));
        repo.update(saved);
        
        String idStr = saved.getOrderId() + "_" + saved.getServiceId();
        assertEquals(new BigDecimal("350.00"), repo.findById(idStr).get().getActualPrice());

        assertTrue(repo.delete(idStr));
    }
}
