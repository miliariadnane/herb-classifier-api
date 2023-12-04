package dev.nano.herbclassifier.service;

import dev.nano.herbclassifier.common.exception.domain.InvalidFileException;
import dev.nano.herbclassifier.common.exception.domain.NotAnImageFileException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageClassificationService {
    private MultiLayerNetwork model;
    private static final String MODEL_FILE_PATH = "model/herb_model.zip";
    private static final String[] LABELS = {"Coriander", "Parsley"};

    @PostConstruct
    public void init() {
        log.info("Initializing ImageClassificationService...");
        try {
            InputStream modelInputStream = new ClassPathResource(MODEL_FILE_PATH).getInputStream();
            model = ModelSerializer.restoreMultiLayerNetwork(modelInputStream);
            log.info("Model loaded from {}", MODEL_FILE_PATH);
        } catch (IOException e) {
            log.error("Failed to load model from {}", MODEL_FILE_PATH, e);
            throw new IllegalStateException("Failed to load model", e);
        }
    }

    public String getHerbClassification(MultipartFile file) {
        validateFile(file);
        try {
            INDArray image = convertFileToINDArray(file);
            String label = classifyImage(image);
            return label;
        } catch (IOException e) {
            log.error("Failed to upload file to AWS S3", e);
            throw new IllegalStateException("Failed to upload file to AWS S3", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Invalid file");
        }
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new NotAnImageFileException("File is not an image");
        }
    }

    private Map<String, String> createMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private INDArray convertFileToINDArray(MultipartFile file) throws IOException {
        BufferedImage img = ImageIO.read(file.getInputStream());
        INDArray image = new NativeImageLoader(28, 28, 1).asMatrix(img).reshape(1, 1, 28, 28);
        new ImagePreProcessingScaler(0, 1).transform(image);
        return image;
    }

    private String classifyImage(INDArray image) {
        long classIdx = Nd4j.argMax(model.output(image), 1).getLong(0);
        return LABELS[(int) classIdx];
    }
}
