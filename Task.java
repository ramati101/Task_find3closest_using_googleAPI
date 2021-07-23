package task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Stack;  

public class Task {
	
	private static String[][] data;

	public static void main(String[] args) throws IOException {

		String inputAddress = new String();
		Scanner scan = new Scanner(System.in); 
		int[] closest = {0,1,2};
		
		// path to directory that contains the sample data.csv file  **** change this path!
		readData("C:\\Users\\Downloads\\Server coding task\\Server coding task\\server coding task sample data.csv");
		
		System.out.println("enter the random street address:");
		inputAddress += scan.nextLine();
		
		closest = findTheClosest(inputAddress);
		
		System.out.println("order id: "+data[closest[2]][0] + "  name: " + data[closest[2]][1] + "  distance: " + measureDist(inputAddress, data[closest[2]][2]));
		System.out.println("order id: "+data[closest[1]][0] + "  name: " + data[closest[1]][1] + "  distance: " + measureDist(inputAddress, data[closest[1]][2]));
		System.out.println("order id: "+data[closest[0]][0] + "  name: " + data[closest[0]][1] + "  distance: " + measureDist(inputAddress, data[closest[0]][2]));
	}
	
	
	// make compares between the input address and all the address at the sample file and find the three nearest.
	// another way to do it, instead of working with stacks is to insert all the Recipients(index, distance from the input address) to array and sort it(heap sort can be a good idea O(nlogn))
	private static int[] findTheClosest(String inputAdd) throws IOException {
		int[] threeClosest = {0,1,2};
		int dist;
		
		Stack<Recipients> s= new Stack<>();
		Stack<Recipients> temp= new Stack<>();
		
		// compare to all addresses on the sample file
		for(int i=0; i<data.length; i++) {
			
			// measure the distance between the input address to the address on index i
			dist = measureDist(inputAdd, data[i][2]);
			
			if(s.isEmpty())
				s.push(new Recipients(dist, i));
		
			// stack isnt empty
			else {
				if(s.peek().dist < dist) {
					if(s.size() >= 3)
						continue;
					else
						s.push(new Recipients(dist, i));
				}	
				else {
					while(!s.isEmpty() && dist>s.peek().dist) 
						temp.push(s.pop());
					s.push(new Recipients(dist,i));
					while(!temp.isEmpty() && s.size()>3)
						s.push(temp.pop());
					temp.clear();				
				}
			}
		}
		
		threeClosest[2] = s.pop().index;
		threeClosest[1] = s.pop().index;
		threeClosest[0] = s.pop().index;
		
		return threeClosest;
	}
	
	// connect to google gis api and measure distance
	private static int measureDist(String orig, String dest) throws IOException {
		String dist;
		String key; //= enter google apis Key
		orig = orig.replace(' ', '+');
		dest = dest.replace(' ', '+');
		
		String request = "https://maps.googleapis.com/maps/api/distancematrix/"
						+ "json"
						+ "?key=" + key
						+ "&origins=" + orig
						+ "&destinations=" + dest;
		URL url = new URL(request);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		String line, outputString = "";
		BufferedReader reader = new BufferedReader(
		new InputStreamReader(conn.getInputStream()));
		
		while ((line = reader.readLine()) != null) {
		     outputString += line;
		}
		
		String text = outputString;
		//pattern to find the distance at all the input data
		Pattern pattern = Pattern.compile("(\"value\" : )(.*?) ");
		Matcher matcher = pattern.matcher(text);
		
		if(matcher.find()) 
			dist = new String(matcher.group(2));
		
		// when cant find the distance
		else
			dist = new String("999999");
	    
		
		return Integer.parseInt(dist);
	}
			
	
	// reads the data from the csv excel file
	private static void readData(String path) throws IOException {
		BufferedReader csvReader = new BufferedReader(new FileReader(path));
		String row;
		int i = 0;
		int length = 200;
		
		data = new String[length][3];
		csvReader.readLine();
		while ((row = csvReader.readLine()) != null) {
		    data[i] = row.split(",");
		    i++;
		}
		csvReader.close();
	}
}
