package com.userdev.winnerstars;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.userdev.winnerstars.adapters.GruposAdapter;
import com.userdev.winnerstars.admin.AddGrupoActivity;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.classesdeapoio.Imgs;
import com.userdev.winnerstars.models.Grupo;
import com.userdev.winnerstars.classesdeapoio.PicturePopup;
import com.userdev.winnerstars.classesdeapoio.Usuario;
import com.userdev.winnerstars.utils.Constants;
import com.userdev.winnerstars.views.imgFiltro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GruposFragment extends Fragment implements RecyclerTouchListener.RecyclerTouchListenerHelper {

    public RecyclerView mRecyclerView;
    public static GruposAdapter gruposAdapter;
    private RecyclerTouchListener onTouchListener;

    public static List<String> votados = new ArrayList<>(), acad = new ArrayList<>(), adm = new ArrayList<>(),
            eletro = new ArrayList<>(), info = new ArrayList<>(), quim = new ArrayList<>(), outro = new ArrayList<>();


    List<String> cod1 = new ArrayList<>(), cod2 = new ArrayList<>();
    imgFiltro img1, img2, img3, img4, img5, img6;
    List<imgFiltro> imgs = new ArrayList<>();
    Integer votadosaaa = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.layout_grupos, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ic_quest = getResources().getDrawable(R.drawable.ic_question_mark);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.btnAddGrupo);
        if (Usuario.adm()) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AddGrupoActivity.class);
                    startActivity(i);
                }
            });
        }



        /*bancoDados.execSQL("CREATE VIEW IF NOT EXISTS ver_grupos AS SELECT cod_grupo, nome_grupo, mesa_grupo, curso_grupo, ano_grupo, tipoano_grupo, icone_grupo, info_grupo, votado_grupo FROM grupos");


        Cursor nha = bancoDados.rawQuery("SELECT cod_grupo FROM grupos", null);
        try {
            if (nha.moveToFirst()) {
                while (nha != null) {
                    String numero  = nha.getString(nha.getColumnIndex("cod_grupo"));
                    cod1.add(nha.getString(nha.getColumnIndex("cod_grupo")));
                    if (!nha.moveToNext())
                        break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        atualizarRaiz();*/
        DbHelper.getInstance().atualizarGruposDoWebservice();



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        atualizar(0);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        onTouchListener = new RecyclerTouchListener(this.getActivity(), mRecyclerView);
        onTouchListener
                .setIndependentViews(R.id.imageView)
                .setViewsToFade(R.id.imageView)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Grupo grupo = gruposAdapter.modelList.get(position);
                        /*Intent intent = new Intent(getActivity(), DetalhesGrupoActivity.class);
                        intent.putExtra("pos", grupo.fon);
                        startActivity(intent);*/

                        Intent i = new Intent(getActivity(), DetalhesGrupoActivity.class);
                        i.putExtra("cod", grupo.cod);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            GruposAdapter.MainViewHolder mainViewHolder = (GruposAdapter.MainViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);

                            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                    new Pair<>((View)mainViewHolder.getImageView(), getString(R.string.grupo_img_transition)),
                                    new Pair<>((View)mainViewHolder.getTextView(), getString(R.string.grupo_txt_transition)));
                            startActivity(i, transitionActivityOptions.toBundle());
                        } else {
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        Grupo grupo = gruposAdapter.modelList.get(position);
                        //Toast.makeText(getActivity(), grupo.nomeProjeto, Toast.LENGTH_LONG).show();
                        if (!grupo.icone.equals(""))
                            try {
                                Bitmap fon = null;
                                //Bitmap bitmap = DbBitmapUtility.getImage(teste);
                                Cursor nha = DbHelper.getInstance().getDatabase()
                                        .rawQuery("SELECT imgicone_grupo FROM grupos WHERE imgicone_grupo IS NOT NULL AND cod_grupo = " + grupo.cod, null);
                                if (nha.getCount() > 0) {
                                    nha.moveToFirst();
                                    fon = Imgs.DbBitmapUtility.getImage(nha.getBlob(nha.getColumnIndex("imgicone_grupo")));
                                    System.out.println("img no banco!");
                                }
                                new PicturePopup(getContext(), R.layout.popup_photo_full, mRecyclerView.findViewById(independentViewID), grupo.icone.trim(), fon);
                            } catch (Exception e) { e.printStackTrace(); }
                    }
                })
                .setLongClickable(true, new RecyclerTouchListener.OnRowLongClickListener() {
                    @Override
                    public void onRowLongClicked(int position) {
                        if (Usuario.adm()) {
                            Grupo grupo = gruposAdapter.modelList.get(position);
                            Intent intent = new Intent(getActivity(), AddGrupoActivity.class);
                            intent.putExtra("cod", grupo.cod);
                            startActivity(intent);
                        }
                    }
                });

        img1 = (imgFiltro) rootView.findViewById(R.id.imageView); img1.intCurso = ID_ACADEMICO;
        img2 = (imgFiltro) rootView.findViewById(R.id.imageView2); img2.intCurso = ID_ADMINISTRACAO;
        img3 = (imgFiltro) rootView.findViewById(R.id.imageView3); img3.intCurso = ID_ELETRONICA;
        img4 = (imgFiltro) rootView.findViewById(R.id.imageView4); img4.intCurso = ID_INFORMATICA;
        img5 = (imgFiltro) rootView.findViewById(R.id.imageView5); img5.intCurso = ID_QUIMICA;
        img6 = (imgFiltro) rootView.findViewById(R.id.imageView6); img6.intCurso = ID_OUTROS;
        imgs.add(img1);
        imgs.add(img2);
        imgs.add(img3);
        imgs.add(img4);
        imgs.add(img5);
        imgs.add(img6);



        for (final imgFiltro img:imgs
                ) {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imgFiltro clicado = (imgFiltro) view;
                    if (cursoFiltro == clicado.intCurso){
                        cursoFiltro = ID_TODOS;
                        clicado.apagar();
                    }
                    else {
                        cursoFiltro = clicado.intCurso;
                        clicado.acender();
                        for (imgFiltro img:imgs
                             ) {
                            if (img != clicado) {
                                img.apagar();
                            }
                        }
                    }
                    atualizar();
                }
            });
        }


        return rootView;
    }

    public static Integer cursoFiltro = 0;

    public static final int ID_TODOS = 0;
    public static final int ID_ACADEMICO = 1;
    public static final int ID_ADMINISTRACAO = 2;
    public static final int ID_ELETRONICA = 3;
    public static final int ID_INFORMATICA = 4;
    public static final int ID_QUIMICA = 5;
    public static final int ID_OUTROS = 6;


    public void atualizar() {
        atualizar(cursoFiltro);
    }

    public void atualizar(Integer curso) {
        List<Grupo> list = DbHelper.getInstance().getGrupos(curso);
        gruposAdapter = new GruposAdapter(getActivity(), list);
        mRecyclerView.setAdapter(gruposAdapter);
        Integer i = 0;
        for (Grupo grupo:list
             ) {
            if (!grupo.votado)
                i++;
        }
        PrincipalActivity.icon.setText(i.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.addOnItemTouchListener(onTouchListener); }

    @Override
    public void onPause() {
        super.onPause();
        mRecyclerView.removeOnItemTouchListener(onTouchListener);
    }

    public void atualizarSearch(String nome) {
        String condicao = "";
        if (cursoFiltro == ID_TODOS)
            condicao = "WHERE nome_grupo LIKE '%" + nome + "%'";
        else
            condicao = "WHERE nome_grupo LIKE '%" + nome + "%' AND curso_grupo = " + cursoFiltro.toString();
        gruposAdapter = new GruposAdapter(getActivity(), DbHelper.getInstance().getGrupos(condicao));
        mRecyclerView.setAdapter(gruposAdapter);
    }



    @Override
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        OnActivityTouchListener touchListener = listener;
    }


    public static Drawable ic_quest;





}
