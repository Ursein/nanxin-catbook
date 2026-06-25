"""Replace UI sections by line index - no Chinese text matching needed."""
with open('docs/详细设计/详细设计说明书.md', 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Find line numbers by pattern
s43_hdr = None  # "### 4.3"
s44_hdr = None  # "### 4.4"
s45_hdr = None  # "### 4.5"
for i, line in enumerate(lines):
    stripped = line.strip()
    if stripped.startswith('### 4.3'):
        s43_hdr = i
    elif stripped.startswith('### 4.4'):
        s44_hdr = i
    elif stripped.startswith('### 4.5'):
        s45_hdr = i

print(f'4.3 header: line {s43_hdr}')
print(f'4.4 header: line {s44_hdr}')
print(f'4.5 header: line {s45_hdr}')

if s43_hdr and s44_hdr:
    # Build new 4.3 content
    new_43 = [
        '### 4.3 首页界面设计\n',
        '\n',
        '首页实际界面截图如下：\n',
        '\n',
        '![主页截图](../docs/照片/主页.png)\n',
        '\n',
        '**布局说明：**\n',
        '- 顶部 Hero 区展示项目标题和简介\n',
        '- 状态 Tab 栏采用药丸形按钮，支持横向滚动\n',
        '- 猫咪卡片采用 4 列 Bento Grid，卡片尺寸交替变化（2x2、2x1、1x1、1x2）\n',
        '- 每张卡片包含猫咪名、状态标签、位置和性别信息\n',
        '- 移动端降级为单列布局\n',
        '\n',
    ]
    # Replace lines from after header to before 4.4 header
    lines[s43_hdr+1:s44_hdr] = new_43[1:]

if s44_hdr and s45_hdr:
    new_44 = [
        '### 4.4 详情页界面设计\n',
        '\n',
        '猫咪详情页实际界面截图如下：\n',
        '\n',
        '![猫咪详情截图](../docs/照片/猫咪详情.png)\n',
        '\n',
        '**布局说明：**\n',
        '- 照片轮播占据页面顶部约 65vh，支持左右箭头切换和底部圆点指示\n',
        '- 台式机右侧固定侧栏展示相似猫咪推荐（调用 P6 算法）\n',
        '- 猫咪信息区包含统计数字、属性表格、性格标签、互动按钮\n',
        '- 留言区支持即时输入和发送，评论按时间倒序排列\n',
        '- 移动端 1024px 以下右侧推荐栏并入主内容流\n',
        '\n',
    ]
    lines[s44_hdr+1:s45_hdr] = new_44[1:]

with open('docs/详细设计/详细设计说明书.md', 'w', encoding='utf-8') as f:
    f.writelines(lines)

print('Done')
# Verify
with open('docs/详细设计/详细设计说明书.md', 'r', encoding='utf-8') as f:
    text = f.read()
print(f'U+FFFD: {text.count(chr(0xfffd))}')
print(f'Emoji count: {sum(1 for c in text if ord(c) > 0x1F300 and ord(c) < 0x1FAFF)}')
