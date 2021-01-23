var db = require('./db');
var template = require('./template.js')
var qs = require('querystring');
var url = require('url');

exports.home=function(request,response){
    db.query('select * from topic',function(err,topics){
        db.query('select * from author',function(err,authors){

            var title = 'author';
            var list = template.list(topics);
            var html =template.html(title,list,
                `
                ${template.authorTable(authors)}
                <style>
                    table{
                        border-collapse:collapse;
                    }
                    td{
                        border:1px solid black;
                    }
                </style>
                `,
                '<a href="/author/create">create</a>'
                ); 
            response.writeHead(200);//200 -> 파일을 성공적으로 전송
            response.end(html);
        });
    }); 
}
exports.create = function(request,response){
    db.query('select * from topic',function(err,topics){
        db.query('select * from author',function(err,authors){
            var title = 'author';
            var list = template.list(topics);
            var html =template.html(title,list,
                `
                ${template.authorTable(authors)}
                <style>
                    table{
                        border-collapse:collapse;
                    }
                    td{
                        border:1px solid black;
                    }
                </style>
                <form action="/author/create_process" method="post">
                    <p>
                        <input type="text" placeholder="name" name="name"/>
                    </p>
                    <p>
                        <textarea type="text" placeholder="profile" name="profile"></textarea>
                    </p>
                    <p>
                        <input type="submit" value="submit">
                    </p>
                </form>
                `,
                '<a href="/author/create">create</a>'
                ); 
            response.writeHead(200);//200 -> 파일을 성공적으로 전송
            response.end(html);
        });
    });
}
exports.create_process=function(request,response){
    var body='';
    request.on('data',function(data){ //정보가 조각조각 들어온다.
        body=body+data;
    });
    request.on('end',function(end){
    var post=qs.parse(body);//post로 데이터 전송된 데이터를 받아 배열로 저장
        db.query(`
            insert into author (name,profile)
            values(?,?)`,[post.name,post.profile],function(err,result){
            if(err) throw err;
            response.writeHead(302,{Location: `/author`}); //302 -> 리다이렉션
            response.end();
        });
    });   
}
exports.update = function(request,response){
    var _url = request.url;
    var queryData = url.parse(_url,true).query;
    db.query('select * from topic',function(err,topics){
        db.query('select * from author',function(err,authors){
            db.query('select * from author where id =?',queryData.id,function(err,author){
                var title = 'author';
                var list = template.list(topics);
                var html =template.html(title,list,
                `
                ${template.authorTable(authors)}
                <style>
                    table{
                        border-collapse:collapse;
                    }
                    td{
                        border:1px solid black;
                    }
                </style>
                <form action="/author/update_process" method="post">
                    <input type="hidden" name="id" value="${queryData.id}"/>
                    <p>
                        <input type="text" value=${author[0].name} name="name"/>
                    </p>
                    <p>
                        <textarea type="text" name="profile">${author[0].profile}</textarea>
                    </p>
                    <p>
                        <input type="submit" value="update">
                    </p>
                </form>
                `,
                '<a href="/author/create">create</a>'
                ); 
                response.writeHead(200);//200 -> 파일을 성공적으로 전송
                response.end(html);
            })
        });
    });
}
exports.update_process = function(request,response){
    var body='';
    request.on("data",function(data){
        body+=data;
    });
    request.on('end',function(end){
        var post=qs.parse(body);
        db.query('update author set name = ?, profile = ? where id =?',[post.name,post.profile,post.id],function(err,result){
            if(err) throw err;
            response.writeHead(302,{Location: `/author`});
            response.end();
        });
    });
}
exports.delete_process=function(request,response){
    var body='';
        request.on("data",function(data){
            body+=data;
        });
        request.on("end",function(end){
            var post=qs.parse(body);
            db.query('delete from author where id = ?',[post.id],function(err,result){
                if(err) throw err;
                response.writeHead(302,{Location: `/author`});
                response.end();
            })
        });
}