package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import util.PVFile;

/**
 * don't have to use it any more, because the newly generated template seems to be no "overlap" templates anymore.
 * @deprecated
 * @author fti
 *
 */
public class InvertSortingTemplateID {

	public static void main(String[] args)
	{
		if(args.length<2)
		{
			System.out.println("Usage: java InvertSortingTemplateID [templateDir] [extension]");
			System.out.println("Example: java InvertSortingTemplateID /home/fti/Catalog-project/miralog aux");
			System.exit(0);
		}
		
		String templateDir = args[0];
		String extension = args[1];
		
		List<String> templateFileList = PVFile.getFiles(templateDir, extension);
		Collections.sort(templateFileList);
		
		Iterator<String> iter = templateFileList.iterator();
		while(iter.hasNext())
		{
			String templateName = iter.next();
			String templatePath = templateDir+"/"+templateName;
			List<String> lineList = PVFile.readFile(templatePath);
			Collections.sort(lineList);
			
			List<String> resultList = new ArrayList<String>();
			for(int p = lineList.size()-1;p>=0;p--)
				resultList.add(lineList.get(p));
			PVFile.print2File(resultList, templatePath);
			System.out.println(templatePath+" has been sorted.");
		}
		System.out.println("done.");
		
	}
}
