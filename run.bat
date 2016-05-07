::This is a helper file to easily run different mutation tests
::Note: only change the command below
::
@echo off
color 71
::
:::::::::::::::::::::
::Command to modify::
set command=java -cp "src/*;bin/" GenerateMutants -num 5 -sim true src/org/bukkit/Color.java src/org/bukkit/ChatColor.java src/org/bukkit/Note.java
:::::::::::::::::::::
::
echo Running GenerateMutants with the following command:
echo %command%
echo.
%command%
echo.
echo Press any key to exit...
pause > nul
::