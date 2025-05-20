package com.example.educalivre_mobilev2;

import java.util.List;

public class RespostaAPI {
    private Metadata metadata;
    private List<Questao> questions;

    public Metadata getMetadata() {
        return metadata;
    }

    public List<Questao> getQuestions() {
        return questions;
    }
}

