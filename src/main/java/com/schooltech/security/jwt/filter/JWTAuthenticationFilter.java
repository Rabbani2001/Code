package com.schooltech.security.jwt.filter;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.security.jwt.service.TenantService;
import com.schooltech.security.jwt.util.JWTHelper;
import com.schooltech.sms.entity.master.SchoolTenant;
import com.schooltech.sms.exception.SchoolTenantNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
JWTAuthenticationFilter that extends OncePerRequestFilter and override method and write the logic
to check the token that is comming in header. We have to write 5 important logic:-
1.Get Token from request
2.Validate Token
3.GetUsername from token
4.Load user associated with this token
5.set authentication
 */
@Component("jwtauthfilter")
//@Order(2)
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    @Autowired
    private JWTHelper jwtHelper;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TenantService tenantService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("SchoolTech....Executing JWTAuthenticationFilter");
        //Setting the tenant in the thread context
        settingTenant(request);
        String requestHeader = request.getHeader("Authorization");
        //Bearer 2352345235sdfrsfgsdfsdf
        logger.info("SchoolTech....Authorization Header: {}", requestHeader);
        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            //This means request is sent with Authorization token
            token = requestHeader.substring(7);
            try {
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("SchoolTech....Illegal Argument while fetching the username !!");
                e.printStackTrace();
            } catch (ExpiredJwtException e) {
                logger.info("SchoolTech....Given jwt token is expired !!");
                e.printStackTrace();
            } catch (MalformedJwtException e) {
                logger.info("SchoolTech....Some changed has done in token !! Invalid Token");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //This means using Authorization token system is able to find who(username) trying to login
            //Now fetching User details from username collected using Authorization token
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            //validating the sent Authorization token
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {
                //set the authentication to the system. This means user can proceed with its request as his passed token is valid and correct.
                logger.info("SchoolTech....Authorization token validation success!!");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.info("SchoolTech....Authorization token validation fails!!");
            }
        }

        //Here request will be continued
        try {
            filterChain.doFilter(request, response);
        } finally {
            logger.info("SchoolTech....Clearing TenantContext for thread as request has been addressed");
            TenantContext.clear(); // Ensure cleanup after the request
        }
    }


    private void settingTenant(HttpServletRequest request) throws SchoolTenantNotFoundException {
        String tenantId = null;
        if (request.getHeader("X-Tenant-CODE") != null) {
            SchoolTenant schoolTenant = tenantService.getSchoolTenant(request.getHeader("X-Tenant-CODE"));
            if (schoolTenant == null)
                throw new SchoolTenantNotFoundException("SchoolTech....Tenant CODE: " + request.getHeader("X-Tenant-CODE") + " not found/registered with us");
            tenantId = schoolTenant.getTenantId();
        } else {
            tenantId = request.getHeader("X-Tenant-ID");
            if (tenantId == null)
                throw new SchoolTenantNotFoundException("SchoolTech....Tenant ID: " + request.getHeader("X-Tenant-ID") + " not found/registered with us");
        }
        logger.info("SchoolTech....Received X-Tenant-ID as: {} from school_tenant table", tenantId);
        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setCurrentTenant(tenantId);
        }
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/docs");
    }
}
