package dr.android.fileselector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.improver.lib.fileselector.fs.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import dr.android.utils.FileUtil;
import dr.android.utils.SdCardUtil;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FileSelectActivity extends AppCompatActivity implements OnItemClickListener, View.OnClickListener {
    public static final String TAG = "FileSelectActivity";

    private String mSelectorFileTitle = FileSelectConstant.SELECTOR_MODE_FILE_TITLE;

    private String mSelectorFolderTitle = FileSelectConstant.SELECTOR_MODE_FOLDER_TITLE;

    private String mSelectorRootPathName = FileSelectConstant.SELECTOR_ROOT_PATH;

    private int mSelectorMode = FileSelectConstant.SELECTOR_MODE_FILE;

    private int mSelectorFileIcon = R.drawable.ic_fileselect_file;

    private int mSelectorFolderIcon = R.drawable.ic_fileselect_folder;

    private int mSelectorIconWidth = -1;

    private int mSelectorIconHeight = -1;

    private boolean mSelectorIsMultiple = false;

    private TextView mFolderPath_tv;

    private ListView mFileSelectListView;

    private FileSelectAdapter mAdapter;

    private boolean isFileOnClickShowOk = true;

    private String[] mFrom;
    private int[] mTo;

    private List<Map<String, Object>> mData;

    /**
     * all path
     **/
    private List<String> rootPaths;
    private TextView btnPrevation;
    private String parentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
        initIntent();
        initParams();
        initData();
        initEvent();
    }


    private void initIntent() {
        // Get the file request code. The default is to select File 100
        mSelectorMode = getIntent().getIntExtra(FileSelectConstant.SELECTOR_REQUEST_CODE_KEY,
                FileSelectConstant.SELECTOR_MODE_FILE);
        String keyFile = getIntent().getStringExtra("keyFile");
        if (keyFile.equals("1")){
            btnPrevation.setText(getString(R.string.comfirm));
        }else{
            btnPrevation.setText(getString(R.string.preservations));
        }

        mSelectorIsMultiple = getIntent().getBooleanExtra(FileSelectConstant.SELECTOR_IS_MULTIPLE, false);
    }

    public void initParams() {

    }

    private void initView() {
        btnPrevation = findViewById(R.id.btn_Preservation);
        btnPrevation.setOnClickListener(this);
        mFolderPath_tv = (TextView) findViewById(R.id.id_fileselect_folderpath);
        mFileSelectListView = (ListView) findViewById(R.id.id_fileselect_listview);
        ImageView imgback = findViewById(R.id.img_back);
        imgback.setOnClickListener(this);
    }

    private void initData() {
//		if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FILE) {
//
//			actionbar.setTitle(mSelectorFileTitle);// Set the title to select document
//
//		} else if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FOLDER) {
//
//			actionbar.setTitle(mSelectorFolderTitle);// Set the title to select path
//
//		}
        mFolderPath_tv.setText(mSelectorRootPathName);

        mFrom = new String[]{"icon", " filename", "childnum", "createtime", "fun"};
        mTo = new int[]{R.id.id_fileselect_icon, R.id.id_fileselect_filename, R.id.id_fileselect_childnum,
                R.id.id_fileselect_createtime, R.id.id_fileselect_fun};

        rootPaths = SdCardUtil.sdList;

        mData = getDataByFolderPath(mSelectorRootPathName);

        File file = (File) mData.get(0).get("file");
        if (file.isFile()) {
            if (!isFileOnClickShowOk) {

            } else {

            }
        } else if (file.isDirectory()) {
            parentPath = file.getAbsolutePath();
            parentPath = SdCardUtil.replaceAbsPathWithLocalName(parentPath);
            mFolderPath_tv.setText(parentPath);
//            refreshByParentPath(parentPath);
        }

        mAdapter = new FileSelectAdapter(this, getDataByFolderPath(parentPath), R.layout.adapter_fileselect_item, mFrom, mTo);
        mAdapter.setSelectorMode(mSelectorMode);
        mAdapter.setSelectorIsMultiple(mSelectorIsMultiple);

        if (mSelectorIconWidth != -1) {
            mAdapter.setSelectorIconWidth(mSelectorIconWidth);
        }
        if (mSelectorIconHeight != -1) {
            mAdapter.setSelectorIconHeight(mSelectorIconHeight);
        }

        mFileSelectListView.setAdapter(mAdapter);

    }

    private void initEvent() {
        mFileSelectListView.setOnItemClickListener(this);
    }

    public List<Map<String, Object>> getDataByFolderPath(String parentFolderPath) {
        List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();

        // root directory
        if (parentFolderPath.equals(mSelectorRootPathName)) {

            for (Storage storage : SdCardUtil.getStorages()) {
                File storageFile = new File(storage.getAbsPath());

                String childNum = "";

                List<File> fileList = new ArrayList<File>();

                File[] files = storageFile.listFiles();
                if (null == files) {
                    throw new NullPointerException(
                            "Error: File[] files is null, please make sure that you have been added the two permissions: WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE!!!");
                }
                for (File child : files) {
                    if (child.getName().startsWith(".") || !child.exists()) {
                        continue;
                    }
                    if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FILE) {
                        fileList.add(child);
                    } else if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FOLDER) {
                        if (child.isDirectory()) {
                            fileList.add(child);
                        }
                    }
                }
                // sort
                FileUtil.sortByName(fileList);

                childNum = "共 " + fileList.size() + " 项";
                Map<String, Object> map = new HashMap<String, Object>();
                int iconId = storageFile.isFile() ? mSelectorFileIcon : mSelectorFolderIcon;
                map.put(mFrom[0], iconId);
                map.put(mFrom[1], storage.getLocalName());
                map.put(mFrom[2], childNum);
                map.put(mFrom[3], getLocalDateByMilliseconds(storageFile.lastModified(), "yyyy-MM-dd"));
                map.put(mFrom[4], false);
                map.put("file", storageFile);

                datalist.add(map);
            }

        } else {

            List<File> fileList = new ArrayList<File>();

            parentFolderPath = SdCardUtil.replaceLocalNameWithAbsPath(parentFolderPath);
            File folderPath = new File(parentFolderPath);
            File[] files = folderPath.listFiles();
            for (File child : files) {
                if (child.getName().startsWith(".") || !child.exists()) {
                    continue;
                }
                if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FILE) {
                    fileList.add(child);
                } else if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FOLDER) {
                    if (child.isDirectory()) {
                        fileList.add(child);
                    }
                }
            }
            // sort
            FileUtil.sortByName(fileList);

            for (int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                String childNum = null;
                if (file.isDirectory()) {
                    List<File> childFileList = new ArrayList<File>();
                    File[] childFiles = file.listFiles();
                    for (File child : childFiles) {
                        if (child.getName().startsWith(".") || !child.exists()) {
                            continue;
                        }
                        if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FILE) {
                            childFileList.add(child);
                        } else if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FOLDER) {
                            if (child.isDirectory()) {
                                childFileList.add(child);
                            }
                        }
                    }
                    childNum = "共 " + childFileList.size() + " 项";
                } else if (file.isFile()) {
                    childNum = FileUtil.convertFileSize(file.length());
                }

                // Add records according to other files
                Map<String, Object> map = new HashMap<String, Object>();
                int iconId = file.isFile() ? mSelectorFileIcon : mSelectorFolderIcon;
                map.put(mFrom[0], iconId);
                map.put(mFrom[1], file.getName());

                map.put(mFrom[2], childNum);
                map.put(mFrom[3], getLocalDateByMilliseconds(file.lastModified(), "yyyy-MM-dd"));
                map.put(mFrom[4], false);
                map.put("file", file);

                datalist.add(map);
            }
        }
        return datalist;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_select, menu);
        MenuItem item_ok = menu.findItem(R.id.action_fileselect_ok);
        item_ok.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    /**
     * actionbar Return key and finish key operations
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_fileselect_ok) {
            onClickOkBtn();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mData = getDataByFolderPath(mFolderPath_tv.getText().toString());
        File file = (File) mData.get(position).get("file");
        if (file.isFile()) {
            if (!isFileOnClickShowOk) {

            } else {

            }
        } else if (file.isDirectory()) {
            String parentPath = file.getAbsolutePath();
            parentPath = SdCardUtil.replaceAbsPathWithLocalName(parentPath);
            mFolderPath_tv.setText(parentPath);
            refreshByParentPath(parentPath);
        }
    }

    public void refreshByParentPath(String parentPath) {
        mAdapter.setData(getDataByFolderPath(parentPath));
        mAdapter.notifyDataSetChanged();

    }

    /**
     * Operation after clicking the "finish" button
     */
    public void onClickOkBtn() {
        if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FILE) {
            ArrayList<String> fileList = new ArrayList<String>();
            for (int i = 0; i < mAdapter.getCount(); i++) {
                Map<String, Object> map = mAdapter.getData().get(i);
                boolean isChecked = (boolean) map.get(mFrom[4]);
                File file = (File) map.get("file");
                if (file.isFile() && isChecked) {
                    fileList.add(file.getAbsolutePath());
                }
            }
            if (fileList.isEmpty()) {
                Toast.makeText(this, getString(R.string.nochoose), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(FileSelectConstant.SELECTOR_BUNDLE_PATHS, fileList);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

        } else if (mSelectorMode == FileSelectConstant.SELECTOR_MODE_FOLDER) {
            ArrayList<String> fileList = new ArrayList<String>();
            for (int i = 0; i < mAdapter.getCount(); i++) {
                Map<String, Object> map = mAdapter.getData().get(i);
                boolean isChecked = (boolean) map.get(mFrom[4]);
                File file = (File) map.get("file");
                if (file!=null){
                    if (file.isDirectory() && isChecked) {
                        fileList.add(file.getAbsolutePath());
                    }
                }
            }

            if (fileList.isEmpty()) {
                fileList.add( mFolderPath_tv.getText().toString());
                Intent intent = new Intent();
                intent.putExtra(FileSelectConstant.SELECTOR_BUNDLE_PATHS,fileList);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                Intent intent = new Intent();
                intent.putExtra(FileSelectConstant.SELECTOR_BUNDLE_PATHS, fileList);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        backOrExit();
    }

    public void backOrExit() {
        String folderPath = mFolderPath_tv.getText().toString();// 当前节点
        if (folderPath.equals(mSelectorRootPathName)) {
            finish();
        } else if (isFileOnClickShowOk) {
            folderPath = SdCardUtil.replaceLocalNameWithAbsPath(folderPath);
            if (rootPaths.contains(folderPath)) {
                mFolderPath_tv.setText(mSelectorRootPathName);
                refreshByParentPath(mSelectorRootPathName);
            } else {
                String parentPath = new File(folderPath).getParentFile().getAbsolutePath();// 返回上一级的节点
                refreshByParentPath(parentPath);
                mFolderPath_tv.setText(SdCardUtil.replaceAbsPathWithLocalName(parentPath));
            }
        }
    }

    public String getSelectorFileTitle() {
        return mSelectorFileTitle;
    }

    public void setSelectorFileTitle(String mSelectorFileTitle) {
        this.mSelectorFileTitle = mSelectorFileTitle;
    }

    public String getSelectorFolderTitle() {
        return mSelectorFolderTitle;
    }

    public void setSelectorFolderTitle(String mSelectorFolderTitle) {
        this.mSelectorFolderTitle = mSelectorFolderTitle;
    }

    public String getSelectorRootPathName() {
        return mSelectorRootPathName;
    }

    public void setSelectorRootPathName(String mSelectorRootPathName) {
        this.mSelectorRootPathName = mSelectorRootPathName;
    }

    public int getSelectorMode() {
        return mSelectorMode;
    }

    public void setSelectorMode(int mSelectorMode) {
        this.mSelectorMode = mSelectorMode;
    }

    public boolean isSelectorIsMultiple() {
        return mSelectorIsMultiple;
    }

    public void setSelectorIsMultiple(boolean mSelectorIsMultiple) {
        this.mSelectorIsMultiple = mSelectorIsMultiple;
    }

    public int getSelectorFolderIcon() {
        return mSelectorFolderIcon;
    }

    public void setSelectorFolderIcon(int mSelectorFolderIcon) {
        this.mSelectorFolderIcon = mSelectorFolderIcon;
    }

    public int getSelectorFileIcon() {
        return mSelectorFileIcon;
    }

    public void setSelectorFileIcon(int mSelectorFileIcon) {
        this.mSelectorFileIcon = mSelectorFileIcon;
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

    public String getLocalDateByMilliseconds(long milliseconds, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        String strDate = new SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.getTime());
        return strDate;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_Preservation) {
            onClickOkBtn();
        }else if (v.getId() == R.id.img_back){
            finish();
        }
    }
}
