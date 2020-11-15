@ECHO OFF

	tasklist /fi "imagename eq cmd.exe" /v | findstr "*gradle run" > temp

	for /f "tokens=2" %%i in (temp) do (
		taskkill /PID %%i
	)

	start cmd /MAX /K "gradle run"
	
	del temp

EXIT