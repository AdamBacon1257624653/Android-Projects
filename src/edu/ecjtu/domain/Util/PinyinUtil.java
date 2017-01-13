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
	 * ͨ���������ֻ�ȡƴ��
	 * 
	 * @param chinese
	 *            ������
	 * @return ��д�ĺ���ƴ��
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
	 * ƴ���ȽϷ���
	 * 
	 * @param one
	 *            ������
	 * @param another
	 *            ��Ҫ�ȽϵĶ���
	 * @return <li>����ֵ����0��one����another<li>����ֵС�� 0��oneС��another <li>
	 *         ����ֵ����0��one����another
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
	 * ������ĸ��textview
	 * 
	 * @param position
	 *            ��ǰ���ڵ�λ��
	 * @param pinyinInterfaces
	 *            ʵ�ַ����Ķ���
	 * @param tv_letter
	 *            ��Ҫ������ĸ�����
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
	 * ���õײ���Ϣ
	 * 
	 * @param position
	 *            ����λ��
	 * @param pinyinInterfaces
	 *            ʵ�ַ����Ķ���
	 * @param tv_count
	 *            ��ʾ������textview
	 * @param text
	 *            ���������������
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
	 * ͨ����ĸ���п��ٶ�λ����
	 * 
	 * @param tv_show_letter
	 *            ��ʾ��ĸ��textview
	 * @param handler
	 *            �Ƴ���ʾ��handler
	 * @param letter
	 *            ��ĸ
	 * @param pinyinInterfaces
	 *            ��Ҫ�����ļ��϶���
	 * @param lv_local_pinyininterfaces
	 *            ��λ��Listview
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
