package com.urlshortener.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShortUrl {
    private String shortCode; // уникальный короткий код
    private String originalUrl;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int visitLimit;
    private int visitCount;
    private boolean isActive;

    public ShortUrl(String originalUrl, UUID ownerId) {
        this.originalUrl = originalUrl;
        this.ownerId = ownerId;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plusDays(1); // время жизни 24 часа
        this.visitLimit = Integer.MAX_VALUE; // по умолчанию без лимита
        this.visitCount = 0;
        this.isActive = true;
    }

    // Полный конструктор
    public ShortUrl(String shortCode, String originalUrl, UUID ownerId,
                    LocalDateTime createdAt, LocalDateTime expiresAt,
                    int visitLimit, int visitCount, boolean isActive) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.visitLimit = visitLimit;
        this.visitCount = visitCount;
        this.isActive = isActive;
    }

    // геттеры
    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public int getVisitLimit() {
        return visitLimit;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public boolean isActive() {
        return isActive;
    }

    // сеттеры
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setVisitLimit(int visitLimit) {
        this.visitLimit = visitLimit;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // вспомогательные методы
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isLimitReached() {
        return visitCount >= visitLimit;
    }

    @Override
    public String toString() {
        return "ShortUrl{" +
                "shortCode='" + shortCode + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", visitCount=" + visitCount +
                ", isActive=" + isActive +
                '}';
    }
}