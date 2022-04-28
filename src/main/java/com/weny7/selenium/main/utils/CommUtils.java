/*
 * Description:
 *
 * History：
 * ========================================
 * Date              Version       Memo
 * 2022/3/29 13:09     1.0      Created by liuj
 * ========================================
 *
 * Copyright 2021, 迪爱斯信息技术股份有限公司保留。
 */
package com.weny7.selenium.main.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.weny7.selenium.main.model.Config;
import com.weny7.selenium.main.model.HttpProxy;
import com.weny7.selenium.main.model.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.io.FileUtil.FILE_SEPARATOR;
import static cn.hutool.core.io.FileUtil.readLines;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/29 13:09
 */
public class CommUtils {

    //加密盐值
    private final static String salt = "liujiangliujiang";


    public static void main(String[] args) throws IOException {
//        String configPath = "C:\\Users\\Administrator\\Desktop\\u.txt";
//        String mkParent = "D:\\video";
//        List<User> users = getUsers(configPath);
//        mkdir(users, mkParent);
        Runtime.getRuntime().exec("F:\\WorkSpace\\study\\WebSelenium\\utils\\1.exe" + " " + "firefox" + " " + "D:\\video\\2624773928\\l.mp4");
        //getVideoFile(null);
    }

    public static String encode(String content) {
        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, salt.getBytes(StandardCharsets.UTF_8));
        //加密为16进制表示
        String encryptHex = aes.encryptHex(content);
        return encryptHex;
    }

    public static String decode(String content) {
        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, salt.getBytes(StandardCharsets.UTF_8));
        //解密为字符串
        String decryptStr = aes.decryptStr(content, CharsetUtil.CHARSET_UTF_8);
        return decryptStr;
    }

    /**
     * 解析验证码
     *
     * @return
     */
    public static Integer getCode(String str) {
        Integer code = 0;
        String reg = "[1-9][0-9]{4,}";    // ( 为特殊字符，需要用 \\ 转义
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        if (m.find()) {
            code = Integer.parseInt(m.group(0));
            System.out.println(code);  // 组提取字符串 0x993902CE
        }
        return code;
    }

    public static String[] getIp() {
        String[] ip;
        try {
            String res = HttpUtil.get(getConfig().getProxy_url());
            if (res.split(":").length >= 2) {
                ip = res.split(":");
            } else {
                ip = null;
            }
        } catch (Exception e) {
            ip = getIp();
        }
        if (ip == null) {
            ip = getIp();
        }
        return ip;
    }

    public static boolean checkIp(String host, int port) {
        String result2 = null;
        try {
            result2 = HttpRequest.post("https://www.ip138.com/")
                    .setHttpProxy(host, port)
                    .timeout(5000)
                    .execute().body();
        } catch (Exception e) {
            System.out.println(host + ":" + port + "==>代理无效");
            return false;
        }
        return true;
    }

    /**
     * 根据用户创建目录
     *
     * @param users
     */
    public static void mkdir(List<User> users, String rootPath) {
        users.stream().forEach(user -> {
            FileUtil.mkdir(rootPath + FILE_SEPARATOR + user.getName());
            FileUtil.mkdir(rootPath + FILE_SEPARATOR + user.getName() + FILE_SEPARATOR + "pic");
        });
    }

    /**
     * 根据用户创建目录
     *
     * @param users
     */
    public static void mkdirWithCk(List<User> users, String rootPath) {
        users.stream().forEach(user -> {
            FileUtil.mkdir(rootPath + FILE_SEPARATOR + user.getName());
            FileUtil.mkdir(rootPath + FILE_SEPARATOR + user.getName() + FILE_SEPARATOR + "pic");
            FileUtil.mkdir(rootPath + FILE_SEPARATOR + user.getName() + FILE_SEPARATOR + "ck");
            FileUtil.mkdir(rootPath + FILE_SEPARATOR + user.getName() + FILE_SEPARATOR + "proxy");
        });
    }

    public static boolean checkElementExists(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static WebElement getElement(WebDriver driver, By by) throws Exception {
        WebElement webElement = null;
        while (ObjectUtil.isNull(webElement)) {
            if (checkElementExists(driver, by)) {
                webElement = driver.findElement(by);
            }
            TimeUnit.SECONDS.sleep(1);
        }
        return webElement;
    }

    /**
     * 获取用户列表
     *
     * @param path
     * @return
     */
    public static List<User> getUsers(String path) {
        List<String> list = FileUtil.readUtf8Lines(path);
        List<User> users = new ArrayList<>();
        list.stream().forEach(s -> {
            String[] array = s.split("----");
            User user = new User();
            user.setName(array[0]);
            user.setPhone(Long.parseLong(array[1]));
            user.setUrl("http://149.28.78.170/getsms?token=" + array[2]);
            user.setTopic(array[3]);
            users.add(user);
        });
        return users;
    }

    /**
     * 获取用户对应的视频文件
     *
     * @param user
     * @return
     */
    public static File getVideoFile(User user) {
        File[] files = FileUtil.ls(getConfig().getVideo_path() + FILE_SEPARATOR + user.getName());
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                return files[i];
            }
        }
        return null;
    }

    /**
     * 获取图片文件
     *
     * @param user
     * @return
     */
    public static File getPicFile(User user) {
        File[] files = FileUtil.ls(getConfig().getVideo_path() + FILE_SEPARATOR + user.getName() + FILE_SEPARATOR + "pic");
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                return files[i];
            }
        }
        return null;
    }

    /**
     * 获取图片文件根据名称
     *
     * @param user
     * @return
     */
    public static File getPicFileByName(User user, String name) {
        File[] files = FileUtil.ls(getConfig().getVideo_path() + FILE_SEPARATOR + user.getName() + FILE_SEPARATOR + "pic");
        for (File file : files) {
            if (FileUtil.mainName(file).trim().equals(name.trim())) {
                return file;
            }
        }
        return null;
    }

    /**
     * 根据名称删除视频和图片文件
     *
     * @param user
     * @return
     */
    public static void delVideoAndPicFileByName(User user, String name) {
        File[] filesv = FileUtil.ls(getConfig().getVideo_path() + FILE_SEPARATOR + user.getName());
        for (File file : filesv) {
            if (FileUtil.mainName(file).trim().equals(name.trim())) {
                FileUtil.del(file);
            }
        }
        File[] filesp = FileUtil.ls(getConfig().getVideo_path() + FILE_SEPARATOR + user.getName() + FILE_SEPARATOR + "pic");
        for (File file : filesp) {
            if (FileUtil.mainName(file).trim().equals(name.trim())) {
                FileUtil.del(file);
            }
        }
    }


    public static String getAbpath() {
        File directory = new File("");
        String alpath = directory.getAbsolutePath();
        return alpath;
    }

    public static Config getConfig() {
        String path = getAbpath() + "\\utils\\config.json";
        FileReader fileReader = new FileReader(path);
        String result = fileReader.readString();
        Config cfg = JSONUtil.toBean(result, Config.class);
        return cfg;
    }

    public static void replaceTxt(String name) {
        String path = getAbpath() + "\\utils\\logs\\" + DateTime.now().toDateStr() + "-wait.txt";
        //默认UTF-8编码，可以在构造中传入第二个参数做为编码
        FileReader fileReader = new FileReader(path);
        String result = fileReader.readString();
        result = result.replace(name, "");
        writeTxt(path, result);
    }

    public static void writeSuccessStr(String name) {
        String path = getAbpath() + "\\utils\\logs\\" + DateTime.now().toDateStr() + "-success.txt";
        FileWriter writer = new FileWriter(path);
        writer.append(name + "\r\n");
    }

    public static boolean clearSuccessStr() {
        String path = getAbpath() + "\\utils\\logs\\" + DateTime.now().toDateStr() + "-success.txt";
        boolean t = FileUtil.del(path);
        return t;
    }

    public static void writeTxt(String path, String str) {

//        FileWriter.create(new File(path), Charset.forName("utf-8"));
        FileWriter writer = new FileWriter(path);
        writer.write(str);
    }

    public static boolean clearWaitTxt() {
        String path = getAbpath() + "\\utils\\logs\\" + DateTime.now().toDateStr() + "-wait.txt";
        boolean t = FileUtil.del(path);
        return t;
    }

    public static void fileAppender(String str) {

        String path = getAbpath() + "\\utils\\logs\\" + DateTime.now().toDateStr() + "-wait.txt";
        FileAppender appender = new FileAppender(new File(path), 16, true);
        appender.append(str);
        appender.flush();
        appender.toString();
    }

    public static WebDriver setFireFoxProxy(String[] ip) {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.http", ip[0]);
        profile.setPreference("network.proxy.http_port", Integer.parseInt(ip[1]));
        profile.setPreference("network.proxy.ssl", ip[0]);
        profile.setPreference("network.proxy.ssl_port", Integer.parseInt(ip[1]));
        FirefoxOptions c = new FirefoxOptions();
        c.setProfile(profile);
        WebDriver driver = new FirefoxDriver(c);
        return driver;
    }

    /**
     * 切换代理ip
     *
     * @param driver
     * @param host
     * @param port
     */
    public static void changeIp(WebDriver driver, String host, String port) {
        driver.get("about:config");
        String js = "var pf = Components.classes[\"@mozilla.org/preferences-service;1\"].getService(Components.interfaces.nsIPrefBranch);\n" +
                "\n" +
                "    pf.setIntPref(\"network.proxy.type\", 1);\n" +
                "\n" +
                "    pf.setCharPref(\"network.proxy.http\", \"{}\");\n" +
                "\n" +
                "    pf.setIntPref(\"network.proxy.http_port\", {});\n" +
                "\n" +
                "    pf.setCharPref(\"network.proxy.ssl\", \"{}\");\n" +
                "\n" +
                "    pf.setIntPref(\"network.proxy.ssl_port\", {});";

        String result1 = StrFormatter.format(js, host, port, host, port);
        JavascriptExecutor j = (JavascriptExecutor) driver;
        j.executeScript(result1);

    }

    /**
     * 写入当前用户的ck
     *
     * @param u
     * @param str
     */
    public static void writeCkTxt(User u, String str) {
        Config cfg = getConfig();
        FileWriter writer = new FileWriter(cfg.getVideo_path() + FILE_SEPARATOR + u.getName() + FILE_SEPARATOR + "ck" + FILE_SEPARATOR + "ck.txt");
        writer.write(str);
    }

    /**
     * 获取用户的ck
     *
     * @param u
     */
    public static String getCkTxt(User u) {
        Config cfg = getConfig();
        String path = cfg.getVideo_path() + FILE_SEPARATOR + u.getName() + FILE_SEPARATOR + "ck" + FILE_SEPARATOR + "ck.txt";
        if (!FileUtil.exist(path)) {
            return "";
        }
        FileReader fileReader = new FileReader(path);
        String result = fileReader.readString();
        return result;
    }

    public static String getCkTxtNoUser() {
        // Config cfg = getConfig();
        String path = getAbpath() + "\\utils\\ck.txt";
        // String path = cfg.getVideo_path() + FILE_SEPARATOR + u.getName() + FILE_SEPARATOR + "ck" + FILE_SEPARATOR + "ck.txt";
        if (!FileUtil.exist(path)) {
            return "";
        }
        FileReader fileReader = new FileReader(path);
        String result = fileReader.readString();
        return result;
    }

    /**
     * 删除ck文件
     *
     * @param u
     * @return
     */
    public static boolean delCkTxt(User u) {
        Config cfg = getConfig();
        String path = cfg.getVideo_path() + FILE_SEPARATOR + u.getName() + FILE_SEPARATOR + "ck" + FILE_SEPARATOR + "ck.txt";
        boolean t = FileUtil.del(path);
        return t;
    }

    public static boolean delVideo(User u, File file) {
        Config cfg = getConfig();
        String path = cfg.getVideo_path() + FILE_SEPARATOR + u.getName() + FILE_SEPARATOR + "ck" + FILE_SEPARATOR + "ck.txt";
        boolean t = FileUtil.del(path);
        return t;
    }

    /**
     * 更新cktxt
     *
     * @param u
     * @return
     */
    public static void updateCkTxt(User u, String str) {
        boolean f = delCkTxt(u);
        if (f) {
            writeCkTxt(u, str);
        }

    }

    /**
     * 动态生成zip包到对应用户目录
     *
     * @param u
     * @param httpProxy
     * @return
     */
    public static File makeProxyZip(User u, HttpProxy httpProxy) {
        String bgjs = "var config = {\n" +
                "  mode: \"fixed_servers\",\n" +
                "  rules: {\n" +
                "    singleProxy: {\n" +
                "      scheme: \"http\",\n" +
                "      host: \"" + httpProxy.getIp() + "\",\n" +
                "      port: " + httpProxy.getPort() + "\n" +
                "    },\n" +
                "    bypassList: [\"mimvp.com\"]\n" +
                "  }\n" +
                "};\n" +
                "chrome.proxy.settings.set({ value: config, scope: \"regular\" }, function () { });\n" +
                "function callbackFn(details) {\n" +
                "  return {\n" +
                "    authCredentials: {\n" +
                "      username: \"" + httpProxy.getUser() + "\",\n" +
                "      password: \"" + httpProxy.getPwd() + "\"\n" +
                "    }\n" +
                "  };\n" +
                "}\n" +
                "chrome.webRequest.onAuthRequired.addListener(\n" +
                "  callbackFn,\n" +
                "  { urls: [\"<all_urls>\"] },\n" +
                "  ['blocking']\n" +
                ");";
        String mainfest = "{\n" +
                "    \"version\": \"1.0.0\",\n" +
                "    \"manifest_version\": 2,\n" +
                "    \"name\": \"Chrome Proxy\",\n" +
                "    \"permissions\": [\n" +
                "        \"proxy\",\n" +
                "        \"tabs\",\n" +
                "        \"unlimitedStorage\",\n" +
                "        \"storage\",\n" +
                "        \"<all_urls>\",\n" +
                "        \"webRequest\",\n" +
                "        \"webRequestBlocking\"\n" +
                "    ],\n" +
                "    \"background\": {\n" +
                "        \"scripts\": [\n" +
                "            \"background.js\"\n" +
                "        ]\n" +
                "    },\n" +
                "    \"minimum_chrome_version\": \"22.0.0\"\n" +
                "}";
        InputStream bgjsIs = new ByteArrayInputStream(bgjs.getBytes());
        InputStream mainfestIs = new ByteArrayInputStream(mainfest.getBytes());

        Config cfg = getConfig();
        String path = cfg.getVideo_path() + FILE_SEPARATOR + u.getName() + FILE_SEPARATOR + "proxy" + FILE_SEPARATOR + "proxy.zip";
        ZipUtil.zip(FileUtil.file(path),
                new String[]{"background.js", "manifest.json"}, new InputStream[]{bgjsIs, mainfestIs}
        );

        return FileUtil.file(path);
    }


    public static void alertJs(WebDriver driver, String str) {
        JavascriptExecutor j = (JavascriptExecutor) driver;
        j.executeScript("document.getElementsByClassName(\"title-line\")[0].innerText='" + str + "'");
    }

    /**
     * 获取用户列表
     *
     * @param path
     * @return
     */
    public static List<User> getCkUsers(String path) {
        List<String> list = FileUtil.readUtf8Lines(path);
        List<User> users = new ArrayList<>();
        list.stream().forEach(s -> {
            if(!StrUtil.isEmpty(s)){
                String[] array = s.split("----");
                User user = new User();
                user.setName(array[0]);
                user.setTopic(array[1]);
                HttpProxy proxy = HttpProxy.builder().ip(array[2])
                        .port(Integer.parseInt(array[3]))
                        .user(array[4])
                        .pwd(array[5])
                        .build();
                user.setHttpProxy(proxy);
                users.add(user);
            }
        });
        return users;
    }

}
