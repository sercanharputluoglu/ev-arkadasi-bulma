package com.example.evarkadasibulma;

import androidx.annotation.Nullable;

public class UserModel {

    private String name;
    private String surname;
    private String department;
    private String grade;
    private StudentStatus status;
    private Double distanceToCampus;
    private String email;
    private String phoneNumber;

    private String id;

    private String duration;


    public UserModel(String name, String surname, String department, String grade, StudentStatus status, Double distanceToCampus,
                     String email, String phoneNumber, String id, String duration) {
        this.name = name;
        this.surname = surname;
        this.department = department;
        this.grade = grade;
        this.status = status;
        this.distanceToCampus = distanceToCampus;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.duration = duration;
    }

    public UserModel() {
    }



    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDepartment() {
        return department;
    }

    public String getGrade() {
        return grade;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public Double getDistanceToCampus() {
        return distanceToCampus;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public void setDistanceToCampus(Double distanceToCampus) {
        this.distanceToCampus = distanceToCampus;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

 @Override
    public String toString() {
        return "UserModel{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", department='" + department + '\'' +
                ", grade='" + grade + '\'' +
                ", status=" + status +
                ", distanceToCampus=" + distanceToCampus +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

}
