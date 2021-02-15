const express= require('express');
const app = express();
const bodyParser = require('body-parser');
const db = require('./mysql.js');
const crypto = require('crypto'); // 비밀번호 암호화
const cookie = require('cookie');
const cookieParser = require('cookie-parser');
const session = require('express-session'); 
const FileStore = require('session-file-store')(session)
const jwt = require('jsonwebtoken');
const secretObj = require('./lib/secret');

app.use(session({
    secret: 'asadlfkj!@#!@#dfgasdg',
    resave: false,
    saveUninitialized: true,
    store:new FileStore({logFn: function(){}})
}))
app.use(bodyParser.urlencoded({extended:false}));
app.use(cookieParser());

app.get('/users',function(req,res){
    console.log('who get in here/users');
    console.log(req.session);
    /*let token = req.cookies.user;
    console.log(token);
    let decode = jwt.verify(token,secretObj.secret);
    console.log(decode.userId);*/
    if(req.session.loginStutus===undefined){
        res.send("login first")
    }else{
        res.send(req.session.uid);
    }
    console.log(req.session)
});
app.get('/sessions',function(req,res){
    /*let token =jwt.sign({
        userId:"good",
        loginStatus:true
    },
    secretObj.secret,{
        expiresIn:'5m'
    })*/
    req.session.uid = result[0].id;
    req.session.loginStutus=true;
    req.session.save(function(){
        res.write("login Success");
        res.end();
    })
    /*res.cookie("user",token);
    res.send(token)*/
});
app.post('/kakao',function(req,res){
    var inputData;
    console.log("kakao")
    req.on('data',function(data){
        inputData = JSON.parse(data); 
        req.on('end',function(){
            console.log(inputData);
            id = inputData.name;
            pwd = inputData.email;
            db.query('insert into kakaoInfo (name,email) values(?,?)',[id,pwd],function(err,result){
                if(err){
                    if(err.code === "ER_DUP_ENTRY"){ //id 중복
                        res.write("Reigster fail duplicate id");
                        res.end();
                    }else{
                        console.log(err);
                    }
                }else{
                    res.write("Register Success");
                    res.end();
                }   
            })
        })
    })
});
app.post('/expire',function(req,res){
    var inputData;
    console.log("expires")
    req.on('data',function(data){
        inputData = JSON.parse(data); 
        req.on('end',function(){
            console.log(inputData);
            email = inputData.email;
            db.query('delete from kakaoInfo where email=?',[email],function(err,result){
                if(err){
                    console.log(err);
                }else{
                    res.write("expire Success");
                    res.end();
                }
            })
        })
    })
});
app.post('/register',function(req,res){
    var inputData;
    req.on('data',function(data){
        inputData = JSON.parse(data);
    })
    req.on('end',function(){
        id = inputData.id;
        pwd = inputData.pwd;
        let salt = Math.round((new Date().valueOf() * Math.random())) + ""; //난수 발생
        let hashpwd =crypto.createHash('sha512').update(pwd+salt).digest('hex'); // 비밀번호 암호화
        db.query('insert into info (id,passwd,salt) values(?,?,?)',[id,hashpwd,salt],function(err,result){
            if(err){
                if(err.code === "ER_DUP_ENTRY"){ //id 중복
                    res.write("Reigster fail duplicate id");
                    res.end();
                }else{
                    console.log(err);
                }
            }else{
                res.write("Register Success");
                res.end();
            }   
        })
    })
})

app.post('/login',function(req,res){
    var inputData;
    req.on('data',function(data){
        inputData = JSON.parse(data);
    })
    req.on('end',function(){
        id = inputData.id;
        pwd = inputData.pwd;
        db.query('select * from info where id = ?',id,function(err,result){
            if(result[0] === undefined){
                res.write("id is none");
                res.end();
            }else{
                let salt = result[0].salt;
                let dbPwd = result[0].passwd;//db에서 암호화된 비번
                let hashpwd =crypto.createHash('sha512').update(pwd+salt).digest('hex'); // 비밀번호 암호화
                if(dbPwd === hashpwd){ //비밀 번호 점검
                    /*let token =jwt.sign({
                        userId:result[0].id,
                        status:true
                    },
                    secretObj.secret,{
                        expiresIn:'5m'
                    })*/
                    req.session.uid = result[0].id;
                    req.session.loginStutus=true;
                    req.session.save(function(){
                        res.json(req.session.uid);
                        res.end();
                    })
                    //res.cookie("user",token);
                    //res.send(token)
                }else{
                    res.write("login fail password is different");
                    res.end();
                }
            }
        })
    })
})

app.listen(80,function(){
    console.log('Example app listening in port 80?')
})