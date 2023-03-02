package com.lr.platform.entity.users;

import com.lr.platform.annotations.PrivacyEncrypt;
import com.lr.platform.enums.PrivacyType;

public class UserInfo {
    private Integer id;

    private String username;

    //@PrivacyEncrypt(type = PrivacyType.NAME)
    private String name;

    //@PrivacyEncrypt(type = PrivacyType.QQ)
    private String qq;

    //@PrivacyEncrypt(type = PrivacyType.PHONE)
    private String phone;

    //@PrivacyEncrypt(type = PrivacyType.STUDENT_NUMBER)
    private String studentNum;

    private String grade;

    private Integer totalScore;

    private Integer totalSolveNum;

    @PrivacyEncrypt(type = PrivacyType.EMAIL)
    private String email;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getTotalSolveNum() {
        return totalSolveNum;
    }

    public void setTotalSolveNum(Integer totalSolveNum) {
        this.totalSolveNum = totalSolveNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
