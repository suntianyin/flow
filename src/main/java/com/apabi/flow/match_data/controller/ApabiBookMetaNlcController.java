package com.apabi.flow.match_data.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author pipi
 * @Date 2019-2-14 17:50
 **/
@RequestMapping("matchData")
@Controller
public class ApabiBookMetaNlcController {

    @RequestMapping("firstRoundClean")
    public String firstRoundClean(){
        return "success";
    }
}
