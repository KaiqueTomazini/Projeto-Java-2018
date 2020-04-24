package com.userdev.winnerstars.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.userdev.winnerstars.R;
import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.classesdeapoio.PicturePopup;
import com.userdev.winnerstars.models.Foto;
import com.userdev.winnerstars.utils.Comandos;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.List;

public class FotosAdapter extends RecyclerView.Adapter<FotosAdapter.MyViewHolder> {

    private Activity activity;
    private List<Foto> fotosList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ExpandableTextView txtSobre;
        public TextView txtGrupo, txtSub;
        public ImageView imgFoto;

        public MyViewHolder(View view) {
            super(view);
            txtSobre = view.findViewById(R.id.expand_text_view);
            txtGrupo = view.findViewById(R.id.txtGrupo);
            txtSub = view.findViewById(R.id.txtSub);
            imgFoto = view.findViewById(R.id.imgGaleria);
        }
    }


    public FotosAdapter(Activity activity, List<Foto> fotosList) {
        this.activity = activity;
        this.fotosList = fotosList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linha_galeria, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Foto foto = fotosList.get(position);
        if (foto != null) {
            holder.txtGrupo.setText(foto.getNomeGrupo());
            holder.txtSobre.setText(foto.sobre);
            holder.txtSub.setText(foto.sub);
        }

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if (Comandos.isConectado(activity)) {
                    try {
                        final Bitmap img = foto.getFoto(activity);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!(img.getHeight() > img.getWidth()))
                                        holder.imgFoto.setAdjustViewBounds(true);
                                    else {
                                        holder.imgFoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    }
                                    holder.imgFoto.setImageBitmap(img);
                                } catch (Exception e) {e.printStackTrace();}
                            }
                        });

                        holder.imgFoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new PicturePopup(activity, holder.imgFoto, WinnerStars.WEBSERVICE + "galeria/" + foto.nomeArquivo + ".jpeg", null, foto.nomeArquivo);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "A foto " + foto.nomeArquivo + " não pôde ser carregada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }.start();


    }

    @Override
    public int getItemCount() {
        return fotosList.size();
    }
}