package ru.yandex.practicum;

import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words;
    public static final int LENGTH_OF_WORD = 5;

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public List<String> getWords() {
        return words;
    }

    public boolean check(String word) throws InvalidWordLengthException, WordNotFoundException {
        if(word.length() == LENGTH_OF_WORD){
            for(String i : words) {
                if(i.equals(word)){
                    return true;
                }
            }
            throw new WordNotFoundException(word);
        } else {
            throw new InvalidWordLengthException(LENGTH_OF_WORD, word.length());
        }
    }

    public String comparisonWords(String word, String answer){
        StringBuilder signsAnswer = new StringBuilder(LENGTH_OF_WORD);
        return checkInCycleWithRecursion(word, answer, signsAnswer, 0).toString();
    }

    public StringBuilder checkInCycleWithRecursion(String word, String answer, StringBuilder signsAnswer, int currentIndex){
        char currentLetterCheck = answer.toCharArray()[0];
        if (currentIndex == 4) {
            for(char words : word.toCharArray()){
                if(words == currentLetterCheck) {
                    if (currentIndex == word.indexOf(words)) {
                        signsAnswer.append('+');
                    } else {
                        signsAnswer.append('^');
                    }
                    break;
                }
            }
            if (signsAnswer.length() < currentIndex + 1) {
                signsAnswer.append('-');
            }
        } else {
            for (char words : word.toCharArray()) {
                if (words == currentLetterCheck) {
                    if (currentIndex == word.indexOf(words)) {
                        signsAnswer.append('+');
                    } else {
                        signsAnswer.append('^');
                    }
                    break;
                }
            }
            if (signsAnswer.length() < currentIndex + 1) {
                signsAnswer.append('-');
            }
            return checkInCycleWithRecursion(word, answer.substring(1), signsAnswer, currentIndex + 1);
        }
        return signsAnswer;
    }

    public String getHelpWord(Map<Integer, String> historyOfWords, String answer) {
        Map<String, String> comparisonMap = comparisonAllHistoryWords(historyOfWords, answer);
        Map<Integer, Character> guessLettersOnYourPlace = new HashMap<>(LENGTH_OF_WORD);
        Map<Integer, Character> guessLettersNotOnYourPlace = new HashMap<>(LENGTH_OF_WORD);
        Set<Character> lettersThatNotExist = new HashSet<>();

        fillCurrentValues(comparisonMap, guessLettersOnYourPlace, guessLettersNotOnYourPlace, lettersThatNotExist,
                historyOfWords, answer);

        String helpWord = null;
        for (String i : words) {
            if (isLetterThatNotExistInWord(lettersThatNotExist, i) && !isWordInHistoryOfWords(i, historyOfWords)) {
                if (!isLetterInWordExistOnYourPlace(guessLettersOnYourPlace, i) ||
                        !isLetterInWordExistButNotOnYourPlace(guessLettersNotOnYourPlace, i)) continue;
                else {
                    helpWord = i;
                    break;
                }
            }
        }

        if (helpWord == null) {
            Random random = new Random();
            return words.get(random.nextInt(words.size()));
        }

        return helpWord;
    }

    public boolean isWordInHistoryOfWords(String word, Map<Integer, String> historyOfWords) {
        return historyOfWords.containsValue(word);
    }

    public boolean isLetterInWordExistOnYourPlace(Map<Integer, Character> guessLettersOnYourPlace, String word){
        for (int t : guessLettersOnYourPlace.keySet()){ // проверка находятся ли в нашем искомом
            // слове ТОЧНО известные буквы на своих местах
            if (word.charAt(t) != guessLettersOnYourPlace.get(t)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLetterInWordExistButNotOnYourPlace(Map<Integer, Character> guessLettersNotOnYourPlace, String word){
        for (int t : guessLettersNotOnYourPlace.keySet()){ // проверка находятся ли в слове те буквы
            // которые там есть но находятся не на своем месте,
            // и проверка чтобы они были на другом месте в слове - подсказке
            if (!word.contains(guessLettersNotOnYourPlace.get(t).toString())) return false;
            if (word.charAt(t) == guessLettersNotOnYourPlace.get(t)) return false;
        }
        return true;
    }

    public boolean isLetterThatNotExistInWord(Set<Character> lettersThatNotExist, String word){
        // проверка что букв, которых мы узнали что нет (из предыдущих итераций), не будет в слове - подсказке
        for (Character i : lettersThatNotExist){
            if(word.contains(i.toString())) return false;
        }
        return true;
    }

    public Map<String, String> comparisonAllHistoryWords(Map<Integer, String> historyOfWords, String answer){
        Map<String, String> comparisonMap = new HashMap<>(); // ключ - слово игрока значение - его сравнение с ответом (схема)
        for(int i : historyOfWords.keySet()) {
            comparisonMap.put(historyOfWords.get(i), comparisonWords(historyOfWords.get(i), answer));
        }
        return comparisonMap;
    }

    public void fillCurrentValues(Map<String, String> comparisonMap, Map<Integer, Character> guessLettersOnYourPlace,
                                  Map<Integer, Character> guessLettersNotOnYourPlace, Set<Character> lettersThatNotExist,
                                  Map<Integer, String> historyOfWords, String answer) {
        for (String str : comparisonMap.keySet()) {
            String lastComparison = comparisonMap.get(str);
            if (lastComparison.contains("+")) {
                int index = 0;
                for (char i : lastComparison.toCharArray()) {
                    if (i == '+' && !guessLettersOnYourPlace.containsValue(str.charAt(index))) {
                        guessLettersOnYourPlace.put(index, str.charAt(index));
                        if (lettersThatNotExist.contains(str.charAt(index))) {
                            lettersThatNotExist.remove(str.charAt(index));
                        }
                    }
                    index++;
                }
            }
            if (lastComparison.contains("^")) {
                int index = 0;
                for (char i : lastComparison.toCharArray()) {
                    if (i == '^' && !guessLettersNotOnYourPlace.containsValue(str.charAt(index))) {
                        guessLettersNotOnYourPlace.put(index, str.charAt(index));
                        if (lettersThatNotExist.contains(str.charAt(index))) {
                            lettersThatNotExist.remove(str.charAt(index));
                        }
                    }
                    index++;
                }
            }
            if (lastComparison.contains("-")) {
                for(int t : historyOfWords.keySet()){
                    String intermediateResult = comparisonWords(historyOfWords.get(t), answer);
                    int index = 0;
                    for (char i : intermediateResult.toCharArray()){
                        if (i == '-') lettersThatNotExist.add(historyOfWords.get(t).toCharArray()[index]);
                        index++;
                    }
                }
            }
        }
    }

}
