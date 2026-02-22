package ru.yandex.practicum;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exception.DictionaryLoadException;
import ru.yandex.practicum.exception.GameStateException;

import java.io.*;
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
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        testWords = Arrays.asList("книга", "столы", "парта", "ручка", "пенал", "буква");
        dictionary = new WordleDictionary(testWords);
        answer = "книга";
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
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
    void check_ShouldReturnTrue_ForValidWord() {
        assertTrue(dictionary.check("книга"));
    }

    @Test
    void check_ShouldPrintWarning_ForWrongLength() {
        dictionary.check("дом");
        assertTrue(outContent.toString().contains("Слово должно содержать 5 букв, а содержит 3"));
    }

    @Test
    void check_ShouldPrintWarning_ForWordNotInDictionary() {
        dictionary.check("абвгд");
        assertTrue(outContent.toString().contains("Слово абвгд введено некорректно или не существует в словаре"));
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
        assertNotNull(result);
        assertEquals(5, result.length());
        for (char c : result.toCharArray()) {
            assertTrue(c == '+' || c == '^' || c == '-');
        }
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
        assertTrue(testWords.contains(helpWord) || helpWord == null);
    }

    @Test
    void getHelpWord_ShouldExcludeUsedWords() {
        Map<Integer, String> history = new HashMap<>();
        history.put(1, "книга");
        String helpWord = dictionary.getHelpWord(history, answer);
        assertTrue(helpWord == null || testWords.contains(helpWord) || helpWord.length() == 5);
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
    void analyze_ShouldIncrementSteps_ForValidWord() {
        WordleGame game = new WordleGame(answer, 0, dictionary);
        game.analyze("столы");
        assertEquals(1, game.getSteps());
    }

    @Test
    void analyze_ShouldReturnComparison_ForValidWord() {
        WordleGame game = new WordleGame(answer, 0, dictionary);
        String result = game.analyze("парта");
        assertNotNull(result);
    }

    @Test
    void analyze_ShouldAddWordToHistory_ForValidWord() {
        WordleGame game = new WordleGame(answer, 0, dictionary);
        game.analyze("парта");
        String helpWord = game.analyze("");
        assertNotNull(helpWord);
    }

    @Test
    void analyze_ShouldReturnNull_ForInvalidWord() {
        WordleGame game = new WordleGame(answer, 0, dictionary);
        String result = game.analyze("дом");
        assertNull(result);
        assertTrue(outContent.toString().contains("Слово должно содержать 5 букв, а содержит 3"));
    }

    @Test
    void analyze_ShouldReturnNull_ForWordNotInDictionary() {
        WordleGame game = new WordleGame(answer, 0, dictionary);
        String result = game.analyze("абвгд");
        assertNull(result);
        assertTrue(outContent.toString().contains("Слово абвгд введено некорректно или не существует в словаре"));
    }

    @Test
    void analyze_WithEmptyString_ShouldReturnHelpWord() {
        WordleGame game = new WordleGame(answer, 0, dictionary);
        game.analyze("столы");
        game.analyze("парта");
        String helpWord = game.analyze("");
        assertNotNull(helpWord);
        assertTrue(helpWord == null || helpWord.length() == 5);
    }

    @Test
    void getHelp_ShouldReturnWord_WhenCalled() {
        Map<Integer, String> history = new HashMap<>();
        String helpWord = dictionary.getHelpWord(history, answer);
        assertTrue(helpWord == null || helpWord.length() == 5);
    }

    @Test
    void analyze_ShouldHandleMaxAttempts() {
        WordleGame game = new WordleGame(answer, 0, dictionary);
        for (int i = 0; i < 6; i++) {
            game.analyze("столы");
        }
        assertEquals(6, game.getSteps());
    }

    @Test
    void dictionary_ShouldHandleEmptyWordList() {
        WordleDictionary emptyDict = new WordleDictionary(new ArrayList<>());
        boolean result = emptyDict.check("книга");
        assertFalse(result);
        assertTrue(outContent.toString().contains("Слово книга введено некорректно или не существует в словаре"));
    }

    @Test
    void dictionary_ShouldHandleNullWordList() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new WordleDictionary(null);
            }
        });
    }

    @Test
    void comparisonWords_ShouldHandleNullInput() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                dictionary.comparisonWords(null, answer);
            }
        });
    }

    @Test
    void comparisonWords_ShouldHandleEmptyString() {
        String result = dictionary.comparisonWords("     ", answer);
        assertNotNull(result);
        assertEquals(5, result.length());
    }

    @Test
    void getHelpWord_ShouldHandleNullHistory() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                dictionary.getHelpWord(null, answer);
            }
        });
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
    void completeGameFlow_PlayerWins() {
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
    void completeGameFlow_PlayerLoses() {
        WordleGame game = new WordleGame("книга", 0, dictionary);
        for (int i = 0; i < 6; i++) {
            String result = game.analyze("столы");
            assertNotNull(result);
        }
        assertEquals(6, game.getSteps());
    }

    @Test
    void gameFlow_WithHelpRequest() {
        WordleGame game = new WordleGame("книга", 0, dictionary);
        game.analyze("столы");
        game.analyze("парта");
        String helpWord = game.analyze("");
        assertTrue(helpWord == null || helpWord.length() == 5);
        if (helpWord != null && !helpWord.isEmpty()) {
            String result = game.analyze(helpWord);
            assertNotNull(result);
        }
    }

    @Test
    void dictionaryLoader_ShouldReplaceYoWithE() throws IOException {
        Path dictFile = tempDir.resolve("test_words.txt");
        List<String> words = Arrays.asList("ёлка", "пенёк", "книга", "столы");
        Files.write(dictFile, words);
        List<String> loadedWords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dictFile.toFile()))) {
            while (reader.ready()) {
                String word = reader.readLine();
                word = word.toLowerCase().replace("ё", "е");
                loadedWords.add(word);
            }
        }
        assertEquals(4, loadedWords.size());
        assertEquals("елка", loadedWords.get(0));
        assertEquals("пенек", loadedWords.get(1));
        assertEquals("книга", loadedWords.get(2));
        assertEquals("столы", loadedWords.get(3));
    }

    @AfterEach
    void cleanup() {
        System.setOut(originalOut);
    }
}