package com.singlee.webpageserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlparser.beans.StringBean;

public class CatchWebUtil {

    public static String getHtmlbyURL(String httpurl) throws IOException {
        String currentStr = "";
        String content = "";
        InputStream is = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(httpurl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.connect();
            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"));
            while ((currentStr = reader.readLine()) != null) {
                content += currentStr + "\n";
            }
            content = Html2Text(content).replaceAll("&nbsp;", "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
            is.close();
        }
        return content;

    }

    public static String getJsonbyURL(String httpurl) throws IOException {
        String currentStr = "";
        String content = "";
        InputStream is = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(httpurl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.connect();
            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"));
            while ((currentStr = reader.readLine()) != null) {
                content += currentStr + "\n";
            }
            content = content.replaceAll("&nbsp;", "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
            is.close();
        }
        return content;

    }

    public static String getContentbyURL(String HttpURL) {
        StringBean sb = new StringBean();
        sb.setLinks(false);
        sb.setReplaceNonBreakingSpaces(true);
        sb.setCollapse(true);
        sb.setURL(HttpURL);
        return sb.getStrings();
    }

    public static String Html2Text(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;

        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            // }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            // }
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll("\n"); // 过滤html标签

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }
}
