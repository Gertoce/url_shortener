package com.urlshortener.model;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

public class User {
    private UUID userId;
    private Set<String> shortUrls; // IDs созданных ссылок
    private String notificationEmail; // опционально для уведомлений

    public User() {
        this.userId = UUID.randomUUID();
        this.shortUrls = new HashSet<>();
    }

    // геттеры и сеттеры
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Set<String> getShortUrls() {
        return shortUrls;
    }

    public void setShortUrls(Set<String> shortUrls) {
        this.shortUrls = shortUrls;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public void addShortUrl(String shortCode) {
        this.shortUrls.add(shortCode);
    }

    public boolean removeShortUrl(String shortCode) {
        return this.shortUrls.remove(shortCode);
    }
}