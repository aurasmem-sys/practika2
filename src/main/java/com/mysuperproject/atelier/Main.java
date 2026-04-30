package com.mysuperproject.atelier;

import com.mysuperproject.atelier.entity.Client;
import com.mysuperproject.atelier.pool.ConnectionPool;
import com.mysuperproject.atelier.repository.ClientRepository;
import com.mysuperproject.atelier.repository.Dao;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    System.out.println("Atelier Management System started!");

    try {
      // Ініціалізація та використання репозиторію
      Dao<Integer, Client> clientDao = new ClientRepository();

      // Створення нового клієнта
      Client newClient =
          Client.builder()
              .firstName("Івааан")
              .lastName("Іванаова")
              .phoneNumber("+380991234299")
              .email("ivanov@exaуmple.com")
              .build();

      Client savedClient = clientDao.save(newClient);
      System.out.println("Збережено нового клієнта: " + savedClient);

      // Отримання всіх клієнтів
      List<Client> clients = clientDao.findAll();
      System.out.println("Всі клієнти:");
      for (Client client : clients) {
        System.out.println(client);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Закриття пулу з'єднань при завершенні програми
      ConnectionPool.getInstance().closePool();
    }
  }
}
