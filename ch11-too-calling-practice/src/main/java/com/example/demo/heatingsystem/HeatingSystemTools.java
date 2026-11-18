package com.example.demo.heatingsystem;

import java.util.Random;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HeatingSystemTools {
    @Tool(description = "타겟온도까지 난방시스템을 가동하는 도구 이다.")
    public String startHeatingSystem(
        @ToolParam(description="사용자가 원하는 온도") int targetTemperature, ToolContext ToolContext) {
        String controlKey = (String) ToolContext.getContext().get("controlKey");

        if (controlKey.equals("12345")) {
            log.info("{}도까지 난방을 시작한다.", targetTemperature);
            return "success";
        } else {
            log.info("제어키가 유효하지않아 난방시스템 시작불가");
            return "falilure";
        }

    }

    @Tool(description = "난방 시스템을 중지하는 도구 이다.")
    public String stopHeatingSystem(ToolContext ToolContext) {
        String controlKey = (String) ToolContext.getContext().get("controlKey");

        if (controlKey.equals("12345")) {
            log.info("난방을 중지합니다.");
            return "success";
        } else {
            log.info("제어키가 유효하지않아 난방시스템 중지불가");
            return "falilure";
        }
    }

    @Tool(description = "현재 온도를 제공하는 도구이다.")
    public int getTemperature() {
        Random random = new Random();
        int temperature = random.nextInt(13) + 18; // 18부터 30도사이 임의 온도가 도출
        log.info("실내 온도:" + temperature);
        return temperature;
    }
}
