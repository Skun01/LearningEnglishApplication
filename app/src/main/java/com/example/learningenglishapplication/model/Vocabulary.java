package com.example.learningenglishapplication.model;

import java.io.Serializable;

// Implement Serializable để có thể gửi đối tượng này qua Intent
public class Vocabulary implements Serializable {
    private long id;
    private String word;
    private String meaning;
    // Bạn có thể thêm các trường khác như pronunciation, example ở đây

    public Vocabulary(long id, String word, String meaning) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }
}