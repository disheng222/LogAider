/**
 * @author Sheng Di
 * @class BuildFieldValueCombination.java
 * @description  
 */

package analysis.RAS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import element.Field;
import element.FieldCombination;
import element.UserField;
import element.ValueCombination;
import util.ConversionHandler;
import util.PVFile;

/**
 * Generate /home/fti/Catalog-project/miralog/fieldCombination based on schema and featureStates
 * @author fti
 *
 */
public class BuildFieldValueCombination {

	static int maxElements = 5; //max number of fields in one combination
	static String[] fields;
	static int[] fieldIndex;
	
	static HashMap<String, String> featureStateMap = new HashMap<String, String>();
	static HashMap<String, Integer> fieldIDMap = new HashMap<String, Integer>();
	
	public static void main(String[] args)
	{
		if(args.length<8)
		{
			System.out.println("Usage: java BuildFieldCombination [maxElementCount] [basicSchemaFile] [fullSchemaDir] [extension] [featureStateDir] [fsExt] [outputDir] [fieldNames....]");
			System.out.println("Example: java BuildFieldCombination 5 /home/fti/Catalog-project/miralog/schema/basicSchema2.txt /home/fti/Catalog-project/miralog/schema/fullSchema/withRatio fsr /home/fti/Catalog-project/miralog/featureState pr /home/fti/Catalog-project/miralog/fieldCombination "
					+ "CATEGORY COMPONENT CTLOCATION MSG_ID SEVERITY TEMPLATE");
			System.exit(0);
		}
		
		maxElements = Integer.parseInt(args[0]);
		String basicSchemaFile = args[1];
		String fullSchemaDir = args[2];
		String fullSchemaExtension = args[3];
		String featureStateDir = args[4];
		String fsExt = args[5];
		String outputDir = args[6];
		
		System.out.println("Loading field values based on schemeDir");
		List<Field> completeFieldList = ExtractValueTypes4EachField.loadBasicSchema(basicSchemaFile);
		Field[] completeFields = ConversionHandler.convertFieldList2FieldArray(completeFieldList);
		for(int i = 0;i<completeFields.length;i++)
			fieldIDMap.put(completeFields[i].getFieldName(), completeFields[i].getIndex());
		
		List<UserField> fieldList = new ArrayList<UserField>();
		for(int i = 0;i<args.length-7;i++)
		{
			String fieldName = args[i+7];
			UserField uf = new UserField(fieldName);
			fieldList.add(uf);
			int fieldIndex = fieldIDMap.get(fieldName);
			uf.setFieldIndex(fieldIndex);
		}
		Collections.sort(fieldList);
		
		fields = new String[fieldList.size()];
		fieldIndex = new int[fields.length];
		Iterator<UserField> uit = fieldList.iterator();
		for(int i = 0;uit.hasNext();i++)
		{
			UserField uf = uit.next();
			fields[i] = uf.getFieldName();
			fieldIndex[i] = uf.getFieldIndex();
		}
		
		List<String>[] fieldValues = new ArrayList[fields.length];
		for(int i = 0;i<fieldValues.length;i++)
		{
			fieldValues[i] = new ArrayList<String>();
			String schemaFilePath = fullSchemaDir+"/"+fields[i]+"."+fullSchemaExtension;
			List<String> lineList = PVFile.readFile(schemaFilePath);
			Iterator<String> iter = lineList.iterator();
			while(iter.hasNext())
			{
				String line = iter.next();
				if(!line.startsWith("#"))
				{
					fieldValues[i].add(line.split("\\s")[0]);
				}
			}
		}
		
		List<String> fieldStringList = new ArrayList<String>();
		for(int i = 0;i<fields.length;i++)
			fieldStringList.add(fields[i]);
		
		System.out.println("Loading feature state dir.....");
		List<ValueCombination> vcList = loadFeatureStates(featureStateDir, fsExt, fieldStringList);
		List<String> totalList2 = new ArrayList<String>();
		ValueCombination[] fsValueComb = new ValueCombination[vcList.size()];
		
		System.out.println("Constructing the map based on vcList");
		Iterator<ValueCombination> iter = vcList.iterator();
		for(int i = 0;iter.hasNext();i++)
		{
			ValueCombination vc = iter.next();
			featureStateMap.put(vc.getID(), null);
			totalList2.add(vc.getID());
			fsValueComb[i] = vc;
		}
		
		System.out.println("Start building the complete combinations.....");
		List<String> totalList3 = new ArrayList<String>();
		List<String> totalList4 = new ArrayList<String>();
		List<String> totalList5 = new ArrayList<String>();
		updateValueCombinationList(fields, fieldValues, totalList3, totalList4, totalList5);
		
		System.out.println("Writing raw results to "+outputDir);
		PVFile.print2File(totalList2, outputDir+"/vcList2.nam");
		PVFile.print2File(totalList3, outputDir+"/vcList3.nam");
		PVFile.print2File(totalList4, outputDir+"/vcList4.nam");
		PVFile.print2File(totalList5, outputDir+"/vcList5.nam");
		
		System.out.println("Writing index results to "+outputDir);
		generateValueCombinationListWithIndex(totalList2,  outputDir+"/vcList2.idx");
		generateValueCombinationListWithIndex(totalList3,  outputDir+"/vcList3.idx");
		generateValueCombinationListWithIndex(totalList4,  outputDir+"/vcList4.idx");
		generateValueCombinationListWithIndex(totalList5,  outputDir+"/vcList5.idx");
		
		List<String> fieldWithIndexList = new ArrayList<String>(fieldList.size());
		fieldWithIndexList.add("# fieldName fieldIndex");
		Iterator<String> itt = fieldStringList.iterator();
		while(itt.hasNext())
		{
			String fieldName = itt.next();
			int index = fieldIDMap.get(fieldName);
			fieldWithIndexList.add(fieldName+" "+index);
		}
		PVFile.print2File(fieldWithIndexList, outputDir+"/fieldList.txt");
		
		System.out.println("done.");
	}
	
	public static void generateValueCombinationListWithIndex(List<String> totalList, String outputFilePath)
	{
		List<String> newList = new ArrayList<String>();
		Iterator<String> iter = totalList.iterator();
		while(iter.hasNext())
		{
			StringBuilder sb = new StringBuilder();
			String vc = iter.next();
			String[] s = vc.split(",");
			
			String[] ss = s[0].split(":");
			String fieldName = ss[0];
			int index = fieldIDMap.get(fieldName);
			sb.append(index).append(":").append(ss[1]);
			
			for(int i = 1;i<s.length;i++)
			{
				ss = s[i].split(":");
				fieldName = ss[0];
				index = fieldIDMap.get(fieldName);
				sb.append(",").append(index).append(":").append(ss[1]);
			}
			String newVC = sb.toString();
			newList.add(newVC);
		}
		PVFile.print2File(newList, outputFilePath);
	}
	
	public static void updateValueCombinationList(String[] fields, List<String>[] fieldValues, 
			List<String> totalList3, List<String> totalList4, List<String> totalList5)
	{
		int n = fields.length;
		double initLogTime = System.currentTimeMillis()/1000.0;
		if(maxElements >=3 )
		{
			for(int i = 0;i<n-2;i++)
			{
				for(int j = i+1;j<n-1;j++)
					for(int k = j+1;k<n;k++)
					{
						String[] fields3 = new String[3];
						fields3[0] = fields[i];
						fields3[1] = fields[j];
						fields3[2] = fields[k];
						List<String>[] fieldValues3 = new ArrayList[3];
						fieldValues3[0] = fieldValues[i];
						fieldValues3[1] = fieldValues[j];
						fieldValues3[2] = fieldValues[k];
						
						List<String> tmpvcList = buildValueCombination(fields3, fieldValues3);
						totalList3.addAll(tmpvcList);
					}
				PVFile.showProgress(initLogTime, i, n-2, "# elements = 3");
			}
		}
		
		if(maxElements >= 4)
		{
			for(int i = 0;i<n-3;i++)
			{
				for(int j = i+1;j<n-2;j++)
					for(int k = j+1;k<n-1;k++)
						for(int p = k+1;p<n;p++)
						{
							String[] fields4 = new String[4];
							fields4[0] = fields[i];
							fields4[1] = fields[j];
							fields4[2] = fields[k];
							fields4[3] = fields[p];
							List<String>[] fieldValues4 = new ArrayList[4];
							fieldValues4[0] = fieldValues[i];
							fieldValues4[1] = fieldValues[j];
							fieldValues4[2] = fieldValues[k];
							fieldValues4[3] = fieldValues[p];
							
							List<String> tmpvcList = buildValueCombination(fields4, fieldValues4);
							totalList4.addAll(tmpvcList);
						}
				PVFile.showProgress(initLogTime, i, n-2, "# elements = 4");
			}
		}
		
		if(maxElements >= 5)
		{
			for(int i = 0;i<n-4;i++)
			{
				for(int j = i+1;j<n-3;j++)
					for(int k = j+1;k<n-2;k++)
						for(int p = k+1;p<n-1;p++)
							for(int q = p+1;q<n;q++)
							{
								String[] fields5 = new String[5];
								fields5[0] = fields[i];
								fields5[1] = fields[j];
								fields5[2] = fields[k];
								fields5[3] = fields[p];
								fields5[4] = fields[q];
								List<String>[] fieldValues5 = new ArrayList[5];
								fieldValues5[0] = fieldValues[i];
								fieldValues5[1] = fieldValues[j];
								fieldValues5[2] = fieldValues[k];
								fieldValues5[3] = fieldValues[p];
								fieldValues5[4] = fieldValues[q];
								List<String> tmpvcList = buildValueCombination(fields5, fieldValues5);
								totalList5.addAll(tmpvcList);							
							}
				PVFile.showProgress(initLogTime, i, n-2, "# elements = 5");
			}
		}
	}
	
	public static List<String> buildValueCombination(String[] fields, List<String>[] fieldValues)
	{
		List<String> resultList = new ArrayList<String>();
		if(fields.length==3)
		{
			Iterator<String> iter1 = fieldValues[0].iterator();
			while(iter1.hasNext())
			{
				String f1 = fields[0];
				String v1 = iter1.next();
				Iterator<String> iter2 = fieldValues[1].iterator();
				while(iter2.hasNext())
				{
					String f2 = fields[1];
					String v2 = iter2.next();
					Iterator<String> iter3 = fieldValues[2].iterator();
					while(iter3.hasNext())
					{
						String f3 = fields[2];
						String v3 = iter3.next();
						
						StringBuilder sb12 = new StringBuilder();
						sb12.append(f1).append(":").append(v1).append(",");
						sb12.append(f2).append(":").append(v2);
						String id12 = sb12.toString();
						if(featureStateMap.containsKey(id12))
						{
							StringBuilder sb13 = new StringBuilder();
							sb13.append(f1).append(":").append(v1).append(",");
							sb13.append(f3).append(":").append(v3);
							String id13 = sb13.toString();
							if(featureStateMap.containsKey(id13))
							{
								StringBuilder sb23 = new StringBuilder();
								sb23.append(f2).append(":").append(v2).append(",");
								sb23.append(f3).append(":").append(v3);
								String id23 = sb23.toString();
								if(featureStateMap.containsKey(id23))
								{
									sb12.append(",").append(f3).append(":").append(v3);
									resultList.add(sb12.toString());
								}
							}
						}
					}
				}
			}
		}
		else if(fields.length==4)
		{
			Iterator<String> iter1 = fieldValues[0].iterator();
			while(iter1.hasNext())
			{
				String f1 = fields[0];
				String v1 = iter1.next();
				Iterator<String> iter2 = fieldValues[1].iterator();
				while(iter2.hasNext())
				{
					String f2 = fields[1];
					String v2 = iter2.next();
					Iterator<String> iter3 = fieldValues[2].iterator();
					while(iter3.hasNext())
					{
						String f3 = fields[2];
						String v3 = iter3.next();
						Iterator<String> iter4 = fieldValues[3].iterator();
						while(iter4.hasNext())
						{
							String f4 = fields[3];
							String v4 = iter4.next();
							StringBuilder sb12 = new StringBuilder();
							sb12.append(f1).append(":").append(v1).append(",");
							sb12.append(f2).append(":").append(v2);
							String id12 = sb12.toString();
							if(featureStateMap.containsKey(id12))
							{
								StringBuilder sb13 = new StringBuilder();
								sb13.append(f1).append(":").append(v1).append(",");
								sb13.append(f3).append(":").append(v3);
								String id13 = sb13.toString();
								if(featureStateMap.containsKey(id13))
								{
									StringBuilder sb14 = new StringBuilder();
									sb14.append(f1).append(":").append(v1).append(",");
									sb14.append(f4).append(":").append(v4);
									String id14 = sb14.toString();
									if(featureStateMap.containsKey(id14))
									{
										StringBuilder sb23 = new StringBuilder();
										sb23.append(f2).append(":").append(v2).append(",");
										sb23.append(f3).append(":").append(v3);
										String id23 = sb23.toString();
										if(featureStateMap.containsKey(id23))
										{
											StringBuilder sb24 = new StringBuilder();
											sb24.append(f2).append(":").append(v2).append(",");
											sb24.append(f4).append(":").append(v4);
											String id24 = sb24.toString();
											if(featureStateMap.containsKey(id24))
											{
												StringBuilder sb34 = new StringBuilder();
												sb34.append(f3).append(":").append(v3).append(",");
												sb34.append(f4).append(":").append(v4);
												String id34 = sb34.toString();
												if(featureStateMap.containsKey(id34))
												{
													sb12.append(",").append(f3).append(":").append(v3);
													sb12.append(",").append(f4).append(":").append(v4);
													resultList.add(sb12.toString());
												}
											}
										}
									}
								}
							}	
						}
					}
				}
			}
		}
		else if(fields.length==5)
		{
			Iterator<String> iter1 = fieldValues[0].iterator();
			while(iter1.hasNext())
			{
				String f1 = fields[0];
				String v1 = iter1.next();
				Iterator<String> iter2 = fieldValues[1].iterator();
				while(iter2.hasNext())
				{
					String f2 = fields[1];
					String v2 = iter2.next();
					Iterator<String> iter3 = fieldValues[2].iterator();
					while(iter3.hasNext())
					{
						String f3 = fields[2];
						String v3 = iter3.next();
						Iterator<String> iter4 = fieldValues[3].iterator();
						while(iter4.hasNext())
						{
							String f4 = fields[3];
							String v4 = iter4.next();
							Iterator<String> iter5 = fieldValues[4].iterator();
							while(iter5.hasNext())
							{
								String f5 = fields[4];
								String v5 = iter5.next();
								StringBuilder sb12 = new StringBuilder();
								sb12.append(f1).append(":").append(v1).append(",");
								sb12.append(f2).append(":").append(v2);
								String id12 = sb12.toString();
								if(featureStateMap.containsKey(id12))
								{
									StringBuilder sb13 = new StringBuilder();
									sb13.append(f1).append(":").append(v1).append(",");
									sb13.append(f3).append(":").append(v3);
									String id13 = sb13.toString();
									if(featureStateMap.containsKey(id13))
									{
										StringBuilder sb14 = new StringBuilder();
										sb14.append(f1).append(":").append(v1).append(",");
										sb14.append(f4).append(":").append(v4);
										String id14 = sb14.toString();
										if(featureStateMap.containsKey(id14))
										{
											StringBuilder sb15 = new StringBuilder();
											sb15.append(f1).append(":").append(v1).append(",");
											sb15.append(f5).append(":").append(v5);
											String id15 = sb15.toString();
											if(featureStateMap.containsKey(id15))
											{
												StringBuilder sb23 = new StringBuilder();
												sb23.append(f2).append(":").append(v2).append(",");
												sb23.append(f3).append(":").append(v3);
												String id23 = sb23.toString();
												if(featureStateMap.containsKey(id23))
												{
													StringBuilder sb24 = new StringBuilder();
													sb24.append(f2).append(":").append(v2).append(",");
													sb24.append(f4).append(":").append(v4);
													String id24 = sb24.toString();
													if(featureStateMap.containsKey(id24))
													{
														StringBuilder sb25 = new StringBuilder();
														sb25.append(f2).append(":").append(v2).append(",");
														sb25.append(f5).append(":").append(v5);
														String id25 = sb25.toString();
														if(featureStateMap.containsKey(id25))
														{
															StringBuilder sb34 = new StringBuilder();
															sb34.append(f3).append(":").append(v3).append(",");
															sb34.append(f4).append(":").append(v4);
															String id34 = sb34.toString();
															if(featureStateMap.containsKey(id34))
															{
																StringBuilder sb35 = new StringBuilder();
																sb35.append(f3).append(":").append(v3).append(",");
																sb35.append(f5).append(":").append(v5);
																String id35 = sb35.toString();
																if(featureStateMap.containsKey(id35))
																{
																	StringBuilder sb45 = new StringBuilder();
																	sb45.append(f4).append(":").append(v4).append(",");
																	sb45.append(f5).append(":").append(v5);
																	String id45 = sb45.toString();
																	if(featureStateMap.containsKey(id45))
																	{
																		sb12.append(",").append(f3).append(":").append(v3);
																		sb12.append(",").append(f4).append(":").append(v4);
																		sb12.append(",").append(f5).append(":").append(v5);
																		resultList.add(sb12.toString());
																	}
																}

															}
														}
													}
												}
											}
										}
									}
								}
							}

						}
					}
				}
			}
		}
		else
		{
			System.out.println("Wrong size: fields.length==5+....");
			System.exit(0);
		}
		return resultList;
	}
	
	private static int getFieldID(String fieldName)
	{
		for(int i = 0;i<fields.length;i++)
		{
			if(fields[i].equals(fieldName))
				return i;
		}
		return -1;
				
	}
	
	public static List<ValueCombination> loadFeatureStates(String featureStateDir, String fsExt, List<String> fieldList)
	{
		List<ValueCombination> vcList = new ArrayList<ValueCombination>();
		
		Iterator<String> fieldIter = fieldList.iterator();
		while(fieldIter.hasNext())
		{
			String srcField = fieldIter.next();
			int srcFieldID = getFieldID(srcField);
			String fieldDir = featureStateDir+"/"+srcField;
			List<String> fileList = PVFile.getFiles(fieldDir, fsExt);
			Iterator<String> iter = fileList.iterator();
			while(iter.hasNext())
			{
				String fileName = iter.next();
				String tgtField = fileName.split("\\.")[0].split("-")[1];
				int tgtFieldID = getFieldID(tgtField);
				if(fieldList.contains(tgtField))
				{
					String filePath = fieldDir+"/"+fileName;
					List<String> lineList = PVFile.readFile(filePath);
					String[] tgtFieldValues = null;
					Iterator<String> iter2 = lineList.iterator();
					while(iter2.hasNext())
					{
						String line = iter2.next(); //line of file....
						if(line.startsWith("#")) //field line
						{
							String[] s = line.split("\\s");
							tgtFieldValues = new String[s.length-1];
							for(int i = 1;i < s.length;i++)
								tgtFieldValues[i-1] = s[i];
						}
						else
						{
							String[] s = line.split("\\s");
							String srcFieldValue = s[0];
							//List<FieldCombination> fcList;
							for(int i = 1;i<s.length;i++)
							{
								float value = Float.parseFloat(s[i]);
								if(value>0)
								{
									//fcList = new ArrayList<FieldCombination>();
									StringBuilder sb = new StringBuilder();
									//FieldCombination fc = new FieldCombination(srcField, srcFieldID, srcFieldValue);
									//fcList.add(fc);
									String tgtFieldValue = tgtFieldValues[i-1];
									//fc = new FieldCombination(tgtField, tgtFieldID, tgtFieldValue);
									//fcList.add(fc);
									String id = getValueCombinationID(srcField, srcFieldValue, tgtField, tgtFieldValue);
									vcList.add(new ValueCombination(id,null));
								}
							}
						}
					}
				}
			}
		}
		return vcList;
	}
	
	private static String getValueCombinationID(String srcField, String srcFieldValue, String tgtField, String tgtFieldValue)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(srcField);
		sb.append(":");
		sb.append(srcFieldValue);
		sb.append(",");
		sb.append(tgtField);
		sb.append(":");
		sb.append(tgtFieldValue);
		return sb.toString();
	}
}
