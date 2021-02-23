package dr.android.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 文件相关工具类
 *
 * @author DarkRanger
 * @datetime 20160530
 *
 */
public class FileUtil {

	/**
	 * 根据文件得到文件的名称 比如abc.txt，返回abc。如果文件名是abc，则返回abc
	 *
	 * @param file
	 * @return
	 */
	public static String getFilePrefixName(File file) {
		String name = file.getName();
		if (name.contains(".")) {
			String prefix = name.substring(0, name.lastIndexOf("."));
			return prefix;
		} else {
			return name;
		}
	}

	/**
	 * 根据文件得到文件的后缀 比如abc.txt，返回.txt。如果文件名是abc，则返回"";
	 *
	 * @param file
	 * @return
	 */
	public static String getFileSuffix(File file) {
		String name = file.getName();
		if (name.contains(".")) {
			String suffix = name.substring(name.lastIndexOf("."));
			return suffix;
		} else {
			return "";
		}
	}

	/**
	 * 根据给出的父节点folder找到所有的file，如果不是folder，则返回size为0的集合。
	 *
	 * @param parentFile
	 * @return
	 */
	public static List<File> getChildFiles(File parentFile) {
		List<File> list = new ArrayList<File>();
		File[] files = parentFile.listFiles();
		if (files == null || files.length == 0) {
			return list;
		}
		for (File file : files) {
			list.add(file);
		}
		// 排序
		sortByName(list);
		return list;
	}

	/**
	 * 根据当前文件路径找到上级路径
	 *
	 * @param filepath
	 * @return
	 */
	public static String getParentFilePath(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return "";
		} else {
			return file.getParent();
		}

	}

	/**
	 * 根据文件名称对文件list集合进行排序（先文件夹，再文件，按文件名称排序，忽略大小写）
	 *
	 * 排序结果类似于这样:
	 *
	 * [F:\FileTest\b, F:\FileTest\d, F:\FileTest\A1.txt, F:\FileTest\a2.txt,
	 * F:\FileTest\A3.txt, F:\FileTest\B1.txt, F:\FileTest\b2.txt,
	 * F:\FileTest\B3.txt]
	 *
	 * @param fileList
	 * @return
	 */
	public static void sortByName(List<File> fileList) {

		Collections.sort(fileList, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile()) {
					return -1;
				}
				if (o1.isFile() && o2.isDirectory()) {
					return 1;
				}
				String o1FirstChar = o1.getName().substring(0, 1).toLowerCase(Locale.CHINA);
				String o2FirstChar = o2.getName().substring(0, 1).toLowerCase(Locale.CHINA);
				return o1FirstChar.compareTo(o2FirstChar);
			}
		});
	}

	/**
	 * 文件复制（通过文件通道FileChannel更加高效），目标文件不存在将会创建
	 *
	 * @param fromPath
	 *            from文件路径
	 * @param toPath
	 *            to文件路径
	 */
	public static void fileCopyByChannel(String fromPath, String toPath) {
		File fromFile = new File(fromPath);
		File toFile = new File(toPath);
		fileCopyByChannel(fromFile, toFile);
	}

	/**
	 * 文件复制（通过文件通道FileChannel更加高效），目标文件不存在将会创建
	 *
	 * @param fromFile
	 * @param toFile
	 */
	public static void fileCopyByChannel(File fromFile, File toFile) {
		if (!fromFile.exists()) {
			LogUtil.v("源文件： " + fromFile + "不存在，复制失败！");
			return;
		}
		if (!toFile.exists()) {
			createFileAndFolder(toFile);
		}
		FileInputStream in = null;
		FileOutputStream out = null;
		FileChannel channelIn = null;
		FileChannel channelOut = null;
		try {
			in = new FileInputStream(fromFile);
			out = new FileOutputStream(toFile);
			channelIn = in.getChannel();
			channelOut = out.getChannel();
			channelIn.transferTo(0, channelIn.size(), channelOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				channelIn.close();
				channelOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void fileCopyByInputStream(InputStream input, String toPath) {
		LogUtil.v("fileCopyByInputStream()");

		createFileAndFolder(toPath);

		InputStream in = input;
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(toPath));
			byte[] b = new byte[2048];
			int i;
			while ((i = in.read(b)) != -1) {
				out.write(b, 0, i);
			}
			LogUtil.v("字节流成功读取到文件\"" + toPath + "\"!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 删除路径下所有的文件和文件夹
	 *
	 * @param userDataDir
	 */
	public static void deleteByParentPath(File userDataFolder) {
		if (userDataFolder.exists()) {
			if (userDataFolder.isFile()) {
				userDataFolder.delete();

				LogUtil.i("删除文件：" + userDataFolder);

			} else {
				File[] files = userDataFolder.listFiles();
				for (File childFile : files) {
					deleteByParentPath(childFile);
				}
			}
		}
	}

	/**
	 * 以字节为单位读写文件内容
	 *
	 * @param filePath
	 *            ：需要读取的文件路径
	 */
	public static void copyFileByByte(String filePath, File toFile) {
		File file = new File(filePath);
		// InputStream:此抽象类是表示字节输入流的所有类的超类。
		InputStream ins = null;
		OutputStream outs = null;
		try {
			// FileInputStream:从文件系统中的某个文件中获得输入字节。
			ins = new FileInputStream(file);
			outs = new FileOutputStream(toFile);
			int temp;
			// read():从输入流中读取数据的下一个字节。
			while ((temp = ins.read()) != -1) {
				outs.write(temp);
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (ins != null && outs != null) {
				try {
					outs.close();
					ins.close();
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
		}
	}

	/**
	 * 以字符为单位读写文件内容
	 *
	 * @param filePath
	 */
	public static void copyFileByCharacter(String filePath, File toFile) {
		File file = new File(filePath);
		// FileReader:用来读取字符文件的便捷类。
		FileReader reader = null;
		FileWriter writer = null;
		try {
			reader = new FileReader(file);
			writer = new FileWriter(toFile);
			int temp;
			while ((temp = reader.read()) != -1) {
				writer.write((char) temp);
			}
		} catch (IOException e) {
			e.getStackTrace();
		} finally {
			if (reader != null && writer != null) {
				try {
					reader.close();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 以行为单位读写文件内容
	 *
	 * @param filePath
	 */
	public static void copyFileByLine(String filePath, File toFile) {
		File file = new File(filePath);
		// BufferedReader:从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取。
		BufferedReader bufReader = null;
		BufferedWriter bufWriter = null;
		try {
			// FileReader:用来读取字符文件的便捷类。
			bufReader = new BufferedReader(new FileReader(file));
			bufWriter = new BufferedWriter(new FileWriter(toFile));
			// buf = new BufferedReader(new InputStreamReader(new
			// FileInputStream(file)));
			String temp = null;
			while ((temp = bufReader.readLine()) != null) {
				bufWriter.write(temp + "\n");
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (bufReader != null && bufWriter != null) {
				try {
					bufReader.close();
					bufWriter.close();
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
		}
	}

	/**
	 * 使用Java.nio ByteBuffer字节将一个文件输出至另一文件
	 *
	 * @param filePath
	 */
	public static void copyFileByBybeBuffer(String filePath, File toFile) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			// 获取源文件和目标文件的输入输出流
			in = new FileInputStream(filePath);
			out = new FileOutputStream(toFile);
			// 获取输入输出通道
			FileChannel fcIn = in.getChannel();
			FileChannel fcOut = out.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				// clear方法重设缓冲区，使它可以接受读入的数据
				buffer.clear();
				// 从输入通道中将数据读到缓冲区
				int r = fcIn.read(buffer);
				if (r == -1) {
					break;
				}
				// flip方法让缓冲区可以将新读入的数据写入另一个通道
				buffer.flip();
				fcOut.write(buffer);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null && out != null) {
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * B-->KB-->M-->G之间的转换
	 *
	 * @param size
	 *            传进来的字节数（B）
	 * @return 返回类似于“12.56 KB”的string格式
	 */
	public static String convertFileSize(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format(Locale.CHINA, "%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(Locale.CHINA, f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(Locale.CHINA, f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format(Locale.CHINA, "%d B", size);
	}

	/**
	 * 根据文件路径创建文件，如果存在，不做任何操作。 如果文件不存在，则创建（如果父级folder不存在，则创建）
	 *
	 * @param filePath
	 */
	public static void createFileAndFolder(String filePath) {
		File targetFile = new File(filePath);
		createFileAndFolder(targetFile);
	}

	/**
	 * 根据文件路径创建文件，如果存在，不做任何操作。 如果文件不存在，则创建（如果父级folder不存在，则创建）
	 *
	 * @param filePath
	 */
	public static void createFileAndFolder(File targetFile) {
		if (!targetFile.exists()) {
			File parentFolder = targetFile.getParentFile();
			if (!parentFolder.exists()) {

				LogUtil.i("创建文件夹[" + parentFolder.getAbsolutePath() + "]成功！");

				parentFolder.mkdirs();

			}
			try {
				targetFile.createNewFile();

				LogUtil.i("文件：" + targetFile + "创建成功！");
			} catch (IOException e) {

				LogUtil.w("文件：" + targetFile + "创建失败！");

				e.printStackTrace();
			}
		}
	}

}
