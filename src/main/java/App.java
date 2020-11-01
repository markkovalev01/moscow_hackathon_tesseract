import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FilenameUtils;
import utils.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    @SneakyThrows
    public static void main(String[] args) {

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("rus");
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(1);

        FileUtils.getFiles("Dataset/БТИ").forEach(dir -> {
            dir.forEach(file -> {
                List<BufferedImage> bufferedImageList = FileUtils.fileToBuffImg(file);
                if(bufferedImageList==null){
                    return;
                }
                HashMap<String, String> pages = new LinkedHashMap<>();
                bufferedImageList.forEach(bi -> {
                    try {
                        long startTime = System.nanoTime();
                        String result = tesseract.doOCR(bi);
                        Pattern spacePattern = Pattern.compile("[\\s\\n]+");
                        Matcher spaceMatcher = spacePattern.matcher(result);
                        String newS = spaceMatcher.replaceAll(" ");
                        long endTime = System.nanoTime();
                        long timeElapsed = endTime - startTime;
                        System.out.println(timeElapsed);
                        System.out.println("Execution time in milliseconds : " +
                                timeElapsed / 1000000);
                        pages.put("page" + bufferedImageList.indexOf(bi), newS);
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            mapper.writeValue(new File(FilenameUtils.removeExtension(file) + ".json"), pages);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (TesseractException e) {
                        e.printStackTrace();
                    }
                });
            });
        });


    }
}
