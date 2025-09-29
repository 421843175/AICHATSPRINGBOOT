-- -查看是否开始了事件
SHOW VARIABLES LIKE 'event_scheduler';


-- -启用事件调度器
SET GLOBAL event_scheduler = ON;



-- -添加切换时间的列
ALTER TABLE friendships
    ADD COLUMN robot_switch_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '状态切换时间';


desc friendships



-- - 创建时间任务  robot_switch_time 过60分钟 会自己设置回0
DELIMITER $$

CREATE EVENT auto_reset_robot_status
ON SCHEDULE EVERY 1 MINUTE
STARTS CURRENT_TIMESTAMP
DO
BEGIN
UPDATE friendship
SET is_robot = 1
WHERE is_robot = 0
  AND robot_switch_time <= NOW() - INTERVAL 60 MINUTE;
END
$$

DELIMITER ;


-- -创建触发器 当is_robot是0的时候自动更新
DELIMITER $$

CREATE TRIGGER update_robot_time
    BEFORE UPDATE ON friendships
    FOR EACH ROW
BEGIN
    -- 当 is_robot 从其他值变为 0 时更新
    IF NEW.is_robot = 0 AND OLD.is_robot != NEW.is_robot THEN
        SET NEW.robot_switch_time = NOW();
END IF;
END
$$

DELIMITER ;

-- - 测试触发器和定时任务
UPDATE friendships
SET is_robot = 0
WHERE id = 3;