package services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    static CustomLogger logger;

    private static Session session;
    private static String email;

    public static void configureSession(String host, String port, String auth, String emailAddress, String password, ) {
        logger = new CustomLogger(EmailSender.class.getName());

        email = emailAddress;

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getDefaultInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailAddress, password);
                    }
                });
    }

    public static boolean sendEmail(String recipient, String subject, String text) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            return true;
        } catch (Exception e) {
            logger.warning("An error occurred while sending email. " +
                    "You might not configured the auth credentials for SMTP server of your provider. " +
                    "Please check the documentation to find out how to do it.");
            return false;
        }
    }
}
