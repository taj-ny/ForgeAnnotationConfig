# ForgeAnnotationConfig ![](https://img.shields.io/jitpack/v/github/taj-ny/ForgeAnnotationConfig) ![](https://jitpack.io/v/taj-ny/ForgeAnnotationConfig/month.svg) ![](https://jitpack.io/v/taj-ny/ForgeAnnotationConfig/week.svg)
An annotation-based wrapper library for Minecraft Forge 1.8.9's mod configuration system (the file format and the GUI). This library allows you to create configuration for your mods.

![Planned features](https://github.com/taj-ny/ForgeAnnotationConfig/projects/1)

![Example mod](https://github.com/taj-ny/ForgeAnnotationConfigExample)

# Installation
Add the JitPack repository to your build.gradle file if you haven't.
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```
In order to use the library you will need to include it in the jar. If you use ![ForgeTemplate](https://github.com/asbyth/ForgeTemplate) all you have to do is the following:
```groovy
dependencies {
    include 'com.github.taj-ny:ForgeAnnotationConfig:v1.0.0-alpha'
}
```

# Features
- Configuration class serialization/deserialization.
- User input validation.

# Limitations
- Supported types: Boolean, Integer, Double, String, arrays and lists of supported types. In order to add support for a specific type you need to create a TypeAdapter.
- No nested arrays/lists.

# Screenshots
![img1](https://user-images.githubusercontent.com/79316397/166160477-34fcc571-1a52-4719-a0fb-c86a616a094e.png)
![img2](https://user-images.githubusercontent.com/79316397/166160505-bd16415e-73ab-4413-b60d-544f8241ead7.png)