import os
import re

# 项目根目录
root_dir = r'd:\01WORK\01CODE\crmeb\crmeb_m'

# 需要替换的模式列表（只替换 Java EE 相关的包，不替换 JDK 内置包）
replacements = [
    # javax.validation -> jakarta.validation
    (r'import javax\.validation\.', r'import jakarta.validation.'),
    # javax.servlet -> jakarta.servlet
    (r'import javax\.servlet\.', r'import jakarta.servlet.'),
    # javax.annotation -> jakarta.annotation
    (r'import javax\.annotation\.', r'import jakarta.annotation.'),
    # javax.xml.bind -> jakarta.xml.bind
    (r'import javax\.xml\.bind\.', r'import jakarta.xml.bind.'),
    # javax.xml.parsers -> jakarta.xml.parsers
    (r'import javax\.xml\.parsers\.', r'import jakarta.xml.parsers.'),
    # javax.xml.transform -> jakarta.xml.transform
    (r'import javax\.xml\.transform\.', r'import jakarta.xml.transform.'),
    # javax.persistence -> jakarta.persistence
    (r'import javax\.persistence\.', r'import jakarta.persistence.'),
    # javax.mail -> jakarta.mail
    (r'import javax\.mail\.', r'import jakarta.mail.'),
    # javax.transaction -> jakarta.transaction
    (r'import javax\.transaction\.', r'import jakarta.transaction.'),
    # javax.websocket -> jakarta.websocket
    (r'import javax\.websocket\.', r'import jakarta.websocket.'),
    # javax.enterprise -> jakarta.enterprise
    (r'import javax\.enterprise\.', r'import jakarta.enterprise.'),
    # javax.inject -> jakarta.inject
    (r'import javax\.inject\.', r'import jakarta.inject.'),
    # javax.ws.rs -> jakarta.ws.rs
    (r'import javax\.ws\.rs\.', r'import jakarta.ws.rs.'),
    # javax.el -> jakarta.el
    (r'import javax\.el\.', r'import jakarta.el.'),
    # javax.sql -> jakarta.sql (注意：这个是 Java EE 的 DataSource)
    (r'import javax\.sql\.', r'import jakarta.sql.'),
]

# 统计信息
stats = {
    'files_processed': 0,
    'files_modified': 0,
    'total_replacements': 0,
    'modified_files': []
}

print('=' * 80)
print('开始批量替换 javax -> jakarta')
print('=' * 80)
print()

# 遍历所有 Java 文件
for subdir, dirs, files in os.walk(root_dir):
    # 跳过 target 和 .git 目录
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
                
                # 执行所有替换
                for pattern, replacement in replacements:
                    matches = re.findall(pattern, content)
                    if matches:
                        count = len(matches)
                        file_replacements += count
                        content = re.sub(pattern, replacement, content)
                
                # 如果有修改，写回文件
                if content != original_content:
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    stats['files_modified'] += 1
                    stats['total_replacements'] += file_replacements
                    rel_path = os.path.relpath(filepath, root_dir)
                    stats['modified_files'].append((rel_path, file_replacements))
                    print(f'✅ [{stats["files_modified"]}] {rel_path} ({file_replacements} 处替换)')
                
                stats['files_processed'] += 1
                
            except Exception as e:
                print(f'❌ 处理文件 {filepath} 时出错: {e}')

# 输出统计结果
print()
print('=' * 80)
print('处理完成！')
print(f'处理的文件总数: {stats["files_processed"]}')
print(f'修改的文件数量: {stats["files_modified"]}')
print(f'总替换次数: {stats["total_replacements"]}')
print('=' * 80)
