package ru.yandex.practicum;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader +
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) throws IOException, GameStateException, DictionaryLoadException, WordNotFoundException, InvalidWordLengthException {
        final int COUNT_OF_STEP = 6;
        WordleDictionary dict = WordleDictionaryLoader.uploadingFiveLetterWords();
        Random random = new Random();
        String answer = dict.getWords().get(random.nextInt(dict.getWords().size()));
        WordleGame game = new WordleGame(answer, 0, dict);
        System.out.printf("Вводите поочередно слова, у вас есть %d попыток\n", COUNT_OF_STEP);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (game.getSteps() == COUNT_OF_STEP) {
                System.out.println("Вы проиграли!");
                break;
            }
            String word = scanner.nextLine();
            if (answer.equals(word)) {
                System.out.printf("Вы отгадали слово: %s\n", game.getAnswer());
                break;
            }
            String answerToTheWord = game.analyze(word);
            if (answerToTheWord != null) {
                System.out.println(answerToTheWord);
                if (word.isEmpty()) System.out.println(game.analyze(answerToTheWord));
            } else {
                throw new GameStateException("Слово введено некорректно или несоответствует параметрам");
            }
        }
    }
}
