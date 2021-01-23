var http = require('http');
var cookie = require('cookie');

http.createServer(function(request,response){
    console.log(request.headers.cookie);
    var cookies={};
    if(request.headers.cookie !== undefined){
        var cookies = cookie.parse(request.headers.cookie);
    }
    console.log(cookies.yummy_cookie)
    response.writeHead(200, {
        'Set-Cookie':[
            'yummy_cookie=choco', 
            'tasty_cookie=strawberry',
            `Permanent=cookies; Max-Ages=${60*60*24*30}`, // 수명
            'Secure=Secure; Secure', //https를 통해서만 쿠키 전달
            'HttpOnly=HttpOnly; HttpOnly', //http를 통해서만 쿠키 전달 가능 자바스크립트로는 불가
            'Path=Path;; Path=/cookie', //경로를 지정에서 그 경로와 하위 경로에서만 쿠키가 살아있다
            'Domain=Domain; Domain=o2.org' //어떠한 서브 도메인에서도 살아 남는다
            
        ]
    });
    response.end('Cookies!');
}).listen(3000);