var mysql = require('mysql');
var db = mysql.createConnection({
    host:'localhost',
    user:'root',
    password :'passwd',
    database:'nodedb'
});
db.connect();
module.exports = db;