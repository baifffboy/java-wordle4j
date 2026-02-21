package ru.yandex.practicum.exception;

public class WordleException extends Exception {
    public WordleException(String message) {
        super(message);
    }

    public WordleException(String message, Throwable cause) {
        super(message, cause);
    }
}
