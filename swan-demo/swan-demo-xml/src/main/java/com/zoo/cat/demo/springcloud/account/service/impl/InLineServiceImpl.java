package com.zoo.cat.demo.springcloud.account.service.impl;

import org.zoo.cat.annotation.Cat;
import org.zoo.cat.annotation.TransTypeEnum;

import com.zoo.cat.demo.springcloud.account.service.InLineService;

import org.springframework.stereotype.Component;

/**
 * The type In line service.
 *
 * @author dzc
 */
@Component
public class InLineServiceImpl implements InLineService {

    @Override
    @Cat(confirmMethod = "confirm", cancelMethod = "cancel", pattern = TransTypeEnum.TCC)
    public void test() {
        System.out.println("执行inline try......");
    }

    /**
     * Confrim.
     */
    public void confirm() {
        System.out.println("执行inline confirm......");
    }

    /**
     * Cancel.
     */
    public void cancel() {
        System.out.println("执行inline cancel......");
    }
}
