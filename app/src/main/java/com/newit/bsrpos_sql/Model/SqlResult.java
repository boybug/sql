package com.newit.bsrpos_sql.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlResult {
    private int iden;
    private String msg;

    public SqlResult() {
    }

    public SqlResult(ResultSet rs) throws SQLException {
        this.iden = rs.getInt("Iden");
        this.msg = rs.getString("Msg");
    }

    public int getIden() {
        return iden;
    }

    public void setIden(int iden) {
        this.iden = iden;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
