@echo off
REM ########################################################################## 
REM 
REM Forge3 batch file for setup_ep
REM
REM ########################################################################## 
REM
REM To debug bat file please replace set STDOUT=NUL by set STDOUT=CON (console)
set STDOUT=NUL
REM
REM ########################################################################## 
REM 
REM Environment setup will be inserted hereSET SIM_NAME="Speczanie\"
set LAUNCH_INSTALL_DIR="C:\Forge_2008\Bin"
set LAUNCH_WORKING_DIR="C:\Forge_2008\Computations\RingTest3D.tsv\Analysis\ResultDataBase\"%SIM_NAME%
set UNC_LAUNCH_INSTALL_DIR="C:\Forge_2008\Bin"
set DATA_SIMULATION_DIR="C:\Forge_2008\Computations\RingTest3D.tsv\"%SIM_NAME%
set LINUX_LAUNCH_WORKING_DIR="C:/Forge_2008/Computations/RingTest3D.tsv/Analysis/ResultDataBase/Speczanie/"
REM 
REM ##########################################################################
REM ########################################################################## 
REM	Arguments
REM
REM
REM	%1 : isQueuemanagerused
REM	%2 : Software Name
REM ########################################################################## 
REM ########################################################################## 
REM 
REM Set the appearance of the console
REM 
REM ##########################################################################

Title Forge F2008 Windows 3D Setup - RingTest3D speczanie.ref

REM ########################################################################## 
REM 
REM Local variable definitions
REM 
REM ##########################################################################

set LAUNCH_INSTALL_DIR_NO_DECOR=%LAUNCH_INSTALL_DIR:~1,-1%
set LAUNCH_WORKING_DIR_NO_DECOR=%LAUNCH_WORKING_DIR:~1,-1%
set UNC_LAUNCH_INSTALL_DIR_NO_DECOR=%UNC_LAUNCH_INSTALL_DIR:~1,-1%


SET PLATFORM_TYPE=windows
SET SOFT=%2
set NP=4

set EXECUTABLE=C:\Forge_2008\\Bin\Windows32\parfg3_v73.exe
set DYNLINKLIB=C:\Forge_2008\\Bin\Windows32\forge3v73userroutines.dll

set PREPARECALCUL="C:\Forge_2008\\Bin\Windows32\PreparCalculFg3.exe"
set MPICH2FILES="C:\Forge_2008\\Bin\MPICH2\Argonne_Win32_1.0.2p1\*"

set SCRIPT=myforge_3d_with_none_windows
set SCRIPT_GEN=myforge3d_windows

set CONNECT_PROTOCOL="none"

IF '%CONNECT_PROTOCOL%' EQU '"none"' (
	set SCRIPT=myforge_3d_with_none_windows.bat
	set SCRIPT_GEN=myforge3d_windows.bat	
)

REM ########################################################################## 
REM 
REM Simulation setup : copy all necessary file
REM 
REM ##########################################################################

REM
REM 	1. Non specific binaries
REM

echo.
echo ### 
echo ### FORGE F2008 3D Solver Setup Started. 
echo ### 
echo. 
echo ###
echo ### Project RingTest3D
echo ### Working directory %LAUNCH_WORKING_DIR_NO_DECOR%
echo ### 
echo. 
echo ###
echo ### Copying required executable tool files
echo ###
echo.

  
copy %LAUNCH_INSTALL_DIR_NO_DECOR%\recolmanu.exe .\recolmanu.exe > %STDOUT%
IF ERRORLEVEL 1 (
call :SETUP_ACTION_ERROR copying recolmanu.exe
exit /B 2
)

copy %LAUNCH_INSTALL_DIR_NO_DECOR%\transportv6.exe .\transportv6.exe > %STDOUT%
IF ERRORLEVEL 1 (
call :SETUP_ACTION_ERROR copying transportv6.exe
exit /B 2
) 

copy %LAUNCH_INSTALL_DIR_NO_DECOR%\marq.exe .\marq.exe > %STDOUT%
IF ERRORLEVEL 1 (
call :SETUP_ACTION_ERROR copying marq.exe
exit /B 2
)


REM
REM 	2. License file
REM

REM EP: gestion du cas ou plusieurs serveur de licences existent on cherche en premier une licence specifique a
REM la machine cible et si on ne trouve rien alors on prend la license generale. 
REM CommonProgramFiles(x86): gestion config 64 bit

cmd /c "copy "%CommonProgramFiles%\Transvalor Solutions\Licenses\Local Machine\license.dat" .\license.dat > %STDOUT%"
IF ERRORLEVEL 1 goto licenseError1
goto licenseOK

:licenseError1
	echo :licenseError1 > %STDOUT%

	cmd /c "copy "%CommonProgramFiles%\Transvalor Solutions\Licenses\license.dat" .\license.dat > %STDOUT%"

IF ERRORLEVEL 1 goto licenseError2
goto licenseOK

:licenseError2
	echo :licenseError2 > %STDOUT%

	cmd /c "copy "%CommonProgramFiles(x86)%\Transvalor Solutions\Licenses\Local Machine\license.dat" .\license.dat > %STDOUT%"

IF ERRORLEVEL 1 goto licenseError3
goto licenseOK


:licenseError3
	echo :licenseError3 > %STDOUT%

	cmd /c "copy "%CommonProgramFiles(x86)%\Transvalor Solutions\Licenses\license.dat" .\license.dat > %STDOUT%"

IF ERRORLEVEL 1 goto licenseError4
goto licenseOK

:licenseError4
	echo :licenseError4  > %STDOUT%

	call :SETUP_ACTION_ERROR copying LicenseFile

	exit /B 2



:licenseOK

echo licenseOK  > %STDOUT%

REM
REM 	3. .COM Files
REM

echo ###
echo ### Copying required ressource files
echo ###
echo.

set COMS="mtcf3_drama.com" "mtcf3_dramaK.com" "mtcf3_light.com" "surf_71.com" "surflight.com" "mring_f3.com" "topologies.dat"

FOR %%I IN (%COMS%) DO (
    copy %LAUNCH_INSTALL_DIR_NO_DECOR%\..\ressources\%%I .\%%I > %STDOUT%
    IF ERRORLEVEL 1 (
        call :SETUP_ACTION_ERROR copying %%I
        exit /B 2
    )
)

REM
REM 	3. Ressource files for computation report
REM

echo ###
echo ### Copying required files for computation report
echo ###
echo.

set REPFILES="forge3.xsl" "LogoTransvalor.jpg" "Customer_Logo.jpg" "FORGE_RESULTSNAMES.TXT"

FOR %%I IN (%REPFILES%) DO (
    copy %LAUNCH_INSTALL_DIR_NO_DECOR%\..\ressources\%%I .\%%I > %STDOUT%
    IF ERRORLEVEL 1 (
        call :SETUP_ACTION_ERROR copying %%I
        exit /B 2
    )
)

REM
REM 	4. Solver files
REM

echo ###
echo ### Copying required solver executables
echo ###
echo.

copy %EXECUTABLE% .\forge3.exe > %STDOUT%
    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying %EXECUTABLE%
    exit /B 2
    )
    
copy %DYNLINKLIB% .\forge3userroutines.dll > %STDOUT%
    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying %DYNLINKLIB%
    exit /B 2
    )

REM
REM 	5. Platform dependent files
REM

echo ###
echo ### Copying required platform dependent files
echo ###
echo.

copy %PREPARECALCUL% .\PreparCalculFg3.exe > %STDOUT%
    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying %PREPARECALCUL%
    exit /B 2
    )

copy  %MPICH2FILES% . > %STDOUT%
    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying  %MPICH2FILES%
    exit /B 2
    )

if '%1' EQU '"NON"' (
copy %LAUNCH_INSTALL_DIR_NO_DECOR%\GetUnc.exe .\GetUnc.exe > %STDOUT%
    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying GetUnc.exe
    exit /B 2
    )
)

copy %LAUNCH_INSTALL_DIR_NO_DECOR%\..\Data\Batches\%SOFT%\%PLATFORM_TYPE%\%SCRIPT% .\%SCRIPT_GEN% > %STDOUT%  

    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying %SCRIPT%
    exit /B 2
    )

if '%1' EQU '"OUI"' (

copy %LAUNCH_INSTALL_DIR_NO_DECOR%\LaunchPBS.exe .\LaunchPBS.exe > %STDOUT%
    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying  LaunchPBS.exe
    exit /B 2
    )

copy %LAUNCH_INSTALL_DIR_NO_DECOR%\WaitingLoop.exe .\WaitingLoop.exe > %STDOUT%
    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying  WaitingLoop.exe
    exit /B 2
    )

copy %LAUNCH_INSTALL_DIR_NO_DECOR%\SpyPBS.exe .\SpyPBS.exe > %STDOUT%
    IF ERRORLEVEL 1 (
    call :SETUP_ACTION_ERROR copying  SpyPBS.exe
    exit /B 2
    )

)

echo ###
echo ### FORGE F2008 3D Solver Setup Ended
echo ###
echo.

exit /B 0


REM
REM Helper to display copy errors uniformly
REM
    :SETUP_ACTION_ERROR
REM

echo ###
echo ### Error %1 file %2 to simulation directory 
echo ### %CD%
echo ###

GOTO :EOF


