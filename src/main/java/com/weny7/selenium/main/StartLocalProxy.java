/*
 * Description:
 *
 * History：
 * ========================================
 * Date              Version       Memo
 * 2022/3/29 11:33     1.0      Created by liuj
 * ========================================
 *
 * Copyright 2021, 迪爱斯信息技术股份有限公司保留。
 */
package com.weny7.selenium.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.weny7.selenium.main.model.Config;
import com.weny7.selenium.main.model.User;
import com.weny7.selenium.main.type.CommitType;
import com.weny7.selenium.main.utils.CommUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/29 11:33
 */
public class StartLocalProxy {


    public static void main(String[] args) throws InterruptedException {

        String alpath = CommUtils.getAbpath();
        Config cfg = CommUtils.getConfig();
        String configPath = alpath + "\\utils\\u.txt";
        if (args.length > 0 && ObjectUtil.isNotNull(args[0])) {
            if (args[0].equals("mkdir")) {
                List<User> users = CommUtils.getUsers(configPath);
                CommUtils.mkdir(users, cfg.getVideo_path());
                return;
            }
        }

        //配置浏览器驱动地址
        System.setProperty("webdriver.gecko.driver",
                alpath + "\\utils\\geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        //打开Chrome浏览器
        if (!CommUtils.clearWaitTxt()) {
            System.out.println("wait文件被占用无法删除！程序终止！");
            return;
        }
        if (!CommUtils.clearSuccessStr()) {
            System.out.println("success文件被占用无法删除！程序终止！");
            return;
        }
        List<User> users = CommUtils.getUsers(configPath);
        users.stream().forEach(user -> {
            CommUtils.fileAppender(user.getName());
        });

        startBrowser(users, cfg);
    }

    private static void startBrowser(List<User> users, Config cfg) {
        List<User> errusers = new ArrayList<>();
        users.stream().forEach(user -> {
            WebDriver driver = new FirefoxDriver();
//            WebDriver driver = null;
            try {
                System.out.println("开始获取代理ip");
                String[] ipPort = CommUtils.getIp();
                while (!CommUtils.checkIp(ipPort[0], Integer.parseInt(ipPort[1].trim()))) {
                    ipPort = CommUtils.getIp();
                }
                System.out.println("获取代理ip完成");
                System.out.println("开始设置代理ip");
                String alpath = CommUtils.getAbpath();
                Runtime.getRuntime().exec(alpath + "\\utils\\sysproxy.exe global " + ipPort[0] + ":" + ipPort[1] + " localhost;127.*;10.*;172.16.*;172.17.*;172.18.*;172.19.*;172.20.*;172.21.*;172.22.*;172.23.*;172.24.*;172.25.*;172.26.*;172.27.*;172.28.*;172.29.*;172.30.*;172.31.*;192.168.");
                // driver = CommUtils.setFireFoxProxy(ipPort);
                System.out.println("设置全局代理ip完成，当前ip==》" + ipPort[0] + ":" + ipPort[1]);
                driver.manage().deleteAllCookies();
                CommitType commitType = new CommitType();
                User u = commitType.xiaohongshuLocal(driver, user, ipPort);
                if (ObjectUtil.isNotNull(u)) {
                    errusers.add(u);
                }
            } catch (Exception e) {
                errusers.add(user);
                e.printStackTrace();
                System.out.println("出现未捕获的异常！执行下一个账号流程！");
                driver.quit();
            }
        });
        startBrowser(errusers, cfg);
    }

}
