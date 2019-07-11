package com.springcloud.zuul.filter;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class ZuulFilter extends com.netflix.zuul.ZuulFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        String serialNumber = req.getParameter("serialNumber");
        return !StringUtils.isEmpty(serialNumber);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        String serialNumber = req.getParameter("serialNumber");
        String reqCode = req.getParameter("verificationCode");
        String verifCode = stringRedisTemplate.opsForValue().get(serialNumber);

        if (verifCode == null || !verifCode.equals(reqCode)) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8.getType());
            ctx.setResponseBody("Verification Code Error!");
        }
        return null;
    }
}
