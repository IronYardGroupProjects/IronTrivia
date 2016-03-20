package com.IronTrivia.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by alexanderhughes on 3/17/16.
 */
@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue
    private int id;
    @NotNull
    int score = 0;
    @ManyToOne
    private User user;
    @JsonIgnore
    @ManyToOne
    private Game game;
    @Transient
    boolean isCorrect;

    public Score() {
    }

    public Score(User user, Game game) {
        this.user = user;
        this.game = game;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addToScore(int points) {
        score += points;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}