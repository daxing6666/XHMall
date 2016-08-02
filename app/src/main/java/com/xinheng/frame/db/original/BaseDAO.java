package com.xinheng.frame.db.original;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xinheng.frame.interfaces.ITransactionListener;

/**
 * [description about this class]
 *
 * @author jack
 * @version [DaxingFrame, 2016/03/09 15:43]
 */

public class BaseDAO {

    private DBHelper dBHelper;
    public final static BaseDAO instance = new BaseDAO();

    private BaseDAO()
    {
        dBHelper = new DBHelper();
    }

    /**
     * 单例对象实例
     */
    public static BaseDAO getInstance(){
        return instance;
    }

    public synchronized void closeDB()
    {
        dBHelper.close();
    }

    /**
     * 使用SQLiteDatabase的beginTransaction()方法可以开启一个事务，
     * 程序执行到endTransaction() 方法时会检查事务的标志是否为成功，
     * 如果程序执行到endTransaction()之前调用了setTransactionSuccessful() 方法设置事务的标志为成功则提交事务，
     * 如果没有调用setTransactionSuccessful() 方法则回滚事务。
     *
     * 事务执行任务(通过接口实现在事务中执行任务)
     * @param transactionListener
     * @return 事务执行成功与否
     */
    public synchronized boolean executeWithTransaction(ITransactionListener transactionListener) {
        try {
            dBHelper.getWritableSQLiteDatabase().beginTransaction();
            if (transactionListener != null)
            {
                transactionListener.doTransaction();
            }
            //调用此方法会在执行到endTransaction() 时提交当前事务，如果不调用此方法会回滚事务
            dBHelper.getWritableSQLiteDatabase().setTransactionSuccessful();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            //由事务的标志决定是提交事务，还是回滚事务
            dBHelper.getWritableSQLiteDatabase().endTransaction();
        }
    }

    //*****************第一种原生的操作(CRUD)*********************
    /**
     * 执行基本的sql语句
     * eg:db.execSQL("insert into person(name, age) values('jack', 24)");
     * @param sql
     */
    public synchronized void execSQL(String sql){
        dBHelper.getWritableSQLiteDatabase().execSQL(sql);
    }

    /**
     * eg:
     *  执行上面SQL语句会往person表中添加进一条记录,在实际应用中,
     *  语句中的“jack”这些参数值会由用户输入界面提供,如果把用户输入的内容原样组拼到上面的insert语句,
     *  当用户输入的内容含有单引号时,组拼出来的SQL语句就会存在语法错误。
     *  要解决这个问题需要对单引号进行转义，也就是把单引号转换成两个单引号。
     *  有些时候用户往往还会输入像“ & ”这些特殊SQL符号，为保证组拼好的SQL语句语法正确,
     *  必须对SQL语句中的这些特殊SQL符号都进行转义，显然，对每条SQL语句都做这样的处理工作是比较烦琐的。
     *  SQLiteDatabase类提供了一个重载后的execSQL(String sql, Object[] bindArgs)方法,
     *  使用这个方法可以解决前面提到的问题，因为这个方法支持使用占位符参数(?)。
     *  db.execSQL("insert into person(name, age) values(?,?)", new Object[]{"jack", 4});
     * @param sql
     * @param bindArgs
     */
    public synchronized void execSQL(String sql,Object[] bindArgs){
        dBHelper.getWritableSQLiteDatabase().execSQL(sql,bindArgs);
    }
    //*****************第二种原生操作(执行select语句只是查询)*********************

    /**
     * Cursor是结果集游标，用于对结果集进行随机访问，如果大家熟悉jdbc， 其实Cursor与JDBC中的ResultSet作用很相似。
     * 使用moveToNext()方法可以将游标从当前行移动到下一行，如果已经移过了结果集的最后一行，返回结果为false，否则为true。
     * 另外Cursor 还有常用的moveToPrevious()方法（用于将游标从当前行移动到上一行，如果已经移过了结果集的第一行，
     * 返回值为false，否则为true ）、moveToFirst()方法（用于将游标移动到结果集的第一行，如果结果集为空，返回值为false，
     * 否则为true ）和moveToLast()方法（用于将游标移动到结果集的最后一行，如果结果集为空，返回值为false，否则为true
     * rawQuery()方法的第一个参数为select语句；第二个参数为select语句中占位符参数的值，
     * 如果select语句没有使用占位符，该参数可以设置为null。
     * eg:
     * (1)Cursor cursor = db.rawQuery("select * from person where name like ? and age=?", new String[]{"%jack%", "4"});
     * (2)
     * Cursor cursor = db.rawQuery("select * from person", null);
       while (cursor.moveToNext()) {
           int personid = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
           String name = cursor.getString(1);//获取第二列的值
           int age = cursor.getInt(2);//获取第三列的值
       }
     *
     *
     *
     * @param sql select语句
     * @param selectionArgs select语句中占位符参数的值
     * @return
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return dBHelper.getReadableSQLiteDatabase().rawQuery(sql,
                selectionArgs);
    }

    //*****************第三种原生操作(CRUD)*********************
    //============(1)增加数据============
    /**不管第三个参数是否包含数据，执行Insert()方法必然会添加一条记录，如果第三个参数为空，
    会添加一条除主键之外其他字段值为Null的记录。Insert()方法内部实际上通过构造insert SQL语句完成数据的添加，
    Insert()方法的第二个参数用于指定空值字段的名称，相信大家对该参数会感到疑惑，
    该参数的作用是什么？是这样的：如果第三个参数values 为Null或者元素个数为0，
    由于Insert()方法要求必须添加一条除了主键之外其它字段为Null值的记录，为了满足SQL语法的需要，
    insert语句必须给定一个字段名，如：insert into person(name) values(NULL)，倘若不给定字段名 ，
    insert语句就成了这样： insert into person() values()，显然这不满足标准SQL的语法。对于字段名，建议使用主键之外的字段，
    如果使用了INTEGER类型的主键字段，执行类似insert into person(personid) values(NULL)的insert语句后，
    该主键字段值也不会为NULL。如果第三个参数values 不为Null并且元素的个数大于0 ，可以把第二个参数设置为null。*/
    /**
     * 插入数据
     *   ContentValues values = new ContentValues();
         values.put("name", "林计钦");
         values.put("age", 24);
         long rowid = db.insert(“person”, null, values);//返回新添记录的行号，与主键id无关
     * @param tableName
     * @param values
     */
    public synchronized long insert(String tableName, ContentValues values) {
        return dBHelper.getWritableSQLiteDatabase().insert(tableName,
                null,
                values);
    }

    /**
     * 插入数据, 适用于数据库创建与升级时使用
     *
     * @param db
     * @param tableName
     * @param values
     */
    public static synchronized long insert(SQLiteDatabase db, String tableName, ContentValues values) {
        return db.insert(tableName,
                null,
                values);
    }

    //============(2)删除数据============

    /**
     * db.delete("person", "personid<?", new String[]{"2"});
     * @param table
     * @param whereClause
     * @param whereArgs
     */
    public synchronized void delete(String table, String whereClause, String[] whereArgs){
        dBHelper.getWritableSQLiteDatabase().delete(table, whereClause, whereArgs);
    }

    //============(3)更新数据============
    /**
     * eg:
     * SQLiteDatabase db = databaseHelper.getWritableDatabase();
       ContentValues values = new ContentValues();
       values.put(“name”, “jack”);//key为字段名，value为值
       db.update("person", values, "personid=?", new String[]{"1"});
       db.close();
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     */
    public synchronized void update(String table, ContentValues values, String whereClause, String[] whereArgs){
        dBHelper.getWritableSQLiteDatabase().update(table,values,whereClause,whereArgs);
    }
    //============(4)查询数据============

    /**
     * query()方法实际上是把select语句拆分成了若干个组成部分，然后作为方法的输入参数：
     */
    /**
     *
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit 指定偏移量和获取的记录数，相当于select语句limit关键字后面的部分。
     * @return
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return dBHelper.getReadableSQLiteDatabase().query(table,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit);
    }

    public static Cursor query(SQLiteDatabase db, String table, String[] columns,
                               String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return db.query(table,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy);
    }

    /**
     * 一些操作示例供参考
     * (1)
     *   SQL语句获取5条记录，跳过前面3条记录
     *   select * from Account limit 5 offset 3 或者 select * from Account limit 3,5
     * (2)
     *   SQLiteDatabase db = databaseHelper.getWritableDatabase();
     Cursor cursor = db.query("person", new String[]{"personid,name,age"}, "name like ?", new String[]{"%ljq%"}, null, null, "personid desc", "1,2");
     while (cursor.moveToNext()) {
     int personid = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
     String name = cursor.getString(1);//获取第二列的值
     int age = cursor.getInt(2);//获取第三列的值
     }
     cursor.close();
     db.close();
     上面代码用于从person表中查找name字段含有“传智”的记录，匹配的记录按personid降序排序，对排序后的结果略过第一条记录，只获取2条记录。
     * @param table 表名。相当于select语句from关键字后面的部分。如果是多表联合查询，可以用逗号将两个表名分开。
     * @param columns 要查询出来的列名。相当于select语句select关键字后面的部分。
     * @param selection 查询条件子句，相当于select语句where关键字后面的部分，在条件子句允许使用占位符“?”
     * @param selectionArgs 对应于selection语句中占位符的值，值在数组中的位置与占位符在语句中的位置必须一致，否则就会有异常。
     * @param groupBy 相当于select语句group by关键字后面的部分
     * @param having 相当于select语句having关键字后面的部分
     * @param orderBy 相当于select语句order by关键字后面的部分，如：personid desc, age asc;
     * @return
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having, String orderBy) {
        return dBHelper.getReadableSQLiteDatabase().query(table,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy);
    }
}
