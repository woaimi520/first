import java.lang.*;
public class Father{
	    int[][]a2d={{1,2},{3,4},{5,6}};;
	    public enum Menbers{JERRY,BOBBY,PHIL};
		public Menbers select; 
		 
	    public String pubString ="pString";
		protected String proString ="proString";
		String deString ="deString";
		private String priString ="priString";
		
		public void toString1(){
		a2d[1][1]=9;
		int [] a=a2d[0]; 
			 select=Menbers.JERRY;
			 System.out.println(select);
		System.out.println(a2d[1][1]);
			System.out.println(a[0]);
		System.out.println(pubString);
		System.out.println(proString);
		System.out.println(deString);
		System.out.println(priString);
	}
}