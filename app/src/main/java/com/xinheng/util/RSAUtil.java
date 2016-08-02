package com.xinheng.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import javax.crypto.Cipher;
public class RSAUtil
{
    
    private static final String RSA_ECB_PADDING = "RSA/ECB/PKCS1Padding";
     private static final String RSA = "RSA";
    private static final String PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3NCUnkti5yA/b0brbiwMc5y7wOT8ufaNrBAYgDN8ZIeo6WdvM1Wm5OVFsclYmWuL7CsyI49Y7ZViUctE9UBA8C+x+0r2GAQmXgee/HaJh2sVwdl+i88gIRS8gumLLYrisHyTHxhqfEb4k9sS1wN8Fc9B7ITZtl2XKyjt3FV4KywIDAQAB";
    
    private static final String PRIVATE_KEY =
        "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALc0JSeS2LnID9vRutuLAxznLvA5Py59o2sEBiAM3xkh6jpZ28zVabk5UWxyViZa4vsKzIjj1jtlWJRy0T1QEDwL7H7SvYYBCZeB578domHaxXB2X6LzyAhFLyC6YstiuKwfJMfGGp8RviT2xLXA3wVz0HshNm2XZcrKO3cVXgrLAgMBAAECgYEAi8EhnsCsx3xQbLXfP18aLtBExEhHZGW/ZgGZ0pU/E2MF1SF7RRSAJCYPcRT/lx66DBu+z6ZOZNz457V23LjYyEnZXcSg1IeRZtaHP4vUH+ptVlHnQfOsZnTVsJwhb8XQ8YLO1AzmdASjBXr2BjbpsO3ydvtK8jLBmtAzVzRm3aECQQDtakfNJq491rdhtiosT/OA1VKD+lpEqou/gy9wTZbBHfHuPA6x/WBlEQWcsFSNiuJ8nGViMnSA8meASV/jhXqJAkEAxYt8NiuplS9sD/C+hDvusQseiNTKib4+CLpYNFSJsEGE9/nxvqNrsxo4wA3W+uZkcy038/5nhTiLQEmTAJM1swJBAOtkzCohJvOTN6F+71OeNe3QFI9cozGf8w2AcYGkvEtfeGdgTqW1dmTeurS/tCXexW8N1gvFwPO05GAy4AsDZhkCQQC75vvVgx6hmyQM3ZA6s89NdCgtO0sUTnMvViEEVc1KbGgQgBzkmwmcqTqF5CQzL+cob0Cjw7+wRqKeGd3MVDplAkB91FRX5ZS1d2qfGmTi5T7VnCgNtPKprnQCerHIiJ1ELKsjuhwkKl7sep/Lf9Rl+rhWZjQvjtX8/eR1FsACqHKn";
    
    /** */
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    
    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;
    
    // change key to String
    @SuppressWarnings("unused")
    private static String getKeyString(Key key)
    {
        return Base64.encode(key.getEncoded());
    }
    
    // change String to key
    private static PublicKey getPublicKey(String keyStr)
        throws Exception
    {
        return KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64.decode(keyStr)));
    }
    
    // change String to key
    private static PrivateKey getPrivateKey(String keyStr)
        throws Exception
    {
        return KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(keyStr)));
    }
    
    // 加密
    private static byte[] encrypt(Key key, byte[] obj, int offSet, int endSet)
    {
        if (key != null)
        {
            try
            {
//               Cipher cipher = Cipher.getInstance(RSA);    //liuyue
                Cipher cipher = Cipher.getInstance(RSA_ECB_PADDING);      //Android 客户端使用 RSA_ECB_PADDING = "RSA/ECB/PKCS1Padding";
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return cipher.doFinal(obj, offSet, endSet);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    // 解密
    private static byte[] decrypt(Key key, byte[] obj, int offSet, int endSet)
    {
        if (key != null)
        {
            try
            {
//                Cipher cipher = Cipher.getInstance(RSA);  //liuyue
                Cipher cipher = Cipher.getInstance(RSA_ECB_PADDING);  //Android 客户端使用 RSA_ECB_PADDING = "RSA/ECB/PKCS1Padding";
                cipher.init(Cipher.DECRYPT_MODE, key);
                return cipher.doFinal(obj, offSet, endSet);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    // 服务端加密
    public static String serverEncrypt(String value)
    {
        if (RSAUtil.isEmpty(value))
        {
            return null;
        }
        try
        {
            RSAPrivateKey privateKey = (RSAPrivateKey)getPrivateKey(PRIVATE_KEY);
            value = java.net.URLEncoder.encode(value, "utf-8");
            byte[] data = value.getBytes();
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0)
            {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK)
                {
                    cache = encrypt(privateKey, data, offSet, MAX_ENCRYPT_BLOCK);
                }
                else
                {
                    cache = encrypt(privateKey, data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] enByte = out.toByteArray();
            out.close();
            return Base64.encode(enByte);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    // 服务端解密
    public static String serverDecrypt(String value)
    {
        if (RSAUtil.isEmpty(value))
        {
            return null;
        }
        try
        {
            RSAPrivateKey privateKey = (RSAPrivateKey)getPrivateKey(PRIVATE_KEY);
            byte[] enBytes = Base64.decode(value);
            int inputLen = enBytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0)
            {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK)
                {
                    cache = decrypt(privateKey, enBytes, offSet, MAX_DECRYPT_BLOCK);
                }
                else
                {
                    cache = decrypt(privateKey, enBytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] deByte = out.toByteArray();
            out.close();
            return java.net.URLDecoder.decode(new String(deByte), "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    // 客户端加密
    public static String clientEncrypt(String value)
    {
        if (RSAUtil.isEmpty(value))
        {
            return null;
        }
        try
        {
            RSAPublicKey publicKey = (RSAPublicKey)getPublicKey(PUBLIC_KEY);
            value = java.net.URLEncoder.encode(value, "utf-8");
            byte[] data = value.getBytes();
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0)
            {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK)
                {
                    cache = encrypt(publicKey, data, offSet, MAX_ENCRYPT_BLOCK);
                }
                else
                {
                    cache = encrypt(publicKey, data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] enByte = out.toByteArray();
            out.close();
            return Base64.encode(enByte);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    // 客户端解密
    public static String clientDecrypt(String value)
    {
        if (RSAUtil.isEmpty(value))
        {
            return null;
        }
        try
        {
            RSAPublicKey publicKey = (RSAPublicKey)getPublicKey(PUBLIC_KEY);
            byte[] enBytes = Base64.decode(value);
            int inputLen = enBytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0)
            {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK)
                {
                    cache = decrypt(publicKey, enBytes, offSet, MAX_DECRYPT_BLOCK);
                }
                else
                {
                    cache = decrypt(publicKey, enBytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] deByte = out.toByteArray();
            out.close();
            return java.net.URLDecoder.decode(new String(deByte), "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean isEmpty(String str)
    {
        return ((str == null) || (str.length() == 0) || ("".equals(str)) || ("null".equals(str)));
    }
    
    public static void main(String[] args)
    {
        try
        {
//            Date date = new Date();
//            System.out.println(date.getTime());
//            String st="{\"account\":\"test001\",\"password\":\"96e79218965eb72c92a549dd5a330112\"}";
//            MD5 md5 = new MD5();
////            System.out.println(md5.getMD5_32((md5.getMD5_32("111111")+Constants.SALT_MD5)));
//            String t = clientEncrypt(st);
//            System.out.println(t);
//            System.out.println(serverDecrypt(t));
            System.out.println(clientDecrypt("Ewn+L3h+4w++pqiL0PcHiQFLrCU4fxTcWH/m0DeX4RJSRUSHBvLTMnAdWpQ97WRvxGfWEF8LymE2dlCvea7Q89a/Cmtgy85yC5F/oHh7B6/S2XFBvpQ1jDDQQ8O3AYNZ773MXXk3uSIosN1MKhgQhB5KogcO2sSPEcGAw9GG3N8="));
//            System.out.println(ObjectUtils.toString(null));
           // System.out.println(encrypt("","test001"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 自定义加密算法
     * 
     * @param password
     * @param salt
     * @return
     * @see [类、类#方法、类#成员]
     */
//    private static String encrypt(String password, String salt)
//    {
//        MD5 md5 = new MD5();
//        return new SimpleHash("SHA-1", md5.getMD5_32(md5.getMD5_32("111111") + Constants.SALT_MD5), salt).toString();
//    }
}