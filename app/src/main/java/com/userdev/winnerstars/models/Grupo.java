package com.userdev.winnerstars.models;

import android.graphics.Bitmap;

import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.utils.Comandos;

/**
 * Created by User on 28/02/2018.
 */

public class Grupo {
    public String cod, nomeProjeto, mesa, icone, infoProjeto, tipoAno;
    public Boolean votado = false;
    public Integer fon = 0;
    public Integer ano;
    public Bitmap img = null;
    public Curso curso;
    public Integer[] votos;

    public Grupo(String cod, String nomeProjeto, String mesa, String ano, String tipoAno,
                 String icone, String infoProjeto, Boolean votado, Curso curso) {
        this.cod = cod;
        this.nomeProjeto = nomeProjeto;
        this.mesa = mesa;
        
        this.ano = Integer.parseInt(ano);
        this.tipoAno = tipoAno;
        if (!icone.equals(""))
            this.icone = WinnerStars.WEBSERVICE + "icones/" + icone;
        else
            this.icone = "";
        this.infoProjeto = infoProjeto;
        this.votado = votado;
        this.curso = curso;
    }

    public void setVotado(Integer[] tst) {
        this.votado = true;
        votos = tst;
    }

    public String getSub() {
        if (votos != null) {
            Integer total = 0;
            for (Integer a : votos)
                total = total + a;
            return ano.toString() + "º " + this.curso.sigla + " (" + total.toString() + "/" + Integer.toString(votos.length * 5) + " pts)";
        } else
        return this.curso.nome + " (" + ano.toString() + "º ano)";
    }

    public String getAnoCompleto() {
        String ret = ano.toString() + "º ano ";
        switch (tipoAno) {
            case "1":
                ret += "FUND"; break;
            case "2":
                ret += "MÉDIO"; break;
            case "3":
                ret += "FUND"; break;
            default:
                return "";
        }
        return ret;
    }


}
