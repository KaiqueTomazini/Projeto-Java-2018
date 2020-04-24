package com.userdev.winnerstars.utils;

public interface Constants {


    String PACKAGE = "com.userdev.winnerstars";
    String DATABASE_NOME = "winnerstarsdb";

    String WEBSERVICE_DIRETORIO = "ws/";
    String WEBSERVICE_BRUNO_LAN = "http://192.168.0.100/" + WEBSERVICE_DIRETORIO;
    String WEBSERVICE_BRUNO_WAN = "http://brunight.servegame.com:25565/" + WEBSERVICE_DIRETORIO;
    String WEBSERVICE_ROUTER_BRUNO = "http://192.168.43.53/" + WEBSERVICE_DIRETORIO;
    String WEBSERVICE_AP_WINDOWS = "http://192.168.137.1/" + WEBSERVICE_DIRETORIO;
    String WEBSERVICE_EMULADOR = "http://10.0.2.2:25565/" + WEBSERVICE_DIRETORIO;
    String WEBSERVICE_FINAL = "http://10.66.10.45/" + WEBSERVICE_DIRETORIO;

    String PREFS_NAME = PACKAGE + "_preferences";
    String PREF_TOUR_COMPLETE = "pref_tour_complete";
    String PREF_REGISTRADO = "pref_registrado";
    String PREF_USUARIO_JSON = "pref_usuario_json";
    String PREF_ULTIMO_WEBSERVICE = "pref_ultimo_webserver";

    String WEBSERVICE_DIRETORIO_QUERIES_APP = "queries/app/";
    String QUERY_ADD_ALUNO = WEBSERVICE_DIRETORIO_QUERIES_APP + "add_aluno.php";
    String QUERY_ADD_APP = WEBSERVICE_DIRETORIO_QUERIES_APP + "add_app.php";
    String QUERY_ADD_CURSO = WEBSERVICE_DIRETORIO_QUERIES_APP + "add_curso.php";
    String QUERY_ADD_FEEDBACK_GRUPO = WEBSERVICE_DIRETORIO_QUERIES_APP + "add_feedback_grupo.php";
    String QUERY_ADD_FOTO_GALERIA = WEBSERVICE_DIRETORIO_QUERIES_APP + "add_foto_galeria.php";
    String QUERY_ADD_GRUPO = WEBSERVICE_DIRETORIO_QUERIES_APP + "add_grupo.php";
    String QUERY_REGISTRAR = WEBSERVICE_DIRETORIO_QUERIES_APP + "registrar.php";
    String QUERY_VOTAR = WEBSERVICE_DIRETORIO_QUERIES_APP + "votar.php";
    String QUERY_DEL_ALUNO = WEBSERVICE_DIRETORIO_QUERIES_APP + "del_aluno.php";
    String QUERY_DEL_GRUPO = WEBSERVICE_DIRETORIO_QUERIES_APP + "del_grupo.php";
    String QUERY_GET_ALUNOS_GRUPO = WEBSERVICE_DIRETORIO_QUERIES_APP + "get_alunos_grupo.php";
    String QUERY_GET_CURSOS = WEBSERVICE_DIRETORIO_QUERIES_APP + "get_cursos.php";
    String QUERY_FAQ = WEBSERVICE_DIRETORIO_QUERIES_APP + "faq.php";
    String QUERY_GET_FOTOS_GALERIA = WEBSERVICE_DIRETORIO_QUERIES_APP + "get_fotos_galeria.php";
    String QUERY_GET_GRUPOS = WEBSERVICE_DIRETORIO_QUERIES_APP + "get_grupos.php";
    String QUERY_LOGAR = WEBSERVICE_DIRETORIO_QUERIES_APP + "logar.php";
    String QUERY_GET_ALUNO_QR = WEBSERVICE_DIRETORIO_QUERIES_APP + "get_aluno_qr.php";
    String QUERY_GET_DADOS_CRIAR_CRACHA = WEBSERVICE_DIRETORIO_QUERIES_APP + "get_dados_criar_cracha.php";
    String QUERY_GET_VOTOS_USUARIO = WEBSERVICE_DIRETORIO_QUERIES_APP + "get_votos_usuario.php";
    String QUERY_DEL_VOTOS_TUDO = WEBSERVICE_DIRETORIO_QUERIES_APP + "del_votos_tudo.php";
    String QUERY_SET_ALUNO = WEBSERVICE_DIRETORIO_QUERIES_APP + "set_aluno.php";
    String QUERY_SET_CURSO = WEBSERVICE_DIRETORIO_QUERIES_APP + "set_curso.php";
    String QUERY_SET_GRUPO = WEBSERVICE_DIRETORIO_QUERIES_APP + "set_grupo.php";
    String QUERY_UPLOAD_CRACHA = WEBSERVICE_DIRETORIO_QUERIES_APP + "upload_cracha.php";
    String QUERY_UPLOAD_FOTO_GALERIA = WEBSERVICE_DIRETORIO_QUERIES_APP + "upload_foto_galeria.php";
}
