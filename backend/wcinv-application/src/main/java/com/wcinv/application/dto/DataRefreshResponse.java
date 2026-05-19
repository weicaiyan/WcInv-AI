package com.wcinv.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataRefreshResponse {

    private final String message;
    private final String status;
    private final List<Item> items;

    public DataRefreshResponse(String message, String status, List<Item> items) {
        this.message = message;
        this.status = status;
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private final String source;
        private final String name;
        private final String status;
        private final String message;

        public Item(String source, String name, String status, String message) {
            this.source = source;
            this.name = name;
            this.status = status;
            this.message = message;
        }

        public String getSource() {
            return source;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
