
https://google.github.io/styleguide/javaguide.html


## Install google-formatter to eclipse

https://github.com/google/google-java-format?tab=readme-ov-file#eclipse

The latest version of the google-java-format Eclipse plugin can be downloaded from the [releases page](https://github.com/google/google-java-format/releases). Drop it into the Eclipse [drop-ins folder](https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fmisc%2Fp2_dropins_format.html) to activate the plugin.

The plugin adds a google-java-format formatter implementation that can be configured in `Window > Preferences > Java > Code Style > Formatter > Formatter Implementation`.

## change eclipse.ini

https://github.com/google/google-java-format/issues/991

add the following lines to your `eclipse.ini` and restart Eclipse IDE

--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED

## Change Organize imports settings

https://stackoverflow.com/questions/64190170/getting-eclipse-java-organize-imports-to-work-with-google-checkstyle

Adding a single "*" static import group in the Organize Imports preference and placing it above the regular import group should keep the static imports and imports separated by one line.

The Organize Imports preference can be found at `Window > Preferences > Java > Code Style > Organize Imports`.