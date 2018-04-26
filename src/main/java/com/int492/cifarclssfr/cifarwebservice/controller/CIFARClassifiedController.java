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
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mix
 */
@RestController
@RequestMapping("api")
public class CIFARClassifiedController {
    
    @Autowired
    private Environment env;

    @RequestMapping(value = "status", method = RequestMethod.GET)
    @ResponseBody
    public String checkStatus() {
        return "api status OK!!";
    }

    @RequestMapping(value = "classified", method = RequestMethod.POST)
    @ResponseBody
    public String classifiedCIFAR(HttpSession session, InputStream dataStream) throws IOException {
        String sessionId = session.getId();
        String outputPath = env.getRequiredProperty("target.file.save")+sessionId+".png";
        File targetFile = new File(outputPath);
        //Path path = Files.createTempFile("C:/Users/Mix/Desktop/tmp/pic", ".png");
        try (FileOutputStream out = new FileOutputStream(targetFile)) {
        byte[] buffer = new byte[1024]; 
        int len; 
        while ((len = dataStream.read(buffer)) != -1) { 
            out.write(buffer, 0, len); 
        }
    } catch (Exception e) {
        // TODO: handle exception
    }
        return sessionId+", Commplete";
    }
}
