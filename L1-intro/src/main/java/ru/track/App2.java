package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;


public class App2 {

    public static final String URL = "http://guarded-mesa-31536.herokuapp.com/track";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_GITHUB = "github";
    public static final String FIELD_EMAIL = "email";
    public static boolean success = false;

    public static void main(String[] args) throws Exception {
        HttpResponse<JsonNode> Response = Unirest.post(URL)
                .field (FIELD_NAME,"Kirill")
                .field (FIELD_GITHUB, "https://github.com/inedostoev")
                .field (FIELD_EMAIL, "inedostoev@mail.ru")
                .asJson();
        success = (Boolean) Response.getBody().getObject().get("success");
        System.out.println(success);
    }

}