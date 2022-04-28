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

import cn.hutool.core.util.ObjectUtil;
import com.weny7.selenium.main.model.Config;
import com.weny7.selenium.main.model.User;
import com.weny7.selenium.main.type.CommitType;
import com.weny7.selenium.main.utils.CommUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/29 11:33
 */
public class StartNoProxy {


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
        System.setProperty("webdriver.gecko.driver",
                alpath + "\\utils\\geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        //打开Chrome浏览器

        List<User> users = CommUtils.getUsers(configPath);
        startBrowser(users, null);
    }

    private static void startBrowser(List<User> users, String[] cfg) {
        List<User> errusers = new ArrayList<>();
        users.stream().forEach(user -> {
            WebDriver driver = new FirefoxDriver();
            driver.manage().deleteAllCookies();
            CommitType commitType = new CommitType();
            try {
                User u = commitType.xiaohongshu(driver, user, cfg);
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
