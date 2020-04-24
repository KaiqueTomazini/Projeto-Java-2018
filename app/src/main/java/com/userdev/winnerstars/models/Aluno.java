package com.userdev.winnerstars.models;

import org.json.JSONObject;

public class Aluno {
    public String cod;
    public String nome;
    public String qr;
    public Integer votos;
    public JSONObject pontos;

    public Aluno(String cod, String nome, String qr, Integer votos, JSONObject pontos) {
        this.cod = cod;
        this.nome = nome;
        this.qr = qr;
        this.votos = votos;
        this.pontos = pontos;
    }

    public Aluno(String nome) {
        this.nome = nome;
    }

    public String getNomeReduzido() {
        String[] nome = this.nome.split(" ");
        return nome[0]+" "+nome[1];
    }

    public Integer getTotalPontos() {
        if (pontos == null)
            return 0;
        else
            try {
                return pontos.getInt("1") +
                        pontos.getInt("2") +
                        pontos.getInt("3") +
                        pontos.getInt("4") +
                        pontos.getInt("5");
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
    }
}
