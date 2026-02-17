package ru.yandex.practicum;

import java.io.FileNotFoundException;
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

    public static void main(String[] args) throws FileNotFoundException {
        final int COUNT_OF_STEP = 6;
        WordleDictionary dict = WordleDictionaryLoader.uploadingFiveLetterWords();
        Random random = new Random();
        WordleGame game = new WordleGame(dict.getWords().get(random.nextInt(dict.getWords().size())), 0, dict);
        System.out.printf("Вводите поочередно слова, у вас есть %d попыток", COUNT_OF_STEP);
        Scanner scanner = new Scanner(System.in);
        String word = scanner.nextLine();
        while (true) {
            if (game.win(word)){
                System.out.printf("Вы отгадали слово: %s", game.getAnswer());
                break;
            }
            if (game.getSteps() == COUNT_OF_STEP) {
                System.out.println("Вы проиграли!");
                break;
            }
        }
    }

}
