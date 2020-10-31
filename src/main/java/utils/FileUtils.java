package utils;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {


    @SneakyThrows
    public static List<BufferedImage> fileToBuffImg(String filePath) {
        if (FilenameUtils.getExtension(filePath.toLowerCase()).equals("pdf")) {
            return pdfToBuffImg(filePath);
        }
        if(FilenameUtils.getExtension(filePath.toLowerCase()).equals("json")){
            return null;
        }
            return imgToBuffImg(filePath);

    }

    @SneakyThrows
    private static List<BufferedImage> imgToBuffImg(String filePath) {
        List<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();
        bufferedImages.add(ImageIO.read(new File(filePath)));
        return bufferedImages;
    }

    @SneakyThrows
    private static List<BufferedImage> pdfToBuffImg(String filePath) {
        PDDocument document = PDDocument.load(new File(filePath));
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<BufferedImage> res = new ArrayList<BufferedImage>();
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(
                    page, 300, ImageType.RGB);

            res.add(bim);
//            System.out.println(res.size());
        }
        document.close();
        return res;
    }

    @SneakyThrows
    public static List<List<String>> getFiles(String path) {
        File file = new File(path);
        List<List<String>> res = new ArrayList<>();
        if (file.isDirectory()) {
            if (!file.listFiles()[0].isDirectory()) {
                res.add(Arrays.stream(file.listFiles()).map(File::getPath).collect(Collectors.toList()));
                return res;
            }
            Arrays.stream(file.listFiles()).forEach(dir -> {
                if (dir.isDirectory()) {
                    res.add(Arrays.stream(dir.listFiles()).map(f -> {
                        return f.getPath();
                    }).collect(Collectors.toList()));
                }
            });
        }
//        else {
//            res.add(Arrays.asList(file.getPath()));
//        }
        return res;
    }
}
