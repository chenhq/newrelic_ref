/*     */ package com.newrelic.agent.android.instrumentation;
/*     */ 
/*     */ import android.content.ContentValues;
/*     */ import android.database.Cursor;
/*     */ import android.database.SQLException;
/*     */ import android.database.sqlite.SQLiteDatabase;
/*     */ import android.database.sqlite.SQLiteDatabase.CursorFactory;
/*     */ import android.os.CancellationSignal;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class SQLiteInstrumentation
/*     */ {
/*  18 */   private static final ArrayList<String> categoryParams = new ArrayList(Arrays.asList(new String[] { "category", MetricCategory.class.getName(), "DATABASE" }));
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor query(SQLiteDatabase database, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
/*     */   {
/*  24 */     TraceMachine.enterMethod("SQLiteDatabase#query", categoryParams);
/*  25 */     Cursor cursor = database.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
/*  26 */     TraceMachine.exitMethod();
/*     */ 
/*  28 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor query(SQLiteDatabase database, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
/*  33 */     TraceMachine.enterMethod("SQLiteDatabase#query", categoryParams);
/*  34 */     Cursor cursor = database.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
/*  35 */     TraceMachine.exitMethod();
/*     */ 
/*  37 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor query(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
/*  42 */     TraceMachine.enterMethod("SQLiteDatabase#query", categoryParams);
/*  43 */     Cursor cursor = database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
/*  44 */     TraceMachine.exitMethod();
/*     */ 
/*  46 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor query(SQLiteDatabase database, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
/*  51 */     TraceMachine.enterMethod("SQLiteDatabase#query", categoryParams);
/*  52 */     Cursor cursor = database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
/*  53 */     TraceMachine.exitMethod();
/*     */ 
/*  55 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor queryWithFactory(SQLiteDatabase database, SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
/*  60 */     TraceMachine.enterMethod("SQLiteDatabase#queryWithFactory", categoryParams);
/*  61 */     Cursor cursor = database.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
/*  62 */     TraceMachine.exitMethod();
/*     */ 
/*  64 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor queryWithFactory(SQLiteDatabase database, SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
/*  69 */     TraceMachine.enterMethod("SQLiteDatabase#queryWithFactory", categoryParams);
/*  70 */     Cursor cursor = database.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
/*  71 */     TraceMachine.exitMethod();
/*     */ 
/*  73 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor rawQuery(SQLiteDatabase database, String sql, String[] selectionArgs) {
/*  78 */     TraceMachine.enterMethod("SQLiteDatabase#rawQuery", categoryParams);
/*  79 */     Cursor cursor = database.rawQuery(sql, selectionArgs);
/*  80 */     TraceMachine.exitMethod();
/*     */ 
/*  82 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor rawQuery(SQLiteDatabase database, String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
/*  87 */     TraceMachine.enterMethod("SQLiteDatabase#rawQuery", categoryParams);
/*  88 */     Cursor cursor = database.rawQuery(sql, selectionArgs, cancellationSignal);
/*  89 */     TraceMachine.exitMethod();
/*     */ 
/*  91 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor rawQueryWithFactory(SQLiteDatabase database, SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable) {
/*  96 */     TraceMachine.enterMethod("SQLiteDatabase#rawQueryWithFactory", categoryParams);
/*  97 */     Cursor cursor = database.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable);
/*  98 */     TraceMachine.exitMethod();
/*     */ 
/* 100 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static Cursor rawQueryWithFactory(SQLiteDatabase database, SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable, CancellationSignal cancellationSignal) {
/* 105 */     TraceMachine.enterMethod("SQLiteDatabase#rawQueryWithFactory", categoryParams);
/* 106 */     Cursor cursor = database.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable, cancellationSignal);
/* 107 */     TraceMachine.exitMethod();
/*     */ 
/* 109 */     return cursor;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static long insert(SQLiteDatabase database, String table, String nullColumnHack, ContentValues values) {
/* 114 */     TraceMachine.enterMethod("SQLiteDatabase#insert", categoryParams);
/* 115 */     long result = database.insert(table, nullColumnHack, values);
/* 116 */     TraceMachine.exitMethod();
/*     */ 
/* 118 */     return result;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static long insertOrThrow(SQLiteDatabase database, String table, String nullColumnHack, ContentValues values) throws SQLException {
/* 123 */     TraceMachine.enterMethod("SQLiteDatabase#insertOrThrow", categoryParams);
/* 124 */     long result = database.insertOrThrow(table, nullColumnHack, values);
/* 125 */     TraceMachine.exitMethod();
/*     */ 
/* 127 */     return result;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static long insertWithOnConflict(SQLiteDatabase database, String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
/* 132 */     TraceMachine.enterMethod("SQLiteDatabase#insertWithOnConflict", categoryParams);
/* 133 */     long result = database.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
/* 134 */     TraceMachine.exitMethod();
/*     */ 
/* 136 */     return result;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static long replace(SQLiteDatabase database, String table, String nullColumnHack, ContentValues initialValues) {
/* 141 */     TraceMachine.enterMethod("SQLiteDatabase#replace", categoryParams);
/* 142 */     long result = database.replace(table, nullColumnHack, initialValues);
/* 143 */     TraceMachine.exitMethod();
/*     */ 
/* 145 */     return result;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static long replaceOrThrow(SQLiteDatabase database, String table, String nullColumnHack, ContentValues initialValues) throws SQLException {
/* 150 */     TraceMachine.enterMethod("SQLiteDatabase#replaceOrThrow", categoryParams);
/* 151 */     long result = database.replaceOrThrow(table, nullColumnHack, initialValues);
/* 152 */     TraceMachine.exitMethod();
/*     */ 
/* 154 */     return result;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static int delete(SQLiteDatabase database, String table, String whereClause, String[] whereArgs) {
/* 159 */     TraceMachine.enterMethod("SQLiteDatabase#delete", categoryParams);
/* 160 */     int result = database.delete(table, whereClause, whereArgs);
/* 161 */     TraceMachine.exitMethod();
/*     */ 
/* 163 */     return result;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static int update(SQLiteDatabase database, String table, ContentValues values, String whereClause, String[] whereArgs) {
/* 168 */     TraceMachine.enterMethod("SQLiteDatabase#update", categoryParams);
/* 169 */     int result = database.update(table, values, whereClause, whereArgs);
/* 170 */     TraceMachine.exitMethod();
/*     */ 
/* 172 */     return result;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static int updateWithOnConflict(SQLiteDatabase database, String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
/* 177 */     TraceMachine.enterMethod("SQLiteDatabase#updateWithOnConflict", categoryParams);
/* 178 */     int result = database.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
/* 179 */     TraceMachine.exitMethod();
/*     */ 
/* 181 */     return result;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static void execSQL(SQLiteDatabase database, String sql) throws SQLException {
/* 186 */     TraceMachine.enterMethod("SQLiteDatabase#execSQL", categoryParams);
/* 187 */     database.execSQL(sql);
/* 188 */     TraceMachine.exitMethod();
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static void execSQL(SQLiteDatabase database, String sql, Object[] bindArgs) throws SQLException {
/* 193 */     TraceMachine.enterMethod("SQLiteDatabase#execSQL", categoryParams);
/* 194 */     database.execSQL(sql, bindArgs);
/* 195 */     TraceMachine.exitMethod();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.SQLiteInstrumentation
 * JD-Core Version:    0.6.2
 */