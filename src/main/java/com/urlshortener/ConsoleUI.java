package com.urlshortener;

import com.urlshortener.service.UrlService;
import com.urlshortener.service.UserService;
import com.urlshortener.model.ShortUrl;
import java.util.*;
import java.awt.Desktop;
import java.net.URI;

public class ConsoleUI {
    private UrlService urlService;
    private UserService userService;
    private Scanner scanner;
    private UUID currentUserId;

    public ConsoleUI() {
        this.urlService = new UrlService();
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Сервис сокращения ссылок ===");
        initializeUser();

        boolean running = true;
        while (running) {
            showMenu();

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        createShortUrl();
                        break;
                    case 2:
                        redirectToUrl();
                        break;
                    case 3:
                        showMyUrls();
                        break;
                    case 4:
                        deleteUrl();
                        break;
                    case 5:
                        System.out.println("Ваш User ID: " + currentUserId);
                        break;
                    case 0:
                        running = false;
                        System.out.println("До свидания!");
                        break;
                    default:
                        System.out.println("Неверный выбор");
                }
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число");
            }
        }

        scanner.close();
    }

    private void initializeUser() {
        System.out.print("Введите ваш User ID (или оставьте пустым для нового): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            currentUserId = userService.createUser();
            System.out.println("Новый пользователь создан. Ваш ID: " + currentUserId);
            System.out.println("Сохраните этот ID для доступа к вашим ссылкам!");
        } else {
            try {
                currentUserId = UUID.fromString(input);
                if (!userService.userExists(currentUserId)) {
                    System.out.println("Пользователь не найден. Создан новый.");
                    currentUserId = userService.createUser();
                } else {
                    System.out.println("Добро пожаловать обратно!");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный формат ID. Создан новый пользователь.");
                currentUserId = userService.createUser();
            }
        }
    }

    private void showMenu() {
        System.out.println("\n=== Меню ===");
        System.out.println("1. Создать короткую ссылку");
        System.out.println("2. Перейти по короткой ссылке");
        System.out.println("3. Мои ссылки");
        System.out.println("4. Удалить ссылку");
        System.out.println("5. Показать мой ID");
        System.out.println("0. Выход");
        System.out.print("Выбор: ");
    }

    private void createShortUrl() {
        System.out.print("Введите оригинальный URL: ");
        String originalUrl = scanner.nextLine().trim();

        if (originalUrl.isEmpty()) {
            System.out.println("URL не может быть пустым");
            return;
        }

        System.out.print("Лимит переходов (оставьте пустым для безлимита): ");
        String limitInput = scanner.nextLine().trim();
        Integer visitLimit = null;

        if (!limitInput.isEmpty()) {
            try {
                visitLimit = Integer.parseInt(limitInput);
                if (visitLimit <= 0) {
                    System.out.println("Лимит должен быть положительным числом");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат числа. Будет установлен безлимит.");
            }
        }

        try {
            String shortUrl = urlService.createShortUrl(originalUrl, currentUserId, visitLimit);
            System.out.println("✅ Ваша короткая ссылка: " + shortUrl);
        } catch (Exception e) {
            System.out.println("Ошибка при создании ссылки: " + e.getMessage());
        }
    }

    private void redirectToUrl() {
        System.out.print("Введите короткий код (часть после clck.ru/): ");
        String shortCode = scanner.nextLine().trim();

        String result = urlService.redirect(shortCode);

        if (result == null) {
            System.out.println("❌ Ссылка не найдена");
        } else if (result.equals("EXPIRED")) {
            System.out.println("⏰ Ссылка истекла");
            System.out.print("Хотите создать новую? (да/нет): ");
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("да")) {
                createShortUrl();
            }
        } else if (result.equals("LIMIT_REACHED")) {
            System.out.println("🚫 Достигнут лимит переходов");
            System.out.print("Хотите создать новую? (да/нет): ");
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("да")) {
                createShortUrl();
            }
        } else if (result.equals("INACTIVE")) {
            System.out.println("🔒 Ссылка неактивна");
        } else {
            try {
                System.out.println("🌐 Открываю: " + result);
                Desktop.getDesktop().browse(new URI(result));
            } catch (Exception e) {
                System.out.println("Не удалось открыть браузер. Перейдите по ссылке вручную:");
                System.out.println(result);
            }
        }
    }

    private void showMyUrls() {
        List<ShortUrl> urls = urlService.getUserUrls(currentUserId);

        if (urls.isEmpty()) {
            System.out.println("У вас нет созданных ссылок");
            return;
        }

        System.out.println("\n=== Ваши ссылки ===");
        for (ShortUrl url : urls) {
            System.out.println("Код: clck.ru/" + url.getShortCode());
            System.out.println("Оригинал: " + url.getOriginalUrl());
            System.out.println("Переходы: " + url.getVisitCount() + "/" +
                    (url.getVisitLimit() == Integer.MAX_VALUE ? "∞" : url.getVisitLimit()));
            System.out.println("Создана: " + url.getCreatedAt());
            System.out.println("Истекает: " + url.getExpiresAt());
            System.out.println("Статус: " + (url.isActive() ? "✅ активна" : "❌ неактивна"));
            System.out.println("---");
        }
        System.out.println("Всего ссылок: " + urls.size());
    }

    private void deleteUrl() {
        System.out.print("Введите короткий код для удаления: ");
        String shortCode = scanner.nextLine().trim();

        if (urlService.deleteUrl(shortCode, currentUserId)) {
            System.out.println("✅ Ссылка удалена");
        } else {
            System.out.println("❌ Ссылка не найдена или у вас нет прав на удаление");
        }
    }
}