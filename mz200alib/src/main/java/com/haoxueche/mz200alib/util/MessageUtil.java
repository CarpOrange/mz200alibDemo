package com.haoxueche.mz200alib.util;

import com.haoxueche.winterlog.L;

public class MessageUtil {
  public static String byte2Hex(Byte paramByte) {
        return String.format("%02x", new Object[] { paramByte }).toUpperCase();
    }

  public static String byteArrToHex(byte[] paramArrayOfByte) {
    StringBuilder localStringBuilder = new StringBuilder();
    int j = paramArrayOfByte.length;
    int i = 0;
    for (;;) {
      if (i >= j) {
        return localStringBuilder.toString();
      }
      localStringBuilder.append(byte2Hex(Byte.valueOf(paramArrayOfByte[i])));
      localStringBuilder.append(" ");
      i += 1;
    }
  }

  public static String ByteArrToHex(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    StringBuilder localStringBuilder = new StringBuilder();
    for (;;) {
      if (paramInt1 >= paramInt2) {
        return localStringBuilder.toString();
      }
      localStringBuilder.append(byte2Hex(Byte.valueOf(paramArrayOfByte[paramInt1])));
      paramInt1 += 1;
    }
  }

  public static byte hexToByte(String paramString) {
    return (byte) Integer.parseInt(paramString, 16);
  }

  /**
   * 把16进制字符串转换成字节数组
   */
  public static byte[] hexToByteArr(String hex) {
    int len = (hex.length() / 2);

//    if(hex.length() % 2 != 0) {
//      len += 1;
//      hex = "0" + hex;
//    }

    byte[] result = new byte[len];
    char[] achar = hex.toCharArray();
    for (int i = 0; i < len; i++) {
      int pos = i * 2;
      result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
    }
    return result;
  }

  private static int toByte(char c) {
    byte b = (byte) "0123456789ABCDEF".indexOf(c);
    return b;
  }

  public static int HexToInt(String paramString) {
    return Integer.parseInt(paramString, 16);
  }

  public static String str2HexStr(String str) {
    char[] chars = "0123456789ABCDEF".toCharArray();
    StringBuilder sb = new StringBuilder("");
    byte[] bs = str.getBytes();
    int bit;
    for (int i = 0; i < bs.length; i++) {
      bit = (bs[i] & 0x0f0) >> 4;
      sb.append(chars[bit]);
      bit = bs[i] & 0x0f;
      sb.append(chars[bit]);
    }
    return sb.toString();
  }

  /**
   * 数组转换成十六进制字符串
   * @return HexString
   */
  public static String bytesToHexString(byte[] bytes) {
    return bytesToHexString(bytes, null);
  }

  public static String bytesToHexString(byte[] bytes, String separator) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      String code = Integer.toHexString(bytes[i] & 0xFF);
      if ((bytes[i] & 0xFF) < 16) {
        sb.append('0');
      }

      sb.append(code);

      if (separator != null && i < bytes.length - 1) {
        sb.append(separator);
      }
    }

    return sb.toString();
  }

  /**
   * 数组转成十六进制字符串
   * @return HexString
   */
  public static String toHexString1(byte[] b){
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < b.length; ++i){
      buffer.append(toHexString1(b[i]));
    }
    return buffer.toString();
  }
  public static String toHexString1(byte b){
    String s = Integer.toHexString(b & 0xFF);
    if (s.length() == 1){
      return "0" + s;
    }else{
      return s;
    }
  }

  /**
   * 十六进制字符串转换成字符串
   * @return String
   */
  public static String hexStr2Str(String hexStr) {

    String str = "0123456789ABCDEF";
    char[] hexs = hexStr.toCharArray();
    byte[] bytes = new byte[hexStr.length() / 2];
    int n;
    for (int i = 0; i < bytes.length; i++) {
      n = str.indexOf(hexs[2 * i]) * 16;
      n += str.indexOf(hexs[2 * i + 1]);
      bytes[i] = (byte) (n & 0xff);
    }
    return new String(bytes);
  }

  /**
   * 十六进制字符串转换字符串
   * @return String
   */
  public static String toStringHex(String s) {
    byte[] baKeyword = new byte[s.length() / 2];
    for (int i = 0; i < baKeyword.length; i++) {
      try {
        baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                i * 2, i * 2 + 2), 16));
      } catch (Exception e) {
        L.e(e);
      }
    }
    try {
      s = new String(baKeyword, "utf-8");// UTF-16le:Not
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return s;
  }

  public static String int2HexStr(int value) {
    String hex = Integer.toHexString(value);
    if(value < 10) {
      hex = "0" + hex;
    }
    return hex;
  }

  public static final String bytesToHexStringReversal(byte[] bArray) {
    StringBuffer sb = new StringBuffer(bArray.length);
    String sTemp;
    for (int i = bArray.length - 1; i >= 0; i--) {
      sTemp = Integer.toHexString(0xFF & bArray[i]);
      if (sTemp.length() < 2) {
          sb.append(0);
      }
      sb.append(sTemp.toUpperCase());
    }
    return sb.toString();
  }

}
