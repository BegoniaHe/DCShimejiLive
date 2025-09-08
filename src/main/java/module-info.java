module com.group_finity.mascot {
    requires transitive java.desktop;
    requires java.management;
    requires java.logging;
    requires java.scripting;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires AbsoluteLayout.RELEASE270;
    requires org.mozilla.rhino;
    requires com.formdev.flatlaf;
    requires com.formdev.flatlaf.intellijthemes;

    exports com.group_finity.mascot;
    exports com.group_finity.mascot.action;
    exports com.group_finity.mascot.animation;
    exports com.group_finity.mascot.animationeditor;
    exports com.group_finity.mascot.animationeditor.model;
    exports com.group_finity.mascot.animationeditor.ui;
    exports com.group_finity.mascot.animationeditor.util;
    exports com.group_finity.mascot.behavior;
    exports com.group_finity.mascot.config;
    exports com.group_finity.mascot.environment;
    exports com.group_finity.mascot.exception;
    exports com.group_finity.mascot.hotspot;
    exports com.group_finity.mascot.image;
    exports com.group_finity.mascot.imagesetchooser;
    exports com.group_finity.mascot.menu;
    exports com.group_finity.mascot.script;
    exports com.group_finity.mascot.sound;
    exports com.group_finity.mascot.win;
    exports com.group_finity.mascot.win.jna;
}
