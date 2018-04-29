/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.int492.cifarclssfr.cifarwebservice.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 *
 * @author Mix
 */
@RestController
@RequestMapping("api")
public class CIFARClassifiedController {

    @Autowired
    private Environment env;

    @Autowired
    private StringRedisTemplate template;

    @RequestMapping(value = "status", method = RequestMethod.GET)
    @ResponseBody
    public String checkStatus() {
        return "api status OK!!";
    }

    @RequestMapping(value = "checkfile", method = RequestMethod.GET)
    @ResponseBody
    public File[] checkfile() {
        String path = env.getRequiredProperty("target.file.save");
        System.out.println("PATH ::::::::::::: " + path);
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return listOfFiles;
    }

    @RequestMapping(value = "classified", method = RequestMethod.POST)
    @ResponseBody
    public String classifiedCIFAR(HttpSession session, InputStream dataStream) throws IOException, InterruptedException {
        long curTimemillis = System.currentTimeMillis();
        String sessionId = session.getId()+curTimemillis;
        String outputPath = env.getRequiredProperty("target.file.save");
        String fileName = sessionId + ".png";

        saveFile(dataStream, outputPath, fileName);
        recordToRedis(sessionId, outputPath, fileName);
        
        String result = null;
        int counter = 0;
        do{
            if(counter >= 10){
                throw new HttpClientErrorException(HttpStatus.REQUEST_TIMEOUT);
            }
            Thread.sleep(3000);
            System.out.println("-------------------CHECK REDIS-------------------");
            result = getResultClassified(sessionId);
            System.out.println("Result : "+ result);
            if(result != null){
                deleteResultClassified(sessionId);
            }
            counter++;
        }while(result == null);

        return result;
    }

    private void saveFile(InputStream dataStream, String outputPath, String fileName) {
        File targetFile = new File(outputPath +"/"+ fileName);
        File f = new File(outputPath);
        if (f.exists()) {
            System.out.println("-------------------------------PATH EXIST------------------------");
        }else{
            System.out.println("-------------------------------PATH NOT EXIST------------------------");
            f.mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = dataStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void recordToRedis(String sessionId, String outputPath, String fileName) {
        ValueOperations<String, String> ops = this.template.opsForValue();
        String key = "@" + sessionId;
        String value = outputPath +"/"+ fileName;
        ops.set(key, value);
    }
    
    private String getResultClassified(String sessionId){
        ValueOperations<String, String> ops = this.template.opsForValue();
        String result = ops.get(sessionId);
        return result;
    }
    
    private void deleteResultClassified(String sessionId){
        this.template.delete(sessionId);
    }
}
