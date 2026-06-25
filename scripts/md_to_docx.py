"""
Convert Markdown documents to .docx format.
Handles headings, tables, code blocks, images, bold, lists, and horizontal rules.
"""

import re
import os
import sys

from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn, nsdecls
from docx.oxml import parse_xml


def add_heading(doc, text, level):
    """Add a heading with proper formatting."""
    h = doc.add_heading(text, level=level)
    return h


def add_paragraph(doc, text, bold=False, font_size=None):
    """Add a paragraph, handling inline bold markers."""
    p = doc.add_paragraph()
    # Parse inline bold (**text**)
    parts = re.split(r'(\*\*.*?\*\*)', text)
    for part in parts:
        if part.startswith('**') and part.endswith('**'):
            run = p.add_run(part[2:-2])
            run.bold = True
        else:
            run = p.add_run(part)
    return p


def add_code_block(doc, code_text):
    """Add a code block with monospace font and gray background."""
    p = doc.add_paragraph()
    p.style = doc.styles['No Spacing']
    # Set shadding
    shading_elm = parse_xml(f'<w:shd {nsdecls("w")} w:fill="F2F2F2" w:val="clear"/>')
    p.paragraph_format.element.get_or_add_pPr().append(shading_elm)
    run = p.add_run(code_text)
    run.font.name = 'Consolas'
    run.font.size = Pt(9)
    run.font.color.rgb = RGBColor(0x33, 0x33, 0x33)
    return p


def add_table(doc, rows):
    """Add a table from parsed markdown rows."""
    if not rows or len(rows) < 2:
        return
    # First row is header, second row is separator
    header = [cell.strip() for cell in rows[0].split('|')[1:-1]]
    data_rows = []
    for row in rows[2:]:
        cells = [cell.strip() for cell in row.split('|')[1:-1]]
        if cells:
            data_rows.append(cells)
    
    if not data_rows:
        return
    
    num_cols = len(header)
    table = doc.add_table(rows=1 + len(data_rows), cols=num_cols)
    table.style = 'Table Grid'
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    
    # Header row
    for i, cell_text in enumerate(header):
        cell = table.rows[0].cells[i]
        cell.text = ''
        run = cell.paragraphs[0].add_run(cell_text)
        run.bold = True
        run.font.size = Pt(9)
        # Gray background for header
        tc = cell._tc
        tcPr = tc.get_or_add_tcPr()
        shading = parse_xml(f'<w:shd {nsdecls("w")} w:fill="D9E2F3" w:val="clear"/>')
        tcPr.append(shading)
    
    # Data rows
    for r_idx, row_data in enumerate(data_rows):
        for c_idx, cell_text in enumerate(row_data):
            if c_idx < num_cols:
                cell = table.rows[r_idx + 1].cells[c_idx]
                cell.text = ''
                run = cell.paragraphs[0].add_run(cell_text)
                run.font.size = Pt(9)
    
    doc.add_paragraph()  # spacing after table


def add_image(doc, image_path):
    """Add an image if it exists, with appropriate sizing."""
    import tempfile
    if image_path is None:
        return
    if os.path.exists(image_path):
        try:
            # SVG → PNG conversion
            if image_path.lower().endswith('.svg'):
                import cairosvg
                # Create a temp PNG file
                with tempfile.NamedTemporaryFile(suffix='.png', delete=False) as tmp:
                    tmp_name = tmp.name
                cairosvg.svg2png(url=image_path, output_width=1200, write_to=tmp_name)
                doc.add_picture(tmp_name, width=Inches(5.5))
                os.unlink(tmp_name)
            else:
                doc.add_picture(image_path, width=Inches(5.5))
            # Center the image
            if doc.paragraphs:
                last_paragraph = doc.paragraphs[-1]
                last_paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER
        except Exception as e:
            p = doc.add_paragraph(f'[图: {os.path.basename(image_path)}]')
            p.italic = True
    else:
        p = doc.add_paragraph(f'[图未找到: {image_path}]')
        p.italic = True


def resolve_image_path(md_file_path, image_rel_path):
    """Resolve a relative image path from the md file location, trying multiple variants."""
    md_dir = os.path.dirname(os.path.abspath(md_file_path))
    
    # Try 1: literal relative path from md file
    full_path = os.path.normpath(os.path.join(md_dir, image_rel_path))
    if os.path.exists(full_path):
        return full_path
    
    # Try 2: remove one level of 'docs/' from path (since md is inside docs/)
    # e.g., ../docs/diagram/x.svg → ../diagram/x.svg
    parts = image_rel_path.replace('\\', '/').split('/')
    if 'docs' in parts:
        new_parts = [p for p in parts if p != 'docs']
        new_rel = '/'.join(new_parts)
        full_path = os.path.normpath(os.path.join(md_dir, new_rel))
        if os.path.exists(full_path):
            return full_path
    
    # Try 3: resolve from project root (go up 2 levels from md_dir)
    # md_dir is e.g. C:/.../nanxin-catbook/docs/需求分析/
    # project_root is C:/.../nanxin-catbook/
    project_root = os.path.normpath(os.path.join(md_dir, '..', '..'))
    full_path = os.path.normpath(os.path.join(project_root, image_rel_path))
    if os.path.exists(full_path):
        return full_path
    
    return None


def parse_md_to_docx(md_path, output_path):
    """Parse a markdown file and generate a docx."""
    doc = Document()
    
    # Set default font
    style = doc.styles['Normal']
    font = style.font
    font.name = 'Microsoft YaHei'
    font.size = Pt(11)
    
    # Set default font for East Asian
    rFonts = style.element.rPr.rFonts if style.element.rPr is not None else None
    if rFonts is None:
        rPr = style.element.get_or_add_rPr()
        rFonts_elem = parse_xml(f'<w:rFonts {nsdecls("w")} w:eastAsia="Microsoft YaHei"/>')
        rPr.append(rFonts_elem)
    
    with open(md_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    i = 0
    in_code_block = False
    code_buffer = []
    in_table = False
    table_buffer = []
    
    while i < len(lines):
        line = lines[i].rstrip('\n')
        
        # Handle code blocks
        if line.startswith('```'):
            if in_code_block:
                add_code_block(doc, '\n'.join(code_buffer))
                code_buffer = []
                in_code_block = False
                i += 1
                continue
            else:
                in_code_block = True
                i += 1
                continue
        
        if in_code_block:
            code_buffer.append(line)
            i += 1
            continue
        
        # Handle table rows
        if line.startswith('|') and line.endswith('|'):
            table_buffer.append(line)
            in_table = True
            i += 1
            continue
        else:
            if in_table and table_buffer:
                add_table(doc, table_buffer)
                table_buffer = []
                in_table = False
        
        # Skip empty lines (but add spacing if not consecutive)
        if not line.strip():
            i += 1
            continue
        
        # Handle headings
        if line.startswith('###### '):
            add_heading(doc, line[7:], 6)
            i += 1
            continue
        if line.startswith('##### '):
            add_heading(doc, line[6:], 5)
            i += 1
            continue
        if line.startswith('#### '):
            add_heading(doc, line[5:], 4)
            i += 1
            continue
        if line.startswith('### '):
            add_heading(doc, line[4:], 3)
            i += 1
            continue
        if line.startswith('## '):
            add_heading(doc, line[3:], 2)
            i += 1
            continue
        if line.startswith('# '):
            add_heading(doc, line[2:], 1)
            i += 1
            continue
        
        # Horizontal rule
        if line.strip() == '---':
            p = doc.add_paragraph()
            p.paragraph_format.space_before = Pt(6)
            p.paragraph_format.space_after = Pt(6)
            # Add a bottom border
            pPr = p.paragraph_format.element.get_or_add_pPr()
            pBdr = parse_xml(
                f'<w:pBdr {nsdecls("w")}>'
                f'  <w:bottom w:val="single" w:sz="6" w:space="1" w:color="999999"/>'
                f'</w:pBdr>'
            )
            pPr.append(pBdr)
            i += 1
            continue
        
        # Image
        if line.startswith('!['):
            alt_match = re.match(r'!\[(.*?)\]\((.*?)\)', line)
            img_path = alt_match.group(2) if alt_match else ''
            alt_text = alt_match.group(1) if alt_match else 'image'
            if alt_match:
                full_img_path = resolve_image_path(md_path, img_path)
                add_image(doc, full_img_path)
            else:
                p = doc.add_paragraph(f'[Image: {alt_text}]')
            i += 1
            continue
        
        # List items
        if line.strip().startswith('- ') or line.strip().startswith('* '):
            text = line.strip()[2:]
            p = doc.add_paragraph(style='List Bullet')
            # Handle bold in list items
            parts = re.split(r'(\*\*.*?\*\*)', text)
            for part in parts:
                if part.startswith('**') and part.endswith('**'):
                    run = p.add_run(part[2:-2])
                    run.bold = True
                else:
                    p.add_run(part)
        
        # Regular paragraph
        else:
            # Handle bold
            add_paragraph(doc, line)
        
        i += 1
    
    # Flush table buffer
    if in_table and table_buffer:
        add_table(doc, table_buffer)
    
    # Flush code buffer
    if in_code_block and code_buffer:
        add_code_block(doc, '\n'.join(code_buffer))
    
    doc.save(output_path)
    print(f'OK Generated: {os.path.basename(output_path)}')


def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    docs_dir = os.path.join(base_dir, 'docs')
    
    files = [
        ('需求分析/需求规格说明书.md', '需求分析/需求规格说明书.docx'),
        ('概要设计/概要设计说明书.md', '概要设计/概要设计说明书.docx'),
        ('详细设计/详细设计说明书.md', '详细设计/详细设计说明书.docx'),
        ('测试/测试报告.md', '测试/测试报告.docx'),
    ]
    
    for md_rel, docx_rel in files:
        md_path = os.path.join(docs_dir, md_rel)
        output_path = os.path.join(docs_dir, docx_rel)
        
        if not os.path.exists(md_path):
            print(f'SKIP Not found: {md_rel}')
            continue
        
        print(f'Converting: {md_rel}')
        parse_md_to_docx(md_path, output_path)


if __name__ == '__main__':
    main()
