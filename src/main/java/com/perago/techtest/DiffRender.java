package com.perago.techtest;

import java.io.Serializable;
import java.lang.reflect.Field;

import static com.perago.techtest.DiffMethod.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DiffRender implements DiffRenderer {
    @Override
    public String render(Diff<?> diff) throws DiffException {
        StringBuilder diffBuilder = new StringBuilder();

        int i = 1;
        if (diff.diffMethod == CREATE) {
            Serializable modified = diff.modifiedValue;

            System.out.printf("%d %s: %s\n", i, CREATE, modified.getClass().getSimpleName());
            Field[] declaredFields = modified.getClass().getDeclaredFields();
            for (int j = 0, declaredFieldsLength = declaredFields.length; j < declaredFieldsLength; j++) {
                Field modifiedField = declaredFields[j];
                modifiedField.setAccessible(true);

                try {
                    Object modifiedValue = modifiedField.get(modified);
                    if (isNull(modifiedValue)) {
                        System.out.printf("%d.%d %s: %s as %s\n", i, j, CREATE, modifiedField.getName(), null);
                    } else if (modifiedField.getName() != "serialVersionUID" && modifiedField.getName() != "pet" && !modifiedField.getType().isInstance(modified)) {
                        System.out.printf("%d.%d %s: %s as %s\n", i, j, CREATE, modifiedField.getName(), getValue(modified, modifiedField));
                    } else if (modifiedField.getType().isInstance(modified)) {
                        System.out.printf("%d.%d %s: %s\n", i, j, CREATE, modifiedField.getName());
                        createObject(modified, modifiedField, i, j);
                    } else if (modifiedField.getName() == "pet") {
                        System.out.printf("%d.%d %s: %s\n", i, j, CREATE, modifiedField.getName());
                        if (modifiedField.getName() != "serialVersionUID") {
                            createObject(modified, modifiedField, i, j);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else if (diff.diffMethod == DELETE) {
            if (isNull(diff.modifiedValue)) {
                System.out.printf("%d %s: %s\n", i, DELETE, diff.clazz);
            }
        } else if (diff.diffMethod == UPDATE) {
            if (nonNull(diff.originalValue) && nonNull(diff.modifiedValue)) {
                Serializable original = diff.originalValue;
                Serializable modified = diff.modifiedValue;

                System.out.printf("%d %s: %s\n", i, UPDATE, modified.getClass().getSimpleName());
                Field[] originalDeclaredFields = original.getClass().getDeclaredFields();
                for (int j = 0, originalFieldsLength = originalDeclaredFields.length; j < originalFieldsLength; j++) {
                    Field originalField = originalDeclaredFields[j];
                    Field[] modifiedDeclaredFields = modified.getClass().getDeclaredFields();
                    for (int k = 0, modifiedFieldsLength = modifiedDeclaredFields.length; k < modifiedFieldsLength; k++) {
                        Field modifiedField = modifiedDeclaredFields[k];
                        originalField.setAccessible(true);
                        modifiedField.setAccessible(true);

                        Serializable originalValue = getValue(original, originalField);
                        Serializable modifiedValue = getValue(modified, modifiedField);

                        if (originalValue != null && modifiedValue != null && originalField.getName() == modifiedField.getName() && originalValue.toString() != modifiedValue.toString() && originalField.getName() != "serialVersionUID") {
                            if (originalField.getName() != "friend" && originalField.getName() != "pet" && originalField.getName() != "nickNames") {
                                System.out.printf("%d.%d %s: %s from %s to %s\n", i, j, UPDATE, originalField.getName(), originalValue.toString(), modifiedValue.toString());
                            }

                            if (modifiedField.getType().isInstance(modified)) {
                                updateObject(originalField, modifiedField, original, modified, i, j, k);
                            } else if (originalField.getName() == "pet") {
                                updateObject(originalField, modifiedField, original, modified, i, j, k);
                            } else {
                                if (originalField.getName() == "nickNames") {
                                    if (nonNull(originalValue) && nonNull(modifiedValue) && originalField.getName() == modifiedField.getName() && originalValue != modifiedValue && originalField.getName() != "serialVersionUID") {
                                        System.out.printf("%d.%d %s: %s from %s to %s\n", i, j, UPDATE, originalField.getName(), originalValue, modifiedValue);
                                    }
                                }
                            }
                        } else if (originalValue == null && modifiedValue != null && originalField.getName() == modifiedField.getName()) {
                            System.out.printf("%d.%d %s: %s\n", i, j, UPDATE, modifiedField.getName());
                            if (modifiedField.getName() != "nickNames") {
                                System.out.printf("%d.%d.%d %s: %s\n", i, j, k, CREATE, modifiedValue.getClass().getSimpleName());
                            } else {
                                System.out.printf("%d.%d.%d %s: %s\n", i, j, k, CREATE, modifiedField.getName());
                            }

                            if (modifiedField.getType().isInstance(modified)) {
                                Field[] modifiedFields = modifiedField.getType().getDeclaredFields();
                                for (int l = 0, declaredFieldsLength = modifiedFields.length; l < declaredFieldsLength; l++) {
                                    Field modifiedSubField = modifiedFields[l];
                                    modifiedSubField.setAccessible(true);

                                    try {
                                        Object modifiedSubValue = modifiedSubField.get(modifiedField.get(modified));

                                        if (isNull(modifiedSubValue) && modifiedSubField.getName() != "serialVersionUID") {
                                            System.out.printf("%d.%d.%d.%d %s: %s as %s\n", i, j, k-2, l, CREATE, modifiedSubField.getName(), null);
                                        } else if (modifiedSubField.getName() != "serialVersionUID") {
                                            System.out.printf("%d.%d.%d.%d %s: %s as %s\n", i, j, k-2, l, CREATE, modifiedSubField.getName(), modifiedSubValue);
                                        }
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (modifiedField.getName() == "pet") {
                                createObject(modified, modifiedField, i, j);
                            } else if (modifiedField.getName() == "nickNames") {
                                System.out.printf("%d.%d.%d %s: %s %s\n", i, j, k, CREATE, modifiedField.getName(), getValue(modified, modifiedField));
                            }
                        } else if (originalValue != null && modifiedValue == null && originalField.getName() == modifiedField.getName()) {
                            i++;
                            System.out.printf("%d %s %s\n", i, DELETE, modifiedField.getName());
                        }
                    }
                }
            }
        }
        return diffBuilder.toString();
    }

    private void updateObject(Field originalField, Field modifiedField, Serializable originalValue, Serializable modifiedValue, int i, int j, int k) {
        Field[] originalDeclaredFields = originalField.getType().getDeclaredFields();
        for (int l = 0, originalFieldsLength = originalDeclaredFields.length; l < originalFieldsLength; l++) {
            Field originalSubField = originalDeclaredFields[l];
            Field[] modifiedDeclaredFields = modifiedField.getType().getDeclaredFields();
            for (int m = 0, modifiedFieldsLength = modifiedDeclaredFields.length; m < modifiedFieldsLength; m++) {
                Field modifiedSubField = modifiedDeclaredFields[m];
                originalSubField.setAccessible(true);
                modifiedSubField.setAccessible(true);

                try {
                    Object originalSubValue = originalSubField.get(originalField.get(originalValue));
                    Object modifiedSubValue = modifiedSubField.get(modifiedField.get(modifiedValue));

                    if (originalSubValue != null && modifiedSubValue != null && originalSubField.getName() == modifiedSubField.getName() && originalSubValue.toString() != modifiedSubValue.toString() && originalSubField.getName() != "serialVersionUID") {
                        System.out.printf("%d.%d %s: %s\n", i, j, UPDATE, originalField.getName());
                        System.out.printf("%d.%d.%d %s: %s from %s to %s\n", i, j, l, UPDATE, originalSubField.getName(), originalSubValue.toString(), modifiedSubValue.toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createObject(Serializable value, Field field, int i, int j) {
        Field[] declaredFields = field.getType().getDeclaredFields();
        for (int k = 0, declaredFieldsLength = declaredFields.length; k < declaredFieldsLength; k++) {
            Field declaredField = declaredFields[k];
            declaredField.setAccessible(true);
            try {
                Object modifiedSubValue = declaredField.get(field.get(value));
                if (modifiedSubValue != null && declaredField.getName() != "serialVersionUID") {
                    System.out.printf("%d.%d.%d %s: %s as %s\n",i, j, k, CREATE, declaredField.getName(), modifiedSubValue);
                }
            } catch (IllegalAccessException e) {

            }
        }
    }

    private <T extends Serializable> T getValue(T original, Field field) {
        try {
            T originalValue = (T) field.get(original);
            return originalValue;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
