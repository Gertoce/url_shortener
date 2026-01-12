package com.urlshortener.service;

import com.urlshortener.model.ShortUrl;
import com.urlshortener.util.UrlGenerator;
import java.util.*;

public class UrlService {
    private Map<String, ShortUrl> urlStorage = new HashMap<>();
    private Map<UUID, Set<String>> userUrls = new HashMap<>();

    public String createShortUrl(String originalUrl, UUID userId, Integer visitLimit) {
        // Генерируем уникальный shortCode
        String shortCode;
        do {
            shortCode = UrlGenerator.generateShortCode(originalUrl, userId);
        } while (urlStorage.containsKey(shortCode));

        // Создаем объект ShortUrl
        ShortUrl shortUrl = new ShortUrl(originalUrl, userId);

        // Устанавливаем shortCode
        shortUrl.setShortCode(shortCode);

        // Устанавливаем лимит, если указан
        if (visitLimit != null && visitLimit > 0) {
            shortUrl.setVisitLimit(visitLimit);
        }

        // Сохраняем в хранилище
        urlStorage.put(shortCode, shortUrl);

        // Добавляем к пользователю
        userUrls.computeIfAbsent(userId, k -> new HashSet<>()).add(shortCode);

        return "clck.ru/" + shortCode;
    }

    public String redirect(String shortCode) {
        ShortUrl shortUrl = urlStorage.get(shortCode);

        if (shortUrl == null) {
            return null; // Ссылка не найдена
        }

        // Проверяем активность
        if (!shortUrl.isActive()) {
            return "INACTIVE";
        }

        // Проверяем время жизни
        if (shortUrl.isExpired()) {
            shortUrl.setActive(false);
            return "EXPIRED";
        }

        // Проверяем лимит переходов
        if (shortUrl.isLimitReached()) {
            shortUrl.setActive(false);
            return "LIMIT_REACHED";
        }

        // Увеличиваем счетчик переходов
        shortUrl.setVisitCount(shortUrl.getVisitCount() + 1);

        return shortUrl.getOriginalUrl();
    }

    public List<ShortUrl> getUserUrls(UUID userId) {
        Set<String> codes = userUrls.get(userId);
        if (codes == null) {
            return new ArrayList<>();
        }

        List<ShortUrl> result = new ArrayList<>();
        for (String code : codes) {
            ShortUrl url = urlStorage.get(code);
            if (url != null) {
                result.add(url);
            }
        }
        return result;
    }

    public boolean deleteUrl(String shortCode, UUID userId) {
        ShortUrl url = urlStorage.get(shortCode);
        if (url != null && url.getOwnerId().equals(userId)) {
            urlStorage.remove(shortCode);
            Set<String> userCodes = userUrls.get(userId);
            if (userCodes != null) {
                userCodes.remove(shortCode);
            }
            return true;
        }
        return false;
    }

    public List<ShortUrl> getExpiredUrls() {
        List<ShortUrl> expired = new ArrayList<>();
        for (ShortUrl url : urlStorage.values()) {
            if (url.isExpired()) {
                expired.add(url);
            }
        }
        return expired;
    }

    public ShortUrl getUrlByShortCode(String shortCode) {
        return urlStorage.get(shortCode);
    }

    public int getTotalUrls() {
        return urlStorage.size();
    }

    public int getUserUrlCount(UUID userId) {
        Set<String> codes = userUrls.get(userId);
        return codes != null ? codes.size() : 0;
    }
}