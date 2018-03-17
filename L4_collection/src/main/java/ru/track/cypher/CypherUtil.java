package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Вспомогательные методы шифрования/дешифрования
 */
public class CypherUtil {

    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Генерирует таблицу подстановки - то есть каждой буква алфавита ставится в соответствие другая буква
     * Не должно быть пересечений (a -> x, b -> x). Маппинг уникальный
     *
     * @return таблицу подстановки шифра
     */
    @NotNull
    public static Map<Character, Character> generateCypher() {
        Map<Character, Character> cypher = new HashMap<>();
        char[] charArray = SYMBOLS.toCharArray();

        List<Character> tmpList = new ArrayList<>();
        int qtChars = charArray.length;

        for(int i = 0; i < qtChars; i++) {
            tmpList.add(charArray[i]);
        }

        Collections.shuffle(tmpList);

        for(int i = 0; i < qtChars; i++) {
            cypher.put(charArray[i], tmpList.get(i));
        }

        return cypher;
    }

}
