package core.algorithms;

/**
 * Created by wso2123 on 8/31/16.
 */
import core.exceptions.AlgorithmNameException;
import core.exceptions.MLModelBuilderException;
import core.interfaces.MLModelBuilder;
import core.internals.MLModelConfigurationContext;
import data.reader.LabeledPoint;
import data.reader.SampleFileParser;
import data.schema.MLConstants;
import data.schema.MLModel;
import data.schema.Workflow;
import hex.deeplearning.DeepLearningModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

/**
 * Build deep learning models
 */
public class DeeplearningModelBuilder extends MLModelBuilder {
    private static final Log log = LogFactory.getLog(DeeplearningModelBuilder.class);

    public DeeplearningModelBuilder(MLModelConfigurationContext context) {
        super(context);
    }

    @Override
    public MLModel build() throws MLModelBuilderException {

        if (log.isDebugEnabled()) {
            log.debug("Start building the Stacked Autoencoders...");
        }
        MLModelConfigurationContext context = getContext();
        /*JavaSparkContext sparkContext = null;
        DatabaseService databaseService = MLCoreServiceValueHolder.getInstance().getDatabaseService();*/
        MLModel mlModel = new MLModel();

        try {

            Workflow workflow = context.getFacts();
            long modelId = context.getModelId();
            SampleFileParser dataReader=context.getDataScanner();
            List<LabeledPoint> data=dataReader.getData();
            int sep=0;
            sep=(int)(data.size()*0.7);
            List<LabeledPoint> trainingData=data.subList(0,sep);
            List<LabeledPoint> testingData=data.subList(sep,data.size());
            // generate train and test datasets by converting tokens to labeled points
            int responseIndex = context.getResponseIndex();
          //  SortedMap<Integer, String> includedFeatures = MLUtils.getIncludedFeaturesAfterReordering(workflow,
                 //   context.getNewToOldIndicesList(), responseIndex);

            // create a deployable MLModel object
            mlModel.setAlgorithmName(workflow.getAlgorithmName());
            mlModel.setAlgorithmClass(workflow.getAlgorithmClass());
            mlModel.setFeatures(workflow.getIncludedFeatures());
            mlModel.setResponseVariable(workflow.getResponseVariable());
            mlModel.setEncodings(context.getEncodings());
            mlModel.setNewToOldIndicesList(context.getNewToOldIndicesList());
            mlModel.setResponseIndex(responseIndex);

           // ModelSummary summaryModel = null;

            MLConstants.DEEPLEARNING_ALGORITHM deeplearningAlgorithm = MLConstants.DEEPLEARNING_ALGORITHM.valueOf(workflow.getAlgorithmName());
            switch (deeplearningAlgorithm) {
                case STACKED_AUTOENCODERS:
                    log.info("Building summary model for SAE");
                    buildStackedAutoencodersModel(modelId, trainingData, testingData, workflow,
                            mlModel);
                    log.info("Successful building summary model for SAE");
                    break;
                default:
                    throw new AlgorithmNameException("Incorrect algorithm name");
            }


          //  databaseService.updateModelSummary(modelId, summaryModel);
            return mlModel;
        } catch (Exception e) {
            throw new MLModelBuilderException(
                    "An error occurred while building supervised machine learning model: " + e.getMessage(), e);
        } finally {
            // do something finally
        }
    }

    private int[] stringArrToIntArr(String str) {
        String[] tokens = str.split(",");
        int[] arr = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            arr[i] = Integer.parseInt(tokens[i]);
        }
        return arr;
    }

    /**
     * Build the stacked autoencoder model
     *
     * @param modelID model ID
     * @param trainingData training data to train the classifier
     * @param testingData testing data to test the classifier and get metrics
     * @param workflow workflow
     * @param mlModel MLModel to be updated with calcualted values
     * @return
     * @throws MLModelBuilderException
     */
    private void buildStackedAutoencodersModel(long modelID,
                                               List<LabeledPoint> trainingData, List<LabeledPoint> testingData, Workflow workflow, MLModel mlModel) throws MLModelBuilderException {
        try {
            StackedAutoencodersClassifier saeClassifier = new StackedAutoencodersClassifier();
            Map<String, String> hyperParameters = workflow.getHyperParameters();

            // train the stacked autoencoder
            DeepLearningModel deeplearningModel = saeClassifier.train(trainingData,
                    Integer.parseInt(hyperParameters.get(MLConstants.BATCH_SIZE)),
                    stringArrToIntArr(hyperParameters.get(MLConstants.LAYER_SIZES)),
                    hyperParameters.get(MLConstants.ACTIVATION_TYPE),
                    Integer.parseInt(hyperParameters.get(MLConstants.EPOCHS)),
                    Integer.parseInt(hyperParameters.get(MLConstants.SEED)),
                    workflow.getResponseVariable(),
                    "Test", mlModel, modelID);

            if (deeplearningModel == null) {
                throw new MLModelBuilderException("DeeplearningModel is Null.");
            }


            // make predictions with the trained model
            Map<Double, Double> predictionsAndLabels = saeClassifier
                    .test(deeplearningModel, testingData, mlModel);

            /*

            // get model summary
            DeeplearningModelSummary deeplearningModelSummary = DeeplearningModelUtils
                    .getDeeplearningModelSummary(sparkContext, testingData, predictionsAndLabels);

            // remove from cache
            testingData.unpersist();

            mlModel.setModel(new MLDeeplearningModel(deeplearningModel));

            deeplearningModelSummary.setFeatures(includedFeatures.values().toArray(new String[0]));
            deeplearningModelSummary.setAlgorithm(MLConstants.DEEPLEARNING_ALGORITHM.STACKED_AUTOENCODERS.toString());

            // set accuracy values
            MulticlassMetrics multiclassMetrics = getMulticlassMetrics(sparkContext, predictionsAndLabels);

            // remove from cache
            predictionsAndLabels.unpersist();

            deeplearningModelSummary
                    .setMulticlassConfusionMatrix(getMulticlassConfusionMatrix(multiclassMetrics, mlModel));
            Double modelAccuracy = getModelAccuracy(multiclassMetrics);
            deeplearningModelSummary.setModelAccuracy(modelAccuracy);
            deeplearningModelSummary.setDatasetVersion(workflow.getDatasetVersion());
*/

           // return deeplearningModelSummary;

        } catch (Exception e) {
            throw new MLModelBuilderException(
                    "An error occurred while building stacked autoencoders model: " + e.getMessage(), e);
        }

    }
}
