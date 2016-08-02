package com.xinheng.logic.user.parser;

import com.alibaba.fastjson.JSONObject;
import com.xinheng.frame.model.InfoResult;
import com.xinheng.frame.parser.JsonParser;
import com.xinheng.logic.user.model.UserInfo;
import com.xinheng.util.GsonUtils;

/**
 * [description about this class]
 *
 * @author jack
 * @date 2016/7/15 10:44
 */
public class UserParser extends JsonParser {
    @Override
    protected void parseResponse(InfoResult infoResult, JSONObject jsonObject) {
        if (infoResult.isSuccess()) {
            JSONObject jsonObjectResult = jsonObject.getJSONObject("properties");
            UserInfo bean = GsonUtils.getInstance().jsonToClass(jsonObjectResult.toJSONString(),UserInfo.class);
            infoResult.setExtraObj(bean);
        }
    }
}
