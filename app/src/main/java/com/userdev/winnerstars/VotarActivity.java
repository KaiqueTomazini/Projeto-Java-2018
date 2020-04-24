package com.userdev.winnerstars;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.userdev.winnerstars.classesdeapoio.Usuario;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.utils.Constants;
import com.userdev.winnerstars.views.imgFiltro;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class VotarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votar);
        try {
            final JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("json"));

            final MaterialRatingBar crit1 = (MaterialRatingBar) findViewById(R.id.crit1);
            final MaterialRatingBar crit2 = (MaterialRatingBar) findViewById(R.id.crit2);
            final MaterialRatingBar crit3 = (MaterialRatingBar) findViewById(R.id.crit3);
            final MaterialRatingBar crit4 = (MaterialRatingBar) findViewById(R.id.crit4);
            final MaterialRatingBar crit5 = (MaterialRatingBar) findViewById(R.id.crit5);
            final List<MaterialRatingBar> crits = new ArrayList<>();
            crits.add(crit1);
            crits.add(crit2);
            crits.add(crit3);
            crits.add(crit4);
            crits.add(crit5);
            //crit1.setProgressTintList();
            //ratingBar.setRating(userRankValue);

            TextView text = (TextView) findViewById(R.id.rank_dialog_text1);
            TextView text2 = (TextView) findViewById(R.id.rank_dialog_text2);

            text.setTypeface(Comandos.burbank);
            text.setText(jsonObject.getString("nome_grupo").toUpperCase());
            text2.setText(jsonObject.getString("nome"));

            for (MaterialRatingBar crit : crits
                    ) {
                crit.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(final RatingBar ratingBar, final float rating, final boolean fromUser) {
                        if (fromUser) {
                            ratingBar.setRating((float) Math.ceil(rating));
                        }
                    }
                });
            }

            Button updateButton = (Button) findViewById(R.id.rank_dialog_button);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        enviar(crits, jsonObject.getString("cod_aluno"), jsonObject.getString("cod_grupo"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (Exception e) { e.printStackTrace(); }
        
    }

    private void enviar(final List<MaterialRatingBar> crits, final String codAluno, final String codGrupo) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WinnerStars.WEBSERVICE + Constants.QUERY_VOTAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                Toast.makeText(VotarActivity.this, jObj.getString("success_msg"), Toast.LENGTH_SHORT).show();
                                DbHelper.getInstance().getDatabase(true).execSQL("UPDATE grupos SET votado_grupo = 1 WHERE cod_grupo = " + codGrupo);
                                PrincipalActivity.gruposFragment.atualizar();
                                finish();
                            } else {
                                //pdLoading.dismiss();
                                Toast.makeText(VotarActivity.this, jObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(VotarActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cod_login", Usuario.cod);
                params.put("cod_aluno", codAluno.trim());
                for (int i = 0; i < crits.size(); i++) {
                    params.put("crit" + Integer.toString(i + 1), Integer.toString((int)crits.get(i).getRating()));
                }
                return params;
            }

        };
        Usuario.requestQueue.add(stringRequest);
    }
}
