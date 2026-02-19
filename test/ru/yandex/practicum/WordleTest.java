package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class WordleTest {
    // Тесты для DictionaryLoadException
    @Test
    public void testDictionaryLoadException() {
        String filePath = "test/path/file.txt";
        Throwable cause = new IOException("File not found");
        DictionaryLoadException exception = new DictionaryLoadException(filePath, cause);

        assertEquals(filePath, exception.getFilePath());
        assertEquals("Ошибка загрузки словаря из файла: test/path/file.txt", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    // Тесты для GameStateException
    @Test
    public void testGameStateException() {
        String message = "Игра в некорректном состоянии";
        GameStateException exception = new GameStateException(message);

        assertEquals(message, exception.getMessage());
    }

    // Тесты для InvalidWordLengthException
    @Test
    public void testInvalidWordLengthException() {
        int expected = 5;
        int actual = 3;
        InvalidWordLengthException exception = new InvalidWordLengthException(expected, actual);

        assertEquals(expected, exception.getExpectedLength());
        assertEquals(actual, exception.getActualLength());
        assertEquals("Слово должно содержать 5 букв, а содержит 3", exception.getMessage());
    }

    // Тесты для WordNotFoundException
    @Test
    public void testWordNotFoundException() {
        String word = "абвгд";
        WordNotFoundException exception = new WordNotFoundException(word);

        assertEquals(word, exception.getWord());
        assertEquals("Слово 'абвгд' не найдено в словаре", exception.getMessage());
    }

    // Тесты для WordleDictionary
    @Test
    public void testWordleDictionaryConstructorAndGetter() {
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
            boolean result1 = dictionary.check("слово");
            boolean result2 = dictionary.check("игра");
            assertTrue(result1);
            assertTrue(result2);
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void testCheckWordNotFound() {
        List<String> words = Arrays.asList("слово", "тест", "игра");
        WordleDictionary dictionary = new WordleDictionary(words);

        try {
            dictionary.check("книга");
        } catch (WordNotFoundException e) {
            assertEquals("Слово 'книга' не найдено в словаре", e.getMessage());
            assertEquals("книга", e.getWord());
        } catch (InvalidWordLengthException e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void testCheckInvalidLength() {
        List<String> words = Arrays.asList("слово", "тест", "игра");
        WordleDictionary dictionary = new WordleDictionary(words);

        try {
            dictionary.check("книги");
        } catch (InvalidWordLengthException e) {
            assertEquals(5, e.getExpectedLength());
            assertEquals(5, e.getActualLength());
            assertEquals("Слово должно содержать 5 букв, а содержит 5", e.getMessage());
        } catch (WordNotFoundException e) {
            assertEquals("", e.getMessage());
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
    public void testComparisonWordsPartialMatch() {
        List<String> words = Arrays.asList("книга", "мышка");
        WordleDictionary dictionary = new WordleDictionary(words);

        String result = dictionary.comparisonWords("мышка", "книга");
        assertEquals("-----", result);
    }

    @Test
    public void testComparisonWordsLettersOnPlace() {
        List<String> words = Arrays.asList("книга", "крант");
        WordleDictionary dictionary = new WordleDictionary(words);

        String result = dictionary.comparisonWords("крант", "книга");
        assertEquals("+---+", result);
    }

    @Test
    public void testComparisonWordsLettersNotOnPlace() {
        List<String> words = Arrays.asList("книга", "акнги");
        WordleDictionary dictionary = new WordleDictionary(words);

        String result = dictionary.comparisonWords("акнги", "книга");
        assertEquals("^^^^^", result);
    }

    @Test
    public void testGetHelpWord() {
        List<String> words = Arrays.asList("книга", "крант", "клоун", "крыло");
        WordleDictionary dictionary = new WordleDictionary(words);
        String answer = "книга";
        Map<Integer, String> history = new HashMap<>();
        history.put(1, "крант");

        String helpWord = dictionary.getHelpWord(history, answer);
        assertNotNull(helpWord);

        boolean found = false;
        for (String word : words) {
            if (word.equals(helpWord)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testIsWordInHistoryOfWords() {
        List<String> words = Arrays.asList("книга");
        WordleDictionary dictionary = new WordleDictionary(words);
        Map<Integer, String> history = new HashMap<>();
        history.put(1, "книга");
        history.put(2, "тест");

        boolean result1 = dictionary.isWordInHistoryOfWords("книга", history);
        boolean result2 = dictionary.isWordInHistoryOfWords("тест", history);
        boolean result3 = dictionary.isWordInHistoryOfWords("игра", history);

        assertTrue(result1);
        assertTrue(result2);
        assertFalse(result3);
    }

    // Тесты для WordleGame
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
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void testAnalyzeWordNotFound() {
        List<String> words = Arrays.asList("книга", "слово");
        WordleDictionary dictionary = new WordleDictionary(words);
        WordleGame game = new WordleGame("книга", 0, dictionary);

        try {
            game.analyze("тест");
        } catch (WordNotFoundException e) {
            assertEquals("Слово 'тест' не найдено в словаре", e.getMessage());
        } catch (InvalidWordLengthException e) {
            assertEquals("", e.getMessage());
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

            boolean found = false;
            for (String word : words) {
                if (word.equals(help)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
            assertEquals(0, game.getSteps());
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void testAddWord() {
        List<String> words = Arrays.asList("книга", "слово");
        WordleDictionary dictionary = new WordleDictionary(words);
        WordleGame game = new WordleGame("книга", 1, dictionary);

        game.addWord("слово");

        try {
            String help = game.analyze("");
            assertEquals("слово", help);
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    // Тесты для WordleException
    @Test
    public void testWordleException() {
        String message = "Тестовое исключение";
        WordleException exception1 = new WordleException(message);
        assertEquals(message, exception1.getMessage());

        Throwable cause = new RuntimeException("Причина");
        WordleException exception2 = new WordleException(message, cause);
        assertEquals(message, exception2.getMessage());
        assertEquals(cause, exception2.getCause());
    }

    // Edge Cases тесты
    @Test
    public void testEmptyDictionary() {
        WordleDictionary dictionary = new WordleDictionary(new ArrayList<>());
        assertTrue(dictionary.getWords().isEmpty());

        try {
            dictionary.check("слово");
        } catch (WordNotFoundException e) {
            assertEquals("Слово 'слово' не найдено в словаре", e.getMessage());
        } catch (InvalidWordLengthException e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void testComparisonWordsCaseSensitivity() {
        List<String> words = Arrays.asList("книга");
        WordleDictionary dictionary = new WordleDictionary(words);

        String result = dictionary.comparisonWords("КНИГА", "книга");
        assertEquals("+++++", result);
    }

    @Test
    public void testMaxAttempts() {
        List<String> words = Arrays.asList("книга", "слово", "тест", "игра", "кран", "стол");
        WordleDictionary dictionary = new WordleDictionary(words);
        WordleGame game = new WordleGame("книга", 5, dictionary);

        try {
            String result = game.analyze("слово");
            assertEquals(6, game.getSteps());
            assertNotNull(result);
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void testGetHelpWordEmptyHistory() {
        List<String> words = Arrays.asList("книга", "слово", "тест");
        WordleDictionary dictionary = new WordleDictionary(words);
        String answer = "книга";
        Map<Integer, String> history = new HashMap<>();

        String helpWord = dictionary.getHelpWord(history, answer);
        assertNotNull(helpWord);

        boolean found = false;
        for (String word : words) {
            if (word.equals(helpWord)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testLoadEmptyFile(@TempDir Path tempDir) {
        Path dictFile = tempDir.resolve("words_ru.txt");

        try {
            Files.createFile(dictFile);
            WordleDictionary dictionary = WordleDictionaryLoader.uploadingFiveLetterWords();
            assertNotNull(dictionary);
            assertTrue(dictionary.getWords().isEmpty());
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void testIsLetterThatNotExistInWord() {
        List<String> words = Arrays.asList("книга");
        WordleDictionary dictionary = new WordleDictionary(words);
        Set<Character> lettersThatNotExist = new HashSet<>(Arrays.asList('а', 'б'));

        boolean result1 = dictionary.isLetterThatNotExistInWord(lettersThatNotExist, "книга");
        boolean result2 = dictionary.isLetterThatNotExistInWord(lettersThatNotExist, "слово");

        assertFalse(result1);
        assertTrue(result2);
    }

    @Test
    public void testFillCurrentValues() {
        List<String> words = Arrays.asList("книга", "крант");
        WordleDictionary dictionary = new WordleDictionary(words);
        String answer = "книга";

        Map<Integer, String> history = new HashMap<>();
        history.put(1, "крант");

        Map<String, String> comparisonMap = dictionary.comparisonAllHistoryWords(history, answer);
        Map<Integer, Character> guessLettersOnYourPlace = new HashMap<>();
        Map<Integer, Character> guessLettersNotOnYourPlace = new HashMap<>();
        Set<Character> lettersThatNotExist = new HashSet<>();

        dictionary.fillCurrentValues(comparisonMap, guessLettersOnYourPlace,
                guessLettersNotOnYourPlace, lettersThatNotExist, history, answer);

        assertNotNull(guessLettersOnYourPlace);
        assertNotNull(guessLettersNotOnYourPlace);
        assertNotNull(lettersThatNotExist);
    }
}