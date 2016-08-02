package com.xinheng.logic.user.logic;

import com.xinheng.frame.logic.BaseOkHttpLogic;
import com.xinheng.frame.net.OkHttpAsyncRequest;
import com.xinheng.logic.user.parser.UserParser;
import com.xinheng.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * [description about this class]
 *
 * @author jack
 * @date 2016/7/15 10:43
 */
public class UserLogic extends BaseOkHttpLogic {

    public UserLogic(Object subscriber) {
        super(subscriber);
    }

    public void userlogin(int tag, String account, String password){

        String url = Constants.USER_LOGIN;
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("password", password);
        String body = toBody(map,null);
        OkHttpAsyncRequest request = new OkHttpAsyncRequest(tag,url,new UserParser(),this);
        sendRequestForPost(request, url, body, false);
    }
}
