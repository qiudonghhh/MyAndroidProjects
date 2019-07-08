import urllib.parse
import urllib.request
import json

"""
爬取有道翻译接口进行翻译（post方式，使用requests）
"""

if __name__ == '__main__':
    url = "http://fanyi.youdao.com/translate?smartresult=dict&smartresult=rule"
    headers = {
        "User-Agent":"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36"
    }
    key = input("请输入您要翻译字:")
    #定义formdata
    formData = {
        "i":key,
        "doctype":"json",
    }
    data = urllib.parse.urlencode(formData).encode("UTF-8")#字典类型
    #向请求绑定fromdata、headers
    reqeust = urllib.request.Request(url,data=data,headers=headers)
    #发送请求并获取数据
    response = urllib.request.urlopen(reqeust)
    print(json.loads(response.read().decode("UTF-8")))







