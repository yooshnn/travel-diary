package com.td.traveldiary.global.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySQL POINT 타입 ↔ Attraction.latitude / Attraction.longitude 변환.
 *
 * MySQL POINT는 JDBC ResultSet에서 byte[]로 반환된다.
 * WKB(Well-Known Binary) 포맷: 앞 4바이트 SRID + 1바이트 byte order + 4바이트 geometry type + 8바이트 X + 8바이트 Y
 * MySQL 저장 순서는 ST_GeomFromText('POINT(lng lat)') 기준 X=경도, Y=위도.
 *
 * setParameter: INSERT/UPDATE 시 ST_GeomFromText('POINT(lng lat)', 4326) 문자열로 전달.
 * → PreparedStatement에 문자열을 넣으면 MySQL이 직접 파싱하므로 별도 바이너리 변환 불필요.
 * → AttractionMapper.xml에서 location 컬럼에 이 핸들러를 적용하면 된다.
 *
 * 주의: MyBatis TypeHandler는 단일 컬럼 ↔ 단일 자바 타입 매핑이 원칙이라
 * latitude/longitude 두 필드를 동시에 처리하기 위해 double[]을 중간 타입으로 사용한다.
 * double[0] = latitude (Y), double[1] = longitude (X)
 */
@MappedTypes(double[].class)
@MappedJdbcTypes(JdbcType.OTHER)
public class PointTypeHandler extends BaseTypeHandler<double[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, double[] coords, JdbcType jdbcType) throws SQLException {
        // coords[0] = latitude, coords[1] = longitude
        // MySQL POINT(X, Y) = POINT(경도, 위도)
        String wkt = String.format("ST_GeomFromText('POINT(%s %s)', 4326)", coords[1], coords[0]);
        ps.setString(i, wkt);
    }

    @Override
    public double[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toCoords(rs.getBytes(columnName));
    }

    @Override
    public double[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toCoords(rs.getBytes(columnIndex));
    }

    @Override
    public double[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toCoords(cs.getBytes(columnIndex));
    }

    /**
     * WKB 파싱: SRID(4) + byteOrder(1) + type(4) + X(8) + Y(8)
     * MySQL은 앞에 SRID 4바이트를 붙여서 반환한다.
     */
    private double[] toCoords(byte[] bytes) {
        if (bytes == null) return null;

        // byte order: 0x01 = little-endian, 0x00 = big-endian
        boolean littleEndian = bytes[4] == 1;

        double x = toDouble(bytes, 13, littleEndian); // 경도
        double y = toDouble(bytes, 21, littleEndian); // 위도

        return new double[]{y, x}; // [latitude, longitude]
    }

    private double toDouble(byte[] bytes, int offset, boolean littleEndian) {
        long bits = 0;
        if (littleEndian) {
            for (int i = 7; i >= 0; i--) {
                bits = (bits << 8) | (bytes[offset + i] & 0xFF);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                bits = (bits << 8) | (bytes[offset + i] & 0xFF);
            }
        }
        return Double.longBitsToDouble(bits);
    }
}
