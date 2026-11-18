
package com.example.demo.datetime;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 현재 날짜와 시간을 조회하는 도구를 제공한다.
 * Spring 빈으로 등록되어 서비스에서 주입받아 사용할 수 있다.
 */
@Component
@Slf4j
public class DateTimeTools {

    @Tool(description = "호출 시점의 현재 날짜와 시간을 서버의 기본 시간대 기준으로 조회합니다. "
            + "오늘 날짜, 현재 시각, 현재 연도·월·일 또는 요일을 판단하기 위한 기준 시간이 필요할 때 사용합니다. "
            + "결과는 yyyy-MM-dd HH:mm:ss 형식의 문자열이며, 예를 들면 2026-11-18 14:30:05입니다. "
            + "시간은 24시간제로 표시되며 시간대 정보와 요일은 결과에 포함되지 않습니다.")
    public String getCurrentDateTime() {
        log.info("시간 툴 실행");
        String currenttime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info(currenttime);
        return currenttime;

    }

    @Tool(description = "사용자가 특정 시각에 알람을 설정해 달라고 요청할 때 사용하는 실습용 도구입니다. "
            + "예를 들어 2026년 11월 19일 오전 7시 30분은 2026-11-19 07:30:00으로 전달합니다. "
            + "'10분 뒤', '내일 아침 7시' 같은 상대적인 요청은 getCurrentDateTime 도구로 현재 시각을 확인한 뒤 "
            + "정확한 날짜와 시간으로 계산하여 전달합니다. ")
    public void setAlram(
            @ToolParam(description = "time에는 서버의 기본 시간대 기준으로 알람을 설정할 날짜와 시간을 yyyy-MM-dd HH:mm:ss 형식의 24시간제 문자열로 전달") String time) {
        log.info("알람 설정 시간:" + time);

    }
}
