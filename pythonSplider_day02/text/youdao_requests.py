import requests
"""
爬取有道翻译接口进行翻译（post方式，使用requests）
"""
if __name__ == '__main__':
    headers = {
        "user-agent": "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36"
    }
    url = "http://fanyi.youdao.com/translate?smartresult=dict&smartresult=rule"
    keyword = input("请输入翻译的字:")
    # post请求把formData和url一起提交，这里写formData(是一个字典)
    formData = {
        "i": keyword,
        "doctype": "json"
    }
    # 设置formData为urlencode编码和"UTF-8"编码

    data=requests.post(url,headers,formData)
    print(data.text)