package com.example.alpha;

/**
 * a user class
 * contains all of the data required for a user.
 */
public class User {
    private String mail, pass, uid, image;

    /**
     * an empty builder.
     * this function is not used, but is required in order to use Firebase.
     */
    public User(){

    }

    /**
     * User class builder.
     * this function gets all of the variables that are required in order to assemble a user
     * @param mail
     * @param pass
     * @param uid
     * @param image
     */
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
