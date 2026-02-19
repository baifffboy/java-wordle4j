package ru.yandex.practicum;

public class DictionaryLoadException extends WordleException {
    private final String filePath;

    public DictionaryLoadException(String filePath, Throwable cause) {
        super(String.format("Ошибка загрузки словаря из файла: %s", filePath), cause);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
