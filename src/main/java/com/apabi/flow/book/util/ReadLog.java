package com.apabi.flow.book.util;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 从最后一行开始读取
 */
public class ReadLog {
  
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
        } catch (IOException e) {
            e.printStackTrace();  
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

    public static void main(String args[]) {
        String read = read("/Users/suntianyin/Downloads/log-2019-01-28.log", "UTF-8",100);
        int a=1;
    }  
}  
