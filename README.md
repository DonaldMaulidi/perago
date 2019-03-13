   ### Perago Diff Service
--------------------

#### Objective
> **The goal of this exercise is to create a library that provides:**

 1. A diff service that:
    - can calculate the differences between two objects and return the resulting "diff".
    - can apply a previously created "diff" against an original object, so that the returned object matches the modified object that was used to calculate the diff.
 2. A diff renderer component that can display a "diff" in a hierarchical, human-readable format

#### Specification

 1. The diff service must implement the DiffEngine interface (see provided source).
 2. The diff renderer must implement the DiffRenderer interface (see provided source).

>>> Your solution must use the above interfaces as provided. `DiffEngine.java` and `DiffRenderer.java` must not be modified.

>>> You are free to implement the details of the `Diff.java` and `DiffException.java` classes as you see fit (within the constraints specified by the requirements below).

#### Detailed Requirements and Examples
>> The diff service and diff renderer must satisfy the below requirements and should be capable of handling the illustrative examples. **Please also provide unit-tests that verify your implementation.**

>> List any assumptions you made that the correctness of your implementation depends on.
It is not necessarily expected that you will be able to satisfy all of the requirements in the allocated time. However, the more complete your implementation, the more favourably it is likely to be scored. For any requirements that you are unable to complete in the allocated time, please explain what made implementing them challenging, and how you could possibly satisfy them if you had more time. 

> **Requirement 1**

> - Diffs must reflect all information that was created/updated/deleted. Information that was not changed must not be reflected in a Diff.
> - A Diff object must not contain a reference to the modified object or a reference to a clone of the modified object.

> **Requirement 2**

> - diffService.calculate() and diffService.apply() must not modify or change the objects that were passed in as their parameters. 

> **Requirement 3**

> - A null original object diffed against non-null modified object must reflect as being “created” in diff.  
> - A non-null original object diffed against null modified object must reflect as being “deleted” in diff.
> - Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified.

> **Requirement 4**

> - Properties of original must in reflected as “updated” if their value differs from that of modified.

> **Requirement 5**

> - Diffs must recursively reflect modifications to all child objects

> **Requirement 6**

> - Properties of original or modified that are collections (arrays, sets, lists, maps etc.) must be taken into consideration when calculating and applying diffs

> **Requirement 7**

> - Cyclic relationships in original or modified should be taken into consideration when calculating and applying diffs

> **Examples**
>> In all examples below, the value of the “diff” argument passed to diffRenderer is assumed to be the output of `diffService.calculate(original,modified)`.

>> For all examples, the result returned from `diffService.apply(original, diff)` must be equal to modified. i.e.
> - If `modified == null` , then `diffService.apply(original, diff) == null`
> - Otherwise, `modified.equals(diffService.apply(original, diff)) == true`

  #### Project Structure
--------------------

My project has three classes and two interfaces. 
> - `Diff` which calculates and applies the diff between two objects.
> - `DiffException` which is an event which may occur during the execution of the diff.
> - `DiffRender` which displays the "diff".  
> - `DiffEngine` is an interface which will be implemented by the `Diff` class.
> - `DiffRenderer` is an interface which will be implemented by the `DiffRender` class. 

> **Code Snippets:**
>>> The `calculate` method can be found in the `Diff` class which implemented the `DiffEngine` interface. In the code below a new object is created of the class which needs to be returned by the `calculate` method, then there's a validation which validates if neither of the objects passed into the method are null, if they're null a `DiffException` is thrown alerting the user that the objects cannot be null, otherwise the method continues executing and validates that the objects are of the same type if they're not another `DiffException` is thrown, if they're of the same type the method continues executing the method. Then it checks if the original object which was passed is null, if it is it then tries to create a new instance from the modified object and if that's successful it then copies the properties of the modified object to the new instance that which was created.

```
public <T extends Serializable> Diff<T> calculate(T original, T modified) throws DiffException {
        Diff<T> newDiffObj = new Diff<>();

        if (isNull(original) && isNull(modified)) {
            throw new DiffException("Objects cannot be null!");
        }

        if (nonNull(original) && nonNull(modified)) {
            if (!original.getClass().equals(modified.getClass())) {
                throw new DiffException("Objects not of the same type!");
            }
        }

        if (isNull(original)) {
            try {
                T newInstance = (T) modified.getClass().newInstance();

                try {
                    BeanUtils.copyProperties(newInstance, modified);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
```

>>> The code below can be found in the `DiffRender` class, it first validates if the diffMethod is equals to `CREATE` if it is, it then gets the `diff.modifiedValue` which was calculated and applied, then it prints out the class's name. The render method then gets the `declaredFields` and loops through them while printing out the field names and their respective values of the diff which are displayed in the console. 
```
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
        }
```
>>> When the `diff.diffMethod` is equals to `CREATE` the out will look like this: 
```
1 Create: Person
1.1 Create: firstName as Fred
1.2 Create: surname as Jones
1.3 Create: friend
1.3.1 Create: firstName as Jim
1.3.2 Create: surname as Brown
1.4 Create: pet
1.4.1 Create: type as Dog
1.4.2 Create: name as Spot
1.5 Create: nickNames as [polly, biff]
```

  #### Testing
--------------------
>>> The Unit Tests can be found in the path below, the Unit Tests test multiple scenarios and will display the output from the render method based on the `diff.diffMethod` they satisfy. 

```
 (YOUR_LOCAL_DRIVE)\(DIRECTORY)\..\..\perago_diff\src\test\java\com\perago\techtest\DiffTest.java
```