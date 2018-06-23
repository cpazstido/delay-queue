package com.meipian.redis.test.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableCreator {

    public static String createTableSql(String className) throws ClassNotFoundException {
        Class<?> cl = Class.forName(className);
        DBTable dbTable = cl.getAnnotation(DBTable.class);
        //如果没有表注解，直接返回
        if(dbTable == null) {
            System.out.println(
                    "No DBTable annotations in class " + className);
            return null;
        }
        String tableName = dbTable.name();
        // If the name is empty, use the Class name:
        if(tableName.length() < 1)
            tableName = cl.getName().toUpperCase();
        List<String> columnDefs = new ArrayList<String>();
        //通过Class类API获取到所有成员字段
        for(Field field : cl.getDeclaredFields()) {
            String columnName = null;
            //获取字段上的注解
            Annotation[] anns = field.getDeclaredAnnotations();
            if(anns.length < 1)
                continue; // Not a db table column

            //判断注解类型
            if(anns[0] instanceof SQLInteger) {
                SQLInteger sInt = (SQLInteger) anns[0];
                //获取字段对应列名称，如果没有就是使用字段名称替代
                if(sInt.name().length() < 1)
                    columnName = field.getName().toUpperCase();
                else
                    columnName = sInt.name();
                //构建语句
                columnDefs.add(columnName + " INT" +
                        getConstraints(sInt.constraint()));
            }
            //判断String类型
            if(anns[0] instanceof SQLString) {
                SQLString sString = (SQLString) anns[0];
                // Use field name if name not specified.
                if(sString.name().length() < 1)
                    columnName = field.getName().toUpperCase();
                else
                    columnName = sString.name();
                columnDefs.add(columnName + " VARCHAR(" +
                        sString.value() + ")" +
                        getConstraints(sString.constraint()));
            }


        }
        //数据库表构建语句
        StringBuilder createCommand = new StringBuilder(
                "CREATE TABLE " + tableName + "(");
        for(String columnDef : columnDefs)
            createCommand.append("\n    " + columnDef + ",");

        // Remove trailing comma
        String tableCreate = createCommand.substring(
                0, createCommand.length() - 1) + "\n);";
        return tableCreate;
    }


    /**
     * 判断该字段是否有其他约束
     * @param con
     * @return
     */
    private static String getConstraints(Constraints con) {
        String constraints = "";
        if(!con.allowNull())
            constraints += " NOT NULL";
        if(con.primaryKey())
            constraints += " PRIMARY KEY";
        if(con.unique())
            constraints += " UNIQUE";
        return constraints;
    }

    public static void main(String[] args) throws Exception {
        String[] arg={"com.meipian.redis.test.annotations.Member"};
        for(String className : arg) {
            System.out.println("Table Creation SQL for " +
                    className + " is :\n" + createTableSql(className));
        }
    }
}
