package com.example.alpha;

public class User {
    private String mail, pass, uid, image;

    public User(){

    }

    public User(String mail, String pass, String uid, String image){
        this.mail=mail;
        this.pass=pass;
        this.uid=uid;
        this.image=image;
    }

    public String getMail(){return mail;}
    public void setMail(String mail){this.mail=mail;}

    public String getPass(){return pass;}
    public void setPass(String pass){this.pass=pass;}

    public String getUid(){return uid;}
    public void setUid(String uid){this.uid=uid;}

    public String getImage(){return image;}
    public void setImage(String image){this.image=image;}
}
