package dr.android.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * LogUtils工具说明: 1 只输出等级大于等于LEVEL的日志 所以在开发和产品发布后通过修改LEVEL来选择性输出日志.
 * 当LEVEL=NOTHING则屏蔽了所有的日志. 2 v,d,i,w,e均对应两个方法. 若不设置TAG或者TAG为空则为设置默认TAG
 *
 * @author DarkRanger
 * @datetime 20160530
 */
public class LogUtil {
	@SuppressWarnings("unused")
	private static int NOTHING = 6;
	private static int VERBOSE = 1;
	private static int DEBUG = 2;
	private static int INFO = 3;
	private static int WARN = 4;
	private static int ERROR = 5;
	private static final String SEPARATOR = ", ";

	private static final int LEVEL = VERBOSE;

	public static void v(String message) {
		if (LEVEL <= VERBOSE) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			String tag = getDefaultTag(stackTraceElement);
			Log.v(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void v(String tag, String message) {
		if (LEVEL <= VERBOSE) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.v(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void d(String message) {
		if (LEVEL <= DEBUG) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			String tag = getDefaultTag(stackTraceElement);
			Log.d(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void d(String tag, String message) {
		if (LEVEL <= DEBUG) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.d(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void i(String message) {
		if (LEVEL <= INFO) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			String tag = getDefaultTag(stackTraceElement);
			Log.i(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void i(String tag, String message) {
		if (LEVEL <= INFO) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.i(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void w(String message) {
		if (LEVEL <= WARN) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			String tag = getDefaultTag(stackTraceElement);
			Log.w(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void w(String tag, String message) {
		if (LEVEL <= WARN) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.w(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void e(String message) {
		if (LEVEL <= ERROR) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			String tag = getDefaultTag(stackTraceElement);
			Log.e(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void e(String tag, String message) {
		if (LEVEL <= ERROR) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.e(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	/**
	 * 获取默认的TAG名称. 比如在MainActivity.java中调用了日志输出. 则TAG为MainActivity
	 */
	public static String getDefaultTag(StackTraceElement stackTraceElement) {
		String fileName = stackTraceElement.getFileName();
		String stringArray[] = fileName.split("\\.");
		String tag = stringArray[0];
		return tag;
	}

	/**
	 * 输出日志所包含的信息
	 */
	public static String getLogInfo(StackTraceElement stackTraceElement) {

		StringBuilder logInfoStringBuilder = new StringBuilder();
		// 获取线程名
		String threadName = Thread.currentThread().getName();
		// 获取线程ID
		long threadID = Thread.currentThread().getId();
		// 获取文件名.即xxx.java
		String fileName = stackTraceElement.getFileName();
		// 获取类名.即包名+类名
		String className = stackTraceElement.getClassName();
		// 获取方法名称
		String methodName = stackTraceElement.getMethodName();
		// 获取生日输出行数
		int lineNumber = stackTraceElement.getLineNumber();

		logInfoStringBuilder.append("[ ");
		logInfoStringBuilder.append("threadID=" + threadID).append(SEPARATOR);
		logInfoStringBuilder.append("threadName=" + threadName).append(SEPARATOR);
		logInfoStringBuilder.append("fileName=" + fileName).append(SEPARATOR);
		logInfoStringBuilder.append("className=" + className).append(SEPARATOR);
		logInfoStringBuilder.append("methodName=" + methodName).append(SEPARATOR);
		logInfoStringBuilder.append("lineNumber=" + lineNumber);
		logInfoStringBuilder.append(" ] ");

		// return logInfoStringBuilder.toString();
		return "";
	}
}
