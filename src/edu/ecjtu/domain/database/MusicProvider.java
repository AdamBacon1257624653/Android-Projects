package edu.ecjtu.domain.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MusicProvider extends ContentProvider {

	private SQLiteDatabase db;
	private static UriMatcher uriMatcher;
	public static final String AUTHORITIY = "edu.ecjtu.music";
	// 定义表格名
	public static final String TABLE_FILEPATH = "FILEPATH";
	public static final String TABLE_ARTIST = "ARTIST";
	public static final String TABLE_ALBUM = "ALBUM";
	public static final String TABLE_SONG = "SONG";
	public static final String TABLE_USER = "USER";
	public static final String TABLE_SEARCHHISTORY = "SEARCHHISTORY";

	// 定义模糊查询数据
	private static final String TABLE_SONG_LIKE = "SONG_LIKE";
	private static final String TABLE_ALBUM_LIKE = "ALBUM_LIKE";
	private static final String TABLE_ARTIST_LIKE = "ARTIST_LIKE";
	private static final String TABLE_FILEPATH_LIKE = "FILEPATH_LIKE";
	// 定义多表查询数据
	private static final String SONG_FAVORITE_PATH = "SONG_FAVORITE";
	private static final String SONG_MULTI_QUERY = "SONG_QUERY";

	/**
	 * 定义urimatcher的code
	 */
	// 定义UriMatcher的匹配码
	private static final int TABLE_FILEPATH_CODE = 0;
	private static final int TABLE_ARTIST_CODE = 1;
	private static final int TABLE_ALBUM_CODE = 2;
	private static final int TABLE_SONG_CODE = 3;
	private static final int TABLE_USER_CODE = 10;
	private static final int TABLE_SEARCHHISTORY_CODE = 11;
	// 定义UriMatcher的模糊查询匹配码
	private static final int TABLE_FILEPATH_LIKE_CODE = 4;
	private static final int TABLE_ARTIST_LIKE_CODE = 5;
	private static final int TABLE_ALBUM_LIKE_CODE = 6;
	private static final int TABLE_SONG_LIKE_CODE = 7;
	// 定义UriMatcher的多表查询的匹配码
	// private static final int TABLE_SONG_FAVORITE_CODE = 8;
	private static final int TABLE_SONG_MULTI_CODE = 9;

	/**
	 * 定义表格各字段
	 */
	// 定义各表格各字段的
	public static final String TABLE_FILEPATH_CREATEDATE = "createdate";
	public static final String TABLE_FILEPATH_PARENTNAME = "parentname";
	public static final String TABLE_FILEPATH_ABSOLUTEPATH = "absolutepath";
	// 艺术家字段
	public static final String TABLE_ARTIST_ID = "_id";
	public static final String TABLE_ARTIST_ARTISTNAME = "artistname";
	// 专辑字段
	public static final String TABLE_ALBUM_ID = "_id";
	public static final String TABLE_ALBUM_ALBUMNAME = "albumname";
	// 歌曲字段
	public static final String TABLE_SONG_ID = "_id";
	public static final String TABLE_SONG_SONGNAME = "songname";
	public static final String TABLE_SONG_ISFAVORITE = "isFavorite";
	public static final String TABLE_SONG_ARTIST_ID = "artist_id";
	public static final String TABLE_SONG_ALBUM_ID = "album_id";
	public static final String TABLE_SONG_PARENTPATH = "parentpath";
	public static final String TABLE_SONG_SONGPATH = "songpath";
	public static final String TABLE_SONG_DURATION = "duration";
	public static final String TABLE_SONG_LASTPLAYTIMEMILLIES = "lastplaytimemillies";
	// 用户字段
	public static final String TABLE_USER_USERNAME = "username";
	public static final String TABLE_USER_PASSWORD = "password";
	public static final String TABLE_USER_ISLOGIN = "islogin";
	public static final String TABLE_USER_HEADPHOTO = "headphoto";
	// 搜索历史字段
	public static final String TABLE_SEARCHHISTORY_CONTENT = "content";
	public static final String TABLE_SEARCHHISTORY_TIMEMILLIES = "timemillies";

	/**
	 * 定义访问表格的Uri
	 */
	// 定义访问的Uri
	public static final Uri ALL_URI = Uri.parse("content://" + AUTHORITIY);
	public static final Uri SONG_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_SONG);
	public static final Uri ALBUM_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_ALBUM);
	public static final Uri ARTIST_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_ARTIST);
	public static final Uri FILEPATH_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_FILEPATH);
	public static final Uri USER_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_USER);
	public static final Uri SEARCHHISTORY_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_SEARCHHISTORY);
	// 定义模糊查询Uri
	public static final Uri SONG_LIKE_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_SONG_LIKE);
	public static final Uri ALBUM_LIKE_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_ALBUM_LIKE);
	public static final Uri ARTIST_LIKE_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_ARTIST_LIKE);
	public static final Uri FILEPATH_LIKE_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), TABLE_FILEPATH_LIKE);
	// 定义多表查询Uri
	// public static final Uri SONG_FAVORITE_URI = Uri.withAppendedPath(
	// Uri.parse("content://" + AUTHORITIY), SONG_FAVORITE_PATH);
	public static final Uri SONG_MULTI_URI = Uri.withAppendedPath(
			Uri.parse("content://" + AUTHORITIY), SONG_MULTI_QUERY);

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITIY, TABLE_FILEPATH, TABLE_FILEPATH_CODE);
		uriMatcher.addURI(AUTHORITIY, TABLE_ARTIST, TABLE_ARTIST_CODE);
		uriMatcher.addURI(AUTHORITIY, TABLE_ALBUM, TABLE_ALBUM_CODE);
		uriMatcher.addURI(AUTHORITIY, TABLE_SONG, TABLE_SONG_CODE);
		uriMatcher.addURI(AUTHORITIY, TABLE_USER, TABLE_USER_CODE);
		uriMatcher.addURI(AUTHORITIY, TABLE_SEARCHHISTORY,
				TABLE_SEARCHHISTORY_CODE);
		// 初始化模糊匹配查询字段
		uriMatcher.addURI(AUTHORITIY, TABLE_SONG_LIKE, TABLE_SONG_LIKE_CODE);
		uriMatcher.addURI(AUTHORITIY, TABLE_ALBUM_LIKE, TABLE_ALBUM_LIKE_CODE);
		uriMatcher
				.addURI(AUTHORITIY, TABLE_ARTIST_LIKE, TABLE_ARTIST_LIKE_CODE);
		uriMatcher.addURI(AUTHORITIY, TABLE_FILEPATH_LIKE,
				TABLE_FILEPATH_LIKE_CODE);
		// 初始化收藏查询字段
		// uriMatcher.addURI(AUTHORITIY, SONG_FAVORITE_PATH,
		// TABLE_SONG_FAVORITE_CODE);
		uriMatcher.addURI(AUTHORITIY, SONG_MULTI_QUERY, TABLE_SONG_MULTI_CODE);
	}

	// 多表查询语句
	private String songsql = "SELECT s._id _id, s.duration duration, s.songpath songpath ,s.songname songname,s.isFavorite isfavorite,"//
			+ "ar.artistname artistname,al.albumname albumname"//
			+ " FROM SONG s INNER JOIN ARTIST ar ON s.artist_id=ar._id"//
			+ " INNER JOIN ALBUM al ON al._id =s.album_id ";//
	private String filepathsql = "SELECT s.count as count ,"
			+ TABLE_FILEPATH_PARENTNAME + "," + TABLE_FILEPATH_ABSOLUTEPATH
			+ ",_id" + " FROM " + TABLE_FILEPATH + " f,("
			+ " SELECT count(*) count,filepath_id f_id " + "FROM " + TABLE_SONG
			+ " " + "GROUP BY filepath_id" + ") s " + "WHERE f._id=s.f_id";
	private String albumsql = "SELECT s.count count ," + TABLE_ALBUM_ALBUMNAME
			+ ",_id" + " FROM " + TABLE_ALBUM + " al,("
			+ " SELECT count(*) count,album_id al_id " + "FROM " + TABLE_SONG
			+ " " + "GROUP BY al_id" + ") s " + "WHERE al._id=s.al_id";
	private String artistsql = "SELECT s.count count ,"
			+ TABLE_ARTIST_ARTISTNAME + ",_id" + " FROM " + TABLE_ARTIST
			+ " ar,(" + " SELECT count(*) count,artist_id ar_id " + "FROM "
			+ TABLE_SONG + " " + "GROUP BY ar_id" + ") s "
			+ "WHERE ar._id=s.ar_id";

	@Override
	public boolean onCreate() {
		MusicSQLiteOpenHelper helper = new MusicSQLiteOpenHelper(getContext(),
				"Music.db", null, 1);
		db = helper.getReadableDatabase();
		return true;
	}

	/**
	 * 数据库查询
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		String[] likeSelectionArgs = selectionArgs;
		if (selectionArgs != null) {
			likeSelectionArgs = new String[] { "%" + selectionArgs[0] + "%" };// 模糊查询
		}
		int code = uriMatcher.match(uri);
		switch (code) {
		case UriMatcher.NO_MATCH:
			break;
		case TABLE_SONG_CODE:// Song查询
			cursor = db.query(TABLE_SONG, projection, selection, selectionArgs,
					null, null, sortOrder);
			break;
		case TABLE_ALBUM_CODE:// Album查询
			cursor = db.rawQuery(albumsql, null);
			break;
		case TABLE_ARTIST_CODE:// Artist查询
			cursor = db.rawQuery(artistsql, null);
			break;
		case TABLE_FILEPATH_CODE:// filepath查询
			cursor = db.rawQuery(filepathsql, null);
			break;
		case TABLE_USER_CODE:
			cursor = db.query(TABLE_USER, null, selection, selectionArgs, null,
					null, sortOrder);
			break;
		case TABLE_SEARCHHISTORY_CODE:
			cursor = db.query(TABLE_SEARCHHISTORY, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case TABLE_SONG_MULTI_CODE:// Song 多表查询
			String querySongsql = selection != null ? songsql + " WHERE "
					+ selection : songsql;
			querySongsql = sortOrder != null ? querySongsql + " ORDER BY "
					+ sortOrder : querySongsql;
			cursor = db.rawQuery(querySongsql, selectionArgs);
			break;
		// 模糊查询
		case TABLE_SONG_LIKE_CODE:// 模糊查询Song
			cursor = FuzzyFindSong(likeSelectionArgs);
			break;
		case TABLE_FILEPATH_LIKE_CODE:// 模糊查询Filepath
			cursor = FuzzyFindFilePath(likeSelectionArgs);
			break;
		case TABLE_ARTIST_LIKE_CODE:// 模糊查询Artist
			cursor = fuzzyFindArtist(likeSelectionArgs);
			break;
		case TABLE_ALBUM_LIKE_CODE:// 模糊查询Album
			cursor = fuzzyFindAlbums(likeSelectionArgs);
			break;
		}
		return cursor;
	}

	/**
	 * @param myselectionArgs
	 * @return
	 */
	private Cursor fuzzyFindAlbums(String[] selectionArgs) {
		Cursor cursor;
		String havingSql = TABLE_ALBUM_ALBUMNAME + " LIKE ?";
		String sql = albumsql + " AND " + havingSql;
		cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}

	/**
	 * 模糊查询歌手
	 * 
	 * @param myselectionArgs
	 *            查询语句中的 ‘?’填充字符串
	 * @return 查询出来的歌手集合
	 */
	private Cursor fuzzyFindArtist(String[] selectionArgs) {
		Cursor cursor;
		String havingSql = TABLE_ARTIST_ARTISTNAME + " LIKE ?";
		String sql = artistsql + " AND " + havingSql;
		cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}

	/**
	 * 模糊查询文件路径
	 * 
	 * @param selectionArgs
	 *            查询语句中的 ‘?’填充字符串
	 * @return 查询出来的文件路径集合
	 */
	private Cursor FuzzyFindFilePath(String[] selectionArgs) {
		Cursor cursor;
		String havingSql = TABLE_FILEPATH_PARENTNAME + " LIKE ?";
		String sql = filepathsql + " AND " + havingSql;
		cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}

	/**
	 * 模糊查询歌曲
	 * 
	 * @param selectionArgs
	 *            模糊查询的值
	 * @return 返回模糊查询的结果
	 */
	private Cursor FuzzyFindSong(String[] selectionArgs) {
		Cursor cursor = null;
		String table_field = "songname";
		String whereString = " WHERE s." + table_field + " LIKE ?";
		String sql = songsql + whereString;
		cursor = db.rawQuery(sql, selectionArgs);// 根据歌名进行查询
		if (cursor.getCount() <= 0) {// 根据歌星名字进行查询
			table_field = "artistname";
			whereString = " WHERE ar." + table_field + " LIKE ?";
			sql = songsql + whereString;
			cursor = db.rawQuery(sql, selectionArgs);
		}
		if (cursor.getCount() <= 0) {// 根据专辑名字进行查询
			table_field = "albumname";
			whereString = " WHERE al." + table_field + " LIKE ?";
			sql = songsql + whereString;
			cursor = db.rawQuery(sql, selectionArgs);
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	/**
	 * 数据库插入操作 ,有toast提示
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Long count = null;
		int code = uriMatcher.match(uri);
		switch (code) {
		case UriMatcher.NO_MATCH:
			break;
		case TABLE_SONG_CODE:
			db.insert(TABLE_SONG, null, values);
			break;
		case TABLE_ALBUM_CODE:
			db.insert(TABLE_ALBUM, null, values);
			break;
		case TABLE_ARTIST_CODE:
			db.insert(TABLE_ARTIST, null, values);
			break;
		case TABLE_FILEPATH_CODE:
			db.insert(TABLE_FILEPATH, null, values);
			break;
		case TABLE_USER_CODE:
			db.insert(TABLE_USER, null, values);
			break;
		case TABLE_SEARCHHISTORY_CODE:
			db.insert(TABLE_SEARCHHISTORY, null, values);
			break;
		}
		return uri;
	}

	/**
	 * 数据库删除操作,没有toast提示
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Integer count = null;
		int code = uriMatcher.match(uri);
		switch (code) {
		case TABLE_SONG_CODE:
			count = db.delete(TABLE_SONG, selection, selectionArgs);
			break;
		case TABLE_ALBUM_CODE:
			count = db.delete(TABLE_ALBUM, selection, selectionArgs);
			break;
		case TABLE_ARTIST_CODE:
			count = db.delete(TABLE_ARTIST, selection, selectionArgs);
			break;
		case TABLE_FILEPATH_CODE:
			count = db.delete(TABLE_FILEPATH, selection, selectionArgs);
			break;
		case TABLE_USER_CODE:
			count = db.delete(TABLE_USER, selection, selectionArgs);
			break;
		case TABLE_SEARCHHISTORY_CODE:
			count = db.delete(TABLE_SEARCHHISTORY, selection, selectionArgs);
			break;
		}
		return count;
	}

	/**
	 * 数据库更新操作,没有toast提示
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Integer count = null;
		int code = uriMatcher.match(uri);
		switch (code) {
		case TABLE_SONG_CODE:
			count = db.update(TABLE_SONG, values, selection, selectionArgs);
			break;
		case TABLE_ALBUM_CODE:
			count = db.update(TABLE_ALBUM, values, selection, selectionArgs);
			break;
		case TABLE_ARTIST_CODE:
			count = db.update(TABLE_ARTIST, values, selection, selectionArgs);
			break;
		case TABLE_FILEPATH_CODE:
			count = db.update(TABLE_FILEPATH, values, selection, selectionArgs);
			break;
		case TABLE_USER_CODE:
			count = db.update(TABLE_USER, values, selection, selectionArgs);
			break;
		case TABLE_SEARCHHISTORY_CODE:
			count = db.update(TABLE_SEARCHHISTORY, values, selection,
					selectionArgs);
			break;
		}
		return count;
	}

}
