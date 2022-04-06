# Reflection

Utility library to perform illegal reflective operation in Java under the Jigsaw module system

### Example

```java
var field = SomeClass.class.getDeclaredField("privateField"); // A field in a module that may or may not be marked as open
Reflection.open(field); // Opens the field
var value = Reflection.open(field).get(null); // Get the field value
```