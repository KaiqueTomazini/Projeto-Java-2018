package com.userdev.winnerstars.models;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatDelegate;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.userdev.winnerstars.R;
import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.utils.Comandos;
import com.userdev.winnerstars.classesdeapoio.Imgs;
import com.userdev.winnerstars.utils.Constants;

import java.io.BufferedInputStream;
import java.io.InputStream;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;



public class Cracha {

    Context context;

    public Cracha() {

    }

    public Bitmap criar(Context context, String txtNome, String txtGrupo, String txtQR, String ano, String cod, Curso curso) {
        int w = 638, h = 1012;
        res = context.getResources();
        this.context = context;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; 
        bitmap = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(WHITE);
        logoEscola();
        escreverTexto(txtGrupo, Color.rgb(61,61,61),
                logo.getHeight() + 10, 1);
        escreverTexto(txtNome, Color.BLACK,
                grupo.height() + logo.getHeight() + 10 + 20, 2);
        logoGrupo(cod, curso.cor);
        criarQr(txtQR, curso.icone);
        
        escreverTexto(ano + "º " + curso.sigla, Color.rgb(61,61,61),
                0, 3);


        return Imgs.addBorda(bitmap, 10, Color.BLACK);

    }

    public Bitmap criar(Context context, String txtNome, String txtQR, Grupo grupo) {
        int w = 638, h = 1012;
        res = context.getResources();
        String idCurso = grupo.curso.cod.toString();
        this.context = context;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        bitmap = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(WHITE);
        logoEscola();
        escreverTexto( "\"" + grupo.nomeProjeto + "\"", Color.rgb(61,61,61),
                logo.getHeight() + 10, 1);
        escreverTexto(txtNome, Color.BLACK,
                this.grupo.height() + logo.getHeight() + 10 + 20, 2);
        logoGrupo(grupo.cod, grupo.curso.cor);
        criarQr(txtQR, grupo.curso.icone);

        escreverTexto(grupo.ano.toString() + "º " + grupo.curso.sigla, Color.rgb(61,61,61),
                0, 3);

        Bitmap resultado = Imgs.addBorda(bitmap, 10, Color.BLACK);
        Imgs.doFileUpload(WinnerStars.WEBSERVICE + Constants.QUERY_UPLOAD_CRACHA, txtNome + "_" + txtQR, resultado, Bitmap.CompressFormat.PNG);


        return resultado;

    }
    public Resources res;
    Bitmap bitmap;



    void escreverTexto(String texto, int cor, int tamanho, Integer qual) {
        float scale = res.getDisplayMetrics().density;
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        if(bitmapConfig == null)
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(cor);
        float testTextSize = 14 * scale;
        paint.setTextSize((int) testTextSize);
        paint.setShadowLayer(1f, 0f, 1f, WHITE);
        Rect bounds = new Rect();
        paint.getTextBounds(texto, 0, texto.length(), bounds);
        Double desiredTextSize = testTextSize * (bitmap.getWidth() * 0.9) / bounds.width();
        paint.setTextSize(desiredTextSize.floatValue());
        bounds = new Rect();
        paint.getTextBounds(texto, 0, texto.length(), bounds);
        switch (qual) {
            case 1:
                grupo = bounds;
                paint.setTypeface(Comandos.burbank);
                bounds = new Rect();
                paint.getTextBounds(texto, 0, texto.length(), bounds);
                break;
            case 2:
                nome = bounds;
                break;
            case 3:
                ano = bounds;
                paint.setTextSize(50);
                paint.getTextBounds(texto, 0, texto.length(), bounds);
                break;
        }
        if (qual != 3)
            canvas.drawText(texto, (bitmap.getWidth() - bounds.width())/2, bounds.height() + tamanho, paint);
        else
            canvas.drawText(texto, (bitmap.getWidth() - bounds.width())/2, bitmap.getHeight() - 20, paint);

    }

    Bitmap logo;

    Rect nome;
    Rect grupo;
    Rect ano;
    Rect qr;

    /*private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }*/


    void logoEscola() {
        try {
            //Bitmap anchieta1 = BitmapFactory.decodeResource(res, img);
            AssetManager assets = context.getResources().getAssets();
            InputStream buffer = new BufferedInputStream((assets.open("logocolegio.png")));
            Bitmap anchieta1 = BitmapFactory.decodeStream(buffer);
            Bitmap anchieta = Bitmap.createScaledBitmap(anchieta1,
                    bitmap.getWidth() - 50, anchieta1.getHeight() / (anchieta1.getWidth() / (bitmap.getWidth() - 50)), false);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(anchieta, (bitmap.getWidth() - anchieta.getWidth()) / 2, 0, null);
            logo = anchieta;
        } catch (Exception e) {e.printStackTrace();}
    }
    
    void logoGrupo(String cod, int cor) {
        try {
            Bitmap fon = DbHelper.getInstance().getIconeGrupo(cod);
            Canvas canvas = new Canvas(bitmap);
            int logoy = nome.height() + 30 + grupo.height();
            if (fon != null) {
                System.out.println("img no banco!");
                Double logox = ((double) logoy / fon.getHeight() * fon.getWidth());
                Bitmap logo = Bitmap.createScaledBitmap(fon,
                        logox.intValue(), logoy, false);
                Paint transp = new Paint();
                transp.setAlpha(100);
                canvas.drawBitmap(logo,(bitmap.getWidth() - logo.getWidth())/2, this.logo.getHeight(), transp);
            }
            else {
                //fon = BitmapFactory.decodeResource(resources, R.drawable.ic_question_mark);
                Drawable logo = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_question_mark, null);
                logo.setBounds(0, 0, logoy, logoy);
                logo.setAlpha(100);
                logo.setColorFilter(cor, PorterDuff.Mode.SRC_ATOP);
                canvas.translate((bitmap.getWidth() - logoy)/2, (this.logo.getHeight()));
                logo.draw(canvas);
                canvas.translate(-(bitmap.getWidth() - logoy)/2, -(this.logo.getHeight()));
            }
            //bitmap.getWidth() - 50, anchieta1.getHeight() / (anchieta1.getWidth() / (bitmap.getWidth() - 50)), false);
        } catch (Exception e) { e.printStackTrace(); }
        
    }
    
    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 512, 512, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 512, 0, 0, w, h);
        return bitmap;
    }
    
    void criarQr(String txtQR, int iconeCurso) {
        Double tamanho1 = Double.longBitsToDouble(0);
        Canvas canvas = new Canvas(bitmap);
        try {

            int teste = bitmap.getHeight() - (nome.height() + logo.getHeight() + 10 + grupo.height() + 20) - 10;
            Bitmap qr1 = encodeAsBitmap(txtQR);
            Bitmap qr = Bitmap.createScaledBitmap(qr1,
                    teste, teste, false);
            tamanho1 = qr.getHeight() * 0.1 + 10;

            canvas.drawBitmap(qr, (bitmap.getWidth() - qr.getWidth()) / 2, nome.height() + logo.getHeight() + 10 + grupo.height() + 35, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tamanho = tamanho1.intValue();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Drawable tst = VectorDrawableCompat.create(context.getResources(), iconeCurso, null);
        tst.setBounds(0, 0, tamanho, tamanho);
        tst.setAlpha(200);
        canvas.translate((bitmap.getWidth() - tamanho) / 2, (nome.height() + logo.getHeight() + 10 + grupo.height() + 20 + 17));
        tst.draw(canvas);
        qr = tst.getBounds();
        canvas.translate(-(bitmap.getWidth() - tamanho) / 2, -(nome.height() + logo.getHeight() + 10 + grupo.height() + 20 + 17));
    }
}
