package com.wxl.backend.config;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * 应用层统一填充时间字段，不依赖数据库的 ON UPDATE 行为。
 * 仅对实体上标注了 FieldFill 的字段生效，且字段已有值时不覆盖。
 */
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时总是刷新，覆盖实体里可能已存在的旧值
        setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
    }
}
