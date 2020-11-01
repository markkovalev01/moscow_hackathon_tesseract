import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

public class AppSpark {


    public static void main(String[] args) throws IOException {
        StringBuffer res = new StringBuffer("");
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap<String, String> map;
        File file = new File("resData/Разр. на стр-во");
        for (File listFile : file.listFiles()) {
            map = objectMapper.readValue(listFile, new TypeReference<LinkedHashMap<String, String>>() {
            });
            map.forEach((s, s2) -> {
                res.append(s2 + " ");
            });
        }

//        File file = new File("resData/БТИ");
//        map = objectMapper.readValue(file, new TypeReference<LinkedHashMap<String, String>>() {
//                        });
//            map.forEach((s, s2) -> {
//                res.append(s2 + " ");
//            });
        List<String> stringList = Arrays.asList(res.toString().split(" "));
        System.out.println(stringList.size());
        Map<String, Double> collect =
                stringList.stream().filter(s -> {
                    return (s.length() >= 3 && !s.matches("^[0-9]*$"));
                }).collect(groupingBy(Function.identity(), summingDouble(value -> 1.0)));

        LinkedHashMap<String, Double> countByWordSorted = collect.entrySet()
                .stream()
                .map(stringDoubleEntry -> {
                    stringDoubleEntry.setValue(stringDoubleEntry.getValue() * 100. / stringList.size());
                    return stringDoubleEntry;
                })
                .filter(stringDoubleEntry -> {
                    return stringDoubleEntry.getValue() >= 0.08;
                })
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(25)
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {
                            throw new IllegalStateException();
                        },
                        LinkedHashMap::new
                ));
        System.out.println(countByWordSorted);

        File resultFile = new File("resData/" + FilenameUtils.getName("resData/Свид. АГР")+".json");
        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }

        objectMapper.writeValue(resultFile, countByWordSorted);
//        PrintWriter writer = new PrintWriter(resultFile);
//        writer.print(countByWordSorted+"");
//        writer.close();
    }
}
