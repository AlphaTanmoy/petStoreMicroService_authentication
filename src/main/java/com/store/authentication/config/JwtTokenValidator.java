package com.store.authentication.config;

import com.store.authentication.utils.EncryptionUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader(KeywordsAndConstants.JWT_HEADER);

        if (jwt != null) {
            jwt = jwt.substring(7); // Remove "Bearer " prefix

            try {
                // Create a signing key
                SecretKey key = Keys.hmacShaKeyFor(KeywordsAndConstants.SECRET_KEY.getBytes());

                // Parse the JWT claims
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String encryptedEmail = String.valueOf(claims.get("email"));

                String email;
                try {
                    email = EncryptionUtils.decrypt(encryptedEmail);
                } catch (Exception e) {
                    throw new BadCredentialsException("Failed to decrypt email from token.", e);
                }

                String authorities = String.valueOf(claims.get("authorities"));
                System.out.println("authorities -------- " + authorities);

                List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auths);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                throw new BadCredentialsException("Invalid token...", e);
            }
        }

        filterChain.doFilter(request, response);
    }

}