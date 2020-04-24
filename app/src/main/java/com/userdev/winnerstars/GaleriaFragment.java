package com.userdev.winnerstars;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.userdev.winnerstars.adapters.FotosAdapter;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.models.Foto;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GaleriaFragment extends Fragment {
    public  static final int RequestPermissionCode  = 1 ;
    Uri imageUri;


    private List<Foto> fotosList = new ArrayList<>();
    public RecyclerView recyclerView;
    public FotosAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_galeria, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.btnAddGaleria);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                else {
                    abrirCamera();
                }
            }
        });
        EnableRuntimePermissionToAccessCamera();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);


        mAdapter = new FotosAdapter(getActivity(), fotosList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        DbHelper.getInstance().atualizarFotosDoWebservice();
        atualizarRaiz();

        return rootView;
    }

    public void atualizarRaiz() {
        fotosList.clear();
        fotosList.addAll(DbHelper.getInstance().getFotosAvaliadasGaleria());
        //mAdapter = new FotosAdapter(getActivity(), fotosList);
        //recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        /*for (Foto foto:list
             ) {

        }*/

/*
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WinnerStars.WEBSERVICE + Constants.QUERY_GET_FOTOS_GALERIA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                fotosList.clear();
                                JSONArray fotos = jObj.getJSONArray("fotos");
                                //System.out.println(gruposFragment);
                                for (int i = 0; i < fotos.length(); i++) {
                                    JSONObject resultado = fotos.getJSONObject(i);
                                    String cod = resultado.getString("cod");
                                    String caminho = resultado.getString("nomeArquivo");
                                    String sobre = resultado.getString("sobre");
                                    String grupo = resultado.getString("grupo");
                                    if (grupo.equals("null"))
                                        grupo = "";
                                    String sub = resultado.getString("sub");
                                    fotosList.add(new Foto(cod, grupo, caminho, sobre, sub));
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
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
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }

        };
        Usuario.requestQueue.add(stringRequest);*/
    }

    void abrirCamera() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        imageUri = generateTimeStampPhotoFileUri();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 7);
    }

    ImageView tst;

    ProgressDialog dialog;
    Bitmap tumb, full;

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Camera Result");
        if (requestCode == 7 && resultCode == getActivity().RESULT_OK) {
            System.out.println("Camera Result True");
            Intent i = new Intent(getActivity(), AddGaleriaActivity.class);
            i.putExtra("uri", imageUri);
            startActivity(i);
        }
    }

    public void EnableRuntimePermissionToAccessCamera(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA))
        {
            // Printing toast message after enabling runtime permission.
            Toast.makeText(getActivity(),"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(),"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
            case 9:
                if (PResult.length > 0
                        && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirCamera();

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
                break;

            case 8:
                if (PResult.length > 0
                        && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Clique novamente no botão de download.", Toast.LENGTH_SHORT).show();;

                } else {
                    Toast.makeText(getContext(), "Não será possível realizar o download.", Toast.LENGTH_SHORT).show();;
                }
                break;

        }
    }

    public Uri generateTimeStampPhotoFileUri() {
        Uri photoFileUri = null;
        File outputDir = new File(Environment.getExternalStorageDirectory(), "WinnerStars/Galeria");
        if (outputDir != null) {
            Time t = new Time();
            t.setToNow();
            File photoFile = new File(outputDir, System.currentTimeMillis()
                    + ".jpeg");
            photoFileUri = Uri.fromFile(photoFile);
        }
        System.out.println(photoFileUri);
        return photoFileUri;
    }


}
