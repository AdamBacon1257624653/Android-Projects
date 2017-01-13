package edu.ecjtu.domain.vo.interfaces;

import edu.ecjtu.domain.services.PlayService.OnMusicOnlinePlayListener;
import edu.ecjtu.domain.services.PlayService.OnMusicPlayListener;
import edu.ecjtu.domain.services.PlayService.OnOnlinePlayStartListener;
import edu.ecjtu.domain.services.PlayService.OnPlayListener;

public interface PlayInterface {

	/**
	 * ����
	 */
	void play();

	/**
	 * �첽����,����������Դ
	 * 
	 * @param url
	 */
	void playOnline(String url);

	/**
	 * ��ͣ
	 */
	void pause();

	/**
	 * ������һ��
	 */
	void playNext();

	/**
	 * ������һ��
	 */
	void playPrevious();

	/**
	 * ���ݸ���λ�ý��и������л�
	 * 
	 * @param position
	 *            �������ڵ�λ��
	 */
	void switchSong(int position);

	/**
	 * ���/����/�϶�����������
	 */
	void playSeekTo(int playPosition);

	/**
	 * �������߲���׼�����ż���
	 * 
	 * @param onOnlinePlayStartListener
	 */
	void setOnOnlinePlayStartListener(
			OnOnlinePlayStartListener onOnlinePlayStartListener);

	/**
	 * ���ò��Ž�������߲��ż���
	 * 
	 * @param musicOnlinePlayListener
	 */
	void setOnMusicOnlinePlayListener(
			OnMusicOnlinePlayListener musicOnlinePlayListener);

	/**
	 * ��ȡ��ǰplayer�Ƿ�������
	 * 
	 * @return<li>true:������<li>false:û������
	 */
	boolean getIsStarted();

	/**
	 * ��ȡ��ǰ���������Ƿ�Ϊ���߲���
	 * 
	 * @return <li>true:�����߲���<li>false:�������߲���
	 */
	boolean getIsPlayOnline();

	/**
	 * ���ż���
	 * 
	 * @param listener
	 */
	void setOnPlayListener(OnPlayListener listener);

	/**
	 * ���ֲ��Ž���ļ���
	 * 
	 * @param musicListener
	 */
	void setOnMusicPlayListener(OnMusicPlayListener musicListener);
}
