import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OutfitDatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "OutfitDatabase"
        const val OUTFIT_TABLE = "outfits_table"

        // Columns
        const val OUTFIT_ID = "outfit_id"
        const val OUTFIT_USER_UID = "user_uid"
        const val OUTFIT_SCHEDULE_DATE = "schedule_date"
        const val OUTFIT_CLOTHES_ITEMS = "clothes_items"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_OUTFIT_TABLE = ("CREATE TABLE IF NOT EXISTS $OUTFIT_TABLE (" +
                "$OUTFIT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$OUTFIT_USER_UID TEXT, " +
                "$OUTFIT_SCHEDULE_DATE TEXT, " +
                "$OUTFIT_CLOTHES_ITEMS TEXT" +
                ")")
        db.execSQL(CREATE_OUTFIT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $OUTFIT_TABLE")
        onCreate(db)
    }
}
