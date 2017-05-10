package com.newit.bsrpos_sql.Model;


import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class User extends ModelBase {

    private int id;
    private String login;
    private String name;
    private boolean admin;
    private boolean deleteorder;

    public User(int id, String login, String name, boolean admin, boolean deleteorder) {
        super(false);
        this.id = id;
        this.login = login;
        this.name = name;
        this.deleteorder = deleteorder;
        this.admin = admin;
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

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public String getSearchString() {
        return name;
    }

    public static List<User> retrieve(List<User> users) {
        users.clear();
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getuser(?,?)}", new String[]{Global.user.getLogin(), String.valueOf(Global.user.isAdmin())});
            while (rs != null && rs.next()) {
                User user = new User(rs.getInt("usr_Id"), rs.getString("login_name"), rs.getString("usr_name"), rs.getBoolean("admin"), rs.getBoolean("deleteorder"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public SqlResult delete() {
        SqlResult result = new SqlResult();
        if (this != Global.user && Global.user.isAdmin()) {
            try {
                ResultSet rs = SqlServer.execute("{call POS.dbo.deleteuser(?,?)}", new String[]{login, String.valueOf(id)});
                if (rs != null && rs.next()) {
                    result.setIden(rs.getInt("Iden"));
                    if (result.getIden() < 0) result.setMsg("ไม่ได้รับคำตอบจาก server");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                result.setMsg("ไม่สามารถเชื่อมต่อกับ server");
            }
        }
        return result;
    }
}
