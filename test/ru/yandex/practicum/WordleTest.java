package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleTest {

    @Test
    public void testDictionaryLoadException() {
        String filePath = "test/path/file.txt";
        Throwable cause = new IOException("File not found");
        DictionaryLoadException exception = new DictionaryLoadException(filePath, cause);

        assertEquals(filePath, exception.getFilePath());
        assertEquals("Ошибка загрузки словаря из файла: test/path/file.txt", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testGameStateException() {
        String message = "Игра в некорректном состоянии";
        GameStateException exception = new GameStateException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testInvalidWordLengthException() {
        int expected = 5;
        int actual = 3;
        InvalidWordLengthException exception = new InvalidWordLengthException(expected, actual);

        assertEquals(expected, exception.getExpectedLength());
        assertEquals(actual, exception.getActualLength());
        assertEquals("Слово должно содержать 5 букв, а содержит 3", exception.getMessage());
    }

    @Test
    public void testWordNotFoundException() {
        String word = "абвгд";
        WordNotFoundException exception = new WordNotFoundException(word);

        assertEquals(word, exception.getWord());
        assertEquals("Слово 'абвгд' не найдено в словаре", exception.getMessage());
    }

    @Test
    public void testWordleDictionaryConstructor() {
        List<String> words = Arrays.asList("слово", "тест", "игра");
        WordleDictionary dictionary = new WordleDictionary(words);

        assertEquals(words, dictionary.getWords());
        assertNotSame(words, dictionary.getWords());
    }

    @Test
    public void testCheckValidWord() {
        List<String> words = Arrays.asList("слово", "тест", "игра", "книга");
        WordleDictionary dictionary = new WordleDictionary(words);

        try {
            assertTrue(dictionary.check("слово"));
            assertTrue(dictionary.check("игра"));
        } catch (Exception e) {
            fail("Не должно быть исключения: " + e.getMessage());
        }
    }

    @Test
    public void testCheckWordNotFound() {
        List<String> words = Arrays.asList("слово", "тест", "игра");
        WordleDictionary dictionary = new WordleDictionary(words);

        try {
            dictionary.check("книга");
            fail("Должно быть исключение WordNotFoundException");
        } catch (WordNotFoundException e) {
            assertEquals("Слово 'книга' не найдено в словаре", e.getMessage());
        } catch (InvalidWordLengthException e) {
            fail("Должно быть WordNotFoundException");
        }
    }

    @Test
    public void testComparisonWordsAllMatch() {
        List<String> words = Arrays.asList("книга");
        WordleDictionary dictionary = new WordleDictionary(words);

        String result = dictionary.comparisonWords("книга", "книга");
        assertEquals("+++++", result);
    }

    @Test
    public void testComparisonWordsLettersOnPlace() {
        List<String> words = Arrays.asList("книга", "крант");
        WordleDictionary dictionary = new WordleDictionary(words);

        String result = dictionary.comparisonWords("крант", "книга");
        assertEquals("+---+", result);
    }

    @Test
    public void testWordleGameConstructor() {
        List<String> words = Arrays.asList("книга", "слово");
        WordleDictionary dictionary = new WordleDictionary(words);
        WordleGame game = new WordleGame("книга", 0, dictionary);

        assertEquals("книга", game.getAnswer());
        assertEquals(0, game.getSteps());
        assertEquals(dictionary, game.getDictionary());
    }

    @Test
    public void testAnalyzeValidWord() {
        List<String> words = Arrays.asList("книга", "слово", "тест");
        WordleDictionary dictionary = new WordleDictionary(words);
        WordleGame game = new WordleGame("книга", 0, dictionary);

        try {
            String result = game.analyze("книга");
            assertEquals("+++++", result);
            assertEquals(1, game.getSteps());
        } catch (Exception e) {
            fail("Не должно быть исключения: " + e.getMessage());
        }
    }

    @Test
    public void testAnalyzeEmptyStringForHelp() {
        List<String> words = Arrays.asList("книга", "слово", "тест");
        WordleDictionary dictionary = new WordleDictionary(words);
        WordleGame game = new WordleGame("книга", 0, dictionary);

        try {
            String help = game.analyze("");
            assertNotNull(help);
            assertTrue(words.contains(help));
            assertEquals(0, game.getSteps());
        } catch (Exception e) {
            fail("Не должно быть исключения: " + e.getMessage());
        }
    }
}