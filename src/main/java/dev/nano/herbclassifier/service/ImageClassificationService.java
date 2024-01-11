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
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageClassificationService {
    private static final String[] LABELS = {"Coriander", "Parsley", "Not sure"};
    private static final String MODEL_FILE_PATH = "model/herb_model.zip";
    private static final double CERTAINTY_THRESHOLD = 0.9;
    private MultiLayerNetwork model;

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
        log.info("Validating file...");
        validateFile(file);
        try {
            INDArray image = convertFileToINDArray(file);
            log.info("Classifying image...");
            String label = classifyImage(image);
            log.info("Image classified as {}", label);
            return label;
        } catch (IOException e) {
            log.error("Failed to process image", e);
            throw new IllegalStateException("Failed to process image", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Invalid file");
        }
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new NotAnImageFileException("File is not an image");
        }
        log.info("File is valid");
    }

    private INDArray convertFileToINDArray(MultipartFile file) throws IOException {
        log.info("Converting file to INDArray...");
        BufferedImage img = ImageIO.read(file.getInputStream());
        INDArray image = new NativeImageLoader(28, 28, 3).asMatrix(img).reshape(1, 3, 28, 28);
        new ImagePreProcessingScaler(0, 1).transform(image);
        log.info("File converted to INDArray");
        return image;
    }

    private String classifyImage(INDArray image) {
        INDArray output = model.output(image);
        long classIdx = Nd4j.argMax(output, 1).getLong(0);

        if (output.getDouble(classIdx) < CERTAINTY_THRESHOLD) {
            return LABELS[2];
        }

        return LABELS[(int) classIdx];
    }
}
