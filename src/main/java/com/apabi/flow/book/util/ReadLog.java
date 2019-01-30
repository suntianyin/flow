package com.apabi.flow.book.util;


import com.apabi.flow.book.controller.BookController;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;

/**
 * 从最后一行开始读取
 */
public class ReadLog {

    private static Logger log = LoggerFactory.getLogger(ReadLog.class);
    /** 
     *  
     * @param filename 目标文件 
     * @param charset 目标文件的编码格式 
     */  
    public static String read(String filename, String charset ,int count1) {
  
        RandomAccessFile rf = null;
        try {  
            rf = new RandomAccessFile(filename, "r");  
            long len = rf.length();
            long start = rf.getFilePointer();
            long nextend = start + len - 1;  
            String line;  
            rf.seek(nextend);  
            int c = -1;
            int count=0;
            StringBuffer stringBuffer=new StringBuffer();
            while (nextend > start) {
                c = rf.read();  
                if (c == '\n' || c == '\r') {  
                    line = rf.readLine();  
                    if (line != null) {
                        count++;
                        stringBuffer.append(new String(line
                                .getBytes("ISO-8859-1"), charset));
                        stringBuffer.append("\n");
                    }
                    if(count==count1)
                        return stringBuffer.toString();
                    nextend--;  
                }  
                nextend--;  
                rf.seek(nextend);  
//                if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行
                if (nextend == 0){
                    // System.out.println(rf.readLine());
                    stringBuffer.append(new String(rf.readLine().getBytes(
                            "ISO-8859-1"), charset));
                    return stringBuffer.toString();
                }  
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.info(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            log.info(e.toString());
        } finally {  
            try {  
                if (rf != null)  
                    rf.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return null;
    }

    public static void main(String args[])throws IOException {
//        String read = read("/Users/suntianyin/Downloads/1log-2019-01-29.log", "UTF-8",100);
//        String s=ReadLog.smbGet1("a");
        String read ="";
        String s="2019-01-30 09:02:01.644 logback [pool-8-thread-19] INFO  c.a.f.b.f.FetchPageAgainConsumer - 新增或修改metaid：m.20181212-SHSH-ZCKM-0023，页码：120的数据成功,请求接口耗时：2030ms,插入数据库耗时：53ms\n@" +
                "2019-01-30 09:02:01.652 logback [pool-8-thread-14] INFO  c.a.f.b.f.FetchPageAgainConsumer - 新增或修改metaid：m.20181212-SHSH-ZCKM-0023，页码：7的数据成功,请求接口耗时：2045ms,插入数据库耗时：48ms\n@" +
                "2019-01-30 09:02:01.652 logback [pool-8-thread-21] INFO  c.a.f.b.f.FetchPageAgainConsumer - 新增或修改metaid：m.20181212-SHSH-ZCKM-0023，页码：111的数据成功,请求接口耗时：2037ms,插入数据库耗时：54ms\n@" +
                "2019-01-30 09:02:01.668 logback [pool-8-thread-19] INFO  c.a.f.b.f.FetchPageAgainConsumer - 删除pageCrawledTemp的id:m.20181212-SHSH-ZCKM-0023,page:120成功\n@" +
                "2019-01-30 09:02:01.677 logback [pool-8-thread-14] INFO  c.a.f.b.f.FetchPageAgainConsumer - 删除pageCrawledTemp的id:m.20181212-SHSH-ZCKM-0023,page:7成功\n@" +
                "2019-01-30 09:02:01.677 logback [pool-8-thread-16] INFO  c.a.f.b.f.FetchPageAgainConsumer - 新增或修改metaid：m.20181212-SHSH-ZCKM-0023，页码：187的数据成功,请求接口耗时：2099ms,插入数据库耗时：18ms\n@" +
                "2019-01-30 09:02:01.677 logback [pool-8-thread-21] INFO  c.a.f.b.f.FetchPageAgainConsumer - 删除pageCrawledTemp的id:m.20181212-SHSH-ZCKM-0023,page:111成功\n@";
        String[] split = s.split("@");
            if(split.length<100) {
                for (int i = split.length-1; i >=0 ; i--) {
                read = split[i] ;
                    System.out.print(read);
                }
            }else {
                for (int i = 99; i >=0 ; i--) {
                    read = split[i] ;
                    System.out.println(read);
                }
            }

    }
    public static String smbGet1(String remoteUrl,Integer len)  {
        StringBuffer stringBuffer2= null;
        try {
            SmbFile smbFile = new SmbFile(remoteUrl);
            int length = smbFile.getContentLength();// 得到文件的大小
            log.info(String.valueOf(length));
            byte buffer[] = new byte[length];
            SmbFileInputStream in = new SmbFileInputStream(smbFile);
            StringBuffer stringBuffer=new StringBuffer();
            stringBuffer2 = new StringBuffer();
            // 建立smb文件输入流
            while ((in.read(buffer)) != -1) {
                String str= new String (buffer,"UTF-8");
//                str=new String(str.getBytes("ISO-8859-1"),"UTF-8");
                log.info(str);
                stringBuffer.append(str).append("@");
            }
            in.close();
            String s = stringBuffer.toString();
            log.info(s);
            String[] split = s.split("@");
            if(split.length<len) {
                for (int i = split.length-1; i >=0 ; i--) {
                    stringBuffer2.append(split[i]);
                }
            }else {
                for (int i = len-1; i >=0 ; i--) {
                    stringBuffer2.append(split[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            return "";
        }

        return stringBuffer2.toString();
    }
}  
