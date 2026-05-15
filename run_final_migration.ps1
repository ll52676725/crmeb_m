# ============================================
# Spring Boot 2.x → 3.x 迁移最终批处理脚本
# ============================================
# 功能：批量替换剩余文件中的 javax.annotation.Resource → jakarta.annotation.Resource
# 使用方法：在项目根目录的 PowerShell 中执行：
# .\run_final_migration.ps1
# ============================================

$rootDir = "d:\01WORK\01CODE\crmeb\crmeb_m"
$count = 0
$modified = 0

Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║         Spring Boot 2.x → 3.x 迁移最终批处理工具                  ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
Write-Host "目标：替换 javax.annotation.Resource → jakarta.annotation.Resource" -ForegroundColor Yellow
Write-Host ""

# 步骤 1：获取所有需要处理的文件
Write-Host "[1/3] 正在扫描需要处理的文件..." -ForegroundColor Gray
$files = Get-ChildItem -Path $rootDir -Filter "*.java" -Recurse | 
    Select-String -Pattern 'import javax.annotation.Resource' | 
    Select-Object Path | 
    Group-Object Path | 
    ForEach-Object { $_.Name }

if ($files.Count -eq 0) {
    Write-Host ""
    Write-Host "✅ 没有找到需要处理的文件！所有 javax.annotation.Resource 已替换完成。" -ForegroundColor Green
    Write-Host ""
    exit 0
}

Write-Host "   找到 $($files.Count) 个需要处理的文件" -ForegroundColor Green
Write-Host ""

# 步骤 2：执行批量替换
Write-Host "[2/3] 正在执行批量替换..." -ForegroundColor Gray
Write-Host ""

foreach ($file in $files) {
    $count++
    
    try {
        $content = Get-Content $file -Raw -Encoding UTF8
        $originalContent = $content
        
        # 执行替换
        $content = $content -replace 'import javax.annotation.Resource', 'import jakarta.annotation.Resource'
        
        # 如果内容有变化，写回文件
        if ($content -ne $originalContent) {
            Set-Content -Path $file -Value $content -Encoding UTF8 -NoNewline
            $modified++
            $relPath = $file.Replace($rootDir, "").TrimStart("\")
            Write-Host "   ✅ [$modified/$($files.Count)] $relPath" -ForegroundColor Cyan
        }
    }
    catch {
        Write-Host "   ❌ 处理文件 $file 时出错: $_" -ForegroundColor Red
    }
}

Write-Host ""

# 步骤 3：验证结果
Write-Host "[3/3] 正在验证替换结果..." -ForegroundColor Gray
$remaining = Get-ChildItem -Path $rootDir -Filter "*.java" -Recurse | 
    Select-String -Pattern 'import javax.annotation.Resource' | 
    Select-Object Path | 
    Group-Object Path | 
    ForEach-Object { $_.Name }

Write-Host ""
Write-Host "═" * 70 -ForegroundColor Gray
Write-Host ""
Write-Host "替换完成！统计结果：" -ForegroundColor Green
Write-Host "  • 扫描的文件总数: $count" -ForegroundColor White
Write-Host "  • 成功修改的文件数: $modified" -ForegroundColor Green
Write-Host "  • 剩余未处理的文件数: $($remaining.Count)" -ForegroundColor Yellow

if ($remaining.Count -gt 0) {
    Write-Host ""
    Write-Host "⚠️  仍有 $($remaining.Count) 个文件需要手动检查：" -ForegroundColor Yellow
    foreach ($f in $remaining) {
        $relPath = $f.Replace($rootDir, "").TrimStart("\")
        Write-Host "   • $relPath" -ForegroundColor Gray
    }
}
else {
    Write-Host ""
    Write-Host "🎉 所有 javax.annotation.Resource 已成功替换！" -ForegroundColor Green
}

Write-Host ""
Write-Host "═" * 70 -ForegroundColor Gray
Write-Host ""
Write-Host "下一步操作建议：" -ForegroundColor Yellow
Write-Host "  1. 执行 mvn clean compile -DskipTests 验证编译"
Write-Host "  2. 检查是否还有其他 javax.* 需要替换（使用 MIGRATION_FINAL_GUIDE.md 中的验证命令）"
Write-Host "  3. 运行单元测试 mvn test"
Write-Host "  4. 启动应用进行功能验证"
Write-Host ""
Write-Host "详细文档请查看：MIGRATION_FINAL_GUIDE.md" -ForegroundColor Cyan
Write-Host ""
