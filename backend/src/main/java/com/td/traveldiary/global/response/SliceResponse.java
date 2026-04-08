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
 * # content null 처리
 * null이면 빈 리스트로 변환한다. 조회 결과가 없는 것은 예외 상황이 아니라 정상 케이스이므로
 * 호출 측에서 null 체크를 강제하지 않는다.
 *
 * # cursor ↔ hasNext 관계
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

        if (content != null && !content.isEmpty()) {
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
