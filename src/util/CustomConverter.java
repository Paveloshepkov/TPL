package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomConverter {
    public List<String> convertStringToList(String string) {

        List<String> list = new ArrayList<>();
        if (!string.isEmpty()) {

            String[] words = string.split("\\s+");

            Set<String> uniqueWords = new HashSet<>();

            for (String word : words) {
                if (!word.isEmpty()) {
                    uniqueWords.add(word);
                }
            }

            list.addAll(uniqueWords);
        }
        return list;
    }
}
