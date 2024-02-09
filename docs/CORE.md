# Core Specification
## Module
A module is a collection of symbols like functions, constants or classes, and exports.

```java
class ModuleContext {
    Map<String, ObjectClass> classes;
    Map<String, Function> functions;
    Map<String, Value> constants;
    Map<String, Value> exports;

    // "Value" is a runtime object
}
```

Each class or function have its own constants defined in the module, so it is not possible to have function name and class name be the same. A symbol can be exported under multiple different names, and a single exported name can only reference a single constant.

```java
void addFunction(String name, Function function) {
    if (constants.containsKey(name)) throw new ModuleException();
    functions.put(name, function);
    constants.put(name, new FunctionValue(function));
}

// void addClass()...

void addExport(String target, String exportAs) {
    if (exports.containsKey(exportAs)) throw new ModuleException();
    exports.put(exportAs, contants.get(target));
}
```

Importing from other module by adding exported symbols from other module to current module:

```java
void importFrom(ModuleContext module) {
    module.exports.forEach((name, value) -> {
        if (value instanceof ObjectClassValue ocv) addClass(name, ocv.clazz());
        if (value instanceof FunctionValue fv) addFunction(name, fv.function());
        addConstant(name, value);
    });
}
```