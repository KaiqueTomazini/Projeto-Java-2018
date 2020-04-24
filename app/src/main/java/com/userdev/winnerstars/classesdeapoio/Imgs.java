package com.userdev.winnerstars.classesdeapoio;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.userdev.winnerstars.GruposFragment;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.utils.Comandos;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by User on 28/02/2018.
 */

public class Imgs extends AsyncTask<String, Void, Bitmap> {
    RoundedImageView bmImage;
    Activity context;
    public static byte[] teste;

    public Imgs(RoundedImageView bmImage, Activity context) {
        this.bmImage = bmImage;
        this.context = context;
    }

    String cor = "";

    protected Bitmap doInBackground(String... params) {
        String urlStr = params[0];
        cor = params[1];
        Bitmap img = null;
        Cursor nha = DbHelper.getInstance().getDatabase().rawQuery("SELECT imgicone_grupo FROM grupos WHERE imgicone_grupo IS NOT NULL AND cod_grupo = " + params[2], null);
        if (!(nha.getCount() > 0)) {
            if (Comandos.isConectado(this.context)) {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(urlStr);
                HttpResponse response;
                try {
                    response = (HttpResponse) client.execute(request);
                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                    InputStream inputStream = bufferedEntity.getContent();
                    img = BitmapFactory.decodeStream(inputStream);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SQLiteStatement p = DbHelper.getInstance().getDatabase(true).compileStatement("UPDATE grupos SET imgicone_grupo = ? WHERE cod_grupo = " + params[2]);
                teste = DbBitmapUtility.getBytes(img);
                p.bindBlob(1, teste);
                p.executeUpdateDelete();
            }
        } else {
            nha.moveToFirst();
            teste = nha.getBlob(nha.getColumnIndex("imgicone_grupo"));
            img = DbBitmapUtility.getImage(teste);
            System.out.println("img no banco!");
        }
        if (img != null)
            return createSquaredBitmap(img);
        else {
            return img;
        }
    }

    private static Bitmap createSquaredBitmap(Bitmap srcBmp) {
        int dim = Math.max(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(srcBmp, (dim - srcBmp.getWidth()) / 2, (dim - srcBmp.getHeight()) / 2, null);

        return dstBmp;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null)
            bmImage.setImageBitmap(result);
        else {
            bmImage.setBackgroundColor(Integer.parseInt(cor));
            bmImage.setImageDrawable(GruposFragment.ic_quest);
            bmImage.setColorFilter(Color.WHITE);
        }
    }


    public static class DbBitmapUtility {
        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                return stream.toByteArray();
            } else
                return null;
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }

    public static Bitmap addBorda(Bitmap bmp, int borderSize, int cor) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(
                bmp.getWidth()
                //+ borderSize * 2
                , bmp.getHeight()
                //+ borderSize * 2
                , bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(cor);

        Bitmap tst = Bitmap.createScaledBitmap(bmp,
                bmp.getWidth() - borderSize * 2, bmp.getHeight() - borderSize * 2, false);
        canvas.drawBitmap(tst, borderSize, borderSize, null);
        return bmpWithBorder;
    }


    public static final String TAG = "Upload Image Apache";

    public static void doFileUpload(final String url, final String nome, final Bitmap bmp, final Bitmap.CompressFormat compressFormat) {

        /*Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();


            }
        });
        t.start();*/
        Log.i(TAG, "Starting Upload...");
        final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("image", convertBitmapToString(bmp, compressFormat)));
        nameValuePairs.add(new BasicNameValuePair("nome", nome));

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            String responseStr = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            System.out.println("Error in http connection " + e.toString());
        }

    }

    public static String convertBitmapToString(Bitmap bmp, Bitmap.CompressFormat compressFormat) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(compressFormat, 100, stream); //compress to which format you want.
        byte[] byte_arr = stream.toByteArray();
        String imageStr = Base64.encodeToString(byte_arr, 0);
        return imageStr;
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        Activity context;

        public DownloadImageTask(ImageView bmImage, Activity context) {
            this.bmImage = bmImage;
            this.context = context;
        }

        protected Bitmap doInBackground(String... params) {
            String urlStr = params[0];
            Bitmap img = null;

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlStr);
            HttpResponse response;
            try {
                response = (HttpResponse) client.execute(request);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                InputStream inputStream = bufferedEntity.getContent();
                img = BitmapFactory.decodeStream(inputStream);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return img;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
