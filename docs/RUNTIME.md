# Runtime Implementation Docs
## Table of Contents
- [Auto casting](#auto-casting)
    + [Primitives](#cast-rule-for-primitives)
    + [Optionals](#cast-rule-for-optionals)
    + [Arrays](#cast-rule-for-arrays)
    + [Objects](#cast-rule-for-objects)
- [Context and Execution](#context-and-execution)
    + [Module context](#module-context)
    + [Local context](#local-context)
- [Default values](#default-values)
    + [Primitives](#default-primitives)
    + [Optionals](#default-optionals)
    + [Arrays](#default-arrays)
    + [Objects](#default-objects)

## Auto casting
Value can be auto casted when assigning new value or calling functions. If the value can't be auto casted, it will throws
an error, telling user to try explict casting.

### Cast rule for primitives
Legends: Column: Cast from type - Row: Cast to type - Keep: Keep the value, don't cast it - Explict: Explict casting required - n/a: Not available

| Type   | `any`  |  `i8`  | `i16`  | `i32`  | `i64`  | `f32`  | `f64`  |
|--------|--------|--------|--------|--------|--------|--------|--------|
| `void` |  n/a   |  n/a   |  n/a   |  n/a   |  n/a   |  n/a   |  n/a   |
| `any`  |  Keep  | Explict| Explict| Explict| Explict| Explict| Explict|
|  `i8`  |  Keep  |  Keep  |  Cast  |  Cast  |  Cast  |  Cast  |  Cast  |
| `i16`  |  Keep  | Explict|  Keep  |  Cast  |  Cast  |  Cast  |  Cast  |
| `i32`  |  Keep  | Explict| Explict|  Keep  |  Cast  |  Cast  |  Cast  |
| `i64`  |  Keep  | Explict| Explict| Explict|  Keep  |  Cast  |  Cast  |
| `f32`  |  Keep  | Explict| Explict| Explict| Explict|  Keep  |  Cast  |
| `f64`  |  Keep  | Explict| Explict| Explict| Explict| Explict|  Keep  |

### Cast rule for optionals
- If the value is `empty`...
    + ...and the target type is `optional` then check the wrapped type of both optionals to see if casting is needed (keep, cast or explict), then pass `empty`.
    + ...and the target type is not `optional` then throw runtime error.
- If the value is `present`...
    + ...and the target type is `optional` then check the wrapped type of both optionals to see if casting is needed, convert the wrapped value and pass that value as `present`.
    + ...and the target type is not `optional` then check the wrapped type against the target type to see if casting is needed, then pass the converted and wrapped value.
- If the value is not `optional`...
    + ...and the target type is `optional` then check the wrapped type against the source type to see if casting is needed, then pass the value as `present`.
    + ...and the target type is not `optional` then perform other auto casting rules.

### Cast rule for arrays
_TODO_

### Cast rule for objects
- If target type is superclass of source type, pass the value as-is.
- Otherwise, throw runtime error.

## Context and execution
### Module context
Module context is the top-level context where its content only accessible within module's functions or through exported symbols. Module context consists of the following parts:
- Collection of defined classes (including system classes + imported).
- Collection of defined functions (including system classes + imported).
- Exported symbols.

Importing from other module context by collecting exported symbols and assign them to collection of defined classes and functions:

```java
// Pseudo Java code
module.getClasses().addAll(importing.getClasses());
module.getFunctions().addAll(importing.getFunctions());
```

### Local context
Local context is a child of module context: it can access module's classes and functions, but it also holds local variables. The layout of local context can look like this:

```java
class LocalContext {
    ModuleContext module;

    // Local
    Map<String, Value> locals;
}

// Getting local variable's value
LocalContext local;
PropertyGetExpression e;
return local.locas.get(e.name());
```

Since local context only being used in functions, the local context can have a more tightly-packed structure like this:

```java
class LocalContext {
    ModuleContext module;

    // Local
    byte[] localBytes;
    short[] localShorts;
    int[] localInts;
    long[] localLongs;
    float[] localFloats;
    double[] localDoubles;
    Value[] localComplex;
}

// Getting local variable's value
LocalContext local;
PropertyGetExpression e;

// Our expression is compiled into LocalGetFloatExpr during runtime's compile phase
LocalGetFloatExpr compiled;
return compiled.getAsFloatValue(null, local); // (thisObject, localContext) => Value
```

The above structure is more efficient in memory and maybe faster than previous approach, but at a cost of compiling the function in runtime.

## Default values
### Default primitives
|  Type | Value |
|-------|-------|
| `any` | ERROR |
| `i8`  | `0B`  |
| `i16` | `0S`  |
| `i32` | `0I`  |
| `i64` | `0L`  |
| `f32` | `0F`  |
| `f64` | `0D`  |

- `any` throws error because the default value of it is undefined. It can be `i8`, but it can also be `f64` or even `string`, which means its default value must be explictly defined in class field 
declaration and local variable declaration.

### Default optionals
The default for all optionals is `empty`.

### Default arrays
The default for all arrays is an array of no elements.

### Default objects
The default for all objects is an object with its field initialized to default value. If the field is `any`, its default value must be explictly defined in class definition.