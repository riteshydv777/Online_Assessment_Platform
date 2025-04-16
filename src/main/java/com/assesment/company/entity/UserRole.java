package com.assesment.company.entity;

public enum UserRole {
    CANDIDATE,
    COMPANY;

    @Override
    public String toString() {
        return name();
    }
} 