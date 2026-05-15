import os
import re

root_dir = r'd:\01WORK\01CODE\crmeb\crmeb_m'
replacements = [
    (r'import javax\.servlet\.', r'import jakarta.servlet.'),
    (r'import javax\.validation\.', r'import jakarta.validation.'),
    (r'import javax\.annotation\.', r'import jakarta.annotation.'),
    (r'import javax\.xml\.bind\.', r'import jakarta.xml.bind.'),
    (r'import javax\.xml\.parsers\.', r'import jakarta.xml.parsers.'),
    (r'import javax\.xml\.transform\.', r'import jakarta.xml.transform.'),
]

count = 0
for subdir, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(subdir, file)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original = content
                for pattern, replacement in replacements:
                    content = re.sub(pattern, replacement, content)
                
                if content != original:
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    count += 1
                    print(f'Updated: {filepath}')
            except Exception as e:
                print(f'Error processing {filepath}: {e}')

print(f'Total files updated: {count}')
