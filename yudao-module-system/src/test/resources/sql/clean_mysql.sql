-- MySQL 方言版清理脚本：每个测试方法结束后执行（对应 H2 版的 clean.sql，那份用双引号、MySQL 不认）
DELETE FROM `system_dept`;
