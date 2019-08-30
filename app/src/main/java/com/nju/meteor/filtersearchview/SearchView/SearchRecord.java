package com.nju.meteor.filtersearchview.SearchView;

import org.litepal.crud.LitePalSupport;

/**
 * 记录搜索过程中的数据，提供搜索提示
 */
public class SearchRecord extends LitePalSupport {
    private String value;
    private long time;
    private String classType;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public SearchRecord(String value, long time, String classType) {
        this.value = value;
        this.time = time;
        this.classType = classType;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }
}
