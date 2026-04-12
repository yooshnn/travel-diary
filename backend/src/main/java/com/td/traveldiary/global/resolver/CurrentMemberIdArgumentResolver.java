package com.td.traveldiary.global.resolver;

import com.td.traveldiary.global.annotation.CurrentMemberId;
import com.td.traveldiary.global.exception.BusinessException;
import com.td.traveldiary.global.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMemberId.class)
                && Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Long resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mvContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws RuntimeException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            // 이론적으로 도달 불가한 코드지만 방어적 체크
            // SecurityConfig에서 authenticated()로 인증 필요 엔드포인트를 막아둬 인증 없는 요청은 컨트롤러까지 도달하지 못한다.
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return (Long) authentication.getPrincipal();
    }

}
