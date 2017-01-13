package edu.ecjtu.domain.vo.interfaces;

import edu.ecjtu.domain.services.PlayService.OnMusicOnlinePlayListener;
import edu.ecjtu.domain.services.PlayService.OnMusicPlayListener;
import edu.ecjtu.domain.services.PlayService.OnOnlinePlayStartListener;
import edu.ecjtu.domain.services.PlayService.OnPlayListener;

public interface PlayInterface {

	/**
	 * 播放
	 */
	void play();

	/**
	 * 异步播放,播放网络资源
	 * 
	 * @param url
	 */
	void playOnline(String url);

	/**
	 * 暂停
	 */
	void pause();

	/**
	 * 播放下一首
	 */
	void playNext();

	/**
	 * 播放上一首
	 */
	void playPrevious();

	/**
	 * 根据歌曲位置进行歌曲的切换
	 * 
	 * @param position
	 *            歌曲所在的位置
	 */
	void switchSong(int position);

	/**
	 * 快进/快退/拖动进度条播放
	 */
	void playSeekTo(int playPosition);

	/**
	 * 设置在线播放准备播放监听
	 * 
	 * @param onOnlinePlayStartListener
	 */
	void setOnOnlinePlayStartListener(
			OnOnlinePlayStartListener onOnlinePlayStartListener);

	/**
	 * 设置播放界面的在线播放监听
	 * 
	 * @param musicOnlinePlayListener
	 */
	void setOnMusicOnlinePlayListener(
			OnMusicOnlinePlayListener musicOnlinePlayListener);

	/**
	 * 获取当前player是否启动过
	 * 
	 * @return<li>true:启动过<li>false:没启动过
	 */
	boolean getIsStarted();

	/**
	 * 获取当前播放音乐是否为在线播放
	 * 
	 * @return <li>true:是在线播放<li>false:不是在线播放
	 */
	boolean getIsPlayOnline();

	/**
	 * 播放监听
	 * 
	 * @param listener
	 */
	void setOnPlayListener(OnPlayListener listener);

	/**
	 * 音乐播放界面的监听
	 * 
	 * @param musicListener
	 */
	void setOnMusicPlayListener(OnMusicPlayListener musicListener);
}
