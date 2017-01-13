package edu.ecjtu.domain.Util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.Album;
import edu.ecjtu.domain.vo.objects.Artist;
import edu.ecjtu.domain.vo.objects.Audio;
import edu.ecjtu.domain.vo.objects.BytesAndStatus;
import edu.ecjtu.domain.vo.objects.Song;

public class MediaUtils {
	/**
	 * _id = bundle.getInt(Media._ID); album_id = bundle.getInt(Media.ALBUM_ID);
	 * artist_id = bundle.getInt(Media.ARTIST_ID);
	 */

	public static final String[] AUDIO_KEYS = new String[] { Media.ARTIST_ID,
			Media.ALBUM_ID, Media._ID, Media.TITLE, Media.ARTIST, Media.ALBUM,
			Media.DATA, Media.DURATION, Media.IS_MUSIC, Media.IS_RINGTONE,
			Media.IS_ALARM };
	public static final String PARENTPATH = "create date";
	public static List<Map<Object, Object>> mapList = new ArrayList<Map<Object, Object>>();

	/**
	 * ��ѯ�Լ����ݿ�ĸ���
	 * 
	 * @param context
	 *            �����Ķ���
	 * @return ���ز�ѯ�ĸ�������
	 */
	public static List<Song> getSongList(Context context, String where,
			String[] selectionArgs) {
		List<Song> songs = new ArrayList<Song>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(MusicProvider.SONG_URI, null, where,
				selectionArgs, null);
		while (cursor.moveToNext()) {
			Song song = new Song();
			song.set_id(cursor.getString(cursor.getColumnIndex("_id")));
			song.setSongname(cursor.getString(cursor.getColumnIndex("songname")));
			song.setAlbum(new Album(cursor.getString(cursor
					.getColumnIndex("album_id")), null));
			song.setArtist(new Artist(cursor.getString(cursor
					.getColumnIndex("artist_id")), null));
			song.setParentpath(cursor.getString(cursor
					.getColumnIndex("parentpath")));
			song.setSongpath(cursor.getString(cursor
					.getColumnIndex(MusicProvider.TABLE_SONG_SONGPATH)));
			song.setIsFavorite(cursor.getString(cursor
					.getColumnIndex(MusicProvider.TABLE_SONG_ISFAVORITE)));
			songs.add(song);
		}
		cursor.close();
		return songs;
	}

	/**
	 * �����ѯ���ݿ�
	 * 
	 * @param tClass
	 *            ��
	 * @param context
	 *            �����Ķ���
	 * @param uri
	 *            ��Ҫ��ѯ��Uri
	 * @param properties
	 *            �������
	 * @param selectionArgs
	 *            '?'������
	 * @return ��ѯ����ļ���
	 */
	public static <T> List<T> getList(Class tClass, Context context, Uri uri,
			String[] properties, String selection, String[] selectionArgs,
			String sortOrder) {
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, null, selection, selectionArgs,
				sortOrder);
		List<T> list = new ArrayList<T>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				T t = null;
				try {
					t = (T) tClass.newInstance();
					if (!tClass.equals(Song.class)) {
						for (String property : properties) {
							Method setMethod = tClass.getDeclaredMethod("set"
									+ property, String.class);
							setMethod.invoke(t, cursor.getString(cursor
									.getColumnIndex(property.toLowerCase())));
						}
					} else {// �����Song����ʱ,
						for (String property : properties) {
							if (property.equals("Artist")) {// ����������
								((Song) t)
										.setArtist(new Artist(
												null,
												cursor.getString(cursor
														.getColumnIndex("artistname"))));
							} else if (property.equals("Album")) {// ����ר��
								((Song) t).setAlbum(new Album(null, cursor
										.getString(cursor
												.getColumnIndex("albumname"))));
							} else {// ���ø�����_id
								Method setMethod = tClass.getDeclaredMethod(
										"set" + property, String.class);
								setMethod.invoke(t,
										cursor.getString(cursor
												.getColumnIndex(property
														.toLowerCase())));
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				list.add(t);
			}
		}
		return list;
	}

	/**
	 * ���ֻ�ϵͳ�Դ����ֻ����в�ѯ����
	 * 
	 * @param context
	 *            �����Ķ���
	 * @return ���ز�ѯ����������
	 */
	public static List<Audio> getAudioList(Context context) {
		List<Audio> audioList = new ArrayList<Audio>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, AUDIO_KEYS,
				null, null, Media.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			Bundle bundle = new Bundle();
			for (int i = 0; i < AUDIO_KEYS.length; i++) {
				String key = AUDIO_KEYS[i];
				int columnIndex = cursor.getColumnIndex(key);
				int type = cursor.getType(columnIndex);
				switch (type) {
				case Cursor.FIELD_TYPE_BLOB:
					break;
				case Cursor.FIELD_TYPE_FLOAT:
					bundle.putFloat(key, cursor.getFloat(columnIndex));
					break;
				case Cursor.FIELD_TYPE_INTEGER:
					bundle.putString(key, cursor.getString(columnIndex));
					break;
				case Cursor.FIELD_TYPE_NULL:
					break;
				case Cursor.FIELD_TYPE_STRING:
					bundle.putString(key, cursor.getString(columnIndex));
					if (key == Media.DATA) {// �������ֵĸ��ļ���·��
						File file = new File(cursor.getString(columnIndex));
						bundle.putString(PARENTPATH, file.getParentFile()
								.getAbsolutePath());
					}
					break;
				}
			}
			if (bundle.getString(Media.DURATION) == null) {// �����ֻ�ϵͳ�еĻ�������
				MediaPlayer mediaPlayer = MediaPlayer.create(context,
						Uri.parse(bundle.getString(Media.DATA)));
				bundle.putString(Media.DURATION, mediaPlayer.getDuration() + "");
				mediaPlayer.release();
				mediaPlayer = null;
				// continue;
			}
			if (bundle.getString(Media.IS_RINGTONE) == null) {
				bundle.putString(Media.IS_RINGTONE, "0");
			}
			if (bundle.getString(Media.IS_ALARM) == null) {
				bundle.putString(Media.IS_ALARM, "0");
			}
			Audio audio = new Audio(bundle);
			audioList.add(audio);
		}
		cursor.close();// �ͷ���Դ
		return audioList;
	}

	/**
	 * ��ȡ�����ֽں�״̬
	 * 
	 * @param downloadId
	 *            ���ص�ID
	 * @return
	 */
	public static List<BytesAndStatus> queryByDownloadStatus(Context context,
			int downloadStatus) {
		Query downloadQuery = new Query().setFilterByStatus(downloadStatus);
		DownloadManager downloadManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		Cursor cursor = downloadManager.query(downloadQuery);
		List<BytesAndStatus> bytesAndStatuseList = new ArrayList<BytesAndStatus>();
		if (cursor != null) {
			if (cursor.moveToNext()) {
				BytesAndStatus bytesAndStatus = new BytesAndStatus();
				bytesAndStatus.setDownloadId(cursor.getLong(cursor
						.getColumnIndex(DownloadManager.COLUMN_ID)));
				bytesAndStatus
						.setTotalSize(cursor.getInt(cursor
								.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
				bytesAndStatus.setTitle(cursor.getString(cursor
						.getColumnIndex(DownloadManager.COLUMN_TITLE)));
				bytesAndStatuseList.add(bytesAndStatus);
			}
		}
		cursor.close();
		return bytesAndStatuseList;
	}
}
