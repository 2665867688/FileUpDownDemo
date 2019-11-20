package com.yue.fileupdown.apkupdate;

/**
 * @author shimy
 * @create 2019/11/19 11:26
 * @desc 检查版本是否更新的规则
 */
public interface CheckVersionRule {
    /**
     * @return true:需要更新 false:不需要更新
     */
    boolean checkVersion();
}
