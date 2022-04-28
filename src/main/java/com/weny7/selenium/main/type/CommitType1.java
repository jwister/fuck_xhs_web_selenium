/*
 * Description:
 *
 * History：
 * ========================================
 * Date              Version       Memo
 * 2022/3/31 22:42     1.0      Created by liuj
 * ========================================
 *
 * Copyright 2021, 迪爱斯信息技术股份有限公司保留。
 */
package com.weny7.selenium.main.type;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.weny7.selenium.main.model.Config;
import com.weny7.selenium.main.model.User;
import com.weny7.selenium.main.utils.CommUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/31 22:42
 */
public class CommitType1 {


    public User xiaohongshu(WebDriver webDriver, User user, Config cfg) throws Exception {
//        wirteLog("开始获取代理ip");
//        String[] ipPort = CommUtils.getIp();
//        while (!CommUtils.checkIp(ipPort[0], Integer.parseInt(ipPort[1].trim()))) {
//            ipPort = CommUtils.getIp();
//        }
//        wirteLog("开始获取代理ip完成");
//        wirteLog("开始设置代理ip");
//        CommUtils.changeIp(webDriver, ipPort[0], ipPort[1]);
//        wirteLog("设置代理ip完成，当前ip==》" + ipPort[0] + ":" + ipPort[1]);
//        TimeUnit.SECONDS.sleep(1);
        //打登录页面网站
        webDriver.get("https://creator.xiaohongshu.com/login");
        wirteLog("开始登录！");
        TimeUnit.SECONDS.sleep(8);
        if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"slider-captcha-loading\"]"))) {
            webDriver.quit();
            return user;
        }

        //选择区号
        //webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[1]/div[1]/div[1]/div/div[1]/div/div/div/input")).click();
        WebElement firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[1]/div[1]/div[1]/div/div[1]/div/div/div/input")));
        firstResult.click();

        webDriver.findElements(By.className("css-1tkdge6")).get(6).click();
        //输入号码
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[1]/div[1]/input")).sendKeys(user.getPhone().toString());

        TimeUnit.SECONDS.sleep(1);
        //点击发送验证码
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[1]/div[2]/div[2]/div/div[2]")).click();

        TimeUnit.SECONDS.sleep(1);
        //开始接码
        Integer i = 0;
        Integer c = 0;
        while (i.equals(0) && c <= 30) {
            c++;
            String res = HttpUtil.get(user.getUrl());
            i = CommUtils.getCode(res);
            wirteLog("等待验证码。。。。");
            TimeUnit.SECONDS.sleep(1);
        }
        if (c >= 30) {
            wirteLog(user.getName() + "=>验证码等待超时：跳过当前用户开始下一个用户");
            webDriver.quit();
            return user;
        }
        wirteLog("取到验证码：" + i);
        //输入验证码
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[1]/div[2]/input")).sendKeys(i.toString());
        TimeUnit.SECONDS.sleep(1);
        //点击登录
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/button")).click();
        TimeUnit.SECONDS.sleep(1);
        // TODO: 2022/4/4 这里得加登录异常的处理
        if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[3]/div"))) {
            String err = webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[3]/div")).getText();
            if (!err.equals("")) {
                webDriver.quit();
                return user;
            }
        }

        //点击发布视频
        // webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")));
        firstResult.click();

       // wirteLog("开始切换本地代理");
        /*******文件上传之前切换代理*****/
        //切换代理上传文件
//        JavascriptExecutor j = (JavascriptExecutor) webDriver;
//        j.executeScript("window.open('https://www.baidu.com')");
//        ArrayList<String> tabs = new ArrayList<String>(webDriver.getWindowHandles());
//        webDriver.switchTo().window(tabs.get(1));
//        CommUtils.changeIp(webDriver, "127.0.0.1", "10809");
//        webDriver.close();
        TimeUnit.SECONDS.sleep(1);
        /*******代理切换完成*****/
        //wirteLog("本地代理切换完成开始上传文件！");
        String filePath = null;
        try {
            filePath = CommUtils.getVideoFile(user).getPath();
        } catch (Exception e) {
            System.out.println(user.getName() + "目录下无视频，将跳过！");
            webDriver.quit();
            return user;
        }
        System.out.println(filePath);
        TimeUnit.SECONDS.sleep(3);
        webDriver.findElement(By.id("app")).click();
        //webDriver.findElement(By.xpath("//*[@id=\\\"publish-container\\\"]/div/div[3]")).click();
//        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
//                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[3]")));
//        firstResult.click();
        File directory = new File("");
        String alpath = directory.getAbsolutePath();
        Runtime.getRuntime().exec(alpath + "\\utils\\1.exe" + " " + "firefox" + " " + filePath);


        TimeUnit.SECONDS.sleep(2);
        //录入标题
        // webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)));
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")));
        firstResult.sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[0]);

        //录入描述
        webDriver.findElement(By.id("post-textarea")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[1]);
        TimeUnit.SECONDS.sleep(1);


        // 等待是否上传完成
        new WebDriverWait(webDriver, Duration.ofSeconds(3000))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[6]/div/div/div[2]/div[4]/div")));


        //编辑封面开始
        //弹出编辑框
        //webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")));
        firstResult.click();
        TimeUnit.SECONDS.sleep(1);
        //webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")));
        firstResult.click();
        //修改封面
        wirteLog("开始上传封面！");
        String picFilePath = null;
        try {
            picFilePath = CommUtils.getPicFile(user).getPath();
        } catch (Exception e) {
            System.out.println(user.getName() + "目录下无封面，将跳过！");
            webDriver.quit();
            return user;
        }
        System.out.println(picFilePath);
        TimeUnit.SECONDS.sleep(1);
        //点击封面上传按钮
        webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/input")).sendKeys(picFilePath);
//        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(160))
//                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/div/div[2]")));
//        firstResult.click();

       // Runtime.getRuntime().exec(alpath + "\\utils\\1.exe" + " " + "firefox" + " " + picFilePath);

        //*[@id="cover-modal-0"]/div/div/div[2]/div/div[2]/div[3]/div[2]/input
        TimeUnit.SECONDS.sleep(2);
        //点击确定
        // webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(40))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")));
        firstResult.click();


        //这里注释的逻辑是自动点选封面
        // JavascriptExecutor j = (JavascriptExecutor) webDriver;
        // j.executeScript("document.querySelector(\"#cover-modal-0 > div > div > div.dyn.content.css-t0051x.css-y1z97h > div > div:nth-child(2) > div.css-1v3caum > div > div.list > ul > li.point\").style.left = '100px';");
        //TimeUnit.SECONDS.sleep(1);
        // webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[2]/div/div[2]/ul/li[21]")).click();
        // TimeUnit.MILLISECONDS.sleep(500);
        // webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")).click();
        //TimeUnit.MILLISECONDS.sleep(20000);

        /****发布之前要切回之前的代理****/
//        j.executeScript("window.open('https://www.baidu.com')");
//        ArrayList<String> tab2 = new ArrayList<String>(webDriver.getWindowHandles());
//        webDriver.switchTo().window(tab2.get(1));
//        CommUtils.changeIp(webDriver, ipPort[0], ipPort[1]);
//        webDriver.close();
        TimeUnit.SECONDS.sleep(1);
//        Actions action = new Actions(webDriver);
//        WebElement pngElement = webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")); //获取元素
//        action.moveToElement(pngElement).perform();

        //点击发布
        webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")).click();
        TimeUnit.SECONDS.sleep(2);
        //关闭浏览器
        webDriver.quit();
        CommUtils.replaceTxt(user.getName());
        return null;
    }

    public User xiaohongshuWithCk(WebDriver webDriver, User user, Config cfg) throws Exception {
        String ck = CommUtils.getCkTxt(user);
        webDriver.get("https://creator.xiaohongshu.com/login");
        if (ck.equals("")) {
            while (!CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a"))) {
                TimeUnit.SECONDS.sleep(1);
            }
//            Set<Cookie> cookie1 = webDriver.manage().getCookies();
        } else {
//            String ck = "[{\"isSecure\":false,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"timestamp2.sig\",\"isHttpOnly\":false,\"expiry\":1680602783000,\"value\":\"vxDvZtIgkLDuw8w5BPsOPi-BUYJs_r_mzdvhwSP_wnM\"},{\"isSecure\":false,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"timestamp2\",\"isHttpOnly\":false,\"expiry\":1680602783000,\"value\":\"202204048c21808309a44e07c4404cda\"},{\"isSecure\":false,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"customerBeakerSessionId\",\"isHttpOnly\":true,\"expiry\":1649077590000,\"value\":\"d12a0fd2b374206cdb73251269991755d9987370gAJ9cQAoWBAAAABjdXN0b21lclVzZXJUeXBlcQFLAVgOAAAAX2NyZWF0aW9uX3RpbWVxAkdB2JKwyZgxJ1gJAAAAYXV0aFRva2VucQNYQQAAADU3NjMyOTRmZDcyMDQ2MGQ5YTZiZjI4ZjhiNDA2NmY5LWIyMTEzZDc4MzIzODQ1ZWM4MjVjZTViZmVjYjMwYWE5cQRYAwAAAF9pZHEFWCAAAABlYmVlMWQyYWJiMmM0OGZlYmE0ZTkwNWYxMzg5NTNhNXEGWA4AAABfYWNjZXNzZWRfdGltZXEHR0HYkrDJmDEnWAYAAAB1c2VySWRxCFgYAAAANjI0YWJlMWMwMDAwMDAwMDEwMDA2NjQxcQl1Lg==\"},{\"isSecure\":false,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"galaxy.creator.beaker.session.id\",\"isHttpOnly\":false,\"expiry\":1649671590000,\"value\":\"1649066790430060655355\"},{\"isSecure\":false,\"path\":\"/\",\"domain\":\"creator.xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"BlackBox_report_meta\",\"isHttpOnly\":false,\"value\":\"%7B%22creator-platform%22%3A%222022-04-04%22%7D\"},{\"isSecure\":true,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"customerClientId\",\"isHttpOnly\":true,\"expiry\":1806746790000,\"value\":\"702610886524635\"}]";
            JSONArray jsonArray = JSONUtil.parseArray(ck);
            if (jsonArray.size() > 0) {
                jsonArray.stream().forEach(o -> {
                    Map cookie = JSONUtil.toBean((JSONObject) o, Map.class);
                    Cookie ckl = new Cookie(
                            cookie.get("name").toString()
                            , cookie.get("value").toString()
                            , ObjectUtil.isNull(cookie.get("domain")) ? null : cookie.get("domain").toString()
                            , ObjectUtil.isNull(cookie.get("path")) ? null : cookie.get("path").toString()
                            , ObjectUtil.isNull(cookie.get("expiry")) ? null : DateUtil.date((Long) cookie.get("expiry"))
                            , Boolean.parseBoolean(cookie.get("isSecure").toString())
                            , Boolean.parseBoolean(cookie.get("isHttpOnly").toString())
                            , ObjectUtil.isNull(cookie.get("sameSite")) ? null : cookie.get("sameSite").toString()
                    );
                    webDriver.manage().addCookie(ckl);
                });
            }
            TimeUnit.SECONDS.sleep(2);
            webDriver.navigate().refresh();

        }

        TimeUnit.SECONDS.sleep(3);

        //点击发布视频
        // webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")).click();
        WebElement firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")));
        firstResult.click();


        TimeUnit.SECONDS.sleep(1);
        /*******代理切换完成*****/
        //wirteLog("本地代理切换完成开始上传文件！");
        String filePath = null;
        try {
            filePath = CommUtils.getVideoFile(user).getPath();
        } catch (Exception e) {
            System.out.println(user.getName() + "目录下无视频，将跳过！");
            webDriver.quit();
            return user;
        }
        System.out.println(filePath);
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.id("app")));
        firstResult.click();
        // TimeUnit.SECONDS.sleep(20);
        // webDriver.findElement(By.id("app")).click();
        //webDriver.findElement(By.xpath("//*[@id=\\\"publish-container\\\"]/div/div[3]")).click();
//        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
//                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[3]")));
//        firstResult.click();
        File directory = new File("");
        String alpath = directory.getAbsolutePath();
        Runtime.getRuntime().exec(alpath + "\\utils\\1.exe" + " " + "chrome" + " " + filePath);


        TimeUnit.SECONDS.sleep(2);
        //录入标题
        // webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)));
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")));
        firstResult.sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[0]);

        //录入描述
        webDriver.findElement(By.id("post-textarea")).sendKeys("111");
        TimeUnit.SECONDS.sleep(1);


        // 等待是否上传完成
        new WebDriverWait(webDriver, Duration.ofSeconds(3000))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[6]/div/div/div[2]/div[4]/div")));


        //编辑封面开始
        //弹出编辑框
        //webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")));
        firstResult.click();
        TimeUnit.SECONDS.sleep(1);
        //webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")));
        firstResult.click();
        //修改封面
        wirteLog("开始上传封面！");
        String picFilePath = null;
        try {
            picFilePath = CommUtils.getPicFile(user).getPath();
        } catch (Exception e) {
            System.out.println(user.getName() + "目录下无封面，将跳过！");
            webDriver.quit();
            return user;
        }
        System.out.println(picFilePath);
        TimeUnit.SECONDS.sleep(1);
        //点击封面上传按钮
        webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/input")).sendKeys(picFilePath);
//        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(160))
//                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/div/div[2]")));
//        firstResult.click();

        // Runtime.getRuntime().exec(alpath + "\\utils\\1.exe" + " " + "firefox" + " " + picFilePath);

        //*[@id="cover-modal-0"]/div/div/div[2]/div/div[2]/div[3]/div[2]/input
        TimeUnit.SECONDS.sleep(2);
        //点击确定
        // webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(40))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")));
        firstResult.click();


        //这里注释的逻辑是自动点选封面
        // JavascriptExecutor j = (JavascriptExecutor) webDriver;
        // j.executeScript("document.querySelector(\"#cover-modal-0 > div > div > div.dyn.content.css-t0051x.css-y1z97h > div > div:nth-child(2) > div.css-1v3caum > div > div.list > ul > li.point\").style.left = '100px';");
        //TimeUnit.SECONDS.sleep(1);
        // webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[2]/div/div[2]/ul/li[21]")).click();
        // TimeUnit.MILLISECONDS.sleep(500);
        // webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")).click();
        //TimeUnit.MILLISECONDS.sleep(20000);


//        j.executeScript("window.open('https://www.baidu.com')");
//        ArrayList<String> tab2 = new ArrayList<String>(webDriver.getWindowHandles());
//        webDriver.switchTo().window(tab2.get(1));
//        CommUtils.changeIp(webDriver, ipPort[0], ipPort[1]);
//        webDriver.close();
        TimeUnit.SECONDS.sleep(5);
//        Actions action = new Actions(webDriver);
//        WebElement pngElement = webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")); //获取元素
//        action.moveToElement(pngElement).perform();

        //点击发布
        webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")).click();
        TimeUnit.SECONDS.sleep(2);
        Set<Cookie> cookie1 = webDriver.manage().getCookies();
        CommUtils.updateCkTxt(user, JSONUtil.toJsonStr(cookie1));
        //关闭浏览器
        webDriver.quit();
        CommUtils.replaceTxt(user.getName());

        return null;
    }


    private void wirteLog(String str) {
        System.out.println(DateTime.now() + "====" + str);
    }
}
