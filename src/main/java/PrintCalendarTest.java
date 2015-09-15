import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class PrintCalendarTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Calendar cal = Calendar.getInstance();

		DateFormat df = new SimpleDateFormat("E");
		
		System.out.println(cal);
		System.out.println(df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		System.out.println(df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		System.out.println(df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		System.out.println(df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		System.out.println(df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);

	}

}
