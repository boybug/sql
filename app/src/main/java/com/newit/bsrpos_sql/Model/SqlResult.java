package com.newit.bsrpos_sql.Model;

public class SqlResult {
    private int iden;
    private String msg;

    public SqlResult() {
    }

    public SqlResult(int iden, String msg) {
        this.iden = iden;
        this.msg = msg;
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
