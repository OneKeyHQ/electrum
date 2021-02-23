package dr.android.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.os.Environment;
import dr.android.fileselector.Storage;

/**
 *
 * SdCard相关工具类
 *
 * @author DarkRanger
 * @datetime 20160530
 *
 */
public class SdCardUtil {
	public static final List<String> sdList;

	static {
		sdList = getAllSdcardPath();
	}

	public static String getInnerExterPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static boolean isSdCardExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static List<String> getAllSdcardPath() {
		List<String> storageList = new ArrayList<String>();

		// 得到路径
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				// 将常见的linux分区过滤掉
				if (line.contains("secure")) {
					continue;
				}
				if (line.contains("asec")) {
					continue;
				}
				if (line.contains("system") || line.contains("cache") || line.contains("sys") || line.contains("data")
						|| line.contains("tmpfs") || line.contains("shell") || line.contains("root")
						|| line.contains("acct") || line.contains("proc") || line.contains("misc")
						|| line.contains("obb") || line.contains("/emulated") || line.contains("/shell")
						|| line.contains("/legacy") || line.contains("/firmware") || line.contains("/data")) {
					continue;
				}

				if (line.contains("fat") || line.contains("fuse")
						|| (line.contains("ntfs") || line.contains("mnt") || line.contains("sdcard"))) {

					if (line.toLowerCase(Locale.CHINA).contains("storage")) {
						String columns[] = line.split(" ");
						if (columns != null && columns.length > 1) {
							String path = columns[1].toLowerCase(Locale.CHINA);
							if (path != null && (path.contains("sd") || path.contains("usb")))
								storageList.add(columns[1]);
						}
					}
				}
			}
			if (!storageList.contains(getInnerExterPath())) {
				storageList.add(getInnerExterPath());
			}
			Collections.sort(storageList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return storageList;
	}

	/**
	 * 根据所有存储路径获取Storage实体类集合（不存在的路径不会添加到Storage里面）
	 *
	 * @param storagePaths
	 * @return
	 */
	public static List<Storage> getStorages() {
		LogUtil.v("getStoragesByPaths()");

		List<Storage> storages = new ArrayList<Storage>();
		for (String storagePath : SdCardUtil.sdList) {
			File file = new File(storagePath);
			if (!file.exists()) {
				continue;
			}
			Storage storage = new Storage(file.getAbsolutePath());
			storages.add(storage);
		}
		return storages;
	}

	/**
	 * 将/storage/sdcard替换成“sd卡存储”
	 *
	 * @param parentFolderPath
	 * @return
	 */
	public static String replaceAbsPathWithLocalName(String parentFolderPath) {
		LogUtil.v("replaceAbsPathWithLocalName()");

		List<Storage> storageList = getStorages();
		for (Storage storage : storageList) {
			if (parentFolderPath.contains(storage.getAbsPath())) {
				parentFolderPath = parentFolderPath.replace(storage.getAbsPath(), storage.getLocalName());
			}
		}
		return parentFolderPath;

	}

	/**
	 * 将“sd卡存储”替换成/storage/sdcard
	 *
	 * @param parentFolderPath
	 * @return
	 */
	public static String replaceLocalNameWithAbsPath(String parentFolderPath) {
		LogUtil.v("replaceLocalNameWithAbsPath()");

		List<Storage> storageList = getStorages();
		for (Storage storage : storageList) {
			if (parentFolderPath.contains(storage.getLocalName())) {
				parentFolderPath = parentFolderPath.replace(storage.getLocalName(), storage.getAbsPath());
			}
		}
		return parentFolderPath;
	}

}
