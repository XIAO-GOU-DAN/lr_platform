package com.lr.platform.entity.users;

import java.io.Serializable;
import java.util.Date;

/**
 * (Users)实体类
 *
 * @author makejava
 * @since 2022-07-18 15:00:32
 */
public class Users implements Serializable {
    private static final long serialVersionUID = 528284292291621003L;
    
    private Integer id;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    private Date deletedAt;
    
    private String username;
    
    private String password;
    
    private String name;
    
    private String qq;
    
    private String phone;
    
    private String studentNum;
    
    private String grade;
    
    private Integer totalScore;
    
    private Integer totalSolveNum;
    
    private String email;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

