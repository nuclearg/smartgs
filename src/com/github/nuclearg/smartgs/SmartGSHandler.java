package com.github.nuclearg.smartgs;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.GenerateGetterAndSetterHandler;
import com.intellij.codeInsight.generation.GenerationInfo;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.generate.template.TemplatesManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class SmartGSHandler extends GenerateGetterAndSetterHandler {
    private static final String PLACEHOLDER = "#JAVADOC_PLACEHOLDER#";

    SmartGSHandler() {
        super();
    }

    @Override
    @Nullable
    protected JComponent getHeaderPanel(Project project) {
        JComponent originalPanel = super.getHeaderPanel(project);
        if (originalPanel == null) {
            return null;
        }

        JPanel javadocPanel = new JPanel(new BorderLayout(2, 2));
        javadocPanel.add(getHeaderPanel(project, SmartGetterTemplateManager.getInstance(), "Getter Javadoc:"), "North");
        javadocPanel.add(getHeaderPanel(project, SmartSetterTemplateManager.getInstance(), "Setter Javadoc:"), "South");

        JPanel container = new JPanel(new BorderLayout(2, 2));
        container.add(originalPanel, "North");
        container.add(javadocPanel, "South");
        return container;
    }

    @Override
    protected boolean hasMembers(@NotNull PsiClass psiClass) {
        for (PsiField psiField : psiClass.getFields()) {
            if (this.isGetterOrSetterAvailable(psiField)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GenerationInfo[] generateMemberPrototypes(PsiClass psiClass, ClassMember original) throws IncorrectOperationException {
        if (!(original instanceof PsiFieldMember)) {
            return GenerationInfo.EMPTY_ARRAY;
        }

        PsiFieldMember psiFieldMember = (PsiFieldMember) original;
        PsiField psiField = psiFieldMember.getElement();

        try {
            List<GenerationInfo> methods = new ArrayList<GenerationInfo>();

            GenerationInfo getter = null;
            GenerationInfo setter = null;

            if (this.isGetterAvailable(psiField)) {
                getter = this.buildGetter(psiFieldMember);
            }
            if (this.isSetterAvailable(psiField)) {
                setter = this.buildSetter(psiFieldMember);
            }

            if (getter != null) {
                methods.add(getter);
            }
            if (setter != null) {
                methods.add(setter);
            }

            return methods.toArray(GenerationInfo.EMPTY_ARRAY);
        } catch (Exception ex) {
            return GenerationInfo.EMPTY_ARRAY;
        }
    }

    private boolean isGetterOrSetterAvailable(PsiField psiField) {
        return this.isGetterAvailable(psiField) || this.isSetterAvailable(psiField);
    }

    private boolean isGetterAvailable(PsiField psiField) {
        return this.isFieldNotStatic(psiField);
    }

    private boolean isSetterAvailable(PsiField psiField) {
        return this.isFieldNotStatic(psiField) && psiField.isWritable();
    }

    private boolean isFieldNotStatic(PsiField psiField) {
        return psiField.getModifierList() != null && !psiField.getModifierList().hasExplicitModifier("static");
    }

    private GenerationInfo buildGetter(PsiFieldMember psiFieldMember) {
        GenerationInfo generationInfo = psiFieldMember.generateGetter();
        insertJavaDoc(psiFieldMember.getElement(), generationInfo, SmartGetterTemplateManager.getInstance());
        return generationInfo;
    }

    private GenerationInfo buildSetter(PsiFieldMember psiFieldMember) {
        GenerationInfo generationInfo = psiFieldMember.generateSetter();
        insertJavaDoc(psiFieldMember.getElement(), generationInfo, SmartSetterTemplateManager.getInstance());
        return generationInfo;
    }

    private void insertJavaDoc(PsiField psiField, GenerationInfo generationInfo, TemplatesManager templatesManager) {
        if (generationInfo == null || generationInfo.getPsiMember() == null) {
            return;
        }

        String javadoc;
        if (psiField.getDocComment() == null) {
            javadoc = psiField.getName();
        } else {
            StringBuilder builder = new StringBuilder();
            PsiElement e = psiField.getDocComment().getFirstChild();
            while ((e = e.getNextSibling()) != null) {
                if (!(e instanceof PsiDocToken)) {
                    continue;
                }
                PsiDocToken token = (PsiDocToken) e;
                if (token.getTokenType() == JavaDocTokenType.DOC_COMMENT_DATA) {
                    builder.append(token.getText());
                }
            }
            javadoc = builder.toString();
        }

        String template = templatesManager.getDefaultTemplate().getTemplate();
        javadoc = StringUtil.replace(template, PLACEHOLDER, javadoc.trim());

        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiField.getProject());
        PsiDocComment javadocElement = elementFactory.createDocCommentFromText(javadoc);
        generationInfo.getPsiMember().addBefore(javadocElement, generationInfo.getPsiMember().getFirstChild());
    }
}
