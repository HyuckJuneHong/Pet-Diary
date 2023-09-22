package kr.co.petdiary.global.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.auth.context.OwnerThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class OwnerInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler
            , Exception ex) {
        if (OwnerThreadLocal.isOwner()) {
            OwnerThreadLocal.clear();
            log.info("====== After Completion - Owner ThreadLocal Clear =======");
        }
    }
}
