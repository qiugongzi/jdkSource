

package com.sun.source.util;

import java.lang.reflect.Method;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler.CompilationTask;

import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;


@jdk.Exported
public abstract class Trees {

    public static Trees instance(CompilationTask task) {
        String taskClassName = task.getClass().getName();
        if (!taskClassName.equals("com.sun.tools.javac.api.JavacTaskImpl")
                && !taskClassName.equals("com.sun.tools.javac.api.BasicJavacTask"))
            throw new IllegalArgumentException();
        return getJavacTrees(CompilationTask.class, task);
    }


    public static Trees instance(ProcessingEnvironment env) {
        if (!env.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment"))
            throw new IllegalArgumentException();
        return getJavacTrees(ProcessingEnvironment.class, env);
    }

    static Trees getJavacTrees(Class<?> argType, Object arg) {
        try {
            ClassLoader cl = arg.getClass().getClassLoader();
            Class<?> c = Class.forName("com.sun.tools.javac.api.JavacTrees", false, cl);
            argType = Class.forName(argType.getName(), false, cl);
            Method m = c.getMethod("instance", new Class<?>[] { argType });
            return (Trees) m.invoke(null, new Object[] { arg });
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }


    public abstract SourcePositions getSourcePositions();


    public abstract Tree getTree(Element element);


    public abstract ClassTree getTree(TypeElement element);


    public abstract MethodTree getTree(ExecutableElement method);


    public abstract Tree getTree(Element e, AnnotationMirror a);


    public abstract Tree getTree(Element e, AnnotationMirror a, AnnotationValue v);


    public abstract TreePath getPath(CompilationUnitTree unit, Tree node);


    public abstract TreePath getPath(Element e);


    public abstract TreePath getPath(Element e, AnnotationMirror a);


    public abstract TreePath getPath(Element e, AnnotationMirror a, AnnotationValue v);


    public abstract Element getElement(TreePath path);


    public abstract TypeMirror getTypeMirror(TreePath path);


    public abstract Scope getScope(TreePath path);


    public abstract String getDocComment(TreePath path);


    public abstract boolean isAccessible(Scope scope, TypeElement type);


    public abstract boolean isAccessible(Scope scope, Element member, DeclaredType type);


    public abstract TypeMirror getOriginalType(ErrorType errorType);


    public abstract void printMessage(Diagnostic.Kind kind, CharSequence msg,
            com.sun.source.tree.Tree t,
            com.sun.source.tree.CompilationUnitTree root);


    public abstract TypeMirror getLub(CatchTree tree);
}
