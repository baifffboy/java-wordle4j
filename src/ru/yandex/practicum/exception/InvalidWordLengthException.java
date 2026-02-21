package ru.yandex.practicum.exception;

public class InvalidWordLengthException extends WordleException {
    private final int expectedLength;
    private final int actualLength;

    public InvalidWordLengthException(int expectedLength, int actualLength) {
        super(String.format("Слово должно содержать %d букв, а содержит %d",
                expectedLength, actualLength));
        this.expectedLength = expectedLength;
        this.actualLength = actualLength;
    }

    public int getExpectedLength() {
        return expectedLength;
    }

    public int getActualLength() {
        return actualLength;
    }
}
