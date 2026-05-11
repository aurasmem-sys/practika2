package com.mysuperproject.atelier.util;

import com.mysuperproject.atelier.entity.Client;

public class UserSession {
    private static Client currentClient;

    public static void setCurrentClient(Client client) {
        currentClient = client;
    }

    public static Client getCurrentClient() {
        return currentClient;
    }
}
