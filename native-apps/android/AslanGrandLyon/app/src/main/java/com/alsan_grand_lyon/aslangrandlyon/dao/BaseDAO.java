package com.alsan_grand_lyon.aslangrandlyon.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nico on 24/04/2017.
 */

public abstract class BaseDAO {

    //TODO Si je décide de la mettre à jour, il faudra changer cet attribut
    protected final static int VERSION = 1;

    // Le nom du fichier qui représente ma base
    protected final static String NOM = "aslan_grand_lyon.db";

    protected SQLiteDatabase sqLiteDatabase = null;
    protected DatabaseHandler databaseHandler = null;

    public BaseDAO(Context context) {
        this.databaseHandler = new DatabaseHandler(context, NOM, null, VERSION);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        sqLiteDatabase = databaseHandler.getWritableDatabase();
        return sqLiteDatabase;
    }

    public void close() {
        sqLiteDatabase.close();
    }

    public SQLiteDatabase getDb() {
        return sqLiteDatabase;
    }
}