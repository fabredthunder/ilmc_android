package fr.ilovemycity.android.models;

import java.io.Serializable;

/**
 * Created by Fab on 12/11/15.
 * All rights reserved
 */

@SuppressWarnings("UnusedDeclaration")
public class Survey implements Serializable {

    /**************
     * ATTRIBUTES *
     **************/

    // Ticket informations
    private String   id;
    private String   name;
    private String   question;

    // City informations
    private String cityId;

    // Count
    private int answers;
    private int yes;
    private int no;

    private Resp answer;

    class Resp {

        private String id;
        private String vote;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVote() {
            return vote;
        }

        public void setVote(String vote) {
            this.vote = vote;
        }
    }

    /***********
     * METHODS *
     ***********/

    /**
     * Getters and setters
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public int getYes() {
        return yes;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Resp getAnswer() {
        return answer;
    }

    public void setAnswer(Resp answer) {
        this.answer = answer;
    }
}