package dr.android.fileselector;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.improver.lib.fileselector.fs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * FileSelectActivity中ListView的适配器
 *
 * @author DarkRanger
 * @datetime 20160530
 *
 */
public class FileSelectAdapter extends BaseAdapter {
	private Context mContext;
	private int mResource;
	private String[] mFrom = new String[] { "icon", " filename", "childnum", "createtime", "fun" };
	private int[] mTo = new int[] { R.id.id_fileselect_icon, R.id.id_fileselect_filename, R.id.id_fileselect_childnum,
			R.id.id_fileselect_createtime, R.id.id_fileselect_fun };

	// 文件或者文件夹选择模式
	private int mSelectorMode;
	// 是否多选模式
	private boolean mSelectorIsMultiple;
	// 图标宽度
	private int mSelectorIconWidth = -1;
	// 图标高度
	private int mSelectorIconHeight = -1;

	private List<Map<String, Object>> mData;

	// 记录上一次点击folder的位置（SelectMode=FileSelectListView.SELECT_MODE_FOLDER）以及对应的checkbox
	private int lastSelectedFolderPosition = -1;
	private CheckBox lastCheckBox;

	public FileSelectAdapter(Context mContext, List<Map<String, Object>> data, int resource, String[] from, int[] to) {
		super();
		this.mContext = mContext;
		this.mData = data;
		this.mResource = resource;
		this.mFrom = from;
		this.mTo = to;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Map<String, Object> dataSet = mData.get(position);
		File file = (File) dataSet.get("file");
		ViewHoder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(mResource, null, false);

			holder = new ViewHoder();
			holder.icon = (ImageView) convertView.findViewById(mTo[0]);
			setWidthAndHeight(holder);
			holder.fileName = (TextView) convertView.findViewById(mTo[1]);
			holder.childNum = (TextView) convertView.findViewById(mTo[2]);
			holder.createTime = (TextView) convertView.findViewById(mTo[3]);
			holder.checkboxFun = (CheckBox) convertView.findViewById(mTo[4]);
			convertView.setTag(holder);
		} else {
			holder = (ViewHoder) convertView.getTag();
		}
		CheckBox fun = (CheckBox) convertView.findViewById(mTo[4]);
		// 选择文件模式的话，隐藏文件夹右边的功能图标
		if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FILE) {
			if (file.isDirectory()) {
				((ViewGroup) (fun.getParent())).setVisibility(View.GONE);
			} else {
				((ViewGroup) (fun.getParent())).setVisibility(View.VISIBLE);
			}
		}

		holder.icon.setImageResource((Integer) dataSet.get(mFrom[0]));
		setWidthAndHeight(holder);
		holder.fileName.setText((CharSequence) dataSet.get(mFrom[1]));
		holder.childNum.setText((CharSequence) dataSet.get(mFrom[2]));
		holder.createTime.setText((CharSequence) dataSet.get(mFrom[3]));
		holder.checkboxFun.setChecked((Boolean) dataSet.get(mFrom[4]));

		bindEvent(position, convertView, fun, file);

		return convertView;
	}

	// 点击右边的功能键，选中当前view
	private void bindEvent(final int position, final View covertView, final CheckBox fun, File file) {
		ViewGroup checkBoxParent = (ViewGroup) fun.getParent();

		// 复选框父Layout代替checkbox设置触摸事件
		checkBoxParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Map<String, Object> map = mData.get(position);
				map.put(mFrom[4], !(Boolean) map.get(mFrom[4]));
				// 更改checkbox状态
				fun.setChecked(!fun.isChecked());
				// 如果是文件夹模式，则checkbox是单选模式
				if (mSelectorIsMultiple == false) {
					boolean ischecked = (Boolean) map.get(mFrom[4]);
					// 选中当前的时候，让上一个选中的取消选择
					if (ischecked) {
						if (lastSelectedFolderPosition >= 0 && lastSelectedFolderPosition != position) {
							lastCheckBox.setChecked(false);
							mData.get(lastSelectedFolderPosition).put(mFrom[4], false);
						}
					}
					lastSelectedFolderPosition = position;
					lastCheckBox = fun;
				}

			}
		});
	}

	/**
	 * 设置icon图标的宽度和高度
	 *
	 * @param holder
	 */
	private void setWidthAndHeight(ViewHoder holder) {
		// 由于getview要多次执行，这里判断下，如果都是按照原图大小走，没有重新设置，则不进行下面的操作
		ImageView iv = holder.icon;
		LayoutParams lp = iv.getLayoutParams();
		if (mSelectorIconWidth != -1) {
			lp.width = mSelectorIconWidth;
		}
		if (mSelectorIconHeight != -1) {
			lp.height = mSelectorIconHeight;
		}
		iv.setLayoutParams(lp);
	}

	public void setData(List<Map<String, Object>> data) {
		this.mData = data;
		// 重新刷新以后，让上一个记录的folderposition<0，这样不会影响下个页面的点击。
		lastSelectedFolderPosition = -1;
	}

	public List<Map<String, Object>> getData() {
		return mData;
	}

	public int getSelectorMode() {
		return mSelectorMode;
	}

	public void setSelectorMode(int mSelectorMode) {
		this.mSelectorMode = mSelectorMode;
	}

	public boolean getSelectorIsMultiple() {
		return mSelectorIsMultiple;
	}

	public void setSelectorIsMultiple(boolean mSelectorIsMultiple) {
		this.mSelectorIsMultiple = mSelectorIsMultiple;
	}

	public int getSelectorIconWidth() {
		return mSelectorIconWidth;
	}

	public void setSelectorIconWidth(int mSelectorIconWidth) {
		this.mSelectorIconWidth = mSelectorIconWidth;
	}

	public int getSelectorIconHeight() {
		return mSelectorIconHeight;
	}

	public void setSelectorIconHeight(int mSelectorIconHeight) {
		this.mSelectorIconHeight = mSelectorIconHeight;
	}

	class ViewHoder {
		ImageView icon;
		TextView fileName;
		TextView childNum;
		TextView createTime;
		CheckBox checkboxFun;
	}

}
