package com.userdev.winnerstars.views;

/**
 * Created by User on 01/03/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Toast;

import com.userdev.winnerstars.PrincipalActivity;
import com.userdev.winnerstars.R;

public class imgFiltro extends android.support.v7.widget.AppCompatImageView {
    public Integer intCurso = 0;
    public Context context;
    public Boolean clicado = false;
    public static Integer cursoAtual = -1;

    public imgFiltro(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        this.context = context;
    }

    public imgFiltro(Context context) {
        super(context);
        this.context = context;
    }

    public imgFiltro(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void clicar() {
        this.clicado = true;
        this.setColorFilter(getResources().getColor(R.color.colorPrimary));
        cursoAtual = this.intCurso;
        PrincipalActivity.searchView.closeSearch();
        PrincipalActivity.gruposFragment.atualizar(intCurso);
    }

    public void desclicar(Boolean bool, Context ctx) {
        this.setColorFilter(Color.WHITE);
        this.clicado = false;
        if (cursoAtual == this.intCurso) {
            cursoAtual = 0;
            PrincipalActivity.gruposFragment.atualizar(0);
        }
        if (bool)
            Toast.makeText(ctx, "Não há nada para mostrar!", Toast.LENGTH_SHORT).show();
    }

    public void acender() {
        this.setColorFilter(getResources().getColor(R.color.colorPrimary));
        PrincipalActivity.searchView.closeSearch();
    }

    public void apagar() {
        this.setColorFilter(Color.WHITE);
        PrincipalActivity.searchView.closeSearch();
    }
}
