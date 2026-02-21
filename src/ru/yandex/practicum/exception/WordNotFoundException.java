package ru.yandex.practicum.exception;

public class WordNotFoundException extends WordleException {
    private final String word;

    public WordNotFoundException(String word) {
        super(String.format("Слово '%s' не найдено в словаре", word));
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
