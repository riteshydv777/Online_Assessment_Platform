package com.assesment.company.entity;

public enum UserRole {
    CANDIDATE {
        @Override
        public String toString() {
            return "ROLE_CANDIDATE";
        }
    },
    COMPANY {
        @Override
        public String toString() {
            return "ROLE_COMPANY";
        }
    }
} 