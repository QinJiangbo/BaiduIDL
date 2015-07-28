import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Iterator;

/**
 * Created by Richard on 7/23/15.
 */
public class BaiduIDL {
    public static void main(String[] args) {
        String httpUrl = "http://apis.baidu.com/apistore/idlocr/ocr";
//        String httpArgs = "fromdevice=pc&clientip=10.10.10.0&detecttype=LocateRecognize&languagetype=CHN_ENG&imagetype=1&image="+getOnlineImage("http://pic31.nipic.com/20130715/3501849_104307686185_2.jpg");
        String httpArgs = "fromdevice=pc&clientip=10.10.10.0&detecttype=LocateRecognize&languagetype=CHN_ENG&imagetype=1&image="+getLocalImage("/Users/Richard/Desktop/ggt.jpg");
        String jsonStr = recognizeRequest(httpUrl, httpArgs);
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        Iterator iterator = jsonObject.getJSONArray("retData").iterator();
        int i = 1;
        while (iterator.hasNext()) {
            JSONObject object = (JSONObject) iterator.next();
            System.out.println("第"+i+"行文字是: "+object.get("word"));
            i++;
        }
//        System.out.println(jsonStr);
    }

    /**
     * 识别请求
     * @param httpUrl 请求接口
     * @param httpArgs 请求参数
     * @return 返回结果
     */
    public static String recognizeRequest(String httpUrl, String httpArgs) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("apikey", "3ce436b9a716ebc57375a41cd1e47fb3");
            connection.setDoOutput(true);
            connection.getOutputStream().write(httpArgs.getBytes("utf-8"));
            connection.connect();

            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is,"utf-8"));

            String strReader = null;
            while ((strReader = reader.readLine())!= null) {
                sbf.append(strReader);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取网络上的图片
     * @param imageUrl 图片的地址
     * @return 图片base64的字符串
     */
    public static String getOnlineImage(String imageUrl) {
        String result = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5 * 1000);
            InputStream inputStream = connection.getInputStream();
            byte[] imageData = readInputStream(inputStream);
            BASE64Encoder encoder = new BASE64Encoder();
            result = encoder.encode(imageData).replaceAll("\\+", "%2B").replaceAll("\\r", "").replaceAll("\\n", "");
//            byte[] imageData = readInputStream(inputStream);
//            File file = new File("/Users/Richard/Downloads/Java.jpg");
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(imageData);
//            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 把输入流转化为字节数组
     * @param inStream 输入流
     * @return 目标字节数组
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    /**
     * 获取本地文件
     * @param path 文件路径
     * @return base64字符串
     */
    public static String getLocalImage(String path) {
        String result = null;
        File file = new File(path);
        try {
            InputStream inputStream = new FileInputStream(file);
            byte[] imageData = readInputStream(inputStream);
            BASE64Encoder encoder = new BASE64Encoder();
            result = encoder.encode(imageData).replaceAll("\\+", "%2B").replaceAll("\\r", "").replaceAll("\\n", "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
