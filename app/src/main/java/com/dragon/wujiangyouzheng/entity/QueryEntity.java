package com.dragon.wujiangyouzheng.entity;

public class QueryEntity {

    /**
     * user_name : 派件员
     * user_tel : 15651792591
     * mail_code : xbnn
     * user_code : 10001
     * upload_date : 2018-07-11 14:57:33
     * photoPath : uploadPhotos/2018/07/11/1531292253284259.jpg,
     * uploadPhotos/2018/07/11/153129225328449.jpg,
     * mail_id : 19
     */

    private String user_name;
    private String user_tel;
    private String mail_code;
    private String user_code;
    private String upload_date;
    private String photoPath;
    private int mail_id;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_tel() {
        return user_tel;
    }

    public void setUser_tel(String user_tel) {
        this.user_tel = user_tel;
    }

    public String getMail_code() {
        return mail_code;
    }

    public void setMail_code(String mail_code) {
        this.mail_code = mail_code;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getMail_id() {
        return mail_id;
    }

    public void setMail_id(int mail_id) {
        this.mail_id = mail_id;
    }
}
