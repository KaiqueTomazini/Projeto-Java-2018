package com.userdev.winnerstars.utils;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.Result;
import com.userdev.winnerstars.PrincipalActivity;
import com.userdev.winnerstars.VotarActivity;
import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.classesdeapoio.QRActivity;
import com.userdev.winnerstars.classesdeapoio.Usuario;
import com.userdev.winnerstars.utils.Constants;
import com.userdev.winnerstars.views.imgFiltro;
import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCodeFragment extends BarCodeScannerFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        //this.stopScan();

        //this.restartPreviewAfterDelay(1000L);
        this.setmCallBack(new IResultCallback() {
            @Override
            public void result(Result lastResult) {
                if (Comandos.isConectado(getActivity()) && Comandos.isAvailable(WinnerStars.WEBSERVICE)) {
                    lerQR(lastResult.getText());
                }
                else {
                    txt.setText(lastResult.getText());
                }

                //Toast.makeText(getActivity(), "Scan: " + lastResult.toString(), Toast.LENGTH_SHORT).show();
            }
        });



        /*if (!isViewShown) {
            this.startScan();
        }*/
        //this.startScan();

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageView tst = (ImageView) getView().findViewById(com.welcu.android.zxingfragmentlib.R.id.refresh);
        if (!getActivity().getIntent().getBooleanExtra("intent", false)) {
            tst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopScan();
                    Intent i = new Intent(getActivity(), QRActivity.class);
                    i.putExtra("intent", true);
                    startActivity(i);
                }
            });
        }
        else if (!getActivity().getIntent().getBooleanExtra("especial", false)) {
            System.out.println("sdnufsdyuysdifhfudh");
        } else
            tst.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.getActivity() != null) {
            startScan();
            if (PrincipalActivity.viewPager != null)
            if (PrincipalActivity.viewPager.getCurrentItem() == 0) {
            } else {
                stopScan();
            }
            PrincipalActivity.tst = true;
        }
    }
    /*@Override
    public void onPause() {
        super.onPause();
        if (PrincipalActivity.viewPager.getCurrentItem() == 0)
            stopScan();
    }*/

    private static final int MY_CAMERA_REQUEST_CODE = 100;


    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (teste)
        if (isVisibleToUser) {
            this.startScan();
            System.out.println("start");
        }
        else {
            this.stopScan();
            System.out.println("stop");
        }
    }*/

    /*private boolean isViewShown = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            startScan();
        } else {
            isViewShown = false;
            stopScan();
        }
    }*/

    private void lerQR(final String qr) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WinnerStars.WEBSERVICE + Constants.QUERY_GET_ALUNO_QR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                stopScan();
                                if (Usuario.adm()) {
                                    String texto = jObj.getString("nome") + " do grupo '" + jObj.getString("nome_grupo") + "'";
                                    txt.setText(texto);
                                }
                                if (!jObj.getBoolean("votou")) {
                                    Intent i = new Intent(getActivity(), VotarActivity.class);
                                    i.putExtra("json", jObj.toString());
                                    stopScan();
                                    startActivity(i);
                                    //dialog(jObj);
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    //builder.setTitle("Titulo");
                                    builder.setMessage("Você já votou em " + jObj.getString("nome_grupo"));
                                    builder.setPositiveButton("Desculpa!", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            startScan();
                                        }
                                    });
                                    AlertDialog alerta = builder.create();
                                    alerta.show();
                                }
                            } else {
                                txt.setText(qr);
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
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("crypt", qr);
                params.put("login", Usuario.cod);
                return params;
            }

        };
        Usuario.requestQueue.add(stringRequest);
    }


}
