package ru.yandex.practicum;

import ru.yandex.practicum.exception.DictionaryLoadException;

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

    public static WordleDictionary uploadingFiveLetterWords() throws DictionaryLoadException, IOException {
        List<String> list = new ArrayList<>();
        String path = "words_ru.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while (reader.ready()) {
                String word = reader.readLine();
                if (word.length() == LENGTH_OF_WORD) list.add(word.toLowerCase().replaceAll("ё", "е"));
            }
            return new WordleDictionary(list);
        } catch (FileNotFoundException exception) {
            throw new DictionaryLoadException("По указанному пути не найдено имя файла! " + exception.getMessage(), exception);
        } catch (IOException e) {
            throw new IOException("Ошибка чтения файла! " + e.getMessage());
        }
    }
}
