package test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestTime {

	public static void main(String[] args)
	{
		String time1 = "2015-04-20-21.37.23.448917";
		computeLongTime(time1);
	}
	
	private static void computeLongTime(String time)
	{
		StringBuilder sb = new StringBuilder();
		String[] s = time.split("-");
		sb.append(s[0]).append("-");
		sb.append(s[1]).append("-");
		sb.append(s[2]).append(" ");
		
		String[] b = s[3].split("\\.");
		sb.append(b[0]).append(":");
		sb.append(b[1]).append(":");
		sb.append(b[2]).append(".").append(b[3]);
		
		String timeString = sb.toString();

		System.out.println("timeString="+timeString);
		Timestamp ts = Timestamp.valueOf(timeString);
		int d = ts.getDate();
		System.out.println(d);
		System.out.println(ts.getTime());
	}
}
