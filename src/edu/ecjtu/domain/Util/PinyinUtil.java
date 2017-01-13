package edu.ecjtu.domain.Util;

import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import edu.ecjtu.domain.vo.interfaces.PinyinInterface;

public class PinyinUtil {
	/**
	 * 通过汉语名字获取拼音
	 * 
	 * @param chinese
	 *            中文字
	 * @return 大写的汉语拼音
	 */
	public static String getPinyin(String chinese) {

		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		char[] charArray = chinese.toCharArray();
		StringBuilder sb = new StringBuilder();
		String s = "";
		try {
			for (char c : charArray) {
				if (Character.isWhitespace(c)) {
					continue;
				}
				if (c >= -127 && c < 128) {
					sb.append(c);
				} else if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
					s = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
					sb.append(s);
				} else {
					sb.append(c);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
			sb.append(s);
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 拼音比较方法
	 * 
	 * @param one
	 *            本对象
	 * @param another
	 *            需要比较的对象
	 * @return <li>返回值大于0：one大于another<li>返回值小于 0：one小于another <li>
	 *         返回值等于0：one等于another
	 */
	public static int compareResult(PinyinInterface one, PinyinInterface another) {
		char c = one.getPinyin().charAt(0);
		char ac = another.getPinyin().charAt(0);
		if (!(c >= 'A' && c <= 'z') && !(ac >= 'A' && ac <= 'z')) {
			return one.getPinyin().compareTo(another.getPinyin());
		}
		if (!(c >= 'A' && c <= 'z')) {
			return 1;
		}
		if (!(ac >= 'A' && ac <= 'z')) {
			return -1;
		}
		return one.getPinyin().compareTo(another.getPinyin());
	}

	/**
	 * 设置字母的textview
	 * 
	 * @param position
	 *            当前所在的位置
	 * @param pinyinInterfaces
	 *            实现方法的对象
	 * @param tv_letter
	 *            需要设置字母的组件
	 */
	public static <T> void initLetterTextView(final int position,
			List<T> pinyinInterfaces, TextView tv_letter) {
		PinyinInterface pinyinInterface = (PinyinInterface) pinyinInterfaces
				.get(position);
		if (position == 0) {
			tv_letter.setText(pinyinInterface.getPinyin().charAt(0) + "");
		} else {
			PinyinInterface prePinyinInterface = (PinyinInterface) pinyinInterfaces
					.get(position - 1);
			char previousChar = prePinyinInterface.getPinyin().charAt(0);
			char currentChar = pinyinInterface.getPinyin().charAt(0);
			if (previousChar != currentChar) {
				if (!(currentChar >= 'A' && currentChar <= 'Z')
						&& (previousChar >= 'A' && previousChar <= 'Z')) {
					tv_letter.setText("*");
				} else if (currentChar >= 'A' && currentChar <= 'Z') {
					tv_letter.setText(currentChar + "");
				} else {
					tv_letter.setVisibility(View.GONE);
				}
			} else {
				tv_letter.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 设置底部信息
	 * 
	 * @param position
	 *            所在位置
	 * @param pinyinInterfaces
	 *            实现方法的对象
	 * @param tv_count
	 *            显示数量的textview
	 * @param text
	 *            数量后的量词描述
	 */
	public static <T> void initBottom(final int position,
			List<T> pinyinInterfaces, TextView tv_count,
			LinearLayout ll_bottom, String text) {
		if (position == pinyinInterfaces.size() - 1) {
			tv_count.setText(pinyinInterfaces.size() + text);
		} else {
			ll_bottom.setVisibility(View.GONE);
		}
	}

	/**
	 * 通过字母进行快速定位查找
	 * 
	 * @param tv_show_letter
	 *            显示字母的textview
	 * @param handler
	 *            推迟显示的handler
	 * @param letter
	 *            字母
	 * @param pinyinInterfaces
	 *            需要遍历的集合对象
	 * @param lv_local_pinyininterfaces
	 *            定位的Listview
	 */
	public static <T> void letterQuickIndex(final TextView tv_show_letter,
			Handler handler, String letter, List<T> pinyinInterfaces,
			ListView lv_local_pinyininterfaces) {
		tv_show_letter.setText(letter);
		tv_show_letter.setVisibility(View.VISIBLE);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				tv_show_letter.setVisibility(View.GONE);
			}
		}, 1000);
		for (int i = 0; i < pinyinInterfaces.size(); i++) {
			PinyinInterface song = (PinyinInterface) pinyinInterfaces.get(i);
			if (letter.equals(song.getPinyin().charAt(0) + "")) {
				lv_local_pinyininterfaces.setSelection(i);
				break;
			}
		}
	}
}
