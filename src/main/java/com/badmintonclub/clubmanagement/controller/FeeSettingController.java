package com.badmintonclub.clubmanagement.controller;

import com.badmintonclub.clubmanagement.entity.FeeSetting;
import com.badmintonclub.clubmanagement.service.FeeSettingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FeeSettingController {

    @Autowired
    private FeeSettingService feeSettingService;

    @GetMapping("/fee-settings")
    public String showFeeSettingForm(Model model) {

        FeeSetting setting = feeSettingService.getCurrentSetting();

        model.addAttribute("setting", setting);

        return "fee-settings/form";
    }

    @PostMapping("/fee-settings/save")
    public String saveFeeSetting(
            @ModelAttribute FeeSetting setting) {

        feeSettingService.saveSetting(setting);

        return "redirect:/fee-settings?success";
    }
}