package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import element.Field;
import util.ConversionHandler;
import util.PVFile;

/**
 * build a mapping between log file and HELO template. 
 * This class is only useful for RAS analysis, because job log has no description field.
 * @author fti
 *
 */
public class BuildMapping4LogandTemplate {

	public static void main(String[] args)
	{
		if(args.length<6)
		{
			System.out.println("Usage: java BuildMapping4LogandTemplate [schemaPath] [descriptionFieldIndex] [logDir] [log_extension] [templateDir] [templateFileName]");
			System.out.println("Example: java BuildMapping4LogandTemplate /home/fti/Catalog-project/miralog/schema/basicSchema.txt 13 /home/fti/Catalog-project/miralog csv /home/fti/Catalog-project/miralog/output eventlog.20150401-20150501.csv_aux");
			System.exit(0);
		}
		
		String schemaPath = args[0];
		int descIndex = Integer.parseInt(args[1]);
		String inputDir = args[2];
		String logExt = args[3];
		String tmpDir = args[4];
		String tmpFileName = args[5];
		String outputDir = inputDir;
		
		System.out.println("Loading basic schema....");
		List<Field> fieldList = ExtractValueTypes4EachField.loadBasicSchema(schemaPath);
		Field[] fields = ConversionHandler.convertFieldList2FieldArray(fieldList);
		
		List<String> inputFileList = PVFile.getFiles(inputDir, logExt);
		Collections.sort(inputFileList);
		//List<String> templateFileList = PVFile.getFiles(tmpDir, tmpExt);
		//Collections.sort(templateFileList);
		
		System.out.println("Processing the files....");
		
		String tmpFilePath = tmpDir+"/"+tmpFileName;
		List<String> templateList = PVFile.readFile(tmpFilePath);
		int[] templateCount = new int[templateList.size()];
		for(int i = 0;i<templateCount.length;i++)
			templateCount[i] = 0;
		
		Iterator<String> iter = inputFileList.iterator();
		//Iterator<String> iter2 = templateFileList.iterator();
		double initLogTime = System.currentTimeMillis()/1000.0;
		for(int s = 1;iter.hasNext();s++)
		{
			String fileName = iter.next();
			//String tmpFileName = iter2.next();
			
/*			if(!checkMatches4TemplateAndLog(tmpFileName, fileName))
			{
				System.out.println("Error: tmpFileName and fileName don't match.");
				System.out.println("tmpFileName: "+tmpFileName);
				System.out.println("logFileName: "+fileName);
				System.exit(0);
			}
			*/
			String filePath = inputDir+"/"+fileName;
			
			System.out.println(templateList.size()+" patterns are founded in the template file path: "+tmpFilePath);
			
			System.out.println("Reading "+filePath);	
			List<String> lineList = PVFile.readFile(filePath);
			System.out.println("Extracting the descprition ....");
			List<String> msgList = new ArrayList<String>();
			Iterator<String> iter3 = lineList.iterator();
			double initLogTime2 = System.currentTimeMillis()/1000.0;
			for(int p=0;iter3.hasNext();p++)
			{
				String line = iter3.next();
				String[] data = line.split(",");
				data = ExtractValueTypes4EachField.fixData(data, fields);
				String desc = data[descIndex].trim();
				int id = matches(desc, templateList);
				if(id>=0)
				{
					msgList.add(line+","+id);
					templateCount[id]++;
				}
				else
				{
					System.out.println("Template file: "+tmpFilePath);
					System.exit(0);
				}
				if(p%3000==0)
					PVFile.showProgress(initLogTime2, p, lineList.size(), fileName);
			}
			PVFile.print2File(msgList, outputDir+"/"+fileName+"tmp");
			PVFile.showProgress(initLogTime, s, inputFileList.size(), fileName);
		}
		
		List<String> tmpCountList = ConversionHandler.convertIntArray2StringList(templateCount);
		List<String> tmpCountList2 = new ArrayList<String>();
		for(int i = 0;i<tmpCountList.size();i++)
			tmpCountList2.add(i+" "+tmpCountList.get(i));
		PVFile.print2File(tmpCountList2, outputDir+"/"+tmpFileName.replace("_aux", "")+".tmpcnt");
		
		System.out.println("outputDir: "+outputDir);
		System.out.println("done.");
	}
	
//	private static List<Pattern> buildPatternList(List<String> templateList)
//	{
//		List<Pattern> patternList = new ArrayList<Pattern>();
//		Iterator<String> iter = templateList.iterator();
//		while(iter.hasNext())
//		{
//			String template = iter.next();
//			Pattern pattern = Pattern.compile( 
//					template,
//			        Pattern.CASE_INSENSITIVE 
//			    );
//			patternList.add(pattern);
//		}
//		return patternList;
//	}
	
	private static boolean checkMatches4TemplateAndLog(String templateFileName, String logFileName)
	{
		String fileName = templateFileName.replace("_aux", "");
		if(fileName.equals(logFileName))
			return true;
		else 
			return false;
	}
	
	private static boolean isNum(String str)
	{
		if(str.startsWith("0x"))
		{
			return true;
		}
		else 
		{
			char c = str.toCharArray()[0];
			if(Character.isDigit(c))
				return true;
		}
		return false;
	}
	
	public static boolean matches(String descMsg, String pattern)
	{
		boolean match = true;
		descMsg = descMsg.toLowerCase().trim();
		pattern = pattern.trim();
		descMsg = descMsg.replace(".", "");
		pattern = pattern.replace(".", "");
		String[] p = pattern.split("\\s+");
		String[] d = descMsg.split("\\s+");
		for(int i = 0;i<p.length;i++)
		{
			if(d.length < p.length && !p[p.length-1].equals("n+"))
			{
				match = false;
				break;
			}
			
			String s = p[i];
			if(s.contains("*"))
			{
				if(s.contains("="))
				{
					if(!d[i].contains("="))
					{
						match = false;
						break;
					}
					else
					{
						String key = s.split("=")[0].trim();
						String key_ = d[i].split("=")[0].trim();
						if(!key.equals(key_))
						{
							match = false;
							break;
						}
					}
					
				}
			}
			else if(s.equals("d+"))
			{
				if(!isNum(d[i])&&!d[i].equals("d+"))
				{
					match = false;
					break;
				}
			}
			else if(s.equals("n+"))
			{
				match = true;
				break;
			}
			else //compare two strings
			{
				if(!p[i].equals(d[i]))
				{
					match = false;
					break;
				}
			}
		}
		return match;
	}
	
	/**
	 * 
	 * @param descMsg
	 * @param templates
	 * @return template id (i.e., the line number in the template file, starting from 0)
	 */
	public static int matches(String descMsg, List<String> templateList)
	{
		int id = 0;
		boolean match = false;
		Iterator<String> iter = templateList.iterator();
		while(iter.hasNext())
		{
			String pattern = iter.next();

			if(matches(descMsg, pattern))
			{
				match = true;
				break;
			}
			id++;
		}
		if(match)
			return id;
		else
		{
			System.out.println("Error: message doesn't match any templates....");
			System.out.println("descMsg: "+descMsg);
			return -1;
		}
	}
}
