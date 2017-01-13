package edu.ecjtu.domain.database;


public class MusicSQLiteOpenHelper extends SQLiteOpenHelper {

	public MusicSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// ����Filepath���
		db.execSQL("CREATE TABLE FILEPATH ("//
				+ " _id INTEGER PRIMARY KEY ,"//
				+ " parentname TEXT,"//
				+ " absolutepath TEXT)");
		// ����Artist���
		db.execSQL("CREATE TABLE ARTIST ("//
				+ " _id INTEGER PRIMARY KEY,"//
				+ " artistname TEXT)");
		// ����Album���
		db.execSQL("CREATE TABLE ALBUM ("//
				+ " _id INTEGER PRIMARY KEY,"//
				+ " albumname TEXT)");// ����ר�����
		// ����Song���
		db.execSQL("CREATE TABLE SONG ("//
				+ " _id INTEGER PRIMARY KEY,"//
				+ " songname TEXT,"//
				+ " songpath TEXT,"// �����ľ���·��
				+ " duration TEXT,"// ����ʱ��
				+ " isFavorite INTEGER,"// �Ƿ��ղ�
				+ " artist_id TEXT REFERENCES ARTIST(_id),"//
				+ " album_id TEXT REFERENCES ALBUM(_id),"//
				+ " filepath_id TEXT REFERENCES FILEPATH(_id),"//
				+ " parentpath TEXT REFERENCES FILEPATH(absolutepath),"
				+ " lastplaytimemillies INTEGER)");
		// ����User���
		db.execSQL("CREATE TABLE USER ("//
				+ " username TEXT PRIMARY KEY,"//
				+ " password TEXT,"
				+ " islogin INTEGER,"
				+ " headphoto BINARY)");// 1����¼״̬,0:δ��¼״̬
		// ����SearchHistory���
		db.execSQL("CREATE TABLE SEARCHHISTORY ("//
				+ " content TEXT PRIMARY KEY,"
				+ " timemillies INTEGER)");

		// ����Trigger
		db.execSQL("CREATE TRIGGER filepath_deleted "//
				+ " BEFORE DELETE ON FILEPATH"//
				+ " BEGIN"//
				+ " DELETE FROM SONG WHERE filepath_id=OLD._id;"//
				+ " END");
		db.execSQL("CREATE TRIGGER artist_deleted "//
				+ " BEFORE DELETE ON ARTIST"//
				+ " BEGIN"//
				+ " DELETE FROM SONG WHERE artist_id=OLD._id;"//
				+ " END");
		db.execSQL("CREATE TRIGGER album_deleted "//
				+ " BEFORE DELETE ON ALBUM"//
				+ " BEGIN"//
				+ " DELETE FROM SONG WHERE album_id=OLD._id;"//
				+ " END");
	}

	/**
	 * �汾����
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS FILEPATH");
		db.execSQL("DROP TABLE IF EXISTS ARTIST");
		db.execSQL("DROP TABLE IF EXISTS ALBUM");
		db.execSQL("DROP TABLE IF EXISTS SONG");
		db.execSQL("DROP TRIGGER IF EXISTS filepath_deleted");
		db.execSQL("DROP TRIGGER IF EXISTS artist_deleted");
		db.execSQL("DROP TRIGGER IF EXISTS album_deleted");
		onCreate(db);
	}

}
