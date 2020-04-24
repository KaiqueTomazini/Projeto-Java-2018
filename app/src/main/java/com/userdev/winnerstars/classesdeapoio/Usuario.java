package com.userdev.winnerstars.classesdeapoio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 28/02/2018.
 */

public class Usuario {
    public Usuario(String cod, String nome, String cat, Context c) {
        this.cod = cod;
        this.nome = nome;
        this.cat = Integer.parseInt(cat.trim());
        Comandos.iniciarFontes(c);

        JSONObject usuario = new JSONObject();
        try {
            usuario.put("cod", cod);
            usuario.put("nome", nome);
            usuario.put("cat", cat);
        } catch (JSONException e) {e.printStackTrace();}

        WinnerStars.getSharedPreferences().edit()
                .putString(Constants.PREF_USUARIO_JSON, usuario.toString()).apply();;
    }

    public static String cod, nome;
    public static Integer cat;

    //public static SQLiteDatabase bancoDados = null;

    /*public static boolean podeExcluir() {
        if (permInt > 0)
            return true;
        else
            return false;
    }

    public static boolean podeAval() {
        if (permInt == 1 || permInt == 3 || permInt == 9)
            return true;
        else
            return false;
    }*/


    public static void iniciarFila(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static RequestQueue requestQueue;


    public static Boolean adm() {
        if (cat >= 9)
            return true;
        else
            return false;
    }

    public static Boolean prof() {
        if (cat >= 2)
            return true;
        else
            return false;
    }
}
