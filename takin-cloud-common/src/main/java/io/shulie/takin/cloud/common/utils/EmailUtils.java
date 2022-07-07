package io.shulie.takin.cloud.common.utils;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {
    private static SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.DES, "sgEsnN6QWq8W7j5H01020304".getBytes());
    public static void main(String[] args) {
        String s = des.encryptHex("afU3#kh1");
        System.out.println(s);
        String s2 = des.decryptStr(s);
        System.out.println(s2);

        String mailTo = "zhaowancheng@shulie.io";
        String mailTittle="文字邮件";
        String mailText = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<title>测试(shulie.com)</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>我的第一个标题</h1>\n" +
                "<p style=\"color:red;\">第一个段落。这是一个简单的邮件</p>\n" +
                "</body>\n" +
                "</html>";
        String mailHost = "111.22.67.133";
        String username = "ctp@cmss.chinamobile.com";
        //String password = "afU3#kh1";
        String password = "02b2493b727b31d781d68e7bdfb9ad9b";

        sendEmail(mailHost,username,password,mailTo,mailTittle,mailText);
    }

    public static void sendEmail(String mailHost,String username,String password,String mailTo, String mailTittle, String mailText) {
        //默认密码为密文，尝试解密
        try {
            password = des.decryptStr(password);
        }catch (Exception e){
            //解不开就是明文
        }
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", mailHost);
        prop.setProperty("mail.smtp.port", "465");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        Session session = Session.getInstance(prop);
        session.setDebug(true);
        try (Transport ts = session.getTransport()) {
            ts.connect(mailHost, username, password);
            Message message = createMail(session,username,mailTo,mailTittle,mailText);
            ts.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static MimeMessage createMail(Session session, String mailFrom, String mailTo, String mailTittle, String mailText) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
            message.setSubject(mailTittle);
            message.setContent(mailText, "text/html;charset=UTF-8");
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
