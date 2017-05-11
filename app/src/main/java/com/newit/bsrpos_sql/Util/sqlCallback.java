package com.newit.bsrpos_sql.Util;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface sqlCallback {
    void processFinish(ResultSet rs, int tag) throws SQLException;
}
