package com.example.nekretninefinalni.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.nekretninefinalni.db.model.Nekretnina;
import com.example.nekretninefinalni.db.model.Slike;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "nekretnine.final.final.";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private Dao<Nekretnina, Integer> getmNekretnina = null;
    private Dao<Slike, Integer> getmSlike = null;

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Nekretnina.class);
            TableUtils.createTable(connectionSource, Slike.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Slike.class, true);
            TableUtils.dropTable(connectionSource, Nekretnina.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Nekretnina, Integer> getNekretnina() throws SQLException {
        if (getmNekretnina == null) {
            getmNekretnina = getDao(Nekretnina.class);
        }
        return getmNekretnina;
    }

    public Dao<Slike, Integer> getSlike() throws SQLException {
        if (getmSlike == null) {
            getmSlike = getDao(Slike.class);
        }
        return getmSlike;
    }

    @Override
    public void close() {
        getmSlike = null;
        getmNekretnina = null;
        super.close();
    }
}
