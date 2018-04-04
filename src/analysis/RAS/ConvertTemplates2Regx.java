package analysis.RAS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

/**
 * 
 * I should write my own matcher instead of using regular expression because Ana's template is not standard regular expression.
 * @deprecated
 * @author fti
 *
 */
public class ConvertTemplates2Regx {

	
	public static void main(String[] args)
	{
		if(args.length<2)
		{
			System.out.println("Usage: java ConvertTemplate22Regx [inputTemplateDir] [extension]");
			System.out.println("Example: java ConvertTemplate22Regx /home/fti/Catalog-project/miralog/output aux");
			System.exit(0);
		}
		
		String inputTemplateDir = args[0];
		String extension = args[1];
		
		List<String> templateFileList = PVFile.getFiles(inputTemplateDir, extension);
		Iterator<String> iter = templateFileList.iterator();
		while(iter.hasNext())
		{
			String fileName = iter.next();
			String tmpFilePath = inputTemplateDir+"/"+fileName;
			List<String> lineList = PVFile.readFile(tmpFilePath);
			List<String> regList = convertTemplates2RegularExp(lineList);
			PVFile.print2File(regList, tmpFilePath.replace("_aux", ".reg"));
		}
		System.out.println("Output path = "+inputTemplateDir);
		System.out.println("done.");
	}
	
	private static List<String> convertTemplates2RegularExp(List<String> templateList)
	{
		List<String> regList = new ArrayList<String>();
		Iterator<String> iter = templateList.iterator();
		while(iter.hasNext())
		{
			String line = iter.next();
			line = line.replace("*", "(-|\\w)+");
			line = line.replace("d+", "(\\d+|(\\D\\d+)-(\\D\\d+)-(\\D\\d+)-(\\D\\d+))(\\.)?");
			line = line.replace(".", "\\.");
			line = line.replace(" n+", ".");
			regList.add(line);
		}
		return regList;
	}
}
