# Spring Boot 2.x -> 3.x javax -> jakarta 批量替换脚本
# 用法: 在 PowerShell 中执行 .\batch_replace.ps1

$rootDir = "d:\01WORK\01CODE\crmeb\crmeb_m"
$count = 0
$modified = 0

Write-Host "开始批量替换 javax -> jakarta..." -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Gray

# 获取所有包含 javax 导入的 Java 文件
$files = Get-ChildItem -Path $rootDir -Filter "*.java" -Recurse | 
    Select-String -Pattern 'import javax\.' | 
    Select-Object Path | 
    Group-Object Path | 
    ForEach-Object { $_.Name }

Write-Host "找到 $($files.Count) 个需要处理的文件" -ForegroundColor Yellow
Write-Host ""

foreach ($file in $files) {
    $count++
    
    try {
        $content = Get-Content $file -Raw -Encoding UTF8
        $originalContent = $content
        
        # 执行替换
        $content = $content -replace 'import javax\.validation\.', 'import jakarta.validation.'
        $content = $content -replace 'import javax\.servlet\.', 'import jakarta.servlet.'
        $content = $content -replace 'import javax\.annotation\.', 'import jakarta.annotation.'
        $content = $content -replace 'import javax\.xml\.bind\.', 'import jakarta.xml.bind.'
        $content = $content -replace 'import javax\.xml\.parsers\.', 'import jakarta.xml.parsers.'
        $content = $content -replace 'import javax\.xml\.transform\.', 'import jakarta.xml.transform.'
        $content = $content -replace 'import javax\.persistence\.', 'import jakarta.persistence.'
        $content = $content -replace 'import javax\.mail\.', 'import jakarta.mail.'
        $content = $content -replace 'import javax\.transaction\.', 'import jakarta.transaction.'
        $content = $content -replace 'import javax\.websocket\.', 'import jakarta.websocket.'
        $content = $content -replace 'import javax\.enterprise\.', 'import jakarta.enterprise.'
        $content = $content -replace 'import javax\.inject\.', 'import jakarta.inject.'
        $content = $content -replace 'import javax\.ws\.rs\.', 'import jakarta.ws.rs.'
        $content = $content -replace 'import javax\.el\.', 'import jakarta.el.'
        $content = $content -replace 'import javax\.sql\.', 'import jakarta.sql.'
        
        # 如果内容有变化，写回文件
        if ($content -ne $originalContent) {
            Set-Content -Path $file -Value $content -Encoding UTF8 -NoNewline
            $modified++
            $relPath = $file.Replace($rootDir, "").TrimStart("\")
            Write-Host "✅ [$modified] $relPath" -ForegroundColor Cyan
        }
    }
    catch {
        Write-Host "❌ 处理文件 $file 时出错: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Gray
Write-Host "处理完成！" -ForegroundColor Green
Write-Host "处理的文件总数: $count" -ForegroundColor Yellow
Write-Host "修改的文件数量: $modified" -ForegroundColor Yellow
Write-Host "============================================" -ForegroundColor Gray
