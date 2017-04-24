package com.alsan_grand_lyon.aslangrandlyon.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nico on 24/04/2017.
 */

public abstract class DAOBase {

    //TODO Si je décide de la mettre à jour, il faudra changer cet attribut
    protected final static int VERSION = 1;

    // Le nom du fichier qui représente ma base
    protected final static String NOM = "aslan_grand_lyon.db";

    protected SQLiteDatabase mDb = null;
    protected DatabaseHandler mHandler = null;

    public DAOBase(Context context) {
        this.mHandler = new DatabaseHandler(context, NOM, null, VERSION);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        mDb = mHandler.getWritableDatabase();
        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }
}