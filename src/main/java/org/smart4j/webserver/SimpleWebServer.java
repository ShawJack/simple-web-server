package org.smart4j.webserver;

import org.smart4j.threadpool.CustomerTheadPool;
import org.smart4j.threadpool.ThreadPool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ithink on 17-8-4.
 */
public class SimpleWebServer {

    //用于处理http请求的线程池
    private static ThreadPool<HttpRequestHandler> threadPool = new CustomerTheadPool<HttpRequestHandler>();
    //Server根路径
    private static String basePath;
    //Server Socket
    private static ServerSocket serverSocket;
    //监听端口，默认8080
    private static int port = 8080;

    public static void setPort(int port){
        SimpleWebServer.port = port;
    }

    public static void setBasePath(String basePath){
        File baseDictionary = new File(basePath);
        if(basePath!=null && baseDictionary.exists() && baseDictionary.isDirectory()){
            SimpleWebServer.basePath = basePath;
        }
    }

    public static void start() throws IOException{
        serverSocket = new ServerSocket(port);
        Socket socket;
        while((socket = serverSocket.accept()) != null){
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }

    static class HttpRequestHandler implements Runnable{
        private Socket socket;

        public HttpRequestHandler(Socket socket){
            this.socket = socket;
        }

        public void run() {
            BufferedReader socketReader = null;
            InputStream fileIn = null;
            PrintWriter printWriter = null;
            BufferedReader htmlReader = null;

            try{
                socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String filePath = basePath + socketReader.readLine().split(" ")[1];

                printWriter = new PrintWriter(socket.getOutputStream());

                if(filePath.endsWith("jpg") || filePath.endsWith("png") || filePath.endsWith("ico")){
                    printWriter.println("HTTP/1.1 200 OK");
                    printWriter.println("Server:Molly");
                    printWriter.println("Content-Type:image/jpeg");

                    fileIn = new FileInputStream(filePath);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int i = 0;
                    while((i = fileIn.read()) != -1){
                        out.write(i);
                    }
                    byte[] array = out.toByteArray();

                    printWriter.println("Content-Length:" + array.length);
                    printWriter.println("");

                    socket.getOutputStream().write(array, 0, array.length);
                    out.flush();
                    out.close();
                }else{
                    htmlReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                    printWriter.println("HTTP/1.1 200 OK");
                    printWriter.println("Server:Molly");
                    printWriter.println("Content-Type:text/html;charset=UTF-8");
                    printWriter.println("");

                    String line;
                    while((line = htmlReader.readLine()) != null){
                        printWriter.println(line);
                    }
                }

                printWriter.flush();
            } catch(IOException e){
                printWriter.println("HTTP/1.1 500");
                printWriter.println("");
                printWriter.flush();
            } finally {
                try{
                    close(socketReader, printWriter, htmlReader, fileIn);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void close(Closeable...closeables) throws IOException{
        if(closeables != null){
            for(Closeable closeable : closeables){
                if(closeable != null) {
                    closeable.close();
                }
            }
        }
    }

}
