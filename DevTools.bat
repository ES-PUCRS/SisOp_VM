@ECHO OFF

	SET "invokeParam=%1"
	
	if NOT defined invokeParam (
		ECHO Mising select param.
		PAUSE
		EXIT
	)

	rem Restart gradle run application 
	if "%invokeParam%" EQU "0" (
		tasklist /fi "imagename eq cmd.exe" /v | findstr "*gradle run" > temp
		for /f "tokens=2" %%i in (temp) do (
			taskkill /PID %%i
		)
		start cmd /MAX /K "gradle run"
		del temp
		EXIT
	)

	rem Git commit 
	if "%invokeParam%" EQU "1" (
		tasklist /fi "imagename eq cmd.exe" /v | findstr "*gradle run" > temp
		for /f "tokens=2" %%i in (temp) do (
			taskkill /PID %%i
		)
		start cmd /MAX /K "gradle run"
		del temp
		EXIT
	)

rem EXIT
	