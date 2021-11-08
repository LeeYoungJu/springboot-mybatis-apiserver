package kr.co.meatmatch.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.meatmatch.common.dto.ResponseDto;
import kr.co.meatmatch.common.dto.ResponseStatus;
import kr.co.meatmatch.common.dto.ResponseStatusString;
import kr.co.meatmatch.common.dto.STATUS_CODE;
import kr.co.meatmatch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

//    private static final List<String> EXCLUDE_URL =
//            Collections.unmodifiableList(
//                    Arrays.asList(
//                            "/api/member",
//                            "/authenticate"
//                    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        try {
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            }

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if(jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    throw new Exception();
                }
            }

            filterChain.doFilter(request, response);
        } catch(Exception e) {
            response.setStatus(HttpStatus.resolve(STATUS_CODE.INVALID_TOKEN).value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            HashMap<String, Object> invalidTokenMap = new HashMap<>();
            invalidTokenMap.put("message", "Unauthenticated.");
//            ResponseDto errorObj = new ResponseDto(ResponseStatusString.builder().code(STATUS_CODE.INVALID_TOKEN).msg("Unauthenticated.").build(), null);

            objectMapper.writeValue(response.getWriter(), invalidTokenMap);
        }
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
//    }
}
