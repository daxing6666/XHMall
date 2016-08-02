package com.xinheng.logic.user.model;

/**
 * [description about this class]
 * 用户信息
 * @author jack
 * @date 2016/7/14 09:32
 */

public class UserInfo {

    /**
     * id : 1
     * name : Tom
     * nickname : 张三
     * photo : 1.png
     * mobile : 1346455688
     * lastlogintime : 5689445665
     * sessionId : 5ccd9757-e68f-4069-9064-1c3f1a372295
     *  defaultAddress : {"id":"1","name":"llxya123","mobile":"l15685246549","city":"江苏省苏州市","address":"工业园区198号","zipcode":"215000"}
     */

    private String id;
    private String name;
    private String nickname;
    private String photo;
    private String mobile;
    private String lastlogintime;
    private String sessionId;
    /**
     * id : 1
     * name : llxya123
     * mobile : l15685246549
     * city : 江苏省苏州市
     * address : 工业园区198号
     * zipcode : 215000
     */

    private defaultAddressEntity defaultAddress;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setLastlogintime(String lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static class defaultAddressEntity {
        private String id;
        private String name;
        private String mobile;
        private String city;
        private String address;
        private String zipcode;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getMobile() {
            return mobile;
        }

        public String getCity() {
            return city;
        }

        public String getAddress() {
            return address;
        }

        public String getZipcode() {
            return zipcode;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public String getMobile() {
        return mobile;
    }

    public String getLastlogintime() {
        return lastlogintime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public defaultAddressEntity getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(defaultAddressEntity defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
