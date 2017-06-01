package com.lakala.library;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by LMQ on 2015/11/25.
 */
public interface OnUpgradeListener {
    void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2);
}
