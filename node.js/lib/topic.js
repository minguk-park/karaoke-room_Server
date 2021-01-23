var db = require('./db');
var template = require('./template.js')
var url = require('url');
var qs = require('querystring');
var sanitizeHtml=require('sanitize-html');

exports.home = function(request,response){
    db.query('select * from topic',function(err,topics){
        var title = 'Welcome';
        var description = 'Hello, Node.js'
        var list = template.list(topics);
        var html =template.html(title,list,
            `<h2>${title}</h2>${description}`,
            `<a href="/create">create</a>`
            );
        
        response.writeHead(200);//200 -> 파일을 성공적으로 전송
        response.end(html);
    }); 
}
exports.page = function(request,response){
    var _url = request.url;
    var queryData = url.parse(_url,true).query;
    db.query('select * from topic',function(err,topics){
        if(err) throw err;
        db.query(`select * from topic left join author on topic.author_id=author.id where topic.id=?`,queryData.id,function(err2,topic){
            if(err2) throw err2;
            var title = topic[0].title;
            var description = topic[0].description;
            var list = template.list(topics);
            var html =template.html(title,list,
                `<h2>${sanitizeHtml(title)}</h2>${description}
               <p> by ${topic[0].name}</p>
                `
                ,`<a href="/create">create</a> 
                <a href="/update?id=${queryData.id}">update</a>
                <form action="/delete_process" method="post">
                    <input type="hidden" name ="id" value="${queryData.id}">
                    <input type="submit" value="delete">
                </form>
                `
                );
            response.writeHead(200);//200 -> 파일을 성공적으로 전송
            response.end(html);
        })
    }); 
}
exports.create = function(request,response){
    db.query('select * from topic',function(err,topics){
        db.query('select * from author',function(err2,authors){
            if(err2) throw err2;
            var title = 'WEB - create';
            var list = template.list(topics);
            var html =template.html(title,list,`
            <form action ="/create_process" method="post">
                <p><input type="text" name="title" placeholder="title">
                <p>
                <textarea name="description" placeholder="description"></textarea>
                </p>
                <P>
                ${template.authorSelect(authors)}
                </P>
                <p><input type="submit" value="submit"></p>
            </form>
            `,``);
            response.writeHead(200);//200 -> 파일을 성공적으로 전송
            response.end(html);
        })
    })
}
exports.create_process = function(request,response){
    var body='';
    request.on('data',function(data){ //정보가 조각조각 들어온다.
        body=body+data;
    });
    request.on('end',function(end){
    var post=qs.parse(body);//post로 데이터 전송된 데이터를 받아 배열로 저장
        db.query(`
            insert into topic (title,description, created,author_id)
            values(?,?,NOW(),?)`,[post.title,post.description,post.author],function(err,result){
            if(err) throw err;
            response.writeHead(302,{Location: `/?id=${result.insertId}`}); //302 -> 리다이렉션
            response.end();
            });
    });
}
exports.update = function(request,response){
    var _url = request.url;
    var queryData = url.parse(_url,true).query;
    db.query(`select * from topic`,function(err,topics){
        db.query('select * from author',function(err2,authors){
            if(err) throw err;  
            var list = template.list(topics);
            db.query(`select * from topic where id = ?`,queryData.id,function(err2,result){
                if(err2) throw err2;
                var title = result[0].title;
                var description = result[0].description;
                var html =template.html(result[0].title,list,
                    `
                    <form action ="/update_process" method="post">
                        <input type="hidden" value="${queryData.id}" name="id">
                        <p><input type="text" name="title"value="${title}">
                        <p>
                        <textarea name="description" >${description}</textarea>
                        </p>
                        <p>
                            ${template.authorSelect(authors,result[0].author_id)}
                        </p>
                        <p><input type="submit" value="submit"></p>
                    </form>
                    `
                    ,`<a href="/create">create</a> <a href="/update?id=${result[0].id}">update</a>`
                    );
                response.writeHead(200);//200 -> 파일을 성공적으로 전송
                response.end(html);
            })
        })
    })
}
exports.update_process = function(request,response){
    var body='';
        request.on("data",function(data){
            body+=data;
        });
        request.on('end',function(end){
            var post=qs.parse(body);
            db.query('update topic set title = ?, description = ?, author_id=? where id =?',[post.title,post.description,post.author,post.id],function(err,result){
                if(err) throw err;
                response.writeHead(302,{Location: `/?id=${post.id}`});
                response.end();
            });
        });
}
exports.delete_process = function(request,response){
    var body='';
        request.on("data",function(data){
            body+=data;
        });
        request.on("end",function(end){
            var post=qs.parse(body);
            var id=post.id;
            db.query('delete from topic where id = ?',[post.id],function(err,result){
                if(err) throw err;
                response.writeHead(302,{Location: `/`});
                response.end();
            })
        });
}