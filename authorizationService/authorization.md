# 后端授权说明
基于Spring Authorization Server OAuth2 PKCE

## 1、/oauth2/authorize
首先请求authorize(GET) http(s)://ip:port/oauth2/authorize
**param参数如下：**
client_id: 当前授权服务器设置的client_id
response_type: 返回类型，本项目默认为配置为code
scope: 授权范围，根据配置文件，多个scope用空格分开
redirect_url: 回调地址，与配置文件中一致
code_challenge: 初始的code
code_challenge_method: 交换code的算法，本项目默认S256

例如：http://localhost:9000/oauth2/authorize?response_type=code&client_id=melon&scope=profile&redirect_uri=http://localhost:3000/login&code_challenge=3OQWa3nmOu85N0-wNr1Gl3_DMcOgwbwE7DnfBJDUdFk&code_challenge_method=S256
PKCE示例如下
code_challenge: 3OQWa3nmOu85N0-wNr1Gl3_DMcOgwbwE7DnfBJDUdFk
code_verifier: n6vdp85csypSzpAMZ5lDgXLdbGaNmGKtXO9U32V8bOOINJ1agNCRhKuzNXob4_q2q9lxK_y1khgaNEMt_BfWmtBpTbJdqbO7KhLSF01KHCxKqfKXXqb2Y4rtzRUP_9t3


## 2、/oauth2/token
请求token(POST) http(S)://ip:port/oauth2/token
**x-www-form-urlencoded参数如下**
grant_type: 授权类型，本项目默认为authorization_code
scope: 授权范围，与authorize中保持一致
client_id: 与authorize中保持一致
redirect_url: 与authorize中保持一致
code: 从/oauth2/authorize重定向到redirect_url中后面携带的code参数
code_verifier: code_challenge在code_challenge_method加密算法后的验证值

**请求成功后返回值类型如下**
{
    "access_token": "",
    "scope": "",
    "token_type": "Bearer",
    "expires_in": xxx
}