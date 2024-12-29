package com.yupi.springbootinit.job.once;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.esdao.PostEsDao;
import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FethInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Override
    public void run(String... args) {
//        获取数据
        String json = "{\"pageSize\":12,\"sortOrder\":\"descend\",\"sortField\":\"_score\",\"tags\":[],\"searchText\":\"\",\"current\":1,\"reviewStatus\":1,\"hiddenContent\":true,\"type\":\"passage\"}";
        String url = "https://api.codefather.cn/api/search/";
        String result = HttpRequest.post(url)
                .body(json)
                .execute().body();
//        转换Json
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        System.out.println(map);
        JSONObject data = (JSONObject) map.get("data");
        JSONObject searchPage = (JSONObject) data.get("searchPage");
        JSONArray records = (JSONArray) searchPage.get("records");
        List<Post> list = new ArrayList<>();
        for (Object record : records) {
            JSONObject jsonObject = (JSONObject) record;
            Post post = new Post();
            post.setTitle(jsonObject.getStr("title"));
            post.setContent(jsonObject.getStr("plainTextDescription"));
            JSONArray tags = (JSONArray) jsonObject.get("tags");
            post.setTags(JSONUtil.toJsonStr(tags));
            post.setUserId(1870128545677393921L);
            list.add(post);
        }
//        存入数据库
        boolean save = postService.saveBatch(list);
        if (save){
            log.info("初始化帖子列表成功,条数为" + list.size());
        }else {
            log.info("初始化帖子列表失败");
        }
    }
}
