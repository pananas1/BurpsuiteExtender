package burp.test;

public class Test {
    public static void main(String[] args) {
        String input = "GET /-0U_dTmfKgQFm2e88IuM_a/ps_fp.htm?pid=superman&fp=undefined&im=undefined&wf=undefined&br=3&qid=0xcba8973f002981c3&bi=6371C32B4C9CED9C8002680B3F41DC3F:FG=1";
        String urlString = input.split(" ")[1];
        String urlString2 = urlString.indexOf("?") > 0 ? urlString.substring(urlString.indexOf("?")+1) : urlString;
        System.out.println(urlString);
        urlString = urlString.indexOf("?") > 0 ? urlString.substring(0, urlString.indexOf("?")) : urlString;
        System.out.println(urlString);
        System.out.println(urlString2);
    }
}
