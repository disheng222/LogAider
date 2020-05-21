package element;

import java.util.HashMap;

public class ExitCodeStatElement implements Comparable<ExitCodeStatElement>{

	public static int mode = 0; //0:ID, 1:userMapSize, 2:projMapSize, 3:jobMapSize
	
	private String ID;
	
	public HashMap<String, Integer> userMap = new HashMap<String, Integer>();
	public HashMap<String, Integer> projMap = new HashMap<String, Integer>();
	public HashMap<String, Integer> jobMap = new HashMap<String, Integer>();
	
		
	public ExitCodeStatElement(String iD) {
		ID = iD;
	}
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	
	public int compareTo(ExitCodeStatElement other)
	{
		switch(mode)
		{
		case 0:
			if(ID.compareTo(other.ID)<0)
				return -1;
			else 
				return 1;
		case 1:
			if(userMap.size()<other.userMap.size())
				return -1;
			else
				return 1;
		case 2:
			if(projMap.size()<other.projMap.size())
				return -1;
			else
				return 1;
		case 3:
			if(jobMap.size()<other.jobMap.size())
				return -1;
			else
				return 1;
		}
		return 0;
	}
	
	
	public String toString()
	{
		return ID+" "+projMap.size()+" "+userMap.size()+" "+jobMap.size();
	}
}
