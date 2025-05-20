package com.example.educalivre_mobilev2;

import java.util.List;

public class Questao {
    private String title;
    private int index;
    private String discipline;
    private String language;
    private int year;
    private String context;
    private List<String> files;
    private String correctAlternative;
    private String alternativesIntroduction;
    private List<Alternativa> alternatives;

    public String getTitle() { return title; }
    public int getIndex() { return index; }
    public String getDiscipline() { return discipline; }
    public String getLanguage() { return language; }
    public int getYear() { return year; }
    public String getContext() { return context; }
    public List<String> getFiles() { return files; }
    public String getCorrectAlternative() { return correctAlternative; }
    public String getAlternativesIntroduction() { return alternativesIntroduction; }
    public List<Alternativa> getAlternatives() { return alternatives; }
}

