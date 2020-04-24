package com.userdev.winnerstars.models;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.classesdeapoio.Imgs;
import com.userdev.winnerstars.db.DbHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.InputStream;

public class Foto {


    public String cod;
    public String codGrupo;
    public String nomeArquivo;
    public String sobre;
    public String sub;
    public Boolean aval;

    public Foto(String cod, String codGrupo, String nomeArquivo, String sobre, String sub, Boolean aval) {
        this.cod = cod;
        if (codGrupo == null || codGrupo.equals("-1"))
            this.codGrupo = "";
        else
            this.codGrupo = codGrupo;
        this.nomeArquivo = nomeArquivo;
        this.sobre = sobre;
        this.sub = sub;
        this.aval = aval;

    }

    public String getNomeGrupo() {
        if (!codGrupo.equals("")) {
            System.out.println(codGrupo);
            return DbHelper.getInstance().getGrupo(codGrupo).nomeProjeto;
        }
        else return "";
    }

    public Bitmap getFoto(Context context) {
        Cursor nha = DbHelper.getInstance().getDatabase().rawQuery("SELECT foto_foto FROM fotos WHERE foto_foto IS NOT NULL AND cod_foto = " + cod, null);
        if (nha.moveToFirst()) {
            Log.i("Foto", "Há img no bd para a foto " + cod);
            return Imgs.DbBitmapUtility.getImage(nha.getBlob(nha.getColumnIndex("foto_foto")));
        }
        return null;
    }
}