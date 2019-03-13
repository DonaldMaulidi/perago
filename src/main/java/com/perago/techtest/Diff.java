package com.perago.techtest;

import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.perago.techtest.DiffMethod.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * The object representing a diff.
 * Implement this class as you see fit. 
 *
 */

enum DiffMethod {
    CREATE, DELETE, UPDATE;
}

public class Diff<T extends Serializable> implements DiffEngine {

    DiffRender diffRender = new DiffRender();
    protected DiffMethod diffMethod;
    protected T originalValue;
    protected T modifiedValue;
    protected String clazz;
    protected List<Diff> diffList = new ArrayList<Diff>();
    protected T newInstanceHolder;

    public Diff() {
    }

    public Diff(DiffMethod diffMethod, T originalValue,  T modifiedValue, String clazz) {
        this.diffMethod = diffMethod;
        this.originalValue = originalValue;
        this.modifiedValue = modifiedValue;
        this.clazz = clazz;
    }

    public void setNewInstanceHolder(T newInstanceHolder){
        this.newInstanceHolder = newInstanceHolder;
    }

    public T getNewInstanceHolder() {
        return newInstanceHolder;
    }

    public <T extends Serializable> T apply(T original, Diff<?> diff) throws DiffException {
        if (isNull(original)) {
            Serializable holder = diff.getNewInstanceHolder();
            return (T) holder;
        } else if (isNull(diff)) {
            return null;
        } else if (nonNull(original) && nonNull(diff)) {
            Serializable holder = diff.getNewInstanceHolder();

            return (T) holder;
        }
        return null;
    }

    public <T extends Serializable> Diff<T> calculate(T original, T modified) throws DiffException {
        Diff<T> newDiffObj = new Diff<>();

        if (isNull(original)) {
            try {
                T newInstance = (T) modified.getClass().newInstance();

                try {
                    BeanUtils.copyProperties(newInstance, modified);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                newDiffObj.setNewInstanceHolder(newInstance);
                renderDiff(original, modified, newDiffObj, CREATE);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (isNull(modified)) {
            newDiffObj = null;
            renderDiff(original, modified, newDiffObj, DELETE);
        } else if (nonNull(original) && nonNull(modified)) {
            try {
                T newInstance = (T) modified.getClass().newInstance();
                BeanUtils.copyProperties(newInstance, modified);
                newDiffObj.setNewInstanceHolder(newInstance);
                renderDiff(original, modified, newDiffObj, UPDATE);

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return newDiffObj;
    }

    private <T extends Serializable> void renderDiff(T original, T modified, Diff<T> newDiffObj, DiffMethod create) throws DiffException {
        T apply = apply(original, newDiffObj);

        if (isNull(modified)) {
            diffList.add(new Diff<T>(create, original, apply, original.getClass().getSimpleName()));
        } else {
            diffList.add(new Diff<T>(create, original, apply, modified.getClass().getSimpleName()));
        }


        diffList.stream().forEach(diff -> {
            try {
                diffRender.render(diff);
            } catch (DiffException e) {
                e.printStackTrace();
            }
        });
    }
}
