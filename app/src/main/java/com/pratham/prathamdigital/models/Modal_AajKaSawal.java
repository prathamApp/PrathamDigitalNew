package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Modal_AajKaSawal {
    @SerializedName("aksVersion")
    public String aksVersion;
    @SerializedName("nodeId")
    public String nodeId;
    @SerializedName("nodeType")
    public String nodeType;
    @SerializedName("nodeTitle")
    public String nodeTitle;
    @SerializedName("QueId")
    public String QueId;
    @SerializedName("Question")
    public String Question;
    @SerializedName("QuestionType")
    public String QuestionType;
    @SerializedName("Option1")
    public String Option1;
    @SerializedName("Option2")
    public String Option2;
    @SerializedName("Option3")
    public String Option3;
    @SerializedName("Option4")
    public String Option4;
    @SerializedName("Answer")
    public String Answer;
    @SerializedName("resourceName")
    public String resourceName;
    @SerializedName("resourceType")
    public String resourceType;
    @SerializedName("resourceId")
    public String resourceId;
    @SerializedName("resourcePath")
    public String resourcePath;
    @SerializedName("programLanguage")
    public String programLanguage;
    @SerializedName("nodelist")
    public List<Modal_AajKaSawal> nodelist;

    public String getAksVersion() {
        return aksVersion;
    }

    public void setAksVersion(String aksVersion) {
        this.aksVersion = aksVersion;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }

    public String getQueId() {
        return QueId;
    }

    public void setQueId(String queId) {
        QueId = queId;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getQuestionType() {
        return QuestionType;
    }

    public void setQuestionType(String questionType) {
        QuestionType = questionType;
    }

    public String getOption1() {
        return Option1;
    }

    public void setOption1(String option1) {
        Option1 = option1;
    }

    public String getOption2() {
        return Option2;
    }

    public void setOption2(String option2) {
        Option2 = option2;
    }

    public String getOption3() {
        return Option3;
    }

    public void setOption3(String option3) {
        Option3 = option3;
    }

    public String getOption4() {
        return Option4;
    }

    public void setOption4(String option4) {
        Option4 = option4;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getProgramLanguage() {
        return programLanguage;
    }

    public void setProgramLanguage(String programLanguage) {
        this.programLanguage = programLanguage;
    }

    public List<Modal_AajKaSawal> getNodelist() {
        return nodelist;
    }

    public void setNodelist(List<Modal_AajKaSawal> nodelist) {
        this.nodelist = nodelist;
    }
}
