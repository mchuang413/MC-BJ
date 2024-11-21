package mcbjpackage;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;

public class Grader {

    public static ArrayList<String> getLabs(String url){
         CloseableHttpClient httpClient = HttpClients.createDefault();
        //GET request for labs
        HttpGet getReq = new HttpGet(url + "/grade/labs");
        try (CloseableHttpResponse response = httpClient.execute(getReq)){ //automatic closing
            HttpEntity entity = response.getEntity();   //Entity = body
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                //Use org.json library to parse
                JSONArray array = new JSONArray(result);
                ArrayList<String> labs = new ArrayList<>();
                for(int i = 0; i < array.length(); i++){
                    labs.add(array.getString(i));
                }
                return labs;
            }
        }catch (IOException e){
            System.out.println("Error");
        }
        return null;
    }

    //Elipses represent the ability to pass in as many files as you want
    public static String getFormattedAIResult(String url, String lab, File... files){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postReq = new HttpPost(url + "/grade");
        //Equivalent to creating FormData()
        MultipartEntityBuilder entity = MultipartEntityBuilder.create(); //same as multipart/form, but why is it not a subclass of HttpEntity?
        entity.addTextBody("lab", lab);

        for (File file : files) {
            entity.addPart("doc", new FileBody(file)); //Binary body part backed by a file
        }
        postReq.setEntity(entity.build());

        try(CloseableHttpResponse response = httpClient.execute(postReq)){
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String result = EntityUtils.toString(resEntity);
                //Use org.json library to parse
                JSONObject jsonObject = new JSONObject(result);
                double score = jsonObject.getDouble("score");
                String comments = jsonObject.getString("comments");
                return ("Score: " + score + "\nComments: " + comments);
            }
        } catch (IOException e){
            System.out.println("Error");
        }
        return "Failed somehow";
    }
}