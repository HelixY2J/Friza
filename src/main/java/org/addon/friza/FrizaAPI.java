package org.addon.friza;

import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zaproxy.zap.extension.api.ApiAction;
import org.zaproxy.zap.extension.api.ApiException;
import org.zaproxy.zap.extension.api.ApiImplementor;
import org.zaproxy.zap.extension.api.ApiResponse;
import org.zaproxy.zap.extension.api.ApiResponseElement;

public class FrizaAPI extends ApiImplementor {
    private static final String PREFIX = "friza";

    private static final String ACTION_HELLO_WORLD = "helloWorld";

    private static final Logger LOGGER = LogManager.getLogger(FrizaAPI.class);

    public FrizaAPI() {
        this.addApiAction(new ApiAction(ACTION_HELLO_WORLD));
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public ApiResponse handleApiAction(String name, JSONObject params) throws ApiException {
        switch (name) {
            case ACTION_HELLO_WORLD:
                LOGGER.debug("hello world called");
                break;

            default:
                throw new ApiException(ApiException.Type.BAD_ACTION);
        }

        return ApiResponseElement.OK;
    }
}
