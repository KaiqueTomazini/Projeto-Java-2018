package com.userdev.winnerstars.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.userdev.winnerstars.PrincipalActivity;
import com.userdev.winnerstars.R;
import com.userdev.winnerstars.SobreActivity;
import com.userdev.winnerstars.WinnerStars;
import com.userdev.winnerstars.db.DbHelper;
import com.userdev.winnerstars.db.ExportarJson;
import com.userdev.winnerstars.views.imgFiltro;

import org.json.JSONObject;

public class MenuPrincipal {
    public static void executarAcao(int id_menu, final PrincipalActivity activity) {
        switch (id_menu) {
            case R.id.menu_qrc:
                AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                adb.setTitle("Criar crachás");
                String mensagem = "Esta operação requer alta capacidade de memória, pois manipulação de imagens " +
                        "não é algo fácil para um celular. Prossiga apenas se acredita que seu aparelho é capaz " +
                        "de executar tal operação sem perda excessiva de memória RAM.";
                if (Build.VERSION.SDK_INT < 24)
                    mensagem += "\r\n\r\nSeu sistema é inferior à versão 7.0, não é recomendado prosseguir!";
                adb.setMessage(mensagem);
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialog dialogg = ProgressDialog.show(activity, "Aguarde.",
                                "Processando..!", true);
                        activity.requestJSON(dialogg);
                    }
                });
                adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.show();
                return;
            case R.id.menu_curso:
                View parentLayout = activity.findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Por enquanto, o gerenciamento dos cursos deve ser feito diretamente no banco de dados.", Snackbar.LENGTH_LONG)
                        .setActionTextColor(activity.getResources().getColor(android.R.color.holo_red_light))
                        .show();
                return;
            case R.id.menu_atualizar:
                /*bancoDados.execSQL("drop table if exists grupos");
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS grupos (" +
                    "  cod_grupo int(11) NOT NULL," +
                    "  nome_grupo varchar(40) NOT NULL," +
                    "  mesa_grupo int(3) NOT NULL," +
                    "  curso_grupo int(1) NOT NULL," +
                    "  ano_grupo int(1) NOT NULL," +
                    "  tipoano_grupo int(1) NOT NULL," +
                    "  icone_grupo text NOT NULL," +
                    "  imgicone_grupo blob," +
                    "  info_grupo text NOT NULL," +
                    "  votado_grupo boolean NOT NULL, UNIQUE(cod_grupo))");
                bancoDados.execSQL("CREATE VIEW IF NOT EXISTS ver_grupos AS SELECT cod_grupo, nome_grupo, mesa_grupo, curso_grupo, ano_grupo, icone_grupo, info_grupo, votado_grupo FROM grupos");*/
                DbHelper.getInstance().recriarDb();
                DbHelper.getInstance().atualizarCursosDoWebservice();
                DbHelper.getInstance().atualizarGruposDoWebservice();
                DbHelper.getInstance().atualizarFotosDoWebservice();
                //RegistroActivity.teste(PrincipalActivity.this);
                activity.gruposFragment.atualizar(0);
                activity.galeriaFragment.atualizarRaiz();
                return;
            case R.id.menu_export:
                /*if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_CONTACTS)) {
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS2);
                    }
                }
                exportDb();*/
                new ExportarJson().uploadParaWeb();
                return;
            case R.id.menu_teste:
                activity.galeriaFragment.atualizarRaiz(); return;
            case R.id.menu_votos:
                try {
                    JSONObject limpar = new JSONObject(Comandos.postDados(WinnerStars.WEBSERVICE + Constants.QUERY_DEL_VOTOS_TUDO, ""));
                    Boolean error = limpar.getBoolean("error");
                    if (!error) {
                        Toast.makeText(activity, limpar.getString("success_msg"), Toast.LENGTH_SHORT).show();
                        DbHelper.getInstance().getDatabase(true).execSQL("UPDATE grupos SET votado_grupo = 0");
                        activity.gruposFragment.atualizar(imgFiltro.cursoAtual);
                    } else {
                        Toast.makeText(activity, limpar.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case R.id.menu_faq:
                Intent i = new Intent(activity, SobreActivity.class);
                activity.startActivity(i);
                return;
        }
    }
}
