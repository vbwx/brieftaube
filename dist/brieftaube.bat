@echo OFF

:: Select the look-and-feel you like best ...
:: or enter another class name of a PLAF
::    metal
::    windows
::    motif
::    tiny    (default)
set laf=tiny

:: Now, select a nice theme ...
::    Metal provides:
::       steel
::       ocean
::    Tiny provides:
::       Default
::       Earth
::       Forest
::       Golden
::       Lego
::       Light
::       Nightly
::       Plastic
::       Pool
::       Silver
::       Small
:: otherwise, keep the line empty after the equals sign (=)
set theme=

:: Don't care about the following lines ...


if %laf%~==metal~   set laf=javax.swing.plaf.metal.MetalLookAndFeel
if %laf%~==windows~ set laf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
if %laf%~==motif~   set laf=com.sun.java.swing.plaf.motif.MotifLookAndFeel
if %laf%~==tiny~    set laf=de.muntjak.tinylookandfeel.TinyLookAndFeel

if %theme%~==steel~   goto switch
if %theme%~==ocean~   goto switch
if %theme%~==default~ goto delete
if %theme%~==Default~ goto delete
if %theme%~==~        goto exec
if %theme%~==Earth~   goto apply
if %theme%~==Forest~  goto apply
if %theme%~==Golden~  goto apply
if %theme%~==Lego~    goto apply
if %theme%~==Light~   goto apply
if %theme%~==Nightly~ goto apply
if %theme%~==Plastic~ goto apply
if %theme%~==Pool~    goto apply
if %theme%~==Silver~  goto apply
if %theme%~==Small~   goto apply
echo Brieftaube: Warning: Unknown theme!
set theme=
goto exec

:switch
set theme=-Dswing.metalTheme=%theme%
goto exec

:delete
del Default.theme
set theme=
goto exec

:apply
copy themes\%theme%.theme .\Default.theme
set theme=
goto exec

:exec
start javaw -Dswing.defaultlaf=%laf% %theme% -jar Brieftaube.jar
goto EOF

:EOF
