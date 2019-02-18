package com.apabi.flow.auth.task;

import com.apabi.flow.auth.dao.CopyrightAgreementMapper;
import com.apabi.flow.auth.model.CopyrightAgreement;
import com.apabi.flow.auth.util.AuthUtil;
import com.apabi.flow.book.util.EMailUtil;
import com.apabi.flow.common.DateUtil;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author guanpp
 * @date 2019/2/18 13:34
 * @description
 */
@Component
public class CheckAgreementValidity {

    private Logger logger = LoggerFactory.getLogger(CheckAgreementValidity.class);

    private static final String CHECK_AGREEMENT_EMAIL = "check_agreement_email";

    @Autowired
    private CopyrightAgreementMapper copyrightAgreementMapper;

    @Autowired
    ApplicationConfig config;

    @Autowired
    SystemConfMapper systemConfMapper;

    /**
     * 定期检查版权协议有效性，并更新状态，每天1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")
    void checkAgreementValidity() {
        //获取需要检查的协议列表，
        List<CopyrightAgreement> copyrightAgreementList = copyrightAgreementMapper.findAgreementValidity();
        if (copyrightAgreementList != null) {
            //存储待发邮件的数据
            List<CopyrightAgreement> listEmail = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            for (CopyrightAgreement copyrightAgreement :
                    copyrightAgreementList) {
                Integer yearNum = copyrightAgreement.getYearNum();
                Date endDate = copyrightAgreement.getEndDate();
                calendar.setTime(endDate);
                calendar.add(Calendar.YEAR, yearNum);
                endDate = calendar.getTime();
                //如果天数小于60天，则发邮件提醒
                int betweenDay = DateUtil.betweenDays(new Date(), endDate);
                if (betweenDay < 60) {
                    listEmail.add(copyrightAgreement);
                }
            }
            logger.info("版权协议有效性，已检查结束");
            if (listEmail.size() > 0) {
                //生成结果
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String resultPath = config.getEmail() + File.separator + df.format(new Date()) + "CopyRightValidity.xlsx";
                AuthUtil.exportExcelEmail(listEmail, resultPath);
                logger.info("版权协议有效性检查结果，已生成");
                //表格路径
                List<String> results = new ArrayList<>();
                results.add(resultPath);
                //将版权协议有效性检查结果发送邮件
                logger.info("开始发送邮件");
                EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
                eMailUtil.createSender();
                String toEmail = systemConfMapper.selectByConfKey(CHECK_AGREEMENT_EMAIL).getConfValue();
                eMailUtil.sendAttachmentsMail(results, "版权协议有效性检查结果", toEmail);
                logger.info("版权协议有效性检查结果，已发送邮件");
                //更新邮件通知状态
                for (CopyrightAgreement agree :
                        listEmail) {
                    agree.setNoticeState(1);
                    copyrightAgreementMapper.updateByPrimaryKeySelective(agree);
                }
            }
        }
    }
}
