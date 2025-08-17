package com.example.learningenglishapplication.Data.model;

import java.io.Serializable;

// Implement Serializable để có thể gửi đối tượng này qua Intent
public class Vocabulary implements Serializable {
    private long id;
    private String word;
    private String meaning;
    private String pronunciation;

    private int isFavorite;
    private int learned;
    private String dateLearned;

    // Optional resources
    private String imageUri;
    private String audioUri;

    // Spaced repetition fields
    private int box; // Leitner box
    private long nextReview; // timestamp in millis


    public Vocabulary(long id, String word, String meaning, String pronunciation) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.pronunciation = pronunciation;
        this.isFavorite = 0;
        this.learned = 0;
        this.dateLearned = null;
        this.imageUri = null;
        this.audioUri = null;
        this.box = 1;
        this.nextReview = 0;
    }

    public Vocabulary(long id, String word, String meaning, String pronunciation, int isFavorite) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.pronunciation = pronunciation;
        this.isFavorite = isFavorite;
        this.learned = 0;
        this.dateLearned = null;
        this.imageUri = null;
        this.audioUri = null;
        this.box = 1;
        this.nextReview = 0;
    }


    public Vocabulary(long id, String word, String meaning, String pronunciation, int isFavorite, int learned, String dateLearned,
                      String imageUri, String audioUri, int box, long nextReview) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.pronunciation = pronunciation;
        this.isFavorite = isFavorite;
        this.learned = learned;
        this.dateLearned = dateLearned;
        this.imageUri = imageUri;
        this.audioUri = audioUri;
        this.box = box;
        this.nextReview = nextReview;
    }


    public long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public boolean isFavorite() {
        return isFavorite == 1;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public int getBox() {
        return box;
    }

    public long getNextReview() {
        return nextReview;
    }

    // Getter mới cho trạng thái đã học
    public int getLearned() {
        return learned;
    }

    // Getter mới cho ngày học
    public String getDateLearned() {
        return dateLearned;
    }


    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public void setNextReview(long nextReview) {
        this.nextReview = nextReview;
    }

    public void setIsFavorite(int favorite) {
        isFavorite = favorite;
    }


    public void setLearned(int learned) {
        this.learned = learned;
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite ? 1 : 0;
    }
    public void setDateLearned(String dateLearned) {
        this.dateLearned = dateLearned;
    }
}