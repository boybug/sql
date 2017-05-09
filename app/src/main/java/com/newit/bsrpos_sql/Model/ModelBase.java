package com.newit.bsrpos_sql.Model;

import java.io.Serializable;

public abstract class ModelBase implements Serializable {
    private RecordStat recordStat;

    public ModelBase(boolean isNew) {
        recordStat = isNew ? RecordStat.I : RecordStat.NULL;
    }

    public RecordStat getRecordStat() {
        return recordStat;
    }

    public void setRecordStat(RecordStat recordStat) {
        this.recordStat = recordStat;
    }

    protected void updateRecordStat() {
        this.setRecordStat(getRecordStat() == RecordStat.NULL ? RecordStat.U : getRecordStat());
    }

    public abstract String getSearchString();
}


