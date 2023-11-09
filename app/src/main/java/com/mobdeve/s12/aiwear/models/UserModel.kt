package com.mobdeve.s12.aiwear.models

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import java.util.Date

@Entity
data class UserModel(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "displayName") var displayName: String,
    @ColumnInfo(name = "bio") var bio: String,
    @ColumnInfo(name = "gender") var gender: String,
    @ColumnInfo(name = "birthday") var birthday: Date
)

@Dao
interface UserDao {
    @Query("SELECT * FROM userModel")
    fun getAll(): List<UserModel>

    @Query("SELECT * FROM userModel WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<UserModel>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: UserModel)

    @Delete
    fun delete(user: UserModel)
}

@Database(entities = [UserModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}