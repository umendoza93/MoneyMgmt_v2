package com.example.moneymgmt_v2;

public class ActivityModel {
    public String atyDate, atyCat;
    public double atyPrice;

    public String toSQL() {
        StringBuilder sb = new StringBuilder("(");
        sb.append("\"").append(atyDate).append("\"").append(",");
        sb.append(atyPrice).append(",");
        sb.append("\"").append(atyCat).append("\"").append(")");
        return sb.toString();
    }
}
