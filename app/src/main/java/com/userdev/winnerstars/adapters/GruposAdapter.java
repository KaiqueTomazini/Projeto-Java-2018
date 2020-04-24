package com.userdev.winnerstars.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.userdev.winnerstars.R;
import com.userdev.winnerstars.classesdeapoio.Imgs;
import com.userdev.winnerstars.models.Grupo;

import java.util.ArrayList;
import java.util.List;

public class GruposAdapter extends RecyclerView.Adapter<GruposAdapter.MainViewHolder> {
    LayoutInflater inflater;
    public List<Grupo> modelList;
    Activity activity;
    //private final GrupoItemClickListener grupoItemClickListener;

    public GruposAdapter(Activity activity, List<Grupo> list) {
        this.activity = activity;

        inflater = LayoutInflater.from(activity);
        modelList = new ArrayList<>(list);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.linha_grupos, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        holder.bindData(modelList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        TextView mainText, subText, mesa;

        RoundedImageView icone;

        LinearLayout fundo;

        public MainViewHolder(View itemView) {
            super(itemView);
            mainText = (TextView) itemView.findViewById(R.id.mainText);
            subText = (TextView) itemView.findViewById(R.id.subText);
            mesa = (TextView) itemView.findViewById(R.id.txtMesa);
            fundo = (LinearLayout) itemView.findViewById(R.id.rowFG);
            icone = (RoundedImageView) itemView.findViewById(R.id.imageView);
        }

        public void bindData(final Grupo grupo) {
            mainText.setText(grupo.nomeProjeto);
            subText.setText(grupo.getSub());
            mesa.setText(grupo.mesa);
            if (grupo.votado)
                fundo.setBackground(activity.getResources().getDrawable(R.drawable.bg_votado));
            else
                fundo.setBackgroundColor(Color.WHITE);
            if (!grupo.icone.equals("")) {
                icone.setColorFilter(null);
                new Imgs(icone, activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, grupo.icone.trim(), grupo.curso.cor.toString(), grupo.cod);
            } else {
                icone.setBackgroundColor(grupo.curso.cor);
                icone.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_question_mark));
                icone.setColorFilter(Color.WHITE);
            }
        }

        public RoundedImageView getImageView() {
            return icone;
        }
        public TextView getTextView() {
            return mainText;
        }
    }



}
