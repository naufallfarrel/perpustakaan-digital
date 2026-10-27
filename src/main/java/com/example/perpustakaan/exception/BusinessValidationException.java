package com.example.perpustakaan.exception;

import java.util.List;

public class BusinessValidationException extends RuntimeException {
    private final List<String> alasan;

    public BusinessValidationException(String message, List<String> alasan) {
        super(message);
        this.alasan = alasan;
    }

    public List<String> getAlasan() { return alasan; }
}
