module.exports = function (app) {

    var authData = {
        email: 'homelala@naver.com',
        password: '1',
        nickname: 'egoing'
      };

    var passport = require('passport'),
        LocalStrategy = require('passport-local').Strategy;

    app.use(passport.initialize());
    app.use(passport.session());

    passport.serializeUser(function (user, done) {//로그인 성공시
        done(null, user.email);// 세션 데이터 안에 email값 들어간다.
    });

    passport.deserializeUser(function (id, done) {//페이지를 방문할 때마다 이 콜백이 실행된다.
        done(null, authData);
        //실제론 id값으로 db에 찾아서 정보를 찾아온다.
    });

    passport.use(new LocalStrategy({//로그인을 시도 했을 때 성공 실패 여부 검사
            usernameField: 'email',
            passwordField: 'pwd'
        },
        function (username, password, done) {
            if (username === authData.email) {
                if (password === authData.password) {
                    return done(null, authData, {
                        message: 'Welcome.'
                    });
                } else {
                    return done(null, false, {
                        message: 'Incorrect password.'
                    });
                }
            } else {
                return done(null, false, {
                    message: 'Incorrect username.'
                });
            }
        }
    ));
    return passport;
}