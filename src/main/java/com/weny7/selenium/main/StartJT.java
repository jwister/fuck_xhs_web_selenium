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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.weny7.selenium.main.model.Config;
import com.weny7.selenium.main.model.User;
import com.weny7.selenium.main.type.CommitType;
import com.weny7.selenium.main.type.CommitType1;
import com.weny7.selenium.main.utils.CommUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/29 11:33
 */
public class StartJT {


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
        System.out.println(alpath);
        //配置浏览器驱动地址
        System.setProperty("webdriver.chrome.driver",
                alpath + "\\utils\\chromedriver.exe");
        //打开Chrome浏览器



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

        startBrowser(users, null);
    }

    private static void startBrowser(List<User> users, String[] cfg) {
        List<User> errusers = new ArrayList<>();
        users.stream().forEach(user -> {

            ChromeOptions chromeOptions = new ChromeOptions();
//            CommUtils.makeProxyZip(user,);
            chromeOptions.addExtensions(new File("D:\\proxy.zip"));
            WebDriver driver = new ChromeDriver(chromeOptions);

            driver.manage().deleteAllCookies();
            CommitType1 commitType1 = new CommitType1();
            try {
                User u = commitType1.xiaohongshuWithCk(driver, user, null);
                if (ObjectUtil.isNotNull(u)) {
                    errusers.add(u);
                }
            } catch (Exception e) {
                errusers.add(user);
                e.printStackTrace();
            }
        });
        startBrowser(errusers, cfg);
    }


}
