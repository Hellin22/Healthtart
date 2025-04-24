package com.dev5ops.healthtart.security;

import com.dev5ops.healthtart.user.domain.CustomUserDetails;
import com.dev5ops.healthtart.user.domain.KakaoUserProfile;
import com.dev5ops.healthtart.user.domain.dto.JwtTokenDTO;
import com.dev5ops.healthtart.user.domain.dto.UserDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    // JWT 토큰 서명 및 검증에 사용할 비밀 키
    private final Key secretKey;
    private long expirationTime;

    public JwtUtil(@Value("${token.secret}") String secretKey, @Value("${token.expiration_time}") long expirationTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
    }

    /* 설명. Token 검증(Bearer 토큰이 넘어왔고, 우리 사이트의 secret key로 만들어 졌는가, 만료되었는지와 내용이 비어있진 않은지) */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT Token claims empty {}", e);
        }

        return false;
    }

    /* 설명. 넘어온 AccessToken으로 인증 객체 추출 */
    public Authentication getAuthentication(String token) {

        /* 설명. 토큰에서 claim들 추출 */
        Claims claims = parseClaims(token);
        log.info("넘어온 AccessToken claims 확인: {}", claims);

        Collection<? extends GrantedAuthority> authorities = null;
        if (claims.get("roles") == null) {
            log.warn("권한 정보가 없는 토큰입니다. Claims: {}", claims);
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        } else {
            /* 설명. 클레임에서 권한 정보들 가져오기 */
            authorities = Arrays.stream(claims.get("roles").toString()
                            .replace("[", "")
                            .replace("]", "")
                            .split(", "))

                    // 각 역할 앞에 "ROLE_" 접두사를 붙임
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }

        log.info("추출된 권한 정보: {}", authorities);

        // claims에서 사용자 정보 추출
//        String userCode = claims.get("userCode", String.class);
        String userCode = claims.getSubject();
        log.info("user code는 {}", userCode);
        String email = claims.get("email", String.class);
        String name = claims.get("nickname", String.class);

        // CustomUserDetails 객체 생성
        UserDTO userDTO = new UserDTO();
        userDTO.setUserCode(userCode);
        userDTO.setUserEmail(email);
        userDTO.setUserName(name);

        // CustomUserDetails 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(userDTO, (List<GrantedAuthority>) authorities, true, true, true, true);

        // 인증 객체 생성 및 반환 -> custom으로 반환
        return new UsernamePasswordAuthenticationToken(customUserDetails, "", authorities);

    }


    /* 설명. Token에서 Claims 추출 */
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    /* 설명. Token에서 사용자의 id(subject 클레임) 추출 */
    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public String generateToken(JwtTokenDTO tokenDTO, List<String> roles, String provider){
        Claims claims = Jwts.claims().setSubject(tokenDTO.getUserCode());
        claims.put("email", tokenDTO.getUserEmail());
        claims.put("nickname", tokenDTO.getUserNickname());
        claims.put("roles", roles);
        claims.put("provider", provider != null ? provider : "local");  // null 대신 "local" 사용

        log.info("claim의 모든 정보를 보자.: {}", claims.toString());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String generateTokenFromKakaoUser(KakaoUserProfile profile, String userCode) {
        // 1. 카카오 사용자 정보 추출
        Long kakaoId = profile.getId();
        String email = profile.getKakao_account().getEmail();
        String nickname = profile.getKakao_account().getProfile().getNickname();

        // 3. DTO 구성
        JwtTokenDTO tokenDTO = new JwtTokenDTO(
                userCode,
                email,
                nickname
        );

        // 4. 기본 역할 설정
        List<String> roles = List.of("MEMBER");

        // 5. 토큰 생성 (provider는 "kakao" 명시)
        return generateToken(tokenDTO, roles, "kakao");
    }



    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("email", String.class));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("name", String.class));
    }

    public String getProviderFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("provider", String.class));
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String getUserCodeFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
}