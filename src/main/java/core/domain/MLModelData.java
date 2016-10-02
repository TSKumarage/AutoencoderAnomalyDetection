package core.domain;

/**
 * Created by wso2123 on 9/2/16.
 */
public class MLModelData {


    private long id;
    private String name;
    private int tenantId;
    private String userName;
    private String createdTime;
    private long analysisId;
    private long versionSetId;
    private String datasetVersion;
    private String storageType;
    private String storageDirectory;
    private String status;
    private String error;
    //private ModelSummary modelSummary;

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getStorageDirectory() {
        return storageDirectory;
    }

    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(long analysisId) {
        this.analysisId = analysisId;
    }

    public long getVersionSetId() {
        return versionSetId;
    }

    public void setVersionSetId(long versionSetId) {
        this.versionSetId = versionSetId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

   /* public ModelSummary getModelSummary() {
        return modelSummary;
    }

    public void setModelSummary(ModelSummary modelSummary) {
        this.modelSummary = modelSummary;
    }*/

    public String getDatasetVersion() {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion) {
        this.datasetVersion = datasetVersion;
    }

    @Override
    public String toString() {
        return "MLModelNew [id=" + id + ", name=" + name + ", tenantId=" + tenantId + ", userName=" + userName
                + ", createdTime=" + createdTime + ", analysisId=" + analysisId + ", versionSetId=" + versionSetId
                + ", storageType=" + storageType + ", storageDirectory=" + storageDirectory + ", status=" + status
                + ", error=" + error + "]";
    }
}
