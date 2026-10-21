package cn.iocoder.yudao.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，提供文档相关的地址
 *
 * @author 芋道源码
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {
    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾

            // 动态获取端口和 Context-Path
            String port = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "");

            log.info("\n----------------------------------------------------------------------\n\t" + "🚀 哇塞，yudao-server 后端服务启动成功啦！\n\t" + "✨ 祝你今天代码零 Bug，开发顺利，准时下班！\n\t" + "📌 本地服务地址: http://127.0.0.1:" + port + contextPath + "\n\t" + "📖 接口文档地址: http://127.0.0.1:" + port + contextPath + "/doc.html\n\t" + "----------------------------------------------------------------------");
        });
    }

}
