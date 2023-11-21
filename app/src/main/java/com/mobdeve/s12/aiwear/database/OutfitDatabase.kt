package com.mobdeve.s12.aiwear.database

import OutfitDatabaseHandler
import OutfitModel
import android.content.ContentValues
import android.content.Context
import com.mobdeve.s12.aiwear.models.UserModel

class OutfitDatabase (context: Context) {
    private lateinit var databaseHandler: OutfitDatabaseHandler

    init {
        this.databaseHandler = OutfitDatabaseHandler(context)
    }

    fun addOutfit(outfit: OutfitModel): Long {
        val db = databaseHandler.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(OutfitDatabaseHandler.OUTFIT_USER_UID, outfit.user_uuid)
        contentValues.put(OutfitDatabaseHandler.OUTFIT_SCHEDULE_DATE, outfit.user_uuid)
        contentValues.put(OutfitDatabaseHandler.OUTFIT_CLOTHES_ITEMS, outfit.formatClothesId())

        val outfit_id = db.insert(OutfitDatabaseHandler.OUTFIT_TABLE, null, contentValues)
        db.close()

        return outfit_id
    }

    fun updateOutfit(outfit: OutfitModel) {
        val db = databaseHandler.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(OutfitDatabaseHandler.OUTFIT_SCHEDULE_DATE, outfit.user_uuid)
        contentValues.put(OutfitDatabaseHandler.OUTFIT_CLOTHES_ITEMS, outfit.formatClothesId())

        val selection = "${OutfitDatabaseHandler.OUTFIT_ID} = ?"
        val selectionArgs = arrayOf(outfit.outfit_id.toString())

        db.update(OutfitDatabaseHandler.OUTFIT_TABLE, contentValues, selection, selectionArgs)
        db.close()
    }

    fun deleteOutfit(outfit: OutfitModel) {
        val db = databaseHandler.writableDatabase
        val selection = "${OutfitDatabaseHandler.OUTFIT_ID} = ?"
        val selectionArgs = arrayOf(outfit.outfit_id.toString())

        db.delete(OutfitDatabaseHandler.OUTFIT_TABLE, selection, selectionArgs)
        db.close()
    }

    fun queryOutfitById(outfit_id: String): ArrayList<OutfitModel> {
        val db = databaseHandler.readableDatabase
        val outfits = ArrayList<OutfitModel>()

        val selection = "${OutfitDatabaseHandler.OUTFIT_ID} = ?"
        val selectionArgs = arrayOf(outfit_id)

        val cursor = db.query(
            OutfitDatabaseHandler.OUTFIT_TABLE,
            null,  // Projection, null returns all columns
            selection,
            selectionArgs,
            null,  // Group by
            null,  // Having
            null   // Order by
        )

        while(cursor.moveToNext()) {
            var outfit = OutfitModel(
                cursor.getLong(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_USER_UID)),
                UserModel.DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_SCHEDULE_DATE))),
            )
            outfit.delimitted_clothes = cursor.getString(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_CLOTHES_ITEMS))
            outfit.parseClothesId()
            outfits.add(outfit)
        }

        return outfits
    }

    fun queryOutfitByDate(date: String): ArrayList<OutfitModel> {
        val db = databaseHandler.readableDatabase
        val outfits = ArrayList<OutfitModel>()

        val selection = "${OutfitDatabaseHandler.OUTFIT_SCHEDULE_DATE} = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.query(
            OutfitDatabaseHandler.OUTFIT_TABLE,
            null,  // Projection, null returns all columns
            selection,
            selectionArgs,
            null,  // Group by
            null,  // Having
            null   // Order by
        )

        while(cursor.moveToNext()) {
            var outfit = OutfitModel(
                cursor.getLong(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_USER_UID)),
                UserModel.DATE_FORMAT.parse(date),
            )
            outfit.delimitted_clothes = cursor.getString(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_CLOTHES_ITEMS))
            outfit.parseClothesId()
            outfits.add(outfit)
        }

        return outfits
    }

    fun queryAllOutfits(user_uuid: String): ArrayList<OutfitModel> {
        val db = databaseHandler.readableDatabase
        val outfits = ArrayList<OutfitModel>()

        val selection = "${OutfitDatabaseHandler.OUTFIT_USER_UID} = ?"
        val selectionArgs = arrayOf(user_uuid)

        val cursor = db.query(
            OutfitDatabaseHandler.OUTFIT_TABLE,
            null,  // Projection, null returns all columns
            selection,
            selectionArgs,
            null,  // Group by
            null,  // Having
            null   // Order by
        )

        while(cursor.moveToNext()) {
            var outfit = OutfitModel(
                cursor.getLong(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_USER_UID)),
                UserModel.DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_SCHEDULE_DATE))),
            )
            outfit.delimitted_clothes = cursor.getString(cursor.getColumnIndexOrThrow(OutfitDatabaseHandler.OUTFIT_CLOTHES_ITEMS))
            outfit.parseClothesId()
            outfits.add(outfit)
        }

        return outfits
    }
}
