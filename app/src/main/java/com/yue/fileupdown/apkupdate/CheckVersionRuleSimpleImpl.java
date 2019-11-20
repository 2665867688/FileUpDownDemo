package com.yue.fileupdown.apkupdate;

/**
 * @author shimy
 * @create 2019/11/19 11:27
 * @desc 默认提供的是否升级的简单的检测规则
 */
public class CheckVersionRuleSimpleImpl implements CheckVersionRule {

    @Override
    public boolean checkVersion() {
        return false;
    }
}
