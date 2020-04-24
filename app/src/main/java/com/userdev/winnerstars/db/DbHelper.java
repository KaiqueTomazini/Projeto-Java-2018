package com.userdev.winnerstars.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.userdev.winnerstars.RegistroActivity;
import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.classesdeapoio.Imgs;
import com.userdev.winnerstars.classesdeapoio.Usuario;
import com.userdev.winnerstars.models.Curso;
import com.userdev.winnerstars.models.Foto;
import com.userdev.winnerstars.models.Grupo;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.utils.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper extends SQLiteOpenHelper {
    private final Context context;
    private final SharedPreferences prefs;
    private static String TAG_DBHELPER = "DBHELPER";

    private static DbHelper instance = null;
    private SQLiteDatabase db;

    public static final String DATABASE_NAME = Constants.DATABASE_NOME;
    private static final int DATABASE_VERSION = 2;


    public static synchronized DbHelper getInstance() {
        return getInstance(WinnerStars.getAppContext());
    }

    public static synchronized DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    }


    //Criar tabelas
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableGrupos(db);
        createTableCursos(db);
        createTableFotos(db);
    }
    @Override
    public void onUpgrade (SQLiteDatabase db,int oldVersion, int newVersion){
        Log.i(TAG_DBHELPER, "Foi detectada uma atualização no banco de dados.\nSua versão antiga era " + oldVersion + " e está sendo atualuzada para " + newVersion);
        switch (oldVersion) {
            case 1:
                createTableFotos(db); break;
        }
    }

    public void recriarDb() {
        getDatabase(true).execSQL("drop table if exists grupos");
        getDatabase(true).execSQL("drop table if exists cursos");
        getDatabase(true).execSQL("drop table if exists fotos");
        onCreate(getDatabase(true));
    }


    public SQLiteDatabase getDatabase() {
        return getDatabase(false);
    }

    public SQLiteDatabase getDatabase(Boolean habilitarEdicao) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            if (db.isReadOnly() && habilitarEdicao) {
                db = getWritableDatabase();
            }
            return db;
        } catch (IllegalStateException e) {
            return this.db;
        }
    }

    public Boolean existeGrupoNoCurso(Integer curso) {
        Cursor mCount = getDatabase().rawQuery("select cod_grupo from grupos where curso_grupo = " + curso.toString(), null);
        boolean bool = mCount.moveToFirst();
        mCount.close();
        return bool;
    }

    public List<Grupo> getGrupos() {
        return getGrupos("WHERE curso_grupo >= 0");
    }

    public List<Grupo> getGrupos(Integer curso) {
        if (curso == 0)
            return getGrupos("");
        else if (curso <= 5)
            return getGrupos("WHERE curso_grupo = " + curso.toString());
        else
            return getGrupos("WHERE curso_grupo > 5");
    }

    public List<Foto> getFotosGaleria(String cond) {
        List<Foto> listaFotos = new ArrayList<>();
        Cursor nha = getDatabase().rawQuery("SELECT * FROM fotos " + cond + " ORDER BY cod_foto DESC", null);
        if (nha.moveToFirst())
            while (nha != null) {
                String cod, nomeArquivo, cmnt, grupo, sub;
                Boolean aval;
                cod = nha.getString(nha.getColumnIndex("cod_foto"));
                nomeArquivo = nha.getString(nha.getColumnIndex("nomearquivo_foto"));
                cmnt = nha.getString(nha.getColumnIndex("cmnt_foto"));
                grupo = nha.getString(nha.getColumnIndex("grupo_foto"));
                sub = nha.getString(nha.getColumnIndex("sub_foto"));
                aval = nha.getInt(nha.getColumnIndex("aval_foto")) > 0;
                Foto foto = new Foto(cod, grupo, nomeArquivo, cmnt, sub, aval);
                listaFotos.add(foto);
                if (!nha.moveToNext())
                    break;
            }
        return listaFotos;
    }

    public List<Foto> getFotosAvaliadasGaleria() {
        return getFotosGaleria("WHERE aval_foto > 0");
    }

    public List<Curso> getCursos() {
        List<Curso> listaCursos = new ArrayList<>();
        Cursor nha = getDatabase().rawQuery("SELECT * FROM cursos ORDER BY cod_curso ASC", null);
        if (nha.moveToFirst())
            while (nha != null) {
                String cod, nome, sigla, cor;
                cod = nha.getString(nha.getColumnIndex("cod_curso"));
                nome = nha.getString(nha.getColumnIndex("nome_curso"));
                sigla = nha.getString(nha.getColumnIndex("sigla_curso"));
                cor = nha.getString(nha.getColumnIndex("cor_curso"));
                Curso curso = new Curso(cod, nome, sigla, cor);

                listaCursos.add(curso);
                if (!nha.moveToNext())
                    break;
            }
        return listaCursos;
    }

    public Curso getCurso(String id) {
        Cursor nha = getDatabase().rawQuery("SELECT * FROM cursos WHERE cod_curso = " + id +" ORDER BY cod_curso ASC", null);
        if (nha.moveToFirst()) {
            String cod, nome, sigla, cor;
            cod = nha.getString(nha.getColumnIndex("cod_curso"));
            nome = nha.getString(nha.getColumnIndex("nome_curso"));
            sigla = nha.getString(nha.getColumnIndex("sigla_curso"));
            cor = nha.getString(nha.getColumnIndex("cor_curso"));
            return new Curso(cod, nome, sigla, cor);
        }
        return null;
    }

    public List<Grupo> getGrupos(String condicao) {
            JSONArray votados1 = null;
            if (Comandos.isConectado(context) && Comandos.isAvailable(WinnerStars.WEBSERVICE))
                try {
                    JSONObject jObj = new JSONObject(Comandos.postDados(WinnerStars.WEBSERVICE + Constants.QUERY_GET_VOTOS_USUARIO,
                            "login=" + Usuario.cod + "&acao=votos"));
                    boolean error = jObj.getBoolean("error");
                    if (!error)
                        votados1 = jObj.getJSONArray("votados");
                    else
                        Log.e("ERROR:", jObj.getString("error_msg"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            List<Grupo> list = new ArrayList<>();
            String cod = "", nomeGrupo = "", mesaGrupo = "", cursoGrupo = "", ano = "", tipoAno = "", icone = "",
                    info = "";
            Boolean votado = false;
            try {
                Cursor nha = getDatabase().rawQuery("SELECT * FROM ver_grupos " + condicao + " ORDER BY votado_grupo ASC, mesa_grupo ASC, cod_grupo DESC", null);
                if (nha.moveToFirst()) {
                    if (nha.moveToFirst())
                        while (nha != null) {
                            cod = nha.getString(nha.getColumnIndex("cod_grupo"));
                            nomeGrupo = nha.getString(nha.getColumnIndex("nome_grupo"));
                            mesaGrupo = nha.getString(nha.getColumnIndex("mesa_grupo"));
                            cursoGrupo = nha.getString(nha.getColumnIndex("curso_grupo"));
                            ano = nha.getString(nha.getColumnIndex("ano_grupo"));
                            tipoAno = nha.getString(nha.getColumnIndex("tipoano_grupo"));
                            icone = nha.getString(nha.getColumnIndex("icone_grupo"));
                            info = nha.getString(nha.getColumnIndex("info_grupo"));
                            votado = nha.getInt(nha.getColumnIndex("votado_grupo")) > 0;
                            Grupo grupo = new Grupo(cod, nomeGrupo, mesaGrupo, ano, tipoAno, icone, info, votado, getCurso(cursoGrupo));
                            if (votados1 != null)
                                for (int i = 0; i < votados1.length(); i++) {
                                    JSONObject voto = votados1.getJSONObject(i);
                                    if (voto.getString("cod_grupo").equals(cod)) {
                                        JSONArray crits = voto.getJSONArray("crits");
                                        Integer[] numbers = new Integer[crits.length()];
                                        for (int ii = 0; ii < crits.length(); ii++) {
                                            numbers[ii] = crits.optInt(ii);
                                        }
                                        grupo.setVotado(numbers);
                                    }
                                }
                            list.add(grupo);
                            grupo.fon = list.indexOf(grupo);
                            System.out.println(nomeGrupo);
                            if (!nha.moveToNext())
                                break;
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
    }

    public Grupo getGrupo(String cod_grupo) {
        Log.i(TAG_DBHELPER, "Buscando grupo com código " + cod_grupo);
        List<Grupo> grupos = getGrupos("WHERE cod_grupo = " + cod_grupo);
        if (grupos.size() > 0)
            return grupos.get(0);
        else return null;
    }

    public Bitmap getIconeGrupo(String cod_grupo) {
        Cursor nha = getDatabase().rawQuery("SELECT imgicone_grupo FROM grupos WHERE imgicone_grupo IS NOT NULL AND cod_grupo = " + cod_grupo, null);
        if (nha.getCount() > 0) {
            nha.moveToFirst();
            return com.userdev.winnerstars.classesdeapoio.Imgs.DbBitmapUtility.getImage(nha.getBlob(nha.getColumnIndex("imgicone_grupo")));
        }
        return null;
    }


    private void createTableCursos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS cursos (" +
                "  cod_curso int(11) NOT NULL," +
                "  nome_curso varchar(45) NOT NULL," +
                "  sigla_curso varchar(5) NOT NULL," +
                "  cor_curso varchar(7) NOT NULL, UNIQUE(cod_curso))");
    }

    private void createTableGrupos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS grupos (" +
                "  cod_grupo int(11) NOT NULL," +
                "  nome_grupo varchar(40) NOT NULL," +
                "  mesa_grupo int(3) NOT NULL," +
                "  curso_grupo int(1) NOT NULL," +
                "  ano_grupo int(1) NOT NULL," +
                "  tipoano_grupo int(1) NOT NULL," +
                "  icone_grupo text NOT NULL," +
                "  imgicone_grupo blob," +
                "  info_grupo text NOT NULL," +
                "  votado_grupo boolean NOT NULL, UNIQUE(cod_grupo))");
        db.execSQL("CREATE VIEW IF NOT EXISTS ver_grupos AS SELECT cod_grupo, nome_grupo, mesa_grupo, curso_grupo, ano_grupo, tipoano_grupo, icone_grupo, info_grupo, votado_grupo FROM grupos");
    }

    private void createTableFotos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS fotos (" +
                "  cod_foto int(11) NOT NULL," +
                "  nomearquivo_foto varchar(45) NOT NULL," +
                "  foto_foto blob," +
                "  cmnt_foto text NOT NULL," +
                "  grupo_foto int(11)," +
                "  sub_foto text NOT NULL," +
                "  aval_foto int(1) NOT NULL, UNIQUE(cod_foto))");
    }

    public void atualizarFotosDoWebservice() {
        if (Comandos.isConectado(context) && Comandos.checarSite(WinnerStars.WEBSERVICE)) {
            final List<String> cod1 = new ArrayList<>(), cod2 = new ArrayList<>();
            Cursor nha = getDatabase().rawQuery("SELECT cod_foto FROM fotos", null);
            try {
                if (nha.moveToFirst()) {
                    while (nha != null) {
                        cod1.add(nha.getString(nha.getColumnIndex("cod_foto")));
                        if (!nha.moveToNext())
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject jObj = new JSONObject(Comandos.postDados(WinnerStars.WEBSERVICE + Constants.QUERY_GET_FOTOS_GALERIA, ""));
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    JSONArray fotos = jObj.getJSONArray("fotos");
                    //System.out.println(gruposFragment);
                    for (int i = 0; i < fotos.length(); i++) {
                        JSONObject resultado = fotos.getJSONObject(i);
                        String cod = resultado.getString("cod");
                        cod2.add(cod);
                        Boolean aval = resultado.getBoolean("aval");
                        if ((aval || Usuario.adm()) && !cod1.contains(cod)) {
                            try {
                                String nomeArquivo = resultado.getString("caminho"),
                                        cmnt = resultado.getString("sobre"),
                                        grupo = resultado.getString("grupo"),
                                        sub = resultado.getString("sub");
                                getDatabase(true).execSQL("INSERT OR IGNORE INTO fotos VALUES(" +
                                        cod + ",'" + //cod
                                        nomeArquivo + "'," + //nomearquivo
                                        null + ",'" + //imagem/blob
                                        cmnt + "'," + //sobre
                                        grupo + ",'" + //cod grupo
                                        sub + "'," + //sub
                                        (aval ? 1 : 0) + ")"); //aval
                                HttpClient client = new DefaultHttpClient();
                                HttpGet request = new HttpGet(WinnerStars.WEBSERVICE + "galeria/" + nomeArquivo + "_tb.jpeg");
                                HttpResponse httpResponse;
                                httpResponse = (HttpResponse) client.execute(request);
                                HttpEntity entity = httpResponse.getEntity();
                                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                                InputStream inputStream = bufferedEntity.getContent();
                                Bitmap img = BitmapFactory.decodeStream(inputStream);
                                SQLiteStatement p = DbHelper.getInstance().getDatabase(true).compileStatement("UPDATE fotos SET foto_foto = ? WHERE cod_foto = " + cod);
                                p.bindBlob(1, Imgs.DbBitmapUtility.getBytes(img));
                                p.executeUpdateDelete();
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Log.e("ERROR:", jObj.getString("error_msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR:", e.toString());
            }
            for (String a1 : cod1
                    ) {
                if (!cod2.contains(a1)) {
                    getDatabase(true).execSQL("DELETE FROM fotos WHERE cod_foto = " + a1);
                    System.out.println(a1 + " apagado");
                }
            }
        }
    }

    public void atualizarCursosDoWebservice() {
        SQLiteDatabase db = getDatabase(true);
        if (Comandos.isConectado(context) && Comandos.checarSite(WinnerStars.WEBSERVICE)) {
            db.execSQL("drop table if exists cursos");
            createTableCursos(db);
            try {
                JSONObject jObj = new JSONObject(Comandos.postDados(WinnerStars.WEBSERVICE + Constants.QUERY_GET_CURSOS, ""));
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    JSONArray cursos = jObj.getJSONArray("cursos");
                    for (int i = 0; i < cursos.length(); i++) {
                        JSONObject resultado = cursos.getJSONObject(i);
                        try {
                            db.execSQL("INSERT OR IGNORE INTO cursos VALUES(" +
                                    resultado.getString("cod") + ",'" +
                                    resultado.getString("nome") + "','" +
                                    resultado.getString("sigla") + "','" +
                                    resultado.getString("cor") + "')");
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    Log.i(TAG_DBHELPER, "Todos os cursos foram atualizados");
                } else {
                    Toast.makeText(context, jObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    Log.e("ERROR:", jObj.getString("error_msg"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR:", e.toString());
            }
        }
    }

    public void atualizarGruposDoWebservice() {
        List<String> cod1 = new ArrayList<>(), cod2 = new ArrayList<>(), votados = new ArrayList<>();
        if (Comandos.isConectado(context) && Comandos.isAvailable(WinnerStars.WEBSERVICE)) {
            Cursor nha = getDatabase().rawQuery("SELECT cod_grupo FROM grupos", null);
            try {
                if (nha.moveToFirst()) {
                    while (nha != null) {
                        cod1.add(nha.getString(nha.getColumnIndex("cod_grupo")));
                        if (!nha.moveToNext())
                            break;
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
            try {
                JSONObject jObj = new JSONObject(Comandos.postDados(WinnerStars.WEBSERVICE + Constants.QUERY_GET_VOTOS_USUARIO, "login=" + Usuario.cod + "&acao=votados"));
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    JSONArray votados1 = jObj.getJSONArray("votados");
                    System.out.println(votados1);
                    for (int i = 0; i < votados1.length(); i++) {
                        String cod = votados1.getString(i);
                        votados.add(cod.trim());
                    }
                } else {
                    Log.e("ERROR:", jObj.getString("error_msg"));
                }
            } catch (Exception e) { e.printStackTrace(); }
            try {
                JSONObject jObj = new JSONObject(Comandos.postDados(WinnerStars.WEBSERVICE + Constants.QUERY_GET_GRUPOS, ""));
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    JSONArray grupos = jObj.getJSONArray("grupos");
                    //System.out.println(gruposFragment);
                    for (int i = 0; i < grupos.length(); i++) {
                        JSONObject resultado = grupos.getJSONObject(i);
                        //System.out.println(resultado);
                        String cod = resultado.getString("cod");
                        String nomeGrupo = resultado.getString("nome");
                        String mesaGrupo = resultado.getString("mesa");
                        String cursoGrupo = resultado.getString("curso");
                        String ano = resultado.getString("ano");
                        String tipoAno = resultado.getString("tipoano");
                        String icone = resultado.getString("icone");
                        String info = resultado.getString("info");
                        String votado = "0";
                        if (votados.contains(cod.trim()))
                            votado = "1";
                        getDatabase(true).execSQL("INSERT OR IGNORE INTO grupos VALUES(" +
                                cod + ",'" +
                                nomeGrupo + "'," +
                                mesaGrupo + "," +
                                cursoGrupo + "," +
                                ano + "," +
                                tipoAno + ",'" +
                                icone + "',null,'" +
                                info + "'," +
                                votado + ")");
                        cod2.add(cod);
                    }
                } else {
                    Log.e("ERROR:", jObj.getString("error_msg"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR:", e.toString());
            }
            for (String a1 : cod1
                    ) {
                if (!cod2.contains(a1)) {
                    getDatabase(true).execSQL("DELETE FROM grupos WHERE cod_grupo = " + a1);
                    System.out.println(a1 + " apagado");
                }
            }
        } else
            Log.e("GruposFragment:", "Sem conexão com o banco de dados! Coletando dados locais...");
    }
}
