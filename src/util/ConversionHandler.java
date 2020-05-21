package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import element.Field;

public class ConversionHandler {

	public static float[] convertFloatList2FloatArray(List<Float> list)
	{
		float[] array = new float[list.size()];
		Iterator<Float> iter = list.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			float v = iter.next();
			array[i] = v;
		}
		return array;
	}
	
	public static List<Double> convertDoubleArray2DoubleList(double[] data) {
		List<Double> dataList = new ArrayList<Double>();
		for (int i = 0; i < data.length; i++)
			dataList.add(data[i]);
		return dataList;
	}

	public static double[] convertDoubleList2DoubleArray(List<Double> list) {
		double[] array = new double[list.size()];
		Iterator<Double> iter = list.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			double v = iter.next();
			array[i] = v;
		}
		return array;
	}

	public static List<Double> convertStringList2DoubleList(List<String> list) {
		List<Double> valueList = new ArrayList<Double>();
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			double value = Double.parseDouble(iter.next().trim());
			valueList.add(value);
		}
		return valueList;
	}

	public static double[] convertArray2List4Comp(double[] countVector,
			List<String> countList, double offset, double x_unit) {
		double[] result = new double[countVector.length];

		int size = countVector.length;

		double sum = 0;
		for (int i = 0; i < size; i++)
			sum += countVector[i];

		for (int i = 0; i < size; i++) {
			String s = String.valueOf(offset + x_unit * (i+1)); //(i+1):including the max value; (i): including the min value
			result[i] = countVector[i] / sum;
			s += " " + String.valueOf(result[i]);
			countList.add(s);
		}
		return result;
	}

	public static String[] convertStringList2StringArray(List<String> list)
	{
		String[] s = new String[list.size()];
		Iterator<String> iter = list.iterator();
		for(int i = 0;iter.hasNext();i++)
		{
			s[i] = iter.next();
		}
		return s;
	}
	
	public static List<String> convertDoubleArray2StringList(double[] data) {
		List<String> lineList = new ArrayList<String>();
		for (int i = 0; i < data.length; i++)
			lineList.add(String.valueOf(data[i]));
		return lineList;
	}

	public static double[][] convertDoubleLists2DoubleArrays(
			List<Double>[] dataList) {
		int size = dataList[0].size();
		double[][] dataArrays = new double[dataList.length][size];
		for (int i = 0; i < dataList.length; i++) {
			Iterator<Double> iter = dataList[i].iterator();
			for (int j = 0; iter.hasNext(); j++) {
				dataArrays[i][j] = iter.next();
			}
		}
		return dataArrays;
	}

	public static double[] convertStringList2DoubleArray(List<String> dataList,
			int index) {
		List<Double> resultList = new ArrayList<Double>();
		Iterator<String> iter = dataList.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (s.startsWith("#"))
				continue;
			String[] data = s.trim().split("\\s");
			if (isNumeric(data[index]))
				resultList.add(Double.valueOf(data[index]));
		}
		double[] result = new double[resultList.size()];
		Iterator<Double> iter2 = resultList.iterator();
		for (int i = 0; iter2.hasNext(); i++)
			result[i] = iter2.next();
		return result;
	}

	public static double[] copy(double[] data) {
		double[] newData = new double[data.length];
		for (int i = 0; i < newData.length; i++)
			newData[i] = data[i];
		return newData;
	}

	public static boolean isNumeric(String str) {
		// Pattern pattern = Pattern.compile("[0-9]+(\\.?)[0-9]*");
		Pattern pattern = Pattern
				.compile("[-+]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][-+]?\\d+)?[dD]?");
		return pattern.matcher(str).matches();
	}

    public static double[] mergeDoubleArray(double[] a1, double[] a2)
    {
    	double[] newA = new double[a1.length+a2.length];
    	for(int i = 0;i<a1.length;i++)
    		newA[i] = a1[i];
    	for(int i = 0, j = a1.length;i<a2.length;i++,j++)
    		newA[j] = a2[i];
    	
    	return newA;
    }
    
    public static int[] convertIntList2Array(List<Integer> intList)
    {
    	int[] array = new int[intList.size()];
    	Iterator<Integer> iter = intList.iterator();
    	for(int i = 0;iter.hasNext();i++)
    	{
    		int v = iter.next();
    		array[i] = v;
    	}
    	return array;
    }
	
    public static Field[] convertFieldList2FieldArray(List<Field> fieldList)
    {
    	Field[] fields = new Field[fieldList.size()];
    	Iterator<Field> iter = fieldList.iterator();
    	for(int i = 0;iter.hasNext();i++)
    	{
    		Field f = iter.next();
    		fields[i] = f;
    	}
    	return fields;
    }
    
    public static List<String> convertIntArray2StringList(int[] data)
    {
    	List<String> list = new ArrayList<String>();
    	for(int i = 0;i<data.length;i++)
    		list.add(String.valueOf(data[i]));
    	return list;
    }
    
    public static List<String> convertStringArray2StringList(String[] array)
    {
    	List<String> sList = new ArrayList<String>();
    	for(String s : array)
    		sList.add(s);
    	return sList;
    }
    

	public static List<Integer> convertIntArray2IntegerList(int[] array)
	{
		List<Integer> list = new ArrayList<Integer>();
		for(int i= 0;i<array.length;i++)
			list.add(array[i]);
		return list;
	}
}
