package com.imvector.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author: vector.huang
 * @date: 2019/10/02 03:47
 */
@SpringBootApplication(scanBasePackages = "com.imvector")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        while (true) {
            try {
                var reader = new BufferedReader(new InputStreamReader(System.in));
                var content = reader.readLine();
                var idContent = content.split("-", 2);

                int exec = Integer.parseInt(idContent[0]);
                switch (exec){
                    case 0:
                        //断开重连

                        break;
                    case 1:
                        //发送消息
                        String[] contents = idContent[1].split("-", 2);
                        ChatManager.sendTextMessage(Integer.parseInt(contents[0]), contents[1]);
                        break;
                    default:
                        System.out.println("不支持的操作");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
