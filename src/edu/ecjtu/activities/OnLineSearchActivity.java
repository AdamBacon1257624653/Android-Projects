package edu.ecjtu.activities;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityDestroyListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ant.liao.GifView;

import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.onlinesearchactivity.SearchResultAdapter;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.services.PlayService.OnOnlinePlayStartListener;
import edu.ecjtu.domain.services.PlayService.OnPlayListener;
import edu.ecjtu.domain.views.MyProgressBar;
import edu.ecjtu.domain.vo.interfaces.ActivityInterface;
import edu.ecjtu.domain.vo.interfaces.PlayInterface;
import edu.ecjtu.domain.vo.objects.BinderObject;
import edu.ecjtu.domain.vo.objects.BottomPlay;
import edu.ecjtu.domain.vo.objects.OnlineMusic;
import edu.ecjtu.domain.vo.objects.Searchhistory;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class OnLineSearchActivity extends Activity implements OnClickListener,
		ActivityInterface {
	private ListView lv_searchhistory, lv_searchresult;
	private List<String> searchContents = new ArrayList<String>();
	private EditText et_onlinesearch_songname, et_onlinesearch_singername;
	private ImageView iv_clearsearch_songname, iv_clearsearch_singername,
			iv_back;
	private Button btn_onlinesearch;
	private GifView gifview_online;

	// bottom布局
	private RelativeLayout include_botttom;
	private OnActivityDestroyListener listener;
	private boolean isAcitivityPaused = false;
	private BinderObject binderObject;
	private PlayInterface playBinder;
	private Song currentSong;
	private MediaPlayer player;
	private ImageView iv_my_start, iv_my_previous, iv_my_next;
	private TextView tv_my_songname, tv_my_artistname;
	private MyProgressBar mpb_musicplay;
	private DisplayMetrics dm;
	private BottomPlay bottomPlay;
	private NetworkInfo networkInfo;
	private ContentResolver resolver;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Utils.RESPONSE_FINISHED:// 网络交互完毕,获取数据成功
				List<OnlineMusic> musics = (List<OnlineMusic>) msg.obj;
				lv_searchresult.setVisibility(View.VISIBLE);
				lv_searchresult.setAdapter(new SearchResultAdapter(musics,
						OnLineSearchActivity.this));
				break;
			case 4000:
				mpb_musicplay.setCurrProgress(player.getCurrentPosition());
				mpb_musicplay.invalidate();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onlinesearch_layout);

		initViews();
		initData();
		inflateViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isAcitivityPaused = false;
		initBinderData();
		inflateBottomViews();
		registerListener();
	}

	/**
	 * 初始化组件
	 */
	private void initViews() {
		this.lv_searchhistory = (ListView) findViewById(R.id.lv_searchhistory);
		this.et_onlinesearch_songname = (EditText) findViewById(R.id.et_onlinesearch_songname);
		this.et_onlinesearch_singername = (EditText) findViewById(R.id.et_onlinesearch_singername);
		this.iv_clearsearch_songname = (ImageView) findViewById(R.id.iv_clearsearch_songname);
		this.iv_clearsearch_singername = (ImageView) findViewById(R.id.iv_clearsearch_singername);
		this.iv_back = (ImageView) findViewById(R.id.iv_back);
		this.iv_my_start = (ImageView) findViewById(R.id.iv_my_start);
		this.iv_my_previous = (ImageView) findViewById(R.id.iv_my_previous);
		this.iv_my_next = (ImageView) findViewById(R.id.iv_my_next);
		this.tv_my_songname = (TextView) findViewById(R.id.tv_my_songname);
		this.tv_my_artistname = (TextView) findViewById(R.id.tv_my_artistname);
		this.btn_onlinesearch = (Button) findViewById(R.id.btn_onlinesearch);
		this.lv_searchresult = (ListView) findViewById(R.id.lv_searchresult);
		this.mpb_musicplay = (MyProgressBar) findViewById(R.id.mpb_musicplay);
		this.include_botttom = (RelativeLayout) findViewById(R.id.include_botttom);
		this.gifview_online = (GifView) findViewById(R.id.gifview_online);
	}

	private void initData() {
		List<Searchhistory> searchhistories = MediaUtils.getList(
				Searchhistory.class, this, MusicProvider.SEARCHHISTORY_URI,
				new String[] { "Content" }, null, null,
				MusicProvider.TABLE_SEARCHHISTORY_TIMEMILLIES + " DESC");
		if (searchhistories.size() > 0) {
			for (Searchhistory searchhistory : searchhistories) {
				searchContents.add(searchhistory.getContent());
			}
		} else {
			searchContents.add("暂无搜索记录");
		}
	}

	private void inflateViews() {
		this.lv_searchhistory.setAdapter(new ArrayAdapter<String>(this,
				R.layout.lv_searchhistory_item, searchContents));
		gifview_online.setGifImage(R.drawable.onlineloading);
	}

	/**
	 * 初始化Service的数据
	 */
	public void initBinderData() {
		binderObject = PlayUtils.binderObject;
		if (player == null) {
			player = binderObject.getPlayer();
			playBinder = binderObject.getPlayBinder();
		}
		currentSong = binderObject.getCurrentSong();
		bottomPlay = new BottomPlay(this, iv_my_start, iv_my_previous,
				iv_my_next, tv_my_songname, tv_my_artistname, mpb_musicplay,
				handler, binderObject);

	}

	/**
	 * 填充组件信息
	 */
	private void inflateBottomViews() {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mpb_musicplay.setMaxWidth(dm.widthPixels);
		if (!playBinder.getIsPlayOnline()) {
			if (currentSong != null) {
				mpb_musicplay.setMaxProgress(Float.valueOf(currentSong
						.getDuration()));
				mpb_musicplay.setCurrProgress(0);
			}
		} else {// 在线播放时，更新播放进度
			mpb_musicplay.setMaxProgress(player.getDuration());
			mpb_musicplay.setCurrProgress(player.getCurrentPosition());
		}
		bottomPlay.updateBottomLayoutViews(currentSong, player);// 填充底部组件
		if (player != null && player.isPlaying()) {// 如果后台Service正在播放的時候
			iv_my_start.setImageResource(R.drawable.pause);
		} else {
			iv_my_start.setImageResource(R.drawable.start);
		}
		if (player.getCurrentPosition() < player.getDuration()) {
			bottomPlay.infalteMusicProgressBar();
		}
	}

	private void registerListener() {
		this.iv_clearsearch_songname.setOnClickListener(this);
		this.iv_clearsearch_singername.setOnClickListener(this);
		this.iv_back.setOnClickListener(this);
		this.btn_onlinesearch.setOnClickListener(this);
		this.include_botttom.setOnClickListener(this);
		this.iv_my_start.setOnClickListener(this);
		this.iv_my_previous.setOnClickListener(this);
		this.iv_my_next.setOnClickListener(this);
		playBinder
				.setOnOnlinePlayStartListener(new OnOnlinePlayStartListener() {

					@Override
					public void OnOnlinePlayStart() {
						if (!isAcitivityPaused) {
							gifview_online.setVisibility(View.GONE);
							iv_my_start.setVisibility(View.VISIBLE);
						}
					}
				});
		this.player
				.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
						if (!isAcitivityPaused) {
							mpb_musicplay.setSecondaryProgress((int) (mp
									.getDuration() * (percent * 1.0 / 100.0f)));
						}

					}
				});
		this.playBinder.setOnPlayListener(new OnPlayListener() {

			@Override
			public void OnPlay() {
				if (!isAcitivityPaused) {// 如果本播放界面退出时，不开启线程
					initBinderData();
					if (!playBinder.getIsPlayOnline()) {
						if (gifview_online.getVisibility() == View.VISIBLE) {// 更新动态图片可见性
							gifview_online.setVisibility(View.GONE);
						}
						mpb_musicplay.setMaxProgress(Float.valueOf(currentSong
								.getDuration()));
						bottomPlay.updateBottomLayoutViews(
								binderObject.getCurrentSong(),
								binderObject.getPlayer());
						mpb_musicplay.setSecondaryProgress(0);
					} else {
						mpb_musicplay.setMaxProgress(player.getDuration());
					}
					bottomPlay.infalteMusicProgressBar();
				}
			}
		});
		this.et_onlinesearch_songname.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				updateText(s.toString(), et_onlinesearch_singername.getText()
						.toString());
			}
		});
		this.et_onlinesearch_singername
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						updateText(et_onlinesearch_songname.getText()
								.toString(), s.toString());
					}
				});
	}

	/**
	 * 更新搜索界面的文字显示和listview的显示
	 * 
	 * @param songName
	 *            歌曲名字
	 * @param singerName
	 *            歌手名
	 */
	private void updateText(String songName, String singerName) {
		iv_clearsearch_songname.setVisibility(songName.isEmpty() ? View.GONE
				: View.VISIBLE);
		iv_clearsearch_singername
				.setVisibility(singerName.isEmpty() ? View.GONE : View.VISIBLE);
		if (songName.isEmpty() && singerName.isEmpty()) {
			lv_searchresult.setVisibility(View.GONE);
		}
		btn_onlinesearch
				.setText((songName.isEmpty() && singerName.isEmpty()) ? "取消"
						: "搜索");
	}

	/**
	 * 通过歌曲名称搜索歌曲
	 * 
	 * @param songName
	 *            歌曲名称
	 */
	protected void searchMusic(String songName, String singerName) {
		requestBaiduServer(songName, singerName);
	}

	/**
	 * 访问百度服务器
	 * 
	 * @param songName
	 *            歌曲名称
	 * @return 服务器响应的字符串
	 */
	private void requestBaiduServer(final String songName,
			final String singerName) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String path = "http://box.zhangmen.baidu.com/x?op=12&count=1&title={0}$${1}";
					path = MessageFormat.format(path,
							URLEncoder.encode(songName, "UTF-8"),
							URLEncoder.encode(singerName, "UTF-8"));
					path = path.replaceAll("\\+", "%20");
					URL url = new URL(path);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					if (connection.getResponseCode() == 200) {
						InputStream is = connection.getInputStream();
						List<OnlineMusic> musics = parseMusic(is);
						Message msg = handler
								.obtainMessage(Utils.RESPONSE_FINISHED);
						msg.obj = musics;
						handler.sendMessage(msg);
					} else {
						Utils.showToast(OnLineSearchActivity.this,
								"服务器连接异常,访问服务器失败!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 解析音乐
	 * 
	 * @param is
	 *            服务器返回的数据流
	 * @return 音乐集合
	 */
	protected List<OnlineMusic> parseMusic(InputStream is) {
		List<OnlineMusic> musics = new ArrayList<OnlineMusic>();
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(is);
			Element root = document.getRootElement();
			List<Element> urlElements = root.elements("url");
			List<Element> durlElements = root.elements("durl");
			for (int i = 0; i < urlElements.size(); i++) {
				OnlineMusic music = new OnlineMusic();
				music.setSongName(et_onlinesearch_songname.getText().toString());
				music.setSingerName(et_onlinesearch_singername.getText()
						.toString());
				String urlPath = formatUrlPath(urlElements, i);// 获取普通品质的音乐
				music.setUrlPath(urlPath);
				if (durlElements.get(i).hasContent()) {
					String HQUrlPath = formatUrlPath(durlElements, i);
					music.setHQUrlPath(HQUrlPath);
				}
				musics.add(music);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return musics;
	}

	/**
	 * 格式化url标签和durl标签
	 * 
	 * @param urlElements
	 *            url或durl标签下的子标签集合
	 * @param i
	 *            标签的位置
	 * @return 格式化后的url地址
	 */
	private String formatUrlPath(List<Element> urlElements, int i) {
		String encodeStr = urlElements.get(i).element("encode").getText();
		String decodeStr = urlElements.get(i).element("decode").getText();
		String urlPath = "";
		if (decodeStr.contains("&")) {
			urlPath = encodeStr.substring(0, encodeStr.lastIndexOf("/") + 1)
					+ decodeStr.substring(0, decodeStr.lastIndexOf("&"));
		} else {
			urlPath = encodeStr.substring(0, encodeStr.lastIndexOf("/") + 1)
					+ decodeStr;
		}
		return urlPath;
	}

	/**
	 * 供adapter调用, 点击网络歌曲时,更新底部
	 */
	public void updateOnlineBottom(String songname, String singername) {
		tv_my_songname.setText(songname);
		tv_my_artistname.setText(singername);
		iv_my_start.setImageResource(R.drawable.pause);// 注:这儿需要变为动态图片
		mpb_musicplay.setSecondaryProgress(0);
		gifview_online.setVisibility(View.VISIBLE);
		iv_my_start.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_clearsearch_songname:
			et_onlinesearch_songname.setText("");
			break;
		case R.id.iv_clearsearch_singername:
			et_onlinesearch_singername.setText("");
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_onlinesearch:
			if (btn_onlinesearch.getText().toString().equals("取消")) {
				finish();
			} else {
				if (checkNetworkConnectionState()) {// 网络有连接
					searchMusic(et_onlinesearch_songname.getText().toString(),
							et_onlinesearch_singername.getText().toString());
					saveSearchHistory();// 将搜索记录保存
				} else {
					Utils.showToast(getApplicationContext(),
							"网络连接错误，请检查您的网络连接！");
				}
			}
			break;
		case R.id.include_botttom:
			Intent playIntent = new Intent(this, MusicPlayActivity.class);
			startActivity(playIntent);
			break;
		case R.id.iv_my_start:// 开始/暂停播放
			if (!player.isPlaying()) {
				bottomPlay.startPlay();
			} else {
				bottomPlay.pausePlay();
			}
			break;
		case R.id.iv_my_previous:// 上一首播放
			bottomPlay.previousPlay();
			iv_my_start.setImageResource(R.drawable.pause);
			break;
		case R.id.iv_my_next:// 下一首播放
			bottomPlay.nextPlay();
			iv_my_start.setImageResource(R.drawable.pause);
			break;
		}
	}

	/**
	 * 保存搜索历史纪录
	 */
	private void saveSearchHistory() {
		String querySognname = et_onlinesearch_songname.getText().toString();
		resolver = getContentResolver();
		List<Searchhistory> searchhistories = MediaUtils.getList(
				Searchhistory.class, this, MusicProvider.SEARCHHISTORY_URI,
				new String[] { "Content", "Timemillies" },
				MusicProvider.TABLE_SEARCHHISTORY_CONTENT + "=?",
				new String[] { querySognname }, null);
		ContentValues values = new ContentValues();
		values.put(MusicProvider.TABLE_SEARCHHISTORY_TIMEMILLIES,
				System.currentTimeMillis());
		if (searchhistories.size() > 0) {
			resolver.update(MusicProvider.SEARCHHISTORY_URI, values,
					MusicProvider.TABLE_SEARCHHISTORY_CONTENT + "=?",
					new String[] { querySognname });
		} else {
			values.put(MusicProvider.TABLE_SEARCHHISTORY_CONTENT, querySognname);
			resolver.insert(MusicProvider.SEARCHHISTORY_URI, values);
		}
	}

	/**
	 * 检测网络连接状态
	 * 
	 * @return <li>true:网络已连接<li>false:网络未连接
	 */
	private boolean checkNetworkConnectionState() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null
				|| !connectivityManager.getBackgroundDataSetting()) {
			return false;
		}
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		isAcitivityPaused = true;
		if (listener != null) {
			listener.onActivityDestroy();
		}
	}

	@Override
	public void setOnActivityDestroyListener(OnActivityDestroyListener listener) {
		this.listener = listener;
	}
}
