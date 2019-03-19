package com.example.nekretninefinalni.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Slike.TABLE_SLIKEE)
public class Slike {

    public static final String TABLE_SLIKEE = "slikee";
    private static final String FIELD_ID ="id";
    private static final String FIELD_SLIKE ="slike";
    private static final String FIELD_NEKRETNINA = "nekretnina";

    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_SLIKE)
    private String slike;

    @DatabaseField(columnName = FIELD_NEKRETNINA, foreign = true, foreignAutoRefresh = true)
    private Nekretnina nekretnina;

    public Slike() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlike() {
        return slike;
    }

    public void setSlike(String slike) {
        this.slike = slike;
    }

    public Nekretnina getNekretnina() {
        return nekretnina;
    }

    public void setNekretnina(Nekretnina nekretnina) {
        this.nekretnina = nekretnina;
    }

    public String toString() {
        return slike;
    }
}
