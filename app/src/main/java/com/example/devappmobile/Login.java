package com.example.devappmobile;

public class Login {

    private String grant_type ;
    private String  scope;
    private String client_id;
    private String client_secret;
    private String username;
    private String password;
    private String refresh_token;


    public Login(String grant_type, String scope, String client_id , String client_secret, String username, String password){

        this.grant_type=grant_type ;
        this.scope=scope;
        this.client_id=client_id;
        this.client_secret=client_secret;
        this.username=username;
        this.password=password;

    }

    public Login(String grant_type){

        this.refresh_token=grant_type;
        this.grant_type="refresh_token" ;
        this.scope="*";
        this.client_id="WMZNSSETCJDPTZSVETRNOPGYFKMAKHHQ";
        this.client_secret="5813427175e8e5d18452a90035077331";

    }
}
