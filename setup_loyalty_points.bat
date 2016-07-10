@echo off
setlocal

echo ------------------------------------
echo -- Starting Loyalty Points installation  --
echo ------------------------------------
echo

if not "%ATGDIR%" == "" (
 set DYNAMO_HOME=%ATGDIR%\home
) else if not "%DYNAMO_HOME%" == "" (
 set ATGDIR=%DYNAMO_HOME%\..
) 

if "%DYNAMO_HOME%" == "" (
  echo. 
  echo. You must set the DYNAMO_HOME variable to point to the
  echo. 'home' directory in your ATG installation 
  echo. ^(for instance, set ATGDIR=C:\ATG\ATG2007.1^)
  echo.
  goto :end
)

set SOLIDDIR=%ATGDIR%\DAS\solid

echo ------------------------------------
echo -- Starting Loyalty Solid database now.  Please press any key when Solid startup completes...
echo ------------------------------------
echo

start %SOLIDDIR%\i486-unknown-win32\solfe.exe -c %SOLIDDIR%\atgdb_com
pause

echo ------------------------------------
echo -- Setup Loyalty Points tables           --
echo ------------------------------------

%SOLIDDIR%\i486-unknown-win32\solsql -f %ATGDIR%\Loyalty\sql\loyalty.sql "tcp localhost 1313" b2b b2b
%SOLIDDIR%\i486-unknown-win32\solsql -f %ATGDIR%\Loyalty\sql\loyalty_points.sql "tcp localhost 1313" b2b b2b

echo ------------------------------------
echo -- Loyalty Points tables setup completed --
echo ------------------------------------

:end