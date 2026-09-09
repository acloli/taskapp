package com.example.taskapp.exception;

/** 業務ルールに違反したときの例外 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}