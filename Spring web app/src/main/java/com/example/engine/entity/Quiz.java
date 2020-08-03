package com.example.engine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity

//@Data
//@RequiredArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Table(name = "quizdb")

public class Quiz {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quiz", orphanRemoval = true)
    @JsonIgnore
    private List<Completed> completed;


    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "User_ID")
    @JsonIgnore
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotNull(message = "Enter the title!")
    private String title;


    @Column(nullable = false)
    @NotNull(message = "Enter the text!")
    private String text;

    @NotNull
    @Size(min = 2, message = "Enter as min 2 arguments")
    private String[] options;

//    @JsonIgnore
//    @Getter(onMethod_ = @JsonProperty(value = "answer"))
    private int[] answer;


/*
    Для ломбок
    @JsonIgnore
    @JsonProperty(value = "answer")
    public int[] getAnswer() {
        return answer;
    }*/


    protected Quiz(){}
    public Quiz(String title, String text, String[] options, int[] answer) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAnswer(int[] answer) {
        this.answer = answer;
    }
    @JsonIgnore
    @JsonProperty(value = "answer")
    public int[] getAnswer() {
        return answer;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonIgnore
    public List<Completed> getCompleted() {
        return completed;
    }

    public void setCompleted(List<Completed> completed) {
        this.completed = completed;
    }

}