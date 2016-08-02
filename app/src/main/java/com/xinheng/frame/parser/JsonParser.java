package com.xinheng.frame.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinheng.frame.interfaces.IresponseParserListener;
import com.xinheng.frame.model.InfoResult;

/**
 * [description about this class]
 * 把网络数据统一转成InfoResult
 * @author jack
 */

public abstract class JsonParser implements IresponseParserListener {

    @Override
    public InfoResult doParse(String response) throws Exception {

        /**
         * -1：没有数据
         * -2：自己定义为没有网络
         * -4:session失效
         */
        JSONObject jsonObject = JSON.parseObject(response);
        InfoResult infoResult = new InfoResult();
        infoResult.setSuccess(jsonObject.getString("result").equals("1"));
        infoResult.setDesc(jsonObject.getString("message"));
        infoResult.setStateResult(jsonObject.getString("result"));
        parseResponse(infoResult, jsonObject);
        return infoResult;
    }

    @Override
    public InfoResult doParse(byte[] responseResult) throws Exception {
        return null;
    }

    protected abstract void parseResponse(final InfoResult infoResult, final JSONObject jsonObject);
}
