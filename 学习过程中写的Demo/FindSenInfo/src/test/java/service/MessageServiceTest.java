package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageServiceTest {
    @Test
    public void test(){
        boolean senInfoInBody = MessageService.isSenInfoInBody("{'username':'pwx','password':'869609268@qq.com'}");
        System.out.println(senInfoInBody);
    }

}