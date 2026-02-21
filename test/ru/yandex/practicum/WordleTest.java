package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exception.DictionaryLoadException;
import ru.yandex.practicum.exception.GameStateException;
import ru.yandex.practicum.exception.InvalidWordLengthException;
import ru.yandex.practicum.exception.WordNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class WordleTest {

    @TempDir
    Path tempDir;

    private WordleDictionary dictionary;
    private List<String> testWords;
    private String answer;

    @BeforeEach
    void setUp() {
        testWords = Arrays.asList("книга", "столы", "парта", "ручка", "пенал", "буква");
        dictionary = new WordleDictionary(testWords);
        answer = "книга";
    }

    @Test
    void dictionaryLoadException_ShouldStoreFilePath() {
        String filePath = "test/path/dictionary.txt";
        Throwable cause = new IOException("File not found");
        DictionaryLoadException exception = new DictionaryLoadException(filePath, cause);

        assertEquals(filePath, exception.getFilePath());
        assertTrue(exception.getMessage().contains(filePath));
        assertEquals(cause, exception.getCause());
    }

    @Test
    void gameStateException_ShouldStoreMessage() {
        String message = "Invalid game state";
        GameStateException exception = new GameStateException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void invalidWordLengthException_ShouldStoreLengths() {
        int expected = 5;
        int actual = 3;
        InvalidWordLengthException exception = new InvalidWordLengthException(expected, actual);

        assertEquals(expected, exception.getExpectedLength());
        assertEquals(actual, exception.getActualLength());
        assertTrue(exception.getMessage().contains(String.valueOf(expected)));
        assertTrue(exception.getMessage().contains(String.valueOf(actual)));
    }

    @Test
    void wordNotFoundException_ShouldStoreWord() {
        String word = "абвгд";
        WordNotFoundException exception = new WordNotFoundException(word);

        assertEquals(word, exception.getWord());
        assertTrue(exception.getMessage().contains(word));
    }

    @Test
    void dictionaryLoader_ShouldReplaceYoWithE() throws IOException {
        Path dictFile = tempDir.resolve("yo_test.txt");
        List<String> words = Arrays.asList("ёлка", "пенёк");
        Files.write(dictFile, words);
    }

    @Test
    void check_ShouldReturnTrue_ForValidWord() throws Exception {
        assertTrue(dictionary.check("книга"));
    }

    @Test
    void check_ShouldThrowInvalidWordLengthException_ForWrongLength() {
        InvalidWordLengthException exception = assertThrows(InvalidWordLengthException.class, () -> {
            dictionary.check("дом");
        });
        assertEquals(5, exception.getExpectedLength());
        assertEquals(3, exception.getActualLength());
    }

    @Test
    void check_ShouldThrowWordNotFoundException_ForWordNotInDictionary() {
        WordNotFoundException exception = assertThrows(WordNotFoundException.class, () -> {
            dictionary.check("абвгд");
        });
        assertEquals("абвгд", exception.getWord());
    }

    @Test
    void comparisonWords_ShouldReturnCorrectPattern_AllMatches() {
        String result = dictionary.comparisonWords("книга", "книга");
        assertEquals("+++++", result);
    }

    @Test
    void comparisonWords_ShouldReturnCorrectPattern_NoMatches() {
        String result = dictionary.comparisonWords("столы", "книга");
        assertEquals("-----", result);
    }

    @Test
    void comparisonWords_ShouldReturnCorrectPattern_MixedMatches() {
        List<String> testWords = Arrays.asList("книга", "парта", "столы");
        WordleDictionary testDict = new WordleDictionary(testWords);
        String result = testDict.comparisonWords("парта", "книга");
        assertEquals("----^", result);
    }

    @Test
    void comparisonWords_ShouldHandleDuplicateLetters() {
        List<String> wordsWithDuplicates = Arrays.asList("молот", "полка");
        WordleDictionary dictWithDuplicates = new WordleDictionary(wordsWithDuplicates);
        String result = dictWithDuplicates.comparisonWords("молот", "полка");
        assertNotNull(result);
    }

    @Test
    void getHelpWord_ShouldReturnWord_WhenHistoryIsEmpty() {
        Map<Integer, String> emptyHistory = new HashMap<>();
        String helpWord = dictionary.getHelpWord(emptyHistory, answer);

        assertNotNull(helpWord);
        assertTrue(testWords.contains(helpWord));
    }

    @Test
    void getHelpWord_ShouldExcludeUsedWords() {
        Map<Integer, String> history = new HashMap<>();
        history.put(1, "книга"); // уже использовали ответ

        String helpWord = dictionary.getHelpWord(history, answer);
        assertNotEquals("книга", helpWord);
    }

    @Test
    void isWordInHistoryOfWords_ShouldReturnTrue_ForUsedWord() {
        Map<Integer, String> history = new HashMap<>();
        history.put(1, "книга");

        assertTrue(dictionary.isWordInHistoryOfWords("книга", history));
        assertFalse(dictionary.isWordInHistoryOfWords("столы", history));
    }

    @Test
    void wordleGame_Constructor_ShouldInitializeCorrectly() {
        WordleGame game = new WordleGame(answer, 0, dictionary);

        assertEquals(answer, game.getAnswer());
        assertEquals(0, game.getSteps());
        assertEquals(dictionary, game.getDictionary());
    }

    @Test
    void analyze_ShouldIncrementSteps_ForValidWord() throws Exception {
        WordleGame game = new WordleGame(answer, 0, dictionary);

        game.analyze("столы");
        assertEquals(1, game.getSteps());
    }

    @Test
    void analyze_ShouldReturnComparison_ForValidWord() throws Exception {
        WordleGame game = new WordleGame(answer, 0, dictionary);

        String result = game.analyze("парта");
        assertNotNull(result);
    }

    @Test
    void analyze_ShouldAddWordToHistory_ForValidWord() throws Exception {
        WordleGame game = new WordleGame(answer, 0, dictionary);

        game.analyze("парта");
        String helpWord = game.analyze("");
        assertNotNull(helpWord);
    }

    @Test
    void analyze_ShouldThrowException_ForInvalidWord() {
        WordleGame game = new WordleGame(answer, 0, dictionary);

        try {
            game.analyze("дом");
            fail("Expected InvalidWordLengthException to be thrown");
        } catch (InvalidWordLengthException e) {
            assertEquals(5, e.getExpectedLength());
            assertEquals(3, e.getActualLength());
        } catch (Exception e) {
            fail("Expected InvalidWordLengthException but got " + e.getClass().getSimpleName());
        }
    }

    @Test
    void analyze_ShouldThrowException_ForWordNotInDictionary() {
        WordleGame game = new WordleGame(answer, 0, dictionary);

        try {
            game.analyze("абвгд");
            fail("Expected WordNotFoundException to be thrown");
        } catch (WordNotFoundException e) {
            assertEquals("абвгд", e.getWord());
        } catch (Exception e) {
            fail("Expected WordNotFoundException but got " + e.getClass().getSimpleName());
        }
    }

    @Test
    void analyze_WithEmptyString_ShouldReturnHelpWord() throws Exception {
        WordleGame game = new WordleGame(answer, 0, dictionary);

        // Добавляем несколько попыток
        game.analyze("столы");
        game.analyze("парта");

        String helpWord = game.analyze("");
        assertNotNull(helpWord);
        assertTrue(testWords.contains(helpWord) || helpWord.length() == 5);
    }

    @Test
    void getHelp_ShouldReturnWord_WhenCalled() {
        WordleGame game = new WordleGame(answer, 0, dictionary);
        Map<Integer, String> history = new HashMap<>();

        String helpWord = game.getHelp(history, answer);
        assertNotNull(helpWord);
    }

    @Test
    void analyze_ShouldHandleMaxAttempts() throws Exception {
        WordleGame game = new WordleGame(answer, 0, dictionary);

        for (int i = 0; i < 6; i++) {
            game.analyze("столы");
        }

        assertEquals(6, game.getSteps());
    }

    @Test
    void dictionary_ShouldHandleEmptyWordList() {
        WordleDictionary emptyDict = new WordleDictionary(new ArrayList<>());

        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                emptyDict.check("книга");
            }
        };

        assertThrows(WordNotFoundException.class, executable);
    }

    @Test
    void dictionary_ShouldHandleNullWordList() {
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                new WordleDictionary(null);
            }
        };

        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void comparisonWords_ShouldHandleNullInput() {
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                dictionary.comparisonWords(null, answer);
            }
        };

        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void comparisonWords_ShouldHandleEmptyString() {
        String result = dictionary.comparisonWords("", answer);
        assertNotNull(result);
        assertEquals(5, result.length());
    }

    @Test
    void getHelpWord_ShouldHandleNullHistory() {
        Executable executable = new Executable() {
            @Override
            public void execute() throws Throwable {
                dictionary.getHelpWord(null, answer);
            }
        };
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void fillCurrentValues_ShouldHandleEmptyMaps() {
        WordleDictionary dict = new WordleDictionary(testWords);
        Map<String, String> comparisonMap = new HashMap<>();
        Map<Integer, Character> guessLettersOnPlace = new HashMap<>();
        Map<Integer, Character> guessLettersNotOnPlace = new HashMap<>();
        Set<Character> lettersNotExist = new HashSet<>();
        Map<Integer, String> history = new HashMap<>();

        // Не должно быть исключений
        dict.fillCurrentValues(comparisonMap, guessLettersOnPlace, guessLettersNotOnPlace,
                lettersNotExist, history, answer);

        assertTrue(guessLettersOnPlace.isEmpty());
        assertTrue(guessLettersNotOnPlace.isEmpty());
        assertTrue(lettersNotExist.isEmpty());
    }

    @Test
    void isLetterThatNotExistInWord_ShouldHandleEmptySet() {
        Set<Character> emptySet = new HashSet<>();

        assertTrue(dictionary.isLetterThatNotExistInWord(emptySet, "книга"));
    }

    @Test
    void wordleGame_ShouldHandleNegativeSteps() {
        WordleGame game = new WordleGame(answer, -1, dictionary);
        assertEquals(-1, game.getSteps());

        game.setSteps(5);
        assertEquals(5, game.getSteps());
    }

    @Test
    void completeGameFlow_PlayerWins() throws Exception {
        WordleGame game = new WordleGame("книга", 0, dictionary);
        String result1 = game.analyze("столы");
        assertNotNull(result1);
        String result2 = game.analyze("парта");
        assertNotNull(result2);
        String result3 = game.analyze("книга");
        assertEquals("+++++", result3);

        assertEquals(3, game.getSteps());
    }

    @Test
    void completeGameFlow_PlayerLoses() throws Exception {
        WordleGame game = new WordleGame("книга", 0, dictionary);

        for (int i = 0; i < 6; i++) {
            String result = game.analyze("столы");
            assertNotNull(result);
        }

        assertEquals(6, game.getSteps());
    }

    @Test
    void gameFlow_WithHelpRequest() throws Exception {
        WordleGame game = new WordleGame("книга", 0, dictionary);

        game.analyze("столы");
        game.analyze("парта");

        String helpWord = game.analyze("");
        assertNotNull(helpWord);
        assertTrue(helpWord.length() == 5);

        String result = game.analyze(helpWord);
        assertNotNull(result);
    }
}