package com.apabi.flow.douban.util;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class DomParseUtil {
	private static final Logger log = LoggerFactory.getLogger(DomParseUtil.class);

	public static String [] ua = {
			"Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1",
			"Opera/9.80 (Macintosh; Intel Mac OS X 10.9.1) Presto/2.12.388 Version/12.16",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68",
			"Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) CriOS/30.0.1599.12 Mobile/11A465 Safari/8536.25",
			"Mozilla/5.0 (iPad; CPU OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4",
			"Mozilla/5.0 (iPad; CPU OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A501 Safari/9537.53"
	};

	public static Document getDomByURL(String url){
		Connection connect = null;
		Document doc = null;
		Connection data = null;
		try {
			connect = Jsoup.connect(url);

			Random index = new Random();
			String u = ua[Math.abs(index.nextInt()%15)];
			Map<String, String> header = new HashMap<String, String>();
			header.put("User-Agent", u);
			header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			header.put("Accept-Language", "zh-CN,zh;q=0.9");
			header.put("Accept-Encoding", "gzip, deflate, br");
			data = connect.headers(header);
			doc = data.cookie("Cookie", "x-wl-uid=1o5x1cnsNX8dIuzgkYcVs7TCLq3Xewe/FqmLRA1+v9CV5WXGsg0pdpVAhHh/oAQBp56wcw5qgxTc=; p2ePopoverID_461-9632702-8669149=1; session-token=BRxOD+zLRKam7jX/R1w3JSdXC4Px9i2ZXx5HIcx7Ij0gtC9ekYeaP08ZVRfWAZSz+GMMgTSY759DSlNmuYNi7SETSpgWrNNIuUSVtwPScBp9/TekHaJudj/6oUN8RQQcoLvptc52pGz7qMGgIRTvA8Fj3Yi0+xYicrY/hsZHmjFlnMfBPagK5UlwBrj6cHmQ; csm-hit=tb:22GWKA6SQNQ6HVDWF889+s-22GWKA6SQNQ6HVDWF889|1533001306221&adb:adblk_no; ubid-acbcn=461-7887216-4648267; session-id-time=2082729601l; session-id=461-9632702-8669149")
					.ignoreContentType(true).get();
			connect.method(Connection.Method.GET);
			connect.followRedirects(false);
//			Connection.Response response = connect.execute();
//			System.out.println("------------"+response.cookies());
		} catch (SocketTimeoutException e1) {
			log.error(url+"页面连接超时失败");
			try{
				int count = 0;
				while(doc == null){
					Thread.sleep(5000);
					doc = data.cookie("Cookie", "x-wl-uid=1o5x1cnsNX8dIuzgkYcVs7TCLq3Xewe/FqmLRA1+v9CV5WXGsg0pdpVAhHh/oAQBp56wcw5qgxTc=; p2ePopoverID_461-9632702-8669149=1; session-token=BRxOD+zLRKam7jX/R1w3JSdXC4Px9i2ZXx5HIcx7Ij0gtC9ekYeaP08ZVRfWAZSz+GMMgTSY759DSlNmuYNi7SETSpgWrNNIuUSVtwPScBp9/TekHaJudj/6oUN8RQQcoLvptc52pGz7qMGgIRTvA8Fj3Yi0+xYicrY/hsZHmjFlnMfBPagK5UlwBrj6cHmQ; csm-hit=tb:22GWKA6SQNQ6HVDWF889+s-22GWKA6SQNQ6HVDWF889|1533001306221&adb:adblk_no; ubid-acbcn=461-7887216-4648267; session-id-time=2082729601l; session-id=461-9632702-8669149")
							.ignoreContentType(true).post();
					count++;
					if(count == 3){
						break;
					}
				}
			}catch (HttpStatusException e) {
				log.error(url+"页面不存在");
			}catch(Exception e){
				e.printStackTrace();
			}
		} catch (HttpStatusException e) {
			log.error(url+"页面不存在");
		} catch (Exception e) {
			log.error("获取"+url+"页面Document对象失败", e);
		}
		return doc;
	}

	public static void main(String[] args) throws IOException {
		Connection conn = Jsoup.connect("http://www.17sct.com/city.php?name=CHANGZHOU");
		conn.method(Connection.Method.GET);
		conn.followRedirects(false);
		Connection.Response response = conn.execute();
		System.out.println(response.cookies());
	}
}
