var express = require('express');
var app = express();
var fs = require('fs');
var bodyParser = require('body-parser');
var compression = require('compression');
var helmet = require('helmet')
app.use(helmet());
const session = require('express-session')
var FileStore = require('session-file-store')(session)
var flash = require('connect-flash');

app.use(express.static('public'));
app.use(bodyParser.urlencoded({
  extended: false
}));  
app.use(compression());
app.use(session({// 미들웨어에서 세션을 사용할 수 있게 해준다
  secure:true,//https 사용
  secret: 'asadlfkj!@#!@#dfgasdg', //필수 보안 적인 요소
  resave: false,//세션 데이터의 값이 바뀌지 전까지 저장소에 값을 저장해주지 않는다
  saveUninitialized: true, //값이 바뀌거나 바뀌지 않아도 저장소에 값을 저장한다
  store: new FileStore()
}))
app.use(flash());

var passport = require('./lib/passport')(app);

app.get('*', function (request, response, next) {
  fs.readdir('./data', function (error, filelist) {
    request.list = filelist;
    next();
  });
});

var indexRouter = require('./routes/index');
var topicRouter = require('./routes/topic');
var authRouter = require('./routes/auth')(passport);

app.use('/', indexRouter);
app.use('/topic', topicRouter);
app.use('/auth', authRouter);

app.use(function (req, res, next) {
  res.status(404).send('Sorry cant find that!');
});

app.use(function (err, req, res, next) {
  console.error(err.stack)
  res.status(500).send('Something broke!')
});

app.listen(3000, function () {
  console.log('Example app listening on port 3000!')
});