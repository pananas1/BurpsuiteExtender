打包成包含依赖的jar包：`mvn assembly:assembly`

项目修改自：https://github.com/gh0stkey/HaE



## 新增两个正则匹配场景

1.精确匹配请求url的内容

人机接口调用机机接口的场景，可以找出所有机机接口

2.精确匹配get请求的参数

快速定位可能存在url跳转漏洞的请求



csv文件存储

每条记录的对象  record

写文件之前需要做去重功能：根据URL和COMMENT两个字段来去重