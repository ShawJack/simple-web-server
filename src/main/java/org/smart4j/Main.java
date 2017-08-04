package org.smart4j;

import org.smart4j.webserver.SimpleWebServer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ithink on 17-8-5.
 */
public class Main {

    public static void main(String[] args) throws Exception{
        String classPath = Main.class.getResource("/").getPath();
        SimpleWebServer.setBasePath(classPath);
        SimpleWebServer.start();
    }

}
