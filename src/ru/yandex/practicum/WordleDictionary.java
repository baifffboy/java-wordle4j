package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;

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

    public boolean check(String word) {
        if(word.length() == LENGTH_OF_WORD){
            for(String i : words) {
                if(i.equals(word)){
                    return true;
                }
            }
        } else {
            System.out.printf("Слово содержит больше или меньше %d букв", LENGTH_OF_WORD);
        }
        return false;
    }

    public String comparisonWords(String word, String answer){
        StringBuilder signsAnswer = new StringBuilder(LENGTH_OF_WORD);
        word = word.toLowerCase().replaceAll("ё", "е");
        answer = answer.toLowerCase().replaceAll("ё", "е");
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
}
