package com.td.traveldiary.global.response;

import java.util.List;
import java.util.function.Function;

/*
 * # SliceResponse
 * 커서 기반 페이징 응답 포맷.
 * 오프셋 페이징과 달리 마지막 아이템의 특정 필드값(cursor)으로 다음 페이지 시작점을 표현한다.
 *
 * # 타입 파라미터
 * T: 리스트 아이템 타입
 * C: cursor 타입. 정렬 기준마다 필요한 필드가 다르므로 Cursor를 구현해 그때그때 정의한다.
 *
 * # cursor와 hasNext의 관계
 * content가 비어있지 않으면 cursor는 항상 마지막 아이템에서 추출된다.
 * content가 비어있으면 cursor=null이다.
 * hasNext는 호출 측에서 결정해서 넘기므로 of()가 보장하지 않는다.
 */
public record SliceResponse<T, C extends SliceResponse.Cursor>(
        List<T> content,
        boolean hasNext,
        C cursor
) {
    public interface Cursor {
        /*
         * 마지막 아이템(T)에서 cursor(C)를 추출하는 함수형 인터페이스.
         *
         * Function<T, C>를 상속받았기 때문에, 유일한 추상 메서드인 apply()를 람다로 구현할 수 있다.
         * generate()는 apply()를 호출하는 역할을 하고, Cursor를 생성한다는 역할을 명확히 한다.
         * 단, default로 선언해야 추상 메서드를 apply() 하나로 유지할 수 있다.
         * 함수형 인터페이스는 추상 메서드가 1개여야 람다로 구현 가능하기 때문이다.
         *
         * Controller에서 사용 예시:
         * SliceResponse.of(
         *     result.data(),
         *     result.hasNext(),
         *     item -> new AttractionCursor(item.bookmarkCount(), item.name(), item.id())
         * );
         */
        interface Generator<T, C extends SliceResponse.Cursor> extends Function<T, C> {
            default C generate(T item) {
                return apply(item);
            }
        }
    }

    public static <T, C extends SliceResponse.Cursor> SliceResponse<T, C> of(
        List<T> content,
        boolean hasNext,
        SliceResponse.Cursor.Generator<T, C> generator
    ) {
        C nextCursor = null;

        if (!content.isEmpty()) {
            T lastItem = content.getLast();
            nextCursor = generator.generate(lastItem);
        }

        return new SliceResponse<>(content, hasNext, nextCursor);
    }

    public <R> SliceResponse<R, C> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream().map(mapper).toList();
        return new SliceResponse<>(mappedContent, hasNext, cursor);
    }
}
