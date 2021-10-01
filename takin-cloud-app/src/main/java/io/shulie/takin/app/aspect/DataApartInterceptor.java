package io.shulie.takin.app.aspect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

import io.shulie.takin.cloud.common.annotation.DataApartInterceptAnnotation;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

/**
 * mybatis 场景报告查询增加客户身份
 *
 * @author qianshui
 * @date 2020/7/22 下午11:22
 */

@Component
@Intercepts({
    @Signature(
        type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class
    })
})
public class DataApartInterceptor implements Interceptor {

    private static final String[] TABLES = new String[] {"t_scene_manage", "t_report"};

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!CloudPluginUtils.checkUserData() || CloudPluginUtils.getContext() == null) {
            return invocation.proceed();
        }
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY,
            SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler
        // ，然后就到BaseStatementHandler的成员变量mappedStatement
        MappedStatement mappedStatement = null;
        try {
            // 与MybatisPlusInterceptor 拦截器 冲突
            mappedStatement = (MappedStatement)metaObject.getValue("delegate.mappedStatement");
        } catch (Exception e) {
            return invocation.proceed();
        }
        if (mappedStatement == null) {
            return invocation.proceed();
        }
        //sql语句类型 select、delete、insert、update
        String sqlCommandType = mappedStatement.getSqlCommandType().toString();
        if (!"select".equals(sqlCommandType.toLowerCase())) {
            return invocation.proceed();
        }
        BoundSql boundSql = statementHandler.getBoundSql();
        //获取到原始sql语句
        String sql = boundSql.getSql();
        String mSql = sql;
        //非指定表，直接跳过
        int tableIndex = matchTableIndex(mSql);
        if (tableIndex < 0) {
            return invocation.proceed();
        }
        //注解逻辑判断  添加注解了才拦截
        String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1);
        //拦截mybatis自动生成的count语句
        if (mName.endsWith("_COUNT")) {
            mSql = setSql(mSql, tableIndex);
        } else {
            Class<?> classType = Class.forName(
                mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf(".")));
            for (Method method : classType.getDeclaredMethods()) {
                if (method.isAnnotationPresent(DataApartInterceptAnnotation.class) && mName.equals(method.getName())) {
                    mSql = setSql(mSql, tableIndex);
                }
            }
        }
        //通过反射修改sql语句
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, mSql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 重置sql
     *
     * @param sql
     * @return
     */
    private String setSql(String sql, int tableIndex) {
        return insertSql(tableIndex, sql, CloudPluginUtils.getTenantId());
    }

    /**
     * 更新sql语句 增加客户id查询
     *
     * @param tableIndex
     * @param sql
     * @param userId
     * @return
     */
    private String insertSql(int tableIndex, String sql, Long userId) {
        StringBuffer sb = new StringBuffer();
        int pos = lowerIndexOf(sql, " where ");

        String filterSql = CloudPluginUtils.getFilterSql();
        if (StringUtils.isNoneBlank(filterSql)) {
            filterSql = "user_id in " + filterSql;
        }
        if (pos > 0) {
            sb.append(sql.substring(0, pos));
            sb.append(" where customer_id = " + userId);
            if (StringUtils.isNoneBlank(filterSql)) {
                sb.append(" and " + filterSql);
            }
            sb.append(" and ");
            sb.append(sql.substring(pos + " where ".length()));
        } else {
            int index = lowerIndexOf(sql, TABLES[tableIndex]);
            sb.append(sql.substring(0, index));
            sb.append(TABLES[tableIndex]);
            sb.append(" where customer_id = " + userId);
            if (StringUtils.isNoneBlank(filterSql)) {
                sb.append(" and " + filterSql);
            }
            sb.append(sql.substring(index + TABLES[tableIndex].length()));
        }
        return sb.toString();
    }

    /**
     * 符合条件的表
     *
     * @param sql
     * @return
     */
    private int matchTableIndex(String sql) {
        for (int i = 0; i < TABLES.length; i++) {
            if (lowerIndexOf(sql, TABLES[i]) > 0
                && lowerIndexOf(sql, TABLES[i] + "_") == -1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * sql转小写再定位
     *
     * @param sql
     * @param str
     * @return
     */
    private int lowerIndexOf(String sql, String str) {
        return sql.toLowerCase().indexOf(str.toLowerCase());
    }
}
