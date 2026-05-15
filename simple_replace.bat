@echo off
setlocal enabledelayedexpansion

echo ============================================
echo Spring Boot 3 Migration - Simple Replace
echo ============================================
echo.

set "ROOT_DIR=d:\01WORK\01CODE\crmeb\crmeb_m"
set COUNT=0
set MODIFIED=0

echo Scanning files...
echo.

for /r "%ROOT_DIR%" %%f in (*.java) do (
    findstr /C:"import javax.annotation.Resource" "%%f" >nul 2>&1
    if !errorlevel! equ 0 (
        set /a COUNT+=1
        echo Processing: %%~nxf
        
        powershell -Command "$content = Get-Content '%%f' -Raw; $content = $content -replace 'import javax.annotation.Resource', 'import jakarta.annotation.Resource'; Set-Content -Path '%%f' -Value $content -NoNewline"
        set /a MODIFIED+=1
    )
)

echo.
echo ============================================
echo Done!
echo Processed: %COUNT% files
echo Modified: %MODIFIED% files
echo ============================================
echo.
pause
