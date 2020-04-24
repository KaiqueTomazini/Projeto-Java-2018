package com.userdev.winnerstars.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.userdev.winnerstars.DetalhesGrupoActivity;
import com.userdev.winnerstars.R;
import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.models.Aluno;
import com.userdev.winnerstars.models.Cracha;
import com.userdev.winnerstars.classesdeapoio.Imgs;
import com.userdev.winnerstars.classesdeapoio.Usuario;
import com.userdev.winnerstars.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.userdev.winnerstars.DetalhesGrupoActivity.grupo;

public class AddAlunoActivity extends AppCompatActivity {


    EditText nome, qr;
    String codAluno;
    Aluno aluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_aluno);
        Integer pos = getIntent().getIntExtra("pos", -1);
        nome = (EditText) findViewById(R.id.editTextt);
        qr = (EditText) findViewById(R.id.editText);
        Button btn = (Button) findViewById(R.id.button4);
        Button btnEliminar = (Button) findViewById(R.id.button5);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_man);
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        nome.setCompoundDrawablesWithIntrinsicBounds( drawable, null, null, null);
        drawable = getResources().getDrawable(R.drawable.ic_qr_code);
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        qr.setCompoundDrawablesWithIntrinsicBounds( drawable, null, null, null);

        if (pos > -1) {
            aluno = DetalhesGrupoActivity.adapterAlunos.getItem(pos);
            codAluno = aluno.cod;
            btnEliminar.setVisibility(View.VISIBLE);
            btn.setText("Atualizar");
            nome.setText(aluno.nome);
            qr.setText(aluno.qr);
            nome.setSelection(nome.getText().length());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enviar(WinnerStars.WEBSERVICE + Constants.QUERY_SET_ALUNO);
                }
            });
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enviar(WinnerStars.WEBSERVICE + Constants.QUERY_DEL_ALUNO);
                }
            });
            qr.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        enviar(WinnerStars.WEBSERVICE + Constants.QUERY_SET_ALUNO);
                        return true;
                    }
                    return false;
                }
            });
        }
        else {
            btn.setText("Adicionar a\n" + grupo.nomeProjeto);
            gerarSenha(null);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enviar(WinnerStars.WEBSERVICE + Constants.QUERY_ADD_ALUNO);
                }
            });
            qr.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        enviar(WinnerStars.WEBSERVICE + Constants.QUERY_ADD_ALUNO);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void gerarSenha(View view) {
        String alfabeto = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String senha = ""; //lembre-se de declarar pass como uma matriz
        for (int i = 0; i < 32; i++) {
            int index = 0 + (int)(Math.random() * ((alfabeto.length() - 1) + 1));
            senha += alfabeto.charAt(index);
        }
        qr.setText(senha);
    }

    private void enviar(final String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                if (!URL.contains("delete"))
                                Imgs.doFileUpload(WinnerStars.WEBSERVICE + Constants.QUERY_UPLOAD_CRACHA, nome.getText().toString() + "_" + qr.getText().toString(),
                                        new Cracha().criar(AddAlunoActivity.this, nome.getText().toString(), qr.getText().toString(), grupo), Bitmap.CompressFormat.PNG);
                                Toast.makeText(AddAlunoActivity.this, jObj.getString("success_msg"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddAlunoActivity.this, DetalhesGrupoActivity.class);
                                intent.putExtra("pos", grupo.fon);
                                startActivity(intent);
                                finish();
                            } else {
                                //pdLoading.dismiss();
                                Toast.makeText(AddAlunoActivity.this, jObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddAlunoActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (!URL.contains("delete")) {
                    params.put("nome", nome.getText().toString());
                    params.put("crypt", qr.getText().toString());
                    if (URL.contains("add"))
                        params.put("grupo", grupo.cod);
                    else
                        params.put("cod", codAluno);
                }
                else
                    params.put("cod", codAluno);
                return params;
            }

        };
        Usuario.requestQueue.add(stringRequest);
    }
}
