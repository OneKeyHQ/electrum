package dr.android.fileselector;

/**
 * 文件/文件夹路径中经常用到的常量
 *
 * @author DarkRanger
 * @datetime 20160530
 *
 */
public class FileSelectConstant {

	/** 文件选择模式下的标题 **/
	public static final String SELECTOR_MODE_FILE_TITLE = "选择文件";

	/** 文件夹选择模式下的标题 **/
	public static final String SELECTOR_MODE_FOLDER_TITLE = "选择路径";

	/** 存储设备的类型 **/
	public static final String SELECTOR_STORAGE_INNDER = "内部存储";
	public static final String SELECTOR_STORAGE_OUTTER = "外部存储";
	public static final String SELECTOR_STORAGE_USB = "USB存储";
	public static final String SELECTOR_STORAGE_OTHER = "其他存储";

	/** 设置 **/

	/** 获取settting的tag **/
	public static final String SETTINGS_TAG = "settings_tag";

	/** FileSelect **/
	public static final String SELECTOR_ROOT_PATH = "设备存储：";

	/** onActivityResult传递回来的bundle参数 **/
	public static final String SELECTOR_BUNDLE_PATHS = "selector_bundle_paths";
	/** 文件选择时的请求码的参数 **/
	public static final String SELECTOR_REQUEST_CODE_KEY = "selector_request_code_key";
	/** 是否多选模式请求码 **/
	public static final String SELECTOR_IS_MULTIPLE = "selector_is_multiple";
	/** 文件选择请求码 **/
	public static final Integer SELECTOR_MODE_FILE = 100;
	/** 文件夹选择请求码 **/
	public static final Integer SELECTOR_MODE_FOLDER = 200;

}
