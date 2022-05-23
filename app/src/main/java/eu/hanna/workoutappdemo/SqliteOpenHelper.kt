package eu.hanna.workoutappdemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class SqliteOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "SevenMinutesWorkout.db"
        private val TABLE_HISTORY = "History"

        private val COLUMN_ID = "_id"
        private val COLUMN_COMPLETED_DATE = "completed_date"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HISTORY_TABLE = ("CREATE TABLE " + TABLE_HISTORY + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_COMPLETED_DATE + " TEXT" + ")")
        db?.execSQL(CREATE_HISTORY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + TABLE_HISTORY)
        onCreate(db) // Calls the onCreate function so all the update tables will be created
    }

    fun addData (date:String){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_COMPLETED_DATE,date)
        db.insert(TABLE_HISTORY,null,values)
        db.close()
    }

    // Getting the list of completed dates from the History Table
    fun getAllCompletedDateList () : ArrayList<String> {

        val list: ArrayList<String> = ArrayList<String>()
        val selectQuery = "SELECT * FROM $TABLE_HISTORY"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery( selectQuery,null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var date:String
      /*  if (cursor.moveToFirst()) {
            do {
                date = cursor.getString(cursor.getColumnIndex(COLUMN_COMPLETED_DATE))
                list.add(date)
            } while(cursor.moveToNext())

        } */

        // Move the cursor to the next row
        while (cursor.moveToNext()) {
            val dateValue = (cursor.getString(cursor.getColumnIndex(COLUMN_COMPLETED_DATE)))
            // Returns the zero-base index for the given column name, or -1 if the column is not exist
            list.add(dateValue) // value is added in the list
        }
        cursor.close()
        return list

    }
}