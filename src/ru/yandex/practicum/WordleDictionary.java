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
    private Map<Integer, Character> guessLettersOnYourPlace = new HashMap<>(LENGTH_OF_WORD);
    private Map<Character, ArrayList<Integer>> guessLettersNotOnYourPlace = new HashMap<>(LENGTH_OF_WORD);
    private Set<Character> lettersThatNotExist = new HashSet<>();

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public List<String> getWords() {
        return words;
    }

    public boolean check(String word) {
        if (word.length() == LENGTH_OF_WORD) {
            for (String i : words) {
                if (i.equals(word)) {
                    return true;
                }
            }
            System.out.printf("Слово %s введено некорректно или не существует в словаре", word);
        } else {
            System.out.printf("Слово должно содержать %d букв, а содержит %d", LENGTH_OF_WORD, word.length());
        }
        return false;
    }

    public String comparisonWords(String word, String answer) {
        StringBuilder signsAnswer = new StringBuilder(LENGTH_OF_WORD);
        List<Boolean> isLetterCheckInAnswer = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) isLetterCheckInAnswer.add(false);
        return checkInCycleWithRecursion(word, answer, signsAnswer, 0, isLetterCheckInAnswer).toString();
    }

    public StringBuilder checkInCycleWithRecursion(String word, String answer, StringBuilder signsAnswer,
                                                   int currentIndex, List<Boolean> isLetterCheckInAnswer) {
        char currentLetterCheck = word.toCharArray()[0];
        checkInCycle(answer, signsAnswer, currentIndex, isLetterCheckInAnswer, currentLetterCheck, word);
        if (currentIndex != 4) {
            return checkInCycleWithRecursion(word.substring(1), answer, signsAnswer,
                    currentIndex + 1, isLetterCheckInAnswer);
        }
        return signsAnswer;
    }

    public void checkInCycle(String answer, StringBuilder signsAnswer,
                             int currentIndex, List<Boolean> isLetterCheckInAnswer, char currentLetterCheck, String word) {
        for (int i = 0; i < answer.length(); i++) {
            if (answer.toCharArray()[i] == currentLetterCheck && isLetterCheckInAnswer.get(i) == false) {
                if (currentIndex == i) {
                    signsAnswer.append('+');
                } else signsAnswer.append('^');
                isLetterCheckInAnswer.set(i, true);
                break;
            } else if (answer.toCharArray()[i] == currentLetterCheck && isLetterCheckInAnswer.get(i) == true
                    && currentIndex == i && i + 1 < answer.length()) { // если буква в слове-предположении уже завоевала ^ но ей пришла на смену
                // вторая которая стоит на своем месте
                while (signsAnswer.length() <= i) {
                    signsAnswer.append('-');
                }
                signsAnswer.setCharAt(i, '+');
                for (int t = 0; t < signsAnswer.length(); t++) {
                    if (t < word.length() && signsAnswer.charAt(t) == '^'
                            && word.charAt(t) == currentLetterCheck) {
                        signsAnswer.setCharAt(t, '-');
                        break;
                    }
                }
            }
        }
        if (signsAnswer.length() < currentIndex + 1) {
            signsAnswer.append('-');
        }
    }

    public String getHelpWord(Map<Integer, String> historyOfWords, String answer) {
        Map<String, String> comparisonMap = comparisonAllHistoryWords(historyOfWords, answer);

        fillCurrentValues(comparisonMap);

        String helpWord = null;
        for (String i : words) {
            if (isLetterThatNotExistInWord(lettersThatNotExist, i) && !isWordInHistoryOfWords(i, historyOfWords)
                    && isLetterInWordExistOnYourPlace(guessLettersOnYourPlace, i) &&
                    isLetterInWordExistButNotOnYourPlace(guessLettersNotOnYourPlace, i) &&
                    !isAllLetterInWordDontExistInAnswerOrAllLetterExist(i, answer)) {
                helpWord = i;
                break;
            }
        }

        if (helpWord == null) {
            for (String i : words) {
                if (comparisonWords(i, answer).equals("+++++")) return i;
            }
        }

        return helpWord;
    }

    public boolean isAllLetterInWordDontExistInAnswerOrAllLetterExist(String i, String answer) {
        return (comparisonWords(i, answer).equals("-----") || comparisonWords(i, answer).equals("+++++"));
    }

    public boolean isWordInHistoryOfWords(String word, Map<Integer, String> historyOfWords) {
        return historyOfWords.containsValue(word);
    }

    public boolean isLetterInWordExistOnYourPlace(Map<Integer, Character> guessLettersOnYourPlace, String word) {
        for (int t : guessLettersOnYourPlace.keySet()) { // проверка находятся ли в нашем искомом
            // слове ТОЧНО известные буквы на своих местах
            if (word.charAt(t) != guessLettersOnYourPlace.get(t)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLetterInWordExistButNotOnYourPlace(Map<Character, ArrayList<Integer>> guessLettersNotOnYourPlace, String word) {
        for (Character t : guessLettersNotOnYourPlace.keySet()) { // проверка находятся ли в слове те буквы
            // которые там есть но находятся не на своем месте,
            // и проверка чтобы они были на другом месте в слове - подсказке
            if (!word.contains(t.toString())) return false;
            for (int i : guessLettersNotOnYourPlace.get(t)) {
                if (word.indexOf(t) == i) return false;
            }
        }
        return true;
    }

    public boolean isLetterThatNotExistInWord(Set<Character> lettersThatNotExist, String word) {
        // проверка что букв, которых мы узнали что нет (из предыдущих итераций), не будет в слове - подсказке
        for (Character i : lettersThatNotExist) {
            if (word.contains(i.toString())) return false;
        }
        return true;
    }

    public Map<String, String> comparisonAllHistoryWords(Map<Integer, String> historyOfWords, String answer) {
        Map<String, String> comparisonMap = new HashMap<>(); // ключ - слово игрока значение - его сравнение с ответом (схема)
        for (int i : historyOfWords.keySet()) {
            comparisonMap.put(historyOfWords.get(i), comparisonWords(historyOfWords.get(i), answer));
        }
        return comparisonMap;
    }

    public void fillCurrentValues(Map<String, String> comparisonMap) {
        for (String str : comparisonMap.keySet()) {
            String lastComparison = comparisonMap.get(str);
            if (lastComparison.contains("+")) {
                int index = 0;
                for (char i : lastComparison.toCharArray()) {
                    if (i == '+') {
                        guessLettersOnYourPlace.put(index, str.charAt(index));
                        if (guessLettersNotOnYourPlace.containsKey(str.charAt(index))) {
                            guessLettersNotOnYourPlace.remove(str.charAt(index));
                        }
                    }
                    index++;
                }
            }
            if (lastComparison.contains("^")) {
                int index = 0;
                for (char i : lastComparison.toCharArray()) {
                    if (i == '^' && !guessLettersOnYourPlace.containsValue(str.charAt(index))) {
                        if (!guessLettersNotOnYourPlace.containsKey(str.charAt(index))) {
                            guessLettersNotOnYourPlace.put(str.charAt(index), new ArrayList<>());
                        }
                        guessLettersNotOnYourPlace.get(str.charAt(index)).add(index);
                    }
                    index++;
                }
            }
            if (lastComparison.contains("-")) {
                int index = 0;
                for (int j = 0; j < lastComparison.length(); j++) {
                    if (lastComparison.charAt(j) == '-' && !guessLettersOnYourPlace.containsValue(str.charAt(index))
                            && !guessLettersNotOnYourPlace.containsKey(str.charAt(index))) {
                        lettersThatNotExist.add(str.charAt(j));
                    }
                    index++;
                }
            }
        }
    }

}
