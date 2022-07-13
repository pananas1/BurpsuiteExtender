# 一、预留
# 二、环境搭建（Java开发环境）
事先安装好JDK、Maven、IDEA。
1. 新建Maven项目
2. pom.xml文件中添加如下两个依赖
```xml
 <!-- burpsuite提供的API-->
<dependency>
    <groupId>net.portswigger.burp.extender</groupId>
    <artifactId>burp-extender-api</artifactId>
    <version>2.3</version>
</dependency>
 <!-- intellij提供的一个图形化设计UI的组件，可选-->
<dependency>
    <groupId>com.intellij</groupId>
    <artifactId>forms_rt</artifactId>
    <version>7.0.3</version>
</dependency>
```
# 三、动态调试
1. 点击IDEA上边的 Add Configuration

  <img src="img\image-20220713203817672.png" alt="image-20220713203817672" style="zoom: 50%;" />

2. 添加远程调试

  <img src="img\image-20220713204303246.png" alt="image-20220713204303246" style="zoom: 50%;" />

3. 记住端口号

  <img src="img\image-20220713204350797.png" alt="image-20220713204350797" style="zoom:50%;" />

4. 使用如下命令启动burpsuite，注意address要与IDEA上配置的一致

  ```shell
  社区版或者是正版的启动命令如下：
  java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar burpsuite.jar
  
  破解版根据破解的方法使用如下命令之一：
  ava -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xbootclasspath/p:burp-loader-keygen-70yeartime-BurpPro.jar -jar burpsuite_pro_v1.7.37.jar
  
  java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xbootclasspath/p:BurpHelper2019.jar -jar burpsuite_pro_v1.7.37.jar
  ```

5. 启动burpsuite之后，在代码相应地方下断点，点击Debug，即可调试代码。

# 四、HelloWorld

目标：在burpsuite的控制台输出一个HelloWorld

burpsuite插件开发规范：

1. 实现类必须在burp包下，且类名为BurpExtender；
2. 实现类必须实现IBurpExtender接口

直接上代码：

```java
// 实现类必须在burp包下
package burp;

import java.io.PrintWriter;

// 类名必须为BurpExtender，且实现IBurpExtender接口
public class BurpExtender implements IBurpExtender{
    // 声明一个IBurpExtenderCallbacks对象，该对象可以调用很多与操作burpsuite相关的方法，使用频率非常高
    private IBurpExtenderCallbacks callbacks;
    // 声明一个IExtensionHelpers对象，该对象可以调用很多辅助方法，比如分析创建HTTP请求，使用频率也非常高
    private IExtensionHelpers helpers;

    // 定义输出流，用于在burpsuite上打印信息
    private PrintWriter stdout;
    private PrintWriter stderr;
    @Override
    // 实现IBurpExtender接口需要重写的方法
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        // 将标准输出转换成由burpsuite提供的输出
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);
        // 设置插件名称
        callbacks.setExtensionName("HelloWorld");
        // 加载完插件后的输出
        stdout.println("HelloWorld");
    }
}
```

使用maven将代码打成jar包，命令：mvn clean package

<img src="img\image-20220713210944643.png" alt="image-20220713210944643" style="zoom:50%;" />

在burpsuite上加载插件，可以看到，输出了HelloWorld

<img src="img\image-20220713211216870.png" alt="image-20220713211216870" style="zoom: 67%;" />



# 五、处理HTTP请求响应

burpsuite最核心的功能就是对HTTP请求响应的处理，掌握了这些API后，就能开发一些简单且实用的插件。

目标：

1. 识别流量中的敏感信息，包括URL中参数，请求体和响应体中的数据；
2. 判断POST请求是否有CSRF请求头。

## 1）HTTP监听器

我们可以通过实现IHttpListener接口，然后调用IBurpExtenderCallbacks.registerHttpListener()来注册一个HTTP监听器。监听器可以收到任何BurpSuite发出的请求和响应的通知，我们就可以在IHttpListener接口提供的方法processHttpMessage中分析或者修改这些消息。

IHttpListener接口定义如下：

```java
public interface IHttpListener
{
    void processHttpMessage(
            int toolFlag,
            boolean messageIsRequest,
            IHttpRequestResponse messageInfo);
}
```

## 2）分析HTTP消息

processHttpMessage方法中有三个参数：

1. toolFlag 用于标识Burpsuite的模块，例如只想处理Proxy模块的消息，则可以用如下语句过滤：

   ```java
   // 在IBurpExtenderCallbacks中还定义了其它的flag，比如TOOL_SCANNER、TOOL_DECODER等等
   if (toolFlag == IBurpExtenderCallbacks.TOOL_PROXY) {
       //
   }
   ```

2. messageIsRequest 判断是否是请求消息

3. messageInfo 标识HTTP消息的详细信息，所有HTTP相关的信息都在这个对象中

通常使用如下方法得到请求消息中的各个部分，直接上代码段：

```java
// 仅处理Proxy模块中的消息
if (toolFlag == IBurpExtenderCallbacks.TOOL_PROXY) {
    // 仅处理请求消息
    if (messageIsRequest) {
        // 使用IExtensionHelpers对象的方法analyzeRequest解析整个请求，得到一个请求消息对象，后续就可以使用该对象调用		  IRequestInfo类提供的方法来获取我们需要的信息，例如请求体、请求头等
        IRequestInfo analyzeRequest = helpers.analyzeRequest(messageInfo); 
        // 获取请求方法
        String requestMethod = analyzeRequest.getMethod();
        // 获取请求的URL
        URL requestUrl = analyzeRequest.getUrl();
        // 获取请求头
        List<String> headers = analyzeRequest.getHeaders();
        // 将请求消息转换成byte数组
        byte[] requestByte = messageInfo.getRequest();
        // 返回请求体开始的位置
        int bodyOffset = analyzeRequest.getBodyOffset(); 
        // 将byte[]类型的请求消息转换成String
        String request = helpers.bytesToString(requestByte); 
        // 截取出body部分，得到请求体
        String requestBody = request.substring(bodyOffset); 
    }
}
```

获得了上面的信息之后，我们就可以进行响应的逻辑判断。

例如判断URL的参数中是否存在敏感信息：

此处省略isSenInfoInUrl方法，总之可以通过正则匹配的方式，来判断URL中是否有符合条件的数据，如果匹配到了，则调用IHttpRequestResponse的setHighlight方法给请求做个标记。

```java
// 判断URL中是否包含敏感信息，如果包含，则高亮
if (MessageService.isSenInfoInUrl(requestMethod, requestUrl.toString())) {
    // 给符合条件的消息标记黄色
    messageInfo.setHighlight("yellow");
}
```

例如判断POST请求中是否有csrf请求头：

```java
// 判断POST请求中是否有csrf请求头，如果没有，则高亮
if ("POST".equals(requestMethod.toUpperCase())) {
    for (String header : headers) {
        if (!header.contains("csrf") && !header.contains("xsrf")) {
            messageInfo.setHighlight("red");
        }
    }
}
```

对响应消息的处理跟请求消息如出一辙，代码段如下:

```java
// 对响应消息的处理
byte[] responseByte = messageInfo.getResponse();
String responseStr = helpers.bytesToString(responseByte);
IResponseInfo analyzeResponse = helpers.analyzeResponse(responseByte);
int bodyOffset = analyzeResponse.getBodyOffset();
String responseBody = responseStr.substring(bodyOffset);
// 如果包含敏感信息，则标记橙色
if (MessageService.isSenInfoInBody(responseBody)) {
    messageInfo.setHighlight("orange");
}
```

最终插件效果如下：

响应中包含敏感信息，记录被标记成橙色：

![image-20220713221010664](img\image-20220713221010664.png)

## 3）总结

通过Burpsuite提供的各种API，我们可以轻松的处理报文中的任何信息，根据报文做一些逻辑判断，除此之外，我们还可以修改请求响应，或者发起一个新的请求。

比如想要更新参数，可以使用

```java
// updateParameter 更新参数
newRequest = helpers.updateParameter(newRequest, newPara);
// setRequest 设置最终的请求消息
messageInfo.setRequest(newRequest);
```

如果想要删除参数，则可以

```java
// removeParameter 删除参数
newRequest = helpers.removeParameter(newRequest, para);
```

如果想要发送一个新的HTTP消息，则可以

```java
// buildHttpMessage 构建http消息，传入请求头和请求体即可
newRequest = helpers.buildHttpMessage(headers, byteBody);
// makeHttpRequest 发起一个http请求，其中messageInfo.getHttpService()提供和host port等信息，这些都是可以自定义的
callbacks.makeHttpRequest(messageInfo.getHttpService(), newRequest);
```

总之，想干啥就干啥。

# 六、UI

UI的设计使用的是Swing组件，可以通过图形化方式去设计：

## 1）创建一个按钮

1. 新建GUI Form

<img src="img\image-20220713225358837.png" alt="image-20220713225358837" style="zoom:50%;" />

2. 可以直接将右边的控件拖拽到主界面中，需要注意的是，要给每个控件都设置好一个属性名，才可以自动生成代码，这里以按钮为例

   <img src="img\image-20220713225509601.png" alt="image-20220713225509601" style="zoom:50%;" />

3. 在IDEA的Settings中勾选Java source code

   <img src="img\image-20220713222302163.png" alt="image-20220713222302163" style="zoom:50%;" />

4. 在UI类中点击构建，即可自动生成代码

   <img src="img\image-20220713225606063.png" alt="image-20220713225606063" style="zoom:50%;" />

5. 给按钮添加一些逻辑，右击对应的控件，点击Create Listener，创建一个ActionListener

   <img src="img\image-20220713225632957.png" alt="image-20220713225632957" style="zoom:50%;" />

6. 创建ActionListener之后，UI类代码如下：

   我们可以在actionPerformed方法里写点击按钮之后需要做的事。

   例如我想在点击按钮之后在Burpsuite中输出一些信息，则可以在构造方法中传入IBurpExtenderCallbacks的对象，并调用相应方法。

   ```java
   public class UI {
       // 控件
       private JButton button1;
       private JPanel rootPanel;
   
       // 初始化代码块，不用管
       {
   // GUI initializer generated by IntelliJ IDEA GUI Designer
   // >>> IMPORTANT!! <<<
   // DO NOT EDIT OR ADD ANY CODE HERE!
           $$$setupUI$$$();
       }
   
       // 主要编写这部分代码
       // 构造方法中传入IBurpExtenderCallbacks的对象
       public UI(IBurpExtenderCallbacks callbacks)) {
           // 按钮相关逻辑
           button1.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   // 这里写点击按钮后的逻辑
                   // 调用IBurpExtenderCallbacks的printOutput方法，在按钮点击后在burpsuite中输出clicked
                   callbacks.printOutput("clicked");
               }
           });
       }
   
       // 下面都是自动生成的代码，不用管
       
       /**
        * Method generated by IntelliJ IDEA GUI Designer
        * >>> IMPORTANT!! <<<
        * DO NOT edit this method OR call it in your code!
        *
        * @noinspection ALL
        */
       private void $$$setupUI$$$() {
           rootPanel = new JPanel();
           rootPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
           button1 = new JButton();
           button1.setText("Button");
           rootPanel.add(button1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
       }
   
       /**
        * @noinspection ALL
        */
       public JComponent $$$getRootComponent$$$() {
           return rootPanel;
       }
   }
   
   ```

7. 想在Burpsuite中新增一个Tab页，需要实现ITab接口，并重写如下两个方法，简化后的代码如下：

   ```java
   public class BurpExtender implements IBurpExtender, ITab{
       private IBurpExtenderCallbacks callbacks;
       // 声明一个UI对象
       private UI ui;
       @Override
       public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
           this.callbacks = callbacks;
           callbacks.setExtensionName("uidemo");
           // 在registerExtenderCallbacks方法中创建UI对象
           // 因为在UI类中给构造方法新增了一个IBurpExtenderCallbacks类型的参数，所以在这里需要传入callbacks
           ui = new UI(callbacks);
           // 注册Tab页
           callbacks.addSuiteTab(this);
   
       }
   
       @Override
       // 设置Tab页名称
       public String getTabCaption() {
           return "tabname";
       }
   
       // 获取组件
       @Override
       public Component getUiComponent() {
           return ui.$$$getRootComponent$$$();
       }
   }
   ```

8. 效果如下，可以看到burpsuite多了一个页签，以及一个大大的按钮，点击按钮，即可在Output中输出内容

   <img src="img\image-20220713231052965.png" alt="image-20220713231052965" style="zoom: 50%;" />

   点击按钮之后，在Output中输出clicked

   <img src="img\image-20220713231145075.png" alt="image-20220713231145075" style="zoom:50%;" />

   

## 2）总结

这部分不咋会，想要掌握估计得系统的学习Swing组件的使用 。

# 七、Burpsuite API

以上提到的API，仅仅是Burpsuite提供的API中的冰山一角。

官方api文档：https://portswigger.net/burp/extender/api/

中文资料：BurpSuite插件开发指南http://drops.xmd5.com/static/drops/tools-14685.html

最快的学习方法应该就是阅读别人写的插件了。（待完善）