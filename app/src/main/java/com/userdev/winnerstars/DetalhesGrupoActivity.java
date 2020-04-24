package com.userdev.winnerstars;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.userdev.winnerstars.adapters.AlunosAdapter;
import com.userdev.winnerstars.admin.AddAlunoActivity;
import com.userdev.winnerstars.classesdeapoio.PicturePopup;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.models.Aluno;
import com.userdev.winnerstars.models.Cracha;
import com.userdev.winnerstars.models.Grafico;
import com.userdev.winnerstars.models.Grupo;
import com.userdev.winnerstars.classesdeapoio.Usuario;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Bitmap.CompressFormat.PNG;

public class DetalhesGrupoActivity extends ListActivity implements OnChartValueSelectedListener {

    public static Grupo grupo;
    public static TextView tbGrupo, tbPontos;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alunos);
        getListView().setFocusable(false);
        grupo = DbHelper.getInstance().getGrupo(getIntent().getStringExtra("cod"));
        requestJSON();
        ((TextView) findViewById(R.id.textView4)).setText(grupo.infoProjeto);
        tbGrupo = (TextView) findViewById(R.id.tbGrupo);
        tbPontos = (TextView) findViewById(R.id.tbPontos);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        tbGrupo.setText(grupo.nomeProjeto);

        toolbar.setTitle(grupo.nomeProjeto);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (!Usuario.adm())
            ((Button) findViewById(R.id.button4)).setVisibility(View.GONE);

        new Imgs((ImageView) findViewById(R.id.imageView6), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                WinnerStars.WEBSERVICE + grupo.icone, grupo.curso.cor.toString(), grupo.cod);

        ((ImageView) findViewById(R.id.imageView6)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!grupo.icone.equals(""))
                    new PicturePopup(getApplicationContext(), R.layout.popup_photo_full, findViewById(android.R.id.content), grupo.icone.trim(), DbHelper.getInstance().getIconeGrupo(grupo.cod));
            }
        });


        if (Usuario.adm()) {
            getListView().setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                               int pos, long id) {
                    ViewGroup viewgroup = (ViewGroup) view;
                    Intent intent = new Intent(DetalhesGrupoActivity.this, AddAlunoActivity.class);
                    intent.putExtra("pos", pos);
                    startActivity(intent);
                    //finish();
                    return true;
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (Usuario.adm()) {
            Aluno aluno = adapterAlunos.getItem(position);
            String url = WinnerStars.WEBSERVICE + "qr/" + aluno.nome + "_" + aluno.qr + ".png";
            try {
                URLConnection connection = new URL(url).openConnection();
                String contentType = connection.getHeaderField("Content-Type");
                boolean image = contentType.startsWith("image/");
                if (image)
                    new PicturePopup(getApplicationContext(), R.layout.popup_photo_full, findViewById(android.R.id.content), url, null);
                else
                    new PicturePopup(getApplicationContext(), R.layout.popup_photo_full, findViewById(android.R.id.content), url,
                            new Cracha().criar(DetalhesGrupoActivity.this, aluno.nome, aluno.qr, grupo));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void add(View view) {
        Intent intent = new Intent(DetalhesGrupoActivity.this, AddAlunoActivity.class);
        intent.putExtra("cod_aluno", "");
        startActivity(intent);
        finish();
    }

    public static AlunosAdapter adapterAlunos;

    public void requestJSON(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WinnerStars.WEBSERVICE + Constants.QUERY_GET_ALUNOS_GRUPO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);

                            boolean error = jObj.getBoolean("error");
                            if (!error){
                                //getListView().setAdapter(n);
                                // Getting Array of Products
                                JSONArray ministries = jObj.getJSONArray("alunos");
                                Integer pontosTotais = jObj.getInt("pontos");
                                tbPontos.setText(pontosTotais + " pts");
                                // Hashmap for ListView
                                ArrayList<HashMap<String, String>> ministriesList = new ArrayList<>();
                                // looping through All Products
                                List<Aluno> list = new ArrayList<>();
                                Boolean pontuou = false;
                                for (int i = 0; i < ministries.length(); i++) {
                                    JSONObject c = ministries.getJSONObject(i);
                                    if (Usuario.prof()) {
                                        JSONObject jsonObject = null;
                                        System.out.println(c.get("pontos"));
                                        if (!(c.get("pontos").toString().equals("[]"))) {
                                            jsonObject = c.getJSONObject("pontos");
                                            pontuou = true;
                                        }
                                        Aluno aluno = new Aluno(c.getString("cod"), c.getString("nome"),
                                                c.getString("qr"), c.getInt("votos"), jsonObject);
                                        list.add(aluno);
                                    } else {
                                        Aluno aluno = new Aluno(c.getString("nome"));
                                        list.add(aluno);
                                    }
                                }
                                adapterAlunos = new AlunosAdapter(DetalhesGrupoActivity.this, R.layout.linha_alunos, list, pontosTotais.doubleValue(), jObj.getDouble("votos"));
                                setListAdapter(adapterAlunos);
                                setListViewHeightBasedOnChildren(getListView());

                                if (Usuario.prof() && pontuou) {
                                    ArrayList<PieEntry> grafPontos = new ArrayList<>();
                                    ArrayList<PieEntry> grafVotos = new ArrayList<>();
                                    ArrayList<BarDataSet> grafCrit1 = new ArrayList<>();
                                    ArrayList<BarDataSet> grafCrit2 = new ArrayList<>();
                                    ArrayList<BarDataSet> grafCrit3 = new ArrayList<>();
                                    ArrayList<BarDataSet> grafCrit4 = new ArrayList<>();
                                    ArrayList<BarDataSet> grafCrit5 = new ArrayList<>();
                                    int i = 0;
                                    for (Aluno aluno : list) {
                                        Integer pontos = aluno.getTotalPontos();
                                        if (pontos != 0) {
                                            grafPontos.add((PieEntry) new PieEntry((float) pontos, aluno.getNomeReduzido()).setTag(Integer.toString(list.indexOf(aluno))));
                                            grafCrit1.add(barDataSet(aluno.pontos.getInt("1"), aluno.votos, aluno.getNomeReduzido(), i));
                                            grafCrit2.add(barDataSet(aluno.pontos.getInt("1"), aluno.votos, aluno.getNomeReduzido(), i));
                                            grafCrit3.add(barDataSet(aluno.pontos.getInt("3"), aluno.votos, aluno.getNomeReduzido(), i));
                                            grafCrit4.add(barDataSet(aluno.pontos.getInt("4"), aluno.votos, aluno.getNomeReduzido(), i));
                                            grafCrit5.add(barDataSet(aluno.pontos.getInt("5"), aluno.votos, aluno.getNomeReduzido(), i));
                                            i++;
                                        }
                                        if (aluno.votos != 0)
                                            grafVotos.add((PieEntry) new PieEntry((float) aluno.votos, aluno.getNomeReduzido()).setTag(Integer.toString(list.indexOf(aluno))));
                                    }
                                    JSONObject votosGrupo = jObj.getJSONObject("grupo");
                                    Integer intVotosGrupo = votosGrupo.getInt("total");
                                    JSONObject votosGrupoAval = jObj.getJSONObject("grupoAval");
                                    Integer intVotosGrupoAval = votosGrupoAval.getInt("total");
                                    JSONObject votosGerais = jObj.getJSONObject("geral");
                                    Integer intVotosGeral = votosGerais.getInt("total");
                                    JSONObject votosCurso = jObj.getJSONObject("curso");
                                    Integer intVotosCurso = votosCurso.getInt("total");
                                    JSONObject votosAno = jObj.getJSONObject("ano");
                                    Integer intVotosAno = votosAno.getInt("total");

                                    grafCrit1.add(barDataSet(votosGrupo.getInt("1"), intVotosGrupo, "Média do Grupo", i));
                                    grafCrit2.add(barDataSet(votosGrupo.getInt("2"), intVotosGrupo, "Média do Grupo", i));
                                    grafCrit3.add(barDataSet(votosGrupo.getInt("3"), intVotosGrupo, "Média do Grupo", i));
                                    grafCrit4.add(barDataSet(votosGrupo.getInt("4"), intVotosGrupo, "Média do Grupo", i));
                                    grafCrit5.add(barDataSet(votosGrupo.getInt("5"), intVotosGrupo, "Média do Grupo", i));

                                    grafCrit1.add(barDataSet(votosGerais.getInt("1"), intVotosGeral, "Média Geral", i + 1));
                                    grafCrit2.add(barDataSet(votosGerais.getInt("2"), intVotosGeral, "Média Geral", i + 1));
                                    grafCrit3.add(barDataSet(votosGerais.getInt("3"), intVotosGeral, "Média Geral", i + 1));
                                    grafCrit4.add(barDataSet(votosGerais.getInt("4"), intVotosGeral, "Média Geral", i + 1));
                                    grafCrit5.add(barDataSet(votosGerais.getInt("5"), intVotosGeral, "Média Geral", i + 1));

                                    LinearLayout linearLayout = findViewById(R.id.linearDetalhesGrupo);
                                    TextView txtTitulo = new TextView(DetalhesGrupoActivity.this);
                                    txtTitulo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    txtTitulo.setText("GRÁFICOS");
                                    txtTitulo.setTextSize(60F);
                                    txtTitulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    txtTitulo.setTypeface(Comandos.burbank);

                                    linearLayout.addView(txtTitulo);
                                    linearLayout.addView(Grafico.divisor(DetalhesGrupoActivity.this));
                                    linearLayout.addView(new Grafico(DetalhesGrupoActivity.this, "Pontos por aluno", new PieDataSet(grafPontos, ""), "pontos", DetalhesGrupoActivity.this));
                                    linearLayout.addView(Grafico.divisor(DetalhesGrupoActivity.this));
                                    linearLayout.addView(new Grafico(DetalhesGrupoActivity.this, "Votos por aluno", new PieDataSet(grafVotos, ""), "votos", DetalhesGrupoActivity.this));
                                    linearLayout.addView(Grafico.divisor(DetalhesGrupoActivity.this));
                                    linearLayout.addView(new Grafico(DetalhesGrupoActivity.this, "Grupo vs geral, curso e ano", new BarData(new BarDataSet[]{
                                            barDataSet((float) (votosGrupo.getInt("1") + votosGrupo.getInt("2") + votosGrupo.getInt("3") + votosGrupo.getInt("4") +
                                                    votosGrupo.getInt("5"))/5, intVotosGrupo, "Média do grupo (total)", 1),
                                            barDataSet((float) (votosGrupoAval.getInt("1") + votosGrupoAval.getInt("2") + votosGrupoAval.getInt("3") + votosGrupoAval.getInt("4") +
                                                    votosGrupoAval.getInt("5"))/5, intVotosGrupoAval, "Média do grupo (avaliadores - *nota pedagógica*)", 2),
                                            barDataSet((float) (votosGerais.getInt("1") + votosGerais.getInt("2") + votosGerais.getInt("3") + votosGerais.getInt("4") +
                                                    votosGerais.getInt("5"))/5, intVotosGeral, "Média geral", 3),
                                            barDataSet((float) (votosCurso.getInt("1") + votosCurso.getInt("2") + votosCurso.getInt("3") + votosCurso.getInt("4") +
                                                    votosCurso.getInt("5"))/5, intVotosCurso, "Média do curso ("+grupo.curso.nome+")", 4),
                                            barDataSet((float) (votosAno.getInt("1") + votosAno.getInt("2") + votosAno.getInt("3") + votosAno.getInt("4") +
                                                    votosAno.getInt("5"))/5, intVotosAno, "Média do ano ("+grupo.getAnoCompleto()+")", 5),
                                    }), "pts"));
                                    linearLayout.addView(Grafico.divisor(DetalhesGrupoActivity.this));
                                    linearLayout.addView(new Grafico(DetalhesGrupoActivity.this, new BarData[] {
                                            new BarData(grafCrit1.toArray(new BarDataSet[0])),
                                            new BarData(grafCrit2.toArray(new BarDataSet[0])),
                                            new BarData(grafCrit3.toArray(new BarDataSet[0])),
                                            new BarData(grafCrit4.toArray(new BarDataSet[0])),
                                            new BarData(grafCrit5.toArray(new BarDataSet[0]))
                                    }, "pts"));

                                    linearLayout.addView(Grafico.divisor(DetalhesGrupoActivity.this));
                                }
                                //pdLoading.dismiss();
                                //Toast.makeText(DetalhesGrupoActivity.this,response,Toast.LENGTH_LONG).show();
                            }else{
                                //pdLoading.dismiss();
                                tbPontos.setText("0 pts");
                                Log.e("ERROR:", jObj.getString("error_msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERROR:", e.toString());
                            Log.e("ERROR:", response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetalhesGrupoActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("cod",grupo.cod);
                params.put("cond","true");
                params.put("perm",Usuario.cat.toString());
                if (Usuario.prof()) {
                    params.put("ano", grupo.ano.toString());
                    params.put("tipoAno", grupo.tipoAno);
                    params.put("curso", grupo.curso.cod);
                }
                return params;
            }

        };
        Usuario.requestQueue.add(stringRequest);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        android.widget.ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (Usuario.adm()) {
            Log.i("ChartListener", "Tag: "+e.getTag());
            Intent intent = new Intent(DetalhesGrupoActivity.this, AddAlunoActivity.class);
            intent.putExtra("pos", Integer.valueOf(e.getTag()));
            startActivity(intent);
        }
    }

    @Override
    public void onNothingSelected() {

    }

    public static class Imgs extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        Activity context;
        public byte[] teste;

        public Imgs(ImageView bmImage, Activity context) {
            this.bmImage = bmImage;
            this.context = context;
        }

        String cor = "";

        protected Bitmap doInBackground(String... params) {
            String urlStr = params[0];
            cor = params[1];
            Bitmap img = null;
            Cursor nha = DbHelper.getInstance().getDatabase().rawQuery("SELECT imgicone_grupo FROM grupos WHERE imgicone_grupo IS NOT NULL AND cod_grupo = " + params[2], null);
            if (!(nha.getCount() > 0)) {
                if (Comandos.isConectado(this.context)) {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(urlStr);
                    HttpResponse response;
                    try {
                        response = (HttpResponse) client.execute(request);
                        HttpEntity entity = response.getEntity();
                        BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                        InputStream inputStream = bufferedEntity.getContent();
                        img = BitmapFactory.decodeStream(inputStream);
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (img != null) {
                        SQLiteStatement p = DbHelper.getInstance().getDatabase(true).compileStatement("UPDATE grupos SET imgicone_grupo = ? WHERE cod_grupo = " + params[2]);
                        teste = DbBitmapUtility.getBytes(img);
                        p.bindBlob(1, teste);
                        p.executeUpdateDelete();
                    }
                }
            } else {
                nha.moveToFirst();
                teste = nha.getBlob(nha.getColumnIndex("imgicone_grupo"));
                img = DbBitmapUtility.getImage(teste);
                System.out.println("img no banco!");
            }
            if (img != null)
                return img;
            else {
                return img;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
                bmImage.setMinimumHeight(bmImage.getWidth());
            }
            else {
                bmImage.setImageDrawable(GruposFragment.ic_quest);
                bmImage.setColorFilter(Integer.parseInt(cor));
            }
        }


        public static class DbBitmapUtility {
            // convert from bitmap to byte array
            public static byte[] getBytes(Bitmap bitmap) {
                if (bitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(PNG, 0, stream);
                    return stream.toByteArray();
                } else
                    return null;
            }

            // convert from byte array to bitmap
            public static Bitmap getImage(byte[] image) {
                return BitmapFactory.decodeByteArray(image, 0, image.length);
            }
        }
}


    BarDataSet barDataSet(float dividendo, Integer divisor, String label, Integer i) {
        ArrayList<BarEntry> tst = new ArrayList<>();
        if (dividendo/divisor > 0)
            tst.add(new BarEntry(1 + i, dividendo/divisor));
        else
            tst.add(new BarEntry(1 + i, 0));
        BarDataSet set = new BarDataSet(tst, label);
        set.setColor(ColorUtils.HSLToColor(new float[]{(i*20)%360,.83F,.50F}));
        return set;
    }
}
