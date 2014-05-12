/* This is original work for CMPE 273 Project Under Ashok Banerjee
 * Onwers: Fnu Lavanya Ramani, Kayal, Jaydev Akkiraju
 * This file implements a http proxy for redirecting requests to actual server and 
 * send the response to a browser client
 */
package clientserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.List;
import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;

public class ServerNew {
	private static Socket socket;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		String returnMessage = null;
		boolean haveValidDataToSend = false;
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", 8020));
			System.out.println("Server Started and listening to the port 8020");

			// Server is running always. This is done using this while(true)
			// loop
			while (true) {

				haveValidDataToSend = false;
				// Reading the message from the client
				System.out.println("Wait for new request from client");
				socket = serverSocket.accept();

				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				HttpParser hParser = new HttpParser(br);

				hParser.Parse();
				String method = hParser.getHttpMethod();
				if (method.matches("invalidurl")) {
					System.out.println("Got an invalid url");
					CloseableHttpClient httpclient = HttpClients
							.createDefault();
					try {
						HttpGet httpGet = new HttpGet(
								"http://127.0.0.1:8080/invalid_url.html");
						CloseableHttpResponse response = httpclient
								.execute(httpGet);
						System.out
								.println("Executed http get with the real http server");

						try {
							StatusLine statusline = response.getStatusLine();

							returnMessage = statusline.getProtocolVersion()
									+ " " + statusline.getStatusCode() + " "
									+ statusline.getReasonPhrase() + "\r\n";
							// returnMessage = "HTTP/1.1" + " " + "403" +
							// " "
							// + "Forbidden" + "\r\n";
							haveValidDataToSend = true;

							// System.out.println(response1.getAllHeaders());
							HttpEntity entity1 = response.getEntity();
							is = entity1.getContent();
							returnMessage = returnMessage + "\r\n";
							returnMessage = returnMessage
									+ (EntityUtils.toString(entity1));

							// do something useful with the response body
							// and ensure it is fully consumed
							EntityUtils.consume(entity1);

						} finally {
							response.close();
						}

					} finally {
						httpclient.close();
					}
				}
				if (method.matches("GET")) {
					String uri = hParser.getHttpUrl();
					if (uri.matches(".*localhost.*")) {
						String url_arr[] = uri.split("/", 2);
						System.out.println("url_arr[1] is" + url_arr[1]);
						// split again
						String url_1_arr[] = url_arr[1].split("/", 2);
						String url_2_arr[] = url_1_arr[1].split("/", 2);
						System.out.println("url_2_arr[0]" + url_2_arr[0]);
						System.out.println("url_2_arr[1]" + url_2_arr[1]);
						// Now extract the page you want and append it to the
						// url
						CloseableHttpClient httpclient = HttpClients
								.createDefault();
						try {
							HttpGet httpGet;
							if (url_2_arr[1].matches(".*1.*")) {
								System.out.println("Retrieving index1.html");
								httpGet = new HttpGet("http://localhost:8080/"
										+ url_2_arr[1]);
							} else {
								httpGet = new HttpGet("http://localhost:8181/"
										+ url_2_arr[1]);
							}
							CloseableHttpResponse response = httpclient
									.execute(httpGet);
							System.out
									.println("Executed http get with the real http server");

							try {
								StatusLine statusline = response
										.getStatusLine();

								returnMessage = statusline.getProtocolVersion()
										+ " " + statusline.getStatusCode()
										+ " " + statusline.getReasonPhrase()
										+ "\r\n";
								System.out.println(returnMessage);

								List<clientserver.Header> Hdr = hParser
										.getHttpHeaders();

								HttpEntity entity1 = response.getEntity();
								is = entity1.getContent();
								returnMessage = returnMessage + "\r\n";

								returnMessage = returnMessage
										+ (EntityUtils.toString(entity1));

								// do something useful with the response body
								// and ensure it is fully consumed
								EntityUtils.consume(entity1);
								haveValidDataToSend = true;

							} finally {
								response.close();
							}

						} finally {
							httpclient.close();

						}

					} else if (uri.matches(".*terminate.*")) {
						System.out
								.println("Closing socket to accept new connections");
						socket.close();
						serverSocket.close();
						break;
					} else if (uri.matches(".*dating.*")
							|| uri.matches(".*einthusan.*")) {
						CloseableHttpClient httpclient = HttpClients
								.createDefault();
						try {
							HttpGet httpGet = new HttpGet(
									"http://127.0.0.1:8080/invalid_request.html");
							CloseableHttpResponse response = httpclient
									.execute(httpGet);
							System.out
									.println("Executed http get with the real http server");

							try {
								StatusLine statusline = response
										.getStatusLine();

								returnMessage = statusline.getProtocolVersion()
										+ " " + statusline.getStatusCode()
										+ " " + statusline.getReasonPhrase()
										+ "\r\n";
								// returnMessage = "HTTP/1.1" + " " + "403" +
								// " "
								// + "Forbidden" + "\r\n";
								haveValidDataToSend = true;

								// System.out.println(response1.getAllHeaders());
								HttpEntity entity1 = response.getEntity();
								is = entity1.getContent();
								returnMessage = returnMessage + "\r\n";
								returnMessage = returnMessage
										+ (EntityUtils.toString(entity1));

								// do something useful with the response body
								// and ensure it is fully consumed
								EntityUtils.consume(entity1);

							} finally {
								response.close();
							}

						} finally {
							httpclient.close();
						}
					}
					// This is plain GET
					else {
						CloseableHttpClient httpclient = HttpClients
								.createDefault();
						HttpGet httpGet;
						CloseableHttpResponse response = null;
						boolean broken_website = false;
						try {
							// 192.168.5.147

							try {
								httpGet = new HttpGet(uri);
								response = httpclient.execute(httpGet);
							} catch (Exception ex) {
								System.out
										.println("Got exception while opening the uri:");
								haveValidDataToSend = true;
								broken_website = true;
							}

							if (broken_website == true) {
								continue;
							}
							System.out
									.println("Executed http get with the real http server");

							try {
								StatusLine statusline = response
										.getStatusLine();

								returnMessage = statusline.getProtocolVersion()
										+ " " + statusline.getStatusCode()
										+ " " + statusline.getReasonPhrase()
										+ "\r\n";
								System.out.println(returnMessage);
								// System.out.println(response1.getAllHeaders());
								Header[] headers = response.getAllHeaders();
								for (Header header : headers) {
									returnMessage = returnMessage
											+ ("Key : " + header.getName()
													+ " ,Value : " + header
														.getValue()) + "\r\n";
								}
								HttpEntity entity1 = response.getEntity();
								if (entity1 != null) {
									is = entity1.getContent();
									returnMessage = returnMessage + "\r\n";
									returnMessage = returnMessage
											+ (EntityUtils.toString(entity1));
									// do something useful with the response
									// body
									// and ensure it is fully consumed
									EntityUtils.consume(entity1);
									haveValidDataToSend = true;
								}

							} finally {
								response.close();
							}

						} finally {
							httpclient.close();

						}

					}

				}

				if (haveValidDataToSend == true) {
					System.out
							.println("\nSending the response back to the client");
					OutputStream os = socket.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					try {
						bw.write(returnMessage);
						System.out.println(returnMessage);
						bw.flush();
					} catch (Exception ex) {
						System.out
								.println("Got exception while message back to client socket. The exception message is:"
										+ ex.getMessage());
					}
				}
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				socket.close();

			} catch (Exception e) {
			}

		}

	}

}
