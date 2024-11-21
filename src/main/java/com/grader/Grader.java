package com.grader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;


public class Grader {

    //Returns array of all the labs
    public static ArrayList<String> getLabs(String url){
        try {
            HttpURLConnection connectionnection = (HttpURLConnection) new URL(url + "/grade/labs").openConnection(); //
            connectionnection.setRequestMethod("GET");

            try (Scanner scanner = new Scanner(connectionnection.getInputStream())) {  //try with resources = automatic closing
                String res = "";
                while (scanner.hasNext()) {
                    res += scanner.nextLine();
                }
                //https://www.javadoc.io/doc/org.json/json/20171018/org/json/JSONArray.html
                JSONArray array = new JSONArray(res);
                ArrayList<String> labs = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    labs.add(array.getString(i)); //
                }
                return labs;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Apache http is way easier but connectionflicts with BlueJ
    //https://gist.github.com/mcxiaoke/8929954 
    public static String getFormattedAIResult(String url, String lab, File... files) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url + "/grade").openConnection();
            String bound =  "--bound"; //not Random number anymore
            String ls = "\r\n"; // Line separator (carriage return + newline)

            connection.setRequestMethod("POST");
            connection.setDoOutput(true); //Why is this even necessary default should be true
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + bound); 

            //Necessary to manually write bytes to the req body (output stream)
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
               
                
                out.writeBytes("--" + bound + ls);
                //Same as <input type='file' required multiple/>
                out.writeBytes("Content-Disposition: form-data; name=\"lab\"" + ls + ls);
                out.writeBytes(lab + ls);

                for (File file : files) {
                    /*
                      Writing the labs field:

                      --bound
                      Content-Disposition: form-data; name="lab"
                      6.1-Taxes

                      --bound
                        Content-Disposition: form-data; name="doc"; filename="PX_Last_First_Lab.java"
                        Content-Type: application/octet-stream <- Tells the server stream is in binary
                     */
                    out.writeBytes("--" + bound + ls);
                    out.writeBytes("Content-Disposition: form-data; name=\"doc\"; filename=\"" + file.getName() + "\"" + ls);
                    out.writeBytes("Content-Type: application/octet-stream" + ls + ls);
                    

                    //Write the file content exactly byte by byte 
                    //Unncessary for now since we're only using uploading
                    try (FileInputStream in = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096]; //Splitting the file into chunks of size 4096B
                        int bytesRead = in.read(buffer); //Reads up to b.length bytes, returns total bytes read or -1
                        while (bytesRead != -1) { //Keeps reading until the end of file
                            out.write(buffer, 0, bytesRead); //data, startoffset, # bytes to write
                            bytesRead = in.read(buffer);
                        }
                    }
                    out.writeBytes(ls); 
                }
               //footer: --bound--
                out.writeBytes("--" + bound + "--" + ls);
                out.flush();
            }

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                String res = "";
                while(scanner.hasNext()){
                    res += scanner.nextLine();
                }
                JSONObject json = new JSONObject(res);
                double score = json.getDouble("score");
                String comments = json.getString("comments");
                return ("Score: " + score + "\nComments:\n" + comments);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return "Failed somehow";
    }
}
