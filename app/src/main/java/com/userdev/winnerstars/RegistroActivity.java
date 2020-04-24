package com.userdev.winnerstars;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.userdev.winnerstars.classesdeapoio.QRActivity;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.intro.IntroActivity;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.models.Curso;
import com.userdev.winnerstars.classesdeapoio.Usuario;
import com.userdev.winnerstars.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

//import static com.userdev.winnerstars.classesdeapoio.Usuario.bancoDados;

public class RegistroActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    public Intent i;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @BindView(R.id.txtNome) EditText txtNome;

    @BindView(R.id.btnReg) Button btnRegistrar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_ApiSpec);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.layout_registro);
        i = new Intent(RegistroActivity.this, PrincipalActivity.class);

        //bancoDados = openOrCreateDatabase("winnerstars", MODE_PRIVATE, null);
        ButterKnife.bind(this);

        WinnerStars.mContext = getApplicationContext();

        if (IntroActivity.mustRun()) {
            Log.i("INTRO", "Mostrando intro...");
            startActivity(new Intent(this.getApplicationContext(), IntroActivity.class));
        }

        Usuario.iniciarFila(getApplicationContext());

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        else {
            /*if (Comandos.checarSite(Constants.WEBSERVICE_BRUNO)) {
                iniciar(Constants.WEBSERVICE_BRUNO);
            } else if (Comandos.checarSite(Constants.WEBSERVICE_ROUTER_BRUNO)) {
                iniciar(Constants.WEBSERVICE_ROUTER_BRUNO);
            } else if (Comandos.checarSite(Constants.WEBSERVICE_AP_WINDOWS)) {
                iniciar(Constants.WEBSERVICE_AP_WINDOWS);
            } else*/
            String ultimoWebservice = WinnerStars.getSharedPreferences().getString(Constants.PREF_ULTIMO_WEBSERVICE, "");
            if (Comandos.checarSite(Constants.WEBSERVICE_FINAL))
                iniciar(Constants.WEBSERVICE_FINAL);
            else
                Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
            if (Comandos.isEmulator()) {
                iniciar(Constants.WEBSERVICE_EMULADOR);
            } else if (!ultimoWebservice.equals("")) {
                if (Comandos.checarSite(ultimoWebservice))
                    iniciar(ultimoWebservice);
                else
                    CreateAlertDialogWithRadioButtonGroup(RegistroActivity.this).show();
            } else
                CreateAlertDialogWithRadioButtonGroup(RegistroActivity.this).show();

        }
        Drawable drawable = getResources().getDrawable( R.drawable.ic_man );
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        txtNome.setCompoundDrawablesWithIntrinsicBounds( drawable, null, null, null);

        /*if (WinnerStars.isDebugBuild())
            ((TextView) findViewById(R.id.txtTrocarwsvc)).setVisibility(View.VISIBLE);*/
    }

    public void iniciar(String webserver) {
        Log.i("WebService", "O WebService em uso nesta sessão é: " + webserver);
        WinnerStars.WEBSERVICE = webserver;
        WinnerStars.getSharedPreferences().edit()
                .putString(Constants.PREF_ULTIMO_WEBSERVICE, webserver).apply();
        verifLogin();
        DbHelper.getInstance().atualizarCursosDoWebservice();
    }

    public void trocarWebservice(View v) {
        CreateAlertDialogWithRadioButtonGroup(RegistroActivity.this).show();
    }

    private void verifLogin() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WinnerStars.WEBSERVICE + Constants.QUERY_LOGAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                new Usuario(jObj.getString("cod"), jObj.getString("nome"), jObj.getString("cat"), RegistroActivity.this);
                                startActivity(i);
                                finish();
                                Toast.makeText(getApplicationContext(), "Bem vindo(a), " + Usuario.nome.split(" ", -1)[0] + "!", Toast.LENGTH_SHORT).show();
                            } else {
                                System.out.println(Comandos.pegarMAC());
                                btnRegistrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!txtNome.getText().toString().equals("")) {
                                            registrar();
                                        }
                                    }
                                });
                                txtNome.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                                            if (!v.getText().toString().equals("")) {
                                                registrar();
                                            }
                                        }
                                        return false;
                                    }
                                });
                                btnRegistrar.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERROR:", e.toString());
                            btnRegistrar.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistroActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("mac", Comandos.pegarMAC());
                return params;
            }

        };
        Usuario.requestQueue.add(stringRequest);
    }

    public void abrirFaq(View view) {
        Intent i = new Intent(RegistroActivity.this, SobreActivity.class);
        startActivity(i);
    }

    public void abrirEspecial(View view) {
        Intent i = new Intent(RegistroActivity.this, QRActivity.class);
        i.putExtra("especial", true);
        startActivity(i);
    }

    private void registrar() {
        if (Comandos.isConectado(getApplicationContext()) && Comandos.isAvailable(WinnerStars.WEBSERVICE)) {
            btnRegistrar.setEnabled(false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, WinnerStars.WEBSERVICE + Constants.QUERY_REGISTRAR,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");
                                if (!error) {
                                    verifLogin();
                                } else {
                                    Toast.makeText(RegistroActivity.this, jObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                                    Log.e("ERROR:", jObj.getString("error_msg"));
                                    btnRegistrar.setEnabled(true);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("ERROR:", e.toString());
                                btnRegistrar.setEnabled(true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegistroActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("mac", Comandos.pegarMAC());
                    params.put("nome", txtNome.getText().toString());
                    params.put("cat", "0");
                    return params;
                }

            };
            Usuario.requestQueue.add(stringRequest);
        } else
            CreateAlertDialogWithRadioButtonGroup(RegistroActivity.this).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Comandos.checarSite(Constants.WEBSERVICE_BRUNO_LAN))
                        iniciar(Constants.WEBSERVICE_BRUNO_LAN);
                    else
                        CreateAlertDialogWithRadioButtonGroup(RegistroActivity.this).show();

                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                }
                return;
            }
        }
    }

    public android.app.AlertDialog CreateAlertDialogWithRadioButtonGroup(final Context ctx){
        try {
            android.app.AlertDialog tst;

            List<CharSequence> list = new ArrayList<>();
            list.add(" Casa do Bruno (LAN) ");
            list.add(" Casa do Bruno (WAN) ");
            list.add(" Roteador do cel do Bruno ");
            list.add(" Ponto de Acesso do Windows ");
            list.add(" 000webhost ");
            list.add(" Outro... ");
            if (WinnerStars.getSharedPreferences().getBoolean(Constants.PREF_TOUR_COMPLETE, false))
                list.add(" Modo offline (experimental) ");


            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);

            builder.setTitle("Selecione o IP do WebService");

            builder.setSingleChoiceItems(list.toArray(new CharSequence[0]), -1, new DialogInterface.OnClickListener() {

                public void onClick(final DialogInterface dialog, int item) {


                    String URL = "";
                    switch (item) {
                        case 0:
                            URL = Constants.WEBSERVICE_BRUNO_LAN;
                            break;
                        case 1:
                            URL = Constants.WEBSERVICE_BRUNO_WAN;
                            break;
                        case 2:
                            URL = Constants.WEBSERVICE_ROUTER_BRUNO;
                            break;
                        case 3:
                            URL = Constants.WEBSERVICE_AP_WINDOWS;
                            break;
                        case 4:
                            URL = Constants.WEBSERVICE_FINAL;
                            break;
                        case 5:
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegistroActivity.this);
                            alertDialog.setTitle("PASSWORD");
                            alertDialog.setMessage("Enter Password");

                            final EditText input = new EditText(RegistroActivity.this);
                            input.setText("http://192.168.15.18/" + Constants.WEBSERVICE_DIRETORIO);
                            input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            alertDialog.setView(input);

                            alertDialog.setPositiveButton("Tentar conexão!",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogg, int which) {
                                            dialogg.dismiss();
                                            dialog.dismiss();
                                            String URL = input.getText().toString();
                                            if (URL.charAt(URL.length() - 1) != '/')
                                                URL += "/";
                                            iniciar(URL);
                                        }
                                    });
                            alertDialog.show();
                            return;
                        case 6:
                            DbHelper.getInstance().atualizarCursosDoWebservice();
                            try {
                                JSONObject usuario = new JSONObject(WinnerStars.getSharedPreferences().getString(Constants.PREF_USUARIO_JSON, ""));
                                new Usuario(usuario.getString("cod"), usuario.getString("nome"),
                                        usuario.getString("cat"), RegistroActivity.this);
                                startActivity(i);
                                finish();
                                return;
                            } catch (JSONException e) {e.printStackTrace();}
                    }
                    iniciar(URL);


                    dialog.dismiss();
                }
            });
            tst = builder.create();
            return tst;
        } catch (Exception e) { e.printStackTrace(); }
        return null;


    }

}



