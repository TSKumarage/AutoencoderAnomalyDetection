package core.internals;

/**
 * Created by wso2123 on 8/31/16.
 */
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import core.domain.MLModelData;
import data.reader.SampleFileReader;
import data.schema.Workflow;

/**
 * Represent configuration objects require to build a model.
 */


public class MLModelConfigurationContext {

    private long modelId;
    private MLModelData model;
    private Workflow facts;
    private String headerRow;
    private String columnSeparator;
    private String[] dataToBePredicted;
    private Map<String,String> summaryStatsOfFeatures;
    private SampleFileReader dataScanner;
    /**
     * Encodings list.
     * index - index of the feature. (last index is response variable)
     * value - encodings.
     */
    private List<Map<String, Integer>> encodings;
    /**
     * new to old index mapping for feature set (without response)
     */
    private List<Integer> newToOldIndicesList;
    private int responseIndex;

    /**
     * Key - feature index
     * Value - feature name
     */
    private SortedMap<Integer,String> includedFeaturesMap;

    public long getModelId() {
        return modelId;
    }
    public void setModelId(long modelId) {
        this.modelId = modelId;
    }
    public Workflow getFacts() {
        return facts;
    }
    public void setFacts(Workflow facts) {
        this.facts = facts;
    }
    public String getHeaderRow() {
        return headerRow;
    }
    public void setHeaderRow(String headerRow) {
        this.headerRow = headerRow;
    }
    public String getColumnSeparator() {
        return columnSeparator;
    }
    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }
    public String[] getDataToBePredicted() {
        return dataToBePredicted;
    }

    public void setDataToBePredicted(String[] dataToBePredicted) {
        if (dataToBePredicted == null) {
            this.dataToBePredicted = new String[0];
        } else {
            this.dataToBePredicted = Arrays.copyOf(dataToBePredicted, dataToBePredicted.length);
        }
    }
    public MLModelData getModel() {
        return model;
    }
    public void setModel(MLModelData model) {
        this.model = model;
    }
    public Map<String,String> getSummaryStatsOfFeatures() {
        return summaryStatsOfFeatures;
    }
    public void setSummaryStatsOfFeatures(Map<String,String> summaryStatsOfFeatures) {
        this.summaryStatsOfFeatures = summaryStatsOfFeatures;
    }
    public List<Map<String, Integer>> getEncodings() {
        return encodings;
    }
    public void setEncodings(List<Map<String, Integer>> encodings) {
        this.encodings = encodings;
    }
    public List<Integer> getNewToOldIndicesList() {
        return newToOldIndicesList;
    }
    public void setNewToOldIndicesList(List<Integer> oldToNewIndicesList) {
        this.newToOldIndicesList = oldToNewIndicesList;
    }
    public SortedMap<Integer,String> getIncludedFeaturesMap() {
        return includedFeaturesMap;
    }
    public void setIncludedFeaturesMap(SortedMap<Integer,String> includedFeaturesMap) {
        this.includedFeaturesMap = includedFeaturesMap;
    }
    public int getResponseIndex() {
        return responseIndex;
    }
    public void setResponseIndex(int responseIndex) {
        this.responseIndex = responseIndex;
    }

    public SampleFileReader getDataScanner() {
        return dataScanner;
    }

    public void setDataScanner(SampleFileReader dataScanner) {
        this.dataScanner = dataScanner;
    }
}
