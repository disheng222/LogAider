package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PVFile {
	public static int number = 0;

	public static DecimalFormat df1 = new DecimalFormat("0000");
	public static DecimalFormat df2 = new DecimalFormat("00");
	public static DecimalFormat df3 = new DecimalFormat("000");
	public static DecimalFormat df4 = new DecimalFormat("0.00");
	
	public PVFile() {
	}

	public static List<String> getSubFile(String path) {
		List<String> list = new LinkedList<String>();
		List<String> ls = getDir(path);
		if (ls.size() > 0) {
			Iterator<String> it = ls.iterator();
			while (it.hasNext()) {
				String curpath = path + File.separator + it.next();
				List<String> sublist = getSubFile(curpath);
				list.addAll(sublist);
			}
		} else {
			ls = getFiles(path);
			if (ls.size() > 0) {
				Iterator<String> it = ls.iterator();
				while (it.hasNext()) {
					String fileName = path + File.separator + it.next();
					list.add(fileName);
				}
			}
		}
		return list;
	}

	public static List<String> getDir(String path) {
		List<String> list = getFileByType(path, 1);
		return list;
	}

	public static List<String> getFiles(String path) {
		List<String> list = getFileByType(path, 2);
		return list;
	}

	public static List<String> getFiles(String path, String extension) {
		List<String> newlist = new ArrayList<String>();
		List<String> list = getFiles(path);
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String fileName = it.next();
			if (fileName.endsWith(extension))
				newlist.add(fileName);
		}
		return newlist;
	}

	private static List<String> getFileByType(String path, int type) {
		List<String> list = new LinkedList<String>();
		List<String> ls = getLs(path);
		if (ls.size() > 0) {
			Iterator<String> it = ls.iterator();
			while (it.hasNext()) {
				String filename = it.next();
				File f = new File(path, filename);
				if (type == 1 && f.isDirectory())
					list.add(filename);
				else if (type == 2 && f.isFile())
					list.add(filename);
			}
		}
		return list;
	}

	private static List<String> getLs(String path) {
		List<String> list = new LinkedList<String>();
		File dir = new File(path);
		if (dir.isDirectory()) {
			String[] fileNames = dir.list();
			for (int i = 0; i < fileNames.length; i++) {
				list.add(fileNames[i]);
			}
		}
		return list;
	}

	/**
	 * Write a sentence to a file
	 * 
	 * @param filename
	 * @param line
	 * @throws IOException
	 */
	public static void writeRec(String filename, String line)
			throws IOException {
		File output = new File(filename);
		if (!output.exists()) {
			output.createNewFile();
		}
		RandomAccessFile raf = new RandomAccessFile(output, "rw");
		FileChannel fc = raf.getChannel();
		FileLock fl = fc.tryLock();

		if (fl.isValid()) {
			raf.seek(0);
			raf.writeBytes(String.valueOf(line));
			fl.release();
		}
		raf.close();
	}

	/**
	 * Read file's content
	 * 
	 * @param filename
	 * @return
	 */
	public static String read(String filename) {
		String line = "";
		try {
			File input = new File(filename);
			if (!input.exists()) {
				input.createNewFile();
			}
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					input), "GBK");
			BufferedReader reader = new BufferedReader(read);
			line = reader.readLine();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

	public static List<String> parseFile(String file) {
		List<String> list = new LinkedList<String>();
		try {
			File input = new File(file);
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					input), "utf-8");
			BufferedReader reader = new BufferedReader(read);
			String line = null;
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void createDir(String dirPath) {
		File f = new File(dirPath);
		if (!f.exists())
			f.mkdirs();
	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			System.out.println("Deleting File: " + fileName + " OK delete");
			return true;
		} else {
			System.out.println("Deleting File: " + fileName + " Failed delete");
			return false;
		}
	}

	public static boolean deleteDir(String dir) {

		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);

		if (!dirFile.exists() || !dirFile.isDirectory()) {
			System.out.println("delete dir failure: " + dir + "");
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}

			else {
				flag = deleteDir(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			System.out.println("failed of deleting file!");
			return false;
		}

		if (dirFile.delete()) {
			System.out.println("deleting " + dir + " OK delte");
			return true;
		} else {
			System.out.println("deleting " + dir + " failed delete");
			return false;
		}
	}

	public static void checkCreateDir(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists())
			dir.mkdirs();
	}

	public static boolean isExist(String path) {
		File file = new File(path);
		if (file.exists())
			return true;
		else
			return false;
	}

	public static File checkCreateFile(String filePath) {
		String[] s = filePath.split("/|\\\\");
		File file;
		String dirPath = "";
		for (int i = 0; i < s.length - 1; i++) {
			dirPath += s[i] + "/";
		}
		File dir = new File(dirPath);
		if (!dir.exists())
			dir.mkdirs();
		file = new File(filePath);
		try {
			if (!file.exists())
				file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	public static void delete(String filePath) {
		File file = new File(filePath);
		if (file.exists())
			if (!file.delete()) {
				System.err.println("Failure to delete " + file.getPath());
				System.exit(0);
			}
	}

	/**
	 * 
	 * @param srcDirPath
	 * @param extension
	 * @return
	 */
	public static List<String> getRecursiveFiles(String srcDirPath,
			String extension) {
		List<String> fileList = new ArrayList<String>();
		List<String> fileNameList = PVFile.getFiles(srcDirPath);
		Iterator<String> iter = fileNameList.iterator();
		for (; iter.hasNext(); number++) {
			String fileName = iter.next();
			if (fileName.endsWith(extension))
				fileList.add(srcDirPath + "/" + fileName);
			if (number % 4000 == 0)
				System.out.println("Load " + number + " " + extension
						+ "-files: " + srcDirPath);
		}

		List<String> dirList = PVFile.getDir(srcDirPath);
		Iterator<String> iter2 = dirList.iterator();
		while (iter2.hasNext()) {
			String dir = iter2.next();
			fileList.addAll(getRecursiveFiles(srcDirPath + "/" + dir, extension));
		}
		return fileList;
	}

	/**
	 * 
	 * @param data
	 * @param interval (in seconds), such as 10
	 * @param referencePeriod (in seconds), such as one day (86400) if the total period is one month
	 * @param fileName
	 */
	public static void print2File(int[] data, int interval, int referencePeriod, String filePath)
	{
		List<String> lineList = new ArrayList<String>();
		for(int i = 0;i<data.length;i++)
		{
			float time = ((float)i*interval)/referencePeriod;
			int value = data[i];
			StringBuilder sb = new StringBuilder();
			sb.append(time).append(" ").append(value);
			lineList.add(sb.toString());
		}
		PVFile.print2File(lineList, filePath);
	}
	
	public static void print2File(int[] data, String filePath)
	{
		List<String> lineList = new ArrayList<String>();
		for(int i = 0;i<data.length;i++)
		{
			int value = data[i];
			StringBuilder sb = new StringBuilder();
			sb.append(i).append(" ").append(value);
			lineList.add(sb.toString());
		}
		PVFile.print2File(lineList, filePath);
	}
	
	/**
	 * 
	 * @param readResult
	 * @param fileName
	 */
	public static List<String> readFile(String fileName) {
		if (!PVFile.isExist(fileName))
			return null;
		List<String> readResult = new ArrayList<String>();
		// try-catch block is used to catch any possible exception when
		// executing this program,
		// such as maybe the file named fileName doesn't exist at all.
		try {
			// FileReader is a class extendting InputStreamReader, and
			// InputStreamReader extends Reader.
			// Reader is the argument of RufferedReader(Reader in).
			// # FileReader() is a connection stream for characters, that
			// connects to a text file.
			// # BufferedReader can be viewed as a buffer used for higher
			// efficiency.
			FileReader fr = new FileReader(fileName);
			BufferedReader in = new BufferedReader(fr);
			String line;
			// read the text one line after another line, until no more line to
			// be read.
			while ((line = in.readLine()) != null) {
				// concatenate lines
				readResult.add(line);
			}
			in.close();
			fr.close();
			return readResult;
		} catch (Exception e) {
			// as long as JVM encounters an exception when executing the
			// program,
			// this catch(){} will catch it and do something.
			System.err.print(e);
		}
		return null;
	}

	public static File print2File(List list, String filePath) {
		File file = PVFile.checkCreateFile(filePath);
		try {
			FileWriter fw = new FileWriter(filePath);
			BufferedWriter bw = new BufferedWriter(fw);
			Iterator it = list.iterator();
			while (it.hasNext()) {
				String xyz = it.next().toString();
				bw.write(xyz);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	public static void showProgress(double initLogTime, int i, int size,
			String fileName) {
		String currentTime = getDateTime("HH:mm", new Date());
		long currentTimeValue = System.currentTimeMillis() / 1000;
		System.out.println(currentTime + " : already "
				+ (currentTimeValue - initLogTime) + " sec passed, (" + i + "/"
				+ size + "): " + fileName);
	}

	public static final String getDateTime(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";
		df = new SimpleDateFormat(aMask);
		returnValue = df.format(aDate);
		return (returnValue);
	}

	/**
	 * 
	 * @param partitions
	 *            the number of lines of each partitioned file
	 * @param times
	 *            the first times"th" files will be generated
	 * @param filePath
	 *            the path of file to be read
	 * @param outputDir
	 *            the partioned files will be put in here
	 */
	public static void splitFile(int partitions, int times, String filePath,
			String outputDir) {
		ArrayList<String> readResult = new ArrayList<String>();
		// try-catch block is used to catch any possible exception when
		// executing this program,
		// such as maybe the file named fileName doesn't exist at all.
		try {
			// FileReader is a class extendting InputStreamReader, and
			// InputStreamReader extends Reader.
			// Reader is the argument of RufferedReader(Reader in).
			// # FileReader() is a connection stream for characters, that
			// connects to a text file.
			// # BufferedReader can be viewed as a buffer used for higher
			// efficiency.
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			String line;
			int j = 1;
			int k = 0;
			// read the text one line after another line, until no more line to
			// be read.
			for (int i = 0; (line = in.readLine()) != null; i++) {
				// concatenate lines
				readResult.add(line);
				if (i % partitions == 0 && i > 0) {
					k++;
					print2File(readResult, outputDir + "/" + j + ".data");
					System.out.println("outputFile:" + outputDir + "/" + j
							+ ".data");
					j++;
					readResult.clear();
					if (k >= times) {
						System.out.println("done.");
						System.exit(0);
					}
				}
			}
			print2File(readResult, outputDir + "/" + j + ".data");
			j++;
			System.out.println("outputFile:" + outputDir + "/" + j + ".data");
		} catch (Exception e) {
			// as long as JVM encounters an exception when executing the
			// program,
			// this catch(){} will catch it and do something.
			System.err.print(e);
		}
	}
	
	public static void writeObjectAndZip(Object object, String zipFilePath)
	{
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
			ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
			objectOut.writeObject(object);
			objectOut.close();
			byte[] bytes = baos.toByteArray();
			
			PVFile.writeObject(bytes, zipFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object readZipFile2Object(String zipFilePath)
	{
		Object obj;
		try {
			byte[] bytes = (byte[]) PVFile.readObject(zipFilePath);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			GZIPInputStream gzipIn = new GZIPInputStream(bais);
			ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
			obj = objectIn.readObject();
			objectIn.close();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Uncompress the incoming file.
	 * 
	 * @param inFileName
	 *            Name of the file to be uncompressed
	 */
	private static void doUncompressFile(String inFileName) {

		try {

			if (!getExtension(inFileName).equalsIgnoreCase("gz")) {
				System.err.println("File name must have extension of \".gz\"");
				System.exit(1);
			}

			System.out.println("Opening the compressed file.");
			GZIPInputStream in = null;
			try {
				in = new GZIPInputStream(new FileInputStream(inFileName));
			} catch (FileNotFoundException e) {
				System.err.println("File not found. " + inFileName);
				System.exit(1);
			}

			System.out.println("Open the output file.");
			String outFileName = getFileName(inFileName);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(outFileName);
			} catch (FileNotFoundException e) {
				System.err.println("Could not write to file. " + outFileName);
				System.exit(1);
			}

			System.out.println("Transfering bytes from compressed file to the output file.");
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			System.out.println("Closing the file and stream");
			in.close();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Used to extract and return the extension of a given file.
	 * 
	 * @param f
	 *            Incoming file to get the extension of
	 * @return <code>String</code> representing the extension of the incoming
	 *         file.
	 */
	public static String getExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');

		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1);
		}
		return ext;
	}

	/**
	 * Used to extract the filename without its extension.
	 * 
	 * @param f
	 *            Incoming file to get the filename
	 * @return <code>String</code> representing the filename without its
	 *         extension.
	 */
	public static String getFileName(String f) {
		String fname = "";
		int i = f.lastIndexOf('.');

		if (i > 0 && i < f.length() - 1) {
			fname = f.substring(0, i);
		}
		return fname;
	}

	public static void append2File(List<String> list, String filePath) {
		PVFile.checkCreateFile(filePath);
		try {
			FileWriter fw = new FileWriter(filePath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			Iterator<String> it = list.iterator();
			while (it.hasNext()) {
				String xyz = it.next();
				bw.write(xyz);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Note that the FileWriter must be instantiated with "true" argument.
	 * Example: FileWriter fw = new FileWriter(filePath, true); BufferedWriter
	 * bw = new BufferedWriter(fw); append2File(line,bw); bw.close();
	 * 
	 * @param line
	 * @param fw
	 * @throws IOException
	 */
	public static void append2File(String line, BufferedWriter bw)
			throws IOException {
		bw.append(line);
	}

	
	public static void writeObject(Object object, String objectFilePath)
	{
		File file = PVFile.checkCreateFile(objectFilePath);

        try {
			ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file));
	        oout.writeObject(object);
	        oout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Object readObject(String objectFilePath)
	{
		File file = new File(objectFilePath);
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			Object o = in.readObject();
			in.close();
			return o;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * format: 
	 * #
R0 0 1 0 1 3 1 3 1 0 2 3 2 3 2 0 0
R1 0 0 0 0 0 0 1 0 1 0 0 0 0 2 3 2
R2 0 3 0 3 0 0 3 3 0 0 0 0 0 0 0 2
R3 1 0 0 1 1 1 3 0 0 2 0 2 0 2 0 2
R4 3 0 3 3 3 3 3 3 3 2 3 2 0 2 3 2
R5 0 0 3 0 0 1 3 1 0 2 3 2 0 2 3 2
	 * @param filePath
	 * @return
	 */
	public static int[][] readFileTo2DIntArray(String filePath)
	{
		int[][] result = null;
		List<String> lineList = readFile(filePath);
		int rows = 0, columns = 0;
		Iterator<String> iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			if(columns==0)
				columns = line.split("\\s").length-1;
			rows++;
		}
		
		result = new int[rows][columns];
		
		int i = 0;
		iter = lineList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			if(line.startsWith("#"))
				continue;
			
			String[] s = line.split("\\s");
			for(int j = 0;j<s.length-1;j++)
			{
				result[i][j] = Integer.parseInt(s[j+1]);
			}
			i++;
		}
		return result;
	}
	
	public static void main(String[] args) {
		String filePath = "inputFile";
		String outputDir = "outputFile";
		splitFile(1000, 2, filePath, outputDir);
		System.out.println("done.");
	}

}