package com.xinheng.util;

import android.content.ContentValues;
import android.database.Cursor;
import com.xinheng.frame.db.original.BaseDAO;
import com.xinheng.frame.interfaces.IDataBaseListener;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * [description about this class]
 * haredPreferences的数据库实现方式
 * @author jack
 */
public class SPDBHelper {

    public static final String TABLE_NAME = "shared_prefs";
    private static final String COLUMN_KEY = "key";
    private static final String COLUMN_VALUE = "value";

    /** 建表语句 */
    public static final String TABLE_CREATE_SQL = new StringBuilder()
            .append("CREATE TABLE ")
            .append(TABLE_NAME)
            .append("(")

            .append(COLUMN_KEY)
            .append(" TEXT,")

            .append(COLUMN_VALUE)
            .append(" TEXT")

            .append(")")
            .toString();

    private static final Executor mExecutor = Executors.newCachedThreadPool();

    /** 数据库操作对象 */
    private BaseDAO baseDAO;

    public SPDBHelper()
    {
        baseDAO = BaseDAO.getInstance();
    }

    public void contains(final String key, final IDataBaseListener<Boolean> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run() {
                listener.onSQLResult(contains(key), tag);
            }
        });
    }

    private boolean contains(final String key) {
        boolean isExist = false;
        Cursor cursor = null;
        try {
            cursor = baseDAO.query(TABLE_NAME,
                    null,
                    COLUMN_KEY + "=?",
                    new String[]
                            { key },
                    null,
                    null,
                    null);
            if (cursor.getCount() > 0) {
                isExist = true;
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isExist;
    }

    //================(1)放入boolean值=============
    public void getBoolean(final String key, final boolean defaultResult, final IDataBaseListener<Boolean> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onSQLResult(getBoolean(key, defaultResult), tag);
            }
        });
    }

    /**
     * 查询某个value是否存在
     *
     * @param key
     * @param defaultResult
     * @return
     */
    public boolean getBoolean(final String key, final boolean defaultResult) {
        boolean result = defaultResult;
        Cursor cursor = null;
        try {
            cursor = baseDAO.query(TABLE_NAME,
                    null,
                    COLUMN_KEY + "=?",
                    new String[]{ key },
                    null,
                    null,
                    null);
            //Cursor必须调用cursor.moveToNext()方法才能开始取数据，需要使用while循环
            if (cursor.moveToNext()) {
               /** 在Java中,boolean值中的true值为1,false值为0,所以,转化的依据就是判断boolean值是否为true,
                        如果为true就返回结果1,否则返回0,*/
                result = cursor.getInt(cursor.getColumnIndex(COLUMN_VALUE)) == 1;
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public void putBoolean(final String key, final boolean value, final IDataBaseListener<Boolean> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                putBoolean(key, value);
                if (listener != null) {
                    listener.onSQLResult(true, tag);
                }
            }
        });
    }

    /**
     * 存入布尔值
     *
     * @param key
     * @param value
     */
    public void putBoolean(String key, boolean value) {
        if (contains(key)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.update(TABLE_NAME,
                    values,
                    COLUMN_KEY + "=?",
                    new String[]
                            { key });
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_KEY,
                    key);
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.insert(TABLE_NAME,
                    values);
        }
    }

    //================(2)放入int值=============
    public void getInteger(final String key, final int defaultResult, final IDataBaseListener<Integer> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onSQLResult(getInteger(key, defaultResult), tag);
            }
        });
    }

    /**
     * 查询某个value是否存在
     *
     * @param key
     * @param defaultResult
     * @return
     */
    public int getInteger(final String key, final int defaultResult) {
        int result = defaultResult;
        Cursor cursor = null;
        try {
            cursor = baseDAO.query(TABLE_NAME,
                    null,
                    COLUMN_KEY + "=?",
                    new String[]{ key },
                    null,
                    null,
                    null);
            //Cursor必须调用cursor.moveToNext()方法才能开始取数据，需要使用while循环
            if (cursor.moveToNext()) {
                /** 在Java中,boolean值中的true值为1,false值为0,所以,转化的依据就是判断boolean值是否为true,
                 如果为true就返回结果1,否则返回0,*/
                result = cursor.getInt(cursor.getColumnIndex(COLUMN_VALUE));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public void putInteger(final String key, final int value, final IDataBaseListener<Integer> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                putInteger(key, value);
                if (listener != null) {
                    listener.onSQLResult(value, tag);
                }
            }
        });
    }

    /**
     * 存入int值
     *
     * @param key
     * @param value
     */
    public void putInteger(String key, int value) {
        if (contains(key)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.update(TABLE_NAME,
                    values,
                    COLUMN_KEY + "=?",
                    new String[]
                            { key });
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_KEY,
                    key);
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.insert(TABLE_NAME,
                    values);
        }
    }

    //================(3)放入float值=============
    public void getFolat(final String key, final float defaultResult, final IDataBaseListener<Float> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onSQLResult(getFolat(key, defaultResult), tag);
            }
        });
    }

    /**
     * 查询某个value是否存在
     *
     * @param key
     * @param defaultResult
     * @return
     */
    public float getFolat(final String key, final float defaultResult) {
        float result = defaultResult;
        Cursor cursor = null;
        try {
            cursor = baseDAO.query(TABLE_NAME,
                    null,
                    COLUMN_KEY + "=?",
                    new String[]{ key },
                    null,
                    null,
                    null);
            //Cursor必须调用cursor.moveToNext()方法才能开始取数据，需要使用while循环
            if (cursor.moveToNext()) {
                /** 在Java中,boolean值中的true值为1,false值为0,所以,转化的依据就是判断boolean值是否为true,
                 如果为true就返回结果1,否则返回0,*/
                result = cursor.getFloat(cursor.getColumnIndex(COLUMN_VALUE));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public void putFolat(final String key, final float value, final IDataBaseListener<Float> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                putFolat(key, value);
                if (listener != null) {
                    listener.onSQLResult(value, tag);
                }
            }
        });
    }

    /**
     * 存入float值
     * @param key
     * @param value
     */
    public void putFolat(String key, float value) {
        if (contains(key)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.update(TABLE_NAME,
                    values,
                    COLUMN_KEY + "=?",
                    new String[]
                            { key });
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_KEY,
                    key);
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.insert(TABLE_NAME,
                    values);
        }
    }

    //================(4)放入double值=============
    public void getDouble(final String key, final double defaultResult, final IDataBaseListener<Double> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onSQLResult(getDouble(key, defaultResult), tag);
            }
        });
    }

    /**
     * 查询某个value是否存在
     *
     * @param key
     * @param defaultResult
     * @return
     */
    public double getDouble(final String key, final double defaultResult) {
        double result = defaultResult;
        Cursor cursor = null;
        try {
            cursor = baseDAO.query(TABLE_NAME,
                    null,
                    COLUMN_KEY + "=?",
                    new String[]{ key },
                    null,
                    null,
                    null);
            //Cursor必须调用cursor.moveToNext()方法才能开始取数据，需要使用while循环
            if (cursor.moveToNext()) {
                /** 在Java中,boolean值中的true值为1,false值为0,所以,转化的依据就是判断boolean值是否为true,
                 如果为true就返回结果1,否则返回0,*/
                result = cursor.getDouble(cursor.getColumnIndex(COLUMN_VALUE));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public void putDouble(final String key, final Double value, final IDataBaseListener<Double> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                putDouble(key, value);
                if (listener != null) {
                    listener.onSQLResult(value, tag);
                }
            }
        });
    }

    /**
     * 存入double值
     * @param key
     * @param value
     */
    public void putDouble(String key, double value)
    {
        if (contains(key)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.update(TABLE_NAME,
                    values,
                    COLUMN_KEY + "=?",
                    new String[]
                            { key });
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_KEY,
                    key);
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.insert(TABLE_NAME,
                    values);
        }
    }

    //================(5)放入String值=============
    public void getString(final String key, final String defaultResult, final IDataBaseListener<String> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onSQLResult(getString(key, defaultResult), tag);
            }
        });
    }

    /**
     * 查询某个value是否存在
     *
     * @param key
     * @param defaultResult
     * @return
     */
    public String getString(final String key, final String defaultResult) {
        String result = defaultResult;
        Cursor cursor = null;
        try {
            cursor = baseDAO.query(TABLE_NAME,
                    null,
                    COLUMN_KEY + "=?",
                    new String[]{ key },
                    null,
                    null,
                    null);
            //Cursor必须调用cursor.moveToNext()方法才能开始取数据，需要使用while循环
            if (cursor.moveToNext()) {
                /** 在Java中,boolean值中的true值为1,false值为0,所以,转化的依据就是判断boolean值是否为true,
                 如果为true就返回结果1,否则返回0,*/
                result = cursor.getString(cursor.getColumnIndex(COLUMN_VALUE));
            }
        } finally {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return result;
    }

    public void putString(final String key, final String value, final IDataBaseListener<String> listener, final int tag) {
        mExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                putString(key, value);
                if (listener != null) {
                    listener.onSQLResult(value, tag);
                }
            }
        });
    }

    /**
     * 存入String值
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        if (contains(key)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.update(TABLE_NAME,
                    values,
                    COLUMN_KEY + "=?",
                    new String[]
                            { key });
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_KEY,
                    key);
            values.put(COLUMN_VALUE,
                    value);
            baseDAO.insert(TABLE_NAME,
                    values);
        }
    }
}
