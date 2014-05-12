/* This is original work for CMPE 273 Project Under Ashok Banerjee
 * Onwers: Fnu Lavanya Ramani, Kayal, Jaydev Akkiraju
 * This file implements a http parser for parsing http packets coming to the proxy 
 */

package clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;

public class HttpParser {
	String httpMethod;
	String uri;
	String httpVersion;
	List<Header> HeaderList = new ArrayList<Header>();
	private BufferedReader br;

	public HttpParser(BufferedReader br) {
		this.br = br;
	}

	public void Parse() {
		String current_line;
		boolean method_parsed = false;
		boolean header_parsed = false;
		boolean body_parsed = false;
		try {
			current_line = br.readLine();
			if (current_line == null) {
				return;
			}
			// Call getHttpMethod to fetch the get/post/accept method
				if (method_parsed == false) {
					parseHttpMethod(current_line);
					method_parsed = true;
				}
				
				if (method_parsed == true && header_parsed == false) {
					// Call getHttpHeaders to fetch all the headers in the
					// header
					// list
					parseHttpHeaders(br);
					header_parsed = true;
				}

				if (header_parsed == true && method_parsed == true && body_parsed == false) {
					// Call getHttpBody to fetch the body
					body_parsed = true;
				}
			} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseHttpHeaders(BufferedReader br) {
		String line;
		try {
			//read line until you get an empty string
			while ((line = br.readLine()) != "") {
				System.out.println("line: " + line);
				if (!line.matches(".*:.*")) {
					break;
				}
				String[] splits = line.split(":");
				HeaderList.add(new Header(splits[0], splits[1].trim()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	private void parseHttpMethod(String packet) {
		// packet is going to contain the entire GET/POST or any other http
		// method along with host string. Return the method.
		System.out.println("packet is" + packet);
		UrlValidator urlValidator = new UrlValidator();
		//valid URL
	    if (urlValidator.isValid(packet)) {
	    this.httpMethod = "invalidurl";
	    }
		String httpUrlString[] = packet.split(" ", 3);
		this.httpMethod = httpUrlString[0];
		this.uri = httpUrlString[1];
		this.httpVersion = httpUrlString[2];
	}
	
	public String getHttpMethod()
	{
		return this.httpMethod;
	}
	
	public String getHttpUrl()
	{
		return this.uri;
	}
	
	public List<Header> getHttpHeaders() 
	{
		return this.HeaderList;
	}
	
}
