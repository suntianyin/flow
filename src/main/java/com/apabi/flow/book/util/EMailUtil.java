package com.apabi.flow.book.util;

import com.apabi.flow.systemconf.dao.SystemConfMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/11/21 16:41
 * @description
 */
public class EMailUtil {

    // SMTP邮件服务器
    private String SERVER;

    // SMTP邮件服务器端口
    private Integer PORT;

    // 发件人
    private String SENDER;

    // 收件人
    private String TO;

    // SMTP邮件用户
    private String USER;

    // SMTP邮件用户密码
    private String PASSWORD;

    private JavaMailSenderImpl sender;

    //初始化邮箱
    public EMailUtil(SystemConfMapper systemConfMapper) {
        this.SERVER = systemConfMapper.selectByConfKey("mail_smtp_server").getConfValue();
        this.PORT = Integer.valueOf(systemConfMapper.selectByConfKey("mail_smtp_port").getConfValue());
        this.SENDER = systemConfMapper.selectByConfKey("mail_sender").getConfValue();
        this.TO = systemConfMapper.selectByConfKey("flowcontent_check_notice_mail").getConfValue();
        this.USER = systemConfMapper.selectByConfKey("mail_smtp_user").getConfValue();
        this.PASSWORD = systemConfMapper.selectByConfKey("mail_smtp_password").getConfValue();
    }

    //添加配置
    public void createSender() {
        try {

            sender = new JavaMailSenderImpl();
            sender.setHost(SERVER);
            sender.setPort(PORT);
            sender.setUsername(USER);
            sender.setPassword(PASSWORD);
            sender.testConnection();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //发送带附件的邮件
    public void sendAttachmentsMail(List<String> attachs) {
        MimeMessage message;
        try {
            message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(SENDER, "北京方正阿帕比技术有限公司");
            if (StringUtils.isNotBlank(TO)) {
                helper.setTo(TO.split(";"));
            }
            helper.setSubject("主题：图书乱码检查结果");
            //附件加入邮件
            Multipart multipart = new MimeMultipart();
            if (attachs != null) {
                for (String file : attachs){
                    BodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(source.getName());
                    multipart.addBodyPart(attachmentPart);
                }
            }
            message.setContent(multipart);
            message.saveChanges();
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
