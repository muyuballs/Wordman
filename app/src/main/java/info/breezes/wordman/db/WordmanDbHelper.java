package info.breezes.wordman.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import info.breezes.orm.OrmSQLiteHelper;
import info.breezes.orm.utils.TableUtils;

/**
 * Created by jianxingqiao on 14-6-6.
 */
public class WordmanDbHelper extends OrmSQLiteHelper {

    public WordmanDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableUtils.createTable(db,ClassTable.class);
        TableUtils.createTable(db,WordTable.class);
        TableUtils.createTable(db,ClasswordsTable.class);
        TableUtils.createTable(db,StudyRecord.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
