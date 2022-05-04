# ForgeAnnotationConfig
An annotation-based wrapper library for Minecraft Forge 1.8.9's mod configuration system (the file format and the GUI). This library allows you to create configuration for your mods.

![Planned features](https://github.com/taj-ny/ForgeAnnotationConfig/projects/1)

![Example mod](https://github.com/taj-ny/ForgeAnnotationConfigExample)

# Getting started
A tutorial for setting everything up can be found on the wiki: https://github.com/taj-ny/ForgeAnnotationConfig/wiki/Getting-started.

# Features
- Configuration class serialization/deserialization.
- User input validation.

# Limitations
- Supported types: Boolean, Integer, Double, String, arrays and lists of them. Anything else will need a type adapter.
- No nested arrays/lists.
- Null values need to be implemented manually. See https://github.com/taj-ny/ForgeAnnotationConfig/wiki/Null-values.

# Screenshots
![img1](https://user-images.githubusercontent.com/79316397/166160477-34fcc571-1a52-4719-a0fb-c86a616a094e.png)
![img2](https://user-images.githubusercontent.com/79316397/166160505-bd16415e-73ab-4413-b60d-544f8241ead7.png)
