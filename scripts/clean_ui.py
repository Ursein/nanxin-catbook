"""Fix 4.3 and 4.4 sections of detailed design by line index."""
with open('docs/详细设计/详细设计说明书.md', 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Find section headers
s43 = s44 = s45 = None
for i, line in enumerate(lines):
    s = line.strip()
    if s.startswith('### 4.3') and s43 is None:
        s43 = i
    elif s.startswith('### 4.4') and s44 is None:
        s44 = i
    elif s.startswith('### 4.5') and s45 is None:
        s45 = i

print(f'4.3: L{s43+1}, 4.4: L{s44+1}, 4.5: L{s45+1}')

# New 4.3 content
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

# New 4.4 content
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

lines[s43+1:s44] = new_43[1:]
lines[s44+1:s45] = new_44[1:]

with open('docs/详细设计/详细设计说明书.md', 'w', encoding='utf-8') as f:
    f.writelines(lines)
print('Done')
