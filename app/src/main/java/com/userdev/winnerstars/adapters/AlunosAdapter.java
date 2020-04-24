package com.userdev.winnerstars.adapters;

import android.content.Context;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.userdev.winnerstars.R;
import com.userdev.winnerstars.models.Aluno;

import java.util.ArrayList;
import java.util.List;

public class AlunosAdapter extends ArrayAdapter<Aluno> {

    public AlunosAdapter(Context context, int resource, List<Aluno> items, Double p, Double v) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.linha_alunos, null);
        }
        Aluno aluno = getItem(position);
        ((TextView) v.findViewById(R.id.txtNomeAluno)).setText(aluno.nome);
        return v;
    }
}
