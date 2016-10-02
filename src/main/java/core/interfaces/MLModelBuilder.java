package core.interfaces;

/**
 * Created by wso2123 on 8/31/16.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import data.schema.MLModel;
import core.exceptions.MLModelBuilderException;
import core.internals.MLModelConfigurationContext;

/**
 * All Model Builders should extend this class.
 */
public abstract class MLModelBuilder {

    private static final Log log = LogFactory.getLog(MLModelBuilder.class);
    private MLModelConfigurationContext context = null;

    public MLModelBuilder(MLModelConfigurationContext context) {
        this.setContext(context);
    }

    /**
     * Build a model using the context.
     * @return build {@link MLModel}
     * @throws MLModelBuilderException if failed to build the model.
     */
    public abstract MLModel build() throws MLModelBuilderException;

    public void handleIgnoreException(String msg, Exception e) {
        log.error(msg, e);
    }

    public MLModelConfigurationContext getContext() {
        return context;
    }

    public void setContext(MLModelConfigurationContext context) {
        this.context = context;
    }

}

