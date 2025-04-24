package com.dev5ops.healthtart.inbody.service;

import net.minidev.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class InbodyDataExtractor {

    // 기본 틀에 있는 숫자 리스트 정의
    private static final List<String> weightScale = Arrays.asList("55", "70", "85", "100", "115", "130", "145", "160", "175", "190", "205");
    private static final List<String> muscleWeightScale = Arrays.asList("70", "80", "90", "100", "110", "120", "130", "140", "150", "160", "170");
    private static final List<String> fatWeightScale = Arrays.asList("40", "60", "80", "100", "160", "220", "280", "340", "400", "460", "520");
    private static final List<String> bmiScaleMale = Arrays.asList("10.0", "15.0", "18.5", "22.0", "25.0", "30.0", "35.0", "40.0", "45.0", "50.0", "55.0");
    private static final List<String> bmiScaleFemale = Arrays.asList("10.0", "15.0", "18.5", "21.0", "25.0", "30.0", "35.0", "40.0", "45.0", "50.0", "55.0");
    private static final List<String> fatPercentageScaleMale = Arrays.asList("0.0", "5.0", "10.0", "15.0", "20.0", "25.0", "30.0", "35.0", "40.0", "45.0", "50.0");
    private static final List<String> fatPercentageScaleFemale = Arrays.asList("8.0", "13.0", "18.0", "23.0", "28.0", "33.0", "38.0", "43.0", "48.0", "53.0", "58.0");

    // 각 line에서 데이터를 추출하여 JSON 형태로 반환
    public static String extractInbodyDataFromLines(String[] lines) {
        // 순서를 보장하는 LinkedHashMap 사용
        Map<String, Object> inbodyData = new LinkedHashMap<>();
        Set<String> extractedFloats = extractAllFloats(lines); // 모든 실수형 값을 추출해 저장

        // 성별 확인
        boolean isFemale = isFemale(lines);

        try {
            // 1. 신장: cm가 포함된 줄의 숫자를 추출
            inbodyData.put("height", extractHeight(lines));

            // 2. 검사일시: 날짜 형태의 값을 추출 (YYYY.MM.DD)
            LocalDateTime date = extractDate(lines);
            inbodyData.put("dayOfInbody", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

            // 3. 인바디 점수: 00/100 형태의 점수 추출
            inbodyData.put("inbodyScore", extractScore(lines));

            // 4. 체중: 두 번째 '체중' 키워드 뒤의 값을 실수형 값들과 비교하여 추출
            inbodyData.put("weight", extractWeightByComparison(lines, "체중", extractedFloats));

            // 5. 골격근량, 체지방량: 소수점이 포함된 값을 추출
            inbodyData.put("muscleWeight", extractDoubleValue(lines, muscleWeightScale, "골격근량"));
            inbodyData.put("fatWeight", extractDoubleValue(lines, fatWeightScale, "체지방량", true)); // 반드시 소수점 포함된 값

            // 6. BMI, 체지방률: 부호나 단위가 없는 순수한 실수형 숫자만 추출
            inbodyData.put("bmi", extractBMI(lines, isFemale));
            inbodyData.put("fatPercentage", extractFatPercentage(lines, isFemale));

            // 7. 기초대사량: kcal 또는 kal 뒤에 나오는 숫자를 추출
            inbodyData.put("basalMetabolicRate", extractBasalMetabolicRate(lines));

        } catch (Exception e) {
            System.err.println("Error extracting inbody data: " + e.getMessage());
        }

        // 순서 보장이 된 데이터를 JSON 형태로 변환
        return new JSONObject(inbodyData).toString();
    }

    // 성별 확인
    private static boolean isFemale(String[] lines) {
        for (String line : lines) {
            if (line.contains("여성") || line.contains("female")) {
                return true; // 여성인 경우
            }
        }
        return false; // 남성인 경우
    }

    // 신장 추출
    private static String extractHeight(String[] lines) {
        for (String line : lines) {
            if (line.contains("cm")) {
                Matcher matcher = Pattern.compile("\\d+\\.\\d+|\\d+").matcher(line);
                if (matcher.find()) {
                    return matcher.group();
                }
            }
        }
        return "N/A";
    }

    // 검사일시 추출
    private static LocalDateTime extractDate(String[] lines) {
        for (String line : lines) {
            Matcher matcher = Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{2}").matcher(line);
            if (matcher.find()) {
                String dateString = matcher.group();
                // 마침표를 하이픈으로 변경
                dateString = dateString.replace(".", "-");
                // LocalDateTime으로 변환, 시간 부분은 기본적으로 00:00:00으로 설정
                return LocalDateTime.parse(dateString + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
        return null; // 값을 찾지 못했을 경우 null 반환
    }

    // 인바디 점수 추출
    private static Integer extractScore(String[] lines) {
        for (String line : lines) {
            Matcher matcher = Pattern.compile("\\d+/100").matcher(line);
            if (matcher.find()) {
                String scoreString = matcher.group();
                String[] scoreParts = scoreString.split("/");
                return Integer.parseInt(scoreParts[0]);
            }
        }
        return null;
    }

    // 특정 키워드 뒤의 기본 틀과 다른 값을 추출 (소수점 포함 숫자)
    private static String extractDoubleValue(String[] lines, List<String> scale, String keyword) {
        return extractDoubleValue(lines, scale, keyword, false);
    }

    // 소수점 필수 여부를 포함한 메소드 오버로드
    private static String extractDoubleValue(String[] lines, List<String> scale, String keyword, boolean forceDecimal) {
        boolean foundKeyword = false;

        for (String line : lines) {
            if (foundKeyword) {
                // 소수점 값 또는 숫자가 나오면 그 값을 반환
                Matcher matcher = Pattern.compile("\\d+\\.\\d+|\\d+").matcher(line);
                while (matcher.find()) {
                    String value = matcher.group();
                    // 기본 틀에 없는 값을 찾음, 소수점 필수일 때는 소수점이 포함된 값만 허용
                    if (!scale.contains(value) && (!forceDecimal || value.contains("."))) {
                        return value;
                    }
                }
            }

            // 키워드를 찾으면 그 이후부터 값을 추출함
            if (line.contains(keyword)) {
                foundKeyword = true;
            }
        }
        return "N/A";
    }

    // 모든 실수형 숫자를 추출하는 메소드
    private static Set<String> extractAllFloats(String[] lines) {
        Set<String> floatSet = new HashSet<>();
        for (String line : lines) {
            Matcher matcher = Pattern.compile("\\d+\\.\\d+").matcher(line); // 소수점을 포함한 실수만 추출
            while (matcher.find()) {
                floatSet.add(matcher.group());
            }
        }
        return floatSet;
    }

    // 체중 추출 (두 번째 '체중' 키워드 뒤의 실수형 값을 비교)
    private static String extractWeightByComparison(String[] lines, String keyword, Set<String> extractedFloats) {
        boolean foundKeyword = false;
        int keywordCount = 0;

        for (String line : lines) {
            if (foundKeyword && keywordCount >= 2) {
                // 소수점 값 또는 숫자가 나오면 실수형 값과 비교하여 반환
                Matcher matcher = Pattern.compile("\\d+\\.\\d+").matcher(line);
                if (matcher.find()) {
                    String value = matcher.group();
                    if (extractedFloats.contains(value)) {
                        return value; // OCR로 추출된 값과 동일하면 반환
                    }
                }
            }

            // '체중' 키워드를 두 번 찾으면 그 이후부터 값을 추출함
            if (line.contains(keyword)) {
                keywordCount++;
                if (keywordCount == 2) {
                    foundKeyword = true;
                }
            }
        }
        return "N/A";
    }

    // 기초대사량 추출 (kcal 또는 kal)
    private static String extractBasalMetabolicRate(String[] lines) {
        for (String line : lines) {
            if (line.contains("kcal") || line.contains("kal")) {
                Matcher matcher = Pattern.compile("\\d+").matcher(line);
                if (matcher.find()) {
                    return matcher.group();
                }
            }
        }
        return "N/A";
    }

    // BMI 추출 (성별에 따른 기본 틀 사용)
    private static String extractBMI(String[] lines, boolean isFemale) {
        List<String> bmiScale = isFemale ? bmiScaleFemale : bmiScaleMale;
        boolean foundKeyword = false;

        for (String line : lines) {
            if (foundKeyword) {
                // '+', '-', 'kg'가 포함된 라인은 스킵
                if (line.contains("+") || line.contains("-") || line.contains("kg")) {
                    continue;
                }
                // 소수점 값 또는 숫자가 나오면 그 값을 반환
                Matcher matcher = Pattern.compile("\\d+\\.\\d+|\\d+").matcher(line);
                while (matcher.find()) {
                    String value = matcher.group();
                    // 기본 틀에 없는 소수점 값을 찾고, 0.0 제외
                    if (!bmiScale.contains(value) && !value.equals("0.0")) {
                        return value;
                    }
                }
            }

            // BMI 키워드를 찾으면 그 이후부터 값을 추출함
            if (line.contains("BMI")) {
                foundKeyword = true;
            }
        }
        return "N/A";
    }

    // 체지방률 추출 (성별에 따른 기본 틀 사용)
    private static String extractFatPercentage(String[] lines, boolean isFemale) {
        List<String> fatPercentageScale = isFemale ? fatPercentageScaleFemale : fatPercentageScaleMale;
        boolean foundKeyword = false;

        for (String line : lines) {
            if (foundKeyword) {
                // '+', '-', 'kg'가 포함된 라인은 스킵
                if (line.contains("+") || line.contains("-") || line.contains("kg")) {
                    continue;
                }
                // 소수점 값 또는 숫자가 나오면 그 값을 반환
                Matcher matcher = Pattern.compile("\\d+\\.\\d+|\\d+").matcher(line);
                while (matcher.find()) {
                    String value = matcher.group();
                    // 기본 틀에 없는 소수점 값을 찾고, 0.0 제외
                    if (!fatPercentageScale.contains(value) && !value.equals("0.0")) {
                        return value;
                    }
                }
            }

            // 체지방률 키워드를 찾으면 그 이후부터 값을 추출함
            if (line.contains("체지방률")) {
                foundKeyword = true;
            }
        }
        return "N/A";
    }
}
