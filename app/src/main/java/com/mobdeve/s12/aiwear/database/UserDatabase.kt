package com.mobdeve.s12.aiwear.database

import android.content.ContentValues
import android.content.Context
import com.mobdeve.s12.aiwear.models.UserModel
import java.util.Date

class UserDatabase (context: Context){
    private lateinit var databaseHandler: UserDatabaseHandler

    init {
        this.databaseHandler = UserDatabaseHandler(context)
    }

    fun addUser(user: UserModel) {
        val db = databaseHandler.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(UserDatabaseHandler.USER_UUID, user.uuid)
        contentValues.put(UserDatabaseHandler.USER_USERNAME, user.userName)
        contentValues.put(UserDatabaseHandler.USER_DISPLAY_NAME, user.displayName)
        contentValues.put(UserDatabaseHandler.USER_BIO, user.bio)
        contentValues.put(UserDatabaseHandler.USER_GENDER, user.gender)
        contentValues.put(UserDatabaseHandler.USER_BIRTHDAY, UserModel.DATE_FORMAT.format(user.birthday))

        db.insert(UserDatabaseHandler.USER_TABLE, null, contentValues)
        db.close()
    }

    fun updateUser(user: UserModel) {
        val db = databaseHandler.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(UserDatabaseHandler.USER_USERNAME, user.userName)
        contentValues.put(UserDatabaseHandler.USER_DISPLAY_NAME, user.displayName)
        contentValues.put(UserDatabaseHandler.USER_BIO, user.bio)
        contentValues.put(UserDatabaseHandler.USER_GENDER, user.gender)
        contentValues.put(UserDatabaseHandler.USER_BIRTHDAY, UserModel.DATE_FORMAT.format(user.birthday))

        val selection = "${UserDatabaseHandler.USER_UUID} = ?"
        val selectionArgs = arrayOf(user.uuid)

        db.update(UserDatabaseHandler.USER_TABLE, contentValues, selection, selectionArgs)
        db.close()
    }

    fun deleteUserByUUID(uuid: String) {
        val db = databaseHandler.writableDatabase
        val selection = "${UserDatabaseHandler.USER_UUID} = ?"
        val selectionArgs = arrayOf(uuid)

        db.delete(UserDatabaseHandler.USER_TABLE, selection, selectionArgs)
        db.close()
    }

    fun deleteUserByUSERNAME(username: String) {
        val db = databaseHandler.writableDatabase
        val selection = "${UserDatabaseHandler.USER_USERNAME} = ?"
        val selectionArgs = arrayOf(username)

        db.delete(UserDatabaseHandler.USER_TABLE, selection, selectionArgs)
        db.close()
    }

    fun queryUserByUUID(uuid: String): UserModel?  {
        val db = databaseHandler.readableDatabase
        val selection = "${UserDatabaseHandler.USER_UUID} = ?"
        val selectionArgs = arrayOf(uuid)

        val cursor = db.query(
            UserDatabaseHandler.USER_TABLE,
            null,  // Projection, null returns all columns
            selection,
            selectionArgs,
            null,  // Group by
            null,  // Having
            null   // Order by
        )

        var user: UserModel? = if (cursor.moveToFirst()) {
            UserModel(
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_UUID)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_BIO)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_GENDER)),
                UserModel.DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_BIRTHDAY)))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return user
    }

    private fun queryUserByUSERNAME(username: String): UserModel? {
        val db = databaseHandler.readableDatabase
        val selection = "${UserDatabaseHandler.USER_USERNAME} = ?"
        val selectionArgs = arrayOf(username)

        val cursor = db.query(
            UserDatabaseHandler.USER_TABLE,
            null,  // Projection, null returns all columns
            selection,
            selectionArgs,
            null,  // Group by
            null,  // Having
            null   // Order by
        )

        var user: UserModel? = null

        if (cursor.moveToFirst()) {
            user = UserModel(
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_UUID)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_BIO)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_GENDER)),
                UserModel.DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHandler.USER_BIRTHDAY)))
            )
        }

        cursor.close()
        db.close()
        return user
    }
    fun isUniqueUsername(username: String): Boolean {
        return this.queryUserByUSERNAME(username) == null
    }

}