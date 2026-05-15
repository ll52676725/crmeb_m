import os
import re

# 项目根目录
root_dir = r'd:\01WORK\01CODE\crmeb\crmeb_m'

# 需要替换的模式列表（包含所有常见的 javax 包）
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
    (r'import javax\.sql\.', r'import jakarta.sql.'),
    (r'import javax\.enterprise\.', r'import jakarta.enterprise.'),
    (r'import javax\.inject\.', r'import jakarta.inject.'),
    (r'import javax\.ws\.rs\.', r'import jakarta.ws.rs.'),
    (r'import javax\.el\.', r'import jakarta.el.'),
]

# 统计信息
stats = {
    'files_processed': 0,
    'files_modified': 0,
    'total_replacements': 0,
    'modified_files': []
}

print('=' * 70)
print('开始批量替换 javax → jakarta...')
print('=' * 70)

# 遍历所有 Java 文件
for subdir, dirs, files in os.walk(root_dir):
    # 跳过 target 目录
    if 'target' in subdir or '.git' in subdir:
        continue
        
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(subdir, file)
            try:
                # 读取文件
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original_content = content
                file_replacements = 0
                file_changes = []
                
                # 执行所有替换
                for pattern, replacement in replacements:
                    matches = re.findall(pattern, content)
                    if matches:
                        count = len(matches)
                        file_replacements += count
                        content = re.sub(pattern, replacement, content)
                        file_changes.append(f'{pattern} → {replacement} ({count}处)')
                
                # 如果有修改，写回文件
                if content != original_content:
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    stats['files_modified'] += 1
                    stats['total_replacements'] += file_replacements
                    stats['modified_files'].append((filepath, file_replacements, file_changes))
                    
                    rel_path = os.path.relpath(filepath, root_dir)
                    print(f'✅ [{stats["files_modified"]}] {rel_path}')
                    for change in file_changes:
                        print(f'   └─ {change}')
                
                stats['files_processed'] += 1
                
            except Exception as e:
                print(f'❌ 处理文件 {filepath} 时出错: {e}')

# 输出统计结果
print(f'\n{"="*70}')
print(f'处理完成！')
print(f'处理的文件总数: {stats["files_processed"]}')
print(f'修改的文件数量: {stats["files_modified"]}')
print(f'总替换次数: {stats["total_replacements"]}')
print(f'{"="*70}')

# 输出详细修改列表
if stats['modified_files']:
    print(f'\n📋 详细修改列表:')
    print(f'{"-"*70}')
    for filepath, count, changes in stats['modified_files']:
        rel_path = os.path.relpath(filepath, root_dir)
        print(f'{rel_path}: {count} 处替换')
