package com.github.nuclearg.smartgs;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;

public class SmartGSAction extends BaseGenerateAction {
    public SmartGSAction() {
        super(new SmartGSHandler());
    }
}
