package com.treeliked.darkme2.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treeliked.darkme2.algorithm.Alus;
import com.treeliked.darkme2.model.stroke.Loc;
import com.treeliked.darkme2.model.stroke.StrokeData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * game controller
 *
 * @author lqs2
 * @date 2018/10/20, Sat
 */
@Slf4j
@Controller
@RequestMapping("/entertainment")
public class RecreationController {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/onestroke", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String playOneStroke(String data, String token) {
        if (StringUtils.isBlank(token) || StringUtils.isBlank(data)) {
            return "-1";
        }
        try {
            StrokeData value = objectMapper.readValue(data, StrokeData.class);
            Stack<Loc> locStack = new Stack<>();
            Alus.strokeCal(locStack, value.getStartX(), value.getStartY(), value.getData());
            if (locStack.size() == 0) {
                return "0";
            }
            List<Loc> steps = new ArrayList<>(locStack);
            return objectMapper.writeValueAsString(steps);
        } catch (IOException e) {
            log.error("", e);
        }
        return "-1";
    }
}

