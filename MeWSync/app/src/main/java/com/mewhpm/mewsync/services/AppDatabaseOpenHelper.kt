package com.mewhpm.mewsync.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class AppDatabaseOpenHelper (ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MeWSync", null, 1) {
    companion object {
        private var instance: AppDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDatabaseOpenHelper {
            if (instance == null) {
                instance = AppDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(
            tableName = "BleDevices",
            ifNotExists = true,
            columns = *arrayOf(
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "mac" to TEXT + UNIQUE,
                "name" to TEXT
            )
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("BleDevices", true)
    }
}

val Context.database: AppDatabaseOpenHelper
    get() = AppDatabaseOpenHelper.getInstance(applicationContext)