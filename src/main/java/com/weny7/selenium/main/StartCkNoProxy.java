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
import com.weny7.selenium.main.utils.CommUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

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
public class StartCkNoProxy {


    public static void main(String[] args) throws InterruptedException {

        String alpath = CommUtils.getAbpath();
        Config cfg = CommUtils.getConfig();
        String configPath = alpath + "\\utils\\u.txt";

        //配置浏览器驱动地址
        System.setProperty("webdriver.chrome.driver",
                alpath + "\\utils\\chromedriver.exe");
        //打开Chrome浏览器

        List<User> users = CommUtils.getUsers(configPath);
        startBrowser(users, cfg);
    }

    private static void startBrowser(List<User> users, Config cfg) throws InterruptedException {
        WebDriver driver = new FirefoxDriver();
        driver.manage().deleteAllCookies();
        driver.get("https://creator.xiaohongshu.com/login");
        String ck = "[{\"isSecure\":false,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"timestamp2.sig\",\"isHttpOnly\":false,\"expiry\":1680602783000,\"value\":\"vxDvZtIgkLDuw8w5BPsOPi-BUYJs_r_mzdvhwSP_wnM\"},{\"isSecure\":false,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"timestamp2\",\"isHttpOnly\":false,\"expiry\":1680602783000,\"value\":\"202204048c21808309a44e07c4404cda\"},{\"isSecure\":false,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"customerBeakerSessionId\",\"isHttpOnly\":true,\"expiry\":1649077590000,\"value\":\"d12a0fd2b374206cdb73251269991755d9987370gAJ9cQAoWBAAAABjdXN0b21lclVzZXJUeXBlcQFLAVgOAAAAX2NyZWF0aW9uX3RpbWVxAkdB2JKwyZgxJ1gJAAAAYXV0aFRva2VucQNYQQAAADU3NjMyOTRmZDcyMDQ2MGQ5YTZiZjI4ZjhiNDA2NmY5LWIyMTEzZDc4MzIzODQ1ZWM4MjVjZTViZmVjYjMwYWE5cQRYAwAAAF9pZHEFWCAAAABlYmVlMWQyYWJiMmM0OGZlYmE0ZTkwNWYxMzg5NTNhNXEGWA4AAABfYWNjZXNzZWRfdGltZXEHR0HYkrDJmDEnWAYAAAB1c2VySWRxCFgYAAAANjI0YWJlMWMwMDAwMDAwMDEwMDA2NjQxcQl1Lg==\"},{\"isSecure\":false,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"galaxy.creator.beaker.session.id\",\"isHttpOnly\":false,\"expiry\":1649671590000,\"value\":\"1649066790430060655355\"},{\"isSecure\":false,\"path\":\"/\",\"domain\":\"creator.xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"BlackBox_report_meta\",\"isHttpOnly\":false,\"value\":\"%7B%22creator-platform%22%3A%222022-04-04%22%7D\"},{\"isSecure\":true,\"path\":\"/\",\"domain\":\".xiaohongshu.com\",\"sameSite\":\"None\",\"name\":\"customerClientId\",\"isHttpOnly\":true,\"expiry\":1806746790000,\"value\":\"702610886524635\"}]";
        JSONArray jsonArray = JSONUtil.parseArray(ck);
        if (jsonArray.size() > 0) {
            jsonArray.stream().forEach(o -> {
                Map cookie = JSONUtil.toBean((JSONObject) o, Map.class);
                Cookie ckl = new Cookie(
                        cookie.get("name").toString()
                        , cookie.get("value").toString()
                        , cookie.get("domain").toString()
                        , cookie.get("path").toString()
                        , ObjectUtil.isNull(cookie.get("expiry")) ? null : DateUtil.date((Long) cookie.get("expiry"))
                        , Boolean.parseBoolean(cookie.get("isSecure").toString())
                        , Boolean.parseBoolean(cookie.get("isHttpOnly").toString())
                        , cookie.get("sameSite").toString()
                );

                driver.manage().addCookie(ckl);
            });
        }


        driver.navigate().refresh();


        while (!CommUtils.checkElementExists(driver, By.xpath("//*[@id=\"page\"]/div/main/div[1]/div/div[1]/a"))) {
            TimeUnit.SECONDS.sleep(1);
        }

        Set<Cookie> cookie1 = driver.manage().getCookies();
        System.out.println(cookie1);

    }


}
