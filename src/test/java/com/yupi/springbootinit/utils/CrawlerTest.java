package com.yupi.springbootinit.utils;
import java.io.IOException;
import java.util.ArrayList;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CrawlerTest {

    @Resource
    private PostService postService;

    @Test
    void fetchPicture() throws IOException {
        Integer current= 1;
        String url = "https://cn.bing.com/images/search?q=刘浩存&first=" + current;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        for (Element element : elements) {
            String m = element.select(".iusc").get(0).attr("m");
            Map<String,Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            String title = (String)map.get("t");
            System.out.println(murl);
            System.out.println(title);
        }
    }

    @Test
    void fetchPostPage(){
        String json = "{\"pageSize\":12,\"sortOrder\":\"descend\",\"sortField\":\"_score\",\"tags\":[],\"searchText\":\"\",\"current\":1,\"reviewStatus\":1,\"hiddenContent\":true,\"type\":\"passage\"}";
        String url = "https://api.codefather.cn/api/search/";
        String result = HttpRequest.post(url)
                .body(json)
                .execute().body();
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
        boolean b = postService.saveBatch(list);
        System.out.println(b);
//        System.out.println(records);
//        System.out.println(result);
    }
}
