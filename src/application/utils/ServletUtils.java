package application.utils;

import application.logic.Admin;
import application.logic.AppManager;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Map;
import java.util.Properties;

public class ServletUtils {

	private static final String APP_MANAGER_ATTRIBUTE_NAME = "appManager";

    public static AppManager getAppManager(ServletContext servletContext) {
		if (servletContext.getAttribute(APP_MANAGER_ATTRIBUTE_NAME) == null) {
			servletContext.setAttribute(APP_MANAGER_ATTRIBUTE_NAME, new AppManager());
		}
		return (AppManager) servletContext.getAttribute(APP_MANAGER_ATTRIBUTE_NAME);
	}

	public static void redirect(HttpServletResponse response, String message, String location)
			throws IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<script type=\"text/javascript\">");
		if (!message.isEmpty())
			out.println(String.format("alert(\"%s\");", message));
		if (!location.isEmpty())
			out.println(String.format("location=\"%s\"", location));
		out.println("</script>");
	}

	public static String uploadImageToCloudinary(Part filePart)
			throws ServletException, IOException {

		String path = saveImageTemporary(filePart);
		if(path == null)
			return null;

		String url = null;
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", "checkeat",
				"api_key", "511198241294818",
				"api_secret", "lM6ZRF0un11U_hNUnldFifOYQsk"));

		try {
			Map uploadResult = cloudinary.uploader().upload(path, ObjectUtils.emptyMap());
			url = uploadResult.get("url").toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return url;
	}

	private static String saveImageTemporary(Part filePart)
			throws ServletException, IOException {

		String fileName = getFileName(filePart);
		if(fileName == null)
			return null;

		String filePath = null;
		OutputStream out = null;
		InputStream filecontent = null;
		String tempDirPath = System.getProperty("java.io.tmpdir");

		try {
			filePath = tempDirPath + File.separator + fileName;
			out = new FileOutputStream(new File(filePath));
			filecontent = filePart.getInputStream();

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
		}
		catch (FileNotFoundException fne) {
			//Problems during file upload
		}
		finally {
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
		}

		return filePath;
	}

	private static String getFileName(final Part part) {
		String partHeader = part.getHeader("content-disposition");

		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(
						content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}

		return null;
	}

	public static String loadImageURL(String url) {
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", "checkeat",
				"api_key", "511198241294818",
				"api_secret", "lM6ZRF0un11U_hNUnldFifOYQsk"));
		try {
			Map uploadResult = cloudinary.uploader().upload(url, ObjectUtils.emptyMap());
			return uploadResult.get("url").toString();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isValidString(String str) {
		if(str != null) {
			String temp = new String(str);
			return !temp.isEmpty() && temp.trim().length() > 0;
		}
		return false;
	}

	public static boolean sendEmail(Admin admin, String userEmail, String subject, String body) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(admin.getEmail(), admin.getEmailPassword());
					}
				});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(admin.getEmail()));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(userEmail)); //TODO userEmail - just to check
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);

		}
		catch (MessagingException e) {
			return false;
		}

		return true;
	}
}