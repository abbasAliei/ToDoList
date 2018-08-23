package com.abbas.aliei.todo.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.abbas.aliei.todo.datamodel.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abbas on 19/05/2018.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBOpenHelper";
    private static final String DATABASE_NAME = "ToDoApp";
    private static final int DATABASE_VERSION = 1;

    private static final String NOTE_TABLE_NAME = "note";
    private static final String ID_FIELD_NAME = "id";
    private static final String TITLE_FIELD_NAME = "title";
    private static final String DESCRIPTION_FIELD_NAME = "description";

    private static final String SQL_COMMAND_FOR_CREATE_NOTE_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTE_TABLE_NAME + " (" +
            ID_FIELD_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE_FIELD_NAME + " TEXT, " +
            DESCRIPTION_FIELD_NAME + " TEXT );";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(SQL_COMMAND_FOR_CREATE_NOTE_TABLE);
        }catch (SQLiteException e){
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long addNote(Note note){
        ContentValues cv = new ContentValues();
        cv.put(TITLE_FIELD_NAME,note.getTitle());
        cv.put(DESCRIPTION_FIELD_NAME,note.getDescription());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long isInserted = sqLiteDatabase.insert(NOTE_TABLE_NAME,null,cv);
        sqLiteDatabase.close();
        if (isInserted > 0){
            return isInserted;
        }else{
            return 0;
        }
    }

    public boolean updateNote(Note note){
        ContentValues cv = new ContentValues();
        cv.put(ID_FIELD_NAME, note.getId());
        cv.put(TITLE_FIELD_NAME, note.getTitle());
        cv.put(DESCRIPTION_FIELD_NAME, note.getDescription());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long isUpdated = sqLiteDatabase.update(NOTE_TABLE_NAME, cv, ID_FIELD_NAME + " = " + note.getId(), null);
        if (isUpdated > 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean removeNote(int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long isDeleted = sqLiteDatabase.delete(NOTE_TABLE_NAME,ID_FIELD_NAME + "=" + id,null);
        sqLiteDatabase.close();
        if (isDeleted > 0){
            return true;
        }else{
            return false;
        }
    }

    public void removeNotes(List<Integer> ids){
        for (int i = 0; i < ids.size(); i++) {
            removeNote(ids.get(i));
        }
    }

    public List<Note> getNotes(){
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + NOTE_TABLE_NAME,null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDescription(cursor.getString(2));
                notes.add(note);
            }while(cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
        }
        return notes;
    }
}
