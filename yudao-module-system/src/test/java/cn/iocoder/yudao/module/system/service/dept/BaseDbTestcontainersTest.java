package cn.iocoder.yudao.module.system.service.dept;

import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.datasource.config.YudaoDataSourceAutoConfiguration;
import cn.iocoder.yudao.framework.mybatis.config.YudaoMybatisAutoConfiguration;
import cn.iocoder.yudao.framework.test.config.SqlInitializationTestConfiguration;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.github.yulichang.autoconfigure.MybatisPlusJoinAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;

/**
 * 依赖真 MySQL（Testcontainers 容器）的单元测试基类
 *
 * 与 {@link cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest} 的唯一区别：
 * 把 H2 内存数据库换成 Testcontainers 临时拉起的 MySQL 8 容器，其余装配（数据源、MyBatis、建表脚本初始化）完全一致，
 * 方便对比「同一个测试，H2 vs 真 MySQL」的运行结果。
 *
 * 注意：运行前必须保证本机 Docker 已启动。容器在测试类加载时启动，JVM 退出时由 Testcontainers 的 Ryuk 自动销毁。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseDbTestcontainersTest.Application.class)
@ActiveProfiles("unit-test-tc") // 设置使用 application-unit-test-tc 配置文件
@Sql(scripts = "/sql/clean_mysql.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // 每个单元测试结束后，清理 DB
public class BaseDbTestcontainersTest {

    /**
     * MySQL 8 容器。数据库名、账号、密码仅供本机测试使用
     */
    protected static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("ruoyi-vue-pro")
            .withUsername("root")
            .withPassword("123456");

    static {
        MYSQL_CONTAINER.start(); // 类加载时启动容器，保证 @DynamicPropertySource 取到连接信息时容器已就绪
    }

    /**
     * 把容器实际的连接信息（随机映射端口）动态注入 Spring 数据源配置，覆盖 application-unit-test-tc.yaml
     */
    @DynamicPropertySource
    static void configureDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> MYSQL_CONTAINER.getJdbcUrl()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai");
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @Import({
            // DB 配置类（与 BaseDbUnitTest 保持一致）
            YudaoDataSourceAutoConfiguration.class, // 自己的 DB 配置类
            DataSourceAutoConfiguration.class, // Spring DB 自动配置类
            DataSourceTransactionManagerAutoConfiguration.class, // Spring 事务自动配置类
            DruidDataSourceAutoConfigure.class, // Druid 自动配置类
            SqlInitializationTestConfiguration.class, // SQL 初始化
            // MyBatis 配置类
            YudaoMybatisAutoConfiguration.class, // 自己的 MyBatis 配置类
            MybatisPlusAutoConfiguration.class, // MyBatis 的自动配置类
            MybatisPlusJoinAutoConfiguration.class, // MyBatis 的Join配置类

            // 其它配置类
            SpringUtil.class
    })
    public static class Application {
    }

}
