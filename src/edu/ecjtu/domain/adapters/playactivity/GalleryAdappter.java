package edu.ecjtu.domain.adapters.playactivity;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import edu.ecjtu.domain.vo.objects.Item;

public class GalleryAdappter extends BaseAdapter {

	/*private int[] images;
	private String[] imageNames;*/
	private List<View> viewList;
//	private Context context;

	/*public GalleryAdappter(Context context, int[] images,
			String[] imageNames) {
		this.context = context;
		this.images = images;
		this.imageNames = imageNames;
	}*/
	public GalleryAdappter(List<View> viewList){
//		this.context=context;
		this.viewList=viewList;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/*View view = null;
		GallaryViewHolder viewHolder = null;
		if (convertView == null) {
			view = View.inflate(context, R.layout.fragment_gallery, null);
			viewHolder = new GallaryViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (GallaryViewHolder) view.getTag();
		}
		viewHolder.iv_pic.setImageResource(images[position]);
		viewHolder.tv_picname.setText(imageNames[position]);
		view.setTag(R.id.iv_pic,images[position]);*/
		View view=viewList.get(position);
		return view;
	}

	/*class GallaryViewHolder {
		MyImageView iv_pic;
		TextView tv_picname;

		public GallaryViewHolder(View v) {
			this.iv_pic = (MyImageView) v.findViewById(R.id.iv_pic);
			this.tv_picname = (TextView) v.findViewById(R.id.tv_picname);
		}
	}*/

	public Item getItemByImageId(Integer imageId){
		Item item=new Item();
		for (int i=0;i<viewList.size();i++) {
			View view=viewList.get(i);
			Integer resId=(Integer) view.getTag();
			if(imageId.equals(resId)){
				item.setView(view);
				item.setPosition(i);
				break;
			}
		}
		return item;
	}
	
	/**
	 * 系统非自动调用的方法
	 */
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
