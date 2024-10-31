package com.podigua.kafka.core.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.sqlite.JDBC;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据源实用程序
 *
 * @author podigua
 * @date 2024/03/19
 */
public class DatasourceUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatasourceUtils.class);
    public static HikariDataSource DATASOURCE = init();

    private static JdbcTemplate TEMPLATE = new JdbcTemplate(DATASOURCE);

    private static HikariDataSource init() {
        try {
            Class.forName(JDBC.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(JDBC.class.getName());
        File db = FileUtils.file("kafka-visark.db");
        config.setJdbcUrl("jdbc:sqlite:" + db);
        return new HikariDataSource(config);
    }

    public static HikariDataSource getDatasource() {
        return DATASOURCE;
    }

    public static Connection getConnection() throws SQLException {
        return DATASOURCE.getConnection();
    }

    /**
     * query4 列表
     *
     * @param sql   SQL格式
     * @param clazz 类型
     * @return {@link List}<{@link T}>
     */
    public static <T> List<T> query4List(String sql, Class<T> clazz) {
        logger.info("查询集合:{}", sql);
        return TEMPLATE.query(sql, new BeanPropertyRowMapper<>(clazz));
    }

    /**
     * Query4 对象
     *
     * @param sql   SQL格式
     * @param clazz 克拉兹
     * @return {@link T}
     */
    public static <T> T query4Object(String sql, Class<T> clazz) {
        List<T> result = query4List(sql, clazz);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.getFirst();
    }

    public static void execute(String sql) {
        logger.info("执行sql:{}", sql);
        TEMPLATE.execute(sql);
    }
}
