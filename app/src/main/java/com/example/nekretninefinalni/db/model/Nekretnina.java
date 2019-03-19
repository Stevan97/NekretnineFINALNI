package com.example.nekretninefinalni.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Nekretnina.TABLE_NEKR)
public class Nekretnina {

    public static final String TABLE_NEKR = "nekr";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAZIV = "naziv";
    private static final String FIELD_OPIS = "opis";
    private static final String FIELD_ADRESA = "adresa";
    private static final String FIELD_BROJ_TELEFONA = "brojTelefona";
    private static final String FIELD_KVADRATURA = "kvadratura";
    private static final String FIELD_BROJ_SOBE = "brojSobe";
    private static final String FIELD_CENA = "cenaNekretnine";
    private static final String FIELD_SLIKE = "slike";

    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NAZIV)
    private String naziv;

    @DatabaseField(columnName = FIELD_OPIS)
    private String opis;

    @DatabaseField(columnName = FIELD_ADRESA)
    private String adresa;

    @DatabaseField(columnName = FIELD_BROJ_TELEFONA)
    private int brojTelefona;

    @DatabaseField(columnName = FIELD_KVADRATURA)
    private double kvadratura;

    @DatabaseField(columnName = FIELD_BROJ_SOBE)
    private int brojSobe;

    @DatabaseField(columnName = FIELD_CENA)
    private double cena;

    @ForeignCollectionField(columnName = FIELD_SLIKE, eager = true)
    private ForeignCollection<Slike> slike;

    public Nekretnina() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public int getBrojTelefona() {
        return brojTelefona;
    }

    public void setBrojTelefona(int brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    public double getKvadratura() {
        return kvadratura;
    }

    public void setKvadratura(double kvadratura) {
        this.kvadratura = kvadratura;
    }

    public int getBrojSobe() {
        return brojSobe;
    }

    public void setBrojSobe(int brojSobe) {
        this.brojSobe = brojSobe;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public ForeignCollection<Slike> getSlike() {
        return slike;
    }

    public void setSlike(ForeignCollection<Slike> slike) {
        this.slike = slike;
    }

    public String toString() {
        return naziv;
    }

}
