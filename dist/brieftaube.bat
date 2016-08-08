@echo OFF

:: Select the look-and-feel you like best ...
:: or enter another class name of a PLAF
::    windows (default)
::    metal
::    motif
set laf=windows

:: For Metal, you can choose between two themes ...
::    steel
::    ocean
:: otherwise, keep the line empty after the equals sign (=)
set theme=

:: Don't care about the following lines ...


if %laf%~==metal~   set laf=javax.swing.plaf.metal.MetalLookAndFeel
if %laf%~==windows~ set laf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
if %laf%~==~        set laf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
if %laf%~==motif~   set laf=com.sun.java.swing.plaf.motif.MotifLookAndFeel

if %theme%~==steel~   goto switch
if %theme%~==ocean~   goto switch
if %theme%~==~        goto exec
echo Brieftaube: Unknown theme!
set theme=
goto exec

:switch
set theme=-Dswing.metalTheme=%theme%
goto exec

:exec
start javaw -Dswing.defaultlaf=%laf% %theme% -jar Brieftaube.jar
goto EOF

:EOF
