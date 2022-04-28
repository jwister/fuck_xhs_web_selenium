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

import com.weny7.selenium.main.type.CommitType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * @author liuj
 * @version 1.0
 * @description: TODO
 * @date 2022/3/29 11:33
 */
public class Start2 {


    public static void main(String[] args) throws InterruptedException {
        //配置浏览器驱动地址
        System.setProperty("webdriver.chrome.driver",
                "F:\\WorkSpace\\study\\WebSelenium\\utils\\chromedriver.exe");
        //打开Chrome浏览器
        WebDriver webDriver = new ChromeDriver();
        CommitType commitType = new CommitType();
        //commitType.xiaohongshu(webDriver);
    }

}
