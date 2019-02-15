package com.apabi.flow.book.util;

import com.apabi.flow.book.task.GetBook4ShuyuanTask;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/11/21 16:41
 * @description
 */
public class EMailUtil {

    private Logger logger = LoggerFactory.getLogger(EMailUtil.class);

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
            logger.warn("创建邮箱时，出现异常{}", e.getMessage());
        }
    }

    //发送带附件的邮件
    public void sendAttachmentsMail(List<String> attachs, String subject) {
        MimeMessage message;
        try {
            message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(SENDER, "北京方正阿帕比技术有限公司");
            if (StringUtils.isNotBlank(TO)) {
                helper.setTo(TO.split(";"));
            }
            helper.setSubject(subject);
            //附件加入邮件
            Multipart multipart = new MimeMultipart();
            if (attachs != null) {
                for (String file : attachs) {
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
            logger.warn("发送邮件时，出现异常{}", e.getMessage());
        }
    }

    //发送带附件的邮件
    public void sendAttachmentsMail(List<String> attachs, String subject, String to) {
        MimeMessage message;
        try {
            message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(SENDER, "北京方正阿帕比技术有限公司");
            if (StringUtils.isNotBlank(to)) {
                helper.setTo(to.split(";"));
            }
            helper.setSubject(subject);
            //附件加入邮件
            Multipart multipart = new MimeMultipart();
            if (attachs != null) {
                for (String file : attachs) {
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
            logger.warn("发送邮件时，出现异常{}", e.getMessage());
        }
    }

    //发送通知邮件
    public void sendNoticeMail(String subject, String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER);
            message.setText(subject);
            message.setSubject("北京方正阿帕比技术有限公司");
            message.setTo(to);
            // 发送邮件
            sender.send(message);
        } catch (Exception e) {
            logger.warn("发送邮件时，出现异常{}", e.getMessage());
        }
    }
}
