import requests
import json
"""
分页爬取今日头条的title,get方式，使用requests，
"""

def page(startPage, endPage):
    for i in range(startPage-1, endPage):
        print("当前第%s页" % (i+1))
        url="https://www.toutiao.com/api/search/content/?aid=24&app_name=web_search&offset={}&format=json&keyword=77%E4%BA%8B%E5%8F%98&autoload=true&count=20&en_qc=1&cur_tab=1&from=search_tab&pd=synthesis&timestamp=1562467283218".format(i*20)
        isPage=loadPage(url)
        if isPage==False:
            return

def loadPage(url):
    headers = {
        "user-agent": "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36",
        "cookie":"tt_webid=6710713392061285902; WEATHER_CITY=%E5%8C%97%E4%BA%AC; tt_webid=6710713392061285902; UM_distinctid=16bc9db8a29f6-0417349b599406-516d3e71-13c680-16bc9db8a2d85; csrftoken=5eb2a0e00bcbb888f417ef261ee5269a; CNZZDATA1259612802=1761938442-1562456487-https%253A%252F%252Fwww.baidu.com%252F%7C1562461887; s_v_web_id=ddb620b1224506f21ba99de20d4169e3; __tasessionId=ned4t635k1562467258609"
    }
    try:
        data = requests.get(url, headers=headers).text
        news = json.loads(data)
        for new in news["data"]:
            if "title" in new.keys():
                print(new["title"])
    except Exception as e:
        print(e)
        return False
    return True

if __name__ == '__main__':
    startPage = int(input("请输入起始页码"))
    endPage = int(input("请输入终止页码"))
    page(startPage,endPage)

