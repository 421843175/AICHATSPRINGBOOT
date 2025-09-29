package com.jupiter.chatweb.util;




import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {

        /**
         * token过期时间七天
         */
        private static final long EXPIRE_TIME = 24*60 * 60 * 1000*7;
        /**
         * token秘钥
         */
        private static final String TOKEN_SECRET = "9c1cj8e8b1a!3Tod6eu&Try9ap6dyChangeInyds9ic6qrtu?if8hefYestll!4fToooChange@for_you5fwwooe5rr5llfddr";


        /**
         * 生成签名，30分钟过期
         * @param username 用户名
         * @param loginTime 登录时间
         * @return 生成的token
         */
        public static String sign(String username, String loginTime,int power) {
            try {
                // 设置过期时间
                Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
                // 私钥和加密算法
                Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
                // 设置头部信息
                Map<String, Object> header = new HashMap<>();
                header.put("Type", "Jwt");
                header.put("alg", "HS256");
                // 返回token字符串
                return JWT.create()
                        .withHeader(header)
                        .withClaim("username", username)
                        .withClaim("loginTime", loginTime)
                        .withClaim("power",power)
                        .withExpiresAt(date)
                        .sign(algorithm);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 检验token是否正确
         * @param token 需要校验的token
         * @return 校验是否成功
         */
        public static boolean verify(String token){
            try {
                //设置签名的加密算法：HMAC256
                Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT jwt = verifier.verify(token);
                System.out.println(jwt.getClaims().get("username").asString());
                return true;
            } catch (Exception e){
                return false;
            }
        }

        /**
         * 获取token中包含的登录用户名
         * @param token 需要校验的token
         * @return 登录用户名
         */
        public static String getLoginName(String token) {
            try {
                Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT jwt = verifier.verify(token);
                return jwt.getClaims().get("username").asString();
            } catch (Exception e){
                return null;
            }
        }




}
