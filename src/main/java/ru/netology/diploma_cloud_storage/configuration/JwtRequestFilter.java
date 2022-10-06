package ru.netology.diploma_cloud_storage.configuration;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.diploma_cloud_storage.domain.JwtToken;
import ru.netology.diploma_cloud_storage.exception.UnauthorizedErrorException;
import ru.netology.diploma_cloud_storage.service.AuthService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtToken jwtToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("auth-token");
        String username = null;
        String jwtToken = null;
        String errorStatus;

        if (requestTokenHeader != null && requestTokenHeader.startsWith(JwtToken.JWT_START_KEY)) {
            jwtToken = requestTokenHeader.substring(JwtToken.JWT_START_KEY.length());
            try {
                username = this.jwtToken.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                errorStatus = "Unable to get JWT Token";
                System.out.println(errorStatus);
                throw new UnauthorizedErrorException("authentication token", errorStatus);
            } catch (ExpiredJwtException e) {
                errorStatus = "JWT Token has expired";
                System.out.println(errorStatus);
                throw new UnauthorizedErrorException("authentication token", errorStatus);
            }
//        } else if (requestTokenHeader == null) {
//            errorStatus = "Does not provide Authorization Header";
//            logger.info(errorStatus);
//            throw new UnauthorizedErrorException(errorStatus);
        } else if (requestTokenHeader != null &&
                !requestTokenHeader.startsWith(JwtToken.JWT_START_KEY)) {
            errorStatus = "JWT Token does not begin with \"" + JwtToken.JWT_START_KEY + "\"";
            logger.warn(errorStatus);
            throw new UnauthorizedErrorException("authentication token", errorStatus);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.authService.loadUserByUsername(username);
            if (this.jwtToken.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}

