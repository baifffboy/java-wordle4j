package ru.yandex.practicum;

import java.util.LinkedHashMap;
import java.util.Map;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private static final int COUNT_OF_ATTEMPTS = 6;
    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private Map<Integer, String> historyOfWords;

    public WordleGame(String answer, int steps, WordleDictionary dictionary) {
        this.answer = answer;
        this.steps = steps;
        this.dictionary = dictionary;
        historyOfWords = new LinkedHashMap<>(COUNT_OF_ATTEMPTS);
    }

    public String getAnswer() {
        return answer;
    }

    public int getSteps() {
        return steps;
    }

    public WordleDictionary getDictionary() {
        return dictionary;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String analyze(String word) throws WordNotFoundException, InvalidWordLengthException {
        if (word.isEmpty()) {
            return getHelp(historyOfWords, answer);
        }
        if (dictionary.check(word)) {
            setSteps(getSteps() + 1);
            addWord(word);
            return dictionary.comparisonWords(word, answer);
        } else {
            return null;
        }
    }

    public void addWord(String word){
        historyOfWords.put(getSteps(), word);
    }

    public String getHelp(Map<Integer, String> historyOfWords, String answer) {
        return dictionary.getHelpWord(historyOfWords, answer);
    }

}
