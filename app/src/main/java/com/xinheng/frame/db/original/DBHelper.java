package com.xinheng.frame.db.original;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.xinheng.AppDroid;
import com.xinheng.util.SPDBHelper;

/**
 * [description about this class]
 * 轻量级数据库的封装
 * @author jack
 * @version [DaxingFrame, 2016/03/09 14:59]
 */

public class DBHelper {

    private DataBaseHelper dbHelper;
    private SQLiteDatabase writableDB;
    private SQLiteDatabase readableDB;
    /** 数据库名称 */
    private static final String DATABASE_NAME = "project.db";
    /** 数据库版本 */
    private static final int DATABASE_VERSION = 1;

    public DBHelper(){
        dbHelper = new DataBaseHelper(AppDroid.getInstance().getApplicationContext());
    }

    /**
     * 获取数据库操作对象
     *
     * @return SQLiteDatabase
     */
    public synchronized SQLiteDatabase getWritableSQLiteDatabase()
    {
        writableDB = dbHelper.getWritableDatabase();
        return writableDB;
    }

    /**
     * 获取数据库操作对象
     *
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getReadableSQLiteDatabase() {
        readableDB = dbHelper.getReadableDatabase();
        return readableDB;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if(writableDB != null && writableDB.isOpen()){
            writableDB.close();
        }
        if(readableDB != null && readableDB.isOpen()){
            writableDB.close();
        }
        writableDB = null;
        readableDB = null;
        if (dbHelper != null)
        {
            dbHelper.close();
        }
    }

    public class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * 在第一次使用数据库时会调用该方法生成相应的数据库表,此方法不是在实例化SQLiteOpenHelper时调用,
         * 而是通过对象调用了getReadableDatabase()或者getWritableDatabase()
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.beginTransaction();
            try {
                /**
                 * (这里如果需要建立其他数据库可以在这里实现)
                 * 生成数据库
                 */
                db.execSQL(SPDBHelper.TABLE_CREATE_SQL);
                db.setTransactionSuccessful();

            }catch (Exception e){

            }finally {
                db.endTransaction();
            }
        }

        /**
         * 当数据库需要进行升级时会调用此方法,一般可以在此方法中将数据表删除,
         * 并且在删除之后往往会调用onCreate重新创建新的数据表
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP DATABASE IF EXISTS " + DATABASE_NAME);
            onCreate(db);
        }
    }
}
