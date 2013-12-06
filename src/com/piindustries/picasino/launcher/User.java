package com.piindustries.picasino.launcher;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private boolean authenticated;

    /**
     * Create a basic user with only a username and password.
     *
     * @param u username of user.
     * @param p password for user.
     */
    public User(String u, String p){
        username = u;
        password = p;
        firstName = "";
        lastName = "";
        email = "";
    }

    /**
     * Create a complete user with all fields provided.
     *
     * @param u username of user.
     * @param p password for user.
     * @param f first name of user.
     * @param l last name of user.
     * @param e email of user.
     */
    public User(String u, String p, String f, String l, String e){
        username = u;
        password = p;
        firstName = f;
        lastName = l;
        email = e;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAuthenticated(){
        return authenticated;
    }

    public void setAuthenticated(boolean b){
        authenticated = b;
    }

}

