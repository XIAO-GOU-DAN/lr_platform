package com.lr.platform.entity.users;

import com.lr.platform.annotations.PrivacyEncrypt;
import com.lr.platform.enums.PrivacyType;

public class UsersRankGuestVo {

    private Integer id;

    private String username;

    //@PrivacyEncrypt(type = PrivacyType.NAME)
    //private String name;

    @PrivacyEncrypt(type = PrivacyType.QQ)
    private String qq;

    //@PrivacyEncrypt(type = PrivacyType.PHONE)
    //private String phone;

    //@PrivacyEncrypt(type = PrivacyType.STUDENT_NUMBER)
    //private String studentNum;

    private String grade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
