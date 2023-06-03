import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PaginatedApiCall {

    public static JSONObject getTitleSingleCall(String author, int page) throws IOException, JSONException {
        List<String> list = new ArrayList<>();
        String api = "https://jsonmock.hackerrank.com/api/articles?<authorName>&page=1";
        api = api.replace("<authorName>", author);

        //Handling url manually
        URL url = new URL(api);
        String urlQuerySection = url.getQuery();
        String[] splitQuerySection = urlQuerySection.split("&");
        String[] splitQueryPageParam = splitQuerySection[1].split("=");

        api = api.replace(splitQuerySection[1], splitQueryPageParam[0] + "=" + page);
        //Overwriting url with edited url
        url = new URL(api);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        JSONObject obj = new JSONObject();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        for (String line = br.readLine(); line != null; line = br.readLine()) {

            obj = new JSONObject(line);
        }
        return obj;
    }

    public static List filterThroughJsonObjects(JSONObject jsonObject, String author, int currentPage) {
        List<String> list = new ArrayList<>();

        try {
            JSONArray jsonObjArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonObjArray.length(); i++) {
                JSONObject tempObj = jsonObjArray.getJSONObject(i);
                String title = tempObj.get("title").toString();

                String storyTitle = tempObj.get("story_title").toString();
                if (title != null && !title.equals("null"))
                    list.add("Author: " + author + " Page: ==>"+currentPage +"   "+ title);
                else {
                    if (storyTitle != null && storyTitle != "null")
                        list.add(author + "==>" + storyTitle);
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    public static List getTitles(String author) {
        List<String> list = new ArrayList<>();
        try {
            JSONObject jsonObj = getTitleSingleCall(author, 1);
            System.out.println(jsonObj);
            int castedTotalPages = Integer.parseInt(jsonObj.get("total_pages").toString());

            if (castedTotalPages == 1)
                list = filterThroughJsonObjects(jsonObj, author,1);

            else {
                for (int i = 1; i <= castedTotalPages; i++) {
                    jsonObj = getTitleSingleCall(author, i);
                    list.addAll( filterThroughJsonObjects(jsonObj, author,i));

                }
            }


        } catch (Exception e) {
            System.out.println(e);
        }
        for(String line:list)
            System.out.println(line);
        return list;
    }


    public static void main(String[] args) {
        String[]authorArray = new String[]{"epaga","poe","Stine"};
        String author = "epaga";
        List list =  getTitles(author);


    }


}



