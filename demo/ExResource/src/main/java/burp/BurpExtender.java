package burp;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class BurpExtender implements IBurpExtender, IHttpListener {

    private IBurpExtenderCallbacks callbacks;
    private static IExtensionHelpers helpers;
    public static PrintWriter stdout;
    public static PrintWriter stderr;

    public static String fileSeparator = File.separator;
    private static final String currentPath = System.getProperty("java.class.path");


    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();

        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);

        String version = "1.0.0";
        callbacks.setExtensionName(String.format("ER %s", version));
        stdout.println("extender load success");
        stdout.println("@author: pananas");

        callbacks.registerHttpListener(BurpExtender.this);
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        IHttpService httpService = messageInfo.getHttpService();
        String host = httpService.getHost();

        if (toolFlag == IBurpExtenderCallbacks.TOOL_PROXY) {
            if (messageIsRequest){
                byte[] request = messageInfo.getRequest();
                byte[] response = messageInfo.getResponse();
                IRequestInfo analyzeRequest = helpers.analyzeRequest(request);
                IResponseInfo analyzeResponse = helpers.analyzeResponse(response);

                List<String> headers = analyzeRequest.getHeaders();
                String urlString = headers.size() > 0 ? headers.get(0).split(" ")[1] : "";
                if (urlString.endsWith(".js")) {
                    stdout.println(urlString);
                    String filePath = currentPath.substring(0, currentPath.lastIndexOf(fileSeparator) + 1) + host + urlString;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] responseBody = Arrays.copyOfRange(response, analyzeResponse.getBodyOffset(), response.length);
                    writeInFile(file,new String(responseBody, StandardCharsets.UTF_8).intern());

                }

            }else {

            }


        }


    }

    private static void writeInFile(File file, String content) {
        Writer writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void generateFile() {
        String host = "baidu.com";
        // E:\tools\burpsuite\burpsuite_pro_v2.1.06.jar
        String currentPath = System.getProperty("java.class.path");
        // E:\tools\burpsuite
        // 要保存的文件夹的路径   如果存在 则直接返回路径  如果不存在 则创建文件夹 然后返回路径
        String savePath = currentPath.substring(0, currentPath.lastIndexOf(fileSeparator) + 1) + host;
        Path path = Paths.get(savePath);
        try {
            Path pathCreate = Files.createDirectory(path);
            System.out.println(pathCreate);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}