package com.userdev.winnerstars.models;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.userdev.winnerstars.R;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by User on 26/03/2018.
 */

public class Curso {

    public String nome, sigla, cod;

    public Integer icone;

    public Integer cor;

    public Curso(String cod, String nome, String sigla, String cor) {
        this.cod = cod;
        this.nome = nome;
        this.sigla = sigla;
        this.cor = Color.parseColor(cor);
        this.icone = getIconeId(cod);
    }

    public Integer getIconeId(String codCurso) {
        switch (codCurso) {
            case "1":
                return R.drawable.ic_academico;
            case "2":
                return R.drawable.ic_administracao;
            case "3":
                return R.drawable.ic_eletronica;
            case "4":
                return R.drawable.ic_informatica;
            case "5":
                return R.drawable.ic_quimica;
            default:
                return R.drawable.ic_plus;
        }
    }
}
