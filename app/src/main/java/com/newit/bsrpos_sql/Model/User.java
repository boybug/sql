package com.newit.bsrpos_sql.Model;


public class User extends ModelBase {

    private int id;
    private String login;
    private String name;
    private boolean admin;
    private boolean deleteorder;
    private String password;
    private String email;

    public User(int id, String login, String name, boolean admin, boolean deleteorder, String password, String email) {
        super(false);
        this.id = id;
        this.login = login;
        this.name = name;
        this.deleteorder = deleteorder;
        this.admin = admin;
        this.password = password;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public boolean isDeleteorder() {
        return deleteorder;
    }

    public void setDeleteorder(boolean deleteorder) {
        this.deleteorder = deleteorder;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getSearchString() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
