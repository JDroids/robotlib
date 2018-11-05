RobotLib
==

[![Build Status](https://travis-ci.org/JDroids/robotlib.svg?branch=master)](https://travis-ci.org/JDroids/robotlib)

What is RobotLib?
--
RobotLib is a library for programming FTC robots. It's central feature is a 
template for command-based design pattern. It seperates robot code into 2 parts.
The main two parts are commands and subsystems. Subsystems are just parts 
of the robot, like a claw, a drivetrain, etc. Commands make subsystems perform 
specific actions.

Installation
--
1. Clone [this](https://github.com/JDroids/robotlib) repository to some known location.
2. Open Android Studio to your ftc_app.
3. Press File -> New -> Import Module.
4. Select wherever you cloned this repository. (You may get an error; this is okay, ignore it.)
5. Press Finish.
6. Then modify your project `build.gradle` to look like this.
```
buildscript {
    ext.kotlin_version = '1.2.71' //this line
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.2.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version" //this line
    }
}
//The lines after this
allprojects {
    repositories {
        google()
        jcenter()
    }
}
```
7. Right click the TeamCode module in the Project Browser. Press Open Module Settings.
8. Go to the Dependencies tab.
9. Press the plus in the top right, and select Module Dependency.
10. Then select `:robotlib`, and press OK twice.
11. You should have RobotLib installed! If you have any questions, ask in the programming channel of the [FTC Discord](https://discord.gg/8v3cbkj)!

Documentation
--
The documentation for RobotLib is written in the [KDoc](https://kotlinlang.org/docs/reference/kotlin-doc.html) format and is compiled into github flavored markdown with [Dokka](https://github.com/Kotlin/dokka). The documentation can be found [here](https://jdroids.github.io/robotlib/docs/robotlib/).

Samples
--
The samples for robotlib can be found [here](https://github.com/JDroids/robotlib-examples).
