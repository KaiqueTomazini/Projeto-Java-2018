package com.userdev.winnerstars;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.userdev.winnerstars.classesdeapoio.Imgs;
import com.userdev.winnerstars.classesdeapoio.Usuario;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGaleriaActivity extends AppCompatActivity {

    ImageView img;
    Bitmap tumb, orig;
    SearchableSpinner spinner;
    TextView txtSobre;
    File localFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_galeria);
        spinner = (SearchableSpinner) findViewById(R.id.spinner);
        txtSobre = findViewById(R.id.editInfo);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_sobre);
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        txtSobre.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Uri uriFotoOrig = (Uri) getIntent().getExtras().get("uri");
                    localFoto = new File(uriFotoOrig.getPath());
                    orig = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriFotoOrig));
                    tumb = redimensionarBitmap(350, orig);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) findViewById(R.id.imgPreview)).setImageBitmap(orig);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


        List<String> tst = new ArrayList<>();
        tst.add("Selecione um grupo/Nenhum");
        cods.add("-1");
        Cursor nha = DbHelper.getInstance().getDatabase().rawQuery("SELECT nome_grupo, cod_grupo FROM grupos", null);
        try {
            if (nha.moveToFirst()) {
                while (nha != null) {
                    tst.add(nha.getString(nha.getColumnIndex("nome_grupo")));
                    cods.add(nha.getString(nha.getColumnIndex("cod_grupo")));
                    if (!nha.moveToNext())
                        break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        tst.toArray(new String[0])); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

    }
    List<String> cods = new ArrayList<>();

    Bitmap redimensionarBitmap(Integer max, Bitmap bmp) {
        int outWidth;
        int outHeight;
        int inWidth = bmp.getWidth();
        int inHeight = bmp.getHeight();
        if (inWidth > inHeight) {
            outWidth = max;
            outHeight = (inHeight * max) / inWidth;
        } else {
            outHeight = max;
            outWidth = (inWidth * max) / inHeight;
        }
        return Bitmap.createScaledBitmap(bmp, outWidth, outHeight, false);
    }

    private void enviar() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WinnerStars.WEBSERVICE + Constants.QUERY_ADD_FOTO_GALERIA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                Toast.makeText(AddGaleriaActivity.this, jObj.getString("success_msg"), Toast.LENGTH_SHORT).show();
                                if (Usuario.adm()) {
                                    ContentValues values = new ContentValues();
                                    values.put("cod_foto", jObj.getString("cod"));
                                    values.put("nomearquivo_foto", nomeImg);
                                    values.put("foto_foto", Imgs.DbBitmapUtility.getBytes(tumb));
                                    values.put("cmnt_foto", txtSobre.getText().toString());
                                    values.put("grupo_foto", cods.get(spinner.getSelectedItemPosition()));
                                    String[] nome = Usuario.nome.split(" ");
                                    values.put("sub_foto", "- " + nome[0] + " " + nome[1] + ", " + jObj.getString("data"));
                                    values.put("aval_foto", true);
                                    DbHelper.getInstance().getDatabase(true).insertWithOnConflict("fotos",
                                            "cod_fotos", values, 0);
                                    PrincipalActivity.galeriaFragment.atualizarRaiz();
                                }
                                finish();
                                //bancoDados.execSQL("UPDATE gruposFragment SET votado_grupo = 1 WHERE cod_grupo = " + codGrupo);
                                //PrincipalActivity.gruposFragment.atualizar(imgFiltro.cursoAtual);
                            } else {
                                //pdLoading.dismiss();
                                Toast.makeText(AddGaleriaActivity.this, jObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddGaleriaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("foto", nomeImg);
                params.put("sobre", txtSobre.getText().toString());
                params.put("cod_login", Usuario.cod);
                params.put("cod_grupo", cods.get(spinner.getSelectedItemPosition()));
                String aval = "0";
                if (Usuario.adm())
                    aval = "1";
                params.put("aval", aval);
                return params;
            }

        };
        Usuario.requestQueue.add(stringRequest);
    }

    String getNomeImg() {
        String[] nomeUsuario = Usuario.nome.split(" ");
        return nomeUsuario[0] + "_" + nomeUsuario[1] + "_" + System.currentTimeMillis();
    }

    String nomeImg;

    public void upload(View view) {
        final ProgressDialog dialog = ProgressDialog.show(AddGaleriaActivity.this, "Galeria",
                "Enviado imagem ao servidor...", true);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                    try {
                        nomeImg = getNomeImg();
                        Imgs.doFileUpload(WinnerStars.WEBSERVICE + Constants.QUERY_UPLOAD_FOTO_GALERIA, nomeImg,
                                orig, Bitmap.CompressFormat.JPEG);
                        Imgs.doFileUpload(WinnerStars.WEBSERVICE + Constants.QUERY_UPLOAD_FOTO_GALERIA, nomeImg + "_tb", tumb, Bitmap.CompressFormat.JPEG);
                        enviar();
                        final File outputDir = new File(Environment.getExternalStorageDirectory(), "WinnerStars/Galeria");
                        localFoto.renameTo(new File(outputDir, nomeImg + ".jpeg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dialog.dismiss();
                    }
                }
        }.start();
    }
}
