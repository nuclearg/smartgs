package com.github.nuclearg.smartgs;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.generate.exception.TemplateResourceException;
import org.jetbrains.java.generate.template.TemplateResource;
import org.jetbrains.java.generate.template.TemplatesManager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@State(
        name = "SmartSetterTemplates",
        storages = {@Storage("smartSetterTemplates.xml")}
)
public class SmartSetterTemplateManager extends TemplatesManager {
    public static SmartSetterTemplateManager getInstance() {
        return ServiceManager.getService(SmartSetterTemplateManager.class);
    }

    @Override
    @NotNull
    public List<TemplateResource> getDefaultTemplates() {
        try {
            return Collections.singletonList(new TemplateResource("Default", readFile("defaultSetterJavadoc.vm", SmartSetterTemplateManager.class), true));
        } catch (IOException ex) {
            throw new TemplateResourceException("Error loading default templates", ex);
        }
    }
}
