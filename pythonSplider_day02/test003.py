import urllib.parse
import urllib.request
import json

if __name__ == '__main__':
    headers={
        "user-agent":"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36"
    }
    url="http://fanyi.youdao.com/translate?smartresult=dict&smartresult=rule"
    keyword=input("请输入翻译的字:")
    #post请求把formData和url一起提交，这里写formData(是一个字典)
    formData={
        "i":keyword,
        "doctype":"json"
    }
    #设置formData为urlencode编码和"UTF-8"编码
    data=urllib.parse.urlencode(formData).encode("UTF-8")
    #绑定请求的url,data,headers
    request=urllib.request.Request(url,data=data,headers=headers)
    #发送请求，获取到的是json串
    response=urllib.request.urlopen(request)
    #读取响应，设置中文编码，decode是将"UTF-8"编码转换成unicode编码
    trans=json.loads(response.read().decode("UTF-8"))
    print("翻译结果:"+trans["translateResult"][0][0]["tgt"])