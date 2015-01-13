-injars      MAINCLASSJAR
-injars      SCALAHOME/lib/scala-library.jar(!META-INF/MANIFEST.MF)
-injars      SCALAHOME/lib/scala-swing.jar(!META-INF/MANIFEST.MF)
-injars      SCALAHOME/lib/scala-actors.jar(!META-INF/MANIFEST.MF)
-outjars     dist/MAINCLASSNAME.jar
-libraryjars <java.home>/lib/rt.jar 

-dontwarn scala.**

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-keep class * implements org.xml.sax.EntityResolver

-keepclassmembers class * {
    ** MODULE$;
}

-keepclassmembers class  scala.concurrent.forkjoin.ForkJoinPool {
    ** ctl;
    ** parkBlocker;
}

-keepclassmembers class scala.concurrent.forkjoin.ForkJoinPool$WorkQueue {
    ** runState;
}

-keepclassmembers class scala.concurrent.forkjoin.LinkedTransferQueue {
    ** head;
    ** tail;
    ** sweepVotes;
}

-keepclassmembers class scala.concurrent.forkjoin.LinkedTransferQueue$Node {
    ** item;
    ** next;
    ** waiter;
}

-dontobfuscate
-dontoptimize
-keepdirectories resources
-target 1.6
