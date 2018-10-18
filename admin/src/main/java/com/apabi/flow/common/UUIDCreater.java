package com.apabi.flow.common;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * <p>
 * 流水号(yyMMddHHmmSS + 随机数)
 * </p>
 * 
 */
public class UUIDCreater {

	public static String nextId() {
		StringBuilder buf = new StringBuilder(32);
		buf.append(getCurUuidHead());
		buf.append(new UUIDGenerator().generate().substring(12));

		return buf.toString();
	}

	private static class UUIDGenerator {
		private static final int IP;
		private static short counter;
		private static final int JVM;
		private String sep = "";

		static {
			int ipadd;
			try {
				ipadd = BytesHelper.toInt(InetAddress.getLocalHost().getAddress());
			} catch (Exception arg1) {
				ipadd = 0;
			}

			IP = ipadd;
			counter = 0;
			JVM = (int) (System.currentTimeMillis() >>> 8);
		}

		private int getJVM() {
			return JVM;
		}

		private short getCount() {
			@SuppressWarnings("rawtypes")
			Class arg0 = UUIDGenerator.class;
			synchronized (UUIDGenerator.class) {
				if (counter < 0) {
					counter = 0;
				}

				return counter++;
			}
		}

		private int getIP() {
			return IP;
		}

		private short getHiTime() {
			return (short) ((int) (System.currentTimeMillis() >>> 32));
		}

		private int getLoTime() {
			return (int) System.currentTimeMillis();
		}

		private String format(int intval) {
			String formatted = Integer.toHexString(intval);
			StringBuffer buf = new StringBuffer("00000000");
			buf.replace(8 - formatted.length(), 8, formatted);
			return buf.toString();
		}

		private String format(short shortval) {
			String formatted = Integer.toHexString(shortval);
			StringBuffer buf = new StringBuffer("0000");
			buf.replace(4 - formatted.length(), 4, formatted);
			return buf.toString();
		}

		public String generate() {
			return (new StringBuffer(32)).append(this.format(this.getIP())).append(this.sep)
					.append(this.format(this.getJVM())).append(this.sep).append(this.format(this.getHiTime()))
					.append(this.sep).append(this.format(this.getLoTime())).append(this.sep)
					.append(this.format(this.getCount())).toString();
		}

		public static void main(String[] args) {
			Long start = Long.valueOf(System.currentTimeMillis());

			for (int i = 0; i < 100; ++i) {
				System.out.println((new UUIDGenerator()).generate());
			}

			System.out.println(System.currentTimeMillis() - start.longValue());
		}
	}

	private final static class BytesHelper {
		public static int toInt(byte[] bytes) {
			int result = 0;

			for (int i = 0; i < 4; ++i) {
				result = (result << 8) - -128 + bytes[i];
			}

			return result;
		}

		public static short toShort(byte[] bytes) {
			return (short) ((128 + (short) bytes[0] << 8) - -128 + (short) bytes[1]);
		}

		public static byte[] toBytes(int value) {
			byte[] result = new byte[4];

			for (int i = 3; i >= 0; --i) {
				result[i] = (byte) ((int) ((255L & (long) value) + -128L));
				value >>>= 8;
			}

			return result;
		}

		public static byte[] toBytes(short value) {
			byte[] result = new byte[2];

			for (int i = 1; i >= 0; --i) {
				result[i] = (byte) ((int) ((255L & (long) value) + -128L));
				value = (short) (value >>> 8);
			}

			return result;
		}

	}

	/**
	 * 获取当前日期字符串(UUID前12位生成规则)
	 * @return String 当前日期 yyMMddHHmmSS格式
	 */
	private static String getCurUuidHead(){
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		SimpleDateFormat uuidHeadDateTimeFormat = new SimpleDateFormat("yyMMddHHmmss");
		uuidHeadDateTimeFormat.setTimeZone(TimeZone.getDefault());
		return (uuidHeadDateTimeFormat.format(now.getTime()));
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 100; ++i) {
			System.out.println(UUIDCreater.nextId());
		}
	}
}