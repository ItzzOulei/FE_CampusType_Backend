package net.ictcampus.campustype.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class TypingResult {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    private int wpm;
    private int raw;
    private int cpm;
    private double accuracy;
    private double time;
    private int words;
    private String sentence;
    private Date timestamp;
    private String userInput;

    // Getter und Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public int getWpm() {
        return wpm;
    }
    public void setWpm(int wpm) {
        this.wpm = wpm;
    }
    public int getRaw() {
        return raw;
    }
    public void setRaw(int raw) {
        this.raw = raw;
    }
    public int getCpm() {
        return cpm;
    }
    public void setCpm(int cpm) {
        this.cpm = cpm;
    }
    public double getAccuracy() {
        return accuracy;
    }
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
    public double getTime() {
        return time;
    }
    public void setTime(double time) {
        this.time = time;
    }
    public int getWords() {
        return words;
    }
    public void setWords(int words) {
        this.words = words;
    }
    public String getSentence() {
        return sentence;
    }
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getUserInput() {
        return userInput;
    }
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}