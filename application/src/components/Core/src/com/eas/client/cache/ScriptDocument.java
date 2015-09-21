/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.script.AmdPropertiesAnnotationsMiner;
import com.eas.script.JsDoc;
import com.eas.script.GMDPropertiesAnnotationsMiner;
import com.eas.script.Scripts;
import java.util.*;
import jdk.nashorn.internal.ir.CallNode;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.runtime.Source;

/**
 * Implementation service support for script related tasks. It is cached and
 * therefor purged from time to time. Don't store references on it in your
 * client code!
 *
 * @author pk, mg refactoring
 */
public class ScriptDocument {

    private List<JsDoc.Tag> moduleAnnotations;
    /**
     * User roles that have access to all module's functions, if empty all users
     * allowed
     */
    private Set<String> moduleAllowedRoles = new HashSet<>();
    /**
     * Roles that have access to specific properties, if empty all users are
     * allowed
     */
    private Map<String, Set<String>> propertyAllowedRoles = new HashMap<>();

    private final Map<String, Set<JsDoc.Tag>> propertyAnnotations = new HashMap<>();
    /**
     * Functions that may be accessed over network via RPC
     */
    private final Set<String> functionProperties = new HashSet<>();

    protected ScriptDocument() {
        super();
    }

    public static ScriptDocument parse(String aSource, String aName) {
        ScriptDocument doc = new ScriptDocument();
        doc.readScriptAnnotations(aSource, aName);
        return doc;
    }

    public Set<String> getModuleAllowedRoles() {
        return moduleAllowedRoles;
    }

    public void setModuleAllowedRoles(Set<String> aModuleAllowedRoles) {
        moduleAllowedRoles = aModuleAllowedRoles;
    }

    public Map<String, Set<String>> getPropertyAllowedRoles() {
        return propertyAllowedRoles;
    }

    public void setPropertyAllowedRoles(Map<String, Set<String>> aAllowedRoles) {
        propertyAllowedRoles = aAllowedRoles;
    }

    public List<JsDoc.Tag> getModuleAnnotations() {
        return moduleAnnotations != null ? Collections.unmodifiableList(moduleAnnotations) : null;
    }

    public Map<String, Set<JsDoc.Tag>> getPropertyAnnotations() {
        return Collections.unmodifiableMap(propertyAnnotations);
    }

    public boolean hasModuleAnnotation(String anAnnotation) {
        return moduleAnnotations != null && moduleAnnotations.stream().anyMatch((JsDoc.Tag aTag) -> {
            return aTag.getName().equalsIgnoreCase(anAnnotation);
        });
    }

    public JsDoc.Tag getModuleAnnotation(String anAnnotation) {
        return moduleAnnotations != null ? moduleAnnotations.stream().filter((JsDoc.Tag aTag) -> {
            return aTag.getName().equalsIgnoreCase(anAnnotation);
        }).findAny().get() : null;
    }

    public Set<String> getFunctionProperties() {
        return functionProperties;
    }

    /**
     * Reads script annotations. Annotations, accompanied with
     *
     * @name annotation are the 'module annotations'. Annotations, followed by
     * any property assignment are the 'property annotations'. Property
     * annotations will be taken into account while accessing through modules.
     * @param aSource
     */
    private void readScriptAnnotations(String aSource, String aName) {
        assert aSource != null : "JavaScript source can't be null";
        moduleAnnotations = new ArrayList<>();
        propertyAllowedRoles.clear();
        Source source = Source.sourceFor(aName, aSource);
        FunctionNode ast = Scripts.parseJs(aSource);
        // For AMD modules
        ast.accept(new AmdPropertiesAnnotationsMiner(source) {

            @Override
            protected void commentedDefineCall(CallNode aCallNode, JsDoc aJsDoc) {
                aJsDoc.parseAnnotations();
                aJsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                    moduleAnnotations.add(tag);
                    if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                        tag.getParams().stream().forEach((role) -> {
                            moduleAllowedRoles.add(role);
                        });
                    }
                });
            }

            @Override
            protected void commentedProperty(String aPropertyName, String aComment) {
                readPropertyRoles(aPropertyName, aComment);
            }

            @Override
            protected void property(String aPropertyName, Expression aValue) {
                if (!aPropertyName.contains(".") && aValue instanceof FunctionNode) {
                    functionProperties.add(aPropertyName);
                }
            }
        });
        // For global scope modules
        Set<String> aliases = Scripts.getThisAliases(PlatypusFilesSupport.extractModuleConstructor(ast, aName));
        if (aliases != null) {
            ast.accept(new GMDPropertiesAnnotationsMiner(source, aliases) {

                @Override
                protected void commentedFunction(FunctionNode aFunction, String aComment) {
                    if (scopeLevel == TOP_CONSTRUCTORS_SCOPE_LEVEL) {
                        JsDoc jsDoc = new JsDoc(aComment);
                        jsDoc.parseAnnotations();
                        jsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                            moduleAnnotations.add(tag);
                            if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                                tag.getParams().stream().forEach((role) -> {
                                    moduleAllowedRoles.add(role);
                                });
                            }
                        });
                    }
                }

                @Override
                protected void commentedProperty(String aPropertyName, String aComment) {
                    readPropertyRoles(aPropertyName, aComment);
                }

                @Override
                protected void property(String aPropertyName, Expression aValue) {
                    if (!aPropertyName.contains(".") && aValue instanceof FunctionNode) {
                        functionProperties.add(aPropertyName);
                    }
                }

            });
        }
    }

    private void readPropertyRoles(String aPropertyName, String aJsDocBody) {
        if (aJsDocBody != null) {
            JsDoc jsDoc = new JsDoc(aJsDocBody);
            jsDoc.parseAnnotations();
            jsDoc.getAnnotations().stream().forEach((JsDoc.Tag tag) -> {
                Set<JsDoc.Tag> tags = propertyAnnotations.get(aPropertyName);
                if (tags == null) {
                    tags = new HashSet<>();
                    propertyAnnotations.put(aPropertyName, tags);
                }
                tags.add(tag);
                if (tag.getName().equalsIgnoreCase(JsDoc.Tag.ROLES_ALLOWED_TAG)) {
                    Set<String> roles = propertyAllowedRoles.get(aPropertyName);
                    if (roles == null) {
                        roles = new HashSet<>();
                    }
                    for (String role : tag.getParams()) {
                        roles.add(role);
                    }
                    propertyAllowedRoles.put(aPropertyName, roles);
                }
            });
        }
    }
}
