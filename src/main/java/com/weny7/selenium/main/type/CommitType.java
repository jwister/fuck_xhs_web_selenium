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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.weny7.selenium.main.model.Config;
import com.weny7.selenium.main.model.HttpProxy;
import com.weny7.selenium.main.model.User;
import com.weny7.selenium.main.utils.CommUtils;
import com.weny7.selenium.main.utils.PublishThread;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/31 22:42
 */
public class CommitType {


    public User xiaohongshu(WebDriver webDriver, User user, String[] ipPort) throws Exception {
//        wirteLog("开始获取代理ip");
//        String[] ipPort = CommUtils.getIp();
//        while (!CommUtils.checkIp(ipPort[0], Integer.parseInt(ipPort[1].trim()))) {
//            ipPort = CommUtils.getIp();
//        }
        //     wirteLog("获取代理ip完成");
        //      wirteLog("开始设置代理ip");
//        CommUtils.changeIp(webDriver, ipPort[0], ipPort[1]);
//        wirteLog("设置代理ip完成，当前ip==》" + ipPort[0] + ":" + ipPort[1]);
        //       wirteLog("设置代理ip完成.");
        TimeUnit.SECONDS.sleep(3);
        //打登录页面网站
        webDriver.get("https://creator.xiaohongshu.com/login");
        // webDriver.get("https://www.ip138.com/");
        writeln("开始登录！");
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
            writeln("等待验证码。。。。");
            TimeUnit.SECONDS.sleep(1);
        }
        if (c >= 30) {
            writeln(user.getName() + "=>验证码等待超时：跳过当前用户开始下一个用户");
            webDriver.quit();
            return user;
        }
        writeln("取到验证码：" + i);
        //输入验证码
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[1]/div[2]/input")).sendKeys(i.toString());
        TimeUnit.SECONDS.sleep(1);
        //点击登录
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/button")).click();
        TimeUnit.SECONDS.sleep(2);

        if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[3]/div"))) {
            String err = webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[3]/div")).getText();
            if (!err.equals("")) {
                writeln(user.getName() + "登录异常，将跳过！");
                webDriver.quit();
                return user;
            }
        }

        //点击发布视频
        // webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")));
        firstResult.click();
        TimeUnit.SECONDS.sleep(3);
        String alpath = CommUtils.getAbpath();
        //Runtime.getRuntime().exec(alpath + "\\utils\\sysproxy.exe set 1 - - -");

        // wirteLog("开始切换本地代理");
        /*******文件上传之前切换代理*****/
        //切换代理上传文件
//        JavascriptExecutor j = (JavascriptExecutor) webDriver;
//        j.executeScript("window.open('https://www.baidu.com')");
//        ArrayList<String> tabs = new ArrayList<String>(webDriver.getWindowHandles());
//        webDriver.switchTo().window(tabs.get(1));
//        CommUtils.changeIp(webDriver, "127.0.0.1", "10809");
//        webDriver.close();
        //  TimeUnit.SECONDS.sleep(2);
        /*******代理切换完成*****/
        // wirteLog("本地代理切换完成开始上传文件！");
        String filePath = null;
        try {
            filePath = CommUtils.getVideoFile(user).getPath();
        } catch (Exception e) {
            writeln(user.getName() + "目录下无视频，将跳过！");
            webDriver.quit();
            return user;
        }
        writeln(filePath);
        TimeUnit.SECONDS.sleep(3);
        webDriver.findElement(By.id("app")).click();
        //webDriver.findElement(By.xpath("//*[@id=\\\"publish-container\\\"]/div/div[3]")).click();
//        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
//                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[3]")));
//        firstResult.click();

        Runtime.getRuntime().exec(alpath + "\\utils\\1.exe" + " " + "firefox" + " " + filePath);


        TimeUnit.SECONDS.sleep(2);
        //录入标题
        // webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)));
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")));
        firstResult.sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)));

        //录入描述
        // String mx = FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[1];
        String[] topics = user.getTopic().split("#");
        for (int j = 0; j < topics.length; j++) {
            String s = topics[j];
            if (!StrUtil.isEmpty(s)) {
                try {
                    writeln(s);
                    webDriver.findElement(By.id("post-textarea")).sendKeys("#");
//                    webDriver.findElement(By.xpath("//*[@id=\"topicBtn\"]/span")).click();
                    TimeUnit.SECONDS.sleep(1);
                    webDriver.findElement(By.id("post-textarea")).sendKeys(s.trim());
                    TimeUnit.SECONDS.sleep(1);
                    webDriver.findElement(By.id("post-textarea")).sendKeys(Keys.ARROW_LEFT);
                    TimeUnit.SECONDS.sleep(1);
                    webDriver.findElement(By.id("post-textarea")).sendKeys(Keys.ARROW_RIGHT);
                    TimeUnit.SECONDS.sleep(5);
                    webDriver.findElement(By.xpath("//*[@id=\"tributeContainer\"]/div/ul/li[1]")).click();
//                    webDriver.findElement(By.id("post-textarea")).sendKeys(" ");
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    writeln(user.getName() + "话题录入错误，检查文件名称格式");
                    webDriver.quit();
                    return user;
                }
            }

        }
        // webDriver.findElement(By.id("post-textarea")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[1]);
        TimeUnit.SECONDS.sleep(1);

        boolean tags = true;
        Integer type = 0;
        //等待上传中的异常判断
        while (tags) {
            String t = webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/main/div[2]/div/div/div/div/div/div/div[2]/div[2]/div[6]/div/div/div/div[2]/div/div/div")).getText();
            if (!"".equals(t.trim())) {
                tags = false;
                type = 1;
            }
            if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[6]/div/div/div[2]/div[4]/div"))) {
                tags = false;
            }
            TimeUnit.SECONDS.sleep(1);
        }
        //  webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/main/div[2]/div/div/div/div/div/div/div[2]/div[2]/div[6]/div/div/div/div[2]/div/div/div"));
        if (type.equals(1)) {
            writeln(user.getName() + "上传异常，跳过");
            webDriver.quit();
            return user;
        }

        // 等待是否上传完成
//        new WebDriverWait(webDriver, Duration.ofSeconds(1205))
//                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[6]/div/div/div[2]/div[4]/div")));


        //编辑封面开始
        //弹出编辑框
        //webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")).click();
        //封面点击判断
        boolean ispicb = CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button"));
        if (ispicb) {
            webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")).click();
        } else {
            try {
                webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[3]/div/button")).click();
            } catch (Exception e) {
                writeln(user.getName() + "封面点击按钮异常！跳过");
                webDriver.quit();
                return user;
            }
        }

        TimeUnit.SECONDS.sleep(1);
        //webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")));
        firstResult.click();
        //修改封面
        writeln("开始上传封面！");
        String picFilePath = null;
        try {
            picFilePath = CommUtils.getPicFile(user).getPath();
        } catch (Exception e) {
            writeln(user.getName() + "目录下无封面，将跳过！");
            webDriver.quit();
            return user;
        }
        writeln(picFilePath);
        TimeUnit.SECONDS.sleep(1);
        //点击封面上传按钮
        webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/input")).sendKeys(picFilePath);
//        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(160))
//                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/div/div[2]")));
//        firstResult.click();

        // Runtime.getRuntime().exec(alpath + "\\utils\\1.exe" + " " + "firefox" + " " + picFilePath);

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
        TimeUnit.SECONDS.sleep(5);
//        Actions action = new Actions(webDriver);
//        WebElement pngElement = webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")); //获取元素
//        action.moveToElement(pngElement).perform();
        // wirteLog("开始切成代理！点击发布");
        // Runtime.getRuntime().exec(alpath + "\\utils\\sysproxy.exe global " + ipPort[0] + ":" + ipPort[1] + " localhost;127.*;10.*;172.16.*;172.17.*;172.18.*;172.19.*;172.20.*;172.21.*;172.22.*;172.23.*;172.24.*;172.25.*;172.26.*;172.27.*;172.28.*;172.29.*;172.30.*;172.31.*;192.168.");
        // TimeUnit.SECONDS.sleep(2);
        //点击发布
        webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")).click();
        writeln("第1次点击发布");
        TimeUnit.SECONDS.sleep(2);
        // TODO: 2022/4/9 判断发布是否成功。如果成功加入到txt文本。并且获取粉丝数
        Integer t = 0;
        while (!CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"app\"]/div/div[2]/img")) && t < 10) {
            webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")).click();
            writeln("第" + (t + 2) + "次点击发布");
            TimeUnit.SECONDS.sleep(5);
            t++;
        }
        if (t >= 10) {
            writeln(user.getName() + "发布失败！跳过！");
            webDriver.quit();
            return user;
        }
        writeln(user.getName() + "发布完成！");
        writeln("开始获取粉丝数");
        TimeUnit.SECONDS.sleep(1);
        //点击主页
        // webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[2]/div/div[1]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(40))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[2]/div/div[1]")));
        firstResult.click();
        TimeUnit.SECONDS.sleep(1);
        //获取粉丝数

        String fans = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[1]/div[1]/div[2]/p[1]/span[2]/label")).getText();
        writeln(user.getName() + "=>粉丝数量:" + fans);
        //关闭浏览器
        webDriver.quit();
        CommUtils.replaceTxt(user.getName());
        CommUtils.writeSuccessStr(user.getName() + "  粉丝:" + fans);
        return null;
    }

    /**
     * 自动流程化
     */
    public void startBatByCkProxy(boolean push) {
        globalSet();
        String alpath = CommUtils.getAbpath();
        String configPath = alpath + "\\utils\\u.txt";
        Config cfg = CommUtils.getConfig();
        List<User> users = CommUtils.getCkUsers(configPath);
        List<List<User>> lists = ListUtil.splitAvg(users, cfg.getThread_size());
        lists.forEach(usersList -> {
            Thread thread = new PublishThread(usersList,push);
            thread.start();
        });

    }

    /**
     * 手动
     */
    public void startLoginByCk() throws Exception {
        String alpath = CommUtils.getAbpath();
        String configPath = alpath + "\\utils\\u.txt";
        //配置浏览器驱动地址
        System.setProperty("webdriver.chrome.driver",
                alpath + "\\utils\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.manage().deleteAllCookies();
        loginByCkSingle(webDriver);
        String path = CommUtils.getAbpath() + "\\utils\\ck.txt";
        Set<Cookie> cookie = webDriver.manage().getCookies();
        String ct = JSONUtil.toJsonStr(cookie);
        boolean t = FileUtil.del(path);
        if (t) {
            FileWriter writer = new FileWriter(path);
            writer.write(ct);
        }
    }


    /**
     * 接码方式操作流程
     *
     * @param webDriver
     * @param user
     * @param proxy
     * @return
     * @throws Exception
     */
    public User batByCode(WebDriver webDriver, User user, HttpProxy proxy) throws Exception {
        User loginU = loginByCode(webDriver, user, proxy);
        User fansU = getFans(webDriver, user);
        User filledU = filled(webDriver, user, proxy);
        User publishU = publish(webDriver, user);
        //关闭浏览器
        webDriver.quit();
        return null;
    }

    /**
     * ck方式操作流程代理认证方式
     *
     * @return
     * @throws Exception
     */
    public void batByCk(List<User> users, boolean push) {
        List<User> errusers = new ArrayList<>();
        for (User user : users) {

            //设置带认证的代理
            WebDriver webDriver = null;
            try {
                ChromeOptions chromeOptions = new ChromeOptions();
                File proxyFile = CommUtils.makeProxyZip(user, user.getHttpProxy());
                chromeOptions.addExtensions(proxyFile);
                //无ui模式
//                if (!ui) {
//                    chromeOptions.addArguments("--headless");
//                }
                webDriver = new ChromeDriver(chromeOptions);
                webDriver.manage().deleteAllCookies();
                writeln("当前流程用户：" + user.getName() + "如未登录请用对应的账号扫码！");
                User loginU = loginByCk(webDriver, user);
                //登录完成保存ck
                updateCk(webDriver, user);
                if (ObjectUtil.isNotNull(loginU)) {
                    errusers.add(loginU);
                    webDriver.quit();
                    continue;
                }
               if (!push){
                   webDriver.quit();
                   continue;
               }
                //上传视频及编辑内容
                User filledU = filled(webDriver, user, user.getHttpProxy());
                if (ObjectUtil.isNotNull(filledU)) {
                    errusers.add(filledU);
                    webDriver.quit();
                    continue;
                }
                //发布按钮点击
                User publishU = publish(webDriver, user);
                if (ObjectUtil.isNotNull(publishU)) {
                    errusers.add(publishU);
                    webDriver.quit();
                    continue;
                }
                //获取粉丝数
                User fansU = getFans(webDriver, user);

                webDriver.quit();
                //上传完成删除对应视频
                CommUtils.delVideoAndPicFileByName(user, user.getTempFileName());
                System.out.println("视频以及封面已删除！");
            } catch (Exception e) {
                errusers.add(user);
                e.printStackTrace();
                System.out.println("出现未捕获的异常！执行下一个账号流程！");
                webDriver.quit();
            }
        }
        if (errusers.size() > 0) {
            batByCk(errusers, push);
        }

    }

    /**
     * 浏览器启动之前全局设置
     */
    public void globalSet() {
        String alpath = CommUtils.getAbpath();
        String configPath = alpath + "\\utils\\u.txt";
        //配置浏览器驱动地址
        System.setProperty("webdriver.chrome.driver",
                alpath + "\\utils\\chromedriver.exe");
        // System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

        if (!CommUtils.clearWaitTxt()) {
            System.out.println("wait文件被占用无法删除！程序终止！");
            return;
        }
        if (!CommUtils.clearSuccessStr()) {
            System.out.println("success文件被占用无法删除！程序终止！");
            return;
        }
        List<User> users = CommUtils.getCkUsers(configPath);
        users.stream().forEach(user -> {
            CommUtils.fileAppender(user.getName());
        });
    }

    /**
     * 接码登录方法
     *
     * @param webDriver
     * @param user
     * @param proxy
     */
    public User loginByCode(WebDriver webDriver, User user, HttpProxy proxy) throws InterruptedException {
        //打登录页面网站
        webDriver.get("https://creator.xiaohongshu.com/login");
        //webDriver.get("https://www.ip138.com/");
        writeln("开始登录！");
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
            writeln("等待验证码。。。。");
            TimeUnit.SECONDS.sleep(1);
        }
        if (c >= 30) {
            writeln(user.getName() + "=>验证码等待超时：跳过当前用户开始下一个用户");
            webDriver.quit();
            return user;
        }
        writeln("取到验证码：" + i);
        //输入验证码
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[1]/div[2]/input")).sendKeys(i.toString());
        TimeUnit.SECONDS.sleep(1);
        //点击登录
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/button")).click();
        TimeUnit.SECONDS.sleep(2);

        if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[3]/div"))) {
            String err = webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[3]/div")).getText();
            if (!err.equals("")) {
                writeln(user.getName() + "登录异常，将跳过！");
                webDriver.quit();
                return user;
            }
        }
        return null;
    }


    /**
     * Ck登录方法
     *
     * @param webDriver
     * @param user
     */
    public User loginByCk(WebDriver webDriver, User user) throws Exception {
        String ck = CommUtils.getCkTxt(user);
        webDriver.get("https://creator.xiaohongshu.com/login");
        TimeUnit.SECONDS.sleep(5);
        if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"slider-captcha-loading\"]"))) {
            return user;
        }
        if (ck.equals("")) {
            CommUtils.alertJs(webDriver, "大泽神装逼提示：当前用户：" + user.getName());

            while (!CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a"))) {
                TimeUnit.SECONDS.sleep(1);
            }
        } else {
            // ck = CommUtils.decode(ck);
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
        return null;
    }

    public void loginByCkSingle(WebDriver webDriver) throws Exception {
        String ck = CommUtils.getCkTxtNoUser();
        webDriver.get("https://creator.xiaohongshu.com/login");
        TimeUnit.SECONDS.sleep(5);
        if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"slider-captcha-loading\"]"))) {
            webDriver.navigate().refresh();
        }
        if (ck.equals("")) {
            while (!CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a"))) {
                TimeUnit.SECONDS.sleep(1);
            }
        } else {
            //ck = CommUtils.decode(ck);
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
    }

    /**
     * 上传视频编辑内容
     *
     * @param webDriver
     * @param user
     * @param proxy
     * @return
     * @throws Exception
     */
    public User filled(WebDriver webDriver, User user, HttpProxy proxy) throws Exception {
        //点击发布视频
        // webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")).click();
        WebElement firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(260))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")));
        firstResult.click();

        //Runtime.getRuntime().exec(alpath + "\\utils\\sysproxy.exe set 1 - - -");

        // wirteLog("开始切换本地代理");
        /*******文件上传之前切换代理*****/

        /*******代理切换完成*****/
        // wirteLog("本地代理切换完成开始上传文件！");

        writeln("开始检查视频和封面");
        String filePath = null;
        File vfile = null;
        try {
            //随机获取一个视频文件
            vfile = CommUtils.getVideoFile(user);
            filePath = vfile.getPath();
            user.setTempFileName(FileUtil.mainName(vfile));
        } catch (Exception e) {
            writeln(user.getName() + "目录下无视频，将跳过！");
            return user;
        }
        writeln(filePath);

        String picFilePath = null;
        try {
            //picFilePath = CommUtils.getPicFile(user).getPath();
            picFilePath = CommUtils.getPicFileByName(user, FileUtil.mainName(vfile)).getPath();
        } catch (Exception e) {
            writeln(user.getName() + "目录下无封面，将跳过！");
            return user;
        }
        writeln(picFilePath);

        //文件上传
        writeln("准备上传");
        CommUtils.getElement(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/input")).sendKeys(filePath);
        TimeUnit.SECONDS.sleep(2);
        //录入标题
        // webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)));
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(160))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")));
        firstResult.sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)));

        //录入描述
        // String mx = FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[1];
        String[] topics = user.getTopic().split("#");
        for (int j = 0; j < topics.length; j++) {
            String s = topics[j];
            if (!StrUtil.isEmpty(s)) {
                try {
                    writeln(s);
                    CommUtils.getElement(webDriver, By.id("post-textarea")).sendKeys("#");


                    CommUtils.getElement(webDriver, By.id("post-textarea")).sendKeys(s.trim());
                    TimeUnit.SECONDS.sleep(1);
                    CommUtils.getElement(webDriver, By.id("post-textarea")).sendKeys(Keys.ARROW_LEFT);
                    TimeUnit.SECONDS.sleep(1);
                    CommUtils.getElement(webDriver, By.id("post-textarea")).sendKeys(Keys.ARROW_RIGHT);
                    TimeUnit.SECONDS.sleep(5);
                    CommUtils.getElement(webDriver, By.xpath("//*[@id=\"tributeContainer\"]/div/ul/li[1]")).click();
//                    webDriver.findElement(By.id("post-textarea")).sendKeys(" ");
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    writeln(user.getName() + "话题录入错误，检查文件名称格式");
                    return user;
                }
            }

        }
        // webDriver.findElement(By.id("post-textarea")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[1]);
        TimeUnit.SECONDS.sleep(1);

        boolean tags = true;
        Integer type = 0;
        //等待上传中的异常判断
        while (tags) {
            String t = webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/main/div[2]/div/div/div/div/div/div/div[2]/div[2]/div[6]/div/div/div/div[2]/div/div/div")).getText();
            if (!"".equals(t.trim())) {
                tags = false;
                type = 1;
            }
            if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[6]/div/div/div[2]/div[4]/div"))) {
                tags = false;
            }
            TimeUnit.SECONDS.sleep(1);
        }
        //  webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/main/div[2]/div/div/div/div/div/div/div[2]/div[2]/div[6]/div/div/div/div[2]/div/div/div"));
        if (type.equals(1)) {
            writeln(user.getName() + "上传异常，跳过");
            return user;
        }


        //编辑封面开始
        //弹出编辑框
        //webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")).click();
        //封面点击判断
        boolean ispicb = CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button"));
        if (ispicb) {
            webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")).click();
        } else {
            try {
                webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[3]/div/button")).click();
            } catch (Exception e) {
                writeln(user.getName() + "封面点击按钮异常！跳过");
                return user;
            }
        }

        TimeUnit.SECONDS.sleep(1);
        //webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(160))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")));
        firstResult.click();
        //修改封面
        writeln("开始上传封面！");

        TimeUnit.SECONDS.sleep(1);
        //点击封面上传按钮
        CommUtils.getElement(webDriver, By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/input")).sendKeys(picFilePath);

        TimeUnit.SECONDS.sleep(2);
        //点击确定
        // webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(40))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")));
        firstResult.click();
        return null;
    }

    /**
     * 获取粉丝数
     *
     * @param webDriver
     * @param user
     * @return
     */
    public User getFans(WebDriver webDriver, User user) throws Exception {
        writeln("开始获取粉丝数");
        TimeUnit.SECONDS.sleep(1);
        //点击主页
        // webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[2]/div/div[1]")).click();
        WebElement firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(160))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[2]/div/div[1]")));
        firstResult.click();
        //获取粉丝数
        String fans = CommUtils.getElement(webDriver, By.xpath("//*[@id=\"app\"]/div/div[1]/div[1]/div[2]/p[1]/span[2]/label")).getText();
        String hid = CommUtils.getElement(webDriver, By.xpath("//*[@id=\"app\"]/div/div[1]/div[1]/div[2]/p[1]/span[5]")).getText().split("：")[1].trim();
        user.setFans(fans);
        user.setHid(hid);
        writeln(user.getName() + "=>粉丝数量:" + fans);
        CommUtils.writeSuccessStr(user.getName() + "  粉丝:" + fans);
        return null;
    }

    /**
     * 发布点击
     *
     * @param webDriver
     * @param user
     * @return
     * @throws Exception
     */
    public User publish(WebDriver webDriver, User user) throws Exception {
        TimeUnit.SECONDS.sleep(5);

        //点击发布
        CommUtils.getElement(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")).click();
        writeln("第1次点击发布");
        TimeUnit.SECONDS.sleep(2);
        // TODO: 2022/4/9 判断发布是否成功。如果成功加入到txt文本。并且获取粉丝数
        Integer t = 0;
        while (!CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"app\"]/div/div[2]/img")) && t < 10) {
            CommUtils.getElement(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")).click();
            writeln("第" + (t + 2) + "次点击发布");
            TimeUnit.SECONDS.sleep(5);
            t++;
        }
        if (t >= 10) {
            writeln(user.getName() + "发布失败！跳过！");
            return user;
        }
        writeln(user.getName() + "发布完成！");
        CommUtils.replaceTxt(user.getName());
        return null;
    }

    /**
     * 保存更新ck
     *
     * @param webDriver
     * @param user
     */
    public void updateCk(WebDriver webDriver, User user) {
        Set<Cookie> cookie = webDriver.manage().getCookies();
        CommUtils.updateCkTxt(user, JSONUtil.toJsonStr(cookie));
    }

    public User xiaohongshuLocal(WebDriver webDriver, User user, String[] ipPort) throws Exception {

        TimeUnit.SECONDS.sleep(3);
        //打登录页面网站
        webDriver.get("https://creator.xiaohongshu.com/login");
        // webDriver.get("https://www.ip138.com/");
        writeln("开始登录！");
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
            writeln("等待验证码。。。。");
            TimeUnit.SECONDS.sleep(1);
        }
        if (c >= 30) {
            writeln(user.getName() + "=>验证码等待超时：跳过当前用户开始下一个用户");
            webDriver.quit();
            return user;
        }
        writeln("取到验证码：" + i);
        //输入验证码
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[1]/div[2]/input")).sendKeys(i.toString());
        TimeUnit.SECONDS.sleep(1);
        //点击登录
        webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/button")).click();
        TimeUnit.SECONDS.sleep(2);

        if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[3]/div"))) {
            String err = webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/div[2]/div[1]/div[2]/div/div/div/div/div[1]/div[2]/div[3]/div")).getText();
            if (!err.equals("")) {
                writeln(user.getName() + "登录异常，将跳过！");
                webDriver.quit();
                return user;
            }
        }

        //点击发布视频
        // webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a")));
        firstResult.click();
        TimeUnit.SECONDS.sleep(3);
        /*******文件上传之前切换代理*****/
        writeln("开始切换本地代理");
        String alpath = CommUtils.getAbpath();
        Runtime.getRuntime().exec(alpath + "\\utils\\sysproxy.exe set 1 - - -");
        writeln("本地代理切换完成，等待10秒！");
        TimeUnit.SECONDS.sleep(10);

        /*******代理切换完成*****/
        writeln("开始上传文件！");
        String filePath = null;
        try {
            filePath = CommUtils.getVideoFile(user).getPath();
        } catch (Exception e) {
            writeln(user.getName() + "目录下无视频，将跳过！");
            webDriver.quit();
            return user;
        }
        writeln(filePath);
        TimeUnit.SECONDS.sleep(3);
        webDriver.findElement(By.id("app")).click();
        //webDriver.findElement(By.xpath("//*[@id=\\\"publish-container\\\"]/div/div[3]")).click();
//        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
//                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[3]")));
//        firstResult.click();

        Runtime.getRuntime().exec(alpath + "\\utils\\1.exe" + " " + "firefox" + " " + filePath);


        TimeUnit.SECONDS.sleep(2);
        //录入标题
        // webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)));
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[3]/input")));
        firstResult.sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)));

        //录入描述
        // String mx = FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[1];
        String[] topics = user.getTopic().split("#");
        for (int j = 0; j < topics.length; j++) {
            String s = topics[j];
            if (!StrUtil.isEmpty(s)) {
                try {
                    writeln(s);
                    webDriver.findElement(By.id("post-textarea")).sendKeys("#");
//                    webDriver.findElement(By.xpath("//*[@id=\"topicBtn\"]/span")).click();
                    TimeUnit.SECONDS.sleep(1);
                    webDriver.findElement(By.id("post-textarea")).sendKeys(s.trim());
                    TimeUnit.SECONDS.sleep(1);
                    webDriver.findElement(By.id("post-textarea")).sendKeys(Keys.ARROW_LEFT);
                    TimeUnit.SECONDS.sleep(1);
                    webDriver.findElement(By.id("post-textarea")).sendKeys(Keys.ARROW_RIGHT);
                    TimeUnit.SECONDS.sleep(5);
                    webDriver.findElement(By.xpath("//*[@id=\"tributeContainer\"]/div/ul/li[1]")).click();
//                    webDriver.findElement(By.id("post-textarea")).sendKeys(" ");
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    writeln(user.getName() + "话题录入错误，检查文件名称格式");
                    webDriver.quit();
                    return user;
                }
            }

        }
        // webDriver.findElement(By.id("post-textarea")).sendKeys(FileNameUtil.mainName(CommUtils.getVideoFile(user)).split("----")[1]);
        TimeUnit.SECONDS.sleep(1);

        boolean tags = true;
        Integer type = 0;
        //等待上传中的异常判断
        while (tags) {
            String t = webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/main/div[2]/div/div/div/div/div/div/div[2]/div[2]/div[6]/div/div/div/div[2]/div/div/div")).getText();
            if (!"".equals(t.trim())) {
                tags = false;
                type = 1;
            }
            if (CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[6]/div/div/div[2]/div[4]/div"))) {
                tags = false;
            }
            TimeUnit.SECONDS.sleep(1);
        }
        //  webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/main/div[2]/div/div/div/div/div/div/div[2]/div[2]/div[6]/div/div/div/div[2]/div/div/div"));
        if (type.equals(1)) {
            writeln(user.getName() + "上传异常，跳过");
            webDriver.quit();
            return user;
        }

        // 等待是否上传完成
//        new WebDriverWait(webDriver, Duration.ofSeconds(1205))
//                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[6]/div/div/div[2]/div[4]/div")));


        //编辑封面开始
        //弹出编辑框
        //webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")).click();
        //封面点击判断
        boolean ispicb = CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button"));
        if (ispicb) {
            webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[4]/div/button")).click();
        } else {
            try {
                webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[2]/div[3]/div/button")).click();
            } catch (Exception e) {
                writeln(user.getName() + "封面点击按钮异常！跳过");
                webDriver.quit();
                return user;
            }
        }

        TimeUnit.SECONDS.sleep(1);
        //webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(60))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[1]/div[2]")));
        firstResult.click();
        //修改封面
        writeln("开始上传封面！");
        String picFilePath = null;
        try {
            picFilePath = CommUtils.getPicFile(user).getPath();
        } catch (Exception e) {
            writeln(user.getName() + "目录下无封面，将跳过！");
            webDriver.quit();
            return user;
        }
        writeln(picFilePath);
        TimeUnit.SECONDS.sleep(1);
        //点击封面上传按钮
        webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/input")).sendKeys(picFilePath);
//        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(160))
//                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[2]/div/div[2]/div[3]/div[2]/div/div[2]")));
//        firstResult.click();

        // Runtime.getRuntime().exec(alpath + "\\utils\\1.exe" + " " + "firefox" + " " + picFilePath);

        TimeUnit.SECONDS.sleep(2);
        //点击确定
        // webDriver.findElement(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(40))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"cover-modal-0\"]/div/div/div[3]/div/button[2]")));
        firstResult.click();

        /****发布之前要切回之前的代理****/
        writeln("上传封面后等待5秒！");
        TimeUnit.SECONDS.sleep(5);
//        Actions action = new Actions(webDriver);
//        WebElement pngElement = webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")); //获取元素
//        action.moveToElement(pngElement).perform();
        writeln("开始切成动态代理！点击发布");
        Runtime.getRuntime().exec(alpath + "\\utils\\sysproxy.exe global " + ipPort[0] + ":" + ipPort[1] + " localhost;127.*;10.*;172.16.*;172.17.*;172.18.*;172.19.*;172.20.*;172.21.*;172.22.*;172.23.*;172.24.*;172.25.*;172.26.*;172.27.*;172.28.*;172.29.*;172.30.*;172.31.*;192.168.");
        writeln("切换完成等待10秒！");
        TimeUnit.SECONDS.sleep(10);
        //点击发布
        webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")).click();
        writeln("第1次点击发布");
        TimeUnit.SECONDS.sleep(2);
        // TODO: 2022/4/9 判断发布是否成功。如果成功加入到txt文本。并且获取粉丝数
        Integer t = 0;
        while (!CommUtils.checkElementExists(webDriver, By.xpath("//*[@id=\"app\"]/div/div[2]/img")) && t < 10) {
            webDriver.findElement(By.xpath("//*[@id=\"publish-container\"]/div/div[2]/div[2]/div[9]/button[1]")).click();
            writeln("第" + (t + 2) + "次点击发布");
            TimeUnit.SECONDS.sleep(5);
            t++;
        }
        if (t >= 10) {
            writeln(user.getName() + "发布失败！跳过！");
            webDriver.quit();
            return user;
        }
        writeln(user.getName() + "发布完成！");
        writeln("开始获取粉丝数");
        TimeUnit.SECONDS.sleep(1);
        //点击主页
        // webDriver.findElement(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[2]/div/div[1]")).click();
        firstResult = new WebDriverWait(webDriver, Duration.ofSeconds(40))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[2]/div/div[1]")));
        firstResult.click();
        TimeUnit.SECONDS.sleep(1);
        //获取粉丝数

        String fans = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[1]/div[1]/div[2]/p[1]/span[2]/label")).getText();
        writeln(user.getName() + "=>粉丝数量:" + fans);
        //关闭浏览器
        webDriver.quit();
        CommUtils.replaceTxt(user.getName());
        CommUtils.writeSuccessStr(user.getName() + "  粉丝:" + fans);
        return null;
    }


    private void writeln(String str) {
        System.out.println(DateTime.now() + "====" + str);
    }
}
