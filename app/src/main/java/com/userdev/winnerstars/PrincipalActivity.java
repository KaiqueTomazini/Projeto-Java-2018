package com.userdev.winnerstars;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.models.Curso;
import com.userdev.winnerstars.classesdeapoio.Imgs;
import com.userdev.winnerstars.classesdeapoio.Usuario;
import com.userdev.winnerstars.utils.Constants;
import com.userdev.winnerstars.utils.MenuPrincipal;
import com.userdev.winnerstars.utils.QRCodeFragment;
import com.userdev.winnerstars.utils.ViewPagerAdapter;
import com.userdev.winnerstars.models.Cracha;
import com.userdev.winnerstars.views.imgFiltro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrincipalActivity extends AppCompatActivity {

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    public static ViewPager viewPager;

    //Fragments

    QRCodeFragment qrCodeFragment;
    public static GruposFragment gruposFragment;
    ParceriasFragment parceriasFragment;
    public static GaleriaFragment galeriaFragment;
    AppsFragment apps;

    public static TextView icon;

    public static MaterialSearchView searchView;

    String[] tabTitle={"","GRUPOS","GALERIA"};
    public int[] unreadCount={0,5,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        try
        {
            setupTabIcons();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                item.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println(newText);
                if (searchView.isSearchOpen())
                    if (!newText.equals(""))
                        gruposFragment.atualizarSearch(newText);
                    else
                        gruposFragment.atualizar(imgFiltro.cursoAtual);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                gruposFragment.atualizar(gruposFragment.cursoFiltro);
            }
        });

        searchView.setVoiceSearch(false);




        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);
                try {
                    String pagina = "";
                    switch (position) {
                        case 0:
                            pagina = "Camera QR"; break;
                        case 1:
                            pagina = "Grupos"; break;
                        case 2:
                            pagina = "Galeria"; break;
                        case 3:
                            pagina = "Parcerias"; break;
                        case 4:
                            pagina = "Apps"; break;
                    }
                    Log.i("ViewPager", "Você está vendo: " + pagina);
                    if (position == 0) {
                        qrCodeFragment.startScan();
                    } else
                        qrCodeFragment.stopScan();
                    if (position != 1) {
                        searchView.setVisibility(View.GONE);
                        item.setVisible(false);
                    } else {
                        searchView.setVisibility(View.VISIBLE);
                        item.setVisible(true);
                    }
                } catch (Exception e) {e.printStackTrace();}
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        WinnerStars.getSharedPreferences().edit()
                .putBoolean(Constants.PREF_REGISTRADO, true).apply();
    }

    public static Boolean tst = false;

    @Override
    public void onResume() {
        super.onResume();
        if (tst)
            qrCodeFragment.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //menu.findItem(R.id.menu_export).setVisible(false);

        item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        searchView.setMenuItem(item);
        if (!Usuario.adm()) {
            menu.findItem(R.id.menu_qrc).setVisible(false);
            menu.findItem(R.id.menu_teste).setVisible(false);
            menu.findItem(R.id.menu_curso).setVisible(false);
            menu.findItem(R.id.menu_export).setVisible(false);
            menu.findItem(R.id.menu_votos).setVisible(false);
        }
        // Associate searchable configuration with the SearchView
        return true;
    }

    MenuItem item;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        MenuPrincipal.executarAcao(item.getItemId(), PrincipalActivity.this);
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        qrCodeFragment =new QRCodeFragment();
        gruposFragment =new GruposFragment();
        parceriasFragment =new ParceriasFragment();
        galeriaFragment =new GaleriaFragment();
        apps=new AppsFragment();
        adapter.addFragment(qrCodeFragment,"QRCODE");
        adapter.addFragment(gruposFragment,"GRUPOS");
        //adapter.addFragment(parceriasFragment,"PARCERIAS");
        adapter.addFragment(galeriaFragment,"GALERIA");
        //adapter.addFragment(apps,"APPS");
        viewPager.setAdapter(adapter);
    }


    private View prepareTabView(int pos) {
        View view = getLayoutInflater().inflate(R.layout.custom_tab,null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_count);
        if (pos == 1) {
            icon = tv_count;
            System.out.println("icon setado!");
        }
        tv_title.setText(tabTitle[pos]);
        if(unreadCount[pos]>0)
        {
            tv_count.setVisibility(View.VISIBLE);
            tv_count.setText(""+unreadCount[pos]);
        }
        else
            tv_count.setVisibility(View.GONE);


        return view;
    }

    public void setupTabIcons()
    {
        tabLayout.getTabAt(0).setCustomView(getLayoutInflater().inflate(R.layout.qrcode_tab, null));
        //qrCodeFragment.stopScan();

        for(int i=1;i<tabTitle.length;i++)
        {
            /*TabLayout.Tab tabitem = tabLayout.newTab();
            tabitem.setCustomView(prepareTabView(i));
            tabLayout.addTab(tabitem);*/

            tabLayout.getTabAt(i).setCustomView(prepareTabView(i));
        }


    }

    public void requestJSON(final ProgressDialog dialog){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WinnerStars.WEBSERVICE + Constants.QUERY_GET_DADOS_CRIAR_CRACHA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final List<Bitmap> bmps = new ArrayList<>();
                            final List<String> bmpsNome = new ArrayList<>();
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                final JSONArray ministries = jObj.getJSONArray("qr");
                                dialog.setMessage("Criando crachás:\r\n0/" + ministries.length());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.setMessage("0/" + ministries.length());
                                    }
                                });
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            for (int i = 0; i < ministries.length(); i++) {
                                                final int ii = i;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.setMessage("Criando crachás:\r\n" + (ii + 1) + "/" + ministries.length());
                                                    }
                                                });
                                                JSONObject c = ministries.getJSONObject(i);
                                                String crypt = c.getString("crypt");
                                                String aluno = c.getString("aluno");
                                                String grupo = c.getString("grupo");
                                                String cursoId = c.getString("curso");
                                                String ano = c.getString("ano");
                                                String cod = c.getString("cod");
                                                Curso curso = DbHelper.getInstance().getCurso(cursoId);
                                                bmps.add(new Cracha().criar(getApplication(), aluno, "\"" + grupo + "\"", crypt, ano, cod, curso));
                                                bmpsNome.add(aluno + "_" + crypt);
                                                //new PicturePopup(getApplicationContext(), R.layout.popup_photo_full, getWindow().getDecorView().findViewById(android.R.id.content), "", coisado);
                                    /*FileOutputStream out = null;
                                    try {
                                        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                                        Date currentTime = Calendar.getInstance().getTime();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                                        String currentDateandTime = sdf.format(currentTime);
                                        String nome = aluno + "_" + crypt + ".png";
                                        String nomeImagem = diretorio.getPath() + "/Winner Stars/QRs/" + nome;
                                        out = new FileOutputStream(nomeImagem);
                                        coisado.compress(Bitmap.CompressFormat.PNG, 100, out);// bmp is your Bitmap instance
                                        System.out.println(out);
                                        System.out.println(nomeImagem);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            if (out != null)
                                                out.close();
                                        } catch (IOException e) { e.printStackTrace();Log.e("ERROR:", response); }
                                    }*/
                                            }
                                            bmps.add(new Cracha().criar(getApplication(), "Aluno Teste de Eletrônica", "\"" + "Grupo Teste de Elet." + "\"",
                                                    "fjsjug2f5e8y9dfyujd58426yug34er", "3", "0", DbHelper.getInstance().getCurso("3")));
                                            bmpsNome.add("Aluno Teste de Eletrônica_fjsjug2f5e8y9dfyujd58426yug34er");
                                            int i = 0;
                                            for (Bitmap coisado : bmps
                                                    ) {
                                                final int ii = i;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.setMessage("Enviando crachás ao servidor:\r\n" + (ii) + "/" + ministries.length());
                                                    }
                                                });
                                                Imgs.doFileUpload(WinnerStars.WEBSERVICE + Constants.QUERY_UPLOAD_CRACHA, bmpsNome.get(bmps.indexOf(coisado)), coisado, Bitmap.CompressFormat.PNG);
                                                i++;
                                            }
                                            //Toast.makeText(getApplication(), "Sucesso", Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                });
                                thread.start();

                            } else {
                                Log.e("ERROR:", jObj.getString("error_msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERROR:", e.toString());
                        }
                        //dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PrincipalActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                //params.put(ACTION,"listar_ministerios");
                return params;
            }
        };
        Usuario.requestQueue.add(stringRequest);
    }

    protected void exportDb() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File dataDirectory = Environment.getDataDirectory();

        FileChannel source = null;
        FileChannel destination = null;

        String currentDBPath = "/data/" + getApplicationContext().getApplicationInfo().packageName + "/databases/winnerstars";
        String backupDBPath = "winnerstars.sqlite";
        File currentDB = new File(dataDirectory, currentDBPath);
        File backupDB = new File(externalStorageDirectory, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());

            Toast.makeText(this, externalStorageDirectory.toString() + "   " + dataDirectory.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (source != null) source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (destination != null) destination.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS2 = 0;

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}