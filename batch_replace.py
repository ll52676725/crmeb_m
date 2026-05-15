import os
import re

# 项目根目录
root_dir = r'd:\01WORK\01CODE\crmeb\crmeb_m'

# 需要替换的模式列表
replacements = [
    (r'import javax\.validation\.', r'import jakarta.validation.'),
    (r'import javax\.servlet\.', r'import jakarta.servlet.'),
    (r'import javax\.annotation\.', r'import jakarta.annotation.'),
    (r'import javax\.xml\.bind\.', r'import jakarta.xml.bind.'),
    (r'import javax\.xml\.parsers\.', r'import jakarta.xml.parsers.'),
    (r'import javax\.xml\.transform\.', r'import jakarta.xml.transform.'),
    (r'import javax\.persistence\.', r'import jakarta.persistence.'),
    (r'import javax\.mail\.', r'import jakarta.mail.'),
    (r'import javax\.transaction\.', r'import jakarta.transaction.'),
    (r'import javax\.websocket\.', r'import jakarta.websocket.'),
]

# 统计信息
stats = {
    'files_processed': 0,
    'files_modified': 0,
    'total_replacements': 0
}

# 遍历所有 Java 文件
for subdir, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(subdir, file)
            try:
                # 读取文件
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original_content = content
                file_replacements = 0
                
                # 执行所有替换
                for pattern, replacement in replacements:
                    matches = re.findall(pattern, content)
                    if matches:
                        file_replacements += len(matches)
                        content = re.sub(pattern, replacement, content)
                
                # 如果有修改，写回文件
                if content != original_content:
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    stats['files_modified'] += 1
                    stats['total_replacements'] += file_replacements
                    print(f'✅ 修改了 {os.path.basename(filepath)}: {file_replacements} 处替换')
                
                stats['files_processed'] += 1
                
            except Exception as e:
                print(f'❌ 处理文件 {filepath} 时出错: {e}')

# 输出统计结果
print(f'\n{"="*60}')
print(f'处理完成！')
print(f'处理的文件总数: {stats["files_processed"]}')
print(f'修改的文件数量: {stats["files_modified"]}')
print(f'总替换次数: {stats["total_replacements"]}')
print(f'{"="*60}')
