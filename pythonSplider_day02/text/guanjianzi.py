import urllib.parse #编码处理
import urllib.request #发送请求

if __name__ == '__main__':
    """
    输入关键字，爬取百度网站html数据
       get方式---爬取数据（html数据）
    """
    #url编码
    url = "https://www.baidu.com/s"
    headers = {
        "user-agent" : "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36"
    }
    kw = input("请输入您要想查询的关键字：")
    wd = {
        "wd":kw
    }
    #将字符串进行url编码
    keyword = urllib.parse.urlencode(wd)
    fullurl = url +"?" + keyword
    request = urllib.request.Request(fullurl,headers=headers)
    response = urllib.request.urlopen(request)
    print(response.read().decode("UTF-8"))