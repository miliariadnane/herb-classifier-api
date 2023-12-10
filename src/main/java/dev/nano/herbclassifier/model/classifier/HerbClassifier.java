package dev.nano.herbclassifier.model.classifier;

import lombok.extern.slf4j.Slf4j;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.Random;

@Slf4j
public class HerbClassifier {

    private static final int height = 28;
    private static final int width = 28;
    private static final int channels = 3; // RGB
    private static final int outputNum = 2; // The number of possible outcomes (coriander or parsley)
    private static final int batchSize = 54; // How many examples to fetch with each step
    private static final int nEpochs = 50; // Number of training epochs (full passes of training data)
    private static final int seed = 1234;
    private static final Random randomGenNum = new Random(seed);
    private static final String dataPath = "src/main/resources/dataset";
    private static final String modelFilePath = "src/main/resources/model/herb_model.zip";

    public static void main(String[] args) throws IOException {

        log.info("Data vectorization...");
        // Vectorize the data
        FileSplit filesInDir = new FileSplit(new File(dataPath), NativeImageLoader.ALLOWED_FORMATS, randomGenNum);
        ImageRecordReader recordReader = new ImageRecordReader(height, width, channels, new ParentPathLabelGenerator());
        recordReader.initialize(filesInDir);

        DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum);
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.fit(dataIter);
        dataIter.setPreProcessor(scaler);

        // Network configuration
        log.info("Network configuration...");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .l2(0.0005)
                //.updater(new Nesterovs(0.006, 0.9)) // (old learning rate)
                //.updater(new Nesterovs(0.01, 0.9))  // Increased learning rate
                .updater(new Adam(0.01, 0.9, 0.999, 0.01)) // changed to Adam
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new ConvolutionLayer.Builder(5, 5)
                        .nIn(channels)
                        .stride(1, 1)
                        .nOut(20)
                        .activation(Activation.RELU)
                        .build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(new ConvolutionLayer.Builder(5, 5)
                        .stride(1, 1)
                        .nOut(50)
                        .activation(Activation.RELU)
                        .build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(new DenseLayer.Builder()
                        .activation(Activation.RELU)
                        //.nOut(500) (old number of nodes)
                        .nOut(1000)  // Increased the number of nodes
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        //.layer(new OutputLayer.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
                        .nOut(outputNum)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(height, width, channels))
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        // Initialize the user interface backend (used to plot the loss function score during training)
        UIServer uiServer = UIServer.getInstance();
        // Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        InMemoryStatsStorage statsStorage = new InMemoryStatsStorage();
        // Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        log.info("Train model....");
        for (int i = 0; i < nEpochs; i++) { // Increased the number of epochs 1 / 10 / 20 / 30 / 50
            model.fit(dataIter);
            log.info("Training epoch number: " + (i + 1) + " with score: " + model.score());
        }

        log.info("Evaluate model....");
        Evaluation eval = model.evaluate(dataIter);
        System.out.println(eval.stats());

        log.info("Save model....");
        // Create directories if they don't exist
        File dir = new File("src/main/resources/model");
        if (!dir.exists()) {
            boolean dirsCreated = dir.mkdirs();
            if (!dirsCreated) {
                log.error("Could not create directories: " + dir.getAbsolutePath());
                return;
            }
        }

        ModelSerializer.writeModel(model, new File(modelFilePath), true);
    }
}
