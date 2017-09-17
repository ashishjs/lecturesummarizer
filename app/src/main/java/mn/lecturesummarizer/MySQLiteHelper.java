package mn.lecturesummarizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by anand on 9/16/2017.
 */

import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private  static final int DATABASE_VERSION = 1;
    private  static final String DATABASE_NAME = "LectureNotesDB";


    public MySQLiteHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_LECTURE_TABLE = "CREATE TABLE lecturenotes ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "title TEXT," +
                "transcript TEXT, " + "summary TEXT )";
        // creating lectures notes table
        sqLiteDatabase.execSQL(CREATE_LECTURE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS lecturenotes");
        this.onCreate(sqLiteDatabase);
    }

    // Crud Operations to add and delete the data from lecturenotes

    private static final String LECTURE_NOTES = "lecturenotes";

    private  static final String KEY_ID = "id";

    private  static  final String KEY_TITLE = "title";

    private  static  final String KEY_TRANSCRIPT = "transcript";

    private  static  final  String KEY_SUMMARY = "summary";

    private  static  final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_TRANSCRIPT,KEY_SUMMARY};

    public void addLecture(LectureNotes myLectureNotes){
        Log.d("Adding Lecture ", myLectureNotes.toString());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE,myLectureNotes.getTitle());
        values.put(KEY_TRANSCRIPT,myLectureNotes.getTranscript());
        values.put(KEY_SUMMARY,myLectureNotes.getSummary());

        //inserting the values to database
        sqLiteDatabase.insert(LECTURE_NOTES,
                null,
                values);
        sqLiteDatabase.close();
        for(int i = 0;i<1000;i++){
            Log.d("Adding Lecture ", myLectureNotes.toString());

        }
    }

    public  LectureNotes getLecture(int id){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();


        Cursor cursor = sqLiteDatabase.query(LECTURE_NOTES,
                COLUMNS,
                " id = ?",
                new String[] {String.valueOf(id) },
                null,
                null,
                null,
                null
                );

        if(cursor != null)
            cursor.moveToFirst();

        LectureNotes notes = new LectureNotes();
        notes.setId(Integer.parseInt(cursor.getString(0)));
        notes.setTitle(cursor.getString(1));
        notes.setTranscript(cursor.getString(2));
        notes.setSummary(cursor.getString(3));
        Log.d("Data :: ", notes.toString());
        return notes;

    }

    public List<LectureNotes> getAllNotes(){
        List<LectureNotes> myNotes = new LinkedList<LectureNotes>();

        String query = "SELECT * FROM " + LECTURE_NOTES;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);

        LectureNotes note = null;

        if(cursor.moveToFirst()){
            do{
                note = new LectureNotes();
                note.setId(Integer.parseInt(cursor.getString(0)));
                Log.d("All notes",cursor.getString(0).toString());
                note.setTitle(cursor.getString(1));
                Log.d("Poudel",cursor.getString(1));
                note.setTranscript(cursor.getString(2));
                Log.d("Jayan",cursor.getString(2));
                note.setSummary(cursor.getString(3));
                Log.d("Rickert",cursor.getString(3));
                myNotes.add(note);
            }
            while (cursor.moveToNext());
        }
        return myNotes;
    }

}
