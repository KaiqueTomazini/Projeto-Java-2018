package com.userdev.winnerstars.admin;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.graphics.Color;
        import android.graphics.PorterDuff;
        import android.graphics.PorterDuffColorFilter;
        import android.graphics.drawable.Drawable;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.userdev.winnerstars.GruposFragment;
        import com.userdev.winnerstars.PrincipalActivity;
        import com.userdev.winnerstars.R;
        import com.userdev.winnerstars.RegistroActivity;
        import com.userdev.winnerstars.WinnerStars;
        import com.userdev.winnerstars.adapters.SpinnerAdapter;
        import com.userdev.winnerstars.db.DbHelper;
        import com.userdev.winnerstars.models.Curso;
        import com.userdev.winnerstars.models.Grupo;
        import com.userdev.winnerstars.classesdeapoio.Usuario;
        import com.userdev.winnerstars.utils.Constants;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import butterknife.BindView;
        import butterknife.ButterKnife;


public class AddGrupoActivity extends AppCompatActivity {
    Grupo grupo;

    @BindView(R.id.editNome) EditText nome;
    @BindView(R.id.editMesa) EditText mesa;
    @BindView(R.id.editInfo) EditText info;

    @BindView(R.id.spinner) Spinner spnCursos;
    @BindView(R.id.spinner2) Spinner spnAnos;

    public String codCurso;
    public String[] ano = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_grupo);
        String cod_grupo = "";
        if (getIntent().getStringExtra("cod") != null)
            cod_grupo = getIntent().getStringExtra("cod");
        ButterKnife.bind(this);

        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_grupo);
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        nome.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        drawable = getResources().getDrawable(R.drawable.ic_sobre);
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        info.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        //drawable = getResources().getDrawable(R.drawable.ic_sobre);
        //drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        //mesa.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        final List<String> nomes = new ArrayList<>();
        List<Integer> icones = new ArrayList<>();
        final List<String> cods = new ArrayList<>();
        for (Curso curso: DbHelper.getInstance().getCursos()) {
            nomes.add(curso.nome);
            icones.add(curso.icone);
            cods.add(curso.cod);
        }

        spnCursos.setAdapter(new SpinnerAdapter(AddGrupoActivity.this, nomes.toArray(new String[0]), icones.toArray(new Integer[0]), new String[]{}));

        spnCursos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                codCurso = cods.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView adapterView) {

            }
        });

        final String[] anosSrc = new String[]{"1º ano FUND" , "2º ano FUND" , "3º ano FUND" , "4º ano FUND" ,
                "5º ano FUND" , "6º ano FUND" , "7º ano FUND" , "8º ano FUND" , "9º ano FUND" ,
                "1º ano MÉDIO" , "2º ano MÉDIO" , "3º ano MÉDIO" ,
                "1º ano FAC" , "2º ano FAC" , "3º ano FAC" , "4º ano FAC"};
        final List<String> anos = Arrays.asList(anosSrc);
        Integer[] imgs = new Integer[]{ R.drawable.ic_ensino_fund,R.drawable.ic_ensino_fund,R.drawable.ic_ensino_fund,
                R.drawable.ic_ensino_fund,R.drawable.ic_ensino_fund,R.drawable.ic_ensino_fund,
                R.drawable.ic_ensino_fund,R.drawable.ic_ensino_fund,R.drawable.ic_ensino_fund,
                R.drawable.ic_ensino_medio,R.drawable.ic_ensino_medio,R.drawable.ic_ensino_medio,
                R.drawable.ic_faculdade,R.drawable.ic_faculdade,R.drawable.ic_faculdade,R.drawable.ic_faculdade,};

        spnAnos.setAdapter(new SpinnerAdapter(AddGrupoActivity.this, anos.toArray(new String[0]), imgs, new String[]{}));

        spnAnos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                String nomeAno = anos.get(i);
                ano[0] = "" + nomeAno.charAt(0);
                switch (nomeAno.split(" ")[2]) {
                    case "FUND":
                        ano[1] = "1";
                        for (String cod : cods
                                ) {
                            if ("1".equals(cod)) {
                                spnCursos.setSelection(cods.indexOf(cod));
                                System.out.println("vaiporra");
                            }
                            System.out.println("1   " + cod);
                        }
                        break;
                    case "MÉDIO":
                        ano[1] = "2";
                        break;
                    case "FAC":
                        ano[1] = "3";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView adapterView) {

            }
        });
        Button btn = (Button) findViewById(R.id.button4);
        Button btnEliminar = (Button) findViewById(R.id.button5);
        if (!cod_grupo.equals("")) {
            grupo = DbHelper.getInstance().getGrupo(cod_grupo);
            btnEliminar.setVisibility(View.VISIBLE);
            btn.setText("Atualizar");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enviar(WinnerStars.WEBSERVICE + Constants.QUERY_SET_GRUPO);
                }
            });
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enviar(WinnerStars.WEBSERVICE + Constants.QUERY_DEL_GRUPO);
                }
            });
            mesa.setText(grupo.mesa);
            nome.setText(grupo.nomeProjeto);
            info.setText(grupo.infoProjeto);
            for (String nome : nomes
                    ) {
                if (grupo.curso.nome.equals(nome)) {
                    spnCursos.setSelection(nomes.indexOf(nome));
                }
            }
            String strAno = grupo.getAnoCompleto();
            for (String ano : anos
                    ) {
                if (strAno.equals(ano))
                    spnAnos.setSelection(anos.indexOf(ano));
            }
        }
        else {
            btn.setText("Adicionar novo grupo");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enviar(WinnerStars.WEBSERVICE + Constants.QUERY_ADD_GRUPO);
                }
            });
            for (String nome : nomes
                    ) {
                if ("Acadêmico".equals(nome)) {
                    spnCursos.setSelection(nomes.indexOf(nome));
                }
            }
        }
    }

    private void enviar(final String URL) {
        final SQLiteDatabase db = DbHelper.getInstance().getDatabase(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                Toast.makeText(AddGrupoActivity.this, jObj.getString("success_msg"), Toast.LENGTH_SHORT).show();
                                if (URL.contains("set"))
                                    db.execSQL("UPDATE grupos SET" +
                                                " nome_grupo = '" + nome.getText().toString() + "'," +
                                                " mesa_grupo = " + mesa.getText().toString() + "," +
                                                " curso_grupo = '" + codCurso + "'," +
                                                " ano_grupo = " + ano[0] + "," +
                                                " tipoano_grupo = " + ano[1] + "," +
                                                //" icone_grupo = '$icone'," +
                                                " info_grupo = '" + info.getText().toString() + "'" +
                                                " WHERE cod_grupo = " + grupo.cod);
                                if (URL.contains("add"))
                                    db.execSQL("INSERT INTO grupos VALUES (" +
                                            jObj.getInt("cod") + "," +
                                            " '" + nome.getText().toString() + "'," +
                                            " " + mesa.getText().toString() + "," +
                                            " '" + codCurso + "'," +
                                            " " + ano[0] + "," +
                                            " " + ano[1] + "," +
                                            " '',''," +
                                            " '" + info.getText().toString() + "',0)");
                                if (URL.contains("del"))
                                    db.execSQL("DELETE FROM grupos WHERE cod_grupo = " + grupo.cod);
                                //PrincipalActivity.gruposFragment.atualizarFiltro();
                                PrincipalActivity.gruposFragment.atualizar();
                                finish();
                            } else {
                                //pdLoading.dismiss();
                                Toast.makeText(AddGrupoActivity.this, jObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                                Log.e("ERROR:", jObj.getString("error_msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERROR:", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddGrupoActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (!URL.contains("del")) {
                    params.put("nome", nome.getText().toString());
                    params.put("mesa", mesa.getText().toString());
                    params.put("curso", codCurso);
                    params.put("ano", ano[0]);
                    params.put("tipoano", ano[1]);
                    params.put("icone", "");
                    params.put("info", info.getText().toString());
                    if (!URL.contains("add"))
                        params.put("cod", grupo.cod);
                }
                else
                    params.put("cod", grupo.cod);
                return params;
            }

        };
        Usuario.requestQueue.add(stringRequest);
    }


}
