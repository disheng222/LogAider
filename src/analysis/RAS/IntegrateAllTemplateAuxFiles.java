package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

/**
 * 
 * Deprecated, because I can "cat all log files and then use helo to generate template"
 * @deprecated
 * @author fti
 *
 */
public class IntegrateAllTemplateAuxFiles {

	public static void main(String[] args)
	{
		if(args.length<2)
		{
			System.out.println("Usage: java InvertSortingTemplateID [templateDir] [extension]");
			System.out.println("Example: java InvertSortingTemplateID /home/fti/Catalog-project/miralog/output aux");
			System.exit(0);
		}
		
		String templateDir = args[0];
		String extension = args[1];
		String aggregatedFile = templateDir+"/aggregateTemplate.tpt";
		
		List<String> templateFileList = PVFile.getFiles(templateDir, extension);
		Collections.sort(templateFileList);
		
		List<String> resultList = new ArrayList<String>();
		
		Iterator<String> iter = templateFileList.iterator();
		while(iter.hasNext())
		{
			String templateName = iter.next();
			String templatePath = templateDir+"/"+templateName;
			List<String> lineList = PVFile.readFile(templatePath);

			Iterator<String> iter2 = lineList.iterator();
			while(iter2.hasNext())
			{
				String tLine = iter2.next();
				boolean tmatchr = false;
				boolean rmatcht = false;
				for(int i = 0;i<resultList.size();i++)
				{
					String rLine = resultList.get(i);
					if(tLine.startsWith("verification of the kernel shutdown failed.") && rLine.startsWith("verification of the kernel shutdown failed."))
					{
						System.out.println();
					}
					rmatcht = BuildMapping4LogandTemplate.matches(rLine, tLine);
					if(rmatcht)
					{
						if(!rLine.equals(rLine))
						{
							resultList.remove(rLine);
							resultList.add(tLine);
							i--;
						}
						break;
					}
					else
					{
						tmatchr = BuildMapping4LogandTemplate.matches(tLine, rLine);
						if(tmatchr)
							break;
					}
				}
				if(!rmatcht&&!tmatchr)
				{
					resultList.add(tLine);
				}	
			}
		}
		
		Collections.sort(resultList);
		PVFile.print2File(resultList, aggregatedFile);
		System.out.println("done: output: "+aggregatedFile);
	}
}
