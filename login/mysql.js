var mysql = require('mysql');
var connection = mysql.createConnection({
    host:'localhost',
    user:'root',
    password :'passwd',
    database:'nodedb',
});

connection.connect();
connection.query('select * from topic',function(err,results,fields){
    if(err){
        console.log(err);
    }
    console.log(results);
    console.log(results[1].name);
});

connection.end();