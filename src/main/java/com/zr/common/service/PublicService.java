package com.zr.common.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zr.common.entity.Inter;
import com.zr.common.entity.PageResult;
import com.zr.common.mapper.InterMapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicService {
    @Autowired
    private InterMapper interMapper;
    // 日志组件
    private Logger LOG = LoggerFactory.getLogger(PublicService.class);
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public PageResult query(Map<String, Object> resultMap) {
        PageResult pageResult = new PageResult();
        long start = System.currentTimeMillis();
        try {
            List<Inter> list = new ArrayList();
            Map<String, Object> map = new HashMap<>();
            String serviceId = resultMap.get("serviceId") + "";
            //1、获取对应接口的配置信息
            Map<String, Object> sqlMap = new HashMap<String, Object>();
            sqlMap.put("sql", "select * from pubService where serviceId='" + serviceId + "' limit 1");//通用接口表中获取基础的配置信息
            List<Map<String, Object>> pubList = interMapper.query(sqlMap);
            if (pubList == null || pubList.size() == 0) {
                pageResult.setStatus(2);
                pageResult.setMsg("没有此接口");
                pageResult.setTime(System.currentTimeMillis() - start);
                return pageResult;
            }
            Map<String, Object> pubMap = pubList.get(0);
            //2、查询数据
            String source = pubMap.get("source") + "";
            if ("es".equals(source)) {
                System.out.println("es查询");
            } else if ("impala".equals(source)) {
                System.out.println("impala查询");
            } else if ("mysql".equals(source)) {
                pageResult = queryMysql(resultMap, pubMap);
            }
        } catch (Exception e) {
            LOG.error("查询失败,检查配置的接口信息：", e);
            pageResult.setStatus(2);
            pageResult.setMsg("查询失败,检查配置的接口信息");
        }
        pageResult.setTime(System.currentTimeMillis() - start);
        return pageResult;
    }

    /**
     * 查询Mysql
     * 1、组装sql
     * 2、执行sql
     * 3、翻译字典
     *
     * @param resultMap
     */
    public PageResult queryMysql(Map<String, Object> resultMap, Map<String, Object> pubMap) throws Exception {
        PageResult pageResult = new PageResult();
        //1、组装sql
        String sql = createSql(resultMap, pubMap);
        int start = 0;
        int limit = 0;
        try {
            String lmt = (resultMap.get("limit") == null ? "" : resultMap.get("limit").toString());
            if (StringUtils.isNotBlank(lmt)) {
                limit = Integer.valueOf(lmt);
            }
            String stt = (resultMap.get("start") == null ? "" : resultMap.get("start").toString());
            if (StringUtils.isNotBlank(stt)) {
                start = Integer.valueOf(stt);
            }
        } catch (Exception e) {
            pageResult.setMsg("limit或start参数值不对");
            pageResult.setStatus(2);
            return pageResult;
        }
        LOG.info("queryMysql:" + sql);
        System.out.println("*******************************************************");
        System.out.println(sql);
        System.out.println("*******************************************************");
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        //2、执行sql
        String isCount = (pubMap.get("isCount") == null ? "" : pubMap.get("isCount").toString());//是1否0需要count:一般分页时需要
        if ("1".equals(isCount) || "true".equals(isCount)) {
            int leng = sql.indexOf("order by") != -1 ? sql.indexOf("order by") : sql.length();
            leng = sql.indexOf("group by") != -1 ? sql.indexOf("group by") : leng;
            sqlMap.put("sql", "select count(0) as total " + sql.substring(sql.indexOf("from"), leng));
            resultList = interMapper.query(sqlMap);
            if (resultList == null || resultList.size() == 0 || "0".equals(resultList.get(0).get("total"))) {
                map.put("data", resultList);
                map.put("total", 0);
                pageResult.setBody(map);
                pageResult.setStatus(1);
                return pageResult;
            }
            map.put("total", resultList.get(0).get("total"));
        }
        if (limit > 0) {
            sql += " limit " + start + "," + limit;
        } else {//防止内存溢出
            sql += " limit 0,50000";
        }
        sqlMap.put("sql", sql);
        resultList = interMapper.query(sqlMap);
        if (resultList == null || resultList.size() == 0) {
            map.put("data", resultList);
            map.put("total", 0);
            pageResult.setBody(map);
            pageResult.setStatus(1);
            return pageResult;
        }

        //3、翻译字典
        String translate = (pubMap.get("translate") == null ? "" : pubMap.get("translate").toString());//翻译	translate	否
        Map<String, Object> mpTslt = new HashMap<String, Object>();//翻译字典
        for (Map<String, Object> mp : resultList) {
            try {
                if (StringUtils.isBlank(translate)) {
                    continue;
                }
                translate(translate, mpTslt, mp);
            } catch (Exception e) {
                LOG.error("翻译错误：" + mp.toString(), e);
            }
        }
        mpTslt.clear();
        mpTslt = null;
        map.put("data", resultList);
        pageResult.setBody(map);
        pageResult.setStatus(1);
        return pageResult;
    }

    /**
     * 组装查询sql
     *
     * @return
     */
    private String createSql(Map<String, Object> resultMap, Map<String, Object> pubMap) throws Exception {
        String source = pubMap.get("source") + "";
        String sql = pubMap.get("sql") + "";//select [clns] from 表名 [where] [groupBy] [orderBy]
        String orderBy = (pubMap.get("orderBy") == null ? "" : pubMap.get("orderBy").toString());//多字段逗号隔开（参数中有传入则以参数为准）
        String order = (pubMap.get("order") == null ? "" : pubMap.get("order").toString());//desc/asc，多字段逗号隔开
        String orderBySql = "";
        String groupBySql = "";
        String clns = pubMap.get("clns") + "";//多字段逗号隔开.例：name,sex,age,type
		/*格式：[{“cln”:”字段名”,”logic”:”逻辑符”,”clnType”:”字段类型”,”isNull”:”必填”,”userCln”:”user字段”,
		 * ”requestClnType”:”参数字段类型”,”alias”:”表别名”}]
		user字段：需要将登录用户中的属性映射到这个字段进行查询的，默认为空（如数据权限需要根据用户的省、市、区、派出所、厂商）
		例： name-like-string-0-,sex-eq-int-0-,age-range-int-0-*/
        String where = (pubMap.get("where") == null ? "" : pubMap.get("where").toString());//
        String whereSql = "";
        if (where != null) {
            Type type = new TypeToken<List<Map<String, String>>>() {
            }.getType();
            List<Map<String, String>> list = gson.fromJson(where, type);
            for (Map<String, String> map : list) {
                String alias = (map.get("alias") == null ? "" : map.get("alias"));
                String cln = (map.get("cln") == null ? "" : map.get("cln"));
                String logic = (map.get("logic") == null ? "" : map.get("logic"));
                String clnType = (map.get("clnType") == null ? "" : map.get("clnType"));
                String requestClnType = (map.get("requestClnType") == null ? "" : map.get("requestClnType"));
                String v = (resultMap.get(cln) == null ? "" : resultMap.get(cln).toString());
                if (StringUtils.isNotBlank(v)) {//字段-逻辑符-字段类型-必填-user字段
                    whereSql += " and " + getWhere(source, alias + cln, logic, clnType, v, requestClnType);
                }
            }
        }
        if (StringUtils.isNotBlank(whereSql)) {
            whereSql = whereSql.replaceFirst("and", "where");
        }
        if (StringUtils.isNotBlank(orderBy)) {
            String[] obs = orderBy.split(",", -1);
            String[] os = order.split(",", -1);
            orderBySql += " order by ";
            if (obs.length != os.length) {
                orderBySql += orderBy + " " + order;
            } else {
                for (int i = 0; i < obs.length; i++) {
                    if (i > 0) {
                        orderBySql += ",";
                    }
                    orderBySql += obs[i] + " " + os[i];
                }
            }
        }
        sql = sql.replace("[clns]", clns).replace("[groupBy]", groupBySql).replace("[orderBy]", orderBySql).replace("[where]", whereSql);
        return sql;
    }

    /**
     * 转换where条件
     *
     * @param source：来源
     * @param logic：逻辑符 like（模糊）、likeStart（前模糊）、likeEnd(后模糊)、eq（等于）、lt（小于）、
     *                  gt(大于)、le（小于等于）、ge（大于等于）、range（范围）
     *                  range格式: 起始值$$结束值
     *                  字段类型 string(字符串)、int(整数)、float(带小数)、datetime(yyyy-mm-dd hh:ii:ss)、date(yyyy-mm-dd)、timestamp(时间秒)
     * @param v：字段值
     * @return
     */
    private String getWhere(String source, String name, String logic, String clnType, String v, String requestClnType) {
        String like = "es".equals(source) ? "*" : "%";
        switch (logic) {
            case "like":
                return name + " like '" + like + v + like + "'";
            case "likeStart":
                return name + " like '" + like + v + "'";//前模糊
            case "likeEnd":
                return name + " like '" + v + like + "'";//后模糊
            case "eq"://等于
                if ("int".equals(clnType) || "float".equals(clnType) || "time".equals(clnType) || "timestamp".equals(clnType)) {
                    return name + " = " + v + "";
                } else {
                    return name + " = '" + v + "'";
                }
            case "lt"://小于
                if ("int".equals(clnType) || "float".equals(clnType) || "time".equals(clnType) || "timestamp".equals(clnType)) {
                    return name + " < " + v + "";
                } else {
                    return name + " < '" + v + "'";
                }
            case "gt"://大于
                if ("int".equals(clnType) || "float".equals(clnType) || "time".equals(clnType) || "timestamp".equals(clnType)) {
                    return name + " > " + v + "";
                } else {
                    return name + " > '" + v + "'";
                }
            case "le"://小于等于
                if ("int".equals(clnType) || "float".equals(clnType) || "time".equals(clnType) || "timestamp".equals(clnType)) {
                    return name + " <= " + v + "";
                } else {
                    return name + " <= '" + v + "'";
                }
            case "ge"://大于等于
                if ("int".equals(clnType) || "float".equals(clnType) || "time".equals(clnType) || "timestamp".equals(clnType)) {
                    return name + " >= " + v + "";
                } else {
                    return name + " >= '" + v + "'";
                }
        }
        return null;
    }

    /**
     * 翻译字典值
     *
     * @param translate:需要翻译的内容
     * @param mpTslt：翻译过的存到缓存中下一次直接使用
     * @param mp：某条记录
     * @throws Exception
     */
    @SuppressWarnings({"unchecked"})
    private void translate(String translate, Map<String, Object> mpTslt, Map<String, Object> mp) throws Exception {
        //格式：[{“source”:”来源”,”sql”:”mysql对应sql”,”redisKey”:”redisKey”,”hashKey”:”redis的hashKey”,
        //”cln”:”字段”,”clnFromType”:字段类型”,”clnToType”:”字段转换类型”}] //来源：redis、mysql、dateto
        Type typee = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> list = gson.fromJson(translate, typee);
        for (Map<String, String> map : list) {
            String source = (map.get("source") == null ? "" : map.get("source"));
            if (StringUtils.isBlank(source)) {
                continue;
            }
            //dateto：{“source”:”dateto”,”cln”:”字段”,”clnFromType”:字段类型”,”clnToType”:”字段转换类型”}
            //类型:timestamp(时间秒)、time(毫秒)、datetime(yyyy-mm-dd hh:ii:ss)、date(yyyy-mm-dd)
            if ("dateto".equals(source)) {
                String cln = (map.get("cln") == null ? "" : map.get("cln"));
                String clnFromType = (map.get("clnFromType") == null ? "" : map.get("clnFromType"));
                String clnToType = (map.get("clnToType") == null ? "" : map.get("clnToType"));
                String v = (mp.get(cln) == null ? "" : mp.get(cln).toString());
                long lg = 0;
                if ("timestamp".equals(clnFromType)) {
                    lg = Long.valueOf(v) * 1000;
                } else if ("time".equals(clnFromType)) {
                    lg = Long.valueOf(v);
                }
                if ("timestamp".equals(clnToType)) {
                    mp.put(cln + "Str", lg / 1000);
                } else if ("time".equals(clnToType)) {
                    mp.put(cln + "Str", lg);
                }
            } else if ("mysql".equals(source)) {
                //格式：{“source”:”mysql”,”sql”:”select name as typeName,xpoint,ypoint from typeTable where code=[type] and value=[age]”}
                //Mysql：=== 单值返回对应字典sql： select name as typeName from typeTable
                //多值返回对应字典sql： select name as typeName,xpoint,ypoint from typeTable
                //单条件：where code=[type] // 多条件：where code=[type] and value=[age]
                String sql = (map.get("sql") == null ? "" : map.get("sql"));
                String[] tps = sql.split("\\[", -1);
                String key = "";
                for (int i = 1; i < tps.length; i++) {
                    String str = tps[i];
                    String s = str.substring(0, str.indexOf("]"));
                    sql = sql.replace("[" + s + "]", "'" + mp.get(s) + "'");
                    key += s + "-" + mp.get(s) + ",";
                }
                Map<String, Object> pMap = ((Map<String, Object>) mpTslt.get(key));
                if (pMap == null) {
                    Map<String, Object> sqlMap1 = new HashMap<String, Object>();
                    sqlMap1.put("sql", sql + " limit 1");
                    List<Map<String, Object>> pList = interMapper.query(sqlMap1);
                    if (pList != null && pList.size() > 0) {
                        pMap = pList.get(0);
                    } else {
                        pMap = new HashMap<String, Object>();
                    }
                    mpTslt.put(key, pMap);
                }
                for (String keys : pMap.keySet()) {
                    mp.put(keys, pMap.get(keys));
                }
            }
        }
    }


}
