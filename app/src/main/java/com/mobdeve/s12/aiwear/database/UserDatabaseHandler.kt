package com.mobdeve.s12.aiwear.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHandler(context: Context): SQLiteOpenHelper (context, this.DATABASE_NAME, null, this.DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserDatabase"
        const val USER_TABLE = "user_table"

        const val USER_UUID = "user_uuid"
        const val USER_USERNAME = "user_username"
        const val USER_DISPLAY_NAME = "user_display_name"
        const val USER_BIO = "user_bio"
        const val USER_GENDER = "user_gender"
        const val USER_BIRTHDAY = "user_birthday"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USER_TABLE = ("CREATE TABLE IF NOT EXISTS " + USER_TABLE + " ("
                + USER_UUID + " VARCHAR(128) PRIMARY KEY, " +
                USER_USERNAME + " VARCHAR(32)," +
                USER_DISPLAY_NAME + " TEXT," +
                USER_BIO + " TEXT," +
                USER_GENDER + " TEXT," +
                USER_BIRTHDAY + " TEXT" +
                ")")
        db?.execSQL(CREATE_USER_TABLE)

        // option to add existing user info
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $USER_TABLE")
        onCreate(db)
    }
}