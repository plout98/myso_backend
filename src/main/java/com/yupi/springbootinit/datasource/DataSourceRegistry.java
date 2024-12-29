package com.yupi.springbootinit.datasource;

import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/* *
 * 数据源注册器
 *
 * @author plout
 **/
@Component
public class DataSourceRegistry {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    private Map<String, DataSource<T>> typeDataSourceMap;

    @PostConstruct
    public void doInit() {
       typeDataSourceMap = new HashMap(){{
       put(SearchTypeEnum.POST.getValue(), postDataSource);
       put(SearchTypeEnum.USER.getValue(), userDataSource);
       put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
       }};
    }

    public DataSource getDataSourceByType(String type) {
        return typeDataSourceMap.get(type);
    }
}
