package burp;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.ConstantUtils;
import common.StringUtils;
import service.MessageService;

import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

public class BurpExtender implements IBurpExtender, IHttpListener {

    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;

    private PrintWriter stdout;
    private PrintWriter stderr;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);
        // 注册HTTP监听器
        callbacks.registerHttpListener(this);
        // 设置插件名称
        callbacks.setExtensionName(ConstantUtils.extenderName);
        // 加载完插件后的输出
        callbacks.printOutput(ConstantUtils.loadMessage);

    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (toolFlag == IBurpExtenderCallbacks.TOOL_PROXY) {
            if (messageIsRequest) {
                // 对请求消息的处理
                IRequestInfo analyzeRequest = helpers.analyzeRequest(messageInfo); // 解析请求
                String requestMethod = analyzeRequest.getMethod(); // 获取请求方法
                URL requestUrl = analyzeRequest.getUrl(); // 获取请求的URL
                List<String> headers = analyzeRequest.getHeaders(); // 获取请求头
                byte[] requestByte = messageInfo.getRequest();
                int bodyOffset = analyzeRequest.getBodyOffset(); // 返回请求体开始的位置
                String request = helpers.bytesToString(requestByte); // 将byte[]类型的请求消息转换成String
                String requestBody = request.substring(bodyOffset); // 截取body部分，得到body

                // 判断URL中是否包含敏感信息，如果包含，则高亮
                if (MessageService.isSenInfoInUrl(requestMethod, requestUrl.toString())){
                    messageInfo.setHighlight("yellow");
                }

                // 判断POST请求中是否有csrf请求头，如果没有，则高亮
                if ("POST".equals(requestMethod.toUpperCase())){
                    for (String header:headers){
                        if (!header.contains("csrf")&&!header.contains("xsrf")){
                            messageInfo.setHighlight("yellow");
                        }
                    }
                }

                // 判断请求体中是否有敏感信息，如果有，则高亮
                if (MessageService.isSenInfoInBody(requestBody)){
                    messageInfo.setHighlight("yellow");
                }

            } else {
                // 对响应消息的处理
                byte[] responseByte = messageInfo.getResponse();
                String responseStr = helpers.bytesToString(responseByte);
                IResponseInfo analyzeResponse = helpers.analyzeResponse(responseByte);
                int bodyOffset = analyzeResponse.getBodyOffset();
                String responseBody = responseStr.substring(bodyOffset);
                // 判断响应体中是否有敏感信息，如果有，则高亮
                if (MessageService.isSenInfoInBody(responseBody)){
                    messageInfo.setHighlight("orange");
                }

            }
        }
    }
}
