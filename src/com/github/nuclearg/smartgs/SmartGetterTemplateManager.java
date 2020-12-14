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
        name = "SmartGetterTemplates",
        storages = {@Storage("smartGetterTemplates.xml")}
)
public class SmartGetterTemplateManager extends TemplatesManager {
    public static SmartGetterTemplateManager getInstance() {
        return ServiceManager.getService(SmartGetterTemplateManager.class);
    }

    @Override
    @NotNull
    public List<TemplateResource> getDefaultTemplates() {
        try {
            return Collections.singletonList(new TemplateResource("Default", readFile("defaultGetterJavadoc.vm", SmartGetterTemplateManager.class), true));
        } catch (IOException ex) {
            throw new TemplateResourceException("Error loading default templates", ex);
        }
    }
}
