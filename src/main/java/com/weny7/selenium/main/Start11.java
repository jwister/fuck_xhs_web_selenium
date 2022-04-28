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

import com.weny7.selenium.main.model.User;
import com.weny7.selenium.main.type.CommitType;
import com.weny7.selenium.main.utils.CommUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.util.List;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/29 11:33
 */
public class Start11 {


    public static void main(String[] args) throws InterruptedException {
        //配置浏览器驱动地址
        System.setProperty("webdriver.gecko.driver",
                "F:\\WorkSpace\\study\\WebSelenium\\utils\\geckodriver.exe");
        //打开Chrome浏览器
        String configPath = "C:\\Users\\Administrator\\Desktop\\u.txt";
        List<User> users = CommUtils.getUsers(configPath);

//        users.stream().forEach(user -> {
//            WebDriver driver = new FirefoxDriver();
//            driver.manage().deleteAllCookies();
//            CommitType commitType = new CommitType();
//            try {
//                commitType.xiaohongshu(driver, user);
//            } catch (InterruptedException | IOException e) {
//                e.printStackTrace();
//            }
//        });

    }

}
