package io.shulie.takin.cloud.biz.cache;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.dao.dict.TDictionaryDataMapper;
import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * 常量字典枚举类
 *
 * @author vernon
 * @date 2019/12/2 16:51
 */
@Component
@Slf4j
public class DictionaryCache {

    @Resource
    private TDictionaryDataMapper tDictionaryDataMapper;

    private Map<String, List<EnumResult>> dicMap = Maps.newHashMap();

    @PostConstruct
    public void initDictionary() {
        //dicMap.put("link_level", LinkLevelEnumMapping.neededEnumResults());
        //dicMap.put("isCore", LinkTypeEnumMapping.neededEnumResults());
        //dicMap.put("isChange", LinkChangeEnumMapping.neededEnumResults());
        //dicMap.put("domain", LinkDomainEnumMapping.neededEnumResults());
        //dicMap.put("changeType", LinkChangeTypeEnumMapping.neededEnumResults());
        //数据字段
        fillDictFromDB();
    }

    private void fillDictFromDB() {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("valueActive", "Y");
        List<TDictionaryVo> voList = tDictionaryDataMapper.queryDictionaryList(paramMap);
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }
        //分组
        Map<String, List<TDictionaryVo>> groupMap = voList.stream().collect(
            Collectors.groupingBy(TDictionaryVo::getTypeAlias));
        //组合数据
        groupMap.forEach((key, value) -> {
            List<EnumResult> resultList = Lists.newArrayList();
            value.forEach(data -> {
                EnumResult result = new EnumResult();
                try {
                    result.setNum(Integer.parseInt(data.getValueOrder()));
                } catch (Exception e) {
                    log.error("异常代码【{}】,异常内容：parse dictionaryData error --> 字典数据转int值异常: {}",
                        TakinCloudExceptionEnum.DICTIONARY_DATA_PARSE_ERROR, e);
                }
                result.setLabel(data.getValueName());
                result.setValue(data.getValueCode());
                result.setDisable("N".equalsIgnoreCase(data.getValueActive()));
                resultList.add(result);
            });
            dicMap.put(key, resultList);
        });
    }

    public Map<String, List<EnumResult>> getDicMap() {
        return this.dicMap;
    }

    public EnumResult getObjectByParamByDefault(String key, Integer valueCode) {
        EnumResult result = getObjectByParam(key, String.valueOf(valueCode));
        return result != null ? result : new EnumResult();
    }

    public EnumResult getObjectByParamByDefault(String key, String valueCode) {
        EnumResult result = getObjectByParam(key, valueCode);
        return result != null ? result : new EnumResult();
    }

    public EnumResult getObjectByParam(String key, Integer valueCode) {
        return getObjectByParam(key, String.valueOf(valueCode));
    }

    public EnumResult getObjectByParam(String key, String valueCode) {
        if (key == null || valueCode == null) {
            return null;
        }
        List<EnumResult> dataList = dicMap.get(key);
        if (CollectionUtils.isEmpty(dataList)) {
            log.warn("can not find key={} in dicCache", key);
            return null;
        }
        EnumResult result = dataList.stream().filter(data -> data.getValue().equals(valueCode)).findFirst().orElse(
            null);
        if (result == null) {
            log.warn("can not find key={}, value={} in dicCache", key, valueCode);
        }
        return result;
    }
}
