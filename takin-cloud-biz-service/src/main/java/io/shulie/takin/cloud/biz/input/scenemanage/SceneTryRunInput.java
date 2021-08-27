package io.shulie.takin.cloud.biz.input.scenemanage;

import lombok.Data;

@Data
public class SceneTryRunInput {
    /**
     * 循环次数
     */
    private Integer loopsNum;

    /**
     * 并发数
     */
    private Integer concurrencyNum;

    public SceneTryRunInput(Integer loopsNum,Integer concurrencyNum) {
        this.loopsNum = loopsNum;
        this.concurrencyNum = concurrencyNum;
    }

    public Integer getLoopsNum() {
        return loopsNum;
    }

    public SceneTryRunInput setLoopsNum(Integer loopsNum) {
        this.loopsNum = loopsNum;
        return this;
    }




}
