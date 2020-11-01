import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AppClassification {
    public static void main(String[] args) throws IOException {
        List<Map<String, String>> templateList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("ethalonTemplate/");
        for (File listFile : file.listFiles()) {
            LinkedHashMap<String, String> map;
            map = objectMapper.readValue(listFile, new TypeReference<LinkedHashMap<String, String>>() {
            });
            templateList.add(map);
        }
        LinkedHashMap<String, String> map;
        map = objectMapper.readValue(new File("Dataset/БТИ/10. ул Шеногина, дом 3, строение 37 изм.json"), new TypeReference<LinkedHashMap<String, String>>() {
        });
        int[] match = new int[templateList.size()];
        map.forEach((s, s2) -> {
            templateList.forEach(stringStringMap -> {
                if (stringStringMap.containsKey(s)) {
                    match[templateList.indexOf(stringStringMap)]++;
                }
            });
        });
        System.out.println(templateList.get(Arrays.stream(match).max().getAsInt()));
    }
}
