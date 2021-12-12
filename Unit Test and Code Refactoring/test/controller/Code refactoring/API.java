package utils;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import entity.payment.CreditCard;
import entity.payment.PaymentTransaction;


/**
 * Class cung cap cac phuong thuc giup gui request len server va nhan du lieu tra ve
 * Date : 12/12/2021
 * @author cuongnq
 * @version 1.0
 * */
public class API {
	
	/**
	 *Thuoc tinh giup format ngay thang theo dinh 
	 */
	public static DateFormat DATE_FORMATER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 *Thuoc tinh giup log thong tin ra consoles 
	 */
	private static Logger LOGGER = Utils.getLogger(Utils.class.getName());
	
	/**
	 *	Phuong thuc giup thiet lap ket noi Http
	 *	@param url: duong dan voi server can request
	 *	@param method: phuong thuc ket noi voi server
	 *	@return conn: ket noi dc thiet lap
	 * 	@throws IOException
	 */
	private static HttpURLConnection setupConnection(String url, String method) throws IOException {
		LOGGER.info("Request URL" + url + "\n");
		URL line_api_url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) line_api_url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content type", "application/json");
		return conn;
	}
	
	/**
	 *	Phuong thuc giup doc du lieu tra ve tu server
	 *	@param conn: ket noi duoc su dung
	 *	@return string: du lieu dc tra ve
	 * 	@throws IOException
	 */
	private static String readResponse(HttpURLConnection conn) throws IOException {
		BufferedReader in;
		String inputLine;
		if (conn.getResponseCode() / 100 == 2) {
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder response = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
		}
		response.append(inputLine + "\n");
		in.close();
		LOGGER.info("Response Info: " + response.substring(0, response.length() - 1).toString());
		return response.substring(0, response.length() - 1).toString();
	}

	/**
	 * Phuong thuc giup goi cac API dang GET
	 * @param url: duong dan toi server can request
	 * @param token: doan hashcode can dung de xac thuc nguoi dung
	 * @return response: phan hoi tu server theo dang string
	 * @throws exception
	 */
	public static String get(String url) throws Exception {
		
		//setup
		HttpURLConnection conn = setupConnection(url, "GET");
		
		//doc du lieu
		String response = readResponse(conn); 
		
		return response;
	}

	int var;
	
	/**
	 * Phuong thuc giup goi cac API dang POST
	 * @param url: duong dan toi server can request
	 * @param data: Du lieu dua len server de xu ly (Dang JSON)
	 * @return response: phan hoi tu server theo dang string
	 * @throws exception
	 */
	public static String post(String url, String data) throws IOException {
		allowMethods("PATCH");
		
		//setup
		HttpURLConnection conn = setupConnection(url, "GET");

		//gui du lieu
		Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		writer.write(data);
		writer.close();
		
		//Doc du lieu gui ve tu server
		String response = readResponse(conn);
		
		return response;
	}

	/**
	 *	Phuong thuc giup goi cac giao thuc API khac PATCH, PUT, ...
	 *	@deprecated: chi hoat dong voi java 11
	 *	@param method: giao thuc ket noi voi server
	 */
	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
			methodsField.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);

			methodsField.set(null/* static field */, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}
