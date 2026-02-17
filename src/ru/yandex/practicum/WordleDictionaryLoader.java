package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    public static final int LENGTH_OF_WORD = 5;

    public static WordleDictionary uploadingFiveLetterWords() throws FileNotFoundException {
        List<String> list = new ArrayList<>();
        String path = "../../words_ru.txt";
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while(reader.ready()){
                if(reader.readLine().length() == LENGTH_OF_WORD) list.add(reader.readLine());
            }
            return new WordleDictionary(list);
        } catch(FileNotFoundException exception) {
            System.out.println("По указанному пути не найдено имя файла! " + exception.getMessage());
            return new WordleDictionary(new ArrayList<>());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла! " + e.getMessage());
            return new WordleDictionary(new ArrayList<>());
        }
    }
}
